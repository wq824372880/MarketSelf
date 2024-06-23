package com.zeekrlife.market.task.utils;

import android.content.pm.PackageManager;
import android.util.Log;


import com.zeekrlife.net.interception.logging.util.XLog;

import java.lang.reflect.Field;

public class PackageManagerHelper {
    private static final String TAG = PackageManagerHelper.class.getSimpleName();

    /**
     * 获取PackageManager中DELETE_KEEP_DATA标志的值。
     * 该方法尝试通过反射从android.content.pm.PackageManager类中获取DELETE_KEEP_DATA字段的值，
     * 如果获取失败，则返回默认值0x00000001。
     *
     * @return int 返回DELETE_KEEP_DATA标志的整型值。如果获取成功，则返回该字段的值；如果获取失败，则返回默认值1。
     */
    public static int DELETE_KEEP_DATA() {
        try {
            // 尝试通过反射获取PackageManager类中的DELETE_KEEP_DATA字段的值
            Class<?> classPackageManager = Class.forName("android.content.pm.PackageManager");
            Field fieldDELETE_KEEP_DATA = classPackageManager.getField("DELETE_KEEP_DATA");
            return (int) fieldDELETE_KEEP_DATA.get(PackageManager.class);
        } catch (Throwable throwable) {
            // 如果在获取过程中发生异常，则打印异常堆栈信息
            XLog.INSTANCE.e(TAG, "DELETE_KEEP_DATA:" + Log.getStackTraceString(throwable));
        }
        // 如果获取失败，返回默认值
        return 0x00000001;
    }
}
