package com.example.mytakeout.ui.activity;

import com.example.mytakeout.R;
import com.example.mytakeout.base.BaseActivity;
import com.example.mytakeout.base.BaseApplication;
import com.example.mytakeout.utils.LogUtils;
import com.squareup.leakcanary.RefWatcher;

/**
 * 创建时间：2018/12/8  11:11
 * 作者：5#
 * 描述：内存泄露分析实例
 */
public class LeakCanaryActivity extends BaseActivity {
    @Override
    protected void initViews() {
        setCustomView(R.layout.activity_leak_canary, true, "LeakCanary");
    }

    @Override
    protected void initDatas() {
        LeakThread leakThread = new LeakThread();
        leakThread.start();
    }

    @Override
    protected void initEvents() {

    }

    class LeakThread extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(6 * 60 * 1000);
                LogUtils.e("sleep");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //activity中没有必要，fragment需要
        RefWatcher refWatcher = BaseApplication.getRefWatcher(this);//1
        refWatcher.watch(this);
    }
}
