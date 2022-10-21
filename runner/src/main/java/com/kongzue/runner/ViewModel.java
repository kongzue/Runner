package com.kongzue.runner;

import static com.kongzue.runner.util.ViewDataSetter.getMethodName;

import static java.lang.Character.toLowerCase;
import static java.lang.Character.toUpperCase;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kongzue.runner.interfaces.BaseModel;
import com.kongzue.runner.interfaces.BindModel;
import com.kongzue.runner.util.ViewDataSetter;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author: Kongzue
 * @github: https://github.com/kongzue/
 * @homepage: http://kongzue.com/
 * @mail: myzcxhh@live.cn
 * @createTime: 2022/10/21 10:41
 */
public class ViewModel {
    
    public static void bindActivity(Activity activity) {
        if (activity == null || activity.isDestroyed()) {
            return;
        }
        Field[] fields = activity.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (field.isAnnotationPresent(BindModel.class)) {
                try {
                    field.setAccessible(true);
                    Object data = field.get(activity);
                    try {
                        Method[] methods = data.getClass().getDeclaredMethods();
                        for (Method m : methods) {
                            if (m != null) {
                                if (m.getName().startsWith("get")) {
                                    String realKeyName = getPropertiesName(m.getName().substring(3));
                                    View view = activity.getWindow().getDecorView().findViewWithTag(realKeyName);
                                    if (view != null) {
                                        Object value = m.invoke(data);
                                        ViewDataSetter.setValue(view, value);
                                        addWatcher(view, data);
                                    }
                                }
                                if (m.getName().startsWith("is")) {
                                    String realKeyName = getPropertiesName(m.getName().substring(2));
                                    View view = activity.getWindow().getDecorView().findViewWithTag(realKeyName);
                                    if (view != null) {
                                        Object value = m.invoke(data);
                                        ViewDataSetter.setValue(view, value);
                                        addWatcher(view, data);
                                    } else {
                                        view = activity.getWindow().getDecorView().findViewWithTag(m.getName());
                                        if (view != null) {
                                            Object value = m.invoke(data);
                                            ViewDataSetter.setValue(view, value);
                                            addWatcher(view, data);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        if (Runner.DEBUGMODE) e.printStackTrace();
                    }
                    
                    Field[] ownFields = data.getClass().getDeclaredFields();
                    for (int d = 0; d < ownFields.length; d++) {
                        Field oField = ownFields[d];
                        oField.setAccessible(true);
                        View view = activity.getWindow().getDecorView().findViewWithTag(oField.getName());
                        if (view != null) {
                            ViewDataSetter.setValue(view, oField.get(data));
                            addWatcher(view, data);
                        }
                    }
                    
                    if (data instanceof BaseModel) {
                        ((BaseModel) data).setActivity(activity);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private static String getPropertiesName(String key) {
        char[] chars = key.toCharArray();
        if (chars.length > 1) {
            chars[0] = toLowerCase(chars[0]);
        } else {
            return key;
        }
        return String.valueOf(chars);
    }
    
    private static CopyOnWriteArrayList<WeakReference<TextWatcher>> textWatcherList = new CopyOnWriteArrayList<>();
    
    private static void addWatcher(View view, Object data) {
        if (view instanceof CompoundButton) {
            ((CompoundButton) view).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (data instanceof BaseModel) {
                        ((BaseModel) data).lockRefreshUI();
                    }
                    ViewDataSetter.setValue(data, (String) view.getTag(), isChecked);
                }
            });
        }
        if (view instanceof TextView) {
            TextWatcher watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
                }
                
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                
                }
                
                @Override
                public void afterTextChanged(Editable s) {
                    if (data instanceof BaseModel) {
                        ((BaseModel) data).lockRefreshUI();
                    }
                    ViewDataSetter.setValue(data, (String) view.getTag(), s.toString());
                }
            };
            for (WeakReference<TextWatcher> textWatcherWeakReference : textWatcherList) {
                if (textWatcherWeakReference != null && textWatcherWeakReference.get() != null) {
                    ((TextView) view).removeTextChangedListener(textWatcherWeakReference.get());
                    textWatcherList.remove(textWatcherWeakReference);
                }
            }
            ((TextView) view).addTextChangedListener(watcher);
            textWatcherList.add(new WeakReference<>(watcher));
        }
    }
}
