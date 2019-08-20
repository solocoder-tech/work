package com.example.mytakeout.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * 创建时间：2019/5/24  17:03
 * 作者：5#
 * 描述：TODO
 */
public class MyViewpagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> mFragments;
    private Context mContext;

    public MyViewpagerAdapter(FragmentManager fm, List<Fragment> fragments, Context context) {
        super(fm);
        mFragments = fragments;
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments != null ? mFragments.get(position) : null;
    }

    @Override
    public int getCount() {
        return mFragments != null ? mFragments.size() : 0;
    }
}
