package com.zhf.wifidemo.broadcast;

import java.util.ArrayList;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;
/**
 * 监听wifi热点变化
 * @author ZHF
 *
 */
public class WIFIBroadcast extends BroadcastReceiver{

	public static ArrayList<EventHandler> ehList = new ArrayList<EventHandler>();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		//搜索到wifi热点结果的广播:  "android.net.wifi.SCAN_RESULTS"
		if(intent.getAction().endsWith(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
			Log.i("WTScanResults---->扫描到了可用网络---->", "android.net.wifi.SCAN_RESULTS");
			//遍历通知各个监听接口
			for(int i = 0; i < ehList.size(); i++) {
				((EventHandler)ehList.get(i)).scanResultsAvaiable();
			}
			
		//wifi打开或关闭状态变化   "android.net.wifi.WIFI_STATE_CHANGED"
		}else if(intent.getAction().endsWith(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
			Log.i("WTScanResults----->wifi状态变化--->", "android.net.wifi.WIFI_STATE_CHANGED");
			//这里不需要连接一个SSID（wifi名称）
			for(int j = 0; j < ehList.size(); j++) {
				((EventHandler)ehList.get(j)).wifiStatusNotification();
			}
			
		//连接上一个SSID后发出的广播，(注：与android.net.wifi.WIFI_STATE_CHANGED的区别)  
		}else if(intent.getAction().endsWith(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
			Log.i("WTScanResults----->网络状态变化---->", "android.net.wifi.STATE_CHANGE");
			for(int m = 0; m < ehList.size(); m++) {
				((EventHandler)ehList.get(m)).handleConnectChange();
			}
		}
	}
	/**
	 * 事件监听接口
	 * @author ZHF
	 *
	 */
	public static abstract interface EventHandler {
		/**处理连接变化事件**/
		public abstract void handleConnectChange();
		/**扫描结果是有效事件**/
		public abstract void scanResultsAvaiable();
		/**wifi状态变化事件**/
		public abstract void wifiStatusNotification();
	}
	
}
