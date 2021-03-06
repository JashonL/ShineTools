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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;

import static com.growatt.shinetools.constant.GlobalConstant.END_USER;
import static com.growatt.shinetools.constant.GlobalConstant.KEFU_USER;
import static com.growatt.shinetools.modbusbox.SocketClientUtil.SOCKET_RECEIVE_BYTES;
import static com.growatt.shinetools.module.localbox.configtype.usconfig.USConfigTypeAllActivity.KEY_OF_ITEM_SETITEMSINDEX;
import static com.growatt.shinetools.utils.BtnDelayUtil.TIMEOUT_RECEIVE;

public class USAllSettingItemActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener,
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
    private String[][] titles;
    private String[][] registers;
    private int[][] itemTypes;


    //????????????
    private int[][][] funs;
    private boolean isReceiveSucc = false;
    private int count = 0;//????????????????????????
    private int[][] nowReadFuns;//?????????????????????

    //????????????
    //?????????????????????????????????????????????????????????
    private int[][][] funsSet;
    private int[][] nowSet;

    private float[][] mMultiples;//????????????
    private float[] mMul = new float[]{1, 1};
    private String[][] mUnits;
    private String[] mUnit = new String[]{"", ""};//????????????

    private int mType = -1;
    private BaseCircleDialog dialogFragment;

    private int user_type = KEFU_USER;
    private MenuItem item;

    @Override
    protected int getContentView() {
        return R.layout.activity_us_all_setting_item;
    }

    @Override
    protected void initViews() {
        initToobar(toolbar);
        tvTitle.setText(R.string.android_key3091);
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
        user_type = ShineToosApplication.getContext().getUser_type();
        //??????????????????
        mType = getIntent().getIntExtra(KEY_OF_ITEM_SETITEMSINDEX, 0);

        String bartitle = getIntent().getStringExtra("bartitle");
        if (!TextUtils.isEmpty(bartitle)) {
            tvTitle.setText(bartitle);
        }


        //??????
        titles = new String[][]{
                {getString(R.string.m402??????PF), getString(R.string.m377????????????)}, //??????PF         0
                {getString(R.string.m399????????????), getString(R.string.m377????????????)}, //????????????      1
                {getString(R.string.m400????????????), getString(R.string.m377????????????)}, //????????????      2
                {getString(R.string.m401??????PF), getString(R.string.m377????????????)}, //??????PF        3
                {getString(R.string.m387????????????????????????), getString(R.string.m388????????????????????????)}, //PF????????????????????????        4

                {getString(R.string.android_key880) + "1", getString(R.string.android_key880) + "2",
                        getString(R.string.android_key880) + "3", getString(R.string.android_key880) + "4"}, //PF????????????????????????       5

                {getString(R.string.android_key883) + "1", getString(R.string.android_key883) + "2",
                        getString(R.string.android_key883) + "3", getString(R.string.android_key883) + "4"}, //PF??????       6

        };
        //??????????????????
        registers = new String[][]{
                {"2", "5"},//??????PF         0            --------(9 4)
                {"2", "3"},//????????????      1             --------(9 1)
                {"2", "4"},//????????????      2             --------(9 2)
                {"2", "5"},//??????PF        3              --------(9 3)
                {"99", "100"},//PF????????????????????????        4   ------(2  4)
                {"110", "112", "114", "116"},//PF????????????????????????       5  (7 0)
                {"111", "113", "115", "117"}, //PF??????       6          (7 1)
        };

        //item???????????????
        itemTypes = new int[][]{
                {UsSettingConstant.SETTING_TYPE_INPUT, UsSettingConstant.SETTING_TYPE_SWITCH},
                {UsSettingConstant.SETTING_TYPE_INPUT, UsSettingConstant.SETTING_TYPE_SWITCH},
                {UsSettingConstant.SETTING_TYPE_INPUT, UsSettingConstant.SETTING_TYPE_SWITCH},
                {UsSettingConstant.SETTING_TYPE_INPUT, UsSettingConstant.SETTING_TYPE_SWITCH},
                {UsSettingConstant.SETTING_TYPE_INPUT, UsSettingConstant.SETTING_TYPE_INPUT},
                {UsSettingConstant.SETTING_TYPE_INPUT, UsSettingConstant.SETTING_TYPE_INPUT, UsSettingConstant.SETTING_TYPE_INPUT, UsSettingConstant.SETTING_TYPE_INPUT},
                {UsSettingConstant.SETTING_TYPE_INPUT, UsSettingConstant.SETTING_TYPE_INPUT, UsSettingConstant.SETTING_TYPE_INPUT, UsSettingConstant.SETTING_TYPE_INPUT},

        };


        funs = new int[][][]{
                {{3, 0, 5}},//??????PF         0
                {{3, 0, 5}},//????????????         1
                {{3, 0, 5}},//????????????         2
                {{3, 0, 5}},//??????PF         3
                {{3, 99, 100}},//pf????????????/????????????
                {{3, 110, 116}},//PF????????????????????????
                {{3, 111, 117}},//PF??????
        };

        /*????????????PF??????5??????10000+?????????*10000??????89(Hold)???1???
          ????????????PF??????5??????10000-?????????*10000??????89(Hold)???1???
          ?????????=(?????????-10000)/10000
         */
        funsSet = new int[][][]{
                {{6, 2, 1}, {6, 5, -1}, {6, 89, 1}},//??????PF         0
                {{6, 2, 1}, {6, 4, -1}, {6, 89, 5}},//????????????
                {{6, 2, 1}, {6, 4, -1}, {6, 89, 4}},//????????????         2
                {{6, 2, 1}, {6, 5, -1}, {6, 89, 1}},//??????PF
                {{6, 99, -1}, {6, 100, -1}},//pf????????????/????????????
                {{6, 110, -1}, {6, 112, -1}, {6, 114, -1}, {6, 116, -1}},//PF????????????????????????1~4
                {{6, 111, -1}, {6, 113, -1}, {6, 115, -1}, {6, 117, -1}},//PF??????
        };


        try {
            mMultiples = new float[][]{
                    {0.1f, 0.1f},
                    {0.1f, 0.1f},
                    {0.1f, 0.1f},
                    {1, 1},
                    {0.1f, 0.1f},//pf????????????/????????????
                    {1, 1, 1, 1},
                    {1, 1, 1, 1}

            };
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mUnits = new String[][]{
                    {"%", "%"},
                    {"V", "V"},
                    {"V", "V"},
                    {"%", "%"},
                    {"V", "V"},
                    {"%", "%", "%", "%"},
                    {"", "", "", ""}
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


        List<USDebugSettingBean> newlist = new ArrayList<>();

        String[] title = titles[mType];
        int[] itemType = itemTypes[mType];
        String[] register = registers[mType];
        for (int i = 0; i < title.length; i++) {
            USDebugSettingBean bean = new USDebugSettingBean();
            bean.setTitle(title[i]);
            bean.setItemType(itemType[i]);
            bean.setRegister(register[i]);
            newlist.add(bean);
        }

        if (mType == 3 || mType == 0) {
            newlist.get(1).setValue("1");
        }

        usParamsetAdapter.replaceData(newlist);

        //????????????
        nowReadFuns = funs[mType];
        refresh();

        //????????????????????????
        nowSet = funsSet[mType];
        //????????????????????????
        nowSet[0][2] = 1;
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
                        if (count < nowReadFuns.length - 1) {
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
                    sendMsg(mClientUtil, nowReadFuns[count]);
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
        //??????int???
        switch (mType) {
            case 0:
            case 3://??????PF
                int value1 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 5, 0, 1));
                double result2 = Arith.div(value1 - 10000, 10000);
                if (result2 == -1) result2 = 1;
                usParamsetAdapter.getData().get(0).setValue(String.valueOf(value1));
                usParamsetAdapter.getData().get(0).setValueStr(String.valueOf(result2));
                break;

            case 1:
            case 2://???????????? ????????????
                int value2 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 4, 0, 1));
                usParamsetAdapter.getData().get(0).setValue(String.valueOf(value2));
                usParamsetAdapter.getData().get(0).setValueStr(String.valueOf(value2));
                break;

            case 4://PF????????????  ????????????
                int value41 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 0, 0, 1));
                int value42 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 1, 0, 1));
                //??????ui
                try {
                    String readValueReal = getReadValueReal(0, value41);
                    usParamsetAdapter.getData().get(0).setValue(String.valueOf(readValueReal));
                    usParamsetAdapter.getData().get(0).setValueStr(String.valueOf(readValueReal));

                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    String readValueReal = getReadValueReal(1, value42);
                    usParamsetAdapter.getData().get(1).setValue(String.valueOf(readValueReal));
                    usParamsetAdapter.getData().get(1).setValueStr(String.valueOf(readValueReal));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case 5:
            case 6://PF????????????????????????
                int value51 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 0, 0, 1));
                int value52 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 2, 0, 1));
                int value53 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 4, 0, 1));
                int value54 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 6, 0, 1));
                //??????ui
                try {
                    String readValueReal = getReadValueReal(0, value51);
                    usParamsetAdapter.getData().get(0).setValue(String.valueOf(readValueReal));
                    usParamsetAdapter.getData().get(0).setValueStr(String.valueOf(readValueReal));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    String readValueReal = getReadValueReal(1, value52);
                    usParamsetAdapter.getData().get(1).setValue(String.valueOf(readValueReal));
                    usParamsetAdapter.getData().get(1).setValueStr(String.valueOf(readValueReal));

                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    String readValueReal = getReadValueReal(2, value53);
                    usParamsetAdapter.getData().get(2).setValue(String.valueOf(readValueReal));
                    usParamsetAdapter.getData().get(2).setValueStr(String.valueOf(readValueReal));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    String readValueReal = getReadValueReal(3, value54);
                    usParamsetAdapter.getData().get(3).setValue(String.valueOf(readValueReal));
                    usParamsetAdapter.getData().get(3).setValueStr(String.valueOf(readValueReal));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

        }
        usParamsetAdapter.notifyDataSetChanged();
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
        switch (item.getItemId()) {
            case R.id.right_action:
                refresh();
                break;
        }
        return true;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        USDebugSettingBean bean = usParamsetAdapter.getData().get(position);
        int itemType = bean.getItemType();
        if (itemType == UsSettingConstant.SETTING_TYPE_INPUT) {//????????????
            if (mType == 0 || mType == 3) {//??????PF
                String title = bean.getTitle();
                String tips = getString(R.string.android_key3048) + ":" + "-1~-0.8,0.8~1";
                String unit = "";
                showInputValueDialog(title, tips, unit, value -> {

                    nowPos = -1;
                    //????????????????????????
                    if (TextUtils.isEmpty(value)) {
                        toast(R.string.all_blank);
                        return;
                    }

                    double v = Double.parseDouble(value);
                    if ((v >= -1 && v <= -0.8) || (v >= 0.8 && v <= 1)) {

                        usParamsetAdapter.getData().get(position).setValueStr(String.valueOf(value));
                        usParamsetAdapter.getData().get(position).setValue(String.valueOf(value));
                        usParamsetAdapter.notifyDataSetChanged();

                        try {
                            double result = Double.parseDouble(value);
                            if (mType == 0) {
                                result = Arith.add(10000, Arith.mul(result, 10000));
                            } else {
                                result = Arith.sub(10000, Arith.mul(result, 10000));
                            }
                            nowSet[1][2] = (int) result;
                            writeRegisterValue();
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            toast(getString(R.string.m363????????????));
                        }
                    }else {
                        toast(R.string.m620??????????????????);
                    }


                });
            }


            if (mType == 1 || mType == 2) {//????????????
                String title = bean.getTitle();
                String tips = getString(R.string.android_key3048) + ":" + "0~60";
                String unit = "%";
                showInputValueDialog(title, tips, unit, value -> {
                    nowPos = -1;
                    //????????????????????????
                    if (TextUtils.isEmpty(value)) {
                        toast(R.string.all_blank);
                        return;
                    }

                    double v = Double.parseDouble(value);
                    if (v < -1 || v > 60) {
                        toast(R.string.m620??????????????????);
                        return;
                    }


                    usParamsetAdapter.getData().get(position).setValueStr(String.valueOf(value));
                    usParamsetAdapter.getData().get(position).setValue(String.valueOf(value));
                    usParamsetAdapter.notifyDataSetChanged();
                    try {
                        double result = Double.parseDouble(value);
                        nowSet[1][2] = (int) result;
                        writeRegisterValue();
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        toast(getString(R.string.m363????????????));
                    }
                });
            }


            if (mType == 4) {//PV??????
                String title = bean.getTitle();
                String tips = getString(R.string.android_key3048) + ":" + "231V~241V";
                String unit = "V";
                showInputValueDialog(title, tips, unit, value -> {
                    nowPos = -1;
                    //????????????????????????
                    if (TextUtils.isEmpty(value)) {
                        toast(R.string.all_blank);
                        return;
                    }

                    double v = Double.parseDouble(value);
                    if (v < 231 || v > 241) {
                        toast(R.string.m620??????????????????);
                        return;
                    }


                    try {
                        int result = Integer.parseInt(value);
                        nowSet[position][2] = result;
                        usParamsetAdapter.getData().get(position).setValueStr(String.valueOf(result));
                        usParamsetAdapter.getData().get(position).setValue(String.valueOf(result));
                        usParamsetAdapter.notifyDataSetChanged();
                        writeRegisterValue();
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        toast(getString(R.string.m363????????????));
                    }
                });
            }


            if (mType == 5) {
                String title = bean.getTitle();
                String tips = getString(R.string.android_key3048) + ":" + "0~100" + "(" + getString(R.string.android_key3078) + 255 + ")";
                String unit = "%";
                showInputValueDialog(title, tips, unit, value -> {
                    nowPos = -1;
                    //????????????????????????
                    if (TextUtils.isEmpty(value)) {
                        toast(R.string.all_blank);
                        return;
                    }

                    double v = Double.parseDouble(value);
                    if (v < 0 || v > 100) {
                        toast(R.string.m620??????????????????);
                        return;
                    }


                    usParamsetAdapter.getData().get(position).setValueStr(String.valueOf(value));
                    usParamsetAdapter.getData().get(position).setValue(String.valueOf(value));
                    usParamsetAdapter.notifyDataSetChanged();
                    try {
                        double result = Double.parseDouble(value);
                        nowSet[position][2] = (int) result;
                        writeRegisterValue();
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        toast(getString(R.string.m363????????????));
                    }
                });
            }


            if (mType == 6) {
                if (user_type == END_USER) {
                    toast(R.string.android_key2099);
                    return;
                }
                String title = bean.getTitle();
                String tips = getString(R.string.android_key3048) + ":" + "-1~-0.8,0.8~1";
                String unit = "";
                showInputValueDialog(title, tips, unit, value -> {
                    nowPos = -1;
                    //????????????????????????
                    if (TextUtils.isEmpty(value)) {
                        toast(R.string.all_blank);
                        return;
                    }
                    usParamsetAdapter.getData().get(position).setValueStr(String.valueOf(value));
                    usParamsetAdapter.getData().get(position).setValue(String.valueOf(value));
                    usParamsetAdapter.notifyDataSetChanged();
                    try {
                        double result = Double.parseDouble(value);
                        nowSet[position][2] = (int) result;
                        writeRegisterValue();
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        toast(getString(R.string.m363????????????));
                    }
                });
            }

        } else if (itemType == UsSettingConstant.SETTING_TYPE_SELECT) {//????????????

        }
    }


    interface OndialogComfirListener {
        void comfir(String value);
    }

    private void showInputValueDialog(String title, String subTitle, String unit, OndialogComfirListener listener) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_input_custom, null, false);

        if (dialogFragment == null) {
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

    }


    @Override
    public void oncheck(boolean check, int position) {
        int value = check ? 1 : 0;
        if (mType == 0 || mType == 1 || mType == 2 || mType == 3) {//??????PF ???????????? ????????????  ??????PF
            nowPos = -1;
            nowSet[0][2] = value;
            //??????????????????
            writeRegisterValue();
        }

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

    /**
     * ????????????handle
     */
    //???????????????????????????:{6, 2, 1}, {6, 4, -1}, {6, 89, 5}
    private byte[] sendBytes;
    //???????????????
    private int nowPos = -1;
    private boolean isWriteFinish;
    Handler mHandlerWrite = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //????????????
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessageWrite(this);
//                    if (nowSet != null) {
//                        isWriteFinish = true;
//                        int len = nowSet.length;
//                        for (int i = 0;  i < len; i++) {
//                            if (nowSet[i][2] != -1) {
//                                nowPos = i;
//                                isWriteFinish = false;
//                                sendBytes = SocketClientUtil.sendMsgToServer(mClientUtilWriter, nowSet[i]);
//                                LogUtil.i("????????????" + (i + 1) + ":" + SocketClientUtil.bytesToHexString(sendBytes));
//                                //????????????????????????-1
//                                nowSet[i][2] = -1;
//                                break;
//                            }
//                        }
//                        //??????tcp??????;????????????????????????
//                        if (isWriteFinish) {
//                            //??????????????????
//                            this.removeMessages(TIMEOUT_RECEIVE);
//                            SocketClientUtil.close(mClientUtilWriter);
//                            BtnDelayUtil.refreshFinish();
//                        }
//                    }
                    if (nowSet != null) {
                        if (nowPos >= nowSet.length - 1) {
                            nowPos = -1;
                            //??????tcp??????
                            if (mClientUtilWriter != null) {
                                mClientUtilWriter.closeSocket();
                                BtnDelayUtil.refreshFinish();
                                //??????????????????
                                this.removeMessages(TIMEOUT_RECEIVE);
                            }
                        } else {
                            nowPos = nowPos + 1;
                            if (nowSet[nowPos][2] == -1) {
                                mHandlerWrite.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                            } else {
                                sendBytes = SocketClientUtil.sendMsgToServer(mClientUtilWriter, nowSet[nowPos]);
                                LogUtil.i("????????????" + (nowPos + 1) + ":" + SocketClientUtil.bytesToHexString(sendBytes));
                            }

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


    public String getReadValueReal(int pos, int read) {
        String value = "";
        //????????????
        switch (mType) {
            case 6://PF??????????????????1-4
                double div = Arith.div(read - 10000, 10000.0, 2);
                if (div == -1) div = 1;
                value = new DecimalFormat("0.00").format(div) + mUnit[pos];
                break;
        }
        if (TextUtils.isEmpty(value)) {
            value = Arith.mul(read, mMul[pos], 2) + mUnit[pos];
        }
        return value;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        SocketClientUtil.close(mClientUtilWriter);
    }
}
