package com.growatt.shinetools.module.localbox.ustool;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import com.growatt.shinetools.ShineToosApplication;
import com.growatt.shinetools.adapter.MaxMainChildAdapter;
import com.growatt.shinetools.adapter.TLXHToolEleAdapter;
import com.growatt.shinetools.adapter.TLXHToolPowerAdapter;
import com.growatt.shinetools.adapter.UsParamsetAdapter;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.MaxWifiParseUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.modbusbox.bean.MaxDataBean;
import com.growatt.shinetools.modbusbox.bean.ToolStorageDataBean;
import com.growatt.shinetools.module.inverterUpdata.InverterUpdataManager;
import com.growatt.shinetools.module.inverterUpdata.UpgradePath;
import com.growatt.shinetools.module.localbox.configtype.MainsCodeParamSetActivity;
import com.growatt.shinetools.module.localbox.configtype.usconfig.USChargeActivity;
import com.growatt.shinetools.module.localbox.configtype.usconfig.USParamsSettingActivity;
import com.growatt.shinetools.module.localbox.max.MaxChartEnergyActivity;
import com.growatt.shinetools.module.localbox.max.MaxCheckActivity;
import com.growatt.shinetools.module.localbox.max.bean.MaxChildBean;
import com.growatt.shinetools.module.localbox.tlxh.bean.TLXHEleBean;
import com.growatt.shinetools.module.localbox.ustool.bean.UsToolParamBean;
import com.growatt.shinetools.module.localbox.ustool.config.USFastConfigAcitivity;
import com.growatt.shinetools.module.localbox.ustool.errorcode.ErrorCode;
import com.growatt.shinetools.utils.ActivityUtils;
import com.growatt.shinetools.utils.BtnDelayUtil;
import com.growatt.shinetools.utils.CircleDialogUtils;
import com.growatt.shinetools.utils.CommenUtils;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.Mydialog;
import com.growatt.shinetools.widget.GridDivider;
import com.growatt.shinetools.widget.LinearDivider;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;

import static com.growatt.shinetools.constant.GlobalConstant.END_USER;
import static com.growatt.shinetools.constant.GlobalConstant.KEFU_USER;
import static com.growatt.shinetools.constant.GlobalConstant.MAINTEAN_USER;
import static com.growatt.shinetools.modbusbox.SocketClientUtil.SOCKET_AUTO_REFRESH;
import static com.growatt.shinetools.modbusbox.SocketClientUtil.SOCKET_RECEIVE_BYTES;
import static com.growatt.shinetools.modbusbox.SocketClientUtil.SOCKET_SEND;

public class USToolsMainActivityV2 extends BaseActivity implements Toolbar.OnMenuItemClickListener, View.OnClickListener, BaseQuickAdapter.OnItemClickListener {
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
    @BindView(R.id.tv_status)
    TextView tvStatus;


    private TextView tvFluxPower;
    private ImageView ivDryStatus;
    private View llDryStatus;
    private TextView tvErrH1;
    private TextView tvWarnH1;


    private MenuItem item;

    private String noteStartStr;
    private String noteStopStr;
    private String errNode;
    String[] pidStatusStrs;
    //????????????
    String[] statusTitles;
    private int [] statusColors;
    private int [] drawableStatus;

    private boolean isReceiveSucc = false;
    private long startTime;
    private boolean btnClick = true;
    private int count = 0;
    //???????????????bdc??????
    private boolean isBDC;
    private int scroolD = 100;
    private String[] c1Title2;
    String[] c2Title1 = {
            "Str1", "Str2", "Str3", "Str4", "Str5", "Str6", "Str7", "Str8",
            "Str9", "Str10", "Str11", "Str12", "Str13", "Str14", "Str15", "Str16"
    };
    String[] c2Title2;
    String[] c3Title1 = {
            "R", "S", "T"
    };
    String[] c3Title2;
    String[] c34Title1 = {
            "R", "S", "T"
    };
    String[] c34Title2;
    String[] c4Title1 = {
            "PID1", "PID2", "PID3", "PID4", "PID5", "PID6", "PID7", "PID8"
    };
    String[] c4Title2;
    String[] c6Title1;
    String[] c5Title1;
    private String[] deratModes;

    int[][] funs = {{3, 0, 124}, {3, 125, 249}, {3, 3000, 3124}, {4, 3000, 3124}, {4, 3125, 3249}};
    //??????????????????????????????
    private MaxDataBean mMaxData = new MaxDataBean();


