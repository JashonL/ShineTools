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
import com.growatt.shinetools.adapter.TLXEEleAdapter;
import com.growatt.shinetools.adapter.TLXHToolEleAdapter;
import com.growatt.shinetools.adapter.UsParamsetAdapter;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.modbusbox.bean.MaxDataBean;
import com.growatt.shinetools.modbusbox.bean.MaxDataDeviceBean;
import com.growatt.shinetools.module.localbox.max.bean.MaxChildBean;
import com.growatt.shinetools.module.localbox.max.config.MaxHvQuicksettingActivity;
import com.growatt.shinetools.module.localbox.tlxh.bean.TLXHEleBean;
import com.growatt.shinetools.module.localbox.ustool.bean.UsToolParamBean;
import com.growatt.shinetools.module.localbox.max.config.MaxBasicSettingActivity;
import com.growatt.shinetools.module.localbox.max.config.MaxGridCodeSettingActivity;
import com.growatt.shinetools.module.localbox.max.config.MaxQuicksettingActivity;
import com.growatt.shinetools.module.localbox.max.config.MaxSystemConfigActivity;
import com.growatt.shinetools.module.localbox.mintool.TLXWarningActivity;
import com.growatt.shinetools.module.localbox.ustool.USAdvanceSetActivity;
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

    private TextView tvFault;
    private String noteStartStr;
    private String noteStopStr;
    String[] pidStatusStrs;
    //????????????
    String[] statusTitles;
    private int [] statusColors;
    private int [] drawableStatus;
    private String errNode ;


    String[] c2Title2;
    String[] c3Title2;
    String[] c34Title2;
    String[] c4Title2;
    String[] c6Title1;
    String[] c5Title1;
    String[] c4Title1;



    private TextView tvErrH1;
    private TextView tvWarnH1;

    private MenuItem item;
    //??????????????????
    private boolean isAutoRefresh;
    //????????????????????????
    private boolean needFresh;
    private boolean isReceiveSucc = false;
    //????????????
    private SocketClientUtil mClientUtil;


    private MaxDataBean mMaxData = new MaxDataBean();
    private int count = 0;
    int[][] funs = {{3, 0, 124}, {4, 0, 99}, {4, 100, 199}, {4, 875, 999}};
    //????????????????????????
    int[] deviceTypeFun = {3,125,249};

    //??????????????????
    private int autoTime = 3000;



    //????????????
    private LinearLayoutManager mLayoutManager;
    private List<MaxChildBean> mGridList;
    private View header;
    private MaxMainChildAdapter mAdapter;



    /**
     * ?????????recyclerview
     */
    private String[] eleTitles;
    public String[][] eleItemTiles;
    private int[] eleResId;
    private TLXEEleAdapter mEleAdapter;
    private List<TLXHEleBean> mEleList;
    private RecyclerView mEleRecycler;
    private ImageView ivDetail;
    private LinearLayout llUpstreamDownstream;


    //?????????
    private UsParamsetAdapter usParamsetAdapter;
    private List<UsToolParamBean> mSettingList=new ArrayList<>();
    private RecyclerView mRvParamset;

    int autoCount = 0;


    private CardView cvWarning;


    //?????????????????????
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
                CircleDialogUtils.showCommentDialog(Max230KTL3HVToolActivity.this, getString(R.string.??????????????????),
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


//        tvTitle.setText(R.string.m240??????????????????);
        tvTitle.setText("MAX TL3-X HV");
        String title = getIntent().getStringExtra("title");
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }
        toolbar.inflateMenu(R.menu.comment_right_menu);
        item = toolbar.getMenu().findItem(R.id.right_action);
        item.setTitle(noteStartStr);
        toolbar.setOnMenuItemClickListener(this);

        //????????????
        mGridList = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MaxMainChildAdapter(R.layout.item_max_childrv, mGridList);
        header = LayoutInflater.from(this).inflate(R.layout.header_max_header_view, (ViewGroup) recyclerView.getParent(), false);
        mAdapter.addHeaderView(header);
        recyclerView.setAdapter(mAdapter);


        //??????
        initRecyclerViewEle();

        //?????????
        initUpstreamDownStream();

        //??????
        initRecyclerViewPower();

        //?????????
        initSettingRecycleView();


        initListener();


        Mydialog.Show(mContext);
        //?????????????????????
        refresh();
    }


    private void initListener() {
        initOnclick(ivDetail, cvWarning);
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
        return false;
    }





    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        String title1 = "";
        Class clazz = null;

        switch (position) {
            case 0:
                clazz = MaxHvQuicksettingActivity.class;
                break;
            case 1:
                clazz = MaxSystemConfigActivity.class;
                break;
            case 2:
                clazz = MaxBasicSettingActivity.class;
                break;
            case 3:
                clazz = MaxGridCodeSettingActivity.class;

                break;
            case 4:
                clazz = MaxCheck1500VActivity.class;

                break;
            case 5:
                clazz = USAdvanceSetActivity.class;
                title1 = getString(R.string.????????????);
                break;

            case 6:
                clazz = MaxHvInfoActivity.class;
                break;

            case 7:
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
     * ?????????????????????
     */
    private void jumpErrorWarnSet() {
        if (!TextUtils.isEmpty(tvErrH1.getText().toString())) {
            stopRefresh();
            Intent intent = new Intent(mContext, TLXWarningActivity.class);
            intent.putExtra("title", tvFault.getText().toString());
            intent.putExtra("errCode", mMaxData.getErrCode());
            intent.putExtra("warmCode", mMaxData.getWarmCode());
            intent.putExtra("errCodeSecond", mMaxData.getErrCodeSecond());
            intent.putExtra("warmCodeSecond", mMaxData.getWarmCodeSecond());
            intent.putExtra("type", 0);
            intent.putExtra("error1", mMaxData.getError1());
            intent.putExtra("error2", mMaxData.getError2());
            ActivityUtils.startActivity(this,intent,false);
        } else {
            toast(errNode);
        }
    }




    /**
     * ????????????
     */
    private void initRecyclerViewEle() {
        mEleList = new ArrayList<>();
        mEleRecycler = header.findViewById(R.id.rvEle);
        ivDetail = header.findViewById(R.id.iv_detail);
        mEleRecycler.setLayoutManager(new LinearLayoutManager(this));
        LinearDivider linearDivider = new LinearDivider(this, LinearLayoutManager.VERTICAL,
                1, ContextCompat.getColor(this, R.color.gray_aaaaaa));
        mEleAdapter = new TLXEEleAdapter(R.layout.item_tlxh_tool_ele_v2, mEleList);
        mEleRecycler.addItemDecoration(linearDivider);
        mEleRecycler.setAdapter(mEleAdapter);
        initEleDatas(eleTitles, null, null, mEleAdapter);
    }


    private void initUpstreamDownStream() {
        llUpstreamDownstream = header.findViewById(R.id.ll_upstream_downstream);
        //???????????????
        llUpstreamDownstream.setVisibility(View.GONE);
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




        int[] res = new int[]{
                R.drawable.quickly, R.drawable.system_config,R.drawable.param_setting,
                R.drawable.city_code, R.drawable.smart_check,
                R.drawable.advan_setting, R.drawable.device_info
        };
        String[] title = new String[]{
                getString(R.string.????????????) , getString(R.string.android_key3052), getString(R.string.m284????????????),
                getString(R.string.android_key3056)
               , getString(R.string.m285????????????)
                , getString(R.string.m286????????????), getString(R.string.m291????????????)
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
        tvFault = header.findViewById(R.id.tv_fault);
        tvWarnH1 = header.findViewById(R.id.tv_warn_value);
        cvWarning = header.findViewById(R.id.cvWarning);

    }


    private void initEleDatas(@NonNull String[] titles, List<String> todays, List<String> totals, TLXEEleAdapter adapter) {
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
            bean.setTodayTitle(eleItemTiles[i][0]);
            bean.setTotalTitle(eleItemTiles[i][0]);
            bean.setDrawableResId(eleResId[i]);
            bean.setUnit(unit);
            newList.add(bean);
        }
        adapter.replaceData(newList);
    }







    private void initString() {
        errNode =getString(R.string.m290????????????????????????);
        noteStartStr = getString(R.string.m268????????????);
        noteStopStr = getString(R.string.m280????????????);
        pidStatusStrs = new String[]{
                "", getString(R.string.all_Waiting), getString(R.string.all_Normal), getString(R.string.m??????)
        };
        statusTitles = new String[]{
                getString(R.string.all_Waiting), getString(R.string.all_Normal),
                getString(R.string.m226?????????), getString(R.string.m??????)
        };

        statusColors=new int[]{
                R.color.color_status_wait,R.color.color_status_grid,R.color.color_status_fault,R.color.color_status_upgrade
        };

        drawableStatus=new int[]{
                R.drawable.circle_wait,R.drawable.circle_grid,R.drawable.circle_fault, R.drawable.circle_upgrade
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
                "+Bus", "-Bus",
                getString(R.string.m310PID????????????), getString(R.string.m311PID??????)
        };
        c5Title1 = new String[]{
                getString(R.string.m312????????????), getString(R.string.m313????????????),
                getString(R.string.dataloggers_list_serial), getString(R.string.m314Model???),
//                getString(R.string.m315??????????????????),  getString(R.string.m316??????????????????)
                getString(R.string.m??????????????????), getString(R.string.m??????????????????)
        };


        c4Title1 = new String[]{
                getString(R.string.fly_cap_volt) + "1", getString(R.string.fly_cap_volt) + "2", getString(R.string.fly_cap_volt) + "3", getString(R.string.fly_cap_volt) + "4",
                getString(R.string.fly_cap_volt) + "5", getString(R.string.fly_cap_volt) + "6", getString(R.string.fly_cap_volt) + "7", getString(R.string.fly_cap_volt) + "8"
                , getString(R.string.fly_cap_volt) + "9", getString(R.string.fly_cap_volt) + "10", getString(R.string.fly_cap_volt) + "11", getString(R.string.fly_cap_volt) + "12",
                getString(R.string.fly_cap_volt) + "13", getString(R.string.fly_cap_volt) + "14", getString(R.string.fly_cap_volt) + "15", "PID"
        };



        eleTitles = new String[]{
                getString(R.string.android_key2019) + "\n" + "(kWh)",
                getString(R.string.m320??????) + "\n(kWh)",
        };


        eleResId = new int[]{
                R.drawable.tlxh_ele_fadian, R.drawable.ele_power,
        };


        eleItemTiles=new String[eleTitles.length][2];
        for (int i = 0; i < eleTitles.length; i++) {
            if (i==0){
                eleItemTiles[i][0]=getString(R.string.android_key408);
                eleItemTiles[i][1]=getString(R.string.android_key1912);
            }else {
                eleItemTiles[i][0]=getString(R.string.????????????);
                eleItemTiles[i][1]=getString(R.string.m189????????????);
            }
        }

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
                    break;
                case SocketClientUtil.SOCKET_RECEIVE_MSG:
                    //????????????????????????
                    isReceiveSucc = true;
                    String recMsg = (String) msg.obj;

                    sb.append("Server:<br>")
                            .append(recMsg)
                            .append("<br><br>");
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
                            //??????ui
                            refreshUI();
                            count = 0;
                            //????????????
                            SocketClientUtil.close(mClientUtil);
                            refreshFinish();
                            //?????????????????????????????????????????????
                            //??????????????????
                            readTypeRegisterValue();
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
//        Mydialog.Dismiss();
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
     * ??????ui??????
     */
    private void refreshUI() {
        //?????????  ??????
        List<String> todayEle = new ArrayList<>();
        todayEle.add(String.valueOf(mMaxData.getTodayEnergy()));
        todayEle.add(String.valueOf(mMaxData.getNormalPower()));
        List<String> totalEle = new ArrayList<>();
        totalEle.add(String.valueOf(mMaxData.getTotalEnergy()));
        totalEle.add(String.valueOf(mMaxData.getTotalPower()));
        initEleDatas(eleTitles, todayEle, totalEle, mEleAdapter);


        //????????????
        int errCode = mMaxData.getErrCode();
        int warmCode = mMaxData.getWarmCode();
        int errCodeSecond = mMaxData.getErrCodeSecond();
        int warmCodeSecond = mMaxData.getWarmCodeSecond();
        //????????????
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
        //??????
        int status = mMaxData.getStatus();
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
        tvStatus.setTextColor(ContextCompat.getColor(Max230KTL3HVToolActivity.this,color));
        Drawable drawableLeft=getResources().getDrawable(drawable);
        tvStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableLeft,null,null,null);
        tvStatus.setCompoundDrawablePadding(4);


        //-----------------------



        //recycleView:grid:????????????
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
        //grid:????????????
        String[] datasParams = new String[10];
        datasParams[0] = deviceBeen.getLastTime();
        datasParams[1] = deviceBeen.getRealOPowerPercent();
        datasParams[2] = String.format("%dk??",deviceBeen.getIso());
        datasParams[3] = deviceBeen.getEnvTemp();
        datasParams[4] = deviceBeen.getBoostTemp();
        datasParams[5] = deviceBeen.getDeviceTemp();
        datasParams[6] = deviceBeen.getpBusV();
        datasParams[7] = deviceBeen.getnBusV();
        datasParams[8] = deviceBeen.getPidErrCode();
        //??????pid??????
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
        //pv??????????????????
        initC1Datas(c1Title1, c1Title2, mMaxData.getPVList(), mC1Adapter);
        //pvc????????????
        initC1Datas(c2Title1, c2Title2, mMaxData.getPVCList(), mC2Adapter);*/
        //ac????????????
        List<String> nowACList = mMaxData.getACList();
        try {
            nowACList.set(4,deviceBeen.getIpf());
        } catch (Exception e) {
            e.printStackTrace();
        }
/*        initC1Datas(c3Title1, c3Title2, nowACList, mC3Adapter);
        //ac????????????
        initC1Datas(c4Title1, c4Title2, mMaxData.getPIDList(), mC4Adapter);
        //svg
        initC1Datas(c34Title1, c34Title2, mMaxData.getSVGList(), mC34Adapter);*/
    }



    /**
     * ????????????????????????
     */
    //????????????:??????????????????
    private SocketClientUtil mClientUtilReadType;
    //?????????????????????
    private void readTypeRegisterValue() {
        mClientUtilReadType = SocketClientUtil.connectServer(mHandlerReadType);
    }
    /**
     * ???????????????handle
     */
    Handler mHandlerReadType= new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //????????????
                case SocketClientUtil.SOCKET_SEND:
                    byte[] sendBytesR = SocketClientUtil.sendMsgToServer(mClientUtilReadType, deviceTypeFun);
                    LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(sendBytesR));
                    break;
                //??????????????????
                case SOCKET_RECEIVE_BYTES:
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //?????????????????????
                        boolean isCheck = ModbusUtil.checkModbus(bytes);
                        if (isCheck) {
                            RegisterParseUtil.parseMax3T125T249(mMaxData,bytes);
                            //??????????????????
                            refreshUIAbout();
//                            toast(R.string.all_success);
                        }
                        LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(bytes));
                        //????????????
                        SocketClientUtil.close(mClientUtilReadType);
                        //?????????????????????????????????????????????
                        if (needFresh){
                            autoRefresh(mHandlerReadAuto);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        //????????????
                        SocketClientUtil.close(mClientUtilReadType);
                    }
                    break;
            }
        }
    };


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
                case SocketClientUtil.SOCKET_SEND:
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
                            this.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        } else {
                            autoCount = 0;
                            //??????ui
                            refreshUI();
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
     * ????????????
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
        //recycleView:grid:????????????
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
     * ????????????
     */
    private void stopRefresh() {
        isAutoRefresh = false;
        item.setTitle(noteStartStr);
        mHandlerReadAuto.removeMessages(SOCKET_AUTO_REFRESH);
        mHandlerReadAuto.removeMessages(SOCKET_SEND);
        //?????????????????????socket
        SocketClientUtil.close(mClientUtilRead);
        SocketClientUtil.close(mClientUtilReadType);
        SocketClientUtil.close(mClientUtil);
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRefresh();
    }



}
