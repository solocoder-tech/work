package com.example.sweeper.net;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity {

    @BindView(R.id.syn_get)
    Button synGet;
    @BindView(R.id.syn_post)
    Button synPost;
    @BindView(R.id.syn_post_form)
    Button synPostForm;
    @BindView(R.id.asyn_get)
    Button asynGet;
    @BindView(R.id.asyn_post)
    Button asynPost;
    @BindView(R.id.asyn_post_form)
    Button asynPostForm;
    private RequestManager mRequestManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mRequestManager = RequestManager.getRequestManagerInstance(this);
    }

    @OnClick({R.id.syn_get, R.id.syn_post, R.id.syn_post_form, R.id.asyn_get, R.id.asyn_post, R.id.asyn_post_form})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.syn_get:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HashMap<String, String> params = new HashMap<>();
                        params.put("tn", "7_5b21fc16ac5950.png");
                        mRequestManager.requestSyn("static.firefoxchina.cn/img/201806/", RequestManager.TYPE_GET, params);
                    }
                }).start();
                break;
            case R.id.syn_post:
                break;
            case R.id.syn_post_form:
                break;
            case R.id.asyn_get:
                break;
            case R.id.asyn_post:
                break;
            case R.id.asyn_post_form:
                break;
        }
    }


}
