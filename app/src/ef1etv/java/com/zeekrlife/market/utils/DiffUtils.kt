package com.zeekrlife.market.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.zeekrlife.common.ext.deepLinkToStartActivity
import com.zeekrlife.common.ext.toStartActivity
import com.zeekrlife.market.R
import com.zeekrlife.market.ui.activity.HomeTVActivity
import com.zeekrlife.market.ui.activity.LauncherTVActivity
import com.zeekrlife.market.ui.activity.SearchTVActivity

/**
 * @author mac
 * @date 2022/12/23 14:18
 * description：TODO
 */
object DiffUtils {

//    fun getSearchRouterViewFromHome(context: Context): View? {
//        return LayoutInflater.from(context).inflate(R.layout.item_home_search_bar, null)
//    }

    /**
     * 从Fragment中获取搜索路由视图。
     *
     * @param context 上下文环境，用于访问应用全局功能。
     * @return 返回搜索路由视图的引用。当前实现中，此方法总是返回null。
     */
    fun getSearchRouterViewFromFragment(context: Context): View? {
        return null
    }

    /**
     * 获取启动Zeekr背景位置的函数
     *
     * 该函数没有参数。
     *
     * @return FloatArray 返回一个包含三个浮点数的数组，分别代表背景位置的三个阶段。
     */
    fun getLaunchZeekrBgPositions(): FloatArray {
        // 返回预定义的背景位置数组
        return floatArrayOf(0f, 0.5f, 0.9f)
    }

    /**
     * 启动电视界面的活动。
     *
     * @param context 上下文环境，用于启动活动。
     * @return 总是返回 true，代表操作成功执行。
     */
    fun toTv(context: Context): Boolean {
        // 使用Intent启动HomeTVActivity
        context.startActivity(Intent(context, HomeTVActivity::class.java))
        return true
    }

    /**
     * 启动名为LauncherTVActivity的Activity。
     *
     * @param context 上下文，通常是指当前的Activity或Application对象。
     * @return 总是返回true，表示启动Activity操作完成。
     */
    fun toLauncherTVActivity(context: Context): Boolean {
        // 使用context启动一个名为LauncherTVActivity的Activity
        context.startActivity(Intent(context, LauncherTVActivity::class.java))
        return true
    }

    /**
     * 跳转到搜索电视活动页面。
     *
     * @param activity 当前的活动实例，可以为null。
     * @param bundle 传递给目标活动的Bundle参数。
     * @return 总是返回true，表示跳转操作已执行。
     */
    fun toSearchTvActivity(activity: Activity?, bundle: Bundle):Boolean {
        // 使用deepLinkToStartActivity方法启动SearchTVActivity，并传递参数
        deepLinkToStartActivity(activity, SearchTVActivity::class.java, bundle)
        return true
    }

    /**
     * 将当前上下文转换为Cx格式。
     *
     * @param context 表示当前的上下文环境。
     * @return 返回一个布尔值，当前实现始终返回false。
     */
    fun toCx(context: Context): Boolean {
        return false
    }

    /**
     * 将当前上下文转换为Launcher活动的函数。
     *
     * @param context 当前的上下文环境，用于进行转换操作。
     * @return 返回一个布尔值，当前实现总是返回false，表示转换操作未成功。
     */
    fun toLauncherCxActivity(context: Context): Boolean {
        return false
    }

    /**
     * 将当前活动转换为搜索活动。
     *
     * @param activity 当前的活动实例，可以为 null。
     * @param bundle 传递给新活动的额外数据包。
     * @return 总是返回 false，表示函数逻辑未实现或不需要进一步操作。
     */
    fun toSearchCxActivity(activity: Activity?, bundle: Bundle): Boolean {
        return false
    }

    /**
     * 跳转到详情页的函数。
     *
     * @param activity 当前的Activity对象，用于启动新的Activity。
     * @param appVersionId 应用的版本ID，用于可能的版本信息检查或传递。
     * @return 总是返回false，表示函数逻辑未实现或不需要进一步操作。
     */
    fun toDetailCxActivity(activity: Activity?,appVersionId: Long):Boolean {
        return false
    }

    /**
     * 根据应用ID跳转到详情页面的函数。
     *
     * @param activity 当前的Activity对象，用于启动新的Activity。
     * @param appId 需要查询的应用的ID。
     * @return 返回一个布尔值，通常用于表示跳转是否成功。此函数始终返回false。
     */
    fun toDetailCxActivityByAppId(activity: Activity?, appId: Long):Boolean {
        return false
    }

    /**
     * 根据包名跳转到详情页面的函数
     *
     * @param activity 当前的Activity对象，用于启动新的Activity
     * @param packageName 需要跳转的详情页面的包名
     * @return Boolean 返回false，表示该函数暂时不实现跳转功能
     */
    fun toDetailCxActivityByPackageName(activity: Activity?, packageName: String): Boolean {
        return false
    }
}