package com.zeekrlife.common.widget.state

import com.kingja.loadsir.callback.Callback
import com.zeekrlife.common.R

class BaseErrorCallback : Callback() {

    override fun onCreateView(): Int {
        return R.layout.layout_error
    }

}