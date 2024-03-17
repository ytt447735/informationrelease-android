package com.ytt.informationrelease;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class LogActivity extends AppCompatActivity {

    private TextView logTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        logTextView = findViewById(R.id.logTextView);

        // 设置 TextView 可以滚动
        logTextView.setMovementMethod(new android.text.method.ScrollingMovementMethod());
    }
}