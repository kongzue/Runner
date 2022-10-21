package com.kongzue.messagebusdemo.util;

import android.graphics.Bitmap;

import com.kongzue.runner.Runner;
import com.kongzue.runner.interfaces.SenderTarget;

/**
 * @author: Kongzue
 * @github: https://github.com/kongzue/
 * @homepage: http://kongzue.com/
 * @mail: myzcxhh@live.cn
 * @createTime: 2022/5/8 11:13
 */
public class User {
    
    public User() {
        Runner.bindAnyObject(this);
    }
    
    private String name;
    
    private int age;
    
    @SenderTarget("avatar")
    private Bitmap bitmap;
    
    public String getName() {
        return name;
    }
    
    public User setName(String name) {
        this.name = name;
        return this;
    }
    
    public Bitmap getAvatar() {
        return bitmap;
    }
    
    public User setAvatar(Bitmap avatar) {
        this.bitmap = avatar;
        return this;
    }
    
    public int getAge() {
        return age;
    }
    
    public User setAge(int age) {
        this.age = age;
        return this;
    }
    
    @Override
    public String toString() {
        return "User{\n" +
                "name='" + name + '\'' +
                ", \nage=" + age +
                ", \navatar(byteCount)=" + (bitmap == null ? 0 : bitmap.getByteCount()) +
                "\n}";
    }
}
