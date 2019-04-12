package com.example.serverudp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {
	TextView label;
	Socket socket = null;
	static DatagramSocket udpSocket = null;
	static DatagramPacket udpPacket = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		label = (TextView) findViewById(R.id.label);
        label.append("\n");
		new Thread(new Runnable() {

			@Override
			public void run() {
				byte[] data = new byte[256];
				try {
					udpSocket = new DatagramSocket(43708);
					udpPacket = new DatagramPacket(data, data.length);
				} catch (SocketException e1) {
					e1.printStackTrace();
				}
				while (true) {
					try {
						udpSocket.receive(udpPacket);
					} catch (Exception e) {
					}
					if (null != udpPacket.getAddress()) {
						final String quest_ip = udpPacket.getAddress().toString();
						final String codeString = new String(data, 0, udpPacket.getLength());
						label.post(new Runnable() {

							@Override
							public void run() {
								label.append("收到来自：" + quest_ip + "UDP请求。。。\n");
								label.append("请求内容：" + codeString + "\n\n");

							}
						});
						try {
							final String ip = udpPacket.getAddress().toString()
									.substring(1);
							label.post(new Runnable() {

								@Override
								public void run() {
									label.append("发送socket请求到：" + ip + "\n");

								}
							});
							socket = new Socket(ip, 8080);

						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							try {
								if (null != socket) {
									socket.close();
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}

			}
		}).start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
 @Override
public void onBackPressed() {
	 udpSocket.close();
	super.onBackPressed();
}
}
