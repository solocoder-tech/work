package com.example.mytakeout.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.mytakeout.R;
import com.example.mytakeout.base.BaseActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 创建时间：2019/8/19  23:34
 * 作者：5#
 * 描述：ViewPager的使用
 * 1.引导页
 * 2.轮播图
 * 3.ViewPager + Fragment 搭建app框架
 */
public class ViewPagerTestActivity extends BaseActivity {
    @Override
    protected void initViews() {
        String title = getIntent().getStringExtra("title");
        setCustomView(R.layout.activity_test_viewpager, true, title);
        ButterKnife.bind(this);
    }

    @Override
    protected void initDatas() {

    }

    @Override
    protected void initEvents() {

    }

    @OnClick({R.id.app_inductor, R.id.app_carousel, R.id.app_vp_fragment,R.id.app_gallery})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.app_inductor:
                startActivity(new Intent(this, ViewPagerInstuctorActivity.class));
                break;
            case R.id.app_carousel:
                startActivity(new Intent(this, ViewPagerCarouselActivity.class));
                break;
            case R.id.app_gallery:
                startActivity(new Intent(this, ViewPagerGralleryActivity.class));
                break;
            case R.id.app_vp_fragment:
            break;
        }
    }
}
