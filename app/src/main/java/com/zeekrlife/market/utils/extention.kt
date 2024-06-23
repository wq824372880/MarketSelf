package com.zeekrlife.market.utils

import android.app.Activity
import android.content.res.Resources
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.transition.MaterialSharedAxis
import com.zeekrlife.market.utils.transition.CustomCubicBezierInterpolator
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.zeekrlife.market.R
import com.zeekrlife.market.utils.transition.ZeekrPageTransition

internal fun View.idToName(id: Int): String {
    return try {
        context.resources.getResourceName(id).substringAfter("/", "")
    } catch (e: Resources.NotFoundException) {
        id.toString()
    }
}

private val slideInterpolator
    get() = CustomCubicBezierInterpolator(0.10f, 0.31f, 0.20f, 1.00f)

//fun Fragment.slideTransition(targetView: ViewGroup, isEnter: Boolean) {
//    targetView.isTransitionGroup = true
//    if (isEnter) {
//        enterTransition = ZeekrPageTransition(true).apply {
//            addTarget(targetView)
//            interpolator = slideInterpolator
//            duration = MAP_SLIDE_DURATION
//        }
//        returnTransition = ZeekrPageTransition(true).apply {
//            addTarget(targetView)
//            interpolator = slideInterpolator
//            duration = MAP_SLIDE_DURATION
//        }
//    } else {
//        exitTransition = ZeekrPageTransition(false).apply {
//            addTarget(targetView)
//            interpolator = slideInterpolator
//            duration = MAP_SLIDE_DURATION
//        }
//        reenterTransition = ZeekrPageTransition(false).apply {
//            addTarget(targetView)
//            interpolator = slideInterpolator
//            duration = MAP_SLIDE_DURATION
//        }
//    }
//}

fun AppCompatActivity.slideTransition(targetView: ViewGroup, isEnter: Boolean) {
    targetView.isTransitionGroup = true
    if (isEnter) {
        window.enterTransition = ZeekrPageTransition(true).apply {
            addTarget(targetView)
            interpolator = slideInterpolator
            duration = MAP_SLIDE_DURATION
        }
        window.returnTransition = ZeekrPageTransition(true).apply {
            addTarget(targetView)
            interpolator = slideInterpolator
            duration = MAP_SLIDE_DURATION
        }
    } else {
        window.exitTransition = ZeekrPageTransition(false).apply {
            addTarget(targetView)
            interpolator = slideInterpolator
            duration = MAP_SLIDE_DURATION
        }
        window.reenterTransition = ZeekrPageTransition(false).apply {
            addTarget(targetView)
            interpolator = slideInterpolator
            duration = MAP_SLIDE_DURATION
        }
    }
}



//fun Fragment.slideReenterTransition(targetView: ViewGroup) {
//    targetView.isTransitionGroup = true
//    reenterTransition = ZeekrPageTransition(false).apply {
//        addTarget(targetView)
//        interpolator = slideInterpolator
//        duration = MAP_SLIDE_DURATION
//    }
//}
//
//fun Fragment.settingsTransition() {
//    enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
//        interpolator = slideInterpolator
//        duration = MAP_SLIDE_DURATION
//    }
//    returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
//        interpolator = slideInterpolator
//        duration = MAP_SLIDE_DURATION
//    }
//}

fun AppCompatActivity.slideReenterTransition(targetView: ViewGroup) {
    targetView.isTransitionGroup = true
    window.reenterTransition = ZeekrPageTransition(false).apply {
        addTarget(targetView)
        interpolator = slideInterpolator
        duration = MAP_SLIDE_DURATION
    }
}

//fun AppCompatActivity.settingsTransition() {
//    window.enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
//        interpolator = slideInterpolator
//        duration = MAP_SLIDE_DURATION
//    }
//    window.returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
//        interpolator = slideInterpolator
//        duration = MAP_SLIDE_DURATION
//    }
//}


private val shareInterpolator
    get() = DecelerateInterpolator(MAP_INTERPOLATOR_FACTOR)

//fun Fragment.shareTransition() {
//    sharedElementEnterTransition = MaterialContainerTransform().apply {
//        duration = MAP_DURATION
//        isDrawDebugEnabled = true
//        fadeMode = MaterialContainerTransform.FADE_MODE_THROUGH
//        fitMode = MaterialContainerTransform.FIT_MODE_WIDTH
//       // interpolator = shareInterpolator
//        fadeProgressThresholds = MaterialContainerTransform.ProgressThresholds(0f, 1f)
//        scrimColor = Color.TRANSPARENT
//    }
//    sharedElementReturnTransition = MaterialContainerTransform().apply {
//        duration = MAP_DURATION
//        fadeMode = MaterialContainerTransform.FADE_MODE_CROSS
//        fitMode = MaterialContainerTransform.FIT_MODE_WIDTH
//        fadeProgressThresholds = MaterialContainerTransform.ProgressThresholds(0f, 1f)
//       // interpolator = shareInterpolator
//        scrimColor = Color.TRANSPARENT
//    }
//}

fun AppCompatActivity.shareTransition() {
    window.sharedElementEnterTransition = MaterialContainerTransform().apply {
        addTarget(R.id.search_bar)
        duration = MAP_DURATION
//        isDrawDebugEnabled = true
        fadeMode = MaterialContainerTransform.FADE_MODE_THROUGH
//        fitMode = MaterialContainerTransform.FIT_MODE_WIDTH
       // interpolator = shareInterpolator
        fadeProgressThresholds = MaterialContainerTransform.ProgressThresholds(0f, 1f)
        scrimColor = Color.TRANSPARENT
    }
    window.sharedElementReturnTransition = MaterialContainerTransform().apply {
        addTarget(R.id.search_bar)
        duration = MAP_DURATION
        fadeMode = MaterialContainerTransform.FADE_MODE_CROSS
//        fitMode = MaterialContainerTransform.FIT_MODE_WIDTH
        fadeProgressThresholds = MaterialContainerTransform.ProgressThresholds(0f, 1f)
       // interpolator = shareInterpolator
        scrimColor = Color.TRANSPARENT
    }

}