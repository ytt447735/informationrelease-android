package com.ytt.informationrelease.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class socket_tcp {
    private static final int PORT = 1974;
    private ServerSocket serverSocket;
    private ExecutorService executorService; // 线程池用于管理客户端连接
    private boolean isRunning = false;

    public socket_tcp() {
        try {
            serverSocket = new ServerSocket(PORT);
            executorService = Executors.newCachedThreadPool(); // 创建一个缓存线程池
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        isRunning = true;
        System.out.println("TCP服务器正在启动...");
        while (isRunning) {
            try {
                final Socket clientSocket = serverSocket.accept(); // 接受连接请求
                System.out.println("已接受来自的连接 " + clientSocket.getInetAddress().getHostAddress());

                // 使用线程池来处理客户端连接，提高效率
                executorService.submit(() -> handleClient(clientSocket));

            } catch (IOException e) {
                if (!isRunning) {
                    System.out.println("服务器已停止.");
                } else {
                    e.printStackTrace();
                }
            }
        }
        executorService.shutdown();
    }

    public void stop() {
        isRunning = false;
        try {
            serverSocket.close(); // 关闭服务器socket
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("被承认的: " + line);
            }
        } catch (IOException e) {
            System.out.println("处理客户端时出错: " + e.getMessage());
        } finally {
            try {
                clientSocket.close(); // 关闭客户端连接
            } catch (IOException e) {
                // Ignored
            }
        }
    }

    public static void main(String[] args) {
        socket_tcp server = new socket_tcp();
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
        server.start();
    }
}
