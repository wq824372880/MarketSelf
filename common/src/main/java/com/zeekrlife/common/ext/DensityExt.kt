package com.zeekrlife.common.ext

import android.app.Activity
import android.app.UiModeManager
import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.zeekr.basic.appContext
import java.lang.reflect.InvocationTargetException

/**
 *  @description:
 *  @author xcl qq:244672784
 *  @Date 2020/7/1
 **/
/************************************** 单位转换*********************************************** */
/**
 * 像素密度
 */
fun getDisplayMetrics(activity: AppCompatActivity) = activity.resources.displayMetrics.density

/**
 * dp 转成为 px
 */
fun dp2px(dpValue: Float): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dpValue,
        appContext.resources.displayMetrics
    ).toInt()
}

fun dp2px(activity: AppCompatActivity,dpValue: Float): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dpValue,
        activity.resources.displayMetrics
    ).toInt()
}

val Float.dp get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,this, appContext.resources.displayMetrics)

val Int.dp get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), appContext.resources.displayMetrics).toInt()

/**
 * px 转成为 dp
 */
fun px2dp(activity: AppCompatActivity,pxValue: Float) = (pxValue / getDisplayMetrics(activity) + 0.5f).toInt()

/**
 * sp转px
 */
fun sp2px(activity: AppCompatActivity,spVal: Float): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        spVal,
        activity.resources.displayMetrics
    ).toInt()
}

/**
 * px转sp
 */
fun px2sp(activity: AppCompatActivity,pxVal: Float) = pxVal / activity.resources.displayMetrics.scaledDensity

/************************************** 屏幕宽高*********************************************** */

/**
 * 获取屏幕宽
 */
fun getScreenWidth(activity: AppCompatActivity): Int {
    val metric = DisplayMetrics()
    (activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        .getMetrics(metric)
    return metric.widthPixels
}

/**
 * 获取屏幕宽是否为2560
 */
fun getScreenWidthIs2560(activity: AppCompatActivity): Boolean {

    val metric = DisplayMetrics()
    (activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        .getMetrics(metric)
    return metric.widthPixels == 2560
}
fun getScreenWidthIs2560(): Boolean {

    val metric = DisplayMetrics()
    (appContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        .getMetrics(metric)
    return metric.widthPixels == 2560
}


fun getScreenWidthIs3200(activity: AppCompatActivity): Boolean {
    val metric = DisplayMetrics()
    (activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        .getMetrics(metric)
    return metric.widthPixels == 3200
}

fun getScreenWidthIs3200(): Boolean {
    val metric = DisplayMetrics()
    (appContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        .getMetrics(metric)
    return metric.widthPixels == 3200
}

fun getScreenWidthIs3840(activity: AppCompatActivity): Boolean {
    val metric = DisplayMetrics()
    (activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        .getMetrics(metric)
    return metric.widthPixels == 3840
}

fun getScreenWidthIs3840(): Boolean {
    val metric = DisplayMetrics()
    (appContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        .getMetrics(metric)
    return metric.widthPixels == 3840
}

/**
 * 吸顶屏
 */
fun getScreenWidthIs2880(): Boolean {
    val metric = DisplayMetrics()
    (appContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        .getMetrics(metric)
    return metric.widthPixels == 2880
}

/**
 * 获取屏幕高，包含状态栏，但不包含虚拟按键，如1920屏幕只有1794
 */
fun getScreenHeight(activity: AppCompatActivity): Int {
    val metric = DisplayMetrics()
    (activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        .getMetrics(metric)
    return metric.heightPixels
}

fun getScreenHeight(): Int {
    val metric = DisplayMetrics()
    (appContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        .getMetrics(metric)
    return metric.heightPixels
}

/**
 * cx靠背屏不参与适配

 */
fun getScreenHeightIs1440(activity: AppCompatActivity): Boolean {
    val metric = DisplayMetrics()
    (activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        .getMetrics(metric)
    return metric.heightPixels == 1440
}

fun getScreenHeightIs1440(): Boolean {
    val metric = DisplayMetrics()
    (appContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        .getMetrics(metric)
    return metric.heightPixels == 1440
}

/**
 * 获取屏幕宽
 */
fun getScreenWidth2(activity: AppCompatActivity): Int {
    val point = Point()
    (activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        .getSize(point)
    return point.x
}

/**
 * 获取屏幕高，包含状态栏，但不包含某些手机最下面的【HOME键那一栏】，如1920屏幕只有1794
 */
fun getScreenHeight2(activity: AppCompatActivity): Int {
    val point = Point()
    (activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        .getSize(point)
    return point.y
}

/**
 * 获取屏幕原始尺寸高度，包括状态栏以及虚拟功能键高度
 */
fun getAllScreenHeight(activity: AppCompatActivity): Int {
    val display =
        (appContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
    try {
        val displayMetrics = DisplayMetrics()
        val method =
            Class.forName("android.view.Display").getMethod(
                "getRealMetrics",
                DisplayMetrics::class.java
            )
        method.invoke(display, displayMetrics)
        return displayMetrics.heightPixels
    } catch (e: NoSuchMethodException) {
        e.localizedMessage
    } catch (e: IllegalAccessException) {
        e.localizedMessage
    } catch (e: InvocationTargetException) {
        e.localizedMessage
    } catch (e: ClassNotFoundException) {
        e.localizedMessage
    } catch (e: Exception) {
        e.localizedMessage
    }catch (e: Exception) {
        e.logStackTrace()
    }
    return 0
}

/*************************** 状态栏、标题栏、虚拟按键**************************************** */

/**
 * 状态栏高度，单位px，一般为25dp
 */
fun getStatusBarHeight(activity: AppCompatActivity): Int {
    var height = 0
    val resourceId =
        activity.resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        height = activity.resources.getDimensionPixelSize(resourceId)
    }
    return height
}

/**
 * 状态栏高度，单位px，【注意】要在onWindowFocusChanged中获取才可以
 */
fun getStatusBarHeight2(activity: Activity): Int {
    val rect = Rect()
    //DecorView是Window中的最顶层view，可以从DecorView获取到程序显示的区域，包括标题栏，但不包括状态栏。所以状态栏的高度即为显示区域的top坐标值
    activity.window.decorView.getWindowVisibleDisplayFrame(rect)
    return rect.top
}

/**
 * 标题栏的高度，【注意】要在onWindowFocusChanged中获取才可以
 */
fun getTitleBarHeight(activity: AppCompatActivity): Int {
    val contentTop =
        activity.window.findViewById<View>(Window.ID_ANDROID_CONTENT)
            .top
    return contentTop - getStatusBarHeight(activity)
}

/**
 * 获取 虚拟按键的高度
 */
fun getBottomBarHeight(activity: AppCompatActivity) = getAllScreenHeight(activity) - getScreenHeight(activity)

/**
 * 获取是否是夜间模式
 */
fun getUINightMode(): Boolean {
    val uiMode = appContext.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
    return uiMode.nightMode == UiModeManager.MODE_NIGHT_YES
}