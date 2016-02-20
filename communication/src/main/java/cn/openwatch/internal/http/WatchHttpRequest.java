package cn.openwatch.internal.http;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cn.openwatch.internal.basic.JsonBean;

public final class WatchHttpRequest extends JsonBean {

    public String url;
    public String rawbodyToString;
    public Map<String, String> headers;
    public Method method;
    public int timeOut;

    public enum Method {
        POST, GET
    }

    @Override
    public String toJson() {
        // TODO Auto-generated method stub

        JSONObject object = new JSONObject();

        try {

            fillJsonObj(this, object);

            object.putOpt("method", method != null ? method.toString() : "");

            JSONObject mapObject = new JSONObject();

            if (headers != null) {
                for (String key : headers.keySet()) {
                    String value = headers.get(key);

                    mapObject.put(key, value);
                }
            }

            object.putOpt("headers", mapObject);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
        }

        return object.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void fromJsonObj(JSONObject object) {
        // TODO Auto-generated method stub
        super.fromJsonObj(object);

        String method = object.optString("method", "");

        if (method.equals(Method.POST.toString())) {
            this.method = Method.POST;
        } else if (method.equals(Method.GET.toString())) {
            this.method = Method.GET;
        }

        JSONObject mapObject = object.optJSONObject("headers");

        if (mapObject != null) {
            Iterator<String> names = mapObject.keys();

            if (headers == null) {
                headers = new HashMap<String, String>();
            }

            while (names.hasNext()) {
                String name = names.next();

                String value = mapObject.optString(name);

                headers.put(name, value);
            }
        }

    }
}
