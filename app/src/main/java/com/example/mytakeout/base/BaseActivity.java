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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mytakeout.R;
import com.example.mytakeout.modle.net.bean.EventScanResult;
import com.example.mytakeout.net.Api;
import com.example.mytakeout.net.RetrofitUtils;
import com.example.mytakeout.utils.ImmersionBarUtils;
import com.example.mytakeout.utils.LogUtils;
import com.example.mytakeout.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    private ImageView titelBack;
    private TextView titelCenterTv;
    protected Api mApi;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getInstance().addActivity(this);
        setContentView(R.layout.activity_base);
        ImmersionBarUtils.setImmersionBar(this, R.color.color_ff3190E8, false);
        mContainer = (FrameLayout) findViewById(R.id.content_container);
        startusBarTv = (TextView) findViewById(R.id.replace_status_bar);
        titelRl = (RelativeLayout) findViewById(R.id.title);
        titelBack = (ImageView) findViewById(R.id.title_back);
        titelCenterTv = (TextView) findViewById(R.id.title_center_tv);
        titelBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivity();
            }
        });

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) titelRl.getLayoutParams();
        layoutParams.setMargins(0, UIUtils.getStatusBarHeight(this), 0, 0);
        titelRl.setLayoutParams(layoutParams);


        mApi = RetrofitUtils.getRetrofitUtils().getApi();
        EventBus.getDefault().register(this);

        initViews();
        initEvents();
        initDatas();
    }


    /**
     * 初始化布局，子类必须实现
     */
    protected abstract void initViews();

    /**
     * 加载数据，子类必须实现
     */
    protected abstract void initDatas();

    /**
     * 初始化监听，子类选择性实现
     */
    protected abstract void initEvents();


    /**
     * 子类选择实现,可以提供自己的布局，判断需不需要标题栏
     *
     * @return
     */
    public void setCustomView(@LayoutRes int layRes, boolean hasTitle, String title) {
        if (hasTitle) {
            titelRl.setVisibility(View.VISIBLE);
            titelCenterTv.setText(title);
            mContainer.addView(View.inflate(this, layRes, null));
        } else {
            titelRl.setVisibility(View.GONE);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.topMargin = 50;
            mContainer.addView(View.inflate(this, layRes, null), layoutParams);
        }
    }

    public void setCustomView(@LayoutRes int layRes, boolean hasTitle) {
        setCustomView(layRes, hasTitle, "");
    }

    /**
     * 子类完成,但是不一定要完成
     *
     * @param msg
     */
    protected void handleMessage(Message msg) {
    }


    protected void toast(String msg, int duration) {
        if (duration == Toast.LENGTH_SHORT) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }
    }

    protected void toast(String msg) {
        toast(msg, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mHandler.removeCallbacksAndMessages(null);
    }

    public void finishActivity() {
        AppManager.getInstance().finishActivity();
    }

    /**
     * EventBus
     * 子类选择的去接受
     *
     * @param eventScanResult
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getEventMsg(EventScanResult eventScanResult) {

    }
}
