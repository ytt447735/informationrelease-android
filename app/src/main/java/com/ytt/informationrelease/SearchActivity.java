package com.ytt.informationrelease;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    public static boolean isDestroyed = false;
    private static ListView listView;
    private static Button button,button1;
    private static ArrayAdapter<String> adapter;
    private static ArrayList<String> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        isDestroyed = true;

        // 初始化ListView和数据列表
        listView = findViewById(R.id.ListView);
        dataList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);

        button=findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //搜索
//                Toast.makeText(ButtonActivity.this,"按钮被点击了",Toast.LENGTH_SHORT).show();
                clearData();
                MainActivity.socketUDP.Search();
            }
        });

        button1=findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //推送
                showInputDialog(SearchActivity.this);
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroyed = false;
    }
    public static void addData(final String item, final String subItem) {
        // 使用runOnUiThread确保UI更新操作在主线程中执行
        ((Activity)listView.getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                button.setText("搜索 当前" + (dataList.size() + 1) + "台设备");

                dataList.add(item + "\n" + subItem);
                adapter.notifyDataSetChanged();
            }
        });
    }
    public static void clearData() {
        // 使用runOnUiThread确保UI更新操作在主线程中执行
        ((Activity)listView.getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                button.setText("搜索");
                dataList.clear();
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void showInputDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("请输入服务器地址");
        final EditText input = new EditText(context);
        builder.setView(input);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString();
                // 处理用户输入的文本
                MainActivity.socketUDP.PushServer(text);
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}