package com.ytt.informationrelease.server;
import java.net.*;

public class PortAvailabilityChecker {
    public static boolean isPortAvailable(int port) {
        try {
            // 尝试在指定端口上创建一个DatagramSocket对象
            DatagramSocket socket = new DatagramSocket(port);

            // 如果能够成功创建DatagramSocket，则说明端口可用
            socket.close();
            return true;
        } catch (Exception e) {
            // 如果在创建DatagramSocket时抛出异常，则说明端口已被占用
            return false;
        }
    }
}

