package com.growatt.shinetools.module.localbox.tlx.base;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import com.growatt.shinetools.adapter.MaxMainChildAdapter;
import com.growatt.shinetools.adapter.TLXEEleAdapter;
import com.growatt.shinetools.adapter.TLXHToolEleAdapter;
import com.growatt.shinetools.adapter.UsParamsetAdapter;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.bean.MaxDataBean;
import com.growatt.shinetools.modbusbox.bean.MaxDataDeviceBean;
import com.growatt.shinetools.module.localbox.max.MaxChartEnergyActivity;
import com.growatt.shinetools.module.localbox.max.type.DeviceConstant;
import com.growatt.shinetools.module.localbox.mintool.TLXWarningActivity;
import com.growatt.shinetools.module.localbox.tlxh.bean.TLXHEleBean;
import com.growatt.shinetools.module.localbox.ustool.bean.UsToolParamBean;
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

public abstract class BaseTLXEActivity extends BaseActivity implements Toolbar.OnMenuItemClickListener,
        BaseQuickAdapter.OnItemClickListener, View.OnClickListener {

    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_status)
    TextView tvStatus;

    private Context mContext;
    private MenuItem item;
    private View header;

    private String errNode;
    private String noteStartStr;
    private String noteStopStr;


    //??????
    public String[] pidStatusStrs;
    public String[] statusTitles;
    public int[] statusColors;
    public int[] drawableStatus;

    //?????????????????????
    public abstract void initStatusRes();


    /**
     * ?????????recyclerview
     */
    private TLXEEleAdapter mEleAdapter;
    private List<TLXHEleBean> mEleList;
    private RecyclerView mEleRecycler;
    private ImageView ivDetail;
    public String[] eleTitles;
    public String[][] eleItemTiles;
    public int[] eleResId;
    private LinearLayout llUpstreamDownstream;

    public abstract boolean initIsUpstream();

    //????????????????????????
    public abstract void initEleRes();

    //???????????????
    private CardView cvWarning;
    private TextView tvErrH1;
    private TextView tvFault;
    private TextView tvWarnH1;


    //??????????????????
    //????????????????????????
    private boolean autoFreshSwitch = false;
    //??????????????????????????????????????????
    private int currenRead = 0;
    private boolean isConnect = false;


    //????????????
    private SocketManager manager;
    public MaxDataBean mMaxData = new MaxDataBean();

    private int count = 0;
    public int[][] funs = {{3, 0, 124}, {4, 0, 99}, {4, 100, 199}, {4, 875, 999}};
    //?????????????????????
    public int autoCount = 0;
    public int[][] autoFun = {{4, 0, 124}, {4, 125, 249}, {4, 875, 999}};
    //????????????????????????
    public int[] deviceTypeFun = {3, 125, 249};

    public String deviceType;

    //?????????????????????
    public abstract void initDeviceType();

    //??????????????????????????????
    public abstract void initGetDataArray();

    //?????????
    public UsParamsetAdapter usParamsetAdapter;
    private List<UsToolParamBean> mSettingList = new ArrayList<>();

    //??????????????????????????????
    public abstract void initSetDataArray();


    //?????????
    public String[] title;
    public int[] res;


    @Override
    protected int getContentView() {
        return R.layout.activity_max230_ktl3_hv_tool;
    }

    @Override
    protected void initViews() {
        mContext = this;
        initString();

        toolbar.setNavigationIcon(R.drawable.icon_return);
        toolbar.setNavigationOnClickListener(v -> CircleDialogUtils.showCommentDialog(BaseTLXEActivity.this, getString(R.string.??????????????????),
                getString(R.string.m???????????????????????????), getString(R.string.android_key1935),
                getString(R.string.android_key2152), Gravity.CENTER, v12 -> finish(), v1 -> {
                }));

        String title = getIntent().getStringExtra("title");
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }
        toolbar.inflateMenu(R.menu.comment_right_menu);
        item = toolbar.getMenu().findItem(R.id.right_action);
        item.setTitle(noteStartStr);
        toolbar.setOnMenuItemClickListener(this);

        //????????????
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        MaxMainChildAdapter mAdapter = new MaxMainChildAdapter(R.layout.item_max_childrv, new ArrayList<>());
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
    }

    private void initUpstreamDownStream() {
        llUpstreamDownstream = header.findViewById(R.id.ll_upstream_downstream);
        boolean b = initIsUpstream();
        if (!b) {//???????????????
            llUpstreamDownstream.setVisibility(View.GONE);
        }
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
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        toSettingActivity(position);
    }


    /**
     * ?????????????????????
     */
    private void jumpErrorWarnSet() {
        if (!TextUtils.isEmpty(tvErrH1.getText().toString())) {
            toOhterSetting = true;
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
            ActivityUtils.startActivity(this, intent, false);
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


/*        int[] res = new int[]{
                R.drawable.quickly, R.drawable.system_config,
                R.drawable.charge_manager, R.drawable.smart_check, R.drawable.param_setting,
                R.drawable.advan_setting, R.drawable.device_info
        };
        String[] title = new String[]{
                getString(R.string.????????????), getString(R.string.android_key3052), getString(R.string.basic_setting)
                , getString(R.string.m285????????????), getString(R.string.m284????????????)
                , getString(R.string.m286????????????), getString(R.string.m291????????????)
        };*/
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
            bean.setTodayTitle(eleItemTiles[i][0]);
            bean.setTodayEle(todayEle);
            bean.setTotalTitle(eleItemTiles[i][1]);
            bean.setContentColor(contentColor);
            bean.setTitle(titles[i]);
            bean.setDrawableResId(eleResId[i]);
            bean.setUnit(unit);
            newList.add(bean);
        }
        adapter.replaceData(newList);
    }


    private void initString() {
        errNode = getString(R.string.m290????????????????????????);
        noteStartStr = getString(R.string.m268????????????);
        noteStopStr = getString(R.string.m280????????????);
        initStatusRes();
        initEleRes();
        initGetDataArray();
        initDeviceType();
        //???????????????
        initSetDataArray();
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
        if (errCode >= 200 || warmCode >= 200) {
            errCodeStr = String.format("%d(%02d)", errCode, errCodeSecond);
            warnCodeStr = String.format("%d(%02d)", warmCode, warmCodeSecond);
        } else {
            if (errCode == 0) {
                errCodeStr = "--";
            } else {
                errCode += 99;
//                errCodeStr = String.format("%d(%02d)",errCode,errCodeSecond<100?errCodeSecond:errCodeSecond%100);
                errCodeStr = String.valueOf(errCode);
            }
            if (warmCode == 0) {
                warnCodeStr = "--";
            } else {
                warmCode += 99;
//                warnCodeStr = String.format("%d(%02d)",warmCode,warmCodeSecond<100?warmCodeSecond:warmCodeSecond%100);
                warnCodeStr = String.valueOf(warmCode);
            }
        }
        tvErrH1.setText(errCodeStr);
        tvWarnH1.setText(warnCodeStr);
        //??????
        int status = mMaxData.getStatus();
        String statusStr;
        int color = R.color.color_text_66;
        int drawable = R.drawable.circle_wait;

        if (status >= 0 && status < statusTitles.length) {
            statusStr = statusTitles[status];
            color = statusColors[status];
            drawable = drawableStatus[status];
        } else {
            statusStr = getString(R.string.m505??????) + " " + status;
        }
        tvStatus.setVisibility(View.VISIBLE);
        tvStatus.setText(statusStr);
        tvStatus.setTextColor(ContextCompat.getColor(BaseTLXEActivity.this, color));
        Drawable drawableLeft = getResources().getDrawable(drawable);
        tvStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableLeft, null, null, null);
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
        } else {
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
        } else {
            datasAbout[5] = String.format("%s-%04d", commSoftVersion, deviceBeen.getCommSoftVersionValue());
        }
        //grid:????????????
        String[] datasParams = new String[10];
        datasParams[0] = deviceBeen.getLastTime();
        datasParams[1] = deviceBeen.getRealOPowerPercent();
        datasParams[2] = String.format("%dk??", deviceBeen.getIso());
        datasParams[3] = deviceBeen.getEnvTemp();
        datasParams[4] = deviceBeen.getBoostTemp();
        datasParams[5] = deviceBeen.getDeviceTemp();
        datasParams[6] = deviceBeen.getpBusV();
        datasParams[7] = deviceBeen.getnBusV();
        datasParams[8] = deviceBeen.getPidErrCode();
        //??????pid??????
        int pidStatus = deviceBeen.getPidStatus();
        String pidStr = "";
        if (pidStatus >= 1 && pidStatus <= 3) {
            pidStr = pidStatusStrs[pidStatus];
        } else {
            pidStr = pidStatus + "";
        }
        datasParams[9] = pidStr;

        //ac????????????
        List<String> nowACList = mMaxData.getACList();
        try {
            nowACList.set(4, deviceBeen.getIpf());
        } catch (Exception e) {
            e.printStackTrace();
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
        intent.putExtra("deviceType", deviceType);
        intent.putExtra(DeviceConstant.KEY_DEVICE_TYPE, deviceType);
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
