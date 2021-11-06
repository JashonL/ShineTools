package com.growatt.shinetools.module.localbox.mintool;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.growatt.shinetools.R;
import com.growatt.shinetools.ShineToosApplication;
import com.growatt.shinetools.adapter.MaxContentAdapter;
import com.growatt.shinetools.adapter.MaxControlAdapter;
import com.growatt.shinetools.adapter.MaxMainChildAdapter;
import com.growatt.shinetools.adapter.MaxMainMuiltAdapter;
import com.growatt.shinetools.adapter.TLXHToolEleAdapter;
import com.growatt.shinetools.adapter.TLXHToolPowerAdapter;
import com.growatt.shinetools.base.DemoBase;
import com.growatt.shinetools.bean.WifiList;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.modbusbox.bean.MaxDataBean;
import com.growatt.shinetools.modbusbox.bean.MaxDataDeviceBean;
import com.growatt.shinetools.modbusbox.bean.ToolStorageDataBean;
import com.growatt.shinetools.module.localbox.max.MaxChartEnergyActivity;
import com.growatt.shinetools.module.localbox.max.MaxCheckActivity;
import com.growatt.shinetools.module.localbox.max.MaxOssPwdActivity;
import com.growatt.shinetools.module.localbox.max.bean.MaxChildBean;
import com.growatt.shinetools.module.localbox.max.bean.MaxContentBean;
import com.growatt.shinetools.module.localbox.max.bean.MaxControlBean;
import com.growatt.shinetools.module.localbox.max.bean.MaxMainMuiltBean;
import com.growatt.shinetools.module.localbox.max.bean.TLXHEleBean;
import com.growatt.shinetools.module.localbox.ustool.USAdvanceSetActivity;
import com.growatt.shinetools.utils.BtnDelayUtil;
import com.growatt.shinetools.utils.CommenUtils;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.MyControl;
import com.growatt.shinetools.utils.Mydialog;
import com.growatt.shinetools.utils.Position;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.growatt.shinetools.constant.GlobalConstant.END_USER;
import static com.growatt.shinetools.modbusbox.SocketClientUtil.SOCKET_AUTO_REFRESH;
import static com.growatt.shinetools.modbusbox.SocketClientUtil.SOCKET_RECEIVE_BYTES;
import static com.growatt.shinetools.modbusbox.SocketClientUtil.SOCKET_SEND;


public class  TLXHToolMainActivity extends DemoBase implements View.OnClickListener, BaseQuickAdapter.OnItemClickListener {
    //刷新流程：刷新03:0-99---》04:0-99---》04:100-199---》刷新设备型号03:100-132--》自动刷新04:0-124
    private String noteStartStr;
    private String noteStopStr;
    @BindView(R.id.tvRight)
    TextView mTvRight;
    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    private String errNode ;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.headerView)
    View mHeaderView;


    private TextView tvStepTitle;



    //gird布局
    private MaxMainChildAdapter mGridAdapter;
    private GridLayoutManager mGridLayoutManager;
    private List<MaxChildBean> mGridList;
    //    String[] titlesGrid = {
//             "厂商信息", "机器型号","序列号","Model号",
//            "固件(外部)版本","固件(内部)版本"
//    };
    String[] pidStatusStrs;
    //标题状态
    String[] statusTitles ;

    //头部
    private View header1;
    private View header2;
    //其他数据
    private View tvTitleLiwang;
    private View tvTitleBdc;
    private View tvTitleBattry;
    //头部1
    private TextView tvTodayEH1;
    private TextView tvTotalEH1;
    private TextView tvTodayPH1;
    private TextView tvTotalPH1;
    private TextView tvFluxPower;
    private ImageView ivDryStatus;
    private View llDryStatus;
    private View llAutoTest;
    private TextView tvErrH1;
    private TextView tvWarnH1;
    //    private View llConfig;
//    private View llParamSet;
//    private View llAdvanceSet;
//    private TextView tvConfig;
//    private TextView tvParamSet;
//    private TextView tvAdvanceSet;
    private TextView tvEnergyStr;
    private TextView tvPowerStr;
    private TextView tvWarningStr;
    private TextView tvResetPwd;
    private View cvEnergy;
    private View cvPower;
    private View cvWarning;
    //头部2：content1
    private List<MaxContentBean> mC1List;
    private MaxContentAdapter mC1Adapter;
    private RecyclerView mC1RecyclerView;
    private View content1Head2;
    private View title1Head2;
    private ImageView t1H2IvStatus;
    //    private String[] c1Title1 = {
