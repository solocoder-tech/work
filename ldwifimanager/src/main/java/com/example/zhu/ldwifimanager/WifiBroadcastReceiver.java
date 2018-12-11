package com.example.zhu.ldwifimanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;


import java.util.List;

/**
 * 接受wifi状态变化的广播，发送者是系统
 */
public class WifiBroadcastReceiver extends BroadcastReceiver {

    private List<ScanResult> mScanResults;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case WifiManager.WIFI_STATE_CHANGED_ACTION:
                //状态变化
                int wifiState = LDWifiUtils.getWifiState();
                switch (wifiState) {
                    case LDWifiState.WIFI_STATE_DISABLING:
                        break;
                    case LDWifiState.WIFI_STATE_DISABLED:
                        break;
                    case LDWifiState.WIFI_STATE_ENABLED:
                        break;
                    case LDWifiState.WIFI_STATE_ENABLING:
                        break;
                    case LDWifiState.WIFI_STATE_UNKNOWN:
                        break;
                }
                break;
            case WifiManager.SCAN_RESULTS_AVAILABLE_ACTION:
                //扫描结果是否可用，不可用是=时getScanResults()为null
                boolean isScanned = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, true);
                if (isScanned) {
                    //扫描完成
                    if (mScanResults != null) {
                        mScanResults.clear();
                    }
                    mScanResults = LDWifiUtils.getScanResults();
                    //构造传递对象
//                    EventScanResult eventScanResult = new EventScanResult();
//                    eventScanResult.setScanResults(mScanResults);
//                    EventBus.getDefault().postSticky(eventScanResult);
                }
                break;
            case WifiManager.SUPPLICANT_STATE_CHANGED_ACTION:
                break;
            case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                break;

        }
    }
}
