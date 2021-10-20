package com.growatt.shinetools.socket;

import android.os.Looper;

import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;

public class SocketManager {

    //连接对象
    private SocketClientUtil mClientUtil;
    private Schedulers mHandler;

    public SocketManager() {
        mClientUtil = SocketClientUtil.newInstance();
    }

    public void onConect(ConnectHandler connectHandler){
        mHandler = new Schedulers(Looper.getMainLooper(),connectHandler);
    }

    /**
     * 连接Socket
     */
    public void connectSocket() {
        if (mClientUtil != null) {
            mClientUtil.connect(mHandler);
        }
    }
    /**
     * 断开Socket连接
     */
    public void disConnectSocket() {
        SocketClientUtil.close(mClientUtil);
    }

    /**
     * 根据命令以及起始寄存器发送查询命令
     *
     * @param sends
     * @return：返回发送的字节数组
     */
    public byte[] sendMsg(int[] sends) {
        if (mClientUtil != null) {
            byte[] sendBytes = ModbusUtil.sendMsg(sends[0], sends[1], sends[2]);
            mClientUtil.sendMsg(sendBytes);
            return sendBytes;
        } else {
            connectSocket();
            return null;
        }
    }


}
