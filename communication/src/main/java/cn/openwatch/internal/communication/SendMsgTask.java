package cn.openwatch.internal.communication;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import cn.openwatch.communication.listener.SendListener;
import cn.openwatch.internal.basic.ThreadsManager;
import cn.openwatch.internal.basic.utils.LogUtils;

public class SendMsgTask extends AsyncTask<Void, Void, SendStatus> {
    private Context cx;
    private String path;
    private SendListener listener;
    private Handler handler;

    public SendMsgTask(Context cx, String path, SendListener listener) {
        this.cx = cx.getApplicationContext();
        this.path = path;
        this.listener = listener;
    }

    @Override
    protected SendStatus doInBackground(Void... params) {
        // TODO Auto-generated method stub

        CacheManager.addSelfCommunicationCount(cx);

        byte[] data = buildData();
        if (data == null)
            return SendStatus.FAIL;

        SendStatus status = Sender.sendMsgAwait(cx, path, data);

        LogUtils.d(SendMsgTask.class, "send msg status:" + status);

        return status;
    }

    protected byte[] buildData() {
        return null;
    }

    @Override
    protected void onPostExecute(SendStatus status) {
        // TODO Auto-generated method stub
        if (listener != null) {

            if (status == SendStatus.SUCCESS) {
                listener.onSuccess();
            } else {
                listener.onError(status.convert2ErrorStatus());
            }

        }
    }

    @TargetApi(11)
    public void start() {
        
        if (Looper.myLooper() != Looper.getMainLooper()) {
            // 在子线程调用
            final SendStatus status = doInBackground();

            if (handler == null)
                handler = new Handler(Looper.getMainLooper());

            handler.post(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    onPostExecute(status);
                }
            });

        } else {

            if (Build.VERSION.SDK_INT >= 11) {
                executeOnExecutor(ThreadsManager.getDefaultThreadPool());
            } else {
                execute();
            }

        }
    }
}