package cn.openwatch.communication.listener;

import cn.openwatch.internal.communication.AbsApiClient;

/**
 * 配对设备间连接状态的监听
 */
public interface ConnectListener {

    /**
     * 与设备的连接丢失了
     */
    int CAUSE_CONNECTION_LOST = AbsApiClient.CAUSE_CONNECTION_LOST;
    /**
     * 系统连接服务被杀了
     */
    int CAUSE_SERVICE_KILLED = AbsApiClient.CAUSE_SERVICE_KILLED;
    /**
     * 未知原因
     */
    int CAUSE_UNKNOWN = AbsApiClient.CAUSE_UNKNOWN;

    /**
     * 配对设备建立连接时回调
     *
     * @param displayName 设备名称
     * @param nodeId      设备id
     */
    void onPeerConnected(String displayName, String nodeId);

    /**
     * 配对设备断开连接时回调
     *
     * @param displayName 设备名称
     * @param nodeId      设备id
     */
    void onPeerDisconnected(String displayName, String nodeId);

    /**
     * 与连接服务意外断开时回调
     *
     * @param cause 断开的原因
     * @see #CAUSE_CONNECTION_LOST
     * @see #CAUSE_SERVICE_KILLED
     * @see #CAUSE_UNKNOWN
     */
    void onServiceConnectionSuspended(int cause);

    /**
     * 与连接服务连接上后回调
     */
    void onServiceConnected();
}
