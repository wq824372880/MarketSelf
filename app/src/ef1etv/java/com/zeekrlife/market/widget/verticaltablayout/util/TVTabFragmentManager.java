package com.zeekrlife.market.widget.verticaltablayout.util;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.zeekrlife.market.widget.verticaltablayout.VerticalTVTabLayout;
import com.zeekrlife.market.widget.verticaltablayout.widget.AbstractTVTabView;

import java.util.List;

public class TVTabFragmentManager {
    private FragmentManager mManager;
    private int mContainerResid;
    public List<Fragment> mFragments;
    private VerticalTVTabLayout mTabLayout;
    private VerticalTVTabLayout.OnTabSelectedListener mListener;

    public TVTabFragmentManager(FragmentManager manager, List<Fragment> fragments, VerticalTVTabLayout tabLayout) {
        this.mManager = manager;
        this.mFragments = fragments;
        this.mTabLayout = tabLayout;
        mListener = new OnFragmentTabSelectedListener();
        mTabLayout.addOnTabSelectedListener(mListener);
    }

    public TVTabFragmentManager(FragmentManager manager, int containerResid, List<Fragment> fragments, VerticalTVTabLayout tabLayout) {
        this(manager, fragments, tabLayout);
        this.mContainerResid = containerResid;
        changeFragment();
    }

    public void changeFragment() {
        FragmentTransaction ft = mManager.beginTransaction();
        int position = mTabLayout.getSelectedTabPosition();
        List<Fragment> addedFragments = mManager.getFragments();
        for (int i = 0; i < mFragments.size(); i++) {
            Fragment fragment = mFragments.get(i);
            if ((addedFragments == null || !addedFragments.contains(fragment)) && mContainerResid != 0) {
                ft.add(mContainerResid, fragment);
            }
            if ((mFragments.size() > position && i == position)
                    || (mFragments.size() <= position && i == mFragments.size() - 1)) {
                ft.show(fragment);
            } else {
                ft.hide(fragment);
            }
        }
        ft.commitAllowingStateLoss();
        mManager.executePendingTransactions();
    }

    public void hideAllFragment() {
        FragmentTransaction ft = mManager.beginTransaction();
        for (int i = 0; i < mFragments.size(); i++) {
            ft.hide(mFragments.get(i));
        }
        ft.commitAllowingStateLoss();
        mManager.executePendingTransactions();
    }


    public void detach() {
        FragmentTransaction ft = mManager.beginTransaction();
        for (Fragment fragment : mFragments) {
            ft.remove(fragment);
        }
        ft.commitAllowingStateLoss();
        mManager.executePendingTransactions();
        mManager = null;
        mFragments = null;
        mTabLayout.removeOnTabSelectedListener(mListener);
        mListener = null;
        mTabLayout = null;
    }


    private class OnFragmentTabSelectedListener implements VerticalTVTabLayout.OnTabSelectedListener {

        @Override
        public void onTabSelected(AbstractTVTabView tab, int position) {
            changeFragment();
        }

        @Override
        public void onTabReselected(AbstractTVTabView tab, int position) {
        }

    }
}
