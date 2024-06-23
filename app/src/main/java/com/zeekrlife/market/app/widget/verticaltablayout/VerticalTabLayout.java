package com.zeekrlife.market.app.widget.verticaltablayout;

import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.zeekrlife.common.ext.CommExtKt;
import com.zeekrlife.common.ext.DensityExtKt;
import com.zeekrlife.market.R;
import com.zeekrlife.market.app.widget.verticaltablayout.adapter.TabAdapter;
import com.zeekrlife.market.app.widget.verticaltablayout.util.TabFragmentManager;
import com.zeekrlife.market.app.widget.verticaltablayout.widget.QTabView;
import com.zeekrlife.market.app.widget.verticaltablayout.widget.TabView;
import com.zeekrlife.net.interception.logging.util.XLog;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chqiu
 * Email:qstumn@163.com
 */
public class VerticalTabLayout extends ScrollView {
    private final Context mContext;
    private TabStrip mTabStrip;
    private TabView mSelectedTab;
    private int mTabMargin;
    private int mTabMode;
    private int mTabHeight;

    public static int TAB_MODE_FIXED = 10;
    public static int TAB_MODE_SCROLLABLE = 11;

    public TabAdapter mTabAdapter;

    private final List<OnTabSelectedListener> mTabSelectedListeners;

    private TabFragmentManager mTabFragmentManager;

    public VerticalTabLayout(Context context) {
        this(context, null);
    }

