package com.growatt.shinetools.module.localbox.configtype.usconfig;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.growatt.shinetools.R;
import com.growatt.shinetools.ShineToosApplication;
import com.growatt.shinetools.adapter.UsSettingAdapter;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.bean.USDebugSettingBean;
import com.growatt.shinetools.bean.UsSettingConstant;
import com.growatt.shinetools.modbusbox.Arith;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.MaxWifiParseUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.utils.ActivityUtils;
import com.growatt.shinetools.utils.BtnDelayUtil;
import com.growatt.shinetools.utils.CircleDialogUtils;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.Mydialog;
import com.growatt.shinetools.widget.GridDivider;
import com.mylhyl.circledialog.BaseCircleDialog;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.res.drawable.CircleDrawable;
import com.mylhyl.circledialog.res.values.CircleColor;
import com.mylhyl.circledialog.res.values.CircleDimen;
import com.mylhyl.circledialog.view.listener.OnCreateBodyViewListener;
import com.mylhyl.circledialog.view.listener.OnLvItemClickListener;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;

import static com.growatt.shinetools.constant.GlobalConstant.END_USER;
import static com.growatt.shinetools.constant.GlobalConstant.KEFU_USER;
import static com.growatt.shinetools.constant.GlobalConstant.MAINTEAN_USER;
import static com.growatt.shinetools.modbusbox.SocketClientUtil.SOCKET_RECEIVE_BYTES;
import static com.growatt.shinetools.utils.BtnDelayUtil.TIMEOUT_RECEIVE;

