package com.growatt.shinetools.module.localbox.tlx;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.growatt.shinetools.R;
import com.growatt.shinetools.ShineToosApplication;
import com.growatt.shinetools.base.DemoBase;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.MaxWifiParseUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.module.localbox.tlxh.TLXHAutoTestOldInvActivity;
import com.growatt.shinetools.utils.BtnDelayUtil;
import com.growatt.shinetools.utils.CommenUtils;
import com.growatt.shinetools.utils.DateUtils;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.Mydialog;
import com.mylhyl.circledialog.CircleDialog;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.growatt.shinetools.constant.GlobalConstant.END_USER;
import static com.growatt.shinetools.constant.GlobalConstant.MAINTEAN_USER;


public class TlxFastConfigActivity extends DemoBase {

    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    @BindView(R.id.tvRight)
    TextView mTvRight;
    @BindView(R.id.tvTitle1)
    TextView mTvTitle1;
    @BindView(R.id.btnSelect1Type)
    TextView mBtnSelect1Type;
    @BindView(R.id.btnSelect1Mech)
    TextView mBtnSelect1Mech;
    @BindView(R.id.btnSelectModel)
    TextView mBtnSelectModel;
    @BindView(R.id.checkBox1)
    CheckBox mCheckBox1;
    @BindView(R.id.btnSelect1)
    Button mBtnSelect1;
    @BindView(R.id.tvTitle2)
    TextView mTvTitle2;
    @BindView(R.id.checkBox2)
    CheckBox mCheckBox2;
    @BindView(R.id.btnSelect2)
    Button mBtnSelect2;
    @BindView(R.id.tvTitle3)
    TextView mTvTitle3;
    @BindView(R.id.checkBox3)
    CheckBox mCheckBox3;
    //    @BindView(R.id.btnSelect3)
//    Button mBtnSelect3;
    @BindView(R.id.tvTitle4)
    TextView mTvTitle4;
    @BindView(R.id.checkBox4)
    CheckBox mCheckBox4;

    @BindView(R.id.tvTitle5)
    TextView mTvTitle5;
    @BindView(R.id.checkBox5)
    CheckBox mCheckBox5;

    @BindView(R.id.btn3TextField1)
    TextView mbtn3TextField1;
    @BindView(R.id.btnSelect3)
    Button mbtnSelect3;
    @BindView(R.id.btn3TextField2)
    TextView mbtn3TextField2;
    @BindView(R.id.btnSelect32)
    Button mbtnSelect32;
    @BindView(R.id.btn3TextField13)
    TextView mbtn3TextField13;
    @BindView(R.id.btnSelect33)
    Button mbtnSelect33;

    @BindView(R.id.btn4TextField1)
    TextView mbtn4TextField1;
    @BindView(R.id.btnSelect4)
    Button mBtnSelect4;
    @BindView(R.id.btn4TextField2)
    TextView mbtn4TextField2;
    @BindView(R.id.btnSelect42)
    EditText mbtnSelect42;
    @BindView(R.id.btn4TextField13)
    TextView mbtn4TextField13;
    @BindView(R.id.btnSelect43)
    EditText mbtnSelect43;


    @BindView(R.id.cb_national_safeReg)
    CheckBox cbNationalSageReg;
    @BindView(R.id.btn_safetyreg)
    Button btnSafeTyReg;
    @BindView(R.id.cb_invert_time)
    CheckBox cbInvetTime;
    @BindView(R.id.btn_inverter_time)
    Button btnInverterTime;
    @BindView(R.id.tv_lcd_title)
    TextView tvLcdTitle;
    @BindView(R.id.cb_lcd)
    CheckBox cbLcd;
    @BindView(R.id.btn_lcd_setting)
    Button btnLcdSetting;


    Calendar mSelectCalendar = Calendar.getInstance();
    @BindView(R.id.ivLeft)
    ImageView ivLeft;
    @BindView(R.id.relativeLayout1)
    RelativeLayout relativeLayout1;
    @BindView(R.id.headerView)
    LinearLayout headerView;
    @BindView(R.id.tv_national_safetyReg)
    TextView tvNationalSafetyReg;
    @BindView(R.id.tv_inveter_time)
    TextView tvInveterTime;

    private String[][] modelToal;

    private List<String> select1Models = new ArrayList<>();
    private List<String> select1ModelValues = new ArrayList<>();

    //当前的值
    private String[] nowModel;

    //弹框选择item
    private String[][] items;
    //设置功能码集合（功能码，寄存器，数据）
    private int[][][] funsSet;
    //读取命令功能码（功能码，开始寄存器，结束寄存器）
    private int[][] funs;


    private List<String> addressList;
    //设置时间
    private String time;
    private int language = -1;
    private int address = -1;
    private String[] sucNotes;
    //寄存器设置数据集合
    private String[] contents = new String[8];
    private String[] select1Types = {
            "MTL-S/-S", "3-15K TL3-S", "17-25K 30-50K TL3-S"
    };
    private int[] seletTypeDTCs = {
            5100, 5200, 5300
    };


