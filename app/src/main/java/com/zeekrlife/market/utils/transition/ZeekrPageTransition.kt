package com.zeekrlife.market.utils.transition

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.transition.TransitionValues
import android.transition.Visibility
import android.view.View
import android.view.ViewGroup


private const val RECEDED_SLIDE = 0.5f
private const val RECEDED_ALPHA = 0f

class ZeekrPageTransition(private val isEnter: Boolean = true) : Visibility() {

    override fun onAppear(
        sceneRoot: ViewGroup,
        view: View,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator {
        val slideDistance = if (isEnter) sceneRoot.width * RECEDED_SLIDE else -sceneRoot.width * RECEDED_SLIDE
       // Log.d(ZEEKR_TAG, "ZeekrPageTransition -- view:${view.idToName(view.id)} onAppear  slideDistance: $slideDistance")
        view.apply {
            alpha = RECEDED_ALPHA
            translationX = slideDistance
        }
        return ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat(View.TRANSLATION_X, slideDistance, 0f),
            PropertyValuesHolder.ofFloat(View.ALPHA, RECEDED_ALPHA, 1f)
        )
    }


    override fun onDisappear(
        sceneRoot: ViewGroup,
        view: View,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): ObjectAnimator {
        val slideDistance = if (isEnter) sceneRoot.width * RECEDED_SLIDE else -sceneRoot.width * RECEDED_SLIDE
        //Log.d(ZEEKR_TAG, "ZeekrPageTransition -- view:${view.idToName(view.id)} onDisappear  slideDistance: $slideDistance")
        return ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0f, slideDistance),
            PropertyValuesHolder.ofFloat(View.ALPHA, 1f, RECEDED_ALPHA)
        )
    }
}