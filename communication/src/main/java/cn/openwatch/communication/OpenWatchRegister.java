package cn.openwatch.communication;

import android.content.Context;

import cn.openwatch.communication.listener.ConnectListener;
import cn.openwatch.communication.listener.DataListener;
import cn.openwatch.communication.listener.MessageListener;
import cn.openwatch.communication.listener.SpecialTypeListener;
import cn.openwatch.internal.communication.event.EventRegister;

/**
 * 配对设备间设备连接和数据通信的的状态监听
 */
public final class OpenWatchRegister {

    /**
     * 添加data数据接收的事件监听
     *
     * @param context 实现了{@link DataListener} 回调接口的context
     * @throws IllegalArgumentException context没有实现DataListener接口
     * @see #addDataListener(Context, DataListener)
     */
    public static void addDataListener(Context context) {

        EventRegister.getInstance().addDataListener(context);
    }

    /**
     * 添加data数据接收的事件监听
     *
     * @param context  context
     * @param listener 事件监听回调
     * @see #addDataListener(Context)
     */
    public static void addDataListener(Context context, DataListener listener) {

        EventRegister.getInstance().addListener(context, listener);
    }

    /**
     * 删除data数据接收的事件监听
     *
     * @param listener 事件监听回调
     */
    public static void removeDataListener(DataListener listener) {
        EventRegister.getInstance().removeListener(listener);
    }

    /**
     * 添加设备连接的事件监听
     *
     * @param context 实现了{@link ConnectListener}回调接口的context
     * @throws IllegalArgumentException context没有实现ConnectListener接口
     * @see #addConnectListener(Context, ConnectListener)
     */
    public static void addConnectListener(Context context) {

        EventRegister.getInstance().addConnectListener(context);
    }

    /**
     * 添加设备连接的事件监听
     *
     * @param context  context
     * @param listener 事件监听回调
     * @see #addConnectListener(Context)
     */
    public static void addConnectListener(Context context, ConnectListener listener) {

        EventRegister.getInstance().addListener(context, listener);
    }

    /**
     * 删除设备连接的事件监听
     *
     * @param listener 事件监听回调
     */
    public static void removeConnectListener(ConnectListener listener) {
        EventRegister.getInstance().removeListener(listener);
    }

    /**
     * 添加临时性或时效性数据接收的事件监听
     *
     * @param context 实现了{@link MessageListener} 回调接口的context
     * @throws IllegalArgumentException context没有实现MessageListener接口
     * @see #addMessageListener(Context, MessageListener)
     */
    public static void addMessageListener(Context context) {

        EventRegister.getInstance().addMessageListener(context);
    }

    /**
     * 添加临时性或时效性数据接收的事件监听
     *
     * @param context  context
     * @param listener 事件监听回调
     * @see #addMessageListener(Context)
     */
    public static void addMessageListener(Context context, MessageListener listener) {

        EventRegister.getInstance().addListener(context, listener);
    }

    /**
     * 删除临时性或时效性数据接收的事件监听
     *
     * @param listener 事件监听回调
     */
    public static void removeMessageListener(MessageListener listener) {
        EventRegister.getInstance().removeListener(listener);
    }

    /**
     * 添加特殊数据（图片、datamap等）接收的事件监听
     *
     * @param context 实现了{@link SpecialTypeListener} 回调接口的context
     * @throws IllegalArgumentException context没有实现SpecialDataListener接口
     * @see #addSpecialTypeListener(Context, SpecialTypeListener)
     */
    public static void addSpecialTypeListener(Context context) {

        EventRegister.getInstance().addSpecialTypeListener(context);
    }

    /**
     * 添加特殊数据（图片、datamap等）接收的事件监听
     *
     * @param context  context
     * @param listener 事件监听回调
     * @see #addSpecialTypeListener(Context)
     */
    public static void addSpecialTypeListener(Context context, SpecialTypeListener listener) {

        EventRegister.getInstance().addListener(context, listener);
    }

    /**
     * 删除特殊数据（图片、datamap等）接收的事件监听
     *
     * @param listener 事件监听回调
     */
    public static void removeSpecialDataListener(SpecialTypeListener listener) {
        EventRegister.getInstance().removeListener(listener);
    }

}
