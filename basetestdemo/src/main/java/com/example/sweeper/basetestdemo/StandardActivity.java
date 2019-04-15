package com.example.sweeper.basetestdemo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 创建时间：2019/4/4  16:15
 * 作者：5#
 * 描述：TODO
 */
public class StandardActivity extends BaseActivity {
    @BindView(R.id.btn_standard)
    Button btnStandard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standard);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_standard)
    public void onViewClicked() {
        startActivity(new Intent(this, StandardActivity.class));
    }
}
