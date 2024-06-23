package com.zeekrlife.market.utils

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.zeekrlife.common.ext.deepLinkToStartActivity
import com.zeekrlife.common.ext.toStartActivity
import com.zeekrlife.market.ui.activity.AppDetailCXActivity
import com.zeekrlife.market.ui.activity.HomeCXActivity
import com.zeekrlife.market.ui.activity.SearchCXActivity

/**
 * @author mac
 * @date 2022/12/23 14:18
 * description：TODO
 */
object DiffUtils {

    /**
     * 将当前对象转换为电视设备。这个函数暂时不实现任何功能，总是返回false。
     *
     * @param context 上下文环境，用于访问应用全局功能。
     * @return 总是返回false，表示当前对象不能被转换为电视设备。
     */
    fun toTv(context: Context): Boolean = false

    /**
     * 跳转到tv屏的搜索页面
     */
    fun toSearchTvActivity(activity: Activity?, bundle: Bundle): Boolean {
        return false
    }

    /**
     * 跳转到启动页面的搜索页面
     */
    fun toLauncherTVActivity(context: Context): Boolean {
        return false
    }

    /**
     * 将当前上下文转换到CX环境的首页。
     *
     * @param activity 当前的上下文环境，用于启动新的Activity。
     * @return 总是返回true，表示转换操作已执行。
     */
    fun toCx(activity: Activity): Boolean {
        // 启动CX环境的首页Activity
        toStartActivity(activity,HomeCXActivity::class.java,Bundle())
        return true
    }

    /**
     * 跳转到搜索界面的函数。
     *
     * @param activity 当前的Activity对象，用于启动新的Activity。
     * @param bundle 传递给目标Activity的Bundle参数。
     * @return 总是返回true，表示跳转操作已执行。
     */
    fun toSearchCxActivity(activity: Activity?, bundle: Bundle): Boolean {
        // 使用deepLinkToStartActivity方法启动SearchCXActivity，并传递参数
        deepLinkToStartActivity(activity, SearchCXActivity::class.java, bundle)
        return true
    }

    /**
     * 跳转到详情页面的函数。
     *
     * @param activity 当前的Activity对象，用于启动新的Activity。
     * @param appVersionId 应用版本的ID，用于在详情页面显示特定版本的信息。
     * @return 总是返回true，表示跳转操作已执行。
     */
    fun toDetailCxActivity(activity: Activity?, appVersionId: Long): Boolean {
        // 启动AppDetailCXActivity，并传入appVersionId
        AppDetailCXActivity.start(activity, appVersionId)
        return true
    }

    /**
     * 基于appId跳转到应用详情页面的函数。
     *
     * @param activity 当前的Activity实例，用于启动新的Activity。
     * @param appId 需要查看详情的应用的ID。
     * @return 总是返回true，表示跳转操作已执行。
     */
    fun toDetailCxActivityByAppId(activity: Activity?, appId: Long): Boolean {
        // 使用appId启动AppDetailCXActivity
        AppDetailCXActivity.startByAppId(activity, appId)
        return true
    }

    /**
     * 通过包名跳转到应用详情页面。
     *
     * @param activity 当前活动，用于启动新的Activity。
     * @param packageName 需要跳转的应用的包名。
     * @return 总是返回true，表示跳转操作已执行。
     */
    fun toDetailCxActivityByPackageName(activity: Activity?, packageName: String): Boolean {
        // 使用包名启动应用详情页面
        AppDetailCXActivity.startByPackageName(activity, packageName)
        return true
    }
}