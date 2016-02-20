package cn.openwatch.internal.communication;

public class SendPath {
    protected SendPath() {
    }

    // 用来标示消息类型是否为内部自定义协议类型
    public static final String TAG = "/openwatch_";

    public static final String PROTOCOL_SPLIT = "?";

    public static final String BIG_DATA_STAMP = "/big_data";

    public static final String SEND_BITMAP = TAG + "send_bitmap" + PROTOCOL_SPLIT;

    public static final String SEND_FILE = TAG + "send_file_path" + PROTOCOL_SPLIT;

    public static final String SEND_STREAM = TAG + "send_stream_path" + PROTOCOL_SPLIT;

    public static final String SEND_DATAMAP = TAG + "send_datamap" + PROTOCOL_SPLIT;

    public static final String BOTHWAY_REQUEST = TAG + "bothway_request" + PROTOCOL_SPLIT;

    public static final String BOTHWAY_REQUEST_BITMAP = TAG + "bothway_bitmap_request" + PROTOCOL_SPLIT;

    public static final String BOTHWAY_REQUEST_FILE = TAG + "bothway_file_request" + PROTOCOL_SPLIT;

    public static final String BOTHWAY_REQUEST_STREAM = TAG + "bothway_stream_request" + PROTOCOL_SPLIT;

    public static final String BOTHWAY_RESPONSE = TAG + "bothway_response" + PROTOCOL_SPLIT;

    public static final String HTTP_REQUEST = TAG + "http_request";

    public static final String HTTP_RESPONSE = TAG + "http_response" + PROTOCOL_SPLIT;

    public static final String HTTP_CANCEL = TAG + "http_cancel";

    public static final String ANALYTIC_COMMUNICATION_COUNT = TAG + "analytic_communication_count";
}