    //????????????
    private LinearLayoutManager mLayoutManager;
    private List<MaxChildBean> mGridList;
    private View header;
    private MaxMainChildAdapter mAdapter;

    //?????????
    private UsParamsetAdapter usParamsetAdapter;
    private List<UsToolParamBean> mSettingList=new ArrayList<>();
    private RecyclerView mRvParamset;


    /**
     * ?????????recyclerview
     */
    private String[] eleTitles;
    private String[] powerTitles;
    private int[] powerResId;
    private int[] eleResId;
    private TLXHToolEleAdapter mEleAdapter;
    private List<TLXHEleBean> mEleList;
    private RecyclerView mEleRecycler;
    private TextView tvDetial;
    private ImageView ivDetail;


    /**
     * ??????recyclerview
     */
    private TLXHToolPowerAdapter mPowerAdapter;
    private List<TLXHEleBean> mPowerList;
    private RecyclerView mPowerRecycler;

    private CardView cvWarning;
    public int user_type = KEFU_USER;

    //????????????
    private boolean promptWifi = true;//????????????wifi??????
    private boolean promptMaxPwd = true;//??????Max????????????
    private int bdcType;//0 bdc  1 bms
    //?????????????????????
//    int[][] autoFun = {{4,0,124},{4,125,249},{4,3000,3124}};
    int[][] autoFun = {{4, 3000, 3124}, {4, 3125, 3249}};
    int autoCount = 0;
    //??????????????????
    private boolean isAutoRefresh;
    //??????????????????
    private int autoTime = 3000;

    //????????????????????????
    private boolean needFresh;

    //bdc?????????
    private int bdcNumber = 0;

    //BDC????????????0???????????????03?????????
    private int[][] bdcChargeFuns = {{4, 3165, 3233}};
    private int[][] bdcDisChargeFuns = {{4, 3165, 3233}};
    private int[][] bdcAllChargeFuns = {{0, 0, 0}};
    private int bdcChargePower = 0;
    private int bdcDisChargePower = 0;

    @Override
    protected int getContentView() {
        return R.layout.activity_us_tools_v2;
    }

