package com.zeekrlife.common.util.dialog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * 单行居中对齐，多行左对齐
 */
public class AutoAlignTextView extends AppCompatTextView {

    public AutoAlignTextView(Context context) {
        super(context);
    }

    public AutoAlignTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoAlignTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //默认 单行居中对齐，多行居左对齐
        int line = this.getLineCount();
        if (line <= 1) {
            this.setGravity(Gravity.CENTER);
        } else {
            this.setGravity(Gravity.START);
        }
    }
}
