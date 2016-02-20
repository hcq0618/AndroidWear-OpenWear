package cn.openwatch.internal.communication.os.google;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.openwatch.internal.communication.AbsMessageApi;
import cn.openwatch.internal.communication.SendStatus;
import cn.openwatch.internal.google.android.gms.common.api.CommonStatusCodes;
import cn.openwatch.internal.google.android.gms.common.api.GoogleApiClient;
import cn.openwatch.internal.google.android.gms.common.api.PendingResult;
import cn.openwatch.internal.google.android.gms.common.api.Status;
import cn.openwatch.internal.google.android.gms.wearable.MessageApi.SendMessageResult;
import cn.openwatch.internal.google.android.gms.wearable.Wearable;

public final class AwMessageApi extends AbsMessageApi<GoogleApiClient> {

    public AwMessageApi(AwApiClient client) {
        super(client);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected SendStatus sendMsgAwait(String path, byte[] msgData, List<String> nodesId) {
        // TODO Auto-generated method stub
        SendStatus status = SendStatus.FAIL;

        if (nodesId != null && !nodesId.isEmpty()) {
            GoogleApiClient apiClient = getApiClient();

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
