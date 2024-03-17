package com.ytt.informationrelease.SleepAwakening;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScreenTimer {

    @SuppressLint("ScheduleExactAlarm")
    public static void scheduleScreenOff(Context context, long delayMillis) {
        Log.e("ScreenTimer","scheduleScreenOff");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ScreenControlReceiver.class);
        intent.setAction(ScreenControlReceiver.ACTION_SCREEN_OFF);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delayMillis, pendingIntent);
    }

    @SuppressLint("ScheduleExactAlarm")
    public static void scheduleScreenOn(Context context, long delayMillis) {
        Log.e("ScreenTimer","scheduleScreenOn");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ScreenControlReceiver.class);
        intent.setAction(ScreenControlReceiver.ACTION_SCREEN_ON);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delayMillis, pendingIntent);
    }
}

