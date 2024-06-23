package com.zeekrlife.market.widget

import android.view.View
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.zeekrlife.market.R
import com.zhpan.indicator.utils.IndicatorUtils
import kotlin.math.abs
import kotlin.math.max

class CustomPageTransformer : ViewPager2.PageTransformer {
    private val orientation = RecyclerView.HORIZONTAL
    private val minScale: Float = 0.85f
    private val unSelectedItemRotation: Float = 0f
    private val unSelectedItemAlpha: Float = 0.2f
    private val itemGap: Float = 0f

    init {
        require(minScale in 0f..1f) { "minScale value should be between 1.0 to 0.0" }
        require(
            unSelectedItemAlpha in 0f..1f
        ) { "unSelectedItemAlpha value should be between 1.0 to 0.0" }
    }

    private var scalingValue = 0.2f

    override fun transformPage(
        page: View,
        position: Float
    ) {
        page.apply {
            scalingValue = if (minScale >= 0.8) {
                0.2f
            } else if (minScale >= 0.6) {
                0.3f
            } else {
                0.4f
            }
            elevation = -abs(position)
            val delta = max(1f - abs(position * (1 - 0.5f)), 0.5f)

            if (unSelectedItemRotation != 0f) {
                val rotation =
                    (1 - delta) * if (position > 0) unSelectedItemRotation else -unSelectedItemRotation

                rotationY = rotation
            }

            val scaleDelta = abs(position * scalingValue)
            val scale = max(1f - 1.6f*scaleDelta, minScale)

            scaleX = scale
            scaleY = scale

            val dp2px = IndicatorUtils.dp2px((itemGap.toInt() / 2).toFloat())
            when (orientation) {
                ViewPager2.ORIENTATION_HORIZONTAL -> {
                    translationX =
                        position * dp2px +
                                if (position > 0) {
                                    (-width * (1f - scale))
                                } else {
                                    (width * (1f - scale))
                                }
                }
                ViewPager2.ORIENTATION_VERTICAL -> {
                    translationY = position * dp2px +
                            if (position > 0) {
                                (-width * (1f - scale))
                            } else {
                                (width * (1f - scale))
                            }
                }
                else -> throw IllegalArgumentException(
                    "Gives correct orientation value, ViewPager2.ORIENTATION_HORIZONTAL or ViewPager2.ORIENTATION_VERTICAL"
                )
            }

            if (unSelectedItemAlpha != 1f) {
                val masView = page.findViewById<ImageFilterView>(R.id.mask_view)
                masView.alpha = 1 - when {
                    position >= -1f && position <= 1f -> { // (0, 1]
                        // page move from right to center.
                        0.5f + ((1 - abs(position)) * 0.5f)
                    }
                    else -> {
                        0.5f / abs(position * position)
                    }
                }
            }
        }
    }
}