package com.kongzue.messagebusdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.kongzue.messagebusdemo.util.LoginInfo;
import com.kongzue.runner.Data;
import com.kongzue.runner.ViewModel;
import com.kongzue.runner.interfaces.BindModel;

public class Activity3 extends AppCompatActivity {
    
    @BindModel
    LoginInfo loginInfo;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_3);
    
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        loginInfo = new LoginInfo("zhangsan", "123456").setIsRememberLogin(true);
        ViewModel.bindActivity(this);
    }
    
    public void btnShowInputInfo(View view) {
        Data.changeDataByTag("txtLog", loginInfo.toString());
    }
    
    public void btnChangeLoginInfo(View view) {
        loginInfo.setUsername("李四")
                .setPassword("987654")
                .setIsRememberLogin(false);
    }
    
    public void showListTest(View view) {
        startActivity(new Intent(this, Activity4.class));
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}