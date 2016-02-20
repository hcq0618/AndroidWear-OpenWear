package cn.openwatch.internal.communication.os.ticwear;

import android.content.Context;

import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.wearable.DataApi;
import com.mobvoi.android.wearable.DataEventBuffer;
import com.mobvoi.android.wearable.MessageApi;
import com.mobvoi.android.wearable.MessageEvent;
import com.mobvoi.android.wearable.Node;
import com.mobvoi.android.wearable.NodeApi;
import com.mobvoi.android.wearable.Wearable;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import cn.openwatch.internal.communication.ClientManager;
import cn.openwatch.internal.communication.event.AbsEventHandler;
import cn.openwatch.internal.communication.event.AbsEventObserver;

public final class TicwearEventObserver extends AbsEventObserver<MobvoiApiClient>
        implements DataApi.DataListener, MessageApi.MessageListener, NodeApi.NodeListener {

    private TicwearEventDataParser dataParser;

    public TicwearEventObserver(Context cx, TicwearApiClient client) {
        super(cx, client);
        // TODO Auto-generated constructor stub
        dataParser = new TicwearEventDataParser();
    }

    @Override
    protected void unRegistDataApi() {

        if (client != null && client.getApiClient() != null) {
            Wearable.DataApi.removeListener(client.getApiClient(), this);
        }

    }

    @Override
    protected void unRegistMessageApi() {
        // TODO Auto-generated method stub

        if (client != null && client.getApiClient() != null) {
            Wearable.MessageApi.removeListener(client.getApiClient(), this);
        }
    }

    @Override
    protected void unRegistNodeApi() {
        // TODO Auto-generated method stub

        if (client != null && client.getApiClient() != null) {
            Wearable.NodeApi.removeListener(client.getApiClient(), this);
        }
    }

    @Override
    protected void registMessageApi() {
        // TODO Auto-generated method stub

        if (client != null && client.getApiClient() != null) {
            Wearable.MessageApi.addListener(client.getApiClient(), this);
        }
    }

    @Override
    protected void registNodeApi() {
        // TODO Auto-generated method stub
        if (client != null && client.getApiClient() != null) {
            Wearable.NodeApi.addListener(client.getApiClient(), this);
        }
    }

    @Override
    protected void registDataApi() {
        if (client != null && client.getApiClient() != null) {
            Wearable.DataApi.addListener(client.getApiClient(), this);
        }

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        // TODO Auto-generated method stub

        ArrayList<AbsEventHandler> eventHandlers = new ArrayList<AbsEventHandler>(ClientManager.getInstance().getEventHandlers().values());

        dataParser.dispatchDataChanged(cx, dataEventBuffer,
                eventHandlers.toArray(new AbsEventHandler[eventHandlers.size()]));
    }

    @Override
    public void onMessageReceived(MessageEvent event) {
        // TODO Auto-generated method stub
        ConcurrentHashMap<String, AbsEventHandler> eventHandlers = ClientManager.getInstance().getEventHandlers();

        String path = event.getPath();

        byte[] rawData = event.getData();

        for (AbsEventHandler eventHandler : eventHandlers.values()) {
            eventHandler.handleMessageReceived(path, rawData);
        }
    }

    @Override
    public void onPeerConnected(Node node) {
        // TODO Auto-generated method stub

        ConcurrentHashMap<String, AbsEventHandler> eventHandlers = ClientManager.getInstance().getEventHandlers();

        String displayName = node.getDisplayName();
        String nodeId = node.getId();
        for (AbsEventHandler eventHandler : eventHandlers.values()) {
            eventHandler.handlePeerConnected(displayName, nodeId);
        }
    }

    @Override
    public void onPeerDisconnected(Node node) {
        // TODO Auto-generated method stub
        ConcurrentHashMap<String, AbsEventHandler> eventHandlers = ClientManager.getInstance().getEventHandlers();

        String displayName = node.getDisplayName();
        String nodeId = node.getId();
        for (AbsEventHandler eventHandler : eventHandlers.values()) {
            eventHandler.handlePeerDisconnected(displayName, nodeId);
        }
    }


}
