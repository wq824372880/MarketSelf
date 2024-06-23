package com.zeekrlife.market.task.utils;

import android.content.IntentSender;
import android.content.pm.PackageInstaller;
import android.util.Log;

import androidx.annotation.NonNull;


import com.zeekrlife.net.interception.logging.util.XLog;

import java.lang.reflect.Method;

public class PackageInstallerHelper {
    private static final String TAG = PackageInstallerHelper.class.getSimpleName();

    /**
     * 用于卸载指定包名的应用程序。
     *
     * @param packageInstaller PackageInstaller对象，用于执行卸载操作。
     * @param packageName 要卸载的应用程序的包名。
     * @param flags 卸载时使用的标志位，可以控制卸载的行为。
     * @param statusReceiver 卸载操作完成后的状态接收器，用于接收卸载结果。
     * @return 返回卸载是否成功的布尔值。成功则返回true，失败则返回false。
     */
    public static boolean uninstall(@NonNull PackageInstaller packageInstaller,
                                    @NonNull String packageName, int flags,
                                    @NonNull IntentSender statusReceiver) {
        try {
            // 尝试使用PackageInstaller卸载指定包名的应用程序。
            packageInstaller.uninstall(packageName, statusReceiver);
            return true;
        } catch (Throwable throwable) {
            // 若卸载过程中出现异常，则捕获并记录异常信息。
            XLog.INSTANCE.e(TAG, "uninstall:" + Log.getStackTraceString(throwable));
        }
        return false;
    }
}
