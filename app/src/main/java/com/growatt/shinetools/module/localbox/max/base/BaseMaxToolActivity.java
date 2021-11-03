package com.growatt.shinetools.module.localbox.max.base;

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
import com.growatt.shinetools.adapter.TLXHToolEleAdapter;
import com.growatt.shinetools.adapter.UsParamsetAdapter;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.bean.MaxDataBean;
import com.growatt.shinetools.modbusbox.bean.MaxDataDeviceBean;
import com.growatt.shinetools.module.localbox.max.MaxChartEnergyActivity;
import com.growatt.shinetools.module.localbox.max.bean.TLXHEleBean;
import com.growatt.shinetools.module.localbox.max.bean.UsToolParamBean;
import com.growatt.shinetools.module.localbox.mintool.TLXWarningActivity;
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

public abstract class BaseMaxToolActivity extends BaseActivity implements Toolbar.OnMenuItemClickListener,
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


    //状态
    public String[] pidStatusStrs;
    public String[] statusTitles;
    public int[] statusColors;
    public int[] drawableStatus;

    //初始化状态数组
    public abstract void initStatusRes();


    /**
     * 发电量recyclerview
     */
    private TLXHToolEleAdapter mEleAdapter;
    private List<TLXHEleBean> mEleList;
    private RecyclerView mEleRecycler;
    private TextView tvDetial;
    private ImageView ivDetail;
    public String[] eleTitles;
    public int[] eleResId;

    //初始化发电量数组
    public abstract void initEleRes();

    //故障和告警
    private CardView cvWarning;
    private TextView tvErrH1;
    private TextView tvFault;
    private TextView tvWarnH1;


    //连接读取数据
    //是否需要自动刷新
    private boolean autoFreshSwitch = false;
    //当前是自动刷新还是只刷新一次
    private int currenRead = 0;
    private boolean isConnect = false;


    //连接对象
    private SocketManager manager;
    public MaxDataBean mMaxData = new MaxDataBean();

    private int count = 0;
    public int[][] funs = {{3, 0, 124}, {4, 0, 99}, {4, 100, 199}, {4, 875, 999}};
    //自动刷新功能码
    public int autoCount = 0;
    public int[][] autoFun = {{4, 0, 124}, {4, 125, 249}, {4, 875, 999}};
    //读机器型号功能码
    public int[] deviceTypeFun = {3, 125, 249};

    //获取数据的寄存器集合
    public abstract void initGetDataArray();

    //设置项
    public UsParamsetAdapter usParamsetAdapter;
    private List<UsToolParamBean> mSettingList = new ArrayList<>();


    @Override
    protected int getContentView() {
        return R.layout.activity_max230_ktl3_hv_tool;
    }

    @Override
    protected void initViews() {
        mContext = this;
        initString();

        toolbar.setNavigationIcon(R.drawable.icon_return);
        toolbar.setNavigationOnClickListener(v -> CircleDialogUtils.showCommentDialog(BaseMaxToolActivity.this, getString(R.string.退出设置提示),
                getString(R.string.m设置未保存是否退出), getString(R.string.android_key1935),
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

        //主题列表
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        MaxMainChildAdapter mAdapter = new MaxMainChildAdapter(R.layout.item_max_childrv, new ArrayList<>());
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
    }

    @Override
    protected void initData() {
        connetSocket();
    }

    private void connetSocket() {
        //初始化连接
        manager = new SocketManager(this);
        //设置连接监听
        manager.onConect(connectHandler);
        //开始连接TCP
        //延迟一下避免频繁操作
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
        public void sendMessage(String msg) {
            LogUtil.i("发送的消息:" + msg);
        }

        @Override
        public void receiveMessage(String msg) {
            LogUtil.i("接收的消息:" + msg);
        }

        @Override
        public void receveByteMessage(byte[] bytes) {
            try {
                //检测内容正确性
                boolean isCheck = ModbusUtil.checkModbus(bytes);
                if (isCheck) {
                    if (currenRead==0) {
                        parseMax(bytes, count);
                    } else {
                        //接收正确，开始解析
                        parseMaxAuto(bytes, count);
                    }

                    if (currenRead==0){
                        getNextData();
                    }else {
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
            //更新ui
            refreshUI();
            count = 0;
            Mydialog.Dismiss();
            //设备信号刷新完后：开启自动刷新
            if (autoFreshSwitch) {
                new Handler().postDelayed(() -> autoGetData(0), 3000);
            }
        }
    }


    private void autoGetNextData() {
        if (count < autoFun.length - 1) {
            autoGetData(++count);
        } else {
            //更新ui
            refreshUI();
            count = 0;
            Mydialog.Dismiss();
            //设备信号刷新完后：开启自动刷新
            if (autoFreshSwitch) {
                new Handler().postDelayed(() -> autoGetData(0), 3000);
            }
        }
    }


    /**
     * 请求获取数据
     *
     * @param pos
     */
    private void getData(int pos) {
        currenRead = 0;
        count = pos;
        if (funs.length > pos) {
            int[] nowfuns = funs[count];
            LogUtil.i("-------------------请求获取:" + Arrays.toString(nowfuns) + "----------------");
            manager.sendMsg(nowfuns);
        }
    }


    /**
     * 自动请求获取数据
     *
     * @param pos
     */
    private void autoGetData(int pos) {
        currenRead = 1;
        count = pos;
        if (autoFun.length > pos) {
            int[] nowfuns = autoFun[count];
            LogUtil.i("-------------------请求获取:" + Arrays.toString(nowfuns) + "----------------");
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
            jumpMaxSet(MaxChartEnergyActivity.class, getString(R.string.m201电量));
        } else if (v == cvWarning) {
            jumpErrorWarnSet();
        }

    }

    //跳转到其他页面
    private boolean toOhterSetting = false;


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.right_action:
                //读取寄存器的值
                LogUtil.i("自动刷新开关：" + autoFreshSwitch);
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
        return false;
    }


    public abstract void toSettingActivity(int position);


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        toSettingActivity(position);
    }


    /**
     * 跳转到故障页面
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
            ActivityUtils.startActivity(this, intent, false);
        } else {
            toast(errNode);
        }
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
        RecyclerView mRvParamset = header.findViewById(R.id.rv_setting);
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
                getString(R.string.快速设置), getString(R.string.android_key3052), getString(R.string.basic_setting)
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
        tvFault = header.findViewById(R.id.tv_fault);
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


    private void initString() {
        errNode = getString(R.string.m290请先读取故障信息);
        noteStartStr = getString(R.string.m268自动刷新);
        noteStopStr = getString(R.string.m280停止刷新);
        initStatusRes();
        initEleRes();
        initGetDataArray();
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


    public abstract void parserMaxAuto(int count, byte[] bytes);

    /**
     * 解析数据
     *
     * @param bytes
     * @param count
     */
    private void parseMaxAuto(byte[] bytes, int count) {
        parserMaxAuto(count, bytes);
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
        //状态
        int status = mMaxData.getStatus();
        String statusStr;
        int color = R.color.color_text_66;
        int drawable = R.drawable.circle_wait;

        if (status >= 0 && status < statusTitles.length) {
            statusStr = statusTitles[status];
            color = statusColors[status];
            drawable = drawableStatus[status];
        } else {
            statusStr = getString(R.string.m505状态) + " " + status;
        }
        tvStatus.setVisibility(View.VISIBLE);
        tvStatus.setText(statusStr);
        tvStatus.setTextColor(ContextCompat.getColor(BaseMaxToolActivity.this, color));
        Drawable drawableLeft = getResources().getDrawable(drawable);
        tvStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableLeft, null, null, null);
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
        //grid:内部参数
        String[] datasParams = new String[10];
        datasParams[0] = deviceBeen.getLastTime();
        datasParams[1] = deviceBeen.getRealOPowerPercent();
        datasParams[2] = String.format("%dkΩ", deviceBeen.getIso());
        datasParams[3] = deviceBeen.getEnvTemp();
        datasParams[4] = deviceBeen.getBoostTemp();
        datasParams[5] = deviceBeen.getDeviceTemp();
        datasParams[6] = deviceBeen.getpBusV();
        datasParams[7] = deviceBeen.getnBusV();
        datasParams[8] = deviceBeen.getPidErrCode();
        //处理pid状态
        int pidStatus = deviceBeen.getPidStatus();
        String pidStr = "";
        if (pidStatus >= 1 && pidStatus <= 3) {
            pidStr = pidStatusStrs[pidStatus];
        } else {
            pidStr = pidStatus + "";
        }
        datasParams[9] = pidStr;

        //ac电压电流
        List<String> nowACList = mMaxData.getACList();
        try {
            nowACList.set(4, deviceBeen.getIpf());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 停止刷新
     */
    private void stopRefresh() {
        item.setTitle(noteStartStr);
    }


    /**
     * 跳转到Max各设置界面
     */
    public void jumpMaxSet(Class clazz, String title) {
        stopRefresh();
        Intent intent = new Intent(mContext, clazz);
        intent.putExtra("title", title);
        intent.putExtra("isTlxhus", true);
        intent.putExtra("deviceType", 0);
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
