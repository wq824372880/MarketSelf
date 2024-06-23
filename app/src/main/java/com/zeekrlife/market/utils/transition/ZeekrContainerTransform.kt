package com.zeekrlife.market.utils.transition

import android.view.View
import androidx.annotation.IdRes
import androidx.transition.Transition
import androidx.transition.TransitionValues




class ZeekrContainerTransform: Transition() {

    @IdRes
    private val startViewId = -1
    @IdRes
    private val endViewId = -1
    private val startView: View? = null
    private val endView: View? = null

    override fun captureStartValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    private fun captureValues(transitionValues: TransitionValues
    ) {

    }

}