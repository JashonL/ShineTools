package com.growatt.shinetools.module.localbox.max;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.growatt.shinetools.R;
import com.growatt.shinetools.adapter.MaxCheckIVAdapter;
import com.growatt.shinetools.adapter.MaxCheckOneKeyAcAdapter;
import com.growatt.shinetools.adapter.MaxCheckOneKeyRSTAdapter;
import com.growatt.shinetools.adapter.MaxCheckOneKeyTHDVAdapter;
import com.growatt.shinetools.base.DemoBase;
import com.growatt.shinetools.constant.GlobalConstant;
import com.growatt.shinetools.db.SqliteUtil;
import com.growatt.shinetools.listeners.OnEmptyListener;
import com.growatt.shinetools.modbusbox.Arith;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.modbusbox.bean.MaxCheckOneKeyAcBean;
import com.growatt.shinetools.modbusbox.bean.MaxCheckOneKeyISOBean;
import com.growatt.shinetools.modbusbox.bean.MaxCheckOneKeyRSTBean;
import com.growatt.shinetools.modbusbox.bean.MaxCheckOneKeyTHDVBean;
import com.growatt.shinetools.module.localbox.max.bean.MaxCheckIVBean;
import com.growatt.shinetools.utils.BtnDelayUtil;
import com.growatt.shinetools.utils.CommenUtils;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.MyControl;
import com.growatt.shinetools.utils.Mydialog;
import com.growatt.shinetools.utils.PermissionCodeUtil;
import com.growatt.shinetools.utils.Position;
import com.growatt.shinetools.utils.SharedPreferencesUnit;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

import static com.growatt.shinetools.modbusbox.SocketClientUtil.SOCKET_10_READ;
import static com.growatt.shinetools.modbusbox.SocketClientUtil.SOCKET_SERVER_SET;
import static com.growatt.shinetools.utils.BtnDelayUtil.TIMEOUT_RECEIVE;


/**
 * Max ????????????
 */
