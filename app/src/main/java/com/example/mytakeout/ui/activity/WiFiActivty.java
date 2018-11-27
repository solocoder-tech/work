package com.example.mytakeout.ui.activity;

import android.view.View;
import android.widget.Button;

import com.example.mytakeout.R;
import com.example.mytakeout.base.BaseActivity;
import com.example.mytakeout.wifi.WifiUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WiFiActivty extends BaseActivity {
    @BindView(R.id.open_wifi)
    Button openWifiBtn;
    @BindView(R.id.close_wifi)
    Button closeWifiBtn;
    @BindView(R.id.scan_wifi)
    Button scanWifiBtn;

    private WifiUtils mWifiUtils;

    @Override
    protected void initViews() {
        setCustomView(R.layout.activity_wifi, true);
        ButterKnife.bind(this);
    }

    @Override
    protected void initDatas() {
        mWifiUtils = WifiUtils.getWifiUtils();
    }


    @OnClick({R.id.open_wifi, R.id.close_wifi, R.id.scan_wifi})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.open_wifi:
                mWifiUtils.setWifiEnable(true);
                break;
            case R.id.close_wifi:
                mWifiUtils.setWifiEnable(false);
                break;
            case R.id.scan_wifi:
                toast("扫描");
                mWifiUtils.startScan();
                break;
        }
    }
}
