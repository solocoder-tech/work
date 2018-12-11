package com.example.zhu.ldwifimanager;

import android.net.wifi.WifiManager;

/**
 * 创建时间：2018/12/11  19:23
 * 作者：5#
 * 描述：wifi状态
 */
public interface LDWifiState {
    //wifi状态
    public static final int WIFI_STATE_DISABLING = 0;
    public static final int WIFI_STATE_DISABLED = 1;
    public static final int WIFI_STATE_ENABLING = 2;
    public static final int WIFI_STATE_ENABLED = 3;
    public static final int WIFI_STATE_UNKNOWN = 4;

}
