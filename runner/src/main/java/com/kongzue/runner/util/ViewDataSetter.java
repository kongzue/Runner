package com.kongzue.runner.util;

import static com.kongzue.runner.Runner.customDataSetter;
import static java.lang.Character.toUpperCase;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.kongzue.runner.Runner;
import com.kongzue.runner.interfaces.AutoCreateListViewInterface;
import com.kongzue.runner.interfaces.BaseModel;
import com.kongzue.runner.interfaces.SenderTarget;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

/**
 * @author: Kongzue
 * @github: https://github.com/kongzue/
 * @homepage: http://kongzue.com/
 * @mail: myzcxhh@live.cn
 * @createTime: 2022/10/21 10:50
 */
public class ViewDataSetter {
    
    public static void setValue(View view, Object data) {
        if (customDataSetter == null || !customDataSetter.setData(view, data)) {
            if (view != null) {
                if (view instanceof CompoundButton) {
                    CompoundButton compoundButton = (CompoundButton) view;
                    if (data instanceof Boolean) {
                        compoundButton.setChecked(true == (boolean) data);
                    } else {
                        compoundButton.setChecked(true == Boolean.valueOf(String.valueOf(data)));
                    }
                } else if (view instanceof TextView) {
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
                } else if (view instanceof AutoCreateListViewInterface) {
                    AutoCreateListViewInterface listView = (AutoCreateListViewInterface) view;
                    if (data instanceof ListAdapter) {
                        listView.setAdapter((ListAdapter) data);
                    } else if (data instanceof List) {
                        if (listView.getAdapter() instanceof BaseAdapter) {
                            ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
                        } else {
                            int itemLayoutRes = listView.itemLayoutRes();
                            if (itemLayoutRes != 0) {
                                listView.setAdapter(new AutoCreateListViewAdapter(listView.getContext(), (List) data, itemLayoutRes));
                            }
                        }
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
    
    public static void setValue(Object ownerClass, String key, Object value) {
        if (key != null && !key.isEmpty()) {
            try {
                String methodName = getMethodName(key);
                Method[] methods = ownerClass.getClass().getDeclaredMethods();
                for (Method m : methods) {
                    if (m != null && Objects.equals("set" + methodName, m.getName())) {
                        m.invoke(ownerClass, value);
                        if (ownerClass instanceof BaseModel) {
                            ((BaseModel) ownerClass).unLockRefreshUI();
                        }
                        return;
                    }
                }
            } catch (Exception e) {
                if (Runner.DEBUGMODE) e.printStackTrace();
            }
            Field[] fields = ownerClass.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].getName().equals(key)) {
                    try {
                        fields[i].setAccessible(true);
                        fields[i].set(ownerClass, value);
                    } catch (IllegalAccessException e) {
                        if (Runner.DEBUGMODE) e.printStackTrace();
                    }
                    if (ownerClass instanceof BaseModel) {
                        ((BaseModel) ownerClass).unLockRefreshUI();
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
                            if (Runner.DEBUGMODE) e.printStackTrace();
                        }
                        if (ownerClass instanceof BaseModel) {
                            ((BaseModel) ownerClass).unLockRefreshUI();
                        }
                        return;
                    }
                }
            }
        }
        if (ownerClass instanceof BaseModel) {
            ((BaseModel) ownerClass).unLockRefreshUI();
        }
    }
    
    public static String getMethodName(String key) {
        char[] chars = key.toCharArray();
        if (chars.length > 1) {
            chars[0] = toUpperCase(chars[0]);
        } else {
            return key;
        }
        return String.valueOf(chars);
    }
}
