package cn.openwatch.internal.communication;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import cn.openwatch.internal.basic.ThreadsManager;
import cn.openwatch.internal.communication.event.AbsEventHandler;
import cn.openwatch.internal.communication.event.AbsEventObserver;
import cn.openwatch.internal.basic.utils.DeviceUtils;
import cn.openwatch.internal.basic.utils.LogUtils;
import cn.openwatch.internal.basic.utils.SystemPropertiesUtils;

public final class ClientManager implements AbsApiClient.ServiceConnectionListener {

    public static final String UNKNOWN_CLIENT_TYPE_NAME = "unknown";

    private static ClientManager instance;
    private static ReentrantLock detectLocker = new ReentrantLock();
    private static AtomicBoolean isAddingEventHandler = new AtomicBoolean(false);

    private AbsApiClient<?> client;// 保证全局只有一个client

    private AbsEventObserver<?> eventObserver;
    private SparseArray<SupportClient> supportClientMap;
    private ConnectStatus currentStatus = ConnectStatus.CONNECT_SERVICE_FAIL;
    private AtomicBoolean isWillResume = new AtomicBoolean(false);
    protected ConcurrentHashMap<String, AbsEventHandler> eventHandlers = new ConcurrentHashMap<String, AbsEventHandler>();

    private ClientManager() {
        supportClientMap = SupportClient.getSupportClientMap();
    }

    public interface GetLocalNodeIdCallback {
        void onGetLocalNodeId(String localNodeId);
    }

    public interface GetClientTypeNameCallback {
        void onGetClientTypeName(String typeName);
    }

    public static ClientManager getInstance() {
        if (instance == null) {
            synchronized (ClientManager.class) {
                if (instance == null)
                    instance = new ClientManager();
            }
        }
        return instance;
    }

    public boolean isConnectedDevice() {
        return currentStatus == ConnectStatus.CONNECT_DEVICE_SUCCESS;
    }

    public String getLastKnownClientTypeName(Context cx) {
        LastConnectedDevice deviceData = CacheManager.getLastConnectedDeviceData(cx);
        if (deviceData != null)
            return deviceData.typeName;

        return UNKNOWN_CLIENT_TYPE_NAME;
    }

    public String getClientTypeNameAwait(Context cx) {

        String typeName;
        if (client != null && currentStatus.isDeviceConnected()) {
            typeName = client.getTypeName();
        } else {
            typeName = getLastKnownClientTypeName(cx);

            if (typeName.equals(UNKNOWN_CLIENT_TYPE_NAME)) {

                detectAwaitIfNeed(cx);
                if (client != null && currentStatus.isDeviceConnected()) {
                    typeName = client.getTypeName();
                }

            }
        }

        return typeName;
    }

    public void getClientTypeName(Context cx, final GetClientTypeNameCallback callback) {
        if (callback == null)
            return;

        String typeName;
        if (client != null && currentStatus.isDeviceConnected()) {
            typeName = client.getTypeName();
        } else {
            typeName = getLastKnownClientTypeName(cx);

            if (typeName.equals(UNKNOWN_CLIENT_TYPE_NAME)) {

                final Context context = cx.getApplicationContext();
                ThreadsManager.execute(new Runnable() {
                    @Override
                    public void run() {
                        detectAwaitIfNeed(context);
                        if (client != null && currentStatus.isDeviceConnected()) {
                            callback.onGetClientTypeName(client.getTypeName());
                        }
                    }
                });

            }
        }

        callback.onGetClientTypeName(typeName);
    }

    public void getLocalNodeId(Context cx, final GetLocalNodeIdCallback callback) {

        if (callback == null)
            return;

        String localNodeId = CacheManager.getLocalNodeId(cx);
        if (TextUtils.isEmpty(localNodeId)) {

            final Context context = cx.getApplicationContext();
            ThreadsManager.execute(new Runnable() {

                @Override
                public void run() {
                    if (currentStatus.isServiceConnected() && client != null) {
                        callback.onGetLocalNodeId(client.getLocalNodeIdAwait());
                    } else {
                        detectAwaitIfNeed(context);
                        if (client != null)
                            callback.onGetLocalNodeId(client.getLocalNodeIdAwait());
                    }
                }
            });

        } else {
            callback.onGetLocalNodeId(localNodeId);
        }
    }

    public int getConnectionType(Context cx) {
        if (client != null) {
            return client.getType();
        } else {
            LastConnectedDevice deviceData = CacheManager.getLastConnectedDeviceData(cx);
            if (deviceData != null)
                return deviceData.type;
        }

        return SupportClient.TYPE_UNKNOWN;
    }

