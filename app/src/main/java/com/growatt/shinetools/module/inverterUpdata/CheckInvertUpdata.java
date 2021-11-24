package com.growatt.shinetools.module.inverterUpdata;

import android.content.Context;
import android.os.Handler;

import androidx.fragment.app.FragmentActivity;

import com.growatt.shinetools.R;
import com.growatt.shinetools.modbusbox.MaxWifiParseUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.socket.ConnectHandler;
import com.growatt.shinetools.socket.SocketManager;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.MyControl;

/**
 * 检测升级
 */
public class CheckInvertUpdata {


    private SocketManager manager;
    private Context context;


    public CheckInvertUpdata(SocketManager manager, Context context) {
        this.manager = manager;
        this.context = context;


        //1.去连接TCP
        connetSocket();

    }


    private void connetSocket() {
        //初始化连接
        manager = new SocketManager(context);
        //设置连接监听
        manager.onConect(connectHandler);
        //开始连接TCP
        //延迟一下避免频繁操作
        new Handler().postDelayed(() -> manager.connectSocket(), 100);
    }


    ConnectHandler connectHandler = new ConnectHandler() {
        @Override
        public void connectSuccess() {
            getCurrentVersion();
        }

        @Override
        public void connectFail() {
            manager.disConnectSocket();
            MyControl.showJumpWifiSet((FragmentActivity) context);
        }

        @Override
        public void sendMsgFail() {
            manager.disConnectSocket();
            MyControl.showTcpDisConnect((FragmentActivity) context, context.getString(R.string.disconnet_retry),
                    () -> {
                        connetSocket();
                    }
            );
        }

        @Override
        public void sendMessage(String msg) {
            LogUtil.i("发送的消息:" + msg);
        }

        @Override
        public void receiveMessage(String msg) {
            LogUtil.i("接收的消息:" + msg);
        }

        @Override
        public void receveByteMessage(byte[] bytes) {
            //解析
            //检测内容正确性
            boolean isCheck = ModbusUtil.checkModbus(bytes);
            if (isCheck) {
                //接收正确，开始解析
                parseCurrentVersion(bytes);
            }

        }
    };


    /**
     * 根据传进来的mtype解析数据
     *
     * @param bytes
     */
    private void parseCurrentVersion(byte[] bytes) {
        //移除外部协议
        byte[] bs = RegisterParseUtil.removePro17(bytes);
        //解析int值
        int value = MaxWifiParseUtil.obtainValueOne(bs);


    }


    /**
     * 请求获取当前软件的版本
     */
    private void getCurrentVersion() {
        int[] funs = new int[]{4,6500,6507};
        manager.sendMsg(funs);
    }


}