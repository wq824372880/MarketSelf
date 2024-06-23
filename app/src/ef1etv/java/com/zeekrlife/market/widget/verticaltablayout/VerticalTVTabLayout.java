package com.zeekrlife.market.widget.verticaltablayout;

import android.animation.AnimatorSet;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.zeekrlife.market.R;
import com.zeekrlife.market.widget.verticaltablayout.adapter.TVTabAdapter;
import com.zeekrlife.market.widget.verticaltablayout.util.TVTabFragmentManager;
import com.zeekrlife.market.widget.verticaltablayout.widget.QTVTabView;
import com.zeekrlife.market.widget.verticaltablayout.widget.AbstractTVTabView;
import com.zeekrlife.net.interception.logging.util.XLog;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chqiu
 * Email:qstumn@163.com
 */
public class VerticalTVTabLayout extends ScrollView {
    private final Context mContext;
    private TabStrip mTabStrip;
    private AbstractTVTabView mSelectedTab;
    private int mTabMargin;
    private int mTabMode;
    private int mTabHeight;

    public static int TAB_MODE_FIXED = 10;
    public static int TAB_MODE_SCROLLABLE = 11;

    public TVTabAdapter mTabAdapter;

    private final List<OnTabSelectedListener> mTabSelectedListeners;

    private final List<OnTabTVListener> mOnTabTVListeners;

    private TVTabFragmentManager mTabFragmentManager;

    public VerticalTVTabLayout(Context context) {
        this(context, null);
    }

