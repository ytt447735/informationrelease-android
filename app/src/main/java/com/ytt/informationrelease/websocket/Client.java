package com.ytt.informationrelease.websocket;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.tencent.smtt.sdk.QbSdk;
import com.ytt.informationrelease.MainActivity;
import com.ytt.informationrelease.comm.Material;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class Client {
    private boolean isWebSocketConnected = false;
    private static final String SOCKET_URL = "ws://your_websocket_server_url";
    private static final long HEARTBEAT_INTERVAL = 2 * 60000; // 心跳间隔时间，单位毫秒
    private static final long MAX_RECONNECT_INTERVAL = 60000; // 最大重连间隔时间，单位毫秒

    private OkHttpClient client;
    private WebSocket webSocket;
    private Timer reconnectTimer;
    private Timer heartbeatTimer;
    private long reconnectInterval = 1000; // 初始重连间隔时间

    public Client() {
        client = new OkHttpClient();
    }

    public void start() {
        Request request = new Request.Builder().url(MainActivity.config.server).build();
        WebSocketListener socketListener = new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                // 连接成功
                // 在这里可以发送消息或执行其他操作
                cancelReconnectTimer(); // 取消重新连接定时器
                startHeartbeat(); // 启动心跳

                String system_ABI = Material.displaySystemInfo_ABI();
                String system_Version = Material.displaySystemInfo_Version();
                String IP = Material.displayIpAddress();
                String device_id = MainActivity.config.DeviceId;
                String AppVersion = MainActivity.config.AppVersion;
                String Data = "{\\\"AppVersion\\\":\\\""+AppVersion+"\\\",\\\"system\\\":\\\""+system_Version+"\\\",\\\"ABI\\\": \\\""+system_ABI+"\\\",\\\"device_id\\\": \\\""+device_id+"\\\",\\\"ip\\\": \\\""+IP+"\\\"}";

                sendMessage("{\"type\":1000,\"id\":\""+device_id+"\",\"data\":\""+Data+"\"}");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                // 收到文本消息
                // 使用 JSONObject 解析 JSON 字符串
                Log.d("onMessage", text);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(text);
                    // 读取JSON对象中的数据
                    int type = jsonObject.getInt("type");
                    switch (type){
                        case 1000:
                            // 推送资料
                            JSONObject dataObject = jsonObject.getJSONObject("data");
                            String url1 = dataObject.getString("url");

                            Log.d("MainActivity", "type: " + type);
                            Log.d("MainActivity", "URL: " + url1);
                            Log.d("MainActivity", "URL: " + MainActivity.config.url);
                            if(!Objects.equals(MainActivity.config.url, url1)){
                                MainActivity.config.url = url1;
                                MainActivity mainActivity = MainActivity.getInstance();
                                if (mainActivity != null) {
                                    mainActivity.OpneURL(url1);
                                }
                            }
                            break;
                        case 1001:
                            // 推送资料 强制
                            JSONObject dataObject0 = jsonObject.getJSONObject("data");
                            String url0 = dataObject0.getString("url");

                            Log.d("MainActivity", "type: " + type);
                            Log.d("MainActivity", "URL: " + url0);
                            Log.d("MainActivity", "URL: " + MainActivity.config.url);
                            MainActivity.config.url = url0;
                            MainActivity mainActivity = MainActivity.getInstance();
                            if (mainActivity != null) {
                                QbSdk.clearAllWebViewCache(mainActivity,true);
                                mainActivity.OpneURL(url0);
                            }
                            break;
                        case 1002:
                            //更改服务器
                            JSONObject dataObject1 = jsonObject.getJSONObject("data");
                            String url2 = dataObject1.getString("server");

                            Log.d("MainActivity", "type: " + type);
                            Log.d("MainActivity", "server: " + url2);
                            Log.d("MainActivity", "server: " + MainActivity.config.server);
                            MainActivity.config.server = url2;

                            MainActivity mainActivityys = MainActivity.getInstance();
                            if (mainActivityys != null) {
                                SharedPreferences sharedPreferences = mainActivityys.getSharedPreferences("data", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("server", url2);
                                editor.apply();
                            }
                            stop();
                            Log.d("WebSocket","重新连接服务器");
                            scheduleReconnect(); // 重新连接
                            break;
                        case 1003:
                            //截屏
                            MainActivity mainActivityy = MainActivity.getInstance();
                            String D = "{\"type\":1003,\"id\":\""+MainActivity.config.DeviceId+"\",\"data\":\"\"}";
                            if (mainActivityy != null) {
                                D="{\"type\":1003,\"id\":\""+MainActivity.config.DeviceId+"\",\"data\":\""+mainActivityy.getWindows()+"\"}";
                            }
                            Log.d("截屏",D);
                            sendMessage(D);
                            break;
                        case 1004:
                            //黑屏
                            MainActivity mainActivity0 = MainActivity.getInstance();
                            String D0 = "{\"type\":1004,\"id\":\""+MainActivity.config.DeviceId+"\",\"data\":\"-1\"}";
                            if (mainActivity0 != null) {
                                mainActivity0.hibernate(true);
                                D0 = "{\"type\":1004,\"id\":\""+MainActivity.config.DeviceId+"\",\"data\":\"1\"}";
                            }
                            Log.d("黑屏",D0);
                            webSocket.send(D0);
                            break;
                        case 1005:
                            //亮屏幕
                            MainActivity mainActivity1 = MainActivity.getInstance();
                            String D1 = "{\"type\":1005,\"id\":\""+MainActivity.config.DeviceId+"\",\"data\":\"-1\"}";
                            if (mainActivity1 != null) {
                                mainActivity1.hibernate(false);
                                D1 = "{\"type\":1005,\"id\":\""+MainActivity.config.DeviceId+"\",\"data\":\"1\"}";
                            }
                            Log.d("亮屏",D1);
                            webSocket.send(D1);
                            break;
                        case 1006:
                            //重启自己
                            MainActivity mainActivity2 = MainActivity.getInstance();
                            if (mainActivity2 != null) {
                                mainActivity2.restartApp();
                            }
                            break;

                    }
                } catch (JSONException e) {
                    Log.d("WebSocket出错了", Objects.requireNonNull(e.getMessage()));
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
                // 收到二进制消息
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                // 连接关闭
                Log.d("WebSocket","连接断开");
                scheduleReconnect(); // 重新连接
                stopHeartbeat(); // 停止心跳
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
                // 连接失败
                Log.d("WebSocket","连接失败");
                scheduleReconnect(); // 重新连接
                stopHeartbeat(); // 停止心跳
            }
        };
        webSocket = client.newWebSocket(request, socketListener);
    }

    private void startHeartbeat() {
        //启动心跳
        stopHeartbeat(); // 停止之前的心跳
        heartbeatTimer = new Timer();
        heartbeatTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // 发送心跳消息
                if (webSocket != null) {
                    long timestamp = System.currentTimeMillis();
                    String D ="{\"type\":1,\"id\":\""+MainActivity.config.DeviceId+"\",\"data\":\""+timestamp+"\"}";
                    Log.d("心跳",D);
                    webSocket.send(D);
                }
            }
        }, 0, HEARTBEAT_INTERVAL);
    }
    private void stopHeartbeat() {
        //停止心条
        if (heartbeatTimer != null) {
            heartbeatTimer.cancel();
            heartbeatTimer = null;
        }
    }

    public void scheduleReconnect() {
        cancelReconnectTimer(); // 取消之前的定时器
        reconnectTimer = new Timer();
        reconnectTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d("WebSocket","重新连接");
                start(); // 重新连接
                // 更新重连间隔时间，使用指数退避算法
                reconnectInterval = Math.min(reconnectInterval * 2, MAX_RECONNECT_INTERVAL);
            }
        }, reconnectInterval);
    }

    private void cancelReconnectTimer() {
        if (reconnectTimer != null) {
            reconnectTimer.cancel();
            reconnectTimer = null;
        }
    }

    public void stop() {
        //停止webSocket
        cancelReconnectTimer(); // 取消重新连接定时器
        if (webSocket != null) {
            webSocket.cancel();
        }
    }

    public void sendMessage(String message) {
        //发生消息
        if (webSocket != null) {
            webSocket.send(message);
        }
    }

}

