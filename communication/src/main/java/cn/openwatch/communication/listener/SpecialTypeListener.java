package cn.openwatch.communication.listener;

import android.graphics.Bitmap;

import java.io.OutputStream;

import cn.openwatch.communication.SpecialData;

/**
 * 配对设备间特殊数据类型通信状态的监听
 */

//channel api http://stackoverflow.com/questions/30693464/android-wear-channelapi-examples
//https://developers.google.com/android/reference/com/google/android/gms/wearable/ChannelApi

public interface SpecialTypeListener {

    /**
     * 接收到配对设备发送来的图片
     *
     * @param path   与数据相关联的key 例如:"/openwatch/path"
     * @param bitmap 收到的图片
     */
    void onBitmapReceived(String path, Bitmap bitmap);

    /**
     * 接收到配对设备发送来的文件字节流
     *
     * @param data 接收到的文件数据对象 其中包含path和data
     * @see SpecialData#getData()
     * @see SpecialData#getPath()
     */
    void onFileReceived(SpecialData data);

    /**
     * 接收到配对设备发送来的数据流
     *
     * @param data 接收到的文件数据对象 其中包含path和data
     * @see SpecialData#getData()
     * @see SpecialData#getPath()
     */
    void onStreamReceived(SpecialData data);

    /**
     * 调用{@link SpecialData#receiveFile(String)}
     * 或{@link SpecialData#receiveStream(OutputStream)}
     * 后 IO操作结束时回调
     *
     * @param path 与数据相关联的key 例如:"/openwatch/path"
     * @see SpecialData#receiveFile(String)
     * @see SpecialData#receiveStream(OutputStream)
     */
    void onInputClosed(String path);
}
