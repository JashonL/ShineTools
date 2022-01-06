package com.growatt.shinetools.module.inverterUpdata;

import android.content.Context;
import android.os.Handler;

import com.growatt.shinetools.R;
import com.growatt.shinetools.modbusbox.DataLogApDataParseUtil;
import com.growatt.shinetools.modbusbox.DatalogApUtil;
import com.growatt.shinetools.modbusbox.bean.DatalogAPSetParam;
import com.growatt.shinetools.modbusbox.bean.DatalogResponBean;
import com.growatt.shinetools.socket.ConnectHandler;
import com.growatt.shinetools.socket.SocketManager;
import com.growatt.shinetools.utils.CommenUtils;
import com.growatt.shinetools.utils.Log;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.ModBusFunUtils18;
import com.growatt.shinetools.utils.ModBusFunUtils19;
import com.growatt.shinetools.utils.ModBusFunUtils26;
import com.growatt.shinetools.utils.MyToastUtils;
import com.growatt.shinetools.utils.datalogupdata.UpdateDatalogUtils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


/**
 * 使用USB WIFI 升级
 * <p>
 * 采集器升级流程
 * 1、设置下发文件类型；格式X#typeXX#URL
 * X：标志本次设置升级是否马上起效（预留作用）
 * X=0 --> 只下载文件但不升级，等待升级触发命令；
 * X=1 --> 下载文件并马上进行升级
 * <p>
 * XX=00 --> 值设置路径不升级；
 * XX=01 --> 采集器bin文件；
 * XX=02 --> 逆变器hex文件；
 * XX=03 --> 逆变器bin文件；
 * <p>
 * 2、发送升级文件包：服务器以0x26开始文件传输-->完成后；按原有升级流程采集器.
 */

public class FileSendToDataLoger03 implements ConnectHandler, ISendInterface {
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
    //当前是第几包数据,从1开始，第一包是20个校验数据
    private int currNum = 0;
    //发送指令的次数
    private int step_send_num = 0;
    //升级回调
    private IUpdataListeners updataListeners;
    //发包错误的次数
    private int errornum = 0;

    private boolean isUpdating = false;


