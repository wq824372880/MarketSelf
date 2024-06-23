package com.zeekrlife.market.sensors

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import com.zeekrlife.common.ext.logStackTrace
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

object TrackUtils {

    /**
     * 因为埋入点击事件较少，暂时通过反射hook
     */
    @SuppressLint("DiscouragedPrivateApi", "PrivateApi", "LogNotTimber")
    fun hookViewOnClick(view: View, proxy: (View) -> Unit) {
        try {
            val method: Method = View::class.java.getDeclaredMethod("getListenerInfo")
            method.isAccessible = true
            val mListenerInfo = method.invoke(view)
            val clz = Class.forName("android.view.View\$ListenerInfo")
            val field = clz.getDeclaredField("mOnClickListener")
            field.isAccessible = true
            val originalOnClickListener = field.get(mListenerInfo)
            if (originalOnClickListener is View.OnClickListener) {
                //重新设置点击事件
                view.setOnClickListener {
                    try {
                        proxy(it)
                    } catch (e: InvocationTargetException) {
                        // 处理代理方法调用异常
                        Log.e("ClickListenerProxy", "Proxy method invocation exception: ${e.targetException}")
                    } catch (e: Exception) {
                        // 处理其他异常
                        Log.e("ClickListenerProxy", "Exception: ${e.message}")
                    }
                    originalOnClickListener.onClick(it)
                }
            }
        } catch (e: NoSuchMethodException) {
            // 处理方法不存在异常
            Log.e("ClickListenerProxy", "NoSuchMethodException: ${e.message}")
        } catch (e: IllegalAccessException) {
            // 处理非法访问异常
            Log.e("ClickListenerProxy", "IllegalAccessException: ${e.message}")
        } catch (e: ClassNotFoundException) {
            // 处理类未找到异常
            Log.e("ClickListenerProxy", "ClassNotFoundException: ${e.message}")
        } catch (e: NoSuchFieldException) {
            // 处理字段不存在异常
            Log.e("ClickListenerProxy", "NoSuchFieldException: ${e.message}")
        } catch (e: Exception) {
            // 处理其他未知异常
            Log.e("ClickListenerProxy", "Exception: ${e.message}")
        }
    }
}