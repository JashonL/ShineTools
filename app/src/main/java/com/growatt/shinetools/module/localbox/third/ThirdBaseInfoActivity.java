package com.growatt.shinetools.module.localbox.third;

import static com.growatt.shinetools.modbusbox.SocketClientUtil.SOCKET_RECEIVE_BYTES;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.growatt.shinetools.R;
import com.growatt.shinetools.adapter.MaxContentAdapter;
import com.growatt.shinetools.adapter.MaxMainChildAdapter;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.modbusbox.bean.MaxDataBean;
import com.growatt.shinetools.modbusbox.bean.MaxDataDeviceBean;
import com.growatt.shinetools.modbusbox.bean.ToolStorageDataBean;
import com.growatt.shinetools.module.localbox.max.bean.MaxChildBean;
import com.growatt.shinetools.module.localbox.max.bean.MaxContentBean;
import com.growatt.shinetools.module.localbox.mintool.TLXHBattryActivity;
import com.growatt.shinetools.module.localbox.mintool.TLXHLiwangActivity;
import com.growatt.shinetools.module.localbox.ustool.USBDCParamActivity;
import com.growatt.shinetools.utils.ActivityUtils;
import com.growatt.shinetools.utils.BtnDelayUtil;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.MyControl;
import com.growatt.shinetools.utils.Mydialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;

