package com.growatt.shinetools.login;

public interface LoginListener {

    void ossloginSuccess();


    void ossLoginFail(String code,String msg);



    void serverLoginSuccess();

    void serverLoginFail(String code,String msg);

}
