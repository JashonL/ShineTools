package com.growatt.shinetools.bean;

import androidx.annotation.NonNull;

public class User {

    private String id;
    private String username;
    private String password;

    public User() {
    }

    public User(String id,String username, String password) {
        this.username = username;
        this.password = password;
        this.id=id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @NonNull
    @Override
    public String toString() {
        return "user=｛用户id：="+id+"\n"+"用户名称："+username+"\n"+"用户密码："+password+"}";
    }
}
