package com.kongzue.messagebusdemo;

import android.app.Application;

import com.kongzue.runner.Runner;
import com.kongzue.dialogx.DialogX;

/**
 * @author: Kongzue
 * @github: https://github.com/kongzue/
 * @homepage: http://kongzue.com/
 * @mail: myzcxhh@live.cn
 * @createTime: 2022/5/4 13:48
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DialogX.init(this);
        Runner.init(this);
    }
}
