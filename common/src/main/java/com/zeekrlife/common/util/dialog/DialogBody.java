package com.zeekrlife.common.util.dialog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

/**
 * 弹框内容布局
 */
public class DialogBody extends LinearLayout {

    public DialogBody(Context context) {
        super(context);
    }

    public DialogBody(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DialogBody(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);
        return true;
    }
}
