package cn.openwatch.communication.listener;

import cn.openwatch.communication.ErrorStatus;

/**
 * 向配对设备发送临时性或时效性数据的状态监听
 */
public interface SendListener {
    /**
     * 向配对设备发送数据成功
     *
     */
    void onSuccess();

    /**
     * 向配对设备发送数据失败
     *
     * @param error 失败的原因
     */
    void onError(ErrorStatus error);
}