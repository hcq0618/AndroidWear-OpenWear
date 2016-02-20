package cn.openwatch.internal.communication;

import android.content.Context;
import android.text.TextUtils;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import cn.openwatch.internal.communication.event.AbsEventObserver;
import cn.openwatch.internal.basic.utils.LogUtils;

public abstract class AbsApiClient<Client> {
    protected Context cx;
    protected Client apiClient;
    protected AbsDataApi<Client> dataApi;
    protected AbsMessageApi<Client> messageApi;
    protected long connectTimeOutMills, getNodesIdTimeOutMills;
    protected ServiceConnectionListener connectionListener;
    private ReentrantLock connectLocker = new ReentrantLock();

    // A suspension cause informing you that a peer device connection was lost.
    public static final int CAUSE_CONNECTION_LOST = 2;
    // A suspension cause informing that the service has been killed.
    public static final int CAUSE_SERVICE_KILLED = 1;
    public static final int CAUSE_UNKNOWN = 0;

    // for反射
    public AbsApiClient() {
    }

    public static AbsApiClient<?> newInstanceByReflect(Context cx, String className) {
        try {
            if (!TextUtils.isEmpty(className)) {
                AbsApiClient<?> client = (AbsApiClient<?>) Class.forName(className).newInstance();
                client.setContext(cx);
                return client;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
        }

        return null;
    }

    public interface ServiceConnectionListener {
        void onConnected();

        void onConnectionSuspended(int cause);
    }

    protected void setContext(Context cx) {
        this.cx = cx.getApplicationContext();
    }

    public void setConnectionListener(ServiceConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }

    public Client getApiClient() {
        return apiClient;
    }

    protected void setConnectTimeOutMills(long mills) {
        this.connectTimeOutMills = mills;
    }

    protected void setGetNodesIdTimeOutMills(long mills) {
        this.getNodesIdTimeOutMills = mills;
    }

    protected abstract Client initClient();

    protected abstract boolean isServiceConnected();

    protected abstract boolean isAppAndServiceAvailable();

    protected abstract void disconnectService();

    protected abstract int blockingConnectService();

    protected abstract boolean isSuccessCode(int code);

    protected abstract boolean isTimeOutCode(int code);

    protected abstract boolean isServiceInvailableCode(int code);

    protected abstract List<String> getConnectedNodesIdAwait();

    protected abstract String getLocalNodeIdAwait();

    protected abstract int getType();

    protected abstract String getTypeName();

    protected abstract AbsDataApi<Client> getDataApi();

    protected abstract AbsMessageApi<Client> getMessageApi();

    protected abstract AbsEventObserver<?> getEventObserver();

    private ConnectStatus convert2ConnectStatus(int code) {

        ConnectStatus result = ConnectStatus.CONNECT_SERVICE_FAIL;

        if (isSuccessCode(code)) {
            result = ConnectStatus.CONNECT_SERVICE_SUCCESS;
        } else if (isTimeOutCode(code)) {
            result = ConnectStatus.TIME_OUT;
        } else if (isServiceInvailableCode(code)) {
            result = ConnectStatus.SERVICE_INVAILABLE;
        }

        return result;
    }

    private void createClientIfNeed() {
        if (apiClient == null) {
            apiClient = initClient();
        }
    }

    protected ConnectStatus connectAwaitIfNeed() {

        // 如果正在走disconnectIfNeed 则等连接断开 再重连
        // 如果正在走connectAwaitIfNeed 则等到连接完 会直接返回
        connectLocker.lock();

        try {

            if (isServiceConnected())
                return ConnectStatus.CONNECT_SERVICE_SUCCESS;

            createClientIfNeed();

            int code = blockingConnectService();

            LogUtils.d(this, "connectAwaitIfNeed code " + code);

            return convert2ConnectStatus(code);

        } finally {
            connectLocker.unlock();
        }
    }

    protected boolean tryDisconnect() {
        // 如果正在走disconnectIfNeed 则直接返回
        // 如果正在走connectAwaitIfNeed 则直接返回
        if (connectLocker.isLocked()) {
            return false;
        } else {

            connectLocker.lock();

            try {
                if (isServiceConnected()) {
                    disconnectService();
                    return true;
                }
            } finally {
                connectLocker.unlock();
            }

            return false;

        }
    }

}
