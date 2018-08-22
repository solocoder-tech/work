package com.example.quchangkeji.mytakeout.base;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import butterknife.ButterKnife;

/**
 * Created by zhuwujing on 2018/8/4.
 */

public abstract class BaseActivity extends FragmentActivity {

    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BaseActivity.this.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getInstance().addActivity(this);
        setContentView(getContentLayoutId());
        ButterKnife.bind(this);
        init();
    }

    /**
     * 子类必须实现,提供自己的布局
     *
     * @return
     */
    public abstract int getContentLayoutId();

    /**
     * 子类必须实现,提供自己的初始化
     */
    protected abstract void init();

    /**
     * 子类完成,但是不一定要完成
     *
     * @param msg
     */
    protected void handleMessage(Message msg) {
    }

    protected void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void finishActivity() {
        AppManager.getInstance().finishActivity();
    }
}
