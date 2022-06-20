package com.kongzue.runner.interfaces;

import android.app.Activity;

import java.lang.ref.WeakReference;

/**
 * @author: Kongzue
 * @github: https://github.com/kongzue/
 * @homepage: http://kongzue.com/
 * @mail: myzcxhh@live.cn
 * @createTime: 2021/12/20 15:21
 */
public abstract class ActivityRunnable<D extends Activity> {
    
    public WeakReference<Activity> activityWeakReference;
    public Class waitRunActivityClass;
    public String waitRunActivityName;
    
    public abstract void run(D activity);
}