//            "PV1", "PV2", "PV3", "PV4", "PV5", "PV6", "PV7", "PV8"
//    };
    private String[] c1Title1 = {
            "PV1", "PV2"
    };
    private String[] c1Title2 ;
    //头部2：content2
    private List<MaxContentBean> mC2List;
    private MaxContentAdapter mC2Adapter;
    private RecyclerView mC2RecyclerView;
    private View content2Head2;
    private View title2Head2;
    private ImageView t2H2IvStatus;
    String[] c2Title1 = {
            "Str1", "Str2", "Str3", "Str4", "Str5", "Str6", "Str7", "Str8",
            "Str9", "Str10", "Str11", "Str12", "Str13", "Str14", "Str15", "Str16"
    };
    String[] c2Title2 ;
    //头部2：content3
    private List<MaxChildBean> mC3List;
    private MaxMainChildAdapter mC3Adapter;
    private RecyclerView mC3RecyclerView;
    private View content3Head2;
    private View title3Head2;
    private ImageView t3H2IvStatus;
    String[] c3Title1 = {
            "R", "S", "T"
    };
    String[] c3Title2 ;
    //头部2：content34:SVG/APF
    private List<MaxContentBean> mC34List;
    private MaxContentAdapter mC34Adapter;
    private RecyclerView mC34RecyclerView;
    private View content34Head2;
    private View title34Head2;
    private ImageView t34H2IvStatus;
    String[] c34Title1 = {
            "R", "S", "T"
    };
    String[] c34Title2 ;
    //头部2：content4
    private List<MaxContentBean> mC4List;
    private MaxContentAdapter mC4Adapter;
    private RecyclerView mC4RecyclerView;
    private View content4Head2;
    private View title4Head2;
    private ImageView t4H2IvStatus;
    String[] c4Title1 = {
            "PID1", "PID2", "PID3", "PID4", "PID5", "PID6", "PID7", "PID8"
    };
    String[] c4Title2 ;
    //头部2：content6
    private List<MaxChildBean> mC6List;
    private MaxMainChildAdapter mC6Adapter;
    private RecyclerView mC6RecyclerView;
    private View content6Head2;
    private View title6Head2;
    private ImageView t6H2IvStatus;
    String[] c6Title1;
    private boolean isShowC6 = false;
    private List<MaxChildBean> mC6ListReal;
    //头部关于本机：content5
    private List<MaxChildBean> mC5List;
    private MaxMainChildAdapter mC5Adapter;
    private RecyclerView mC5RecyclerView;
    private View title5Head2;
    private ImageView t5H2IvStatus;
    private View content5Head2;
    String[] c5Title1 ;
    //头部关于本机
    private MaxMainMuiltAdapter mAdapter;
    private LinearLayoutManager layoutManager;
    private List<MaxMainMuiltBean> mList;
    private int prePos = -1;
    private View headerView;
    private long startTime;
    private boolean btnClick = true;
    private boolean isReceiveSucc = false;
    //    int[][] funs = {{3, 0, 124},{3, 125, 249}, {4, 0, 99}, {4, 100, 199},{4,3000,3124}};
    int[][] funs = {{3, 0, 124},{3, 125, 249},{4,3000,3124},{4,3125,3249}};
    //读机器型号功能码
    int[] deviceTypeFun = {3,100,132};
    private int count = 0;
    //所有本地获取数据集合
    private MaxDataBean mMaxData = new MaxDataBean();
    //自动刷新功能码
//    int[][] autoFun = {{4,0,124},{4,125,249},{4,3000,3124}};
    int[][] autoFun = {{4,3000,3124},{4,3125,3249}};
    int autoCount = 0;
    //开启自动刷新
    private boolean isAutoRefresh;
    //自动刷新时间
    private int autoTime = 3000;
    //显示或隐藏recycler
    private boolean isShowRecycler = false;
    private List<MaxChildBean> mGridListReal;//实际数据
    //提示问题
    private boolean promptWifi = true;//提示连接wifi模块
    private boolean promptMaxPwd = true;//提示Max设置密码
    /**
     * 降额模式
     */
    private String[] deratModes;

    /**
     * 发电量recyclerview
     */
    private String[] eleTitles;
    private int[] eleResId;
    private TLXHToolEleAdapter mEleAdapter;
    private List<TLXHEleBean> mEleList;
    private RecyclerView mEleRecycler;
    private TextView tvDetial;
    /**
     * 功率recyclerview
     */
    private String[] powerTitles;
    private int[] powerResId;
    private TLXHToolPowerAdapter mPowerAdapter;
    private List<TLXHEleBean> mPowerList;
    private RecyclerView mPowerRecycler;
    //是否读取了bdc的值
    private boolean isBDC;
    private int bdcType;//0 bdc  1 bms
    private int scroolD = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tlxhtool_main);
        ButterKnife.bind(this);
        initString();
        initHeaderView();
