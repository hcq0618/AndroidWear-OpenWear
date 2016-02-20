package cn.openwatch.internal.communication.os.google.china;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.openwatch.internal.communication.AbsApiClient;
import cn.openwatch.internal.communication.AbsDataApi;
import cn.openwatch.internal.communication.AbsMessageApi;
import cn.openwatch.internal.communication.SupportClient;
import cn.openwatch.internal.google.china.android.gms.common.ConnectionResult;
import cn.openwatch.internal.google.china.android.gms.common.api.GoogleApiClient;
import cn.openwatch.internal.google.china.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import cn.openwatch.internal.google.china.android.gms.common.api.PendingResult;
import cn.openwatch.internal.google.china.android.gms.wearable.Node;
import cn.openwatch.internal.google.china.android.gms.wearable.NodeApi.GetConnectedNodesResult;
import cn.openwatch.internal.google.china.android.gms.wearable.NodeApi.GetLocalNodeResult;
import cn.openwatch.internal.google.china.android.gms.wearable.Wearable;
import cn.openwatch.internal.basic.utils.AppUtils;

public final class AwApiClient extends AbsApiClient<GoogleApiClient> implements ConnectionCallbacks {

    private AwEventObserver eventObserver;

    // for反射
    public AwApiClient() {
        super();
    }

    @Override
    protected GoogleApiClient initClient() {
        // TODO Auto-generated method stub
        if (cx != null) {
            return new GoogleApiClient.Builder(cx).addApi(Wearable.API).addConnectionCallbacks(this).build();
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
        return cx != null && AppUtils.isAppInstalled(cx, "com.google.android.gms")
                && AppUtils.isAppInstalled(cx, "com.google.android.wearable.app.cn");

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
        return SupportClient.TYPE_ANDROID_WEAR_CHINA;
    }

    @Override
    protected String getTypeName() {
        // TODO Auto-generated method stub
        return SupportClient.TYPENAME_ANDROID_WEAR_CHINA;
    }

    @Override
    protected AbsDataApi<GoogleApiClient> getDataApi() {
        // TODO Auto-generated method stub
        if (dataApi == null) {
            dataApi = new AwDataApi(this);
        }

        return dataApi;
    }

    @Override
    protected AbsMessageApi<GoogleApiClient> getMessageApi() {
        // TODO Auto-generated method stub
        if (messageApi == null) {
            messageApi = new AwMessageApi(this);
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
    protected AwEventObserver getEventObserver() {
        // TODO Auto-generated method stub
        if (eventObserver == null)
            eventObserver = new AwEventObserver(cx, this);
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
