package cn.openwatch.internal.communication.os.ticwear;

import android.net.Uri;

import com.mobvoi.android.common.api.CommonStatusCodes;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.common.api.Status;
import com.mobvoi.android.wearable.Asset;
import com.mobvoi.android.wearable.PutDataMapRequest;
import com.mobvoi.android.wearable.PutDataRequest;
import com.mobvoi.android.wearable.Wearable;

import java.util.concurrent.TimeUnit;

import cn.openwatch.internal.communication.AbsDataApi;
import cn.openwatch.internal.communication.SendStatus;

public final class TicwearDataApi extends AbsDataApi<MobvoiApiClient> {

    public TicwearDataApi(TicwearApiClient client) {
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
            MobvoiApiClient apiClient = getApiClient();
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

        MobvoiApiClient apiClient = getApiClient();
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
