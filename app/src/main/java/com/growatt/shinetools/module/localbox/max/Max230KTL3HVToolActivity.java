package com.growatt.shinetools.module.localbox.max;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.growatt.shinetools.R;
import com.growatt.shinetools.adapter.MaxMainChildAdapter;
import com.growatt.shinetools.adapter.TLXHToolEleAdapter;
import com.growatt.shinetools.adapter.TLXHToolPowerAdapter;
import com.growatt.shinetools.adapter.UsParamsetAdapter;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.modbusbox.bean.MaxDataBean;
import com.growatt.shinetools.modbusbox.bean.MaxDataDeviceBean;
import com.growatt.shinetools.module.localbox.max.bean.MaxChildBean;
import com.growatt.shinetools.module.localbox.max.bean.TLXHEleBean;
import com.growatt.shinetools.module.localbox.max.bean.UsToolParamBean;
import com.growatt.shinetools.module.localbox.max.config.MaxBasicSettingActivity;
import com.growatt.shinetools.module.localbox.max.config.MaxGridCodeSettingActivity;
import com.growatt.shinetools.module.localbox.max.config.MaxQuicksettingActivity;
import com.growatt.shinetools.module.localbox.max.config.MaxSystemConfigActivity;
import com.growatt.shinetools.module.localbox.ustool.USAdvanceSetActivity;
import com.growatt.shinetools.module.localbox.ustool.USDeviceInfoActivity;
import com.growatt.shinetools.module.localbox.ustool.USFaultDetailActivity;
import com.growatt.shinetools.module.localbox.ustool.errorcode.ErrorCode;
import com.growatt.shinetools.utils.ActivityUtils;
import com.growatt.shinetools.utils.BtnDelayUtil;
import com.growatt.shinetools.utils.CircleDialogUtils;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.Mydialog;
import com.growatt.shinetools.widget.GridDivider;
import com.growatt.shinetools.widget.LinearDivider;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;

import static com.growatt.shinetools.modbusbox.SocketClientUtil.SOCKET_AUTO_REFRESH;
import static com.growatt.shinetools.modbusbox.SocketClientUtil.SOCKET_RECEIVE_BYTES;
import static com.growatt.shinetools.modbusbox.SocketClientUtil.SOCKET_SEND;

public class Max230KTL3HVToolActivity extends BaseActivity implements Toolbar.OnMenuItemClickListener,
        BaseQuickAdapter.OnItemClickListener, View.OnClickListener{

    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.header_title)
    LinearLayout headerTitle;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_status)
    TextView tvStatus;

    private int scroolD = 100;
    private String noteStartStr;
    private String noteStopStr;
    private String errNode;
    String[] pidStatusStrs;
    //标题状态
    String[] statusTitles;
    private int [] statusColors;
    private int [] drawableStatus;




    private String[] c1Title2;
    String[] c2Title2;
    String[] c3Title2;
    String[] c34Title2;
    String[] c4Title2;
    String[] c6Title1;
    String[] c5Title1;
    String[] c4Title1;


    /**
     * 功率recyclerview
     */
    private TLXHToolPowerAdapter mPowerAdapter;
    private List<TLXHEleBean> mPowerList;
    private RecyclerView mPowerRecycler;

    private TextView tvErrH1;
    private TextView tvWarnH1;

    private MenuItem item;
    //开启自动刷新
    private boolean isAutoRefresh;
    //是否需要自动刷新
    private boolean needFresh;
    private boolean isReceiveSucc = false;
    //连接对象
    private SocketClientUtil mClientUtil;
    //所有本地获取数据集合

    private long startTime;
    private boolean btnClick = true;

    private MaxDataBean mMaxData = new MaxDataBean();
    private int count = 0;
    int[][] funs = {{3, 0, 124}, {4, 0, 99}, {4, 100, 199}, {4, 875, 999}};
    //读机器型号功能码
    int[] deviceTypeFun = {3,125,249};

    //自动刷新时间
    private int autoTime = 3000;



    //表格列表
    private LinearLayoutManager mLayoutManager;
    private List<MaxChildBean> mGridList;
    private View header;
    private MaxMainChildAdapter mAdapter;



    /**
     * 发电量recyclerview
     */
    private String[] eleTitles;
    private int[] powerResId;
    private int[] eleResId;
    private TLXHToolEleAdapter mEleAdapter;
    private List<TLXHEleBean> mEleList;
    private RecyclerView mEleRecycler;
    private TextView tvDetial;
    private ImageView ivDetail;



    //设置项
    private UsParamsetAdapter usParamsetAdapter;
    private List<UsToolParamBean> mSettingList=new ArrayList<>();
    private RecyclerView mRvParamset;

    int autoCount = 0;


    private CardView cvWarning;


    //自动刷新功能码
    int[][] autoFun = {{4,0,124},{4,125,249}, {4, 875, 999}};



    @Override
    protected int getContentView() {
        return R.layout.activity_max230_ktl3_hv_tool;
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initData() {
        initString();

        toolbar.setNavigationIcon(R.drawable.icon_return);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CircleDialogUtils.showCommentDialog(Max230KTL3HVToolActivity.this, getString(R.string.退出设置提示),
                        getString(R.string.m设置未保存是否退出), getString(R.string.android_key1935),
                        getString(R.string.android_key2152), Gravity.CENTER, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();

                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });

            }
        });


