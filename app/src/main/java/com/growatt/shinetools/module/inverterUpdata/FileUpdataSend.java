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
import com.growatt.shinetools.timer.CustomTimer;
import com.growatt.shinetools.utils.CommenUtils;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.MyControl;
import com.growatt.shinetools.utils.datalogupdata.UpdateDatalogUtils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


/**
 * 升级下发文件
 * 先发送hex文件  发送bin文件逆变器会重启
 */

public class FileUpdataSend implements ConnectHandler {

    private SocketManager manager;
    private Context context;
    //需要下发的文件列表
    private List<File> updataFile;
    //文件切割字节数组
    private List<List<ByteBuffer>> fileData = new ArrayList<>();
    //当前执行的步骤
    private int step = 0;
    //当前发送第几个文件
    private int fileIndex = 0;
    //当前是第几包数据,从1开始，第一包是CRC
    private int curDataIndex = 0;

    //定时刷新的计时器
    private CustomTimer mFreshTimer;

    public FileUpdataSend(Context context, List<File> updataFile) {
        this.updataFile = updataFile;
        this.context = context;

        //1.将文件分包
        for (int i = 0; i < updataFile.size(); i++) {
            try {
                List<ByteBuffer> file = UpdateDatalogUtils.getFileByte(updataFile.get(i).getAbsolutePath());
                fileData.add(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //1.去连接TCP
        connetSocket();
    }


    /**
     * 固定一分钟刷新一次
     */

    private void startFreshTimer() {
        //初始化一个定时器并立即执行
        mFreshTimer = new CustomTimer(() -> {
            try {
                check();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 2 * 1000, 2 * 1000);

        mFreshTimer.timerStart();
    }


    private void stopFreshTimer() {
        if (mFreshTimer != null) {
            mFreshTimer.timerDestroy();
            mFreshTimer = null;
        }
    }


    private void connetSocket() {
        //初始化连接
        manager = new SocketManager(context);
        //设置连接监听
        manager.onConect(this);
        //开始连接TCP
        //延迟一下避免频繁操作
        new Handler().postDelayed(() -> manager.connectSocket(), 100);
    }


    @Override
    public void connectSuccess() {
        sendSaveComend();
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
                this::connetSocket);
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


        switch (step) {
            case 1://1.向02号保持寄存器写入0x01，保存波特率
                boolean isCheck = UpdataUtils.checkReceiver0617(bytes);
                if (isCheck) {//检测成功，相当于设置成功
                    setBaudRate();
                }
                break;

            case 2: //2.送命令设置波特率
                boolean isCheck2 = UpdataUtils.checkReceiver0617(bytes);
                if (isCheck2) {//检测成功，相当于设置成功
                    sendFile(fileIndex);
                }

                break;


            case 3://3.当前发送文件.bin 01  .hex 00
                boolean isCheck3 = UpdataUtils.checkReceiver0617(bytes);
                if (isCheck3) {//检测成功，相当于设置成功
                    sendFileLength(fileIndex);
                }
                break;

            case 4://4.发送文件大小
                boolean isCheck4 = UpdataUtils.checkReceiver0617(bytes);
                if (isCheck4) {
                    sendFileCRC(fileIndex);
                }
                break;

            case 5://5.发送烧录文件CRC校验
                boolean isCheck5 = UpdataUtils.checkReceiver0617(bytes);
                if (isCheck5) {
                    sendFlash();
                }
                break;
            case 6:
                sendData(fileIndex, curDataIndex);
                break;
            case 7://发送烧录数据
                List<ByteBuffer> byteBuffers = fileData.get(fileIndex);
                if (curDataIndex < byteBuffers.size() - 1) {
                    sendData(fileIndex, ++curDataIndex);
                } else {
                    sendFinish();
                }
                break;
            case 8://每隔两秒查一次进度
                startFreshTimer();
                break;
            case 9:
                //解析进度
                boolean isCheck9 = ModbusUtil.checkModbus(bytes);
                if (isCheck9) {
                    //移除数服协议,保留modbus协议
                    byte[] bs = RegisterParseUtil.removePro(bytes);
                    byte[] value = new byte[2];
                    value[0] = bs[3];
                    value[0] = bs[4];
                    int progress = MaxWifiParseUtil.obtainValueOne(value);
                    if (progress == 100) {//成功
                        stopFreshTimer();
                        commendSave();
                    } else if (progress < 100) {
                        //显示进度
                    } else if (progress > 100) {//失败
                        stopFreshTimer();
                    }

                } else {

                }
                break;
            case 10:
                boolean isCheck10 = UpdataUtils.checkReceiver0617(bytes);
                if (isCheck10) {//检测成功，相当于设置成功
                    resRate();
                }
                break;
            case 11:
                if (fileIndex < updataFile.size() - 1) {
                    fileIndex++;
                    curDataIndex = 0;
                    sendSaveComend();
                } else {//两个文件都发送完成
                    fileIndex = 0;
                    curDataIndex = 0;
                }
                break;
        }
    }


    //1.向02号保持寄存器写入0x01，保存波特率
    private void sendSaveComend() {
        LogUtil.i("向02号保持寄存器写入0x01，保存波特率");
        step = 1;
        manager.sendMsg(new int[]{6, 2, 1});
    }

    //2.送命令设置波特率
    private void setBaudRate() {
        step = 2;
        LogUtil.i("送命令设置波特率");
        manager.sendMsg(new int[]{6, 22, 1});
    }

    //3.当前发送文件.bin 01  .hex 00
    private void sendFile(int current) {
        LogUtil.i("当前发送文件.bin 01  .hex 00:" + current);
        step = 3;
        manager.sendMsg(new int[]{6, 0x1f, current});
    }

    //4.发送文件大小
    private void sendFileLength(int current) {
        LogUtil.i("发送文件大小");
        step = 4;
        File file = updataFile.get(current);
        int len = (int) file.length();
        byte[] bytes = CommenUtils.int4Byte(len);
        manager.sendMsg17(17, 2, bytes);
    }

    //5.发送烧录文件CRC校验
    private void sendFileCRC(int current) {
        LogUtil.i("发送烧录文件CRC校验");
        step = 5;
        List<ByteBuffer> byteBuffers = fileData.get(current);
        byte[] array = byteBuffers.get(0).array();
        manager.sendMsg17(17, 3, array);
    }

    //6.PC发送Flash擦除指令
    private void sendFlash() {
        LogUtil.i("PC发送Flash擦除指令");
        step = 6;
        byte[] array = new byte[]{0x00, 0x00};
        manager.sendMsg17(17, 4, array);
    }

    //7.发送烧录数据
    private void sendData(int current, int index) {
        LogUtil.i("发送烧录数据");
        step = 7;
        List<ByteBuffer> byteBuffers = fileData.get(current);
        ByteBuffer byteBuffer = byteBuffers.get(index + 1);
        byte[] array = byteBuffer.array();
        manager.sendMsg1705(17, 5, index, array);
    }

    //8.PC发送结束命令
    private void sendFinish() {
        LogUtil.i("PC发送结束命令");
        step = 8;
        byte[] array = new byte[]{0x00, 0x00};
        manager.sendMsg17(17, 6, array);
    }


    //9.发送查询指令
    private void check() {
        LogUtil.i("发送查询指令");
        step = 9;
        manager.sendMsgCheckProgress(0x03, 0x1f, 0x01);
    }

    //10.发送命令保存指令
    private void commendSave() {
        LogUtil.i("发送命令保存指令");
        step = 10;
        manager.sendMsg(new int[]{6, 2, 1});
    }


    //11.发送命令恢复波特率
    private void resRate() {
        LogUtil.i("发送命令恢复波特率");
        step = 11;
        manager.sendMsg(new int[]{6, 22, 1});
    }


}
