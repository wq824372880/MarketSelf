package com.zeekrlife.market.app.widget.verticaltablayout.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.Px;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.zeekrlife.common.ext.DensityExtKt;
import com.zeekrlife.market.R;
import com.zeekrlife.net.interception.logging.util.LogExtKt;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.DisplayUtil;

/**
 * @author chqiu
 * Email:qstumn@163.com
 */
public class QTabView extends TabView {

    private Context mContext;
    private TextView mTitle;
    private Badge mBadgeView;
    private TabIcon mTabIcon;
    private TabTitle mTabTitle;
    private ImageView mImageView;
    private TabBadge mTabBadge;
    private boolean mChecked;
    private Drawable mDefaultBackground;
    public static boolean mRetryGlideUntilSuccess = true;

    public static final String ICON_RECOMMEND_UNABLE = "icon_recommend_unable";
    public static final String ICON_LOGO_UNABLE = "icon_logo_unable";
    public static final String ICON_SETTING_UNABLE = "icon_setting_unable";

    public QTabView(Context context) {
        super(context);
        mContext = context;
        mTabIcon = new TabIcon.Builder().build();
        mTabTitle = new TabTitle.Builder().build();
        mTabBadge = new TabBadge.Builder().build();
        initView();
        int[] attrs;
        attrs = new int[] { android.R.attr.selectableItemBackgroundBorderless };
        TypedArray a = mContext.getTheme().obtainStyledAttributes(attrs);
        mDefaultBackground = a.getDrawable(0);
        a.recycle();
        setDefaultBackground();
    }

    /**
     * 初始化视图元素。
     * 该方法负责初始化标题视图（如果尚未初始化）、图标视图（当前注释掉的功能）和徽章视图。
     * 通过对mTitle和mImageView的条件检查，确保它们在需要时被正确创建和配置。
     * 并调用initTitleView、initIconView（当前未使用）和initBadge方法进行进一步的初始化。
     */
    private void initView() {
        // 设置最小高度
        setMinimumHeight(DisplayUtil.dp2px(mContext, 25));

        // 初始化标题视图，如果还未初始化
        if (mTitle == null) {
            mTitle = new TextView(mContext);
            // 配置标题视图的布局参数
            FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
            params.leftMargin = DisplayUtil.dp2px(mContext, mContext.getResources().getDimension(R.dimen.tab_title_margin_left));
            params.rightMargin = DisplayUtil.dp2px(mContext, 70);
            mTitle.setLayoutParams(params);
            this.addView(mTitle);
        }

        // 初始化图标视图，如果还未初始化
        if (mImageView == null) {
            mImageView = new ImageView(mContext);
            // 配置图标视图的布局参数
            FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(DisplayUtil.dp2px(mContext, mContext.getResources().getDimension(R.dimen.tab_image_width)),
                    DisplayUtil.dp2px(mContext, mContext.getResources().getDimension(R.dimen.tab_image_width)));
            params.leftMargin = DisplayUtil.dp2px(mContext, mContext.getResources().getDimension(R.dimen.tab_image_margin_left));
            params.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
            mImageView.setLayoutParams(params);
            mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            this.addView(mImageView);
        }

        // 进一步初始化标题视图、图标视图和徽章视图
        initTitleView();
        //initIconView();
        initBadge();
    }

    /**
     * 设置视图的相对内边距。此方法重写了{@link View#setPaddingRelative(int, int, int, int)}方法，
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

    /**
     * 设置标题的内边距。
     * <p>此方法通过指定的左、上、右、下内边距值来调整标题文本的内边距。</p>
     *
     * @param left  左边距，以像素为单位
     * @param top   上边距，以像素为单位
     * @param right 右边距，以像素为单位
     * @param bottom 下边距，以像素为单位
     */
    @Override
    public void setPadding(@Px int left, @Px int top, @Px int right, @Px int bottom) {
        mTitle.setPadding(left, top, right, bottom); // 将指定的内边距值设置给标题对象
    }

