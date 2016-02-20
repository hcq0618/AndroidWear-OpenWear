package cn.openwatch.internal.communication;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import cn.openwatch.communication.listener.SendListener;
import cn.openwatch.internal.basic.ThreadsManager;

public class SendDataTask extends AsyncTask<Void, Void, SendStatus> {

    private Context cx;
    private String path;
    private SendListener listener;
    private boolean isForceChange;
    private Handler handler;

    public SendDataTask(Context cx, String path, boolean isForceChange, SendListener listener) {
        this.cx = cx.getApplicationContext();
        this.path = path;
        this.listener = listener;
        this.isForceChange = isForceChange;
    }

    @Override
    protected SendStatus doInBackground(Void... params) {
        // TODO Auto-generated method stub

        CacheManager.addSelfCommunicationCount(cx);

        byte[] data = buildData();
        if (data == null)
            return SendStatus.FAIL;

        return Sender.sendDataAwait(cx, path, data, isForceChange);
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