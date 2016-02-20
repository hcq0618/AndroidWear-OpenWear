package cn.openwatch.internal.communication.os.google.china;

import android.os.Binder;
import android.os.Handler;

import java.util.List;

import cn.openwatch.communication.service.OpenWatchListenerService;
import cn.openwatch.internal.communication.IServiceBinder;
import cn.openwatch.internal.communication.event.ServiceEventHandler;
import cn.openwatch.internal.google.china.android.gms.common.GooglePlayServicesUtil;
import cn.openwatch.internal.google.china.android.gms.common.data.DataHolder;
import cn.openwatch.internal.google.china.android.gms.wearable.DataEventBuffer;
import cn.openwatch.internal.google.china.android.gms.wearable.internal.AmsEntityUpdateParcelable;
import cn.openwatch.internal.google.china.android.gms.wearable.internal.AncsNotificationParcelable;
import cn.openwatch.internal.google.china.android.gms.wearable.internal.CapabilityInfoParcelable;
import cn.openwatch.internal.google.china.android.gms.wearable.internal.ChannelEventParcelable;
import cn.openwatch.internal.google.china.android.gms.wearable.internal.MessageEventParcelable;
import cn.openwatch.internal.google.china.android.gms.wearable.internal.NodeParcelable;
import cn.openwatch.internal.google.china.android.gms.wearable.internal.zzav;

public final class AwServiceBinder extends zzav.zza implements IServiceBinder {

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

    private void checkSecurity() throws SecurityException {
        int i = Binder.getCallingUid();
        if (i == uid) {
            return;
        }
        if (GooglePlayServicesUtil.zza(service, i)) {
            uid = i;
            return;
        }
        throw new SecurityException("Caller is not GooglePlayServices");
    }

    @Override
    public void zza(final DataHolder paramDataHolder) {
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
    public void zza(final MessageEventParcelable paramMessageEventParcelable) {
        checkSecurity();
        synchronized (AwServiceBinder.class) {
            if (isDestory) {
                return;
            }
            handler.post(new Runnable() {
                public void run() {

                    String path = paramMessageEventParcelable.getPath();

                    byte[] rawData = paramMessageEventParcelable.getData();

                    eventHandler.handleMessageReceived(path, rawData);
                }
            });
        }
    }

    @Override
    public void zza(final NodeParcelable paramNodeParcelable) {
        checkSecurity();
        synchronized (AwServiceBinder.class) {
            if (isDestory) {
                return;
            }
            handler.post(new Runnable() {
                public void run() {

                    eventHandler.handlePeerConnected(paramNodeParcelable.getDisplayName(), paramNodeParcelable.getId());
                }
            });
        }
    }

    @Override
    public void zzb(final NodeParcelable paramNodeParcelable) {
        checkSecurity();
        synchronized (AwServiceBinder.class) {
            if (isDestory) {
                return;
            }
            handler.post(new Runnable() {
                public void run() {

                    eventHandler.handlePeerDisconnected(paramNodeParcelable.getDisplayName(),
                            paramNodeParcelable.getId());
                }
            });
        }
    }

    @Override
    public void onConnectedNodes(final List<NodeParcelable> connectedNodes) {

    }

    @Override
    public void zza(final CapabilityInfoParcelable paramCapabilityInfoParcelable) {

    }

    @Override
    public void zza(final AncsNotificationParcelable paramAncsNotificationParcelable) {

    }

    @Override
    public void zza(final AmsEntityUpdateParcelable paramAmsEntityUpdateParcelable) {

    }

    @Override
    public void zza(final ChannelEventParcelable paramChannelEventParcelable) {

    }

}
