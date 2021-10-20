package com.growatt.shinetools.socket;


/**
 * TCP连接成功回调
 */
public interface ConnectHandler {

    //连接成功
    void connectSuccess();

    void connectFail();

    void disconnect();

    void sendMessage(String msg);

    void receiveMessage(String msg);

    void receveByteMessage(byte[] bytes);



}
