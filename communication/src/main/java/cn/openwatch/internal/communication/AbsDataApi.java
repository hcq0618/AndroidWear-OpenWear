package cn.openwatch.internal.communication;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public abstract class AbsDataApi<Client> {

    protected long timeOutMills;

    private AbsApiClient<Client> client;

    // 为了强制接收到数据 添加时间戳 保证数据发送了变化
    protected static final String TIME_STAMP_KEY = "time_stamp";
    public static final String BYTE_ARRAY_KEY = "byte_array";

    public AbsDataApi(AbsApiClient<Client> client) {
        this.client = client;
    }

    protected abstract SendStatus sendDataAwaitImpl(String path, byte[] data, boolean isForceChange);

    protected abstract SendStatus sendDataNoLimitAwaitImpl(String path, String assetKey, byte[] data,
                                                           boolean isForceChange);

    protected abstract SendStatus deleteDataItemsAwaitImpl(Uri uri);

    protected void setTimeOutMills(long timeOutMills) {
        this.timeOutMills = timeOutMills;
    }

    // 没有数据大小限制
    protected SendStatus sendDataNoLimitAwait(String path, byte[] data, boolean isForceChange) {
        ConnectStatus status = client.connectAwaitIfNeed();

        if (status == ConnectStatus.CONNECT_SERVICE_SUCCESS) {
            // path==assetKey
            return sendDataNoLimitAwaitImpl(path, path, data, isForceChange);
        }

        return SendStatus.convertConnectStatus(status);
    }

    // 有100k大小限制
    protected SendStatus sendDataAwait(String path, byte[] data, boolean isForceChange) {
        ConnectStatus status = client.connectAwaitIfNeed();

        if (status == ConnectStatus.CONNECT_SERVICE_SUCCESS) {
            // path==assetKey
            return sendDataAwaitImpl(path, data, isForceChange);
        }

        return SendStatus.convertConnectStatus(status);
    }

    // https://developers.google.com/android/reference/com/google/android/gms/wearable/PutDataRequest
    // https://developers.google.com/android/reference/com/google/android/gms/wearable/DataApi
    // http://stackoverflow.com/questions/24601251/what-is-the-uri-for-wearable-dataapi-getdataitem-after-using-putdatamaprequest
    protected List<Uri> makeDataUrisAwait(List<String> pathList) {
        if (pathList == null || pathList.isEmpty()) {
            return null;
        }

        if (client != null) {
            List<String> nodesId = client.getConnectedNodesIdAwait();
            if (nodesId != null && !nodesId.isEmpty()) {
                List<Uri> uris = new ArrayList<Uri>();
                for (String nodeId : nodesId) {
                    for (String path : pathList) {
                        Uri uri = new Uri.Builder().scheme("wear").authority(nodeId).path(path).build();
                        uris.add(uri);
                    }
                }

                return uris;
            }
        }
        return null;
    }

    protected SendStatus deleteDataItemsAwait(List<String> pathList) {
        List<Uri> uris = makeDataUrisAwait(pathList);

        if (uris == null || uris.isEmpty())
            return SendStatus.FAIL;

        int size = uris.size();

        ConnectStatus connectStatus = client.connectAwaitIfNeed();
        if (connectStatus == ConnectStatus.CONNECT_SERVICE_SUCCESS) {
            for (int i = 0; i < size; i++) {
                SendStatus status = deleteDataItemsAwaitImpl(uris.get(i));
                if (status == SendStatus.SUCCESS) {
                    return SendStatus.SUCCESS;
                }
            }
        }

        return SendStatus.convertConnectStatus(connectStatus);
    }

    protected Client getApiClient() {
        if (client != null)
            return client.getApiClient();

        return null;
    }

}
