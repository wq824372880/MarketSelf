package com.zeekrlife.common.widget.state

import android.content.Context
import android.view.View
import com.kingja.loadsir.callback.Callback
import com.zeekrlife.common.R

class BaseEmptyCallback : Callback() {

    override fun onCreateView(): Int {
        return R.layout.layout_empty
    }

    override fun onReloadEvent(context: Context?, view: View?): Boolean {
        return true
    }

}