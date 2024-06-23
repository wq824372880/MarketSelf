package com.zeekrlife.market.task.install;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.zeekrlife.common.ext.CommExtKt;
import com.zeekrlife.common.util.FileIOUtils;
import com.zeekrlife.common.util.FileUtils;
import com.zeekrlife.common.util.GsonUtils;
import com.zeekrlife.market.task.bean.PackagesBean;
import com.zeekrlife.market.task.uninstall.UninstallSilentManager;
import com.zeekrlife.market.task.utils.FileVerifyUtils;
import com.zeekrlife.market.task.utils.OverlayHelper;
import com.zeekrlife.market.task.utils.ZipUtils;

import java.io.File;

public class FwkInstaller implements IFwkInstaller {

    private static final String TAG = "FwkInstaller";

    protected Context context;
    protected PackageManager packageManager;

    private volatile static FwkInstaller instance;

    public static FwkInstaller getInstance(Context context) {
        if (instance == null) {
            synchronized (FwkInstaller.class) {
                if (instance == null) {
                    instance = new FwkInstaller(context);
                }
            }
        }
        return instance;
    }

    public FwkInstaller(Context context) {
        this.context = context.getApplicationContext();
        this.packageManager = this.context.getPackageManager();
    }

    /**
     * hash预校验
     *
     * @param filePath
     * @param hash
     * @return
     */
    @Override
    public boolean preVerify(@NonNull String filePath, @NonNull String hash) {
        return FileVerifyUtils.verify(filePath, hash);
    }

    /**
     * 解压
     *
     * @param zipFilePath zip包路径
     * @return
     */
    @Override
    public String unzip(@NonNull String zipFilePath) {
        Log.d(TAG, "unzip() called with: zipFilePath = [" + zipFilePath + "]");
        if (TextUtils.isEmpty(zipFilePath) || zipFilePath.lastIndexOf(".") == -1) {
            Log.w(TAG, "unzip, zipFilePath is empty or invalid");
            return null;
        }
        if (!FileUtils.INSTANCE.isFile(zipFilePath)) {
            Log.w(TAG, "unzip, file of " + zipFilePath + " not exist");
            return null;
        } else {
            String unzipDirPath = zipFilePath.substring(0, zipFilePath.lastIndexOf(".")) + File.separator;
            FileUtils.INSTANCE.createOrExistsDir(unzipDirPath);
            FileUtils.INSTANCE.deleteFilesInDir(unzipDirPath);
            return ZipUtils.unZip(zipFilePath, unzipDirPath) ? unzipDirPath : null;
        }
    }

