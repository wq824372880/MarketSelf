package com.zeekrlife.market.utils

import android.content.Context
import android.hardware.display.DisplayManager
import android.view.Display


object DisplayUtils {
    fun isSecondScreen(context: Context): Boolean {
        // 获取DisplayManager对象
        val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        // 获取所有连接的显示器列表
        val displays = displayManager.displays
        if(!displays.isNullOrEmpty()){
            for (display in displays) {
                // 这是副屏
                if (display.displayId != Display.DEFAULT_DISPLAY) {
                    return true
                }
            }
        }
        return false

    }

}