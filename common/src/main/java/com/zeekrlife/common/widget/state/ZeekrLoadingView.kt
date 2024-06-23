package com.zeekrlife.common.widget.state

import android.content.Context
import android.util.AttributeSet
import com.airbnb.lottie.LottieAnimationView

class ZeekrLoadingView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    LottieAnimationView(context, attrs) {

    init {
        setAnimation("loadinginfinite.json")
        playAnimation()
    }
}
