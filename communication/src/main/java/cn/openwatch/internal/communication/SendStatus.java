package cn.openwatch.internal.communication;

import cn.openwatch.communication.ErrorStatus;

public enum SendStatus {
    SUCCESS, FAIL, TIME_OUT, CONNECT_SUCCESS, CONNECT_DEVICE_FAIL, CONNECT_SERVICE_FAIL, SERVICE_INVAILABLE;

    public static SendStatus convertConnectStatus(ConnectStatus status) {

        switch (status) {
            case CONNECT_DEVICE_FAIL:
                return CONNECT_DEVICE_FAIL;
            case CONNECT_SERVICE_FAIL:
                return SendStatus.CONNECT_SERVICE_FAIL;
            case SERVICE_INVAILABLE:
                return SendStatus.SERVICE_INVAILABLE;
            case TIME_OUT:
            case INTERRUPTED:
                return TIME_OUT;

            default:
                break;
        }

        return CONNECT_SUCCESS;
    }

    public ErrorStatus convert2ErrorStatus() {
        switch (this) {
            case FAIL:
                return ErrorStatus.SEND_FAIL;
            case TIME_OUT:
                return ErrorStatus.TIME_OUT;
            case CONNECT_DEVICE_FAIL:
                return ErrorStatus.CONNECT_DEVICE_FAIL;
            case CONNECT_SERVICE_FAIL:
                return ErrorStatus.CONNECT_SERVICE_FAIL;
            case SERVICE_INVAILABLE:
                return ErrorStatus.SERVICE_INVAILABLE;

            default:
                break;
        }

        return null;
    }
}
