package com.zeekrlife.common.ext

import android.annotation.SuppressLint
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import com.zeekrlife.net.interception.logging.util.LogUtils
import kotlin.math.abs

const val TAG = "ClickExt"

/**
 * 设置防止重复点击事件
 * @param views 需要设置点击事件的view
 * @param interval 时间间隔 默认0.5秒
 * @param onClick 点击触发的方法
 */
fun setOnclickNoRepeat(vararg views: View?, interval: Long = 500, onClick: (View) -> Unit) {
    views.forEach {
        it?.clickNoRepeat(interval = interval) { view ->
            onClick.invoke(view)
        }
    }
}

/**
 * 防止重复点击事件 默认0.5秒内不可重复点击
 * @param interval 时间间隔 默认0.5秒
 * @param action 执行方法
 */
var lastClickTime = 0L
fun View.clickNoRepeat(interval: Long = 500, action: (view: View) -> Unit) {
    setOnClickListener {
        val currentTime = System.currentTimeMillis()
        if (lastClickTime != 0L && (currentTime - lastClickTime < interval)) {
            return@setOnClickListener
        }
        lastClickTime = currentTime
        action.invoke(it)
    }
}

/**
 * 设置点击事件
 * @param views 需要设置点击事件的view
 * @param onClick 点击触发的方法
 */
fun setOnclick(vararg views: View?, onClick: (View) -> Unit) {
    views.forEach {
        it?.setOnClickListener { view ->
            onClick.invoke(view)
        }
    }
}

private var sLastClickTime: Long = 0
private const val NO_QUICK_CLICK_INTERVAL: Long = 200
private var isQuickClick = false
/**
 * 设置按钮在TV屏触发点击事件
 * 1.使用setOnTouchListener代替setOnClickListener （原因是，使用focusableInTouchMode=true后会导致click需要点击两次才能触发->第一次会触发焦点）
 * 2.需要响应ok事件，用以代替原来按下ok键自动触发的Click事件
 */
@SuppressLint("ClickableViewAccessibility")
@JvmOverloads
fun View.setTVClickListener(needChangeAlpha:Boolean = true, clickListener: () -> Unit){

    setTVClickTouchListener(needChangeAlpha, clickListener)
    setOnKeyListener { v, keyCode, event ->
        if (KeyEventUtil.isOkKeyEvent(keyCode)) {
            if (event.action == KeyEvent.ACTION_UP) {
                v.isPressed = false
                isQuickClick("setTVClickListener ACTION_DOWN isOkKeyEvent"){
                    sLastClickTime = System.currentTimeMillis()
                    clickListener.invoke()
                }
                return@setOnKeyListener true
            }else { //点击事件
                setQuickClick()
                v.isPressed = true
            }
        }
        return@setOnKeyListener false
    }
}
@SuppressLint("ClickableViewAccessibility")
fun View.setTVClickTouchListener(needChangeAlpha:Boolean = true, clickListener: () -> Unit){
    setOnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // 表明你的视图已经处理了按下动作
                setQuickClick()
                if(needChangeAlpha){
                    isQuickClick("setTVClickListener ACTION_DOWN"){v.alpha = 0.5f}
                }
                true // 返回true以确保接收到ACTION_UP事件
            }
            MotionEvent.ACTION_UP -> {
                // 用户释放触摸，处理点击逻辑
                // 在这里执行你的点击事件处理逻辑
                isQuickClick("setTVClickListener ACTION_UP"){
                    if(needChangeAlpha){
                        v.alpha = 1.0f
                    }
                    requestFocus(this)
                    sLastClickTime = System.currentTimeMillis()
                    clickListener.invoke()
                }
                true // 返回true表示这个事件被消耗掉了
            }
            else -> false // 其他情况返回false
        }
    }
}
private fun setQuickClick(){
    isQuickClick =  abs(System.currentTimeMillis() - sLastClickTime) < NO_QUICK_CLICK_INTERVAL
}

private fun isQuickClick(tag: String, action: () -> Unit){
    if (!isQuickClick) {
        action.invoke()
    } else {
        LogUtils.e(TAG, "$tag -> quick click")
    }
}
private fun requestFocus(view: View){
    if(!view.hasFocus()){
        view.requestFocus()
    }
}

object KeyEventUtil {
    fun isOkKeyEvent(keyCode: Int): Boolean {
        return keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER
    }
}