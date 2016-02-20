package cn.openwatch.internal.communication.os.duwear;

import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;
import android.os.RemoteException;

import org.owa.wear.ows.DataEventBuffer;
import org.owa.wear.ows.common.data.c;
import org.owa.wear.ows.internal.q;
import org.owa.wear.ows.internal.t;
import org.owa.wear.ows.internal.v;

import cn.openwatch.communication.service.OpenWatchListenerService;
import cn.openwatch.internal.communication.IServiceBinder;
import cn.openwatch.internal.communication.event.ServiceEventHandler;

public class DuwearServiceBinder extends q.a implements IServiceBinder {

    private OpenWatchListenerService service;
    private volatile int uid = -1;
    private Handler handler;
    private boolean isDestory;
    private ServiceEventHandler eventHandler;
    private DuwearEventDataParser dataParser;

    // for反射
    public DuwearServiceBinder() {
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
        dataParser = new DuwearEventDataParser();
    }

    @Override
    public void onServiceDestory() {
        // TODO Auto-generated method stub
        synchronized (DuwearServiceBinder.class) {
            isDestory = true;
        }
    }

    private void checkSecurity() throws SecurityException {
        int i = Binder.getCallingUid();
        if (i == uid) {
            return;
        }
        if ((checkCallerUid("org.owa.wear.ows", i))) {
            uid = i;
            return;
        }
        throw new SecurityException("Caller is not Open Wear Service");
    }

    private boolean checkCallerUid(String paramString, int paramInt) {
        PackageManager localPackageManager = service.getPackageManager();
        String[] arrayOfString1 = localPackageManager.getPackagesForUid(paramInt);
        for (String str : arrayOfString1) {
            if (str.equals(paramString)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void a(final c paramc) throws RemoteException {
        checkSecurity();
        synchronized (DuwearServiceBinder.class) {
            if (isDestory) {
                paramc.h();
                return;
            }
            handler.post(new Runnable() {
                public void run() {
                    DataEventBuffer localDataEventBuffer = new DataEventBuffer(paramc);
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
    public void a(final t paramt) throws RemoteException {
        checkSecurity();
        synchronized (DuwearServiceBinder.class) {
            if (isDestory) {
                return;
            }
            handler.post(new Runnable() {
                public void run() {
                    String path = paramt.getPath();

                    byte[] rawData = paramt.getData();

                    eventHandler.handleMessageReceived(path, rawData);
                }
            });
        }
    }

    @Override
    public void a(final v paramv) throws RemoteException {
        checkSecurity();
        synchronized (DuwearServiceBinder.class) {
            if (isDestory) {
                return;
            }
            handler.post(new Runnable() {
                public void run() {
                    eventHandler.handlePeerConnected(paramv.getDisplayName(), paramv.getId());
                }
            });
        }
    }

    @Override
    public void b(final v paramv) throws RemoteException {
        checkSecurity();
        synchronized (DuwearServiceBinder.class) {
            if (isDestory) {
                return;
            }
            handler.post(new Runnable() {
                public void run() {
                    eventHandler.handlePeerDisconnected(paramv.getDisplayName(), paramv.getId());
                }
            });
        }
    }

}
