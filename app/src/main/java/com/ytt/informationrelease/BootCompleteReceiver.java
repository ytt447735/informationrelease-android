package com.ytt.informationrelease;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
        Log.i("开机自启==", "我接收到广播啦");
        Intent it = new Intent(context, MainActivity.class);
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(it);
//        }
        switch (intent.getAction()){
            case Intent.ACTION_BOOT_COMPLETED:
                Log.i("开机自启==","手机开机了");
                Intent mainActivityIntent = new Intent(context, MainActivity.class);  // 要启动的Activity
                mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(mainActivityIntent);
                break;
            case  Intent.ACTION_SHUTDOWN:
                Log.i("开机自启==","手机关机了");
                break;
            case Intent.ACTION_SCREEN_ON:
                Log.i("开机自启==","亮屏");
                break;
            case Intent.ACTION_SCREEN_OFF:
                Log.i("开机自启==","息屏");
                break;
            case Intent.ACTION_USER_PRESENT:
                Log.i("开机自启==","手机解锁");
                break;
        }
    }

}