    /**
     * 初始化徽章视图。
     * 该方法负责根据 mTabBadge 中设置的属性来配置 mBadgeView 的显示效果。它会检查 mTabBadge 中各项属性的值，
     * 并根据这些值来调用 mBadgeView 的相应方法进行设置。例如，如果 mTabBadge 的背景颜色不为默认值，
     * 则设置 mBadgeView 的背景颜色为 mTabBadge 的背景颜色。
     */
    private void initBadge() {
        mBadgeView = TabBadgeView.bindTab(this); // 绑定Tab的徽章视图

        // 设置徽章的背景颜色、文字颜色、边框颜色和宽度、背景图片及剪裁、文字大小、内边距、显示的数字或文本、位置 gravity 和偏移量、精确模式以及是否显示阴影。
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

    /**
     * 初始化标题视图的方法。
     * 该方法根据当前选中状态、UI模式（白天/夜间）以及标题和图标配置，设置标题栏的文字颜色、大小、内容、样式以及图片。
     * 不接受任何参数，也不返回任何值。
     */
    @SuppressLint("WrongConstant")
    private void initTitleView() {
        // 设置标题文字颜色和大小
        mTitle.setTextColor(isChecked() ? mTabTitle.getColorSelected() : mTabTitle.getColorNormal());
        mTitle.setTextSize(mTabTitle.getTitleTextSize());
        // 设置标题文字内容
        mTitle.setText(mTabTitle.getContent());
        // 设置标题文字样式
        mTitle.setTypeface(isChecked() ? Typeface.defaultFromStyle(mTabTitle.getTitleStyle()) : Typeface.defaultFromStyle(0));
        // 设置标题文字居中显示，最多一行，超出部分省略显示
        mTitle.setGravity(Gravity.CENTER);
        mTitle.setMaxLines(1);
        mTitle.setEllipsize(TextUtils.TruncateAt.END);

        // 根据当前UI模式选择对应的图片显示
        boolean uiMode = DensityExtKt.getUINightMode();
        switch (mTabTitle.getImageUrl()) {
            case ICON_RECOMMEND_UNABLE:
                // 根据UI模式加载推荐图标
                if (uiMode) {
                    Glide.with(mContext).load(R.drawable.ic_recommend_able_night).into(mImageView);
                } else {
                    Glide.with(mContext).load(R.drawable.ic_recommend_able).into(mImageView);
                }
                break;
            case ICON_LOGO_UNABLE:
                // 根据UI模式加载Logo图标
                if (uiMode) {
                    Glide.with(mContext).load(R.drawable.ic_logo_able_night).into(mImageView);
                } else {
                    Glide.with(mContext).load(R.drawable.ic_logo_able).into(mImageView);
                }
                break;
            case ICON_SETTING_UNABLE:
                // 根据UI模式加载设置图标
                if (uiMode) {
                    Glide.with(mContext).load(R.drawable.ic_setting_able_night).into(mImageView);
                } else {
                    Glide.with(mContext).load(R.drawable.ic_setting_able).into(mImageView);
                }
                break;
            default:
                // 对于未指定的情况，尝试执行重试逻辑以加载图片
                retryGlideUntilSuccess();
                break;
        }
    }

    /**
     * 初始化图标视图。
     * 此方法配置图标的位置和大小，并将其与标题栏结合。
     * 根据 mTabIcon 设置的图标重力，图标会被放置在标题的左、上、右或下侧。
     * 图标的大小可以通过 mTabIcon 设置的宽高进行调整，如果未设置，则采用图标的固有大小。
     */
    private void initIconView() {
        Drawable drawable = mTabIcon.getNormalDrawable();
        if (drawable != null) {
            // 设置图标边界，根据 mTabIcon 中指定的宽度和高度调整图标大小，
            // 如果未指定，则使用图标的原始大小。
            int r = mTabIcon.getIconWidth() != -1 ? mTabIcon.getIconWidth() : drawable.getIntrinsicWidth();
            int b = mTabIcon.getIconHeight() != -1 ? mTabIcon.getIconHeight() : drawable.getIntrinsicHeight();
            drawable.setBounds(0, 0, r, b);
        }
        // 根据 mTabIcon 中设置的图标重力，将图标放置在标题的相应位置。
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
                // 如果未设置重力，不进行任何操作。
                break;
        }
        // 刷新绘制对象的内边距，确保图标和文字之间有合适的间距。
        refreshDrawablePadding();
    }


    /**
     * 刷新Drawable的内边距。
     * 该方法用于根据当前Tab的图标和标题内容，来调整标题文本的复合Drawable的内边距。
     * 无参数。
     * 无返回值。
     */
    private void refreshDrawablePadding() {
        // 获取当前Tab的正常状态图标（Drawable）
        Drawable drawable = mTabIcon.getNormalDrawable();
        if (drawable != null) {
            // 如果标题内容不为空且当前的复合Drawable内边距不等于图标边距，则设置复合Drawable内边距为图标边距
            if (!TextUtils.isEmpty(mTabTitle.getContent()) && mTitle.getCompoundDrawablePadding() != mTabIcon.getMargin()) {
                mTitle.setCompoundDrawablePadding(mTabIcon.getMargin());
            } else if (TextUtils.isEmpty(mTabTitle.getContent())) {
                // 如果标题内容为空，则将复合Drawable的内边距设置为0
                mTitle.setCompoundDrawablePadding(0);
            }
        } else {
            // 如果图标为空，也将复合Drawable的内边距设置为0
            mTitle.setCompoundDrawablePadding(0);
        }
    }

    /**
     * 设置标签的徽章。
     * 该方法允许为当前的标签设置一个徽章表示，徽章可以用来显示一些简短的信息，如未读消息数。
     * 如果传入的徽章对象不为null，则更新当前标签的徽章为传入的徽章对象。
     *
     * @param badge 徽章对象，用于设置标签上的徽章。如果为null，则不进行任何操作。
     * @return 返回当前的QTabView对象，允许链式调用。
     */
    @Override
    public QTabView setBadge(TabBadge badge) {
        // 如果传入的badge不为空，则更新mTabBadge为传入的badge
        if (badge != null) {
            mTabBadge = badge;
        }
        // 初始化徽章显示
        initBadge();
        return this;
    }

