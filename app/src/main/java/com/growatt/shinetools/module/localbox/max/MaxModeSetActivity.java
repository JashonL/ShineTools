package com.growatt.shinetools.module.localbox.max;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.growatt.shinetools.R;
import com.growatt.shinetools.base.DemoBase;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.MaxWifiParseUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.utils.BtnDelayUtil;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.Mydialog;
import com.growatt.shinetools.utils.Position;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MaxModeSetActivity extends DemoBase {
    String readStr ;
    @BindView(R.id.tvContent1)
    TextView mTvContent1;
    @BindView(R.id.btnSetting)
    Button mBtnSetting;
    @BindView(R.id.et1)
    EditText mEt1;
    @BindView(R.id.et2)
    EditText mEt2;
    @BindView(R.id.et3)
    EditText mEt3;
    @BindView(R.id.et4)
    EditText mEt4;
    @BindView(R.id.et5)
    EditText mEt5;
    @BindView(R.id.et6)
    EditText mEt6;
    @BindView(R.id.et7)
    EditText mEt7;
    @BindView(R.id.et8)
    EditText mEt8;
    private String mTitle;
    @BindView(R.id.headerView)
    View headerView;
    @BindView(R.id.tvRight)
    TextView mTvRight;
    private int mType = 0;
    //读取命令功能码（功能码，开始寄存器，结束寄存器）
    private int[][] funs;
    private int[][] nowSet;
    //edittenxt集合
    private EditText[] ets;
    //用户输入内容集合
    private String[] contents = new String[8];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_max_mode_set);
        ButterKnife.bind(this);
        initString();
        initIntent();
        initHeaderView();
    }

    private void initString() {
        readStr = getString(R.string.m369读取值);
        //读取命令功能码（功能码，开始寄存器，结束寄存器）
        funs = new int[][]{
                //参数设置
                {3, 28, 29}//Model
        };
        //需要设置的内容
        nowSet = new int[][]{
                {6, funs[mType][1], -1}
                , {6, funs[mType][2], -1}
        };
        ets = new EditText[]{
          mEt1,mEt2, mEt3,mEt4,mEt5,mEt6,mEt7,mEt8
        };
    }

    private void initIntent() {
        Intent mIntent = getIntent();
        if (mIntent != null) {
            mTitle = mIntent.getStringExtra("title");
        }
    }

    private void initHeaderView() {
        setHeaderImage(headerView, R.drawable.icon_return, Position.LEFT, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setHeaderTitle(headerView, mTitle);
        setHeaderTvTitle(headerView, getString(R.string.m370读取), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //读取寄存器的值
                readRegisterValue();
            }
        });
    }

    //连接对象:用于读取数据
    private SocketClientUtil mClientUtilRead;
    //连接对象:用于设置数据
    private SocketClientUtil mClientUtilWriter;

    //读取寄存器的值
    private void readRegisterValue() {
        Mydialog.Show(mContext);
        mClientUtilRead = SocketClientUtil.connectServer(mHandlerRead);
    }

    //读取寄存器的值
    private void writeRegisterValue() {
        Mydialog.Show(mContext);
        mClientUtilWriter = SocketClientUtil.connectServer(mHandlerWrite);
    }

    /**
     * 写寄存器handle
     */
    //当前发送的字节数组
    private byte[] sendBytes;
    Handler mHandlerWrite = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    if (nowSet != null) {
                        if (nowSet[0][2] == -1 && nowSet[1][2] == -1) {
                            //关闭tcp连接
                            SocketClientUtil.close(mClientUtilWriter);
                            BtnDelayUtil.refreshFinish();
                        } else {
//                            for (int len = nowSet.length, i = len - 1; i >= 0; i--) {
//                                if (nowSet[i][2] != -1) {
//                                    sendBytes = SocketClientUtil.sendMsgToServer(mClientUtilWriter, nowSet[i]);
//                                    LogUtil.i("发送写入" + (i + 1) + ":" + SocketClientUtil.bytesToHexString(sendBytes));
//                                    //发送完将值设置为-1
//                                    nowSet[i][2] = -1;
//                                    break;
//                                }
//                            }
                            for (int len = nowSet.length, i = 0; i < len; i++) {
                                if (nowSet[i][2] != -1) {
                                    BtnDelayUtil.sendMessageWrite(this);
                                    sendBytes = SocketClientUtil.sendMsgToServer(mClientUtilWriter, nowSet[i]);
                                    LogUtil.i("发送写入" + (i + 1) + ":" + SocketClientUtil.bytesToHexString(sendBytes));
                                    //发送完将值设置为-1
                                    nowSet[i][2] = -1;
                                    break;
                                }
                            }
                        }
                    }
                    break;
                //接收字节数组
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //检测内容正确性
                        boolean isCheck = MaxUtil.checkReceiverFull(bytes);
                        if (isCheck) {
                            //移除外部协议
                            byte[] bs = RegisterParseUtil.removePro17Fun6(bytes);
                            //解析int值
//                            int value0 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,1,0,1));

                            toast(getString(R.string.all_success));
                            //继续发送设置命令
                            mHandlerWrite.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        } else {
                            toast(getString(R.string.all_failed));
                            //将内容设置为-1初始值
                            nowSet[0][2] = -1;
                            nowSet[1][2] = -1;
                            //关闭tcp连接
                            SocketClientUtil.close(mClientUtilWriter);
                            BtnDelayUtil.refreshFinish();
                        }
                        LogUtil.i("接收写入" + ":" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        //关闭tcp连接
                        SocketClientUtil.close(mClientUtilWriter);
                        BtnDelayUtil.refreshFinish();
                    }
                    break;
                default:
                    BtnDelayUtil.dealMaxBtnWrite(this, what, mContext, mBtnSetting, mTvRight);
                    break;
            }
        }
    };
    /**
     * 读取寄存器handle
     */
    Handler mHandlerRead = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessage(this);
                    byte[] sendBytesR = SocketClientUtil.sendMsgToServer(mClientUtilRead, funs[mType]);
                    LogUtil.i("发送读取：" + SocketClientUtil.bytesToHexString(sendBytesR));
                    break;
                //接收字节数组
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //检测内容正确性
                        boolean isCheck = ModbusUtil.checkModbus(bytes);
                        if (isCheck) {
                            //移除外部协议
                            byte[] bs = RegisterParseUtil.removePro17(bytes);
                            //解析int值
                            int value0 = MaxWifiParseUtil.obtainValueHAndL(bs);
                            //更新ui
                            mTvContent1.setText(readStr + ":" + MaxUtil.getDeviceModel(value0));
                            //设置model值
//                            mEt1.setText(Integer.toHexString((value0 & 0xF0000000) >>> 28).toUpperCase());
//                            mEt2.setText(Integer.toHexString((value0 & 0x0F000000) >>> 24).toUpperCase());
//                            mEt3.setText(Integer.toHexString((value0 & 0x00F00000) >>> 20).toUpperCase());
//                            mEt4.setText(Integer.toHexString((value0 & 0x000F0000) >>> 16).toUpperCase());
//                            mEt5.setText(Integer.toHexString((value0 & 0x0000F000) >>> 12).toUpperCase());
//                            mEt6.setText(Integer.toHexString((value0 & 0x00000F00) >>> 8).toUpperCase());
//                            mEt7.setText(Integer.toHexString((value0 & 0x000000F0) >>> 4).toUpperCase());
//                            mEt8.setText(Integer.toHexString(value0 & 0x0000000F).toUpperCase());
                            mEt1.setText(MaxUtil.getDeviceModelSingle(value0,8));
                            mEt2.setText(MaxUtil.getDeviceModelSingle(value0,7));
                            mEt3.setText(MaxUtil.getDeviceModelSingle(value0,6));
                            mEt4.setText(MaxUtil.getDeviceModelSingle(value0,5));
                            mEt5.setText(MaxUtil.getDeviceModelSingle(value0,4));
                            mEt6.setText(MaxUtil.getDeviceModelSingle(value0,3));
                            mEt7.setText(MaxUtil.getDeviceModelSingle(value0,2));
                            mEt8.setText(MaxUtil.getDeviceModelSingle(value0,1));
                            toast(R.string.all_success);
                        } else {
                            toast(R.string.all_failed);
                        }
                        LogUtil.i("接收读取：" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //关闭tcp连接
                        SocketClientUtil.close(mClientUtilRead);
                        BtnDelayUtil.refreshFinish();
                    }
                    break;
                default:
                    BtnDelayUtil.dealMaxBtn(this, what, mContext, mBtnSetting, mTvRight);
                    break;
            }
        }
    };

    @OnClick(R.id.btnSetting)
    public void onViewClicked() {
        //判断用户是否输入
        for (int i=0,len=ets.length;i< len;i++){
            String content = ets[i].getText().toString();
            if (TextUtils.isEmpty(content)){
                toast(R.string.all_blank);
                return;
            }
            contents[i] = content;
        }
            try {
                StringBuilder sbHigh = new StringBuilder();
                StringBuilder sbLow = new StringBuilder();
                for (int i =0,len = contents.length;i<len;i++){
                    if (i<4){
                        sbHigh.append(contents[i]);
                    }else {
//                        sbHigh.append("0");
                        sbLow.append(contents[i]);
                    }
                }
                int high = Integer.parseInt(sbHigh.toString(),16);
                int low = Integer.parseInt(sbLow.toString(),16);
                nowSet[0][2] = high;
                nowSet[1][2] = low;
                writeRegisterValue();
            } catch (Exception e) {
                e.printStackTrace();
                toast(getString(R.string.m363设置失败));
                nowSet[0][2] = -1;
                nowSet[1][2] = -1;
            }
    }
}
