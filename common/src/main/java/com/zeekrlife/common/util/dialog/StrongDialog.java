package com.zeekrlife.common.util.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.zeekrlife.common.R;


/**
 * @desc: 公共的中部弹窗封装
 */
public class StrongDialog extends android.app.Dialog {
    private final Builder builder;
    private LinearLayout mContentContainer;
    private TextView titleView;
    private TextView contentView;
    private Button closeBtn;
    private TextView positiveBtn;
    private TextView negativeBtn;

    public StrongDialog(Builder builder) {
        super(builder.getContext());
        this.builder = builder;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_strong_layout);
        titleView = findViewById(R.id.dialog_title);
        contentView = findViewById(R.id.dialog_content);
        closeBtn = findViewById(R.id.dialog_close_btn);
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
            CharSequence title = builder.getTitleText();
            if (TextUtils.isEmpty(title)) {
                titleView.setVisibility(View.GONE);
            } else {
                titleView.setVisibility(View.VISIBLE);
                titleView.setText(title);
            }
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
                StrongDialog.this.dismiss();
                if (positiveBtnClickListener != null) {
                    positiveBtnClickListener.onClick(StrongDialog.this, v);
                }
            });

            if (!TextUtils.isEmpty(builder.getNegativeBtnText())) {
                negativeBtn.setText(builder.getNegativeBtnText());
                negativeBtn.setOnClickListener(v -> {
                    StrongDialog.this.dismiss();
                    if (negativeBtnClickListener != null) {
                        negativeBtnClickListener.onClick(StrongDialog.this, v);
                    }
                });
                negativeBtn.setVisibility(View.VISIBLE);
            }
        }

        if (!builder.isShowCloseBtn()) {
            closeBtn.setVisibility(View.GONE);
        }
        closeBtn.setOnClickListener(v -> StrongDialog.this.dismiss());
        setOnDismissListener(dialog -> {
            if (builder.getOnDismissListener() != null) {
                builder.getOnDismissListener().onDismiss(dialog);
            }
        });
        setCanceledOnTouchOutside(builder.isCanTouchOutside());
        setCancelable(builder.isCancelable());

    }
    public TextView getContentView(){
        return contentView;
    }

    public interface DialogInterface {
        interface ActionClickListener {
            void onClick(StrongDialog dialog, View view);
        }
    }

    public static class Builder {
        private final Context context;

        public Context getContext() {
            return context;
        }

        private CharSequence titleText;
        private CharSequence contentText;
        private boolean showCloseBtn;
        private Drawable positiveBtnBg;
        private Drawable negativeBtnBg;
        private CharSequence positiveBtnText;
        private CharSequence negativeBtnText;
        private DialogInterface.ActionClickListener negativeClickListener;
        private DialogInterface.ActionClickListener positiveClickListener;
        private OnDismissListener dismissListener;
        private boolean canTouchOutside = true;
        private boolean cancelable = true;
        private View contentView;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setTitle(CharSequence title) {
            titleText = title;
            return this;
        }

        public Builder setContent(CharSequence content) {
            contentText = content;
            return this;
        }

        public Builder setCloseBtnVisible(boolean visible) {
            showCloseBtn = visible;
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

        public Builder setCancelAble(boolean cancelable) {
            this.cancelable = cancelable;
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

        public CharSequence getTitleText() {
            return titleText;
        }

        public CharSequence getContentText() {
            return contentText;
        }

        public boolean isShowCloseBtn() {
            return showCloseBtn;
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

        public boolean isCancelable() {
            return cancelable;
        }

        public View getContentView() {
            return contentView;
        }

        public StrongDialog build() {
            return new StrongDialog(this);
        }
    }
}
