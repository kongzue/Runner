package com.kongzue.messagebusdemo.util;

import com.kongzue.runner.interfaces.BaseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Kongzue
 * @github: https://github.com/kongzue/
 * @homepage: http://kongzue.com/
 * @mail: myzcxhh@live.cn
 * @createTime: 2022/10/21 10:39
 */
public class LoginInfo extends BaseModel {
    
    private String username;
    private String password;
    private boolean isRememberLogin;
    
    public LoginInfo(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public String getUsername() {
        return username;
    }
    
    public LoginInfo setUsername(String username) {
        this.username = username;
        refreshUI();
        return this;
    }
    
    public boolean isRememberLogin() {
        return isRememberLogin;
    }
    
    public LoginInfo setIsRememberLogin(boolean rememberLogin) {
        isRememberLogin = rememberLogin;
        refreshUI();
        return this;
    }
    
    public String getPassword() {
        return password;
    }
    
    public LoginInfo setPassword(String password) {
        this.password = password;
        refreshUI();
        return this;
    }
    
    @Override
    public String toString() {
        return "LoginInfo{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", isRememberLogin=" + isRememberLogin +
                '}';
    }
}
