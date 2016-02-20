package cn.openwatch.internal.communication.os.ticwear;

import android.os.Binder;
import android.os.Handler;
import android.os.RemoteException;

import com.mobvoi.android.wearable.DataEventBuffer;
import com.mobvoi.android.wearable.internal.DataHolder;
import com.mobvoi.android.wearable.internal.IWearableListener;
import com.mobvoi.android.wearable.internal.MessageEventHolder;
import com.mobvoi.android.wearable.internal.NodeHolder;

import cn.openwatch.communication.service.OpenWatchListenerService;
import cn.openwatch.internal.communication.IServiceBinder;
import cn.openwatch.internal.communication.event.ServiceEventHandler;

public class TicwearServiceBinder extends IWearableListener.a implements IServiceBinder {

    private OpenWatchListenerService service;
    private volatile int uid = -1;
    private Handler handler;
    private boolean isDestory;
    private ServiceEventHandler eventHandler;
    private TicwearEventDataParser dataParser;

    // for反射
    public TicwearServiceBinder() {
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
        dataParser = new TicwearEventDataParser();
    }

    @Override
    public void onServiceDestory() {
        // TODO Auto-generated method stub
        synchronized (TicwearServiceBinder.class) {
            isDestory = true;
        }
    }

    private void checkSecurity() {
        int i = Binder.getCallingUid();
        if (i != uid) {
            if (checkCallerUid(i)) {
                uid = i;
            } else {
                throw new SecurityException("Caller is not MobvoiServices");
            }
        }
    }

    private boolean checkCallerUid(int paramInt) {
        String[] arrayOfString1 = service.getPackageManager().getPackagesForUid(paramInt);
        String str1 = service.getPackageName();
        boolean bool = false;
        if (arrayOfString1 != null) {
            for (String str2 : arrayOfString1) {
                if (("com.mobvoi.android".equals(str2)) || ("com.mobvoi.companion".equals(str2))
                        || (str1.equals(str2))) {
                    bool = true;
                    break;
                }
            }
        }
        return bool;
    }

    @Override
    public void onMessageReceived(final MessageEventHolder paramMessageEventHolder) throws RemoteException {
        checkSecurity();
        synchronized (TicwearServiceBinder.class) {
            if (isDestory) {
                return;
            }
            handler.post(new Runnable() {
                public void run() {

                    String path = paramMessageEventHolder.getPath();

                    byte[] rawData = paramMessageEventHolder.getData();

                    eventHandler.handleMessageReceived(path, rawData);
                }
            });
        }

    }

    @Override
    public void onDataChanged(final DataHolder paramDataHolder) throws RemoteException {
        checkSecurity();
        synchronized (TicwearServiceBinder.class) {
            if (isDestory) {
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
    public void onPeerConnected(final NodeHolder paramNodeHolder) throws RemoteException {
        checkSecurity();
        synchronized (TicwearServiceBinder.class) {
            if (isDestory) {
                return;
            }
            handler.post(new Runnable() {
                public void run() {

                    eventHandler.handlePeerConnected(paramNodeHolder.getDisplayName(), paramNodeHolder.getId());
                }
            });
        }
    }

    @Override
    public void onPeerDisconnected(final NodeHolder paramNodeHolder) throws RemoteException {
        checkSecurity();
        synchronized (TicwearServiceBinder.class) {
            if (isDestory) {
                return;
            }
            handler.post(new Runnable() {
                public void run() {

                    eventHandler.handlePeerDisconnected(paramNodeHolder.getDisplayName(), paramNodeHolder.getId());
                }
            });
        }
    }

}
