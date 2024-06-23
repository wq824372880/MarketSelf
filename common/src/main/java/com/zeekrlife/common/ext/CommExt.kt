package com.zeekrlife.common.ext

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.zeekr.basic.appContext
import com.zeekr.basic.currentActivity
import com.zeekr.sdk.multidisplay.setting.bean.ScreenType
import com.zeekrlife.common.util.ToastUtils


val gson: Gson by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { Gson() }

fun Any?.toJsonStr(): String {
    return gson.toJson(this)
}

fun Any?.toast() {
    ToastUtils.show(this.toJsonStr())
}

/**
 * 关闭键盘
 */
fun EditText.hideKeyboard() {
    val imm = appContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(
        this.windowToken,
        InputMethodManager.HIDE_IMPLICIT_ONLY
    )
}

/**
 * 关闭软键盘
 */
fun EditText.hideOffKeyboard() {
    val imm = appContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(
        this.windowToken,
        0
    )
}

/**
 * 打开键盘
 */
fun EditText.openKeyboard() {
    this.apply {
        isFocusable = true
        isFocusableInTouchMode = true
        requestFocus()
    }
    val inputManager =
        appContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

/**
 * 关闭键盘焦点
 */
fun Activity.hideOffKeyboard() {
    val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    if (imm.isActive && this.currentFocus != null) {
        if (this.currentFocus?.windowToken != null) {
            imm.hideSoftInputFromWindow(
                this.currentFocus?.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }
}

fun toStartActivity(@NonNull clz: Class<*>) {
    val intent = Intent(appContext, clz)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    appContext.startActivity(intent)
//    currentActivity?.overridePendingTransition(0, 0)
}

fun toStartActivity(@NonNull clz: Class<*>, @NonNull bundle: Bundle) {
    val intent = Intent(appContext, clz)
    intent.apply {
        putExtras(bundle)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    appContext.startActivity(intent)
//    currentActivity?.overridePendingTransition(0, 0)
}

/**
 * 最好使用这个
 */
fun toStartActivity(activity: Activity?,  clz: Class<*>?, bundle: Bundle) {
    Log.e("CommExt", "activity $activity")
    if (activity == null) {
        return
    }
    val intent = Intent(activity, clz)
    intent.apply { putExtras(bundle) }
    activity.startActivity(intent)
//    currentActivity?.overridePendingTransition(0, 0)
}

/**
 * 外部跳转使用这个
 */
fun deepLinkToStartActivity(activity: Activity?,  clz: Class<*>?, bundle: Bundle) {
    Log.e("CommExt", "activity $activity")
    if (activity == null) {
        return
    }
    val intent = Intent(activity, clz)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.apply { putExtras(bundle) }
    activity.startActivity(intent)
//    currentActivity?.overridePendingTransition(0, 0)
}

fun toStartActivity(
    activity: Activity,
    @NonNull clz: Class<*>,
    code: Int,
    @NonNull bundle: Bundle
) {
    activity.startActivityForResult(Intent(appContext, clz).putExtras(bundle), code)
//    activity.overridePendingTransition(0, 0)
}

fun toStartActivity(
    fragment: Fragment,
    @NonNull clz: Class<*>,
    code: Int,
    @NonNull bundle: Bundle
) {
    fragment.startActivityForResult(Intent(appContext, clz).putExtras(bundle), code)
}

fun toStartActivity(activity: Activity, @NonNull intent: Intent, code: Int) {
    activity.startActivityForResult(intent, code)
}

fun toStartActivity(
    @NonNull type: Any,
    @NonNull clz: Class<*>,
    code: Int,
    @NonNull bundle: Bundle
) {
    if (type is Activity) {
        toStartActivity(type, clz, code, bundle)
//        type.overridePendingTransition(0, 0)
    } else if (type is Fragment) {
        toStartActivity(type, clz, code, bundle)
    }
}

fun finishCurrentActivity(activity: Activity?) {
    activity?.apply {
        finish()
//        overridePendingTransition(0, 0)
    }

}

/**
 * 隐藏状态栏
 */
fun hideStatusBar(activity: Activity) {
    val attrs = activity.window.attributes
    attrs.flags = attrs.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
    activity.window.attributes = attrs
}

/**
 * 显示状态栏
 */
fun showStatusBar(activity: Activity) {
    val attrs = activity.window.attributes
    attrs.flags = attrs.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
    activity.window.attributes = attrs
}

/**
 * 横竖屏
 */
fun isLandscape(context: Context) =
    context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

/**
 * 应用商店
 */
fun gotoStore() {
    val uri =
        Uri.parse("market://details?id=" + appContext.packageName)
    val goToMarket = Intent(Intent.ACTION_VIEW, uri)
    try {
        goToMarket.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        appContext.startActivity(goToMarket)
    } catch (ignored: ActivityNotFoundException) {
    }
}

/**
 * 字符串相等
 */
fun isEqualStr(value: String?, defaultValue: String?) =
    if (value.isNullOrEmpty() || defaultValue.isNullOrEmpty()) false else TextUtils.equals(
        value,
        defaultValue
    )

/**
 * Int类型相等
 *
 */
fun isEqualIntExt(value: Int, defaultValue: Int) = value == defaultValue

fun getDrawableExt(id: Int): Drawable? = ContextCompat.getDrawable(appContext, id)

fun getColorExt(id: Int): Int = ContextCompat.getColor(appContext, id)

fun getStringExt(id: Int) = appContext.resources.getString(id)

fun getStringArrayExt(id: Int): Array<String> = appContext.resources.getStringArray(id)

fun getIntArrayExt(id: Int) = appContext.resources.getIntArray(id)

fun getDimensionExt(id: Int) = appContext.resources.getDimension(id)

fun covert2ScreenType(code: Int) = when (code) {
    0 -> ScreenType.csd
    1 -> ScreenType.tv
    2 -> ScreenType.dim
    3 -> ScreenType.hud
    4 -> ScreenType.console
    5 -> ScreenType.armrest
    6 -> ScreenType.door_panel
    7 -> ScreenType.backrest
    8 -> ScreenType.ceiling
    else -> ScreenType.csd
}
/**
 * 打印 Throwable 详细描述及其堆栈跟踪
 */

fun Throwable.logStackTrace(tag: String) {
    try {
        Log.e(tag, this.stackTraceToString())
    } catch (_: Exception) {
    }
}

fun Throwable.logStackTrace() {
    try {
        Log.e("System.error", this.stackTraceToString())
    } catch (_: Exception) {
    }
}