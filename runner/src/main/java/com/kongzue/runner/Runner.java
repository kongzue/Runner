package com.kongzue.runner;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    public static CustomDataSetter customDataSetter;
    
    private static Application application;
    private static Application.ActivityLifecycleCallbacks activityLifecycle;
    private static List<Activity> activityList;
    private static CopyOnWriteArrayList<AnyObjectSetter> anyObjectSetterList;
    private static CopyOnWriteArrayList<WeakReference> anyObjectList;
    private static CopyOnWriteArrayList<ActivityRunnable> waitRunnableList;
    private static CopyOnWriteArrayList<ActivityRunnable> resumeRunnableList;
    private static WeakReference<Activity> topActivity;
    
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
     * 发送任意对象到指定对象
     *
     * @param owner 实例化的对象
     * @param key   owner 中已存在的成员名称，或使用 @SenderTarget(...) 注解修饰的 key
     * @param value 赋值的内容
     */
    public static void sendToAnyObject(Object owner, String key, Object value) {
        try {
            setValue(owner, key, value);
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
     * 发送任意对象到指定对象
     * 请在 objClass 创建时，使用 #bindAnyObject(objClass) 绑定到 Runner
     *
     * @param objClass 不确定是否实例化的 object 的 Class
     * @param key      object 中已存在的成员名称，或使用 @SenderTarget(...) 注解修饰的 key
     * @param value    赋值的内容
     */
    public static void sendToAnyObject(Class objClass, String key, Object value) {
        if (anyObjectList == null || anyObjectList.isEmpty()) {
            waitSetValue(objClass, key, value);
            return;
        }
        for (int i = anyObjectList.size() - 1; i >= 0; i--) {
            if (anyObjectList.get(i) != null && anyObjectList.get(i).get() != null) {
                if (anyObjectList.get(i).get().getClass() == objClass) {
                    sendToAnyObject(anyObjectList.get(i).get(), key, value);
                    return;
                }
            }
        }
        waitSetValue(objClass, key, value);
    }
    
    private static void waitSetValue(Class objClass, String key, Object value) {
        if (anyObjectSetterList == null) {
            anyObjectSetterList = new CopyOnWriteArrayList<>();
        }
        anyObjectSetterList.add(new AnyObjectSetter(objClass, key, value));
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
    
    /**
     * 发送任意对象到指定对象
     * 请在对象创建时，使用 #bindAnyObject(objClass) 绑定到 Runner
     *
     * @param objectName 不确定是否实例化的对象的类名
     * @param key        对象中已存在的成员名称，或使用 @SenderTarget(...) 注解修饰的 key
     * @param value      赋值的内容
     */
    public static void sendToAnyObject(String objectName, String key, Object value) {
        if (anyObjectList == null || anyObjectList.isEmpty()) {
            waitSetValue(objectName, key, value);
            return;
        }
        for (int i = anyObjectList.size() - 1; i >= 0; i--) {
            if (anyObjectList.get(i) != null && anyObjectList.get(i).get() != null) {
                if (Objects.equals(anyObjectList.get(i).get().getClass().getSimpleName(), objectName)) {
                    sendToAnyObject(anyObjectList.get(i).get(), key, value);
                    return;
                }
            }
        }
        waitSetValue(objectName, key, value);
    }
    
    private static void waitSetValue(String objectName, String key, Object value) {
        if (anyObjectSetterList == null) {
            anyObjectSetterList = new CopyOnWriteArrayList<>();
        }
        anyObjectSetterList.add(new AnyObjectSetter(objectName, key, value));
    }
    
    private static void setValue(Object ownerClass, String key, Object value) {
        Field[] fields = ownerClass.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getName().equals(key)) {
                try {
                    fields[i].setAccessible(true);
                    fields[i].set(ownerClass, value);
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
                        fields[i].set(ownerClass, value);
                    } catch (IllegalAccessException e) {
                        log("写入 " + key + " 的值： " + value + " 失败！");
                        e.printStackTrace();
                    }
                    return;
                }
            }
        }
    }
    
    public static void changeData(String key, Object data) {
        if (activityList == null) {
            return;
        }
        for (Activity activity : activityList) {
            Field[] fields = activity.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                if (field.isAnnotationPresent(DataWatcher.class)) {
                    DataWatcher dataWatcher = field.getAnnotation(DataWatcher.class);
                    if (dataWatcher != null && Objects.equals(key, dataWatcher.value())) {
                        try {
                            field.setAccessible(true);
                            View view = (View) field.get(activity);
                            setValue(view, data);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (field.isAnnotationPresent(DataWatchers.class)) {
                    DataWatchers dataWatchers = field.getAnnotation(DataWatchers.class);
                    if (dataWatchers != null) {
                        String[] keys = dataWatchers.value();
                        if (keys.length > 0) {
                            if (Arrays.asList(keys).contains(key)) {
                                try {
                                    field.setAccessible(true);
                                    View view = (View) field.get(activity);
                                    setValue(view, data);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public static void changeDataByTag(String key, Object data) {
        if (activityList == null) {
            return;
        }
        for (Activity activity : activityList) {
            View view = activity.getWindow().getDecorView().findViewWithTag(key);
            if (view != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setValue(view, data);
                    }
                });
            }
        }
    }
    
    private static void setValue(View view, Object data) {
        if (view != null) {
            if (customDataSetter == null || !customDataSetter.setData(view, data)) {
                if (view instanceof TextView) {
                    TextView textview = (TextView) view;
                    if (data instanceof CharSequence) {
                        textview.setText(String.valueOf(data));
                    } else if (data instanceof Integer) {
                        textview.setText((int) data);
                    }
                } else if (view instanceof ImageView) {
                    ImageView imageView = (ImageView) view;
                    if (data instanceof Bitmap) {
                        imageView.setImageBitmap((Bitmap) data);
                    } else if (data instanceof Integer) {
                        imageView.setImageResource((int) data);
                    } else if (data instanceof Drawable) {
                        imageView.setImageDrawable((Drawable) data);
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (data instanceof Icon) {
                            imageView.setImageIcon((Icon) data);
                        }
                    } else if (data instanceof Uri) {
                        imageView.setImageURI((Uri) data);
                    }
                } else if (view instanceof ListView) {
                    ListView listView = (ListView) view;
                    if (data instanceof ListAdapter) {
                        listView.setAdapter((ListAdapter) data);
                    } else if (data instanceof List) {
                        if (listView.getAdapter() instanceof BaseAdapter) {
                            ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
                        }
                    }
                }
            }
        }
    }
    
    private static void log(Object o) {
        Log.i("Runner>>>", String.valueOf(o));
    }
}
