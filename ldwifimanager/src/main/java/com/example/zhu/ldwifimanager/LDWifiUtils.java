package com.example.zhu.ldwifimanager;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;


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
 * <p>
 * wifi 连接
 * 如果wifi 已经保存就不需要输入密码，直接连接
 * 如果wifi 没有保存，就要自己构造WifiConfiguration对象，需要区分不同的加密方式
 */
public class LDWifiUtils {


    //单例模式  懒汉式
    private static LDWifiUtils mWifiUtils;
    private static WifiManager mWifiManager;

    private LDWifiUtils(Context context) {
        mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //注册广播接受wifi状态变化
    }

    public static LDWifiUtils initLDWifi(Context context) {
        if (mWifiUtils == null) {
            synchronized (LDWifiUtils.class) {
                if (mWifiUtils == null) {
                    mWifiUtils = new LDWifiUtils(context);
                }
            }
        }
        return mWifiUtils;
    }

    /**
     * 打开/关闭wifi
     *
     * @param isEnable
     * @return 返回值代表操作是否成功，不代表wifi是否打开，打开wifi是一个耗时操作
     */
    public boolean setWifiEnable(boolean isEnable) {
        boolean wifiEnabled = false;
        mWifiManager.setWifiEnabled(isEnable);
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
    public static List<ScanResult> getScanResults() {
        return mWifiManager.getScanResults();
    }


    /**
     * 获取wifi状态 ， 5种
     *
     * @return
     */
    public static int getWifiState() {
        return mWifiManager.getWifiState();
    }

    /**
     * 开始扫描,一定要注意动态权限问题
     */
    public void startScan() {
        mWifiManager.startScan();
    }

    /**
     * 获取扫描wifi的加密方式
     *
     * @return wifi的三种加密方式  wpa  wep 无密码
     */
    public String getWifiEncryptType(ScanResult scanResult) {
        String encryptType = "";
        if (scanResult.capabilities.contains("WPA") || scanResult.capabilities.contains("wpa")) {
            encryptType = "wpa";
        } else if (scanResult.capabilities.contains("WEP") || scanResult.capabilities.contains("wep")) {
            encryptType = "wep";
        } else {
            encryptType = "";
        }
        return encryptType;
    }

    /**
     * 连接wifi
     *
     * @param scanResult 要连接的热点
     *                   判断是否已经保存
     *                   保存，直接连接
     *                   不保存，创建WifiConfiguration 对象，注意不同的加密方式
     */
    public void connectWifi(ScanResult scanResult) {
        WifiConfiguration existConfig = getExistConfig(scanResult.SSID);
        if (existConfig != null) {
            int netId = mWifiManager.addNetwork(existConfig);
            //WifiManager的enableNetwork接口，就可以连接到netId对应的wifi了
            //其中boolean参数，主要用于指定是否需要断开其它Wifi网络
            mWifiManager.enableNetwork(netId, true);
        } else {
            String wifiEncryptType = getWifiEncryptType(scanResult);
            WifiConfiguration configuration = createConfiguration(scanResult.SSID, wifiEncryptType, "");
            int netId = mWifiManager.addNetwork(configuration);
            mWifiManager.enableNetwork(netId, true);
        }

    }

    /**
     * 获取当前连接的wifi信息
     *
     * @return
     */
    public WifiInfo getCurrentWifi() {
        return mWifiManager.getConnectionInfo();
    }

    /**
     * 创建 WifiConfiguration 对象，注意不同的加密类型
     *
     * @param ssid
     * @param encryptionType
     * @param password
     * @return
     */
    public WifiConfiguration createConfiguration(String ssid, String encryptionType, String password) {
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = "\"" + ssid + "\"";

        if (encryptionType.contains("wep")) {
            int i = password.length();
            if (((i == 10 || (i == 26) || (i == 58))) && (password.matches("[0-9A-Fa-f]*"))) {
                config.wepKeys[0] = password;
            } else {
                config.wepKeys[0] = "\"" + password + "\"";
            }
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (encryptionType.contains("wpa")) {
            config.preSharedKey = "\"" + password + "\"";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        } else {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        return config;
    }


    private WifiConfiguration getExistConfig(String ssid) {
        //返回所有已经保存的wifi
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        if (existingConfigs != null) {
            for (WifiConfiguration existingConfig : existingConfigs) {
                if (ssid.equals(existingConfig.SSID))
                    return existingConfig;
            }
        }
        return null;
    }

}
