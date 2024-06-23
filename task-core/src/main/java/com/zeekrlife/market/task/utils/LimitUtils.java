package com.zeekrlife.market.task.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.zeekr.car.util.SystemProperties;
import com.zeekrlife.common.ext.CommExtKt;
import com.zeekrlife.common.util.GsonUtils;
import com.zeekrlife.market.task.data.expand.AppEntity;
import com.zeekrlife.market.task.data.expand.ExpandEntity;
import com.zeekrlife.market.task.data.expand.ExpandType;
import com.zeekrlife.market.task.data.source.TaskEntity;
import com.zeekrlife.net.interception.logging.util.XLog;

import java.util.ArrayList;
import java.util.List;


public class LimitUtils {

    private static final String TAG = "LimitUtils";

    private static final boolean DEBUG = false;

    private static final String RELEASE_LIMIT_KEY = "ro.zeekrlife.appstore.limit";
    private static final String DEBUG_LIMIT_KEY = "persist.zeekrlife.appstore.limit";
    private static final String LIMIT_KEY = DEBUG ? DEBUG_LIMIT_KEY : RELEASE_LIMIT_KEY;

    /**
     * 检查指定的应用程序是否受到系统限制。
     * <p>
     * 该功能首先检查系统是否启用了应用限制功能，如果未启用，则直接返回未受限制。
     * 若启用，则获取当前安装的应用数量，并与系统设定的应用数量限制进行比较。
     * 如果安装的应用数量超过了限制数目，则认为指定的应用受限制。
     *
     * @param context 上下文对象，用于访问应用的环境信息。
     * @param pkgName 要检查的应用程序的包名。
     * @return 如果指定的应用受系统限制，则返回true；否则返回false。
     */
    public static boolean isLimitedBySystem(Context context, String pkgName) {
        // 检查系统是否启用了应用限制
        if (!isSystemLimitEnabled()) {
            return false;
        }

        // 获取系统设置的应用数量限制
        int appNumberLimit = SystemProperties.getInt(LIMIT_KEY);

        // 收集当前安装的第三方应用并且具有启动意图的应用包名
        List<String> installedPackageNameList = new ArrayList<>();
        PackageManager pkgManager = context.getPackageManager();
        List<PackageInfo> packageInfoList = pkgManager.getInstalledPackages(0);
        for (int index = 0; index < packageInfoList.size(); index++) {
            String installedPackageName = packageInfoList.get(index).packageName;
            // 如果是目标包名，则直接返回未受限制
            if (installedPackageName.equals(pkgName)) {
                if (DEBUG) {
                    XLog.INSTANCE.e(TAG, "update not limited : " + pkgName);
                }
                return false;
            }
            // 收集符合条件的第三方应用包名
            if (isThirdPartApp(pkgManager, installedPackageName) && hasLaucherIntent(pkgManager, installedPackageName)) {
                installedPackageNameList.add(installedPackageName);
            }
        }
        // 输出调试信息：当前安装的符合条件的应用数量
        if (DEBUG) {
            XLog.INSTANCE.e(TAG, "installed app number : " + installedPackageNameList.size());
        }
        // 判断安装的应用数量是否达到了系统限制
        if (installedPackageNameList.size() >= appNumberLimit) {
            // 输出调试信息：应用受限制
            if (DEBUG) {
                XLog.INSTANCE.e(TAG, "isLimitedBySystem : " + true);
            }
            return true;
        } else {
            // 输出调试信息：应用未受限制
            if (DEBUG) {
                XLog.INSTANCE.e(TAG, "isLimitedBySystem : " + false);
            }
            return false;
        }
    }

    /**
     * 判断给定的包是否为第三方应用。
     *
     * @param pkgManager PackageManager的实例，用于获取包信息。
     * @param pkgName 要判断的包名。
     * @return 返回true如果包是非系统应用，否则返回false。
     */
    private static boolean isThirdPartApp(PackageManager pkgManager, String pkgName) {
        try {
            // 尝试获取指定包的信息，并检查其是否为系统应用
            PackageInfo pkg = pkgManager.getPackageInfo(pkgName, PackageManager.GET_ACTIVITIES);
            return (pkg.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0;
        } catch (PackageManager.NameNotFoundException e) {
            // 如果包名未找到，记录日志并返回false
            CommExtKt.logStackTrace(e);
            return false;
        }
    }


    private static boolean hasLaucherIntent(PackageManager pkgManager, String pkgName) {
        return pkgManager.getLaunchIntentForPackage(pkgName) != null;
    }

    /**
     * 检查系统限制是否启用。
     * 该函数通过读取系统属性来判断是否启用了应用数量限制。
     * 如果系统属性指定的限制数量大于0，则认为限制启用；否则，认为限制未启用。
     *
     * @return boolean - 如果系统限制启用，则返回true；否则返回false。
     */
    public static boolean isSystemLimitEnabled() {
        // 从系统属性中获取应用数量限制值
        int appNumberLimit = SystemProperties.getInt(LIMIT_KEY);
        if (DEBUG) {
            // 在调试模式下，记录系统限制的最大应用数量
            XLog.INSTANCE.e(TAG, "system limit max : " + appNumberLimit);
        }
        // 判断应用数量限制是否启用
        if (appNumberLimit <= 0) {
            if (DEBUG) {
                // 在调试模式下，记录系统限制未启用的信息
                XLog.INSTANCE.e(TAG, "system limit not enabled");
            }
            return false; // 系统限制未启用
        } else {
            if (DEBUG) {
                // 在调试模式下，记录系统限制已启用的信息
                XLog.INSTANCE.e(TAG, "system limit enabled");
            }
            return true; // 系统限制启用
        }
    }

    /**
     * 判断任务是否受到系统限制。
     *
     * @param context 上下文对象，用于访问应用的环境信息。
     * @param taskInfo 任务实体，包含任务的详细信息。
     * @return 返回任务是否受系统限制的布尔值。如果任务是APK类型并且系统对该APK有限制，则返回true；否则返回false。
     */
    public static boolean isTaskLimitBySystem(Context context, TaskEntity taskInfo) {
        // 从任务扩展信息中解析出扩展类型
        String expandStr = taskInfo.getExpand();
        ExpandEntity expand = GsonUtils.fromJson(expandStr, ExpandEntity.class);
        int expandType;
        if (expand == null) {
            expandType = ExpandType.APK; // 默认扩展类型为APK
        } else {
            expandType = expand.getType(); // 从扩展实体中获取扩展类型
        }

        // 根据扩展类型判断任务是否受系统限制
        if (expandType == ExpandType.APK) {
            // 如果任务是APK类型，尝试从扩展信息解析出APK实体
            AppEntity expandEntity = GsonUtils.fromJson(expandStr, AppEntity.class);
            String packageName = expandEntity.getPackageName();
            // 检查系统是否限制了该APK
            return isLimitedBySystem(context, packageName);
        } else {
            // 非APK类型的任务不受系统限制
            return false;
        }
    }
}