package com.ytt.informationrelease;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class ScreenCaptureHelper {
    private static final int REQUEST_CODE = 100;
    private static MediaProjectionManager mProjectionManager;
    private static MediaProjection mMediaProjection;
    private static ImageReader mImageReader;
    private static int mScreenWidth;
    private static int mScreenHeight;
    private static int mScreenDensity;
    private static Context mContext;

    public static void init(Context context) {
        mContext = context.getApplicationContext();
        // 获取屏幕尺寸和密度
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;
        mScreenDensity = metrics.densityDpi;

        // 初始化ImageReader
        mImageReader = ImageReader.newInstance(mScreenWidth, mScreenHeight, PixelFormat.RGBA_8888, 2);

        // 获取MediaProjectionManager
        mProjectionManager = (MediaProjectionManager) mContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    }

    public static void captureScreen() {
        if (mProjectionManager == null || mImageReader == null || mContext == null) {
            return;
        }
        // 请求用户授权
        Intent intent = mProjectionManager.createScreenCaptureIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ((Activity) mContext).startActivityForResult(intent, REQUEST_CODE);
    }

    public static String onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // 获取MediaProjection
            mMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);

            // 创建VirtualDisplay
            mMediaProjection.createVirtualDisplay("ScreenCapture",
                    mScreenWidth, mScreenHeight, mScreenDensity,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    mImageReader.getSurface(), null, null);

            // 获取屏幕图像
            Image image = mImageReader.acquireLatestImage();
            if (image != null) {
                // 创建Bitmap
                Bitmap bitmap = imageToBitmap(image);
                if (bitmap != null) {
                    // 将Bitmap转换为Base64字符串
                    return bitmapToBase64(bitmap);
                }
                image.close();
            }
        }
        return null;
    }

    private static Bitmap imageToBitmap(Image image) {
        if (image == null) return null;
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * mScreenWidth;

        Bitmap bitmap = Bitmap.createBitmap(mScreenWidth + rowPadding / pixelStride, mScreenHeight, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        return Bitmap.createBitmap(bitmap, 0, 0, mScreenWidth, mScreenHeight);
    }

    private static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}