    @Override
    protected void initViews() {
        initString();

        //???????????????
//        initToobar(toolbar);

        toolbar.setNavigationIcon(R.drawable.icon_return);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CircleDialogUtils.showCommentDialog(USToolsMainActivityV2.this, getString(R.string.??????????????????),
                        getString(R.string.m???????????????????????????), getString(R.string.android_key1935),
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


        tvTitle.setText("MIN TL-XH-US");
        toolbar.inflateMenu(R.menu.comment_right_menu);
        item = toolbar.getMenu().findItem(R.id.right_action);
        item.setTitle(noteStartStr);
        toolbar.setOnMenuItemClickListener(this);
        //????????????
        mGridList = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MaxMainChildAdapter(R.layout.item_max_childrv, mGridList);
        header = LayoutInflater.from(this).inflate(R.layout.header_us_tool_main_v2, (ViewGroup) mRecyclerView.getParent(), false);
        mAdapter.addHeaderView(header);
        mRecyclerView.setAdapter(mAdapter);

        //??????
        initRecyclerViewEle();

        //??????
        initRecyclerViewPower();
        //?????????
        initSettingRecycleView();

        initListener();

        Mydialog.Show(mContext);
        //?????????????????????
        refresh();
    }


    private void initString() {
        scroolD = getResources().getDimensionPixelSize(R.dimen.dp_50);
        noteStartStr = getString(R.string.m268????????????);
        noteStopStr = getString(R.string.m280????????????);
        errNode = getString(R.string.m290????????????????????????);
        pidStatusStrs = new String[]{
                "", getString(R.string.all_Waiting), getString(R.string.all_Normal), getString(R.string.m??????)
        };
        statusTitles = new String[]{
                getString(R.string.all_Waiting), getString(R.string.m206???????????????),
                 getString(R.string.m??????), getString(R.string.m226?????????)
        };

        statusColors=new int[]{
          R.color.color_status_wait,R.color.color_status_grid,R.color.color_status_fault,R.color.color_status_upgrade
        };

        drawableStatus=new int[]{
                R.drawable.circle_wait,R.drawable.circle_grid,R.drawable.circle_fault, R.drawable.circle_upgrade
        };

        c1Title2 = new String[]{
                String.format("%s(V)", getString(R.string.m318??????)),
                String.format("%s(A)", getString(R.string.m319??????)),
                String.format("%s(W)", getString(R.string.m320??????))
        };
        c2Title2 = new String[]{
                String.format("%s(V)", getString(R.string.m318??????)),
                String.format("%s(A)", getString(R.string.m319??????))
        };
        c3Title2 = new String[]{
                String.format("%s(V)", getString(R.string.m318??????)),
                String.format("%s(Hz)", getString(R.string.m321??????)),
                String.format("%s(A)", getString(R.string.m319??????)),
                String.format("%s(W)", getString(R.string.m320??????)),
                "PF"
        };
        c34Title2 = new String[]{
                String.format("%s(A)", getString(R.string.mCT?????????)),
                String.format("%s(Var)", getString(R.string.mCT?????????)),
                String.format("%s(A)", getString(R.string.mCT????????????)),
                String.format("%s(Var)", getString(R.string.m???????????????)),
                String.format("%s(A)", getString(R.string.m???????????????)),
                getString(R.string.mSVG????????????),
        };
        c4Title2 = new String[]{
                String.format("%s(V)", getString(R.string.m318??????)),
                String.format("%s(mA)", getString(R.string.m319??????))
        };
        c6Title1 = new String[]{
                getString(R.string.m305???????????????), getString(R.string.m306???????????????),
                "ISO",
                getString(R.string.m307??????????????????), getString(R.string.m308Boost??????),
                getString(R.string.m309INV??????),
                "+Bus", "-Bus"
                , getString(R.string.m????????????)
        };
        c5Title1 = new String[]{
                getString(R.string.m312????????????), getString(R.string.m313????????????),
                getString(R.string.dataloggers_list_serial), getString(R.string.m314Model???),
                getString(R.string.m??????????????????), getString(R.string.m??????????????????), getString(R.string.bdc????????????),
                getString(R.string.battery_version)
        };
//        deratModes = new String[]{
//                getString(R.string.m?????????),getString(R.string.m??????),getString(R.string.m??????),
//                getString(R.string.m????????????),getString(R.string.m????????????),getString(R.string.mBoost??????),
//                getString(R.string.m????????????),getString(R.string.m????????????),getString(R.string.m??????),getString(R.string.m????????????)
//        };
        deratModes = new String[]{
                getString(R.string.m?????????), getString(R.string.PV????????????), getString(R.string.????????????????????????),
                getString(R.string.??????????????????), getString(R.string.????????????), getString(R.string.DC???????????????),
                getString(R.string.????????????????????????), getString(R.string.??????????????????), getString(R.string.m??????), getString(R.string.m??????)
                , getString(R.string.????????????????????????), getString(R.string.????????????????????????), getString(R.string.??????????????????)
                , getString(R.string.?????????????????????), getString(R.string.?????????????????????), getString(R.string.????????????????????????), getString(R.string.??????CT???????????????)
        };
        eleTitles = new String[]{
                getString(R.string.android_key2019) + "\n" + "(kWh)",
                getString(R.string.android_key2371) + "\n(kWh)",
                getString(R.string.android_key2370) + "\n(kWh)",
                getString(R.string.android_key1319) + "\n(kWh)",
                getString(R.string.android_key1320) + "\n(kWh)",
        };
        eleResId = new int[]{
                R.drawable.tlxh_ele_fadian, R.drawable.tlxh_ele_chongdian,
                R.drawable.tlxh_ele_fangdian, R.drawable.tlxh_ele_bingwang, R.drawable.tlxh_ele_yonghushiyong
        };
        powerTitles = new String[]{
                getString(R.string.android_key1993),
                getString(R.string.????????????),
                getString(R.string.android_key1807),
                getString(R.string.android_key1824)
        };
        powerResId = new int[]{
                R.drawable.tlxh_power_dangqian, R.drawable.tlxh_power_eding,
                R.drawable.tlxh_power_chongdian, R.drawable.tlxh_power_fangdian
        };
    }


    /**
     * ???????????????
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
    }


    /**
     * ????????????
     */
    private void initRecyclerViewEle() {
        mEleList = new ArrayList<>();
        mEleRecycler = header.findViewById(R.id.rvEle);
        tvDetial = header.findViewById(R.id.tvDetial);
        ivDetail = header.findViewById(R.id.iv_detail);
        tvDetial.setText(String.format("%s>", getString(R.string.commondata_title)));
        mEleRecycler.setLayoutManager(new LinearLayoutManager(this));
        LinearDivider linearDivider = new LinearDivider(this, LinearLayoutManager.VERTICAL, 1, ContextCompat.getColor(this, R.color.gray_aaaaaa));
        mEleAdapter = new TLXHToolEleAdapter(R.layout.item_tlxh_tool_ele_v2, mEleList);
        mEleRecycler.addItemDecoration(linearDivider);
        mEleRecycler.setAdapter(mEleAdapter);
        initEleDatas(eleTitles, null, null, mEleAdapter);
    }


    private void initRecyclerViewPower() {
        mPowerList = new ArrayList<>();
        mPowerRecycler = header.findViewById(R.id.rvPower);
        mPowerRecycler.setLayoutManager(new GridLayoutManager(this,powerTitles.length));
        mPowerAdapter = new TLXHToolPowerAdapter(R.layout.item_tlxh_tool_power, mPowerList);
        mPowerRecycler.setAdapter(mPowerAdapter);
        initPowerDatas(powerTitles, null, mPowerAdapter);

        tvFluxPower = header.findViewById(R.id.tvFluxPower);
        ivDryStatus = header.findViewById(R.id.ivDryStatus);
        llDryStatus = header.findViewById(R.id.llDryStatus);
        tvErrH1 = header.findViewById(R.id.tv_fault_value);
        tvWarnH1 = header.findViewById(R.id.tv_warn_value);
        cvWarning = header.findViewById(R.id.cvWarning);

    }


    @Override
    protected void initData() {

        user_type= ShineToosApplication.getContext().getUser_type();
        String[] title = new String[]{
                getString(R.string.????????????), getString(R.string.????????????), getString(R.string.android_key3056)
                , getString(R.string.android_key1308), getString(R.string.m285????????????), getString(R.string.m284????????????)
                , getString(R.string.m286????????????), getString(R.string.m291????????????)
        };

        int[] res = new int[]{
                R.drawable.quickly, R.drawable.system_config, R.drawable.city_code,
                R.drawable.charge_manager, R.drawable.smart_check, R.drawable.param_setting,
                R.drawable.advan_setting, R.drawable.device_info
        };

        if (user_type == END_USER||user_type==MAINTEAN_USER) {
            title = new String[]{
                    getString(R.string.????????????), getString(R.string.????????????), getString(R.string.android_key3056)
                    , getString(R.string.android_key1308), getString(R.string.m285????????????), getString(R.string.m284????????????)
                    , getString(R.string.m291????????????)
            };


            res = new int[]{
                    R.drawable.quickly, R.drawable.system_config, R.drawable.city_code,
                    R.drawable.charge_manager, R.drawable.smart_check, R.drawable.param_setting,
                    R.drawable.device_info
            };

        }


        List<UsToolParamBean> usSetItems = new ArrayList<>();
        for (int i = 0; i < title.length; i++) {
            UsToolParamBean bean = new UsToolParamBean();
            bean.setIcon(res[i]);
            bean.setTitle(title[i]);
            usSetItems.add(bean);
        }
        usParamsetAdapter.replaceData(usSetItems);


        //????????????wifi
        if (promptWifi) {
            promptWifi = false;
            if (!CommenUtils.isWifi(this)) {
                MaxUtil.showJumpWifiSet(this, getString(R.string.m?????????WIFI??????), getString(R.string.m???????????????WIFI));
            }
        }

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


    private void initEleDatas(@NonNull String[] titles, List<String> todays, List<String> totals, TLXHToolEleAdapter adapter) {
        List<TLXHEleBean> newList = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            TLXHEleBean bean = new TLXHEleBean();
            //??????
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


    /**
     * ????????????
     */
    private void refresh() {
//        handler.sendEmptyMessageDelayed(1,3000);
        connectSendMsg();
    }

    /**
     * ?????????????????????
     */
    private void connectSendMsg() {
        isReceiveSucc = false;
        //1.??????????????????
//        parseInputCommand(mEtCommand.getText().toString().trim());
        //2.tcp???????????????
        connectServer();
//        //3.????????????
//        sendMsg();
        //??????adapter??????
//        mSendAdapter.setNewData(new ArrayList<String>());
//        mReceiverAdapter.setNewData(new ArrayList<String>());
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

                    LogUtil.i(text);

                    break;
                case SocketClientUtil.SOCKET_CLOSE:
                    text = "????????????";
                    break;
                case SocketClientUtil.SOCKET_OPEN:
                    text = "????????????";
                    break;
                case SocketClientUtil.SOCKET_SEND_MSG:
                    BtnDelayUtil.sendMessage(this);
                    //??????????????????
                    startTime = System.currentTimeMillis();
                    //????????????
                    btnClick = false;
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
                            mHandler.sendEmptyMessage(SOCKET_SEND);
                        } else {
                            //??????ui
                            refreshUI();
                            count = 0;
                            //????????????
                            SocketClientUtil.close(mClientUtil);
                            refreshFinish();
//                            //?????????????????????????????????????????????
//                            //??????????????????
//                            readTypeRegisterValue();
                            if (needFresh) {
                                autoRefresh(mHandlerReadAuto);
                            } else {
                                LogUtil.i("bdcNumber:" + bdcNumber);
                                if (mMaxData.getBdcStatus() != 0&&bdcNumber > 1) {
                                    //???????????????
                                    bdcChargePower = 0;
                                    bdcDisChargePower = 0;
                                    LogUtil.i("????????????bdc??????");
                                    readBdcValue();
                                }else {
                                    checkUpdata();
                                }

                            }
                        }
//                        else {
//                            toast(R.string.all_failed);
//                            count = 0;
//                            //????????????
//                            SocketClientUtil.close(mClientUtil);
//                        }
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
                case SOCKET_SEND:
                    if (count < funs.length) {
                        sendMsg(mClientUtil, funs[count]);
                    }
                    break;
                case 100://??????????????????
                    btnClick = true;
                    String myuuid = (String) msg.obj;
                    if (uuid.equals(myuuid) && !isReceiveSucc) {
                        toast("??????????????????????????????");
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
//            if (msg.what != SocketClientUtil.SOCKET_SEND && msg.what != 100 && msg.what != 101 && what != 6 && what != SOCKET_RECEIVE_BYTES) {
//                toast(text);
//            }
        }
    };


    //?????????????????????
    private void readBdcValue() {
        toReadBdc();
    }


    //????????????:??????????????????
    private SocketClientUtil mReadBdcUtil;

    private void toReadBdc() {
        Mydialog.Show(mContext);
        mReadBdcUtil = SocketClientUtil.connectServer(bdcHadler);
    }


    private int pos = 0;
    Handler bdcHadler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case SocketClientUtil.SOCKET_EXCETION_CLOSE:
                    break;
                case SocketClientUtil.SOCKET_CLOSE:
                    break;
                case SocketClientUtil.SOCKET_OPEN:
                    break;
                case SocketClientUtil.SOCKET_SEND_MSG:
                    BtnDelayUtil.sendMessage(this);
                    //?????????????????????????????????????????????
                    uuid = UUID.randomUUID().toString();
                    Message msgSend = Message.obtain();
                    msgSend.what = 100;
                    msgSend.obj = uuid;
                    bdcHadler.sendEmptyMessageDelayed(100, 3000);

                    String sendMsg = (String) msg.obj;
                    LogUtil.i("????????????:" + sendMsg);
                    break;
                case SocketClientUtil.SOCKET_RECEIVE_MSG:
                    break;
                //??????????????????
                case SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //?????????????????????
                        boolean isCheck = ModbusUtil.checkModbus(bytes);
                        if (isCheck) {
                            parseBdc(bytes, pos);
                        }

                        if (pos < bdcAllChargeFuns.length - 1) {
                            pos++;
                            bdcHadler.sendEmptyMessage(SOCKET_SEND);
                        } else {
                            //??????ui
                            freshPower();
                            pos = 0;
                            //????????????
                            SocketClientUtil.close(mReadBdcUtil);
                            refreshFinish();
                            checkUpdata();
                        }

                        LogUtil.i("????????????:" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        pos = 0;
                        //????????????
                        SocketClientUtil.close(mReadBdcUtil);
                        Mydialog.Dismiss();
                    }
                    break;
                case SocketClientUtil.SOCKET_CONNECT:
                    break;
                case SOCKET_SEND:
                    if (pos < bdcAllChargeFuns.length) {
                        sendMsg(mReadBdcUtil, bdcAllChargeFuns[pos]);
                    }
                    break;
                case 100://??????????????????
                    String myuuid = (String) msg.obj;
                    if (uuid.equals(myuuid) && !isReceiveSucc) {

                    }
                    refreshFinish();
                    break;
                case 101:
                    toReadBdc();
                    break;
                default:
                    BtnDelayUtil.dealTLXBtn(this, what, 2500, mContext, toolbar);
                    break;
            }
        }
    };



    private void parseBdc(byte[] bytes, int pos) {
        //??????????????????
        byte[] bs = RegisterParseUtil.removePro17(bytes);
        int value2 = MaxWifiParseUtil.obtainValueTwo(bs, 0);
        if (pos < bdcNumber) {//????????????
            bdcChargePower += value2;
        } else {//??????
            bdcDisChargePower += value2;
        }
    }

    private void freshPower() {
        List<String> powers = new ArrayList<>();
        powers.add(String.valueOf(mMaxData.getNormalPower()));
        powers.add(String.valueOf(mMaxData.getTotalPower()));
        powers.add(String.valueOf(bdcChargePower));
        powers.add(String.valueOf(bdcDisChargePower));
        initPowerDatas(powerTitles, powers, mPowerAdapter);
    }



    public void checkUpdata() {
        //??????????????????
        if (END_USER != ShineToosApplication.getContext().getUser_type()) {
            try {
                InverterUpdataManager.getInstance().checkUpdata(this, UpgradePath.MIN_TL_XH_US_PATH);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ???????????????????????????????????????????????????
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
                RegisterParseUtil.parseHold3kT3124(mMaxData, bytes);

                break;
            case 3:
                RegisterParseUtil.parseInput3kT3124V2(mMaxData, bytes);
                isBDC = true;
                break;
            case 4:
                RegisterParseUtil.parseInput3125T3249(mMaxData, bytes);
                break;
        }
    }


    /**
     * ??????ui??????
     */
    private void refreshUI() {
        ToolStorageDataBean storageBeen = mMaxData.getStorageBeen();
        //???????????????
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
        initEleDatas(eleTitles, todayEle, totalEle, mEleAdapter);
        //????????????
        List<String> powers = new ArrayList<>();
        powers.add(String.valueOf(mMaxData.getNormalPower()));
        powers.add(String.valueOf(mMaxData.getTotalPower()));
        powers.add(String.valueOf(storageBeen.getpCharge()));
        powers.add(String.valueOf(storageBeen.getpDischarge()));
        initPowerDatas(powerTitles, powers, mPowerAdapter);


        //???????????????
        double pusertogrid = mMaxData.getPusertogrid();
        String fluxStr = "0W";
        if (pusertogrid > 0) {
            fluxStr = String.format("%s %sW", getString(R.string.m??????), String.valueOf(pusertogrid));
        } else if (pusertogrid < 0) {
            fluxStr = String.format("%s %sW", getString(R.string.m??????), String.valueOf(Math.abs(pusertogrid)));
        }
        tvFluxPower.setText(fluxStr);

        //?????????
        ivDryStatus.setImageResource(mMaxData.getDryStatus() == 0 ? R.drawable.ganjiedian_off : R.drawable.ganjiedian_on);
        //?????????
        if (mMaxData.getDryStatus() == 0) {
            ivDryStatus.setImageResource(R.drawable.ganjiedian_off);
            llDryStatus.setBackgroundResource(R.drawable.shape_gray_corner_360bg);
        } else {
            ivDryStatus.setImageResource(R.drawable.ganjiedian_on);
            llDryStatus.setBackgroundResource(R.drawable.shape_green_corner_360bg);
        }
        //????????????
        int errCode = mMaxData.getErrCode();
        int warmCode = mMaxData.getWarmCode();
        int errCodeSecond = mMaxData.getErrCodeSecond();
        int warmCodeSecond = mMaxData.getWarmCodeSecond();
        //????????????
        String errCodeStr = "";
        String warnCodeStr = "";
        if (errCode >= 200 || warmCode >= 200) {
            errCodeStr = String.format("%d(%02d)", errCode, errCodeSecond);
            warnCodeStr = String.format("%d(%02d)", warmCode, warmCodeSecond);
        } else {
            if (errCode == 0) {
//                errCodeStr = getString(R.string.m351???????????????);
                errCodeStr = "--";
            } else {
                errCode += 99;
                errCodeStr = String.format("%d(%02d)", errCode, errCodeSecond < 100 ? errCodeSecond : errCodeSecond % 100);
            }
            if (warmCode == 0) {
//                warnCodeStr = getString(R.string.m352???????????????);
                warnCodeStr ="--";
            } else {
                warmCode += 99;
                warnCodeStr = String.format("%d(%02d)", warmCode, warmCodeSecond < 100 ? warmCodeSecond : warmCodeSecond % 100);
            }
        }
        tvErrH1.setText(errCodeStr);
        tvWarnH1.setText(warnCodeStr);


        //??????
        int status = mMaxData.getStatus();
        int bdcStatus = mMaxData.getBdcStatus();
        if (bdcStatus!=0){//?????????
            statusTitles = new String[]{
                    getString(R.string.all_Waiting), getString(R.string.m206???????????????),getString(R.string.m207???????????????),
                    getString(R.string.m??????), getString(R.string.m226?????????)
            };

            statusColors=new int[]{
                    R.color.color_status_wait,R.color.color_status_grid,R.color.color_status_offgrid,
                    R.color.color_status_fault,R.color.color_status_upgrade
            };

            drawableStatus=new int[]{
                    R.drawable.circle_wait,R.drawable.circle_grid,R.drawable.circle_offgrid,R.drawable.circle_fault, R.drawable.circle_upgrade
            };
        }else {
            statusTitles = new String[]{
                    getString(R.string.all_Waiting), getString(R.string.m206???????????????),
                    getString(R.string.m??????), getString(R.string.m226?????????)
            };

            statusColors=new int[]{
                    R.color.color_status_wait,R.color.color_status_grid,R.color.color_status_fault,R.color.color_status_upgrade
            };

            drawableStatus=new int[]{
                    R.drawable.circle_wait,R.drawable.circle_grid,R.drawable.circle_fault, R.drawable.circle_upgrade
            };
        }


        String statusStr;
        int color=R.color.color_text_66;
        int drawable=R.drawable.circle_wait;

        if (status >= 0 && status < statusTitles.length){
            statusStr = statusTitles[status];
            color=statusColors[status];
            drawable=drawableStatus[status];
        }else {
            statusStr = getString(R.string.m505??????)+" "+status;
        }
        tvStatus.setVisibility(View.VISIBLE);
        tvStatus.setText(statusStr);
        tvStatus.setTextColor(ContextCompat.getColor(USToolsMainActivityV2.this,color));
        Drawable drawableLeft=getResources().getDrawable(drawable);
        tvStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableLeft,null,null,null);
        tvStatus.setCompoundDrawablePadding(4);
        //??????
        bdcNumber = mMaxData.getBdcNumber();
        if (mMaxData.getBdcStatus() != 0&&bdcNumber > 1) {
            bdcChargeFuns = new int[bdcNumber][3];
            bdcDisChargeFuns = new int[bdcNumber][3];
            bdcAllChargeFuns = new int[bdcNumber * 2][3];
            //?????????????????????????????????
            for (int i = 0; i < bdcNumber; i++) {
                //??????
                bdcChargeFuns[i][0] = 4;
                bdcChargeFuns[i][1] = 3180 + 843 + 108 * i;
                bdcChargeFuns[i][2] = 3181 + 843 + 108 * i;
                //??????
                bdcDisChargeFuns[i][0] = 4;
                bdcDisChargeFuns[i][1] = 3178 + 843 + 108 * i;
                bdcDisChargeFuns[i][2] = 3179 + 843 + 108 * i;
            }
            //?????????????????????
            System.arraycopy(bdcChargeFuns, 0, bdcAllChargeFuns, 0, bdcChargeFuns.length);
            System.arraycopy(bdcDisChargeFuns, 0, bdcAllChargeFuns, bdcChargeFuns.length, bdcDisChargeFuns.length);
            //??????????????????
            autoFun = new int[][]{{4, 3000, 3124}, {4, 3125, 3249}};
            System.arraycopy(bdcChargeFuns, 0, autoFun, 2, bdcChargeFuns.length);
            System.arraycopy(bdcDisChargeFuns, 0, autoFun, bdcChargeFuns.length + 2, bdcDisChargeFuns.length);

        }

    }


    /**
     * ????????????
     */
    private void stopRefresh() {
        isAutoRefresh = false;
        item.setTitle(noteStartStr);
        mHandlerReadAuto.removeMessages(SOCKET_AUTO_REFRESH);
        mHandlerReadAuto.removeMessages(SOCKET_SEND);
        //?????????????????????socket
        SocketClientUtil.close(mClientUtilRead);
        SocketClientUtil.close(mClientUtil);
        SocketClientUtil.close(mReadBdcUtil);

    }


    /**
     * ????????????
     */
    //????????????:??????????????????
    private SocketClientUtil mClientUtilRead;

    //?????????????????????
    private void autoReadRegisterValue() {
        isAutoRefresh = true;
        item.setTitle(noteStopStr);
        mClientUtilRead = SocketClientUtil.connectServerAuto(mHandlerReadAuto);
    }

    /**
     * ???????????????handle
     */
    Handler mHandlerReadAuto = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //????????????
                case SOCKET_SEND:
                    if (autoCount < autoFun.length) {
                        byte[] sendBytesR = SocketClientUtil.sendMsgToServer(mClientUtilRead, autoFun[autoCount]);
                        LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(sendBytesR));
                    }
                    break;
                //??????????????????
                case SOCKET_RECEIVE_BYTES:
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //?????????????????????
                        boolean isCheck = ModbusUtil.checkModbus(bytes);
                        if (isCheck) {
                            //???????????????????????????
                            parseMaxAuto(bytes, autoCount);
                        }
                        if (autoCount < autoFun.length - 1) {
                            autoCount++;
                            this.sendEmptyMessage(SOCKET_SEND);
                        } else {
                            autoCount = 0;
                            //??????ui
                            refreshUI();
                            freshPower();
                            //????????????
//                            autoRefresh(this);
                            this.sendEmptyMessageDelayed(SOCKET_SEND,3000);
                        }
