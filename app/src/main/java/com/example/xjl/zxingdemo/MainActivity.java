package com.example.xjl.zxingdemo;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Hashtable;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText inputtxt;
    private ImageView imageview;
    private Button save;
    private Button show;
    private  String data;
    private String contents;
    private      Bitmap bitmap;
    private   HashMap hints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        inputtxt = (EditText) findViewById(R.id.inputtxt);
        imageview = (ImageView) findViewById(R.id.imageview);
        save = (Button) findViewById(R.id.save);
        show = (Button) findViewById(R.id.show);
        save.setOnClickListener(this);
        show.setOnClickListener(this);
        hints= new HashMap();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");    //定义内容字符集的编码
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);        //定义纠错等级
        hints.put(EncodeHintType.MARGIN, 2);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save:
                        saveInfo();
                break;
            case R.id.show:
                       showInfo();
                break;
        }
    }

    private void saveInfo() {
        data=inputtxt.getText().toString();
        if(data.equals("")){
            Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show();
        }else{
        //保存数据
        BitMatrix bitMatrix=null;
        try {
            contents= new String(data.getBytes("UTF-8"), "ISO-8859-1");;
            bitMatrix  =new MultiFormatWriter().encode(contents,BarcodeFormat.QR_CODE, 500, 500);
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 根据true,false(1,0)编码形成像素数组
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (bitMatrix.get(x, y)) {
                    pixels[y * width + x] = Color.BLACK;
                }else{
                    pixels[y * width + x] =Color.WHITE;
                }
            }
        }
        bitmap  =Bitmap.createBitmap(width,height,Bitmap.Config.RGB_565);
        bitmap.setPixels(pixels,0,width,0,0,width,height);
        imageview.setImageBitmap(bitmap);
    }
    }

    private void showInfo() {
        data=inputtxt.getText().toString();
        if (data.equals("")) {
            Toast.makeText(this, "无法显示", Toast.LENGTH_SHORT).show();
        } else {
            Result result = null;
            int pies[] = new int[bitmap.getWidth() * bitmap.getHeight()];
            bitmap.getPixels(pies, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), pies)));
            Hashtable<DecodeHintType, Object> h2 = new Hashtable<>();
            // 注意要使用 utf-8，因为刚才生成二维码时，使用了utf-8
            hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
            try {
                result = new MultiFormatReader().decode(binaryBitmap, h2);
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
            Toast.makeText(getApplicationContext(), result.getText(), Toast.LENGTH_LONG).show();

        }
    }
}
