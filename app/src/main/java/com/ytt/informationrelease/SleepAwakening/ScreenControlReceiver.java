package com.ytt.informationrelease.SleepAwakening;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

public class ScreenControlReceiver extends BroadcastReceiver {

    public static final String ACTION_SCREEN_OFF = "com.example.action.SCREEN_OFF";
    public static final String ACTION_SCREEN_ON = "com.example.action.SCREEN_ON";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            Log.e("ScreenControlReceiver",action);
            if (ACTION_SCREEN_OFF.equals(action)) {
                // 熄屏
                setScreenBrightness(context, 0); // 设置屏幕亮度为0，即熄屏
            } else if (ACTION_SCREEN_ON.equals(action)) {
                // 亮屏
                setScreenBrightness(context, 255); // 设置屏幕亮度最大，即亮屏
            }
        }
    }

    private void setScreenBrightness(Context context, int brightness) {
        try {
            // 设置屏幕亮度
            Settings.System.putInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS, brightness);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}

