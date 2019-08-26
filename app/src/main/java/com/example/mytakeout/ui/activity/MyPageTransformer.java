package com.example.mytakeout.ui.activity;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewParent;

import com.example.mytakeout.utils.LogUtils;

/**
 * 创建时间：2019/8/23  11:46
 * 作者：5#
 * 描述：TODO
 */
public class MyPageTransformer implements ViewPager.PageTransformer {

    private ViewPager mViewPager;

    @Override
    public void transformPage(@NonNull View view, float position) {
        if (mViewPager == null) {
            mViewPager = (ViewPager) view.getParent();
        }
        if (position >= -1 && position < 0) {
            view.setScaleX(1 + position);
            view.setScaleY(1 + position);
        } else if (position >= 0 && position < 1) {
            view.setScaleX(1 - position);
            view.setScaleY(1 - position);
        }
    }
}