//        initDatas();
//        initRecyclerView();
        initRecyclerView1();
        initRecyclerViewEle();
        initRecyclerViewPower();
        initOtherView();
        initHeader1();
        initContent1();
        initContent2();
        initContent3();
        initContent34();
        initContent4();
        initContent5();
        initContent6();
        initListener();
        if (promptWifi){
            promptWifi = false;
            if (!CommenUtils.isWifi(this)){
                MaxUtil.showJumpWifiSet(this,getString(R.string.m未连接WIFI模块),getString(R.string.m请跳转连接WIFI));
            }
        }
    }




    @Override
    protected void onResume() {
        super.onResume();
    }



    @Override
    protected void onPause() {
        super.onPause();

    }


    private void initOtherView() {
        tvTitleLiwang = header2.findViewById(R.id.tvTitleLiwang);
        tvTitleBdc = header2.findViewById(R.id.tvTitleBdc);
        tvTitleBattry = header2.findViewById(R.id.tvTitleBattry);
    }

    private void initString() {
        scroolD = getResources().getDimensionPixelSize(R.dimen.dp_50);
        noteStartStr = getString(R.string.m268自动刷新);
        noteStopStr = getString(R.string.m280停止刷新);
        errNode =getString(R.string.m290请先读取故障信息);
        pidStatusStrs = new String[]{
                "",getString(R.string.all_Waiting),getString(R.string.all_Normal),getString(R.string.m故障)
        };
        statusTitles = new String[]{
                getString(R.string.all_Waiting), getString(R.string.all_Normal),
                getString(R.string.m226升级中), getString(R.string.m故障)
        };
        c1Title2 = new String[]{
                String.format("%s(V)",getString(R.string.m318电压)),
                String.format("%s(A)",getString(R.string.m319电流))
        };
        c2Title2 = new String[]{
                String.format("%s(V)",getString(R.string.m318电压)),
                String.format("%s(A)",getString(R.string.m319电流))
        };
        c3Title2 = new String[]{
                String.format("%s(V)",getString(R.string.m318电压)),
                String.format("%s(Hz)",getString(R.string.m321频率)),
                String.format("%s(A)",getString(R.string.m319电流)),
                String.format("%s(W)",getString(R.string.m320功率)),
                "PF"
        };
        c34Title2 = new String[]{
                String.format("%s(A)",getString(R.string.mCT侧电流)),
                String.format("%s(Var)",getString(R.string.mCT侧无功)),
                String.format("%s(A)",getString(R.string.mCT侧谐波量)),
                String.format("%s(Var)",getString(R.string.m补偿无功量)),
                String.format("%s(A)",getString(R.string.m补偿谐波量)),
                getString(R.string.mSVG工作状态),
        };
        c4Title2 = new String[]{
                String.format("%s(V)",getString(R.string.m318电压)),
                String.format("%s(mA)",getString(R.string.m319电流))
        };
        c6Title1 = new String[]{
                getString(R.string.m305并网倒计时),  getString(R.string.m306功率百分比),
                "ISO",
                getString(R.string.m307内部环境温度),  getString(R.string.m308Boost温度),
                getString(R.string.m309INV温度),
                "+Bus", "-Bus"
                , getString(R.string.m降额模式)
        };
        c5Title1 = new String[]{
                getString(R.string.m312厂商信息),  getString(R.string.m313机器型号),
                getString(R.string.dataloggers_list_serial),  getString(R.string.m314Model号),
                getString(R.string.m控制软件版本),  getString(R.string.m通信软件版本)
        };
//        deratModes = new String[]{
//                getString(R.string.m无降额),getString(R.string.m面板),getString(R.string.m保留),
//                getString(R.string.m电网电压),getString(R.string.m电网频率),getString(R.string.mBoost温度),
//                getString(R.string.m逆变温度),getString(R.string.m手动控制),getString(R.string.m保留),getString(R.string.m定时恢复)
//        };
        deratModes = new String[]{
                getString(R.string.m无降额),getString(R.string.PV高压降载),getString(R.string.老化固定功率降载),
                getString(R.string.电网高压降载),getString(R.string.过频降载),getString(R.string.DC源模式降载),
                getString(R.string.逆变模块过温降载),getString(R.string.有功设定限载),getString(R.string.m保留),getString(R.string.m保留)
                ,getString(R.string.内部环境过温降载),getString(R.string.外部环境过温降载),getString(R.string.线路阻抗降载)
                , getString(R.string.并机防逆流降载),getString(R.string.单机防逆流降载),getString(R.string.负载优先模式降载),getString(R.string.检测CT错反接降载)
        };
        eleTitles = new String[]{
                getString(R.string.photovoltaic_generatingcapacity)+ "\n(kWh)",
                getString(R.string.m1261Charged)+ "\n(kWh)",
                getString(R.string.m1260Discharged)+ "\n(kWh)",
                getString(R.string.m并网电量)+ "\n(kWh)",
                getString(R.string.m用户使用电量)+ "\n(kWh)"
        };
        eleResId = new int[]{
                R.drawable.tlxh_ele_fadian,R.drawable.tlxh_ele_chongdian,
                R.drawable.tlxh_ele_fangdian,R.drawable.tlxh_ele_bingwang,R.drawable.tlxh_ele_yonghushiyong
        };
        powerTitles = new String[]{
                getString(R.string.InverterAct_current_power),
                getString(R.string.m189额定功率),
                getString(R.string.m265充电功率),
                getString(R.string.m266放电功率)
        };
        powerResId = new int[]{
                R.drawable.tlxh_power_dangqian,R.drawable.tlxh_power_eding,
                R.drawable.tlxh_power_chongdian,R.drawable.tlxh_power_fangdian
        };
    }

    private void initHeaderView() {
        setHeaderImage(mHeaderView, -1, Position.LEFT, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setHeaderTitle(mHeaderView, getString(R.string.m240本地调试工具));
        setHeaderTvTitle(mHeaderView,noteStartStr, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.i("是否在自动刷新：" + isAutoRefresh);
                if (!isAutoRefresh) {
                    Mydialog.Show(mContext);
                    //读取寄存器的值
                    refresh();
                }else {
                    stopRefresh();
                }
            }
        });
    }


    /**
     * 停止刷新
     */
    private void stopRefresh() {
        isAutoRefresh = false;
        mTvRight.setText(noteStartStr);
        mHandlerReadAuto.removeMessages(SOCKET_AUTO_REFRESH);
        mHandlerReadAuto.removeMessages(SOCKET_SEND);
        //停止刷新；关闭socket
        SocketClientUtil.close(mClientUtilRead);
        SocketClientUtil.close(mClientUtil);
        SocketClientUtil.close(mClientUtilBDC);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRefresh();
    }
    private RecyclerView mControlRecyclerView;
    private MaxControlAdapter mControlAdapter;
    private List<MaxControlBean> mControlList;
    private void initHeader1() {
        tvTodayEH1 = (TextView) header1.findViewById(R.id.tvTodayEH1);
        tvTotalEH1 = (TextView) header1.findViewById(R.id.tvTotalEH1);
        tvTodayPH1 = (TextView) header1.findViewById(R.id.tvTodayPH1);
        tvTotalPH1 = (TextView) header1.findViewById(R.id.tvTotalPH1);
        tvFluxPower = (TextView) header1.findViewById(R.id.tvFluxPower);
        ivDryStatus = (ImageView) header1.findViewById(R.id.ivDryStatus);
        llDryStatus =  header1.findViewById(R.id.llDryStatus);
        tvErrH1 = (TextView) header1.findViewById(R.id.tvErrH1);
        tvWarnH1 = (TextView) header1.findViewById(R.id.tvWarnH1);
//        llParamSet = header1.findViewById(R.id.llParamSet);
//        llConfig = header1.findViewById(R.id.llConfig);
//        llAdvanceSet = header1.findViewById(R.id.llAdvanceSet);
//        tvConfig = (TextView) header1.findViewById(R.id.tvConfig);
//        tvParamSet = (TextView) header1.findViewById(R.id.tvParamSet);
//        tvAdvanceSet = (TextView) header1.findViewById(R.id.tvAdvanceSet);
        tvEnergyStr = (TextView) header1.findViewById(R.id.tvEnergyStr);
        tvPowerStr = (TextView) header1.findViewById(R.id.tvPowerStr);
        tvWarningStr = (TextView) header1.findViewById(R.id.tvWarningStr);
        tvResetPwd = (TextView) header1.findViewById(R.id.tvResetPwd);
        cvEnergy = header1.findViewById(R.id.cvEnergy);
        cvPower = header1.findViewById(R.id.cvPower);
        cvWarning = header1.findViewById(R.id.cvWarning);
        mControlRecyclerView = (RecyclerView) header1.findViewById(R.id.rvControl);
        llAutoTest = header1.findViewById(R.id.llAutoTest);
        int count= 5;
        if (END_USER == ShineToosApplication.getContext().getUser_type()){
            count=4;
        }
        mControlRecyclerView.setLayoutManager(new GridLayoutManager(this,count));
        mControlList = new ArrayList<>();
        String[] mConTitles = new String[]{
                getString(R.string.m283设置配置), getString(R.string.m284参数设置),getString(R.string.m充放电管理),
                getString(R.string.m285智能检测), getString(R.string.m286高级设置)
//                "设置配置","参数设置","智能检测","高级设置"
        };
        int[] mConImgId = new int[]{
                R.drawable.max_set, R.drawable.max_parameter, R.drawable.tlxh_control_manager,R.drawable.max_intelligent_icon,
                R.drawable.max_advance_set
        };


        if (END_USER == ShineToosApplication.getContext().getUser_type()){
            mConTitles = new String[]{
                    getString(R.string.m283设置配置), getString(R.string.m284参数设置),getString(R.string.m充放电管理),
                    getString(R.string.m285智能检测)
            };
            mConImgId = new int[]{
                    R.drawable.max_set, R.drawable.max_parameter, R.drawable.tlxh_control_manager,R.drawable.max_intelligent_icon
            };
            llAutoTest.setVisibility(View.GONE);
        }


        for (int i = 0;i<mConTitles.length;i++){
            MaxControlBean bean = new MaxControlBean();
            bean.setTitle(mConTitles[i]);
            bean.setImgId(mConImgId[i]);
            mControlList.add(bean);
        }
        mControlAdapter = new MaxControlAdapter(mControlList);
        mControlRecyclerView.setAdapter(mControlAdapter);
        mControlAdapter.setOnItemClickListener(this);
    }

    private void initContent1() {
        title1Head2 = header2.findViewById(R.id.tvTitle1);
        content1Head2 = header2.findViewById(R.id.tvContent1);
        t1H2IvStatus = (ImageView) title1Head2.findViewById(R.id.ivStatus);
        mC1List = new ArrayList<>();
        mC1RecyclerView = (RecyclerView) content1Head2.findViewById(R.id.recyclerViewC1);
        mC1RecyclerView.setLayoutManager(new GridLayoutManager(this, c1Title2.length + 1, LinearLayoutManager.HORIZONTAL, false));
        mC1Adapter = new MaxContentAdapter(R.layout.item_grid_textview_max_big_3col, mC1List);
        mC1RecyclerView.setAdapter(mC1Adapter);
        initC1Datas(c1Title1, c1Title2, null, mC1Adapter);
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
    private void initEleDatas(@NonNull String[] titles, List<String> todays, List<String> totals, TLXHToolEleAdapter adapter) {
        List<TLXHEleBean> newList = new ArrayList<>();
        for (int i =0;i<titles.length;i++){
            TLXHEleBean bean = new TLXHEleBean();
            //标题
            String todayEle = "";
            String totalEle = "";
            String unit = "";
            int contentColor = -1;

            unit = "kWh";
            contentColor = R.color.title_bg_white;
            if (todays == null){
                todayEle = "--";
            }else {
                todayEle = todays.get(i);
            }
            if (totals == null){
                totalEle = "--";
            }else {
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
        for (int i=0;i<titles.length;i++){
            TLXHEleBean bean = new TLXHEleBean();
            bean.setDrawableResId(powerResId[i]);
            bean.setTitle(titles[i]);
            if (contents == null){
                bean.setContent("--");
            }else {
                bean.setContent(contents.get(i));
            }
            newList.add(bean);
        }
        adapter.replaceData(newList);
    }

    private void initContent2() {
        title2Head2 = header2.findViewById(R.id.tvTitle2);
        content2Head2 = header2.findViewById(R.id.tvContent2);
        title2Head2.setVisibility(View.GONE);
        t2H2IvStatus = (ImageView) title2Head2.findViewById(R.id.ivStatus);
        mC2List = new ArrayList<>();
        mC2RecyclerView = (RecyclerView) content2Head2.findViewById(R.id.recyclerViewC1);
        mC2RecyclerView.setLayoutManager(new GridLayoutManager(this, c2Title2.length + 1, LinearLayoutManager.HORIZONTAL, false));
        mC2Adapter = new MaxContentAdapter(R.layout.item_grid_textview, mC2List);
        mC2RecyclerView.setAdapter(mC2Adapter);
        initC1Datas(c2Title1, c2Title2, null, mC2Adapter);

    }

    private void initContent3() {
        title3Head2 = header2.findViewById(R.id.tvTitle3);
        content3Head2 = header2.findViewById(R.id.tvContent3);
//        content3Head2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,getResources().getDimensionPixelSize(R.dimen.y130)));
        t3H2IvStatus = (ImageView) title3Head2.findViewById(R.id.ivStatus);
        mC3List = new ArrayList<>();
        mC3RecyclerView = (RecyclerView) content3Head2.findViewById(R.id.recyclerViewC1);
        int paddingH = getResources().getDimensionPixelOffset(R.dimen.dp_10);
        mC3RecyclerView.setPadding(paddingH*2,paddingH,paddingH*2,paddingH);
        mC3RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mC3Adapter = new MaxMainChildAdapter(R.layout.item_tlx_childrv, mC3List);
        mC3RecyclerView.setAdapter(mC3Adapter);
        initGridDateParam(c3Title2, null, mC3Adapter);
    }
    private void initContent34() {
        title34Head2 = header2.findViewById(R.id.tvTitle34);
        content34Head2 = header2.findViewById(R.id.tvContent34);
        content34Head2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,getResources().getDimensionPixelSize(R.dimen.dp_150)));
        t34H2IvStatus = (ImageView) title34Head2.findViewById(R.id.ivStatus);
        mC34List = new ArrayList<>();
        mC34RecyclerView = (RecyclerView) content34Head2.findViewById(R.id.recyclerViewC1);
        mC34RecyclerView.setLayoutManager(new GridLayoutManager(this, c34Title2.length + 1, LinearLayoutManager.HORIZONTAL, false));
        mC34Adapter = new MaxContentAdapter(R.layout.item_grid_textview_max_big, mC34List);
        mC34RecyclerView.setAdapter(mC34Adapter);
        initC1Datas(c34Title1, c34Title2, null, mC34Adapter);
    }

    private void initContent4() {
        title4Head2 = header2.findViewById(R.id.tvTitle4);
        title5Head2 = header2.findViewById(R.id.tvTitle5);
        t5H2IvStatus = (ImageView) title5Head2.findViewById(R.id.ivStatus);
        content5Head2 =  header2.findViewById(R.id.tvContent5);
        content4Head2 = header2.findViewById(R.id.tvContent4);
        t4H2IvStatus = (ImageView) title4Head2.findViewById(R.id.ivStatus);
        mC4List = new ArrayList<>();
        mC4RecyclerView = (RecyclerView) content4Head2.findViewById(R.id.recyclerViewC1);
        mC4RecyclerView.setLayoutManager(new GridLayoutManager(this, c4Title2.length + 1, LinearLayoutManager.HORIZONTAL, false));
        mC4Adapter = new MaxContentAdapter(R.layout.item_grid_textview, mC4List);
        mC4RecyclerView.setAdapter(mC4Adapter);
        initC1Datas(c4Title1, c4Title2, null, mC4Adapter);
    }
    private void initContent5() {
        title5Head2 = header2.findViewById(R.id.tvTitle5);
        content5Head2 = header2.findViewById(R.id.tvContent5);
        t5H2IvStatus = (ImageView) title5Head2.findViewById(R.id.ivStatus);
        mC5List = new ArrayList<>();
        mC5RecyclerView = (RecyclerView) content5Head2.findViewById(R.id.recyclerViewC1);
        int padding = getResources().getDimensionPixelOffset(R.dimen.dp_10);
        mC5RecyclerView.setPadding(padding,padding,padding,padding);
        mC5RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mC5Adapter = new MaxMainChildAdapter(R.layout.item_tlx_childrv, mC5List);
        mC5RecyclerView.setAdapter(mC5Adapter);
        //数据刷新
        initGridDateParam(c5Title1, null, mC5Adapter);
    }
    private void initContent6() {
        title6Head2 = header2.findViewById(R.id.tvTitle6);
        content6Head2 = header2.findViewById(R.id.tvContent6);
        t6H2IvStatus = (ImageView) title6Head2.findViewById(R.id.ivStatus);
        mC6List = new ArrayList<>();
        mC6RecyclerView = (RecyclerView) content6Head2.findViewById(R.id.recyclerViewC1);
        mC6RecyclerView.setLayoutManager(new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false));
        mC6Adapter = new MaxMainChildAdapter(R.layout.item_max_childrv, mC6List);
        mC6RecyclerView.setAdapter(mC6Adapter);
        //数据刷新
        initGridDateParam(c6Title1, null, mC6Adapter);
    }

    private void initRecyclerView1() {
        mGridList = new ArrayList<>();
        mGridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mGridAdapter = new MaxMainChildAdapter(R.layout.item_max_childrv, mGridList);
//        header1 = LayoutInflater.from(this).inflate(R.layout.header_maxmain2, (ViewGroup) mRecyclerView.getParent(), false);
        header2 = LayoutInflater.from(this).inflate(R.layout.header_tlxh_tool_main_head2, (ViewGroup) mRecyclerView.getParent(), false);
        header1 = header2.findViewById(R.id.head1);
//        mGridAdapter.addHeaderView(header1);
        mGridAdapter.addHeaderView(header2);
        mRecyclerView.setAdapter(mGridAdapter);
        //数据刷新
//        initGridDate(titlesGrid, null, mGridAdapter);
    }
    private void initRecyclerViewEle() {
        mEleList = new ArrayList<>();
        mEleRecycler = header2.findViewById(R.id.rvEle);
        tvDetial = header2.findViewById(R.id.tvDetial);
        tvDetial.setText(String.format("%s>",getString(R.string.commondata_title)));
        mEleRecycler.setLayoutManager(new LinearLayoutManager(this));
        mEleAdapter = new TLXHToolEleAdapter(R.layout.item_tlxh_tool_ele,mEleList);
        mEleRecycler.setAdapter(mEleAdapter);
        initEleDatas(eleTitles,null,null,mEleAdapter);
    }
    private void initRecyclerViewPower() {
        mPowerList = new ArrayList<>();
        mPowerRecycler = header2.findViewById(R.id.rvPower);
        mPowerRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));
        mPowerAdapter = new TLXHToolPowerAdapter(R.layout.item_tlxh_tool_power,mPowerList);
        mPowerRecycler.setAdapter(mPowerAdapter);
        initPowerDatas(powerTitles,null,mPowerAdapter);
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
//            mGridListReal = newList;
//            if (isShowRecycler){
//                mGridList.clear();
//                mGridList.addAll(newList);
//                adapter.notifyItemRangeChanged(1,adapter.getItemCount()-1);
//            }
//        }
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
    public void toast(String msg, int duration) {
//        Toast.makeText(this, msg, duration).show();
        toast(msg);
    }

    public void toast(String msg) {
        toast(msg, Toast.LENGTH_SHORT);
    }


    /**
     * 真正的连接逻辑
     */
    private void connectSendMsg() {
        isReceiveSucc = false;
        //1.解析输入命令
//        parseInputCommand(mEtCommand.getText().toString().trim());
        //2.tcp连接服务器
        connectServer();
//        //3.发送数据
//        sendMsg();
        //清空adapter数据
//        mSendAdapter.setNewData(new ArrayList<String>());
//        mReceiverAdapter.setNewData(new ArrayList<String>());
    }

    //连接对象
    private SocketClientUtil mClientUtil;

    private void connectServer() {
        mClientUtil = SocketClientUtil.newInstance();
        if (mClientUtil != null) {
            mClientUtil.connect(mHandler);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == WifiList.FIRSTACT_TO_WIFI) {
                //从wifi列表跳转回来，连接socket
                connectSendMsg();
            }
        }
    }


    /**
     * 刷新界面
     */
    private void refresh() {
//        handler.sendEmptyMessageDelayed(1,3000);
        connectSendMsg();

    }

    private void initListener() {
//        initOnclick(title1Head2, title2Head2, title3Head2, title4Head2, title5Head2,title6Head2,llConfig, llParamSet, cvEnergy, cvPower, cvWarning, llAdvanceSet,tvResetPwd);
        initOnclick(title1Head2, title2Head2, title3Head2, title34Head2, title4Head2,
                title5Head2,title6Head2, cvEnergy, cvPower, cvWarning,tvResetPwd,tvTitleLiwang,tvTitleBdc,tvTitleBattry,tvDetial
        ,llAutoTest);
    }

    private void initOnclick(View... views) {
        if (views != null) {
            for (View view : views) {
                view.setOnClickListener(this);
            }
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
//                            //主界面刷新完成后，刷新设备型号
//                            //刷新设备型号
//                            readTypeRegisterValue();
                            autoRefresh(mHandlerReadAuto);
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
                    BtnDelayUtil.dealTLXBtn(this, what, 2500, mContext, mTvRight);
                    break;
            }
//            if (msg.what != SocketClientUtil.SOCKET_SEND && msg.what != 100 && msg.what != 101 && what != 6 && what != SOCKET_RECEIVE_BYTES) {
//                toast(text);
//            }
        }
    };

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
                    byte[] sendBytesR = sendMsgBDC(mClientUtilBDC, funs[2]);
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
                            RegisterParseUtil.parseInput3kT3124(mMaxData, bytes);
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
                    BtnDelayUtil.dealMaxBtn(this,what,mContext);
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
    //连接对象:用于读取数据
    private SocketClientUtil mClientUtilBDC;

    private void connectServerBDC() {
        stopRefresh();
        Mydialog.Show(mContext);
        mClientUtilBDC = SocketClientUtil.connectServer(mHandler3124);
    }
    /**
     * 刷新完成
     */
    private void refreshFinish() {
//        Mydialog.Dismiss();
    }

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
        initEleDatas(eleTitles,todayEle,totalEle,mEleAdapter);
        //功率设置
        List<String> powers = new ArrayList<>();
        powers.add(String.valueOf(mMaxData.getNormalPower()));
        powers.add(String.valueOf(mMaxData.getTotalPower()));
        powers.add(String.valueOf(storageBeen.getpCharge()));
        powers.add(String.valueOf(storageBeen.getpDischarge()));
        initPowerDatas(powerTitles,powers,mPowerAdapter);
        //装机功率
