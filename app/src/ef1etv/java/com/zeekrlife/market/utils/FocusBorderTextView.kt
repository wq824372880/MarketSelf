package com.zeekrlife.market.utils

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.zeekr.component.tv.ZeekrTVFocusedBorderDrawable
import com.zeekr.component.tv.ZeekrTVOnFocusChangeListener

class FocusBorderTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    AppCompatTextView(context, attrs) {
    // 焦点态
    private val mFocusBorderDrawable: ZeekrTVFocusedBorderDrawable by lazy {
        ZeekrTVFocusedBorderDrawable(this@FocusBorderTextView)
    }

    init {
        onFocusChangeListener = object : ZeekrTVOnFocusChangeListener() {
            override fun onZeekrTVFocusChange(v: View, hasFocus: Boolean) {
                if (hasFocus) {
                    playFocusedBorderAnimate()
                    // playVolumeUpAnimator(mLightAnimatorPaint, mLightAnimatorPath, measuredWidth.toFloat(), measuredHeight.toFloat(), mLightValueAnimator)
                } else {
                    stopFocusedBorderAnimate()
                }
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mFocusBorderDrawable.setBounds(0, 0, w, h)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mFocusBorderDrawable.draw(canvas)
    }

    /**
     * 播放聚焦边框动画。
     * 该函数没有参数。
     * 该函数没有返回值。
     */
    fun playFocusedBorderAnimate() {
        // 尝试播放聚焦边框的动画
        mFocusBorderDrawable?.play()
    }

    /**
     * 停止当前焦点边框的动画。
     * 该函数没有参数。
     * 该函数没有返回值。
     */
    fun stopFocusedBorderAnimate() {
        // 尝试停止焦点边框的动画
        mFocusBorderDrawable?.stop()
    }
}