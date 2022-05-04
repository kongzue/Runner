package com.kongzue.runner;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author: Kongzue
 * @github: https://github.com/kongzue/
 * @homepage: http://kongzue.com/
 * @mail: myzcxhh@live.cn
 * @createTime: 2022/5/4 13:31
 */
public class Runner {
    
    /**
     * 是否打印日志
     */
    public static boolean DEBUGMODE = true;
    
    private static Application application;
    private static Application.ActivityLifecycleCallbacks activityLifecycle;
    private static List<Activity> activityList;
    private static CopyOnWriteArrayList<ActivityRunnable> waitRunnableList;
    
    /**
     * 初始化
     *
     * @param context 任意上下文
     * @param debugMode 是否开启日志打印
     */
    public static void init(Context context, boolean debugMode) {
        DEBUGMODE = debugMode;
        init(context);
    }
    
    /**
     * 初始化
     *
     * @param context 任意上下文
     */
    public static void init(Context context) {
        application = (Application) context.getApplicationContext();
        if (activityLifecycle != null) {
            application.unregisterActivityLifecycleCallbacks(activityLifecycle);
        }
        application.registerActivityLifecycleCallbacks(activityLifecycle = new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                log("#onActivityCreated: " + activity.getClass().getName());
                if (activityList == null) {
                    activityList = new ArrayList<>();
                }
                activityList.add(activity);
                if (waitRunnableList != null && !waitRunnableList.isEmpty()) {
                    for (int i = waitRunnableList.size() - 1; i >= 0; i--) {
                        ActivityRunnable runnable = waitRunnableList.get(i);
                        if (Objects.equals(runnable.waitRunActivityClass, activity.getClass()) || Objects.equals(runnable.waitRunActivityName, activity.getClass().getSimpleName())) {
                            runnable.run(activity);
                            waitRunnableList.remove(runnable);
                        }
                    }
                }
            }
            
            @Override
            public void onActivityStarted(@NonNull Activity activity) {
            
            }
            
            @Override
            public void onActivityResumed(@NonNull Activity activity) {
            
            }
            
            @Override
            public void onActivityPaused(@NonNull Activity activity) {
            
            }
            
            @Override
            public void onActivityStopped(@NonNull Activity activity) {
            
            }
            
            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
            
            }
            
            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                log("#onActivityDestroyed: " + activity.getClass().getName());
                activityList.remove(activity);
            }
        });
    }
    
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
     * 发送任意对象到指定 Activity
     *
     * @param activity 实例化的 Activity 对象
     * @param key      activity 中已存在的成员名称，或使用 @SenderTarget(...) 注解修饰的 key
     * @param value    赋值的内容
     */
    public static void sendToActivity(Activity activity, String key, Object value) {
        try {
            setValue(activity, key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 发送任意对象到指定 Activity
     *
     * @param activityClass 不确定是否实例化的 Activity 的 Class
     * @param key           activity 中已存在的成员名称，或使用 @SenderTarget(...) 注解修饰的 key
     * @param value         赋值的内容
     */
    public static void sendToActivity(Class activityClass, String key, Object value) {
        if (activityList == null || activityList.isEmpty()) {
            return;
        }
        for (int i = activityList.size() - 1; i >= 0; i--) {
            if (activityList.get(i).getClass() == activityClass) {
                sendToActivity(activityList.get(i), key, value);
                return;
            }
        }
        waitRunOnActivity(activityClass, new ActivityRunnable() {
            @Override
            public void run(Activity activity) {
                sendToActivity(activity, key, value);
            }
        });
    }
    
    /**
     * 发送任意对象到指定 Activity
     *
     * @param activityName 不确定是否实例化的 Activity 的类名
     * @param key          activity 中已存在的成员名称，或使用 @SenderTarget(...) 注解修饰的 key
     * @param value        赋值的内容
     */
    public static void sendToActivity(String activityName, String key, Object value) {
        if (activityList == null || activityList.isEmpty()) {
            return;
        }
        for (int i = activityList.size() - 1; i >= 0; i--) {
            if (Objects.equals(activityList.get(i).getClass().getSimpleName(), activityName)) {
                sendToActivity(activityList.get(i), key, value);
                return;
            }
        }
        waitRunOnActivity(activityName, new ActivityRunnable() {
            @Override
            public void run(Activity activity) {
                sendToActivity(activity, key, value);
            }
        });
    }
    
    private static void setValue(Activity activityClass, String key, Object value) {
        Field[] fields = activityClass.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getName().equals(key)) {
                try {
                    fields[i].setAccessible(true);
                    fields[i].set(activityClass, value);
                } catch (IllegalAccessException e) {
                    log("写入 " + key + " 的值： " + value + " 失败！");
                    e.printStackTrace();
                }
                return;
            }
            if (fields[i].isAnnotationPresent(SenderTarget.class)) {
                SenderTarget senderTarget = fields[i].getAnnotation(SenderTarget.class);
                if (senderTarget != null && Objects.equals(key, senderTarget.value())) {
                    try {
                        fields[i].setAccessible(true);
                        fields[i].set(activityClass, value);
                    } catch (IllegalAccessException e) {
                        log("写入 " + key + " 的值： " + value + " 失败！");
                        e.printStackTrace();
                    }
                    return;
                }
            }
        }
    }
    
    private static void log(Object o) {
        Log.i("Runner>>>", String.valueOf(o));
    }
}
