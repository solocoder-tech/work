package com.example.quchangkeji.mytakeout.ui.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import butterknife.ButterKnife;

/**
 * Created by zhuwujing on 2018/8/4.
 */

public abstract class BaseActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    protected void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