//        tvTotalPH1.setText(mMaxData.getTotalPower() + "W");
//        tvTodayPH1.setText(mMaxData.getNormalPower() + "W");
//        tvTodayEH1.setText(mMaxData.getTodayEnergy() + "kWh");
//        tvTotalEH1.setText(mMaxData.getTotalEnergy() + "kWh");
        //顺逆流功率
        double pusertogrid = mMaxData.getPusertogrid();
        String fluxStr = "0W";
        if (pusertogrid > 0){
            fluxStr = String.format("%s %sW",getString(R.string.m顺流), String.valueOf(pusertogrid));
        }else if (pusertogrid < 0){
            fluxStr = String.format("%s %sW",getString(R.string.m逆流), String.valueOf(Math.abs(pusertogrid)));
        }
        tvFluxPower.setText(fluxStr);
        //干接点
        ivDryStatus.setImageResource(mMaxData.getDryStatus()==0?R.drawable.ganjiedian_off:R.drawable.ganjiedian_on);
        //干接点
        if (mMaxData.getDryStatus() == 0){
            ivDryStatus.setImageResource(R.drawable.ganjiedian_off);
            llDryStatus.setBackgroundResource(R.drawable.shape_gray_corner_360bg);
        }else {
            ivDryStatus.setImageResource(R.drawable.ganjiedian_on);
            llDryStatus.setBackgroundResource(R.drawable.shape_green_corner_360bg);
        }
        //故障告警
        int errCode = mMaxData.getErrCode();
        int warmCode = mMaxData.getWarmCode();
        int errCodeSecond = mMaxData.getErrCodeSecond();
        int warmCodeSecond = mMaxData.getWarmCodeSecond();
        //添加副码
        String errCodeStr = "";
        String warnCodeStr = "";
        if (errCode >= 200 || warmCode >= 200){
            errCodeStr = String.format("%d(%02d)",errCode,errCodeSecond);
            warnCodeStr = String.format("%d(%02d)",warmCode,warmCodeSecond);
        }else {
            if (errCode == 0){
                errCodeStr = getString(R.string.m351无故障信息);
            }else {
                errCode+=99;
                errCodeStr = String.format("%d(%02d)",errCode,errCodeSecond<100?errCodeSecond:errCodeSecond%100);
            }
            if (warmCode == 0){
                warnCodeStr = getString(R.string.m352无警告信息);
            }else {
                warmCode+=99;
                warnCodeStr = String.format("%d(%02d)",warmCode,warmCodeSecond<100?warmCodeSecond:warmCodeSecond%100);
            }
        }
        tvErrH1.setText(errCodeStr);
        tvWarnH1.setText(warnCodeStr);
        //状态
        int status = mMaxData.getStatus();
        String statusStr = "";
        if (status >= 0 && status < statusTitles.length){
            statusStr = statusTitles[status];
        }else {
            statusStr = "状态:"+status;
        }
        mTvTitle.setText(statusStr);
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
        initGridDateParam(c3Title2, nowACList.toArray(new String[nowACList.size()]), mC3Adapter);
        //ac电压电流
        initC1Datas(c4Title1, c4Title2, mMaxData.getPIDList(), mC4Adapter);
        //svg
        initC1Datas(c34Title1, c34Title2, mMaxData.getSVGList(), mC34Adapter);
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
                RegisterParseUtil.parseHold125T249(mMaxData, bytes);
                break;
