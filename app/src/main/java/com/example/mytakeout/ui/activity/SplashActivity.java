package com.example.mytakeout.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.mytakeout.R;
import com.example.mytakeout.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 创建时间：2019/8/20  0:06
 * 作者：5#
 * 描述：TODO
 */
public class SplashActivity extends BaseActivity {
    private final int GO_MAIN = 1001;
    @BindView(R.id.splash_image)
    ImageView splashImage;

    @Override
    protected void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case GO_MAIN:
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finishActivity();
                break;
        }
    }

    @Override
    protected void initViews() {
        setCustomView(R.layout.activity_splash, false);
        ButterKnife.bind(this);
    }

    @Override
    protected void initDatas() {
        mHandler.sendEmptyMessageDelayed(GO_MAIN, 3000);
        Glide.with(this)
                .load(R.drawable.timg)
                .into(splashImage);
    }

    @Override
    protected void initEvents() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

}
