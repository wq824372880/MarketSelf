package com.zeekrlife.market.task.install;

import static com.zeekrlife.market.task.install.InstallManager.ACTION_INSTALL_PACKAGE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.zeekrlife.common.ext.CommExtKt;
import com.zeekrlife.common.util.ToastUtils;
import com.zeekrlife.market.task.utils.PackageUtils;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class InstallPackageReceiver extends BroadcastReceiver {
    private static final String TAG = "zzzInstallPackageReceiver";

    /**
     * 当接收到广播时，处理安装包的接收逻辑。
     *
     * @param context 上下文环境，用于访问应用全局功能。
     * @param intent 携带了广播的内容。
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // 如果传入的context或intent为null，则直接返回，不进行处理
        if (context == null || intent == null) {
            return;
        }
        // 打印接收到的广播的log
        Log.d(TAG, "onReceive() called with: context = [" + context + "], intent = [" + intent + "]");
        // 从intent中获取动作类型
        String action = intent.getAction();
        // 如果动作类型不是安装包的动作，则直接返回，不进行处理
        if (!ACTION_INSTALL_PACKAGE.equals(action)) {
            return;
        }
        // 从intent中获取额外信息
        Bundle extras = intent.getExtras();

        // 初始化安装状态为失败
        int status = PackageInstaller.STATUS_FAILURE;
        String msg;
        // 如果extras非空，从中解析安装状态和状态消息
        if (extras != null) {
            // 解析状态码
            int statusCode = extras.getInt("android.content.pm.extra.LEGACY_STATUS", -1);
            status = extras.getInt(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE);
            // 解析状态消息
            msg = extras.getString(PackageInstaller.EXTRA_STATUS_MESSAGE, "");
            // 打印安装状态和消息的log
            Log.d(TAG, "install status: " + status + "; msg: " + msg);
        } else {
            // 如果extras为空，打印log
            Log.d(TAG, "extras is null");
        }

        // 根据安装状态处理不同的逻辑
        switch (status) {
            case PackageInstaller.STATUS_PENDING_USER_ACTION:
                // 如果安装需要用户操作，则启动对应的确认界面
                Log.d(TAG, "install pending user action");
                Intent confirmIntent = (Intent) extras.get(Intent.EXTRA_INTENT);
                confirmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(confirmIntent);
                break;
            case PackageInstaller.STATUS_SUCCESS:
                // 如果安装成功，则仅打印log
                Log.d(TAG, "install success");
                break;
            // 处理各种安装失败的情况
            case PackageInstaller.STATUS_FAILURE:
            case PackageInstaller.STATUS_FAILURE_BLOCKED:
            case PackageInstaller.STATUS_FAILURE_ABORTED:
            case PackageInstaller.STATUS_FAILURE_INVALID:
            case PackageInstaller.STATUS_FAILURE_CONFLICT:
            case PackageInstaller.STATUS_FAILURE_STORAGE:
            case PackageInstaller.STATUS_FAILURE_INCOMPATIBLE:
            default:
                // 打印安装失败的log
                Log.w(TAG, "install failure, status: " + status);
                break;
        }
    }

    private void onInstallFailureToast(Context context, int statusCode) {
        if (statusCode == PackageUtils.INSTALL_FAILED_ALREADY_EXISTS) {
            showToast(context, com.zeekrlife.common.R.string.install_failed_already_exists);
        } else if (statusCode == PackageUtils.INSTALL_FAILED_INVALID_URI || statusCode == PackageUtils.INSTALL_FAILED_INVALID_APK) {
            showToast(context, com.zeekrlife.common.R.string.install_failed_invalid);
        } else if (statusCode == PackageUtils.INSTALL_FAILED_INSUFFICIENT_STORAGE || statusCode == PackageInstaller.STATUS_FAILURE_STORAGE) {
            showToast(context, com.zeekrlife.common.R.string.install_failed_insufficient_storage);
        } else if (statusCode == PackageUtils.INSTALL_FAILED_DUPLICATE_PACKAGE) {
            showToast(context, com.zeekrlife.common.R.string.install_failed_duplicate_package);
        } else if (statusCode == PackageUtils.INSTALL_FAILED_UPDATE_INCOMPATIBLE) {
            showToast(context, com.zeekrlife.common.R.string.install_failed_update_incompatible);
        } else if (statusCode == PackageUtils.INSTALL_FAILED_VERSION_DOWNGRADE) {
            showToast(context, com.zeekrlife.common.R.string.install_failed_version_downgrade);
        }
    }

    /**
     * 在UI线程上延迟1秒后展示Toast消息。
     *
     * @param context 上下文环境，用于访问应用全局功能。
     * @param resId 要展示的Toast消息的资源ID。
     */
    private void showToast(Context context, int resId) {
        try {
            // 延迟1秒后，在主线程中执行展示Toast消息的操作
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 使用ToastUtils的实例展示Toast消息
                    ToastUtils.INSTANCE.show(context, context.getString(resId),null);
                }
            }, 1000);
        } catch (Exception e) {
            // 捕获并记录异常信息
            CommExtKt.logStackTrace(e);
        }
    }
}
