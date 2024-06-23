package com.zeekrlife.market.utils

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.zeekr.component.tv.ZeekrTVFocusedBorderDrawable
import com.zeekr.component.tv.ZeekrTVOnFocusChangeListener
import com.zeekr.component.tv.doFocusChangeScaleAnimate
import com.zeekrlife.market.R

open class FocusBorderFrameLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs) {

    // 放大系数
    var scaleValue = 1.0F

    /**
     * 设置缩放值
     * @param scaleValue 缩放的比例值，类型为Float
     * 该函数没有返回值
     */
    fun setScaleValue1(scaleValue: Float) {
        this.scaleValue = scaleValue
    }

    // 焦点态
    private val mFocusBorderDrawable: ZeekrTVFocusedBorderDrawable by lazy {
        ZeekrTVFocusedBorderDrawable(this@FocusBorderFrameLayout)
    }

    init {
        // 读取属性
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FocusBorderConstraintLayout)
        // 读取放大倍数
        scaleValue = typedArray.getFloat(R.styleable.FocusBorderConstraintLayout_scaleValue, scaleValue)

        onFocusChangeListener = object : ZeekrTVOnFocusChangeListener() {
            override fun onZeekrTVFocusChange(v: View, hasFocus: Boolean) {
                if (hasFocus) {
                    playFocusedBorderAnimate()
                    // playVolumeUpAnimator(mLightAnimatorPaint, mLightAnimatorPath, measuredWidth.toFloat(), measuredHeight.toFloat(), mLightValueAnimator)
                } else {
                    stopFocusedBorderAnimate()
                }
                // 执行缩放动画
                doFocusChangeScaleAnimate(hasFocus, scaleValue)
                mOnFocusChangeListener?.invoke(v, hasFocus)
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

    private var mOnFocusChangeListener: ((View, Boolean) -> Unit)? = null

    fun setOnFocusChangeListener(onFocusChangeListener: (View, Boolean) -> Unit) {
        mOnFocusChangeListener = onFocusChangeListener
    }
}