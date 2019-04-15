package com.example.sendudpbroadcasttest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static String LOG_TAG = "WifiBroadcastActivity";
	private boolean start = true;
	private EditText IPAddress;
	private String address;
	public static final int DEFAULT_PORT = 7913;
	private static final int MAX_DATA_PACKET_LENGTH = 40;
	private byte[] buffer = new byte[MAX_DATA_PACKET_LENGTH];
	Button startButton;
	Button stopButton;
	TextView label;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		IPAddress = (EditText) this.findViewById(R.id.address);
		startButton = (Button) this.findViewById(R.id.start);
		stopButton = (Button) this.findViewById(R.id.stop);
		label = (TextView) this.findViewById(R.id.label);
		startButton.setEnabled(true);
		stopButton.setEnabled(false);
		new Thread(new TcpReceive()).start();
		address = getLocalIPAddress();
		if (address != null) {
			IPAddress.setText(address);
			Log.e("TcpReceive",address);
		} else {
			IPAddress.setText("Can not get IP address");

			return;
		}
		startButton.setOnClickListener(listener);
		stopButton.setOnClickListener(listener);
	}

	private View.OnClickListener listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			label.setText("");
			if (v == startButton) {
				start = true;

				new BroadCastUdp(IPAddress.getText().toString()).start();
				startButton.setEnabled(false);
				stopButton.setEnabled(true);
			} else if (v == stopButton) {
				start = false;
				startButton.setEnabled(true);
				stopButton.setEnabled(false);
			}
		}
	};

	private String getLocalIPAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e(LOG_TAG, ex.toString());
		}
		return null;
	}

	public class BroadCastUdp extends Thread {
		private String dataString;
		private DatagramSocket udpSocket;

		public BroadCastUdp(String dataString) {
			this.dataString = dataString;
		}

		public void run() {
			DatagramPacket dataPacket = null;

			try {
				udpSocket = new DatagramSocket(DEFAULT_PORT);

				//定义用来接收数据的DatagramPacket实例
				//没有设置端口号，所以是接收数据，要是设置了端口号和主机就是发送数据
				dataPacket = new DatagramPacket(buffer, MAX_DATA_PACKET_LENGTH);
				byte[] data = dataString.getBytes();
				dataPacket.setData(data);
				dataPacket.setLength(data.length);
				dataPacket.setPort(DEFAULT_PORT);

				InetAddress broadcastAddr;

				broadcastAddr = InetAddress.getByName("255.255.255.255");
				dataPacket.setAddress(broadcastAddr);
			} catch (Exception e) {
				Log.e(LOG_TAG, e.toString());
			}
			// while( start ){
			try {
				udpSocket.send(dataPacket);
				sleep(10);
			} catch (Exception e) {
				Log.e(LOG_TAG, e.toString());
			}
			// }

			udpSocket.close();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private class TcpReceive implements Runnable {
		public void run() {
			while (true) {
				Socket socket = null;
				ServerSocket ss = null;
				BufferedReader in = null;
				try {
					Log.i("TcpReceive", "ServerSocket +++++++");
					ss = new ServerSocket(7913);

					socket = ss.accept();

					Log.i("TcpReceive", "connect +++++++");
					if (socket != null) {
						in = new BufferedReader(new InputStreamReader(
								socket.getInputStream()));

						StringBuilder sb = new StringBuilder();
						sb.append(socket.getInetAddress().getHostAddress());
						String line = null;
						while ((line = in.readLine()) != null) {
							sb.append(line);
						}
						Log.i("TcpReceive", "connect :" + sb.toString());

						final String ipString =sb.toString().trim();// "192.168.0.104:8731";
						label.post( new Runnable() {
							
							@Override
							public void run() {
								label.append("收到："+ipString+"\n");
								
							}
						});
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (in != null)
							in.close();
						if (socket != null)
							socket.close();
						if (ss != null)
							ss.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}
