package com.example.mytakeout.ui.activity;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.example.mytakeout.R;
import com.example.mytakeout.adapter.VPAdapter;
import com.example.mytakeout.base.BaseActivity;
import com.example.mytakeout.utils.LogUtils;

import java.util.ArrayList;

import butterknife.BindView;
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
public class ViewPagerInstuctorActivity extends BaseActivity {
    @BindView(R.id.vp)
    ViewPager mViewPager;

    @Override
    protected void initViews() {
        String title = getIntent().getStringExtra("title");
        setCustomView(R.layout.activity_test_viewpager_instructor, true, title);
        ButterKnife.bind(this);
    }

    @Override
    protected void initDatas() {
        LogUtils.e("vp===" + mViewPager.hashCode());
        ArrayList<String> datas = new ArrayList<>();
        datas.add("0");
        datas.add("1");
        datas.add("2");
        datas.add("3");
        mViewPager.setAdapter(new VPAdapter(datas, this));
        mViewPager.setPageTransformer(false, new CustPagerTransformer(this));
//        mViewPager.setPageMargin(20);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                页面滑动状态停止前一直调用
//                position：当前点击滑动页面的位置
//                positionOffset：当前页面偏移的百分比   0-->1
//                positionOffsetPixels：当前页面偏移的像素位置
                LogUtils.e("onPageScrolled==position= " + position + "==positionOffset=" + positionOffset + "==positionOffsetPixels=" + positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                //滑动后显示的页面和滑动前不同，调用
                // position：选中显示页面的位置
                LogUtils.e("onPageSelected==position= " + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
//                页面状态改变时调用
//                state：当前页面的状态
//                SCROLL_STATE_IDLE：空闲状态
//                SCROLL_STATE_DRAGGING：滑动状态
//                SCROLL_STATE_SETTLING：滑动后滑翔的状态
                switch (state) {
                    case ViewPager.SCROLL_STATE_IDLE:
                        LogUtils.e("onPageScrollStateChanged==state=空闲状态 ");
                        break;
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        LogUtils.e("onPageScrollStateChanged==state=滑动状态 ");
                        break;
                    case ViewPager.SCROLL_STATE_SETTLING:
                        LogUtils.e("onPageScrollStateChanged==state=滑动后滑翔的状态 ");
                        break;
                }
            }
        });
    }

    @Override
    protected void initEvents() {

    }

}
