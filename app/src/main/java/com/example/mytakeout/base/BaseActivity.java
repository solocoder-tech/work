package com.example.mytakeout.base;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mytakeout.R;
import com.example.mytakeout.utils.LogUtils;
import com.example.mytakeout.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by zhuwujing on 2018/8/4.
 */

public abstract class BaseActivity extends FragmentActivity {


    private TextView startusBarTv;
    protected FrameLayout mContainer;
    private RelativeLayout titelRl;

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
        setContentView(R.layout.activity_base);
        mContainer = (FrameLayout) findViewById(R.id.content_container);
        startusBarTv = (TextView) findViewById(R.id.replace_status_bar);
        titelRl = (RelativeLayout) findViewById(R.id.title);

        initViews();
        initEvents();
        initDatas();
    }


    protected abstract void initViews();

    /**
     * 子类必须实现,提供自己的布局
     *
     * @return
     */
    public void setCustomView(@LayoutRes int layRes, boolean hasTitle) {
        mContainer.addView(View.inflate(this, layRes, null));
        if (hasTitle) {
            titelRl.setVisibility(View.VISIBLE);
        } else {
            titelRl.setVisibility(View.GONE);
        }
    }

    /**
     * 子类完成,但是不一定要完成
     *
     * @param msg
     */
    protected void handleMessage(Message msg) {
    }

    /**
     * 加载数据，子类选择性实现
     */
    protected abstract void initDatas();

    /**
     * 初始化监听，子类选择性实现
     */
    protected void initEvents() {

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
