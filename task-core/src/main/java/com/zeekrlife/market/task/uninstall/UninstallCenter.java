package com.zeekrlife.market.task.uninstall;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

public class UninstallCenter {
    private static final String TAG = "UninstallCenter";

    private volatile static UninstallCenter instance;

    public static UninstallCenter getInstance() {
        if (instance == null) {
            synchronized (UninstallCenter.class) {
                if (instance == null) {
                    instance = new UninstallCenter();
                }
            }
        }
        return instance;
    }

    /**
     * 在后台线程中卸载指定的应用程序。
     * <p>该方法会根据Android版本的不同，使用不同的卸载方法进行卸载操作。</p>
     *
     * @param context        上下文对象，用于访问应用全局功能。
     * @param packageName    需要卸载的应用程序的包名。
     * @param keepData       指示是否在卸载时保留应用程序的数据。
     * @param installCallback 卸载回调接口，用于接收卸载操作的结果。
     */
    @WorkerThread
    public void unInstall(@NonNull Context context, @NonNull String packageName, boolean keepData, @NonNull UninstallCallback installCallback) {
        // 根据Android版本选择合适的卸载方法
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // 对于Android 5.0（LOLLIPOP）以下的版本，使用卸载静默方式
            UninstallSilentManager.getInstance(context).uninstallSilent(packageName, keepData, installCallback);
        } else {
            // 对于Android 5.0及以上的版本，使用适用于LOLLIPOP及更高版本的卸载静默方式
            UninstallSilentManager.getInstance(context).uninstallSilent4L(packageName, keepData, installCallback);
        }
    }
}
