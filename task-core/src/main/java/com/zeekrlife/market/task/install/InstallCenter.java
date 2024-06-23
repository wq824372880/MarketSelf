package com.zeekrlife.market.task.install;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

//import com.hjq.toast.ToastUtils;
import com.zeekr.basic.Common;
import com.zeekr.car.api.MediaCenterApiManager;
import com.zeekrlife.common.ext.CommExtKt;
import com.zeekrlife.common.util.FileIOUtils;
import com.zeekrlife.common.util.FileUtils;
import com.zeekrlife.common.util.GsonUtils;
import com.zeekrlife.common.util.ToastUtils;
import com.zeekrlife.market.task.R;
import com.zeekrlife.market.task.bean.PackagesBean;
import com.zeekrlife.market.task.data.expand.AppEntity;
import com.zeekrlife.market.task.data.expand.ExpandEntity;
import com.zeekrlife.market.task.data.expand.ExpandType;
import com.zeekrlife.market.task.data.source.TaskEntity;
import com.zeekrlife.market.task.utils.PackageUtils;
import com.zeekrlife.market.task.utils.ShellUtils;

import java.io.File;

public class InstallCenter {
    private static final String TAG = "zzzInstallCenter";

    private volatile static InstallCenter instance;

    private Handler handler = new Handler(Looper.getMainLooper());

    public static InstallCenter getInstance() {
        if (instance == null) {
            synchronized (InstallCenter.class) {
                if (instance == null) {
                    instance = new InstallCenter();
                }
            }
        }
        return instance;
    }

    /**
     * 检查当前应用是否为自己。
     *
     * @param packageName 要检查的包名。
     * @return 返回true如果当前应用的包名与传入的包名相等，否则返回false。
     */
    private boolean isSelfApp(String packageName) {
        try {
            // 尝试获取当前应用的包名并与传入的包名比较。
            return Common.app.getPackageName().equals(packageName);
        } catch (Exception e) {
            // 捕获异常并记录堆栈跟踪。
            CommExtKt.logStackTrace(e);
        }
        // 如果发生异常或不相等，则返回false。
        return false;
    }

