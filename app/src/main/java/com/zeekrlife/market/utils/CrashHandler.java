package com.zeekrlife.market.utils;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteFullException;
import android.util.Log;

import com.zeekrlife.common.ext.CommExtKt;

/**
 * 全局异常捕获
 *
 * @author
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    /**
     * 系统默认UncaughtExceptionHandler
     */
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private String TAG = this.getClass().getSimpleName();

    @SuppressLint("StaticFieldLeak")
    private static CrashHandler mInstance;

    private CrashHandler() {

    }

    /**
     * 获取CrashHandler实例
     */
    public static synchronized CrashHandler getInstance() {
        if (null == mInstance) {
            mInstance = new CrashHandler();
        }
        return mInstance;
    }

    public void init() {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * uncaughtException 回调函数
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        //如果自己没处理交给系统处理
        if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        }
    }

    /**
     * @return 处理了该异常返回true, 否则false
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }

        if (ex instanceof SQLiteFullException) {
            //android.database.sqlite.SQLiteFullException: database or disk is full (code 13 SQLITE_FULL)
            //磁盘被写满时，下载库报错并为处理该运行时异常，暂且该方式处理，避免应用崩溃
            Log.e(TAG, Log.getStackTraceString(ex));
            CommExtKt.logStackTrace(ex);
            return true;
        }
        return false;
    }
}