    /**
     * 校验
     *
     * @param unzipDirPath 解压目录
     * @return
     */
    @Override
    public PackagesBean verify(@NonNull String unzipDirPath) {
        Log.d(TAG, "verify() called with: unzipDirPath = [" + unzipDirPath + "]");
        if (!FileUtils.INSTANCE.isDir(unzipDirPath)) {
            Log.w(TAG, "verify, dir of " + unzipDirPath + " not exist");
            return null;
        }
        String packagesFilePath = unzipDirPath + "packages.json";
        if (!FileUtils.INSTANCE.isFile(packagesFilePath)) {
            Log.w(TAG, "verify, packages.json not exist");
            return null;
        }
        PackagesBean packagesBean = parsePackagesFile(packagesFilePath);
        if (packagesBean != null) {
            for (PackagesBean.PackageBean packageBean : packagesBean.packageBeanList) {
                Log.d(TAG, "start verify for " + packageBean);
                String apkPath = unzipDirPath + packageBean.apkName;
                if (!FileUtils.INSTANCE.isFile(apkPath)) {
                    Log.w(TAG, "verify, " + packageBean.apkName + " not exist");
                    return null;
                }
                PackageInfo packageArchiveInfo = packageManager.getPackageArchiveInfo(apkPath, PackageManager.GET_SIGNATURES);
                if (packageArchiveInfo == null) {
                    Log.w(TAG, "verify, packageArchiveInfo is null");
                    return null;
                }
                // 验包名
                if (!TextUtils.equals(packageArchiveInfo.packageName, packageBean.pkgName)) {
                    Log.w(TAG, "verify, packageArchiveInfo's packageName is incorrect");
                    return null;
                }
                // 验内部版本号
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                    if (packageArchiveInfo.versionCode != packageBean.pkgVersion) {
                        Log.w(TAG, "verify, packageArchiveInfo's versionCode is incorrect");
                        return null;
                    }
                } else {
                    if (packageArchiveInfo.getLongVersionCode() != packageBean.pkgVersion) {
                        Log.w(TAG, "verify, packageArchiveInfo's versionCode is incorrect");
                        return null;
                    }
                }
                if (!verify(packagesBean.packageName, packageBean, packageArchiveInfo)) {
                    return null;
                }
            }
        }
        return packagesBean;
    }

    /**
     * 是否需要验证为系统应用
     *
     * @return
     */
    @Override
    public boolean verifySystem() {
        return true;
    }

    /**
     * 安装指定目录下的APK文件。
     *
     * @param unzipDirPath 解压目录的路径。
     * @param packagesBean 包含待安装APK信息的PackagesBean对象。
     * @return 如果所有APK都成功安装则返回true，否则返回false。
     */
    @Override
    public boolean install(@NonNull String unzipDirPath, @NonNull PackagesBean packagesBean) {
        // 日志记录函数调用信息
        Log.d(TAG, "install() called with: unzipDirPath = [" + unzipDirPath + "], packagesBean = [" + packagesBean + "]");

        boolean lastResult = true;
        // 遍历所有待安装的包信息
        for (PackagesBean.PackageBean packageBean : packagesBean.packageBeanList) {
            // 如果包已经安装，则跳过该包的安装流程
            if (packageBean.installed) {
                Log.d(TAG, "package for " + packageBean + " has already installed, continue");
                continue;
            }

            // 拼接APK文件路径
            String apkPath = unzipDirPath + packageBean.apkName;
            // 备份已安装的APK
            String backupPath = backupApk(apkPath, packageBean.pkgName);
            if (!TextUtils.isEmpty(backupPath)) {
                packageBean.backupPath = backupPath;
                // 尝试获取已备份APK的版本号
                try {
                    packageBean.backupVersionCode = packageManager.getPackageInfo(packageBean.pkgName, 0).versionCode;
                } catch (PackageManager.NameNotFoundException e) {
                    CommExtKt.logStackTrace(e);
                }
                // 保存更新后的packages文件
                savePackagesFile(unzipDirPath, packagesBean);
                Log.d(TAG, "backup success");
            }

            // 开始安装APK
            Log.d(TAG, "install start with: apkPath = [" + apkPath + "]");
            // 根据Android版本选择安装方法
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                lastResult = InstallManager.getInstance(context).installSilent(apkPath, null);
            } else {
                lastResult = InstallManager.getInstance(context).installSilent4L(apkPath, packageBean.pkgName, packageBean.pkgVersion, null);
            }

            // 如果APK安装成功，则更新安装状态并检查是否需要启用应用
            if (lastResult) {
                // 标记为已安装
                packageBean.installed = true;
                // 保存安装状态
                savePackagesFile(unzipDirPath, packagesBean);
                // 如果需要，则启用应用
                if (packageBean.needEnable()) {
                    lastResult = OverlayHelper.setEnable(packageBean.needEnable, true);
                    if (!lastResult) {
                        Log.w(TAG, "setEnable failure");
                    }
                }
            }
            // 根据安装结果记录日志，并在失败时跳出循环
            if (lastResult) {
                Log.d(TAG, "install success");
            } else {
                Log.w(TAG, "install failure");
                break;
            }
        }
        // 返回最后一次安装操作的结果
        return lastResult;
    }

    /**
     * 回滚
     *
     * @param unzipDirPath
     * @param packagesBean
     */
    public void rollback(@NonNull String unzipDirPath, @NonNull PackagesBean packagesBean) {
        Log.d(TAG, "rollback() called with: unzipDirPath = [" + unzipDirPath + "], packagesBean = [" + packagesBean + "]");
        for (PackagesBean.PackageBean packageBean : packagesBean.packageBeanList) {
            if (packageBean.installed) {
                Log.d(TAG, "rollback start with: packageBean = [" + packageBean + "]");
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    boolean result = UninstallSilentManager.getInstance(context).uninstallSilent(packageBean.pkgName, packageBean.hasBackup(), null);
                    Log.d(TAG, "rollback, uninstallSilent() called with: result = [" + result + "]");
                    packageBean.installed = false;
                    savePackagesFile(unzipDirPath, packagesBean);
                    if (packageBean.hasBackup()) {
                        result = InstallManager.getInstance(context).installSilent(packageBean.backupPath, null);
                        Log.d(TAG, "rollback, installSilent() called with: result = [" + result + "]");
                        if (result) {
                            packageBean.backupPath = null;
                            savePackagesFile(unzipDirPath, packagesBean);
                        }
                    }
                } else {
                    boolean result = UninstallSilentManager.getInstance(context).uninstallSilent4L(packageBean.pkgName, packageBean.hasBackup(), null);
                    packageBean.installed = false;
                    savePackagesFile(unzipDirPath, packagesBean);
                    if (packageBean.hasBackup()) {
                        result = InstallManager.getInstance(context).installSilent4L(packageBean.backupPath, packageBean.pkgName, packageBean.backupVersionCode, null);
                        Log.d(TAG, "rollback, installSilent4L() called with: result = [" + result + "]");
                        if (result) {
                            packageBean.backupPath = null;
                            savePackagesFile(unzipDirPath, packagesBean);
                        }
                    }
                    Log.d(TAG, "rollback finished with: result = [" + result + "]");
                }
            }
        }
        Log.d(TAG, "rollback() called finished");
    }

    /**
     * 解析packages.json
     *
     * @param packagesFilePath
     * @return
     */
    public PackagesBean parsePackagesFile(String packagesFilePath) {
        if (!FileUtils.INSTANCE.isFile(packagesFilePath)) {
            Log.w(TAG, "parsePackagesFile, file of " + packagesFilePath + " not exist");
            return null;
        }
        String packagesInfoStr = FileIOUtils.Companion.readFile2String(packagesFilePath);
        return GsonUtils.fromJson(packagesInfoStr, PackagesBean.class);
    }

    private boolean verify(@NonNull String packageName, @NonNull PackagesBean.PackageBean packageBean, @NonNull PackageInfo packageArchiveInfo) {
        Log.d(TAG, "verify() called with: packageBean = [" + packageBean + "], packageArchiveInfo = [" + packageArchiveInfo + "]");
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(packageArchiveInfo.packageName, PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            //
        }
        if (packageInfo != null) {
            if (!packageBean.installed) {
                // 本地已安装，校验如下信息
                if (verifySystem() && !packageName.equals(packageBean.pkgName)) {
                    // 验系统应用
                    if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                        Log.w(TAG, "verify, app is not system");
                        return false;
                    }
                }
                // 验签名
                if (packageInfo.signatures != null && packageInfo.signatures.length > 0
                        && packageArchiveInfo.signatures != null && packageArchiveInfo.signatures.length > 0
                        && TextUtils.equals(packageInfo.signatures[0].toCharsString(),
                        packageArchiveInfo.signatures[0].toCharsString())) {
                    // 签名一致
                } else {
                    Log.w(TAG, "verify, packageArchiveInfo's signature is incorrect");
                    return false;
                }
                // 验内部版本号
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                    if (packageInfo.versionCode >= packageArchiveInfo.versionCode) {
                        Log.w(TAG, "verify, package is installed");
                        packageBean.installed = true;
                    }
                } else {
                    if (packageInfo.getLongVersionCode() >= packageArchiveInfo.getLongVersionCode()) {
                        Log.w(TAG, "verify, package is installed");
                        packageBean.installed = true;
                    }
                }
                // 验persistent
                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_PERSISTENT) != 0) {
                    Log.w(TAG, "verify, app is persistent");
                    return false;
                }
            }
            return true;
        } else {
            if (packageBean.installed) {
                packageBean.installed = false;
            }
            if (verifySystem()) {
                // 最后一个应用与最外层包名匹配则跳过校验
                return packageName.equals(packageBean.pkgName);
            } else {
                return true;
            }
        }
    }

    /**
     * 备份
     * <p>
     * xxx/Download/overlay/xxx/com.ecarx.sample.apk -> xxx/Download/overlay/xxx/backup/com.ecarx.sample.apk
     *
     * @param apkPath
     * @param pkgName
     * @return
     */
    private String backupApk(String apkPath, String pkgName) {
        Log.d(TAG, "backupApk() called with: apkPath = [" + apkPath + "], pkgName = [" + pkgName + "]");
        try {
            String sourceDir = packageManager.getPackageInfo(pkgName, 0).applicationInfo.sourceDir;
            String apkBackupDirPath = apkPath.substring(0, apkPath.lastIndexOf(".apk")) + "backup" + File.separator;
            FileUtils.INSTANCE.createOrExistsDir(apkBackupDirPath);
            FileUtils.INSTANCE.deleteFilesInDir(apkBackupDirPath);
            String apkBackupPath = apkBackupDirPath + apkPath.lastIndexOf(File.separator);
            return FileUtils.INSTANCE.copyFile(sourceDir, apkBackupPath) ? apkBackupPath : null;
        } catch (PackageManager.NameNotFoundException e) {
            //
        }
        return null;
    }

    /**
     * 保存备份信息
     *
     * @param unzipDirPath
     * @param packagesBean
     */
    private void savePackagesFile(@NonNull String unzipDirPath, @NonNull PackagesBean packagesBean) {
        FileIOUtils.Companion.writeFileFromString(unzipDirPath + "packages.json", GsonUtils.toJson(packagesBean));
    }
}