    private ConnectStatus deepDetectAwait(Context cx, int filterType) {

        ConnectStatus status = ConnectStatus.SERVICE_INVAILABLE;
        ConnectStatus statusWithAppAndServiceValiable = null;

        AbsApiClient<?> client;

        int size = supportClientMap.size();
        for (int i = 0; i < size; i++) {
            int type = supportClientMap.keyAt(i);

            // 前面已检查过的就跳过不检查了
            if (filterType == type)
                continue;

            client = AbsApiClient.newInstanceByReflect(cx, supportClientMap.get(type).className);

            if (client != null) {
                status = detectSpecificAwait(cx, client);
            } else {
                status = ConnectStatus.SERVICE_INVAILABLE;
            }

            if (status != ConnectStatus.SERVICE_INVAILABLE) {
                // 连接服务可用

                this.client = client;

                if (status.isDeviceConnected()) {
                    // 连接成功 则直接返回
                    return status;
                } else {
                    // 未连接成功 则继续循环检查其他连接服务
                    statusWithAppAndServiceValiable = status;
                }
            }

            // 连接服务不可用 则继续循环检查其他连接服务

        }

        // 连接服务可用则返回连接失败或超时等状态 不可用则返回不可用状态
        return statusWithAppAndServiceValiable != null ? statusWithAppAndServiceValiable : status;
    }

    private int getClientTypeBySystemCharacter(Context cx) {
        int size = supportClientMap.size();
        for (int i = 0; i < size; i++) {
            String character = supportClientMap.valueAt(i).systemCharacter;
            if (character != null && !TextUtils.isEmpty(SystemPropertiesUtils.get(cx, character))) {
                LogUtils.d(this, "getClientTypeBySystemCharacter");
                return supportClientMap.keyAt(i);
            }
        }

        return SupportClient.TYPE_UNKNOWN;
    }

    private ConnectStatus detectSpecificAwait(Context cx, AbsApiClient<?> client) {

        // 经测试不同系统对于service是否安装的返回值不同 有的是返回超时 有的是返回连接失败
        // 为了确保当service未安装时 一定是返回SERVICE_INVAILABLE 先自行判断一下
        // 且这种情况下不用走连接 响应也更快
        boolean isAppAndServiceAvailable = client.isAppAndServiceAvailable();

        if (!isAppAndServiceAvailable) {
            return ConnectStatus.SERVICE_INVAILABLE;
        }

        List<String> ids;

        client.setConnectTimeOutMills(3 * 1000);
        client.setGetNodesIdTimeOutMills(3 * 1000);

        ConnectStatus status = client.connectAwaitIfNeed();
        client.setConnectionListener(this);

        if (status.isServiceConnected()) {
            // 与连接服务连接成功
            ids = client.getConnectedNodesIdAwait();

            if (ids != null && !ids.isEmpty()) {
                // 与配对设备连接成功

                CacheManager.saveLastConnectedDeviceData(cx, client.getTypeName(), client.getType());

                status = ConnectStatus.CONNECT_DEVICE_SUCCESS;
            } else {
                status = ConnectStatus.CONNECT_DEVICE_FAIL;
            }
        }

        return status;
    }

    // 判断应使用的连接方式并尝试连接上
    protected ConnectStatus detectAwaitIfNeed(Context cx) {

        detectLocker.lock();

        try {

            cx = cx.getApplicationContext();

            if (currentStatus.isDeviceConnected()) {
                return currentStatus;
            }

            ConnectStatus status;

            if (client != null) {
                // 已创建过实例 则判断当前连接方式是否依然可用

                status = detectSpecificAwait(cx, client);

                if (!DeviceUtils.isWearableDevice(cx) && !status.isDeviceConnected()) {
                    // 手机设备可能因为切换成其他连接服务
                    // 不能确定连接方式 尝试深度判断
                    status = deepDetectAwait(cx, client.getType());

                    LogUtils.d(this, "reuse client");

                } else {
                    // 依然可连或者是手表设备有系统特征 不需要再尝试深度判断
                    currentStatus = status;
                }
            } else {

                // 没创建过实例

                LogUtils.d(this, "new client");

                LastConnectedDevice deviceData = CacheManager.getLastConnectedDeviceData(cx);

                if (deviceData != null) {
                    // 尝试之前连接过的设备
                    status = reset2SpecificClient(cx, deviceData.type);

                    if (!DeviceUtils.isWearableDevice(cx) && !status.isDeviceConnected()) {
                        // 手机设备可能因为切换成其他连接服务
                        // 不能确定连接方式 尝试深度判断
                        status = deepDetectAwait(cx, deviceData.type);
                    }
                } else {
                    // 没有之前连接过的设备数据

                    int type = getClientTypeBySystemCharacter(cx);

                    if (type != SupportClient.TYPE_UNKNOWN) {

                        status = reset2SpecificClient(cx, type);

                    } else {
                        // 不含有系统特征 不能确定连接方式 尝试深度判断
                        status = deepDetectAwait(cx, SupportClient.TYPE_UNKNOWN);
                    }
                }

            }

            if (status.isDeviceConnected()) {
                // 连接成功才能记录本设备的nodeId
                if (client != null)
                    CacheManager.saveLocalNodeId(cx, client.getLocalNodeIdAwait());
            } else {
                // 连接失败 则清除记录的设备数据
                CacheManager.clearLastConnectedDeviceData(cx);
            }

            resetEventObserver();

            LogUtils.d(this, "detectOs:" + (client == null ? "unknown" : (client.getTypeName() + " " + status)));

            currentStatus = status;

            return currentStatus;

        } finally {
            detectLocker.unlock();
        }
    }

