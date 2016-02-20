package cn.openwatch.communication;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import cn.openwatch.communication.listener.SendListener;
import cn.openwatch.communication.service.OpenWatchListenerService;
import cn.openwatch.internal.basic.ThreadsManager;
import cn.openwatch.internal.basic.utils.FileUtils;
import cn.openwatch.internal.communication.SendPath;
import cn.openwatch.internal.communication.SendDataTask;
import cn.openwatch.internal.communication.SendMsgTask;
import cn.openwatch.internal.communication.Sender;
import cn.openwatch.internal.basic.utils.IOUtils;

/**
 * 配对设备间的数据发送 不需要关心连接/通信/数据层
 * <p>
 * 连接自动管理 在不需要的时候 会自己断开
 * <p>
 * 支持设备一连多
 * <p>
 * 目前支持的智能手表操作系统有:Android Wear、Duwear、Ticwear
 */
public final class OpenWatchSender {

    private OpenWatchSender() {
    }

    /**
     * 设置发送数据的超时时间 默认10秒
     *
     * @param mills 超时时间
     */
    public static void setTimeOutMills(int mills) {
        if (mills > 0) {
            Sender.setSendTimeOutMills(mills);
        }
    }

    /**
     * 删除向配对设备发送的数据
     *
     * @param context        context
     * @param deletePathList 要删除的与数据相关联的key 例如:"/openwatch/path"
     */
    public static void batchDeleteData(Context context, final ArrayList<String> deletePathList) {
        final Context cx = context.getApplicationContext();

        ThreadsManager.execute(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Sender.delectDataItemsAwait(cx, deletePathList);
            }
        });
    }

    /**
     * 发送数据给配对设备 当配对设备未连接 数据并不会被丢失 会在下次连接上配对设备时接收到数据 数据没有大小限制<br>
     * <br>
     * 无论发送的data是否发生改变 配对设备都会接收到数据 且只有配对设备会接收到<br>
     * 若想要只有data发生改变时 配对设备才接收到数据 则应该使用
     * {@link #sendData(Context, String, String, boolean, SendListener)}
     * 并给isForceChange参数传false
     * <p>
     * <br>
     * <br>
     * 配对设备接收数据有两种方式：<br>
     * <br>
     * 1. 继承{@link OpenWatchListenerService} <br>
     * 2. 使用{@link OpenWatchRegister}
     *
     * @param context  context
     * @param path     与数据相关联的key 例如:"/openwatch/path"
     * @param data     要发送的数据 其他数据类型可用{@link String#valueOf(boolean)},
     *                 {@link String#valueOf(int)}等方式转换
     * @param listener 数据发送状态的回调监听
     * @see #sendData(Context, String, String, boolean, SendListener)
     */
    public static void sendData(Context context, String path, String data, SendListener listener) {
        sendData(context, path, data == null ? "".getBytes() : data.getBytes(), listener);
    }

    /**
     * 发送数据给配对设备 当配对设备未连接 数据并不会被丢失 会在下次连接上配对设备时接收到数据 数据没有大小限制<br>
     * <br>
     * 无论发送的data是否发生改变 配对设备都会接收到数据 且只有配对设备会接收到<br>
     * 若想要只有data发生改变时 配对设备才接收到数据 则应该使用
     * {@link #sendData(Context, String, byte[], boolean, SendListener)}
     * 并给isForceChange参数传false
     * <p>
     * <br>
     * <br>
     * 配对设备接收数据有两种方式：<br>
     * <br>
     * 1. 继承{@link OpenWatchListenerService} <br>
     * 2. 使用{@link OpenWatchRegister}
     *
     * @param context  context
     * @param path     与数据相关联的key 例如:"/openwatch/path"
     * @param data     要发送的数据
     * @param listener 数据发送状态的回调监听
     * @see #sendData(Context, String, byte[], boolean, SendListener)
     */
    public static void sendData(Context context, String path, byte[] data, SendListener listener) {
        sendData(context, path, data, true, listener);
    }

    /**
     * 发送数据给配对设备 当配对设备未连接 数据并不会被丢失 会在下次连接上配对设备时接收到数据 数据没有大小限制<br>
     * <br>
     * isForceChange为false时 若发送的data未发生改变 配对设备只会接收到一次数据 且只有配对设备会接收到<br>
     * isForceChange为true时 无论发送的data是否发生改变 配对设备都能接收到数据 且只有配对设备会接收到
     * <p>
     * <br>
     * <br>
     * 配对设备接收数据有两种方式：<br>
     * <br>
     * 1. 继承{@link OpenWatchListenerService} <br>
     * 2. 使用{@link OpenWatchRegister}
     *
     * @param context       context
     * @param path          与数据相关联的key 例如:"/openwatch/path"
     * @param data          要发送的数据 其他数据类型可用{@link String#valueOf(boolean)},
     *                      {@link String#valueOf(int)}等方式转换
     * @param isForceChange 若为true 无论data是否发生改变 配对设备都能接收到数据 且只有配对设备会接收到
     * @param listener      数据发送状态的回调监听
     */
    public static void sendData(Context context, String path, String data, boolean isForceChange,
                                SendListener listener) {
        sendData(context, path, data == null ? "".getBytes() : data.getBytes(), isForceChange, listener);
    }

    /**
     * 发送数据给配对设备 当配对设备未连接 数据并不会被丢失 会在下次连接上配对设备时接收到数据 数据没有大小限制<br>
     * <br>
     * isForceChange为false时 若发送的data未发生改变 配对设备只会接收到一次数据 且只有配对设备会接收到<br>
     * isForceChange为true时 无论发送的data是否发生改变 配对设备都能接收到数据 且只有配对设备会接收到
     * <p>
     * <br>
     * <br>
     * 配对设备接收数据有两种方式：<br>
     * <br>
     * 1. 继承{@link OpenWatchListenerService} <br>
     * 2. 使用{@link OpenWatchRegister}
     *
     * @param context       context
     * @param path          与数据相关联的key 例如:"/openwatch/path"
     * @param rawData       要发送的数据
     * @param isForceChange 若为true 无论data是否发生改变 配对设备都能接收到数据 且只有配对设备会接收到
     * @param listener      数据发送状态的回调监听
     */
    public static void sendData(Context context, String path, final byte[] rawData, boolean isForceChange,
                                SendListener listener) {

        new SendDataTask(context, path, isForceChange, listener) {

            @Override
            protected byte[] buildData() {
                return rawData == null ? "".getBytes() : rawData;
            }
        }.start();

    }

    /**
     * 发送键值对给配对设备 当配对设备未连接 数据并不会被丢失 会在下次连接上配对设备时接收到数据 数据没有大小限制<br>
     * <br>
     * 无论键值对是否发生改变 配对设备都能接收到数据 且只有配对设备会接收到 <br>
     * <br>
     * 配对设备接收数据有两种方式：<br>
     * <br>
     * 1. 继承{@link OpenWatchListenerService} <br>
     * 2. 使用{@link OpenWatchRegister}
     *
     * @param context  context
     * @param path     与数据相关联的key 例如:"/openwatch/path"
     * @param dataMap  要发送的键值对
     * @param listener 数据发送状态的回调监听
     * @see #sendData(Context, String, DataMap, SendListener)
     */
    public static void sendData(Context context, String path, final DataMap dataMap, SendListener listener) {
        if (dataMap == null) {
            if (listener != null)
                listener.onError(ErrorStatus.SEND_FAIL);
            return;
        }

        new SendDataTask(context, SendPath.SEND_DATAMAP + path, true, listener) {

            @Override
            protected byte[] buildData() {
                // TODO Auto-generated method stub
                return dataMap.toJson().getBytes();
            }
        }.start();
    }

    /**
     * 发送图片给配对设备 当配对设备未连接 数据并不会被丢失 会在下次连接上配对设备时接收到数据 数据没有大小限制<br>
     * <br>
     * 无论bitmap是否发生改变 配对设备都能接收到数据 且只有配对设备会接收到 <br>
     * <br>
     * 配对设备接收数据有两种方式：<br>
     * <br>
     * 1. 继承{@link OpenWatchListenerService} <br>
     * 2. 使用{@link OpenWatchRegister}
     *
     * @param context  context
     * @param path     与数据相关联的key 例如:"/openwatch/path"
     * @param bitmap   要发送的图片
     * @param listener 数据发送状态的回调监听
     */
    public static void sendData(Context context, String path, final Bitmap bitmap, SendListener listener) {
        if (bitmap == null) {
            if (listener != null)
                listener.onError(ErrorStatus.SEND_FAIL);
            return;
        }

        new SendDataTask(context, SendPath.SEND_BITMAP + path, true, listener) {

            @Override
            protected byte[] buildData() {
                byte[] raw = IOUtils.bitmapToBytes(bitmap);
                if (raw != null) {
                    return raw;
                }

                return null;
            }

        }.start();
    }

    /**
     * 发送文件给配对设备 当配对设备未连接 数据并不会被丢失 会在下次连接上配对设备时接收到数据 数据没有大小限制<br>
     * <br>
     * 无论文件是否发生改变 配对设备都能接收到数据 且只有配对设备会接收到 <br>
     * <br>
     * 配对设备接收数据有两种方式：<br>
     * <br>
     * 1. 继承{@link OpenWatchListenerService} <br>
     * 2. 使用{@link OpenWatchRegister}
     *
     * @param context  context
     * @param path     与数据相关联的key 例如:"/openwatch/path"
     * @param file     要发送的文件
     * @param listener 数据发送状态的回调监听
     */
    public static void sendData(Context context, String path, final File file, SendListener listener) {
        if (file == null || !file.exists()) {
            if (listener != null)
                listener.onError(ErrorStatus.SEND_FAIL);
            return;
        }

        new SendDataTask(context, SendPath.SEND_FILE + path, true, listener) {

            @Override
            protected byte[] buildData() {
                byte[] raw = FileUtils.fileToBytes(file);
                if (raw != null) {
                    return raw;
                }

                return null;
            }

        }.start();
    }

    /**
     * 发送数据流给配对设备 当配对设备未连接 数据并不会被丢失 会在下次连接上配对设备时接收到数据 数据没有大小限制<br>
     * <br>
     * 无论数据流是否发生改变 配对设备都能接收到数据 且只有配对设备会接收到 <br>
     * <br>
     * 配对设备接收数据有两种方式：<br>
     * <br>
     * 1. 继承{@link OpenWatchListenerService} <br>
     * 2. 使用{@link OpenWatchRegister}
     *
     * @param context  context
     * @param path     与数据相关联的key 例如:"/openwatch/path"
     * @param stream   要发送的数据流
     * @param listener 数据发送状态的回调监听
     */
    public static void sendData(Context context, String path, final InputStream stream, SendListener listener) {
        if (stream == null) {
            if (listener != null)
                listener.onError(ErrorStatus.SEND_FAIL);
            return;
        }

        new SendDataTask(context, SendPath.SEND_STREAM + path, true, listener) {

            @Override
            protected byte[] buildData() {
                byte[] raw = IOUtils.streamToBytes(stream);
                if (raw != null) {
                    return raw;
                }

                return null;
            }

        }.start();
    }

    /**
     * 发送临时性数据给配对设备 当配对设备未连接 数据会被丢失
     *
     * @param context context
     * @param path    与数据相关联的key 例如:"/openwatch/path"
     */
    public static void sendMsg(Context context, String path) {
        sendMsg(context, path, null);
    }

    /**
     * 发送临时性数据给配对设备 当配对设备未连接 数据会被丢失
     *
     * @param context  context
     * @param path     与数据相关联的key 例如:"/openwatch/path"
     * @param listener 数据发送状态的回调监听
     */
    public static void sendMsg(Context context, String path, SendListener listener) {
        sendMsg(context, path, "".getBytes(), listener);
    }

    /**
     * 发送临时性数据给配对设备 当配对设备未连接 数据会被丢失 数据没有大小限制
     *
     * @param context  context
     * @param path     与数据相关联的key 例如:"/openwatch/path"
     * @param data     要发送的数据 其他数据类型可用{@link String#valueOf(boolean)},
     *                 {@link String#valueOf(int)}等方式转换
     * @param listener 数据发送状态的回调监听
     */
    public static void sendMsg(Context context, String path, String data, SendListener listener) {
        sendMsg(context, path, data == null ? "".getBytes() : data.getBytes(), listener);
    }

    /**
     * 发送临时性图片给配对设备 当配对设备未连接 数据会被丢失 数据没有大小限制
     *
     * @param context  context
     * @param path     与数据相关联的key 例如:"/openwatch/path"
     * @param bitmap   要发送的图片
     * @param listener 数据发送状态的回调监听
     */
    public static void sendMsg(Context context, String path, final Bitmap bitmap, SendListener listener) {
        if (bitmap == null) {
            if (listener != null)
                listener.onError(ErrorStatus.SEND_FAIL);
            return;
        }

        new SendMsgTask(context, SendPath.SEND_BITMAP + path, listener) {

            @Override
            protected byte[] buildData() {
                // TODO Auto-generated method stub
                byte[] raw = IOUtils.bitmapToBytes(bitmap);
                if (raw != null) {
                    return raw;
                }

                return null;
            }
        }.start();
    }

    /**
     * 发送临时性键值对给配对设备 当配对设备未连接 数据会被丢失 数据没有大小限制
     *
     * @param context  context
     * @param path     与数据相关联的key 例如:"/openwatch/path"
     * @param dataMap  要发送的键值对
     * @param listener 数据发送状态的回调监听
     */
    public static void sendMsg(Context context, String path, final DataMap dataMap, SendListener listener) {

        if (dataMap == null) {
            if (listener != null)
                listener.onError(ErrorStatus.SEND_FAIL);
            return;
        }

        new SendMsgTask(context, SendPath.SEND_DATAMAP + path, listener) {

            @Override
            protected byte[] buildData() {
                // TODO Auto-generated method stub
                return dataMap.toJson().getBytes();
            }
        }.start();
    }

    /**
     * 发送临时性文件给配对设备 当配对设备未连接 数据会被丢失 数据没有大小限制
     *
     * @param context  context
     * @param path     与数据相关联的key 例如:"/openwatch/path"
     * @param file     要发送的文件
     * @param listener 数据发送状态的回调监听
     */
    public static void sendMsg(Context context, String path, final File file, SendListener listener) {
        if (file == null || !file.exists()) {
            if (listener != null)
                listener.onError(ErrorStatus.SEND_FAIL);
            return;
        }

        new SendMsgTask(context, SendPath.SEND_FILE + path, listener) {

            @Override
            protected byte[] buildData() {
                // TODO Auto-generated method stub

                byte[] raw = FileUtils.fileToBytes(file);
                if (raw != null) {
                    return raw;
                }

                return null;
            }
        }.start();
    }

    /**
     * 发送临时性数据流给配对设备 当配对设备未连接 数据会被丢失 数据没有大小限制
     *
     * @param context  context
     * @param path     与数据相关联的key 例如:"/openwatch/path"
     * @param stream   要发送的数据流
     * @param listener 数据发送状态的回调监听
     */
    public static void sendMsg(Context context, String path, final InputStream stream, SendListener listener) {
        if (stream == null) {
            if (listener != null)
                listener.onError(ErrorStatus.SEND_FAIL);
            return;
        }

        new SendMsgTask(context, SendPath.SEND_STREAM + path, listener) {

            @Override
            protected byte[] buildData() {
                // TODO Auto-generated method stub

                byte[] raw = IOUtils.streamToBytes(stream);
                if (raw != null) {
                    return raw;
                }

                return null;
            }
        }.start();
    }

    /**
     * 发送临时性字节数组给配对设备 当配对设备未连接 数据会被丢失 数据没有大小限制
     *
     * @param context  context
     * @param path     与数据相关联的key 例如:"/openwatch/path"
     * @param data     要发送的字节数组
     * @param listener 数据发送状态的回调监听
     */
    public static void sendMsg(Context context, String path, final byte[] data, SendListener listener) {

        new SendMsgTask(context, path, listener) {

            @Override
            protected byte[] buildData() {
                // TODO Auto-generated method stub
                return data == null ? "".getBytes() : data;
            }
        }.start();

    }

}