public abstract class ThirdBaseInfoActivity extends BaseActivity implements View.OnClickListener, Toolbar.OnMenuItemClickListener {
    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.header_title)
    LinearLayout headerTitle;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;



    //其他数据
    private View tvTitleLiwang;
    private View tvTitleBdc;
    private View tvTitleBattry;


    //头部2：content1
    private List<MaxContentBean> mC1List;
    private MaxContentAdapter mC1Adapter;
    private RecyclerView mC1RecyclerView;
    private View content1Head2;
    private View title1Head2;
    private ImageView t1H2IvStatus;

    public String[] c1Title1 = {
            "PV1", "PV2", "PV3", "PV4"
    };
    public String[] c1Title2;
    //初始化AC电流电压
    public abstract void initACVolCurString();

    //头部2：content2
    private List<MaxContentBean> mC2List;
    private MaxContentAdapter mC2Adapter;
    private RecyclerView mC2RecyclerView;
    private View content2Head2;
    private View title2Head2;
    private ImageView t2H2IvStatus;
    public String[] c2Title1 = {
            "Str1", "Str2", "Str3", "Str4", "Str5", "Str6", "Str7", "Str8",
            "Str9", "Str10", "Str11", "Str12", "Str13", "Str14", "Str15", "Str16"
    };

    public  String[] c2Title2;

    //初始化组串电压电流
    public abstract void initStringVolCurString();

    //头部2：content3
    private List<MaxContentBean> mC3List;
    private MaxContentAdapter mC3Adapter;
    private RecyclerView mC3RecyclerView;
    private View content3Head2;
    private View title3Head2;
    private ImageView t3H2IvStatus;
    public  String[] c3Title1 = {
            "R", "S", "T"
    };
    public String[] c3Title2;
    //初始化电压电流功率
    public abstract void initVolFreCurString();



    //头部2：content34:SVG/APF
    private List<MaxContentBean> mC34List;
    private MaxContentAdapter mC34Adapter;
    private RecyclerView mC34RecyclerView;
    private View content34Head2;
    private View title34Head2;
    private ImageView t34H2IvStatus;

    public   String[] c34Title1 = {
            "R", "S", "T"
    };

    public  String[] c34Title2;

    //初始化SVGAPF
    public abstract void initSVGAPFString();




    //头部2：content4
    private List<MaxContentBean> mC4List;
    private MaxContentAdapter mC4Adapter;
    private RecyclerView mC4RecyclerView;
    private View content4Head2;
    private View title4Head2;
    private ImageView t4H2IvStatus;
    public  String[] c4Title1 = {
            "PID1", "PID2", "PID3", "PID4", "PID5", "PID6", "PID7", "PID8"
    };
    public String[] c4Title2;

    //初始化PID
    public abstract void initPIDString();


    //头部关于本机：content5
    private List<MaxChildBean> mC5List;
    private MaxMainChildAdapter mC5Adapter;
    private RecyclerView mC5RecyclerView;
    private View title5Head2;
    private ImageView t5H2IvStatus;
    private View content5Head2;
    public String[] c5Title1;

    //关于设备
    public abstract void initAboutDeviceString();

    //头部2：content6
    private List<MaxChildBean> mC6List;
    private MaxMainChildAdapter mC6Adapter;
    private RecyclerView mC6RecyclerView;
    private View content6Head2;
    private View title6Head2;
    private ImageView t6H2IvStatus;
    public String[] c6Title1;

    //内部参数
    public abstract void initInternalParamString();




    private MenuItem item;



    private boolean isReceiveSucc = false;
    private int[][] funs;
    private int count = 0;
    //所有本地获取数据集合
    public MaxDataBean mMaxData = new MaxDataBean();

    //获取数据的寄存器集合
    public abstract int[][] initGetDataArray();


    //是否读取了bdc的值
    public boolean isBDC;
    private int bdcType;//0 bdc  1 bms
    private int scroolD = 100;





    //表格列表
    private LinearLayoutManager mLayoutManager;
    private List<MaxChildBean> mGridList;
    public View header;
    private MaxMainChildAdapter mAdapter;

    /**
     * 降额模式
     */
    private String[] deratModes;

    public abstract String[] deratModes();


    public abstract int getHeaderView();
    private int rvHeaderView;


    public boolean isOther=false;
    public abstract void setOther();

    @Override
    protected int getContentView() {
        return R.layout.activity_us_device_info;
    }

    @Override
    protected void initViews() {
        //头部信息
        initToobar(toolbar);
        tvTitle.setText(R.string.m291设备信息);
        String title = getIntent().getStringExtra("title");
        if (TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }
        toolbar.inflateMenu(R.menu.comment_right_menu);
        item = toolbar.getMenu().findItem(R.id.right_action);
        item.setTitle(R.string.android_key816);
        toolbar.setOnMenuItemClickListener(this);

        //主体列表
        mGridList = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MaxMainChildAdapter(R.layout.item_max_childrv, mGridList);
        rvHeaderView=getHeaderView();
        header = LayoutInflater.from(this).inflate(rvHeaderView, (ViewGroup) mRecyclerView.getParent(), false);
        mAdapter.addHeaderView(header);
        mRecyclerView.setAdapter(mAdapter);
        //头部初始化
        initString();
        //pv电压 电流 功率
        initContent1();
        //组串电压电流
        initContent2();
        //AC电压
        initContent3();
        //SVG
        initContent34();
        //PID电压
        initContent4();
        //关于设备
        initContent5();
        //内部参数
        initContent6();
        //离网参数/BDC/电池参数
        setOther();
        if (isOther){
            initOtherView();
        }
        //升级
        initUpdata();
    }


    public void initUpdata(){

    }


    private void initString() {
        initVolFreCurString();
        initStringVolCurString();
        initACVolCurString();
        initSVGAPFString();
        initPIDString();
        initAboutDeviceString();
        initInternalParamString();
        initGetDataArray();
        deratModes = deratModes();

    }


    private void initContent1() {
        title1Head2 = header.findViewById(R.id.tvTitle1);
        content1Head2 = header.findViewById(R.id.tvContent1);
        t1H2IvStatus = title1Head2.findViewById(R.id.ivStatus);
        TextView t1h2TvTitle = title1Head2.findViewById(R.id.tvHeadTitle);
        t1h2TvTitle.setTextColor(ContextCompat.getColor(this, R.color.color_text_33));
        mC1List = new ArrayList<>();
        mC1RecyclerView = content1Head2.findViewById(R.id.recyclerViewC1);
        mC1RecyclerView.setLayoutManager(new GridLayoutManager(this, c1Title2.length + 1, LinearLayoutManager.HORIZONTAL, false));
        mC1Adapter = new MaxContentAdapter(R.layout.item_grid_textview_max_big_3col, mC1List);
        mC1RecyclerView.setAdapter(mC1Adapter);
        initC1Datas(c1Title1, c1Title2, null, mC1Adapter);
    }


    private void initContent2() {
        title2Head2 = header.findViewById(R.id.tvTitle2);
        content2Head2 = header.findViewById(R.id.tvContent2);
        t2H2IvStatus = title2Head2.findViewById(R.id.ivStatus);
        TextView t2h2TvTitle = title2Head2.findViewById(R.id.tvHeadTitle);
        t2h2TvTitle.setTextColor(ContextCompat.getColor(this, R.color.color_text_33));
        mC2List = new ArrayList<>();
        mC2RecyclerView = content2Head2.findViewById(R.id.recyclerViewC1);
        mC2RecyclerView.setLayoutManager(new GridLayoutManager(this, c2Title2.length + 1, LinearLayoutManager.HORIZONTAL, false));
        mC2Adapter = new MaxContentAdapter(R.layout.item_grid_textview, mC2List);
        mC2RecyclerView.setAdapter(mC2Adapter);
        initC1Datas(c2Title1, c2Title2, null, mC2Adapter);

    }


    private void initContent3() {
        title3Head2 = header.findViewById(R.id.tvTitle3);
        content3Head2 = header.findViewById(R.id.tvContent3);
        TextView t3h2TvTitle = title3Head2.findViewById(R.id.tvHeadTitle);
        t3h2TvTitle.setTextColor(ContextCompat.getColor(this, R.color.color_text_33));
        t3H2IvStatus = title3Head2.findViewById(R.id.ivStatus);
        mC3List = new ArrayList<>();
        mC3RecyclerView = content3Head2.findViewById(R.id.recyclerViewC1);
        mC3RecyclerView.setLayoutManager(new GridLayoutManager(this, c3Title2.length + 1, LinearLayoutManager.HORIZONTAL, false));
        mC3Adapter = new MaxContentAdapter(R.layout.item_grid_textview_max_big, mC3List);
        mC3RecyclerView.setAdapter(mC3Adapter);
        initC1Datas3(c3Title1, c3Title2, null, mC3Adapter);
    }


    private void initContent34() {
        title34Head2 = header.findViewById(R.id.tvTitle34);
        content34Head2 = header.findViewById(R.id.tvContent34);
        content34Head2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.dp_150)));
        t34H2IvStatus = (ImageView) title34Head2.findViewById(R.id.ivStatus);
        TextView t34h2TvTitle = title34Head2.findViewById(R.id.tvHeadTitle);
        t34h2TvTitle.setTextColor(ContextCompat.getColor(this, R.color.color_text_33));
        mC34List = new ArrayList<>();
        mC34RecyclerView = (RecyclerView) content34Head2.findViewById(R.id.recyclerViewC1);
        mC34RecyclerView.setLayoutManager(new GridLayoutManager(this, c34Title2.length + 1, LinearLayoutManager.HORIZONTAL, false));
        mC34Adapter = new MaxContentAdapter(R.layout.item_grid_textview_max_big, mC34List);
        mC34RecyclerView.setAdapter(mC34Adapter);
        initC1Datas(c34Title1, c34Title2, null, mC34Adapter);
    }


    private void initContent4() {
        title4Head2 = header.findViewById(R.id.tvTitle4);
        title5Head2 = header.findViewById(R.id.tvTitle5);
        t5H2IvStatus = (ImageView) title5Head2.findViewById(R.id.ivStatus);
        content5Head2 = header.findViewById(R.id.tvContent5);
        content4Head2 = header.findViewById(R.id.tvContent4);
        t4H2IvStatus = (ImageView) title4Head2.findViewById(R.id.ivStatus);
        TextView t4h2TvTitle = title4Head2.findViewById(R.id.tvHeadTitle);
        t4h2TvTitle.setTextColor(ContextCompat.getColor(this, R.color.color_text_33));
        mC4List = new ArrayList<>();
        mC4RecyclerView = (RecyclerView) content4Head2.findViewById(R.id.recyclerViewC1);
        mC4RecyclerView.setLayoutManager(new GridLayoutManager(this, c4Title2.length + 1, LinearLayoutManager.HORIZONTAL, false));
        mC4Adapter = new MaxContentAdapter(R.layout.item_grid_textview, mC4List);
        mC4RecyclerView.setAdapter(mC4Adapter);
        initC1Datas(c4Title1, c4Title2, null, mC4Adapter);
    }


    private void initContent5() {
        title5Head2 = header.findViewById(R.id.tvTitle5);
        content5Head2 = header.findViewById(R.id.tvContent5);
        t5H2IvStatus = (ImageView) title5Head2.findViewById(R.id.ivStatus);
        TextView t5h2TvTitle = title5Head2.findViewById(R.id.tvHeadTitle);
        t5h2TvTitle.setTextColor(ContextCompat.getColor(this, R.color.color_text_33));
        mC5List = new ArrayList<>();
        mC5RecyclerView = (RecyclerView) content5Head2.findViewById(R.id.recyclerViewC1);
        int padding = getResources().getDimensionPixelOffset(R.dimen.dp_10);
        mC5RecyclerView.setPadding(padding, padding, padding, padding);
        mC5RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mC5Adapter = new MaxMainChildAdapter(R.layout.item_tlx_childrv, mC5List);
        mC5RecyclerView.setAdapter(mC5Adapter);
        //数据刷新
        initGridDateParam(c5Title1, null, mC5Adapter);
    }


    private void initContent6() {
        title6Head2 = header.findViewById(R.id.tvTitle6);
        content6Head2 = header.findViewById(R.id.tvContent6);
        t6H2IvStatus = (ImageView) title6Head2.findViewById(R.id.ivStatus);
        TextView t6h2TvTitle = title6Head2.findViewById(R.id.tvHeadTitle);
        t6h2TvTitle.setTextColor(ContextCompat.getColor(this, R.color.color_text_33));
        mC6List = new ArrayList<>();
        mC6RecyclerView = (RecyclerView) content6Head2.findViewById(R.id.recyclerViewC1);
        mC6RecyclerView.setLayoutManager(new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false));
        mC6Adapter = new MaxMainChildAdapter(R.layout.item_max_childrv, mC6List);
        mC6RecyclerView.setAdapter(mC6Adapter);
        //数据刷新
        initGridDateParam(c6Title1, null, mC6Adapter);
    }


    private void initOtherView() {
        tvTitleLiwang = header.findViewById(R.id.tvTitleLiwang);
        TextView tvLiwangTitle = tvTitleLiwang.findViewById(R.id.tvHeadTitle);
        tvLiwangTitle.setTextColor(ContextCompat.getColor(this, R.color.color_text_33));
        tvTitleBdc = header.findViewById(R.id.tvTitleBdc);
        TextView bdcTitle = tvTitleBdc.findViewById(R.id.tvHeadTitle);
        bdcTitle.setTextColor(ContextCompat.getColor(this, R.color.color_text_33));
        bdcTitle.setText(R.string.android_key1315);

        tvTitleBattry = header.findViewById(R.id.tvTitleBattry);
        TextView battryTitle = tvTitleBattry.findViewById(R.id.tvHeadTitle);
        battryTitle.setTextColor(ContextCompat.getColor(this, R.color.color_text_33));
    }


    @Override
    protected void initData() {
        funs = initGetDataArray();
        initListener();
        //请求数据
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
        isReceiveSucc = false;
        Mydialog.Show(mContext);
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
                            Mydialog.Dismiss();
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
                    if (count < funs.length) {
                        sendMsg(mClientUtil, funs[count]);
                    }
                    break;
                case 100://恢复按钮点击
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
//            if (msg.what != SocketClientUtil.SOCKET_SEND && msg.what != 100 && msg.what != 101 && what != 6 && what != SOCKET_RECEIVE_BYTES) {
//                toast(text);
//            }
        }
    };


    /**
     * 更新ui数据
     */
    private void refreshUI() {
        ToolStorageDataBean storageBeen = mMaxData.getStorageBeen();
        //发电量设置
        List<String> todayEle = new ArrayList<>();
        todayEle.add(String.valueOf(mMaxData.getTodayEnergy()));
        todayEle.add(String.valueOf(storageBeen.geteChargeToday()));
        todayEle.add(String.valueOf(storageBeen.geteDischargeToday()));
        todayEle.add(String.valueOf(mMaxData.getEtoGridToday()));
        todayEle.add(String.valueOf(mMaxData.geteLoadToday()));
        List<String> totalEle = new ArrayList<>();
        totalEle.add(String.valueOf(mMaxData.getTotalEnergy()));
        totalEle.add(String.valueOf(storageBeen.geteChargeTotal()));
        totalEle.add(String.valueOf(storageBeen.geteDischargeTotal()));
        totalEle.add(String.valueOf(mMaxData.getEtoGridTotal()));
        totalEle.add(String.valueOf(mMaxData.geteLoadTotal()));
        //功率设置
        List<String> powers = new ArrayList<>();
        powers.add(String.valueOf(mMaxData.getNormalPower()));
        powers.add(String.valueOf(mMaxData.getTotalPower()));
        powers.add(String.valueOf(storageBeen.getpCharge()));
        powers.add(String.valueOf(storageBeen.getpDischarge()));


        //recycleView:grid:关于本机
        String[] datasAbout = new String[8];
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
//        datasAbout[4] = deviceBeen.getFirmVersionOut();
//        datasAbout[5] = deviceBeen.getFirmVersionIn();
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
        datasAbout[6]=mMaxData.getBdcVervison();
        datasAbout[7]=mMaxData.getBatVersion();


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
        int deratMode = deviceBeen.getDerateMode2();
        if (deratMode >=0 && deratMode <=16){
//            datasParams[8] = deratModes[deratMode] + "/" + deratMode;
            datasParams[8] = deratModes[deratMode];
        }else {
            datasParams[8] = String.valueOf(deratMode);
        }
        initGridDate(c5Title1, datasAbout, mC5Adapter);
        initGridDateParam(c6Title1, datasParams, mC6Adapter);
        //pv电流电压电量
        initC1Datas(c1Title1, c1Title2, mMaxData.getPVList(), mC1Adapter);
        //pvc电压电流
        initC1Datas(c2Title1, c2Title2, mMaxData.getPVCList(), mC2Adapter);
        //ac电压电流
        List<String> nowACList = mMaxData.getACList();
        //ac电压电流
        try {
            nowACList.set(4,deviceBeen.getIpf());
        } catch (Exception e) {
            e.printStackTrace();
        }
        initC1Datas3(c3Title1,c3Title2 ,nowACList, mC3Adapter);
        //ac电压电流
        initC1Datas(c4Title1, c4Title2, mMaxData.getPIDList(), mC4Adapter);
        //svg
        initC1Datas(c34Title1, c34Title2, mMaxData.getSVGList(), mC34Adapter);
    }


    public abstract void parserData(int count, byte[] bytes);


    /**
     * 解析数据
     *
     * @param bytes
     * @param count
     */
    private void parseMax(byte[] bytes, int count) {
        parserData(count, bytes);
    }


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

    public void initListener() {
        if (isOther){
            initOnclick(title1Head2, title2Head2, title3Head2, title34Head2, title4Head2,
                    title5Head2, title6Head2,tvTitleLiwang,tvTitleBdc);
        }else {

            initOnclick(title1Head2, title2Head2, title3Head2, title34Head2, title4Head2,
                    title5Head2, title6Head2);
        }

    }

    private void initOnclick(View... views) {
        if (views != null) {
            for (View view : views) {
                view.setOnClickListener(this);
            }
        }
    }


    /**
     * grid数据刷新:关于本机
     */
    private void initGridDate(String[] titles, String[] values, MaxMainChildAdapter adapter) {
        if (titles == null) return;
        List<MaxChildBean> newList = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            MaxChildBean bean = new MaxChildBean();
            bean.setTitle(titles[i]);
            if (values != null && values.length > i && values[i] != null) {
                bean.setContent(values[i]);
            } else {
                bean.setContent("");
            }
            newList.add(bean);
        }
        if (adapter != null) {
            adapter.replaceData(newList);
        }
    }


    private void initC1Datas(String[] title1, String[] title2, List<String> c1List, MaxContentAdapter adapter) {
        if (title1 == null || title2 == null) return;
        List<MaxContentBean> newList = new ArrayList<>();
        int title1Len = title1.length + 1;
        int title2Len = title2.length + 1;
        int size = title1Len * title2Len;
        for (int i = 0; i < size; i++) {
            MaxContentBean bean = new MaxContentBean();
            if (i != 0) {
                if (i < title2Len) {
                    bean.setText(title2[i - 1]);
                    bean.setStatus(1);
                } else {
                    if (i % title2Len == 0) {
                        bean.setText(title1[i / title2Len - 1]);
                        bean.setStatus(0);
                    } else {
                        bean.setStatus(2);
                        if (c1List == null || c1List.size() < (title1Len - 1) * (title2Len - 1)) {
                            bean.setText("");
                        } else {
                            int dataIndex = (i - title2Len - 1) * (title2Len - 1);
                            if (dataIndex % title2Len == 0) {
                                dataIndex = dataIndex / title2Len;
                            } else {
                                dataIndex = dataIndex / title2Len + 1;
                            }
                            bean.setText(c1List.get(dataIndex));
                        }
                    }
                }
            }
            newList.add(bean);
        }
//        adapter.setNewData(newList);
        adapter.replaceData(newList);
    }


    /**
     * grid数据刷新:内部参数
     */
    private void initGridDateParam(String[] titles, String[] values, MaxMainChildAdapter adapter) {
        if (titles == null) return;
        List<MaxChildBean> newList = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            MaxChildBean bean = new MaxChildBean();
            bean.setTitle(titles[i]);
            if (values != null && values.length > i && values[i] != null) {
                bean.setContent(values[i]);
            } else {
                bean.setContent("");
            }
            newList.add(bean);
        }
        if (adapter != null) {
            adapter.replaceData(newList);
//            mC6ListReal = newList;
//            if (isShowC6){
//                mC6List.clear();
//                mC6List.addAll(newList);
//                adapter.notifyItemRangeChanged(1,adapter.getItemCount()-1);
//            }
        }
    }



    private void initC1Datas3(String[] title1, String[] title2, List<String> c1List, MaxContentAdapter adapter) {
        if (title1 == null || title2 == null) return;
        List<MaxContentBean> newList = new ArrayList<>();
        int title1Len = title1.length + 1;
        int title2Len = title2.length + 1;
        int size = title1Len * title2Len;
        for (int i = 0; i < size; i++) {
            MaxContentBean bean = new MaxContentBean();
            if (i != 0) {
                if (i < title2Len) {
                    bean.setText(title2[i - 1]);
                    bean.setStatus(1);
                } else {
                    if (i % title2Len == 0) {
                        bean.setText(title1[i / title2Len - 1]);
                        bean.setStatus(0);
                    } else {
                        bean.setStatus(2);
                        if (c1List == null || c1List.size() < (title1Len - 1) * (title2Len - 1)) {
                            bean.setText("");
                        } else {
                            int dataIndex = (i - title2Len - 1) * (title2Len - 1);
                            if (dataIndex % title2Len == 0) {
                                dataIndex = dataIndex / title2Len;
                            } else {
                                dataIndex = dataIndex / title2Len + 1;
                            }
                            bean.setText(c1List.get(dataIndex));
                        }
                    }
                }
            }
            newList.add(bean);
        }
//        adapter.setNewData(newList);
        adapter.replaceData(newList);
    }




    @Override
    public void onClick(View view) {

        if (view == title1Head2) {
            showOrHideView(content1Head2, t1H2IvStatus);
        } else if (view == title2Head2) {
            showOrHideView(content2Head2, t2H2IvStatus);
        } else if (view == title3Head2) {
            showOrHideView(content3Head2, t3H2IvStatus);
        } else if (view == title34Head2) {
            showOrHideView(content34Head2, t34H2IvStatus);
        } else if (view == title4Head2) {
            showOrHideView(content4Head2, t4H2IvStatus);
        } else if (view == title5Head2) {
            showOrHideView(content5Head2, t5H2IvStatus);
//            showOrHideRecycler(mGridAdapter,t5H2IvStatus);
        } else if (view == title6Head2) {
            showOrHideView(content6Head2, t6H2IvStatus);
//            showOrHideRecyclerParams(mC6Adapter,t6H2IvStatus);
        } else if (view == tvTitleLiwang) {
            EventBus.getDefault().postSticky(mMaxData);
            jumpMaxSet(TLXHLiwangActivity.class, "");
        } else if (view == tvTitleBdc) {
            bdcType = 0;
            if (!isBDC) {
                connectServerBDC();
                return;
            }
            jumpBDC();
        } else if (view == tvTitleBattry) {
            bdcType = 1;
            if (!isBDC) {
                connectServerBDC();
                return;
            }
            jumpBDC();
        }
//        else if (v == cvPower){
//            Intent intent = new Intent(mContext, MaxChartPowerActivity.class);
//            intent.putExtra("title", tvPowerStr.getText().toString());
//            jumpTo(intent, false);
//        }
    }

    public void jumpBDC() {
        if (isBDC && mMaxData.getBdcStatus() == 0) {
            MyControl.circlerDialog(this, getString(R.string.没有接入BDC), -1, false);
            return;
        }
        if (bdcType == 0) {
            EventBus.getDefault().postSticky(mMaxData);
            jumpMaxSet(USBDCParamActivity.class, "");
        } else if (bdcType == 1) {
            EventBus.getDefault().postSticky(mMaxData);
            jumpMaxSet(TLXHBattryActivity.class, "");
        }
    }

    //连接对象:用于读取数据
    private SocketClientUtil mClientUtilBDC;

    private void connectServerBDC() {
        Mydialog.Show(mContext);
        mClientUtilBDC = SocketClientUtil.connectServer(mHandler3124);
    }

    /**
     * 读取寄存器handle
     */
    Handler mHandler3124 = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessage(this);
                    byte[] sendBytesR = sendMsgBDC(mClientUtilBDC, funs[3]);
                    LogUtil.i("发送读取：" + SocketClientUtil.bytesToHexString(sendBytesR));
                    break;
                //接收字节数组
                case SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //检测内容正确性
                        boolean isCheck = ModbusUtil.checkModbus(bytes);
                        if (isCheck) {
                            toast(R.string.all_success);
                            RegisterParseUtil.parseInput3kT3124V2(mMaxData, bytes);
                            isBDC = true;
                            jumpBDC();
                        } else {
                            toast(R.string.all_failed);
                        }
                        LogUtil.i("接收读取：" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //关闭tcp连接
                        SocketClientUtil.close(mClientUtilBDC);
                        BtnDelayUtil.refreshFinish();
                    }
                    break;
                default:
                    BtnDelayUtil.dealMaxBtn(this, what, mContext, toolbar);
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
    private byte[] sendMsgBDC(SocketClientUtil clientUtil, int[] sends) {
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
     * 跳转到Max各设置界面
     */
    private void jumpMaxSet(Class clazz, String title) {
        Intent intent = new Intent(mContext, clazz);
        intent.putExtra("title", title);
        intent.putExtra("isTlxhus", true);
        ActivityUtils.startActivity(this, intent, false);
    }


    public void showOrHideView(View view, ImageView iv) {
        if (view.getVisibility() == View.GONE) {
            view.setVisibility(View.VISIBLE);
            iv.setImageResource(R.drawable.max_up);
            try {
                if (view == content5Head2) {
                    scrollHandle.sendEmptyMessageDelayed(0, 40);
                } else {
                    mRecyclerView.scrollBy(0, scroolD);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            view.setVisibility(View.GONE);
            iv.setImageResource(R.drawable.max_down);
        }
    }


    @SuppressLint("HandlerLeak")
    Handler scrollHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mRecyclerView.scrollBy(0, scroolD);
                    break;
            }
        }
    };

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        refresh();
        return true;
    }
}
