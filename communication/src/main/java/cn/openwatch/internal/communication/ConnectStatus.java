package cn.openwatch.internal.communication;

public enum ConnectStatus {
    CONNECT_SERVICE_SUCCESS, CONNECT_SERVICE_FAIL, CONNECT_DEVICE_SUCCESS, CONNECT_DEVICE_FAIL, SERVICE_INVAILABLE, TIME_OUT, INTERRUPTED;

    public boolean isServiceConnected() {
        return this == CONNECT_DEVICE_SUCCESS || this == ConnectStatus.CONNECT_SERVICE_SUCCESS;
    }

    public boolean isDeviceConnected() {
        return this == ConnectStatus.CONNECT_DEVICE_SUCCESS;
    }
}