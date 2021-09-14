package com.growatt.shinetools.module.localbox.max;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.view.listener.OnLvItemClickListener;

import java.math.BigInteger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;



/**
 * Max 本地调试国家以及安规
 */
public class MaxParamCountryActivity extends DemoBase {
    String readStr;
    @BindView(R.id.tvContent1)
    TextView mTvContent1;
    @BindView(R.id.btnSetting)
    Button mBtnSetting;
    @BindView(R.id.btnSelect)
    Button mBtnSelect;
    private String mTitle;
    @BindView(R.id.headerView)
    View headerView;
    @BindView(R.id.tvRight)
    TextView mTvRight;
    private int mType = 1;
    //读取命令功能码（功能码，开始寄存器，结束寄存器）
    private int[][] funs;
    private int[][] nowSet;
    //读取的model值
    private int mReadValue = -1;
    private String[][][][] modelToal;
    //当前读取的组
    private String[][] nowModels;
    //当前的值
    private String[] nowModel;
    //寄存器设置数据集合
    private String[] contents = new String[8];
    /**
     * model类型：-1未获取，0：老版本（28-29寄存器）；1：新版本（118-121寄存器）
     */
    private int modeType = -1;
    private int[][] funs2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_max_param_country);
        ButterKnife.bind(this);
        initString();
        initIntent();
        initHeaderView();
        //读取寄存器的值
        readRegisterValue();
    }

    private void initString() {
        modelToal = new String[][][][]{
                {{{"A0S1", "VDE0126"}, {"A0S4", "Italy"}, {"A0S7", "Germany"}, {"A0SB", "EN50438_Standard"}, {"A0SD", "Belgium"}, {"A1S3", "EN50438_Denmark"}, {"A1S4", "Sweden"}, {"A1S5", "EN50438_Norway"}, {"A1S7", "France"}, {"A1SA", "CEI0-16"}, {"A1SB", "DRRG"}, {"A1SC", "Chile"}, {"A1SD", "Argentina"}, {"A1SE", "BDEW"}, {"A0S6", "Greece"}, {"A2S0", "TR3.2.1 Denmark"}},
                        {{"A0S2", "UK_G59"}, {"A0S8", "UK_G83"}, {"A0S9", "EN50438_Ireland"}},
                        {{"A0S3", "AS4777_Australia"}, {"A1S0", "AS4777_Newzealand"}},
                        {{"A0SE", "MEA"}, {"A0SF", "PEA"}}}
                , {{{"S01", "VDE0126"}, {"S04", "Italy"}, {"S07", "Germany"}, {"S0B", "EN50438_tandard"}, {"S0D", "Belgium"}, {"S13", "EN50438_Denmark"}, {"S14", "Sweden"}, {"S15", "EN50438_Norway"}, {"S17", "France"}, {"S1A", "CEI0-16"}, {"S1B", "DRRG"}, {"S1C", "Chile"}, {"S1D", "Argentina"}, {"S1E", "BDEW"}, {"S06", "Greece"}, {"S20", "TR3.2.1 Denmark"}},
                {{"S02", "UK_G59"}, {"S08", "UK_G83"}, {"S09", "EN50438_Ireland"}},
                {{"S03", "AS4777_Australia"}, {"S10", "SS4777_Newzealand"}},
                {{"S0E", "MEA"}, {"S0F", "PEA"}}}
        };
        readStr = getString(R.string.m369读取值);
        //读取命令功能码（功能码，开始寄存器，结束寄存器）
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
                            byte[] bs = RegisterParseUtil.removePro17(bytes);
                            byte[] valueBs = MaxWifiParseUtil.subBytesFull(bs, 118, 0, 4);
                            BigInteger bigInteger = new BigInteger(1, valueBs);
                            if (bigInteger.longValue() == 0) {
                                //使用旧版本
                                modeType = 0;
                                mType = 0;
                                //设置旧版本值
                                valueBs = MaxWifiParseUtil.subBytesFull(bs, 28, 0, 2);
                            } else {
                                modeType = 1;
                                mType = 1;
                            }
                            if (modeType == 0) {
                                parseOldModle(valueBs);
                            } else if (modeType == 1) {
                                parseNewModle(valueBs);
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
                    BtnDelayUtil.dealMaxBtn(this, what, mContext, mBtnSetting, mTvRight);
                    break;
            }
        }
    };

    private void parseOldModle(byte[] bs) {
        //解析int值
        int value0 = MaxWifiParseUtil.obtainValueHAndL(bs);
        mReadValue = value0;
        //设置值
        contents[0] = MaxUtil.getDeviceModelSingle(value0, 8);
        contents[1] = MaxUtil.getDeviceModelSingle(value0, 7);
        contents[2] = MaxUtil.getDeviceModelSingle(value0, 6);
        contents[3] = MaxUtil.getDeviceModelSingle(value0, 5);
        contents[4] = MaxUtil.getDeviceModelSingle(value0, 4);
        contents[5] = MaxUtil.getDeviceModelSingle(value0, 3);
        contents[6] = MaxUtil.getDeviceModelSingle(value0, 2);
        contents[7] = MaxUtil.getDeviceModelSingle(value0, 1);
        //model as简写
        String as = String.format("A%sS%s", contents[0], contents[7]);
        String[][][] singleModel = modelToal[mType];
        out:
        for (int i = 0; i < singleModel.length; i++) {
            String[][] models = singleModel[i];
            int jLen = models.length;
            for (int j = 0; j < jLen; j++) {
                String[] model = models[j];
                if (model[0].equals(as)) {
                    nowModels = singleModel[i];
                    nowModel = nowModels[j];
                    //更新ui
                    mBtnSelect.setText(nowModel[1]);
                    mTvContent1.setText(String.format("%s-%s", nowModel[0], nowModel[1]));
                    break out;
                }
            }
        }
    }

    private void parseNewModle(byte[] bs) {
        //解析int值
        BigInteger big = new BigInteger(1, bs);
        long bigInteger = big.longValue();
        //更新ui
//        mTvContent1.setText(readStr + ":" + MaxUtil.getDeviceModelNew4(bigInteger));
//        mEt1New4.setText(MaxUtil.getDeviceModelSingleNew(bigInteger, 16) + MaxUtil.getDeviceModelSingleNew(bigInteger, 15));
//        mEt2New4.setText(MaxUtil.getDeviceModelSingleNew(bigInteger, 14) + MaxUtil.getDeviceModelSingleNew(bigInteger, 13));
//        mEt3New4.setText(MaxUtil.getDeviceModelSingleNew(bigInteger, 12) + MaxUtil.getDeviceModelSingleNew(bigInteger, 11));
//        mEt4New4.setText(MaxUtil.getDeviceModelSingleNew(bigInteger, 10) + MaxUtil.getDeviceModelSingleNew(bigInteger, 9));
//        mEt5New4.setText(MaxUtil.getDeviceModelSingleNew(bigInteger, 8) + MaxUtil.getDeviceModelSingleNew(bigInteger, 7));
//        mEt6New4.setText(MaxUtil.getDeviceModelSingleNew(bigInteger, 6) + MaxUtil.getDeviceModelSingleNew(bigInteger, 5));
//        mEt7New4.setText(MaxUtil.getDeviceModelSingleNew(bigInteger, 4) + MaxUtil.getDeviceModelSingleNew(bigInteger, 3)
//                + MaxUtil.getDeviceModelSingleNew(bigInteger, 2)  + MaxUtil.getDeviceModelSingleNew(bigInteger, 1) );
        //设置值
        contents[0] = MaxUtil.getDeviceModelSingleNew(bigInteger, 16) + MaxUtil.getDeviceModelSingleNew(bigInteger, 15);
        contents[1] = MaxUtil.getDeviceModelSingleNew(bigInteger, 14) + MaxUtil.getDeviceModelSingleNew(bigInteger, 13);
        contents[2] = MaxUtil.getDeviceModelSingleNew(bigInteger, 12) + MaxUtil.getDeviceModelSingleNew(bigInteger, 11);
        contents[3] = MaxUtil.getDeviceModelSingleNew(bigInteger, 10) + MaxUtil.getDeviceModelSingleNew(bigInteger, 9);
        contents[4] = MaxUtil.getDeviceModelSingleNew(bigInteger, 8) + MaxUtil.getDeviceModelSingleNew(bigInteger, 7);
        contents[5] = MaxUtil.getDeviceModelSingleNew(bigInteger, 6) + MaxUtil.getDeviceModelSingleNew(bigInteger, 5);
        contents[6] = MaxUtil.getDeviceModelSingleNew(bigInteger, 4) + MaxUtil.getDeviceModelSingleNew(bigInteger, 3);
        contents[7] = MaxUtil.getDeviceModelSingleNew(bigInteger, 2) + MaxUtil.getDeviceModelSingleNew(bigInteger, 1);
        //model as简写
        String as = String.format("S%s", contents[0]);
        String[][][] singleModel = modelToal[mType];
        out:
        for (int i = 0; i < singleModel.length; i++) {
            String[][] models = singleModel[i];
            int jLen = models.length;
            for (int j = 0; j < jLen; j++) {
                String[] model = models[j];
                if (model[0].equals(as)) {
                    nowModels = singleModel[i];
                    nowModel = nowModels[j];
                    //更新ui
                    mBtnSelect.setText(nowModel[1]);
                    mTvContent1.setText(String.format("%s-%s", nowModel[0], nowModel[1]));
                    break out;
                }
            }
        }
    }

    @OnClick({R.id.btnSelect, R.id.btnSetting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnSelect:
                if (nowModels == null) return;
                String[] items = new String[nowModels.length];
                for (int i = 0; i < nowModels.length; i++) {
//                    items[i] = String.format("%s-%s",nowModels[i][0],nowModels[i][1]);
                    items[i] = nowModels[i][1];
                }

                new CircleDialog.Builder()
                        .setTitle(getString(R.string.countryandcity_first_country))
                        .setWidth(0.8f)
                        .setMaxHeight(0.5f)
                        .setItems(items, new OnLvItemClickListener() {
                            @Override
                            public boolean onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                mBtnSelect.setText(items[position]);
                                //设置选中的值
                                String value = nowModels[position][0];
                                switch (mType) {
                                    case 0:
                                        contents[0] = value.substring(1, 2);
                                        contents[7] = value.substring(3, 4);
                                        break;
                                    case 1:
                                        contents[0] = value.substring(1, 3);
                                        break;
                                }
                                return true;
                            }
                        })
                        .setGravity(Gravity.CENTER)
                        .setNegative(getString(R.string.all_no), null)
                        .show(getSupportFragmentManager());
                break;
            case R.id.btnSetting:
                //需要设置的内容
                nowSet = new int[][]{
                        {6, funs2[mType][1], -1}
                        , {6, funs2[mType][1] + 1, -1}
                        , {6, funs2[mType][1] + 2, -1}
                        , {6, funs2[mType][1] + 3, -1}
                };
                switch (mType) {
                    case 0:
                        setOld();
                        break;
                    case 1:
                        setNew();
                        break;
                }
                break;
        }
    }

    private void setOld() {
        try {
            StringBuilder sbHigh = new StringBuilder();
            StringBuilder sbLow = new StringBuilder();
            for (int i = 0, len = contents.length; i < len; i++) {
                if (i < 4) {
                    sbHigh.append(contents[i]);
                } else {
//                            sbHigh.append("0");
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
        try {
            nowSet[0][2] = Integer.parseInt(contents[0] + contents[1], 16);
            nowSet[1][2] = Integer.parseInt(contents[2] + contents[3], 16);
            nowSet[2][2] = Integer.parseInt(contents[4] + contents[5], 16);
            nowSet[3][2] = Integer.parseInt(contents[6] + contents[7], 16);
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
