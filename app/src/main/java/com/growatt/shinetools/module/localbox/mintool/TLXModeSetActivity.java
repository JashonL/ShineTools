package com.growatt.shinetools.module.localbox.mintool;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.growatt.shinetools.R;
import com.growatt.shinetools.base.DemoBase;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.MaxWifiParseUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.utils.BtnDelayUtil;
import com.growatt.shinetools.utils.CommenUtils;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.Mydialog;
import com.growatt.shinetools.utils.Position;

import java.math.BigInteger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TLXModeSetActivity extends DemoBase {
    String readStr;
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
    @BindView(R.id.llModeTypeOld2)
    LinearLayout mLlModeTypeOld2;
    @BindView(R.id.et1New4)
    EditText mEt1New4;
    @BindView(R.id.et2New4)
    EditText mEt2New4;
    @BindView(R.id.et3New4)
    EditText mEt3New4;
    @BindView(R.id.et4New4)
    EditText mEt4New4;
    @BindView(R.id.et5New4)
    EditText mEt5New4;
    @BindView(R.id.et6New4)
    EditText mEt6New4;
    @BindView(R.id.et7New4)
    EditText mEt7New4;
    @BindView(R.id.llModeTypeNew4)
    LinearLayout mLlModeTypeNew4;
    private String mTitle;
    @BindView(R.id.headerView)
    View headerView;
    @BindView(R.id.tvRight)
    TextView mTvRight;
    private int mType = 1;
    //读取命令功能码（功能码，开始寄存器，结束寄存器）
    private int[][] funs;
    private int[][] nowSet;
    //edittenxt集合
    private EditText[] ets;
    private EditText[] etsNew;
    //用户输入内容集合
    private String[] contents = new String[8];
    /**
     * model类型：-1未获取，0：老版本（28-29寄存器）；1：新版本（118-121寄存器）
     */
    private int modeType = -1;
    private int[][] funs2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tlxmode_set);
        ButterKnife.bind(this);
        initString();
        initIntent();
        initHeaderView();
        //读取新寄存器的值
        readRegisterValue();
    }

    private void initString() {
        readStr = getString(R.string.m369读取值);
        //读取命令功能码（功能码，开始寄存器，结束寄存器）
        funs = new int[][]{
                //参数设置
                {3, 0, 124}
        };
        funs2 = new int[][]{
                //参数设置
                {3, 28, 29}//Model old
                , {3, 118, 121}//Model new
        };
        //需要设置的内容
        nowSet = new int[][]{
                {6, funs2[mType][1], -1}
                , {6, funs2[mType][1] + 1, -1}
                , {6, funs2[mType][1] + 2, -1}
                , {6, funs2[mType][1] + 3, -1}
        };
        ets = new EditText[]{
                mEt1, mEt2, mEt3, mEt4, mEt5, mEt6, mEt7, mEt8
        };
        etsNew = new EditText[]{
                mEt1New4, mEt2New4, mEt3New4, mEt4New4, mEt5New4, mEt6New4, mEt7New4
        };
    }

    private void initIntent() {
        Intent mIntent = getIntent();
        if (mIntent != null) {
            mTitle = mIntent.getStringExtra("title");
        }
    }

    private void initHeaderView() {
        setHeaderImage(headerView, -1, Position.LEFT, new View.OnClickListener() {
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
                        if ((mType == 0 && nowSet[0][2] == -1 && nowSet[1][2] == -1) ||
                             (mType == 1 && nowSet[0][2] == -1 && nowSet[1][2] == -1 && nowSet[2][2] == -1 && nowSet[3][2] == -1)
                            ){
                            //关闭tcp连接
                            SocketClientUtil.close(mClientUtilWriter);
                            BtnDelayUtil.refreshFinish();
                        }else {
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
                            nowSet[2][2] = -1;
                            nowSet[3][2] = -1;
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
                    BtnDelayUtil.dealTLXBtnWrite(this, what, mContext, mBtnSetting, mTvRight);
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
                    byte[] sendBytesR = SocketClientUtil.sendMsgToServer(mClientUtilRead, funs[0]);
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
                            byte[] bsTotal = RegisterParseUtil.removePro17(bytes);
                            byte[] bs = MaxWifiParseUtil.subBytesFull(bsTotal,118,0,4);
                            BigInteger bigInteger = new BigInteger(1, bs);
                            if (bigInteger.longValue() == 0){
                                //使用旧版本
                                modeType = 0;
                                mType = 0;
                                //设置旧版本值
                                bs = MaxWifiParseUtil.subBytesFull(bsTotal,28,0,2);
                            }else {
                                modeType = 1;
                                mType = 1;
                            }
                            if (mType == 0) {
                                CommenUtils.showAllView(mLlModeTypeOld2);
                                CommenUtils.hideAllView(View.GONE,mLlModeTypeNew4);
                                parseOldModel(bs);
                            }else if (mType == 1){
                                parseNewModel(bs);
                                CommenUtils.showAllView(mLlModeTypeNew4);
                                CommenUtils.hideAllView(View.GONE,mLlModeTypeOld2);
                            }
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
                    BtnDelayUtil.dealTLXBtn(this, what, mContext, mBtnSetting, mTvRight);
                    break;
            }
        }
    };

    private void parseOldModel(byte[] bs) {
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
        mEt1.setText(MaxUtil.getDeviceModelSingle(value0, 8));
        mEt2.setText(MaxUtil.getDeviceModelSingle(value0, 7));
        mEt3.setText(MaxUtil.getDeviceModelSingle(value0, 6));
        mEt4.setText(MaxUtil.getDeviceModelSingle(value0, 5));
        mEt5.setText(MaxUtil.getDeviceModelSingle(value0, 4));
        mEt6.setText(MaxUtil.getDeviceModelSingle(value0, 3));
        mEt7.setText(MaxUtil.getDeviceModelSingle(value0, 2));
        mEt8.setText(MaxUtil.getDeviceModelSingle(value0, 1));
    }
    private void parseNewModel(byte[] bs) {
        //解析int值
        BigInteger big = new BigInteger(1, bs);
        long bigInteger = big.longValue();
        //更新ui
        mTvContent1.setText(readStr + ":" + MaxUtil.getDeviceModelNew4(bigInteger));
        mEt1New4.setText(MaxUtil.getDeviceModelSingleNew(bigInteger, 16) + MaxUtil.getDeviceModelSingleNew(bigInteger, 15));
        mEt2New4.setText(MaxUtil.getDeviceModelSingleNew(bigInteger, 14) + MaxUtil.getDeviceModelSingleNew(bigInteger, 13));
        mEt3New4.setText(MaxUtil.getDeviceModelSingleNew(bigInteger, 12) + MaxUtil.getDeviceModelSingleNew(bigInteger, 11));
        mEt4New4.setText(MaxUtil.getDeviceModelSingleNew(bigInteger, 10) + MaxUtil.getDeviceModelSingleNew(bigInteger, 9));
        mEt5New4.setText(MaxUtil.getDeviceModelSingleNew(bigInteger, 8) + MaxUtil.getDeviceModelSingleNew(bigInteger, 7));
        mEt6New4.setText(MaxUtil.getDeviceModelSingleNew(bigInteger, 6) + MaxUtil.getDeviceModelSingleNew(bigInteger, 5));
        mEt7New4.setText(MaxUtil.getDeviceModelSingleNew(bigInteger, 4) + MaxUtil.getDeviceModelSingleNew(bigInteger, 3)
                + MaxUtil.getDeviceModelSingleNew(bigInteger, 2)  + MaxUtil.getDeviceModelSingleNew(bigInteger, 1) );
    }
    @OnClick(R.id.btnSetting)
    public void onViewClicked() {
        nowSet = new int[][]{
                {6, funs2[mType][1], -1}
                , {6, funs2[mType][1] + 1, -1}
                , {6, funs2[mType][1] + 2, -1}
                , {6, funs2[mType][1] + 3, -1}
        };
        switch (mType){
            case 0:
                setOld();
                break;
            case 1:
                setNew();
                break;
        }

    }

    private void setOld() {
        //判断用户是否输入
        for (int i = 0, len = ets.length; i < len; i++) {
            String content = ets[i].getText().toString();
            if (TextUtils.isEmpty(content)) {
                toast(R.string.all_blank);
                return;
            }
            contents[i] = content;
        }
        try {
            StringBuilder sbHigh = new StringBuilder();
            StringBuilder sbLow = new StringBuilder();
            for (int i = 0, len = contents.length; i < len; i++) {
                if (i < 4) {
                    sbHigh.append(contents[i]);
                } else {
//                        sbHigh.append("0");
                    sbLow.append(contents[i]);
                }
            }
            int high = Integer.parseInt(sbHigh.toString(), 16);
            int low = Integer.parseInt(sbLow.toString(), 16);
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
    private void setNew() {
        //判断用户是否输入
        for (int i = 0, len = etsNew.length; i < len; i++) {
            String content = etsNew[i].getText().toString();
            if (TextUtils.isEmpty(content)) {
                toast(R.string.all_blank);
                return;
            }
            contents[i] = content;
        }
        try {
            nowSet[0][2] = Integer.parseInt(contents[0] + contents[1],16);
            nowSet[1][2] = Integer.parseInt(contents[2] + contents[3],16);
            nowSet[2][2] = Integer.parseInt(contents[4] + contents[5],16);
            nowSet[3][2] = Integer.parseInt(contents[6],16);
            writeRegisterValue();
        } catch (Exception e) {
            e.printStackTrace();
            toast(getString(R.string.m363设置失败));
            nowSet[0][2] = -1;
            nowSet[1][2] = -1;
            nowSet[2][2] = -1;
            nowSet[3][2] = -1;
        }
    }
}
