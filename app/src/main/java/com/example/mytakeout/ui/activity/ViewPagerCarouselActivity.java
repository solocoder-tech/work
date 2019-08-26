package com.example.mytakeout.ui.activity;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.mytakeout.R;
import com.example.mytakeout.base.BaseActivity;
import com.example.mytakeout.utils.LogUtils;
import com.youth.banner.Banner;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 创建时间：2019/8/23  16:52
 * 作者：5#
 * 描述：TODO
 */
public class ViewPagerCarouselActivity extends BaseActivity {
    @BindView(R.id.banner)
    Banner mBanner;

    @Override
    protected void initViews() {
        setCustomView(R.layout.activity_viewpager_carousel, false);
        ButterKnife.bind(this);
    }

    @Override
    protected void initDatas() {
        mBanner.setImageLoader(new ImageLoader() {
            /**
             注意：
             1.图片加载器由自己选择，这里不限制，只是提供几种使用方法
             2.返回的图片路径为Object类型，由于不能确定你到底使用的那种图片加载器，
             传输的到的是什么格式，那么这种就使用Object接收和返回，你只需要强转成你传输的类型就行，
             切记不要胡乱强转！
             */
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                //Glide 加载图片简单用法
                LogUtils.e("path==" + path);
                Glide.with(context).load(path).into(imageView);
            }
        });
        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(R.drawable.item1);
        arrayList.add(R.drawable.item2);
        arrayList.add(R.drawable.item3);
        arrayList.add(R.drawable.item4);
        arrayList.add(R.drawable.item5);
        mBanner.setImages(arrayList);
        mBanner.start();
    }

    @Override
    protected void initEvents() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mBanner.startAutoPlay();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBanner.stopAutoPlay();
    }
}
