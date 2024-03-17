package com.ytt.informationrelease;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Base64;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class EmpowerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empower);

        // 要生成二维码的字符串
        String text = "Your QR Code Text";
        String base64EncodedString = Base64.encodeToString(text.getBytes(), Base64.DEFAULT);

        // 生成二维码并显示在 ImageView 中
        ImageView qrCodeImageView = findViewById(R.id.qr_code_image);
        Bitmap qrCodeBitmap = generateQRCode(base64EncodedString, 500, 500); // 调整二维码大小
        qrCodeImageView.setImageBitmap(qrCodeBitmap);
    }

    // 生成二维码的方法
    private Bitmap generateQRCode(String text, int width, int height) {
        try {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? getResources().getColor(R.color.black) : getResources().getColor(R.color.white)); // 设置二维码颜色
                }
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}