//        tvTitle.setText(R.string.m240本地调试工具);
        tvTitle.setText("MAX 230KTL3 HV");
        toolbar.inflateMenu(R.menu.comment_right_menu);
        item = toolbar.getMenu().findItem(R.id.right_action);
        item.setTitle(noteStartStr);
        toolbar.setOnMenuItemClickListener(this);

        //主题列表
        mGridList = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MaxMainChildAdapter(R.layout.item_max_childrv, mGridList);
        header = LayoutInflater.from(this).inflate(R.layout.header_max_header_view, (ViewGroup) recyclerView.getParent(), false);
        mAdapter.addHeaderView(header);
        recyclerView.setAdapter(mAdapter);


        //发电
        initRecyclerViewEle();

        //告警
        initRecyclerViewPower();

        //设置项
        initSettingRecycleView();


        initListener();


        Mydialog.Show(mContext);
        //读取寄存器的值
        refresh();
    }


    private void initListener() {
        initOnclick(tvDetial, ivDetail, cvWarning);
    }




    private void initOnclick(View... views) {
        if (views != null) {
            for (View view : views) {
                view.setOnClickListener(this);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == ivDetail) {
            jumpMaxSet(MaxChartEnergyActivity.class, getString(R.string.m201电量));
        } else if (v == cvWarning) {
            jumpErrorWarnSet();
        }

    }





    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.right_action:
                //读取寄存器的值
                LogUtil.i("是否在自动刷新：" + isAutoRefresh);
                if (!isAutoRefresh) {
                    Mydialog.Show(mContext);
                    needFresh = true;
                    //读取寄存器的值
                    refresh();
                } else {
                    stopRefresh();
                }
                break;
        }
        return false;
    }





    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        String title1 = "";
        Class clazz = null;

        switch (position) {
            case 0:
                clazz = MaxQuicksettingActivity.class;
                break;
            case 1:
                clazz = MaxSystemConfigActivity.class;
                break;
            case 2:
                clazz = MaxBasicSettingActivity.class;
                break;
            case 3:
                clazz = MaxCheck1500VActivity.class;
                break;
            case 4:
                clazz = MaxGridCodeSettingActivity.class;
                break;
            case 5:
                clazz = USAdvanceSetActivity.class;
                break;

            case 6:
                clazz = USAdvanceSetActivity.class;
                title1 = getString(R.string.高级设置);
                break;

            case 7:
                clazz = USDeviceInfoActivity.class;
                break;


            default:
                clazz = null;
                break;
        }

        try {
            UsToolParamBean item = usParamsetAdapter.getItem(position);
            title1 = item.getTitle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final String title = title1;
        if (clazz == null) return;
        jumpMaxSet(clazz, title);

    }




    /**
     * 跳转到故障页面
     */
    private void jumpErrorWarnSet() {
        stopRefresh();
        int errCode = mMaxData.getErrCode();
        int warmCode = mMaxData.getWarmCode();
        int errCodeSecond = mMaxData.getErrCodeSecond();
        int warmCodeSecond = mMaxData.getWarmCodeSecond();
        Intent intent = new Intent(mContext, USFaultDetailActivity.class);
        intent.putExtra(ErrorCode.KEY_US_ERROR, errCode);
        intent.putExtra(ErrorCode.KEY_US_WARNING, warmCode);
        intent.putExtra(ErrorCode.KEY_US_SECOND_ERROR, errCodeSecond);
        intent.putExtra(ErrorCode.KEY_US_SECOND_WARNING, warmCodeSecond);
        ActivityUtils.startActivity(this, intent, false);
    }




    /**
     * 发电列表
     */
    private void initRecyclerViewEle() {
        mEleList = new ArrayList<>();
        mEleRecycler = header.findViewById(R.id.rvEle);
        tvDetial = header.findViewById(R.id.tvDetial);
        ivDetail = header.findViewById(R.id.iv_detail);
        tvDetial.setText(String.format("%s>", getString(R.string.commondata_title)));
        mEleRecycler.setLayoutManager(new LinearLayoutManager(this));
        LinearDivider linearDivider = new LinearDivider(this, LinearLayoutManager.VERTICAL,
                1, ContextCompat.getColor(this, R.color.gray_aaaaaa));
        mEleAdapter = new TLXHToolEleAdapter(R.layout.item_tlxh_tool_ele_v2, mEleList);
        mEleRecycler.addItemDecoration(linearDivider);
        mEleRecycler.setAdapter(mEleAdapter);
        initEleDatas(eleTitles, null, null, mEleAdapter);
    }


    /**
     * 设置项列表
     */
    private void initSettingRecycleView() {
        mRvParamset = header.findViewById(R.id.rv_setting);
        mRvParamset.setLayoutManager(new GridLayoutManager(this, 3));
        usParamsetAdapter = new UsParamsetAdapter(R.layout.item_us_setting, mSettingList);
        int div = (int) getResources().getDimension(R.dimen.dp_20);
        GridDivider gridDivider = new GridDivider(ContextCompat.getColor(this, R.color.white), div, div);
        mRvParamset.addItemDecoration(gridDivider);
        mRvParamset.setAdapter(usParamsetAdapter);
        usParamsetAdapter.setOnItemClickListener(this);




        int[] res = new int[]{
                R.drawable.quickly, R.drawable.system_config,
                R.drawable.charge_manager, R.drawable.smart_check, R.drawable.param_setting,
                R.drawable.advan_setting, R.drawable.device_info
        };
        String[] title = new String[]{
                getString(R.string.快速设置) , getString(R.string.android_key3052), getString(R.string.basic_setting)
               , getString(R.string.m285智能检测), getString(R.string.m284参数设置)
                , getString(R.string.m286高级设置), getString(R.string.m291设备信息)
        };
        List<UsToolParamBean> usSetItems = new ArrayList<>();
        for (int i = 0; i < title.length; i++) {
            UsToolParamBean bean = new UsToolParamBean();
            bean.setIcon(res[i]);
            bean.setTitle(title[i]);
            usSetItems.add(bean);
        }
        usParamsetAdapter.replaceData(usSetItems);

    }



    private void initRecyclerViewPower() {
        tvErrH1 = header.findViewById(R.id.tv_fault_value);
        tvWarnH1 = header.findViewById(R.id.tv_warn_value);
        cvWarning = header.findViewById(R.id.cvWarning);

    }


    private void initEleDatas(@NonNull String[] titles, List<String> todays, List<String> totals, TLXHToolEleAdapter adapter) {
        List<TLXHEleBean> newList = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            TLXHEleBean bean = new TLXHEleBean();
            //标题
            String todayEle = "";
            String totalEle = "";
            String unit = "";
            int contentColor = -1;

            unit = "kWh";
            contentColor = R.color.title_bg_white;
            if (todays == null) {
                todayEle = "--";
            } else {
                todayEle = todays.get(i);
            }
            if (totals == null) {
                totalEle = "--";
            } else {
                totalEle = totals.get(i);
            }
            bean.setTotalEle(totalEle);
            bean.setTodayEle(todayEle);
            bean.setContentColor(contentColor);
            bean.setTitle(titles[i]);
            bean.setDrawableResId(eleResId[i]);
            bean.setUnit(unit);
            newList.add(bean);
        }
        adapter.replaceData(newList);
    }


    private void initPowerDatas(@NonNull String[] titles, List<String> contents, TLXHToolPowerAdapter adapter) {
        List<TLXHEleBean> newList = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            TLXHEleBean bean = new TLXHEleBean();
            bean.setDrawableResId(powerResId[i]);
            bean.setTitle(titles[i]);
            if (contents == null) {
                bean.setContent("--");
            } else {
                bean.setContent(contents.get(i));
            }
            newList.add(bean);
        }
        adapter.replaceData(newList);
    }




    private void initString() {
        scroolD = getResources().getDimensionPixelSize(R.dimen.dp_50);
        noteStartStr = getString(R.string.m268自动刷新);
        noteStopStr = getString(R.string.m280停止刷新);
        errNode = getString(R.string.m290请先读取故障信息);
        pidStatusStrs = new String[]{
                "", getString(R.string.all_Waiting), getString(R.string.all_Normal), getString(R.string.m故障)
        };
        statusTitles = new String[]{
                getString(R.string.all_Waiting), getString(R.string.all_Normal),
                getString(R.string.m226升级中), getString(R.string.m故障)
        };

        statusColors=new int[]{
                R.color.color_status_wait,R.color.color_status_grid,R.color.color_status_fault,R.color.color_status_upgrade
        };

        drawableStatus=new int[]{
                R.drawable.circle_wait,R.drawable.circle_grid,R.drawable.circle_fault, R.drawable.circle_upgrade
        };



        c1Title2 = new String[]{
                String.format("%s(V)", getString(R.string.m318电压)),
                String.format("%s(A)", getString(R.string.m319电流))
        };
        c2Title2 = new String[]{
                String.format("%s(V)", getString(R.string.m318电压)),
                String.format("%s(A)", getString(R.string.m319电流))
        };
        c3Title2 = new String[]{
                String.format("%s(V)", getString(R.string.m318电压)),
                String.format("%s(Hz)", getString(R.string.m321频率)),
                String.format("%s(A)", getString(R.string.m319电流)),
                String.format("%s(W)", getString(R.string.m320功率)),
                "PF"
        };
        c34Title2 = new String[]{
                String.format("%s(A)", getString(R.string.mCT侧电流)),
                String.format("%s(Var)", getString(R.string.mCT侧无功)),
                String.format("%s(A)", getString(R.string.mCT侧谐波量)),
                String.format("%s(Var)", getString(R.string.m补偿无功量)),
                String.format("%s(A)", getString(R.string.m补偿谐波量)),
                getString(R.string.mSVG工作状态),
        };
        c4Title2 = new String[]{
                String.format("%s(V)", getString(R.string.m318电压)),
                String.format("%s(mA)", getString(R.string.m319电流))
        };
        c6Title1 = new String[]{
                getString(R.string.m305并网倒计时), getString(R.string.m306功率百分比),
                "ISO",
                getString(R.string.m307内部环境温度), getString(R.string.m308Boost温度),
                getString(R.string.m309INV温度),
                "+Bus", "-Bus",
                getString(R.string.m310PID故障信息), getString(R.string.m311PID状态)
        };
        c5Title1 = new String[]{
                getString(R.string.m312厂商信息), getString(R.string.m313机器型号),
                getString(R.string.dataloggers_list_serial), getString(R.string.m314Model号),
//                getString(R.string.m315固件外部版本),  getString(R.string.m316固件内部版本)
                getString(R.string.m控制软件版本), getString(R.string.m通信软件版本)
        };


        c4Title1 = new String[]{
                getString(R.string.fly_cap_volt) + "1", getString(R.string.fly_cap_volt) + "2", getString(R.string.fly_cap_volt) + "3", getString(R.string.fly_cap_volt) + "4",
                getString(R.string.fly_cap_volt) + "5", getString(R.string.fly_cap_volt) + "6", getString(R.string.fly_cap_volt) + "7", getString(R.string.fly_cap_volt) + "8"
                , getString(R.string.fly_cap_volt) + "9", getString(R.string.fly_cap_volt) + "10", getString(R.string.fly_cap_volt) + "11", getString(R.string.fly_cap_volt) + "12",
                getString(R.string.fly_cap_volt) + "13", getString(R.string.fly_cap_volt) + "14", getString(R.string.fly_cap_volt) + "15", "PID"
        };



        eleTitles = new String[]{
                getString(R.string.android_key2019) + "\n" + "(kWh)",
                getString(R.string.m320功率) + "\n(kWh)",
        };


        eleResId = new int[]{
                R.drawable.tlxh_ele_fadian, R.drawable.ele_power,
        };

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
        isReceiveSucc = false;
        connectServer();
    }


    private void connectServer() {
        mClientUtil = SocketClientUtil.newInstance();
        if (mClientUtil != null) {
            mClientUtil.connect(mHandler);
        }
    }


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
                case SocketClientUtil.SOCKET_CLOSE:
                    text = "连接关闭";
                    break;
                case SocketClientUtil.SOCKET_OPEN:
                    text = "连接成功";
                    break;
                case SocketClientUtil.SOCKET_SEND_MSG:
                    BtnDelayUtil.sendMessage(this);
                    //记录请求时间
                    startTime = System.currentTimeMillis();
                    //禁用按钮
                    btnClick = false;
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
                        if (count < funs.length - 1) {
                            count++;
                            mHandler.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        } else {
                            //更新ui
                            refreshUI();
                            count = 0;
                            //关闭连接
                            SocketClientUtil.close(mClientUtil);
                            refreshFinish();
                            //主界面刷新完成后，刷新设备型号
                            //刷新设备型号
                            readTypeRegisterValue();
                        }
