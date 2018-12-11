package com.zhf.wifidemo.wifi;

import java.lang.reflect.Method;
import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.zhf.wifidemo.R;
import com.zhf.wifidemo.broadcast.WIFIBroadcast;
import com.zhf.wifidemo.broadcast.WIFIBroadcast.EventHandler;
import com.zhf.wifidemo.data.WFOperateEnum;
import com.zhf.wifidemo.ui.CreateAPProcess;
import com.zhf.wifidemo.ui.GifView;
import com.zhf.wifidemo.ui.WFSearchAnimationFrameLayout;
import com.zhf.wifidemo.ui.WFSearchProcess;
import com.zhf.wifidemo.wifi.utils.WifiAdmin;

/**
 * 主界面
 * @author ZHF
 * 
 */
public class MainActivity extends Activity implements EventHandler{
	//消息事件
	public static final int m_nWifiSearchTimeOut = 0;// 搜索超时
	public static final int m_nWTScanResult = 1;// 搜索到wifi返回结果
	public static final int m_nWTConnectResult = 2;// 连接上wifi热点
	public static final int m_nCreateAPResult = 3;// 创建热点结果
	public static final int m_nUserResult = 4;// 用户上线人数更新命令(待定)
	public static final int m_nWTConnected = 5;// 点击连接后断开wifi，3.5秒后刷新adapter
	
	//一些常量
	public static final String PACKAGE_NAME = "com.zhf.wifidemo.wifi";  //应用包名
	public static final String FIRST_OPEN_KEY = "version";  //版本号信息
	public static final String WIFI_AP_HEADER = "zhf_";
	public static final String WIFI_AP_PASSWORD ="zhf12345";
	//wifi操作事件（枚举）-->方便弹出对话框处理事件
	private int wFOperateEnum = WFOperateEnum.NOTHING;
	
	//三个重要的类
	public WFSearchProcess m_wtSearchProcess; //WiFi搜索进度条线程
	public WifiAdmin m_wiFiAdmin; //Wifi管理类
	public CreateAPProcess m_createAPProcess; //创建Wifi热点线程
	
	//相关控件
	private WFSearchAnimationFrameLayout m_FrameLWTSearchAnimation;  //自定义雷达动画布局
	private GifView m_gifRadar;  //wifi信号动画布局
	
	private LinearLayout m_LinearLIntroduction; //第一次打开应用程序介绍 
	
	private LinearLayout m_linearLCreateAP; //创建热点View
	private ProgressBar m_progBarCreatingAP; //创建热点进度条
	private TextView m_textVPromptAP; //创建热点进度条文字
	
	private Button m_btnBack; //左上角返回按钮
	private Button m_btnSearchWF; //右上角wifi搜索按钮
	private Button m_btnCreateWF; //创建wifi热点
	private ListView m_listVWT; //显示信息
	
	private LinearLayout m_LinearLDialog; //提醒对话框
	private TextView m_textVContentDialog;  //对话框文本内容
	private Button m_btnConfirmDialog, m_btnCancelDialog; //提醒对话框上的按钮
	
	private TextView m_textVWTPrompt; //中间文字提示
	
	ArrayList<ScanResult> m_listWifi = new ArrayList();//检测到热点信息列表
	private WTAdapter m_wTAdapter; //网络列表适配器

	public Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case m_nWifiSearchTimeOut: // 搜索超时
				m_wtSearchProcess.stop();
				m_FrameLWTSearchAnimation.stopAnimation();
				m_listWifi.clear();  //网络列表
				//设置控件
				m_textVWTPrompt.setVisibility(View.VISIBLE);
				m_textVWTPrompt.setText("需要重新搜索，点右上角重新搜索或创建新的热点...");
				break;
				
