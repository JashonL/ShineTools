package com.growatt.shinetools.socket;

import android.content.Context;
import android.os.Looper;

import com.growatt.shinetools.R;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.module.inverterUpdata.UpdataUtils;
import com.growatt.shinetools.utils.MyToastUtils;
import com.growatt.shinetools.utils.Mydialog;
import com.hjq.toast.ToastUtils;

public class SocketManager {

    private Context mContext;

    //连接对象
    private SocketClientUtil mClientUtil;
    private Schedulers mHandler;

    //是否已连接
    private boolean isConnect = true;

    public SocketManager(Context context) {
        mClientUtil = SocketClientUtil.newInstance();
        mContext = context;
    }

    public void onConect(ConnectHandler connectHandler) {
        Mydialog.Show(mContext);
        mHandler = new Schedulers(Looper.getMainLooper(), connectHandler,this);
    }


    public void onConectNoDialog(ConnectHandler connectHandler) {
        mHandler = new Schedulers(Looper.getMainLooper(), connectHandler,this);
    }

    /**
     * 连接Socket
     */
    public void connectSocket() {
        mClientUtil = SocketClientUtil.newInstance();
        mClientUtil.connect(mHandler);
    }

    /**
     * 断开Socket连接
     */
    public void disConnectSocket() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        SocketClientUtil.close(mClientUtil);
        mClientUtil = null;
        setDisConnect(true);
    }


    /**
     * 根据命令以及起始寄存器发送查询命令
     *
     * @param sends
     * @return：返回发送的字节数组
     */
    public byte[] sendMsg(int[] sends) {
        Mydialog.Show(mContext);
        if (mClientUtil != null) {
            byte[] sendBytes = ModbusUtil.sendMsg(sends[0], sends[1], sends[2]);
            mClientUtil.sendMsg(sendBytes);
            return sendBytes;
        } else {
            Mydialog.Dismiss();
            MyToastUtils.toast(R.string.android_key2583);
            return null;
        }
    }



    /**
     * 根据命令以及起始寄存器发送查询命令
     *
     * @param bytes
     * @return：返回发送的字节数组
     */
    public void sendBytes(byte[] bytes) {
        if (mClientUtil != null) {
            mClientUtil.sendMsg(bytes);
        } else {
            Mydialog.Dismiss();
            MyToastUtils.toast(R.string.android_key2583);
        }
    }



    /**
     * 根据命令以及起始寄存器发送查询命令
     *
     * @return：返回发送的字节数组
     */
    public byte[] sendMsg17(int fun,int subfun, byte[] values) {
        if (mClientUtil != null) {
            byte[] sendBytes = UpdataUtils.sendMsg17(fun, subfun, values);
            mClientUtil.sendMsg(sendBytes);
            return sendBytes;
        } else {
            Mydialog.Dismiss();
            MyToastUtils.toast(R.string.android_key3129);
            return null;
        }
    }




    /**
     * 根据命令以及起始寄存器发送查询命令
     *
     * @return：返回发送的字节数组
     */
    public byte[] sendMsg1705(int fun,int subfun,int num, byte[] values) {
        if (mClientUtil != null) {
            byte[] sendBytes = UpdataUtils.sendMsg1705(fun, subfun,num, values);
            mClientUtil.sendMsg(sendBytes);
            return sendBytes;
        } else {
            Mydialog.Dismiss();
            MyToastUtils.toast(R.string.android_key3129);
            return null;
        }
    }



    /**
     * 发送查询进度指令
     *
     * @return：返回发送的字节数组
     */
    public byte[] sendMsgCheckProgress(int fun,int cmd,int data) {
        if (mClientUtil != null) {
            byte[] sendBytes = UpdataUtils.sendMsgProgress( fun, cmd, data);
            mClientUtil.sendMsg(sendBytes);
            return sendBytes;
        } else {
            Mydialog.Dismiss();
            MyToastUtils.toast(R.string.android_key3129);
            return null;
        }
    }



    /**
     * 根据命令以及起始寄存器发送查询命令
     *
     * @param sends
     * @return：返回发送的字节数组
     */
    public byte[] sendMsgNoDialog(int[] sends) {
        if (mClientUtil != null) {
            byte[] sendBytes = ModbusUtil.sendMsg(sends[0], sends[1], sends[2]);
            mClientUtil.sendMsg(sendBytes);
            return sendBytes;
        } else {
            Mydialog.Dismiss();
            MyToastUtils.toast(R.string.android_key539);
            return null;
        }
    }




    /**
     * 不走数服协议
     *
     *
     * @param sends
     * @return：返回发送的字节数组
     */
    public byte[] sendMsgNoNum(int[] sends) {
        if (mClientUtil != null) {
            byte[] sendBytes = UpdataUtils.sendMsg(sends[0], sends[1], sends[2]);
            mClientUtil.sendMsg(sendBytes);
            return sendBytes;
        } else {
            Mydialog.Dismiss();
            MyToastUtils.toast(R.string.android_key539);
            return null;
        }
    }


    /**
     * 发送连续的寄存器去设置
     *
     * @param sends
     * @return：返回发送的字节数组
     */
    public  byte[] sendMsgToServer10(int[] sends, int[] values) {
        Mydialog.Show(mContext);
        if (mClientUtil != null) {
            byte[] sendBytes = ModbusUtil.sendMsg10(sends[0], sends[1], sends[2], values);
            mClientUtil.sendMsg(sendBytes);
            return sendBytes;
        }else {
            Mydialog.Dismiss();
            ToastUtils.show(R.string.all_failed);
            return null;
        }
    }

    public boolean isDisConnect() {
        return isConnect;
    }

    public void setDisConnect(boolean disConnect) {
        isConnect = disConnect;
    }
}
