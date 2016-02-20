package cn.openwatch.communication.listener;

/**
 * 配对设备间数据通信状态的监听
 */
public interface MessageListener {
    /**
     * 接收到配对设备发送来的临时性数据
     *
     * @param path    与数据相关联的key 例如:"/openwatch/path"
     * @param rawData 收到的原始数据
     */
    void onMessageReceived(String path, byte[] rawData);

}
