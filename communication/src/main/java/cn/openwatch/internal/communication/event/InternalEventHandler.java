package cn.openwatch.internal.communication.event;

import java.util.Map;

import cn.openwatch.communication.HttpCallback;
import cn.openwatch.communication.IHttpCaller;
import cn.openwatch.communication.OpenWatchHttp;
import cn.openwatch.internal.basic.utils.DeviceUtils;
import cn.openwatch.internal.basic.utils.IOUtils;
import cn.openwatch.internal.communication.BothWay;
import cn.openwatch.internal.communication.ClientManager;
import cn.openwatch.internal.communication.SendPath;
import cn.openwatch.internal.http.InternalHttpCaller;
import cn.openwatch.internal.http.WatchHttpRequest;
import cn.openwatch.internal.http.WatchHttpRequest.Method;
import cn.openwatch.internal.http.WatchHttpResponse;

//内部协议消息处理 目的为了保证当用户既没有注册监听服务 也没有注册界面上的消息监听 内部功能逻辑依然可用
public final class InternalEventHandler extends AbsEventHandler {

    private IHttpCaller httpCaller;

    // 若消息事件被处理则返回true
    @Override
    public boolean handleMessageReceived(String path, byte[] rawData) {
        // TODO Auto-generated method stub
        
        if (path.equals(SendPath.HTTP_REQUEST)) {
            //接收到手表端网络请求
            handleWatchHttpRequest(rawData);

            return true;
        } else if (path.equals(SendPath.HTTP_CANCEL)) {
            //接收到手表端取消网络请求
            if (httpCaller != null) {
                httpCaller.cancelAll();
            }

            return true;
        } else if (path.startsWith(SendPath.BOTHWAY_RESPONSE)
                || path.startsWith(SendPath.HTTP_RESPONSE)) {
            //接收到双向通信响应||接收到手表端网络请求的响应
            String requestPath = path.substring(path.lastIndexOf(SendPath.PROTOCOL_SPLIT) + 1);
            BothWay.onResponse(requestPath, rawData);

            return true;
        } 
        return false;
    }

    private class HttpListener implements HttpCallback {

        @Override
        public void onResponse(byte[] data, int statusCode, Map<String, String> headers) {
            // TODO Auto-generated method stub

//            LogUtils.d(InternalEventHandler.class, "onResponse:" + statusCode
//                    + "-" + new String(data));

            WatchHttpResponse httpResp = new WatchHttpResponse();
            httpResp.isSuccess = true;
            httpResp.status = statusCode;
            httpResp.headers = headers;
            httpResp.response = IOUtils.bytesToString(data);

            BothWay.sendResponse(getContext(), SendPath.HTTP_RESPONSE + SendPath.HTTP_REQUEST,
                    httpResp.toJson().getBytes(), null);

        }

        @Override
        public void onError(byte[] data, int statusCode, Map<String, String> headers) {
            // TODO Auto-generated method stub

//            LogUtils.d(InternalEventHandler.class, "onError:" + statusCode +
//                    "-" + new String(data));

            WatchHttpResponse httpResp = new WatchHttpResponse();
            httpResp.isSuccess = false;
            httpResp.status = statusCode;
            httpResp.headers = headers;
            httpResp.response = IOUtils.bytesToString(data);

            BothWay.sendResponse(getContext(), SendPath.HTTP_RESPONSE + SendPath.HTTP_REQUEST,
                    httpResp.toJson().getBytes(), null);
        }
    }

    private void handleWatchHttpRequest(byte[] rawData) {
        boolean isWearableDevice = DeviceUtils.isWearableDevice(getContext());

        if (isWearableDevice)
            return;

        WatchHttpRequest request = new WatchHttpRequest();
        request.fromJson(new String(rawData));

        IHttpCaller customHttpCaller = OpenWatchHttp.getCustomHttpCaller();
        if (customHttpCaller == null) {
            if (httpCaller == null || !(httpCaller instanceof InternalHttpCaller))
                httpCaller = new InternalHttpCaller(getContext());
        } else {
            httpCaller = customHttpCaller;
        }

        if (request.timeOut > 0)
            httpCaller.setTimeOutMills(request.timeOut);

        if (request.method == Method.GET) {

            httpCaller.getWithHeaders(request.url, null, request.headers, new HttpListener());

        } else if (request.method == Method.POST) {

            httpCaller.postWithHeaders(request.url, IOUtils.stringToBytes(request.rawbodyToString), request.headers, new HttpListener());

        }
    }

    @Override
    public boolean handlePeerConnected(String displayName, String nodeId) {
        // TODO Auto-generated method stub
        ClientManager.getInstance().resumeIfNeed(getContext());
        return true;
    }

    @Override
    public boolean handlePeerDisconnected(String displayName, String nodeId) {
        // TODO Auto-generated method stub
        // 设备断开连接后 需要重置client 以防止用户更换了手表或卸载了连接服务
        ClientManager.getInstance().reset(true, true);
        return true;
    }

    @Override
    public boolean handleServiceConnectionSuspended(int cause) {
        // TODO Auto-generated method stub
        // 服务断开连接后 需要重置client 以防止用户更换了手表或卸载了连接服务或服务被杀
        ClientManager.getInstance().reset(true, false);
        return true;
    }

    @Override
    public boolean handleServiceConnected() {
        ClientManager.getInstance().resumeIfNeed(getContext());
        return true;
    }

}
