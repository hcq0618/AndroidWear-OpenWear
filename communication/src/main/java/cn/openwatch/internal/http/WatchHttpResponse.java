package cn.openwatch.internal.http;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cn.openwatch.internal.basic.JsonBean;

public final class WatchHttpResponse extends JsonBean {

    public boolean isSuccess;

    public int status;

    public Map<String, String> headers;

    public String response;

    @Override
    public String toJson() {
        // TODO Auto-generated method stub

        JSONObject object = new JSONObject();

        try {

            fillJsonObj(this, object);

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
