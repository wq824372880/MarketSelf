package com.zeekrlife.market.utils.transition

import android.animation.TimeInterpolator
import android.graphics.PointF


class CustomCubicBezierInterpolator(
    private val startControlX: Float,
    private val startControlY: Float,
    private val endControlX: Float,
    private val endControlY: Float
) : TimeInterpolator {

    private val ACCURACY = 4096
    private var mLastI = 0
    private val mControlPoint1 = PointF(startControlX, startControlY)
    private val mControlPoint2 = PointF(endControlX, endControlY)

    override fun getInterpolation(input: Float): Float {

        var t = input
        for (i in mLastI until ACCURACY) {
            t = 1.0f * i / ACCURACY
            val x = cubicCurves(
                t.toDouble(),
                0.toDouble(),
                mControlPoint1.x.toDouble(),
                mControlPoint2.x.toDouble(),
                1.toDouble()
            )
            if (x >= input) {
                mLastI = i
                break
            }
        }

        var value = cubicCurves(
            t.toDouble(),
            0.toDouble(),
            mControlPoint1.y.toDouble(),
            mControlPoint2.y.toDouble(),
            1.toDouble()
        )

        if (value > 0.999) {
            value = 1.0
            mLastI = 0
        }

        return value.toFloat()
    }

    private fun cubicCurves(
        t: Double, value0: Double, value1: Double,
        value2: Double, value3: Double
    ): Double {
        var value: Double
        val u = 1 - t
        val tt = t * t
        val uu = u * u
        val uuu = uu * u
        val ttt = tt * t
        value = uuu * value0
        value += 3 * uu * t * value1
        value += 3 * u * tt * value2
        value += ttt * value3
        return value
    }
}