//                        else {//?????????????????????
//                            autoCount = 0;
//                            this.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
//                        }
                        LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        autoCount = 0;
                        //????????????
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
     * ????????????
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
                RegisterParseUtil.parseInput3kT3124V2(mMaxData, bytes);
                break;
            case 1:
                RegisterParseUtil.parseInput3125T3249(mMaxData, bytes);
                break;
            default:
                //??????????????????
                byte[] bs = RegisterParseUtil.removePro17(bytes);
                int value2 = MaxWifiParseUtil.obtainValueTwo(bs, 0);
                if (count < bdcNumber - 2) {//????????????
                    bdcChargePower += value2;
                } else {//??????
                    bdcDisChargePower += value2;
                }
                break;
        }
    }


    /**
     * ????????????
     *
     * @param handler
     */
    public void autoRefresh(Handler handler) {
        bdcChargePower = 0;
        bdcDisChargePower = 0;
        if (handler != null) {
            isAutoRefresh = true;
            item.setTitle(noteStopStr);
            handler.sendEmptyMessageDelayed(SOCKET_AUTO_REFRESH, autoTime);
        } else {
            item.setTitle(noteStopStr);
            isAutoRefresh = false;
        }
    }






    /**
     * ???????????????????????????????????????????????????
     *
     * @param clientUtil
     * @param sends
     * @return??????????????????????????????
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
     * ?????????Max???????????????
     */
    private void jumpMaxSet(Class clazz, String title) {
        stopRefresh();
        Intent intent = new Intent(mContext, clazz);
        intent.putExtra("title", title);
        intent.putExtra("isTlxhus", true);
        ActivityUtils.startActivity(this, intent, false);
    }


    /**
     * ?????????????????????
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
            jumpMaxSet(MaxChartEnergyActivity.class, getString(R.string.m201??????));
        } else if (v == cvWarning) {
            jumpErrorWarnSet();
        }

    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.right_action:
                //?????????????????????
                LogUtil.i("????????????????????????" + isAutoRefresh);
                if (!isAutoRefresh) {
                    Mydialog.Show(mContext);
                    needFresh = true;
                    //?????????????????????
                    refresh();
                } else {
                    stopRefresh();
                }
                break;
        }
        return true;
    }


    @Override
    public void onItemClick(final BaseQuickAdapter adapter, View view, final int position) {
        if (adapter == usParamsetAdapter) {
            String title1 = "";
            Class clazz = null;

            if (user_type==END_USER||user_type==MAINTEAN_USER){

                switch (position) {
                    case 0:
                        clazz = USFastConfigAcitivity.class;
                        break;
                    case 1:
                        clazz = UsSystemSettingActivity.class;
                        break;
                    case 2:
                        clazz = MainsCodeParamSetActivity.class;
                        break;
                    case 3:
                        clazz = USChargeActivity.class;
                        break;
                    case 4:
                        clazz = MaxCheckActivity.class;
                        break;
                    case 5:
                        clazz = USParamsSettingActivity.class;
                        break;
                    case 6:
                        clazz = USDeviceInfoActivity.class;
                        break;

                    default:
                        clazz = null;
                        break;
                }

            }else {
                switch (position) {
                    case 0:
                        clazz = USFastConfigAcitivity.class;
                        break;
                    case 1:
                        clazz = UsSystemSettingActivity.class;
                        break;
                    case 2:
                        clazz = MainsCodeParamSetActivity.class;
                        break;
                    case 3:
                        clazz = USChargeActivity.class;
                        break;
                    case 4:
                        clazz = MaxCheckActivity.class;
                        break;
                    case 5:
                        clazz = USParamsSettingActivity.class;
                        break;

                    case 6:
                        clazz = USAdvanceSetActivity.class;
                        title1 = getString(R.string.????????????);
                        break;

                    case 7:
                        clazz = USDeviceInfoActivity.class;
                        break;

                    case 8:
                        break;


                    default:
                        clazz = null;
                        break;
                }
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
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRefresh();
    }

}
