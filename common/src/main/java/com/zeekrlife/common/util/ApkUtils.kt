package com.zeekrlife.common.util

import android.app.ActivityManager
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources.NotFoundException
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import com.zeekrlife.common.ext.logStackTrace


/**
 * 1. 安装apk
 * 2. 获取本机安装的应用程序信息-包名、版本
 */
object ApkUtils {

    /**
     * 封装App信息的Bean类
     * @param name        名称
     * @param icon        图标
     * @param packageName 包名
     * @param versionName 版本号
     * @param versionCode 版本Code
     * @param isSD        是否安装在SD卡
     * @param isUser      是否是用户程序
     */
    data class AppInfo(
        val name: String?, val icon: Drawable?, val packageName: String?,
        val versionName: String?, val versionCode: Int, val isSD: Boolean, val isUser: Boolean
    )

    /**
     * 根据包名获取意图
     *
     * @param context     上下文
     * @param packageName 包名
     * @return 意图
     */
    private fun getIntentByPackageName(context: Context, packageName: String): Intent? {
        return context.packageManager.getLaunchIntentForPackage(packageName)
    }

    /**
     * 根据包名判断App是否安装
     *
     * @param context     上下文
     * @param packageName 包名
     * @return true: 已安装<br></br>false: 未安装
     */
    fun isAppInstalled(context: Context, packageName: String): Boolean {
        return getIntentByPackageName(context, packageName) != null
    }

    /**
     * 打开指定包名的App
     *
     * @param context     上下文
     * @param packageName 包名
     * @return true: 打开成功<br></br>false: 打开失败
     */
    fun openAppByPackageName(context: Context, packageName: String,displayId: Int = 0): Boolean {
        val intent = getIntentByPackageName(context, packageName)
        if (intent != null) {
            val activityOptions = ActivityOptions.makeBasic()
            activityOptions.setLaunchDisplayId(displayId)
            context.startActivity(intent,activityOptions.toBundle())
            return true
        }
        return false
    }

    /**
     * 判断当前App处于前台还是后台
     *
     * 需添加权限 android.permission.GET_TASKS
     *
     * 并且必须是系统应用该方法才有效
     *
     * @param context 上下文
     * @return true: 后台<br></br>false: 前台
     */
    fun isAppBackground(context: Context): Boolean {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val tasks = am.getRunningTasks(1)
        if (tasks.isNotEmpty()) {
            val topActivity = tasks[0].topActivity
            if (topActivity!!.packageName != context.packageName) {
                return true
            }
        }
        return false
    }

    /**
     * 根据包名获取App信息
     *
     * AppInfo（名称，图标，包名，版本号，版本Code，是否安装在SD卡，是否是用户程序）
     *
     * @param context 上下文
     * @return 当前应用的AppInfo
     */
    fun getAppInfo(context: Context, packageName: String): AppInfo? {
        val pm = context.packageManager
        var pi: PackageInfo? = null
        try {
            pi = pm.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.logStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return if (pi != null) getInfo(context, pm, pi) else null
    }

    /**
     * 获取当前App信息
     *
     * AppInfo（名称，图标，包名，版本号，版本Code，是否安装在SD卡，是否是用户程序）
     *
     * @param context 上下文
     * @return 当前应用的AppInfo
     */
    fun getAppInfo(context: Context): AppInfo? {
        return getAppInfo(context, context.applicationContext.packageName)
    }

    /**
     * 得到AppInfo的Bean
     *
     * @param pm 包的管理
     * @param pi 包的信息
     * @return AppInfo类
     */
    private fun getInfo(context: Context, pm: PackageManager, pi: PackageInfo): AppInfo {
        val ai = pi.applicationInfo
        val name = ai.loadLabel(pm).toString()
        val packageName = pi.packageName
        val icon = getHigherDensityIcon(context, packageName) ?: ai.loadIcon(pm)
        val versionName = pi.versionName
        val versionCode = pi.versionCode
        val isSD = ApplicationInfo.FLAG_SYSTEM and ai.flags != ApplicationInfo.FLAG_SYSTEM
        val isUser = ApplicationInfo.FLAG_SYSTEM and ai.flags != ApplicationInfo.FLAG_SYSTEM
        return AppInfo(name, icon, packageName, versionName, versionCode, isSD, isUser)
    }

    /**
     * 获取本地已安装应用的 更高像素密度 的图标
     */
    private fun getHigherDensityIcon(context: Context, packageName: String): Drawable? {
        val pm = context.packageManager
        return try {
            val pi = pm.getPackageInfo(packageName, 0)
            val otherAppCtx =
                context.createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY)
            val displayMetrics = intArrayOf(
//                DisplayMetrics.DENSITY_XXXHIGH,
                DisplayMetrics.DENSITY_XXHIGH,
                DisplayMetrics.DENSITY_XHIGH,
                DisplayMetrics.DENSITY_HIGH,
            )
            for (displayMetric in displayMetrics) {
                try {
                    val d = otherAppCtx.resources.getDrawableForDensity(
                        pi.applicationInfo.icon,
                        displayMetric
                    )
                    if (d != null) {
                        return d
                    }
                } catch (e: NotFoundException) {
                    continue
                } catch (e: Exception) {
                    continue
                }
            }

            pm.getApplicationInfo(packageName, 0).loadIcon(pm)
        } catch (e: Exception) {
            null
        }
    }