    /**
     * 设置标签页的图标。
     *
     * @param icon TabIcon类型，代表要设置的图标。如果为null，则不设置图标。
     * @return 返回QTabView类型的实例，支持链式调用。
     */
    @Override
    public QTabView setIcon(TabIcon icon) {
        // 检查传入的图标是否非空，非空则更新图标
        if (icon != null) {
            mTabIcon = icon;
        }
        // 初始化图标视图
        initIconView();
        return this;
    }

    /**
     * 设置当前标签页的标题，并初始化标题视图。
     *
     * @param title 指定的标签页标题，如果为null，则不更新标题。
     * @return 返回当前的QTabView实例，支持链式调用。
     */
    @Override
    public QTabView setTitle(TabTitle title) {
        // 检查传入的标题是否非空，非空则更新标题
        if (title != null) {
            mTabTitle = title;
        }
        // 初始化或更新标题视图
        initTitleView();
        return this; // 支持链式调用
    }

    public ImageView setImageView() {
        return mImageView;
    }

    /**
     * 设置Tab视图的背景资源。
     * 如果传入的资源id大于0，则将该资源设置为背景；
     * 如果传入的资源id等于0，则恢复为默认背景；
     * 如果传入的资源id小于0，则移除背景。
     *
     * @param resId 要设置的背景资源的ID。如果小于等于0，则代表要移除或恢复默认背景。
     * @return 返回当前的QTabView实例，以支持链式调用。
     */
    @Override
    public QTabView setBackground(int resId) {
        // 根据资源ID的不同，选择不同的背景设置逻辑
        if (resId == 0) {
            setDefaultBackground(); // 恢复默认背景
        } else if (resId <= 0) {
            setBackground(null); // 移除背景
        } else {
            super.setBackgroundResource(resId); // 设置指定的背景资源
        }
        return this; // 支持链式调用
    }

    /**
     * 获取当前对象关联的Tab标签。
     *
     * @return TabBadge 返回当前对象所关联的Tab标签。这个方法没有参数。
     */
    @Override
    public TabBadge getBadge() {
        return mTabBadge;
    }


    /**
     * 获取当前对象的Tab图标。
     *
     * @return TabIcon 返回当前对象关联的Tab图标实例。
     */
    @Override
    public TabIcon getIcon() {
        return mTabIcon;
    }

    /**
     * 获取当前对象的标签标题。
     *
     * @return 返回一个TabTitle对象，代表当前对象的标题。
     */
    @Override
    public TabTitle getTitle() {
        return mTabTitle;
    }

    /**
     * 获取图标视图的函数。
     * <p>
     * 本方法已弃用，建议使用其他方式获取图标视图。
     *
     * @return 返回一个ImageView对象，代表图标视图。
     */
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

    /**
     * 获取图标视图的函数。
     * <p>
     * 本方法已弃用，建议使用其他方式获取图标视图。
     *
     * @return 返回一个ImageView对象，代表图标视图。
     */
    private void setDefaultBackground() {
        if (getBackground() != mDefaultBackground) {
            setBackground(mDefaultBackground);
        }
    }

    /**
     * 设置当前项是否被选中。
     * 这个方法会同时设置选中状态，刷新Drawable状态，并根据选中状态改变标题文字颜色。
     *
     * @param checked 指定当前项是否被选中。如果为true，则表示选中；如果为false，则表示未选中。
     */
    @Override
    public void setChecked(boolean checked) {
        mChecked = checked; // 设置选中状态
        setSelected(checked); // 根据选中状态设置选中效果
        refreshDrawableState(); // 刷新Drawable的状态

        // 根据选中状态改变标题文字颜色
        mTitle.setTextColor(checked ? mTabTitle.getColorSelected() : mTabTitle.getColorNormal());

        initIconView(); // 初始化图标视图
    }


    /**
     * 检查当前状态是否被选中。
     *
     * 该方法没有参数。
     *
     * @return boolean - 返回当前对象的选中状态。如果选中，则返回true；否则返回false。
     */
    @Override
    public boolean isChecked() {
        return mChecked;
    }


    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

/**
 * 使用Glide库重试加载图片直至成功。
 * 该方法会检查上下文是否有效，然后尝试加载指定的图片到ImageView中。
 * 若加载失败，则在5秒后重试，直至图片加载成功。
 *
 * 该方法不接受任何参数且无返回值。
 */
@Override
public void retryGlideUntilSuccess(){
    if(mContext != null){
        // 使用Glide库加载图片，设置占位符和错误图，并添加监听器以处理加载成功或失败的情况
        Glide.with(mImageView.getContext())
                .load(mTabTitle.getImageUrl())
                .placeholder(R.drawable.img_bg_default)
                .error(R.drawable.img_bg_error)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // 当图片加载失败时，标记不再重试，并记录日志
                        mRetryGlideUntilSuccess = false;
                        LogExtKt.logE("QTabView","QTabView retryGlideUntilSuccess: onLoadFailed");
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        // 当图片加载成功时，标记为可以重试加载
                        mRetryGlideUntilSuccess = true;
                        return false;
                    }
                })
                .into(mImageView);
    }
}

}