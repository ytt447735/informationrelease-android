package com.ytt.informationrelease.comm;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;

public class MacAddressHelper {

    // 获取网卡MAC地址
    public static String getMacAddress(Context context) {
        String macAddress = null;

        // 如果是 Android 6.0 及以上版本，则需要获取定位权限才能获取到 MAC 地址
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                macAddress = getMacAddressFromWifiManager(context);
            }
        } else {
            macAddress = getMacAddressFromWifiManager(context);
        }

        return macAddress;
    }

    // 通过 WifiManager 获取 MAC 地址
    private static String getMacAddressFromWifiManager(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String macAddress = wifiInfo.getMacAddress();

        // 针对 Android 6.0 以下版本，如果获取到的 MAC 地址为 "02:00:00:00:00:00"，则可能表示没有权限
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M && TextUtils.equals(macAddress, "02:00:00:00:00:00")) {
            macAddress = null;
        }

        return macAddress;
    }
}

