package com.zeekr.car.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * {@see android.os.SystemProperties}
 */
public final class SystemProperties {

    private static Class<?> getSystemPropertiesClass() {
        try {
            return Class.forName("android.os.SystemProperties");
        } catch (ClassNotFoundException e) {
           CarLogUtils.logStackTrace(e);
        }
        return null;
    }

    public static String get( String key,  String def) {
        Class<?> clazz = getSystemPropertiesClass();
        if (clazz == null) {
            return def;
        }
        try {
            Method method = clazz.getMethod("get", String.class, String.class);
            return (String) method.invoke(clazz, key, def);
        } catch (NoSuchMethodException e) {
           CarLogUtils.logStackTrace(e);
        } catch (IllegalAccessException e) {
           CarLogUtils.logStackTrace(e);
        } catch (InvocationTargetException e) {
           CarLogUtils.logStackTrace(e);
        }
        return def;
    }

    public static void set( String key,  String value) {
        Class<?> clazz = getSystemPropertiesClass();
        if (clazz == null) {
            return;
        }
        try {
            Method method = clazz.getMethod("set", String.class, String.class);
            method.invoke(clazz, key, value);
        } catch (NoSuchMethodException e) {
           CarLogUtils.logStackTrace(e);
        } catch (IllegalAccessException e) {
           CarLogUtils.logStackTrace(e);
        } catch (InvocationTargetException e) {
           CarLogUtils.logStackTrace(e);
        }
    }

    public static String getString( String key) {
        return get(key, "");
    }

    public static String getString( String key,  String def) {
        return get(key, def);
    }

    public static int getInt( String key) {
        return getInt(key, 0);
    }

    public static int getInt( String key, int def) {
        String value = get(key, String.valueOf(def));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
           CarLogUtils.logStackTrace(e);
        }
        return def;
    }

    public static long getLong(String key) {
        return getLong(key, 0);
    }

    public static long getLong(String key, long def) {
        String value = get(key, String.valueOf(def));
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
           CarLogUtils.logStackTrace(e);
        }
        return def;
    }

    public static boolean getBoolean(String key) {
        String value = get(key, "");
        return Boolean.parseBoolean(value);
    }
}
