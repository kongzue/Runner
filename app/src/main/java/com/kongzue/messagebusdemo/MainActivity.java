package com.kongzue.messagebusdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.runner.ActivityRunnable;
import com.kongzue.runner.Runner;
import com.kongzue.dialogx.dialogs.PopTip;

public class MainActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
    /**
     * 设置一个事件，等待 Activity2 启动后执行
     * @param view 按钮
     */
    public void waitRunOnActivity2(View view) {
        Runner.runOnActivity(Activity2.class, new ActivityRunnable<Activity2>() {
            @Override
            public void run(Activity2 activity) {
                MessageDialog.build()
                        .setTitle("提示")
                        .setMessage("这个执行事件来自于 MainActivity 设置。")
                        .setOkButton("OK")
                        .show(activity);
            }
        });
        PopTip.show("已准备就绪");
    }
    
    /**
     * 启动 Activity2
     * @param view 按钮
     */
    public void runActivity2(View view) {
        startActivity(new Intent(this, Activity2.class));
    }
    
    /**
     * 传递一个 Bitmap 到 Activity2
     * @param view 按钮
     */
    public void sendToActivity2(View view) {
        Runner.sendToActivity("Activity2","bitmapResult", BitmapFactory.decodeResource(getResources(),R.mipmap.img_bug));
        PopTip.show("已准备就绪\n请进入 Activity2 查看！");
    }
}