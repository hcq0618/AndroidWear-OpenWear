package cn.openwatch.internal.communication.os.duwear;

import org.owa.wear.ows.MessageApi.SendMessageResult;
import org.owa.wear.ows.OwsApiClient;
import org.owa.wear.ows.Wearable;
import org.owa.wear.ows.common.PendingResult;
import org.owa.wear.ows.common.Status;
import org.owa.wear.ows.internal.CommonStatusCodes;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.openwatch.internal.communication.AbsMessageApi;
import cn.openwatch.internal.communication.SendStatus;

public final class DuwearMessageApi extends AbsMessageApi<OwsApiClient> {

    public DuwearMessageApi(DuwearApiClient client) {
        super(client);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected SendStatus sendMsgAwait(String path, byte[] msgData, List<String> nodesId) {
        // TODO Auto-generated method stub
        SendStatus status = SendStatus.FAIL;

        if (nodesId != null && !nodesId.isEmpty()) {
            OwsApiClient apiClient = getApiClient();

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
