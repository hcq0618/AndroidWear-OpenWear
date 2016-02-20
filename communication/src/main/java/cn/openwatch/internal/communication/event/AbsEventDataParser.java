package cn.openwatch.internal.communication.event;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import java.util.List;

import cn.openwatch.internal.basic.ThreadsManager;
import cn.openwatch.internal.communication.SendPath;
import cn.openwatch.internal.communication.ClientManager;
import cn.openwatch.internal.communication.ClientManager.GetLocalNodeIdCallback;
import cn.openwatch.internal.basic.utils.AppUtils;

public abstract class AbsEventDataParser<DataEventBuffer, DataEvent, Asset> {

    protected abstract List<DataEvent> getDataEvents(DataEventBuffer dataEvent);

    protected abstract void release(DataEventBuffer dataEvent);

    protected abstract int getType(DataEvent dataEvent);

    protected abstract Uri getUri(DataEvent dataEvent);

    protected abstract byte[] getBytes(DataEvent dataEvent);

    protected abstract int getTypeChanged();

    protected abstract int getTypeDeleted();

    protected abstract Asset getAsset(DataEvent event);

    // 经测试 当应用退出时 这个里面会阻塞 当应用下次启动时会继续执行
    protected abstract byte[] getInputStreamAwait(Asset asset);

    private byte[] parseAssetAwait(Context cx, DataEvent event) {
        Asset profileAsset = getAsset(event);

        if (profileAsset != null && AppUtils.isMyAppInForeground(cx))
            // 经测试 当应用未在前台时 解析大数据
            // 这里面调用GetFdForAssetResult的getInputStream函数会被阻塞
            // 当应用下次启动时会继续执行
            // 而如果应用长时间未启动 则线程就会长时间在此处阻塞
            // 为避免这个问题 同时无法确定数据有多大
            // 则应用未在前台且接收的是大数据时 干脆不解析数据
            return getInputStreamAwait(profileAsset);

        return null;
    }

    private void onDataChanged(Context cx, List<DataEvent> events, String localNodeId, AbsEventHandler... handlers) {

        for (DataEvent event : events) {

            Uri uri = getUri(event);
            String nodeId = uri.getHost();

            // 若是自身发送的data消息 则不回调
            if (TextUtils.equals(localNodeId, nodeId)) {
                continue;
            }

            String path = uri.getPath();
            int type = getType(event);

            if (handlers != null) {

                for (AbsEventHandler handler : handlers) {

                    if (type == getTypeChanged()) {

                        // 传的是大数据
                        if (path.startsWith(SendPath.BIG_DATA_STAMP)) {

                            final Context context = cx.getApplicationContext();
                            final String tempPath = path;
                            final AbsEventHandler tempHandler = handler;
                            final DataEvent tempEvent = event;

                            ThreadsManager.execute(new Runnable() {

                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    byte[] data = parseAssetAwait(context, tempEvent);
                                    String path = tempPath.substring(SendPath.BIG_DATA_STAMP.length());
                                    tempHandler.handleDataChanged(path, data);
                                }
                            });

                        } else {
                            handler.handleDataChanged(path, getBytes(event));
                        }

                    } else if (type == getTypeDeleted()) {

                        handler.handleDataDeleted(path);

                    }

                }
            }

        }
    }

    public void dispatchDataChanged(Context cx, DataEventBuffer dataEvents, final AbsEventHandler... handlers) {
        if (dataEvents == null)
            return;

        final List<DataEvent> events = getDataEvents(dataEvents);
        release(dataEvents);

        if (events != null) {
            final Context context = cx.getApplicationContext();
            ClientManager.getInstance().getLocalNodeId(cx, new GetLocalNodeIdCallback() {

                @Override
                public void onGetLocalNodeId(String localNodeId) {
                    // TODO Auto-generated method stub
                    onDataChanged(context, events, localNodeId, handlers);
                }
            });
        }
    }

}