    Handler mHandlerWrite = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    try {
                        if (nowSetPos != -1) {
                            mPos = 0;
                            BtnDelayUtil.sendMessageWrite(this);
                            int[][] nowSet = funsSet[nowSetPos];
                            boolean isNowSet = false;
                            for (int len = nowSet.length, i = 0; i < len; i++) {
                                if (nowSet[i][2] != -1) {
                                    mPos = i;
                                    BtnDelayUtil.sendMessageWrite(this);
                                    sendBytes = SocketClientUtil.sendMsgToServer(mClientUtilWriter, nowSet[i]);
                                    LogUtil.i("发送写入" + (i + 1) + ":" + SocketClientUtil.bytesToHexString(sendBytes));
                                    //发送完将值设置为-1
                                    nowSet[i][2] = -1;
                                    isNowSet = true;
                                    break;

                                }
                            }
                            if (!isNowSet) {
                                writeValue(false);
                            }
                        } else {
                            //关闭tcp连接
                            SocketClientUtil.close(mClientUtilWriter);
                            BtnDelayUtil.refreshFinish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        //关闭tcp连接
                        SocketClientUtil.close(mClientUtilWriter);
                        BtnDelayUtil.refreshFinish();
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
                            String success = "";
                            if (nowSetPos == 0 || nowSetPos == 1) {
                                success = sucNotes[nowSetPos] + " " + getString(R.string.all_success);
                            } else if (nowSetPos == 2) {
                                String[] afciArray = {getString(R.string.AFCI使能), getString(R.string.AFCI自检), getString(R.string.AFCI复位)};
                                if (mPos < afciArray.length) {
                                    success = afciArray[mPos] + " " + getString(R.string.all_success);
                                }
                            } else if (nowSetPos == 3) {
                                String[] antiArray = {getString(R.string.防逆流使能), getString(R.string.m防逆流功率百分比), getString(R.string.m防逆流失效后默认功率百分比)};
                                if (mPos < antiArray.length) {
                                    success = antiArray[mPos] + " " + getString(R.string.all_success);
                                }
                            }


//                            if (mPos == funsSet[nowSetPos].length - 1) {
//                                success = sucNotes[nowSetPos] + " " + getString(R.string.all_success);
//                            }
                            if (!TextUtils.isEmpty(success)) {
                                toast(success);
                            }
                            //继续发送设置命令
                            mHandlerWrite.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        } else {
                            toast(getString(R.string.all_failed));
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
                    BtnDelayUtil.dealTLXBtnWrite(this, what, mContext, mTvRight);
                    break;
            }
        }
    };
    private List<String> comList;
    private List<String> meterList;
    private List<String> antiList;
    private String[] select1Mechs = {
            "EU Model", "Australia Model", "UK Model"
    };

    private int mSelectType = -1;
    private int mSelectMech = -1;
    private String mSelectModel = "";
    private boolean isFlagModel;//是否匹配到了DTC model
    private int deviceType;
    //连接对象:用于设置数据
    private SocketClientUtil mClientUtilWriter;
    private int nowSetPos = -1;
    /**
     * 写寄存器handle
     */
    //当前发送的字节数组
    private byte[] sendBytes;
    private int mPos = 0;
    private String[] selectOnOrOff = {
            "Off", "On"
    };
    //连接对象:用于读取数据
    private SocketClientUtil mClientUtilRead;
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
                    byte[] sendBytesR = SocketClientUtil.sendMsgToServerOldInv(mClientUtilRead, funs[0]);
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
                            int comAddress = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 30, 0, 1));
                            ModbusUtil.setComAddressOldInv(comAddress);
                            //设置modle中间位
                            parseOldModleCenter(MaxWifiParseUtil.subBytes125(bs, 28, 0, 2));
                            //关闭tcp连接
                            SocketClientUtil.close(mClientUtilRead);
                            BtnDelayUtil.refreshFinish();
                            //设置值
                            writeValue(true);
                        } else {
                            toast(R.string.all_failed);
                            //关闭tcp连接
                            SocketClientUtil.close(mClientUtilRead);
                            BtnDelayUtil.refreshFinish();
                        }
                        LogUtil.i("接收读取：" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        //关闭tcp连接
                        SocketClientUtil.close(mClientUtilRead);
                        BtnDelayUtil.refreshFinish();
                    }
                    break;
                default:
                    BtnDelayUtil.dealTLXBtn(this, what, mContext, mTvRight);
                    break;
            }
        }
    };
    //连接对象:用于读取数据
    private SocketClientUtil mClientUtilReadDTC;
    /**
     * 读取寄存器handle
     */
    private int[][] autoFunDTC = {{3, 0, 124}};
    private int autoCountDTC = 0;
    Handler mHandlerReadAutoDTC = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    if (autoCountDTC < autoFunDTC.length) {
                        byte[] sendBytesR = SocketClientUtil.sendMsgToServer(mClientUtilReadDTC, autoFunDTC[autoCountDTC]);
                        LogUtil.i("发送读取：" + SocketClientUtil.bytesToHexString(sendBytesR));
                    }
                    break;
                //接收字节数组
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    this.removeMessages(SocketClientUtil.SOCKET_EXCETION_CLOSE);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //检测内容正确性
                        boolean isCheck = ModbusUtil.checkModbus(bytes);
                        if (isCheck) {
                            byte[] bs = RegisterParseUtil.removePro17(bytes);
                            //解析mo'de'l
                            byte[] valueBs = MaxWifiParseUtil.subBytesFull(bs, 118, 0, 4);
                            parseNewModle(valueBs);
                            //解析DTC
                            int dtc = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 43, 0, 1));
                            //是否在可选DTC上
                            boolean isFlag = false;
                            for (int seletTypeDTC : seletTypeDTCs) {
                                if (dtc == seletTypeDTC) {
                                    isFlag = true;
                                    break;
                                }
                            }
                            if (!isFlag) {
                                mHandlerReadAutoDTC.sendEmptyMessage(SocketClientUtil.SOCKET_EXCETION_CLOSE);
                            } else {
                                isFlagModel = false;//是否能匹配到model
                                //model as简写
                                String as = String.format("S%s", contents[0]);
                                for (String[] model : modelToal) {
                                    if (model[0].equals(as)) {
                                        nowModel = model;
                                        isFlagModel = true;
                                        //更新ui
//                mBtnSelectModel.setText(String.format("%s(%s)", nowModel[0], nowModel[1]));
                                        break;
                                    }
                                }
                                String model1 = String.format("%s(%s)", nowModel[0], nowModel[1]);
                                if (isFlagModel) {//识别到了model对应分组
                                    btnSafeTyReg.setText(model1);
                                    new CircleDialog.Builder()
                                            .setWidth(0.7f)
                                            .setGravity(Gravity.CENTER)
                                            .setMaxHeight(0.5f)
                                            .setTitle(getString(R.string.m225请选择))
                                            .setNegative(getString(R.string.all_no), null)
                                            .setItems(select1Models, (parent, view1, position, id) -> {
                                                btnSafeTyReg.setText(select1Models.get(position));
                                                mSelectModel = select1ModelValues.get(position);
                                                return true;
                                            })
                                            .show(getSupportFragmentManager());
                                } else {//没有识别到，直接显示model
                                    mSelectModel = as;
                                    btnSafeTyReg.setText(model1);
                                    //弹框是否手动选择
                                    dialogShow(getString(R.string.m1259未识别对应的安规));
                                }
                            }
                        }
