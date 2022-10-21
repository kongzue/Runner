package com.kongzue.runner.util;

import static java.lang.Character.toLowerCase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.kongzue.runner.Runner;
import com.kongzue.runner.interfaces.BaseModel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author: Kongzue
 * @github: https://github.com/kongzue/
 * @homepage: http://kongzue.com/
 * @mail: myzcxhh@live.cn
 * @createTime: 2022/10/21 18:09
 */
public class AutoCreateListViewAdapter extends BaseAdapter {
    
    private List data;
    private Context context;
    private LayoutInflater mInflater;
    private int itemLayoutResId;
    
    public AutoCreateListViewAdapter(Context context, List data, int itemLayoutResId) {
        this.context = context;
        this.itemLayoutResId = itemLayoutResId;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
    }
    
    @Override
    public int getCount() {
        return data.size();
    }
    
    @Override
    public Object getItem(int position) {
        return data.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(itemLayoutResId, null);
        }
        Object data = getItem(position);
        try {
            Method[] methods = data.getClass().getDeclaredMethods();
            for (Method m : methods) {
                if (m != null) {
                    if (m.getName().startsWith("get")) {
                        String realKeyName = getPropertiesName(m.getName().substring(3));
                        View view = convertView.findViewWithTag(realKeyName);
                        if (view != null) {
                            Object value = m.invoke(data);
                            ViewDataSetter.setValue(view, value);
                        }
                    }
                    if (m.getName().startsWith("is")) {
                        String realKeyName = getPropertiesName(m.getName().substring(2));
                        View view = convertView.findViewWithTag(realKeyName);
                        if (view != null) {
                            Object value = m.invoke(data);
                            ViewDataSetter.setValue(view, value);
                        } else {
                            view = convertView.findViewWithTag(m.getName());
                            if (view != null) {
                                Object value = m.invoke(data);
                                ViewDataSetter.setValue(view, value);
                            }
                        }
                    }
                }
            }
            
            Field[] ownFields = data.getClass().getDeclaredFields();
            for (int d = 0; d < ownFields.length; d++) {
                Field oField = ownFields[d];
                oField.setAccessible(true);
                View view = convertView.findViewWithTag(oField.getName());
                if (view != null) {
                    ViewDataSetter.setValue(view, oField.get(data));
                }
            }
        } catch (Exception e) {
            if (Runner.DEBUGMODE) e.printStackTrace();
        }
        
        return convertView;
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
}
