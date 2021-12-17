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
    //当前要下发的文件
    private List<ByteBuffer> curBuffer = new ArrayList<>();
    //当前执行的步骤
    private int step = 0;
    //当前发送第几个文件
    private int fileIndex = 0;
    //当前是第几包数据,从1开始，第一包是CRC
    private int curDataIndex = 0;
    //发送擦除指令的次数
    private int step_send_num = 0;

    //定时刷新的计时器
    private CustomTimer mFreshTimer;


    //定时查询下发文件
    private IUpdataListeners updataListeners;

    //定时查询的handler
    private Handler readHandler = new Handler();

    //当前发送第几包
    private int preIndex = curDataIndex;


    public FileUpdataSend(Context context, List<File> updataFile, IUpdataListeners updataListeners) {
        this.updataFile = updataFile;
        this.context = context;
        this.updataListeners = updataListeners;
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

  /*  public FileUpdataSend(Context context, List<List<ByteBuffer>> fileData, IUpdataListeners updataListeners) {
        this.context = context;
        this.fileData = fileData;
        this.updataListeners = updataListeners;

        //1.去连接TCP
        connetSocket();
    }*/


    /**
     * 固定一分钟刷新一次
     */

    private void startFreshTimer() {
        //初始化一个定时器并立即执行
        mFreshTimer = new CustomTimer(() -> {
            try {
                //断开重连
                manager.disConnectSocket();
                connetSocket();
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
        if (step == 7) {
            sendData(fileIndex, curDataIndex);
        } else if (step == 9) {//查进度
            check();
        } else {
            sendSaveComend();
        }
    }

    @Override
    public void connectFail() {
        manager.disConnectSocket();
        MyControl.showJumpWifiSet((FragmentActivity) context);
        String errMsg = context.getString(R.string.错误) + ":" + step;
        updataListeners.updataFail(errMsg);
    }

    @Override
    public void sendMsgFail() {
        manager.disConnectSocket();
        MyControl.showTcpDisConnect((FragmentActivity) context, context.getString(R.string.disconnet_retry),
                this::connetSocket);
    }

    @Override
    public void readTimeOut() {

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
                } else {
                    String errMsg = context.getString(R.string.错误) + ":" + step;
                    updataListeners.updataFail(errMsg);
                }
                break;

            case 2: //2.送命令设置波特率
                boolean isCheck2 = UpdataUtils.checkReceiver0617(bytes);
                if (isCheck2) {//检测成功，相当于设置成功
                    sendFile(fileIndex);
                } else {
                    String errMsg = context.getString(R.string.错误) + ":" + step;
                    updataListeners.updataFail(errMsg);
                }

                break;


            case 3://3.当前发送文件.bin 01  .hex 00
                boolean isCheck3 = UpdataUtils.checkReceiver0617(bytes);
                if (isCheck3) {//检测成功，相当于设置成功
                    sendFileLength(fileIndex);
                } else {
                    String errMsg = context.getString(R.string.错误) + ":" + step;
                    updataListeners.updataFail(errMsg);
                }
                break;

            case 4://4.发送文件大小
                boolean isCheck4 = UpdataUtils.checkReceiver0617(bytes);
                if (isCheck4) {
                    sendFileCRC(fileIndex);
                } else {
                    String errMsg = context.getString(R.string.错误) + ":" + step;
                    updataListeners.updataFail(errMsg);
                }
                break;

            case 5://5.发送烧录文件CRC校验
                boolean isCheck5 = UpdataUtils.checkReceiver0617(bytes);
                if (isCheck5) {
                    step_send_num = 0;
                    sendFlash();
                } else {
                    if (step_send_num < 6) {
                        new Handler().postDelayed(this::sendFlash, 3000);
                    } else {//提示升级失败
                        step_send_num = 0;
                        //擦除失败  重复第10和11的步骤
                        commendSave();
                        String errMsg = context.getString(R.string.错误) + ":" + step;
                        updataListeners.updataFail(errMsg);
                    }
                }
                break;
            case 6:
                //判断是否擦除成功   不成功要隔3秒再次擦除  一直到成功为止
                boolean isCheck6 = UpdataUtils.checkReceiver0617(bytes);
                if (isCheck6) {
                    sendData(fileIndex, curDataIndex);
                } else {
                    if (step_send_num < 6) {
                        step_send_num = 0;
                        new Handler().postDelayed(this::sendFlash, 3000);
                    } else {//提示升级失败
                        step_send_num = 0;
                        //擦除失败  重复第10和11的步骤
                        commendSave();
                        String errMsg = context.getString(R.string.错误) + ":" + step;
                        updataListeners.updataFail(errMsg);
                    }

                }

                break;
            case 7://发送烧录数据
                readHandler.removeCallbacksAndMessages(null);
                boolean isCheck7 = UpdataUtils.checkReceiver0617(bytes);
                if (isCheck7) {
                    step_send_num = 0;
                    if (curDataIndex < curBuffer.size() - 1) {
                        sendData(fileIndex, ++curDataIndex);
                    } else {
                        sendFinish();
                    }
                } else {
                    //发送失败 ，重新发这一包
                    if (step_send_num < 6) {
                        sendData(fileIndex, curDataIndex);
                    } else {
                        String errMsg = context.getString(R.string.错误) + ":" + step;
                        updataListeners.updataFail(errMsg);
                    }
                }


                break;
            case 8://每隔两秒查一次进度
                boolean isCheck8 = UpdataUtils.checkReceiver0617(bytes);
                if (isCheck8) {
                    step_send_num = 0;
                    step = 9;
                    startFreshTimer();
                } else {
                    //返回失败 ，重新发这一包
                    if (step_send_num < 6) {
                        sendFinish();
                    } else {
                        String errMsg = context.getString(R.string.错误) + ":" + step;
                        updataListeners.updataFail(errMsg);
                    }
                }
                break;
            case 9:
                //解析进度
                boolean isCheck9 = ModbusUtil.checkModbus(bytes);
                if (isCheck9) {
                    //移除数服协议,保留modbus协议
                    byte[] bs = RegisterParseUtil.removePro(bytes);
                    byte[] value = new byte[2];
                    value[0] = bs[3];
                    value[1] = bs[4];
                    int progress = MaxWifiParseUtil.obtainValueOne(value);
                    if (progress == 100) {//成功
                        step_send_num = 0;
                        commendSave();
                        stopFreshTimer();
                    } else if (progress < 100) {
                        step_send_num = 0;
                        //显示进度
                        updataListeners.updataUpdataProgress(fileData.size(), fileIndex + 1, progress);
                    } else {//失败
                        if (step_send_num > 100) {//查询100次  都失败的话就显示失败
                            stopFreshTimer();
                            String errMsg = context.getString(R.string.错误) + ":" + step;
                            updataListeners.updataFail(errMsg);
                        }
                    }

                } else {
                    if (step_send_num > 100) {//查询20次  都失败的话就显示失败
                        stopFreshTimer();
                        String errMsg = context.getString(R.string.错误) + ":" + step;
                        updataListeners.updataFail(errMsg);
                    }
                }
                break;
            case 10:
                boolean isCheck10 = UpdataUtils.checkReceiver0617(bytes);

                if (isCheck10) {//检测成功，相当于设置成功
                    resRate();
                } else {
                    String errMsg = context.getString(R.string.错误) + ":" + step;
                    updataListeners.updataFail(errMsg);
                }
                break;
            case 11:
                boolean isCheck11 = UpdataUtils.checkReceiver0617(bytes);
                if (isCheck11) {
                    if (fileIndex < fileData.size() - 1) {
                        fileIndex++;
                        curDataIndex = 0;
                        sendSaveComend();
                    } else {//两个文件都发送完成
                        fileIndex = 0;
                        curDataIndex = 0;
                        updataListeners.updataSuccess();

                    }
                } else {
                    String errMsg = context.getString(R.string.错误) + ":" + step;
                    updataListeners.updataFail(errMsg);
                }

                break;
        }
    }


    //1.向02号保持寄存器写入0x01，保存波特率
    private void sendSaveComend() {
        updataListeners.preparing();
        curBuffer = new ArrayList<>(fileData.get(fileIndex));
        curBuffer.remove(0);//移除第一包 是CRC
        step = 1;
        LogUtil.i("1.向02号保持寄存器写入0x01");
        manager.sendMsgNoNum(new int[]{6, 2, 1});
    }

    //2.发送命令设置波特率
    private void setBaudRate() {
        step = 2;
        LogUtil.i("2.发送命令设置波特率");
        manager.sendMsgNoNum(new int[]{6, 22, 1});

    }

    //3.当前发送文件.bin 01  .hex 10
    private void sendFile(int current) {
        step = 3;
        int data = 0x10;
        File file = updataFile.get(current);
        if (file.getName().endsWith(".hex")) {
            data = 0x10;
        }
        LogUtil.i("3.当前发送文件"+data);
        manager.sendMsgNoNum(new int[]{6, 0x1f, data});
    }

    //4.发送文件大小
    private void sendFileLength(int current) {
        step = 4;
//        File file = updataFile.get(current);
//        int len = (int) file.length();
        int len = (curBuffer.size() - 1) * 256;
        ByteBuffer byteBuffer = curBuffer.get(curBuffer.size() - 1);
        byte[] array = byteBuffer.array();
        len += array.length;
        byte[] bytes = CommenUtils.int4Byte(len);
        LogUtil.i("4.发送文件大小");
        manager.sendMsg17(0x17, 0x02, bytes);
    }

    //5.发送烧录文件CRC校验
    private void sendFileCRC(int current) {
        step_send_num++;
        step = 5;
        List<ByteBuffer> byteBuffers = fileData.get(current);
        byte[] array = byteBuffers.get(0).array();
        LogUtil.i("5.发送烧录文件CRC校验");
        manager.sendMsg17(0x17, 0x03, array);
    }

    //6.PC发送Flash擦除指令
    private void sendFlash() {
        step = 6;
        step_send_num++;
        byte[] array = new byte[]{0x00, 0x00};
        LogUtil.i("6.PC发送Flash擦除指令");
        manager.sendMsg17(0x17, 0x04, array);
    }

    //7.发送烧录数据
    private void sendData(int current, int index) {
        preIndex = curDataIndex;
        step_send_num++;
        readHandler.postDelayed(new SendDataRunable(), 5000);
        int progress = (index + 1) * 100 / curBuffer.size();
        updataListeners.sendFileProgress(fileData.size(), current, progress);
        step = 7;
        ByteBuffer byteBuffer = curBuffer.get(index);
        byte[] array = byteBuffer.array();
        LogUtil.i("7.发送烧录数据"+index);
        manager.sendMsg1705(0x17, 0x05, index, array);
    }

    //8.PC发送结束命令
    private void sendFinish() {
        step = 8;
        byte[] array = new byte[]{0x00, 0x00};
        manager.sendMsg17(0x17, 0x06, array);
    }


    //9.发送查询指令
    private void check() {
        step_send_num++;
        step = 9;
        manager.sendMsgCheckProgress(0x03, 0x1f, 0x01);
    }


    //10.发送命令保存指令
    private void commendSave() {
        step = 10;
        manager.sendMsg(new int[]{6, 2, 1});
    }


    //11.发送命令恢复波特率
    private void resRate() {
        step = 11;
        manager.sendMsg(new int[]{6, 22, 1});
    }


    public void close() {
        manager.disConnectSocket();
    }


    class SendDataRunable implements Runnable {
        @Override
        public void run() {
            if (preIndex == curDataIndex && step_send_num < 3) {
                //断开重连
                manager.disConnectSocket();
                connetSocket();
            }else {
                //提示升级失败
                manager.disConnectSocket();
                String errMsg = context.getString(R.string.错误) + ":" + step;
                updataListeners.updataFail(errMsg);
            }
        }
    }


}
