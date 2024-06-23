package com.zeekrlife.market.widget.verticaltablayout.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Px;

import com.bumptech.glide.Glide;
import com.zeekrlife.common.ext.DensityExtKt;
import com.zeekrlife.market.R;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.DisplayUtil;

/**
 * @author chqiu
 * Email:qstumn@163.com
 */
public class QTVTabView extends AbstractTVTabView {

    private Context mContext;
    private TextView mTitle;
    private Badge mBadgeView;
    private TabIcon mTabIcon;
    private TabTitle mTabTitle;
    private ImageView mImageView;
    private TabBadge mTabBadge;
    private boolean mChecked;
    private Drawable mDefaultBackground;

    public static final String ICON_RECOMMEND_UNABLE = "icon_recommend_unable";
    public static final String ICON_LOGO_UNABLE = "icon_logo_unable";
    public static final String ICON_SETTING_UNABLE = "icon_setting_unable";

    public QTVTabView(Context context) {
        super(context);
        mContext = context;
        mTabIcon = new TabIcon.Builder().build();
        mTabTitle = new TabTitle.Builder().build();
        mTabBadge = new TabBadge.Builder().build();
        initView();
//        int[] attrs;
//        attrs = new int[] { android.R.attr.selectableItemBackgroundBorderless };
//        TypedArray a = mContext.getTheme().obtainStyledAttributes(attrs);
        mDefaultBackground = null;//a.getDrawable(0);
//        a.recycle();
//        setDefaultBackground();
        setDefaultFocusHighlightEnabled(false);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    private void initView() {
        setMinimumHeight(DisplayUtil.dp2px(mContext, 25));
        if (mTitle == null) {
            mTitle = new TextView(mContext);
            LayoutParams params =
                    new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
            params.leftMargin = (int) mContext.getResources().getDimension(R.dimen.tv_88);
            params.rightMargin = (int) mContext.getResources().getDimension(R.dimen.tv_35);
            mTitle.setLayoutParams(params);
            this.addView(mTitle);
        }
        if (mImageView == null) {
            mImageView = new ImageView(mContext);
            LayoutParams params =
                    new LayoutParams((int) mContext.getResources().getDimension(R.dimen.tv_32),
                            (int) mContext.getResources().getDimension(R.dimen.tv_32));
            params.leftMargin = (int) mContext.getResources().getDimension(R.dimen.tv_27);
            params.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
            mImageView.setLayoutParams(params);
            mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            this.addView(mImageView);
        }
        initTitleView();
        //initIconView();
        initBadge();
    }

    /**
     * 设置视图的相对内边距。此方法重写了{@link (int, int, int, int)}方法，
     * 用于调整标题视图的内边距。
     *
     * @param start 左内边距（对于RTL布局，这是右内边距）
     * @param top 上内边距
     * @param end 右内边距（对于RTL布局，这是左内边距）
     * @param bottom 下内边距
     */
    @Override
    public void setPaddingRelative(@Px int start, @Px int top, @Px int end, @Px int bottom) {
        // 将指定的内边距设置给标题视图
        mTitle.setPaddingRelative(start, top, end, bottom);
    }

    @Override
    public void setPadding(@Px int left, @Px int top, @Px int right, @Px int bottom) {
        mTitle.setPadding(left, top, right, bottom);
    }

    private void initBadge() {
        mBadgeView = TVTabBadgeView.bindTab(this);
        if (mTabBadge.getBackgroundColor() != 0xFFE84E40) {
            mBadgeView.setBadgeBackgroundColor(mTabBadge.getBackgroundColor());
        }
        if (mTabBadge.getBadgeTextColor() != 0xFFFFFFFF) {
            mBadgeView.setBadgeTextColor(mTabBadge.getBadgeTextColor());
        }
        if (mTabBadge.getStrokeColor() != Color.TRANSPARENT || mTabBadge.getStrokeWidth() != 0) {
            mBadgeView.stroke(mTabBadge.getStrokeColor(), mTabBadge.getStrokeWidth(), true);
        }
        if (mTabBadge.getDrawableBackground() != null || mTabBadge.isDrawableBackgroundClip()) {
            mBadgeView.setBadgeBackground(mTabBadge.getDrawableBackground(), mTabBadge.isDrawableBackgroundClip());
        }
        if (mTabBadge.getBadgeTextSize() != 11) {
            mBadgeView.setBadgeTextSize(mTabBadge.getBadgeTextSize(), true);
        }
        if (mTabBadge.getBadgePadding() != 5) {
            mBadgeView.setBadgePadding(mTabBadge.getBadgePadding(), true);
        }
        if (mTabBadge.getBadgeNumber() != 0) {
            mBadgeView.setBadgeNumber(mTabBadge.getBadgeNumber());
        }
        if (mTabBadge.getBadgeText() != null) {
            mBadgeView.setBadgeText(mTabBadge.getBadgeText());
        }
        if (mTabBadge.getBadgeGravity() != (Gravity.END | Gravity.TOP)) {
            mBadgeView.setBadgeGravity(mTabBadge.getBadgeGravity());
        }
        if (mTabBadge.getGravityOffsetX() != 5 || mTabBadge.getGravityOffsetY() != 5) {
            mBadgeView.setGravityOffset(mTabBadge.getGravityOffsetX(), mTabBadge.getGravityOffsetY(), true);
        }
        if (mTabBadge.isExactMode()) {
            mBadgeView.setExactMode(mTabBadge.isExactMode());
        }
        if (!mTabBadge.isShowShadow()) {
            mBadgeView.setShowShadow(mTabBadge.isShowShadow());
        }
        if (mTabBadge.getOnDragStateChangedListener() != null) {
            mBadgeView.setOnDragStateChangedListener(mTabBadge.getOnDragStateChangedListener());
        }
    }

    @SuppressLint("WrongConstant")
    private void initTitleView() {
        mTitle.setTextColor(isChecked() ? mTabTitle.getColorSelected() : mTabTitle.getColorNormal());
        mTitle.setTextSize(mTabTitle.getTitleTextSize());
        mTitle.setText(mTabTitle.getContent());
        mTitle.setTypeface(isChecked() ? Typeface.defaultFromStyle(mTabTitle.getTitleStyle()) : Typeface.defaultFromStyle(0));
        mTitle.setGravity(Gravity.CENTER);
        mTitle.setMaxLines(1);
        mTitle.setEllipsize(TextUtils.TruncateAt.END);

        boolean uiMode = DensityExtKt.getUINightMode();
        switch (mTabTitle.getImageUrl()) {

            case ICON_RECOMMEND_UNABLE:
                if (uiMode) {
                    Glide.with(mContext).load(R.drawable.ic_recommend_able_night).into(mImageView);
                } else {
                    Glide.with(mContext).load(R.drawable.ic_recommend_able).into(mImageView);
                }

                break;
            case ICON_LOGO_UNABLE:
                if (uiMode) {
                    Glide.with(mContext).load(R.drawable.ic_logo_able_night).into(mImageView);
                } else {
                    Glide.with(mContext).load(R.drawable.ic_logo_able).into(mImageView);
                }
                break;
            case ICON_SETTING_UNABLE:
                if (uiMode) {
                    Glide.with(mContext).load(R.drawable.ic_setting_able_night).into(mImageView);
                } else {
                    Glide.with(mContext).load(R.drawable.ic_setting_able).into(mImageView);
                }

                break;
            default:
                Glide.with(mContext)
                        .load(mTabTitle.getImageUrl())
                        .placeholder(R.drawable.img_bg_default)
                        .error(R.drawable.img_bg_error)
                        .into(mImageView);

                break;
        }
    }

    private void initIconView() {
        //        int iconResid =  mTabIcon.getNormalIcon();
        Drawable drawable = mTabIcon.getNormalDrawable();
        if (drawable != null) {
            //            drawable = mContext.getResources().getDrawable(iconResid);
            int r = mTabIcon.getIconWidth() != -1 ? mTabIcon.getIconWidth() : drawable.getIntrinsicWidth();
            int b = mTabIcon.getIconHeight() != -1 ? mTabIcon.getIconHeight() : drawable.getIntrinsicHeight();
            drawable.setBounds(0, 0, r, b);
        }
        switch (mTabIcon.getIconGravity()) {
            case Gravity.START:
                mTitle.setCompoundDrawables(drawable, null, null, null);
                break;
            case Gravity.TOP:
                mTitle.setCompoundDrawables(null, drawable, null, null);
                break;
            case Gravity.END:
                mTitle.setCompoundDrawables(null, null, drawable, null);
                break;
            case Gravity.BOTTOM:
                mTitle.setCompoundDrawables(null, null, null, drawable);
                break;
            default:
                break;
        }
        refreshDrawablePadding();
    }

    private void refreshDrawablePadding() {
        //        int iconResid =  mTabIcon.getNormalIcon();
        Drawable drawable = mTabIcon.getNormalDrawable();
        if (drawable != null) {
            if (!TextUtils.isEmpty(mTabTitle.getContent()) && mTitle.getCompoundDrawablePadding() != mTabIcon.getMargin()) {
                mTitle.setCompoundDrawablePadding(mTabIcon.getMargin());
            } else if (TextUtils.isEmpty(mTabTitle.getContent())) {
                mTitle.setCompoundDrawablePadding(0);
            } else {
            }
        } else {
            mTitle.setCompoundDrawablePadding(0);
        }
    }

    @Override
    public QTVTabView setBadge(TabBadge badge) {
        if (badge != null) {
            mTabBadge = badge;
        }
        initBadge();
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        return this;
    }

    @Override
    public QTVTabView setIcon(TabIcon icon) {
        if (icon != null) {
            mTabIcon = icon;
        }
        initIconView();
        return this;
    }

    @Override
    public QTVTabView setTitle(TabTitle title) {
        if (title != null) {
            mTabTitle = title;
        }
        initTitleView();
        return this;
    }

    public ImageView setImageView() {
        return mImageView;
    }

    /**
     * @param resId The Drawable res to use as the background, if less than 0 will to remove the
     *              background
     */
    @Override
    public QTVTabView setBackground(int resId) {
        if (resId == 0) {
            setDefaultBackground();
        } else if (resId <= 0) {
            setBackground(null);
        } else {
            super.setBackgroundResource(resId);
        }
        return this;
    }

    @Override
    public TabBadge getBadge() {
        return mTabBadge;
    }

    @Override
    public TabIcon getIcon() {
        return mTabIcon;
    }

    @Override
    public TabTitle getTitle() {
        return mTabTitle;
    }

    @Override
    @Deprecated
    public ImageView getIconView() {
        return mImageView;
    }

    @Override
    public TextView getTitleView() {
        return mTitle;
    }

    @Override
    public Badge getBadgeView() {
        return mBadgeView;
    }

    @Override
    public void setBackground(Drawable background) {
        super.setBackground(background);
    }

    @Override
    public void setBackgroundResource(int resid) {
        setBackground(resid);
    }

    private void setDefaultBackground() {
        if (getBackground() != mDefaultBackground) {
            setBackground(mDefaultBackground);
        }
    }

    @Override
    public void setChecked(boolean checked) {
        mChecked = checked;
        setSelected(checked);
        refreshDrawableState();
        mTitle.setTextColor(checked ? mTabTitle.getColorSelected() : mTabTitle.getColorNormal());
        initIconView();
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }
}