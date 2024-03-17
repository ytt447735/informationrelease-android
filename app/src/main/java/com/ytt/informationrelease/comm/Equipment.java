package com.ytt.informationrelease.comm;

import android.provider.Settings;
import android.content.Context;

public class Equipment {

    // 获取设备的唯一 ID 方法
    public static String getDeviceId(Context context) {
        String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceIdUpperCase = deviceId.toUpperCase();
        return deviceIdUpperCase;
    }
}
