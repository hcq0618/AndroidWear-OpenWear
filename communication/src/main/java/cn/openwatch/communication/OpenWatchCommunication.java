package cn.openwatch.communication;

import android.content.Context;

import java.util.concurrent.atomic.AtomicBoolean;

import cn.openwatch.internal.communication.ClientManager;
import cn.openwatch.internal.communication.Config;
import cn.openwatch.internal.communication.SupportClient;

/**
 * Created by hcq0618 on 2015/10/30.
 */
public final class OpenWatchCommunication extends cn.openwatch.internal.basic.Config {

    private static AtomicBoolean init = new AtomicBoolean(false);

    public static final int CONNECTION_TYPE_ANDROID_WEAR = SupportClient.TYPE_ANDROID_WEAR;
    public static final int CONNECTION_TYPE_ANDROID_WEAR_CHINA = SupportClient.TYPE_ANDROID_WEAR_CHINA;
    public static final int CONNECTION_TYPE_TICWEAR = SupportClient.TYPE_TICWEAR;
    public static final int CONNECTION_TYPE_DUWEAR = SupportClient.TYPE_DUWEAR;
    public static final int CONNECTION_TYPE_UNKNOWN = SupportClient.TYPE_UNKNOWN;

    public static final String UNKNOWN_OS_NAME = ClientManager.UNKNOWN_CLIENT_TYPE_NAME;

    private OpenWatchCommunication() {
    }

    public interface FetchWearableCallback {
        void onFetchOsName(String osName);
    }

    /**
     * 初始化
     *
     * @param context
     * @return 是否初始化成功 若初始化失败或已初始化过 则返回false
     */
    public static boolean init(Context context) {

        if (!init.getAndSet(true)) {

            Config.init(context, null);

            return true;
        }

        return false;
    }

    /**
     * 获取当前连接方式的类型
     *
     * @param context
     * @return int 连接方式的类型
     * @see #CONNECTION_TYPE_ANDROID_WEAR
     * @see #CONNECTION_TYPE_ANDROID_WEAR_CHINA
     * @see #CONNECTION_TYPE_DUWEAR
     * @see #CONNECTION_TYPE_TICWEAR
     * @see #CONNECTION_TYPE_UNKNOWN
     */
    public static int getConnectionType(Context context) {
        return ClientManager.getInstance().getConnectionType(context);
    }


    /**
     * 获取上一次连接过的手表设备的系统名称
     *
     * @param context
     * @return 上一次连接过的手表设备的系统名称 若从未连接过手表设备 则返回{@link #UNKNOWN_OS_NAME}
     * @see #UNKNOWN_OS_NAME
     */
    public static String getLastKnownWearableOsName(Context context) {

        String osType = ClientManager.getInstance().getLastKnownClientTypeName(context);

        return osType;
    }

    /**
     * 当前手表设备是否已连接
     *
     * @return 当前手表设备是否已连接
     */
    public static boolean isConnectedDevice() {
        return ClientManager.getInstance().isConnectedDevice();
    }

    /**
     * 实时获取手表设备系统名称 获取失败则返回{@link #UNKNOWN_OS_NAME}
     *
     * @param context  context
     * @param callback 回调
     * @see #UNKNOWN_OS_NAME
     */
    public static void getWearableOsName(Context context, final FetchWearableCallback callback) {
        ClientManager.getInstance().getClientTypeName(context, new ClientManager.GetClientTypeNameCallback() {
            @Override
            public void onGetClientTypeName(String typeName) {
                if (callback != null)
                    callback.onFetchOsName(typeName);
            }
        });
    }

    /**
     * 实时获取手表设备系统名称 这是个同步函数 需要在子线程中调用
     *
     * @param context context
     * @return 系统名称 获取失败则返回{@link #UNKNOWN_OS_NAME}
     * @see #UNKNOWN_OS_NAME
     */
    public static String getWearableOsNameSync(Context context) {
        return ClientManager.getInstance().getClientTypeNameAwait(context);
    }
}
