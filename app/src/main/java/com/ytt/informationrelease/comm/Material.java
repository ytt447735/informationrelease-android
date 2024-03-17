package com.ytt.informationrelease.comm;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Objects;

public class Material {
    public static String displayIpAddress() {
        // 创建一个 StringBuilder 来保存所有 IP 地址
        StringBuilder ipAddressesBuilder = new StringBuilder();
        // 获取当前设备的所有 IP 地址
        int count = 1; // 计数器，用于序号
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (inetAddress instanceof Inet4Address) { // 仅处理 IPv4 地址
                        String ipAddress = inetAddress.getHostAddress();
                        if(!Objects.equals(ipAddress, "127.0.0.1")) {
//                            ipAddressesBuilder.append("ip").append(count).append(" ").append(ipAddress).append(","); // 在 IP 地址前添加序号和 "ip" 字符串
                            ipAddressesBuilder.append(ipAddress).append(",");
                            count++; // 更新序号
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        // 获取 StringBuilder 中的文本，并打印出来
        // 删除末尾的逗号
        String str =ipAddressesBuilder.toString();
        if (str.endsWith(",")) {
            str = str.substring(0, str.length() - 1);
        }
        return str; // 去除末尾的换行符
//        Log.d("IPAddress", "IP Addresses: \n" + ipAddresses);
    }
    public static String displaySystemInfo_Version(){
        // 获取系统版本号
        int versionCode = Build.VERSION.SDK_INT;
        String versionName = Build.VERSION.RELEASE; // 或者使用 Build.VERSION_CODES 对应的名称
        // 输出版本信息和 ABI
        String Content = versionName+" (API Level " + versionCode + ")";
        return Content;
    }
    public static String displaySystemInfo_ABI(){
        // 获取 ABI
        String abi = Build.SUPPORTED_ABIS[0]; // 如果设备支持多个 ABI，则可以遍历 Build.SUPPORTED_ABIS 数组
        return abi;
    }
    public static String displayCurrentTime() {
        // 获取当前时间
        Calendar calendar = Calendar.getInstance();
        // 格式化时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentTime = sdf.format(calendar.getTime());
        return currentTime;
    }
    public static String getAppVersion(Context context) {
        //获取当前程序版本
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("AppVersionHelper", "获取应用程序版本号失败", e);
            return "";
        }
    }
}
