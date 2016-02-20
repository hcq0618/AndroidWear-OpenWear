package cn.openwatch.communication.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.IBinder;

import java.util.List;

import cn.openwatch.communication.DataMap;
import cn.openwatch.communication.SpecialData;
import cn.openwatch.communication.listener.ConnectListener;
import cn.openwatch.communication.listener.DataListener;
import cn.openwatch.communication.listener.MessageListener;
import cn.openwatch.communication.listener.SpecialTypeListener;
import cn.openwatch.internal.communication.IServiceBinder;
import cn.openwatch.internal.communication.os.duwear.DuwearServiceBinder;
import cn.openwatch.internal.communication.os.ticwear.TicwearServiceBinder;

/**
 * 数据通信和设备连接的监听服务 用于接收配对设备发送过来的数据 以及监听与配对设备的连接状态
 * 不需要自己管理WearableListenerService的生命周期，当有数据发送过来时会自动bind service 当不需要再工作时
 * 会自动unbind service
 */
public abstract class OpenWatchListenerService extends Service
        implements DataListener, ConnectListener, MessageListener, SpecialTypeListener {

    private IServiceBinder binder;

    private static final String DUWEAR_BIND_INTENT_ACTION = "org.owa.wear.ows.BIND_LISTENER";
    private static final String GOOGLE_BIND_INTENT_ACTION = "com.google.android.gms.wearable.BIND_LISTENER";
    private static final String TICWEAR_BIND_INTENT_ACTION = "com.mobvoi.android.wearable.BIND_LISTENER";

    @Override
    public void onDestroy() {
        if (binder != null) {
            binder.onServiceDestory();
        }
        super.onDestroy();
    }

    // 只有采用Context.bindService()方法启动服务时才会回调该方法。该方法在调用者与服务绑定时被调用，当调用者与服务已经绑定，
    // 多次调用Context.bindService()方法并不会导致该方法被多次调用。
    @Override
    public final IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        String action = intent.getAction();

        if (GOOGLE_BIND_INTENT_ACTION.equals(action)) {
            // 当国内版和谷歌版android wear同时兼容时 使用任意一个binder都可以
            // 当只兼容某一个时 哪个binder类可以被加载 则使用哪个
            try {

                cn.openwatch.internal.communication.os.google.AwServiceBinder binder = (cn.openwatch.internal.communication.os.google.AwServiceBinder) Class
                        .forName("cn.openwatch.internal.communication.os.google.AwServiceBinder").newInstance();
                binder.setService(this);
                this.binder = binder;

            } catch (Exception e) {

                try {
                    cn.openwatch.internal.communication.os.google.china.AwServiceBinder binder = (cn.openwatch.internal.communication.os.google.china.AwServiceBinder) Class
                            .forName("cn.openwatch.internal.communication.os.google.china.AwServiceBinder")
                            .newInstance();
                    binder.setService(this);
                    this.binder = binder;

                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                }

            }

        } else if (DUWEAR_BIND_INTENT_ACTION.equals(action)) {

            try {
                DuwearServiceBinder binder = (DuwearServiceBinder) Class
                        .forName("cn.openwatch.internal.communication.os.duwear.DuwearServiceBinder").newInstance();
                binder.setService(this);
                this.binder = binder;

            } catch (Exception e) {
                // TODO Auto-generated catch block
            }

        } else if (TICWEAR_BIND_INTENT_ACTION.equals(action)) {

            try {
                TicwearServiceBinder binder = (TicwearServiceBinder) Class
                        .forName("cn.openwatch.internal.communication.os.ticwear.TicwearServiceBinder").newInstance();
                binder.setService(this);
                this.binder = binder;

            } catch (Exception e) {
                // TODO Auto-generated catch block
            }

        }

        if (binder != null) {
            binder.onServiceBind();
        }
        return (IBinder) binder;
    }

    @Override
    public void onMessageReceived(String path, byte[] rawData) {
    }

    @Override
    public void onBitmapReceived(String path, Bitmap bitmap) {
    }

    @Override
    public void onFileReceived(SpecialData data) {

    }

    @Override
    public void onStreamReceived(SpecialData data) {
    }

    @Override
    public void onInputClosed(String path) {

    }

    @Override
    public void onDataMapReceived(String path, DataMap dataMap) {
    }

    @Override
    public void onDataReceived(String path, byte[] rawData) {
    }

    @Override
    public void onDataDeleted(String path) {
    }

    @Override
    public void onPeerConnected(String displayName, String nodeId) {
    }

    @Override
    public void onPeerDisconnected(String displayName, String nodeId) {
    }

    @Override
    public final void onServiceConnectionSuspended(int cause) {
        // 自定义的监听service中不需要也不存在回调这个
        // 所以该函数声明为final

    }

    @Override
    public final void onServiceConnected() {
        // 自定义的监听service中不需要也不存在回调这个
        // 所以该函数声明为final
    }

    // 检查是否注册了后台监听服务
    public static boolean detectDeclaredService(Context cx) {
        cx = cx.getApplicationContext();

        boolean isDeclared = false;

        String packageName = cx.getPackageName();
        PackageManager manager = cx.getPackageManager();
        String[] surpportActions = {GOOGLE_BIND_INTENT_ACTION, DUWEAR_BIND_INTENT_ACTION, TICWEAR_BIND_INTENT_ACTION};
        for (String action : surpportActions) {
            Intent intent = new Intent(action);
            intent.setPackage(packageName);

            List<ResolveInfo> infos = manager.queryIntentServices(intent, PackageManager.GET_INTENT_FILTERS);
            if (infos != null && infos.size() > 0) {
                isDeclared = true;
                break;
            }
        }

        return isDeclared;
    }
}
