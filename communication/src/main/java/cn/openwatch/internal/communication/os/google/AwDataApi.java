package cn.openwatch.internal.communication.os.google;

import android.net.Uri;

import java.util.concurrent.TimeUnit;

import cn.openwatch.internal.communication.AbsDataApi;
import cn.openwatch.internal.communication.SendStatus;
import cn.openwatch.internal.google.android.gms.common.api.CommonStatusCodes;
import cn.openwatch.internal.google.android.gms.common.api.GoogleApiClient;
import cn.openwatch.internal.google.android.gms.common.api.Status;
import cn.openwatch.internal.google.android.gms.wearable.Asset;
import cn.openwatch.internal.google.android.gms.wearable.PutDataMapRequest;
import cn.openwatch.internal.google.android.gms.wearable.PutDataRequest;
import cn.openwatch.internal.google.android.gms.wearable.Wearable;

public final class AwDataApi extends AbsDataApi<GoogleApiClient> {

    public AwDataApi(AwApiClient client) {
        super(client);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected SendStatus sendDataAwaitImpl(String path, byte[] data, boolean isForceChange) {
        // TODO Auto-generated method stub
        PutDataRequest dataRequest = null;
        if (data != null) {

            PutDataMapRequest dataMapRequest = PutDataMapRequest.create(path);
            dataMapRequest.getDataMap().putByteArray(BYTE_ARRAY_KEY, data);
            if (isForceChange)
                dataMapRequest.getDataMap().putLong(TIME_STAMP_KEY, System.currentTimeMillis());

            dataRequest = dataMapRequest.asPutDataRequest();

        }

        return sendAwaitImpl(dataRequest);
    }

    @Override
    protected SendStatus sendDataNoLimitAwaitImpl(String path, String assetKey, byte[] data, boolean isForceChange) {
        // TODO Auto-generated method stub

        PutDataRequest dataRequest = null;
        if (data != null) {

            Asset asset = Asset.createFromBytes(data);

            PutDataMapRequest dataMapRequest = PutDataMapRequest.create(path);
            dataMapRequest.getDataMap().putAsset(assetKey, asset);
            if (isForceChange)
                dataMapRequest.getDataMap().putLong(TIME_STAMP_KEY, System.currentTimeMillis());

            dataRequest = dataMapRequest.asPutDataRequest();

        }

        return sendAwaitImpl(dataRequest);

    }

    private SendStatus sendAwaitImpl(PutDataRequest request) {
        // TODO Auto-generated method stub

        SendStatus status = SendStatus.FAIL;

        if (request != null) {
            GoogleApiClient apiClient = getApiClient();
            if (apiClient != null) {
                Status resultStatus;
                if (timeOutMills > 0)
                    resultStatus = Wearable.DataApi.putDataItem(apiClient, request)
                            .await(timeOutMills, TimeUnit.MILLISECONDS).getStatus();
                else
                    resultStatus = Wearable.DataApi.putDataItem(apiClient, request).await().getStatus();

                if (resultStatus.isSuccess()) {
                    status = SendStatus.SUCCESS;
                } else if (resultStatus.getStatusCode() == CommonStatusCodes.TIMEOUT) {
                    status = SendStatus.TIME_OUT;
                }
            }
        }

        return status;

    }

    @Override
    protected SendStatus deleteDataItemsAwaitImpl(Uri uri) {
        // TODO Auto-generated method stub

        SendStatus status = SendStatus.FAIL;

        GoogleApiClient apiClient = getApiClient();
        if (apiClient != null) {
            Status resultStatus;
            if (timeOutMills > 0)
                resultStatus = Wearable.DataApi.deleteDataItems(apiClient, uri)
                        .await(timeOutMills, TimeUnit.MILLISECONDS).getStatus();
            else
                resultStatus = Wearable.DataApi.deleteDataItems(apiClient, uri).await().getStatus();

            if (resultStatus.isSuccess()) {
                status = SendStatus.SUCCESS;
            } else if (resultStatus.getStatusCode() == CommonStatusCodes.TIMEOUT) {
                status = SendStatus.TIME_OUT;
            }
        }

        return status;
    }

}