//            case 2:
//                RegisterParseUtil.parseMax2(mMaxData, bytes);
//                break;
//            case 3:
//                RegisterParseUtil.parseMax3(mMaxData, bytes);
//                break;
            case 2:
                RegisterParseUtil.parseInput3kT3124(mMaxData, bytes);
                isBDC = true;
                break;
            case 3:
                RegisterParseUtil.parseInput3125T3249(mMaxData, bytes);
                break;
        }
    }
    /**
     * 解析数据
     *
     * @param bytes
     * @param count
     */
    private void parseMaxAuto(byte[] bytes, int count) {
        switch (count) {
//            case 0:
//                RegisterParseUtil.parseMax4T0T125(mMaxData,bytes);
//                break;
//            case 1:
//                RegisterParseUtil.parseMax4T125T250(mMaxData,bytes);
//                break;
            case 0:
                RegisterParseUtil.parseInput3kT3124(mMaxData,bytes);
                break;
            case 1:
                RegisterParseUtil.parseInput3125T3249(mMaxData,bytes);
                break;
        }
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

    @Override
    public void onClick(View v) {
        if (v == title1Head2) {
            showOrHideView(content1Head2, t1H2IvStatus);
        } else if (v == title2Head2) {
            showOrHideView(content2Head2, t2H2IvStatus);
        } else if (v == title3Head2) {
            showOrHideView(content3Head2, t3H2IvStatus);
        } else if (v == title34Head2) {
            showOrHideView(content34Head2, t34H2IvStatus);
        } else if (v == title4Head2) {
            showOrHideView(content4Head2, t4H2IvStatus);
        } else if (v == title5Head2) {
            showOrHideView(content5Head2, t5H2IvStatus);
//            showOrHideRecycler(mGridAdapter,t5H2IvStatus);
        } else if (v == title6Head2) {
            showOrHideView(content6Head2,t6H2IvStatus);
//            showOrHideRecyclerParams(mC6Adapter,t6H2IvStatus);
        }else if (v == tvResetPwd) {
            jumpMaxSet(MaxOssPwdActivity.class,tvResetPwd.getText().toString());
        }
        else if (v == cvEnergy) {
            jumpMaxSet(MaxChartEnergyActivity.class,tvEnergyStr.getText().toString());
        }
        else if (v == tvTitleLiwang) {
            EventBus.getDefault().postSticky(mMaxData);
            jumpMaxSet(TLXHLiwangActivity.class,"");
        }
        else if (v == tvTitleBdc) {
            bdcType = 0;
            if (!isBDC){
                connectServerBDC();
                return;
            }
            jumpBDC();
        }
        else if (v == tvTitleBattry) {
            bdcType = 1;
            if (!isBDC){
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
        else if (v == cvWarning) {
//            if (!TextUtils.isEmpty(tvErrH1.getText().toString())) {
//                stopRefresh();
//                Intent intent = new Intent(mContext, TLXWarningActivity.class);
//                intent.putExtra("title", tvWarningStr.getText().toString());
//                intent.putExtra("errCode", mMaxData.getErrCode());
//                intent.putExtra("warmCode", mMaxData.getWarmCode());
//                intent.putExtra("errCodeSecond", mMaxData.getErrCodeSecond());
//                intent.putExtra("warmCodeSecond", mMaxData.getWarmCodeSecond());
//                intent.putExtra("type", 1);
//                intent.putExtra("error1", mMaxData.getError1());
//                intent.putExtra("error2", mMaxData.getError2());
//                jumpTo(intent, false);
//            } else {
//                toast(errNode);
//            }
        }else if (v == tvDetial){
            jumpMaxSet(MaxChartEnergyActivity.class,getString(R.string.m201电量));
        }else if (v == llAutoTest){
            stopRefresh();
            jumpTo(TLXHAutoTestActivity.class,false);
        }

    }
    public void jumpBDC(){
        if (isBDC && mMaxData.getBdcStatus() == 0){
            MyControl.circlerDialog(this,getString(R.string.没有接入BDC),-1,false);
            return;
        }
        if (bdcType == 0){
            EventBus.getDefault().postSticky(mMaxData);
            jumpMaxSet(TLXHBDCActivity.class,"");
        }else if (bdcType == 1){
            EventBus.getDefault().postSticky(mMaxData);
            jumpMaxSet(TLXHBattryActivity.class,"");
        }
    }
    /**
     * 跳转到Max各设置界面
     */
    private void jumpMaxSet(Class clazz, String title) {
        stopRefresh();
        Intent intent = new Intent(mContext, clazz);
        intent.putExtra("title", title);
        jumpTo(intent, false);
    }

    @SuppressLint("HandlerLeak")
    Handler scrollHandle = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    mRecyclerView.scrollBy(0,scroolD);
                    break;
            }
        }
    };
    public void showOrHideView(View view, ImageView iv) {
        if (view.getVisibility() == View.GONE) {
            view.setVisibility(View.VISIBLE);
            iv.setImageResource(R.drawable.max_up);
            try {
                if (view == content5Head2){
                    scrollHandle.sendEmptyMessageDelayed(0,40);
                }else {
                    mRecyclerView.scrollBy(0,scroolD);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            view.setVisibility(View.GONE);
            iv.setImageResource(R.drawable.max_down);
        }
    }
    /**
     * 显示或者隐藏关于本机
     * @param mAdapter
     * @param iv
     */
    public void showOrHideRecycler(MaxMainChildAdapter mAdapter, ImageView iv) {
        if (!isShowRecycler) {
            isShowRecycler = true;
            mGridList.clear();
            mGridList.addAll(mGridListReal);
            iv.setImageResource(R.drawable.max_up);
        } else {
            isShowRecycler = false;
            mGridList.clear();
            iv.setImageResource(R.drawable.max_down);
        }
//        mAdapter.notifyItemRangeChanged(1,mAdapter.getItemCount()-1);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());
    }

    /**
     * 显示或者隐藏内部参数
     * @param mAdapter
     * @param iv
     */
    public void showOrHideRecyclerParams(MaxMainChildAdapter mAdapter, ImageView iv) {
        if (!isShowC6) {
            isShowC6 = true;
            mC6List.clear();
            mC6List.addAll(mC6ListReal);
            iv.setImageResource(R.drawable.max_up);
        } else {
            isShowC6 = false;
            mC6List.clear();
            iv.setImageResource(R.drawable.max_down);
        }
        mAdapter.notifyDataSetChanged();
    }
    /**
     * 自动刷新
     */
    //连接对象:用于读取数据
    private SocketClientUtil mClientUtilRead;
    //读取寄存器的值
    private void autoReadRegisterValue() {
        isAutoRefresh = true;
        mTvRight.setText(noteStopStr);
        mClientUtilRead = SocketClientUtil.connectServerAuto(mHandlerReadAuto);
    }
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
                            //自动刷新
//                            autoRefresh(this);
                            this.sendEmptyMessageDelayed(SocketClientUtil.SOCKET_SEND,3000);
                            //更新ui
                            refreshUI();
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
                case SocketClientUtil.SOCKET_AUTO_REFRESH:
                    autoReadRegisterValue();
                    break;
                case SocketClientUtil.SOCKET_CLOSE:
                    this.removeMessages(SocketClientUtil.SOCKET_AUTO_REFRESH);
                    isAutoRefresh = false;
                    mTvRight.setText(noteStartStr);
                    break;
            }
        }
    };

    /**
     * 自动刷新
     * @param handler
     */
    public void autoRefresh(Handler handler){
        if (handler != null) {
            isAutoRefresh = true;
            mTvRight.setText(noteStopStr);
            handler.sendEmptyMessageDelayed(SOCKET_AUTO_REFRESH, autoTime);
        }else {
            mTvRight.setText(noteStartStr);
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
        if (commSoftVersion == null) commSoftVersion = "";
        datasAbout[5] = String.format("%s-%04d",commSoftVersion,deviceBeen.getCommSoftVersionValue());
        initGridDate(c5Title1, datasAbout, mC5Adapter);
    }

    @Override
    public void onItemClick(final BaseQuickAdapter adapter, View view, final int position) {
        if (adapter == mControlAdapter){
            MaxControlBean item = ((MaxControlAdapter) adapter).getItem(position);
            final String title = item.getTitle();
            final Class clazz;
            switch (position){
                case 0:
                    clazz = TLXHToolConfigActivity.class;
                    break;
                case 1:
                    clazz = TLXHToolParamActivity.class;
                    break;
                case 2:
                    clazz = TLXToolChargeManagerActivity.class;
                    break;
                case 3:
                    clazz = MaxCheckActivity.class;
                    break;
                case 4:
                    clazz = USAdvanceSetActivity.class;
                    break;
                default:
                    clazz = null;
                    break;
            }


            if (clazz != null) {
                jumpMaxSet(clazz, title);
            }

       /*     if (GlobalConstant.MAX_NEED_PWD_FALSE.equals(SharedPreferencesUnit.getInstance(mContext).get(GlobalConstant.MAX_NEED_PWD))){
                if (clazz != null) {
                    jumpMaxSet(clazz, title);
                }
            }else {
                //判断有无设置密码
                MaxUtil.getControlMaxPwd(TLXHToolMainActivity.this, new OnHandlerStrListener() {
                    @Override
                    public void handlerDealStr(String result) {
                        if ("-1".equals(result)) {
                            //验证密码
                            if (promptMaxPwd) {
                                MaxUtil.showSetMaxPwd(TLXHToolMainActivity.this, new OnHandlerStrListener() {
                                    @Override
                                    public void handlerDealStr(String result) {
                                        if ("1".equals(result)) {
                                            //密码验证成功：跳转界面：不在提示
                                            promptMaxPwd = false;
                                            if (clazz != null) {
                                                jumpMaxSet(clazz, title);
                                            }
                                        } else {
//                                            OssUtils.circlerDialog(MaxMain2Activity.this, getString(R.string.m密码错误), -1, false);
                                            new CircleDialog.Builder()
                                                    .setWidth(0.7f)
                                                    .setTitle(getString(R.string.温馨提示))
                                                    .setText(getString(R.string.m密码错误))
                                                    .setPositive(getString(R.string.m重置密码), new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            jumpMaxSet(MaxOssPwdActivity.class, "");
                                                        }
                                                    })
                                                    .show(getSupportFragmentManager());
                                        }
                                    }
                                });
                            } else {
                                if (clazz != null) {
                                    jumpMaxSet(clazz, title);
                                }
                            }
                        } else {
                            Class clazz = null;
                            if ("0".equals(result)) {//通过Oss账号获取
                                clazz = MaxOssPwdActivity.class;
                            } else if ("1".equals(result)) {//初始密码
                                clazz = MaxOssPwdActivity.class;
                            }
                            if (clazz != null){
                                stopRefresh();
                                Intent intent = new Intent(mContext,clazz);
                                intent.putExtra("type",result);
                                jumpTo(intent,false);
                            }
                        }
                    }
                });
            }*/
        }
    }
}
