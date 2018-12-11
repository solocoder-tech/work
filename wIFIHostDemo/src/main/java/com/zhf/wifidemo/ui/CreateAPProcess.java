package com.zhf.wifidemo.ui;

import android.net.wifi.WifiManager;
import android.os.Message;
import com.zhf.wifidemo.wifi.MainActivity;
/**
 * 创建Wifi热点
 * @author ZHF
 *
 */
public class CreateAPProcess implements Runnable{
	
	public MainActivity context;
	
	public boolean running = false;
	private long startTime = 0L;
	private Thread thread = null;

	public CreateAPProcess(MainActivity context) {
		super();
		this.context = context;
	}

	@Override
	public void run() {
		while(true) {
			if(!running)  return;
			//WIFI_STATE_ENABLED 3 
			//WIFI_AP_STATE_ENABLED  13
			if((context.m_wiFiAdmin.getWifiApState() == WifiManager.WIFI_STATE_ENABLED)
					|| (context.m_wiFiAdmin.getWifiApState() == 13)
					|| (System.currentTimeMillis() - this.startTime >= 30000L)){
				//wifi可用 或 热点可用
				Message msg = context.mHandler.obtainMessage(context.m_nCreateAPResult);
				context.mHandler.sendMessage(msg);
			}
			try {
				Thread.sleep(5L);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	

	public void start() {
		try {
			thread = new Thread(this);
			running = true;
			startTime = System.currentTimeMillis();
			thread.start(); //开启线程
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void stop() {
		try {
			running = false;
			thread = null;
			startTime = 0L;
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
