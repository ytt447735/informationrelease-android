package com.ytt.informationrelease.temporary;
import android.content.Context;
import android.util.Log;

import com.ytt.informationrelease.MainActivity;
import com.ytt.informationrelease.comm.Equipment;
import com.ytt.informationrelease.comm.HttpClientUtil;
import com.ytt.informationrelease.comm.Material;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Authentication {
    public static String Server = MainActivity.config.server;
    public static void run(){
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                GerInformation();
            }
        }, 0, 60, TimeUnit.SECONDS);  // 每2秒执行一次
    }
    public static void GerInformation() {
        Log.d("开始检测","111");
        if (Objects.equals(Server, "")){
            return;
        }
        String url = Server + "/api/client/getUrl?key="+ MainActivity.config.DeviceId;
        Log.d("Authentication-url",  url);
        String system_ABI = Material.displaySystemInfo_ABI();
        String system_Version = Material.displaySystemInfo_Version();
        String IP = Material.displayIpAddress();
        String device_id = MainActivity.config.DeviceId;
        String RunTime = MainActivity.getAppRunningTime();
        String AppVersion = MainActivity.config.AppVersion;
        String Data = "{\"RunTime\":\""+RunTime+"\",\"AppVersion\":\""+AppVersion+"\",\"system\":\""+system_Version+"\",\"ABI\": \""+system_ABI+"\",\"device_id\": \""+device_id+"\",\"ip\": \""+IP+"\"}";
        // 获取信息
        String R = HttpClientUtil.GerInformation(url, Data);
        Log.d("Authentication-data", R);

        // 使用 JSONObject 解析 JSON 字符串
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(R);
            // 读取JSON对象中的数据
            int code = jsonObject.getInt("code");
            if(code == 200){
                JSONObject dataObject = jsonObject.getJSONObject("data");
                String url1 = dataObject.getString("url");

                Log.d("MainActivity", "Code: " + code);
                Log.d("MainActivity", "URL: " + url1);
                Log.d("MainActivity", "URL: " + MainActivity.config.url);
                if(!Objects.equals(MainActivity.config.url, url1)){
                    MainActivity.config.url = url1;
                    MainActivity mainActivity = MainActivity.getInstance();
                    if (mainActivity != null) {
                        mainActivity.OpneURL(url1);
                    }
                }


            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        Log.d("开始检测","222");
    }

}
