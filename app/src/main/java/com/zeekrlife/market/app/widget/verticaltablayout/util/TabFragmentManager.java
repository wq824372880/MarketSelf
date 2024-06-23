package com.zeekrlife.market.app.widget.verticaltablayout.util;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.zeekrlife.market.app.widget.verticaltablayout.VerticalTabLayout;
import com.zeekrlife.market.app.widget.verticaltablayout.widget.TabView;
import java.util.List;

public class TabFragmentManager {
    private FragmentManager mManager;
    private int mContainerResid;
    public List<Fragment> mFragments;
    private VerticalTabLayout mTabLayout;
    private VerticalTabLayout.OnTabSelectedListener mListener;

    public TabFragmentManager(FragmentManager manager, List<Fragment> fragments, VerticalTabLayout tabLayout) {
        this.mManager = manager;
        this.mFragments = fragments;
        this.mTabLayout = tabLayout;
        mListener = new OnFragmentTabSelectedListener();
        mTabLayout.addOnTabSelectedListener(mListener);
    }

    public TabFragmentManager(FragmentManager manager, int containerResid, List<Fragment> fragments, VerticalTabLayout tabLayout) {
        this(manager, fragments, tabLayout);
        this.mContainerResid = containerResid;
        changeFragment();
    }

    /**
     * 更改当前显示的Fragment。
     * 该方法不接受参数，并且没有返回值。
     * 它通过获取当前TabLayout选中的位置，来决定要显示的Fragment，并对其他Fragment进行隐藏或添加操作。
     */
    public void changeFragment() {
        // 开始进行Fragment事务
        FragmentTransaction ft = mManager.beginTransaction();

        // 获取当前选中的Tab位置
        int position = mTabLayout.getSelectedTabPosition();

        // 获取已添加的Fragment列表
        List<Fragment> addedFragments = mManager.getFragments();

        // 遍历所有要管理的Fragment，进行添加、隐藏或显示的操作
        for (int i = 0; i < mFragments.size(); i++) {
            Fragment fragment = mFragments.get(i);

            // 如果Fragment未被添加且容器资源ID不为0，则将其添加到容器中
            if ((addedFragments == null || !addedFragments.contains(fragment)) && mContainerResid != 0) {
                ft.add(mContainerResid, fragment);
            }

            // 根据当前Tab的位置，决定是显示还是隐藏该Fragment
            if ((mFragments.size() > position && i == position)
                    || (mFragments.size() <= position && i == mFragments.size() - 1)) {
                ft.show(fragment);
            } else {
                ft.hide(fragment);
            }
        }

        // 提交事务，并允许状态丢失
        ft.commitAllowingStateLoss();
        // 执行所有待处理的Fragment事务
        mManager.executePendingTransactions();
    }

    /**
     * 隐藏所有片段。
     * 这个方法通过遍历片段列表，对每个片段执行隐藏操作。它适用于需要一次性隐藏应用中所有片段的场景。
     * 注意：这个方法不会移除片段，只是将它们隐藏起来。
     */
    public void hideAllFragment() {
        // 开始一个片段事务，用于执行隐藏操作
        FragmentTransaction ft = mManager.beginTransaction();
        for (int i = 0; i < mFragments.size(); i++) {
            // 遍历片段列表，对每个片段执行隐藏操作
            ft.hide(mFragments.get(i));
        }
        // 提交事务，允许在状态丢失的情况下提交，这意味着如果发生异常，事务可能不会被保存
        ft.commitAllowingStateLoss();
        // 强制执行所有待处理的片段事务，确保隐藏操作立即生效
        mManager.executePendingTransactions();
    }


    /**
     * 从其管理器中移除所有片段，并清理相关资源。
     * 该方法不接受参数且无返回值。
     * 这是分离（或清理）碎片和相关联的Tab布局资源的实用方法。
     * 它通过以下步骤实现：
     * 1. 开始一个片段事务。
     * 2. 遍历并从事务中移除每个片段。
     * 3. 提交事务，允许状态丢失。
     * 4. 执行任何待处理的事务。
     * 5. 清理管理器和片段集合引用。
     * 6. 移除Tab布局的选中监听器。
     * 7. 清理监听器和Tab布局引用。
     */
    public void detach() {
        // 开始一个片段事务
        FragmentTransaction ft = mManager.beginTransaction();

        // 遍历并从事务中移除每个片段
        for (Fragment fragment : mFragments) {
            ft.remove(fragment);
        }

        // 提交事务，允许状态丢失
        ft.commitAllowingStateLoss();

        // 执行任何待处理的事务
        mManager.executePendingTransactions();

        // 清理管理器和片段集合引用
        mManager = null;
        mFragments = null;

        // 移除Tab布局的选中监听器并清理相关引用
        mTabLayout.removeOnTabSelectedListener(mListener);
        mListener = null;
        mTabLayout = null;
    }



    private class OnFragmentTabSelectedListener implements VerticalTabLayout.OnTabSelectedListener {

        /**
         * 当Tab被选中时触发的回调方法。
         * @param tab 被选中的TabView对象。
         * @param position 被选中Tab的位置索引。
         */
        @Override
        public void onTabSelected(TabView tab, int position) {
            // 切换到相应的Fragment
            changeFragment();
        }


        /**
         * 当某个Tab被再次选中时调用的回调方法。
         *
         * @param tab 被再次选中的TabView对象。
         * @param position 被再次选中的Tab的位置索引。
         */
        @Override
        public void onTabReselected(TabView tab, int position) {
            // 此处可以添加当Tab被再次选中时需要执行的逻辑代码
        }

    }
}
