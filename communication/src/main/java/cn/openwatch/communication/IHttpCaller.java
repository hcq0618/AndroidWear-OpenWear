package cn.openwatch.communication;

import java.util.Map;

/**
 * 自定义网络请求的实现接口
 */
public interface IHttpCaller {

    /**
     * 设置请求超时
     *
     * @param timeOut 超时时间
     */
    void setTimeOutMills(int timeOut);

    /**
     * get请求的自定义实现
     *
     * @param url      请求的url
     * @param params   请求的参数
     * @param headers  请求头
     * @param callback 请求的回调
     */
    void getWithHeaders(String url, Map<String, String> params, Map<String, String> headers,
                        HttpCallback callback);

    /**
     * post请求的自定义实现
     *
     * @param url      请求的url
     * @param body     请求的实体
     * @param headers  请求头
     * @param callback 请求的回调
     */
    void postWithHeaders(String url, byte[] body, Map<String, String> headers, HttpCallback callback);

    /**
     * 取消网络请求
     *
     */
    void cancelAll();
}