    public FileSendToDataLoger03(Context context, List<File> updataFile, IUpdataListeners updataListeners) {
        this.context = context;
        this.updataFile = updataFile;
        this.updataListeners = updataListeners;

        //1.将文件分包
        for (int i = 0; i < updataFile.size(); i++) {
            try {
                List<ByteBuffer> file = UpdateDatalogUtils.getFileByte1024(updataFile.get(i).getAbsolutePath());
                fileData.add(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //2.去连接TCP
        connetSocket();
    }

    private void connetSocket() {
        step = 0;
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
        //连接成功下发80命令
        sendComend();
    }


    @Override
    public void connectFail() {

    }

    @Override
    public void sendMsgFail() {

    }

    @Override
    public void socketClose() {
        if (isUpdating){
            String errMsg = context.getString(R.string.错误) + ":" + step;
            updataListeners.updataFail(errMsg);
        }
    }

    @Override
    public void sendMessage(String msg) {

    }

    @Override
    public void receiveMessage(String msg) {

    }


    //1.发送18指令设置升级的文件类型， 参数编号 80
    private void sendComend() {
        updataListeners.preparing(fileData.size(), fileIndex);
        curBuffer = new ArrayList<>(fileData.get(fileIndex));
        step = 1;
        LogUtil.i("1.发送0x18命令");
        //判断文件的类型
        File file = updataFile.get(fileIndex);
        String value = "1#type";
        if (file.getName().endsWith(".hex")) {
            value += "02";
        } else if (file.getName().endsWith(".bin")) {
            value += "03";
        }
        DatalogAPSetParam bean = new DatalogAPSetParam();
        bean.setParamnum(80);
        bean.setLength(value.length());
        bean.setValue(value);
        byte[] bytes = ModBusFunUtils18.sendMsg03(bean);
        manager.sendBytes(bytes);
        isUpdating = true;
    }


    @Override
    public void receveByteMessage(byte[] bytes) {
        Log.i("服务器返回数据：============" + CommenUtils.bytesToHexString(bytes));
        try {
            boolean isCheck = DatalogApUtil.checkData(bytes);
            if (isCheck) {
                //接收正确，开始解析
                byte type = bytes[7];

                //1.去除头部包头
                byte[] removePro = DataLogApDataParseUtil.removePro(bytes);
                if (removePro == null) {
                    MyToastUtils.toast(R.string.android_key7);
                    return;
                }
                Log.i("去除头部包头" + CommenUtils.bytesToHexString(removePro));
                //2.解密
                byte[] desBytes = DataLogApDataParseUtil.getDesBytes(removePro);
                Log.i("解密" + CommenUtils.bytesToHexString(desBytes));
                //3.解析数据
                parserData(type, desBytes);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 解析数据
     *
     * @param bytes
     */
    private void parserData(byte type, byte[] bytes) {
        try {
            //1.字节数组成bean
            DatalogResponBean bean = DataLogApDataParseUtil.paserData(type, bytes);
            if (bean == null) return;
            if (bean.getFuncode() == DatalogApUtil.DATALOG_GETDATA_0X26) {
                List<DatalogResponBean.ParamBean> paramBeanList = bean.getParamBeanList();
                for (int i = 0; i < paramBeanList.size(); i++) {
                    DatalogResponBean.ParamBean paramBean = paramBeanList.get(i);
                    int dataNum = paramBean.getDataNum();
                    int dataCode = paramBean.getDataCode();
                    switch (dataCode) {
                        case 0://成功,发送下一包
                            errornum = 0;
                            if (dataNum < curBuffer.size() - 1) {
                                currNum = dataNum + 1;
                                int total = curBuffer.size();
                                senDataToLoger(total);

                            } else {//最后一包
                                //开始查询升级进度隔5秒钟查一次
                                new Handler().postDelayed(this::checkProgress, 5000);
                            }
                            break;
                        case 1://接收异常，再次发送当前包
                            errornum++;
                            if (errornum > 3) {//显示升级失败
                                updataError();
                            } else {
                                int total = curBuffer.size();
                                senDataToLoger(total);
                            }
                            break;
                        default://整体检验错误，重新发送第一包，做个弹框确认吧
                            errornum++;
                            if (errornum > 3) {
                                updataError();
                            } else {
                                currNum = 0;
                                int total = curBuffer.size();
                                senDataToLoger(total);
                            }

                            break;
                    }


                }
            } else if (bean.getFuncode() == DatalogApUtil.DATALOG_GETDATA_0X18) {
                int statusCode = bean.getStatusCode();
                if (statusCode == 1) {
                    MyToastUtils.toast(R.string.android_key3129);
                    return;
                }
                //18设置成功 开始发送文件
                int total = curBuffer.size();
                senDataToLoger(total);
            } else if (bean.getFuncode() == DatalogApUtil.DATALOG_GETDATA_0X19) {
                int statusCode = bean.getStatusCode();
                if (statusCode == 1) {
                    MyToastUtils.toast(R.string.android_key3129);
                    return;
                }
                //升级进度
                int progress = bean.getValue();
                Log.i("返回进度");
                if (progress == 100) {//成功
                    step_send_num = 0;
                    //判断文件是否已经下发完成
                    if (fileIndex < fileData.size() - 1) {
                        fileIndex++;
                        currNum = 0;
                        //等待6秒钟发下一个文件
                        new Handler().postDelayed(this::sendComend, 5000);
                    } else {//发送完成
                        fileIndex = 0;
                        currNum = 0;
                        updataListeners.updataSuccess();
                    }

                } else if (progress < 100) {
                    step_send_num = 0;
                    //显示进度
                    updataListeners.updataUpdataProgress(fileData.size(), fileIndex + 1, progress);
                    //隔5秒继续查询
                    //开始查询升级进度隔5秒钟查一次
                    new Handler().postDelayed(this::checkProgress, 5000);
                } else {//失败
                    if (step_send_num > 10) {//查询10次  都失败的话就显示失败
                        String errMsg = context.getString(R.string.错误) + ":" + step;
                        updataListeners.updataFail(errMsg);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void senDataToLoger(int total) throws Exception {
        Log.i("下发文件：" + "总数量：" + total + "当前第几包：" + currNum);
        step = 2;
        int progress = (currNum + 1) * 100 / curBuffer.size();
        updataListeners.sendFileProgress(fileData.size(), fileIndex, progress);
        byte[] bytes = ModBusFunUtils26.sendMsg03(total, currNum, curBuffer.get(currNum).array());
        manager.sendBytes(bytes);

    }


    //升级失败
    private void updataError() {
        //初始化界面
        errornum = 0;
        String errMsg = context.getString(R.string.错误) + ":" + step;
        isUpdating = false;
        updataListeners.updataFail(errMsg);
    }


    //1.发送19指令 参数编号 80-80 查询升级进度
    private void checkProgress() {
        Log.i("查询进度");
        step_send_num++;
        step = 3;
        int[] values = {80, 80};
        byte[] bytes = ModBusFunUtils19.sendMsg03(values);
        manager.sendBytes(bytes);

    }

    public void close() {
        isUpdating = false;
        manager.disConnectSocket();
    }


}
