package com.ytt.informationrelease;

import static com.ytt.informationrelease.comm.DataConversion.getBitmapString;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.os.Looper;
import android.os.PowerManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.util.Objects;

import com.airbnb.lottie.LottieAnimationView;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.ytt.informationrelease.comm.Equipment;
import com.ytt.informationrelease.comm.MacAddressHelper;
import com.ytt.informationrelease.comm.Material;
import com.ytt.informationrelease.models.Config;
import com.ytt.informationrelease.server.socket_udp;
import com.ytt.informationrelease.websocket.Client;


public class MainActivity extends AppCompatActivity {

    public static long startTimeMillis;//记录启动时间
    public static com.tencent.smtt.sdk.WebView web ;
    public static Config config = new Config();
    @SuppressLint("StaticFieldLeak")
    public static socket_udp socketUDP = new socket_udp();
    private static MainActivity instance;
    private static LottieAnimationView animationView;
    private ConstraintLayout constraintLayout;
    private int originalBackgroundColor;
    public static  Client client;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this; // 在创建时保存引用
        animationView = findViewById(R.id.animation_view);

        constraintLayout = findViewById(R.id.constraint_layout);

        // 获取原始背景颜色
        originalBackgroundColor = constraintLayout.getDrawingCacheBackgroundColor();

//        startActivity(new Intent(this, EngineeringActivity.class));

        // 初始化 TextView
//        ipAddressTextView = findViewById(R.id.textView);
        // 记录应用程序启动时间
        startTimeMillis = SystemClock.elapsedRealtime();

        // 初始化配置
        SharedPreferences sharedPreferences = this.getSharedPreferences("data", Context.MODE_PRIVATE);
        config.server = sharedPreferences.getString("server", "ws://192.168.2.152:8080/message");//ws://irs.hzfyht.cn/message
        config.url = sharedPreferences.getString("url", "");
        config.DeviceId = Equipment.getDeviceId(this);
        config.AppVersion = Material.getAppVersion(this);
        config.MacAddress = MacAddressHelper.getMacAddress(this);
        Log.d("服务器",config.server);
        Log.d("链接",config.url);
        Log.d("设备ID",config.DeviceId);

//        Log.d("IP:",  Material.displayIpAddress());
//        ServerManager s = new ServerManager(getApplicationContext());
//        s.startServer();


//        initX5WebView web = new initX5WebView();
//        web.initX5WebView(this);
//
//        java.util.HashMap map = new java.util.HashMap();
//        map.put(com.tencent.smtt.export.external.TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
//        map.put(com.tencent.smtt.export.external.TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
//        com.tencent.smtt.sdk.QbSdk.initTbsSettings(map);
        web = findViewById(R.id.TX_X5_webview);
        //清除缓存
        QbSdk.clearAllWebViewCache(this,true);
//        web.clearCache(true);
//        web.clearHistory();

//        web.setInitialScale(android.view.View.GONE);// 隐藏
        web.setBackgroundColor(0);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setBuiltInZoomControls(true);
        web.getSettings().setDisplayZoomControls(false);
        web.getSettings().setSupportZoom(true);

        web.getSettings().setAllowFileAccess(true);
        web.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        web.getSettings().setDomStorageEnabled(true);


//        config.url="http://www.baidu.com";
        Log.d("url",config.url);
        Log.d("server",config.server);

        if(!Objects.equals(config.url, "")){
//            View fullscreenView = findViewById(R.id.fullscreen_view);
//            fullscreenView.setVisibility(android.view.View.GONE);
            Send404(false);
            web.setVisibility(android.view.View.VISIBLE);
            web.loadUrl(config.url);
        }else{
            LongPress(this);
        }
        if (web.getX5WebViewExtension() == null){
            Toast.makeText(this,"X5内核未启动",Toast.LENGTH_SHORT).show();
        }

        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 在当前 WebView 中加载链接，返回 false 表示不要将链接交给系统默认浏览器处理
                Send404(false);
                view.loadUrl(url);
                return false;
            }
        });
////        web.loadUrl("http://debugtbs.qq.com/");
//        if (web.getX5WebViewExtension()==null){
//            Toast.makeText(this,"X5内核未启动", Toast.LENGTH_SHORT).show();
//            web.loadUrl("http://debugtbs.qq.com/");
//        }else {
//            web.loadUrl("https://ccedit.com/template/62b22876c67e9269212a983bde750325");
//        }
//
//        web.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                view.loadUrl(url);
//                return super.shouldOverrideUrlLoading(view, url);
//            }
//        });

        //检测url
//        Authentication.run();
        client = new Client();
        client.start();

        //本地客户端
        socketUDP.start(this);

