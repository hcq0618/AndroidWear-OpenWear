package cn.openwatch.internal.communication.event;

import android.graphics.Bitmap;

import java.util.concurrent.ConcurrentHashMap;

import cn.openwatch.communication.DataMap;
import cn.openwatch.communication.SpecialData;
import cn.openwatch.communication.service.OpenWatchListenerService;

// 用户消息和内部消息都需要处理
public final class ServiceEventHandler extends AbsEventHandler {

    private OpenWatchListenerService userService;
    private static ConcurrentHashMap<String, AbsEventHandler> eventHandlers = new ConcurrentHashMap<String, AbsEventHandler>();

    public ServiceEventHandler() {
        super();
        // TODO Auto-generated constructor stub

    }

    public void setUserService(OpenWatchListenerService userService) {
        this.userService = userService;
        setContext(userService);

        eventHandlers.put(UserEventHandler.class.getName(), new UserEventHandler().setEventCallback(new ServiceEventCallback()));
        eventHandlers.put(InternalEventHandler.class.getName(), new InternalEventHandler());

        for (AbsEventHandler eventHandler : eventHandlers.values()) {
            eventHandler.setContext(userService);
        }
    }

    public static void putExtraEventHandler(Class<? extends AbsEventHandler> clz) {
        try {
            eventHandlers.put(clz.getName(), clz.newInstance());
        } catch (Exception e) {
            // TODO Auto-generated catch block
        }
    }

    // path是唯一的 所以不需要遍历
    @Override
    public boolean handleMessageReceived(String path, byte[] rawData) {
        // TODO Auto-generated method stub

        for (AbsEventHandler eventHandler : eventHandlers.values()) {
            if (eventHandler.handleMessageReceived(path, rawData))
                break;
        }

        return true;
    }

    // path是唯一的 所以不需要遍历
    @Override
    public boolean handleDataChanged(String path, byte[] data) {
        // TODO Auto-generated method stub

        for (AbsEventHandler eventHandler : eventHandlers.values()) {
            if (eventHandler.handleDataChanged(path, data))
                break;
        }

        return true;
    }

    // path是唯一的 所以不需要遍历
    @Override
    public boolean handleDataDeleted(String path) {
        // TODO Auto-generated method stub

        for (AbsEventHandler eventHandler : eventHandlers.values()) {
            if (eventHandler.handleDataDeleted(path))
                break;
        }

        return true;
    }

    // 没有path 所以需要遍历
    @Override
    public boolean handlePeerConnected(String displayName, String nodeId) {
        // TODO Auto-generated method stub
        for (AbsEventHandler eventHandler : eventHandlers.values()) {
            eventHandler.handlePeerConnected(displayName, nodeId);
        }

        return true;
    }

    // 没有path 所以需要遍历
    @Override
    public boolean handlePeerDisconnected(String displayName, String nodeId) {
        // TODO Auto-generated method stub
        for (AbsEventHandler eventHandler : eventHandlers.values()) {
            eventHandler.handlePeerDisconnected(displayName, nodeId);
        }

        return true;
    }

    private final class ServiceEventCallback extends EventCallback {

        @Override
        public void callbackMessage(String path, byte[] rawData) {
            // TODO Auto-generated method stub
            if (userService != null)
                userService.onMessageReceived(path, rawData);
        }

        @Override
        public void callbackBitmap(String path, Bitmap bitmap) {
            // TODO Auto-generated method stub
            if (userService != null)
                userService.onBitmapReceived(path, bitmap);
        }

        @Override
        public void callbackFile(SpecialData data) {
            // TODO Auto-generated method stub
            if (userService != null) {
                data.setListener(userService);
                userService.onFileReceived(data);
            }
        }

        @Override
        public void callbackStream(SpecialData data) {
            // TODO Auto-generated method stub
            if (userService != null) {
                data.setListener(userService);
                userService.onStreamReceived(data);
            }
        }

        @Override
        public void callInputClosed(String path) {
            if (userService != null)
                userService.onInputClosed(path);
        }

        @Override
        public void callbackDataMap(String path, DataMap dataMap) {
            // TODO Auto-generated method stub
            if (userService != null)
                userService.onDataMapReceived(path, dataMap);
        }

        @Override
        public void callbackData(String path, byte[] rawData) {
            // TODO Auto-generated method stub
            if (userService != null)
                userService.onDataReceived(path, rawData);
        }

        @Override
        public void callbackDataDeleted(String path) {
            // TODO Auto-generated method stub
            if (userService != null)
                userService.onDataDeleted(path);
        }

        @Override
        public void callbackPeerConnected(String displayName, String nodeId) {
            // TODO Auto-generated method stub
            if (userService != null)
                userService.onPeerConnected(displayName, nodeId);
        }

        @Override
        public void callbackPeerDisconnected(String displayName, String nodeId) {
            // TODO Auto-generated method stub
            if (userService != null)
                userService.onPeerDisconnected(displayName, nodeId);
        }

        @Override
        public void callbackServiceConnectionSuspended(int cause) {
            // TODO Auto-generated method stub
            // 自定义的监听service中不需要也不存在回调这个
        }

        @Override
        public void callbackServiceConnected() {
            // 自定义的监听service中不需要也不存在回调这个
        }
    }

}