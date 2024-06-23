package com.zeekrlife.common.widget.state

import android.content.Context
import android.view.View
import com.kingja.loadsir.callback.Callback
import com.zeekrlife.common.R

class BaseLoadingCallback: Callback() {

//    lateinit var loadingView: ZeekrLoadingView

    override fun onCreateView(): Int {
//        loadingView.setProgress()
        return R.layout.layout_loading
    }

    /**
     * 是否是 点击不可重试
     */
    override fun onReloadEvent(context: Context?, view: View?): Boolean {
        return true
    }
}