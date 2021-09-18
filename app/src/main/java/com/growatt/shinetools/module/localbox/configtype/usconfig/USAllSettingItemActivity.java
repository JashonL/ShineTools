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


    //设置项
    private String[][] titles;
    private String[][] registers;
    private int[][] itemTypes;


    //读取数据
    private int[][][] funs;
    private boolean isReceiveSucc = false;
    private int count = 0;//当前设置项的下标
    private int[][] nowReadFuns;//当前读取的数据

    //设置数据
    //设置功能码集合（功能码，寄存器，数据）
    private int[][][] funsSet;
    private int[][] nowSet;

    private float[][] mMultiples;//倍数集合
    private float[] mMul = new float[]{1, 1};
    private String[][] mUnits;
    private String[] mUnit = new String[]{"", ""};//当前单位

    private int mType = -1;
    private BaseCircleDialog dialogFragment;

    private int user_type = KEFU_USER;
    private   MenuItem item;

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
        user_type= ShineToosApplication.getContext().getUser_type();
        //设置项的下标
        mType = getIntent().getIntExtra(KEY_OF_ITEM_SETITEMSINDEX, 0);

        String bartitle = getIntent().getStringExtra("bartitle");
        if (!TextUtils.isEmpty(bartitle)) {
            tvTitle.setText(bartitle);
        }


        //标题
        titles = new String[][]{
                {getString(R.string.m402感性PF), getString(R.string.m377记忆使能)}, //感性PF         0
                {getString(R.string.m399感性载率), getString(R.string.m377记忆使能)}, //感性载率      1
                {getString(R.string.m400容性载率), getString(R.string.m377记忆使能)}, //容性载率      2
                {getString(R.string.m401容性PF), getString(R.string.m377记忆使能)}, //容性PF        3
                {getString(R.string.m387无功曲线切入电压), getString(R.string.m388无功曲线切出电压)}, //PF曲线切入切出电压        4

                {getString(R.string.android_key880) + "1", getString(R.string.android_key880) + "2",
                        getString(R.string.android_key880) + "3", getString(R.string.android_key880) + "4"}, //PF限制负载百分比点       5

                {getString(R.string.android_key883) + "1", getString(R.string.android_key883) + "2",
                        getString(R.string.android_key883) + "3", getString(R.string.android_key883) + "4"}, //PF限值       6

        };
        //对应的寄存器
        registers = new String[][]{
                {"2", "5"},//感性PF         0            --------(9 4)
                {"2", "3"},//感性载率      1             --------(9 1)
                {"2", "4"},//容性载率      2             --------(9 2)
                {"2", "5"},//容性PF        3              --------(9 3)
                {"99", "100"},//PF曲线切入切出电压        4   ------(2  4)
                {"110", "112", "114", "116"},//PF限制负载百分比点       5  (7 0)
                {"111", "113", "115", "117"}, //PF限值       6          (7 1)
        };

        //item的显示类型
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
                {{3, 0, 5}},//感性PF         0
                {{3, 0, 5}},//感性载率         1
                {{3, 0, 5}},//容性载率         2
                {{3, 0, 5}},//容性PF         3
                {{3, 99, 100}},//pf曲线切入/切出电压
                {{3, 110, 116}},//PF限制负载百分比点
                {{3, 111, 117}},//PF限值
        };

        /*设置容性PF：先5写（10000+设置值*10000）；89(Hold)写1；
          设置感性PF：先5写（10000-设置值*10000）；89(Hold)写1；
          显示值=(读取值-10000)/10000
         */
        funsSet = new int[][][]{
                {{6, 2, 1}, {6, 5, -1}, {6, 89, 1}},//感性PF         0
                {{6, 2, 1}, {6, 4, -1}, {6, 89, 5}},//感性载率
                {{6, 2, 1}, {6, 4, -1}, {6, 89, 4}},//容性载率         2
                {{6, 2, 1}, {6, 5, -1}, {6, 89, 1}},//容性PF
                {{6, 99, -1}, {6, 100, -1}},//pf曲线切入/切出电压
                {{6, 110, -1}, {6, 112, -1}, {6, 114, -1}, {6, 116, -1}},//PF限制负载百分比点1~4
                {{6, 111, -1}, {6, 113, -1}, {6, 115, -1}, {6, 117, -1}},//PF限值
        };


        try {
            mMultiples = new float[][]{
                    {0.1f, 0.1f},
                    {0.1f, 0.1f},
                    {0.1f, 0.1f},
                    {1, 1},
                    {1, 1},//pf曲线切入/切出电压
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

        if (mType==3||mType==0){
            newlist.get(1).setValue("1");
        }

        usParamsetAdapter.replaceData(newlist);

        //读取数据
        nowReadFuns = funs[mType];
        refresh();

        //需要设置的寄存器
        nowSet = funsSet[mType];
        //默认打开记忆功能
        nowSet[0][2] = 1;
    }

    /**
     * 刷新界面
     */
    private void refresh() {
        connectSendMsg();
    }

    /**
     * 真正的连接逻辑
     */
    private void connectSendMsg() {
        Mydialog.Show(this);
        connectServer();
    }

    //连接对象
    private SocketClientUtil mClientUtil;

    private void connectServer() {
        mClientUtil = SocketClientUtil.newInstance();
        if (mClientUtil != null) {
            mClientUtil.connect(mHandler);
        }
    }

    /**
     * 读取寄存器handle
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
                    text = "异常退出：" + message;
                    break;
                case SocketClientUtil.SOCKET_CLOSE:
                    text = "连接关闭";
                    break;
                case SocketClientUtil.SOCKET_OPEN:
                    text = "连接成功";
                    break;
                case SocketClientUtil.SOCKET_SEND_MSG:
                    BtnDelayUtil.sendMessage(this);
                    //设置接收消息超时时间和唯一标示
                    uuid = UUID.randomUUID().toString();
                    Message msgSend = Message.obtain();
                    msgSend.what = 100;
                    msgSend.obj = uuid;
                    mHandler.sendEmptyMessageDelayed(100, 3000);

                    String sendMsg = (String) msg.obj;
                    LogUtil.i("发送消息:" + sendMsg);
//                    text = "发送消息成功";
////                    mEditText.setText("");
//                    sb.append("Client:<br>")
//                            .append(sendMsg)
//                            .append("<br><br>");
                    break;
                case SocketClientUtil.SOCKET_RECEIVE_MSG:
                    //设置接收消息成功
                    isReceiveSucc = true;
                    //设置请求按钮可用
//                    mHandler.sendEmptyMessage(100);

                    String recMsg = (String) msg.obj;
                    text = "接收消息成功";
                    sb.append("Server:<br>")
                            .append(recMsg)
                            .append("<br><br>");
//                    Log.i("receive" ,recMsg);
                    break;
                //接收字节数组
                case SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //检测内容正确性
                        boolean isCheck = ModbusUtil.checkModbus(bytes);
                        if (isCheck) {
                            //接收正确，开始解析
                            parseMax(bytes, count);
                        }
                        if (count < nowReadFuns.length - 1) {
                            count++;
                            mHandler.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        } else {
                            count = 0;
                            //关闭连接
                            SocketClientUtil.close(mClientUtil);
                            refreshFinish();
                        }

                        LogUtil.i("接收消息:" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        count = 0;
                        //关闭连接
                        SocketClientUtil.close(mClientUtil);
                        Mydialog.Dismiss();
                    }
                    break;
                case SocketClientUtil.SOCKET_CONNECT:
                    text = "socket已连接";
                    break;
                case SocketClientUtil.SOCKET_SEND:
                    sendMsg(mClientUtil, nowReadFuns[count]);
                    break;
                case 100://恢复按钮点击
                    String myuuid = (String) msg.obj;
                    if (uuid.equals(myuuid) && !isReceiveSucc) {
                        toast("接收消息超时，请重试");
                    }
                    refreshFinish();
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
     * 根据传进来的mtype解析数据
     *
     * @param bytes
     * @param count
     */
    private void parseMax(byte[] bytes, int count) {
        //移除外部协议
        byte[] bs = RegisterParseUtil.removePro17(bytes);
        //解析int值
        switch (mType) {
            case 0:
            case 3://感性PF
                int value1 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 5, 0, 1));
                double result2 = Arith.div(value1 - 10000, 10000);
                if (result2 == -1) result2 = 1;
                usParamsetAdapter.getData().get(0).setValue(String.valueOf(value1));
                usParamsetAdapter.getData().get(0).setValueStr(String.valueOf(result2));
                break;

            case 1:
            case 2://感性载率 容性载率
                int value2 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 4, 0, 1));
                usParamsetAdapter.getData().get(0).setValue(String.valueOf(value2));
                usParamsetAdapter.getData().get(0).setValueStr(String.valueOf(value2));
                break;

            case 4://PF曲线切入  切出电压
                int value41 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 0, 0, 1));
                int value42 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 1, 0, 1));
                //更新ui
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
            case 6://PF限制负载百分比点
                int value51 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 0, 0, 1));
                int value52 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 2, 0, 1));
                int value53 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 4, 0, 1));
                int value54 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 6, 0, 1));
                //更新ui
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
     * 根据命令以及起始寄存器发送查询命令
     *
     * @param clientUtil
     * @param sends
     * @return：返回发送的字节数组
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
     * 刷新完成
     */
    private void refreshFinish() {
        Mydialog.Dismiss();
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
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
        if (itemType == UsSettingConstant.SETTING_TYPE_INPUT) {//输入类型
            if (mType == 0 || mType == 3) {//感性PF
                String title = bean.getTitle();
                String tips = getString(R.string.android_key3048) + ":" + "-1~-0.8,0.8~1";
                String unit = "";
                showInputValueDialog(title, tips, unit, value -> {

                    nowPos = -1;
                    //获取用户输入内容
                    if (TextUtils.isEmpty(value)) {
                        toast(R.string.all_blank);
                        return;
                    }

                    usParamsetAdapter.getData().get(position).setValueStr(String.valueOf(value));
                    usParamsetAdapter.getData().get(position).setValue(String.valueOf(value));
                    usParamsetAdapter.notifyDataSetChanged();

                    try {
                        double result = Double.parseDouble(value);
                        if (mType==0){
                            result = Arith.add(10000, Arith.mul(result, 10000));
                        }else {
                            result = Arith.sub(10000,Arith.mul(result,10000));
                        }
                        nowSet[1][2] = (int) result;
                        writeRegisterValue();
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        toast(getString(R.string.m363设置失败));
                    }
                });
            }


            if (mType == 1 || mType == 2) {//感性载率
                String title = bean.getTitle();
                String tips = getString(R.string.android_key3048) + ":" + "0~60";
                String unit = "%";
                showInputValueDialog(title, tips, unit, value -> {
                    nowPos = -1;
                    //获取用户输入内容
                    if (TextUtils.isEmpty(value)) {
                        toast(R.string.all_blank);
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
                        toast(getString(R.string.m363设置失败));
                    }
                });
            }


            if (mType == 4) {//PV曲线
                String title = bean.getTitle();
                String tips = getString(R.string.android_key3048) + ":" + "231V~241V";
                String unit = "V";
                showInputValueDialog(title, tips, unit, value -> {
                    nowPos = -1;
                    //获取用户输入内容
                    if (TextUtils.isEmpty(value)) {
                        toast(R.string.all_blank);
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
                        toast(getString(R.string.m363设置失败));
                    }
                });
            }


            if (mType == 5) {
                String title = bean.getTitle();
                String tips = getString(R.string.android_key3048) + ":" + "0~100" + "(" + getString(R.string.android_key3078) + 255 + ")";
                String unit = "%";
                showInputValueDialog(title, tips, unit, value -> {
                    nowPos = -1;
                    //获取用户输入内容
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
                        toast(getString(R.string.m363设置失败));
                    }
                });
            }


            if (mType == 6) {
                if (user_type==END_USER){
                    toast(R.string.android_key2099);
                    return;
                }
                String title = bean.getTitle();
                String tips = getString(R.string.android_key3048) + ":" + "-1~-0.8,0.8~1";
                String unit = "";
                showInputValueDialog(title, tips, unit, value -> {
                    nowPos = -1;
                    //获取用户输入内容
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
                        toast(getString(R.string.m363设置失败));
                    }
                });
            }

        } else if (itemType == UsSettingConstant.SETTING_TYPE_SELECT) {//选择类型

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
                    }, Gravity.CENTER,false);
        }

    }


    @Override
    public void oncheck(boolean check, int position) {
        int value = check ? 1 : 0;
        if (mType == 0 || mType == 1 || mType == 2 || mType == 3) {//感性PF 感性载率 容性载率  容性PF
            nowPos = -1;
            nowSet[0][2] = value;
            //设置记忆使能
            writeRegisterValue();
        }

        usParamsetAdapter.getData().get(position).setValue(String.valueOf(value));
        usParamsetAdapter.notifyDataSetChanged();
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
    //当前发送的字节数组:{6, 2, 1}, {6, 4, -1}, {6, 89, 5}
    private byte[] sendBytes;
    //当前设置项
    private int nowPos = -1;
    private boolean isWriteFinish;
    Handler mHandlerWrite = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
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
//                                LogUtil.i("发送写入" + (i + 1) + ":" + SocketClientUtil.bytesToHexString(sendBytes));
//                                //发送完将值设置为-1
//                                nowSet[i][2] = -1;
//                                break;
//                            }
//                        }
//                        //关闭tcp连接;判断是否请求完毕
//                        if (isWriteFinish) {
//                            //移除接收超时
//                            this.removeMessages(TIMEOUT_RECEIVE);
//                            SocketClientUtil.close(mClientUtilWriter);
//                            BtnDelayUtil.refreshFinish();
//                        }
//                    }
                    if (nowSet != null) {
                        if (nowPos >= nowSet.length-1) {
                            nowPos = -1;
                            //关闭tcp连接
                            if (mClientUtilWriter != null) {
                                mClientUtilWriter.closeSocket();
                                BtnDelayUtil.refreshFinish();
                                //移除接收超时
                                this.removeMessages(TIMEOUT_RECEIVE);
                            }
                        } else {
                            nowPos = nowPos + 1;
                            if (nowSet[nowPos][2]==-1){
                                mHandlerWrite.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                            }else {
                                sendBytes = SocketClientUtil.sendMsgToServer(mClientUtilWriter, nowSet[nowPos]);
                                LogUtil.i("发送写入" + (nowPos + 1) + ":" + SocketClientUtil.bytesToHexString(sendBytes));
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
                            toast(getString(R.string.all_success));
                            //继续发送设置命令
                            mHandlerWrite.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        } else {
                            toast(getString(R.string.all_failed));
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
                    BtnDelayUtil.dealTLXBtnWrite(this, what, mContext, toolbar);
                    break;
            }
        }
    };


    public String getReadValueReal(int pos, int read) {
        String value = "";
        //特殊处理
        switch (mType) {
            case 6://PF限制功率因数1-4
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
        mHandler=null;
        SocketClientUtil.close(mClientUtilWriter);
    }
}
