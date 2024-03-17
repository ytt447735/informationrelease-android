package com.ytt.informationrelease;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.ytt.informationrelease.comm.Equipment;
import com.ytt.informationrelease.comm.Material;

public class InformationActivity extends AppCompatActivity {

    private TextView ipAddressTextView;
    private static final int REFRESH_INTERVAL = 1000; // 刷新间隔，单位毫秒
    private Handler handler = new Handler();
    private Runnable refreshRunnable;
    private Context C;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        C = this;

        // 初始化 TextView
        ipAddressTextView = findViewById(R.id.textView);

        // 初始化定时刷新任务
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                // 获取并显示 Wi-Fi IP 地址
//                displayWifiIpAddress();
                String Content = "系统版本:" + Material.displaySystemInfo_Version()+"\n";
                Content = Content + "ABI:" + Material.displaySystemInfo_ABI()+"\n";
                Content = Content + "IP:" +  Material.displayIpAddress()+"\n";
                Content = Content + "设备ID:" + Equipment.getDeviceId(C)+"\n";
                Content = Content +"当前时间:" + Material.displayCurrentTime();
                Log.d("Device",  Content);
                ipAddressTextView.setText(Content);

                // 5秒后再次执行
                handler.postDelayed(this, REFRESH_INTERVAL);
            }
        };
    }
    @Override
    protected void onResume() {
        super.onResume();
        // 开始定时刷新
        handler.postDelayed(refreshRunnable, REFRESH_INTERVAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 停止定时刷新
        handler.removeCallbacks(refreshRunnable);
    }


}