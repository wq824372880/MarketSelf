package com.zeekrlife.market.task.install;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.zeekrlife.common.ext.CommExtKt;
import com.zeekrlife.market.task.bean.PackagesBean;
import com.zeekrlife.market.task.uninstall.UninstallSilentManager;
import com.zeekrlife.market.task.utils.OverlayHelper;

import java.util.List;

@RequiresApi(Build.VERSION_CODES.P)
public class OverlayInstaller extends FwkInstaller {

    private static final String TAG = OverlayInstaller.class.getSimpleName();

    private volatile static OverlayInstaller instance;

    public static OverlayInstaller getInstance(Context context) {
        if (instance == null) {
            synchronized (OverlayInstaller.class) {
                if (instance == null) {
                    instance = new OverlayInstaller(context);
                }
            }
        }
        return instance;
    }

    public OverlayInstaller(Context context) {
        super(context);
    }

    /**
     * 验证解压目录下的APK文件。
     * 该方法首先调用超类的verify方法进行初步验证，然后针对"overlay"和"target"类型的APK进行额外的验证步骤。
     * 需要验证overlay和target APK的包信息是否一致。
     *
     * @param unzipDirPath 解压目录的路径，不能为空。
     * @return PackagesBean对象，包含验证通过的包信息；如果验证失败或参数无效，返回null。
     */
    @NonNull
    @Override
    public PackagesBean verify(@NonNull String unzipDirPath) {
        // 调用超类的verify方法进行初步验证
        PackagesBean packagesBean = super.verify(unzipDirPath);
        if (packagesBean == null) {
            return new PackagesBean();
        }

        // 初始化overlay和target APK的路径
        String overlayApkPath = "";
        String targetApkPath = "";
        // 遍历packageBeanList，分别获取overlay和target APK的路径
        for (PackagesBean.PackageBean packageBean : packagesBean.packageBeanList) {
            if ("overlay".equals(packageBean.pkgType)) {
                overlayApkPath = unzipDirPath + packageBean.apkName;
                Log.d(TAG, "verify, overlayApkPath: " + overlayApkPath);
            } else if ("target".equals(packageBean.pkgType)) {
                targetApkPath = unzipDirPath + packageBean.apkName;
                Log.d(TAG, "verify, targetApkPath: " + targetApkPath);
            } else {
                // 如果遇到非"overlay"或"target"类型的包，记录警告并退出验证流程
                Log.w(TAG, "verify, pkg_type is error");
                return packagesBean;
            }
        }

        // 获取overlay和target APK的包信息
        PackageInfo overlayPackageArchiveInfo = packageManager.getPackageArchiveInfo(overlayApkPath, PackageManager.GET_SIGNING_CERTIFICATES);
        PackageInfo targetPackageArchiveInfo = packageManager.getPackageArchiveInfo(targetApkPath, PackageManager.GET_SIGNING_CERTIFICATES);
        // 验证overlay和target APK的包信息是否合法
        if (overlayPackageArchiveInfo == null || TextUtils.isEmpty(overlayPackageArchiveInfo.packageName)) {
            Log.w(TAG, "verify, overlayPackageArchiveInfo is null or its packageName is empty");
            return packagesBean;
        }
        if (targetPackageArchiveInfo == null || TextUtils.isEmpty(targetPackageArchiveInfo.packageName)) {
            Log.w(TAG, "verify, targetPackageArchiveInfo is null or its packageName is empty");
            return packagesBean;
        }
        // 验证overlay和target APK的包名是否一致
        String overlayInfo = OverlayHelper.getOverlayInfo(overlayPackageArchiveInfo.packageName);
        if (overlayInfo != null && !overlayInfo.equals(targetPackageArchiveInfo.packageName)) {
            Log.w(TAG, "verify, overlayPackageArchiveInfo's targetPackageName is incorrect");
            return packagesBean;
        }
        // 如果所有验证通过，则返回packageBean信息
        return packagesBean;
    }

    @Override
    public boolean verifySystem() {
        return false;
    }

    /**
     * 安装指定的包。此方法首先会检查是否有标记为"target"类型的包未安装，如果是，则先卸载该包的所有overlay。
     *
     * @param unzipDirPath 解压目录路径，用于安装包的解压位置。
     * @param packagesBean 包含待安装包信息的PackagesBean对象。
     * @return 返回安装结果，true表示安装成功，false表示安装失败。
     */
    @Override
    public boolean install(@NonNull String unzipDirPath, @NonNull PackagesBean packagesBean) {
        // 遍历包列表，查找标记为"target"类型的包，并尝试卸载其所有对应的overlay包
        for (PackagesBean.PackageBean packageBean : packagesBean.packageBeanList) {
            if ("target".equals(packageBean.pkgType)) {
                PackageInfo targetPackageInfo = null;
                try {
                    // 尝试获取包信息，判断是否已安装
                    targetPackageInfo = packageManager.getPackageInfo(packageBean.pkgName, 0);
                } catch (PackageManager.NameNotFoundException e) {
                    // 日志记录未找到包名异常
                    CommExtKt.logStackTrace(e);
                }
                if (targetPackageInfo == null) {
                    // 如果target包未安装，卸载所有对应的overlay包
                    List<String> overlayPackageNameList = OverlayHelper.getOverlayInfosForTarget(packageBean.pkgName);
                    if (overlayPackageNameList != null && !overlayPackageNameList.isEmpty()) {
                        for (String overlayPackageName : overlayPackageNameList) {
                            // 根据Android版本选择合适的卸载方法
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                                UninstallSilentManager.getInstance(context).uninstallSilent(overlayPackageName, false, null);
                            } else {
                                UninstallSilentManager.getInstance(context).uninstallSilent4L(overlayPackageName, false, null);
                            }
                        }
                    }
                    break; // 找到并处理完一个target包后即退出循环
                }
            }
        }
        // 调用父类的install方法继续安装流程
        return super.install(unzipDirPath, packagesBean);
    }
}
