package com.zeekrlife.common.util.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.zeekrlife.common.R;


/**
 * @author: Andy
 * @date: 2021/4/8 11:23 AM
 * @desc: 公共的弹窗封装
 */
public class WeakDialog extends android.app.Dialog {
    private final Builder builder;
    private LinearLayout mContentContainer;
    private TextView contentView;
    private TextView positiveBtn;
    private TextView negativeBtn;

    public WeakDialog(Builder builder) {
        super(builder.getContext());
        this.builder = builder;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_weak_layout);
        contentView = findViewById(R.id.dialog_content);
        positiveBtn = findViewById(R.id.dialog_positive_btn);
        negativeBtn = findViewById(R.id.dialog_negative_btn);
        mContentContainer = findViewById(R.id.dialog_content_container);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        initView(builder);
    }

    private void initView(Builder builder) {
        contentView.setHighlightColor(ContextCompat.getColor(getContext(), R.color.transparent));
        contentView.setMovementMethod(LinkMovementMethod.getInstance());
        if (builder.getContentView() != null) {
            mContentContainer.removeAllViewsInLayout();
            mContentContainer.addView(builder.getContentView());
        } else {
            CharSequence content = builder.getContentText();
            if (TextUtils.isEmpty(content)) {
                contentView.setVisibility(View.GONE);
            } else {
                contentView.setVisibility(View.VISIBLE);
                contentView.setText(content);
            }
            final DialogInterface.ActionClickListener positiveBtnClickListener = builder.getPositiveClickListener();
            final DialogInterface.ActionClickListener negativeBtnClickListener = builder.getNegativeClickListener();
            if (builder.getPositiveBtnBg() != null) {
                positiveBtn.setBackground(builder.getPositiveBtnBg());
            }
            if (builder.getNegativeBtnBg() != null) {
                negativeBtn.setBackground(builder.getNegativeBtnBg());
            }
            positiveBtn.setText(builder.getPositiveBtnText());
            positiveBtn.setOnClickListener(v -> {
                WeakDialog.this.dismiss();
                if (positiveBtnClickListener != null) {
                    positiveBtnClickListener.onClick(WeakDialog.this, v);
                }
            });
            if (!TextUtils.isEmpty(builder.getNegativeBtnText())) {
                negativeBtn.setText(builder.getNegativeBtnText());
                negativeBtn.setOnClickListener(v -> {
                    WeakDialog.this.dismiss();
                    if (negativeBtnClickListener != null) {
                        negativeBtnClickListener.onClick(WeakDialog.this, v);
                    }
                });
                negativeBtn.setVisibility(View.VISIBLE);
            }
        }

        setOnDismissListener(dialog -> {
            if (builder.getOnDismissListener() != null) {
                builder.getOnDismissListener().onDismiss(dialog);
            }
        });
        setCanceledOnTouchOutside(builder.isCanTouchOutside());
    }

    public interface DialogInterface {
        interface ActionClickListener {
            void onClick(WeakDialog dialog, View view);
        }
    }

    public static class Builder {
        private final Context context;

        public Context getContext() {
            return context;
        }

        private CharSequence contentText;
        private Drawable positiveBtnBg;
        private Drawable negativeBtnBg;
        private CharSequence positiveBtnText;
        private CharSequence negativeBtnText;
        private DialogInterface.ActionClickListener negativeClickListener;
        private DialogInterface.ActionClickListener positiveClickListener;
        private OnDismissListener dismissListener;
        private boolean canTouchOutside = true;
        private View contentView;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setContent(CharSequence content) {
            contentText = content;
            return this;
        }

        public Builder setPositiveBtnBg(Drawable positiveBtnBg) {
            this.positiveBtnBg = positiveBtnBg;
            return this;
        }

        public Builder setPositiveBtn(CharSequence btnText, DialogInterface.ActionClickListener listener) {
            positiveBtnText = btnText;
            positiveClickListener = listener;
            return this;
        }

        public Builder setNegativeBtnBg(Drawable negativeBtnBg) {
            this.negativeBtnBg = negativeBtnBg;
            return this;
        }

        public Builder setNegativeBtn(CharSequence btnText, DialogInterface.ActionClickListener listener) {
            negativeBtnText = btnText;
            negativeClickListener = listener;
            return this;
        }

        public Builder setCanceledOnTouchOutside(boolean canTouchOutside) {
            this.canTouchOutside = canTouchOutside;
            return this;
        }

        public Builder setOnDismissListener(OnDismissListener listener) {
            this.dismissListener = listener;
            return this;
        }

        public Builder setContentView(View view) {
            contentView = view;
            return this;
        }

        public CharSequence getContentText() {
            return contentText;
        }

        public Drawable getPositiveBtnBg() {
            return positiveBtnBg;
        }

        public CharSequence getPositiveBtnText() {
            return positiveBtnText;
        }

        public Drawable getNegativeBtnBg() {
            return negativeBtnBg;
        }

        public CharSequence getNegativeBtnText() {
            return negativeBtnText;
        }

        public DialogInterface.ActionClickListener getNegativeClickListener() {
            return negativeClickListener;
        }

        public DialogInterface.ActionClickListener getPositiveClickListener() {
            return positiveClickListener;
        }

        public OnDismissListener getOnDismissListener() {
            return dismissListener;
        }

        public boolean isCanTouchOutside() {
            return canTouchOutside;
        }

        public View getContentView() {
            return contentView;
        }

        public WeakDialog build() {
            return new WeakDialog(this);
        }
    }
}
