package com.kongzue.runner;

import static com.kongzue.runner.Data.sendToAnyObject;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import com.kongzue.runner.interfaces.ActivityRunnable;
import com.kongzue.runner.interfaces.CustomDataSetter;
import com.kongzue.runner.util.AnyObjectSetter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Runner 中枢
 *
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
    
    /**
     * 初始化
     *
     * @param context   任意上下文
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
                topActivity = new WeakReference<>(activity);
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
                log("#onActivityResumed: " + activity.getClass().getName());
                topActivity = new WeakReference<>(activity);
                if (resumeRunnableList != null && !resumeRunnableList.isEmpty()) {
                    for (int i = resumeRunnableList.size() - 1; i >= 0; i--) {
                        ActivityRunnable runnable = resumeRunnableList.get(i);
                        if (Objects.equals(runnable.waitRunActivityClass, activity.getClass()) || Objects.equals(runnable.waitRunActivityName, activity.getClass().getSimpleName()) || (runnable.activityWeakReference != null && runnable.activityWeakReference.get() == activity)) {
                            runnable.run(activity);
                            resumeRunnableList.remove(runnable);
                        }
                    }
                }
                
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
                if (topActivity.get() == activity) {
                    topActivity.clear();
                }
            }
        });
    }
    
    /**
     * 绑定任意对象。
     * 建立绑定关系可以使 Runner 通过 Class/className 查找已经绑定的对象并向其成员推送内容。
     *
     * @param o 对象
     */
    public static void bindAnyObject(Object o) {
        if (anyObjectList == null) {
            anyObjectList = new CopyOnWriteArrayList<>();
        }
        anyObjectList.add(new WeakReference(o));
        if (o instanceof LifecycleOwner) {
            ((LifecycleOwner) o).getLifecycle().addObserver(new DefaultLifecycleObserver() {
                @Override
                public void onDestroy(@NonNull LifecycleOwner owner) {
                    DefaultLifecycleObserver.super.onDestroy(owner);
                    WeakReference weakReference = findObjectInAnyObjectList(owner);
                    if (weakReference != null) {
                        weakReference.clear();
                        anyObjectList.remove(weakReference);
                    }
                }
            });
        }
        //处理未处理的事务
        if (anyObjectSetterList != null && !anyObjectSetterList.isEmpty()) {
            for (int i = anyObjectSetterList.size() - 1; i >= 0; i--) {
                AnyObjectSetter anyObjectSetter = anyObjectSetterList.get(i);
                if (anyObjectSetter.getValue() != null) {
                    if (anyObjectSetter.getWaitObjectClass() != null) {
                        sendToAnyObject(anyObjectSetter.getWaitObjectClass(), anyObjectSetter.getKey(), anyObjectSetter.getValue());
                    }
                    if (anyObjectSetter.getWaitObjectName() != null) {
                        sendToAnyObject(anyObjectSetter.getWaitObjectName(), anyObjectSetter.getKey(), anyObjectSetter.getValue());
                    }
                }
                anyObjectSetterList.remove(anyObjectSetter);
            }
        }
    }
    
    private static WeakReference findObjectInAnyObjectList(Object o) {
        for (WeakReference weakReference : anyObjectList) {
            if (weakReference.get() == o) {
                return weakReference;
            }
        }
        return null;
    }
    
    /**
     * 解绑对象。
     *
     * @param o 对象
     */
    public static void unbindAnyObject(Object o) {
        if (anyObjectList == null) {
            anyObjectList = new CopyOnWriteArrayList<>();
        }
        for (WeakReference weakReference : anyObjectList) {
            if (weakReference == null || weakReference.get() == null || weakReference.get() == o) {
                anyObjectList.remove(weakReference);
            }
        }
    }
    
    protected static void log(Object o) {
        Log.i("Runner>>>", String.valueOf(o));
    }
    
    public static CopyOnWriteArrayList<WeakReference> getAnyObjectList() {
        return anyObjectList;
    }
    
    protected static Application application;
    protected static Application.ActivityLifecycleCallbacks activityLifecycle;
    protected static List<Activity> activityList;
    protected static CopyOnWriteArrayList<AnyObjectSetter> anyObjectSetterList;
    protected static CopyOnWriteArrayList<WeakReference> anyObjectList;
    protected static CopyOnWriteArrayList<ActivityRunnable> waitRunnableList;
    protected static CopyOnWriteArrayList<ActivityRunnable> resumeRunnableList;
    protected static WeakReference<Activity> topActivity;
}
