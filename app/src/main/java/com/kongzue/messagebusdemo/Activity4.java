package com.kongzue.messagebusdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.kongzue.messagebusdemo.util.ListData;
import com.kongzue.messagebusdemo.util.LoginInfo;
import com.kongzue.runner.ViewModel;
import com.kongzue.runner.interfaces.BindModel;

public class Activity4 extends AppCompatActivity {
    
    @BindModel
    ListData listData;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_4);
        
        listData = new ListData();
        ViewModel.bindActivity(this);
    }
}