package com.growatt.shinetools.module.localbox.max;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.growatt.shinetools.R;
import com.growatt.shinetools.adapter.MaxCheckIVAdapter;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.constant.GlobalConstant;
import com.growatt.shinetools.db.SqliteUtil;
import com.growatt.shinetools.listeners.OnEmptyListener;
import com.growatt.shinetools.modbusbox.Arith;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.MaxWifiParseUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.module.localbox.max.bean.MaxCheckIVBean;
import com.growatt.shinetools.socket.ConnectHandler;
import com.growatt.shinetools.socket.SocketManager;
import com.growatt.shinetools.utils.ChartUtils;
import com.growatt.shinetools.utils.CommenUtils;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.MyControl;
import com.growatt.shinetools.utils.SharedPreferencesUnit;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.growatt.shinetools.modbusbox.SocketClientUtil.SOCKET_10_READ;

public class NewMaxCheckIVActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {


    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.header_title)
    LinearLayout headerTitle;
    @BindView(R.id.tvLastTime)
    TextView tvLastTime;
    @BindView(R.id.rBtn1)
    RadioButton rBtn1;
    @BindView(R.id.rBtn2)
    RadioButton rBtn2;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.tvYUnit)
    TextView tvYUnit;
    @BindView(R.id.lineChart)
    LineChart lineChart;
    @BindView(R.id.tvXUnit)
    TextView tvXUnit;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.readView)
    View readView;
    @BindView(R.id.tvStart)
    TextView tvStart;

    private String readStr1;
    private String readStr2;
    private String readStr3;

    private LinearLayoutManager mLayoutManager;
    private MaxCheckIVAdapter mAdapter;
    private List<MaxCheckIVBean> mList;
    private String mTitle;
    //???????????????
    private List<ArrayList<Entry>> dataList;
    private List<ArrayList<Entry>> newDataList;
    int[] colors = {R.color.max_iv_graph_linechart1, R.color.max_iv_graph_linechart2, R.color.max_iv_graph_linechart3, R.color.max_iv_graph_linechart4,
            R.color.max_iv_graph_linechart5, R.color.max_iv_graph_linechart6, R.color.max_iv_graph_linechart7, R.color.max_iv_graph_linechart8};
    int[] colors_a = {R.color.max_iv_graph_linechart1, R.color.max_iv_graph_linechart2, R.color.max_iv_graph_linechart3, R.color.max_iv_graph_linechart4,
            R.color.max_iv_graph_linechart5, R.color.max_iv_graph_linechart6, R.color.max_iv_graph_linechart7, R.color.max_iv_graph_linechart8};


    String[] colors32 = {
            "#60acfc", "#27a1ea", "#39b3ea", "#35c5ea", "#32d3eb", "#4ebecd", "#40cec7", "#63d5b2"
            , "#5bc49f", "#9cdc82", "#d4ec59", "#ffda43", "#feb64d", "#ff9f69", "#fa816d", "#fb6e6c"
            , "#ff7b7b", "#e9668e", "#d660a8", "#b55cbd", "#9287e7", "#747be1", "#6370de", "#668ed6"
            , "#ff7b7b", "#e9668e", "#d660a8", "#b55cbd", "#9287e7", "#747be1", "#6370de", "#668ed6"
    };

    String[] colors32_a = {
            "#60acfc", "#27a1ea", "#39b3ea", "#35c5ea", "#32d3eb", "#4ebecd", "#40cec7", "#63d5b2"
            , "#5bc49f", "#9cdc82", "#d4ec59", "#ffda43", "#feb64d", "#ff9f69", "#fa816d", "#fb6e6c"
            , "#ff7b7b", "#e9668e", "#d660a8", "#b55cbd", "#9287e7", "#747be1", "#6370de", "#668ed6"
            , "#ff7b7b", "#e9668e", "#d660a8", "#b55cbd", "#9287e7", "#747be1", "#6370de", "#668ed6"
    };

    //????????????
    /**
     * ?????????????????????
     */
    private int waitTime = 60000;
    /**
     * ????????????????????????
     */
    private int readTimeOne = 800;
    /**
     * ??????ui??????
     */
    private int updataUiTime = 100;
    /**
     * ????????????
     */
    private int nowTime = 0;
    /**
     * ????????????????????????
     */
    private int nextTime = 0;
    /**
     * ???????????????
     */
    private int totalTime = waitTime + readTimeOne * colors.length;
    /**
     * ??????????????????
     */
    private boolean isReading;

    /**
     * ?????????????????????,?????????,?????????
     */
    private boolean isFirst = true;
    /**
     * ????????????
     */
    private int mWidth = 100;
    private FrameLayout.LayoutParams mViewParams;

    /**
     * 0:I-V
     * 1:P-V
     */
    private int mIPType = 0;
    private final int TIME_COUNT = 2;//?????????????????? + 1
    private int nowTimeCount = 0;

    /**
     * ???????????????handle
     */
    private int count;

    private SocketManager manager;
    private int posTime = 0;


    //hold183??????????????????????????????????????????????????????183???????????????0???
    // ????????????????????????IV???????????????????????????????????????????????????PV??????????????????
    // ????????????????????????????????????????????????????????????
    private int hold183;
    private int type = 0;//0?????????  1?????????

    private int[][] funs =
            {{0x14, 0, 0x7d - 1}, {0x14, 0x7d, 0x7d * 2 - 1},
                    {0x14, 0x7d * 2, 0x7d * 3 - 1},
                    {0x14, 0x7d * 3, 0x7d * 4 - 1}, {0x14, 0x7d * 4, 0x7d * 5 - 1},
                    {0x14, 0x7d * 5, 0x7d * 6 - 1}, {0x14, 0x7d * 6, 0x7d * 7 - 1},
                    {0x14, 0x7d * 7, 0x7d * 8 - 1}};

    private int[][] funs32;


    //?????????????????????
    private int step = 1;


    private int[] nowSet = {16, 250, 250};
    private int[] values = {1};


    @Override
    protected int getContentView() {
        return R.layout.activity_new_max_check_iv;
    }

    @Override
    protected void initViews() {
        initString();
        initIntent();
        initView();
        initHeaderView();
        initRecyclerView();
        initLineChart();
        initLastData();
        setLineChart();
        initListener();
    }


    @OnClick(R.id.tvStart)
    public void onViewClicked() {
        nowTime = 0;
        posTime = nowTime;
        nextTime = readTimeOne;
        initDatalist();
        if (!isReading) {
            isReading = true;
            totalTime = waitTime + readTimeOne * (funs.length + 1);
            set250();
            readHandler.sendEmptyMessage(200);
        } else {
            reStartBtn();
        }
    }


    //????????????????????????
    private Handler readHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case 200:
                    //?????????
                    int time = nextTime - nowTime;
                    if (posTime < time) {
                        posTime = posTime + time / updataUiTime;
                    } else {
                        posTime = time;
                    }
                    //????????????
                    int width = (nowTime + posTime) * mWidth / totalTime;
                    tvStart.setText(String.format("%s%d%%", readStr2, (nowTime + posTime) * 100 / totalTime));
                    if (nowTime == totalTime) {
                        width = 0;
                        tvStart.setText(readStr1);
                    } else {
                        //?????????
                        this.sendEmptyMessageDelayed(200, time / updataUiTime);
                    }
                    mViewParams.width = width;
                    readView.setLayoutParams(mViewParams);
                    break;

                case SOCKET_10_READ:
                    noticeUpdateTime(0, true);
                    getData();
                    break;
            }

        }
    };


    private void getData() {
        type = 0;
        if (hold183 == 0) {
            int[] fun = funs[count];
            manager.sendMsg(fun);
        } else {
            int[] funs = funs32[count];
            manager.sendMsg(funs);

        }

    }


    private void initListener() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                adapterCheck(position);
            }
        });
        radioGroup.setOnCheckedChangeListener(this);
    }


    @Override
    protected void initData() {
        initDatalist();
        funs32 = new int[32][3];
        for (int i = 0; i < 32; i++) {
            int[] fun = funs32[i];
            fun[0] = 0x14;
            fun[1] = 0x7d * (56 + i);
            fun[2] = fun[1] + 124;
        }
        connetSocket();
    }


    public void initDatalist() {
        dataList = new ArrayList<>();
        for (int i = 0; i < colors.length; i++) {
            ArrayList<Entry> list = new ArrayList<>();
            dataList.add(list);
        }
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
            //??????183??????
            getHold183();
        }

        @Override
        public void connectFail() {
            manager.disConnectSocket();
            MyControl.showJumpWifiSet(NewMaxCheckIVActivity.this);
        }

        @Override
        public void sendMsgFail() {
            manager.disConnectSocket();
            MyControl.showTcpDisConnect(NewMaxCheckIVActivity.this, getString(R.string.disconnet_retry),
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
            if (type == 0) {
                //?????????????????????
                boolean isCheck = ModbusUtil.checkModbus(bytes);
                if (isCheck) {
                    //???????????????????????????
                    parseMax(bytes);
                }

            } else {//??????
                if (step == 250) {
                    nowTimeCount = 0;
                    //?????????????????????
                    boolean isFlag = MaxUtil.isCheckFull10(mContext, bytes);
                    if (isFlag) {
                        //????????????
                        readHandler.sendEmptyMessageDelayed(SOCKET_10_READ, waitTime);
                        //????????????
                        noticeUpdateTime(-1, true);
                    } else {
                        //??????????????????
                        noticeUpdateTime(-1, false);
                        reStartBtn();
                    }
                }
            }
        }
    };


    /**
     * ????????????????????????
     *
     * @param pos:???????????????-1???????????????0-6????????????
     * @param isSuccess??????????????????
     */
    public void noticeUpdateTime(int pos, boolean isSuccess) {
        posTime = 0;
        switch (pos) {
            case -1:
                if (isSuccess) {
                    isFirst = true;
                } else {
                    isFirst = true;
                }
                nowTime = nextTime;
                nextTime = nowTime + waitTime;
                break;
            case 0:
                nowTime = nextTime;
                nextTime = nowTime + readTimeOne;
                break;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                nowTime = nextTime;
                nextTime = nowTime + readTimeOne;
                break;
        }
    }


    private void parseMax(byte[] bytes) {
        //??????????????????
        byte[] bs = RegisterParseUtil.removePro17(bytes);
        switch (step) {
            case 183:
                hold183 = MaxWifiParseUtil.obtainValueOne(bs);
                break;
        }


    }

    /**
     * ??????183???????????????
     */
    private void getHold183() {
        step = 183;
        type = 0;
        int[] funs = {3, 183, 183};
        manager.sendMsg(funs);
    }


    private void set250() {
        step = 250;
        type = 1;
        manager.sendMsgToServer10(nowSet, values);


    }


    private void initString() {
        readStr1 = getString(R.string.m460??????);
        readStr2 = getString(R.string.m461???????????????);
        readStr3 = String.format("%s:", getString(R.string.m267?????????????????????));
    }

    private void initIntent() {
        Intent mIntent = getIntent();
        if (mIntent != null) {
            mTitle = mIntent.getStringExtra("title");
        }
    }


    private void initView() {
        tvXUnit.setText("V");
        tvYUnit.setText("A");
        mWidth = CommenUtils.getScreenWidth(mContext);
        mViewParams = (FrameLayout.LayoutParams) readView.getLayoutParams();
        tvLastTime.setText(String.format("%s%s", readStr3, SharedPreferencesUnit.getInstance(mContext).get(GlobalConstant.MAX_IV_LAST_TIME, "")));
    }


    private void initHeaderView() {
        toolbar.setNavigationIcon(R.drawable.icon_return);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reStartBtn();
                finish();
            }
        });
        if (TextUtils.isEmpty(mTitle)) {
            mTitle = "";
        }
        tvTitle.setText(mTitle);
    }


    private void reStartBtn() {
        isReading = false;
        count = 0;
        mViewParams.width = 0;
        readView.setLayoutParams(mViewParams);
        tvStart.setText(readStr1);
    }


    private TextView tvTitle1;
    private TextView tvTitle2;
    private TextView tvXValue;

    private void initRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        mList = new ArrayList<>();
        mAdapter = new MaxCheckIVAdapter(R.layout.item_max_check_iv_act, mList);
        View headerView = LayoutInflater.from(mContext).inflate(R.layout.header_max_check_iv_act, recyclerView, false);
        tvXValue = (TextView) headerView.findViewById(R.id.tvXValue);
        tvTitle1 = (TextView) headerView.findViewById(R.id.tvTitle1);
        tvTitle2 = (TextView) headerView.findViewById(R.id.tvTitle2);
        mAdapter.addHeaderView(headerView);
        recyclerView.setAdapter(mAdapter);
        initRecyclerData(null);
    }


    public void initRecyclerData(List<MaxCheckIVBean> list) {
        if (list == null || list.size() == 0) {
            List<MaxCheckIVBean> newList = new ArrayList<>();
            for (int i = 0; i < colors.length; i++) {
                MaxCheckIVBean bean = new MaxCheckIVBean();
                bean.setImgColorId(colors[i]);
                bean.setTitle(String.valueOf(i + 1));
                newList.add(bean);
            }
            mAdapter.replaceData(newList);
        } else {
            mAdapter.replaceData(list);
        }
    }


    /**
     * ???????????????
     */
    private void initLineChart() {
        ChartUtils.initLineChart(mContext, lineChart, 0, "", true,
                R.color.grid_bg_white, true, R.color.grid_bg_white, true,
                R.color.note_bg_white, R.color.grid_bg_white, R.color.grid_bg_white, R.color.highLightColor,
                false, R.string.m4, R.string.m5, new OnEmptyListener() {
                    @Override
                    public void onEmpty(Entry e, Highlight highlight) {
                        LogUtil.i("x??????" + e.getX());
                        getValuesByEntry(e);
                    }
                });
        lineChart.getAxisLeft().setAxisMinimum(0f);
        Description description = new Description();
        description.setText("V(V)");
        description.setTextColor(ContextCompat.getColor(this, R.color.grid_bg_white));
        lineChart.setDescription(description);
    }


    private void getValuesByEntry(Entry e) {
        LineData lineData = lineChart.getData();
        List<ILineDataSet> dataSets = lineData.getDataSets();
        if (dataSets != null) {
            for (int i = 0; i < dataSets.size(); i++) {
                int xPos = (int) e.getX();
                //?????????????????????
                LineDataSet lineDataSet = (LineDataSet) dataSets.get(i);
                //??????????????????
                List<Entry> entries = lineDataSet.getValues();
                //??????????????????
                MaxCheckIVBean maxCheckIVBean = mList.get(i);
                //???????????????
                maxCheckIVBean.setxValue(String.valueOf(xPos));
                //??????X??????
//                tvXValue.setText(String.format("x=%s",maxCheckIVBean.getxValue()));
                //??????x???
                float xMax = lineDataSet.getXMax();
                //??????y???
                float yPos = 0;
                if (xPos > xMax) {
                    yPos = 0;
                } else {
                    List<Entry> posEntry = lineDataSet.getEntriesForXValue(xPos);
                    if (posEntry != null && posEntry.size() > 0) {
                        yPos = posEntry.get(0).getY();
                    } else {
                        //?????????????????????????????????xy??????
                        if (entries != null) {
                            float prePosX = 0;
                            float prePosY = 0;
                            float nowPosX = 0;
                            float nowPosY = 0;
                            for (int j = 0, jsize = entries.size(); j < jsize; j++) {
                                Entry nowEntry = entries.get(j);
                                if (nowEntry.getX() > xPos) {
                                    nowPosX = nowEntry.getX();
                                    nowPosY = nowEntry.getY();
                                    if (j > 0) {
                                        Entry preEn = entries.get(j - 1);
                                        prePosX = preEn.getX();
                                        prePosY = preEn.getY();
                                    }
                                    break;
                                }
                            }
                            yPos = MaxUtil.getValueByPos(prePosX, prePosY, nowPosX, nowPosY, xPos);
                            yPos = (float) Arith.round(yPos, 1);
                        } else {
                            yPos = 0;
                        }
                    }
                }
                maxCheckIVBean.setyValue(String.valueOf(yPos));
                //?????????y???
                setMax(lineDataSet, entries, maxCheckIVBean);
            }
            mAdapter.notifyDataSetChanged();
        }
    }


    private void setMax(LineDataSet lineDataSet, List<Entry> entries, MaxCheckIVBean maxCheckIVBean) {
        float yMax = lineDataSet.getYMax();
        float xMax = lineDataSet.getXMax();
        if (xMax < 0) {
            xMax = 0;
        }
        if (yMax < 0) {
            yMax = 0;
        }
        if (mIPType == 1) {
            if (entries != null) {
                for (Entry en : entries) {
                    if (en.getY() == yMax) {
                        maxCheckIVBean.setxMaxValue(String.valueOf((int) en.getX()));
                        maxCheckIVBean.setyMaxValue(String.valueOf(yMax));
                        break;
                    }
                }
            }
        } else {
            maxCheckIVBean.setxMaxValue(String.valueOf((int) xMax));
            maxCheckIVBean.setyMaxValue(String.valueOf(yMax));
        }
    }


    /**
     * ?????????????????????
     */
    private List<ArrayList<Entry>> entryList;

    private void initLastData() {
        entryList = MaxUtil.getEntryList(SqliteUtil.getListJson(GlobalConstant.MAX_IV_LAST_DATA));
        if (entryList != null) {
            dataList = entryList;
        }
    }


    /**
     * ????????????
     */
    public void setLineChart() {
        MaxUtil.setLineChartData(mContext, lineChart, dataList, colors, colors_a, dataList.size(), R.color.highLightColor);
        hideLastData();
    }

    /**
     * ????????????????????????
     */
    private void hideLastData() {
        adapterCheck(6);
        adapterCheck(7);
        if (entryList != null) {
            getValuesByEntry(new Entry(0, 0));
            entryList = null;
        }
    }


    private void adapterCheck(int position) {
        try {
            MaxCheckIVBean item = mAdapter.getItem(position);
            item.setSelect(!item.isSelect());

            if (!item.isSelect()) {
                item.setImgColorId(R.color.max_main_text_content);
            } else {
                item.setImgColorId(colors[position]);
            }
            mAdapter.notifyDataSetChanged();

            if (lineChart != null && lineChart.getData() != null) {
                int count = lineChart.getData().getDataSetCount();
                if (count > position) {
                    LineDataSet dataSet = (LineDataSet) lineChart.getData().getDataSets().get(position);
                    if (!item.isSelect()) {
                        MaxUtil.clearDataSetByIndex(lineChart, position);
                    } else {
                        if (mIPType == 1 && newLists != null && newLists.size() > 0) {
                            MaxUtil.replaceDataSet(lineChart, newLists, position);
                        } else {
                            MaxUtil.replaceDataSet(lineChart, dataList, position);
                        }
                    }
                    //                        YAxis axisLeft = mLineChart.getAxisLeft();
                    //                        axisLeft.resetAxisMaximum();
                    LogUtil.i("MaxUtil.getMaxY:" + MaxUtil.getMaxY(lineChart) + ";getYMax:" + lineChart.getYMax() + ";getY:" + lineChart.getY() + ";getScaleY:" + lineChart.getScaleY());
                    //                        axisLeft.setAxisMaximum(mLineChart.getYMax() * 1.1f);

                    lineChart.getData().notifyDataChanged();
                    lineChart.notifyDataSetChanged();
                    lineChart.invalidate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * ??????I-V/P-V??????
     *
     * @param type????????????
     * 0???I-V
     * 1???P-V
     */
    private List<ArrayList<Entry>> newLists;

    public void setIPVData(int type) {
        switch (type) {
            case 0://I-V
                MaxUtil.setLineChartData(mContext, lineChart, dataList, colors, colors_a, dataList.size(), R.color.highLightColor);
                break;
            case 1://P-V
                newLists = new ArrayList<>();
                if (dataList != null) {
                    int size = dataList.size();
                    for (int i = 0; i < size; i++) {
                        ArrayList<Entry> newEntries = new ArrayList<>();
                        ArrayList<Entry> entries = dataList.get(i);
                        for (int j = 0, count = entries.size(); j < count; j++) {
                            Entry newEntry = new Entry();
                            Entry entry = entries.get(j);
                            newEntry.setX(entry.getX());
                            newEntry.setY((int) Arith.round(Arith.mul(entry.getX(), entry.getY()), 0));
                            newEntries.add(newEntry);
                        }
                        newLists.add(newEntries);
                    }
                }
                MaxUtil.setLineChartData(mContext, lineChart, newLists, colors, colors_a, newLists.size(), R.color.highLightColor);
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group == radioGroup) {
            final String xUnit;
            final String yUnit;
            switch (checkedId) {
                case R.id.rBtn1:
                    xUnit = "V";
                    yUnit = "A";
                    mIPType = 0;
                    tvTitle1.setText("MPPT(Voc , Isc)");
                    tvTitle2.setText("(Vpv , Ipv)");
                    tvXUnit.setText("V");
                    tvYUnit.setText("A");
                    break;
                case R.id.rBtn2:
                default:
                    xUnit = "V";
                    yUnit = "W";
                    mIPType = 1;
                    tvTitle1.setText("MPPT(Vmpp , Pmpp)");
                    tvTitle2.setText("(Vpv , Ppv)");
                    tvXUnit.setText("V");
                    tvYUnit.setText("W");
                    break;
            }
            StringBuilder sb = new StringBuilder();
            if (mList != null) {
                for (int i = 0, size = mList.size(); i < size; i++) {
                    MaxCheckIVBean maxCheckIVBean = mList.get(i);
                    maxCheckIVBean.setxUnit(xUnit);
                    maxCheckIVBean.setyUnit(yUnit);
                    if (!maxCheckIVBean.isSelect()) {
                        maxCheckIVBean.setSelect(true);
                        sb.append(i).append("_");
                    }
                }
                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                mAdapter.notifyDataSetChanged();
            }
            //???????????????
            YAxis axisLeft = lineChart.getAxisLeft();
            axisLeft.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return (float) (Math.round(value * 10)) / 10 + "";
                }
            });

            setIPVData(mIPType);

            if (sb.length() > 0) {
                String[] split = String.valueOf(sb).split("_");
                for (String pos : split) {
                    int index = Integer.parseInt(pos);
                    adapterCheck(index);
                }
            }
            getValuesByEntry(new Entry(0, 0));
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        manager.disConnectSocket();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            reStartBtn();
        }
        return super.onKeyDown(keyCode, event);
    }

}




