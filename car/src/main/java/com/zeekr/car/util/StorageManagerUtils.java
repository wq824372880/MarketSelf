package com.zeekr.car.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.storage.StorageManager;
import android.util.Log;
import com.zeekr.basic.Common;
import java.io.File;
import java.lang.reflect.Method;

/**
 * @author Lei.Chen29
 * @date 2023/11/3 17:43
 * description：StorageManagerUtils
 */
public class StorageManagerUtils {

    private static final String TAG = "StorageManagerUtils";

    /**
     * 800M
     */
    private static final int MIN_NEED_SPACE = 800 * 1024 * 1024;

    /**
     * getStorageLowBytes
     *
     * @param file f
     */
    @SuppressLint("DiscouragedPrivateApi")
    public static long getStorageLowBytes(File file) {
        try {
            if (Common.app != null) {
                Context context = Common.app.getApplicationContext();
                StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
                Method storageLowBytesMethod = StorageManager.class.getDeclaredMethod("getStorageLowBytes", File.class);
                if (storageLowBytesMethod != null) {
                    storageLowBytesMethod.setAccessible(true);
                    long storageLowBytes = (long) storageLowBytesMethod.invoke(storageManager, file);
                    Log.e(TAG, "storageLowBytes  : " + storageLowBytes);
                    return storageLowBytes;
                }
            }
        } catch (Throwable t) {
            Log.e(TAG, "getStorageLowBytes error : " + Log.getStackTraceString(t));
        }
        return MIN_NEED_SPACE;
    }
}
