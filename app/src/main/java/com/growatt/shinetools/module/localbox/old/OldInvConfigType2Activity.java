package com.growatt.shinetools.module.localbox.old;

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
import com.growatt.shinetools.modbusbox.Arith;
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

import static com.growatt.shinetools.utils.BtnDelayUtil.TIMEOUT_RECEIVE;


/**
 * 2个寄存器分别设置
 */
public class OldInvConfigType2Activity extends DemoBase {

    String readStr ;
    String notSetStr;
    @BindView(R.id.tvTitle1)
    TextView mTvTitle1;
    @BindView(R.id.etContent1)
    EditText mEtContent1;
    @BindView(R.id.tvContent1)
    TextView mTvContent1;
    @BindView(R.id.tvTitle2)
    TextView mTvTitle2;
    @BindView(R.id.etContent2)
    EditText mEtContent2;
    @BindView(R.id.tvContent2)
    TextView mTvContent2;
    @BindView(R.id.btnSetting)
    Button mBtnSetting;
    @BindView(R.id.headerView)
    View headerView;
    @BindView(R.id.tvRight)
    TextView mTvRight;
    private String mTitle;
    private int mType = -1;
    //读取命令功能码（功能码，开始寄存器，结束寄存器）
    private int[][] funs;
    //内容标题显示容器
    private String[][] titles;
    private String[][] registers;
    //设置的内容
    private int[][] nowSet;
    private float[][] mMultiples;//倍数集合
    private float[] mMul = new float[]{
            1,1
    };//当前倍数
    private String[][] mUnits;
    private String[] mUnit = new String[]{
            "",""
    };//当前单位
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_inv_config_type2);
        ButterKnife.bind(this);
        ModbusUtil.setComAddressOldInv();
        initIntent();
        initString();
        initHeaderView();
    }

    private void initString() {
        readStr = getString(R.string.m369读取值);
        notSetStr = getString(R.string.m393暂不允许设置);
        //读取命令功能码（功能码，开始寄存器，结束寄存器）
        funs = new int[][]{
                {3, 82, 131}//21
                , {3, 108, 109}//22
                , {3, 110, 111}//23
                , {3, 113, 118}//24
                , {3, 114, 115}//25  -----------------4
                , {3, 233, 234}//26
                //参数设置
                , {3, 122, 123}//15
                , {3, 19, 20}//16
                , {3, 21, 22}//17
                , {3, 35, 36}//18   -------------------9
                , {3, 37, 38}//19
                , {3, 184, 185}//20
                , {3, 188, 189}//21
                , {3, 39, 40}//22
                , {3, 41, 42}//23   -----------------14
                , {3, 51, 52}//24
                , {3, 53, 54}//25
                , {3, 186, 187}//26
                , {3, 55, 56}//27
                , {3, 57, 58}//28  ---------------19
                , {3, 190, 191}//29
        };
        registers = new String[][]{
                {"(82)","(131)"},
                {"(108)","(109)"},
                {"(110)","(111)"},
                {"(113)","(118)"},
                {"(114)","(115)"},
                {"1(233)","2(234)"},

                {"(122)","(123)"},
                {"(19)","(20)"},
                {"(21)","(22)"},
                {"(35)","(36)"},
                {"(37)","(38)"},
                {"(184)","(185)"},
                {"(188)","(189)"},
                {"(39)","(40)"},
                {"(41)","(42)"},
                {"(51)","(52)"},
                {"(53)","(54)"},
                {"(186)","(187)"},
                {"(55)","(56)"},
                {"(57)","(58)"},
                {"(190)","(191)"}



        };
        titles = new String[][]{
                {getString(R.string.m379电源启动斜率),getString(R.string.m380电源重启斜率)},
                {getString(R.string.m381Qv切入高压),getString(R.string.m382Qv切出高压)},
                {getString(R.string.m383Qv切入低压),getString(R.string.m384Qv切出低压)},
                {getString(R.string.m385Qv切入功率),getString(R.string.m386Qv切出功率)},
                {getString(R.string.m387无功曲线切入电压),getString(R.string.m388无功曲线切出电压)},
                {getString(R.string.m389检查固件),getString(R.string.m389检查固件)},
//                {"电源启动斜率(20)", "电源重启斜率(21)"}
//                , {"Q(v)切入高压(93)", "Q(v)切出高压(94)"}
//                , {"Q(v)切入低压(95)", "Q(v)切出低压(96)"}
//                , {"Q(v)切入功率(97)", "Q(v)切出功率(98)"}
//                , {"无功曲线切入电压(99)", "无功曲线切出电压(100)"}
//                , {"检查固件1(233)", "检查固件2(234)"}
                //参数设置
                {getString(R.string.m436逆变器经纬度),getString(R.string.m436逆变器经纬度)},
                {String.format("%s",getString(R.string.m373低)),String.format("%s",getString(R.string.m372高))},
                {String.format("%s",getString(R.string.m373低)),String.format("%s",getString(R.string.m372高))},
                {String.format("%s",getString(R.string.m373低)),String.format("%s",getString(R.string.m372高))},
                {String.format("%s",getString(R.string.m373低)),String.format("%s",getString(R.string.m372高))},
                {String.format("%s",getString(R.string.m373低)),String.format("%s",getString(R.string.m372高))},
                {String.format("%s",getString(R.string.m373低)),String.format("%s",getString(R.string.m372高))},
                {getString(R.string.m373低),getString(R.string.m372高)},
                {getString(R.string.m373低),getString(R.string.m372高)},

                {getString(R.string.m373低),getString(R.string.m372高)},
                {getString(R.string.m373低),getString(R.string.m372高)},
                {getString(R.string.m373低),getString(R.string.m372高)},
                {getString(R.string.m373低),getString(R.string.m372高)},
                {getString(R.string.m373低),getString(R.string.m372高)},
                {getString(R.string.m373低),getString(R.string.m372高)}
//                {String.format("AC%s1%s",getString(R.string.m441电压限制时间),getString(R.string.m373低)),String.format("AC%s1%s",getString(R.string.m441电压限制时间),getString(R.string.m372高))},
//                {String.format("AC%s2%s",getString(R.string.m441电压限制时间),getString(R.string.m373低)),String.format("AC%s2%s",getString(R.string.m441电压限制时间),getString(R.string.m372高))},
//                {String.format("AC%s1%s",getString(R.string.m442频率限制时间),getString(R.string.m373低)),String.format("AC%s1%s",getString(R.string.m442频率限制时间),getString(R.string.m372高))},
//                {String.format("AC%s2%s",getString(R.string.m442频率限制时间),getString(R.string.m373低)),String.format("AC%s2%s",getString(R.string.m442频率限制时间),getString(R.string.m372高))},
//                {String.format("AC%s3%s",getString(R.string.m441电压限制时间),getString(R.string.m373低)),String.format("AC%s3%s",getString(R.string.m441电压限制时间),getString(R.string.m372高))},
//                {String.format("AC%s3%s",getString(R.string.m442频率限制时间),getString(R.string.m373低)),String.format("AC%s3%s",getString(R.string.m442频率限制时间),getString(R.string.m372高))}
//                , {"逆变器经度(122)", "逆变器纬度(123)"}
//                , {"AC1限制电压低(52)", "AC1限制电压高(53)"}
//                , {"AC1频率限制低(54)", "AC1频率限制高(55)"}
//                , {"AC2限制电压低(56)", "AC2限制电压高(57)"}
//                , {"AC2频率限制低(58)", "AC2频率限制高(59)"}
//                , {"AC3限制电压低(60)", "AC3限制电压高(61)"}
//                , {"AC3频率限制低(62)", "AC3频率限制高(63)"}
//                , {"并网电压限制低(64)", "并网电压限制高(65)"}
//                , {"并网频率限制低(66)", "并网频率限制高(67)"}
//                , {"AC电压限制时间1低(68)", "AC电压限制时间1高(69)"}
//                , {"AC电压限制时间2低(70)", "AC电压限制时间2高(71)"}
//                , {"AC频率限制时间1低(72)", "AC频率限制时间1高(73)"}
//                , {"AC频率限制时间2低(74)", "AC频率限制时间2高(75)"}
//                , {"AC电压限制时间3低(76)", "AC电压限制时间3高(77)"}
//                , {"AC频率限制时间3低(78)", "AC频率限制时间3高(79)"}
        };
        mTvTitle1.setText(String.format("%s%s",titles[mType][0],registers[mType][0]));
        mTvTitle2.setText(String.format("%s%s",titles[mType][1],registers[mType][1]));
        //需要设置的内容
        nowSet = new int[][]{
                {6, funs[mType][1], -1}
                , {6, funs[mType][2], -1}
        };
        try {
            mMultiples = new float[][]{
                    {0.1f,0.1f},{0.1f,0.1f},{0.1f,0.1f},{1,1},{0.1f,0.1f}
                    ,{1,1},{1,1},{0.1f,0.1f},{0.01f,0.01f},{0.1f,0.1f}
                    ,{0.01f,0.01f},{0.1f,0.1f},{0.01f,0.01f},{0.1f,0.1f},{0.01f,0.01f}
                    ,{1,1},{1,1},{1,1},{1,1},{1,1}
                    ,{1,1}
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mUnits = new String[][]{
                    {"%","%"},{"V","V"},{"V","V"},{"%","%"},{"V","V"}
                    ,{"",""} ,{"",""},{"V","V"},{"Hz","Hz"},{"V","V"}
                    ,{"Hz","Hz"},{"V","V"},{"Hz","Hz"},{"V","V"},{"Hz","Hz"}
                    ,{"",""} ,{"",""},{"",""} ,{"",""},{"",""}
                    ,{"",""}
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mMul = mMultiples[mType];
            mUnit = mUnits[mType];
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String getReadValueReal(int pos,int read){
        String value = Arith.mul(read,mMul[pos],2) + mUnit[pos];
        return value;
    }
    public int getWriteValueReal(int pos,double write){
        try {
            return (int) Math.round(Arith.div(write,mMul[pos]));
        } catch (Exception e) {
            e.printStackTrace();
            return (int) write;
        }
    }
    private void initIntent() {
        Intent mIntent = getIntent();
        if (mIntent != null) {
            mTitle = mIntent.getStringExtra("title");
            mType = mIntent.getIntExtra("type", -1);
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
                if (funs[mType][2] - funs[mType][1] > 44){
                    readRegisterValue2();
                }else {
                    //刷新
                    readRegisterValue();
                }
            }
        });
    }

    //连接对象:用于读取数据
    private SocketClientUtil mClientUtilRead;

    //读取寄存器的值
    private void readRegisterValue() {
        Mydialog.Show(mContext);
        mClientUtilRead = SocketClientUtil.connectServer(mHandlerRead);
    }
    //读取寄存器的值
    private void readRegisterValue2() {
        Mydialog.Show(mContext);
        mClientUtilRead = SocketClientUtil.connectServer(mHandlerRead2);
    }

    //连接对象:用于设置数据
    private SocketClientUtil mClientUtilWriter;
    //设置寄存器的值
    private void writeRegisterValue() {
        Mydialog.Show(mContext);
        mClientUtilWriter = SocketClientUtil.connectServer(mHandlerWrite);
    }
    /**
     * 写寄存器handle
     */
    //当前发送的字节数组
    private byte[] sendBytes;
    //当前设置的输入框：0:text1/1:text2
    private int nowPos = -1;
    Handler mHandlerWrite = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessageWrite(this);
                    if (nowSet != null) {
                        if (nowSet[0][2] == -1 && nowSet[1][2] == -1) {
                            nowPos = -1;
                            //关闭tcp连接
                            if (mClientUtilWriter != null) {
                                mClientUtilWriter.closeSocket();
                                BtnDelayUtil.refreshFinish();
                                //移除接收超时
                                this.removeMessages(TIMEOUT_RECEIVE);
                            }
                        } else {
                            for (int i = 0, len = nowSet.length; i < len; i++) {
                                if (nowSet[i][2] != -1) {
                                    nowPos = i;
                                    sendBytes = SocketClientUtil.sendMsgToServerOldInv(mClientUtilWriter, nowSet[i]);
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
                            toast(titles[mType][nowPos] + ":" + getString(R.string.all_success));
                            //继续发送设置命令
                            mHandlerWrite.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        } else {
                            toast(titles[mType][nowPos] + ":" + getString(R.string.all_failed));
                            //将内容设置为-1初始值
                            nowSet[0][2] = -1;
                            nowSet[1][2] = -1;
                            //关闭tcp连接
                            SocketClientUtil.close(mClientUtilWriter);
                            BtnDelayUtil.refreshFinish();
                        }
                        LogUtil.i("接收写入" + (nowPos + 1) + ":" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        //关闭tcp连接
                        SocketClientUtil.close(mClientUtilWriter);
                        BtnDelayUtil.refreshFinish();
                    }
                    break;
                default:
                    BtnDelayUtil.dealTLXBtnWrite(this,what,mContext,mBtnSetting,mTvRight);
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
                    byte[] sendBytesR = SocketClientUtil.sendMsgToServerOldInv(mClientUtilRead, funs[mType]);
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
                            int value0 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 0, 0, 1));
                            int value1 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, bs.length/2-1, 0, 1));
                            //更新ui
                            try {
                                mTvContent1.setText(readStr + ":" + getReadValueReal(0,value0));
                            } catch (Exception e) {
                                e.printStackTrace();
                                mTvContent1.setText(readStr + ":" + value0);
                            }
                            try {
                                mTvContent2.setText(readStr + ":" + getReadValueReal(1,value1));
                            } catch (Exception e) {
                                e.printStackTrace();
                                mTvContent2.setText(readStr + ":" + value1);
                            }
                            toast(R.string.all_success);
                        } else {
                            toast(R.string.all_failed);
                        }
                        LogUtil.i("接收读取：" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        SocketClientUtil.close(mClientUtilRead);
                        BtnDelayUtil.refreshFinish();
                    }
                    break;
                default:
                    BtnDelayUtil.dealTLXBtn(this,what,mContext,mBtnSetting,mTvRight);
                    break;
            }
        }
    };
    /**
     * 读取寄存器handle 分开读取
     */
    int nowReadPos = 0;
    Handler mHandlerRead2 = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessage(this);
                    if (nowReadPos < 2){
                        int[] funReads = new int[3];
                        funReads[0] = funs[mType][0];
                        funReads[1] = funReads[2] = funs[mType][nowReadPos+1];
                        byte[] sendBytesR = SocketClientUtil.sendMsgToServerOldInv(mClientUtilRead, funReads);
                        LogUtil.i("发送读取：" + SocketClientUtil.bytesToHexString(sendBytesR));
                    }
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
                            int value0 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 0, 0, 1));
                            if (nowReadPos == 0){
                                //更新ui
                                try {
                                    mTvContent1.setText(readStr + ":" + getReadValueReal(0,value0));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    mTvContent1.setText(readStr + ":" + value0);
                                }
                            }else if (nowReadPos == 1){
                                try {
                                    mTvContent2.setText(readStr + ":" + getReadValueReal(1,value0));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    mTvContent2.setText(readStr + ":" + value0);
                                }
                            }
                            toast(R.string.all_success);
                        } else {
                            toast(R.string.all_failed);
                        }
                        if (nowReadPos < 1) {
                            nowReadPos++;
                            sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        }else {
                            //关闭连接
                            nowReadPos = 0;
                            SocketClientUtil.close(mClientUtilRead);
                            BtnDelayUtil.refreshFinish();
                        }
                        LogUtil.i("接收读取：" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        nowReadPos = 0;
                        SocketClientUtil.close(mClientUtilRead);
                        BtnDelayUtil.refreshFinish();
                    }
                    break;
                default:
                    BtnDelayUtil.dealTLXBtn(this,what,mContext,mBtnSetting,mTvRight);
                    break;
            }
        }
    };

    @OnClick(R.id.btnSetting)
    public void onViewClicked() {
        if (mType == 5){
            toast(notSetStr);
            return;
        }
        //获取用户输入内容
        String content1 = mEtContent1.getText().toString();
        String content2 = mEtContent2.getText().toString();
        if (TextUtils.isEmpty(content1) && TextUtils.isEmpty(content2)) {
            toast(R.string.all_blank);
        } else {
            try {
                if (!TextUtils.isEmpty(content1)) {
                    nowSet[0][2] = getWriteValueReal(0,Double.parseDouble(content1));
                }
                if (!TextUtils.isEmpty(content2)) {
                    nowSet[1][2] = getWriteValueReal(1,Double.parseDouble(content2));
                }
                readRegisterValueCom();
            } catch (NumberFormatException e) {
                e.printStackTrace();
                toast(getString(R.string.m363设置失败));
                nowSet[0][2] = -1;
                nowSet[1][2] = -1;
            } finally {
                mEtContent1.setText("");
                mEtContent2.setText("");
            }
        }
    }
    //连接对象:用于读取数据
    private SocketClientUtil mClientUtilReadCom;
    private int[] funCom = {3,0,40};
    //读取寄存器的值
    private void readRegisterValueCom() {
        Mydialog.Show(mContext);
        mClientUtilReadCom = SocketClientUtil.connectServer(mHandlerReadCom);
    }
    /**
     * 读取寄存器handle
     */
    Handler mHandlerReadCom = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessage(this);
                    byte[] sendBytesR = SocketClientUtil.sendMsgToServerOldInv(mClientUtilReadCom, funCom);
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
                            //解析读取值，设置com地址以及model
                            int comAddress = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes125(bs,30,0,1));
                            ModbusUtil.setComAddressOldInv(comAddress);
                            //关闭tcp连接
                            SocketClientUtil.close(mClientUtilReadCom);
                            BtnDelayUtil.refreshFinish();
                            //设置值
                            writeRegisterValue();
                        } else {
                            toast(R.string.all_failed);
                            //关闭tcp连接
                            SocketClientUtil.close(mClientUtilReadCom);
                            BtnDelayUtil.refreshFinish();
                        }
                        LogUtil.i("接收读取：" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        //关闭tcp连接
                        SocketClientUtil.close(mClientUtilReadCom);
                        BtnDelayUtil.refreshFinish();
                    }
                    break;
                default:
                    BtnDelayUtil.dealTLXBtn(this, what, mContext,  mTvRight);
                    break;
            }
        }
    };
}
