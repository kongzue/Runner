package com.kongzue.messagebusdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.kongzue.runner.ActivityRunnable;
import com.kongzue.runner.Runner;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.runner.SenderTarget;

public class Activity2 extends AppCompatActivity {
    
    //不放心或者需要混淆的话，请使用 @SenderTarget("bitmapResult") 来标记接收 key 的成员
    Bitmap bitmapResult;
    
    private ImageView imgSenderPicture;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
        
        //如果有图像，直接显示 bitmapResult
        imgSenderPicture = findViewById(R.id.img_sender_picture);
        if (bitmapResult != null) {
            imgSenderPicture.setImageBitmap(bitmapResult);
        }
    
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    
    /**
     * 在回到 MainActivity 后执行一个事件
     * @param view 按钮
     */
    public void runOnMainActivity(View view) {
        Runner.runOnActivity("MainActivity", new ActivityRunnable() {
            @Override
            public void run(Activity activity) {
                MessageDialog.build()
                        .setTitle("提示")
                        .setMessage("这个事件来自 Activity2 创建。")
                        .setOkButton("OK")
                        .show(activity);
            }
        });
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //点击返回图标事件
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}