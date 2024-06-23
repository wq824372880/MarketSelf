package com.zeekrlife.market.utils;

import com.zeekrlife.common.ext.CommExtKt;

import java.lang.reflect.Method;

/**
 * @author Lei.Chen29
 * @Description 9.0反射限制问题
 */
public final class ReflectionLimit {
    private static Object sVMRuntime;
    private static Method setHiddenApiExemptions;

    static {
        try {
            Method forName = Class.class.getDeclaredMethod("forName", String.class);
            Method getDeclaredMethod = Class.class.getDeclaredMethod("getDeclaredMethod", String.class, Class[].class);
            Class<?> vmRuntimeClass = (Class<?>) forName.invoke(null, "dalvik.system.VMRuntime");
            Method getRuntime = (Method) getDeclaredMethod.invoke(vmRuntimeClass, "getRuntime", null);
            setHiddenApiExemptions =
                (Method) getDeclaredMethod.invoke(vmRuntimeClass, "setHiddenApiExemptions", new Class[] { String[].class });
            setHiddenApiExemptions.setAccessible(true);
            sVMRuntime = getRuntime.invoke(null);
        } catch (Exception e) {
            CommExtKt.logStackTrace(e);
        }
    }

    /**
     * 消除限制
     */
    public static boolean clearLimit() {
        if (sVMRuntime == null || setHiddenApiExemptions == null) {
            return false;
        }
        try {
            setHiddenApiExemptions.invoke(sVMRuntime, new Object[] { new String[] { "L" } });
            return true;
        } catch (Exception e) {
            CommExtKt.logStackTrace(e);
        }
        return false;
    }
}