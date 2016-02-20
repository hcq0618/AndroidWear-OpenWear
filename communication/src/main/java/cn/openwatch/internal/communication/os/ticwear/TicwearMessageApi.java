package cn.openwatch.internal.communication.os.ticwear;

import com.mobvoi.android.common.api.CommonStatusCodes;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.common.api.PendingResult;
import com.mobvoi.android.common.api.Status;
import com.mobvoi.android.wearable.MessageApi.SendMessageResult;
import com.mobvoi.android.wearable.Wearable;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.openwatch.internal.communication.AbsMessageApi;
import cn.openwatch.internal.communication.SendStatus;

public final class TicwearMessageApi extends AbsMessageApi<MobvoiApiClient> {

    public TicwearMessageApi(TicwearApiClient client) {
        super(client);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected SendStatus sendMsgAwait(String path, byte[] msgData, List<String> nodesId) {
        // TODO Auto-generated method stub
        SendStatus status = SendStatus.FAIL;

        if (nodesId != null && !nodesId.isEmpty()) {
            MobvoiApiClient apiClient = getApiClient();

            if (apiClient != null) {
                for (String id : nodesId) {
                    PendingResult<SendMessageResult> pendingResult = Wearable.MessageApi.sendMessage(apiClient, id,
                            path, msgData);

                    Status resultStatus;
                    if (timeOutMills > 0)
                        resultStatus = pendingResult.await(timeOutMills, TimeUnit.MILLISECONDS).getStatus();
                    else
                        resultStatus = pendingResult.await().getStatus();

                    if (resultStatus.isSuccess()) {
                        status = SendStatus.SUCCESS;
                    } else if (resultStatus.getStatusCode() == CommonStatusCodes.TIMEOUT) {
                        status = SendStatus.TIME_OUT;
                    }
                }
            }
        }
        return status;
    }
}
