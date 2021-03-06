package com.growatt.shinetools.module.localbox.sph.base;

import android.content.Intent;
import android.os.Handler;
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
import androidx.fragment.app.FragmentActivity;
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
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.bean.MaxDataBean;
import com.growatt.shinetools.modbusbox.bean.MaxDataDeviceBean;
import com.growatt.shinetools.modbusbox.bean.ToolStorageDataBean;
import com.growatt.shinetools.module.localbox.max.MaxChartEnergyActivity;
import com.growatt.shinetools.module.localbox.max.bean.MaxChildBean;
import com.growatt.shinetools.module.localbox.max.type.DeviceConstant;
import com.growatt.shinetools.module.localbox.tlxh.bean.TLXHEleBean;
import com.growatt.shinetools.module.localbox.ustool.USFaultDetailActivity;
import com.growatt.shinetools.module.localbox.ustool.bean.UsToolParamBean;
import com.growatt.shinetools.module.localbox.ustool.errorcode.ErrorCode;
import com.growatt.shinetools.socket.ConnectHandler;
import com.growatt.shinetools.socket.SocketManager;
import com.growatt.shinetools.utils.ActivityUtils;
import com.growatt.shinetools.utils.CircleDialogUtils;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.MyControl;
import com.growatt.shinetools.utils.Mydialog;
import com.growatt.shinetools.widget.GridDivider;
import com.growatt.shinetools.widget.LinearDivider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

import static com.growatt.shinetools.constant.GlobalConstant.KEFU_USER;

