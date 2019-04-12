package com.example.serverudp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * 创建时间：2019/3/25  17:35
 * 作者：5#
 * 描述：TODO
 */
public class TextActivity extends Activity {
    /*
     *   Data
     * */
    private final static String SEND_IP = "192.168.17.162";  //发送IP
    private final static int SEND_PORT = 8080;               //发送端口号
    private final static int RECEIVE_PORT = 7913;            //接收端口号

    private boolean listenStatus = true;  //接收线程的循环标识
    private byte[] receiveInfo;     //接收报文信息
    private byte[] buf;

    private DatagramSocket receiveSocket;
    private DatagramSocket sendSocket;
    private InetAddress serverAddr;
    private SendHandler sendHandler = new SendHandler();
    private ReceiveHandler receiveHandler = new ReceiveHandler();

    /*
     *   UI
     * */
    private TextView tvMessage;
    private Button btnSendUDP;

    class ReceiveHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            tvMessage.setText("接收到数据了" + receiveInfo.toString());
            Toast.makeText(TextActivity.this, "接收到数据了", Toast.LENGTH_SHORT).show();
        }
    }

    class SendHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            tvMessage.setText("UDP报文发送成功");
            Toast.makeText(TextActivity.this, "成功发送", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        btnSendUDP = (Button) findViewById(R.id.btn_send);
        tvMessage = (TextView) findViewById(R.id.tv_show);

        btnSendUDP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击按钮则发送UDP报文
                new UdpSendThread().start();
            }
        });

        //进入Activity时开启接收报文线程
        new UdpReceiveThread().start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //停止接收线程，关闭套接字连接
        listenStatus = false;
        receiveSocket.close();
    }

    /*
     *   UDP数据发送线程
     * */
    public class UdpSendThread extends Thread {
        @Override
        public void run() {
            try {
                buf = "i am an android developer, hello android! ".getBytes();

                // 创建DatagramSocket对象，使用随机端口
                sendSocket = new DatagramSocket();
                serverAddr = InetAddress.getByName(SEND_IP);

                DatagramPacket outPacket = new DatagramPacket(buf, buf.length, serverAddr, SEND_PORT);
                sendSocket.send(outPacket);

                sendSocket.close();
                sendHandler.sendEmptyMessage(1);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
     *   UDP数据接收线程
     * */
    public class UdpReceiveThread extends Thread {
        @Override
        public void run() {
            try {
                sendSocket = new DatagramSocket(RECEIVE_PORT);
                serverAddr = InetAddress.getByName(SEND_IP);

                while (listenStatus) {
                    byte[] inBuf = new byte[1024];
                    DatagramPacket inPacket = new DatagramPacket(inBuf, inBuf.length);
                    sendSocket.receive(inPacket);

                    if (!inPacket.getAddress().equals(serverAddr)) {
                        throw new IOException("未知名的报文");
                    }

                    receiveInfo = inPacket.getData();
                    receiveHandler.sendEmptyMessage(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
