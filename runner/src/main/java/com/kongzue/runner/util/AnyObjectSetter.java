package com.kongzue.runner.util;

import java.lang.ref.WeakReference;

/**
 * @author: Kongzue
 * @github: https://github.com/kongzue/
 * @homepage: http://kongzue.com/
 * @mail: myzcxhh@live.cn
 * @createTime: 2022/5/8 10:56
 */
public class AnyObjectSetter {
    
    private Class waitObjectClass;
    private String waitObjectName;
    private String key;
    private WeakReference value;
    
    public AnyObjectSetter(String waitObjectName, String key, Object value) {
        this.waitObjectName = waitObjectName;
        this.key = key;
        this.value = new WeakReference(value);
    }
    
    public AnyObjectSetter(Class waitObjectClass, String key, Object value) {
        this.waitObjectClass = waitObjectClass;
        this.key = key;
        this.value = new WeakReference(value);
    }
    
    public Class getWaitObjectClass() {
        return waitObjectClass;
    }
    
    public AnyObjectSetter setWaitObjectClass(Class waitObjectClass) {
        this.waitObjectClass = waitObjectClass;
        return this;
    }
    
    public String getWaitObjectName() {
        return waitObjectName;
    }
    
    public AnyObjectSetter setWaitObjectName(String waitObjectName) {
        this.waitObjectName = waitObjectName;
        return this;
    }
    
    public String getKey() {
        return key;
    }
    
    public AnyObjectSetter setKey(String key) {
        this.key = key;
        return this;
    }
    
    public Object getValue() {
        return value == null ? null : value.get();
    }
    
    public AnyObjectSetter setValue(Object value) {
        this.value = new WeakReference(value);
        return this;
    }
}
