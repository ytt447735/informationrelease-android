package com.ytt.informationrelease;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class DebugWebActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_web);

        // 获取传递的参数
        Intent intent = getIntent();
        if (intent != null) {
            String value = intent.getStringExtra("url");
            // 使用获取到的参数进行操作
            if (value != null) {
                // 对传递的参数进行操作
//                Log.d("EngineeringActivity", "Value received: " + value);
                final com.tencent.smtt.sdk.WebView web = findViewById(R.id.webview);
//        web.setInitialScale(android.view.View.GONE);// 隐藏
                web.getSettings().setBuiltInZoomControls(true);
                web.getSettings().setDisplayZoomControls(false);
                web.getSettings().setJavaScriptEnabled(true);
                web.getSettings().setSupportZoom(true);

                web.getSettings().setAllowFileAccess(true);
                web.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                web.getSettings().setDomStorageEnabled(true);
                web.loadUrl(value);
            }
        }
    }
}