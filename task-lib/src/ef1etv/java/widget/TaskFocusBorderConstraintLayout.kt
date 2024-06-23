package widget

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.zeekr.component.tv.ZeekrTVFocusedBorderDrawable
import com.zeekr.component.tv.ZeekrTVOnFocusChangeListener
import com.zeekr.component.tv.doFocusChangeScaleAnimate
import com.zeekrlife.task.base.R


open class TaskFocusBorderConstraintLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    ConstraintLayout(context, attrs) {

    // 放大系数
    var scaleValue = 1.0F

    // 焦点态
    private val mFocusBorderDrawable: ZeekrTVFocusedBorderDrawable by lazy {
        ZeekrTVFocusedBorderDrawable(this@TaskFocusBorderConstraintLayout)
    }

    init {
        // 读取属性
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TaskFocusBorderConstraintLayout)
        // 读取放大倍数
        scaleValue = typedArray.getFloat(R.styleable.TaskFocusBorderConstraintLayout_scaleValue, scaleValue)

        onFocusChangeListener = object : ZeekrTVOnFocusChangeListener() {
            override fun onZeekrTVFocusChange(v: View, hasFocus: Boolean) {
                if (hasFocus) {
                    playFocusedBorderAnimate()
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

    fun playFocusedBorderAnimate() {
        mFocusBorderDrawable?.play()
    }

    fun stopFocusedBorderAnimate() {
        mFocusBorderDrawable?.stop()
    }

    private var mOnFocusChangeListener: ((View, Boolean) -> Unit)? = null

    fun setOnFocusChangeListener(onFocusChangeListener: (View, Boolean) -> Unit) {
        mOnFocusChangeListener = onFocusChangeListener
    }
}