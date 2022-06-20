package com.kongzue.runner;

import android.app.Activity;

import com.kongzue.runner.interfaces.ActivityRunnable;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 事件处理器
 *
 * @author: Kongzue
 * @github: https://github.com/kongzue/
 * @homepage: http://kongzue.com/
 * @mail: myzcxhh@live.cn
 * @createTime: 2022/6/20 16:49
 */
public class Event extends Runner{
    /**
     * 在指定 Activity 上执行事件 ActivityRunnable
     *
     * @param activity         上下文
     * @param activityRunnable 事件体
     */
    public static void runOnActivity(Activity activity, ActivityRunnable activityRunnable) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activityRunnable.run(activity);
            }
        });
    }
    
    /**
     * 在指定已知 Activity 的 Class 但不确定是否实例化的 Activity 上执行事件 ActivityRunnable
     *
     * @param activityClass    Activity 的 Class
     * @param activityRunnable 事件体
     */
    public static void runOnActivity(Class activityClass, ActivityRunnable activityRunnable) {
        if (activityList == null || activityList.isEmpty()) {
            return;
        }
        for (int i = activityList.size() - 1; i >= 0; i--) {
            if (activityList.get(i).getClass() == activityClass) {
                runOnActivity(activityList.get(i), activityRunnable);
                return;
            }
        }
        waitRunOnActivity(activityClass, activityRunnable);
    }
    
    /**
     * 在指定已知 Activity 的类名但不确定是否实例化的 Activity 上执行事件 ActivityRunnable
     *
     * @param activityName     Activity 的类名
     * @param activityRunnable 事件体
     */
    public static void runOnActivity(String activityName, ActivityRunnable activityRunnable) {
        if (activityList == null || activityList.isEmpty()) {
            return;
        }
        for (int i = activityList.size() - 1; i >= 0; i--) {
            if (Objects.equals(activityList.get(i).getClass().getSimpleName(), activityName)) {
                runOnActivity(activityList.get(i), activityRunnable);
                return;
            }
        }
        waitRunOnActivity(activityName, activityRunnable);
    }
    
    /**
     * 等待在已知 Class 的新创建的 Activity 上执行事件 ActivityRunnable
     *
     * @param activityClass    Activity 的 Class
     * @param activityRunnable 事件体
     */
    public static void waitRunOnActivity(Class activityClass, ActivityRunnable activityRunnable) {
        if (waitRunnableList == null) {
            waitRunnableList = new CopyOnWriteArrayList<>();
        }
        activityRunnable.waitRunActivityClass = activityClass;
        waitRunnableList.add(activityRunnable);
    }
    
    /**
     * 等待在已知类名的新创建的 Activity 上执行事件 ActivityRunnable
     *
     * @param activityName     Activity 的类名
     * @param activityRunnable 事件体
     */
    public static void waitRunOnActivity(String activityName, ActivityRunnable activityRunnable) {
        if (waitRunnableList == null) {
            waitRunnableList = new CopyOnWriteArrayList<>();
        }
        activityRunnable.waitRunActivityName = activityName;
        waitRunnableList.add(activityRunnable);
    }
    
    
    /**
     * 等待指定 Activity 恢复至前台时执行事件 ActivityRunnable
     * 若指定 Activity 已经在前台，会立即执行事件
     *
     * @param activity         上下文
     * @param activityRunnable 事件体
     */
    public static void runOnResume(Activity activity, ActivityRunnable activityRunnable) {
        if (resumeRunnableList == null) {
            resumeRunnableList = new CopyOnWriteArrayList<>();
        }
        if (topActivity != null && activity == topActivity.get()) {
            runOnActivity(activity, activityRunnable);
        } else {
            activityRunnable.activityWeakReference = new WeakReference(activity);
            resumeRunnableList.add(activityRunnable);
        }
    }
    
    /**
     * 在指定已知 Activity 的 Class 但不确定是否实例化的 Activity 上执行事件 ActivityRunnable
     *
     * @param activityClass    Activity 的 Class
     * @param activityRunnable 事件体
     */
    public static void runOnResume(Class activityClass, ActivityRunnable activityRunnable) {
        if (activityList == null || activityList.isEmpty()) {
            return;
        }
        for (int i = activityList.size() - 1; i >= 0; i--) {
            if (activityList.get(i).getClass() == activityClass) {
                runOnResume(activityList.get(i), activityRunnable);
                return;
            }
        }
        waitRunOnResume(activityClass, activityRunnable);
    }
    
    /**
     * 在指定已知 Activity 的类名但不确定是否实例化的 Activity 上执行事件 ActivityRunnable
     *
     * @param activityName     Activity 的类名
     * @param activityRunnable 事件体
     */
    public static void runOnResume(String activityName, ActivityRunnable activityRunnable) {
        if (activityList == null || activityList.isEmpty()) {
            return;
        }
        for (int i = activityList.size() - 1; i >= 0; i--) {
            if (Objects.equals(activityList.get(i).getClass().getSimpleName(), activityName)) {
                runOnResume(activityList.get(i), activityRunnable);
                return;
            }
        }
        waitRunOnResume(activityName, activityRunnable);
    }
    
    /**
     * 等待在已知 Class 的新创建的 Activity 上的 onResume 时执行事件 ActivityRunnable
     *
     * @param activityClass    Activity 的 Class
     * @param activityRunnable 事件体
     */
    public static void waitRunOnResume(Class activityClass, ActivityRunnable activityRunnable) {
        if (resumeRunnableList == null) {
            resumeRunnableList = new CopyOnWriteArrayList<>();
        }
        activityRunnable.waitRunActivityClass = activityClass;
        resumeRunnableList.add(activityRunnable);
    }
    
    /**
     * 等待在已知类名的新创建的 Activity 上的 onResume 时执行事件 ActivityRunnable
     *
     * @param activityName     Activity 的类名
     * @param activityRunnable 事件体
     */
    public static void waitRunOnResume(String activityName, ActivityRunnable activityRunnable) {
        if (resumeRunnableList == null) {
            resumeRunnableList = new CopyOnWriteArrayList<>();
        }
        activityRunnable.waitRunActivityName = activityName;
        resumeRunnableList.add(activityRunnable);
    }

}