    public VerticalTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mTabSelectedListeners = new ArrayList<>();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VerticalTabLayout);
        mTabMargin = (int) typedArray.getDimension(R.styleable.VerticalTabLayout_tab_margin, 0);
        mTabMode = typedArray.getInteger(R.styleable.VerticalTabLayout_tab_mode, TAB_MODE_FIXED);
        int defaultTabHeight = LinearLayout.LayoutParams.WRAP_CONTENT;
        mTabHeight = (int) typedArray.getDimension(R.styleable.VerticalTabLayout_tab_height, defaultTabHeight);
        typedArray.recycle();
    }

    /**
     * 当视图充气完成时调用此方法。重写此方法以在视图充气结束后执行自定义初始化。
     * 这个方法主要做两件事：
     * 1. 检查子视图数量，如果有则移除所有视图，确保一个干净的开始状态。
     * 2. 调用initTabStrip()初始化Tab条目。
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate(); // 调用父类的onFinishInflate方法

        // 如果当前视图已有子视图，则移除所有子视图
        if (getChildCount() > 0) {
            removeAllViews();
        }

        // 初始化Tab条目
        initTabStrip();
    }

    /**
     * 初始化TabStrip。
     * 这个函数没有参数。
     * 没有返回值。
     * 主要完成了TabStrip的创建和添加到当前视图的操作。
     */
    private void initTabStrip() {
        // 创建一个TabStrip实例
        mTabStrip = new TabStrip(mContext);
        // 将TabStrip实例添加到当前视图中，使用MATCH_PARENT匹配父视图的宽度和高度
        addView(mTabStrip, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }


    /**
     * 清除所有标签页。
     * 该方法不接受参数，也不返回任何值。
     * 它将从标签条中移除所有视图，并将当前选中的标签页设置为null。
     */
    public void removeAllTabs() {
        // 从标签条中移除所有视图
        mTabStrip.removeAllViews();
        // 将当前选中的标签页设置为null
        mSelectedTab = null;
    }


    public void removeSelectedTab() {
        mSelectedTab = null;
    }

    public TabView getTabAt(int position) {
        return (TabView) mTabStrip.getChildAt(position);
    }

    /**
     * 获取当前Tab的数量。
     * <p>该方法不接受任何参数。</p>
     *
     * @return 返回Tab的数量，类型为int。
     */
    public int getTabCount() {
        // 返回Tab条目数量
        return mTabStrip.getChildCount();
    }

    /**
     * 获取当前选中的标签页位置。
     * <p>该方法首先会查找 mSelectedTab 在 mTabStrip 中的位置，如果找到则返回其索引，
     * 如果未找到，则默认返回 0，表示第一个标签页。</p>
     *
     * @return 当前选中标签页的索引位置。如果未找到选中的标签页，则返回 0。
     */
    public int getSelectedTabPosition() {
        // 在 mTabStrip 中查找 mSelectedTab 的索引
        int index = mTabStrip.indexOfChild(mSelectedTab);
        // 如果未找到 mSelectedTab，则返回 0，否则返回找到的索引
        return index == -1 ? 0 : index;
    }


    public TabView getSelectedTab() {
        return mSelectedTab;
    }

    /**
     * 在Tab条目中添加一个Tab，并根据当前模式进行初始化。
     * @param tabView 要添加的Tab视图
     */
    private void addTabWithMode(TabView tabView) {
        // 创建线性布局参数，设置宽度为匹配父视图，高度为包裹内容
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        // 初始化Tab视图，根据当前模式设置相应的属性
        initTabWithMode(params);
        // 将Tab视图添加到Tab条目中
        mTabStrip.addView(tabView, params);
        // 如果添加的Tab是第一个子视图，则将其设为选中状态，并调整其外边距
        if (mTabStrip.indexOfChild(tabView) == 0) {
            tabView.setChecked(true);
            params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
            // 将选中状态的Tab外边距设置为0
            params.setMargins(0, 0, 0, 0);
            tabView.setLayoutParams(params);
            // 更新当前选中的Tab
            mSelectedTab = tabView;
        }
    }

    /**
     * 根据当前的标签模式初始化标签的布局参数。
     * 该方法根据 mTabMode 的值，来设置标签的高、重、以及边距等布局属性。
     * 对于固定模式（TAB_MODE_FIXED），设置高度为0，权重为1.0f，无边距，并启用填充视口。
     * 对于可滚动模式（TAB_MODE_SCROLLABLE），设置高度为 mTabHeight，权重为0f，上下左右边距为 mTabMargin，并禁用填充视口。
     * 如果 mTabMode 不是上述两种模式，记录日志信息。
     *
     * @param params 布局参数，将根据 mTabMode 的值进行配置。
     */
    private void initTabWithMode(LinearLayout.LayoutParams params) {
        if (mTabMode == TAB_MODE_FIXED) {
            // 固定模式下的布局参数设置
            params.height = 0;
            params.weight = 1.0f;
            params.setMargins(0, 0, 0, 0);
            setFillViewport(true);
        } else if (mTabMode == TAB_MODE_SCROLLABLE) {
            // 可滚动模式下的布局参数设置
            params.height = mTabHeight;
            params.weight = 0f;
            params.setMargins(0, mTabMargin, 0, 0);
            setFillViewport(false);
        } else {
            // 非固定且非可滚动模式时，记录日志信息
            XLog.INSTANCE.i("initTabWithMode", "mTabMode ==> " + mTabMode);
        }
    }

    /**
     * 滚动到指定位置的标签页。
     * <p>此方法计算给定位置的标签页与其在视图中心的垂直偏移量，并执行平滑滚动以将标签页居中。</p>
     *
     * @param position 要滚动到的标签页的位置索引。
     */
    private void scrollToTab(int position) {
        // 获取指定位置的标签视图
        final TabView tabView = getTabAt(position);
        // 获取当前滚动的Y轴位置
        int y = getScrollY();
        // 计算标签页顶部位置与其高度的一半减去当前滚动Y轴位置的差值
        int tabTop = tabView.getTop() + tabView.getHeight() / 2 - y;
        // 计算目标位置，即视图高度的一半
        int target = getHeight() / 2;
        // 如果标签页顶部位置大于目标位置，则向下滑动
        if (tabTop > target) {
            smoothScrollBy(0, tabTop - target);
        // 如果标签页顶部位置小于目标位置，则向上滑动
        } else if (tabTop < target) {
            smoothScrollBy(0, tabTop - target);
        // 如果标签页已经处于目标位置，则不执行滚动
        } else {
        }
    }

    private float mLastPositionOffset;

    private void scrollByTab(int position, final float positionOffset) {
        final TabView tabView = getTabAt(position);
        int y = getScrollY();
        int tabTop = tabView.getTop() + tabView.getHeight() / 2 - y;
        int target = getHeight() / 2;
        int nextScrollY = tabView.getHeight() + mTabMargin;
        if (positionOffset > 0) {
            float percent = positionOffset - mLastPositionOffset;
            if (tabTop > target) {
                smoothScrollBy(0, (int) (nextScrollY * percent));
            }
        }
        mLastPositionOffset = positionOffset;
    }

    /**
     * 添加一个Tab视图到Tab条目中。
     * 该方法首先检查传入的TabView是否为null，如果不为null，则将其添加到Tab条目中，并设置点击监听器，
     * 当点击Tab时，会切换到对应的Tab内容。如果传入的TabView为null，会抛出一个IllegalStateException异常。
     *
     * @param tabView 要添加的Tab视图，不可为null。
     * @throws IllegalStateException 如果tabView为null，抛出此异常。
     */
    public void addTab(TabView tabView) {
        if (tabView != null) {
            // 添加TabView到Tab条目，同时设置其显示模式
            addTabWithMode(tabView);

            // 设置TabView的点击监听器，用于在点击时切换Tab
            tabView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 获取点击的Tab在Tab条目中的位置，并设置该Tab为选中状态
                    int position = mTabStrip.indexOfChild(view);
                    setTabSelected(position);
                }
            });
        } else {
            // 如果传入的TabView为null，则抛出异常
            throw new IllegalStateException("tabview can't be null");
        }
    }

    public void setTabSelected(final int position) {
        setTabSelected(position, true, true);
    }

    private void setTabSelected(final int position, final boolean updataIndicator, final boolean callListener) {
                setTabSelectedImpl(position, updataIndicator, callListener);
    }

    /**
     * 实际设置指定位置的标签为选中状态。
     * @param position 标签的位置
     * @param updataIndicator 是否更新指示器
     * @param callListener 是否调用监听器
     */
    private void setTabSelectedImpl(final int position, boolean updataIndicator, boolean callListener) {
        // 尝试获取指定位置的标签视图
        TabView view = getTabAt(position);
        boolean selected;
        // 判断当前标签是否被选中，并处理选中状态的变更
        if (selected = (view != mSelectedTab) && view != null) {
            // 如果当前有选中的标签，则将其取消选中
            if (mSelectedTab != null) {
                mSelectedTab.setChecked(false);
            }
            // 设置当前标签为选中状态
            view.setChecked(true);
            mSelectedTab = view;
            // 滚动到当前选中的标签
            scrollToTab(position);
        }
        // 如果需要，调用监听器通知选中状态的变更
        if (callListener) {
            // 遍历所有监听器并分别调用相应的状态变更方法
            for (int i = 0; i < mTabSelectedListeners.size(); i++) {
                OnTabSelectedListener listener = mTabSelectedListeners.get(i);
                if (listener != null) {
                    // 如果是新选中的标签，则调用 onTabSelected 方法
                    if (selected) {
                        listener.onTabSelected(view, position);
                    } else {
                        // 如果是重新选中的标签，则调用 onTabReselected 方法
                        listener.onTabReselected(view, position);
                    }
                }
            }
        }
    }


    public void setTabBadge(int tabPosition, int badgeNum) {
        getTabAt(tabPosition).getBadgeView().setBadgeNumber(badgeNum);
    }

    public void setTabBadge(int tabPosition, String badgeText) {
        getTabAt(tabPosition).getBadgeView().setBadgeText(badgeText);
    }

