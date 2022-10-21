package com.kongzue.runner.interfaces;

import android.app.Activity;
import android.util.Log;

import com.kongzue.runner.ViewModel;

import java.lang.ref.WeakReference;

/**
 * @author: Kongzue
 * @github: https://github.com/kongzue/
 * @homepage: http://kongzue.com/
 * @mail: myzcxhh@live.cn
 * @createTime: 2022/10/21 13:31
 */
public abstract class BaseModel {
    
    private WeakReference<Activity> activityWeakReference;
    private boolean lockRefreshUI;
    
    public void refreshUI() {
        synchronized (this) {
            if (!lockRefreshUI) {
                lockRefreshUI = true;
                ViewModel.bindActivity(getActivity());
                lockRefreshUI = false;
            }
        }
    }
    
    public Activity getActivity() {
        return activityWeakReference == null ? null : activityWeakReference.get();
    }
    
    public BaseModel setActivity(Activity activity) {
        this.activityWeakReference = new WeakReference<>(activity);
        return this;
    }
    
    public void lockRefreshUI() {
        lockRefreshUI = true;
    }
    
    public void unLockRefreshUI() {
        lockRefreshUI = false;
    }
}
