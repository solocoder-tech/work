package com.example.mytakeout.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import com.example.mytakeout.utils.LogUtils;
import com.example.mytakeout.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 接受wifi状态变化的广播，发送者是系统
 */
public class WifiBroadcastReceiver extends BroadcastReceiver {

    private List<ScanResult> mScanResults = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case WifiManager.WIFI_STATE_CHANGED_ACTION:
                //状态变化
                int wifiState = WifiUtils.getWifiUtils().getWifiState();
                switch (wifiState) {
                    case WifiUtils.WIFI_STATE_DISABLING:
                        LogUtils.e("===wifi关闭中====");
                        break;
                    case WifiUtils.WIFI_STATE_DISABLED:
                        LogUtils.e("===wifi关闭====");
                        break;
                    case WifiUtils.WIFI_STATE_ENABLED:
                        LogUtils.e("===wifi打开====");
                        break;
                    case WifiUtils.WIFI_STATE_ENABLING:
                        LogUtils.e("===wifi打开中====");
                        break;
                    case WifiUtils.WIFI_STATE_UNKNOWN:
                        LogUtils.e("===wifi未知状态====");
                        break;
                }
                break;
            case WifiManager.SCAN_RESULTS_AVAILABLE_ACTION:
                //扫描完成
                mScanResults.clear();
                mScanResults = WifiUtils.getWifiUtils().getScanResults();
                for (ScanResult scanResult : mScanResults) {
                    LogUtils.e("==SSID" + scanResult.SSID + "==BSSID==" + scanResult.BSSID);
                    if ("app".equals(scanResult.SSID)) {
                        break;
                    }
                }
                break;
            case WifiManager.SUPPLICANT_STATE_CHANGED_ACTION:
                break;
            case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                break;

        }
    }
}
