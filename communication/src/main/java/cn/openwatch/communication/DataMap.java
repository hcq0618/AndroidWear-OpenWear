package cn.openwatch.communication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

import cn.openwatch.internal.basic.JsonBean;


public final class DataMap extends JsonBean {
    private HashMap<String, String> map = new HashMap<String, String>();

    public void clear() {
        map.clear();
    }

    @Override
    public void fillJsonObj(Object obj, JSONObject jsonObj) {
        // TODO Auto-generated method stub
        super.fillJsonObj(obj, jsonObj);

        JSONObject mapObject = new JSONObject();

        try {

            for (String key : map.keySet()) {
                String value = map.get(key);

                mapObject.put(key, value);

            }

            jsonObj.putOpt("map", mapObject);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void fromJsonObj(JSONObject object) {
        // TODO Auto-generated method stub
        super.fromJsonObj(object);

        JSONObject mapObject = object.optJSONObject("map");

        if (mapObject != null) {
            Iterator<String> names = mapObject.keys();

            while (names.hasNext()) {
                String name = names.next();

                String value = mapObject.optString(name);

                map.put(name, value);
            }
        }

    }

    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(map.get(key));
    }

    public byte[] getByteArray(String key) {
        String value = map.get(key);
        if (value != null) {
            return value.getBytes();
        }

        return null;
    }

    public double getDouble(String key) {
        try {
            return Double.parseDouble(map.get(key));
        } catch (NumberFormatException e) {

        }
        return 0d;
    }

    public float getFloat(String key) {
        try {
            return Float.parseFloat(map.get(key));
        } catch (NumberFormatException e) {

        }
        return 0f;
    }

    public int getInt(String key) {
        try {
            return Integer.parseInt(map.get(key));
        } catch (NumberFormatException e) {

        }
        return 0;
    }

    public long getLong(String key) {
        try {
            return Long.parseLong(map.get(key));
        } catch (NumberFormatException e) {

        }
        return 0L;
    }

    public String getString(String key) {
        return map.get(key);
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public void putBoolean(String key, boolean value) {
        map.put(key, String.valueOf(value));
    }

    public void putByteArray(String key, byte[] byteArray) {
        map.put(key, String.valueOf(byteArray));
    }

    public void putDouble(String key, double value) {
        map.put(key, String.valueOf(value));
    }

    public void putFloat(String key, float value) {
        map.put(key, String.valueOf(value));
    }

    public void putInt(String key, int value) {
        map.put(key, String.valueOf(value));
    }

    public void putLong(String key, long value) {
        map.put(key, String.valueOf(value));
    }

    public void putString(String key, String value) {
        map.put(key, value);
    }

    public void remove(String key) {
        map.remove(key);
    }

    public int size() {
        return map.size();
    }
}