package com.growatt.shinetools.socket;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.growatt.shinetools.modbusbox.SocketClientUtil;

public class Schedulers extends Handler {


    private ConnectHandler connectHandler;


    public Schedulers(@NonNull Looper looper, ConnectHandler connectHandler) {
        super(looper);
        this.connectHandler = connectHandler;
    }

    @NonNull
    @Override
    public String getMessageName(@NonNull Message message) {
        int what = message.what;
        switch (what) {
            case SocketClientUtil.SOCKET_OPEN://Socket连接成功
            case SocketClientUtil.SOCKET_SEND://Socket 连接成功
                connectHandler.connectSuccess();
                break;

            case SocketClientUtil.SOCKET_SERVER_SET://Socket连接异常
            case SocketClientUtil.SOCKET_CLOSE://接收消息异常
                connectHandler.connectFail();
                break;


            case SocketClientUtil.SOCKET_SEND_MSG://发送消息的内容
                String sendMsg = (String) message.obj;
                connectHandler.sendMessage(sendMsg);
                break;

            case SocketClientUtil.SOCKET_RECEIVE_MSG://接收字符串数组
                String receive = (String) message.obj;
                connectHandler.receiveMessage(receive);
                break;

            case SocketClientUtil.SOCKET_RECEIVE_BYTES://接收字节数组
                byte[] bytes = (byte[]) message.obj;
                connectHandler.receveByteMessage(bytes);
                break;





        }
        return super.getMessageName(message);
    }


}
