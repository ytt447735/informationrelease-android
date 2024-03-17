package com.ytt.informationrelease.comm;

import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class HttpClientUtil {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(500, TimeUnit.MILLISECONDS) // 设置连接超时时间为500毫秒
            .readTimeout(500, TimeUnit.MILLISECONDS) // 设置读取超时时间为500毫秒
            .build();

    public static String GerInformation(String url,String jsonBody) {
        try {
            return post(url, jsonBody);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            return response.body().string();
        }
    }
}
