package cn.openwatch.internal.communication.os.duwear;

import android.net.Uri;

import org.owa.wear.ows.Asset;
import org.owa.wear.ows.OwsApiClient;
import org.owa.wear.ows.PutDataMapRequest;
import org.owa.wear.ows.PutDataRequest;
import org.owa.wear.ows.Wearable;
import org.owa.wear.ows.common.Status;
import org.owa.wear.ows.internal.CommonStatusCodes;

import java.util.concurrent.TimeUnit;

import cn.openwatch.internal.communication.AbsDataApi;
import cn.openwatch.internal.communication.SendStatus;

public final class DuwearDataApi extends AbsDataApi<OwsApiClient> {

    public DuwearDataApi(DuwearApiClient client) {
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

        OwsApiClient apiClient = getApiClient();
        if (request != null) {
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

        OwsApiClient apiClient = getApiClient();
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
