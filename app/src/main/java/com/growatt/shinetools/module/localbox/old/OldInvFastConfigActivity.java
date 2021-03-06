package com.growatt.shinetools.module.localbox.old;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.growatt.shinetools.R;
import com.growatt.shinetools.base.DemoBase;
import com.growatt.shinetools.bean.ToolModelBean;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OldInvFastConfigActivity extends DemoBase {

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
    @BindView(R.id.btnSelect3)
    Button mBtnSelect3;
    @BindView(R.id.tvTitle4)
    TextView mTvTitle4;
    @BindView(R.id.checkBox4)
    CheckBox mCheckBox4;
    @BindView(R.id.btnSelect4)
    Button mBtnSelect4;
    @BindView(R.id.tvTitle5)
    TextView mTvTitle5;
    @BindView(R.id.checkBox5)
    CheckBox mCheckBox5;
    private String[][][][] modelToal;
    private List<String> models;
    private String model;
    //????????????item
    private String[][] items;
    //?????????????????????????????????????????????????????????
    private int[][][] funsSet;
    //????????????????????????????????????????????????????????????????????????
    private int[][] funs;
    private List<String> addressList;
    //????????????
    private String time;
    private int language = -1;
    private int address = -1;
    private String[] sucNotes;
    //???????????????????????????
    private String[] contents = new String[8];
    private String[] select1Types = {
            "MTL-S/-S", "3-15K TL3-S", "17-25K 30-50K TL3-S"
    };
    private int[] seletTypeDTCs = {
            210, 2049, 2069
    };
    private String[] select1Mechs = {
            "EU Model", "Australia Model", "UK Model"
    };
    private List<String> select1Models = new ArrayList<>();
    private List<String> select1ModelValues = new ArrayList<>();
    private int mSelectType = -1;
    private int mSelectMech = -1;
    private String mSelectModel = "";
    private boolean isFlagModel;//??????????????????DTC model

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_inv_fast_config);
        ButterKnife.bind(this);
        ModbusUtil.setComAddressOldInv();
        initString();
    }

    private void initString() {
        mTvTitle.setText(R.string.????????????);
        mTvRight.setText(R.string.m??????);
        sucNotes = new String[]{
                getString(R.string.m????????????)
                , getString(R.string.mlocal???????????????)
                , getString(R.string.mLCD??????)
                , getString(R.string.m423????????????)
        };
        funs = new int[][]{
                //????????????
                {3, 0, 40}
        };
//        modelToal = new String[][][][]{
//                {{{"A0S1", "VDE0126"}, {"A0S4", "Italy"}, {"A0S7", "Germany"}, {"A0SB", "EN50438_Standard"}, {"A0SD", "Belgium"}, {"A1S3", "EN50438_Denmark"}, {"A1S4", "EN50438_Sweden"}, {"A1S5", "EN50438_Norway"}, {"A1S7", "France"}, {"A1SA", "CEI0-16"}, {"A1SB", "DRRG"}, {"A1SC", "Chile"}, {"A1SD", "Argentina"}, {"A1SE", "BDEW"}, {"A0S6", "Greece"}, {"A2S0", "TR3.2.1 Denmark"}},
//                        {{"A0S2", "UK_G59"}, {"A0S8", "UK_G83"}, {"A0S9", "EN50438_Ireland"}},
//                        {{"A0S3", "AS4777_Australia"}, {"A1S0", "AS4777_Newzealand"}},
//                        {{"A0SE", "MEA"}, {"A0SF", "PEA"}}}
//                ,{{{"S01", "VDE0126"}, {"S04", "Italy"}, {"S07", "Germany"}, {"S0B", "EN50438_tandard"}, {"S0D", "Belgium"}, {"S13", "EN50438_Denmark"}, {"S14", "EN50438_Sweden"}, {"S15", "EN50438_Norway"}, {"S17", "France"}, {"S1A", "CEI0-16"}, {"S1B", "DRRG"}, {"S1C", "Chile"}, {"S1D", "Argentina"}, {"S1E", "BDEW"}, {"S06", "Greece"}, {"S20", "TR3.2.1 Denmark"}},
//                {{"S02", "UK_G59"}, {"S08", "UK_G83"}, {"S09", "EN50438_Ireland"}},
//                {{"S03", "AS4777_Australia"}, {"S10", "SS4777_Newzealand"}},
//                {{"S0E", "MEA"}, {"S0F", "PEA"}}}
//        };
        modelToal = new String[][][][]{
                {{
                        {"A0S1", "VDE0126"}, {"A0S1", "Spain"}, {"A0S4", "Italy"}
                        , {"A0S7", "Germany"}, {"A0S7", "Austria"}, {"A0S7", "Switzerland"}
                        , {"A0SB", "EN50438"}, {"A0SB", "Poland"}
                        , {"A0SD", "Belgium"}, {"A1S4", "Sweden"}
                        , {"A1S5", "EN50438_Norway"}, {"A0S6", "Greece"}
                        , {"A1S7", "France"}, {"A1SA", "CEI0-16"}
                        , {"A2S0", "Denmark???DK1)"}, {"A2S0", "Denmark???DK2)"}, {"A1SE", "BDEW"}
                        , {"A1SB", "DRRG"}, {"A1SC", "Chile"}
                        , {"A1SD", "Argentina"}
                },
                        {{"A0S2", "UK_G59"}, {"A0S8", "UK_G83"}, {"A0S9", "EN50438_Ireland"}},
                        {{"A0S3", "AS4777_Australia"}, {"A1S0", "AS4777_Newzealand"}},
                        {{"A0SE", "MEA"}, {"A0SF", "PEA"}},
                        {{"A0S4", "Italy"}},
                        {{"A0SC", "Hungary"}},
//                        {{"A0SA", "??????-CGC"}},
//                        {{"A1S1", "??????-CQC"}},
                        {{"A1S2", "India"}},
                        {{"A1S8", "Korea"}},
                        {{"A1S9", "Brazil"}},
                }
                ,
                {{
                        {"S01", "VDE0126", "VDE0126;Spain"}, {"S04", "Italy"}
                        , {"S07", "N4105", "Germany;Austria;Switzerland"}, {"S0B", "EN50438", "EN50438;Poland"}
                        , {"S0D", "Belgium"}, {"S14", "Sweden"}
                        , {"S15", "EN50438_Norway"}, {"S06", "Greece"}
                        , {"S17", "France"}, {"S1A", "CEI0-16"}
                        , {"S20", "Denmark", "Denmark???DK1);Denmark???DK2???"}, {"S1E", "BDEW"}
                        , {"S1B", "DRRG"}, {"S1C", "Chile"}
                        , {"S1D", "Argentina"}
                },
                        {{"S02", "UK_G59"}, {"S08", "UK_G83"}, {"S09", "EN50438_Ireland"}},
                        {{"S03", "AS4777_Australia"}, {"S10", "AS4777_Newzealand"}},
                        {{"S0E", "MEA"}, {"S0F", "PEA"}},
                        {{"S04", "Italy"}},
                        {{"S0C", "Hungary"}},
//                        {{"S0A", "??????"}},
//                        {{"S11", "??????"}},
                        {{"S12", "India"}},
                        {{"S18", "Korea"}},
                        {{"S19", "Brazil"}},
                }
        };
        addressList = new ArrayList<>();
        for (int i = 1; i < 100; i++) {
            addressList.add(String.valueOf(i));
        }
        //?????????????????????
        items = new String[][]{
                {getString(R.string.?????????), getString(R.string.??????), getString(R.string.??????), getString(R.string.????????????), getString(R.string.??????), getString(R.string.????????????), getString(R.string.????????????), getString(R.string.?????????), getString(R.string.????????????)}
        };
        //?????????????????????????????????????????????????????????
        funsSet = new int[][][]{
                {{6, 28, -1}, {6, 29, -1}}//??????model
                , {{6, 45, -1}, {6, 46, -1}, {6, 47, -1}, {6, 48, -1}, {6, 49, -1}, {6, 50, -1}}//??????
                , {{6, 15, -1}}//??????
                , {{6, 30, -1}}//???????????????
        };

        models = new ArrayList<>();
        for (String[][] countryStrings : modelToal[0]) {
            for (String[] modle : countryStrings) {
                models.add(modle[1]);
            }
        }
    }

    @OnClick({R.id.ivLeft, R.id.tvRight, R.id.btnSelect1, R.id.btnSelect2, R.id.btnSelect3, R.id.btnSelect4, R.id.tvTitle1, R.id.tvTitle2, R.id.tvTitle3, R.id.tvTitle4, R.id.tvTitle5, R.id.btnSelectModel, R.id.btnSelect1Mech, R.id.btnSelect1Type})
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
                        .setTitle(getString(R.string.m225?????????))
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
                        .setTitle(getString(R.string.m225?????????))
                        .setNegative(getString(R.string.all_no), null)
                        .setItems(select1Mechs, (parent, view1, position, id) -> {
                            mBtnSelect1Mech.setText(select1Mechs[position]);
                            mSelectMech = position;
                            return true;
                        })
                        .show(getSupportFragmentManager());
                break;
            case R.id.btnSelectModel:
                if (mSelectType == -1) {
                    toast(R.string.m257??????????????????);
                    return;
                }
                if (mSelectMech == -1) {
                    toast(R.string.m257??????????????????);
                    return;
                }
                LinkedHashMap<String, String> modelMap = ToolModelBean.getOldInvModelMap().get(mSelectType).get(mSelectMech);
                if (modelMap == null) return;
                select1ModelValues.clear();
                select1Models.clear();
                for (Map.Entry<String, String> entry : modelMap.entrySet()) {
                    select1ModelValues.add(entry.getValue());
                    select1Models.add(String.format("%s(%s)", entry.getKey(), entry.getValue()));
                }
                new CircleDialog.Builder()
                        .setWidth(0.7f)
                        .setGravity(Gravity.CENTER)
                        .setMaxHeight(0.5f)
                        .setTitle(getString(R.string.m225?????????))
                        .setNegative(getString(R.string.all_no), null)
                        .setItems(select1Models, (parent, view1, position, id) -> {
                            mBtnSelectModel.setText(select1Models.get(position));
                            mSelectModel = select1ModelValues.get(position);
                            return true;
                        })
                        .show(getSupportFragmentManager());
                break;
            case R.id.tvRight:
                //????????????????????????
                if (mCheckBox1.isChecked() && TextUtils.isEmpty(mSelectModel)) {
                    toast(R.string.m257??????????????????);
                    return;
                }
                if (mCheckBox2.isChecked() && TextUtils.isEmpty(time)) {
                    toast(R.string.m257??????????????????);
                    return;
                }
                if (mCheckBox3.isChecked() && language == -1) {
                    toast(R.string.m257??????????????????);
                    return;
                }
                if (mCheckBox4.isChecked() && address == -1) {
                    toast(R.string.m257??????????????????);
                    return;
                }
                if ((!mCheckBox1.isChecked())
                        && (!mCheckBox2.isChecked())
                        && (!mCheckBox3.isChecked())
                        && (!mCheckBox4.isChecked())
                        && (!mCheckBox5.isChecked())
                ) {
                    return;
                }
                //?????????
                if (mCheckBox1.isChecked()) {
                    setModelOld();
                }
                if (mCheckBox2.isChecked()) {
                    setTime();
                }
                if (mCheckBox3.isChecked()) {
                    funsSet[2][0][2] = language;
                }
                if (mCheckBox4.isChecked()) {
                    funsSet[3][0][2] = address;
                }