public class USParamsSettingActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener,
        UsSettingAdapter.OnChildCheckLiseners, Toolbar.OnMenuItemClickListener {
    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.header_title)
    LinearLayout headerTitle;
    @BindView(R.id.rv_system)
    RecyclerView rvSystem;


    private UsSettingAdapter usParamsetAdapter;


    //?????????
    private String[] titles;
    private String[] registers;
    private int[] itemTypes;


    //????????????
    private int[][] funs;
    private boolean isReceiveSucc = false;
    private int count = 0;//????????????????????????
    private int[][] nowReadFuns;//?????????????????????
    private int modelPos = -1;//??????model??????

    private int[][] registerValues = {
            {-1, -1, -1, -1}
    };

    private int[] nowRegister;


    //????????????
    //?????????????????????????????????????????????????????????
    private int[][][] nowSet;
    private int[] curSet;


    private float[] mMultiples;//????????????
    private float mMul = 1;//????????????
    private String[] mUnits;
    private String mUnit = "";//????????????


    //????????????????????????
    private List<String> models;
    private String[][] modelToal;


    private int currentSetPos = 0;
    //????????????item
    private String[][] items;
    private BaseCircleDialog dialogFragment;
    private String[] nowItems;

    //?????????
    private int nowSecond;
    private BaseCircleDialog baseCircleDialog;

    private int user_type = KEFU_USER;


    @Override
    protected int getContentView() {
        return R.layout.activity_us_param_setting;
    }

    @Override
    protected void initViews() {

        initToobar(toolbar);
        tvTitle.setText(R.string.android_key622);
        String title = getIntent().getStringExtra("title");
        if (TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }
        toolbar.setOnMenuItemClickListener(this);

        rvSystem.setLayoutManager(new LinearLayoutManager(this));
        usParamsetAdapter = new UsSettingAdapter(new ArrayList<>(), this);
        int div = (int) getResources().getDimension(R.dimen.dp_1);
        GridDivider gridDivider = new GridDivider(ContextCompat.getColor(this, R.color.white), div, div);
        rvSystem.addItemDecoration(gridDivider);
        rvSystem.setAdapter(usParamsetAdapter);
        usParamsetAdapter.setOnItemClickListener(this);
    }

    @Override
    protected void initData() {
        //1.??????????????????
        user_type = ShineToosApplication.getContext().getUser_type();
        //2.??????????????????????????????????????????????????????
        if (user_type == END_USER) {
            titles = new String[]{getString(R.string.android_key663)};
            //??????????????????
            registers = new String[]{""};
            //item???????????????
            itemTypes = new int[]{UsSettingConstant.SETTING_TYPE_SELECT,};
            //????????????????????????
            funs = new int[][]{{3, 45, 50}};//???????????????

        } else if (user_type==MAINTEAN_USER){
            //??????6,9
            //??????
            titles = new String[]{
                    getString(R.string.m????????????),
                    getString(R.string.android_key982),
                    getString(R.string.m404?????????????????????),
                    getString(R.string.android_key663),
                    getString(R.string.android_key1005),
                    getString(R.string.android_key947),
//                    getString(R.string.android_key2395),
                    getString(R.string.android_key259),
                    getString(R.string.android_key1415),
//                    getString(R.string.android_key828)
            };

            //??????????????????
            registers = new String[]{
                    "", "", "", "",
                    "", "", "",
                    "", "", ""
            };


            //item???????????????
            itemTypes = new int[]{
                    UsSettingConstant.SETTING_TYPE_SELECT,
                    UsSettingConstant.SETTING_TYPE_INPUT,
                    UsSettingConstant.SETTING_TYPE_SELECT,
                    UsSettingConstant.SETTING_TYPE_SELECT,
                    UsSettingConstant.SETTING_TYPE_ONLYREAD,
                    UsSettingConstant.SETTING_TYPE_SWITCH,
//                    UsSettingConstant.SETTING_TYPE_INPUT,
                    UsSettingConstant.SETTING_TYPE_SELECT,
                    UsSettingConstant.SETTING_TYPE_SELECT,
//                    UsSettingConstant.SETTING_TYPE_NEXT,

            };


            //????????????????????????
            funs = new int[][]{
                    {3, 0, 124},//????????????
                    {3, 30, 30},//????????????
                    {3, 22, 22},//?????????????????????
                    {3, 45, 50},//???????????????
                    {3, 88, 88},//modbus??????
                    {3, 231, 231},//????????????
//                    {4, 3051, 3052},//??????????????????
                    {3, 32, 32},//??????????????????
                    {3, 33, 33},//??????????????????

            };
        }else {
            //??????
            titles = new String[]{
                    getString(R.string.m????????????),
                    getString(R.string.android_key982),
                    getString(R.string.m404?????????????????????),
                    getString(R.string.android_key663),
                    getString(R.string.android_key1005),
                    getString(R.string.android_key947),
                    getString(R.string.android_key2395),
                    getString(R.string.android_key259),
                    getString(R.string.android_key1415),
                    getString(R.string.android_key828)
            };

            //??????????????????
            registers = new String[]{
                    "", "", "", "",
                    "", "", "",
                    "", "", ""
            };

            //item???????????????
            itemTypes = new int[]{
                    UsSettingConstant.SETTING_TYPE_SELECT,
                    UsSettingConstant.SETTING_TYPE_INPUT,
                    UsSettingConstant.SETTING_TYPE_SELECT,
                    UsSettingConstant.SETTING_TYPE_SELECT,
                    UsSettingConstant.SETTING_TYPE_ONLYREAD,
                    UsSettingConstant.SETTING_TYPE_SWITCH,
                    UsSettingConstant.SETTING_TYPE_INPUT,
                    UsSettingConstant.SETTING_TYPE_SELECT,
                    UsSettingConstant.SETTING_TYPE_SELECT,
                    UsSettingConstant.SETTING_TYPE_NEXT,

            };
            //????????????????????????
            funs = new int[][]{
                    {3, 0, 124},//????????????
                    {3, 30, 30},//????????????
                    {3, 22, 22},//?????????????????????
                    {3, 45, 50},//???????????????
                    {3, 88, 88},//modbus??????
                    {3, 231, 231},//????????????
                    {4, 3051, 3052},//??????????????????
                    {3, 32, 32},//??????????????????
                    {3, 33, 33},//??????????????????

            };

        }





        /*??????*/
        nowSet = new int[][][]{
                {{0x10, 118, 121}},//????????????
                {{6, 30, -1}},//????????????
                {{6, 22, 4},{6,22,5}},//?????????????????????
                {{6, 45, -1}, {6, 46, -1}, {6, 47, -1}, {6, 48, -1}, {6, 49, -1}, {6, 50, -1}},//???????????????
                {{6, 88, -1}},//modbus??????
                {{6, 231, -1}},//????????????
                {{6, 7147, -1}, {6, 7148, -1}},//??????????????????
                {{6, 32, -1}},//??????????????????
                {{6, 33, -1}},//??????????????????

        };

        curSet = nowSet[0][0];
        try {
            mMultiples = new float[]{0.1f, 1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f};
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mUnits = new String[]{"", "", "", "", "", "", "kWh", "", "", ""};
        } catch (Exception e) {
            e.printStackTrace();
        }



        //?????????????????????
        items = new String[][]{
                {""},
                {""},
                {"9600bps", "38400bps"},//us????????????,
                {""},//us????????????,
                {""},//us????????????,
                {""},//us????????????,
                {""},//us????????????,
                {getString(R.string.android_key3107), getString(R.string.android_key3108)},//us????????????,
                {getString(R.string.android_key3109), getString(R.string.android_key3110)},//us????????????,
                {""},//us????????????,
        };


   /*     try {
            mMul = mMultiples[mType];
            mUnit = mUnits[mType];
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        modelToal = new String[][]{
                {"S25", "IEEE1547-208", "5", "208V", "25"}
                , {"S25", "IEEE1547-240", "1", "240V", "25"}
                , {"S31", "RULE 21-208", "5", "208V", "31"}
                , {"S31", "RULE 21-240", "1", "240V", "31"}
                , {"S32", "HECO-208", "5", "208V", "32"}
                , {"S32", "HECO-240", "1", "240V", "32"}
                , {"S35", "PRC-East-208", "5", "208V", "35"}
                , {"S35", "PRC-East-240", "1", "240V", "35"}
                , {"S36", "PRC-West-208", "5", "208V", "36"}
                , {"S36", "PRC-West-240", "1", "240V", "36"}
                , {"S37", "PRC-Quebec-208", "5", "208V", "37"}
                , {"S37", "PRC-Quebec-240", "1", "240V", "37"}
        };


        models = new ArrayList<>();
        for (String[] strings : modelToal) {
            models.add(strings[1]);
        }


        List<USDebugSettingBean> newlist = new ArrayList<>();


        for (int i = 0; i < titles.length; i++) {
            USDebugSettingBean bean = new USDebugSettingBean();
            bean.setTitle(titles[i]);
            bean.setItemType(itemTypes[i]);
            bean.setRegister(registers[i]);
            newlist.add(bean);
        }
        usParamsetAdapter.replaceData(newlist);


        //????????????
//        nowReadFuns = funs[0];
        refresh();

        //????????????????????????
//        nowSet = funsSet[mType];
        //????????????????????????
//        nowSet[0][2] = 1;

    }


    /**
     * ????????????
     */
    private void refresh() {
        connectSendMsg();
    }


    /**
     * ?????????????????????
     */
    private void connectSendMsg() {
        Mydialog.Show(this);
        connectServer();
    }

    //????????????
    private SocketClientUtil mClientUtil;

    private void connectServer() {
        mClientUtil = SocketClientUtil.newInstance();
        if (mClientUtil != null) {
            mClientUtil.connect(mHandler);
        }
    }

    /**
     * ???????????????handle
     */
    StringBuilder sb = new StringBuilder();
    private String uuid;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            String text = "";
            int what = msg.what;
            switch (what) {
                case SocketClientUtil.SOCKET_EXCETION_CLOSE:
                    String message = (String) msg.obj;
                    text = "???????????????" + message;
                    break;
                case SocketClientUtil.SOCKET_CLOSE:
                    text = "????????????";
                    break;
                case SocketClientUtil.SOCKET_OPEN:
                    text = "????????????";
                    break;
                case SocketClientUtil.SOCKET_SEND_MSG:
                    BtnDelayUtil.sendMessage(this);
                    //?????????????????????????????????????????????
                    uuid = UUID.randomUUID().toString();
                    Message msgSend = Message.obtain();
                    msgSend.what = 100;
                    msgSend.obj = uuid;
                    mHandler.sendEmptyMessageDelayed(100, 3000);

                    String sendMsg = (String) msg.obj;
                    LogUtil.i("????????????:" + sendMsg);
//                    text = "??????????????????";
////                    mEditText.setText("");
//                    sb.append("Client:<br>")
//                            .append(sendMsg)
//                            .append("<br><br>");
                    break;
                case SocketClientUtil.SOCKET_RECEIVE_MSG:
                    //????????????????????????
                    isReceiveSucc = true;
                    //????????????????????????
//                    mHandler.sendEmptyMessage(100);

                    String recMsg = (String) msg.obj;
                    text = "??????????????????";
                    sb.append("Server:<br>")
                            .append(recMsg)
                            .append("<br><br>");
//                    Log.i("receive" ,recMsg);
                    break;
                //??????????????????
                case SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //?????????????????????
                        boolean isCheck = ModbusUtil.checkModbus(bytes);
                        if (isCheck) {
                            //???????????????????????????
                            parseMax(bytes, count);
                        }
                        if (count < funs.length - 1) {
                            count++;
                            mHandler.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        } else {
                            count = 0;
                            //????????????
                            SocketClientUtil.close(mClientUtil);
                            refreshFinish();
                        }

                        LogUtil.i("????????????:" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        count = 0;
                        //????????????
                        SocketClientUtil.close(mClientUtil);
                        Mydialog.Dismiss();
                    }
                    break;
                case SocketClientUtil.SOCKET_CONNECT:
                    text = "socket?????????";
                    break;
                case SocketClientUtil.SOCKET_SEND:
                    sendMsg(mClientUtil, funs[count]);
                    break;
                case 100://??????????????????
                    String myuuid = (String) msg.obj;
                    if (uuid.equals(myuuid) && !isReceiveSucc) {
                        toast(R.string.android_key1134);
                    }
//                    refreshFinish();
                    break;
                case 101:
                    connectSendMsg();
                    break;
                default:
                    BtnDelayUtil.dealTLXBtn(this, what, 2500, mContext, toolbar);
                    break;
            }
        }
    };


    /**
     * ??????????????????mtype????????????
     *
     * @param bytes
     * @param count
     */
    private void parseMax(byte[] bytes, int count) {
        //??????????????????
        byte[] bs = RegisterParseUtil.removePro17(bytes);
        if (user_type==END_USER){
            if (count == 0) {//???????????????
                parserTimeData(bs);
            }
        }else if (user_type==MAINTEAN_USER){
            //??????int???
            switch (count) {
                case 0://????????????
                    parserNSR(bytes, bs);
                    break;
                case 1://????????????
                    //??????int???
                    parserAddress(bs);
                    break;
                case 2://?????????????????????
                    //??????int???
                    parserRate(bs, 2);
                    break;
                case 3://???????????????
                    //??????int???
                    parserTimeData(bs);
                    break;
                case 4://MODBUS?????????
                    //??????int???
                    parserRate(bs, 4);
                    break;
                case 5://????????????
                    //??????int???
                    parserRate(bs, 5);
                    break;
                case 6://??????????????????
                    //??????int???
                    int value6 = MaxWifiParseUtil.obtainValueOne(bs);
                    String s = String.valueOf(value6);
                    if (value6 < items[7].length) {
                        s = items[7][value6];
                    }
                    usParamsetAdapter.getData().get(6).setValue(String.valueOf(value6));
                    usParamsetAdapter.getData().get(6).setValueStr(s);
                    break;
                case 7://??????????????????
                    //??????int???
                    int value7 = MaxWifiParseUtil.obtainValueOne(bs);
                    String s1 = String.valueOf(value7);
                    if (value7 < items[8].length) {
                        s1 = items[8][value7];
                    }

                    usParamsetAdapter.getData().get(7).setValue(String.valueOf(value7));
                    usParamsetAdapter.getData().get(7).setValueStr(s1);
                    break;
            }

        }else {
            //??????int???
            switch (count) {
                case 0://????????????
                    parserNSR(bytes, bs);
                    break;
                case 1://????????????
                    //??????int???
                    parserAddress(bs);
                    break;

                case 2://?????????????????????
                    //??????int???
                    parserRate(bs, 2);
                    break;
                case 3://???????????????
                    //??????int???
                    parserTimeData(bs);
                    break;

                case 4://MODBUS?????????
                    //??????int???
                    parserRate(bs, 4);
                    break;
                case 5:
                    //??????int???
                    parserRate(bs, 5);
                    break;
                case 6://??????????????????
                    //??????int???
                    parserAllCharge(bs);
                    break;
                case 7://??????????????????
                    //??????int???
                    parserHistoryData(bs, 7);
                    break;
                case 8:
                    //??????int???
                    parserHistoryData(bs, 8);
                    break;
            }
        }
        usParamsetAdapter.notifyDataSetChanged();
    }

    private void parserHistoryData(byte[] bs, int i) {
        int value7 = MaxWifiParseUtil.obtainValueOne(bs);
        usParamsetAdapter.getData().get(i).setValue(String.valueOf(value7));
        String s = String.valueOf(value7);
        if (value7 < items[i].length) {
            s = items[i][value7];
        }
        usParamsetAdapter.getData().get(i).setValueStr(s);
    }

    private void parserAllCharge(byte[] bs) {
        int value6 = MaxWifiParseUtil.obtainValueHAndL(bs);
        //??????ui
        initMul(6);
        String readResult6 = getReadValueReal(value6, 6);
        usParamsetAdapter.getData().get(6).setValue(String.valueOf(value6));
        usParamsetAdapter.getData().get(6).setValueStr(readResult6);
    }

    /**
     *
     * @param bs
     * @param i
     */

    private void parserRate(byte[] bs, int i) {
        int value2 = MaxWifiParseUtil.obtainValueOne(bs);
        String readResult = getReadResult(value2, i);
        usParamsetAdapter.getData().get(i).setValue(String.valueOf(value2));
        usParamsetAdapter.getData().get(i).setValueStr(readResult);
    }

    private void parserAddress(byte[] bs) {
        int value0 = MaxWifiParseUtil.obtainValueOne(bs);
        initMul(1);
        String readValueReal = getReadValueReal(value0, 1);
        usParamsetAdapter.getData().get(1).setValue(readValueReal);
        usParamsetAdapter.getData().get(1).setValueStr(readValueReal);
    }

    /**
     * ??????????????????
     * @param bytes
     * @param bs
     */
    private void parserNSR(byte[] bytes, byte[] bs) {
        LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(bytes));
        //??????model
        byte[] valueBs = MaxWifiParseUtil.subBytesFull(bs, 118, 0, 4);
        registerValues[0][0] = MaxWifiParseUtil.obtainValueOne(bs, 118);
        registerValues[0][1] = MaxWifiParseUtil.obtainValueOne(bs, 119);
        registerValues[0][2] = MaxWifiParseUtil.obtainValueOne(bs, 120);
        registerValues[0][3] = MaxWifiParseUtil.obtainValueOne(bs, 121);


        BigInteger big = new BigInteger(1, valueBs);
        long bigInteger = big.longValue();
        String modelS = MaxUtil.getDeviceModelSingleNew(bigInteger, 16) + MaxUtil.getDeviceModelSingleNew(bigInteger, 15);
        String readModel = String.format("S%s", modelS);
        //??????U???
        //??????120?????????  U ?????? 0-2bit
        int readVol = MaxWifiParseUtil.obtainValueOne(bs, 120) & 0x0007;
        //??????
        boolean isFlag = false;
        for (int i = 0; i < modelToal.length; i++) {
            String[] model = modelToal[i];
            if (model[0].equals(readModel) && model[2].equals(String.valueOf(readVol))) {
                isFlag = true;
                modelPos = i;
                usParamsetAdapter.getData().get(0).setValueStr(model[1]);
                break;
            }
        }
        if (!isFlag) {
            modelPos = -1;
            usParamsetAdapter.getData().get(0).setValueStr("");
            usParamsetAdapter.getData().get(0).setValue("");
        }
    }


    /**
     * ?????????????????????
     * @param bs
     */
    private void parserTimeData(byte[] bs) {
        try {
            int year = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 0, 0, 1));
            int month = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 1, 0, 1));
            int day = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 2, 0, 1));
            int hour = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 3, 0, 1));
            int min = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 4, 0, 1));
            int seconds = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 5, 0, 1));
            //??????ui
            StringBuilder sb = new StringBuilder()
                    .append(year).append("-")
                    .append(month).append("-")
                    .append(day).append(" ")
                    .append(hour).append(":")
                    .append(min).append(":")
                    .append(seconds);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timeS = sb.toString();
            Date date = sdf.parse(timeS);
            String timeStr = sdf.format(date);

            if (user_type==END_USER){
                usParamsetAdapter.getData().get(0).setValueStr(timeStr);
            }else {
                usParamsetAdapter.getData().get(3).setValueStr(timeStr);
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private void initMul(int position) {
        try {
            mMul = mMultiples[position];
            mUnit = mUnits[position];
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * ????????????????????????????????????????????????
     *
     * @param value
     * @return
     */
    public String getReadResult(int value, int mType) {
        String result = "";
        switch (mType) {
            case 0:
                if (value == 3) value = 2;
                break;
            case 1:
                if (value == 3) value = 2;
                if (value == 2) value = 1;
                break;
            case 2:
                if ((value & 0x000C) >> 2 == 0) value = 0;
                if ((value & 0x000C) >> 2 == 1) value = 1;
                break;
            case 4:
            case 5:
                result = String.valueOf(value);
                break;
        }
        try {
            if (TextUtils.isEmpty(result)) {
                result = items[mType][value];
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = String.valueOf(value);
        }
        return result;
    }


    public String getReadValueReal(int read, int mType) {
        boolean isNum = ((int) mMul) == mMul;
        String value = "";
        if (isNum) {
            value = read * ((int) mMul) + mUnit;
        } else {
            value = Arith.mul(read, mMul, 2) + mUnit;
        }
        //??????????????????
        switch (mType) {
            case 13:
                value = getWeek(read);
                break;//????????????
        }
        return value;
    }


    public String getWeek(int read) {
        String value = String.valueOf(read);
        switch (read) {
            case 0:
                value = getString(R.string.m41);
                break;
            case 1:
                value = getString(R.string.m35);
                break;
            case 2:
                value = getString(R.string.m36);
                break;
            case 3:
                value = getString(R.string.m37);
                break;
            case 4:
                value = getString(R.string.m38);
                break;
            case 5:
                value = getString(R.string.m39);
                break;
            case 6:
                value = getString(R.string.m40);
                break;
        }
        return value;
    }


    /**
     * ???????????????????????????????????????????????????
     *
     * @param clientUtil
     * @param sends
     * @return??????????????????????????????
     */
    private byte[] sendMsg(SocketClientUtil clientUtil, int[] sends) {
        if (clientUtil != null) {
            byte[] sendBytes = ModbusUtil.sendMsg(sends[0], sends[1], sends[2]);
            clientUtil.sendMsg(sendBytes);
            return sendBytes;
        } else {
            connectServer();
            return null;
        }
    }


    /**
     * ????????????
     */
    private void refreshFinish() {
        Mydialog.Dismiss();
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (user_type==END_USER){
            if (position == 0) {//???????????????
                setInvTime(position);
            }

        }else if (user_type==MAINTEAN_USER){
            switch (position) {
                case 0://????????????
                    setNSR();
                    break;
                case 1://????????????
                    initMul(1);
                    setCommunicationAddr(position);
                    break;
                case 2://???????????????
                    setCommunicateRate(2);
                    break;
                case 3://???????????????
                    setInvTime(position);
                    break;
                case 4://Modbus??????
                    break;
                case 5://????????????
                    break;
                case 6://??????????????????
//                    initMul(6);
                    setCommunicateRate(7);
                    break;
                case 7://??????????????????
                    setCommunicateRate(8);
                    break;
            }
        }else {
            switch (position) {
                case 0://????????????
                    setNSR();
                    break;
                case 1://????????????
                    initMul(1);
                    setCommunicationAddr(position);
                    break;
                case 2://???????????????
                    setCommunicateRate(2);
                    break;
                case 3://???????????????
                    setInvTime(position);
                    break;
                case 4://Modbus??????
                    break;
                case 5://????????????
                    break;
                case 6://??????????????????
                    initMul(6);
                    setAllCharge(position);
                    break;
                case 7://??????????????????
                    setCommunicateRate(7);
                    break;
                case 8://
                    setCommunicateRate(8);
                    break;
                case 9://??????model
                    toSetModel(position);
                    break;

            }

        }

    }

    private void toSetModel(int position) {
        String title9 = usParamsetAdapter.getData().get(position).getTitle();
        Intent intent = new Intent(USParamsSettingActivity.this, USModeSetActivity.class);
        intent.putExtra("title", title9);
        ActivityUtils.startActivity(USParamsSettingActivity.this, intent, false);
    }

    private void setAllCharge(int position) {
        String title1 = usParamsetAdapter.getData().get(position).getTitle();
        String tips1 = "";
        String unit1 = "kWh";
        showInputValueDialog(title1, tips1, unit1, value -> {
            double result = Double.parseDouble(value);
            String pValue = value+"kWh";
            usParamsetAdapter.getData().get(position).setValueStr(pValue);
            usParamsetAdapter.getData().get(position).setValue(String.valueOf(result));
            usParamsetAdapter.notifyDataSetChanged();

            currentSetPos = 6;
            //?????????????????????
            if (TextUtils.isEmpty(value)) {
                toast(R.string.all_blank);
            } else {
                try {
                    int low = getWriteValueReal(Double.parseDouble(value));
                    int high = 0;
                    if (low > 0xffff) {
                        high = low - 0xffff;
                        low = 0xffff;
                    }
                    nowSet[position][0][2] = high;
                    nowSet[position][1][2] = low;
                    connectServerWrite();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    toast(getString(R.string.m363????????????));
                    nowSet[position][0][2] = -1;
                    nowSet[position][1][2] = -1;
                }
            }

        });
    }

    private void setCommunicateRate(int itemIndex) {
        nowItems = items[itemIndex];
        new CircleDialog.Builder()
                .setWidth(0.7f)
                .setMaxHeight(0.5f)
                .setGravity(Gravity.CENTER)
                .setTitle(getString(R.string.countryandcity_first_country))
                .setItems(nowItems, new OnLvItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (nowItems != null && nowItems.length > position) {
                            try {
                                usParamsetAdapter.getData().get(itemIndex).setValueStr(nowItems[position]);
                                usParamsetAdapter.getData().get(itemIndex).setValue(String.valueOf(position));
                                usParamsetAdapter.notifyDataSetChanged();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //??????
                            currentSetPos = itemIndex;
                            if (itemIndex==2){
                                curSet = nowSet[itemIndex][position];
                            }else {
                                curSet = nowSet[itemIndex][0];
                                curSet[2] = position;
                            }

                            connectServerWrite();

                        }
                        return true;
                    }
                })
                .setNegative(getString(R.string.all_no), null)
                .show(getSupportFragmentManager());
    }

    private void setCommunicationAddr(int position) {
        String title = usParamsetAdapter.getData().get(position).getTitle();
        String tips = getString(R.string.android_key3048) + ":" + "1~254";
        String unit = "";
        showInputValueDialog(title, tips, unit, value -> {
            double result = Double.parseDouble(value);
            String pValue = value;
            usParamsetAdapter.getData().get(position).setValueStr(pValue);
            usParamsetAdapter.getData().get(position).setValue(String.valueOf(result));
            usParamsetAdapter.notifyDataSetChanged();


            currentSetPos = 1;
            //??????
            curSet = nowSet[1][0];
            //?????????????????????
            if (TextUtils.isEmpty(value)) {
                toast(R.string.all_blank);
            } else {
                try {
                    curSet[2] = getWriteValueReal(Double.parseDouble(value));
                    connectServerWrite();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    toast(getString(R.string.m363????????????));
                    curSet[2] = -1;
                }
            }

        });
    }

    private void setNSR() {
        new CircleDialog.Builder()
                .setTitle(getString(R.string.android_key499))
                .setWidth(0.7f)
                .setGravity(Gravity.CENTER)
                .setMaxHeight(0.5f)
                .setItems(models, (parent, view1, pos, id) -> {
                    currentSetPos = 0;
                    modelPos = pos;
                    setModelRegistValue();
                    usParamsetAdapter.getData().get(0).setValueStr(models.get(pos));
                    usParamsetAdapter.notifyDataSetChanged();
                    curSet = nowSet[0][0];
                    nowRegister = registerValues[0];
                    connectServerWrite();
                    return true;
                })
                .setNegative(getString(R.string.all_no), null)
                .show(getSupportFragmentManager());
    }


    private void setModelRegistValue() {
        String[] selectModels = modelToal[modelPos];
        int sModel = Integer.parseInt(selectModels[4], 16) << 8 | (registerValues[0][0] & 0x00FF);
        int uModel = Integer.parseInt(selectModels[2]) | ((registerValues[0][2] & 0b1111111111111000));
        registerValues[0][0] = sModel;
//                        registerValues[1][1] = 0;
        registerValues[0][2] = uModel;
    }


    /**
     * ?????????????????????
     * @param position
     */
    private void setInvTime(int position) {
        currentSetPos = 3;
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(
                mContext
                , (view12, year, month, dayOfMonth) -> {
            // ????????????TimePickerDialog??????????????????????????????
            Calendar c2 = Calendar.getInstance();
            new TimePickerDialog(mContext,
                    // ???????????????
                    (view121, hourOfDay, minute) -> {
                        if (year > 2000) {
                            nowSet[3][0][2] = year - 2000;
                        } else {
                            nowSet[3][0][2] = year;
                        }
                        nowSet[3][1][2] = month + 1;
                        nowSet[3][2][2] = dayOfMonth;
                        nowSet[3][3][2] = hourOfDay;
                        nowSet[3][4][2] = minute;
                        nowSecond = Calendar.getInstance().get(Calendar.SECOND);
                        nowSet[3][5][2] = nowSecond;
                        //??????ui
                        StringBuilder sb = new StringBuilder()
                                .append(year).append("-");
                        if (month + 1 < 10) {
                            sb.append("0");
                        }
                        sb.append(month + 1).append("-");
                        if (dayOfMonth < 10) {
                            sb.append("0");
                        }
                        sb.append(dayOfMonth).append(" ");
                        if (hourOfDay < 10) {
                            sb.append("0");
                        }
                        sb.append(hourOfDay).append(":");
                        if (minute < 10) {
                            sb.append("0");
                        }
                        sb.append(minute).append(":");
                        if (nowSecond < 10) {
                            sb.append("0");
                        }
                        sb.append(nowSecond);
                        usParamsetAdapter.getData().get(position).setValueStr(sb.toString());

                        connectServerWrite();

                    }
                    // ??????????????????
                    , c2.get(Calendar.HOUR_OF_DAY)
                    , c2.get(Calendar.MINUTE),
                    // true????????????24?????????
                    true).show();
        }
                , c.get(Calendar.YEAR)
                , c.get(Calendar.MONTH)
                , c.get(Calendar.DAY_OF_MONTH)
        ).show();
    }


    public int getWriteValueReal(double write) {
        try {
            return (int) Math.round(Arith.div(write, mMul));
        } catch (Exception e) {
            e.printStackTrace();
            return (int) write;
        }
    }


    private void showInputValueDialog(String title, String subTitle, String unit,
                                      USConfigTypeAllActivity.OndialogComfirListener listener) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_input_custom, null, false);
        dialogFragment = CircleDialogUtils.showCommentBodyDialog(0.75f,
                0.8f, contentView, getSupportFragmentManager(), new OnCreateBodyViewListener() {
                    @Override
                    public void onCreateBodyView(View view) {
                        CircleDrawable bgCircleDrawable = new CircleDrawable(CircleColor.DIALOG_BACKGROUND
                                , CircleDimen.DIALOG_RADIUS, CircleDimen.DIALOG_RADIUS, CircleDimen.DIALOG_RADIUS, CircleDimen.DIALOG_RADIUS);
                        view.setBackground(bgCircleDrawable);
                        TextView tvTitle = view.findViewById(R.id.tv_title);
                        TextView tvSubTtile = view.findViewById(R.id.tv_sub_title);
                        TextView tvUnit = view.findViewById(R.id.tv_unit);
                        TextView tvCancel = view.findViewById(R.id.tv_button_cancel);
                        TextView tvConfirm = view.findViewById(R.id.tv_button_confirm);
                        TextView etInput = view.findViewById(R.id.et_input);
                        tvCancel.setText(R.string.mCancel_ios);
                        tvConfirm.setText(R.string.android_key1935);


                        tvSubTtile.setText(subTitle);
                        tvUnit.setText(unit);
                        tvTitle.setText(title);

                        tvCancel.setOnClickListener(view1 -> {

                            dialogFragment.dialogDismiss();
                            dialogFragment = null;
                        });
                        tvConfirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String value = etInput.getText().toString();
                                if (TextUtils.isEmpty(value)) {
                                    toast(R.string.android_key1945);
                                    return;
                                }
                                dialogFragment.dialogDismiss();
                                listener.comfir(value);
                                dialogFragment = null;
                            }
                        });

                    }
                }, Gravity.CENTER, false);
    }


    @Override
    public void oncheck(boolean check, int position) {
        currentSetPos = 5;
        int value = check ? 1 : 0;
        //??????
        curSet = nowSet[5][0];
        curSet[2] = value;
        connectServerWrite();

        usParamsetAdapter.getData().get(position).setValue(String.valueOf(value));
        usParamsetAdapter.notifyDataSetChanged();
    }


    //????????????:???????????????
    private SocketClientUtil mClientUtilW;

    //?????????????????????
    private void connectServerWrite() {
        Mydialog.Show(mContext);
        mClientUtilW = SocketClientUtil.connectServer(mHandlerW);
    }


    /**
     * ????????????handle
     */
    //???????????????????????????
    private byte[] sendBytes;
    private boolean isWriteFinish;
    Handler mHandlerW = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //????????????
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessageWrite(this);
                    switch (currentSetPos) {
                        case 0:
                            sendBytes = SocketClientUtil.sendMsgToServer10(mClientUtilW, curSet, nowRegister);
                            LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(sendBytes));
                            break;
                        case 3:
                            if (nowSet != null) {
                                isWriteFinish = true;
                                for (int i = 0, len = nowSet[3].length; i < len; i++) {
                                    if (nowSet[3][i][2] != -1) {
                                        isWriteFinish = false;
                                        sendBytes = SocketClientUtil.sendMsgToServer(mClientUtilW, nowSet[3][i]);
                                        LogUtil.i("????????????" + (i + 1) + ":" + SocketClientUtil.bytesToHexString(sendBytes));
                                        //????????????????????????-1
                                        nowSet[3][i][2] = -1;
                                        break;
                                    }
                                }
                                //??????tcp??????;????????????????????????
                                if (isWriteFinish) {
                                    //??????????????????
                                    this.removeMessages(TIMEOUT_RECEIVE);
                                    SocketClientUtil.close(mClientUtilW);
                                    BtnDelayUtil.refreshFinish();
                                }
                            }
                            break;
                        case 6:
                            if (nowSet != null) {
                                isWriteFinish = true;
                                for (int i = 0, len = nowSet[6].length; i < len; i++) {
                                    if (nowSet[6][i][2] != -1) {
                                        isWriteFinish = false;
                                        sendBytes = SocketClientUtil.sendMsgToServer(mClientUtilW, nowSet[6][i]);
                                        LogUtil.i("????????????" + (i + 1) + ":" + SocketClientUtil.bytesToHexString(sendBytes));
                                        //????????????????????????-1
                                        nowSet[6][i][2] = -1;
                                        break;
                                    }
                                }
                                //??????tcp??????;????????????????????????
                                if (isWriteFinish) {
                                    //??????????????????
                                    this.removeMessages(TIMEOUT_RECEIVE);
                                    SocketClientUtil.close(mClientUtilW);
                                    BtnDelayUtil.refreshFinish();
                                }
                            }
                            break;
                        default:
                            if (curSet != null && curSet[2] != -1) {
                                sendBytes = SocketClientUtil.sendMsgToServer(mClientUtilW, curSet);
                                LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(sendBytes));
                            } else {
                                toast("??????????????????????????????");
                            }
                            break;
                    }

                    break;
                //??????????????????
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);


                    if (currentSetPos == 0) {//?????????????????????
                        try {
                            byte[] bytes = (byte[]) msg.obj;
                            //?????????????????????
                            boolean isCheck = MaxUtil.checkReceiverFull10(bytes);
                            if (isCheck) {
                                toast(getString(R.string.all_success));
                            } else {
                                toast(getString(R.string.all_failed));
                            }
                            //????????????
                            SocketClientUtil.close(mClientUtilW);
                            Mydialog.Dismiss();
                            LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(bytes));
                        } catch (Exception e) {
                            e.printStackTrace();
                            //????????????
                            SocketClientUtil.close(mClientUtilW);
                            Mydialog.Dismiss();
                        }
                    } else if (currentSetPos == 3) {
                        try {
                            byte[] bytes = (byte[]) msg.obj;
                            //?????????????????????
                            boolean isCheck = MaxUtil.checkReceiverFull(bytes);
                            if (isCheck) {
                                //??????????????????
//                            byte[] bs = RegisterParseUtil.removePro17Fun6(bytes);
                                //??????int???
//                            int value0 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,1,0,1));

                                toast(getString(R.string.android_key121));

                                //????????????????????????
                                mHandlerW.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                            } else {
                                toast(getString(R.string.android_key3129));
                                //??????????????????-1?????????
                                nowSet[3][0][2] = -1;
                                //??????tcp??????
                                SocketClientUtil.close(mClientUtilW);
                                BtnDelayUtil.refreshFinish();
                            }
                            LogUtil.i("???????????? " + ":" + SocketClientUtil.bytesToHexString(bytes));
                        } catch (Exception e) {
                            e.printStackTrace();
                            //??????tcp??????
                            SocketClientUtil.close(mClientUtilW);
                            BtnDelayUtil.refreshFinish();
                        }
                    } else if (currentSetPos == 6) {
                        try {
                            byte[] bytes = (byte[]) msg.obj;
                            //?????????????????????
                            boolean isCheck = MaxUtil.checkReceiverFull(bytes);
                            if (isCheck) {
                                //??????????????????
                                byte[] bs = RegisterParseUtil.removePro17Fun6(bytes);
                                //??????int???
//                            int value0 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,1,0,1));

                                toast(getString(R.string.all_success));
                                //????????????????????????
                                mHandlerW.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                            } else {
                                toast(getString(R.string.all_failed));
                                //??????????????????-1?????????
                                nowSet[6][0][2] = -1;
                                nowSet[6][1][2] = -1;
                                //??????tcp??????
                                SocketClientUtil.close(mClientUtilW);
                                BtnDelayUtil.refreshFinish();
                            }
                            LogUtil.i("????????????" + ":" + SocketClientUtil.bytesToHexString(bytes));
                        } catch (Exception e) {
                            e.printStackTrace();
                            //??????tcp??????
                            SocketClientUtil.close(mClientUtilW);
                            BtnDelayUtil.refreshFinish();
                        }
                    } else {
                        try {
                            byte[] bytes = (byte[]) msg.obj;
                            //?????????????????????
                            MaxUtil.isCheckFull(mContext, bytes);
                            LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(bytes));
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            //??????tcp??????
                            SocketClientUtil.close(mClientUtilW);
                            BtnDelayUtil.refreshFinish();
                        }
                    }


                    break;
                default:
                    BtnDelayUtil.dealTLXBtnWrite(this, what, mContext, toolbar);
                    break;
            }
        }
    };

}
