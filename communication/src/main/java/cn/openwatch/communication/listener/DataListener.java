package cn.openwatch.communication.listener;

import cn.openwatch.communication.DataMap;

/**
 * 配对设备间数据通信状态的监听
 */
public interface DataListener {

    /**
     * 接收到配对设备发送来的键值对
     *
     * @param path    与数据相关联的key 例如:"/openwatch/path"
     * @param dataMap 收到的dataMap
     */
    void onDataMapReceived(String path, DataMap dataMap);

    /**
     * 接收到配对设备发送来的数据
     *
     * @param path    与数据相关联的key 例如:"/openwatch/path"
     * @param rawData 收到的原始数据
     */
    void onDataReceived(String path, byte[] rawData);

    /**
     * 接收到配对设备删除的数据
     *
     * @param path 与数据相关联的key 例如:"/openwatch/path"
     */
    void onDataDeleted(String path);

}