public class MaxCheckOneKeyActivity extends DemoBase implements
        BaseQuickAdapter.OnItemChildClickListener,
        BaseQuickAdapter.OnItemClickListener,
        View.OnClickListener,
        RadioGroup.OnCheckedChangeListener,
        View.OnTouchListener,
        View.OnLongClickListener {
    @BindView(R.id.scrollView)
    NestedScrollView mScrollView;
    private String readStr1;
    private String readStr2;
    private String titleStr;
    private String readStr3;
    @BindView(R.id.inIv)
    View mInIv;
    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    @BindView(R.id.inAc)
    View mInAc;
    @BindView(R.id.inThdv)
    View mInThdv;
    @BindView(R.id.inRst)
    View mInRst;
    @BindView(R.id.inIso)
    View mInIso;

    @BindView(R.id.headerView)
    View headerView;
    @BindView(R.id.readView)
    View mReadView;
    @BindView(R.id.tvStart)
    TextView mTvStart;
    @BindView(R.id.rBtn1)
    RadioButton mRBtn1;
    @BindView(R.id.rBtn2)
    RadioButton mRBtn2;
    @BindView(R.id.radioGroup)
    RadioGroup mRadioGroup;
    @BindView(R.id.lineChartIV)
    LineChart mLineChartIV;
    @BindView(R.id.recyclerViewIV)
    RecyclerView mRecyclerViewIV;
    @BindView(R.id.lineChartAC)
    LineChart mLineChartAC;
    @BindView(R.id.recyclerViewAC)
    RecyclerView mRecyclerViewAC;
    @BindView(R.id.barChartTHDV)
    BarChart mBarChartTHDV;
    @BindView(R.id.recyclerViewTHDV)
    RecyclerView mRecyclerViewTHDV;
    @BindView(R.id.recyclerViewRST)
    RecyclerView mRecyclerViewRST;
    @BindView(R.id.tvLastTime)
    TextView mTvLastTime;
    @BindView(R.id.tvValueXAC)
    TextView tvValueXAC;
    @BindView(R.id.tvTileIV)
    TextView tvTileIV;
    @BindView(R.id.ivTHDetails)
    ImageView mIvTHDetails;
    @BindView(R.id.tvYUnit)
    TextView tvYUnit;
    @BindView(R.id.tvXUnit)
    TextView tvXUnit;
    @BindView(R.id.tvISO)
    TextView tvISO;
    private String mTitle;
    /**
     * ???????????????0123???
     */
    private String mContentSelect;
    private String nowContent;


    //???????????????
    private List<ArrayList<Entry>> dataList;
    private MaxCheckIVAdapter mAdapterIV;
    private List<MaxCheckIVBean> mListIV;
    private List<ArrayList<Entry>> newDataList;
    int[] colors = {R.color.max_iv_graph_linechart1, R.color.max_iv_graph_linechart2, R.color.max_iv_graph_linechart3, R.color.max_iv_graph_linechart4,
            R.color.max_iv_graph_linechart5, R.color.max_iv_graph_linechart6, R.color.max_iv_graph_linechart7, R.color.max_iv_graph_linechart8};
    int[] colors_a = {R.color.max_iv_graph_linechart1, R.color.max_iv_graph_linechart2, R.color.max_iv_graph_linechart3, R.color.max_iv_graph_linechart4,
            R.color.max_iv_graph_linechart5, R.color.max_iv_graph_linechart6, R.color.max_iv_graph_linechart7, R.color.max_iv_graph_linechart8};

    //iv
    /**
     * 0:I-V
     * 1:P-V
     */
    private int mIPType = 0;
    //????????????
    /**
     * ?????????????????????
     */
    private int waitTime = 60000;
    private int waitTime1 = 25000;
    private int waitTime2 = 15000;
    private int waitTime3 = 20000;
    private int waitTime4 = 30000;
    /**
     * ????????????????????????
     */
    private int readTimeOne = 800;
    private int readTimeOne1 = 800;
    private int readTimeOne2 = 800;
    private int readTimeOne3 = 800;
    private int readTimeOne4 = 800;
    /**
     * ??????ui??????
     */
    private int updataUiTime = 100;
    private int updataUiTime1 = 100;
    private int updataUiTime2 = 100;
    private int updataUiTime3 = 100;
    private int updataUiTime4 = 100;
    /**
     * ????????????
     */
    private int nowTime = 0;
    private int nowTime1 = 0;
    private int nowTime2 = 0;
    private int nowTime3 = 0;
    private int nowTime4 = 0;
    /**
     * ????????????????????????
     */
    private int nextTime = 0;
    private int nextTime1 = 0;
    private int nextTime2 = 0;
    private int nextTime3 = 0;
    private int nextTime4 = 0;

    /**
     * ???????????????
     */
    private int totalTime0 = waitTime + readTimeOne1 * 8;
    private int totalTime1 = waitTime1 + readTimeOne1 * 15;
    private int totalTime2 = waitTime2 + readTimeOne2 * 1;
    private int totalTime3 = waitTime3 + readTimeOne3 * 1;
    private int totalTime4 = waitTime4 + readTimeOne4 * 1;
    private int totalTime = totalTime0 + totalTime1 + totalTime2 + totalTime3 + totalTime4;
    /**
     * ??????????????????
     */
    private boolean isReading;

    /**
     * ?????????????????????,?????????,?????????
     */
    private boolean isFirst = true;
    private boolean isFirst1 = true;
    private boolean isFirst2 = true;
    private boolean isFirst3 = true;
    private boolean isFirst4 = true;
    /**
     * ????????????
     */
    private int mWidth = 100;
    private FrameLayout.LayoutParams mViewParams;
    private final int TIME_COUNT = 2;//?????????????????? + 1
    private int nowTimeCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_max_check_one_key);
        ButterKnife.bind(this);
        initString();
        initIntent();
        initHeaderView();
        initView();
        if (mContentSelect.contains("0")) {
            CommenUtils.showAllView(mInIv);
            initRecyclerViewIV();
        }
        if (mContentSelect.contains("1")) {
            CommenUtils.showAllView(mInAc);
            initRecyclerViewAC();
        }
        if (mContentSelect.contains("2")) {
            CommenUtils.showAllView(mInThdv);
            initRecyclerViewTHDV();
        }
        if (mContentSelect.contains("3")) {
            CommenUtils.showAllView(mInRst);
            initRecyclerViewRST();
        }
        if (mContentSelect.contains("4")) {
            CommenUtils.showAllView(mInIso);
            initRecyclerViewISO();
        }
        initListener();
    }

    private void initString() {
        readStr1 = getString(R.string.m460??????);
        readStr2 = getString(R.string.m461???????????????);
        titleStr = getString(R.string.m448????????????);
        readStr3 = String.format("%s:", getString(R.string.m267?????????????????????));
        mTitlesRST = new String[]{
                String.format("R%s", getString(R.string.m463?????????)),
                String.format("S%s", getString(R.string.m463?????????)),
                String.format("T%s", getString(R.string.m463?????????))
//            "R?????????", "S?????????", "T?????????"
        };
    }

    private void initView() {
        mWidth = CommenUtils.getScreenWidth(mContext);
        mViewParams = (FrameLayout.LayoutParams) mReadView.getLayoutParams();
        mTvStart.setFocusable(true);
        mTvStart.setFocusableInTouchMode(true);
        mTvStart.requestFocus();
        mTvLastTime.setText(String.format("%s%s", readStr3, SharedPreferencesUnit.getInstance(mContext).get(GlobalConstant.MAX_ONEKEY_LAST_TIME, "")));
    }

    private void initListener() {
        mIvTHDetails.setOnClickListener(this);
        if (mAdapterIV != null) {
            mAdapterIV.setOnItemClickListener(this);
            mLineChartIV.setOnTouchListener(this);
            mLineChartIV.setOnLongClickListener(this);
        }
        if (mAdapterAc != null) {
            mAdapterAc.setOnItemChildClickListener(this);
            mLineChartAC.setOnTouchListener(this);
            mLineChartAC.setOnLongClickListener(this);
        }
        if (mAdapterTHDV != null) {
            mAdapterTHDV.setOnItemChildClickListener(this);
            mBarChartTHDV.setOnTouchListener(this);
            mBarChartTHDV.setOnLongClickListener(this);
        }
        if (mRadioGroup != null) {
            mRadioGroup.setOnCheckedChangeListener(this);
        }
    }

    /**
     * AC????????????????????? ---------------------------------------------------------------------------------------------------------------
     */
    private List<MaxCheckOneKeyAcBean> mListAc;
    private MaxCheckOneKeyAcAdapter mAdapterAc;
    private String[] mTitlesAc = {
            "RS", "ST", "TR"
    };
    int[] colorsAC = {R.color.max_iv_graph_linechart1, R.color.max_iv_graph_linechart2, R.color.max_iv_graph_linechart3};
    private List<ArrayList<Entry>> dataListAC;

    private void initRecyclerViewAC() {
        mRecyclerViewAC.setLayoutManager(new LinearLayoutManager(this));
        mListAc = new ArrayList<>();
        mAdapterAc = new MaxCheckOneKeyAcAdapter(R.layout.item_max_check_onekey_ac_act, mListAc);
        View headerAc = LayoutInflater.from(this).inflate(R.layout.header_max_check_onekey_ac_act, mRecyclerViewAC, false);
        mAdapterAc.addHeaderView(headerAc);
        mRecyclerViewAC.setAdapter(mAdapterAc);
        initRecyclerDataAC(null);
        initLineChartAC();
        initACLastData();
    }

    private void initLineChartAC() {
        tvXUnit.setText("V");
        tvYUnit.setText("A");
        MaxUtil.initLineChart(mContext, mLineChartAC, 0, "", true, R.color.grid_bg_white, true, R.color.grid_bg_white, true, R.color.note_bg_white, R.color.grid_bg_white, R.color.grid_bg_white, R.color.highLightColor, false, R.string.m4, R.string.m5, new OnEmptyListener() {
            @Override
            public void onEmpty(Entry e, Highlight highlight) {
                LogUtil.i("x??????" + e.getX());
                getValuesByEntryAC(e);
            }
        });
        initDataAC();
    }

    private void getValuesByEntryAC(Entry e) {
        LineData lineData = mLineChartAC.getData();
        List<ILineDataSet> dataSets = lineData.getDataSets();
        if (dataSets != null) {
            for (int i = 0; i < dataSets.size(); i++) {
                float xPos = e.getX();
                //?????????????????????
                LineDataSet lineDataSet = (LineDataSet) dataSets.get(i);
                //??????????????????
                List<Entry> entries = lineDataSet.getValues();
                //??????????????????
                MaxCheckOneKeyAcBean bean = mListAc.get(i);
                //???????????????
                bean.setAcXValue(String.valueOf((int) xPos));
                tvValueXAC.setText(String.format("X=%s", bean.getAcXValue()));
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
                bean.setAcValue(String.valueOf(yPos));
                //?????????y???
                float yMax = lineDataSet.getYMax();
                if (entries != null) {
                    for (Entry en : entries) {
                        if (en.getY() == yMax) {
                            bean.setMaxXValue(String.valueOf((int) en.getX()));
                            bean.setMaxYValue(String.valueOf(yMax));
                            break;
                        }
                    }
                }
            }
            mAdapterAc.notifyDataSetChanged();
        }
    }

    public void initDataAC() {
        dataListAC = new ArrayList<>();
        for (int i = 0; i < colorsAC.length; i++) {
            ArrayList<Entry> list = new ArrayList<>();
            dataListAC.add(list);
        }
        List<ArrayList<Entry>> entryList = MaxUtil.getEntryList(SqliteUtil.getListJson(GlobalConstant.MAX_ONEKEY_LAST_DATA2));
        if (entryList != null) {
            dataListAC = entryList;
        }
        MaxUtil.setLineChartDataSpe(mContext, mLineChartAC, dataListAC, colorsAC, colorsAC, dataListAC.size(), R.color.highLightColor, true);
    }

    private List<ArrayList<Entry>> entryListAC;

    private void initACLastData() {
        entryListAC = MaxUtil.getEntryList(SqliteUtil.getListJson(GlobalConstant.MAX_ONEKEY_LAST_DATA2));
        if (entryListAC != null) {
            dataListAC = entryListAC;
        }
        if (entryListAC != null) {
            getValuesByEntryAC(new Entry(1, 0));
            entryListAC = null;
        }
        //?????????
        String json = SharedPreferencesUnit.getInstance(mContext).get(GlobalConstant.MAX_ONEKEY_LIST_LAST_DATA2, null);
        if (!TextUtils.isEmpty(json)) {
            try {
                List<MaxCheckOneKeyAcBean> newList = new Gson().fromJson(json, new TypeToken<List<MaxCheckOneKeyAcBean>>() {
                }.getType());
                if (newList != null && mAdapterAc != null) {
                    for (int i = 0; i < newList.size(); i++) {
                        MaxCheckOneKeyAcBean bean = newList.get(i);
                        bean.setImgColorId(colorsAC[i]);
                    }
                    mAdapterAc.replaceData(newList);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void initRecyclerDataAC(List<MaxCheckOneKeyAcBean> list) {
        if (list == null || list.size() == 0) {
            List<MaxCheckOneKeyAcBean> newList = new ArrayList<>();
            for (int i = 0; i < colorsAC.length; i++) {
                MaxCheckOneKeyAcBean bean = new MaxCheckOneKeyAcBean();
                bean.setImgColorId(colorsAC[i]);
                bean.setTitle(mTitlesAc[i]);
                newList.add(bean);
            }
            mAdapterAc.replaceData(newList);
        } else {
            mAdapterAc.replaceData(list);
        }
    }

    //????????????
    private SocketClientUtil mClientUtilAC;

    private void connectServerAC() {
        mClientUtilAC = SocketClientUtil.newInstance();
        if (mClientUtilAC != null) {
            mClientUtilAC.connect(mHandlerAC);
        }
    }

    /**
     * ???????????????handle
     */
    private int countAC = 0;
    int[][] funsAC = {
            {0x14, 6036, 6039},
            {0x14, 0x0dac, 0x0dac + 0x7d - 1}, {0x14, 0x0e29, 0x0e29 + 0x7d - 1}, {0x14, 0x0ea6, 0x0ea6 + 0x7d - 1}, {0x14, 0x0f23, 0x0f23 + 0x7d - 1}, {0x14, 0x0fa0, 0x0fa0 + 0x7d - 1},
            {0x14, 0x101d, 0x101d + 0x7d - 1}, {0x14, 0x109a, 0x109a + 0x7d - 1}, {0x14, 0x1117, 0x1117 + 0x7d - 1}, {0x14, 0x1194, 0x1194 + 0x7d - 1}, {0x14, 0x1211, 0x1211 + 0x7d - 1},
            {0x14, 0x128e, 0x128e + 0x7d - 1}, {0x14, 0x130b, 0x130b + 0x7d - 1}, {0x14, 0x1388, 0x1388 + 0x7d - 1}, {0x14, 0x1405, 0x1405 + 0x7d - 1}, {0x14, 0x1482, 0x1482 + 0x7d - 1}
    };
    Handler mHandlerAC = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //????????????
                case SocketClientUtil.SOCKET_SEND:
                    if (countAC < funsAC.length) {
                        sendMsg(mClientUtilAC, funsAC[countAC]);
                    }
                    break;
                case SocketClientUtil.SOCKET_SEND_MSG:
                    BtnDelayUtil.sendMessage(this);
                    //??????????????????
                    //????????????
                    //?????????????????????????????????????????????
                    Message msgSend = Message.obtain();
                    msgSend.what = 100;
                    mHandlerAC.sendEmptyMessageDelayed(100, 3000);
                    String sendMsg = (String) msg.obj;
                    LogUtil.i("????????????:" + sendMsg);
                    break;
                //??????????????????
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    nowTimeCount = 0;
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //?????????????????????
                        boolean isCheck = ModbusUtil.checkModbus(bytes);
                        if (isCheck) {
                            //???????????????????????????
                            RegisterParseUtil.parseMaxErrAC(dataListAC, mListAc, bytes, countAC);
                            //??????ui
                            updateUiAC();
                        }
                        noticeUpdateTime(countAC + 1, true, 2);
                        if (countAC < funsAC.length - 1) {
                            countAC++;
                            mHandlerAC.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        } else {
                            //????????????
                            SqliteUtil.setListJson(GlobalConstant.MAX_ONEKEY_LAST_DATA2, MaxUtil.saveList(dataListAC));
                            //??????list
                            //????????????
                            if (mListAc != null) {
                                SharedPreferencesUnit.getInstance(mContext).put(GlobalConstant.MAX_ONEKEY_LIST_LAST_DATA2, new Gson().toJson(mListAc));
                            }
                            //??????ui
//                            refreshUI();
                            countAC = 0;
                            //????????????
                            SocketClientUtil.close(mClientUtilAC);
                            refreshFinish();
                            startReal();
                        }
                        LogUtil.i("????????????:" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        countAC = 0;
                        //????????????
                        SocketClientUtil.close(mClientUtilAC);
                        Mydialog.Dismiss();
                        startReal();
                    }
                    break;
                case TIMEOUT_RECEIVE://????????????
                    if (nowTimeCount < TIME_COUNT){
                        LogUtil.e("??????nowTimecount:" + nowTimeCount);
                        BtnDelayUtil.receiveMessage(this);
                        //??????????????????
                        this.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        nowTimeCount++;
                        break;
                    }
                    nowTimeCount = 0;
                case SOCKET_SERVER_SET://?????????wifi??????
                    LogUtil.e("nowTimecount:" + nowTimeCount);
                    try {
                        MyControl.showJumpWifiSet(MaxCheckOneKeyActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    reStartBtn();
                    break;
                case SocketClientUtil.SOCKET_CLOSE:
                case SocketClientUtil.SOCKET_EXCETION_CLOSE:
                    countAC = 0;
                    SocketClientUtil.close(mClientUtilAC);
                    break;
                default:
                    BtnDelayUtil.dealMaxBtn(this, what, mContext);
                    break;
            }
        }
    };


    /**
     * ????????????
     */
    public static void refreshFinish() {
//        Mydialog.Dismiss();
    }



    //????????????:??????????????????
    private SocketClientUtil mClientUtilWriterAC;

    //?????????????????????
    private void writeRegisterValueAC() {
        mClientUtilWriterAC = SocketClientUtil.connectServer(mHandlerWriteAC);
    }

    /**
     * ????????????handle
     */
    //???????????????????????????
    private byte[] sendBytesAC;
    private int[] nowSetAC = {0x10, 0x0104, 0x0108};
    private int[] valueAC = {4, 5, 6, 7, 1};
    private int posTimeAC = 0;
    Handler mHandlerWriteAC = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //????????????
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessageWrite(this);
                    if (nowSetAC != null && nowSetAC[2] != -1) {
                        sendBytesAC = SocketClientUtil.sendMsgToServer10(mClientUtilWriterAC, nowSetAC, valueAC);
                        LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(sendBytesAC));
                    } else {
                        toast("??????????????????????????????");
                    }
                    break;
                //??????????????????
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    nowTimeCount = 0;
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //?????????????????????
                        boolean isFlag = MaxUtil.isCheckFull10(mContext, bytes);
                        if (isFlag) {
                            //????????????
                            this.sendEmptyMessageDelayed(SOCKET_10_READ, waitTime1);
                            //????????????
                            noticeUpdateTime(-1, true, 2);
                        } else {
                            //??????????????????
                            noticeUpdateTime(-1, false, 2);
                            reStartBtn();
                        }
                        LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //??????tcp??????
                        SocketClientUtil.close(mClientUtilWriterAC);
                        BtnDelayUtil.refreshFinish();
                    }
                    break;
                case SOCKET_10_READ:
                    noticeUpdateTime(0, true, 2);
                    connectServerAC();
                    break;
                case 200://????????????
                    //?????????
                    int time = nextTime1 - nowTime1;
                    if (posTimeAC < time) {
                        posTimeAC = posTimeAC + time / updataUiTime1;
                    } else {
                        posTimeAC = time;
                    }
                    //????????????
                    int width = countProgressTime(1, nowTime1 + posTimeAC) * mWidth / totalTime;
                    String progressText = String.format("%s%d%%", readStr2, countProgressTime(1, nowTime1 + posTimeAC) * 100 / totalTime);
                    mTvStart.setText(progressText);
                    mTvTitle.setText(progressText);
                    LogUtil.i("????????????0???" + nowTime + "---?????????0???" + totalTime0 + "---????????????" + totalTime);
                    if (nowTime1 == totalTime1) {
                        width = 0;
                        mTvStart.setText(readStr1);
                        mTvTitle.setText(titleStr);
                    } else {
                        //?????????
                        this.sendEmptyMessageDelayed(200, time / updataUiTime1);
                    }
                    mViewParams.width = width;
                    mReadView.setLayoutParams(mViewParams);
                    break;
                case TIMEOUT_RECEIVE://????????????
                    if (nowTimeCount < TIME_COUNT){
                        LogUtil.e("??????nowTimecount:" + nowTimeCount);
                        BtnDelayUtil.receiveMessage(this);
                        //??????????????????
                        this.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        nowTimeCount++;
                        break;
                    }
                    nowTimeCount = 0;
                case SOCKET_SERVER_SET://?????????wifi??????
                    LogUtil.e("nowTimecount:" + nowTimeCount);
                    try {
                        MyControl.showJumpWifiSet(MaxCheckOneKeyActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    reStartBtn();
                    break;
                case SocketClientUtil.SOCKET_CLOSE:
                case SocketClientUtil.SOCKET_EXCETION_CLOSE:
                    countAC = 0;
                    SocketClientUtil.close(mClientUtilWriterAC);
                    break;
                default:
                    BtnDelayUtil.dealMaxBtn(this, what, mContext);
                    break;
            }
        }
    };

    /**
     * ????????????UI
     */
    private void updateUiAC() {
        MaxUtil.setLineChartDataSpe(mContext, mLineChartAC, dataListAC, colorsAC, colorsAC, dataListAC.size(), R.color.highLightColor, true);
        mAdapterAc.notifyDataSetChanged();
    }

    /**
     * THDV?????????????????????
     */
    private List<MaxCheckOneKeyTHDVBean> mListTHDV;
    private MaxCheckOneKeyTHDVAdapter mAdapterTHDV;
    private String[] mTitlesTHDV = {
            "Total(R)", "Total(S)", "Total(T)"
    };
    int[] colorsTHDV = {R.color.max_iv_graph_linechart1, R.color.max_iv_graph_linechart2, R.color.max_iv_graph_linechart3};
    private List<List<BarEntry>> dataListTHDV;

    private void initRecyclerViewTHDV() {
        mRecyclerViewTHDV.setLayoutManager(new LinearLayoutManager(this));
        mListTHDV = new ArrayList<>();
        mAdapterTHDV = new MaxCheckOneKeyTHDVAdapter(R.layout.item_max_check_onekey_thdv_act, mListTHDV);
        mRecyclerViewTHDV.setAdapter(mAdapterTHDV);
        initRecyclerDataTHDV(null);
        initBarChartTHDV();
        initLastDataTHDV();
    }

    private void initLastDataTHDV() {
        //?????????
        String json = SharedPreferencesUnit.getInstance(mContext).get(GlobalConstant.MAX_ONEKEY_LIST_LAST_DATA3, null);
        if (!TextUtils.isEmpty(json)) {
            try {
                List<MaxCheckOneKeyTHDVBean> newList = new Gson().fromJson(json, new TypeToken<List<MaxCheckOneKeyTHDVBean>>() {
                }.getType());
                if (newList != null && mAdapterTHDV != null) {
                    for (int i = 0;i < newList.size();i++){
                        MaxCheckOneKeyTHDVBean bean = newList.get(i);
                        bean.setImgColorId(colorsTHDV[i]);
                    }
                    mAdapterTHDV.replaceData(newList);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initBarChartTHDV() {
        MaxUtil.initBarChart(this, mBarChartTHDV, "%", true, R.color.note_bg_white, R.color.grid_bg_white, R.color.grid_bg_white, true, R.color.grid_bg_white, true, R.color.grid_bg_white, R.color.highLightColor, null);
        initDataTHDV();
        initLastTHDV();
        MaxUtil.setBarChartData(mContext, mBarChartTHDV, dataListTHDV, colorsTHDV, colorsTHDV, 3);
    }

    private void initLastTHDV() {
        List<List<BarEntry>> entryList = MaxUtil.getBarEntryList(SqliteUtil.getListJson(GlobalConstant.MAX_ONEKEY_LAST_DATA3));
        if (entryList != null) {
            dataListTHDV = entryList;
        }
    }

    public void initDataTHDV() {
        dataListTHDV = new ArrayList<>();
        for (int i = 0; i < colorsTHDV.length; i++) {
            List<BarEntry> list = new ArrayList<>();
            dataListTHDV.add(list);
        }
    }

    public void initRecyclerDataTHDV(List<MaxCheckOneKeyTHDVBean> list) {
        if (list == null || list.size() == 0) {
            List<MaxCheckOneKeyTHDVBean> newList = new ArrayList<>();
            for (int i = 0; i < colorsTHDV.length; i++) {
                MaxCheckOneKeyTHDVBean bean = new MaxCheckOneKeyTHDVBean();
                bean.setImgColorId(colorsTHDV[i]);
                bean.setTitle(mTitlesTHDV[i]);
                newList.add(bean);
            }
            mAdapterTHDV.replaceData(newList);
        } else {
            mAdapterTHDV.replaceData(list);
        }
    }

    //????????????
    private SocketClientUtil mClientUtilTHDV;

    private void connectServerTHDV() {
        mClientUtilTHDV = SocketClientUtil.newInstance();
        if (mClientUtilTHDV != null) {
            mClientUtilTHDV.connect(mHandlerTHDV);
        }
    }

    /**
     * ???????????????handle
     */
    private int countTHDV = 0;
    int[][] funsTHDV = {
            {0x14, 0x1770, 0x1770 + 0x7d - 1}

    };
    Handler mHandlerTHDV = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //????????????
                case SocketClientUtil.SOCKET_SEND:
                    if (countTHDV < funsTHDV.length) {
                        sendMsg(mClientUtilTHDV, funsTHDV[countTHDV]);
                    }
                    break;
                case SocketClientUtil.SOCKET_SEND_MSG:
                    BtnDelayUtil.sendMessage(this);
                    //??????????????????
                    //????????????
                    //?????????????????????????????????????????????
                    Message msgSend = Message.obtain();
                    msgSend.what = 100;
                    mHandlerTHDV.sendEmptyMessageDelayed(100, 3000);
                    String sendMsg = (String) msg.obj;
                    LogUtil.i("????????????:" + sendMsg);
                    break;
                //??????????????????
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    nowTimeCount = 0;
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //?????????????????????
                        boolean isCheck = ModbusUtil.checkModbus(bytes);
                        if (isCheck) {
                            //???????????????????????????
                            RegisterParseUtil.parseMaxErrTHDV(dataListTHDV, mListTHDV, bytes, countTHDV);
//                            //??????ui
                            updateUiTHDV();
                        }
                        noticeUpdateTime(countTHDV + 1, true, 3);
                        if (countTHDV < funsTHDV.length - 1) {
                            countTHDV++;
                            mHandlerTHDV.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        } else {
                            //????????????
                            SqliteUtil.setListJson(GlobalConstant.MAX_ONEKEY_LAST_DATA3, MaxUtil.saveBarList(dataListTHDV));
//                            LogUtil.i("thdv??????:"+MaxUtil.saveBarList(dataListTHDV));
                            //????????????
                            if (mListTHDV != null) {
                                SharedPreferencesUnit.getInstance(mContext).put(GlobalConstant.MAX_ONEKEY_LIST_LAST_DATA3, new Gson().toJson(mListTHDV));
                            }
                            //??????ui
//                            refreshUI();
                            countTHDV = 0;
                            //????????????
                            SocketClientUtil.close(mClientUtilTHDV);
                            refreshFinish();
                            startReal();
                        }
                        LogUtil.i("????????????:" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        countAC = 0;
                        //????????????
                        SocketClientUtil.close(mClientUtilAC);
                        Mydialog.Dismiss();
                        startReal();
                    }
                    break;
                case TIMEOUT_RECEIVE://????????????
                    if (nowTimeCount < TIME_COUNT){
                        LogUtil.e("??????nowTimecount:" + nowTimeCount);
                        BtnDelayUtil.receiveMessage(this);
                        //??????????????????
                        this.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        nowTimeCount++;
                        break;
                    }
                    nowTimeCount = 0;
                case SOCKET_SERVER_SET://?????????wifi??????
                    LogUtil.e("nowTimecount:" + nowTimeCount);
                    try {
                        MyControl.showJumpWifiSet(MaxCheckOneKeyActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    reStartBtn();
                    break;
                case SocketClientUtil.SOCKET_CLOSE:
                case SocketClientUtil.SOCKET_EXCETION_CLOSE:
                    countTHDV = 0;
                    SocketClientUtil.close(mClientUtilTHDV);
                    break;
                default:
                    BtnDelayUtil.dealMaxBtn(this, what, mContext);
                    break;
            }
        }
    };

    private void updateUiTHDV() {
        MaxUtil.setBarChartData(mContext, mBarChartTHDV, dataListTHDV, colorsTHDV, colorsTHDV, 3);
        mAdapterTHDV.notifyDataSetChanged();
    }

    //????????????:??????????????????
    private SocketClientUtil mClientUtilWriterTHDV;

    //?????????????????????
    private void writeRegisterValueTHDV() {
        mClientUtilWriterTHDV = SocketClientUtil.connectServer(mHandlerWriteTHDV);
    }

    /**
     * ????????????handle
     */
    //???????????????????????????
    private byte[] sendBytesTHDV;
    private int[] nowSetTHDV = {0x10, 0x0109, 0x0109};
    private int[] valueTHDV = {1};
    private int posTimeTHDV = 0;
    Handler mHandlerWriteTHDV = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //????????????
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessageWrite(this);
                    if (nowSetTHDV != null && nowSetTHDV[2] != -1) {
                        sendBytesTHDV = SocketClientUtil.sendMsgToServer10(mClientUtilWriterTHDV, nowSetTHDV, valueTHDV);
                        LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(sendBytesTHDV));
                    } else {
                        toast("??????????????????????????????");
                    }
                    break;
                //??????????????????
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    nowTimeCount = 0;
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //?????????????????????
                        boolean isFlag = MaxUtil.isCheckFull10(mContext, bytes);
                        if (isFlag) {
                            //????????????
                            this.sendEmptyMessageDelayed(SOCKET_10_READ, waitTime2);
                            //????????????
                            noticeUpdateTime(-1, true, 3);
                        } else {
                            //??????????????????
                            noticeUpdateTime(-1, false, 3);
                            reStartBtn();
                        }
                        LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //??????tcp??????
                        SocketClientUtil.close(mClientUtilWriterTHDV);
                        BtnDelayUtil.refreshFinish();
                    }
                    break;
                case SOCKET_10_READ:
                    noticeUpdateTime(0, true, 3);
                    connectServerTHDV();
                    break;
                case 200://????????????
                    //?????????
                    int time = nextTime2 - nowTime2;
                    if (posTimeTHDV < time) {
                        posTimeTHDV = posTimeTHDV + time / updataUiTime2;
                    } else {
                        posTimeTHDV = time;
                    }
                    //????????????
                    int width = countProgressTime(2, nowTime2 + posTimeTHDV) * mWidth / totalTime;
                    String progressText = String.format("%s%d%%", readStr2, countProgressTime(2, nowTime2 + posTimeTHDV) * 100 / totalTime);
                    mTvStart.setText(progressText);
                    mTvTitle.setText(progressText);
                    if (nowTime2 == totalTime2) {
                        width = 0;
                        mTvStart.setText(readStr1);
                        mTvTitle.setText(titleStr);
                    } else {
                        //?????????
                        this.sendEmptyMessageDelayed(200, time / updataUiTime2);
                    }
                    mViewParams.width = width;
                    mReadView.setLayoutParams(mViewParams);
                    break;
                case TIMEOUT_RECEIVE://????????????
                    if (nowTimeCount < TIME_COUNT){
                        LogUtil.e("??????nowTimecount:" + nowTimeCount);
                        BtnDelayUtil.receiveMessage(this);
                        //??????????????????
                        this.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        nowTimeCount++;
                        break;
                    }
                    nowTimeCount = 0;
                case SOCKET_SERVER_SET://?????????wifi??????
                    LogUtil.e("nowTimecount:" + nowTimeCount);
                    try {
                        MyControl.showJumpWifiSet(MaxCheckOneKeyActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    reStartBtn();
                    break;
                case SocketClientUtil.SOCKET_CLOSE:
                case SocketClientUtil.SOCKET_EXCETION_CLOSE:
                    countAC = 0;
                    SocketClientUtil.close(mClientUtilWriterTHDV);
                    break;
                default:
                    BtnDelayUtil.dealMaxBtn(this, what, mContext);
                    break;
            }
        }
    };
    /**
     * RST?????????????????????
     */
    private List<MaxCheckOneKeyRSTBean> mListRST;
    private MaxCheckOneKeyRSTAdapter mAdapterRST;
    private String[] mTitlesRST;

    private void initRecyclerViewRST() {
        mRecyclerViewRST.setLayoutManager(new LinearLayoutManager(this));
        mListRST = new ArrayList<>();
        mAdapterRST = new MaxCheckOneKeyRSTAdapter(R.layout.item_max_check_onekey_rst_act, mListRST);
        mRecyclerViewRST.setAdapter(mAdapterRST);
        initRecyclerDataRST(MaxUtil.getRSTBean(SqliteUtil.getListJson(GlobalConstant.MAX_ONEKEY_LAST_DATA4)));
    }
    /**
     * ISO???????????????
     */
    MaxCheckOneKeyISOBean mISOBean;
    private void initRecyclerViewISO() {
        mISOBean = new MaxCheckOneKeyISOBean();
        MaxCheckOneKeyISOBean bean = MaxUtil.getISOBean(SqliteUtil.getListJson(GlobalConstant.MAX_ONEKEY_LAST_DATA5));
        if (bean != null && !TextUtils.isEmpty(bean.getIsoValue())){
            tvISO.setText(bean.getIsoValue());
        }
    }

    public void initRecyclerDataRST(List<MaxCheckOneKeyRSTBean> list) {
        if (list == null || list.size() == 0) {
            List<MaxCheckOneKeyRSTBean> newList = new ArrayList<>();
            for (int i = 0; i < mTitlesRST.length; i++) {
                MaxCheckOneKeyRSTBean bean = new MaxCheckOneKeyRSTBean();
                bean.setTitle(mTitlesRST[i]);
                newList.add(bean);
            }
            mAdapterRST.replaceData(newList);
        } else {
            for (int i = 0; i < list.size(); i++) {
                MaxCheckOneKeyRSTBean bean = list.get(i);
                bean.setTitle(mTitlesRST[i]);
            }
            mAdapterRST.replaceData(list);
        }
    }

    //????????????
    private SocketClientUtil mClientUtilRST;

    private void connectServerRST() {
        mClientUtilRST = SocketClientUtil.newInstance();
        if (mClientUtilRST != null) {
            mClientUtilRST.connect(mHandlerRST);
        }
    }

    /**
     * ???????????????handle
     */
    private int countRST = 0;
    int[][] funsRST = {
            {0x14, 0x1770, 0x1770 + 0x7d - 1}

    };
    Handler mHandlerRST = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //????????????
                case SocketClientUtil.SOCKET_SEND:
                    if (countRST < funsRST.length) {
                        sendMsg(mClientUtilRST, funsRST[countRST]);
                    }
                    break;
                case SocketClientUtil.SOCKET_SEND_MSG:
                    BtnDelayUtil.sendMessage(this);
                    //??????????????????
                    //????????????
                    //?????????????????????????????????????????????
                    Message msgSend = Message.obtain();
                    msgSend.what = 100;
                    mHandlerRST.sendEmptyMessageDelayed(100, 3000);
                    String sendMsg = (String) msg.obj;
                    LogUtil.i("????????????:" + sendMsg);
                    break;
                //??????????????????
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    nowTimeCount = 0;
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //?????????????????????
                        boolean isCheck = ModbusUtil.checkModbus(bytes);
                        if (isCheck) {
                            //???????????????????????????
                            RegisterParseUtil.parseMaxErrRST(mListRST, bytes, countRST);
                            //??????ui
                            mAdapterRST.notifyDataSetChanged();
                        }
                        noticeUpdateTime(countRST + 1, true, 4);
                        if (countRST < funsRST.length - 1) {
                            countRST++;
                            mHandlerRST.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        } else {
                            //????????????
                            SqliteUtil.setListJson(GlobalConstant.MAX_ONEKEY_LAST_DATA4, MaxUtil.saveRSTBean(mListRST));
                            //??????ui
//                            refreshUI();
                            countRST = 0;
                            //????????????
                            SocketClientUtil.close(mClientUtilRST);
                            refreshFinish();
                            startReal();
                        }
                        LogUtil.i("????????????:" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        countRST = 0;
                        //????????????
                        SocketClientUtil.close(mClientUtilRST);
                        Mydialog.Dismiss();
                        startReal();
                    }
                    break;
                case TIMEOUT_RECEIVE://????????????
                    if (nowTimeCount < TIME_COUNT){
                        LogUtil.e("??????nowTimecount:" + nowTimeCount);
                        BtnDelayUtil.receiveMessage(this);
                        //??????????????????
                        this.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        nowTimeCount++;
                        break;
                    }
                    nowTimeCount = 0;
                case SOCKET_SERVER_SET://?????????wifi??????
                    LogUtil.e("nowTimecount:" + nowTimeCount);
                    try {
                        MyControl.showJumpWifiSet(MaxCheckOneKeyActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    reStartBtn();
                    break;
                case SocketClientUtil.SOCKET_CLOSE:
                case SocketClientUtil.SOCKET_EXCETION_CLOSE:
                    countRST = 0;
                    SocketClientUtil.close(mClientUtilRST);
                    break;
                default:
                    BtnDelayUtil.dealMaxBtn(this, what, mContext);
                    break;
            }
        }
    };
    //????????????:??????????????????
    private SocketClientUtil mClientUtilWriterRST;

    //?????????????????????
    private void writeRegisterValueRST() {
        mClientUtilWriterRST = SocketClientUtil.connectServer(mHandlerWriteRST);
    }

    /**
     * ????????????handle
     */
    //???????????????????????????
    private byte[] sendBytesRST;
    private int[] nowSetRST = {0x10, 0x010a, 0x010a};
    private int[] valueRST = {1};
    private int posTimeRST = 0;
    Handler mHandlerWriteRST = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //????????????
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessageWrite(this);
                    if (nowSetRST != null && nowSetRST[2] != -1) {
                        sendBytesRST = SocketClientUtil.sendMsgToServer10(mClientUtilWriterRST, nowSetRST, valueRST);
                        LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(sendBytesRST));
                    } else {
                        toast("??????????????????????????????");
                    }
                    break;
                //??????????????????
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    nowTimeCount = 0;
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //?????????????????????
                        boolean isFlag = MaxUtil.isCheckFull10(mContext, bytes);
                        if (isFlag) {
                            //????????????
                            this.sendEmptyMessageDelayed(SOCKET_10_READ, waitTime3);
                            //????????????
                            noticeUpdateTime(-1, true, 4);
                        } else {
                            //??????????????????
                            noticeUpdateTime(-1, false, 4);
                            reStartBtn();
                        }
                        LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //??????tcp??????
                        SocketClientUtil.close(mClientUtilWriterRST);
                        BtnDelayUtil.refreshFinish();
                    }
                    break;
                case SOCKET_10_READ:
                    noticeUpdateTime(0, true, 4);
                    connectServerRST();
                    break;
                case 200://????????????
                    //?????????
                    int time = nextTime3 - nowTime3;
                    if (posTimeRST < time) {
                        posTimeRST = posTimeRST + time / updataUiTime3;
                    } else {
                        posTimeRST = time;
                    }
                    //????????????
                    int width = countProgressTime(3, nowTime3 + posTimeRST) * mWidth / totalTime;
                    String progressText = String.format("%s%d%%", readStr2, countProgressTime(3, nowTime3 + posTimeRST) * 100 / totalTime);
                    LogUtil.i(String.format("?????????%s", progressText));
                    mTvStart.setText(progressText);
                    mTvTitle.setText(progressText);
                    if (nowTime3 == totalTime3) {
                        width = 0;
                        mTvStart.setText(readStr1);
                        mTvTitle.setText(titleStr);
                    } else {
                        //?????????
                        this.sendEmptyMessageDelayed(200, time / updataUiTime3);
                    }
                    mViewParams.width = width;
                    mReadView.setLayoutParams(mViewParams);
                    break;
                case TIMEOUT_RECEIVE://????????????
                    if (nowTimeCount < TIME_COUNT){
                        LogUtil.e("??????nowTimecount:" + nowTimeCount);
                        BtnDelayUtil.receiveMessage(this);
                        //??????????????????
                        this.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        nowTimeCount++;
                        break;
                    }
                    nowTimeCount = 0;
                case SOCKET_SERVER_SET://?????????wifi??????
                    LogUtil.e("nowTimecount:" + nowTimeCount);
                    try {
                        MyControl.showJumpWifiSet(MaxCheckOneKeyActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    reStartBtn();
                    break;
                case SocketClientUtil.SOCKET_CLOSE:
                case SocketClientUtil.SOCKET_EXCETION_CLOSE:
                    countAC = 0;
                    SocketClientUtil.close(mClientUtilWriterRST);
                    break;
                default:
                    BtnDelayUtil.dealMaxBtn(this, what, mContext);
                    break;
            }
        }
    };

    //????????????
    private SocketClientUtil mClientUtilISO;

    private void connectServerISO() {
        mClientUtilISO = SocketClientUtil.newInstance();
        if (mClientUtilISO != null) {
            mClientUtilISO.connect(mHandlerISO);
        }
    }

    /**
     * ???????????????handle
     */
    private int countISO = 0;
    int[][] funsISO = {
            {0x14, 0x1770, 0x1770 + 0x7d - 1}

    };
    Handler mHandlerISO = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //????????????
                case SocketClientUtil.SOCKET_SEND:
                    if (countISO < funsISO.length) {
                        sendMsg(mClientUtilISO, funsISO[countISO]);
                    }
                    break;
                case SocketClientUtil.SOCKET_SEND_MSG:
                    BtnDelayUtil.sendMessage(this);
                    //??????????????????
                    //????????????
                    //?????????????????????????????????????????????
                    Message msgSend = Message.obtain();
                    msgSend.what = 100;
                    mHandlerISO.sendEmptyMessageDelayed(100, 3000);
                    String sendMsg = (String) msg.obj;
                    LogUtil.i("????????????:" + sendMsg);
                    break;
                //??????????????????
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    nowTimeCount = 0;
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //?????????????????????
                        boolean isCheck = ModbusUtil.checkModbus(bytes);
                        if (isCheck) {
                            //???????????????????????????
                            RegisterParseUtil.parseMaxErrISO(mISOBean, bytes, countISO);
                            //??????ui
                            if (mISOBean != null && !TextUtils.isEmpty(mISOBean.getIsoValue())){
                                tvISO.setText(mISOBean.getIsoValue());
                            }
                        }
                        noticeUpdateTime(countISO + 1, true, 5);
                        if (countISO < funsISO.length - 1) {
                            countISO++;
                            mHandlerISO.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        } else {
                            //????????????
                            SqliteUtil.setListJson(GlobalConstant.MAX_ONEKEY_LAST_DATA5, MaxUtil.saveISOBean(mISOBean));
                            //??????ui
//                            refreshUI();
                            countISO = 0;
                            //????????????
                            SocketClientUtil.close(mClientUtilISO);
                            refreshFinish();
                            startReal();
                        }
                        LogUtil.i("????????????:" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        countISO = 0;
                        //????????????
                        SocketClientUtil.close(mClientUtilISO);
                        Mydialog.Dismiss();
                        startReal();
                    }
                    break;
                case TIMEOUT_RECEIVE://????????????
                    if (nowTimeCount < TIME_COUNT){
                        BtnDelayUtil.receiveMessage(this);
                        LogUtil.e("??????nowTimecount:" + nowTimeCount);
                        //??????????????????
                        this.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        nowTimeCount++;
                        break;
                    }
                    nowTimeCount = 0;
                case SOCKET_SERVER_SET://?????????wifi??????
                    try {
                        LogUtil.e("nowTimecount:" + nowTimeCount);
                        MyControl.showJumpWifiSet(MaxCheckOneKeyActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    reStartBtn();
                    break;
                case SocketClientUtil.SOCKET_CLOSE:
                case SocketClientUtil.SOCKET_EXCETION_CLOSE:
                    countISO = 0;
                    SocketClientUtil.close(mClientUtilISO);
                    break;
                default:
                    BtnDelayUtil.dealMaxBtn(this, what, mContext);
                    break;
            }
        }
    };
    //????????????:??????????????????
    private SocketClientUtil mClientUtilWriterISO;

    //?????????????????????
    private void writeRegisterValueISO() {
        mClientUtilWriterISO = SocketClientUtil.connectServer(mHandlerWriteISO);
    }

    /**
     * ????????????handle
     */
    //???????????????????????????
    private byte[] sendBytesISO;
    private int[] nowSetISO = {0x10, 0x0135, 0x0135};
    private int[] valueISO = {1};
    private int posTimeISO = 0;
    Handler mHandlerWriteISO = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //????????????
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessageWrite(this);
                    if (nowSetISO != null && nowSetISO[2] != -1) {
                        sendBytesISO = SocketClientUtil.sendMsgToServer10(mClientUtilWriterISO, nowSetISO, valueISO);
                        LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(sendBytesISO));
                    } else {
                        toast("??????????????????????????????");
                    }
                    break;
                //??????????????????
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    nowTimeCount = 0;
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //?????????????????????
                        boolean isFlag = MaxUtil.isCheckFull10(mContext, bytes);
                        if (isFlag) {
                            //????????????
                            this.sendEmptyMessageDelayed(SOCKET_10_READ, waitTime4);
                            //????????????
                            noticeUpdateTime(-1, true, 5);
                        } else {
                            //??????????????????
                            noticeUpdateTime(-1, false, 5);
                            reStartBtn();
                        }
                        LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //??????tcp??????
                        SocketClientUtil.close(mClientUtilWriterISO);
                        BtnDelayUtil.refreshFinish();
                    }
                    break;
                case SOCKET_10_READ:
                    noticeUpdateTime(0, true, 5);
                    connectServerISO();
                    break;
                case 200://????????????
                    //?????????
                    int time = nextTime4 - nowTime4;
                    if (posTimeISO < time) {
                        posTimeISO = posTimeISO + time / updataUiTime4;
                    } else {
                        posTimeISO = time;
                    }
                    //????????????
                    int width = countProgressTime(4, nowTime4 + posTimeISO) * mWidth / totalTime;
                    String progressText = String.format("%s%d%%", readStr2, countProgressTime(4, nowTime4 + posTimeISO) * 100 / totalTime);
                    LogUtil.i(String.format("?????????%s", progressText));
                    mTvStart.setText(progressText);
                    mTvTitle.setText(progressText);
                    if (nowTime4 == totalTime4) {
                        width = 0;
                        mTvStart.setText(readStr1);
                        mTvTitle.setText(titleStr);
                    } else {
                        //?????????
                        this.sendEmptyMessageDelayed(200, time / updataUiTime4);
                    }
                    mViewParams.width = width;
                    mReadView.setLayoutParams(mViewParams);
                    break;
                case TIMEOUT_RECEIVE://????????????
                    if (nowTimeCount < TIME_COUNT){
                        LogUtil.e("??????nowTimecount:" + nowTimeCount);
                        BtnDelayUtil.receiveMessage(this);
                        //??????????????????
                        this.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        nowTimeCount++;
                        break;
                    }
                    nowTimeCount = 0;
                case SOCKET_SERVER_SET://?????????wifi??????
                    try {
                        LogUtil.e("nowTimecount:" + nowTimeCount);
                        MyControl.showJumpWifiSet(MaxCheckOneKeyActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    reStartBtn();
                    break;
                case SocketClientUtil.SOCKET_CLOSE:
                case SocketClientUtil.SOCKET_EXCETION_CLOSE:
                    countISO = 0;
                    SocketClientUtil.close(mClientUtilWriterISO);
                    break;
                default:
                    BtnDelayUtil.dealMaxBtn(this, what, mContext);
                    break;
            }
        }
    };

    /**
     * IV?????????????????????
     */
    private TextView tvTitle1;
    private TextView tvTitle2;
    private TextView tvXValue;

    private void initRecyclerViewIV() {
        mRecyclerViewIV.setLayoutManager(new LinearLayoutManager(this));
        mListIV = new ArrayList<>();
        mAdapterIV = new MaxCheckIVAdapter(R.layout.item_max_check_iv_act, mListIV);
        View headerView = LayoutInflater.from(mContext).inflate(R.layout.header_max_check_iv_act, mRecyclerViewIV, false);
        tvXValue = (TextView) headerView.findViewById(R.id.tvXValue);
        tvTitle1 = (TextView) headerView.findViewById(R.id.tvTitle1);
        tvTitle2 = (TextView) headerView.findViewById(R.id.tvTitle2);
        mAdapterIV.addHeaderView(headerView);
        mRecyclerViewIV.setAdapter(mAdapterIV);
        initRecyclerDataIV(null);
        initLineChartIV();
    }

    public void initRecyclerDataIV(List<MaxCheckIVBean> list) {
        if (list == null || list.size() == 0) {
            List<MaxCheckIVBean> newList = new ArrayList<>();
            for (int i = 0; i < colors.length; i++) {
                MaxCheckIVBean bean = new MaxCheckIVBean();
                bean.setImgColorId(colors[i]);
                bean.setTitle(String.valueOf(i + 1));
                newList.add(bean);
            }
            mAdapterIV.replaceData(newList);
        } else {
            mAdapterIV.replaceData(list);
        }
    }

    /**
     * ???????????????IV---------------------------------------------------------------------------------------------------------------
     */
    private void initLineChartIV() {
        MaxUtil.initLineChart(mContext, mLineChartIV, 0, "", true, R.color.grid_bg_white, true, R.color.grid_bg_white, true, R.color.note_bg_white, R.color.grid_bg_white, R.color.grid_bg_white, R.color.highLightColor, false, R.string.m4, R.string.m5, new OnEmptyListener() {
            @Override
            public void onEmpty(Entry e, Highlight highlight) {
                LogUtil.i("x??????" + e.getX());
                getValuesByEntryIV(e);
            }
        });
        mLineChartIV.getAxisLeft().setAxisMinimum(0);
        Description description = new Description();
        description.setText("V(V)");
        description.setTextColor(ContextCompat.getColor(this, R.color.grid_bg_white));
        mLineChartIV.setDescription(description);
        initDataIV();
        initLastDataIV();
        MaxUtil.setLineChartData(mContext, mLineChartIV, dataList, colors, colors_a, dataList.size(), R.color.highLightColor);
        hideIVLast();
    }

    private List<ArrayList<Entry>> entryListIv;

    private void initLastDataIV() {
        entryListIv = MaxUtil.getEntryList(SqliteUtil.getListJson(GlobalConstant.MAX_ONEKEY_LAST_DATA1));
        if (entryListIv != null) {
            dataList = entryListIv;
        }
    }

    public void initDataIV() {
        dataList = new ArrayList<>();
        for (int i = 0; i < colors.length; i++) {
            ArrayList<Entry> list = new ArrayList<>();
            dataList.add(list);
        }
    }

    private void getValuesByEntryIV(Entry e) {
        LineData lineData = mLineChartIV.getData();
        List<ILineDataSet> dataSets = lineData.getDataSets();
        if (dataSets != null) {
            for (int i = 0; i < dataSets.size(); i++) {
                float xPos = e.getX();
                //?????????????????????
                LineDataSet lineDataSet = (LineDataSet) dataSets.get(i);
                //??????????????????
                List<Entry> entries = lineDataSet.getValues();
                //??????????????????
                MaxCheckIVBean maxCheckIVBean = mListIV.get(i);
                //???????????????
                maxCheckIVBean.setxValue(String.valueOf((int) xPos));
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
            mAdapterIV.notifyDataSetChanged();
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

    //????????????:??????????????????
    private SocketClientUtil mClientUtilWriter;

    //?????????????????????
    private void writeRegisterValue() {
        mClientUtilWriter = SocketClientUtil.connectServer(mHandlerWrite);
    }

    /**
     * ????????????handle
     */
    //???????????????????????????
    private byte[] sendBytes;
    private int[] nowSet = {16, 250, 250};
    private int[] values = {1};
    private int posTime = 0;
    Handler mHandlerWrite = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //????????????
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessageWrite(this);
                    if (nowSet != null && nowSet[2] != -1) {
                        sendBytes = SocketClientUtil.sendMsgToServer10(mClientUtilWriter, nowSet, values);
                        LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(sendBytes));
                    } else {
                        toast("??????????????????????????????");
                    }
                    break;
                //??????????????????
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    nowTimeCount = 0;
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //?????????????????????
                        boolean isFlag = MaxUtil.isCheckFull10(mContext, bytes);
                        if (isFlag) {
                            //????????????
                            this.sendEmptyMessageDelayed(SOCKET_10_READ, waitTime);
                            //????????????
                            noticeUpdateTime(-1, true, 1);
                        } else {
                            //??????????????????
                            noticeUpdateTime(-1, false, 1);
                            reStartBtn();
                        }
                        LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //??????tcp??????
                        SocketClientUtil.close(mClientUtilWriter);
                        refreshFinish();
                    }
                    break;
                case SOCKET_10_READ:
                    noticeUpdateTime(0, true, 1);
                    connectServer();
                    break;
                case 200://????????????
                    //?????????
                    int time = nextTime - nowTime;
                    if (posTime < time) {
                        posTime = posTime + time / updataUiTime;
                    } else {
                        posTime = time;
                    }
                    //????????????
                    int width = countProgressTime(0, nowTime + posTime) * mWidth / totalTime;
                    String progressText = String.format("%s%d%%", readStr2, countProgressTime(0, nowTime + posTime) * 100 / totalTime);
                    mTvStart.setText(progressText);
                    mTvTitle.setText(progressText);
                    if (nowTime == totalTime0) {
                        width = 0;
                        mTvStart.setText(readStr1);
                        mTvTitle.setText(titleStr);
                    } else {
                        //?????????
                        this.sendEmptyMessageDelayed(200, time / updataUiTime);
                    }
                    mViewParams.width = width;
                    mReadView.setLayoutParams(mViewParams);
                    break;
                case TIMEOUT_RECEIVE://????????????
                    if (nowTimeCount < TIME_COUNT){
                        LogUtil.e("??????nowTimecount:" + nowTimeCount);
                        BtnDelayUtil.receiveMessage(this);
                        //??????????????????
                        this.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        nowTimeCount++;
                        break;
                    }
                    nowTimeCount = 0;
                case SOCKET_SERVER_SET://?????????wifi??????
                    try {
                        LogUtil.e("nowTimecount:" + nowTimeCount);
                        MyControl.showJumpWifiSet(MaxCheckOneKeyActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    reStartBtn();
                    break;
                case SocketClientUtil.SOCKET_CLOSE:
                case SocketClientUtil.SOCKET_EXCETION_CLOSE:
                    count = 0;
                    SocketClientUtil.close(mClientUtilWriter);
                    break;
                default:
                    BtnDelayUtil.dealMaxBtn(this, what, mContext);
                    break;
            }
        }
    };


    //????????????
    private SocketClientUtil mClientUtil;

    private void connectServer() {
        mClientUtil = SocketClientUtil.newInstance();
        if (mClientUtil != null) {
            mClientUtil.connect(mHandler);
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

    public void initNoSelectDataIv() {
        int count = mLineChartIV.getData().getDataSetCount();
        for (int i = 0, size = mAdapterIV.getItemCount() - mAdapterIV.getHeaderLayoutCount(); i < size; i++) {
            MaxCheckIVBean item = mAdapterIV.getItem(i);
            if (!item.isSelect() && count > i) {
                MaxUtil.clearDataSetByIndex(mLineChartIV, i);
            }
        }
    }

    /**
     * ???????????????handle
     */
    private int count;
    int[][] funs = {{0x14, 0, 0x7d - 1}, {0x14, 0x7d, 0x7d * 2 - 1}, {0x14, 0x7d * 2, 0x7d * 3 - 1}, {0x14, 0x7d * 3, 0x7d * 4 - 1}, {0x14, 0x7d * 4, 0x7d * 5 - 1}, {0x14, 0x7d * 5, 0x7d * 6 - 1}, {0x14, 0x7d * 6, 0x7d * 7 - 1}, {0x14, 0x7d * 7, 0x7d * 8 - 1}};
    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //????????????
                case SocketClientUtil.SOCKET_SEND:
                    if (count < funs.length) {
                        sendMsg(mClientUtil, funs[count]);
                    }
                    break;
                case SocketClientUtil.SOCKET_SEND_MSG:
                    BtnDelayUtil.sendMessage(this);
                    //??????????????????
                    //????????????
                    //?????????????????????????????????????????????
                    Message msgSend = Message.obtain();
                    msgSend.what = 100;
                    mHandler.sendEmptyMessageDelayed(100, 3000);
                    String sendMsg = (String) msg.obj;
                    LogUtil.i("????????????:" + sendMsg);
                    break;
                //??????????????????
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    nowTimeCount = 0;
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //?????????????????????
                        boolean isCheck = ModbusUtil.checkModbus(bytes);
                        if (isCheck) {
                            //???????????????????????????
                            RegisterParseUtil.parseMax14(dataList, bytes, count);
                            //??????ui
                            setIPVData(mIPType);
//                            MaxUtil.setLineChartData(context, mLineChart, dataList, colors, colors_a, dataList.size(), R.color.highLightColor);
                            //???????????????
                            updateMax(count);
                        }
                        noticeUpdateTime(count + 1, true, 1);
                        if (count < funs.length - 1) {
                            count++;
                            mHandler.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        } else {
                            //????????????
                            SqliteUtil.setListJson(GlobalConstant.MAX_ONEKEY_LAST_DATA1, MaxUtil.saveList(dataList));
//                            hideIVLast();
                            initNoSelectDataIv();
                            //??????ui
//                            refreshUI();
                            count = 0;
                            //????????????
                            SocketClientUtil.close(mClientUtil);
                            refreshFinish();
                            startReal();
                        }
                        LogUtil.i("????????????:" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        count = 0;
                        //????????????
                        SocketClientUtil.close(mClientUtil);
                        Mydialog.Dismiss();
                        startReal();
                    }
                    break;
                case TIMEOUT_RECEIVE://????????????
                    if (nowTimeCount < TIME_COUNT){
                        LogUtil.e("??????nowTimecount:" + nowTimeCount);
                        BtnDelayUtil.receiveMessage(this);
                        //??????????????????
                        this.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        nowTimeCount++;
                        break;
                    }
                    nowTimeCount = 0;
                case SOCKET_SERVER_SET://?????????wifi??????
                    LogUtil.e("nowTimecount:" + nowTimeCount);
                    try {
                        try {
                            MyControl.showJumpWifiSet(MaxCheckOneKeyActivity.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    reStartBtn();
                    break;
                case SocketClientUtil.SOCKET_CLOSE:
                case SocketClientUtil.SOCKET_EXCETION_CLOSE:
                    count = 0;
                    SocketClientUtil.close(mClientUtil);
                    break;
                default:
                    BtnDelayUtil.dealMaxBtn(this, what, mContext);
                    break;
            }
        }
    };

    /**
     * ????????????????????????
     *
     * @param pos:???????????????-1???????????????0-6????????????
     * @param isSuccess??????????????????
     * @param count:???????????????
     */
    public void noticeUpdateTime(int pos, boolean isSuccess, int count) {
        if (count == 1) {
            posTime = 0;
            if (pos <= funs.length) {
                if (pos == -1) {
                    if (isSuccess) {
                        isFirst = true;
                    } else {
                        isFirst = true;
                    }
                    nowTime = nextTime;
                    nextTime = nowTime + waitTime;
                } else {
                    nowTime = nextTime;
                    nextTime = nowTime + readTimeOne;
                }
            }
        } else if (count == 2) {
            posTimeAC = 0;
            if (pos <= funsAC.length) {
                if (pos == -1) {
                    if (isSuccess) {
                        isFirst1 = true;
                    } else {
                        isFirst1 = true;
                    }
                    nowTime1 = nextTime1;
                    nextTime1 = nowTime1 + waitTime1;
                } else {
                    nowTime1 = nextTime1;
                    nextTime1 = nowTime1 + readTimeOne1;
                }
            }
        } else if (count == 3) {
            posTimeTHDV = 0;
            if (pos <= funsTHDV.length) {
                if (pos == -1) {
                    if (isSuccess) {
                        isFirst2 = true;
                    } else {
                        isFirst2 = true;
                    }
                    nowTime2 = nextTime2;
                    nextTime2 = nowTime2 + waitTime2;
                } else {
                    nowTime2 = nextTime2;
                    nextTime2 = nowTime2 + readTimeOne2;
                }
            }
        } else if (count == 4) {
            posTimeRST = 0;
            if (pos <= funsRST.length) {
                if (pos == -1) {
                    if (isSuccess) {
                        isFirst3 = true;
                    } else {
                        isFirst3 = true;
                    }
                    nowTime3 = nextTime3;
                    nextTime3 = nowTime3 + waitTime3;
                } else {
                    nowTime3 = nextTime3;
                    nextTime3 = nowTime3 + readTimeOne3;
                }
            }
        }else if (count == 5) {
            posTimeISO = 0;
            if (pos <= funsISO.length) {
                if (pos == -1) {
                    if (isSuccess) {
                        isFirst4 = true;
                    } else {
                        isFirst4 = true;
                    }
                    nowTime4 = nextTime4;
                    nextTime4 = nowTime4 + waitTime4;
                } else {
                    nowTime4 = nextTime4;
                    nextTime4 = nowTime4 + readTimeOne4;
                }
            }
        }
    }

    private void reStartBtn() {
        isReading = false;
        count = 0;
        mHandlerWrite.removeMessages(200);
        mHandlerWrite.removeMessages(SOCKET_10_READ);
        mHandler.removeMessages(SocketClientUtil.SOCKET_SEND);
        SocketClientUtil.close(mClientUtilWriter);
        SocketClientUtil.close(mClientUtil);

        mHandlerWriteAC.removeMessages(200);
        mHandlerWriteAC.removeMessages(SOCKET_10_READ);
        mHandlerAC.removeMessages(SocketClientUtil.SOCKET_SEND);
        SocketClientUtil.close(mClientUtilWriterAC);
        SocketClientUtil.close(mClientUtilAC);

        mHandlerWriteTHDV.removeMessages(200);
        mHandlerWriteTHDV.removeMessages(SOCKET_10_READ);
        mHandlerTHDV.removeMessages(SocketClientUtil.SOCKET_SEND);
        SocketClientUtil.close(mClientUtilWriterTHDV);
        SocketClientUtil.close(mClientUtilTHDV);

        mHandlerWriteRST.removeMessages(200);
        mHandlerWriteRST.removeMessages(SOCKET_10_READ);
        mHandlerRST.removeMessages(SocketClientUtil.SOCKET_SEND);
        SocketClientUtil.close(mClientUtilWriterRST);
        SocketClientUtil.close(mClientUtilRST);

        mViewParams.width = 0;
        mReadView.setLayoutParams(mViewParams);
        mTvStart.setText(readStr1);
        mTvTitle.setText(titleStr);
    }

    /**
     * ???????????????
     */
    private void updateMax(int position) {
        LineData lineData = mLineChartIV.getData();
        List<ILineDataSet> dataSets = lineData.getDataSets();
        if (dataSets.size() > position) {
            //?????????????????????
            LineDataSet lineDataSet = (LineDataSet) dataSets.get(position);
            //??????????????????
            List<Entry> entries = lineDataSet.getValues();
            //??????????????????
            MaxCheckIVBean maxCheckIVBean = mListIV.get(position);
            //?????????y???
            setMax(lineDataSet, entries, maxCheckIVBean);
            mAdapterIV.notifyDataSetChanged();
        }
    }

    private void initIntent() {
        Intent mIntent = getIntent();
        if (mIntent != null) {
            mTitle = mIntent.getStringExtra("title");
            mContentSelect = mIntent.getStringExtra("content");
        }
    }


    private void initHeaderView() {
        setHeaderImage(headerView, R.drawable.icon_return, Position.LEFT, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reStartBtn();
                finish();
            }
        });
        setHeaderTitle(headerView, titleStr);

//        setHeaderTvTitle(headerView, "??????", new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MaxUtil.shootScrollView(mScrollView,path + "/aaaa.png");
//            }
//        });

//        if (getLanguage() == 0) {
//            setHeaderTvTitle(headerView, getString(R.string.m??????), new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (Build.VERSION.SDK_INT >= 23) {
//                        if (EasyPermissions.hasPermissions(MaxCheckOneKeyActivity.this, PermissionCodeUtil.PERMISSION_EXTERNAL_STORAGE)) {
//                            share();
//                        } else {
//                            EasyPermissions.requestPermissions(MaxCheckOneKeyActivity.this, String.format(getString(R.string.m???????????????????????????), getString(R.string.m??????)), PermissionCodeUtil.PERMISSION_EXTERNAL_STORAGE_CODE, PermissionCodeUtil.PERMISSION_EXTERNAL_STORAGE);
//                        }
//                    } else {
//                        share();
//                    }
//
//                }
//            });
//        }
    }

