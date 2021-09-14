package com.growatt.shinetools.module.localbox.mintool;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
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
import com.growatt.shinetools.utils.CommenUtils;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.Mydialog;
import com.growatt.shinetools.utils.Position;
import com.growatt.shinetools.widget.AutoFitTextView;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.view.listener.OnLvItemClickListener;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 单个寄存器选择设置
 * pos==13设置国家进行修改逻辑
 */
public class  OldInvConfigTypeSelectActivity extends DemoBase {
    String readStr ;
    @BindView(R.id.tvTitle1)
    TextView mTvTitle1;
    @BindView(R.id.btnSelect)
    Button mBtnSelect;
    @BindView(R.id.tvContent1)
    TextView mTvContent1;
    @BindView(R.id.tvNote)
    TextView mTvNote;
    @BindView(R.id.btnSetting)
    Button mBtnSetting;
    @BindView(R.id.headerView)
    View headerView;
    @BindView(R.id.tvRight)
    TextView mTvRight;
    private String mTitle;
    private int mType = -1;
    //弹框选择item
    private String[][] items;
    private String[] nowItems;
    private int nowPos = -1;//当前选择下标
    //读取命令功能码（功能码，开始寄存器，结束寄存器）
    private int[][] funs;
    //设置功能码集合（功能码，寄存器，数据）
    private int[][][] funsSet;
    private int[] nowSet = null;
    //国家相关model
    private String[][][] modelToal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_inv_config_type_select);
        ButterKnife.bind(this);
        ModbusUtil.setComAddressOldInv();
        initIntent();
        initString();
        initHeaderView();
        initUI();
    }

    private void initUI() {
        String note = "";
        //是否显示note
        switch (mType){
            case 9:note = getString(R.string.只能在待机状态下设置);break;
        }
        if (!TextUtils.isEmpty(note)){
            CommenUtils.showAllView(mTvNote);
            mTvNote.setText(note);
        }
    }


    private void initString() {
//        国家相关选择Map
        //安规大类{黄色安规号，蓝色安规号，绿色安规号、灰色安规号，#红色安规号(无)}
        // --》{具体安规号+安规值}
        modelToal = new String[][][]{
                {{"A0S1","VDE0126"}, {"A0S4","Italy"},{"A0S7","Germany"},{"A0SB","EN50438_Standard"},{"A0SD","Belgium"},{"A1S3","Demark"},{"A1S4","EN50438_Sweden"},{"A1S5","EN50438_Norway"},{"A1S7","France"},{"A1SA","CEI0-16"},{"A1SB","DEWA"},{"A1SC","BDEW"}},
                {{"A0S2","UK_G59"}, {"A0S8","UK_G83"},{"A0S9","EN50438_Ireland"}},
                {{"A0S3","AS4777_Australia"}, {"A1S0","AS4777_Newzealand"}},
                {{"A0SE","MEA"}, {"A0SF","PEA"}}
        };
        readStr = getString(R.string.m369读取值);
        //读取命令功能码（功能码，开始寄存器，结束寄存器）
        funs = new int[][]{
                {3, 0, 0}//开关机0
                , {3, 1, 1}//安规使能1
                , {3, 2, 2}//记忆使能2
                , {3, 22, 22}//通讯波特率7  0 1 2选择
                , {3, 99, 99}//pf模式8  ----------4
                , {3, 144, 144}//island使能14
                , {3, 150, 150}//风扇检查15
                , {3, 151, 151}//n线使能16
                , {3, 192, 192}//监控功能使能17
                , {3, 194, 194}//电压范围使能18 ---------9
                , {3, 193, 193}//island使能19
                , {3, 163, 163}//MPPT使能20
                //参数设置
                , {3, 15, 15}//语言0
                , {3, 16, 16}//国家1
                , {3, 87, 87}//pid模式11 --------14
                , {3, 88, 88}//pid开关12
                /*-----------------TLX设置16-------------------------*/
                , {3, 202, 202}//防逆流使能
                , {3, 3016, 3016}//干接点使能
                , {3, 3068, 3068}//ct种类选择
                , {3, 3070, 3070}//电池种类 ---------19
                , {3, 3079, 3079}//离网功能使能
                , {3, 3049, 3049}//AC充电使能21
                , {3, 533, 533}//功率采集
                , {3, 3021, 3021}//手动离网使能
                , {3, 3081, 3081}//离网频率-------- 24
                , {3, 3080, 3080}//离网电压
                , {3, 22, 22}//通讯波特率 0 1选择
                , {3, 89, 89}//默认pf曲线
                , {3, 122, 122}//防逆流使能 3个选项
                , {3, 194, 194}//宽电压范围是能 三个选项-------- 29
                , {3, 83, 83}//pv输入模式0-1
                , {3, 163, 163}//pv输入模式0-2
        };
        //弹框选择的数据
        items = new String[][]{
                {getString(R.string.all_close), getString(R.string.all_open)}
                , {getString(R.string.重复5), getString(R.string.重复4)}
                , {getString(R.string.重复5), getString(R.string.重复4)}
                , {"9600bps", "38400bps", "115200bps"}
                , {"PF=1", getString(R.string.PF值设置), getString(R.string.默认PF曲线), getString(R.string.用户设定PF曲线), getString(R.string.滞后无功功率1), getString(R.string.滞后无功功率2), getString(R.string.QV模式), getString(R.string.正负无功值调节)}
                , {getString(R.string.重复5), getString(R.string.重复4)}
                , {getString(R.string.重复5), getString(R.string.重复4)}
                , {getString(R.string.重复5), getString(R.string.重复4)}
                , {getString(R.string.重复5), getString(R.string.重复4)}
                , {"Disable Nonstandard AC voltage range", "Enable Nonstandard AC voltage range"}
                , {getString(R.string.重复5), getString(R.string.重复4)}
                , {"Independent", "DC Source", "Parallel"}
                //参数设置
//                , {"0", "1", "2", "3", "4", "5"}
//                , getResources().getStringArray(R.array.mLCD语言_tool)
                , {getString(R.string.意大利),getString(R.string.英语),getString(R.string.德语),getString(R.string.西班牙语),getString(R.string.法语),getString(R.string.匈牙利语),getString(R.string.土耳其语),getString(R.string.波兰语),getString(R.string.葡萄牙语)}
                , {"Need to Select", "Have Selected"}
                , {"Automatic", "Continual", "Overnight"}
                , {getString(R.string.重复5), getString(R.string.重复4)}
                 /*-----------------TLX设置-------------------------*/
                , {getString(R.string.不使能防逆流), getString(R.string.使能485接口防逆流),getString(R.string.使能232接口防逆流)}
//                , {"Disable exportLimit", "Enable 485 exportLimit", "Enable 232 exportLimit","Enable CT exportLimit"}
                , {getString(R.string.m89禁止), getString(R.string.m88使能)}
                , {"cWiredCT","cWirelessCT","METER"}
                , {"Lithium","Lead-acid","other"}
                , {getString(R.string.m89禁止), getString(R.string.m88使能)}
                , {getString(R.string.m89禁止), getString(R.string.m88使能)}
                ,{getString(R.string.m657不连接功率采集计),getString(R.string.m电表),"CT"}//不使能0，电表值是1  ct3
                , {getString(R.string.m89禁止),getString(R.string.m88使能)}
                , {"50Hz","60Hz"}
                , {"230V","208V","240V"}
                , {"9600bps", "38400bps"}
                , {"PF=1", getString(R.string.PF值设置), getString(R.string.默认PF曲线), getString(R.string.用户设定PF曲线), getString(R.string.滞后无功功率1), getString(R.string.滞后无功功率2), getString(R.string.QV模式), getString(R.string.正负无功值调节)}
                , {getString(R.string.不使能防逆流), getString(R.string.使能485接口防逆流),getString(R.string.使能CT防逆流)}
                , {"0", "1","2"}
                , {"0", "1"}
                , {"0", "1","2"}
        };
        //设置功能码集合（功能码，寄存器，数据）
        funsSet = new int[][][]{
                {{6, 0, 0}, {6, 0, 1}}
                , {{6, 1, 0}, {6, 1, 1}}
                , {{6, 2, 0}, {6, 2, 1}}
                , {{6, 22, 0}, {6, 22, 1}, {6, 22, 2}}
                , {{6, 99, 0}, {6, 99, 1}, {6, 99, 2}, {6, 99, 3}, {6, 99, 4}, {6, 99, 5}, {6, 99, 6}, {6, 99, 7}}
                , {{6, funs[mType][1], 0}, {6, funs[mType][1], 1}}
                , {{6, funs[mType][1], 0}, {6, funs[mType][1], 1}}
                , {{6, funs[mType][1], 0}, {6, funs[mType][1], 1}}
                , {{6, funs[mType][1], 0}, {6, funs[mType][1], 1}}
                , {{6, funs[mType][1], 0}, {6, funs[mType][1], 1}}
                , {{6, funs[mType][1], 0}, {6, funs[mType][1], 1}}
                , {{6, funs[mType][1], 0}, {6, funs[mType][1], 1}, {6, funs[mType][1], 2}}
                //参数设置
                , {{6, 15, 0}, {6, 15, 1}, {6, 15, 2}, {6, 15, 3}, {6, 15, 4}, {6, 15, 5},{6, 15, 6}, {6, 15, 7},{6, 15, 8}}
                , {{6, 16, 0}, {6, 16, 1}}
                , {{6, funs[mType][1], 0}, {6, funs[mType][1], 1}, {6, funs[mType][1], 2}}
                , {{6, funs[mType][1], 0}, {6, funs[mType][1], 1}}
                 /*-----------------TLX设置-------------------------*/
                , {{6,  funs[mType][1], 0}, {6,  funs[mType][1], 1},{6,  funs[mType][1], 2}}
//                , {{6,  funs[mType][1], 0}, {6,  funs[mType][1], 1},{6,  funs[mType][1], 2},{6,  funs[mType][1], 3}}
                , {{6, 3016, 0}, {6, 3016, 1}}
                , {{6, 3068, 0}, {6, 3068, 1},{6, 3068, 2}}
                , {{6, 3070, 0}, {6, 3070, 1},{6, 3070, 2}}
                , {{6, 3079, 0}, {6, 3079, 1}}
                , {{6, 3049, 0}, {6, 3049, 1}}
                , {{6, 533, 0}, {6, 533, 1},{6, 533, 3}}
                , {{6, 3021, 0}, {6, 3021, 1}}
                , {{6, 3081, 0}, {6, 3081, 1}}
                , {{6, 3080, 0}, {6, 3080, 1},{6, 3080, 3}}
                , {{6, 22, 0}, {6, 22, 1}}
                , {{6, 89, 0}, {6, 89, 1}, {6, 89, 2}, {6, 89, 3}, {6, 89, 4}, {6, 89, 5}, {6, 89, 6}, {6, 89, 7}}
                , {{6, 122, 0}, {6, 122, 1},{6, 122, 3}}
                , {{6, 194, 0}, {6, 194, 1},{6, 194, 2}}
                , {{6, 83, 0}, {6, 83, 1}}
                , {{6, 163, 0}, {6, 163, 1},{6, 163, 2}}
        };
        nowItems = items[mType];
        mTvTitle1.setText(mTitle);
        //有些设置进行隐藏
//        if (mType == 4){
//            mBtnSelect.setVisibility(View.INVISIBLE);
//            nowPos = 0;
//            nowSet = funsSet[mType][nowPos];
//        }else
            if (mType == 27){
            mBtnSelect.setVisibility(View.INVISIBLE);
            nowPos = 2;
            nowSet = funsSet[mType][nowPos];
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
                //读取寄存器的值
                readRegisterValue();
            }
        });
    }

    //读取寄存器的值
    private void readRegisterValue() {
        connectServer();
    }

    //连接对象:用于读取数据
    private SocketClientUtil mClientUtil;

    private void connectServer() {
        Mydialog.Show(mContext);
        mClientUtil = SocketClientUtil.connectServer(mHandler);
    }

    //连接对象:用于写数据
    private SocketClientUtil mClientUtilW;

    private void connectServerWrite() {
        Mydialog.Show(mContext);
        mClientUtilW = SocketClientUtil.connectServer(mHandlerW);
    }

    /**
     * 写寄存器handle
     */
    //当前发送的字节数组
    private byte[] sendBytes;
    Handler mHandlerW = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessageWrite(this);
                    sendBytes = sendMsg(mClientUtilW, nowSet);
                    LogUtil.i("发送写入：" + SocketClientUtil.bytesToHexString(sendBytes));
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
//                            byte[] bs = RegisterParseUtil.removePro17Fun6(bytes);
//                            //解析int值
//                            int value0 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 1, 0, 1));
//                            //更新ui
//                            mTvContent1.setText(readStr + ":" + value0);
                            toast(R.string.all_success);
                        } else {
                            toast(R.string.all_failed);
                        }
                        LogUtil.i("接收写入：" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //关闭tcp连接
                        SocketClientUtil.close(mClientUtilW);
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
    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessage(this);
                    byte[] sendBytesR = sendMsg(mClientUtil, funs[mType]);
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
                            int value0 = MaxWifiParseUtil.obtainValueOne(bs);
                            //更新ui
                            mTvContent1.setText(getReadResult(value0));
                            toast(R.string.all_success);
                        } else {
                            toast(R.string.all_failed);
                        }
                        LogUtil.i("接收读取：" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //关闭tcp连接
                        SocketClientUtil.close(mClientUtil);
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
     * 获取读取的值并解析，部分特殊处理
     * @param value
     * @return
     */
    public String getReadResult(int value){
        String result = "";
        switch (mType){
            case 0:
                value = value & 0x0011;
                break;
            case 22:
                if (value == 3) value = 2;
                break;
            case 28:
                if (value == 3) value = 2;
                if (value == 2) value = 1;
                break;
        }
        try {
            if (TextUtils.isEmpty(result)){
                result = readStr + ":" + items[mType][value];
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = readStr + ":" + value;
        }
        return result;
    }
    /**
     * 根据命令以及起始寄存器发送查询命令
     *
     * @param clientUtil
     * @param sends
     * @return：返回发送的字节数组
     */
    private byte[] sendMsg(SocketClientUtil clientUtil, int[] sends) {
        if (clientUtil != null) {
            byte[] sendBytes = ModbusUtil.sendMsgOldInv(sends[0], sends[1], sends[2]);
            clientUtil.sendMsg(sendBytes);
            return sendBytes;
        } else {
            connectServer();
            return null;
        }
    }

    @OnClick({R.id.btnSelect, R.id.btnSetting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnSelect:
                new CircleDialog.Builder()
                        .setWidth(0.7f)
                        .setMaxHeight(0.5f)
                        .setGravity(Gravity.CENTER)
                        .setTitle(getString(R.string.countryandcity_first_country))
                        .setItems(nowItems, new OnLvItemClickListener() {
                            @Override
                            public boolean onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                if (nowItems != null && nowItems.length > position) {
                                    mBtnSelect.setText(nowItems[position] + "(" + position + ")");
                                    nowPos = position;
                                }
                                return true;
                            }
                        })
                        .setNegative(getString(R.string.all_no),null)
                        .show(getSupportFragmentManager());
                break;
            case R.id.btnSetting:
                if (nowPos == -1) {
                    toast(getString(R.string.m257请选择设置值));
                } else {
                    nowSet = funsSet[mType][nowPos];
//                    connectServerWrite();
                    readRegisterValueCom();
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
                            connectServerWrite();
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
