package cn.openwatch.internal.communication.os.ticwear;

import android.net.Uri;

import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.common.data.FreezableUtils;
import com.mobvoi.android.wearable.Asset;
import com.mobvoi.android.wearable.DataApi.GetFdForAssetResult;
import com.mobvoi.android.wearable.DataEvent;
import com.mobvoi.android.wearable.DataEventBuffer;
import com.mobvoi.android.wearable.DataMapItem;
import com.mobvoi.android.wearable.Wearable;

import java.io.InputStream;
import java.util.List;

import cn.openwatch.internal.communication.AbsDataApi;
import cn.openwatch.internal.communication.ClientManager;
import cn.openwatch.internal.communication.event.AbsEventDataParser;
import cn.openwatch.internal.basic.utils.IOUtils;

public final class TicwearEventDataParser extends AbsEventDataParser<DataEventBuffer, DataEvent, Asset> {

    @Override
    protected List<DataEvent> getDataEvents(DataEventBuffer dataEvent) {
        // TODO Auto-generated method stub
        if (dataEvent != null)
            return FreezableUtils.freezeIterable(dataEvent);

        return null;
    }

    @Override
    protected void release(DataEventBuffer dataEvent) {
        // TODO Auto-generated method stub
        if (dataEvent != null)
            dataEvent.release();
    }

    @Override
    protected int getType(DataEvent dataEvent) {
        // TODO Auto-generated method stub
        return dataEvent.getType();
    }

    @Override
    protected Uri getUri(DataEvent dataEvent) {
        // TODO Auto-generated method stub
        return dataEvent.getDataItem().getUri();
    }

    @Override
    protected byte[] getBytes(DataEvent dataEvent) {
        // TODO Auto-generated method stub
        DataMapItem dataMapItem = DataMapItem.fromDataItem(dataEvent.getDataItem());
        return dataMapItem.getDataMap().getByteArray(AbsDataApi.BYTE_ARRAY_KEY);
    }

    @Override
    protected int getTypeChanged() {
        // TODO Auto-generated method stub
        return DataEvent.TYPE_CHANGED;
    }

    @Override
    protected int getTypeDeleted() {
        // TODO Auto-generated method stub
        return DataEvent.TYPE_DELETED;
    }

    @Override
    protected Asset getAsset(DataEvent event) {
        // TODO Auto-generated method stub
        DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());

        return dataMapItem.getDataMap().getAsset(event.getDataItem().getUri().getPath());
    }

    @Override
    protected byte[] getInputStreamAwait(Asset asset) {
        // TODO Auto-generated method stub
        MobvoiApiClient apiClient = (MobvoiApiClient) ClientManager.getInstance().getApiClient(MobvoiApiClient.class);

        byte[] result = null;
        if (apiClient != null) {
            GetFdForAssetResult assetResult = Wearable.DataApi.getFdForAsset(apiClient, asset).await();
            InputStream is = assetResult.getInputStream();

            result = is == null ? null : IOUtils.streamToBytes(is);

            assetResult.release();
        }

        return result;
    }
}