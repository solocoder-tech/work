package com.example.mytakeout.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import com.example.mytakeout.utils.UIUtils;

import java.util.List;

/**
 * wifi 设置的公共方法
 * <p>
 * 关注的四个类：
 * 1.WifiManager  对wifi的统一管理，完成wifi的各种操作
 * 2.WifiInfo  描述当前连接wifi的热点信息
 * 3.WifiConfiguration  wifi的网络配置信息
 * 4.ScanResult  描述扫描出的Wifi热点信息
 * <p>
 * 需要权限
 * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
 * <uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>
 * 动态权限
 * <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
 * <p>
 * 专业名词：
 * SSID: 描述wifi热点的名称，就是搜索到的wifi名称
 * BSSID:
 * networkId:数字型id,唯一标示一个wifi
 * RSSI :描述wifi强弱值，level
 * <p>
 * WIFI 状态
 * WIFI_STATE_DISABLING | 0|wifi正在关闭  disabling
 * WIFI_STATE_DISABLED | 1|wifi关闭     disabled
 * WIFI_STATE_ENABLING | 2|wifi正在开启  enabling
 * WIFI_STATE_ENABLED | 3|wifi开启    enabled
 * WIFI_STATE_UNKNOWN | 4|wifi未知  unknown
 * <p>
 * 采用广播来监听wifi状态的变化
 * WifiManager.WIFI_STATE_CHANGED_ACTION 	wifi状态变化通知
 * WifiManager.SCAN_RESULTS_AVAILABLE_ACTION 	wifi扫描结果通知
 * WifiManager.SUPPLICANT_STATE_CHANGED_ACTION 	wifi连接结果通知
 * WifiManager.NETWORK_STATE_CHANGED_ACTION 	网络状态变化通知
 */
public class WifiUtils {
    //wifi状态
    public static final int WIFI_STATE_DISABLING = 0;
    public static final int WIFI_STATE_DISABLED = 1;
    public static final int WIFI_STATE_ENABLING = 2;
    public static final int WIFI_STATE_ENABLED = 3;
    public static final int WIFI_STATE_UNKNOWN = 4;

    //单例模式  饿汉式
    private static WifiUtils mWifiUtils = new WifiUtils();
    private static WifiManager mWifiManager;

    private WifiUtils() {
        mWifiManager = (WifiManager) UIUtils.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //注册广播接受wifi状态变化
    }

    public static WifiUtils getWifiUtils() {
        return mWifiUtils;
    }

    /**
     * 打开/关闭wifi
     *
     * @param b
     * @return 返回值代表操作是否成功，不代表wifi是否打开，打开wifi是一个耗时操作
     */
    public boolean setWifiEnable(boolean b) {
        boolean wifiEnabled = false;
        if (b) {
            if (!mWifiManager.isWifiEnabled()) {
                wifiEnabled = mWifiManager.setWifiEnabled(true);
            }
        } else {
            if (mWifiManager.isWifiEnabled()) {
                wifiEnabled = mWifiManager.setWifiEnabled(false);
            }
        }
        return wifiEnabled;
    }

    /**
     * wifi是否可用
     *
     * @return
     */
    public boolean isWifiEable() {
        return mWifiManager.isWifiEnabled();
    }

    /**
     * 获取扫描结果
     */
    public List<ScanResult> getScanResults() {
        return mWifiManager.getScanResults();
    }


    /**
     * 获取wifi状态 ， 5种
     *
     * @return
     */
    public int getWifiState() {
        return mWifiManager.getWifiState();
    }

    /**
     * 开始扫描,一定要注意动态权限问题
     */
    public void startScan() {
        mWifiManager.startScan();
    }
}
