package cn.openwatch.communication;

import android.content.Context;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.openwatch.internal.communication.SendPath;
import cn.openwatch.internal.communication.BothWay;
import cn.openwatch.internal.communication.BothWay.InternalBothWayCallback;
import cn.openwatch.internal.http.HttpUtils;
import cn.openwatch.internal.http.InternalHttpCaller;
import cn.openwatch.internal.http.WatchHttpRequest;
import cn.openwatch.internal.http.WatchHttpRequest.Method;
import cn.openwatch.internal.http.WatchHttpResponse;
import cn.openwatch.internal.basic.utils.IOUtils;

/**
 * 用于在手表端直接发起网络请求
 */
public final class OpenWatchHttp implements IHttpCaller {

    /**
     * 设备连接通信失败
     */
    public static final int ERROR_DEVICE_COMMUNICATION_FAIL = -101;
    /**
     * 设备连接服务不可用
     */
    public static final int ERROR_DEVICE_SERVICE_INAVAILABLE = -102;

    /**
     * 响应超时
     */
    public static final int ERROR_TIME_OUT = InternalHttpCaller.TIME_OUT_CODE;

    private Context cx;
    private CopyOnWriteArraySet<BothWayCallbackProxy> responseCallbacks = new CopyOnWriteArraySet<BothWayCallbackProxy>();
    private BothWay bothWay = new BothWay();
    private int timeOut = 15 * 1000;
    private static IHttpCaller customHttpCaller;

    public OpenWatchHttp(Context context) {
        cx = context.getApplicationContext();

        bothWay.setTimeOutMills(timeOut);
    }

    /**
     * 自定义http请求的实现
     *
     * @param httpCaller http请求的接口实现
     */
    public static void setCustomHttpCaller(IHttpCaller httpCaller) {
        customHttpCaller = httpCaller;
    }

    public static IHttpCaller getCustomHttpCaller() {
        return customHttpCaller;
    }

    /**
     * 设置手表端网络请求超时 默认15秒
     *
     * @param timeOut 超时时间
     */
    @Override
    public void setTimeOutMills(int timeOut) {
        this.timeOut = timeOut;
        bothWay.setTimeOutMills(timeOut);
    }

    public int getTimeOutMills() {
        // TODO Auto-generated method stub
        return timeOut;
    }

    private class BothWayCallbackProxy extends InternalBothWayCallback {

        private HttpCallback callback;
        private AtomicBoolean isCanceled = new AtomicBoolean(false);

        protected BothWayCallbackProxy(HttpCallback callback) {
            // TODO Auto-generated constructor stub
            this.callback = callback;
        }

        public void setCanceled(boolean isCanceled) {
            this.isCanceled.getAndSet(isCanceled);
        }

        @Override
        public void onResponsed(byte[] rawData) {
            // TODO Auto-generated method stub
            // 手机端http响应成功

            if (!isCanceled.get() && callback != null) {

                WatchHttpResponse httpResponse = new WatchHttpResponse();
                httpResponse.fromJson(rawData == null ? "" : new String(rawData));

                byte[] responseData = null;

                if (httpResponse.response != null)
                    responseData = IOUtils.stringToBytes(httpResponse.response);

                if (httpResponse.isSuccess) {
                    callback.onResponse(responseData, httpResponse.status, httpResponse.headers);
                } else {
                    callback.onError(responseData, httpResponse.status, httpResponse.headers);
                }
            }

            responseCallbacks.remove(this);
        }

        @Override
        public void onError(ErrorStatus error) {
            // TODO Auto-generated method stub

            if (error == ErrorStatus.TIME_OUT) {
                // 手机端http响应超时
                if (!isCanceled.get() && callback != null) {
                    callback.onError(null, ERROR_TIME_OUT, null);
                }

                OpenWatchSender.sendMsg(cx, SendPath.HTTP_CANCEL, "", null);
                
            } else {
                // 手表端http请求发送失败

                if (!isCanceled.get() && callback != null) {
                    callback.onError(null, connectStatus2Code(error), null);
                }
            }

            responseCallbacks.remove(this);
        }

        private int connectStatus2Code(ErrorStatus status) {
            int code = ERROR_DEVICE_COMMUNICATION_FAIL;

            switch (status) {
                case SERVICE_INVAILABLE:
                    code = ERROR_DEVICE_SERVICE_INAVAILABLE;
                    break;

                default:
                    break;
            }

            return code;
        }

    }

    /**
     * 手表端发起get网络请求
     *
     * @param url      网络请求的url
     * @param callback 网络请求的响应回调
     */
    public void get(String url, HttpCallback callback) {
        // TODO Auto-generated method stub
        getWithHeaders(url, null, null, callback);
    }

    /**
     * 手表端发起get网络请求
     *
     * @param url      网络请求的url
     * @param params   网络请求的键值对参数
     * @param callback 网络请求的响应回调
     */
    public void get(String url, Map<String, String> params, HttpCallback callback) {
        // TODO Auto-generated method stub
        getWithHeaders(url, params, null, callback);
    }

