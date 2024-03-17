package com.ytt.informationrelease.server;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.ytt.informationrelease.MainActivity;
import com.ytt.informationrelease.SearchActivity;
import com.ytt.informationrelease.comm.Material;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;

public class socket_udp {
    public static HashMap<String, String> search_Client = new HashMap<String, String>();
    private static final int PORT = 1974;
    private DatagramSocket socket;
    private Thread listenThread;
    private Context C;

    public socket_udp() {
        try {
            socket = new DatagramSocket(PORT);
            socket.setBroadcast(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(Context Context) {
        C = Context;
        listenThread = new Thread(new Runnable() {
            @Override
            public void run() {
                listen();
            }
        });
        listenThread.start();
    }

    public void stop() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    private void listen() {
        try {
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            while (!socket.isClosed()) {
                socket.receive(packet);
                // 获取发送方的IP地址
                InetAddress senderAddress = packet.getAddress();
                String senderIP = senderAddress.getHostAddress();
//                System.out.println("来包ip:" + senderIP);

                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println("收到广播: " + message);
                // 在这里处理接收到的消息，例如发送响应
                String[] lines = message.split("\n");
                if(lines.length >= 2){
                    if (lines[0].equals("server")) {
                        switch (lines[1]){
                            case "000":
                                // 搜索
                                String Content = "client\n";
                                Content = Content + Material.displaySystemInfo_Version()+"\n";
                                Content = Content + Material.displaySystemInfo_ABI()+"\n";
                                Content = Content + MainActivity.config.DeviceId +"\n";
                                Content = Content + Material.displayIpAddress()+"\n";
                                Content = Content + MainActivity.config.MacAddress;
                                byte[] replyData = Content.getBytes();
                                DatagramPacket replyPacket = new DatagramPacket(replyData, replyData.length, senderAddress, PORT);
                                socket.send(replyPacket);
//                                System.out.println("Replied to " + senderIP + ": " + Content);
                                break;
                            case "001":
                                //修改服务器
                                SharedPreferences sharedPreferences = C.getSharedPreferences("data", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("server", lines[2]);
                                editor.apply();
                                MainActivity.config.server=lines[2];
                                MainActivity.client.stop();
                                Log.d("socket_udp","重新连接服务器");
                                MainActivity.client.scheduleReconnect(); // 重新连接
                                break;
                            case "002":
                                //重新连接服务器
                                MainActivity.client.stop();
                                MainActivity.client.scheduleReconnect();
                                break;
                            case "003":
                                //推送url
                                MainActivity.config.url = lines[2];
                                MainActivity mainActivityy = MainActivity.getInstance();
                                if (mainActivityy != null) {
                                    mainActivityy.OpneURL(lines[2]);
                                }
                                break;
                            case "004":
                                //截屏
                                MainActivity mainActivity = MainActivity.getInstance();
                                if (mainActivity != null) {
                                    SendMsg(mainActivity.getWindows(),senderAddress);
                                }else {
                                    SendMsg("no",senderAddress);
                                }
                                break;
                            case "005":
                                //黑屏幕
                                MainActivity mainActivity0 = MainActivity.getInstance();
                                String D0 = "no";
                                if (mainActivity0 != null) {
                                    mainActivity0.hibernate(true);
                                    D0 = "ok";
                                }
                                Log.d("黑屏",D0);
                                SendMsg(D0,senderAddress);
                                break;
                            case "006":
                                //亮屏幕
                                MainActivity mainActivity1 = MainActivity.getInstance();
                                String D1 = "no";
                                if (mainActivity1 != null) {
                                    mainActivity1.hibernate(false);
                                    D1 = "ok";
                                }
                                Log.d("亮屏",D1);
                                SendMsg(D1,senderAddress);
                                break;
                            case "007":
                                //重启自己
                                MainActivity mainActivity2 = MainActivity.getInstance();
                                if (mainActivity2 != null) {
                                    mainActivity2.restartApp();
                                }
                                break;
                        }
                    }
                    if (lines[0].equals("client")) {
                        if(SearchActivity.isDestroyed) {
                            if(!lines[3].equals(MainActivity.config.DeviceId)){
//                                String Content = lines[1];
//                                Content = Content + lines[2];
//                                Content = Content + lines[3];
//                                Content = Content + lines[4];
//                                search_Client.put(lines[2],Content);
                                SearchActivity.addData(lines[4], lines[3]);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void  SendMsg(String T, InetAddress senderAddress) throws IOException {
        byte[] replyData0 = T.getBytes();
        int maxPacketSize = 1024; // UDP有效载荷的理论最大值

        for (int i = 0; i < replyData0.length; i += maxPacketSize) {
            int end = Math.min(replyData0.length, i + maxPacketSize);
            byte[] packet = Arrays.copyOfRange(replyData0, i, end);
            // 发送 packet
            DatagramPacket replyPacket0 = new DatagramPacket(packet, packet.length, senderAddress, PORT);
            socket.send(replyPacket0);
        }
    }

    public void Search(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 搜索
                try {
                    String Content = "server\n";
                    Content = Content + "000";
                    byte[] replyData = Content.getBytes();
                    InetAddress broadcastAddress = InetAddress.getByName("255.255.255.255");
                    DatagramPacket replyPacket = new DatagramPacket(replyData, replyData.length, broadcastAddress, PORT);
                    socket.send(replyPacket);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public void PushServer(String server){
        //推送服务器
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 搜索
                try {
                    String Content = "server\n";
                    Content = Content + "001";
                    Content = Content + server;
                    byte[] replyData = Content.getBytes();
                    InetAddress broadcastAddress = InetAddress.getByName("255.255.255.255");
                    DatagramPacket replyPacket = new DatagramPacket(replyData, replyData.length, broadcastAddress, PORT);
                    socket.send(replyPacket);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}