//        hibernate(true);
    }
    //休眠
    public void hibernate(boolean enable){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 更新UI元素
                if(enable){
                    //黑色
                    constraintLayout.setBackgroundColor(Color.BLACK);
                    Send404(false);
                    web.setVisibility(View.GONE);

                }else{
                    //原色
                    constraintLayout.setBackgroundColor(originalBackgroundColor);
                    Send404(true);
                    web.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    /**
     * 判断我们的应用是否在白名单中
     *
     * @param context
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean isIgnoringBatteryOptimizations(Context context) {
        boolean isIgnoring = false;
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            isIgnoring = powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
        }
        return isIgnoring;
    }

    /**
     * 申请加入白名单
     *
     * @param context
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void requestIgnoreBatteryOptimizations(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null; // 清除引用以避免内存泄漏
    }
    public static MainActivity getInstance() {
        return instance;
    }
    public void OpneURL(String url){
        if (Objects.equals(url, "")){
            Send404(true);
            return;
        }
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d("重新加载url1", url);
                web.setVisibility(android.view.View.VISIBLE);
                //                web.setBackgroundColor(0);
                //                web.getSettings().setJavaScriptEnabled(true);
                //                web.getSettings().setBuiltInZoomControls(true);
                //                web.getSettings().setDisplayZoomControls(false);
                //                web.getSettings().setSupportZoom(true);
                //
                //                web.getSettings().setAllowFileAccess(true);
                //                web.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                //                web.getSettings().setDomStorageEnabled(true);
                Send404(false);
                web.loadUrl(url);
                Log.d("重新加载url2", url);
            }
        });
    }

    // 获取应用程序已经启动的时间
    public static String getAppRunningTime() {
        long currentTimeMillis = SystemClock.elapsedRealtime();
        long appRunningTimeMillis = currentTimeMillis - startTimeMillis;

        // 计算年、天、小时和分钟
        long years = appRunningTimeMillis / (1000 * 60 * 60 * 24 * 365);
        long days = (appRunningTimeMillis % (1000 * 60 * 60 * 24 * 365)) / (1000 * 60 * 60 * 24);
        long hours = (appRunningTimeMillis % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (appRunningTimeMillis % (1000 * 60 * 60)) / (1000 * 60);

        StringBuilder runningTime = new StringBuilder();
        if (years > 0) {
            runningTime.append(years).append("年 ");
        }
        if (days > 0) {
            runningTime.append(days).append("天 ");
        }
        if (hours > 0) {
            runningTime.append(hours).append("小时 ");
        }
        if (minutes > 0 && hours == 0) { // 当分钟大于0且小时为0时才显示
            runningTime.append(minutes).append("分钟 ");
        }

        return runningTime.toString().trim();
    }

    public String getWindows() {
        // 截屏
        View view = getWindow().getDecorView();
        view.setDrawingCacheEnabled(true); // 设置缓存，可用于实时截图
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        view.setDrawingCacheEnabled(false); // 清空缓存，可用于实时截图
        String R = getBitmapString(bitmap);
        R = R.replaceAll("\\s*|\r|\n|\t","");
        return R; // 确保你有这个方法来转换Bitmap为String
    }

    public void Send404(boolean Display){
        // 是否显示 404
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 更新UI元素
                if(Display){
                    //显示
//            if (animationView.isAnimating()) {
                    animationView.setVisibility(View.VISIBLE);
                    animationView.resumeAnimation();
                }else{
                    //隐藏
                    animationView.cancelAnimation();
                    animationView.setVisibility(View.GONE);
                }
            }
        });
    }

    public void restartApp() {
        Log.e("重启","重启了");
        Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    private Handler handler1;
    private Runnable longPressed;
    private void LongPress(Context C){
        // 长按
        View fullscreenView = findViewById(R.id.fullscreen_view);

        fullscreenView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 按下时启动计时器
                        handler1 = new Handler();
                        longPressed = new Runnable() {
                            public void run() {
                                // 跳转到指定的Activity
                                startActivity(new Intent(C, EngineeringActivity.class));
                            }
                        };
                        handler1.postDelayed(longPressed, 3000); // 3秒后执行跳转操作
                        break;
                    case MotionEvent.ACTION_UP:
                        // 手指抬起时取消计时器
                        if (handler1 != null && longPressed != null) {
                            handler1.removeCallbacks(longPressed);
                        }
                        break;
                }
                return true;
            }
        });
    }



    private static final long LONG_PRESS_TIMEOUT = 3000; // 长按超时时间，单位毫秒
    private boolean okKeyPressed = false; // 记录 OK 键是否被按下
    private Handler handler = new Handler(); // 用于延迟处理长按事件的 Handler
    private Runnable longPressRunnable = new Runnable() {
        @Override
        public void run() {
            // 在计时时间结束后执行长按事件
            onOkKeyLongPress();
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            // OK 键按下时，启动计时器
            okKeyPressed = true;
            handler.postDelayed(longPressRunnable, LONG_PRESS_TIMEOUT);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            // OK 键释放时，取消计时器
            okKeyPressed = false;
            handler.removeCallbacks(longPressRunnable);
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void onOkKeyLongPress() {
        // 在这里执行长按 OK 键的逻辑
        startActivity(new Intent(this, EngineeringActivity.class));
    }
}

