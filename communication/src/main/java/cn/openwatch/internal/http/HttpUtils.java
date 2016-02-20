package cn.openwatch.internal.http;

import android.text.TextUtils;

import java.util.Map;

public final class HttpUtils {

    private HttpUtils() {
    }

    public static String makeRequestUrl(String url, Map<String, String> params) {
        if (TextUtils.isEmpty(url))
            return "";

        StringBuilder sb = null;

        if (params != null) {

            sb = new StringBuilder(url);

            if (!url.substring(url.length() - 1, url.length()).equals("?")) {
                sb.append("?");
            }

            makeParams(sb, params);
        }

        return sb == null ? url : sb.toString();
    }

    public static String makeParams(Map<String, String> params) {

        StringBuilder sb = new StringBuilder();

        return makeParams(sb, params);
    }

    public static String makeParams(StringBuilder sb, Map<String, String> params) {

        if (sb == null)
            return "";

        if (params == null || params.isEmpty()) {
            return sb.toString();
        }

        for (String key : params.keySet()) {
            sb.append(key).append("=").append(params.get(key)).append("&");
        }

        int len = sb.length();
        sb.replace(len - 1, len, "");

        return sb.toString();
    }
}
