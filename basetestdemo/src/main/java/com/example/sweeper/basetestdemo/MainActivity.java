package com.example.sweeper.basetestdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.btn_standard)
    Button btnStandard;
    @BindView(R.id.btn_singleTop)
    Button btnSingleTop;
    @BindView(R.id.btn_singleTask)
    Button btnSingleTask;
    @BindView(R.id.btn_singleInstance)
    Button btnSingleInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.btn_standard, R.id.btn_singleTop, R.id.btn_singleTask, R.id.btn_singleInstance})
    public void onViewClicked(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.btn_standard:
                intent = new Intent(this, StandardActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_singleTop:
                intent = new Intent(this, SingleTopActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_singleTask:
                break;
            case R.id.btn_singleInstance:

                break;
        }
    }
}
