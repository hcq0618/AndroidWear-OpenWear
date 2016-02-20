package cn.openwatch.internal.communication.event;

import android.content.Context;

import cn.openwatch.internal.communication.SendPath;

public abstract class AbsEventHandler {

    private Context cx;

    // for 反射
    public AbsEventHandler() {
    }

    public AbsEventHandler setContext(Context cx) {
        this.cx = cx.getApplicationContext();
        return this;
    }

    protected Context getContext() {
        return cx;
    }

    // 若消息事件被处理则返回true
    public boolean handleMessageReceived(String path, byte[] rawData) {
        return false;
    }

    // 若消息事件被处理则返回true
    public boolean handleDataChanged(String path, byte[] data) {
        return false;
    }

    // 若消息事件被处理则返回true
    public boolean handleDataDeleted(String path) {
        return false;
    }

    // 若消息事件被处理则返回true
    public boolean handlePeerConnected(String displayName, String nodeId) {
        return false;
    }

    // 若消息事件被处理则返回true
    public boolean handlePeerDisconnected(String displayName, String nodeId) {
        return false;
    }

    // 若消息事件被处理则返回true
    public boolean handleServiceConnectionSuspended(int cause) {
        return false;
    }

    // 若消息事件被处理则返回true
    public boolean handleServiceConnected() {
        return false;
    }

    // 不回调内部定义的协议类型
    protected boolean isExternalEvent(String path) {
        return !path.startsWith(SendPath.TAG);
    }

    protected boolean isBothWayPath(String path) {
        return path.startsWith(SendPath.BOTHWAY_REQUEST);
    }

    protected String parseBothWayRealPath(String path) {
        return path.substring(path.lastIndexOf(SendPath.PROTOCOL_SPLIT) + 1);
    }
}
