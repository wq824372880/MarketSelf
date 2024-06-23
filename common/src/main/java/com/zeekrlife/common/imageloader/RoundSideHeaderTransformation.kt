package com.zeekrlife.common.imageloader

import android.graphics.*
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import java.security.MessageDigest

class RoundSideHeaderTransformation : BitmapTransformation {
    private var mRoundingRadius: Int
    private var mGap = 6
    private var mColor = Color.LTGRAY
    private var mPaint: Paint? = null

    constructor(roundingRadius: Int, gap: Int) {
        mRoundingRadius = roundingRadius
        mGap = gap
    }

    constructor(roundingRadius: Int, gap: Int, color: Int) {
        mRoundingRadius = roundingRadius
        mGap = gap
        mColor = color
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        val bmp = TransformationUtils.roundedCorners(pool, toTransform, mRoundingRadius)
        val half = mGap / 2
        try {
            if (null == mPaint) {
                mPaint = Paint()
                mPaint!!.isAntiAlias = true
                mPaint!!.strokeWidth = mGap.toFloat()
                mPaint!!.isAntiAlias = true
                mPaint!!.style = Paint.Style.STROKE
                mPaint!!.color = mColor
            }
            val oval = RectF(
                (0 + half).toFloat(),
                (0 + half).toFloat(),
                (bmp.width - half).toFloat(),
                (bmp.height - half).toFloat()
            )
            val canvas = Canvas(bmp)
            canvas.drawArc(oval, 0f, 360f, false, mPaint!!)
            canvas.setBitmap(null)
        } finally {
        }
        return bmp
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {}
}