package cn.openwatch.internal.basic;

import android.content.Context;
import android.os.Process;
import android.text.TextUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.openwatch.internal.basic.utils.AppUtils;

/**
 * sdk基本参数设置及初始化
 */
public class Config {

    private static String appKey;

    private static String channelName;

    protected Config() {
    }

    /**
     * 设置sdk内部任务处理的全局线程池 默认使用CachedThreadPool 创建非守护线程
     * 线程优先级为Process.THREAD_PRIORITY_BACKGROUND
     *
     * @param threadPool 设置的线程池
     * @see Executors#newCachedThreadPool
     * @see Process#THREAD_PRIORITY_BACKGROUND
     */
    public static void setTheadPool(ExecutorService threadPool) {
        ThreadsManager.setDefaultThreadPool(threadPool);
    }

    /**
     * 设置appkey
     *
     * @param appKey
     */
    public static void setAppKey(String appKey) {
        Config.appKey = appKey;
    }

    public static String getAppKey(Context context) {
        if (TextUtils.isEmpty(appKey)) {
            appKey = AppUtils.getMeta(context, BasicConstants.OPENWATCH_APPKEY);
        }

        return appKey;
    }

    /**
     * 设置应用渠道号
     *
     * @param channelName 应用渠道号 用于统计分析
     */
    public static void setChannelName(String channelName) {
        Config.channelName = channelName;
    }


    public static String getChannelName(Context context) {
        if (TextUtils.isEmpty(channelName)) {
            channelName = AppUtils.getMeta(context, BasicConstants.OPENWATCH_CHANNEL_NAME);
        }

        return channelName;
    }

}