public abstract class SPHSPABaseActivity extends BaseActivity implements Toolbar.OnMenuItemClickListener,
        BaseQuickAdapter.OnItemClickListener, View.OnClickListener {


    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.header_title)
    LinearLayout headerTitle;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private View header;
    private MenuItem item;

    private String noteStartStr;
    private String noteStopStr;


    //????????????
    public String[] pidStatusStrs;
    public String[] statusTitles;
    public int[] statusColors;
    public int[] drawableStatus;

    //?????????????????????
    public abstract void initStatusRes();


    /**
     * ?????????recyclerview
     */
    private TLXHToolEleAdapter mEleAdapter;
    private List<TLXHEleBean> mEleList;
    private RecyclerView mEleRecycler;
    private TextView tvDetial;
    private ImageView ivDetail;
    public String[] eleTitles;
    public int[] eleResId;
    //????????????????????????
    public abstract void initEleRes();

    //???????????????
    private TextView tvFluxPower;
    //?????????
    private ImageView ivDryStatus;
    private View llDryStatus;

    //??????
    public String[] powerTitles;
    public int[] powerResId;
    public abstract void initPowerRes();

    //????????????
    private CardView cvWarning;
    private TextView tvErrH1;
    private TextView tvWarnH1;



    //????????????
    private SocketManager manager;
    public MaxDataBean mMaxData = new MaxDataBean();


    private int count = 0;
    public int[][] funs = {{3, 0, 124}, {3, 125, 249}, {3, 3000, 3124}, {4, 3000, 3124}, {4, 3125, 3249}};
    public int autoCount = 0;
    //?????????????????????
    public  int[][] autoFun = {{4, 3000, 3124}, {4, 3125, 3249}};

    //?????????????????????
    public int deviceType = 0;
    public abstract void initDeviceType();

    //??????????????????????????????
    public abstract void initGetDataArray();
    /**
     * ??????recyclerview
     */
    private TLXHToolPowerAdapter mPowerAdapter;
    private List<TLXHEleBean> mPowerList;
    private RecyclerView mPowerRecycler;


    public int user_type = KEFU_USER;


    //?????????
    public String[] title;
    public int[] res;
    //?????????
    public UsParamsetAdapter usParamsetAdapter;
    private List<UsToolParamBean> mSettingList = new ArrayList<>();
    //??????????????????????????????
    public abstract void initSetDataArray();


    //??????????????????
    //????????????????????????
    private boolean autoFreshSwitch = false;
    //??????????????????????????????????????????
    private int currenRead = 0;
    private boolean isConnect = false;

    //???????????????bdc??????
    public boolean isBDC;

    @Override
    protected int getContentView() {
        return R.layout.activity_us_tools_v2;
    }

    @Override
    protected void initViews() {
        initString();
        //???????????????
        toolbar.setNavigationIcon(R.drawable.icon_return);
        toolbar.setNavigationOnClickListener(v -> CircleDialogUtils.showCommentDialog(SPHSPABaseActivity.this, getString(R.string.??????????????????),
                getString(R.string.m???????????????????????????), getString(R.string.android_key1935),
                getString(R.string.android_key2152), Gravity.CENTER, v1 -> finish(), v12 -> {}));


        String title = getIntent().getStringExtra("title");
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }
        toolbar.inflateMenu(R.menu.comment_right_menu);
        item = toolbar.getMenu().findItem(R.id.right_action);
        item.setTitle(noteStartStr);
        toolbar.setOnMenuItemClickListener(this);
        //????????????
        List<MaxChildBean> mGridList = new ArrayList<>();
        //????????????
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        MaxMainChildAdapter mAdapter = new MaxMainChildAdapter(R.layout.item_max_childrv, mGridList);
        header = LayoutInflater.from(this).inflate(R.layout.header_us_tool_main_v2, (ViewGroup) recyclerView.getParent(), false);
        mAdapter.addHeaderView(header);
        recyclerView.setAdapter(mAdapter);
        //??????
        initRecyclerViewEle();
        //??????
        initRecyclerViewPower();
        //?????????
        initSettingRecycleView();

        initListener();

    }



    @Override
    protected void initData() {
        connetSocket();
    }


    private void connetSocket() {
        //???????????????
        manager = new SocketManager(this);
        //??????????????????
        manager.onConect(connectHandler);
        //????????????TCP
        //??????????????????????????????
        new Handler().postDelayed(() -> manager.connectSocket(), 100);
    }


    ConnectHandler connectHandler = new ConnectHandler() {
        @Override
        public void connectSuccess() {
            isConnect = true;
            getData(0);
        }

        @Override
        public void connectFail() {
            isConnect = false;
            manager.disConnectSocket();
            MyControl.showJumpWifiSet((FragmentActivity) mContext);
        }

        @Override
        public void sendMsgFail() {
            manager.disConnectSocket();
            MyControl.showTcpDisConnect((FragmentActivity) mContext, getString(R.string.disconnet_retry),
                    () -> {
                        connetSocket();
                    }
            );
        }

        @Override
        public void socketClose() {

        }

        @Override
        public void sendMessage(String msg) {
            LogUtil.i("???????????????:" + msg);
        }

        @Override
        public void receiveMessage(String msg) {
            LogUtil.i("???????????????:" + msg);
        }

        @Override
        public void receveByteMessage(byte[] bytes) {
            try {
                //?????????????????????
                boolean isCheck = ModbusUtil.checkModbus(bytes);
                if (isCheck) {
                    if (currenRead == 0) {
                        parseMax(bytes, count);
                    } else {
                        //???????????????????????????
                        parseMaxAuto(bytes, count);
                    }

                    if (currenRead == 0) {
                        getNextData();
                    } else {
                        autoGetNextData();
                    }

                } else {
                    count = 0;
                    Mydialog.Dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
                count = 0;
                Mydialog.Dismiss();
            }

        }
    };

    private void getNextData() {
        if (count < funs.length - 1) {
            getData(++count);
        } else {
            //??????ui
            refreshUI();
            count = 0;
            Mydialog.Dismiss();
            //?????????????????????????????????????????????
            if (autoFreshSwitch) {
                new Handler().postDelayed(() -> autoGetData(0), 3000);
            }
        }
    }


    private void autoGetNextData() {
        if (count < autoFun.length - 1) {
            autoGetData(++count);
        } else {
            //??????ui
            refreshUI();
            count = 0;
            Mydialog.Dismiss();
            //?????????????????????????????????????????????
            if (autoFreshSwitch) {
                new Handler().postDelayed(() -> autoGetData(0), 3000);
            }
        }
    }

    /**
     * ??????????????????
     *
     * @param pos
     */
    private void getData(int pos) {
        currenRead = 0;
        count = pos;
        if (funs.length > pos) {
            int[] nowfuns = funs[count];
            LogUtil.i("-------------------????????????:" + Arrays.toString(nowfuns) + "----------------");
            manager.sendMsg(nowfuns);
        }
    }


    /**
     * ????????????????????????
     *
     * @param pos
     */
    private void autoGetData(int pos) {
        currenRead = 1;
        count = pos;
        if (autoFun.length > pos) {
            int[] nowfuns = autoFun[count];
            LogUtil.i("-------------------????????????:" + Arrays.toString(nowfuns) + "----------------");
            manager.sendMsgNoDialog(nowfuns);
        }
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
    //?????????????????????
    private boolean toOhterSetting = false;

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.right_action:
                //?????????????????????
                LogUtil.i("?????????????????????" + autoFreshSwitch);
                if (!autoFreshSwitch) {
                    item.setTitle(noteStopStr);
                    autoFreshSwitch = true;
                    if (toOhterSetting || !isConnect) {
                        toOhterSetting = false;
                        Mydialog.Show(mContext);
                        connetSocket();
                    } else {
                        getData(0);
                    }

                } else {
                    autoFreshSwitch = false;
                    stopRefresh();
                }
                break;
        }
        return true;
    }

    public abstract void toSettingActivity(int position);

    @Override
    public void onItemClick(final BaseQuickAdapter adapter, View view, final int position) {
        toSettingActivity(position);
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

    /**
     * ???????????????
     */
    private void initSettingRecycleView() {
        RecyclerView mRvParamset = header.findViewById(R.id.rv_setting);
        mRvParamset.setLayoutManager(new GridLayoutManager(this, 3));
        usParamsetAdapter = new UsParamsetAdapter(R.layout.item_us_setting, mSettingList);
        int div = (int) getResources().getDimension(R.dimen.dp_20);
        GridDivider gridDivider = new GridDivider(ContextCompat.getColor(this, R.color.white), div, div);
        mRvParamset.addItemDecoration(gridDivider);
        mRvParamset.setAdapter(usParamsetAdapter);
        usParamsetAdapter.setOnItemClickListener(this);


        user_type = ShineToosApplication.getContext().getUser_type();
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
        mPowerList = new ArrayList<>();
        mPowerRecycler = header.findViewById(R.id.rvPower);
        mPowerRecycler.setLayoutManager(new GridLayoutManager(this, powerTitles.length));
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


    private void initString() {
        noteStartStr = getString(R.string.m268????????????);
        noteStopStr = getString(R.string.m280????????????);
        //????????????
        initStatusRes();
        //?????????
        initEleRes();
        //????????????
        initPowerRes();
        //???????????????
        initSetDataArray();
        //???????????????
        initGetDataArray();
        //?????????????????????
        initDeviceType();
    }

    public abstract void parserData(int count, byte[] bytes);






    /**
     * ????????????
     *
     * @param bytes
     * @param count
     */
    private void parseMax(byte[] bytes, int count) {
        parserData(count, bytes);
    }


    public abstract void parserMaxAuto(int count, byte[] bytes);

    /**
     * ????????????
     *
     * @param bytes
     * @param count
     */
    private void parseMaxAuto(byte[] bytes, int count) {
        parserMaxAuto(count, bytes);
    }


    /**
     * ??????ui??????
     */
    private void refreshUI() { ToolStorageDataBean storageBeen = mMaxData.getStorageBeen();
        //???????????????
        List<String> todayEle = new ArrayList<>();
        todayEle.add(String.valueOf(mMaxData.getTodayEnergy()));
        todayEle.add(String.valueOf(storageBeen.geteChargeToday()));
        todayEle.add(String.valueOf(storageBeen.geteDischargeToday()));
        //????????????
        todayEle.add(String.valueOf(mMaxData.geteObtainToday()));
        //??????/????????????
        todayEle.add(String.valueOf(mMaxData.getEtoGridToday()));
        todayEle.add(String.valueOf(mMaxData.geteLoadToday()));
        List<String> totalEle = new ArrayList<>();
        totalEle.add(String.valueOf(mMaxData.getTotalEnergy()));
        totalEle.add(String.valueOf(storageBeen.geteChargeTotal()));
        totalEle.add(String.valueOf(storageBeen.geteDischargeTotal()));
        //????????????
        totalEle.add(String.valueOf(mMaxData.geteObtainTotal()));
        //??????/????????????
        totalEle.add(String.valueOf(mMaxData.getEtoGridTotal()));
        totalEle.add(String.valueOf(mMaxData.geteLoadTotal()));
        initEleDatas(eleTitles,todayEle,totalEle,mEleAdapter);
        //????????????
        List<String> powers = new ArrayList<>();
        powers.add(String.valueOf(mMaxData.getNormalPowerMix()));
        powers.add(String.valueOf(storageBeen.getpCharge2()));
        powers.add(String.valueOf(storageBeen.getpDischarge2()));
        initPowerDatas(powerTitles,powers,mPowerAdapter);

        //???????????????
        double pusertogrid = mMaxData.getPusertogrid();
        String fluxStr = "0W";
        if (pusertogrid > 0){
            fluxStr = String.format("%s %sW",getString(R.string.m??????),String.valueOf(pusertogrid));
        }else if (pusertogrid < 0){
            fluxStr = String.format("%s %sW",getString(R.string.m??????),String.valueOf(Math.abs(pusertogrid)));
        }
        tvFluxPower.setText(fluxStr);

        tvWarnH1.setText(mMaxData.getWarmDetail());

        String[] datasAbout = new String[5];
        MaxDataDeviceBean deviceBeen = mMaxData.getDeviceBeen();
        datasAbout[0] = deviceBeen.getCompany();
//        datasAbout[1] = deviceBeen.getDeviceType();
        datasAbout[1] = deviceBeen.getSn();
        if (deviceBeen.getNewModel() == 0) {
            int model = deviceBeen.getModel();
            datasAbout[2] = MaxUtil.getDeviceModel(model);
        }else {
            datasAbout[2] = MaxUtil.getDeviceModelNew4(deviceBeen.getNewModel());
        }
//        datasAbout[4] = deviceBeen.getFirmVersionOut();
//        datasAbout[5] = deviceBeen.getFirmVersionIn();
        StringBuilder sbFirm = new StringBuilder()
                .append("(")
                .append(deviceBeen.getFirmVersionOut())
                .append(")")
                .append(deviceBeen.getFirmVersionIn());
        datasAbout[3] = sbFirm.toString();
        String commSoftVersion = deviceBeen.getCommSoftVersion();
        if (TextUtils.isEmpty(commSoftVersion)) {
            datasAbout[4] = "--";
        }else {
            datasAbout[4] = String.format("%s-%04d",commSoftVersion,deviceBeen.getCommSoftVersionValue());
        }
    }


    /**
     * ????????????
     */
    private void stopRefresh() {
        autoFreshSwitch = false;
        item.setTitle(noteStartStr);
    }


    /**
     * ?????????Max???????????????
     */
    public void jumpMaxSet(Class clazz, String title) {
        toOhterSetting = true;
        stopRefresh();
        Intent intent = new Intent(mContext, clazz);
        intent.putExtra("title", title);
        intent.putExtra("isTlxhus", true);
        intent.putExtra("deviceType", 0);
        intent.putExtra(DeviceConstant.KEY_DEVICE_TYPE,deviceType);
        ActivityUtils.startActivity(this, intent, false);
    }


    @Override
    protected void onPause() {
        super.onPause();
        stopRefresh();
        manager.disConnectSocket();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
