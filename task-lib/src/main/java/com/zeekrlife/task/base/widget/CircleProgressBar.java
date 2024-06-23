package com.zeekrlife.task.base.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import androidx.core.content.ContextCompat;
import com.zeekrlife.task.base.R;

/**
 * 圆形进度条
 *
 * @author
 */
public class CircleProgressBar extends ProgressBar {

    private int mDefaultColor;
    private final int mReachedColor;
    private int mStateBlockColor;
    private final float mDefaultHeight;
    private final float mReachedHeight;
    private final float mRadius;

    private Paint mPaint;

    private Status mStatus = Status.Waiting;

    private float centerX;

    private float centerY;

    private RectF loadRectLeft;

    private RectF loadRectRight;

    private RectF waitRect;

    private RectF waitRect1;

    private RectF waitRect2;

    private Path pausePath;

    public CircleProgressBar(Context context) {
        this(context, null);
    }

    public CircleProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar);
        ////默认圆的颜色
        mDefaultColor =
            typedArray.getColor(R.styleable.CircleProgressBar_defaultColor, ContextCompat.getColor(context, R.color.transparent));
        //进度条的颜色
        mReachedColor = typedArray.getColor(R.styleable.CircleProgressBar_reachedColor, Color.parseColor("#F88650"));
        //状态块颜色
        mStateBlockColor = typedArray.getColor(R.styleable.CircleProgressBar_stateBlockColor,
            ContextCompat.getColor(getContext(), R.color.color_CC_383A3D));
        //默认圆的高度
        mDefaultHeight = typedArray.getDimension(R.styleable.CircleProgressBar_defaultHeight, dp2px(context, 2.5f));
        //进度条的高度
        mReachedHeight = typedArray.getDimension(R.styleable.CircleProgressBar_reachedHeight, dp2px(context, 4f));
        //圆的半径
        mRadius = typedArray.getDimension(R.styleable.CircleProgressBar_radius, dp2px(context, 30));
        typedArray.recycle();

        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    /**
     * 设置深色主题
     */
    public void setDarkStyle() {
        mDefaultColor = ContextCompat.getColor(getContext(), R.color.white);
        mStateBlockColor = ContextCompat.getColor(getContext(), R.color.color_CC_383A3D);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //比较两数，取最大值
        float paintHeight = Math.max(mReachedHeight, mDefaultHeight);
        if (heightMode != MeasureSpec.EXACTLY) {
            //如果用户没有精确指出宽高时，我们就要测量整个View所需要分配的高度了，测量自定义圆形View设置的上下内边距+圆形view的直径+圆形描边边框的高度
            int exceptHeight = (int) (getPaddingTop() + getPaddingBottom() + mRadius * 2 + paintHeight);
            //然后再将测量后的值作为精确值传给父类，告诉他我需要这么大的空间，你给我分配吧
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(exceptHeight, MeasureSpec.EXACTLY);
        }
        if (widthMode != MeasureSpec.EXACTLY) {
            //这里在自定义属性中没有设置圆形边框的宽度，所以这里直接用高度代替
            int exceptWidth = (int) (getPaddingLeft() + getPaddingRight() + mRadius * 2 + paintHeight);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(exceptWidth, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = getPaddingStart() + (getWidth() / 2.0f);
        centerY = getPaddingTop() + (getHeight() / 2.0f);
        loadRectLeft = new RectF(mRadius / 2 + 7.5f, mRadius / 2 + 7, mRadius * 3 / 2 - 17.5f, mRadius * 3 / 2 - 7);
        loadRectRight =
            new RectF(loadRectLeft.left + loadRectLeft.width() + 4, loadRectLeft.top, loadRectLeft.right + +loadRectLeft.width() + 4,
                loadRectLeft.bottom);

        waitRect = new RectF(mRadius / 2 + 13.5f, mRadius / 2 + 12.5f, mRadius * 3 / 2 - 11.5f, mRadius * 3 / 2 - 12.5f);
        waitRect1 = new RectF(waitRect.left - waitRect.width() - 4f, waitRect.top, waitRect.right - waitRect.width() - 4f, waitRect.bottom);
        waitRect2 = new RectF(waitRect.left + waitRect.width() + 4f, waitRect.top, waitRect.right + waitRect.width() + 4f, waitRect.bottom);

        pausePath = new Path();
        float triangleWith = mRadius / 2 + 4;
        float leftX = (float) ((2 * mRadius - Math.sqrt(3.0) / 2 * triangleWith) / 2);
        float realX = (float) (leftX + leftX * 0.2);
        pausePath.moveTo(realX, mRadius - (triangleWith / 2));
        pausePath.lineTo(realX, mRadius + (triangleWith / 2));
        pausePath.lineTo((float) (realX + Math.sqrt(3.0) / 2 * triangleWith), mRadius);
        pausePath.lineTo(realX, mRadius - (triangleWith / 2));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(centerX - mRadius, centerY - mRadius);

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mDefaultColor);
        mPaint.setStrokeWidth(mDefaultHeight);
        canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);

        //画进度条的一些设置
        mPaint.setStyle(Paint.Style.STROKE);
        if(mStatus == Status.Waiting) {
            mPaint.setColor(Color.parseColor("#979899"));
        } else {
            mPaint.setColor(mReachedColor);
        }
        mPaint.setStrokeWidth(mReachedHeight);
        //根据进度绘制圆弧
        float sweepAngle = getProgress() * 1.0f / getMax() * 360;
        @SuppressLint("DrawAllocation")
        RectF rectF = new RectF(0 + 1.5f, 0 + 1.5f, mRadius * 2 - 1.5f, mRadius * 2 - 1.5f);
        canvas.drawArc(rectF, -90, sweepAngle, false, mPaint);

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mStateBlockColor);

        if (mStatus == Status.Waiting) {
            canvas.drawRoundRect(waitRect, 2, 2, mPaint);
            canvas.drawRoundRect(waitRect1, 2, 2, mPaint);
            canvas.drawRoundRect(waitRect2, 2, 2, mPaint);
        } else if (mStatus == Status.Loading) {
            canvas.drawRoundRect(loadRectLeft, 1, 1, mPaint);
            canvas.drawRoundRect(loadRectRight, 1, 1, mPaint);
        } else if (mStatus == Status.Pause) {
            canvas.drawPath(pausePath, mPaint);
        } else if (mStatus == Status.Update) {
        } else {
        }
        canvas.restore();
    }

    public Status getStatus() {
        return mStatus;
    }

    public void setStatus(Status status) {
        if (mStatus == status) {
            return;
        }
        mStatus = status;
        postInvalidate();
    }

    public enum Status {
        /**
         * 等待
         */
        Waiting,
        /**
         * 暂停
         */
        Pause,
        /**
         * 加载
         */
        Loading,
        /**
         * 更新不可点击
         */
        Update
    }

    float dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }
}