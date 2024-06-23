package com.zeekr.car.tsp;

import android.content.Context;
import java.lang.reflect.Method;

/**
 * @author mac
 * @date 2022/7/19 14:26
 * description：TODO
 */
final public class PropertiesUtil {
    PropertiesUtil() {
    }

    public static String getStringProp(Context context, String key) throws IllegalArgumentException {
        return getString(context, key, "");
    }

    public static int getIntProp(Context context, String key) throws IllegalArgumentException {
        return getInt(context, key, 0);
    }

    public static String getString(Context context, String key, String def) throws IllegalArgumentException {
        String ret;
        try {
            ClassLoader cl = context.getClassLoader();
            Class SystemProperties = cl.loadClass("android.os.SystemProperties");
            Class[] paramTypes = new Class[] { String.class, String.class };
            Method get = SystemProperties.getMethod("get", paramTypes);
            Object[] params = new Object[] { new String(key), new String(def) };
            ret = (String) get.invoke(SystemProperties, params);
        } catch (IllegalArgumentException var9) {
            throw var9;
        } catch (Exception var10) {
            ret = def;
        }

        return ret;
    }

    public static Integer getInt(Context context, String key, int def) throws IllegalArgumentException {
        Integer ret = def;

        try {
            ClassLoader cl = context.getClassLoader();
            Class SystemProperties = cl.loadClass("android.os.SystemProperties");
            Class[] paramTypes = new Class[] { String.class, Integer.TYPE };
            Method getInt = SystemProperties.getMethod("getInt", paramTypes);
            Object[] params = new Object[] { new String(key), new Integer(def) };
            ret = (Integer) getInt.invoke(SystemProperties, params);
        } catch (IllegalArgumentException var9) {
            throw var9;
        } catch (Exception var10) {
            ret = def;
        }

        return ret;
    }

    public static Long getLong(Context context, String key, long def) throws IllegalArgumentException {
        Long ret = def;

        try {
            ClassLoader cl = context.getClassLoader();
            Class SystemProperties = cl.loadClass("android.os.SystemProperties");
            Class[] paramTypes = new Class[] { String.class, Long.TYPE };
            Method getLong = SystemProperties.getMethod("getLong", paramTypes);
            Object[] params = new Object[] { new String(key), new Long(def) };
            ret = (Long) getLong.invoke(SystemProperties, params);
        } catch (IllegalArgumentException var10) {
            throw var10;
        } catch (Exception var11) {
            ret = def;
        }

        return ret;
    }

    public static Boolean getBoolean(Context context, String key, boolean def) throws IllegalArgumentException {
        Boolean ret = def;

        try {
            ClassLoader cl = context.getClassLoader();
            Class SystemProperties = cl.loadClass("android.os.SystemProperties");
            Class[] paramTypes = new Class[] { String.class, Boolean.TYPE };
            Method getBoolean = SystemProperties.getMethod("getBoolean", paramTypes);
            Object[] params = new Object[] { new String(key), new Boolean(def) };
            ret = (Boolean) getBoolean.invoke(SystemProperties, params);
        } catch (IllegalArgumentException var9) {
            throw var9;
        } catch (Exception var10) {
            ret = def;
        }

        return ret;
    }
}
