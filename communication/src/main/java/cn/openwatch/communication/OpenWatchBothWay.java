package cn.openwatch.communication;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.io.InputStream;

import cn.openwatch.communication.listener.SendListener;
import cn.openwatch.internal.basic.utils.FileUtils;
import cn.openwatch.internal.communication.SendPath;
import cn.openwatch.internal.communication.BothWay;
import cn.openwatch.internal.communication.BothWay.InternalBothWayCallback;
import cn.openwatch.internal.basic.utils.IOUtils;

/**
 * 配对设备间的消息双向请求响应
 */
public final class OpenWatchBothWay {

    private static BothWay bothWay = new BothWay();

    private OpenWatchBothWay() {
    }

    /**
     * 设置响应超时 默认10秒
     *
     * @param mills 超时时间
     */
    public static void setTimeOutMills(long mills) {
        bothWay.setTimeOutMills(mills);
    }

    /**
     * 无法确定响应的超时时间 想要一直等待 默认为false
     *
     * @param isIndeterminate 是否无法确定响应的超时时间
     */
    public static void setIndeterminateTimeOut(boolean isIndeterminate) {
        bothWay.setIndeterminateTimeOut(isIndeterminate);
    }

    private static class BothWayCallbackProxy extends InternalBothWayCallback {
        private BothWayCallback callback;

        public BothWayCallbackProxy(BothWayCallback callback) {
            // TODO Auto-generated constructor stub
            this.callback = callback;
        }

        @Override
        public void onResponsed(byte[] rawData) {
            // TODO Auto-generated method stub
            if (callback != null) {
                callback.onResponsed(rawData);
            }
        }

        @Override
        public void onError(ErrorStatus error) {
            // TODO Auto-generated method stub
            if (callback != null)
                callback.onError(error);
        }
    }

    /**
     * 向配对设备发送数据 并监听配对设备的响应数据
     *
     * @param context  context
     * @param path     消息请求的key
     * @param data     要发送的数据 其他数据类型可用{@link String#valueOf(boolean)},
     *                 {@link String#valueOf(int)}等方式转换
     * @param callback 消息响应回调
     */
    public static void request(Context context, String path, String data, BothWayCallback callback) {
        request(context, path, data.getBytes(), callback);
    }

    /**
     * 向配对设备发送数据 并监听配对设备的响应数据
     *
     * @param context  context
     * @param path     消息请求的key
     * @param data     要发送的数据
     * @param callback 消息响应回调
     */
    public static void request(Context context, String path, byte[] data, BothWayCallback callback) {

        bothWay.sendRequest(context, SendPath.BOTHWAY_REQUEST + path,
                data == null ? "".getBytes() : data, new BothWayCallbackProxy(callback));
    }

    /**
     * 向配对设备发送图片 并监听配对设备的响应数据
     *
     * @param context  context
     * @param path     消息请求的key
     * @param bitmap   要发送的图片
     * @param callback 消息响应回调
     */
    public static void request(Context context, String path, Bitmap bitmap, BothWayCallback callback) {
        byte[] raw = IOUtils.bitmapToBytes(bitmap);

        bothWay.sendRequest(context, SendPath.BOTHWAY_REQUEST_BITMAP + path,
                raw == null ? "".getBytes() : raw, new BothWayCallbackProxy(callback));
    }

    /**
     * 向配对设备发送文件 并监听配对设备的响应数据
     *
     * @param context  context
     * @param path     消息请求的key
     * @param file     要发送的文件
     * @param callback 消息响应回调
     */
    public static void request(Context context, String path, File file, BothWayCallback callback) {
        byte[] raw = FileUtils.fileToBytes(file);

        bothWay.sendRequest(context, SendPath.BOTHWAY_REQUEST_FILE + path,
                raw == null ? "".getBytes() : raw, new BothWayCallbackProxy(callback));
    }

    /**
     * 向配对设备发送数据流 并监听配对设备的响应数据
     *
     * @param context  context
     * @param path     消息请求的key
     * @param stream   要发送的数据流
     * @param callback 消息响应回调
     */
    public static void request(Context context, String path, InputStream stream, BothWayCallback callback) {
        byte[] raw = IOUtils.streamToBytes(stream);

        bothWay.sendRequest(context, SendPath.BOTHWAY_REQUEST_STREAM + path,
                raw == null ? "".getBytes() : raw, new BothWayCallbackProxy(callback));
    }

    /**
     * 响应配对设备的请求消息
     *
     * @param context     context
     * @param requestPath 消息请求的key 要与request的key保持一致
     * @param response    要响应的数据
     * @param listener    消息响应的响应监听
     */
    public static void response(Context context, String requestPath, String response, SendListener listener) {

        response(context, requestPath, response.getBytes(), listener);
    }

    /**
     * 响应配对设备的请求消息
     *
     * @param context     context
     * @param requestPath 消息请求的key 要与request的key保持一致
     * @param response    要响应的数据
     */
    public static void response(Context context, String requestPath, String response) {

        response(context, requestPath, response.getBytes(), null);
    }

    /**
     * 响应配对设备的请求消息
     *
     * @param context      context
     * @param requestPath  消息请求的key 要与request的key保持一致
     * @param responseData 要响应的数据 图片或文件可以转成byte[]来传
     */
    public static void response(Context context, String requestPath, byte[] responseData) {

        response(context, requestPath, responseData, null);
    }

    /**
     * 响应配对设备的请求消息
     *
     * @param context      context
     * @param requestPath  消息请求的key 要与request的key保持一致
     * @param responseData 要响应的数据 图片或文件可以转成byte[]来传
     * @param listener     消息响应的响应监听
     */
    public static void response(Context context, String requestPath, byte[] responseData, SendListener listener) {

        BothWay.sendResponse(context, SendPath.BOTHWAY_RESPONSE + requestPath, responseData, listener);
    }

}
