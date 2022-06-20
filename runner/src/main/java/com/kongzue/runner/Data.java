package com.kongzue.runner;

import static com.kongzue.runner.Event.waitRunOnActivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.kongzue.runner.interfaces.ActivityRunnable;
import com.kongzue.runner.interfaces.CustomDataSetter;
import com.kongzue.runner.interfaces.DataWatcher;
import com.kongzue.runner.interfaces.DataWatchers;
import com.kongzue.runner.interfaces.RootViewInterface;
import com.kongzue.runner.interfaces.SenderTarget;
import com.kongzue.runner.util.AnyObjectSetter;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 数据处理器
 *
 * @author: Kongzue
 * @github: https://github.com/kongzue/
 * @homepage: http://kongzue.com/
 * @mail: myzcxhh@live.cn
 * @createTime: 2022/6/20 16:51
 */
public class Data extends Runner{
    
    public static CustomDataSetter customDataSetter;
    
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
        if (activityList != null) {
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
                                preSetValue(view, data);
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
                                        preSetValue(view, data);
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
        if (anyObjectList != null) {
            for (WeakReference<Object> objectWeakReference : anyObjectList) {
                if (objectWeakReference.get() != null) {
                    Object object = objectWeakReference.get();
                    Field[] fields = object.getClass().getDeclaredFields();
                    for (int i = 0; i < fields.length; i++) {
                        Field field = fields[i];
                        if (field.isAnnotationPresent(DataWatcher.class)) {
                            DataWatcher dataWatcher = field.getAnnotation(DataWatcher.class);
                            if (dataWatcher != null && Objects.equals(key, dataWatcher.value())) {
                                try {
                                    field.setAccessible(true);
                                    View view = (View) field.get(object);
                                    preSetValue(view, data);
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
                                            View view = (View) field.get(object);
                                            preSetValue(view, data);
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
        }
    }
    
    public static void changeDataByTag(String key, Object data) {
        if (activityList != null) {
            for (Activity activity : activityList) {
                View view = activity.getWindow().getDecorView().findViewWithTag(key);
                if (view != null) {
                    preSetValue(view, data);
                }
            }
        }
        if (anyObjectList != null) {
            for (WeakReference<Object> objectWeakReference : anyObjectList) {
                if (objectWeakReference.get() != null) {
                    Object object = objectWeakReference.get();
                    if (object instanceof RootViewInterface) {
                        View rootView = ((RootViewInterface) object).getRootView();
                        if (rootView instanceof ViewGroup) {
                            View view = rootView.findViewWithTag(key);
                            if (view != null) {
                                preSetValue(view, data);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private static void preSetValue(View view, Object data) {
        if (view.getContext() instanceof Activity) {
            ((Activity) view.getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setValue(view, data);
                }
            });
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    setValue(view, data);
                }
            });
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
}