    private void resetEventObserver() {
        LogUtils.d(this, "resetEventObserver");
        if (client == null)
            return;

        if (eventObserver != null) {
            eventObserver.unRegistIfNeed();
        }

        eventObserver = client.getEventObserver();

        if (!eventHandlers.isEmpty())
            eventObserver.registIfNeed();

    }

    private ConnectStatus reset2SpecificClient(Context cx, int type) {
        client = AbsApiClient.newInstanceByReflect(cx, supportClientMap.get(type).className);

        if (client != null) {
            return detectSpecificAwait(cx, client);
        } else {
            return ConnectStatus.SERVICE_INVAILABLE;
        }
    }

    // 没有消息发送任务和事件监听了 才能断开
    public void tryDisconnect() {
        if (Sender.getSendTaskCount() == 0 && (eventObserver == null || !eventObserver.isRegisted())) {
            boolean isDisconnected = forceDisconnect(false);
            if (isDisconnected)
                LogUtils.d(ClientManager.class, "auto disconnected");
        }
    }

    // 强制断开
    private boolean forceDisconnect(boolean isDisconnectDevice) {
        boolean isDisconnected = false;
        if (client != null) {
            isDisconnected = client.tryDisconnect();

            if (isDisconnected) {
                currentStatus = isDisconnectDevice ? ConnectStatus.CONNECT_DEVICE_FAIL : ConnectStatus.CONNECT_SERVICE_FAIL;
            }
        }

        return isDisconnected;
    }

    public Object getApiClient(Class<?> clazz) {
        if (clazz != null && client != null && client.getApiClient() != null && clazz.isInstance(client.getApiClient()))
            return client.getApiClient();

        return null;
    }

    protected AbsApiClient<?> getClient() {
        return client;
    }

    public ConcurrentHashMap<String, AbsEventHandler> getEventHandlers() {
        return eventHandlers;
    }

    public void putEventHandler(Context cx, AbsEventHandler eventHandler) {
        if (eventHandler != null) {

            LogUtils.d(this, "putExtraEventHandler");
            eventHandlers.put(eventHandler.getClass().getName(), eventHandler);

            if (eventObserver != null && currentStatus.isServiceConnected()) {

                if (!eventObserver.isRegisted()) {
                    eventObserver.registIfNeed();
                }

            } else {

                if (!isAddingEventHandler.getAndSet(true)) {
                    final Context context = cx.getApplicationContext();
                    ThreadsManager.execute(new Runnable() {
                        @Override
                        public void run() {
                            detectAwaitIfNeed(context);

                            isAddingEventHandler.set(false);
                        }
                    });
                }

            }
        }
    }

    public void removeEventHandler(AbsEventHandler eventHandler) {
        if (eventHandler != null) {
            LogUtils.d(this, "removeEventHandler");
            eventHandlers.remove(eventHandler.getClass().getName());

            if (eventHandlers.isEmpty() && eventObserver != null && eventObserver.isRegisted()) {
                eventObserver.unRegistIfNeed();
            }
        }
    }

    public void resumeIfNeed(Context cx) {

        if (this.isWillResume.getAndSet(false)) {

            LogUtils.d(this, "connection resume");

            final Context context = cx.getApplicationContext();
            ThreadsManager.execute(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    detectAwaitIfNeed(context);
                }
            });
        }

    }

    public void reset(boolean isWillResume, boolean isDisconnectDevice) {
        this.isWillResume.getAndSet(isWillResume);

        Sender.resetSendTaskCount();

        if (eventObserver != null)
            eventObserver.unRegistIfNeed();

        forceDisconnect(isDisconnectDevice);

        LogUtils.d(this, "connection reset");

    }

    @Override
    public void onConnected() {
        // TODO Auto-generated method stub
        for (AbsEventHandler eventHandler : eventHandlers.values()) {
            eventHandler.handleServiceConnected();
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // TODO Auto-generated method stub
        for (AbsEventHandler eventHandler : eventHandlers.values()) {
            eventHandler.handleServiceConnectionSuspended(cause);
        }
    }

}