//                        else {
//                            toast(R.string.all_failed);
//                            count = 0;
//                            //关闭连接
//                            SocketClientUtil.close(mClientUtil);
//                        }
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
                    if (count < funs.length) {
                        sendMsg(mClientUtil, funs[count]);
                    }
                    break;
                case 100://恢复按钮点击
                    btnClick = true;
                    String myuuid = (String) msg.obj;
                    if (uuid.equals(myuuid) && !isReceiveSucc) {
                        toast(R.string.android_key1134);
                    }
                    refreshFinish();
                    break;
                case 101:
                    connectSendMsg();
                    break;
                default:
                    BtnDelayUtil.dealMaxBtn(this, what, 2500, mContext, toolbar);
                    break;
            }
//            if (msg.what != SocketClientUtil.SOCKET_SEND && msg.what != 100 && msg.what != 101 && what != 6 && what != SOCKET_RECEIVE_BYTES) {
//                toast(text);
//            }
        }
    };


    /**
     * 根据命令以及起始寄存器发送查询命令
     *
     * @param clientUtil
     */
    private void sendMsg(SocketClientUtil clientUtil, int[] sends) {
        if (clientUtil != null) {
            clientUtil.sendMsg(ModbusUtil.sendMsg(sends[0], sends[1], sends[2]));
        } else {
            connectServer();
        }
    }


    /**
     * 刷新完成
     */
    private void refreshFinish() {
//        Mydialog.Dismiss();
    }

    /**
     * 解析数据
     *
     * @param bytes
     * @param count
     */
    private void parseMax(byte[] bytes, int count) {
        switch (count) {
            case 0:
                RegisterParseUtil.parseHold0T124(mMaxData, bytes);
                break;
            case 1:
                RegisterParseUtil.parseMax1500V2(mMaxData, bytes);
                break;
            case 2:
                RegisterParseUtil.parseMax1500V3(mMaxData, bytes);
                break;
            case 3:
                RegisterParseUtil.parseMax04T875T999(mMaxData, bytes);
                break;
        }
    }


    /**
     * 更新ui数据
     */
    private void refreshUI() {
        //发电量  功率
        List<String> todayEle = new ArrayList<>();
        todayEle.add(String.valueOf(mMaxData.getTodayEnergy()));
        todayEle.add(String.valueOf(mMaxData.getNormalPower()));
        List<String> totalEle = new ArrayList<>();
        totalEle.add(String.valueOf(mMaxData.getTotalEnergy()));
        totalEle.add(String.valueOf(mMaxData.getTotalPower()));
        initEleDatas(eleTitles, todayEle, totalEle, mEleAdapter);


        //故障告警
        int errCode = mMaxData.getErrCode();
        int warmCode = mMaxData.getWarmCode();
        int errCodeSecond = mMaxData.getErrCodeSecond();
        int warmCodeSecond = mMaxData.getWarmCodeSecond();
        //添加副码
        String errCodeStr = "";
        String warnCodeStr = "";
        if (errCode >= 200 || warmCode>=200){
            errCodeStr = String.format("%d(%02d)",errCode,errCodeSecond);
            warnCodeStr = String.format("%d(%02d)",warmCode,warmCodeSecond);
        }else {
            if (errCode == 0){
                errCodeStr = "--";
            }else {
                errCode+=99;
//                errCodeStr = String.format("%d(%02d)",errCode,errCodeSecond<100?errCodeSecond:errCodeSecond%100);
                errCodeStr = String.valueOf(errCode);
            }
            if (warmCode == 0){
                warnCodeStr ="--";
            }else {
                warmCode+=99;
//                warnCodeStr = String.format("%d(%02d)",warmCode,warmCodeSecond<100?warmCodeSecond:warmCodeSecond%100);
                warnCodeStr = String.valueOf(warmCode);
            }
        }
        tvErrH1.setText(errCodeStr);
        tvWarnH1.setText(warnCodeStr);
        //状态
        int status = mMaxData.getStatus();
        String statusStr;
        int color=R.color.color_text_66;
        int drawable=R.drawable.circle_wait;

        if (status >= 0 && status < statusTitles.length){
            statusStr = statusTitles[status];
            color=statusColors[status];
            drawable=drawableStatus[status];
        }else {
            statusStr = getString(R.string.m505状态)+" "+status;
        }
        tvStatus.setVisibility(View.VISIBLE);
        tvStatus.setText(statusStr);
        tvStatus.setTextColor(ContextCompat.getColor(Max230KTL3HVToolActivity.this,color));
        Drawable drawableLeft=getResources().getDrawable(drawable);
        tvStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableLeft,null,null,null);
        tvStatus.setCompoundDrawablePadding(4);


        //-----------------------



        //recycleView:grid:关于本机
        String[] datasAbout = new String[6];
        MaxDataDeviceBean deviceBeen = mMaxData.getDeviceBeen();
        datasAbout[0] = deviceBeen.getCompany();
        datasAbout[1] = deviceBeen.getDeviceType();
        datasAbout[2] = deviceBeen.getSn();
        if (deviceBeen.getNewModel() == 0) {
            int model = deviceBeen.getModel();
            datasAbout[3] = MaxUtil.getDeviceModel(model);
        }else {
            datasAbout[3] = MaxUtil.getDeviceModelNew4(deviceBeen.getNewModel());
        }
        StringBuilder sbFirm = new StringBuilder()
                .append("(")
                .append(deviceBeen.getFirmVersionOut())
                .append(")")
                .append(deviceBeen.getFirmVersionIn());
        datasAbout[4] = sbFirm.toString();
        String commSoftVersion = deviceBeen.getCommSoftVersion();
        if (TextUtils.isEmpty(commSoftVersion)) {
            datasAbout[5] = "--";
        }else {
            datasAbout[5] = String.format("%s-%04d",commSoftVersion,deviceBeen.getCommSoftVersionValue());
        }
        //grid:内部参数
        String[] datasParams = new String[10];
        datasParams[0] = deviceBeen.getLastTime();
        datasParams[1] = deviceBeen.getRealOPowerPercent();
        datasParams[2] = String.format("%dkΩ",deviceBeen.getIso());
        datasParams[3] = deviceBeen.getEnvTemp();
        datasParams[4] = deviceBeen.getBoostTemp();
        datasParams[5] = deviceBeen.getDeviceTemp();
        datasParams[6] = deviceBeen.getpBusV();
        datasParams[7] = deviceBeen.getnBusV();
        datasParams[8] = deviceBeen.getPidErrCode();
        //处理pid状态
        int pidStatus = deviceBeen.getPidStatus();
        String pidStr = "";
        if (pidStatus >=1 && pidStatus <=3){
            pidStr = pidStatusStrs[pidStatus];
        }else {
            pidStr = pidStatus + "";
        }
        datasParams[9] = pidStr;