    /**
     * 手表端发起get网络请求
     *
     * @param url      网络请求的url
     * @param headers  自定义网络请求的http headers
     * @param callback 网络请求的响应回调
     */
    public void getWithHeaders(String url, Map<String, String> headers, HttpCallback callback) {
        // TODO Auto-generated method stub
        getWithHeaders(url, null, headers, callback);
    }

    /**
     * 手表端发起get网络请求
     *
     * @param url      网络请求的url
     * @param params   网络请求的键值对参数
     * @param headers  自定义网络请求的http headers
     * @param callback 网络请求的响应回调
     */
    @Override
    public void getWithHeaders(String url, Map<String, String> params, Map<String, String> headers,
                               HttpCallback callback) {
        // TODO Auto-generated method stub
        WatchHttpRequest request = new WatchHttpRequest();
        request.url = HttpUtils.makeRequestUrl(url, params);
        request.headers = headers;
        request.method = Method.GET;
        request.timeOut = timeOut;

        BothWayCallbackProxy callbackProxy = new BothWayCallbackProxy(callback);
        bothWay.sendRequest(cx, SendPath.HTTP_REQUEST, request.toJson().getBytes(), callbackProxy);

        responseCallbacks.add(callbackProxy);
    }

    /**
     * 手表端发起post网络请求
     *
     * @param url      网络请求的url
     * @param callback 网络请求的响应回调
     */
    public void post(String url, HttpCallback callback) {
        // TODO Auto-generated method stub
        postWithHeaders(url, null, callback);
    }

    /**
     * 手表端发起post网络请求
     *
     * @param url      网络请求的url
     * @param params   网络请求的键值对提交参数
     * @param callback 网络请求的响应回调
     */
    public void post(String url, Map<String, String> params, HttpCallback callback) {
        // TODO Auto-generated method stub
        postWithHeaders(url, params, null, callback);
    }

    /**
     * 手表端发起post网络请求
     *
     * @param url      网络请求的url
     * @param body     网络请求的提交实体
     * @param callback 网络请求的响应回调
     */
    public void post(String url, String body, HttpCallback callback) {
        // TODO Auto-generated method stub
        try {
            postWithHeaders(url, body == null ? "".getBytes() : body.getBytes("utf-8"), null, callback);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
        }
    }

    /**
     * 手表端发起post网络请求
     *
     * @param url      网络请求的url
     * @param headers  自定义网络请求的http headers
     * @param callback 网络请求的响应回调
     */
    public void postWithHeaders(String url, Map<String, String> headers, HttpCallback callback) {
        // TODO Auto-generated method stub
        postWithHeaders(url, "".getBytes(), headers, callback);
    }

    /**
     * 手表端发起post网络请求
     *
     * @param url      网络请求的url
     * @param params   网络请求的键值对提交参数
     * @param headers  自定义网络请求的http headers
     * @param callback 网络请求的响应回调
     */
    public void postWithHeaders(String url, Map<String, String> params, Map<String, String> headers,
                                HttpCallback callback) {
        // TODO Auto-generated method stub
        try {
            postWithHeaders(url, params == null ? "".getBytes() : HttpUtils.makeParams(params).getBytes("utf-8"),
                    headers, callback);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
        }
    }

    /**
     * 手表端发起post网络请求
     *
     * @param url      网络请求的url
     * @param body     网络请求的提交实体
     * @param headers  自定义网络请求的http headers
     * @param callback 网络请求的响应回调
     */
    public void postWithHeaders(String url, String body, Map<String, String> headers, HttpCallback callback) {
        // TODO Auto-generated method stub
        try {
            postWithHeaders(url, body == null ? "".getBytes() : body.getBytes("utf-8"), headers, callback);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
        }
    }

    /**
     * 手表端发起post网络请求
     *
     * @param url      网络请求的url
     * @param body     网络请求的提交实体
     * @param headers  自定义网络请求的http headers
     * @param callback 网络请求的响应回调
     */
    @Override
    public void postWithHeaders(String url, byte[] body, Map<String, String> headers, HttpCallback callback) {
        // TODO Auto-generated method stub
        WatchHttpRequest request = new WatchHttpRequest();
        request.url = url;
        request.headers = headers;
        request.method = Method.POST;
        request.timeOut = timeOut;
        request.rawbodyToString = IOUtils.bytesToString(body);

        BothWayCallbackProxy callbackProxy = new BothWayCallbackProxy(callback);
        bothWay.sendRequest(cx, SendPath.HTTP_REQUEST, request.toJson().getBytes(), callbackProxy);

        responseCallbacks.add(callbackProxy);
    }

    /**
     * 取消手表端所有发起的网络请求
     */
    public void cancelAll() {
        // TODO Auto-generated method stub
        OpenWatchSender.sendMsg(cx, SendPath.HTTP_CANCEL, "", null);

        for (BothWayCallbackProxy callback : responseCallbacks) {
            callback.setCanceled(true);
            responseCallbacks.remove(callback);
        }
    }

}
