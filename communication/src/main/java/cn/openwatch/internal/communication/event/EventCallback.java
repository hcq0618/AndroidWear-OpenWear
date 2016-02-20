package cn.openwatch.internal.communication.event;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import cn.openwatch.communication.DataMap;
import cn.openwatch.communication.SpecialData;
import cn.openwatch.communication.listener.ConnectListener;
import cn.openwatch.communication.listener.DataListener;
import cn.openwatch.communication.listener.MessageListener;
import cn.openwatch.communication.listener.SpecialTypeListener;

public abstract class EventCallback implements DataListener, ConnectListener, MessageListener, SpecialTypeListener {

    public abstract void callbackMessage(String path, byte[] rawData);

    public abstract void callbackBitmap(String path, Bitmap bitmap);

    public abstract void callbackFile(SpecialData data);

    public abstract void callbackStream(SpecialData data);

    public abstract void callInputClosed(String path);

    public abstract void callbackDataMap(String path, DataMap dataMap);

    public abstract void callbackData(String path, byte[] rawData);

    public abstract void callbackDataDeleted(String path);

    public abstract void callbackPeerConnected(String displayName, String nodeId);

    public abstract void callbackPeerDisconnected(String displayName, String nodeId);

    public abstract void callbackServiceConnectionSuspended(int cause);

    public abstract void callbackServiceConnected();

    // 1、确保在主线程回调
    // 2、同时防止若proxyservice已销毁
    // 而回调中有异步操作会导致异常sending message to a Handler on a dead thread的问题
    private Handler handler = new Handler();

    @Override
    public void onMessageReceived(final String path, final byte[] rawData) {

        if (Looper.myLooper() == Looper.getMainLooper())
            callbackMessage(path, rawData);
        else
            handler.post(new Runnable() {

                @Override
                public void run() {
                    callbackMessage(path, rawData);
                }
            });

    }

    @Override
    public void onBitmapReceived(final String path, final Bitmap bitmap) {
        if (Looper.myLooper() == Looper.getMainLooper())
            callbackBitmap(path, bitmap);
        else
            handler.post(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    callbackBitmap(path, bitmap);
                }
            });

    }

    @Override
    public void onFileReceived(final SpecialData data) {
        // TODO Auto-generated method stub
        if (Looper.myLooper() == Looper.getMainLooper())
            callbackFile(data);
        else
            handler.post(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    callbackFile(data);
                }
            });

    }

    @Override
    public void onStreamReceived(final SpecialData data) {
        // TODO Auto-generated method stub
        if (Looper.myLooper() == Looper.getMainLooper())
            callbackStream(data);
        else
            handler.post(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    callbackStream(data);
                }
            });

    }

    @Override
    public void onInputClosed(final String path) {
        if (Looper.myLooper() == Looper.getMainLooper())
            callInputClosed(path);
        else
            handler.post(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    callInputClosed(path);
                }
            });
    }

    @Override
    public void onDataMapReceived(final String path, final DataMap dataMap) {
        if (Looper.myLooper() == Looper.getMainLooper())
            callbackDataMap(path, dataMap);
        else
            handler.post(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    callbackDataMap(path, dataMap);
                }
            });

    }

    @Override
    public void onDataReceived(final String path, final byte[] rawData) {
        if (Looper.myLooper() == Looper.getMainLooper())
            callbackData(path, rawData);
        else
            handler.post(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    callbackData(path, rawData);
                }
            });

    }

    @Override
    public void onDataDeleted(final String path) {
        if (Looper.myLooper() == Looper.getMainLooper())
            callbackDataDeleted(path);
        else
            handler.post(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    callbackDataDeleted(path);
                }
            });

    }

    @Override
    public void onPeerConnected(final String displayName, final String nodeId) {
        if (Looper.myLooper() == Looper.getMainLooper())
            callbackPeerConnected(displayName, nodeId);
        else
            handler.post(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    callbackPeerConnected(displayName, nodeId);
                }
            });

    }

    @Override
    public void onPeerDisconnected(final String displayName, final String nodeId) {
        if (Looper.myLooper() == Looper.getMainLooper())
            callbackPeerDisconnected(displayName, nodeId);
        else
            handler.post(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    callbackPeerDisconnected(displayName, nodeId);
                }
            });

    }

    @Override
    public void onServiceConnectionSuspended(final int cause) {
        // TODO Auto-generated method stub
        if (Looper.myLooper() == Looper.getMainLooper())
            callbackServiceConnectionSuspended(cause);
        else
            handler.post(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    callbackServiceConnectionSuspended(cause);
                }
            });

    }

    @Override
    public void onServiceConnected() {
        if (Looper.myLooper() == Looper.getMainLooper())
            callbackServiceConnected();
        else
            handler.post(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    callbackServiceConnected();
                }
            });
    }
}