package cn.openwatch.internal.communication.event;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import cn.openwatch.communication.DataMap;
import cn.openwatch.communication.SpecialData;
import cn.openwatch.internal.communication.SendPath;

//用户消息处理
public final class UserEventHandler extends AbsEventHandler {
    private EventCallback eventCallback;

    public UserEventHandler() {
        super();
    }

    public UserEventHandler setEventCallback(EventCallback eventLayerCallback) {
        this.eventCallback = eventLayerCallback;
        return this;
    }

    // 若消息事件被处理则返回true
    @Override
    public boolean handleDataDeleted(String path) {
        // TODO Auto-generated method stub

        if (eventCallback != null) {
            eventCallback.onDataDeleted(path);
            return true;
        }

        return false;
    }

    // 若消息事件被处理则返回true
    @Override
    public boolean handleMessageReceived(String path, byte[] rawData) {
        // TODO Auto-generated method stub

        if (path.startsWith(SendPath.SEND_BITMAP)
                || path.startsWith(SendPath.BOTHWAY_REQUEST_BITMAP)) {
            //接收到bitmap类型数据||双向通信请求 携带bitmap类型数据
            return handleBitmap(path, rawData);

        } else if (path.startsWith(SendPath.SEND_DATAMAP)) {
            //接收到datamap数据类型
            return handleDataMap(path, rawData);

        } else if (path.startsWith(SendPath.SEND_FILE) || path.startsWith(SendPath.SEND_STREAM)
                || path.startsWith(SendPath.BOTHWAY_REQUEST_FILE)
                || path.startsWith(SendPath.BOTHWAY_REQUEST_STREAM)) {

            //接收到文件类型数据||流类型数据||双向通信请求 携带文件类型数据||双向通信请求 流文件类型数据
            return handleFileOrStream(path, rawData);
            
        } else if (isBothWayPath(path)) {
            //接收到双向通信请求
            if (eventCallback != null) {
                String realPath = parseBothWayRealPath(path);
                if (isExternalEvent(realPath)) {
                    eventCallback.onMessageReceived(realPath, rawData);
                    return true;
                }
            }

        } else {
            //接收到其他类型数据
            if (eventCallback != null && isExternalEvent(path)) {
                eventCallback.onMessageReceived(path, rawData);
                return true;
            }
        }

        return false;
    }

    // 若消息事件被处理则返回true
    @Override
    public boolean handleDataChanged(String path, byte[] data) {
        // TODO Auto-generated method stub

        if (path.startsWith(SendPath.SEND_BITMAP)) {
            //接收到bitmap类型数据
            return handleBitmap(path, data);

        } else if (path.startsWith(SendPath.SEND_DATAMAP)) {
            //接收到datamap数据类型
            return handleDataMap(path, data);

        } else if (path.startsWith(SendPath.SEND_FILE) || path.startsWith(SendPath.SEND_STREAM)) {
            //接收到文件类型数据||流类型数据
            return  handleFileOrStream(path, data);
            
        } else {
            //接收到其他类型数据
            if (isExternalEvent(path)) {
                handleNoLimitData(path, data);
                return true;
            }
        }

        return false;
    }

    private void handleNoLimitData(String path, byte[] rawData) {
        if (rawData == null || eventCallback == null)
            return;

        eventCallback.onDataReceived(path, rawData);

    }

    private boolean handleDataMap(String path, byte[] rawData) {
        if (rawData == null || eventCallback == null)
            return false;

        String realPath = parseBothWayRealPath(path);

        if (isExternalEvent(realPath)) {
            DataMap datamap = new DataMap();
            datamap.fromJson(new String(rawData));

            eventCallback.onDataMapReceived(realPath, datamap);

            return true;
        }

        return false;
    }

    private boolean handleBitmap(String path, byte[] rawData) {
        if (rawData == null || eventCallback == null)
            return false;

        String realPath = parseBothWayRealPath(path);

        if (isExternalEvent(realPath)) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(rawData, 0, rawData.length);

            eventCallback.onBitmapReceived(realPath, bitmap);

            return true;
        }

        return false;
    }

    private boolean handleFileOrStream(String path, byte[] rawData) {
        if (rawData == null || eventCallback == null)
            return false;

        String realPath = parseBothWayRealPath(path);

        if (isExternalEvent(realPath)) {
            SpecialData data = new SpecialData(realPath, rawData);

            if (path.startsWith(SendPath.SEND_FILE)
                    || path.startsWith(SendPath.BOTHWAY_REQUEST_FILE))
                eventCallback.onFileReceived(data);
            else
                eventCallback.onStreamReceived(data);
            
            return true;
        }
        
        return false;
    }

    @Override
    public boolean handlePeerConnected(String displayName, String nodeId) {
        // TODO Auto-generated method stub
        if (eventCallback != null) {

            eventCallback.onPeerConnected(displayName, nodeId);

            return true;
        }

        return false;
    }

    @Override
    public boolean handlePeerDisconnected(String displayName, String nodeId) {
        // TODO Auto-generated method stub
        if (eventCallback != null) {

            eventCallback.onPeerDisconnected(displayName, nodeId);

            return true;
        }

        return false;
    }

    @Override
    public boolean handleServiceConnectionSuspended(int cause) {
        // TODO Auto-generated method stub
        if (eventCallback != null) {

            eventCallback.onServiceConnectionSuspended(cause);

            return true;
        }
        return false;
    }

    @Override
    public boolean handleServiceConnected() {
        // TODO Auto-generated method stub
        if (eventCallback != null) {

            eventCallback.onServiceConnected();

            return true;
        }
        return false;
    }
}
