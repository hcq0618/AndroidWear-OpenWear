package cn.openwatch.internal.communication;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.openwatch.communication.ErrorStatus;
import cn.openwatch.communication.OpenWatchSender;
import cn.openwatch.communication.listener.SendListener;

public final class BothWay {

    private static Timer timer;
    private long timeOutMills = 10 * 1000;
    private static Handler handler;

    //无法确定响应的超时时间 则一直等待
    private boolean isIndeterminateTimeOut;

    private static CopyOnWriteArrayList<InternalBothWayCallback> callbacks = new CopyOnWriteArrayList<InternalBothWayCallback>();

    public static abstract class InternalBothWayCallback {

        private String requestPath;
        private long timeStamp;

        public abstract void onResponsed(byte[] rawData);

        public abstract void onError(ErrorStatus error);
    }

    public void setTimeOutMills(long timeOut) {
        timeOutMills = timeOut;
    }

    public void setIndeterminateTimeOut(boolean isIndeterminate) {
        isIndeterminateTimeOut = isIndeterminate;
    }

    public void sendRequest(Context context, String path, byte[] data, final InternalBothWayCallback callback) {

        if (TextUtils.isEmpty(path))
            return;

        if (callback != null) {
            callback.requestPath = path.substring(path.lastIndexOf(SendPath.PROTOCOL_SPLIT) + 1);

            callback.timeStamp = System.currentTimeMillis();

            callbacks.add(callback);

            startTimeOutTimer(callback.timeStamp);
        }
        
        OpenWatchSender.sendMsg(context, path, data, new SendListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
            }

            @Override
            public void onError(ErrorStatus error) {
                // TODO Auto-generated method stub
                if (callback != null && error != ErrorStatus.TIME_OUT) {
                    callback.onError(error);

                    callbacks.remove(callback);
                    destoryIfNeed();
                }
            }
        });

    }

    public static void sendResponse(Context context, String requestPath, byte[] responseData, SendListener listener) {
        if (TextUtils.isEmpty(requestPath))
            return;

        OpenWatchSender.sendMsg(context, requestPath, responseData, listener);
    }

    public static void onResponse(String requestPath, byte[] rawData) {

        for (InternalBothWayCallback callback : callbacks) {
            if (TextUtils.equals(requestPath, callback.requestPath)) {
                callback.onResponsed(rawData);
                callbacks.remove(callback);
            }
        }

        destoryIfNeed();

    }

    private static void destoryIfNeed() {

        if (callbacks.isEmpty() && timer != null) {
            synchronized (BothWay.class) {
                if (timer != null) {
                    timer.cancel();
                    timer.purge();
                    timer = null;
                }

                handler = null;
            }
        }
    }

    private void startTimeOutTimer(final long timeStamp) {
        if (isIndeterminateTimeOut) {
            return;
        }

        synchronized (BothWay.class) {
            if (timer == null) {
                timer = new Timer(false);
            }

            if (handler == null) {
                handler = new Handler();
            }
        }

        // 若超时未收到消息 则视为超时
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                for (final InternalBothWayCallback callback : callbacks) {
                    if (callback.timeStamp == timeStamp) {
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                callback.onError(ErrorStatus.TIME_OUT);
                            }
                        });

                        callbacks.remove(callback);
                    }
                }

                destoryIfNeed();

            }
        }, timeOutMills);

    }

}
