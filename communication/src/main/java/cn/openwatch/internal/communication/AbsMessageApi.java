package cn.openwatch.internal.communication;

import java.util.List;

public abstract class AbsMessageApi<Client> {

    private AbsApiClient<Client> client;
    protected long timeOutMills;

    public AbsMessageApi(AbsApiClient<Client> client) {
        this.client = client;
    }

    protected void setTimeOutMills(long timeOutMills) {
        this.timeOutMills = timeOutMills;
    }

    protected abstract SendStatus sendMsgAwait(String path, byte[] msgData, List<String> nodesId);

    protected SendStatus sendMsgAwait(String path, byte[] msgData) {
        // TODO Auto-generated method stub
        ConnectStatus connectStatus = connectAwaitIfNeed();

        if (connectStatus == ConnectStatus.CONNECT_SERVICE_SUCCESS) {
            // 获取设备连接列表
            List<String> connectNodeIds = getConnectedNodesIdAwait();

            return sendMsgAwait(path, msgData, connectNodeIds);
        }
        return SendStatus.convertConnectStatus(connectStatus);
    }

    protected ConnectStatus connectAwaitIfNeed() {
        if (client != null)
            return client.connectAwaitIfNeed();

        return ConnectStatus.CONNECT_SERVICE_FAIL;
    }

    protected List<String> getConnectedNodesIdAwait() {
        if (client != null)
            return client.getConnectedNodesIdAwait();

        return null;
    }

    protected Client getApiClient() {
        if (client != null)
            return client.getApiClient();

        return null;
    }
}
