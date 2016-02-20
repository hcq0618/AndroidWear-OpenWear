package cn.openwatch.internal.http;

import android.content.Context;

import org.apache.http.Header;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHeader;

import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import cn.openwatch.communication.HttpCallback;
import cn.openwatch.communication.IHttpCaller;
import cn.openwatch.internal.basic.ThreadsManager;
import cn.openwatch.internal.basic.utils.AppUtils;
import cn.openwatch.internal.basic.utils.LogUtils;
import cn.openwatch.internal.http.loopj.AsyncHttpClient;
import cn.openwatch.internal.http.loopj.AsyncHttpResponseHandler;
import cn.openwatch.internal.http.loopj.RequestParams;
import cn.openwatch.internal.http.loopj.SyncHttpClient;
import cn.openwatch.internal.basic.utils.DeviceUtils;

public final class InternalHttpCaller implements IHttpCaller {

    public static final int TIME_OUT_CODE = -1;
    public static final int NO_NETWORK_CODE = -2;

    private static AsyncHttpClient asyncClient = new AsyncHttpClient();
    private static SyncHttpClient syncClient = new SyncHttpClient();
    private Long tag;
    private Context cx;

    public InternalHttpCaller(Context context) {
        cx = context.getApplicationContext();
        asyncClient.setThreadPool(ThreadsManager.getDefaultThreadPool());
        syncClient.setThreadPool(ThreadsManager.getDefaultThreadPool());
        tag = System.currentTimeMillis();
    }

    // 连接超时+响应超时
    @Override
    public void setTimeOutMills(int timeOut) {
        // TODO Auto-generated method stub
        if (timeOut > 0) {
            asyncClient.setTimeout(timeOut / 2);
            syncClient.setTimeout(timeOut / 2);
        }
    }

    // 连接超时+响应超时
    public int getTimeOutMills() {
        // TODO Auto-generated method stub
        return asyncClient.getConnectTimeout() + asyncClient.getResponseTimeout();
    }

    private Header[] makeRequestHeaders(Map<String, String> headers) {

        if (headers == null || headers.isEmpty()) {
            headers = new HashMap<String, String>();
        }

        if (!headers.containsKey("Content-Type"))
            headers.put("Content-Type", "text/html;charset=utf-8");

        int size = headers.size();
        Header[] headerArray = new Header[size];

        int i = 0;
        for (String key : headers.keySet()) {
            headerArray[i] = new BasicHeader(key, headers.get(key));
            i++;
        }

        return headerArray;
    }

    private HashMap<String, String> makeResponseHeaders(Header[] headers) {
        HashMap<String, String> headerMap = null;

        if (headers != null && headers.length > 0) {
            headerMap = new HashMap<String, String>();
            for (Header header : headers) {
                headerMap.put(header.getName(), header.getValue());
            }
        }

        return headerMap;
    }

    private void handleError(int statusCode, Header[] headers, byte[] responseBody, Throwable error, HttpCallback callback) {
        if (callback != null) {
            if (error != null) {
                LogUtils.d(this, "error className " + error.toString());
                if (error instanceof ConnectTimeoutException ||
                        error instanceof SocketTimeoutException) {
                    LogUtils.d(this, "error isTimeout");
                    statusCode = TIME_OUT_CODE;
                }
            }
            callback.onError(responseBody,
                    statusCode,
                    makeResponseHeaders(headers));
        }
    }

    public void get(String url, Map<String, String> params, HttpCallback callback) {
        getWithHeaders(url, params, null, callback);
    }

    @Override
    public void getWithHeaders(String url, Map<String, String> params, Map<String, String> headers,
                               final HttpCallback callback) {
        if (!DeviceUtils.isNetworkAvailable(cx) || !AppUtils.checkAppPermission(cx, "android.permission.INTERNET")) {
            handleError(NO_NETWORK_CODE, null, null, null, callback);
            return;
        }

        asyncClient
                .get(null, url, makeRequestHeaders(headers), new RequestParams(params), new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        // TODO Auto-generated method stub
                        if (callback != null) {
                            callback.onResponse(responseBody, statusCode, makeResponseHeaders(headers));
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        // TODO Auto-generated method stub
                        handleError(statusCode, headers, responseBody, error, callback);
                    }
                }).setTag(tag);
    }

    public void post(String url, Map<String, String> params, HttpCallback callback) {
        postWithHeaders(url, params, null, callback);
    }

    public void post(String url, String body, HttpCallback callback) {
        try {
            postWithHeaders(url, body.getBytes("utf-8"), null, callback);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
        }
    }

    public void postSync(String url, byte[] body, final HttpCallback callback) {

        if (!DeviceUtils.isNetworkAvailable(cx) || !AppUtils.checkAppPermission(cx, "android.permission.INTERNET")) {
            handleError(NO_NETWORK_CODE, null, null, null, callback);
            return;
        }

        ByteArrayEntity entity = new ByteArrayEntity(body);
        syncClient.post(null, url, makeRequestHeaders(null), entity, null, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // TODO Auto-generated method stub
                if (callback != null) {
                    callback.onResponse(responseBody, statusCode, makeResponseHeaders(headers));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // TODO Auto-generated method stub
                handleError(statusCode, headers, responseBody, error, callback);
            }
        }).setTag(tag);
    }

    public void postWithHeaders(String url, Map<String, String> params, Map<String, String> headers,
                                final HttpCallback callback) {

        if (!DeviceUtils.isNetworkAvailable(cx) || !AppUtils.checkAppPermission(cx, "android.permission.INTERNET")) {
            handleError(NO_NETWORK_CODE, null, null, null, callback);
            return;
        }

        asyncClient.post(null, url, makeRequestHeaders(headers), new RequestParams(params), null,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        // TODO Auto-generated method stub
                        if (callback != null) {
                            callback.onResponse(responseBody, statusCode, makeResponseHeaders(headers));
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        // TODO Auto-generated method stub
                        handleError(statusCode, headers, responseBody, error, callback);
                    }
                }).setTag(tag);
    }

    @Override
    public void postWithHeaders(String url, byte[] body, Map<String, String> headers, final HttpCallback callback) {
        // TODO Auto-generated method stub

        if (!DeviceUtils.isNetworkAvailable(cx) || !AppUtils.checkAppPermission(cx, "android.permission.INTERNET")) {
            handleError(NO_NETWORK_CODE, null, null, null, callback);
            return;
        }

        ByteArrayEntity entity = new ByteArrayEntity(body);
        asyncClient.post(null, url, makeRequestHeaders(headers), entity, null, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // TODO Auto-generated method stub
                if (callback != null) {
                    callback.onResponse(responseBody, statusCode, makeResponseHeaders(headers));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // TODO Auto-generated method stub
                handleError(statusCode, headers, responseBody, error, callback);
            }
        }).setTag(tag);
    }

    @Override
    public void cancelAll() {
        asyncClient.cancelRequestsByTAG(tag, true);
        syncClient.cancelRequestsByTAG(tag, true);
    }

}
