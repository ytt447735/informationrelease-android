package com.ytt.informationrelease;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ytt.informationrelease.SleepAwakening.ScreenTimer;


public class EngineeringActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private String[] items = {"安装BT浏览器", "浏览器检测", "设备信息","激活","搜索","关机","黑屏"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engineering);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MyAdapter());
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_engineering_item, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.textView.setText(items[position]);
        }

        @Override
        public int getItemCount() {
            return items.length;
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                switch (position) {
                    case 0:
                        Intent intent = new Intent(EngineeringActivity.this, DebugWebActivity.class);
                        intent.putExtra("url", "http://debugtbs.qq.com/");
                        startActivity(intent);
                        break;
                    case 1:
                        Intent intent1 = new Intent(EngineeringActivity.this, DebugWebActivity.class);
                        intent1.putExtra("url", "https://ie.icoa.cn/");
                        startActivity(intent1);
                        break;
                    case 2:
                        startActivity(new Intent(EngineeringActivity.this, InformationActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(EngineeringActivity.this, EmpowerActivity.class));
                        break;
                    case 4:
                        startActivity(new Intent(EngineeringActivity.this, SearchActivity.class));
                        break;
                    case 5:
//                        Intent shutdownIntent = new Intent(Intent.ACTION_SHUTDOWN);
//                        sendBroadcast(shutdownIntent);
                        MainActivity mainActivity = MainActivity.getInstance();
                        if (mainActivity != null) {
                            // 3分钟后休眠
                            long delayMillisForSleep =  1 * 60 * 1000; // 3分钟，以毫秒为单位
//                            SleepTimer.setSleepTimer(mainActivity, delayMillisForSleep);
                            ScreenTimer.scheduleScreenOff(mainActivity,delayMillisForSleep);

                            // 再3分钟后唤醒
                            long delayMillisForWakeup = 3 * 60 * 1000; // 6分钟，以毫秒为单位
//                            WakeupTimer.setWakeupTimer(mainActivity, delayMillisForWakeup);
                            ScreenTimer.scheduleScreenOn(mainActivity,delayMillisForWakeup);
                        }
                        break;
                    case 6:
                        MainActivity mainActivity0 = MainActivity.getInstance();
                        if (mainActivity0 != null) {
                            mainActivity0.hibernate(true);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }
}