//    private void share() {
//        String picName = "OneKeyCheck.jpg";
//        new CircleDialog.Builder(MaxCheckOneKeyActivity.this)
//                .setTitle(getResources().getString(R.string.????????????))
//                .setText("?????????????????????")
//                .setNegative(getResources().getString(R.string.all_no), new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        ShareUtil.shareLongPicture(MaxCheckOneKeyActivity.this, picName, titleStr, mScrollView, shareListener, false);
//                    }
//                })
//                .setPositive(getResources().getString(R.string.all_ok), new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        ShareUtil.shareLongPicture(MaxCheckOneKeyActivity.this, picName, titleStr, mScrollView, shareListener, true);
//                    }
//                }).show();
//    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            reStartBtn();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * ??????I-V/P-V??????
     *
     * @param type???????????? 0???I-V
     * 1???P-V
     */
    private List<ArrayList<Entry>> newLists;

    public void setIPVData(int type) {
        switch (type) {
            case 0://I-V
                MaxUtil.setLineChartData(mContext, mLineChartIV, dataList, colors, colors_a, dataList.size(), R.color.highLightColor);
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
                MaxUtil.setLineChartData(mContext, mLineChartIV, newLists, colors, colors_a, newLists.size(), R.color.highLightColor);
                break;
        }
    }


    /**
     * ??????????????????????????????
     */
    public void startReal() {
        nowTimeCount = 0;
        if (nowContent.contains("3")) {
            nowTime3 = 0;
            posTimeRST = nowTime3;
            nextTime3 = readTimeOne3;
            if (isFirst3) {
                writeRegisterValueRST();
            } else {
                connectServerRST();
            }
            nowContent = nowContent.replace("3", "");
            mHandlerWriteRST.sendEmptyMessage(200);
        } else if (nowContent.contains("0")) {
            nowTime = 0;
            posTime = nowTime;
            nextTime = readTimeOne;
            if (isFirst) {
                writeRegisterValue();
            } else {
                connectServer();
            }
            nowContent = nowContent.replace("0", "");
            mHandlerWrite.sendEmptyMessage(200);
        } else if (nowContent.contains("2")) {
            nowTime2 = 0;
            posTimeTHDV = nowTime2;
            nextTime2 = readTimeOne2;
            initDataTHDV();
            if (isFirst2) {
                writeRegisterValueTHDV();
            } else {
                connectServerTHDV();
            }
            nowContent = nowContent.replace("2", "");
            mHandlerWriteTHDV.sendEmptyMessage(200);
        } else if (nowContent.contains("1")) {
            nowTime1 = 0;
            posTimeAC = nowTime1;
            nextTime1 = readTimeOne1;
            if (isFirst1) {
                writeRegisterValueAC();
            } else {
                connectServerAC();
            }

            nowContent = nowContent.replace("1", "");
            mHandlerWriteAC.sendEmptyMessage(200);
        }else if (nowContent.contains("4")) {
            nowTime4 = 0;
            posTimeISO = nowTime4;
            nextTime4 = readTimeOne4;
            if (isFirst4) {
                writeRegisterValueISO();
            } else {
                connectServerISO();
            }

            nowContent = nowContent.replace("4", "");
            mHandlerWriteISO.sendEmptyMessage(200);
        }
    }

    /**
     * ???????????????
     */
    public void totalTime() {
        totalTime = 0;
        if (mContentSelect.contains("0")) {
            if (isFirst) {
                totalTime0 = waitTime + readTimeOne * (funs.length + 1);
            } else {
                totalTime0 = readTimeOne * funs.length;
            }
            totalTime = totalTime + totalTime0;
        }
        if (mContentSelect.contains("1")) {
            if (isFirst1) {
                totalTime1 = waitTime1 + readTimeOne1 * (funsAC.length + 1);
            } else {
                totalTime1 = readTimeOne1 * funsAC.length;
            }
            totalTime = totalTime + totalTime1;
        }
        if (mContentSelect.contains("2")) {
            if (isFirst2) {
                totalTime2 = waitTime2 + readTimeOne2 * (funsTHDV.length + 1);
            } else {
                totalTime2 = readTimeOne2 * funsTHDV.length;
            }
            totalTime = totalTime + totalTime2;
        }
        if (mContentSelect.contains("3")) {
            if (isFirst3) {
                totalTime3 = waitTime3 + readTimeOne3 * (funsRST.length + 1);
            } else {
                totalTime3 = readTimeOne3 * funsRST.length;
            }
            totalTime = totalTime + totalTime3;
        }
        if (mContentSelect.contains("4")) {
            if (isFirst4) {
                totalTime4 = waitTime4 + readTimeOne4 * (funsISO.length + 1);
            } else {
                totalTime4 = readTimeOne4 * funsISO.length;
            }
            totalTime = totalTime + totalTime4;
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mIvTHDetails) {
            jumpTo(MaxCheckOneKeyTHDVDetailsActivity.class, false);
        }
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (adapter == mAdapterAc) {
            switch (view.getId()) {
                case R.id.llTotal:
                    MaxCheckOneKeyAcBean item = mAdapterAc.getItem(position);
                    item.setSelect(!item.isSelect());
                    mAdapterAc.notifyDataSetChanged();
                    if (mLineChartAC != null && mLineChartAC.getData() != null) {
                        int count = mLineChartAC.getData().getDataSetCount();
                        if (count > position) {
                            if (!item.isSelect()) {
                                MaxUtil.clearDataSetByIndex(mLineChartAC, position);
                            } else {
                                MaxUtil.replaceDataSet(mLineChartAC, dataListAC, position);
                            }
                            MaxUtil.setMaxY(mLineChartAC, true);
                            mLineChartAC.invalidate();
                        }
                    }
                    break;
            }
        } else if (adapter == mAdapterTHDV) {
//            switch (view.getId()){
//                case R.id.llTotal:
//                    MaxCheckOneKeyTHDVBean item = mAdapterTHDV.getItem(position);
//                    item.setSelect(!item.isSelect());
//                    mAdapterTHDV.notifyDataSetChanged();
//                    if (mBarChartTHDV != null && mBarChartTHDV.getData() != null) {
//                        int count = mBarChartTHDV.getData().getDataSetCount();
//                        if (count > position) {
//                            BarDataSet dataSet = (BarDataSet) mBarChartTHDV.getData().getDataSets().get(position);
//                            if (!item.isSelect()) {
//                                dataSet.setColor(Color.TRANSPARENT);
//                            } else {
//                                dataSet.setColor(ContextCompat.getColor(mContext, colorsAC[position]));
//                            }
//                            mBarChartTHDV.invalidate();
//                        }
//                    }
//                    break;
//            }
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (adapter == mAdapterIV) {
            adapterIv(position);
        }
    }

    public void hideIVLast() {
        adapterIv(6);
        adapterIv(7);
        if (entryListIv != null) {
            getValuesByEntryIV(new Entry(1, 0));
            entryListIv = null;
        }
    }

    private void adapterIv(int position) {
        try {
            MaxCheckIVBean item = mAdapterIV.getItem(position);
            item.setSelect(!item.isSelect());
            if (!item.isSelect()) {
                item.setImgColorId(R.color.max_main_text_content);
            }else{
                item.setImgColorId(colors[position]);
            }

            mAdapterIV.notifyDataSetChanged();
            if (mLineChartIV != null && mLineChartIV.getData() != null) {
                int count = mLineChartIV.getData().getDataSetCount();
                if (count > position) {
                    LineDataSet dataSet = (LineDataSet) mLineChartIV.getData().getDataSets().get(position);
                    if (!item.isSelect()) {
                        MaxUtil.clearDataSetByIndex(mLineChartIV, position);
                    } else {
                        if (mIPType == 1 && newLists != null && newLists.size() > 0) {
                            MaxUtil.replaceDataSet(mLineChartIV, newLists, position);
                        } else {
                            MaxUtil.replaceDataSet(mLineChartIV, dataList, position);
                        }
                    }
                    mLineChartIV.invalidate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        if (group == mRadioGroup) {
            String xUnit = "";
            final String yUnit;
            switch (checkedId) {
                case R.id.rBtn1:
                    xUnit = "V";
                    yUnit = "A";
                    mIPType = 0;
                    tvTitle1.setText("MPPT(Voc , Isc)");
                    tvTitle2.setText("(Vpv , Ipv)");
                    tvTileIV.setText("I-V");
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
                    tvTileIV.setText("P-V");
                    tvXUnit.setText("V");
                    tvYUnit.setText("W");
                    break;
            }
            StringBuilder sb = new StringBuilder();
            if (mListIV != null) {
                for (int i = 0, size = mListIV.size(); i < size; i++) {
                    MaxCheckIVBean maxCheckIVBean = mListIV.get(i);
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
                mAdapterIV.notifyDataSetChanged();
            }
            //???????????????
            YAxis axisLeft = mLineChartIV.getAxisLeft();


            axisLeft.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return (float) (Math.round(value * 10)) / 10 + "";
                }
            });

            //???????????????
            setIPVData(mIPType);
            if (sb.length() > 0) {
                String[] split = String.valueOf(sb).split("_");
                for (String pos : split) {
                    int index = Integer.parseInt(pos);
                    adapterIv(index);
                }
            }
            getValuesByEntryIV(new Entry(1, 0));
        }
    }

    /**
     * ??????????????????????????????
     *
     * @param pos?????????????????????
     * @param nowTime???????????????????????????
     * @return
     */
    public int countProgressTime(int pos, int nowTime) {
        int time = 0;
        switch (pos) {
            case 3:
                break;
            case 0:
                if (mContentSelect.contains("3")) {
                    time = time + totalTime3;
                }
                break;
            case 2:
                if (mContentSelect.contains("3")) {
                    time = time + totalTime3;
                }
                if (mContentSelect.contains("0")) {
                    time = time + totalTime0;
                }
                break;
            case 1:
                if (mContentSelect.contains("3")) {
                    time = time + totalTime3;
                }
                if (mContentSelect.contains("0")) {
                    time = time + totalTime0;
                }
                if (mContentSelect.contains("2")) {
                    time = time + totalTime2;
                }
                break;
            case 4:
                if (mContentSelect.contains("3")) {
                    time = time + totalTime3;
                }
                if (mContentSelect.contains("0")) {
                    time = time + totalTime0;
                }
                if (mContentSelect.contains("2")) {
                    time = time + totalTime2;
                }
                if (mContentSelect.contains("1")) {
                    time = time + totalTime1;
                }
                break;

        }
        time = time + nowTime;
        if (time >= totalTime) {
            //????????????????????????
            SharedPreferencesUnit.getInstance(mContext).put(GlobalConstant.MAX_ONEKEY_LAST_TIME, CommenUtils.getFormatDate(null, null));
            mTvLastTime.setText(String.format("%s%s", readStr3, SharedPreferencesUnit.getInstance(mContext).get(GlobalConstant.MAX_ONEKEY_LAST_TIME)));
        }
        return time;
    }

    @OnClick({R.id.tvTitle, R.id.tvStart})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvTitle:
            case R.id.tvStart:
                //??????????????????
                nowContent = mContentSelect;
                if (!isReading) {
                    if (dataList != null) {
                        initDataIV();
                    }
                    if (dataListAC != null) {
                        initDataAC();
                    }
                    if (dataListTHDV != null) {
                        initDataTHDV();
                    }
                    isReading = true;
                    totalTime();
                    startReal();
                } else {
                    reStartBtn();
                }
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            v.getParent().requestDisallowInterceptTouchEvent(false);
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.i("touch", "--ACTION_DOWN-");
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
//            v.getParent().requestDisallowInterceptTouchEvent(true);
        }
        return false;
    }

    @Override
    public boolean onLongClick(View v) {
        v.getParent().requestDisallowInterceptTouchEvent(true);
        return false;
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