    /**
     * 安装应用程序的函数。
     *
     * @param context 上下文环境，用于访问应用全局功能。
     * @param path 应用程序的安装包路径。
     * @param appEntity 包含应用程序详细信息的实体对象，如包名和版本号。
     * @param installCallback 安装过程的回调接口，用于接收安装结果。
     */
    private void appInstall(Context context, String path, AppEntity appEntity, InstallCallback installCallback) {
        try {
            // 根据Android版本选择不同的安装方法
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                // 对于5.0以下的版本使用常规的安装方法
                InstallManager.getInstance(context).install(path, appEntity.getPackageName(), appEntity.getVersionCode(), installCallback);
            } else {
                // 对于5.0及以上的版本使用兼容5.0的安装方法
                InstallManager.getInstance(context)
                    .install4L(path, appEntity.getPackageName(), appEntity.getVersionCode(), installCallback);
            }
        } catch (Exception e) {
            // 捕获安装过程中可能出现的异常，并进行日志记录
            CommExtKt.logStackTrace(e);
        }
    }

    public void install(@NonNull Context context, @NonNull TaskEntity taskInfo, @NonNull InstallCallback installCallback) {
        String path = taskInfo.getPath();
        ExpandEntity expandEntity = GsonUtils.fromJson(taskInfo.getExpand(), ExpandEntity.class);
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                switch (expandEntity.getType()) {
                    case ExpandType.APK:
                        AppEntity appEntity = GsonUtils.fromJson(taskInfo.getExpand(), AppEntity.class);
                        //应用市场自更新需Toast提示：下载完成，即将开始自动安装，应用市场将自动退出
                        if (isSelfApp(appEntity.getPackageName())) {
                            handler.postAtFrontOfQueue(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtils.show(Common.app.getString(R.string.app_install_self_updating));
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            ThreadPoolManager.getInstance().execute(new Runnable() {
                                                @Override
                                                public void run() {
                                                    appInstall(context, path, appEntity, installCallback);
                                                }
                                            });
                                        }
                                    }, 2000);
                                }
                            });
                        } else if ((PackageUtils.isTopActivity(Common.app, appEntity.getPackageName()) != null && PackageUtils.isTopActivity(Common.app, appEntity.getPackageName()))
                                || MediaCenterApiManager.checkAppIsPlaying(appEntity.getPackageName())) {
                            handler.postAtFrontOfQueue(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtils.INSTANCE.show(
                                        String.format(Common.app.getString(R.string.install_prepare_toast_tip), appEntity.getApkName()));
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            ThreadPoolManager.getInstance().execute(new Runnable() {
                                                @Override
                                                public void run() {
                                                    appInstall(context, path, appEntity, installCallback);
                                                }
                                            });
                                        }
                                    }, 5000);
                                }
                            });
                        } else {
                            appInstall(context, path, appEntity, installCallback);
                        }
                        break;
                    case ExpandType.OVERLAY:
                    case ExpandType.FRAMEWORK:
                        installCallback.installStarted();
                        if (!(PackageUtils.isSystemApplication(context) || ShellUtils.checkRootPermission())) {
                            Log.w(TAG, "install overlay/framework, require system/root permission");
                            installCallback.installError();
                            return;
                        }
                        if (expandEntity.getType() == ExpandType.OVERLAY) {
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                                Log.w(TAG, "install overlay, require Android P");
                                installCallback.installError();
                                return;
                            }
                            if (ContextCompat.checkSelfPermission(context, "android.permission.CHANGE_OVERLAY_PACKAGES")
                                != PackageManager.PERMISSION_GRANTED) {
                                Log.w(TAG, "install overlay, require CHANGE_OVERLAY_PACKAGES permission");
                                installCallback.installError();
                                return;
                            }
                            String hiddenapiPackageWhitelist =
                                FileIOUtils.Companion.readFile2String("/etc/sysconfig/hiddenapi-package-whitelist.xml");
                            if (TextUtils.isEmpty(hiddenapiPackageWhitelist) || !hiddenapiPackageWhitelist.contains(
                                "com.zeekrlife.market")) {
                                Log.w(TAG, "install overlay, require hiddenapi package whitelist");
                                installCallback.installError();
                                return;
                            }
                        }

                        String unzipDirPath = path.substring(0, path.lastIndexOf(".")) + File.separator;
                        if (!TextUtils.isEmpty(unzipDirPath)) {
                            String packagesFilePath = unzipDirPath + "packages.json";
                            if (FileUtils.INSTANCE.isFile(packagesFilePath)) {
                                // 说明是继续安装
                                PackagesBean packagesBean = FwkInstaller.getInstance(context).parsePackagesFile(packagesFilePath);
                                install(context, expandEntity.getType(), unzipDirPath, packagesBean, installCallback);
                                return;
                            }
                        }

                        // 1. 预校验
                        boolean result = FwkInstaller.getInstance(context).preVerify(path, taskInfo.getHash());
                        if (result) {
                            // 2. 解压
                            unzipDirPath = FwkInstaller.getInstance(context).unzip(path);
                            result = !TextUtils.isEmpty(unzipDirPath);
                            if (result) {
                                PackagesBean packagesBean = null;
                                // 3. 校验
                                if (expandEntity.getType() == ExpandType.OVERLAY) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                        packagesBean = OverlayInstaller.getInstance(context).verify(unzipDirPath);
                                    }
                                } else if (expandEntity.getType() == ExpandType.FRAMEWORK) {
                                    packagesBean = FwkInstaller.getInstance(context).verify(unzipDirPath);
                                }
                                // 4. 安装
                                install(context, expandEntity.getType(), unzipDirPath, packagesBean, installCallback);
                            } else {
                                if (unzipDirPath != null){
                                    FileUtils.INSTANCE.deleteAllInDir(unzipDirPath);
                                    FileUtils.INSTANCE.delete(unzipDirPath);
                                }
                                installCallback.installError();
                            }
                        } else {
                            installCallback.installError();
                        }
                        break;
                    default:

                        break;
                }
            }
        });
    }

    /**
     * overlay/target安装
     */
    private boolean install(@NonNull Context context, int expandType, @NonNull String unzipDirPath, @Nullable PackagesBean packagesBean,
        @NonNull InstallCallback installCallback) {
        boolean result = false;
        if (packagesBean != null) {
            result = FwkInstaller.getInstance(context).install(unzipDirPath, packagesBean);
            if (result) {
                FileUtils.INSTANCE.deleteAllInDir(unzipDirPath);
                FileUtils.INSTANCE.delete(unzipDirPath);
                installCallback.installCompleted();
            } else {
                if (expandType == ExpandType.OVERLAY) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        OverlayInstaller.getInstance(context).rollback(unzipDirPath, packagesBean);
                    }
                } else if (expandType == ExpandType.FRAMEWORK) {
                    FwkInstaller.getInstance(context).rollback(unzipDirPath, packagesBean);
                }
                FileUtils.INSTANCE.deleteAllInDir(unzipDirPath);
                FileUtils.INSTANCE.delete(unzipDirPath);
                installCallback.installError();
            }
        } else {
            FileUtils.INSTANCE.deleteAllInDir(unzipDirPath);
            FileUtils.INSTANCE.delete(unzipDirPath);
            installCallback.installError();
        }
        return result;
    }
}