//                        if (autoCountDTC < autoFunDTC.length - 1) {
//                            autoCountDTC++;
//                            this.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
//                        } else {
//                            autoCountDTC = 0;
//                            operateJump();
//                        }
                        LogUtil.i("接收读取：" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        autoCountDTC = 0;
                    } finally {
                        //关闭tcp连接
                        SocketClientUtil.close(mClientUtilReadDTC);
                        Mydialog.Dismiss();
                        autoCountDTC = 0;
                    }
                    break;
                case SocketClientUtil.SOCKET_EXCETION_CLOSE:
                    //关闭tcp连接
                    SocketClientUtil.close(mClientUtilReadDTC);
                    Mydialog.Dismiss();
                    dialogShow(getString(R.string.reminder));
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tlx_fast_config);
        ButterKnife.bind(this);


        ModbusUtil.setComAddressOldInv();
        initString();
    }

    private void initString() {
        Intent intent = getIntent();
        deviceType = intent.getIntExtra("deviceType", 0);
        int user_type = ShineToosApplication.getContext().getUser_type();

        if ((user_type==MAINTEAN_USER||user_type == END_USER )&& deviceType==0) {
            CommenUtils.hideAllView(View.GONE,tvNationalSafetyReg,tvInveterTime,tvLcdTitle,mTvTitle1,mTvTitle2,mTvTitle5 );
        }

        comList = new ArrayList<>();
        for (int i = 1; i < 100; i++) {
            comList.add(String.valueOf(i));
        }
        meterList = new ArrayList<>();
        meterList.add("None");
        meterList.add("Meter");
        if (deviceType == 1) {
            meterList.add("CT");
        }

        antiList = new ArrayList<>();
        antiList.add("Off");
        antiList.add("On(with Meter)");
        if (deviceType == 1) {
            antiList.add("On(with CT)");
        }

        mTvTitle.setText(R.string.快速设置);
        mTvRight.setText(R.string.m配置);
        sucNotes = new String[]{
                getString(R.string.m国家安规)
                , getString(R.string.mlocal逆变器时间)
                , getString(R.string.mLCD语言),
                getString(R.string.m423通信地址)
                , getString(R.string.m1219功率采集器_ios)
                , "AFCI"
                , getString(R.string.m防逆流设置)
        };
        funs = new int[][]{
                //参数设置
                {3, 0, 124}
        };


//
//        };
        modelToal = new String[][]{
                {"VDE0126", "S01"}
                , {"G99", "S02"}
                , {"AS4777", "S03"}
                , {" CEI0_21", "S04"}
                , {"SPAIN", "S05"}
                , {"GREECE_CONTINENT", "S06"}
                , {"N4105", "S07"}
                , {"G98", "S08"}
                , {"IRELAND", "S09"}
                , {"CQC_2013", "S0A"}
                , {"EN50438", "S0B"}
                , {"HUNGARY", "S0C"}
                , {"BELAGIUM", "S0D"}
                , {"MEA", "S0E"}
                , {"PEA", "S0F"}
                , {"NEWZWALAND", "S10"}
                , {"CQC_PLANT", "S11"}
                , {"INDIA", "S12"}
                , {"DEMARK_DK1", "S13"}
                , {"SWEDEN", "S14"}
                , {" NORWAY", "S15"}
                , {"QUEENSLAND", "S16"}
                , {"FRANCE", "S17"}
                , {"KOREA_60HZ", "S18"}
                , {"BRAZIL", "S19"}
                , {"CEI0_16", "S1A"}
                , {"DEWA", "S1B"}
                , {"CHILE", "S1C"}
                , {"ARGENTINA", "S1D"}
                , {"N4110_BDEW", "S1E"}
                , {"TAIWAN_VPC", "S1F"}
                , {"DEMARK_DK2", "S20"}
                , {"CQC_2018", "S21"}
                , {"DEMARK_TR3_3_1", "S22"}
                , {"PLOAND", "S23"}
                , {"TAIWAN_TPC", "S24"}
                , {"IEEE1547.1", "S25"}
                , {"BRAZIL_240V", "S26"}
                , {"EN50549", "S27"}
                , {"AU_VICTORIA", "S28"}
                , {"AU_WESTERN", "S29"}
                , {"AU_HORIZON ", "S2A"}
                , {"AU_AUSGRID", "S2B"}
                , {"AU_ENDEAVOUR", "S2C"}
                , {"AU_ERGONENERGY", "S2D"}
                , {"AU_ENERGEX", "S2E"}
                , {"AU_SANETWORK", "S2F"}
                , {"US_UL1741", "S30"}
                , {"US_RULE21", "S31"}
                , {"US_RULE14_HECO", "S32"}
                , {"NRS097", "S33"}
                , {"TUNISIA", "S34"}
                , {"PRC_EAST", "S35"}
                , {"PRC_WEST", "S36"}
                , {"PRC_QUEBEC", "S37"}
                , {"AUSTRIA", "S38"}
                , {"ESTONIA", "S39"}
                , {"NI_G98", "S3A"}
                , {"NI_G99", "S3B"}
                , {"INDIA_KERALA", "S3C"}
        };

        addressList = new ArrayList<>();
        for (int i = 1; i < 100; i++) {
            addressList.add(String.valueOf(i));
        }
        //弹框选择的数据
        items = new String[][]{
                {getString(R.string.意大利), getString(R.string.英语), getString(R.string.德语), getString(R.string.西班牙语), getString(R.string.法语), getString(R.string.匈牙利语), getString(R.string.土耳其语), getString(R.string.波兰语), getString(R.string.葡萄牙语)}
        };
        //设置功能码集合（功能码，寄存器，数据）
        int powerLimitReg = 3000;
        if (deviceType == 0) {
            powerLimitReg = 304;
        }
        funsSet = new int[][][]{
                {{6, 118, -1}, {6, 119, -1}, {6, 120, -1}, {6, 121, -1}},//国家安规
                {{6, 45, -1}, {6, 46, -1}, {6, 47, -1}, {6, 48, -1}, {6, 49, -1}, {6, 50, -1}},//逆变器时间
                {{6, 15, -1}},//LCD语言
                {{6, 30, -1}}// 地址
                , {{6, 533, -1}}//功率采集器
                , {{6, 541, -1}, {6, 542, -1}, {6, 543, -1}}//时间
                , {{6, 122, -1}, {6, 123, -1}, {6, powerLimitReg, -1}}//逆变器地址
        };


        for (String[] countryStrings : modelToal) {
            String modelEn = String.format("%s(%s)", countryStrings[0], countryStrings[1]);
            select1Models.add(modelEn);//选项卡
            select1ModelValues.add(countryStrings[1]);//model值
        }
    }

    public void selectTheValue(int buttonID) {

        new CircleDialog.Builder()
                .setWidth(0.7f)
                .setGravity(Gravity.CENTER)
                .setMaxHeight(0.5f)
                .setTitle(getString(R.string.m225请选择))
                .setNegative(getString(R.string.all_no), null)
                .setItems(selectOnOrOff, (parent, view1, position, id) -> {
                    if (buttonID == R.id.btnSelect3) {
                        mbtnSelect3.setText(selectOnOrOff[position]);
                    } else if (buttonID == R.id.btnSelect32) {
                        mbtnSelect32.setText(selectOnOrOff[position]);
                    } else if (buttonID == R.id.btnSelect33) {
                        mbtnSelect33.setText(selectOnOrOff[position]);
                    }


                    return true;
                })
                .show(getSupportFragmentManager());
    }

    @OnClick({R.id.ivLeft,
            R.id.tv_national_safetyReg, R.id.btn_safetyreg, R.id.tv_inveter_time, R.id.btn_inverter_time,
            R.id.tv_lcd_title, R.id.btn_lcd_setting,
            R.id.tvRight, R.id.tvTitle1, R.id.btnSelect1, R.id.btnSelect2, R.id.btnSelect32, R.id.btnSelect33, R.id.btnSelect3, R.id.btnSelect4,
            R.id.tvTitle2, R.id.tvTitle3, R.id.tvTitle4, R.id.tvTitle5, R.id.btnSelectModel, R.id.btnSelect1Mech, R.id.btnSelect1Type})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivLeft:
                finish();
                break;
            case R.id.btnSelect1Type:
                new CircleDialog.Builder()
                        .setWidth(0.7f)
                        .setGravity(Gravity.CENTER)
                        .setMaxHeight(0.5f)
                        .setTitle(getString(R.string.m225请选择))
                        .setNegative(getString(R.string.all_no), null)
                        .setItems(select1Types, (parent, view1, position, id) -> {
                            mBtnSelect1Type.setText(select1Types[position]);
                            mSelectType = position;
                            return true;
                        })
                        .show(getSupportFragmentManager());
                break;
            case R.id.btnSelect1Mech:
                new CircleDialog.Builder()
                        .setWidth(0.7f)
                        .setGravity(Gravity.CENTER)
                        .setMaxHeight(0.5f)
                        .setTitle(getString(R.string.m225请选择))
                        .setNegative(getString(R.string.all_no), null)
                        .setItems(select1Mechs, (parent, view1, position, id) -> {
                            mBtnSelect1Mech.setText(select1Mechs[position]);
                            mSelectMech = position;
                            return true;
                        })
                        .show(getSupportFragmentManager());
                break;
            case R.id.btnSelectModel:
        /*        if (mSelectType == -1) {
                    toast(R.string.m257请选择设置值);
                    return;
                }
                if (mSelectMech == -1) {
                    toast(R.string.m257请选择设置值);
                    return;
                }*/
                new CircleDialog.Builder()
                        .setWidth(0.7f)
                        .setGravity(Gravity.CENTER)
                        .setMaxHeight(0.5f)
                        .setTitle(getString(R.string.m225请选择))
                        .setNegative(getString(R.string.all_no), null)
                        .setItems(select1Models, (parent, view1, position, id) -> {
                            mBtnSelectModel.setText(select1Models.get(position));
                            mSelectModel = select1ModelValues.get(position);
                            return true;
                        })
                        .show(getSupportFragmentManager());
                break;
            case R.id.tvRight:

                initString();


                //判断是否有选择值
                //设置值
                if (cbNationalSageReg.isChecked()) {
                    setNew();
                }
                if (cbInvetTime.isChecked()) {
                    setTime();
                }
                if (cbLcd.isChecked()) {
                    funsSet[2][0][2] = language;
                }

                if (mCheckBox1.isChecked()) {
                    String Value1 = mBtnSelect1.getText().toString();
                    if (comList.contains(Value1)) {
                        funsSet[3][0][2] = Integer.parseInt(Value1);
                    }
                }
                if (mCheckBox2.isChecked()) {
                    String Value2 = mBtnSelect2.getText().toString();
                    if (meterList.contains(Value2)) {
                        int valueNum = meterList.indexOf(Value2);
                        if (valueNum == 2) {
                            valueNum = valueNum + 1;
                        }
                        funsSet[4][0][2] = valueNum;
                    }
                }

                if (mCheckBox3.isChecked()) {
                    String Value31 = mbtnSelect3.getText().toString();
                    String Value32 = mbtnSelect32.getText().toString();
                    String Value33 = mbtnSelect33.getText().toString();
                    if (Arrays.asList(selectOnOrOff).contains(Value31)) {
                        int valueNum = Arrays.asList(selectOnOrOff).indexOf(Value31);
                        if (valueNum == 0) {
                            valueNum = 165;
                        } else if (valueNum == 0) {
                            valueNum = 160;
                        }
                        funsSet[5][0][2] = valueNum;
                    }

                    if (Arrays.asList(selectOnOrOff).contains(Value32)) {
                        int valueNum = Arrays.asList(selectOnOrOff).indexOf(Value32);
                        funsSet[5][1][2] = valueNum;
                    }
                    if (Arrays.asList(selectOnOrOff).contains(Value33)) {
                        int valueNum = Arrays.asList(selectOnOrOff).indexOf(Value33);
                        funsSet[5][2][2] = valueNum;
                    }
                }

                if (mCheckBox4.isChecked()) {
                    String Value41 = mBtnSelect4.getText().toString();
                    String Value42 = mbtnSelect42.getText().toString();
                    String Value43 = mbtnSelect43.getText().toString();
                    if (antiList.contains(Value41)) {
                        int valueNum = antiList.indexOf(Value41);
                        if (valueNum == 2) {
                            valueNum = valueNum + 1;
                        }
                        funsSet[6][0][2] = valueNum;
                    }
                    if (Value42.length() > 0) {
                        funsSet[6][1][2] = Integer.parseInt(Value42);
                    }
                    if (Value43.length() > 0) {
                        funsSet[6][2][2] = Integer.parseInt(Value43);
                    }
                }

                //判断是否有选择值

                if (cbNationalSageReg.isChecked() && TextUtils.isEmpty(mSelectModel)) {
                    toast(R.string.m257请选择设置值);
                    return;
                }
                if (cbInvetTime.isChecked() && TextUtils.isEmpty(time)) {
                    toast(R.string.m257请选择设置值);
                    return;
                }

                if (cbLcd.isChecked() && language == -1) {
                    toast(R.string.m257请选择设置值);
                    return;
                }


                if (mCheckBox1.isChecked() && (funsSet[0][0][2] == -1)) {
                    toast(R.string.m257请选择设置值);
                    return;
                }
                if (mCheckBox2.isChecked() && (funsSet[1][0][2] == -1)) {
                    toast(R.string.m257请选择设置值);
                    return;
                }
                if (mCheckBox3.isChecked()) {
                    if ((funsSet[2][0][2] == -1) && (funsSet[2][1][2] == -1) && (funsSet[2][2][2] == -1)) {
                        toast(R.string.m257请选择设置值);
                        return;
                    }
                }
                if (mCheckBox4.isChecked() && (funsSet[3][0][2] == -1) && (funsSet[3][1][2] == -1) && (funsSet[3][2][2] == -1)) {
                    toast(R.string.m257请选择设置值);
                    return;
                }


                if ((!cbNationalSageReg.isChecked() && !cbInvetTime.isChecked() && !cbLcd.isChecked()
                        && !mCheckBox1.isChecked())
                        && (!mCheckBox2.isChecked())
                        && (!mCheckBox3.isChecked())
                        && (!mCheckBox4.isChecked())
                        && (!mCheckBox5.isChecked())
                ) {
                    return;
                }
                //设置值

                nowSetPos = -1;
                writeValue(true);


                break;
            case R.id.btnSelect1:    //地址


                new CircleDialog.Builder()
                        .setWidth(0.7f)
                        .setGravity(Gravity.CENTER)
                        .setMaxHeight(0.5f)
                        .setTitle(getString(R.string.m225请选择))
                        .setNegative(getString(R.string.all_no), null)
                        .setItems(comList, (parent, view1, position, id) -> {
                            mBtnSelect1.setText(comList.get(position));
                            return true;
                        })
                        .show(getSupportFragmentManager());

                break;
            case R.id.btnSelect2://功率采集器


                new CircleDialog.Builder()
                        .setWidth(0.7f)
                        .setGravity(Gravity.CENTER)
                        .setMaxHeight(0.5f)
                        .setTitle(getString(R.string.m225请选择))
                        .setNegative(getString(R.string.all_no), null)
                        .setItems(meterList, (parent, view1, position, id) -> {
                            mBtnSelect2.setText(meterList.get(position));
                            return true;
                        })
                        .show(getSupportFragmentManager());


                break;
            case R.id.btnSelect3://AFCI 1
                selectTheValue(R.id.btnSelect3);
                break;
            case R.id.btnSelect32://AFCI 2
                selectTheValue(R.id.btnSelect32);
                break;
            case R.id.btnSelect33://AFCI 3
                selectTheValue(R.id.btnSelect33);
                break;
            case R.id.btnSelect4:

                new CircleDialog.Builder()
                        .setWidth(0.7f)
                        .setGravity(Gravity.CENTER)
                        .setMaxHeight(0.5f)
                        .setTitle(getString(R.string.m225请选择))
                        .setNegative(getString(R.string.all_no), null)
                        .setItems(antiList, (parent, view1, position, id) -> {
                            mBtnSelect4.setText(antiList.get(position));
                            return true;
                        })
                        .show(getSupportFragmentManager());
                break;
            case R.id.tvTitle1:
                setCheckShow(mCheckBox1, mBtnSelect1);
                break;
            case R.id.tvTitle2:
                setCheckShow(mCheckBox2, mBtnSelect2);
                break;
            case R.id.tvTitle3:

                setCheckShow2(mCheckBox3, mbtn3TextField1, mbtnSelect3, mbtn3TextField2, mbtnSelect32, mbtn3TextField13, mbtnSelect33);
                break;
            case R.id.tvTitle4:


                setCheckShow2(mCheckBox4, mbtn4TextField1, mBtnSelect4, mbtn4TextField2, mbtnSelect42, mbtn4TextField13, mbtnSelect43);

                break;
            case R.id.tvTitle5:
                if (!mCheckBox5.isChecked()) {
                    new CircleDialog.Builder()
                            .setWidth(0.7f)
                            .setGravity(Gravity.CENTER)
                            .setTitle(getString(R.string.reminder))
                            .setText(getString(R.string.请确认是否为意大利机型))
                            .setNegative(getString(R.string.all_no), null)
                            .setPositive(getString(R.string.all_ok), v -> mCheckBox5.setChecked(true))
                            .show(getSupportFragmentManager());
                } else {
                    mCheckBox5.setChecked(false);
                }
                break;

            case R.id.tv_national_safetyReg:
                setCheckShow(cbNationalSageReg, btnSafeTyReg);
                break;
            case R.id.btn_safetyreg:
                if (isFlagModel) {
                    new CircleDialog.Builder()
                            .setWidth(0.7f)
                            .setGravity(Gravity.CENTER)
                            .setMaxHeight(0.5f)
                            .setTitle(getString(R.string.m225请选择))
                            .setNegative(getString(R.string.all_no), null)
                            .setItems(select1Models, (parent, view1, position, id) -> {
                                btnSafeTyReg.setText(select1Models.get(position));
                                mSelectModel = select1ModelValues.get(position);
                                return true;
                            })
                            .show(getSupportFragmentManager());
                } else {
                    readRegisterValueDTC();
                }
                //先读取