/*        initGridDate(c5Title1, datasAbout, mC5Adapter);
        initGridDateParam(c6Title1, datasParams, mC6Adapter);
        //pv电流电压电量
        initC1Datas(c1Title1, c1Title2, mMaxData.getPVList(), mC1Adapter);
        //pvc电压电流
        initC1Datas(c2Title1, c2Title2, mMaxData.getPVCList(), mC2Adapter);*/
        //ac电压电流
        List<String> nowACList = mMaxData.getACList();
        try {
            nowACList.set(4,deviceBeen.getIpf());
        } catch (Exception e) {
            e.printStackTrace();
        }
/*        initC1Datas(c3Title1, c3Title2, nowACList, mC3Adapter);
        //ac电压电流
        initC1Datas(c4Title1, c4Title2, mMaxData.getPIDList(), mC4Adapter);
        //svg
        initC1Datas(c34Title1, c34Title2, mMaxData.getSVGList(), mC34Adapter);*/
    }



    /**
     * 用于读取机器型号
     */
    //连接对象:用于读取数据
    private SocketClientUtil mClientUtilReadType;
    //读取寄存器的值
    private void readTypeRegisterValue() {
        mClientUtilReadType = SocketClientUtil.connectServer(mHandlerReadType);
    }
    /**
     * 读取寄存器handle
     */
    Handler mHandlerReadType= new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    byte[] sendBytesR = SocketClientUtil.sendMsgToServer(mClientUtilReadType, deviceTypeFun);
                    LogUtil.i("发送读取：" + SocketClientUtil.bytesToHexString(sendBytesR));
                    break;
                //接收字节数组
                case SOCKET_RECEIVE_BYTES:
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //检测内容正确性
                        boolean isCheck = ModbusUtil.checkModbus(bytes);
                        if (isCheck) {
                            RegisterParseUtil.parseMax3T125T249(mMaxData,bytes);
                            //更新机器型号
                            refreshUIAbout();
//                            toast(R.string.all_success);
                        }
                        LogUtil.i("接收读取：" + SocketClientUtil.bytesToHexString(bytes));
                        //关闭连接
                        SocketClientUtil.close(mClientUtilReadType);
                        //设备信号刷新完后：开启自动刷新
                        if (needFresh){
                            autoRefresh(mHandlerReadAuto);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        //关闭连接
                        SocketClientUtil.close(mClientUtilReadType);
                    }
                    break;
            }
        }
    };


    /**
     * 读取寄存器handle
     */
    Handler mHandlerReadAuto = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    if (autoCount < autoFun.length) {
                        byte[] sendBytesR = SocketClientUtil.sendMsgToServer(mClientUtilRead, autoFun[autoCount]);
                        LogUtil.i("发送读取：" + SocketClientUtil.bytesToHexString(sendBytesR));
                    }
                    break;
                //接收字节数组
                case SOCKET_RECEIVE_BYTES:
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //检测内容正确性
                        boolean isCheck = ModbusUtil.checkModbus(bytes);
                        if (isCheck) {
                            //接收正确，开始解析
                            parseMaxAuto(bytes, autoCount);
                        }
                        if (autoCount < autoFun.length - 1) {
                            autoCount++;
                            this.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        } else {
                            autoCount = 0;
                            //更新ui
                            refreshUI();
                            //自动刷新
//                            autoRefresh(this);
                            this.sendEmptyMessageDelayed(SOCKET_SEND,3000);

                        }
