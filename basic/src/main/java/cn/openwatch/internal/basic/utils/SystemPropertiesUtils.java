package cn.openwatch.internal.basic.utils;

import android.content.Context;

import java.lang.reflect.Method;

public class SystemPropertiesUtils {

    private SystemPropertiesUtils() {
    }

    /**
     * 根据给定Key获取值.
     *
     * @return 如果不存在该key则返回空字符串
     */

    public static String get(Context context, String key) throws IllegalArgumentException {

        String ret = "";

        try {

            ClassLoader cl = context.getClassLoader();

            Class<?> SystemProperties = cl.loadClass("android.os.SystemProperties");

            // 参数类型

            Class<?>[] paramTypes = new Class[1];
            paramTypes[0] = String.class;

            Method get = SystemProperties.getMethod("get", paramTypes);

            // 参数
            Object[] params = new Object[]{key};

            ret = (String) get.invoke(SystemProperties, params);

        } catch (Exception e) {
        }

        return ret;

    }

    /**
     * 根据Key获取值.
     *
     * @return 如果key不存在, 并且如果def不为空则返回def否则返回空字符串
     */
    public static String get(Context context, String key, String def) throws IllegalArgumentException {

        String ret = def;

        try {

            ClassLoader cl = context.getClassLoader();

            Class<?> SystemProperties = cl.loadClass("android.os.SystemProperties");

            // 参数类型

            Class<?>[] paramTypes = new Class[2];
            paramTypes[0] = String.class;
            paramTypes[1] = String.class;

            Method get = SystemProperties.getMethod("get", paramTypes);

            // 参数
            Object[] params = new Object[]{key, def};

            ret = (String) get.invoke(SystemProperties, params);

        } catch (Exception e) {
        }

        return ret;

    }

    /**
     * 根据给定的key返回int类型值.
     *
     * @param key 要查询的key
     * @param def 默认返回值
     * @return 返回一个int类型的值, 如果没有发现则返回默认值
     */
    public static Integer getInt(Context context, String key, int def) throws IllegalArgumentException {

        Integer ret = def;

        try {

            ClassLoader cl = context.getClassLoader();

            Class<?> SystemProperties = cl.loadClass("android.os.SystemProperties");

            // 参数类型

            Class<?>[] paramTypes = new Class[2];
            paramTypes[0] = String.class;
            paramTypes[1] = int.class;

            Method getInt = SystemProperties.getMethod("getInt", paramTypes);

            // 参数
            Object[] params = new Object[]{key, def};

            ret = (Integer) getInt.invoke(SystemProperties, params);

        } catch (Exception e) {
        }

        return ret;

    }

    /**
     * 根据给定的key返回long类型值.
     *
     * @param key 要查询的key
     * @param def 默认返回值
     * @return 返回一个long类型的值, 如果没有发现则返回默认值
     */
    public static Long getLong(Context context, String key, long def) throws IllegalArgumentException {

        Long ret = def;

        try {

            ClassLoader cl = context.getClassLoader();

            Class<?> SystemProperties = cl.loadClass("android.os.SystemProperties");

            // 参数类型

            Class<?>[] paramTypes = new Class[2];
            paramTypes[0] = String.class;
            paramTypes[1] = long.class;

            Method getLong = SystemProperties.getMethod("getLong", paramTypes);

            // 参数
            Object[] params = new Object[]{key, def};

            ret = (Long) getLong.invoke(SystemProperties, params);

        } catch (Exception e) {
        }

        return ret;

    }

    /**
     * 根据给定的key返回boolean类型值. 如果值为 'n', 'no', '0', 'false' or 'off' 返回false.
     * 如果值为'y', 'yes', '1', 'true' or 'on' 返回true. 如果key不存在, 或者是其它的值, 则返回默认值.
     *
     * @param key 要查询的key
     * @param def 默认返回值
     * @return 返回一个boolean类型的值, 如果没有发现则返回默认值
     */
    public static Boolean getBoolean(Context context, String key, boolean def) throws IllegalArgumentException {

        Boolean ret = def;

        try {

            ClassLoader cl = context.getClassLoader();

            Class<?> SystemProperties = cl.loadClass("android.os.SystemProperties");

            // 参数类型

            Class<?>[] paramTypes = new Class[2];
            paramTypes[0] = String.class;
            paramTypes[1] = boolean.class;

            Method getBoolean = SystemProperties.getMethod("getBoolean", paramTypes);

            // 参数
            Object[] params = new Object[]{key, def};

            ret = (Boolean) getBoolean.invoke(SystemProperties, params);

        } catch (Exception e) {
        }

        return ret;

    }
}