//                new CircleDialog.Builder()
//                        .setWidth(0.7f)
//                        .setGravity(Gravity.CENTER)
//                        .setMaxHeight(0.5f)
//                        .setTitle(getString(R.string.m225请选择))
//                        .setNegative(getString(R.string.all_no),null)
//                        .setItems(models,(parent, view1, position, id) -> {
//                            String country = models.get(position);
//                            mBtnSelect1.setText(country);
//                            out:for (String[][] countryStrings : modelToal[0]) {
//                                for (String[] modles : countryStrings) {
//                                    if (modles[1].equals(country)) {
//                                        model = modles[0];
//                                        break out;
//                                    }
//                                }
//                            }
//                        })
//                        .show(getSupportFragmentManager());
                break;
            case R.id.tv_inveter_time:
                setCheckShow(cbInvetTime, btnInverterTime);
                break;
            case R.id.btn_inverter_time:
                showTimePickView();
                break;

            case R.id.tv_lcd_title:
                setCheckShow(cbLcd, btnLcdSetting);
                break;

            case R.id.btn_lcd_setting:
                new CircleDialog.Builder()
                        .setWidth(0.7f)
                        .setGravity(Gravity.CENTER)
                        .setMaxHeight(0.5f)
                        .setTitle(getString(R.string.m225请选择))
                        .setNegative(getString(R.string.all_no), null)
                        .setItems(items[0], (parent, view1, position, id) -> {
                            btnLcdSetting.setText(items[0][position]);
                            language = position;
                            return true;
                        })
                        .show(getSupportFragmentManager());
                break;
        }
    }

    private void setModelOld() {
        try {
            contents[0] = mSelectModel.substring(1, 2);
            contents[7] = mSelectModel.substring(3, 4);
            StringBuilder sbHigh = new StringBuilder();
            StringBuilder sbLow = new StringBuilder();
            for (int i = 0, len = contents.length; i < len; i++) {
                if (i < 4) {
                    sbHigh.append(contents[i]);
                } else {
                    sbLow.append(contents[i]);
                }
            }
            int high = Integer.parseInt(sbHigh.toString(), 16);
            int low = Integer.parseInt(sbLow.toString(), 16);
            funsSet[0][0][2] = high;
            funsSet[0][1][2] = low;
        } catch (Exception e) {
            e.printStackTrace();
            toast(getString(R.string.m363设置失败));
        }
    }


    private void setNew() {
        if (TextUtils.isEmpty(mSelectModel)) return;
        contents[0] = mSelectModel.substring(1, 3);
        try {
            funsSet[0][0][2] = Integer.parseInt(contents[0] + contents[1], 16);
            funsSet[0][1][2] = Integer.parseInt(contents[2] + contents[3], 16);
            funsSet[0][2][2] = Integer.parseInt(contents[4] + contents[5], 16);
            funsSet[0][3][2] = Integer.parseInt(contents[6] + contents[7], 16);
        } catch (Exception e) {
            e.printStackTrace();
            toast(getString(R.string.m363设置失败));
            funsSet[0][0][2] = -1;
            funsSet[0][1][2] = -1;
            funsSet[0][2][2] = -1;
            funsSet[0][3][2] = -1;
        }
    }


    public void setTime() {
        //设置初始化时间
        int year = mSelectCalendar.get(Calendar.YEAR);
        int month = mSelectCalendar.get(Calendar.MONTH);
        int dayOfMonth = mSelectCalendar.get(Calendar.DAY_OF_MONTH);
        int hourOfDay = mSelectCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = mSelectCalendar.get(Calendar.MINUTE);
        int nowSecond = mSelectCalendar.get(Calendar.SECOND);
        if (year > 2000) {
            funsSet[1][0][2] = year - 2000;
        } else {
            funsSet[1][0][2] = year;
        }
        funsSet[1][1][2] = month + 1;
        funsSet[1][2][2] = dayOfMonth;
        funsSet[1][3][2] = hourOfDay;
        funsSet[1][4][2] = minute;
        funsSet[1][5][2] = nowSecond;
    }

    private void parseNewModle(byte[] bs) {
        //解析int值
        BigInteger big = new BigInteger(1, bs);
        long bigInteger = big.longValue();
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
//        String as = String.format("S%s", contents[0]);
//        for (int i = 0; i < modelToal.length; i++) {
//            String[] model = modelToal[i];
//            if (model[0].equals(as)) {
//                nowModel = model;
//                //更新ui
////                mBtnSelectModel.setText(String.format("%s(%s)", nowModel[0], nowModel[1]));
//                break ;
//            }
//        }
    }

    private void parseOldModleCenter(byte[] bs) {
        //解析int值
        int value0 = MaxWifiParseUtil.obtainValueHAndL(bs);
        //设置值
        contents[1] = MaxUtil.getDeviceModelSingle(value0, 7);
        contents[2] = MaxUtil.getDeviceModelSingle(value0, 6);
        contents[3] = MaxUtil.getDeviceModelSingle(value0, 5);
        contents[4] = MaxUtil.getDeviceModelSingle(value0, 4);
        contents[5] = MaxUtil.getDeviceModelSingle(value0, 3);
        contents[6] = MaxUtil.getDeviceModelSingle(value0, 2);
    }

    public void setCheckShow2(CheckBox checkBox, View... views) {
        if (checkBox.isChecked()) {
            checkBox.setChecked(false);

            CommenUtils.hideAllView(View.GONE, views);

        } else {
            checkBox.setChecked(true);
            CommenUtils.showAllView(views);


        }
    }


    public void setCheckShow(CheckBox checkBox, View view) {
        if (checkBox.isChecked()) {
            checkBox.setChecked(false);
            CommenUtils.hideAllView(View.GONE, view);
            if (checkBox == cbNationalSageReg) {
                mSelectModel = "";
                CommenUtils.hideAllView(View.GONE, mBtnSelectModel, mBtnSelect1Type, mBtnSelect1Mech);
            }
        } else {
            checkBox.setChecked(true);
            CommenUtils.showAllView(view);


        }
    }

    //读取寄存器的值
    private void writeRegisterValue() {
        Mydialog.Show(mContext);
        mClientUtilWriter = SocketClientUtil.connectServer(mHandlerWrite);
    }

    public void writeValue(boolean isFirst) {
        Mydialog.Show(this);
        if (cbNationalSageReg.isChecked() && nowSetPos < 0) {
            nowSetPos = 0;
            if (isFirst) {
                writeRegisterValue();
            } else {
                //继续发送设置命令
                mHandlerWrite.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
            }
        } else if (cbInvetTime.isChecked() && nowSetPos < 1) {
            nowSetPos = 1;
            if (isFirst) {
                writeRegisterValue();
            } else {
                //继续发送设置命令
                mHandlerWrite.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
            }
        } else if (cbLcd.isChecked() && nowSetPos < 2) {
            nowSetPos = 2;
            if (isFirst) {
                writeRegisterValue();
            } else {
                //继续发送设置命令
                mHandlerWrite.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
            }
        } else if (mCheckBox1.isChecked() && nowSetPos < 3) {
            nowSetPos = 3;
            if (isFirst) {
                writeRegisterValue();
            } else {
                //继续发送设置命令
                mHandlerWrite.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
            }
        } else if (mCheckBox2.isChecked() && nowSetPos < 4) {
            nowSetPos = 4;
            if (isFirst) {
                writeRegisterValue();
            } else {
                //继续发送设置命令
                mHandlerWrite.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
            }
        } else if (mCheckBox3.isChecked() && nowSetPos < 5) {
            nowSetPos = 5;
            if (isFirst) {
                writeRegisterValue();
            } else {
                //继续发送设置命令
                mHandlerWrite.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
            }
        } else if (mCheckBox4.isChecked() && nowSetPos < 6) {
            nowSetPos = 6;
            if (isFirst) {
                writeRegisterValue();
            } else {
                //继续发送设置命令
                mHandlerWrite.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
            }
        } else if (mCheckBox5.isChecked()) {
            //跳转到自动测试
            nowSetPos = -1;
            jumpTo(TLXHAutoTestOldInvActivity.class, false);
            Mydialog.Dismiss();
        } else {
            nowSetPos = -1;
            BtnDelayUtil.receiveMessage(mHandlerWrite);
            SocketClientUtil.close(mClientUtilWriter);
            Mydialog.Dismiss();
        }
    }

    /**
     * 弹出时间选择器
     */
    public void showTimePickView() {
        try {
            DateUtils.showTotalTime(this, new DateUtils.SeletctTimeListeners() {
                @Override
                public void seleted(String date) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        time = date;
                        Date sDate = sdf.parse(date);
                        mSelectCalendar.setTime(sDate);
                        btnInverterTime.setText(time);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void ymdHms(int year, int month, int day, int hour, int min, int second) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //读取寄存器的值
    private void readRegisterValue() {
        Mydialog.Show(mContext);
        ModbusUtil.setComAddressOldInv();
        mClientUtilRead = SocketClientUtil.connectServer(mHandlerRead);
    }

    //读取寄存器的值
    private void readRegisterValueDTC() {
        Mydialog.Show(this);
        mClientUtilReadDTC = SocketClientUtil.connectServerAuto(mHandlerReadAutoDTC);
        mHandlerReadAutoDTC.sendEmptyMessageDelayed(SocketClientUtil.SOCKET_EXCETION_CLOSE, 3000);
    }

    /**
     * 弹框提示选择进入界面
     */
    private void dialogShow(String title) {
        new CircleDialog.Builder()
                .setTitle(title)
                .setText(getString(R.string.m1257是否需要手动选择) + "?")
                .setGravity(Gravity.CENTER)
                .setWidth(0.8f)
                .setNegative(getString(R.string.all_no), null)
                .setPositive(getString(R.string.all_ok), view -> {
                    CommenUtils.showAllView(mBtnSelectModel);
                    CommenUtils.hideAllView(View.INVISIBLE, btnSafeTyReg);
                })
                .show(getSupportFragmentManager());
    }

}