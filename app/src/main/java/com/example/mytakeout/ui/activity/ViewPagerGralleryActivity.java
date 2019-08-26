package com.example.mytakeout.ui.activity;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.mytakeout.R;
import com.example.mytakeout.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 创建时间：2019/8/23  19:01
 * 作者：5#
 * 描述：TODO
 */
public class ViewPagerGralleryActivity extends BaseActivity {
    @BindView(R.id.vp)
    ViewPager mViewPager;

    @Override
    protected void initViews() {
        setCustomView(R.layout.activity_viewpager_gallery, false);
        ButterKnife.bind(this);
    }

    @Override
    protected void initDatas() {
        mViewPager.setPageMargin((int)(getResources().getDisplayMetrics().density * 15));
        // 为了左右无限滑动，显示在中间，且显示第一张
        int i = Integer.MAX_VALUE/2%4;
        mViewPager.setCurrentItem(Integer.MAX_VALUE/2 + (4-i));
        mViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return Integer.MAX_VALUE;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
                return view == o;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                position %= 4;
                View inflate = View.inflate(ViewPagerGralleryActivity.this, R.layout.item_banner, null);
                ImageView iv = (ImageView) inflate.findViewById(R.id.iv_banner);
                int resouceId = getResources().getIdentifier("item" + (position + 1), "drawable", getApplication().getPackageName());
                Glide.with(ViewPagerGralleryActivity.this).load(resouceId).into(iv);
                container.addView(inflate);
                return inflate;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((View) object);
            }

            @Override
            public float getPageWidth(int position) {
                // 加上这句
                if(getCount() < 2){
                    return super.getPageWidth(position);
                }
                float itemWidth =  (getResources().getDisplayMetrics().density * 300);
                float vpWidth = (getResources().getDisplayMetrics().widthPixels - getResources().getDisplayMetrics().density * 60);
                return  itemWidth / vpWidth;
            }
        });
    }

    @Override
    protected void initEvents() {

    }
}
