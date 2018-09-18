package com.ysu.demo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.ysu.tablayout.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MyPageAdapter extends TabLayout.ViewPagerAdapter {

    public List<BaseFragment> mFragments = new ArrayList<>();

    public MyPageAdapter(FragmentManager fm) {
        super(fm);
        mFragments.add(FirstFragment.newInstance());
        mFragments.add(SecondFragment.newInstance());
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragments.get(position).getTitle();
    }
}
