package cn.openwatch.communication;

import java.util.Map;

/**
 * 手表端发起网络请求的响应回调
 */
public interface HttpCallback {

    /**
     * 响应成功
     *
     * @param data       网络响应的原始数据
     * @param statusCode 网络响应http状态码
     * @param headers    网络响应的headers
     */
    void onResponse(byte[] data, int statusCode, Map<String, String> headers);

    /**
     * 响应失败
     *
     * @param data      网络响应的原始数据
     * @param errorCode 响应的错误码 若小于0 则为设备通信错误 大于0 则为网络响应http状态码
     * @param headers   网络响应的headers
     * @see OpenWatchHttp#ERROR_DEVICE_COMMUNICATION_FAIL
     * @see OpenWatchHttp#ERROR_DEVICE_SERVICE_INAVAILABLE
     * @see OpenWatchHttp#ERROR_TIME_OUT
     */
    void onError(byte[] data, int errorCode, Map<String, String> headers);
}