			case m_nWTScanResult:  //扫描到结果
				m_listWifi.clear();
				if(m_wiFiAdmin.mWifiManager.getScanResults() != null) {
					for (int i = 0; i < m_wiFiAdmin.mWifiManager.getScanResults().size(); i++) {
						ScanResult scanResult = m_wiFiAdmin.mWifiManager.getScanResults().get(i);
						//和指定连接热点比较，将其他的过滤掉！
						if(scanResult.SSID.startsWith(WIFI_AP_HEADER)) {
							m_listWifi.add(scanResult);
						}
					}
					if(m_listWifi.size() > 0) {
						m_wtSearchProcess.stop();
						m_FrameLWTSearchAnimation.stopAnimation();
						m_textVWTPrompt.setVisibility(View.GONE);
						//更新列表，显示出搜索到的热点
						m_wTAdapter.setData(m_listWifi); 
						m_wTAdapter.notifyDataSetChanged();
					}
				}
				break;
			case m_nWTConnectResult:  //连接结果
				m_wTAdapter.notifyDataSetChanged(); //刷新适配器数据
				break;
			case m_nCreateAPResult:  //创建wifi热点结果
				m_createAPProcess.stop();
				m_progBarCreatingAP.setVisibility(View.GONE); //旋转进度条
				if((m_wiFiAdmin.getWifiApState() == 3 || m_wiFiAdmin.getWifiApState() == 13) && (m_wiFiAdmin.getApSSID().startsWith(WIFI_AP_HEADER))) {
					//设置控件
					m_textVWTPrompt.setVisibility(View.GONE);
					m_linearLCreateAP.setVisibility(View.VISIBLE);
					m_btnCreateWF.setVisibility(View.VISIBLE);
					m_gifRadar.setVisibility(View.VISIBLE);
					m_btnCreateWF.setBackgroundResource(R.drawable.x_ap_close);
					
					m_textVPromptAP.setText("热点创建成功！"+ "\n热点名："+ m_wiFiAdmin.getApSSID()+ "\n连接密码：zhf12345");
				} else {
					m_btnCreateWF.setVisibility(View.VISIBLE);
					m_btnCreateWF.setBackgroundResource(R.drawable.x_wt_create);
					m_textVPromptAP.setText("热点创建失败，您可以重新创建或者搜索其它热点");
				}
				break;
			case m_nUserResult :
				//更新用户上线人数
				break;
			case m_nWTConnected:  //点击连接后断开wifi，3.5s后刷新
				m_wTAdapter.notifyDataSetChanged();
				break;
			
			}

		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wt_main);
		//搜索Wifi
		m_wtSearchProcess = new WFSearchProcess(this);
		//创建Wifi热点
		m_createAPProcess = new CreateAPProcess(this);
		//wifi管理类
		m_wiFiAdmin  = WifiAdmin.getInstance(this);
		
		//初始化View
		initView();
	}
	
	/**初始化View**/
	private void initView() {
		// 监听广播
		WIFIBroadcast.ehList.add(this);
		
		/******************************实例化布局**************************************/
		m_linearLCreateAP = (LinearLayout) findViewById(R.id.create_ap_llayout_wt_main);  //创建热点View
		m_progBarCreatingAP = (ProgressBar) findViewById(R.id.creating_progressBar_wt_main);  //创建热点进度条
		m_textVPromptAP = (TextView) findViewById(R.id.prompt_ap_text_wt_main); //创建热点进度条文字
		
		m_FrameLWTSearchAnimation = ((WFSearchAnimationFrameLayout) findViewById(R.id.search_animation_wt_main));// 搜索时的动画
		m_listVWT = ((ListView) findViewById(R.id.wt_list_wt_main));// 搜索到的热点listView
		//注意此处
		m_wTAdapter = new WTAdapter(this, m_listWifi);
		m_listVWT.setAdapter(m_wTAdapter);

		m_textVWTPrompt = (TextView) findViewById(R.id.wt_prompt_wt_main); //中间提醒文字
		m_gifRadar = (GifView) findViewById(R.id.radar_gif_wt_main); //gif动画

		//提醒对话框布局
		m_LinearLDialog = (LinearLayout) findViewById(R.id.dialog_layout_wt_main);
		m_textVContentDialog = (TextView) findViewById(R.id.content_text_wtdialog);
		m_btnConfirmDialog = (Button) findViewById(R.id.confirm_btn_wtdialog);
		m_btnCancelDialog = (Button) findViewById(R.id.cancel_btn_wtdialog);
		
		//左上角返回键
		m_btnBack = (Button) findViewById(R.id.back_btn_wt_main); 
		m_btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed(); //相当于调用系统返回键，结束当前Activity
			}
		});
		
		//右上角搜索热点按钮
		m_btnSearchWF = (Button) findViewById(R.id.search_btn_wt_main);
		m_btnSearchWF.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!m_wtSearchProcess.running) { //搜索线程没有开启
					//1.当前热点或wifi连接着    WIFI_STATE_ENABLED 3 //WIFI_AP_STATE_ENABLED  13
					if(m_wiFiAdmin.getWifiApState() == 3 || m_wiFiAdmin.getWifiApState() == 13) {
						wFOperateEnum = WFOperateEnum.SEARCH; //搜索wifi事件
						m_LinearLDialog.setVisibility(View.VISIBLE); ///wifi提示对话框显示
						m_textVContentDialog.setText("是否关闭当前热点去搜索其他热点？");
						return;  //跳出此方法，交由对话框来处理事件
					}
					//2.当前没有热点或wifi连接着
					if(!m_wiFiAdmin.mWifiManager.isWifiEnabled()) { //如果wifi没打开
						m_wiFiAdmin.OpenWifi();
					}
					m_textVWTPrompt.setVisibility(View.VISIBLE); //中间提示文字
					m_textVWTPrompt.setText("正在搜索附近的热点...");
					m_linearLCreateAP.setVisibility(View.GONE); //创建wifi热点布局消失
					m_gifRadar.setVisibility(View.GONE); //热点连接动画消失
					m_btnCreateWF.setBackgroundResource(R.drawable.x_wt_create); //更改按钮文字“创建”
					//开始搜索wifi
					m_wiFiAdmin.startScan();
					m_wtSearchProcess.start(); //开启搜索线程
					m_FrameLWTSearchAnimation.startAnimation(); //开启波纹动画
				}else {//搜索线程开启着，再次点击按钮
					//重新启动
					m_wtSearchProcess.stop();
					m_wiFiAdmin.startScan(); 	//开始搜索wifi
					m_wtSearchProcess.start();
				}
			}
		});
		
		//中间创建wifi热点按钮
		m_btnCreateWF = (Button) findViewById(R.id.create_btn_wt_main);
		m_btnCreateWF.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(m_wiFiAdmin.getWifiApState() == 4) { // WIFI_STATE_UNKNOWN
					Toast.makeText(getApplicationContext(),"您的设备不支持热点创建!", Toast.LENGTH_SHORT).show();
					return;
				}
				if(m_wiFiAdmin.mWifiManager.isWifiEnabled()) { //目前连着wifi
					wFOperateEnum = WFOperateEnum.CREATE;  //wifi热点创建事件
					m_LinearLDialog.setVisibility(View.VISIBLE); //对话框可用
					m_textVContentDialog.setText("创建热点会关闭当前的WiFi，确认继续？");
					return;
				}
				if((m_wiFiAdmin.getWifiApState() == 3 || m_wiFiAdmin.getWifiApState() == 13)
						&& (!m_wiFiAdmin.getApSSID().startsWith(WIFI_AP_HEADER))) {//目前连接着其他热点名
					wFOperateEnum = WFOperateEnum.CREATE;  //wifi热点创建事件
					m_LinearLDialog.setVisibility(View.VISIBLE);
					m_textVContentDialog.setText("系统热点被占用，点确定开启热点以传输文件！");
					return;
				}
				if (((m_wiFiAdmin.getWifiApState() == 3) || (m_wiFiAdmin.getWifiApState() == 13))
						&& (m_wiFiAdmin.getApSSID().startsWith(WIFI_AP_HEADER))) {//目前连接着自己指定的Wifi热点
					wFOperateEnum = WFOperateEnum.CLOSE;  //wifi热点关闭事件
					m_LinearLDialog.setVisibility(View.VISIBLE);
					m_textVContentDialog.setText("关闭热点会中断当前传输，您确定这么做？");
					return;
				}
				if(m_wtSearchProcess.running) {
					m_wtSearchProcess.stop(); //停止线程
					m_FrameLWTSearchAnimation.stopAnimation(); //停止动画
				}
				
				/******************点击创建热点时没有连接wifi或热点的情况*****************************/
				//关闭Wifi
				m_wiFiAdmin.closeWifi(); 
				//创建热点（名字，密码，加密类型,wifi/ap类型）
				m_wiFiAdmin.createWifiAP(m_wiFiAdmin.createWifiInfo(WIFI_AP_HEADER + getLocalHostName(), WIFI_AP_PASSWORD, 3, "ap"), true);
				m_createAPProcess.start(); //开启创建热点线程
				
				//将wifi信息列表设置到listview中
				m_listWifi.clear();
				m_wTAdapter.setData(m_listWifi); 
				m_wTAdapter.notifyDataSetChanged();
				//设置布局
				m_linearLCreateAP.setVisibility(View.VISIBLE); //热点布局可用
				m_progBarCreatingAP.setVisibility(View.VISIBLE);
				m_textVPromptAP.setText("正在创建热点"); //进度条文字
				m_btnCreateWF.setVisibility(View.GONE); //点击一次不可再点
				m_textVWTPrompt.setVisibility(View.GONE);
			}
		});
	
		//对话框确认按钮
		m_btnConfirmDialog.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				m_LinearLDialog.setVisibility(View.GONE); //让对话框布局消失
				switch (wFOperateEnum) { //根据wifi操作事件
				case WFOperateEnum.CLOSE:  //关闭wifi热点
					//设置布局
					m_textVWTPrompt.setVisibility(View.VISIBLE);
					m_textVWTPrompt.setText("热点已关闭！可以进行其他操作了！"); //中间提醒文字
					m_btnCreateWF.setBackgroundResource(R.drawable.x_wt_create); //按钮文字改回“创建”
					m_gifRadar.setVisibility(View.GONE); //热点动画停止
					m_linearLCreateAP.setVisibility(View.GONE); //下部创建热点布局不可用
					
					//关闭热点
					m_wiFiAdmin.createWifiAP(m_wiFiAdmin.createWifiInfo(m_wiFiAdmin.getApSSID(), "zhf123456", 3, "ap"), false);
					break;
				case WFOperateEnum.CREATE:  //创建wifi热点
					if(m_wtSearchProcess.running) {
						m_wtSearchProcess.stop();  //搜索wifi线程停止
						m_FrameLWTSearchAnimation.stopAnimation(); //搜索wifi动画停止
					}
					//关闭wifi
					m_wiFiAdmin.closeWifi();
					//创建WiFi热点
					m_wiFiAdmin.createWifiAP(m_wiFiAdmin.createWifiInfo(WIFI_AP_HEADER + getLocalHostName(), WIFI_AP_PASSWORD, 3, "ap"),true);
					m_createAPProcess.start();
					//刷新listView布局
					m_listWifi.clear();
					m_wTAdapter.setData(m_listWifi);
					m_wTAdapter.notifyDataSetChanged();
					//设置布局
					m_linearLCreateAP.setVisibility(View.VISIBLE);
					m_progBarCreatingAP.setVisibility(View.VISIBLE); //旋转进度条
					m_btnCreateWF.setVisibility(View.GONE);
					m_textVWTPrompt.setVisibility(View.GONE);
					m_textVPromptAP.setText("正在创建热点..."); //进度条文字
					break;
				case WFOperateEnum.SEARCH:  //搜索可用热点
					//设置布局
					m_textVWTPrompt.setVisibility(View.VISIBLE);
					m_textVWTPrompt.setText("正在搜索附近的热点...");
					m_linearLCreateAP.setVisibility(View.GONE); //创建热点布局不可用
					m_btnCreateWF.setVisibility(View.VISIBLE);
					m_btnCreateWF.setBackgroundResource(R.drawable.x_wt_create); //按钮文字改回“创建”
					m_gifRadar.setVisibility(View.GONE); //热点动画停止
					m_linearLCreateAP.setVisibility(View.GONE); //下部创建热点布局不可用
					
					//搜索热点线程关闭
					if (m_createAPProcess.running)
						m_createAPProcess.stop();
					//关闭热点
					m_wiFiAdmin.createWifiAP(m_wiFiAdmin.createWifiInfo(m_wiFiAdmin.getApSSID(), WIFI_AP_PASSWORD, 3, "ap"),false);
					//打开wifi
					m_wiFiAdmin.OpenWifi();
					m_wtSearchProcess.start();
					m_FrameLWTSearchAnimation.startAnimation();
					
					break;
				}
			}
		});
		//对话框取消按钮事件
		m_btnCancelDialog.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//对话框布局消失
				m_LinearLDialog.setVisibility(View.GONE);
			}
		});
	}
	
	/**Wifi是否连接**/
	private boolean isWifiConnect() {
		boolean isConnect = true;
		//用于网络连接状态的检测
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if(!cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) 
			isConnect = false;
		return isConnect;
	}

	/**获取wifi热点状态**/
	public boolean getWifiApState() {
		try {
			WifiManager localWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			Method m = localWifiManager.getClass().getMethod("getWifiApState", new Class[0]);
			int i = (Integer)(m.invoke(localWifiManager, new Object[0]));
			return (3 == i) || (13 == i);  //WIFI_STATE_ENABLED 3  //WIFI_AP_STATE_ENABLED  13 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**获取手机信息**/
	public String getLocalHostName() {
		String str1 = Build.BRAND; //主板
		String str2 = Build.MODEL;  //机型
		if (-1 == str2.toUpperCase().indexOf(str1.toUpperCase()))
			str2 = str1 + "_" + str2;
		return str2;
	}
	
	private void init() {
		//线程是否在运行
		if(this.m_wtSearchProcess.running || this.m_createAPProcess.running) {
			return;
		}
		//没有连接上wifi或者是wifi热点
		if(!isWifiConnect() && !getWifiApState()) {
			m_wiFiAdmin.OpenWifi();
			m_wtSearchProcess.start(); //开启搜索wifi超时检测线程
			m_wiFiAdmin.startScan(); //开启搜索wifi
			//开启搜索动画
			m_FrameLWTSearchAnimation.startAnimation();
			
			//设置控件
			m_textVWTPrompt.setVisibility(View.VISIBLE);
			m_textVWTPrompt.setText(" 正在搜索附近的热点...");
			m_linearLCreateAP.setVisibility(View.GONE);
			m_btnCreateWF.setBackgroundResource(R.drawable.x_wt_create);
		}
		//连接上wifi
		if(isWifiConnect()) {
			this.m_wiFiAdmin.startScan(); 
			this.m_wtSearchProcess.start();
			this.m_FrameLWTSearchAnimation.startAnimation();
			//设置控件
			this.m_textVWTPrompt.setVisibility(0);
			this.m_textVWTPrompt.setText("正在搜索附近的热点...");
			this.m_linearLCreateAP.setVisibility(View.GONE);
			this.m_btnCreateWF.setBackgroundResource(R.drawable.x_wt_create);
			this.m_gifRadar.setVisibility(View.GONE);
			
			m_listWifi.clear();
			if(m_wiFiAdmin.mWifiManager.getScanResults() != null) {
				for (int i = 0; i < m_wiFiAdmin.mWifiManager.getScanResults().size(); i++) {
					//识别出自己自定连接的wifi
					if(m_wiFiAdmin.mWifiManager.getScanResults().get(i).SSID.startsWith(WIFI_AP_HEADER)) {
						m_listWifi.add(m_wiFiAdmin.mWifiManager.getScanResults().get(i)); //将指定wifi添加进去
					}
				}
				m_wTAdapter.setData(m_listWifi); //将连接的信息添加到listView中
				m_wTAdapter.notifyDataSetChanged();
			}
			//连接上wifi热点
			if(getWifiApState()){
				if(m_wiFiAdmin.getApSSID().startsWith(WIFI_AP_HEADER)) {
					//设置控件
					m_textVWTPrompt.setVisibility(View.GONE);
					m_linearLCreateAP.setVisibility(View.VISIBLE);
					m_progBarCreatingAP.setVisibility(View.GONE);
					m_btnCreateWF.setVisibility(View.VISIBLE);
					m_gifRadar.setVisibility(View.VISIBLE);
					m_btnCreateWF.setBackgroundResource(R.drawable.x_ap_close);
					m_textVPromptAP.setText("\n热点名："+ m_wiFiAdmin.getApSSID() + "\n连接密码：zhf12345");
				}
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		WIFIBroadcast.ehList.remove(this);
	}
	

	@Override
	public void handleConnectChange() {
		Message msg = mHandler.obtainMessage(m_nWTConnectResult);
		mHandler.sendMessage(msg);
	}

	@Override
	public void scanResultsAvaiable() {
		Message msg = mHandler.obtainMessage(m_nWTScanResult);
		mHandler.sendMessage(msg);
	}

	@Override
	public void wifiStatusNotification() {
		m_wiFiAdmin.mWifiManager.getWifiState(); //获取当前wifi状态
	}
}
