package com.ytt.informationrelease;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.tencent.smtt.sdk.QbSdk;

public class initX5WebView {
    /**************腾讯X5webview**************/
    Context context;
    void initX5WebView(Context C) {  //使用腾讯x5 webview，解决安卓原生wenview不适配不同机型问题
        context = C;

        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。

        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。

                if(arg0){//true
                    Toast.makeText(context,"onViewInitFinished 加载 成功", Toast.LENGTH_SHORT).show();
                    Log.e("腾讯X5", " onViewInitFinished 加载 成功 " + arg0);
                }else{
                    Toast.makeText(context,"onViewInitFinished 加载 失败!!!使用原生安卓webview"+arg0, Toast.LENGTH_SHORT).show();
                    Log.e("腾讯X5", " onViewInitFinished 加载 失败！！！使用原生安卓webview "+arg0);
                }
            }
            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(context,  cb);

    }
}
