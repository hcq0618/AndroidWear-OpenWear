package cn.openwatch.internal.communication.os.ticwear;

import android.os.Bundle;

import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.MobvoiApiManager;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.common.api.MobvoiApiClient.ConnectionCallbacks;
import com.mobvoi.android.common.api.PendingResult;
import com.mobvoi.android.wearable.Node;
import com.mobvoi.android.wearable.NodeApi.GetConnectedNodesResult;
import com.mobvoi.android.wearable.NodeApi.GetLocalNodeResult;
import com.mobvoi.android.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.openwatch.internal.communication.AbsApiClient;
import cn.openwatch.internal.communication.AbsDataApi;
import cn.openwatch.internal.communication.AbsMessageApi;
import cn.openwatch.internal.communication.SupportClient;

public final class TicwearApiClient extends AbsApiClient<MobvoiApiClient> implements ConnectionCallbacks {

    private TicwearEventObserver eventObserver;

    // for反射
    public TicwearApiClient() {
        super();
    }

    // 在App启动时调用MobvoiApiManager.getInstance().adaptService(context)，
    // 该方法必须在任何可能的API调用操作前调用，它将会自动探测当前系统情况，选择底层是使用MMS或GMS。
    // 如果想自己决定使用哪种API，可以通过调用MobvoiApiManager.getInstance().loadService(context,
    // group)来指定使用Ticwear或Android
    // Wear的API，以取代上面的adaptService方法。
    // 如果这两个方法都没有被调用，API会变成仅Ticwear系统能使用的方式。
    @Override
    protected MobvoiApiClient initClient() {
        // TODO Auto-generated method stub
        if (cx != null) {
            return new MobvoiApiClient.Builder(cx).addApi(Wearable.API).addConnectionCallbacks(this).build();
        }

        return null;
    }

    @Override
    protected boolean isServiceConnected() {
        // TODO Auto-generated method stub
        return apiClient != null && apiClient.isConnected();

    }

    @Override
    protected void disconnectService() {
        // TODO Auto-generated method stub
        if (apiClient != null)
            apiClient.disconnect();
    }

    @Override
    protected int blockingConnectService() {
        // TODO Auto-generated method stub
        if (apiClient != null) {

            if (connectTimeOutMills > 0)
                return apiClient.blockingConnect(connectTimeOutMills, TimeUnit.MILLISECONDS).getErrorCode();
            else
                return apiClient.blockingConnect().getErrorCode();
        }

        return ConnectionResult.INTERNAL_ERROR;
    }

    @Override
    protected boolean isAppAndServiceAvailable() {
        // TODO Auto-generated method stub

        if (cx != null) {
            MobvoiApiManager manager = MobvoiApiManager.getInstance();
            return manager.isMmsAvailable(cx);
        }

        return false;
    }

    @Override
    protected boolean isSuccessCode(int code) {
        // TODO Auto-generated method stub
        return code == ConnectionResult.SUCCESS;
    }

    @Override
    public List<String> getConnectedNodesIdAwait() {
        List<String> ids = null;

        if (apiClient != null) {
            PendingResult<GetConnectedNodesResult> connectNodePendingResult = Wearable.NodeApi
                    .getConnectedNodes(apiClient);

            GetConnectedNodesResult connectNodeResult;
            if (getNodesIdTimeOutMills > 0)
                connectNodeResult = connectNodePendingResult.await(getNodesIdTimeOutMills, TimeUnit.MILLISECONDS);
            else
                connectNodeResult = connectNodePendingResult.await();

            if (connectNodeResult != null) {

                List<Node> nodes = connectNodeResult.getNodes();

                if (nodes != null && !nodes.isEmpty()) {

                    ids = new ArrayList<String>();

                    for (Node node : nodes) {
                        if (node != null)
                            ids.add(node.getId());
                    }
                }
            }
        }

        return ids;
    }

    // http://stackoverflow.com/questions/24601251/what-is-the-uri-for-wearable-dataapi-getdataitem-after-using-putdatamaprequest
    @Override
    protected String getLocalNodeIdAwait() {
        // TODO Auto-generated method stub
        PendingResult<GetLocalNodeResult> pendingNodeResult = Wearable.NodeApi.getLocalNode(apiClient);

        GetLocalNodeResult nodeResult;
        if (getNodesIdTimeOutMills > 0) {
            nodeResult = pendingNodeResult.await(getNodesIdTimeOutMills, TimeUnit.MILLISECONDS);
        } else {
            nodeResult = pendingNodeResult.await();
        }

        Node localNode = nodeResult.getNode();
        if (localNode != null)
            return localNode.getId();
        else
            return "";
    }

    @Override
    protected int getType() {
        // TODO Auto-generated method stub
        return SupportClient.TYPE_TICWEAR;
    }

    @Override
    protected String getTypeName() {
        // TODO Auto-generated method stub
        return SupportClient.TYPENAME_TICWEAR;
    }

    @Override
    protected AbsDataApi<MobvoiApiClient> getDataApi() {
        // TODO Auto-generated method stub
        if (dataApi == null) {
            dataApi = new TicwearDataApi(this);
        }

        return dataApi;
    }

    @Override
    protected AbsMessageApi<MobvoiApiClient> getMessageApi() {
        // TODO Auto-generated method stub

        if (messageApi == null) {
            messageApi = new TicwearMessageApi(this);
        }

        return messageApi;
    }

    @Override
    protected boolean isTimeOutCode(int code) {
        // TODO Auto-generated method stub
        return code == ConnectionResult.TIMEOUT;
    }

    @Override
    protected boolean isServiceInvailableCode(int code) {
        // TODO Auto-generated method stub
        return code == ConnectionResult.SERVICE_DISABLED || code == ConnectionResult.SERVICE_MISSING
                || code == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED
                // The version of the Google Play services installed on this
                // device is
                // not authentic.
                || code == ConnectionResult.SERVICE_INVALID;
    }

    @Override
    protected TicwearEventObserver getEventObserver() {
        // TODO Auto-generated method stub
        if (eventObserver == null)
            eventObserver = new TicwearEventObserver(cx, this);
        return eventObserver;
    }

    @Override
    public void onConnected(Bundle bundle) {
        // TODO Auto-generated method stub
        if (connectionListener != null)
            connectionListener.onConnected();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // TODO Auto-generated method stub
        if (connectionListener != null) {
            switch (cause) {
                case ConnectionCallbacks.CAUSE_NETWORK_LOST:
                    cause = AbsApiClient.CAUSE_CONNECTION_LOST;
                    break;
                case ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED:
                    cause = AbsApiClient.CAUSE_SERVICE_KILLED;
                    break;

                default:
                    cause = AbsApiClient.CAUSE_UNKNOWN;
                    break;
            }
            connectionListener.onConnectionSuspended(cause);
        }
    }
}
