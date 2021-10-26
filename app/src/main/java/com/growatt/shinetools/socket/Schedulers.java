package com.growatt.shinetools.socket;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.utils.Mydialog;

public class Schedulers extends Handler {


    private ConnectHandler connectHandler;

    private SocketManager manager;

    public Schedulers(@NonNull Looper looper, ConnectHandler connectHandler,SocketManager manager) {
        super(looper);
        this.connectHandler = connectHandler;
        this.manager=manager;
    }


    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        int what = msg.what;
        switch (what) {
            case SocketClientUtil.SOCKET_SEND://Socket 连接成功
                Mydialog.Dismiss();
                manager.setDisConnect(false);
                connectHandler.connectSuccess();
                break;

            case SocketClientUtil.SOCKET_EXCETION_CLOSE://发送消息时捕获异常
                connectHandler.sendMsgFail();
                break;
            case SocketClientUtil.SOCKET_SERVER_SET://Socket连接异常
                Mydialog.Dismiss();
                connectHandler.connectFail();
                break;

            case SocketClientUtil.SOCKET_CLOSE://接收线程消息异常,关闭Socket也会抛出异常 可不处理

                break;




            case SocketClientUtil.SOCKET_SEND_MSG://发送消息的内容
                String sendMsg = (String) msg.obj;
                connectHandler.sendMessage(sendMsg);
                break;

            case SocketClientUtil.SOCKET_RECEIVE_MSG://接收字符串数组
                String receive = (String) msg.obj;
                connectHandler.receiveMessage(receive);
                break;

            case SocketClientUtil.SOCKET_RECEIVE_BYTES://接收字节数组
                byte[] bytes = (byte[]) msg.obj;
                connectHandler.receveByteMessage(bytes);
                break;


        }
    }



}