    fun getAllAppsInfo(context: Context): List<AppInfo> {
        return getAllAppsInfo(context, true)
    }

    /**
     * 获取所有已安装App信息
     *
     * AppInfo（名称，图标，包名，版本号，版本Code，是否安装在SD卡，是否是用户程序）
     *
     * 依赖上面的getBean方法
     *
     * @param context 上下文
     * @return 所有已安装的AppInfo列表
     */
    fun getAllAppsInfo(context: Context, filterNotActive: Boolean): List<AppInfo> {
        val list: MutableList<AppInfo> = ArrayList()
        val pm = context.packageManager
        // 获取系统中安装的所有软件信息
        val installedPackages = pm.getInstalledPackages(0)
        for (pi in installedPackages) {
            if (pi != null) {
                //是否过滤不能启动App
                if (filterNotActive) {
                    val isActiveApp = isActiveApp(context, pi.packageName)
                    if (isActiveApp) continue
                }
                list.add(getInfo(context, pm, pi))
            }
        }
        return list
    }

    /**
     * 跟进包名获取
     */
    fun getPackageInfo(context: Context, packageName: String?): PackageInfo? {
        if (packageName.isNullOrEmpty()) {
            return null
        }
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = context.packageManager
                .getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
        } catch(e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }catch (e: Exception) {
            e.printStackTrace()
        }
        return packageInfo
    }

    /**
     * 是否可启动
     */
    fun isActiveApp(context: Context, packageName: String): Boolean {
        return context.packageManager.getLaunchIntentForPackage(packageName) == null
    }

    /**
     * 是否为Android或者Google系统应用
     * 包名以"android、com.android、google、com.google"开头的应用
     *
     * @param packageName
     * @return
     */
    fun isAndroidOrGoogleApp(packageName: String): Boolean {
//        if (TextUtils.isEmpty(packageName)) {
//            return false
//        }
//        return (packageName.startsWith("android") || packageName.startsWith("com.android")
//                || packageName.startsWith("google") || packageName.startsWith("com.google"))
        return false
    }

    /**
     * 判断是否为系统应用
     * @param applicationInfo
     */
    fun isSystemApp(applicationInfo: ApplicationInfo?): Boolean {
        try {
            if (applicationInfo == null) return false
            return (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
        } catch (e: NullPointerException) {
            // 捕获空指针异常
            e.printStackTrace()
        } catch (e: Exception) {
            e.logStackTrace()
        }
        return false
    }

    /**
     * 判断是否是系统更新APP
     * @param applicationInfo
     */
    fun isUpdatedSystemApp(applicationInfo: ApplicationInfo?): Boolean {
        try {
            if (applicationInfo == null) return false
            return (applicationInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
        } catch (e: NullPointerException) {
            // 捕获空指针异常
            e.printStackTrace()
        } catch (e: Exception) {
            e.logStackTrace()
        }
        return false
    }
}