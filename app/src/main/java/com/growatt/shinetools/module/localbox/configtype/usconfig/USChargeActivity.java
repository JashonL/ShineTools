package com.growatt.shinetools.module.localbox.configtype.usconfig;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.growatt.shinetools.R;
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
import com.growatt.shinetools.utils.CommenUtils;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.Mydialog;
import com.growatt.shinetools.widget.GridDivider;
import com.mylhyl.circledialog.BaseCircleDialog;
import com.mylhyl.circledialog.res.drawable.CircleDrawable;
import com.mylhyl.circledialog.res.values.CircleColor;
import com.mylhyl.circledialog.res.values.CircleDimen;
import com.mylhyl.circledialog.view.listener.OnCreateBodyViewListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;

import static com.growatt.shinetools.modbusbox.SocketClientUtil.SOCKET_RECEIVE_BYTES;
import static com.growatt.shinetools.utils.BtnDelayUtil.TIMEOUT_RECEIVE;

public class USChargeActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener,
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

    private String[] onkeyBdcStr;

    //????????????
    private int[][] funs;
    private boolean isReceiveSucc = false;
    private int count = 0;//????????????????????????


    //????????????
    //?????????????????????????????????????????????????????????
    private int[][][] funsSet;
    private int[][] nowSet;

    private BaseCircleDialog dialogFragment;
    //???????????????
    private int nowPos = -1;
    private boolean isWriteFinish;

    private float[] mMultiples;//????????????
    private float mMul = 1;//????????????
    private String[] mUnits;
    private String mUnit = "";//????????????

    private int mType = -1;
    private MenuItem item;


    @Override
    protected int getContentView() {
        return R.layout.activity_us_charge;
    }

    @Override
    protected void initViews() {
        initToobar(toolbar);
        tvTitle.setText(R.string.android_key3091);
        String title = getIntent().getStringExtra("title");
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }
        toolbar.setOnMenuItemClickListener(this);
        toolbar.inflateMenu(R.menu.comment_right_menu);
        item = toolbar.getMenu().findItem(R.id.right_action);
        item.setTitle(R.string.android_key816);
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

        onkeyBdcStr = new String[]{
                getString(R.string.android_key480),
                getString(R.string.android_key345),
                getString(R.string.android_key467),
                getString(R.string.disable),
        };


        //??????
        titles = new String[]{
                getString(R.string.m??????????????????????????????),
                getString(R.string.android_key260),
                getString(R.string.android_key1346),
                getString(R.string.m????????????SOC),
                getString(R.string.m?????????????????????),
                getString(R.string.android_key502),
                getString(R.string.one_key_set_bdc_mode)
        };

        //??????????????????
        registers = new String[]{
                "3125~3249",
                "3049",
                "3047",
                "3048",
                "3036",
                "3037",
                "608"
        };


        //item???????????????
        itemTypes = new int[]{
                UsSettingConstant.SETTING_TYPE_NEXT,
                UsSettingConstant.SETTING_TYPE_SWITCH,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_NEXT,
        };


        funs = new int[][]{
                {3,3049,3049},
                {3, 3049, 3049},
                {3, 3047, 3047},
                {3, 3048, 3048},
                {3, 3036, 3036},
                {3, 3037, 3037},
                {3, 608, 608}
        };


        funsSet = new int[][][]{
                {{6, 3125, 0}},
                {{6, 3049, 0}},
                {{6, 3047, -1}},
                {{6, 3048, -1}},
                {{6, 3036, -1}},
                {{6, 3037, -1}},
                {{6, 608, -1}},
        };


        List<USDebugSettingBean> newlist = new ArrayList<>();


        for (int i = 0; i < titles.length; i++) {
            USDebugSettingBean bean = new USDebugSettingBean();
            bean.setTitle(titles[i]);
            bean.setItemType(itemTypes[i]);
            bean.setRegister(registers[i]);
            if (i != titles.length - 1) {
                bean.setUnit("%");
            }
            newlist.add(bean);
        }
        usParamsetAdapter.replaceData(newlist);


        try {
            mMultiples = new float[]{
                    1, 1, 1, 1, 1f
                    , 1, 1, 1, 1, 1
                    , 1, 1, 1, 1, 1
                    , 1, 1, 1, 1, 1
                    , 1, 1, 1, 1, 1
                    , 1, 1, 1, 1, 1
                    , 1, 1
                    , 1, 1, 1, 1, 1
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mUnits = new String[]{
                    "", "", "", "", ""
                    , "", "", "", "", ""
                    , "", "", "", "", ""
                    , "", "", "", "", ""
                    , "", "", "", "", ""
                    , "", "", "", "", ""
                    , "", ""
                    , "", "", "", "", ""
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
                    if (count < funs.length) {
                        sendMsg(mClientUtil, funs[count]);
                    }
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
     * ??????????????????mtype????????????
     *
     * @param bytes
     * @param count
     */
    private void parseMax(byte[] bytes, int count) {
        //??????????????????
        byte[] bs = RegisterParseUtil.removePro17(bytes);
        //??????int???
        switch (count) {
            case 1://AC????????????
                int value0 = MaxWifiParseUtil.obtainValueOne(bs);
                usParamsetAdapter.getData().get(1).setValue(String.valueOf(value0));
                usParamsetAdapter.getData().get(1).setValueStr(getReadValueReal(value0));
                break;
            case 2:
                int value1 = MaxWifiParseUtil.obtainValueOne(bs);
                usParamsetAdapter.getData().get(2).setValue(String.valueOf(value1));
                usParamsetAdapter.getData().get(2).setValueStr(getReadValueReal(value1));
                break;
            case 3:
                int value2 = MaxWifiParseUtil.obtainValueOne(bs);
                usParamsetAdapter.getData().get(3).setValue(String.valueOf(value2));
                usParamsetAdapter.getData().get(3).setValueStr(getReadValueReal(value2));
                break;
            case 4:
                int value3 = MaxWifiParseUtil.obtainValueOne(bs);
                float roundV = value3 * 100 / 255f;
                value3 =Math.round(roundV);
                usParamsetAdapter.getData().get(4).setValue(String.valueOf(value3));
                usParamsetAdapter.getData().get(4).setValueStr(getReadValueReal(value3));
                break;
            case 5:
                int value4 = MaxWifiParseUtil.obtainValueOne(bs);
                usParamsetAdapter.getData().get(5).setValue(String.valueOf(value4));
                usParamsetAdapter.getData().get(5).setValueStr(getReadValueReal(value4));
                break;
            case 6:
                int value5 = MaxWifiParseUtil.obtainValueOne(bs);
                String value = String.valueOf(value5);
                if (value5 < onkeyBdcStr.length) {
                    value = onkeyBdcStr[value5];
                }
                usParamsetAdapter.getData().get(6).setValue(String.valueOf(value5));
                usParamsetAdapter.getData().get(6).setValueStr(value);
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
     * ????????????
     */
    private void refreshFinish() {
        Mydialog.Dismiss();
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.right_action) {
            refresh();
        }
        return true;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        USDebugSettingBean bean = usParamsetAdapter.getData().get(position);
        showUnit(position);
        switch (position) {
            case 0:
                ActivityUtils.gotoActivity(USChargeActivity.this, USChargeTimeActivity.class, false);
                break;
            case 1://

                break;
            case 2:
            case 3:
            case 4:
            case 5:
                String title = bean.getTitle();
                String tips = getString(R.string.android_key3048) + ":" + "0~100";
                String unit = "";
                showInputValueDialog(title, tips, unit, value -> {
                    nowPos = -1;
                    //?????????????????????
                    if (TextUtils.isEmpty(value)) {
                        toast(R.string.all_blank);
                    } else {
                        try {
                            double v = Double.parseDouble(value);
                            if (CommenUtils.isOuter(0, 100, v)) {
                                toast(R.string.m620??????????????????);
                                return;
                            }

                            nowSet = funsSet[position];
                            int value1 = getWriteValueReal(Double.parseDouble(value));

                            int setValue = 0;
                            if (position == 4) {//??????????????????0-255???????????????????????????????????????
                                float v1 = value1 * 255 / 100f;
                                setValue =Math.round(v1) ;
                            }

                            nowSet[0][2] = setValue;
                            bean.setValue(String.valueOf(value1));
                            bean.setValueStr(getReadValueReal(value1));
                            usParamsetAdapter.notifyDataSetChanged();

                            writeRegisterValue();
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            toast(getString(R.string.m363????????????));
                        }
                    }
                });
                break;
            case 6:
                CircleDialogUtils.showCommentItemDialog(this, getString(R.string.android_key1809),
                        Arrays.asList(onkeyBdcStr), Gravity.CENTER, (parent, view1, pos, id) -> {
                            if (onkeyBdcStr.length > pos) {
                                String text = onkeyBdcStr[pos];
                                usParamsetAdapter.getData().get(position).setValueStr(text);
                                usParamsetAdapter.getData().get(position).setValue(String.valueOf(pos));
                                usParamsetAdapter.notifyDataSetChanged();
                                //?????????
                                nowSet = funsSet[position];
                                nowSet[0][2] = pos;
                                writeRegisterValue();
                            }
                            return true;
                        }, null);
                break;
        }
    }

    public int getWriteValueReal(double write) {
        try {
            return (int) Math.round(Arith.div(write, mMul));
        } catch (Exception e) {
            e.printStackTrace();
            return (int) write;
        }
    }


    private void showUnit(int pos) {
        mType = pos;
        try {
            mMul = mMultiples[mType];
            mUnit = mUnits[mType];
        } catch (Exception e) {
            e.printStackTrace();
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
        int value = check ? 1 : 0;
        //??????AC????????????
        nowSet = funsSet[0];
        nowSet[0][2] = value;
        //??????????????????
        writeRegisterValue();
        usParamsetAdapter.getData().get(position).setValue(String.valueOf(value));
        usParamsetAdapter.notifyDataSetChanged();
    }

    //????????????:??????????????????
    private SocketClientUtil mClientUtilWriter;

    //?????????????????????
    private void writeRegisterValue() {
        Mydialog.Show(mContext);
        mClientUtilWriter = SocketClientUtil.connectServer(mHandlerWrite);
    }

    private byte[] sendBytes;
    Handler mHandlerWrite = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //????????????
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessageWrite(this);
                    if (nowSet != null) {
                        isWriteFinish = true;
                        for (int i = 0, len = nowSet.length; i < len; i++) {
                            if (nowSet[i][2] != -1) {
                                nowPos = i;
                                isWriteFinish = false;
                                sendBytes = SocketClientUtil.sendMsgToServer(mClientUtilWriter, nowSet[i]);
                                LogUtil.i("????????????" + (i + 1) + ":" + SocketClientUtil.bytesToHexString(sendBytes));
                                //????????????????????????-1
                                nowSet[i][2] = -1;
                                break;
                            }
                        }
                        //??????tcp??????;????????????????????????
                        if (isWriteFinish) {
                            //??????????????????
                            this.removeMessages(TIMEOUT_RECEIVE);
                            SocketClientUtil.close(mClientUtilWriter);
                            BtnDelayUtil.refreshFinish();
                        }
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
                            toast(getString(R.string.all_success));
                            //????????????????????????
                            mHandlerWrite.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        } else {
                            toast(getString(R.string.all_failed));
                            //??????tcp??????
                            SocketClientUtil.close(mClientUtilWriter);
                            BtnDelayUtil.refreshFinish();
                        }
                        LogUtil.i("????????????" + (nowPos + 1) + ":" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
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


}
