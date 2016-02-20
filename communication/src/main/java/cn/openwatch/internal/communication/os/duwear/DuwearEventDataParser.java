package cn.openwatch.internal.communication.os.duwear;

import android.net.Uri;

import org.owa.wear.ows.Asset;
import org.owa.wear.ows.DataApi.GetFdForAssetResult;
import org.owa.wear.ows.DataEvent;
import org.owa.wear.ows.DataEventBuffer;
import org.owa.wear.ows.DataMapItem;
import org.owa.wear.ows.OwsApiClient;
import org.owa.wear.ows.Wearable;
import org.owa.wear.ows.common.data.FreezableUtils;

import java.io.InputStream;
import java.util.List;

import cn.openwatch.internal.communication.AbsDataApi;
import cn.openwatch.internal.communication.ClientManager;
import cn.openwatch.internal.communication.event.AbsEventDataParser;
import cn.openwatch.internal.basic.utils.IOUtils;

public final class DuwearEventDataParser extends AbsEventDataParser<DataEventBuffer, DataEvent, Asset> {

    @Override
    protected List<DataEvent> getDataEvents(DataEventBuffer dataEvent) {
        // TODO Auto-generated method stub
        if (dataEvent != null)
            return FreezableUtils.freezeIterable(dataEvent);

        return null;
    }

    @Override
    public void release(DataEventBuffer dataEvent) {
        // TODO Auto-generated method stub
        if (dataEvent != null)
            dataEvent.release();
    }

    @Override
    public int getType(DataEvent dataEvent) {
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
        OwsApiClient apiClient = (OwsApiClient) ClientManager.getInstance().getApiClient(OwsApiClient.class);

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