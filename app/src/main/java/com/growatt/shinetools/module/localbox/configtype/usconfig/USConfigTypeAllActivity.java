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
    //设置项
    private String[] titls;
    private String[] registers;

    //读取数据
    private int[][][] funs;
    private boolean isReceiveSucc = false;
    private int count = 0;//当前设置项的下标

    //设置数据
    //设置功能码集合（功能码，寄存器，数据）
    private int[][][] funsSet;
    private int[][] nowSet;
    private int[] setItem;
    private String[] antiReflux;//防逆流选项
    private float[] mMultiples;//倍数集合
    private float mMul = 1;//当前倍数
    private String[] mUnits;
    private String mUnit = "";//当前单位

    private String[] frequency;//防逆流选项
    private String[] voltage;//防逆流选项

    public static final String KEY_OF_ITEM_SETITEMSINDEX = "KEY_OF_ITEM_SETITEMSINDEX";
    private int setItemsIndex;

    private BaseCircleDialog dialogFragment;


    //                 {3, 3, 3}//有功功率百分比3
    //                , {3, 4, 4}//无功功率百分比4
    //                , {3, 5, 5}//功率因子5
    //                , {3, 8, 8}//pv电压6
    //                , {3, 91, 91}//过频降额起点9 ----------4
    //                , {3, 92, 92}//负载限制10
    //                , {3, 107, 107}//无功延时11
    //                , {3, 108, 108}//过频降额延时12
    //                , {3, 109, 109}//曲线Q最大值13
    //                //参数设置
    //                , {3, 17, 17}//2启动电压 --------9
    //                , {3, 18, 18}//3启动时间
    //                , {3, 19, 19}//4故障回复后重启延迟时间
    //                , {3, 30, 30}//5通讯地址
    //                , {3, 51, 51}//7系统一周
    //                , {3, 80, 80}//8 ac 10分钟保护值 ------------14
    //                , {3, 81, 81}//9 pv 电压高故障
    //                , {3, 88, 88}//10 modbus版本
    //                , {3, 203, 203}//13 pid工作电压
    //                 /*-----------------TLX设置 pos=18-------------------------*/
    //                , {3, 123, 123}//防逆流功率百分比
    //                , {3, 3000, 3000}//防逆流失效后默认百分比  ----------------19
    //                , {3, 3017, 3017}//干接点开通的功率百分比
    //                , {3, 3080, 3080}//离网电压
    //                , {3, 3081, 3081}//离网频率
    //                , {3, 3030, 3030}//cv电压23
    //                , {3, 3024, 3024}//cc电流24  ---------------24
    //                , {3, 3047, 3047}//充电功率百分比
    //                , {3, 3048, 3048}//充电停止soc
    //                , {3, 3036, 3036}//放电功率百分比
    //                , {3, 3037, 3037}//放电停止soc 28
    //                 /*-----------------其他-------------------------*/
    //                , {3, 1, 1}//安规功能使能：max 和  tl-x   pos=29   --------------29
    //                , {3, 3019, 3019}//干接点关闭功率百分比
    //                , {3, 539, 539}//过温降载
    //
    //                , {3, 544, 544}//阈值1
    //                , {3, 545, 545}//阈值2
    //                , {3, 546, 546}//阈值3
    //                , {3, 547, 547}//FFT

    //这个值跟上面的顺序对应

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
        item.setTitle(R.string.m370读取);
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
        //1.获取用户类型
        user_type= ShineToosApplication.getContext().getUser_type();
        //设置项的下标
        setItemsIndex = getIntent().getIntExtra(KEY_OF_ITEM_SETITEMSINDEX, 0);
        switch (setItemsIndex) {
            case 0://有功功率百分比
                titls = new String[]{getString(R.string.m398有功功率百分比), getString(R.string.android_key836)};
                registers = new String[]{"(3)", "(3)"};
                break;
            case 1://防逆流设置
                titls = new String[]{getString(R.string.m防逆流设置),
                        getString(R.string.m防逆流功率百分比),
                        getString(R.string.android_key748)};
                registers = new String[]{"(122)", "(123)", "(3000)"};
                break;
            case 2://干接点设置
                titls = new String[]{getString(R.string.m干接点状态)
                        , getString(R.string.m干接点开通的功率百分比), getString(R.string.m干接点关闭功率百分比)};
                registers = new String[]{"(3016)", "(3017)", "(3019)"};
                break;
            case 3://离网功能
                if (user_type == END_USER) {
                    titls = new String[]{getString(R.string.m离网功能使能)};
                    registers = new String[]{"(3079)"};
                }else {
                    titls = new String[]{getString(R.string.m离网使能)
                            , getString(R.string.m离网频率), getString(R.string.m离网电压)};
                    registers = new String[]{"(3079)", "(3081)", "(3080)"};
                }

                break;
            case 4://AFCI
                titls = new String[]{getString(R.string.android_key2396), getString(R.string.AFCI阈值)+1, getString(R.string.AFCI阈值)+2,
                        getString(R.string.AFCI阈值)+3, getString(R.string.FFT最大累计次数), getString(R.string.AFCI曲线扫描)};
                registers = new String[]{"", "(544)", "(545)", "(546)", "(547)", ""};
                break;
        }


        List<USDebugSettingBean> newlist = new ArrayList<>();
        for (int i = 0; i < titls.length; i++) {
            USDebugSettingBean bean = new USDebugSettingBean();
            bean.setTitle(titls[i]);
            int itemType = 0;
            switch (setItemsIndex) {
                case 0://有功功率百分比
                    if (i == 0) {
                        itemType = UsSettingConstant.SETTING_TYPE_INPUT;
                    } else {
                        itemType = UsSettingConstant.SETTING_TYPE_SWITCH;
                    }
                    break;
                case 1://防逆流设置
                    if (i == 0) {
                        itemType = UsSettingConstant.SETTING_TYPE_SELECT;
                    } else {
                        itemType = UsSettingConstant.SETTING_TYPE_INPUT;
                    }
                    break;
                case 2://干接点设置
                    if (i == 0) {
                        itemType = UsSettingConstant.SETTING_TYPE_SWITCH;
                    } else {
                        itemType = UsSettingConstant.SETTING_TYPE_INPUT;
                    }
                    break;
                case 3://离网功能
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
                {{3, 0, 5}},//有功功率百分比
                {{3, 122, 122}, {3, 123, 123}, {3, 3000, 3000}},//防逆流设置3个选项
                {{3, 3016, 3016}, {3, 3017, 3017}, {3, 3019, 3019}},//干接点设置3个选项
                {{3, 3079, 3079}, {3, 3081, 3081}, {3, 3080, 3080}},//离网功能
                {{3,541,541},{3, 544, 544}, {3, 545, 545}, {3, 546, 546},{3,547,547}},//AFCI 6项
        };


        funsSet = new int[][][]{
                {{6, 2, 1}, {6, 3, -1}},//有功功率百分比
                {{6, 122, -1}, {6, 123, -1}, {6, 3000, -1}},//防逆流设置3个选项,后改成2个
                {{6, 3016, -1}, {6, 3017, -1}, {6, 3019, -1}},//干接点设置3个选项
                {{6, 3079, -1}, {6, 3081, -1}, {6, 3080, -1}},//离网功能
                {{6,541,-1},{6, 544, -1}, {6, 545, -1}, {6, 546, -1}, {6, 547, -1}},//AFCI 6项
        };
        nowSet = funsSet[setItemsIndex];

        antiReflux = new String[]{getString(R.string.android_key2885), getString(R.string.android_key2886)};
        frequency = new String[]{"50Hz", "60Hz"};
        voltage = new String[]{"230V", "208V", "240V"};
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
    /**
     * 连接处理器
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
                        if (count < funs[setItemsIndex].length - 1) {
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
                    if (count < funs[setItemsIndex].length) {
                        sendMsg(mClientUtil, funs[setItemsIndex][count]);
                    }
                    break;
                case 100://恢复按钮点击
                    String myuuid = (String) msg.obj;
                    if (uuid.equals(myuuid) && !isReceiveSucc) {
                        toast("接收消息超时，请重试");
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


    /**
     * 解析数据
     *
     * @param bytes
     * @param count
     */
    private void parseMax(byte[] bytes, int count) {
        //移除外部协议
        byte[] bs = RegisterParseUtil.removePro17(bytes);
        //解析int值
        switch (setItemsIndex) {
            case 0://有功功率百分比(读取记忆功能)
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
            case 1://防逆流设置
                if (count == 0) {//防逆流设置
                    int value1 = MaxWifiParseUtil.obtainValueOne(bs);
                    usParamsetAdapter.getData().get(0).setValue(String.valueOf(value1));
                    String sValue = String.valueOf(value1);
                    if (antiReflux.length > value1) {
                        sValue = antiReflux[value1];
                    }
                    usParamsetAdapter.getData().get(0).setValueStr(sValue);
                } else if (count == 1) {//防逆流功率百分比
                    mType = 18;
                    try {
                        mMul = mMultiples[18];
                        mUnit = mUnits[18];
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //解析int值
                    int value2 = MaxWifiParseUtil.obtainValueOne(bs);
                    usParamsetAdapter.getData().get(1).setValue(String.valueOf(value2));
                    usParamsetAdapter.getData().get(1).setValueStr(getReadValueReal(value2));



                } else if (count == 2) {//防逆流失效后默认功率百分比

                    mType = 19;
                    try {
                        mMul = mMultiples[19];
                        mUnit = mUnits[19];
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //解析int值
                    int value2 = MaxWifiParseUtil.obtainValueOne(bs);
                    usParamsetAdapter.getData().get(2).setValue(String.valueOf(value2));
                    usParamsetAdapter.getData().get(2).setValueStr(getReadValueReal(value2));


                }
                break;
            case 2://干接点
                if (count == 0) {//干接点状态
                    int value1 = MaxWifiParseUtil.obtainValueOne(bs);
                    usParamsetAdapter.getData().get(0).setValue(String.valueOf(value1));
                } else if (count == 1) {//干接点开通的功率百分比
                    mType = 20;
                    try {
                        mMul = mMultiples[3];
                        mUnit = mUnits[3];
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //解析int值
                    int value2 = MaxWifiParseUtil.obtainValueOne(bs);
                    usParamsetAdapter.getData().get(1).setValue(String.valueOf(value2));
                    usParamsetAdapter.getData().get(1).setValueStr(getReadValueReal(value2));



                } else if (count == 2) {//干接点关闭功率百分比
                    mType = 30;
                    try {
                        mMul = mMultiples[3];
                        mUnit = mUnits[3];
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //解析int值
                    int value2 = MaxWifiParseUtil.obtainValueOne(bs);
                    usParamsetAdapter.getData().get(2).setValue(String.valueOf(value2));
                    usParamsetAdapter.getData().get(2).setValueStr(getReadValueReal(value2));

                }

                break;


            case 3://离网功能

                if (count == 0) {//离网使能
                    int value1 = MaxWifiParseUtil.obtainValueOne(bs);
                    usParamsetAdapter.getData().get(0).setValue(String.valueOf(value1));
                } else if (count == 1) {//离网频率
                    //解析int值
                    int value2 = MaxWifiParseUtil.obtainValueOne(bs);
                    usParamsetAdapter.getData().get(1).setValue(String.valueOf(value2));

                    String sValue = String.valueOf(value2);
                    if (frequency.length > value2) {
                        sValue = frequency[value2];
                    }
                    usParamsetAdapter.getData().get(1).setValueStr(sValue);

                } else if (count == 2) {//离网电压
                    //解析int值
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
                if (count == 0) {//AFCI使能
                    int value1 = MaxWifiParseUtil.obtainValueOne(bs);
                    usParamsetAdapter.getData().get(0).setValue(String.valueOf(value1));
                } else if (count == 1) {//AFCI阈值1
                    mType = 32;
                    try {
                        mMul = mMultiples[mType];
                        mUnit = mUnits[mType];
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //解析int值
                    int value2 = MaxWifiParseUtil.obtainValueOne(bs);
                    usParamsetAdapter.getData().get(1).setValue(String.valueOf(value2));
                    usParamsetAdapter.getData().get(1).setValueStr(getReadValueReal(value2));

                } else if (count == 2) {//AFCI阈值2
                    mType = 33;
                    try {
                        mMul = mMultiples[mType];
                        mUnit = mUnits[mType];
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //解析int值
                    int value2 = MaxWifiParseUtil.obtainValueOne(bs);
                    usParamsetAdapter.getData().get(2).setValue(String.valueOf(value2));
                    usParamsetAdapter.getData().get(2).setValueStr(getReadValueReal(value2));

                }else if (count==3){//AFCI阈值3
                    mType = 34;
                    try {
                        mMul = mMultiples[mType];
                        mUnit = mUnits[mType];
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //解析int值
                    int value2 = MaxWifiParseUtil.obtainValueOne(bs);
                    usParamsetAdapter.getData().get(3).setValue(String.valueOf(value2));
                    usParamsetAdapter.getData().get(3).setValueStr(getReadValueReal(value2));
                }else if (count==4){//FFT值超过阈值最大累计
                    mType = 35;
                    try {
                        mMul = mMultiples[mType];
                        mUnit = mUnits[mType];
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //解析int值
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

        //其他特殊处理
        switch (mType) {
            case 13:
                value = getWeek(read);
                break;//系统一周
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
                //读取寄存器的值
                refresh();
                break;
        }
        return true;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        switch (setItemsIndex) {
            case 0://有功功率百分比
                if (position == 0) {
                    String title = getString(R.string.m398有功功率百分比);
                    String tips = getString(R.string.android_key3048) + ":" + "0~100";
                    String unit = "%";
                    showInputValueDialog(title, tips, unit, value -> {
                        //设置功率百分比
                        double result = Double.parseDouble(value);
                        String pValue = value + "%";
                        usParamsetAdapter.getData().get(position).setValueStr(pValue);
                        usParamsetAdapter.getData().get(position).setValue(String.valueOf(result));
                        usParamsetAdapter.notifyDataSetChanged();

                        //设置
                        setItem = nowSet[1];
                        setItem[2] = (int) result;
                        writeRegisterValue();
                    });
                }
                break;
            case 1://防逆流设置
                if (position == 0) {//防逆流设置
                    CircleDialogUtils.showCommentItemDialog(this, getString(R.string.android_key1809),
                            Arrays.asList(antiReflux), Gravity.CENTER, new OnLvItemClickListener() {
                                @Override
                                public boolean onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                                    if (antiReflux != null && antiReflux.length > pos) {
                                        String text = antiReflux[pos];
                                        usParamsetAdapter.getData().get(position).setValueStr(text);
                                        usParamsetAdapter.getData().get(position).setValue(String.valueOf(pos));
                                        usParamsetAdapter.notifyDataSetChanged();
                                        //去设置
                                        setItem = nowSet[0];
                                        setItem[2] = pos;
                                        writeRegisterValue();
                                    }
                                    return true;
                                }
                            }, null);


                } else if (position == 1) {
                    String title = getString(R.string.m防逆流功率百分比);
                    String tips = getString(R.string.android_key3048) + ":" + "0~100";
                    String unit = "%";
                    showInputValueDialog(title, tips, unit, value -> {
                        //设置功率百分比
                        double result = Double.parseDouble(value);
                        String pValue = value + "%";
                        usParamsetAdapter.getData().get(position).setValueStr(pValue);
                        usParamsetAdapter.getData().get(position).setValue(String.valueOf(result));
                        usParamsetAdapter.notifyDataSetChanged();

                        //设置
                        setItem = nowSet[1];
                        setItem[2] = (int) result;
                        writeRegisterValue();
                    });
                } else if (position == 2) {
                    String title = getString(R.string.android_key748);
                    String tips = getString(R.string.android_key3048) + ":" + "0~100";
                    String unit = "%";
                    showInputValueDialog(title, tips, unit, value -> {
                        //设置功率百分比
                        double result = Double.parseDouble(value);
                        String pValue = value + "%";
                        usParamsetAdapter.getData().get(position).setValueStr(pValue);
                        usParamsetAdapter.getData().get(position).setValue(String.valueOf(result));
                        usParamsetAdapter.notifyDataSetChanged();

                        //设置
                        setItem = nowSet[2];
                        setItem[2] = (int) result;
                        writeRegisterValue();
                    });
                }
                break;
            case 2://干接点设置
                if (position == 1) {
                    String title = getString(R.string.m干接点开通的功率百分比);
                    String tips = getString(R.string.android_key3048) + ":" + "0~1000";
                    String unit = "%";
                    showInputValueDialog(title, tips, unit, value -> {
                        //设置功率百分比
                        double result = Double.parseDouble(value);
                        String pValue = value + "%";
                        usParamsetAdapter.getData().get(position).setValueStr(pValue);
                        usParamsetAdapter.getData().get(position).setValue(String.valueOf(result));
                        usParamsetAdapter.notifyDataSetChanged();

                        //设置
                        setItem = nowSet[1];
                        setItem[2] = (int) result;
                        LogUtil.d("干接点设置1："+Arrays.toString(setItem));
                        writeRegisterValue();
                    });
                } else if (position == 2) {
                    String title = getString(R.string.m干接点关闭功率百分比);
                    String tips = getString(R.string.android_key3048) + ":" + "0~1000";
                    String unit = "%";
                    showInputValueDialog(title, tips, unit, value -> {
                        //设置功率百分比
                        double result = Double.parseDouble(value);
                        String pValue = value + "%";
                        usParamsetAdapter.getData().get(position).setValueStr(pValue);
                        usParamsetAdapter.getData().get(position).setValue(String.valueOf(result));
                        usParamsetAdapter.notifyDataSetChanged();

                        //设置
                        setItem = nowSet[2];
                        setItem[2] = (int) result;
                        LogUtil.d("干接点设置1："+Arrays.toString(setItem));
                        writeRegisterValue();
                    });
                }
                break;
            case 3://离网功能
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
                                    //去设置
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
                    String title = getString(R.string.FFT最大累计次数);
                    String tips = getString(R.string.android_key3048) + ":" + "0~255";
                    String unit = "";
                    showInputValueDialog(title, tips, unit, value -> {
                        //设置功率百分比
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
                    intent.putExtra("title", String.format("%s%s",getString(R.string.AFCI曲线扫描),""));
                    ActivityUtils.startActivity(USConfigTypeAllActivity.this,intent,false);
                }
                break;
        }
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
    Handler mHandlerWrite = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    if (setItem != null && setItem[2] != -1) {
                        BtnDelayUtil.sendMessageWrite(this);
                        sendBytes = sendMsg(mClientUtilWriter, setItem);
                        LogUtil.i("发送写入：" + SocketClientUtil.bytesToHexString(sendBytes));
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
                    case 1://记忆
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
                    case 0://干接点状态
                        setItem = nowSet[0];
                        setItem[2] = value;
                        writeRegisterValue();
                        break;
                }
                usParamsetAdapter.getData().get(position).setValue(String.valueOf(value));
                usParamsetAdapter.notifyDataSetChanged();
                break;
            case 3://离网功能
                switch (position) {
                    case 0://干接点状态
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
        String title = getString(R.string.AFCI阈值)+position;
        String tips = getString(R.string.android_key3048) + ":" + "0~65000"+"("+getString(R.string.AFCI阈值)+1
                + "<" +getString(R.string.AFCI阈值)+2+"<"+getString(R.string.AFCI阈值)+3+")";
        String unit = "";
        showInputValueDialog(title, tips, unit, value -> {
            //设置功率百分比
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
