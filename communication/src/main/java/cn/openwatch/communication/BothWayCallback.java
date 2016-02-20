package cn.openwatch.communication;

/**
 * 配对设备间的消息双向请求响应的状态监听
 */
public interface BothWayCallback {

    /**
     * 配对设备响应回来的数据
     *
     * @param rawData 响应的原始数据
     */
    void onResponsed(byte[] rawData);

    /**
     * 配对设备响应数据失败
     *
     * @param error 失败的原因
     */
    void onError(ErrorStatus error);
}