//                writeValue();
                //??????????????????
                readRegisterValue();
                break;
            case R.id.btnSelect1:
                if (isFlagModel) {
                    new CircleDialog.Builder()
                            .setWidth(0.7f)
                            .setGravity(Gravity.CENTER)
                            .setMaxHeight(0.5f)
                            .setTitle(getString(R.string.m225?????????))
                            .setNegative(getString(R.string.all_no), null)
                            .setItems(select1Models, (parent, view1, position, id) -> {
                                mBtnSelect1.setText(select1Models.get(position));
                                mSelectModel = select1ModelValues.get(position);
                                return true;
                            })
                            .show(getSupportFragmentManager());
                } else {
                    readRegisterValueDTC();
                }
                //?????????
//                new CircleDialog.Builder()
//                        .setWidth(0.7f)
//                        .setGravity(Gravity.CENTER)
//                        .setMaxHeight(0.5f)
//                        .setTitle(getString(R.string.m225?????????))
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
            case R.id.btnSelect2://??????
                showTimePickView();
                break;
            case R.id.btnSelect3://??????
                new CircleDialog.Builder()
                        .setWidth(0.7f)
                        .setGravity(Gravity.CENTER)
                        .setMaxHeight(0.5f)
                        .setTitle(getString(R.string.m225?????????))
                        .setNegative(getString(R.string.all_no), null)
                        .setItems(items[0], (parent, view1, position, id) -> {
                            mBtnSelect3.setText(items[0][position]);
                            language = position;
                            return true;
                        })
                        .show(getSupportFragmentManager());
                break;
            case R.id.btnSelect4:
                new CircleDialog.Builder()
                        .setWidth(0.7f)
                        .setGravity(Gravity.CENTER)
                        .setMaxHeight(0.5f)
                        .setTitle(getString(R.string.m225?????????))
                        .setNegative(getString(R.string.all_no), null)
                        .setItems(addressList, (parent, view1, position, id) -> {
                            mBtnSelect4.setText(addressList.get(position));
                            address = position + 1;
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
                setCheckShow(mCheckBox3, mBtnSelect3);
                break;
            case R.id.tvTitle4:
                setCheckShow(mCheckBox4, mBtnSelect4);
                break;
            case R.id.tvTitle5:
                if (!mCheckBox5.isChecked()) {
                    new CircleDialog.Builder()
                            .setWidth(0.7f)
                            .setGravity(Gravity.CENTER)
                            .setTitle(getString(R.string.reminder))
                            .setText(getString(R.string.?????????????????????????????????))
                            .setNegative(getString(R.string.all_no), null)
                            .setPositive(getString(R.string.all_ok), v -> mCheckBox5.setChecked(true))
                            .show(getSupportFragmentManager());
                } else {
                    mCheckBox5.setChecked(false);
                }
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
            toast(getString(R.string.m363????????????));
        }
    }

    Calendar mSelectCalendar = Calendar.getInstance();

    public void setTime() {
        //?????????????????????
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

    private void parseOldModle(byte[] bs) {
        //??????int???
        int value0 = MaxWifiParseUtil.obtainValueHAndL(bs);
        //?????????
        contents[0] = MaxUtil.getDeviceModelSingle(value0, 8);
        contents[1] = MaxUtil.getDeviceModelSingle(value0, 7);
        contents[2] = MaxUtil.getDeviceModelSingle(value0, 6);
        contents[3] = MaxUtil.getDeviceModelSingle(value0, 5);
        contents[4] = MaxUtil.getDeviceModelSingle(value0, 4);
        contents[5] = MaxUtil.getDeviceModelSingle(value0, 3);
        contents[6] = MaxUtil.getDeviceModelSingle(value0, 2);
        contents[7] = MaxUtil.getDeviceModelSingle(value0, 1);
    }

    private void parseOldModleCenter(byte[] bs) {
        //??????int???
        int value0 = MaxWifiParseUtil.obtainValueHAndL(bs);
        //?????????
        contents[1] = MaxUtil.getDeviceModelSingle(value0, 7);
        contents[2] = MaxUtil.getDeviceModelSingle(value0, 6);
        contents[3] = MaxUtil.getDeviceModelSingle(value0, 5);
        contents[4] = MaxUtil.getDeviceModelSingle(value0, 4);
        contents[5] = MaxUtil.getDeviceModelSingle(value0, 3);
        contents[6] = MaxUtil.getDeviceModelSingle(value0, 2);
    }

    public void setCheckShow(CheckBox checkBox, View view) {
        if (checkBox.isChecked()) {
            checkBox.setChecked(false);
            CommenUtils.hideAllView(View.GONE, view);
            if (checkBox == mCheckBox1) {
                mSelectModel = "";
                CommenUtils.hideAllView(View.GONE, mBtnSelectModel, mBtnSelect1Type, mBtnSelect1Mech);
            }
        } else {
            checkBox.setChecked(true);
            CommenUtils.showAllView(view);


        }
    }

    //????????????:??????????????????
    private SocketClientUtil mClientUtilWriter;

    //?????????????????????
    private void writeRegisterValue() {
        Mydialog.Show(mContext);
        mClientUtilWriter = SocketClientUtil.connectServer(mHandlerWrite);
    }

    private int nowSetPos = -1;

    public void writeValue(boolean isFirst) {
        Mydialog.Show(this);
        if (mCheckBox1.isChecked() && nowSetPos < 0) {
            nowSetPos = 0;
            if (isFirst) {
                writeRegisterValue();
            } else {
                //????????????????????????
                mHandlerWrite.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
            }
        } else if (mCheckBox2.isChecked() && nowSetPos < 1) {
            nowSetPos = 1;
            if (isFirst) {
                writeRegisterValue();
            } else {
                //????????????????????????
                mHandlerWrite.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
            }
        } else if (mCheckBox3.isChecked() && nowSetPos < 2) {
            nowSetPos = 2;
            if (isFirst) {
                writeRegisterValue();
            } else {
                //????????????????????????
                mHandlerWrite.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
            }
        } else if (mCheckBox4.isChecked() && nowSetPos < 3) {
            nowSetPos = 3;
            if (isFirst) {
                writeRegisterValue();
            } else {
                //????????????????????????
                mHandlerWrite.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
            }
        } else if (mCheckBox5.isChecked()) {
            //?????????????????????
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
     * ?????????????????????
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
                        mBtnSelect2.setText(time);
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

    /**
     * ????????????handle
     */
    //???????????????????????????
    private byte[] sendBytes;
    private int mPos = 0;
    Handler mHandlerWrite = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //????????????
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
                                    sendBytes = SocketClientUtil.sendMsgToServerOldInv(mClientUtilWriter, nowSet[i]);
                                    LogUtil.i("????????????" + (i + 1) + ":" + SocketClientUtil.bytesToHexString(sendBytes));
                                    //????????????????????????-1
                                    nowSet[i][2] = -1;
                                    isNowSet = true;
                                    break;
                                }
                            }
                            if (!isNowSet) {
                                writeValue(false);
                            }
                        } else {
                            //??????tcp??????
                            SocketClientUtil.close(mClientUtilWriter);
                            BtnDelayUtil.refreshFinish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        //??????tcp??????
                        SocketClientUtil.close(mClientUtilWriter);
                        BtnDelayUtil.refreshFinish();
                    }
                    break;
                //??????????????????
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //?????????????????????
                        boolean isCheck = MaxUtil.checkReceiverFull(bytes);
                        if (isCheck) {
                            String success = "";
                            if (mPos == funsSet[nowSetPos].length - 1) {
                                success = sucNotes[nowSetPos] + " " + getString(R.string.all_success);
                            }
                            if (!TextUtils.isEmpty(success)) {
                                toast(success);
                            }
                            //????????????????????????
                            mHandlerWrite.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        } else {
                            toast(getString(R.string.all_failed));
                            //??????tcp??????
                            SocketClientUtil.close(mClientUtilWriter);
                            BtnDelayUtil.refreshFinish();
                        }
                        LogUtil.i("????????????" + ":" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        //??????tcp??????
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
    //????????????:??????????????????
    private SocketClientUtil mClientUtilRead;

    //?????????????????????
    private void readRegisterValue() {
        Mydialog.Show(mContext);
        ModbusUtil.setComAddressOldInv();
        mClientUtilRead = SocketClientUtil.connectServer(mHandlerRead);
    }

    /**
     * ???????????????handle
     */
    Handler mHandlerRead = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //????????????
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessage(this);
                    byte[] sendBytesR = SocketClientUtil.sendMsgToServerOldInv(mClientUtilRead, funs[0]);
                    LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(sendBytesR));
                    break;
                //??????????????????
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //?????????????????????
                        boolean isCheck = ModbusUtil.checkModbus(bytes);
                        if (isCheck) {
                            //??????????????????
                            byte[] bs = RegisterParseUtil.removePro17(bytes);
                            //????????????????????????com????????????model
                            int comAddress = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 30, 0, 1));
                            ModbusUtil.setComAddressOldInv(comAddress);
                            //??????modle?????????
                            parseOldModleCenter(MaxWifiParseUtil.subBytes125(bs, 28, 0, 2));
                            //??????tcp??????
                            SocketClientUtil.close(mClientUtilRead);
                            BtnDelayUtil.refreshFinish();
                            //?????????
                            writeValue(true);
                        } else {
                            toast(R.string.all_failed);
                            //??????tcp??????
                            SocketClientUtil.close(mClientUtilRead);
                            BtnDelayUtil.refreshFinish();
                        }
                        LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        //??????tcp??????
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
    //????????????:??????????????????
    private SocketClientUtil mClientUtilReadDTC;

    //?????????????????????
    private void readRegisterValueDTC() {
        Mydialog.Show(this);
        mClientUtilReadDTC = SocketClientUtil.connectServerAuto(mHandlerReadAutoDTC);
        mHandlerReadAutoDTC.sendEmptyMessageDelayed(SocketClientUtil.SOCKET_EXCETION_CLOSE, 3000);
    }

    /**
     * ???????????????handle
     */
    private int[][] autoFunDTC = {{3, 0, 44}};
    private int autoCountDTC = 0;
    Handler mHandlerReadAutoDTC = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //????????????
                case SocketClientUtil.SOCKET_SEND:
                    if (autoCountDTC < autoFunDTC.length) {
                        byte[] sendBytesR = SocketClientUtil.sendMsgToServer(mClientUtilReadDTC, autoFunDTC[autoCountDTC]);
                        LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(sendBytesR));
                    }
                    break;
                //??????????????????
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    this.removeMessages(SocketClientUtil.SOCKET_EXCETION_CLOSE);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //?????????????????????
                        boolean isCheck = ModbusUtil.checkModbus(bytes);
                        if (isCheck) {
                            byte[] bs = RegisterParseUtil.removePro17(bytes);
                            //??????mo'de'l
                            byte[] valueBs = MaxWifiParseUtil.subBytesFull(bs, 28, 0, 2);
                            parseOldModle(valueBs);
                            //??????DTC
                            int dtc = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 43, 0, 1));
                            //???????????????DTC???
                            boolean isFlag = false;
                            int typePos = -1;
                            for (int i = 0; i < seletTypeDTCs.length; i++) {
                                if (dtc == seletTypeDTCs[i]) {
                                    typePos = i;
                                    isFlag = true;
                                    break;
                                }
                            }
                            if (!isFlag) {
                                mHandlerReadAutoDTC.sendEmptyMessage(SocketClientUtil.SOCKET_EXCETION_CLOSE);
                            } else {
                                //??????????????????model
                                LinkedHashMap<Integer, LinkedHashMap<String, String>> typeMap = ToolModelBean.getOldInvModelMap().get(typePos);
                                //??????model
                                String nowModel = String.format("A%sS%s", contents[0], contents[7]);
                                isFlagModel = false;//??????????????????model
                                int mechType = -1;
                                out:
                                for (Map.Entry<Integer, LinkedHashMap<String, String>> mechEntry : typeMap.entrySet()) {
                                    LinkedHashMap<String, String> mechMap = mechEntry.getValue();
                                    mechType = mechEntry.getKey();
                                    for (Map.Entry<String, String> entry : mechMap.entrySet()) {
                                        if (nowModel.equals(entry.getValue())) {
                                            isFlagModel = true;
                                            break out;
                                        }
                                    }
                                }
                                if (isFlagModel) {//????????????model????????????
                                    mBtnSelect1.setText(nowModel);
                                    select1ModelValues.clear();
                                    select1Models.clear();
                                    for (Map.Entry<String, String> entry : typeMap.get(mechType).entrySet()) {
                                        String modelEn = String.format("%s(%s)", entry.getKey(), entry.getValue());
                                        if (nowModel.equals(entry.getValue())) {
                                            mBtnSelect1.setText(modelEn);
                                        }
                                        select1ModelValues.add(entry.getValue());
                                        select1Models.add(modelEn);
                                    }
                                    new CircleDialog.Builder()
                                            .setWidth(0.7f)
                                            .setGravity(Gravity.CENTER)
                                            .setMaxHeight(0.5f)
                                            .setTitle(getString(R.string.m225?????????))
                                            .setNegative(getString(R.string.all_no), null)
                                            .setItems(select1Models, (parent, view1, position, id) -> {
                                                mBtnSelect1.setText(select1Models.get(position));
                                                mSelectModel = select1ModelValues.get(position);
                                                return true;
                                            })
                                            .show(getSupportFragmentManager());
                                } else {//??????????????????????????????model
                                    mSelectModel = nowModel;
                                    mBtnSelect1.setText(nowModel);
                                    //????????????????????????
                                    dialogShow(getString(R.string.m1259????????????????????????));
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
                        LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        autoCountDTC = 0;
                    } finally {
                        //??????tcp??????
                        SocketClientUtil.close(mClientUtilReadDTC);
                        Mydialog.Dismiss();
                        autoCountDTC = 0;
                    }
                    break;
                case SocketClientUtil.SOCKET_EXCETION_CLOSE:
                    //??????tcp??????
                    SocketClientUtil.close(mClientUtilReadDTC);
                    Mydialog.Dismiss();
                    dialogShow(getString(R.string.reminder));
                    break;
            }
        }
    };

    /**
     * ??????????????????????????????
     */
    private void dialogShow(String title) {
        new CircleDialog.Builder()
                .setTitle(title)
                .setText(getString(R.string.m1257????????????????????????) + "?")
                .setGravity(Gravity.CENTER)
                .setWidth(0.8f)
                .setNegative(getString(R.string.all_no), null)
                .setPositive(getString(R.string.all_ok), view -> {
                    CommenUtils.showAllView(mBtnSelectModel, mBtnSelect1Type, mBtnSelect1Mech);
                    CommenUtils.hideAllView(View.INVISIBLE, mBtnSelect1);
                })
                .show(getSupportFragmentManager());
    }
}
