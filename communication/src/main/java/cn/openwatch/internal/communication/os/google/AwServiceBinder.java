package cn.openwatch.internal.communication.os.google;

import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;

import cn.openwatch.communication.service.OpenWatchListenerService;
import cn.openwatch.internal.communication.IServiceBinder;
import cn.openwatch.internal.communication.event.ServiceEventHandler;
import cn.openwatch.internal.google.android.gms.common.data.DataHolder;
import cn.openwatch.internal.google.android.gms.wearable.DataEventBuffer;
import cn.openwatch.internal.google.android.gms.wearable.internal.ae;
import cn.openwatch.internal.google.android.gms.wearable.internal.ai;
import cn.openwatch.internal.google.android.gms.wearable.internal.al;

public final class AwServiceBinder extends ae.a implements IServiceBinder {

    private OpenWatchListenerService service;
    private volatile int uid = -1;
    private Handler handler;
    private boolean isDestory;
    private ServiceEventHandler eventHandler;
    private AwEventDataParser dataParser;

    // for反射
    public AwServiceBinder() {
    }

    public void setService(OpenWatchListenerService service) {
        this.service = service;
    }

    @Override
    public void onServiceBind() {
        // TODO Auto-generated method stub
        handler = new Handler();

        eventHandler = new ServiceEventHandler();
        eventHandler.setUserService(service);
        dataParser = new AwEventDataParser();
    }

    @Override
    public void onServiceDestory() {
        // TODO Auto-generated method stub
        synchronized (AwServiceBinder.class) {
            isDestory = true;
        }
    }

    // 为兼容国内版android wear 此处与源码不同
    private void checkSecurity() throws SecurityException {
        int i = Binder.getCallingUid();
        if (i == uid) {
            return;
        }
        if (checkCallerUid(i)) {
            uid = i;
            return;
        }
        throw new SecurityException("Caller is not GooglePlayServices");
    }

    // 为兼容国内版android wear 此处与源码不同
    private boolean checkCallerUid(int paramInt) {
        PackageManager localPackageManager = service.getPackageManager();
        String[] arrayOfString = localPackageManager.getPackagesForUid(paramInt);
        if (arrayOfString != null) {
            for (int i = 0; i < arrayOfString.length; i++) {
                if ("com.google.android.gms".equals(arrayOfString[i])
                        || "com.google.android.wearable.app.cn".equals(arrayOfString[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void aa(final DataHolder paramDataHolder) {
        checkSecurity();
        synchronized (AwServiceBinder.class) {
            if (isDestory) {
                paramDataHolder.close();
                return;
            }
            handler.post(new Runnable() {
                public void run() {
                    DataEventBuffer localDataEventBuffer = new DataEventBuffer(paramDataHolder);

                    try {
                        // 数据统计和取消操作的行为要保证数据必达 需要用dataAPI
                        dataParser.dispatchDataChanged(service, localDataEventBuffer, eventHandler);
                    } finally {
                        localDataEventBuffer.release();
                    }
                }
            });
        }
    }

    @Override
    public void a(final ai paramai) {
        checkSecurity();
        synchronized (AwServiceBinder.class) {
            if (isDestory) {
                return;
            }
            handler.post(new Runnable() {
                public void run() {

                    String path = paramai.getPath();

                    byte[] rawData = paramai.getData();

                    eventHandler.handleMessageReceived(path, rawData);
                }
            });
        }
    }

    @Override
    public void a(final al paramal) {
        checkSecurity();
        synchronized (AwServiceBinder.class) {
            if (isDestory) {
                return;
            }
            handler.post(new Runnable() {
                public void run() {

                    eventHandler.handlePeerConnected(paramal.getDisplayName(), paramal.getId());
                }
            });
        }
    }

    @Override
    public void b(final al paramal) {
        checkSecurity();
        synchronized (AwServiceBinder.class) {
            if (isDestory) {
                return;
            }
            handler.post(new Runnable() {
                public void run() {

                    eventHandler.handlePeerDisconnected(paramal.getDisplayName(), paramal.getId());
                }
            });
        }
    }

}
