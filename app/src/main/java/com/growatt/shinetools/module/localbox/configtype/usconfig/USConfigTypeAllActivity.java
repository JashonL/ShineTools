package com.growatt.shinetools.module.localbox.configtype.usconfig;

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
import com.growatt.shinetools.module.localbox.afci.AFCIChartActivity;
import com.growatt.shinetools.utils.ActivityUtils;
import com.growatt.shinetools.utils.BtnDelayUtil;
import com.growatt.shinetools.utils.CircleDialogUtils;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.Mydialog;
import com.growatt.shinetools.widget.GridDivider;
import com.mylhyl.circledialog.BaseCircleDialog;
import com.mylhyl.circledialog.res.drawable.CircleDrawable;
import com.mylhyl.circledialog.res.values.CircleColor;
import com.mylhyl.circledialog.res.values.CircleDimen;
import com.mylhyl.circledialog.view.listener.OnCreateBodyViewListener;
import com.mylhyl.circledialog.view.listener.OnLvItemClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;

import static com.growatt.shinetools.constant.GlobalConstant.END_USER;
import static com.growatt.shinetools.constant.GlobalConstant.KEFU_USER;
import static com.growatt.shinetools.modbusbox.SocketClientUtil.SOCKET_RECEIVE_BYTES;

public class USConfigTypeAllActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener,
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


    private MenuItem item;
    //?????????
    private String[] titls;
    private String[] registers;

    //????????????
    private int[][][] funs;
    private boolean isReceiveSucc = false;
    private int count = 0;//????????????????????????

    //????????????
    //?????????????????????????????????????????????????????????
    private int[][][] funsSet;
    private int[][] nowSet;
    private int[] setItem;
    private String[] antiReflux;//???????????????
    private float[] mMultiples;//????????????
    private float mMul = 1;//????????????
    private String[] mUnits;
    private String mUnit = "";//????????????

    private String[] frequency;//???????????????
    private String[] voltage;//???????????????

    public static final String KEY_OF_ITEM_SETITEMSINDEX = "KEY_OF_ITEM_SETITEMSINDEX";
    private int setItemsIndex;

    private BaseCircleDialog dialogFragment;


    //                 {3, 3, 3}//?????????????????????3
    //                , {3, 4, 4}//?????????????????????4
    //                , {3, 5, 5}//????????????5
    //                , {3, 8, 8}//pv??????6
    //                , {3, 91, 91}//??????????????????9 ----------4
    //                , {3, 92, 92}//????????????10
    //                , {3, 107, 107}//????????????11
    //                , {3, 108, 108}//??????????????????12
    //                , {3, 109, 109}//??????Q?????????13
    //                //????????????
    //                , {3, 17, 17}//2???????????? --------9
    //                , {3, 18, 18}//3????????????
    //                , {3, 19, 19}//4?????????????????????????????????
    //                , {3, 30, 30}//5????????????
    //                , {3, 51, 51}//7????????????
    //                , {3, 80, 80}//8 ac 10??????????????? ------------14
    //                , {3, 81, 81}//9 pv ???????????????
    //                , {3, 88, 88}//10 modbus??????
    //                , {3, 203, 203}//13 pid????????????
    //                 /*-----------------TLX?????? pos=18-------------------------*/
    //                , {3, 123, 123}//????????????????????????
    //                , {3, 3000, 3000}//?????????????????????????????????  ----------------19
    //                , {3, 3017, 3017}//?????????????????????????????????
    //                , {3, 3080, 3080}//????????????
    //                , {3, 3081, 3081}//????????????
    //                , {3, 3030, 3030}//cv??????23
    //                , {3, 3024, 3024}//cc??????24  ---------------24
    //                , {3, 3047, 3047}//?????????????????????
    //                , {3, 3048, 3048}//????????????soc
    //                , {3, 3036, 3036}//?????????????????????
    //                , {3, 3037, 3037}//????????????soc 28
    //                 /*-----------------??????-------------------------*/
    //                , {3, 1, 1}//?????????????????????max ???  tl-x   pos=29   --------------29
    //                , {3, 3019, 3019}//??????????????????????????????
    //                , {3, 539, 539}//????????????
    //
    //                , {3, 544, 544}//??????1
    //                , {3, 545, 545}//??????2
    //                , {3, 546, 546}//??????3
    //                , {3, 547, 547}//FFT

    //?????????????????????????????????

    private int mType = -1;

    private int user_type = KEFU_USER;

    @Override
    protected int getContentView() {
        return R.layout.activity_config_type_all;
    }

    @Override
    protected void initViews() {
        initToobar(toolbar);
        tvTitle.setText(R.string.android_key3091);
        toolbar.inflateMenu(R.menu.comment_right_menu);
        item = toolbar.getMenu().findItem(R.id.right_action);
        item.setTitle(R.string.m370??????);
        toolbar.setOnMenuItemClickListener(this);

        rvSystem.setLayoutManager(new LinearLayoutManager(this));
        usParamsetAdapter = new UsSettingAdapter(new ArrayList<>(), this);
        int div = (int) getResources().getDimension(R.dimen.dp_1);
        GridDivider gridDivider = new GridDivider(ContextCompat.getColor(this, R.color.white), div, div);
        rvSystem.addItemDecoration(gridDivider);
        rvSystem.setAdapter(usParamsetAdapter);
        usParamsetAdapter.setOnItemClickListener(this);

        String title = getIntent().getStringExtra("title");
        if (!TextUtils.isEmpty(title)){
            tvTitle.setText(title);
        }

    }

    @Override
    protected void initData() {
        //1.??????????????????
        user_type= ShineToosApplication.getContext().getUser_type();
        //??????????????????
        setItemsIndex = getIntent().getIntExtra(KEY_OF_ITEM_SETITEMSINDEX, 0);
        switch (setItemsIndex) {
            case 0://?????????????????????
                titls = new String[]{getString(R.string.m398?????????????????????), getString(R.string.android_key836)};
                registers = new String[]{"(3)", "(3)"};
                break;
            case 1://???????????????
                titls = new String[]{getString(R.string.m???????????????),
                        getString(R.string.m????????????????????????),
                        getString(R.string.android_key748)};
                registers = new String[]{"(122)", "(123)", "(3000)"};
                break;
            case 2://???????????????
                titls = new String[]{getString(R.string.m???????????????)
                        , getString(R.string.m?????????????????????????????????), getString(R.string.m??????????????????????????????)};
                registers = new String[]{"(3016)", "(3017)", "(3019)"};
                break;
            case 3://????????????
                if (user_type == END_USER) {
                    titls = new String[]{getString(R.string.m??????????????????)};
                    registers = new String[]{"(3079)"};
                }else {
                    titls = new String[]{getString(R.string.m????????????)
                            , getString(R.string.m????????????), getString(R.string.m????????????)};
                    registers = new String[]{"(3079)", "(3081)", "(3080)"};
                }

                break;
            case 4://AFCI
                titls = new String[]{getString(R.string.android_key2396), getString(R.string.AFCI??????)+1, getString(R.string.AFCI??????)+2,
                        getString(R.string.AFCI??????)+3, getString(R.string.FFT??????????????????), getString(R.string.AFCI????????????)};
                registers = new String[]{"", "(544)", "(545)", "(546)", "(547)", ""};
                break;
        }


        List<USDebugSettingBean> newlist = new ArrayList<>();
        for (int i = 0; i < titls.length; i++) {
            USDebugSettingBean bean = new USDebugSettingBean();
            bean.setTitle(titls[i]);
            int itemType = 0;
            switch (setItemsIndex) {
                case 0://?????????????????????
                    if (i == 0) {
                        itemType = UsSettingConstant.SETTING_TYPE_INPUT;
                    } else {
                        itemType = UsSettingConstant.SETTING_TYPE_SWITCH;
                    }
                    break;
                case 1://???????????????
                    if (i == 0) {
                        itemType = UsSettingConstant.SETTING_TYPE_SELECT;
                    } else {
                        itemType = UsSettingConstant.SETTING_TYPE_INPUT;
                    }
                    break;
                case 2://???????????????
                    if (i == 0) {
                        itemType = UsSettingConstant.SETTING_TYPE_SWITCH;
                    } else {
                        itemType = UsSettingConstant.SETTING_TYPE_INPUT;
                    }
                    break;
                case 3://????????????
                    if (i == 0) {
                        itemType = UsSettingConstant.SETTING_TYPE_SWITCH;
                    } else {
                        itemType = UsSettingConstant.SETTING_TYPE_SELECT;
                    }
                    break;
                case 4://AFCI
                    if (i == 0) {
                        itemType = UsSettingConstant.SETTING_TYPE_SWITCH;
                    } else {
                        itemType = UsSettingConstant.SETTING_TYPE_INPUT;
                    }
                    break;

            }

            bean.setItemType(itemType);
            bean.setRegister(registers[i]);
            newlist.add(bean);
        }
        usParamsetAdapter.replaceData(newlist);


        funs = new int[][][]{
                {{3, 0, 5}},//?????????????????????
                {{3, 122, 122}, {3, 123, 123}, {3, 3000, 3000}},//???????????????3?????????
                {{3, 3016, 3016}, {3, 3017, 3017}, {3, 3019, 3019}},//???????????????3?????????
                {{3, 3079, 3079}, {3, 3081, 3081}, {3, 3080, 3080}},//????????????
                {{3,541,541},{3, 544, 544}, {3, 545, 545}, {3, 546, 546},{3,547,547}},//AFCI 6???
        };


        funsSet = new int[][][]{
                {{6, 2, 1}, {6, 3, -1}},//?????????????????????
                {{6, 122, -1}, {6, 123, -1}, {6, 3000, -1}},//???????????????3?????????,?????????2???
                {{6, 3016, -1}, {6, 3017, -1}, {6, 3019, -1}},//???????????????3?????????
                {{6, 3079, -1}, {6, 3081, -1}, {6, 3080, -1}},//????????????
                {{6,541,-1},{6, 544, -1}, {6, 545, -1}, {6, 546, -1}, {6, 547, -1}},//AFCI 6???
        };
        nowSet = funsSet[setItemsIndex];

        antiReflux = new String[]{getString(R.string.android_key2885), getString(R.string.android_key2886)};
        frequency = new String[]{"50Hz", "60Hz"};
        voltage = new String[]{"240V"};
        try {
            mMultiples = new float[]{
                    1, 1, 1, 0.1f, 0.01f
                    , 1, 1, 50, 0.1f, 0.1f
                    , 1, 1, 1, 1, 0.1f
                    , 0.1f, 1, 1, 1f, 1f
                    , 0.1f, 1, 1, 0.01f, 0.1f
                    , 1, 1, 1, 1, 1
                    , 0.1f, 1
                    , 1, 1, 1, 1
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mUnits = new String[]{
                    "", "", "", "%", ""
                    , "", "", "", "", ""
                    , "", "", "", "", ""
                    , "", "", "", "%", "%"
                    , "", "", "", "", ""
                    , "", "", "", "", ""
                    , "", ""
                    , "", "", "", ""
            };
        } catch (Exception e) {
            e.printStackTrace();
        }


        refresh();

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
    /**
     * ???????????????
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
                        if (count < funs[setItemsIndex].length - 1) {
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
                    if (count < funs[setItemsIndex].length) {
                        sendMsg(mClientUtil, funs[setItemsIndex][count]);
                    }
                    break;
                case 100://??????????????????
                    String myuuid = (String) msg.obj;
                    if (uuid.equals(myuuid) && !isReceiveSucc) {
                        toast("??????????????????????????????");
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


    /**
     * ????????????
     *
     * @param bytes
     * @param count
     */
    private void parseMax(byte[] bytes, int count) {
        //??????????????????
        byte[] bs = RegisterParseUtil.removePro17(bytes);
        //??????int???
        switch (setItemsIndex) {
            case 0://?????????????????????(??????????????????)
                if (count==0){
                    int value = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3, 0, 1));
                    usParamsetAdapter.getData().get(0).setValue(String.valueOf(value));
                    String s = value + "%";
                    usParamsetAdapter.getData().get(0).setValueStr(s);

                    int value1 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 2, 0, 1));
                    usParamsetAdapter.getData().get(1).setValue(String.valueOf(value1));
                    usParamsetAdapter.getData().get(1).setValueStr(s);

                }
                break;
            case 1://???????????????
                if (count == 0) {//???????????????
                    int value1 = MaxWifiParseUtil.obtainValueOne(bs);
                    usParamsetAdapter.getData().get(0).setValue(String.valueOf(value1));
                    String sValue = String.valueOf(value1);
                    if (antiReflux.length > value1) {
                        sValue = antiReflux[value1];
                    }
                    usParamsetAdapter.getData().get(0).setValueStr(sValue);
                } else if (count == 1) {//????????????????????????
                    mType = 18;
                    try {
                        mMul = mMultiples[18];
                        mUnit = mUnits[18];
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //??????int???
                    int value2 = MaxWifiParseUtil.obtainValueOne(bs);
                    usParamsetAdapter.getData().get(1).setValue(String.valueOf(value2));
                    usParamsetAdapter.getData().get(1).setValueStr(getReadValueReal(value2));



                } else if (count == 2) {//???????????????????????????????????????

                    mType = 19;
                    try {
                        mMul = mMultiples[19];
                        mUnit = mUnits[19];
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //??????int???
                    int value2 = MaxWifiParseUtil.obtainValueOne(bs);
                    usParamsetAdapter.getData().get(2).setValue(String.valueOf(value2));
                    usParamsetAdapter.getData().get(2).setValueStr(getReadValueReal(value2));


                }
                break;
            case 2://?????????
                if (count == 0) {//???????????????
                    int value1 = MaxWifiParseUtil.obtainValueOne(bs);
                    usParamsetAdapter.getData().get(0).setValue(String.valueOf(value1));
                } else if (count == 1) {//?????????????????????????????????
                    mType = 20;
                    try {
                        mMul = mMultiples[3];
                        mUnit = mUnits[3];
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //??????int???
                    int value2 = MaxWifiParseUtil.obtainValueOne(bs);
                    usParamsetAdapter.getData().get(1).setValue(String.valueOf(value2));
                    usParamsetAdapter.getData().get(1).setValueStr(getReadValueReal(value2));



                } else if (count == 2) {//??????????????????????????????
                    mType = 30;
                    try {
                        mMul = mMultiples[3];
                        mUnit = mUnits[3];
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //??????int???
                    int value2 = MaxWifiParseUtil.obtainValueOne(bs);
                    usParamsetAdapter.getData().get(2).setValue(String.valueOf(value2));
                    usParamsetAdapter.getData().get(2).setValueStr(getReadValueReal(value2));

                }

                break;


            case 3://????????????

                if (count == 0) {//????????????
                    int value1 = MaxWifiParseUtil.obtainValueOne(bs);
                    usParamsetAdapter.getData().get(0).setValue(String.valueOf(value1));
                } else if (count == 1) {//????????????
                    //??????int???
                    int value2 = MaxWifiParseUtil.obtainValueOne(bs);
                    usParamsetAdapter.getData().get(1).setValue(String.valueOf(value2));

                    String sValue = String.valueOf(value2);
                    if (frequency.length > value2) {
                        sValue = frequency[value2];
                    }
                    usParamsetAdapter.getData().get(1).setValueStr(sValue);

                } else if (count == 2) {//????????????
                    //??????int???
                    int value3 = MaxWifiParseUtil.obtainValueOne(bs);
                    usParamsetAdapter.getData().get(2).setValue(String.valueOf(value3));
                    String sValue = String.valueOf(value3);
                    if (voltage.length > value3) {
                        sValue = voltage[value3];
                    }
                    usParamsetAdapter.getData().get(2).setValueStr(sValue);

                }
                break;
            case 4:
                if (count == 0) {//AFCI??????
                    int value1 = MaxWifiParseUtil.obtainValueOne(bs);
                    usParamsetAdapter.getData().get(0).setValue(String.valueOf(value1));
                } else if (count == 1) {//AFCI??????1
                    mType = 32;
                    try {
                        mMul = mMultiples[mType];
                        mUnit = mUnits[mType];
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //??????int???
                    int value2 = MaxWifiParseUtil.obtainValueOne(bs);
                    usParamsetAdapter.getData().get(1).setValue(String.valueOf(value2));
                    usParamsetAdapter.getData().get(1).setValueStr(getReadValueReal(value2));

                } else if (count == 2) {//AFCI??????2
                    mType = 33;
                    try {
                        mMul = mMultiples[mType];
                        mUnit = mUnits[mType];
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //??????int???
                    int value2 = MaxWifiParseUtil.obtainValueOne(bs);
                    usParamsetAdapter.getData().get(2).setValue(String.valueOf(value2));
                    usParamsetAdapter.getData().get(2).setValueStr(getReadValueReal(value2));

                }else if (count==3){//AFCI??????3
                    mType = 34;
                    try {
                        mMul = mMultiples[mType];
                        mUnit = mUnits[mType];
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //??????int???
                    int value2 = MaxWifiParseUtil.obtainValueOne(bs);
                    usParamsetAdapter.getData().get(3).setValue(String.valueOf(value2));
                    usParamsetAdapter.getData().get(3).setValueStr(getReadValueReal(value2));
                }else if (count==4){//FFT???????????????????????????
                    mType = 35;
                    try {
                        mMul = mMultiples[mType];
                        mUnit = mUnits[mType];
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //??????int???
                    int value2 = MaxWifiParseUtil.obtainValueOne(bs);
                    usParamsetAdapter.getData().get(4).setValue(String.valueOf(value2));
                    usParamsetAdapter.getData().get(4).setValueStr(getReadValueReal(value2));
                }
                break;

        }
        usParamsetAdapter.notifyDataSetChanged();
    }

    public String getReadValueReal(int read) {
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

    public int getWriteValueReal(double write){
        try {
            return (int) Math.round(Arith.div(write,mMul));
        } catch (Exception e) {
            e.printStackTrace();
            return (int) write;
        }
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

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.right_action:
                //?????????????????????
                refresh();
                break;
        }
        return true;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        switch (setItemsIndex) {
            case 0://?????????????????????
                if (position == 0) {
                    String title = getString(R.string.m398?????????????????????);
                    String tips = getString(R.string.android_key3048) + ":" + "0~100";
                    String unit = "%";
                    showInputValueDialog(title, tips, unit, value -> {
                        //?????????????????????
                        double result = Double.parseDouble(value);
                        String pValue = value + "%";
                        usParamsetAdapter.getData().get(position).setValueStr(pValue);
                        usParamsetAdapter.getData().get(position).setValue(String.valueOf(result));
                        usParamsetAdapter.notifyDataSetChanged();

                        //??????
                        setItem = nowSet[1];
                        setItem[2] = (int) result;
                        writeRegisterValue();
                    });
                }
                break;
            case 1://???????????????
                if (position == 0) {//???????????????
                    CircleDialogUtils.showCommentItemDialog(this, getString(R.string.android_key1809),
                            Arrays.asList(antiReflux), Gravity.CENTER, new OnLvItemClickListener() {
                                @Override
                                public boolean onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                                    if (antiReflux != null && antiReflux.length > pos) {
                                        String text = antiReflux[pos];
                                        usParamsetAdapter.getData().get(position).setValueStr(text);
                                        usParamsetAdapter.getData().get(position).setValue(String.valueOf(pos));
                                        usParamsetAdapter.notifyDataSetChanged();
                                        //?????????
                                        setItem = nowSet[0];
                                        setItem[2] = pos;
                                        writeRegisterValue();
                                    }
                                    return true;
                                }
                            }, null);


                } else if (position == 1) {
                    String title = getString(R.string.m????????????????????????);
                    String tips = getString(R.string.android_key3048) + ":" + "0~100";
                    String unit = "%";
                    showInputValueDialog(title, tips, unit, value -> {
                        //?????????????????????
                        double result = Double.parseDouble(value);
                        String pValue = value + "%";
                        usParamsetAdapter.getData().get(position).setValueStr(pValue);
                        usParamsetAdapter.getData().get(position).setValue(String.valueOf(result));
                        usParamsetAdapter.notifyDataSetChanged();

                        //??????
                        setItem = nowSet[1];
                        setItem[2] = (int) result;
                        writeRegisterValue();
                    });
                } else if (position == 2) {
                    String title = getString(R.string.android_key748);
                    String tips = getString(R.string.android_key3048) + ":" + "0~100";
                    String unit = "%";
                    showInputValueDialog(title, tips, unit, value -> {
                        //?????????????????????
                        double result = Double.parseDouble(value);
                        String pValue = value + "%";
                        usParamsetAdapter.getData().get(position).setValueStr(pValue);
                        usParamsetAdapter.getData().get(position).setValue(String.valueOf(result));
                        usParamsetAdapter.notifyDataSetChanged();

                        //??????
                        setItem = nowSet[2];
                        setItem[2] = (int) result;
                        writeRegisterValue();
                    });
                }
                break;
            case 2://???????????????
                if (position == 1) {
                    String title = getString(R.string.m?????????????????????????????????);
                    String tips = getString(R.string.android_key3048) + ":" + "0~1000";
                    String unit = "%";
                    showInputValueDialog(title, tips, unit, value -> {
                        //?????????????????????
                        double result = Double.parseDouble(value);
                        String pValue = value + "%";
                        usParamsetAdapter.getData().get(position).setValueStr(pValue);
                        usParamsetAdapter.getData().get(position).setValue(String.valueOf(result));
                        usParamsetAdapter.notifyDataSetChanged();

                        //??????
                        setItem = nowSet[1];
                        setItem[2] = (int) result;
                        LogUtil.d("???????????????1???"+Arrays.toString(setItem));
                        writeRegisterValue();
                    });
                } else if (position == 2) {
                    String title = getString(R.string.m??????????????????????????????);
                    String tips = getString(R.string.android_key3048) + ":" + "0~1000";
                    String unit = "%";
                    showInputValueDialog(title, tips, unit, value -> {
                        //?????????????????????
                        double result = Double.parseDouble(value);
                        String pValue = value + "%";
                        usParamsetAdapter.getData().get(position).setValueStr(pValue);
                        usParamsetAdapter.getData().get(position).setValue(String.valueOf(result));
                        usParamsetAdapter.notifyDataSetChanged();

                        //??????
                        setItem = nowSet[2];
                        setItem[2] = (int) result;
                        LogUtil.d("???????????????1???"+Arrays.toString(setItem));
                        writeRegisterValue();
                    });
                }
                break;
            case 3://????????????
                if (position==0)return;
                List<String> list=new ArrayList<>();
                if (position==1){
                    list= Arrays.asList(frequency);
                }else if (position==2){
                    list = Arrays.asList(voltage);
                }
                List<String> finalList = list;
                CircleDialogUtils.showCommentItemDialog(this, getString(R.string.android_key1809),
                        list, Gravity.CENTER, new OnLvItemClickListener() {
                            @Override
                            public boolean onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                                if (finalList.size() > pos) {
                                    String text = finalList.get(pos);
                                    usParamsetAdapter.getData().get(position).setValueStr(text);
                                    usParamsetAdapter.getData().get(position).setValue(String.valueOf(pos));
                                    usParamsetAdapter.notifyDataSetChanged();
                                    //?????????
                                    setItem = nowSet[position];
                                    setItem[2] = pos;
                                    writeRegisterValue();
                                }
                                return true;
                            }
                        }, null);
                break;

            case 4://AFCI
                if (position == 1) {
                    setAFCIThreshold(position,32);
                }else if (position==2){
                    setAFCIThreshold(position,33);
                }else if (position==3){
                    setAFCIThreshold(position,34);
                }else if (position==4){
                    String title = getString(R.string.FFT??????????????????);
                    String tips = getString(R.string.android_key3048) + ":" + "0~255";
                    String unit = "";
                    showInputValueDialog(title, tips, unit, value -> {
                        //?????????????????????
                        double result = Double.parseDouble(value);
                        String pValue = value ;
                        usParamsetAdapter.getData().get(position).setValueStr(pValue);
                        usParamsetAdapter.getData().get(position).setValue(String.valueOf(result));
                        usParamsetAdapter.notifyDataSetChanged();


                        mType = 35;
                        try {
                            mMul = mMultiples[mType];
                            mUnit = mUnits[mType];
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        setItem = nowSet[position];
                        setItem[2] = getWriteValueReal(result);;
                        writeRegisterValue();
                    });
                }else if (position==5){
                    Intent intent = new Intent(mContext, AFCIChartActivity.class);
                    intent.putExtra("type", 36);
                    intent.putExtra("title", String.format("%s%s",getString(R.string.AFCI????????????),""));
                    ActivityUtils.startActivity(USConfigTypeAllActivity.this,intent,false);
                }
                break;
        }
    }


    //????????????:??????????????????
    private SocketClientUtil mClientUtilWriter;

    //?????????????????????
    private void writeRegisterValue() {
        Mydialog.Show(mContext);
        mClientUtilWriter = SocketClientUtil.connectServer(mHandlerWrite);
    }

    /**
     * ????????????handle
     */
    //???????????????????????????:{6, 2, 1}, {6, 4, -1}, {6, 89, 5}
    private byte[] sendBytes;
    //???????????????
    private int nowPos = -1;
    Handler mHandlerWrite = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //????????????
                case SocketClientUtil.SOCKET_SEND:
                    if (setItem != null && setItem[2] != -1) {
                        BtnDelayUtil.sendMessageWrite(this);
                        sendBytes = sendMsg(mClientUtilWriter, setItem);
                        LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(sendBytes));
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
                            //??????????????????
//                            byte[] bs = RegisterParseUtil.removePro17Fun6(bytes);
//                            //??????int???
//                            int value0 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 1, 0, 1));
//                            //??????ui
//                            mTvContent1.setText(readStr + ":" + value0);
                            toast(R.string.all_success);
                        } else {
                            toast(R.string.all_failed);
                        }
                        LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //??????tcp??????
                        SocketClientUtil.close(mClientUtilWriter);
                        BtnDelayUtil.refreshFinish();
                    }
                    break;
                default:
                    BtnDelayUtil.dealTLXBtnWrite(this, what, mContext, toolbar);
                    break;
            }
        }
    };


    @Override
    public void oncheck(boolean check, int position) {
        int value = check ? 1 : 0;
        switch (setItemsIndex) {
            case 0:
                switch (position) {
                    case 1://??????
                        setItem = nowSet[0];
                        setItem[2] = value;
                        writeRegisterValue();
                        break;
                }
                usParamsetAdapter.getData().get(position).setValue(String.valueOf(value));
                usParamsetAdapter.notifyDataSetChanged();
                break;

            case 2:
                switch (position) {
                    case 0://???????????????
                        setItem = nowSet[0];
                        setItem[2] = value;
                        writeRegisterValue();
                        break;
                }
                usParamsetAdapter.getData().get(position).setValue(String.valueOf(value));
                usParamsetAdapter.notifyDataSetChanged();
                break;
            case 3://????????????
                switch (position) {
                    case 0://???????????????
                        setItem = nowSet[0];
                        setItem[2] = value;
                        writeRegisterValue();
                        break;
                }
                usParamsetAdapter.getData().get(position).setValue(String.valueOf(value));
                usParamsetAdapter.notifyDataSetChanged();

                break;
        }

    }


    interface OndialogComfirListener {
        void comfir(String value);
    }


    private void showInputValueDialog(String title, String subTitle, String unit, OndialogComfirListener listener) {
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
                            dialogFragment=null;
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
                                dialogFragment=null;
                            }
                        });

                    }
                },Gravity.CENTER,false);
    }



    private void setAFCIThreshold(int position,int type){
        String title = getString(R.string.AFCI??????)+position;
        String tips = getString(R.string.android_key3048) + ":" + "0~65000"+"("+getString(R.string.AFCI??????)+1
                + "<" +getString(R.string.AFCI??????)+2+"<"+getString(R.string.AFCI??????)+3+")";
        String unit = "";
        showInputValueDialog(title, tips, unit, value -> {
            //?????????????????????
            double result = Double.parseDouble(value);
            String pValue = value ;
            usParamsetAdapter.getData().get(position).setValueStr(pValue);
            usParamsetAdapter.getData().get(position).setValue(String.valueOf(result));
            usParamsetAdapter.notifyDataSetChanged();


            mType = type;
            try {
                mMul = mMultiples[mType];
                mUnit = mUnits[mType];
            } catch (Exception e) {
                e.printStackTrace();
            }

            setItem = nowSet[position];
            setItem[2] = getWriteValueReal(result);;
            writeRegisterValue();
        });

    }


}
