package cn.openwatch.internal.communication;

import android.content.Context;
import android.text.TextUtils;

import cn.openwatch.internal.basic.PrefsManager;

public final class CacheManager {

    private static final String LAST_CONNECTED_DEVICE_DATA_KEY = "last_connected_device_data";

    private static final String LOCAL_NODE_ID_KEY = "local_node_id_key";

    private static final String COMMUNICATION_COUNT_KEY = "communication_count";

    // 缓存已配对连接的nodeId 留作备用
    protected static void saveLastConnectedDeviceData(Context cx, String typeName, int type) {

        LastConnectedDevice deviceData = new LastConnectedDevice();
        deviceData.type = type;
        deviceData.typeName = typeName;

        PrefsManager.saveData(cx, LAST_CONNECTED_DEVICE_DATA_KEY, deviceData.toJson());
    }

    protected static LastConnectedDevice getLastConnectedDeviceData(Context cx) {

        String cacheDeviceDataJson = PrefsManager.getData(cx, LAST_CONNECTED_DEVICE_DATA_KEY);

        if (!TextUtils.isEmpty(cacheDeviceDataJson)) {

            try {
                LastConnectedDevice deviceData = new LastConnectedDevice();
                deviceData.fromJson(cacheDeviceDataJson);

                return deviceData;
            } catch (Exception e) {
                // TODO Auto-generated catch block

                // 数据异常 清除缓存的设备数据
                clearLastConnectedDeviceData(cx);
            }
        }

        return null;
    }

    protected static void clearLastConnectedDeviceData(Context cx) {
        PrefsManager.clearData(cx, LAST_CONNECTED_DEVICE_DATA_KEY);
    }

    // 缓存本机设备id 用于其他地方
    protected static void saveLocalNodeId(Context cx, String nodeId) {
        PrefsManager.saveData(cx, LOCAL_NODE_ID_KEY, nodeId);
    }

    protected static String getLocalNodeId(Context cx) {
        return PrefsManager.getData(cx, LOCAL_NODE_ID_KEY);
    }

    protected static void addSelfCommunicationCount(Context cx) {
        PrefsManager.saveData(cx, COMMUNICATION_COUNT_KEY, String.valueOf(getCommunicationCount(cx) + 1));
    }

    public static int getCommunicationCount(Context cx) {
        String count = PrefsManager.getData(cx, COMMUNICATION_COUNT_KEY);
        if (!TextUtils.isEmpty(count) && TextUtils.isDigitsOnly(count))
            return Integer.valueOf(count);

        return 0;
    }

    protected static void clearCommunicationCount(Context cx) {
        PrefsManager.saveData(cx, COMMUNICATION_COUNT_KEY, "");
    }

}
