//package com.example.mytakeout.ui.activity;
//
//import android.net.wifi.ScanResult;
//import android.view.View;
//import android.widget.Button;
//
//import com.example.mytakeout.R;
//import com.example.mytakeout.base.BaseActivity;
//import com.example.mytakeout.modle.net.bean.EventScanResult;
//import com.example.mytakeout.utils.LogUtils;
//
//import java.util.List;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//
//public class WiFiActivty extends BaseActivity {
//    @BindView(R.id.open_wifi)
//    Button openWifiBtn;
//    @BindView(R.id.close_wifi)
//    Button closeWifiBtn;
//    @BindView(R.id.scan_wifi)
//    Button scanWifiBtn;
//
//    private WifiUtils mWifiUtils;
//
//    @Override
//    protected void initViews() {
//        setCustomView(R.layout.activity_wifi, true,"WIFI");
//        ButterKnife.bind(this);
//
//    }
//
//    @Override
//    protected void initDatas() {
//        mWifiUtils = WifiUtils.getWifiUtils();
//    }
//
//    @Override
//    protected void initEvents() {
//
//    }
//
//
//    @OnClick({R.id.open_wifi, R.id.close_wifi, R.id.scan_wifi})
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.open_wifi:
//                mWifiUtils.setWifiEnable(true);
//                break;
//            case R.id.close_wifi:
//                mWifiUtils.setWifiEnable(false);
//                break;
//            case R.id.scan_wifi:
//                toast("扫描");
//                mWifiUtils.startScan();
//                break;
//        }
//    }
//
//    public void getEventMsg(EventScanResult eventScanResult) {
//        List<ScanResult> scanResults = eventScanResult.getScanResults();
//        for (ScanResult scanResult : scanResults) {
//            StringBuffer sb = new StringBuffer();
//            sb.append("ssid=" + scanResult.SSID + "\n");
//            sb.append("capabilities=" + scanResult.capabilities + "\n");
//            sb.append("BSSID=" + scanResult.BSSID + "\n");
//            sb.append("frequency=" + scanResult.frequency + "");
//            LogUtils.e(sb.toString());
//
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }
//}