    public VerticalTVTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalTVTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mTabSelectedListeners = new ArrayList<>();
        mOnTabTVListeners = new ArrayList<>();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VerticalTabLayout);
        mTabMargin = (int) typedArray.getDimension(R.styleable.VerticalTabLayout_tab_margin, 0);
        mTabMode = typedArray.getInteger(R.styleable.VerticalTabLayout_tab_mode, TAB_MODE_FIXED);
        int defaultTabHeight = LinearLayout.LayoutParams.WRAP_CONTENT;
        mTabHeight = (int) typedArray.getDimension(R.styleable.VerticalTabLayout_tab_height, defaultTabHeight);
        typedArray.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0) {
            removeAllViews();
        }
        initTabStrip();
    }

    private void initTabStrip() {
        mTabStrip = new TabStrip(mContext);
        addView(mTabStrip, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    public void removeAllTabs() {
        mTabStrip.removeAllViews();
        mSelectedTab = null;
    }

    public void removeSelectedTab() {
        mSelectedTab = null;
    }

    public AbstractTVTabView getTabAt(int position) {
        return (AbstractTVTabView) mTabStrip.getChildAt(position);
    }

    public int getTabCount() {
        return mTabStrip.getChildCount();
    }

    public int getSelectedTabPosition() {
        int index = mTabStrip.indexOfChild(mSelectedTab);
        return index == -1 ? 0 : index;
    }

    public AbstractTVTabView getSelectedTab() {
        return mSelectedTab;
    }

    private void addTabWithMode(AbstractTVTabView tabView) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        initTabWithMode(params);
        mTabStrip.addView(tabView, params);
        if (mTabStrip.indexOfChild(tabView) == 0) {
            tabView.setChecked(true);
            params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
            params.setMargins(mTabMargin, mTabMargin, mTabMargin, mTabMargin);
            tabView.setLayoutParams(params);
            mSelectedTab = tabView;
        }
    }

    private void initTabWithMode(LinearLayout.LayoutParams params) {
        if (mTabMode == TAB_MODE_FIXED) {
            params.height = 0;
            params.weight = 1.0f;
            params.setMargins(mTabMargin, mTabMargin, mTabMargin, mTabMargin);
            setFillViewport(true);
        } else if (mTabMode == TAB_MODE_SCROLLABLE) {
            params.height = mTabHeight;
            params.weight = 0f;
            params.setMargins(mTabMargin, mTabMargin, mTabMargin, mTabMargin);
            setFillViewport(false);
        } else {
            XLog.INSTANCE.i("initTabWithMode", "mTabMode ==> " + mTabMode);
        }
    }

    private void scrollToTab(int position) {
        final AbstractTVTabView tabView = getTabAt(position);
        int y = getScrollY();
        int tabTop = tabView.getTop() + tabView.getHeight() / 2 - y;
        int target = getHeight() / 2;
        if (tabTop > target) {
            smoothScrollBy(0, tabTop - target);
        } else if (tabTop < target) {
            smoothScrollBy(0, tabTop - target);
        } else {
        }
    }

    private float mLastPositionOffset;

    private void scrollByTab(int position, final float positionOffset) {
        final AbstractTVTabView tabView = getTabAt(position);
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

    View oldFocusView;

    public void addTab(AbstractTVTabView tabView) {
        if (tabView != null) {
            addTabWithMode(tabView);
            tabView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = mTabStrip.indexOfChild(view);
                    setTabSelected(position);
                }
            });

            tabView.setOnKeyListener(new OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
                        int position = mTabStrip.indexOfChild(tabView);
                        for (int i = 0; i < mOnTabTVListeners.size(); i++) {
                            OnTabTVListener listener = mOnTabTVListeners.get(i);
                            if (listener != null) {
                                listener.onTabEnter(v, position);
                            }
                        }
                    }

                    getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
                        @Override
                        public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                            getViewTreeObserver().removeOnGlobalFocusChangeListener(this::onGlobalFocusChanged);
                            oldFocusView = oldFocus;
                            XLog.INSTANCE.i("setOnFocusChangeListener", "oldFocus ==> " + oldFocus);
                        }
                    });
                    return false;
                }
            });

            tabView.setOnFocusChangeListener((view, hasFocus) -> {
                for (int i = 0; i < mOnTabTVListeners.size(); i++) {
                    OnTabTVListener listener = mOnTabTVListeners.get(i);
                    if (listener != null) {
                        listener.onTabFocusChange(mTabStrip.indexOfChild(view), hasFocus, oldFocusView);
                    }
                }
                return null;
            });

        } else {
            throw new IllegalStateException("tabview can't be null");
        }
    }

    public void setTabSelected(final int position) {
        setTabSelected(position, true, true);
    }

    private void setTabSelected(final int position, final boolean updataIndicator, final boolean callListener) {
        setTabSelectedImpl(position, updataIndicator, callListener);
    }

    private void setTabSelectedImpl(final int position, boolean updataIndicator, boolean callListener) {
        AbstractTVTabView view = getTabAt(position);
        boolean selected;
        if (selected = (view != mSelectedTab) && view != null) {
            if (mSelectedTab != null) {
                mSelectedTab.setChecked(false);
            }
            view.setChecked(true);
            mSelectedTab = view;
            scrollToTab(position);
        }
        if (callListener) {
            for (int i = 0; i < mTabSelectedListeners.size(); i++) {
                OnTabSelectedListener listener = mTabSelectedListeners.get(i);
                if (listener != null) {
                    if (selected) {
                        listener.onTabSelected(view, position);
                    } else {
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setReset() {
        mSelectedTab = null;
        for (int i = 0; i < getTabCount(); i++) {
            getTabAt(i).getTitleView().setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            getTabAt(i).getTitleView().setTextColor(mContext.getColor(R.color.theme_main_text_color));
//                getTabAt(i).getTabView().setAlpha(0.8f);
            getTabAt(i).getTabView().getTitleView().setAlpha(0.8f);
            getTabAt(i).getTabView().getIconView().setAlpha(0.8f);
            getTabAt(i).setBackground(null);
        }
    }

    public void setBold() {
        for (int i = 0; i < getTabCount(); i++) {
            if (i == getSelectedTabPosition()) {
                getTabAt(i).getTitleView().setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
//                getTabAt(i).getTabView().setAlpha(0.4f);
                getTabAt(i).getTabView().requestFocus();
                getTabAt(i).getTabView().getTitleView().setAlpha(1f);
                getTabAt(i).getTabView().getIconView().setAlpha(1f);
//                getTabAt(i).setBackground(R.drawable.tv_item_bg);
                getTabAt(i).setBackground(R.drawable.tv_item_focus_border);
            } else {
                getTabAt(i).getTitleView().setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                getTabAt(i).getTabView().getTitleView().setAlpha(0.8f);
                getTabAt(i).getTabView().getIconView().setAlpha(0.8f);
                getTabAt(i).setBackground(null);
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


    public void addOnTabSelectedListener(OnTabSelectedListener listener) {
        if (listener != null) {
            mTabSelectedListeners.add(listener);
        }
    }

    public void removeOnTabSelectedListener(OnTabSelectedListener listener) {
        if (listener != null) {
            mTabSelectedListeners.remove(listener);
        }
    }

    public void addOnTabTVListener(OnTabTVListener listener) {
        if (listener != null) {
            mOnTabTVListeners.add(listener);
        }
    }

    public void removeOnTabTVListener(OnTabTVListener listener) {
        if (listener != null) {
            mOnTabTVListeners.remove(listener);
        }
    }

    public void setTabAdapter(TVTabAdapter adapter) {
        removeAllTabs();
        if (adapter != null) {
            mTabAdapter = adapter;
            for (int i = 0; i < adapter.getCount(); i++) {
                addTab(new QTVTabView(mContext).setTitle(adapter.getTitle(i)).setBadge(adapter.getBadge(i)));
                //.setBackground(adapter.getBackground(i)));
            }
        }
    }

    /**
     * 追加
     */
    public void addSetTabAdapter(TVTabAdapter adapter) {
        if (mTabAdapter != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                addTab(new QTVTabView(mContext).setTitle(adapter.getTitle(i)));
                //.setBackground(adapter.getBackground(i)));
            }
        }
    }

    public void setupWithFragment(FragmentManager manager, int containerResid, List<Fragment> fragments) {
        if (mTabFragmentManager != null) {
            mTabFragmentManager.detach();
        }
        if (containerResid != 0) {
            mTabFragmentManager = new TVTabFragmentManager(manager, containerResid, fragments, this);
        } else {
            mTabFragmentManager = new TVTabFragmentManager(manager, fragments, this);
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

    public void setupWithFragment(FragmentManager manager, int containerResid, List<Fragment> fragments, TVTabAdapter adapter) {
        setTabAdapter(adapter);
        setupWithFragment(manager, containerResid, fragments);
    }

    /**
     * 追加fragment
     */
    public void addSetupWithFragment(FragmentManager manager, int containerResid, List<Fragment> fragments, TVTabAdapter adapter) {
        addSetTabAdapter(adapter);
        addSetupWithFragment(fragments);
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

        void onTabSelected(AbstractTVTabView tab, int position);

        void onTabReselected(AbstractTVTabView tab, int position);
    }

    public interface OnTabTVListener {
        void onTabFocusChange(int position, boolean hasFocus, View oldFocusView);

        default void onTabEnter(View view, int position) {
        }
    }
}
