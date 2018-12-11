package com.zhf.wifidemo.ui;

import android.os.Message;

import com.zhf.wifidemo.wifi.MainActivity;

/**
 * Wifi搜索进度
 * @author ZHF
 *
 */
public class WFSearchProcess implements Runnable {
	
	public MainActivity context;
	public WFSearchProcess(MainActivity context) {
		this.context = context;
	}

	public boolean running = false;
	private long startTime = 0L;
	private Thread thread  = null;
			
	@Override
	public void run() {
		while(true) {
			//是否
			if(!running) return;
			if(System.currentTimeMillis() - startTime >= 30000L) {
				//发送（搜索超时）消息
				Message msg = context.mHandler.obtainMessage(context.m_nWifiSearchTimeOut);
				context.mHandler.sendMessage(msg);
			}
			try {
				Thread.sleep(10L);
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
			// TODO: handle exception
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
