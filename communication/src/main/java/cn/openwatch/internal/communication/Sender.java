package cn.openwatch.internal.communication;

import android.content.Context;
import android.os.Looper;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import cn.openwatch.internal.basic.utils.LogUtils;

public final class Sender {

    private static long sendTimeOutMills = 10 * 1000;
    private static final Timer timer = new Timer();
    private static AtomicInteger sendTaskCounter = new AtomicInteger();// 已连接数

    private Sender() {
    }

    public static void setSendTimeOutMills(long mills) {
        sendTimeOutMills = mills;
    }

    private static ConnectStatus detectAwaitIfNeed(Context cx) {
        sendTaskCounter.incrementAndGet();

        return ClientManager.getInstance().detectAwaitIfNeed(cx);
    }

    // 全部任务执行完后自动断开
    private static void disconnectIfNeed() {
        if (sendTaskCounter.get() > 0)
            sendTaskCounter.decrementAndGet();

        LogUtils.d(ClientManager.class, "remain conn size:" + sendTaskCounter.get());

        ClientManager.getInstance().tryDisconnect();
    }

    protected static int getSendTaskCount() {
        return sendTaskCounter.get();
    }

    protected static void resetSendTaskCount() {
        sendTaskCounter.getAndSet(0);
    }

    public static SendStatus delectDataItemsAwait(Context cx, ArrayList<String> deletePathList) {
        if (deletePathList == null || deletePathList.isEmpty())
            return SendStatus.FAIL;

        ConnectStatus connectStatus = detectAwaitIfNeed(cx);

        SendStatus sendStatus;
        if (connectStatus.isDeviceConnected()) {
            sendStatus = ClientManager.getInstance().getClient().getDataApi().deleteDataItemsAwait(deletePathList);
        } else {
            sendStatus = SendStatus.convertConnectStatus(connectStatus);
        }

        disconnectIfNeed();

        return sendStatus;

    }

    public static SendStatus sendDataAwait(Context cx, String path, byte[] data, boolean isForceChange) {
        SendStatus status = SendStatus.FAIL;

        if (path == null || data == null)
            return status;

        // 不能interrupted主线程
        if (Looper.myLooper() == Looper.getMainLooper()) {
            return status;
        }

        SendTimerTask timerTask = startTimer();

        ConnectStatus connectStatus = detectAwaitIfNeed(cx);

        if (connectStatus.isDeviceConnected()) {
            // 超过100kb
            if (data.length >= 1024 * 100)
                status = ClientManager.getInstance().getClient().getDataApi()
                        .sendDataNoLimitAwait(SendPath.BIG_DATA_STAMP + path, data, isForceChange);
            else
                status = ClientManager.getInstance().getClient().getDataApi().sendDataAwait(path, data, isForceChange);
        } else {
            status = SendStatus.convertConnectStatus(connectStatus);
        }

        // thread.interrupt执行后 下面的操作依然会继续执行 只是中断了await操作
        // 因为最底层通信服务trycatch了interruptedException
        if (timerTask.isTimeOut) {
            status = SendStatus.TIME_OUT;
        }

        timerTask.cancel();
        timer.purge();

        disconnectIfNeed();

        return status;
    }

    public static SendStatus sendMsgAwait(Context cx, String path, byte[] msgData) {
        SendStatus status = SendStatus.FAIL;

        if (path == null || msgData == null)
            return status;

        // 不能interrupted主线程
        if (Looper.myLooper() == Looper.getMainLooper()) {
            return status;
        }

        SendTimerTask timerTask = startTimer();

        ConnectStatus connectStatus = detectAwaitIfNeed(cx);

        if (connectStatus.isDeviceConnected()) {
            status = ClientManager.getInstance().getClient().getMessageApi().sendMsgAwait(path, msgData);
        } else {
            status = SendStatus.convertConnectStatus(connectStatus);
        }

        // thread.interrupt执行后 下面的操作依然会继续执行 只是中断了await操作
        // 因为最底层通信服务trycatch了interruptedException
        if (timerTask.isTimeOut) {
            status = SendStatus.TIME_OUT;
        }

        timerTask.cancel();
        timer.purge();

        disconnectIfNeed();

        return status;
    }

    private static SendTimerTask startTimer() {
        // 不能直接设置总超时时间给client和messager 因为可能需要遍历每个系统平台
        // 但无法估计出每个系统平台的连接/配对/超时和总超时时间的分配关系
        // http://deepfuture.iteye.com/blog/599682
        // 由于futureTask的局限性 但仿造其原理用timertask来限制总超时时间
        // 一旦到了总超时时间 则interrupted当前线程
        SendTimerTask timerTask = new SendTimerTask();
        timerTask.thread = Thread.currentThread();
        timer.schedule(timerTask, sendTimeOutMills);

        return timerTask;
    }

    private static class SendTimerTask extends TimerTask {

        public boolean isTimeOut;
        public Thread thread;

        @Override
        public void run() {
            // TODO Auto-generated method stub
            isTimeOut = true;

            // Threads blocked in one of Object's wait() methods or one of
            // Thread's join() or sleep() methods
            // will be woken up, their interrupt status will be cleared, and
            // they receive an InterruptedException.
            thread.interrupt();
        }
    }
}