//                        else {//错误后重新开始
//                            autoCount = 0;
//                            this.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
//                        }
                        LogUtil.i("接收读取：" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        autoCount = 0;
                        //关闭连接
                        SocketClientUtil.close(mClientUtilRead);
                    }
                    break;
                case SOCKET_AUTO_REFRESH:
                    autoReadRegisterValue();
                    break;
                case SocketClientUtil.SOCKET_CLOSE:
                    this.removeMessages(SOCKET_AUTO_REFRESH);
                    isAutoRefresh = false;
                    item.setTitle(noteStartStr);
                    break;
            }
        }
    };



    /**
     * 解析数据
     *
     * @param bytes
     * @param count
     */
    private void parseMaxAuto(byte[] bytes, int count) {
        switch (count) {
            case 0:
                RegisterParseUtil.parseMax1500V4T0T125(mMaxData,bytes);
                break;
            case 1:
                RegisterParseUtil.parseMax1500V4T125T250(mMaxData,bytes);
                break;
            case 2:
                RegisterParseUtil.parseMax04T875T999(mMaxData, bytes);
                break;
        }
    }


    /**
     * 自动刷新
     */
    //连接对象:用于读取数据
    private SocketClientUtil mClientUtilRead;

    //读取寄存器的值
    private void autoReadRegisterValue() {
        isAutoRefresh = true;
        item.setTitle(noteStopStr);
        mClientUtilRead = SocketClientUtil.connectServerAuto(mHandlerReadAuto);
    }


    /**
     * 自动刷新
     *
     * @param handler
     */
    public void autoRefresh(Handler handler) {
        if (handler != null) {
            isAutoRefresh = true;
            item.setTitle(noteStopStr);
            handler.sendEmptyMessageDelayed(SOCKET_AUTO_REFRESH, autoTime);
        } else {
            item.setTitle(noteStopStr);
            isAutoRefresh = false;
        }
    }

    private void refreshUIAbout() {
        //recycleView:grid:关于本机
        String[] datasAbout = new String[6];
        MaxDataDeviceBean deviceBeen = mMaxData.getDeviceBeen();
        datasAbout[0] = deviceBeen.getCompany();
        datasAbout[1] = deviceBeen.getDeviceType();
        datasAbout[2] = deviceBeen.getSn();
        int model = deviceBeen.getModel();
        datasAbout[3] = MaxUtil.getDeviceModel(model);
        datasAbout[4] = deviceBeen.getFirmVersionOut();
        datasAbout[5] = deviceBeen.getFirmVersionIn();
//        initGridDate(c5Title1, datasAbout, mC5Adapter);
    }



    /**
     * 停止刷新
     */
    private void stopRefresh() {
        isAutoRefresh = false;
        item.setTitle(noteStartStr);
        mHandlerReadAuto.removeMessages(SOCKET_AUTO_REFRESH);
        mHandlerReadAuto.removeMessages(SOCKET_SEND);
        //停止刷新；关闭socket
        SocketClientUtil.close(mClientUtilRead);
        SocketClientUtil.close(mClientUtilReadType);
        SocketClientUtil.close(mClientUtil);
    }




    /**
     * 跳转到Max各设置界面
     */
    private void jumpMaxSet(Class clazz, String title) {
        stopRefresh();
        Intent intent = new Intent(mContext, clazz);
        intent.putExtra("title", title);
        intent.putExtra("isTlxhus", true);
        ActivityUtils.startActivity(this, intent, false);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRefresh();
    }



}
