package cn.openwatch.internal.basic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

//gson库文件太大了 200多k  写个简单的 需要不断完善
public abstract class JsonBean {

    // for反射
    public JsonBean() {
    }

    public String toJson() {
        JSONObject object = new JSONObject();
        fillJsonObj(this, object);
        return object.toString();
    }

    public void fillJsonObj(Object obj, JSONObject jsonObj) {

        // getDeclaredFields只获取类自己声明的所有域
        // getFields获取类自己、及其父类和实现的接口声明的public域
        // 要获取类自己、及其父类和实现的接口声明的所有域 则需要用getSuperClass来遍历获取
        // http://stackoverflow.com/questions/16295949/get-all-fields-even-private-and-inherited-from-class
        Field[] fields = obj.getClass().getFields();

        for (Field field : fields) {
            try {
                field.setAccessible(true);

                // 跳过静态成员变量
                boolean isStatic = Modifier.isStatic(field.getModifiers());
                if (isStatic) {
                    continue;
                }

                Object valueObj = field.get(obj);
                if (valueObj != null) {
                    if (isSurpportType(valueObj)) {

                        jsonObj.putOpt(field.getName(), valueObj);

                    } else if (valueObj instanceof List<?>) {

                        JSONArray array = new JSONArray();
                        List<?> list = (List<?>) valueObj;
                        for (Object o : list) {
                            if (isSurpportType(o)) {
                                array.put(o);
                            } else {
                                // 复杂对象
                                JSONObject newJsonObj = new JSONObject();
                                fillJsonObj(o, newJsonObj);
                                array.put(newJsonObj);
                            }
                        }

                        jsonObj.putOpt(field.getName(), array);

                    } else {

                        // 递归
                        JSONObject newJsonObj = new JSONObject();
                        fillJsonObj(valueObj, newJsonObj);
                        jsonObj.put(field.getName(), newJsonObj);
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    // 见jsonObj.put函数的注解 支持的类型
    protected static boolean isSurpportType(Object obj) {
        return obj instanceof String || obj instanceof Integer || obj instanceof Long || obj instanceof Double
                || obj instanceof Boolean || obj instanceof Float || obj instanceof Void;
    }

    public void fromJson(String json) {
        try {
            JSONObject object = new JSONObject(json);
            fromJsonObj(object);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void fromJsonObj(JSONObject jsonObj) {
        Field[] fields = getClass().getFields();

        for (Field field : fields) {
            try {
                field.setAccessible(true);

                // 跳过静态成员变量
                boolean isStatic = Modifier.isStatic(field.getModifiers());
                if (isStatic) {
                    continue;
                }

                String className = field.getType().getName();
                if (className.equals(String.class.getName())) {
                    field.set(this, jsonObj.optString(field.getName()));
                } else if (className.equals(Integer.class.getName()) || className.equals("int")) {
                    field.setInt(this, jsonObj.optInt(field.getName()));
                } else if (className.equals(Long.class.getName()) || className.equals("long")) {
                    field.setLong(this, jsonObj.optLong(field.getName()));
                } else if (className.equals(Double.class.getName()) || className.equals("double")) {
                    field.setDouble(this, jsonObj.optDouble(field.getName(), 0.0d));
                } else if (className.equals(Float.class.getName()) || className.equals("float")) {
                    field.setFloat(this, (float) jsonObj.optDouble(field.getName(), 0.0f));
                } else if (className.equals(Boolean.class.getName()) || className.equals("boolean")) {
                    field.setBoolean(this, jsonObj.optBoolean(field.getName()));
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }
}