/**
 * 重置所有标签页的状态。
 * 该方法将所有标签页的字体样式设置为正常，文本颜色设置为应用的主题主文本颜色。
 * 同时，设置所有标签页的背景为未选中状态的指示器。此方法适用于API等级23及以上。
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public void setReset() {
    mSelectedTab = null; // 清除当前选中的标签页

    // 遍历所有标签页，重置其状态
    for (int i = 0; i < getTabCount(); i++) {
        // 设置标签页标题的字体样式和文本颜色
        getTabAt(i).getTitleView().setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        getTabAt(i).getTitleView().setTextColor(mContext.getColor(R.color.theme_main_text_color));

        // 设置标签页的透明度和图标透明度
        getTabAt(i).getTabView().getTitleView().setAlpha(0.8f);
        getTabAt(i).getTabView().getIconView().setAlpha(0.8f);

        // 设置标签页的背景为未选中状态的指示器
        getTabAt(i).setBackground(R.drawable.indicator_unselector);
    }
}

    /**
     * 将当前选中的Tab设置为粗体，并调整其透明度和背景；对其他非选中Tab进行相应的样式设置。
     * 此函数遍历所有Tab，根据选中状态应用不同的样式。
     * 选中状态的Tab会设置为粗体，完全可见（透明度1f），并应用特定的背景图片。
     * 非选中状态的Tab会设置为正常字体，降低透明度（至0.8f），并应用另一种背景图片。
     * 同时，如果满足特定条件（如屏幕宽度为2560px），会对非选中Tab的透明度进行进一步调整。
     * 如果设置了Tab的图片加载失败的重试策略，则会尝试重试加载图片。
     */
    public void setBold() {
        for (int i = 0; i < getTabCount(); i++) {
            if(i == getSelectedTabPosition()){
                getTabAt(i).getTitleView().setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                getTabAt(i).getTabView().setAlpha(1f);
                getTabAt(i).getTabView().getTitleView().setAlpha(1f);
                getTabAt(i).getTabView().getIconView().setAlpha(1f);
                getTabAt(i).setBackground(R.drawable.indicator_selector);
            }else {
                getTabAt(i).getTitleView().setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
//                if(DensityExtKt.getScreenWidthIs2560()){
//                getTabAt(i).getTabView().setAlpha(0.8f);
                    getTabAt(i).getTabView().getTitleView().setAlpha(0.8f);
                    getTabAt(i).getTabView().getIconView().setAlpha(0.8f);
//                }else {
//                    getTabAt(i).getTabView().setAlpha(0.6f);
//                }
                getTabAt(i).setBackground(R.drawable.indicator_unselector);
            }
            if(!QTabView.mRetryGlideUntilSuccess){
                getTabAt(i).getTabView().retryGlideUntilSuccess();
            }

        }
    }

    public void setTabMode(int mode) {
        if (mode != TAB_MODE_FIXED && mode != TAB_MODE_SCROLLABLE) {
            throw new IllegalStateException("only support TAB_MODE_FIXED or TAB_MODE_SCROLLABLE");
        }
        if (mode == mTabMode) {
            return;
        }
        mTabMode = mode;
        for (int i = 0; i < mTabStrip.getChildCount(); i++) {
            View view = mTabStrip.getChildAt(i);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
            initTabWithMode(params);
            if (i == 0) {
                params.setMargins(0, 0, 0, 0);
            }
            view.setLayoutParams(params);
        }
        mTabStrip.invalidate();
    }

    /**
     * only in TAB_MODE_SCROLLABLE mode will be supported
     *
     * @param margin margin
     */
    public void setTabMargin(int margin) {
        if (margin == mTabMargin) {
            return;
        }
        mTabMargin = margin;
        if (mTabMode == TAB_MODE_FIXED) {
            return;
        }
        for (int i = 0; i < mTabStrip.getChildCount(); i++) {
            View view = mTabStrip.getChildAt(i);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
            params.setMargins(0, i == 0 ? 0 : mTabMargin, 0, 0);
            view.setLayoutParams(params);
        }
        mTabStrip.invalidate();
    }

    /**
     * only in TAB_MODE_SCROLLABLE mode will be supported
     *
     * @param height height
     */
    public void setTabHeight(int height) {
        if (height == mTabHeight) {
            return;
        }
        mTabHeight = height;
        if (mTabMode == TAB_MODE_FIXED) {
            return;
        }
        for (int i = 0; i < mTabStrip.getChildCount(); i++) {
            View view = mTabStrip.getChildAt(i);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
            params.height = mTabHeight;
            view.setLayoutParams(params);
        }
        mTabStrip.invalidate();
    }


    /**
     * 添加一个Tab选中监听器。
     * 当Tab被选中或取消选中时，此监听器会收到相应的回调。
     * 如果传入的监听器不为null，则将其添加到监听器列表中。
     *
     * @param listener 要添加的Tab选中监听器。该监听器不应为null。
     */
    public void addOnTabSelectedListener(OnTabSelectedListener listener) {
        // 如果监听器不为空，则将其添加到mTabSelectedListeners列表中
        if (listener != null) {
            mTabSelectedListeners.add(listener);
        }
    }


    /**
     * 从tab选中的监听器列表中移除指定的监听器。
     * 如果指定的监听器存在，则从监听器列表中移除它。
     *
     * @param listener 要移除的Tab选中监听器。
     */
    public void removeOnTabSelectedListener(OnTabSelectedListener listener) {
        // 如果监听器非空，则从监听器列表中移除
        if (listener != null) {
            mTabSelectedListeners.remove(listener);
        }
    }


    /**
     * 设置Tab适配器，用于生成和管理Tab栏的视图。
     * @param adapter Tab适配器，负责提供每个Tab的标题、徽章等信息。
     */
    public void setTabAdapter(TabAdapter adapter) {
        // 先移除所有已存在的Tab
        removeAllTabs();

        if (adapter != null) {
            mTabAdapter = adapter; // 保存传入的适配器实例

            // 遍历适配器，为每个Tab添加视图
            for (int i = 0; i < adapter.getCount(); i++) {
                // 创建新的Tab视图，并设置标题和徽章
                addTab(new QTabView(mContext).setTitle(adapter.getTitle(i)).setBadge(adapter.getBadge(i)));
                // 注释掉的代码块可能是用于设置Tab背景的，但当前未启用
            }
        }
    }


    /**
     * 追加
     */
    public void addSetTabAdapter(TabAdapter adapter) {
        if (mTabAdapter != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                addTab(new QTabView(mContext).setTitle(adapter.getTitle(i)));
                //.setBackground(adapter.getBackground(i)));
            }
        }
    }

    public void setupWithFragment(FragmentManager manager, int containerResid, List<Fragment> fragments) {
        if (mTabFragmentManager != null) {
            mTabFragmentManager.detach();
        }
        if (containerResid != 0) {
            mTabFragmentManager = new TabFragmentManager(manager, containerResid, fragments, this);
        } else {
            mTabFragmentManager = new TabFragmentManager(manager, fragments, this);
        }
    }

    /**
     * 追加
     */
    public void addSetupWithFragment(List<Fragment> fragments) {
        mTabFragmentManager.mFragments.addAll(fragments);
        mTabFragmentManager.changeFragment();
    }

    public void clearTabFragment() {
        if (mTabFragmentManager != null) {
            mTabFragmentManager.hideAllFragment();
        }
    }

    public void setupWithFragment(FragmentManager manager, int containerResid, List<Fragment> fragments, TabAdapter adapter) {
        setTabAdapter(adapter);
        setupWithFragment(manager, containerResid, fragments);
    }

    /**
     * 追加fragment
     */
    @SuppressLint("LogNotTimber")
    public void addSetupWithFragment(FragmentManager manager, int containerResid, List<Fragment> fragments, TabAdapter adapter) {
        try {
            addSetTabAdapter(adapter);
            addSetupWithFragment(fragments);
        }catch (Exception e) {
            e.printStackTrace();
            Log.e("", "addSetupWithFragment fragments : "+ fragments +" ;error:" + Log.getStackTraceString(e));
        }
    }

    private class TabStrip extends LinearLayout {
        private AnimatorSet mIndicatorAnimatorSet;

        public TabStrip(Context context) {
            super(context);
            setWillNotDraw(false);
            setOrientation(LinearLayout.VERTICAL);
        }

    }

    public interface OnTabSelectedListener {

        void onTabSelected(TabView tab, int position);

        void onTabReselected(TabView tab, int position);
    }
}
