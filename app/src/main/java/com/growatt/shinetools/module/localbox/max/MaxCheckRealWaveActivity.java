package com.growatt.shinetools.module.localbox.max;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.growatt.shinetools.R;
import com.growatt.shinetools.adapter.MaxCheckErrAdapter;
import com.growatt.shinetools.base.DemoBase;
import com.growatt.shinetools.module.localbox.max.bean.MaxCheckErrBean;
import com.growatt.shinetools.constant.GlobalConstant;
import com.growatt.shinetools.db.SqliteUtil;
import com.growatt.shinetools.listeners.OnEmptyListener;
import com.growatt.shinetools.modbusbox.Arith;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.modbusbox.bean.MaxCheckErrorTotalBean;
import com.growatt.shinetools.utils.BtnDelayUtil;
import com.growatt.shinetools.utils.ChartUtils;
import com.growatt.shinetools.utils.CircleDialogUtils;
import com.growatt.shinetools.utils.CommenUtils;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.MyControl;
import com.growatt.shinetools.utils.Mydialog;
import com.growatt.shinetools.utils.Position;
import com.growatt.shinetools.utils.SharedPreferencesUnit;
import com.mylhyl.circledialog.callback.ConfigInput;
import com.mylhyl.circledialog.params.InputParams;
import com.mylhyl.circledialog.view.listener.OnInputClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.growatt.shinetools.modbusbox.SocketClientUtil.SOCKET_10_READ;
import static com.growatt.shinetools.modbusbox.SocketClientUtil.SOCKET_SERVER_SET;
import static com.growatt.shinetools.utils.BtnDelayUtil.TIMEOUT_RECEIVE;
import static com.growatt.shinetools.utils.BtnDelayUtil.refreshFinish;

public class MaxCheckRealWaveActivity extends DemoBase implements BaseQuickAdapter.OnItemChildClickListener, View.OnClickListener {

    private String readStr1;
    private String readStr2;
    private String errorNum = "?????????ID???";
    private String strNoteID = "??????????????????ID";
    private String strNote1 = "????????????????????????";
    private String strNote2 = "???????????????";
    private String strNote3 = "???????????????";
    private String readStr3 = "?????????????????????:";
    @BindView(R.id.lineChart)
    LineChart mLineChart;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.readView)
    View mReadView;
    @BindView(R.id.tvStart)
    TextView mTvStart;
    @BindView(R.id.tvLastTime)
    TextView mTvLastTime;
    @BindView(R.id.tvValueX)
    TextView tvValueX;
    @BindView(R.id.headerView)
    View headerView;
    private String mTitle;
    /**
     * ????????????
     */
    private int mWidth = 100;
    private FrameLayout.LayoutParams mViewParams;
    /**
     * ??????
     */
    private List<MaxCheckErrBean> mList;
    private MaxCheckErrAdapter mAdapter;
    int[] colors = {R.color.max_iv_graph_linechart1, R.color.max_iv_graph_linechart2, R.color.max_iv_graph_linechart3, R.color.max_iv_graph_linechart4};
    int[] colors_a = {R.color.max_iv_graph_linechart1, R.color.max_iv_graph_linechart2, R.color.max_iv_graph_linechart3, R.color.max_iv_graph_linechart4};
    //????????????
    /**
     * ?????????????????????
     */
    private int waitTime = 25000;
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
    private int totalTime = waitTime + readTimeOne * 5;
    /**
     * ??????????????????
     */
    private boolean isReading;

    /**
     * ?????????????????????,?????????,?????????
     */
    private boolean isFirst = true;
    //???????????????
    private List<ArrayList<Entry>> dataList;
    private List<ArrayList<Entry>> newDataList;

    /**
     * ????????????????????????
     */
    private MaxCheckErrorTotalBean mTotalBean;
    private final int TIME_COUNT = 2;//?????????????????? + 1
    private int nowTimeCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_max_check_real_wave);
        ButterKnife.bind(this);
        initString();
        initIntent();
        initView();
        initHeaderView();
        initRecyclerView();
        initLineChart();
        initData();
        initLastData();
        setLineChart();
        initListener();
    }

    private void initString() {
        readStr1 = getString(R.string.m460??????);
        readStr2 = getString(R.string.m461???????????????);
        errorNum = getString(R.string.m475?????????ID???);
        strNoteID = getString(R.string.m476??????????????????ID???);
        strNote1 = getString(R.string.m363????????????);
        strNote2 = getString(R.string.m293??????);
        strNote3 = getString(R.string.m473??????????????????????????????);
        readStr3 = String.format("%s:", getString(R.string.m267?????????????????????));
    }

    private void initLastData() {
        entryList = MaxUtil.getEntryList(SqliteUtil.getListJson(GlobalConstant.MAX_REAL_LAST_DATA));
        if (entryList != null) {
            dataList = entryList;
        }
        //?????????
        String json = SharedPreferencesUnit.getInstance(mContext).get(GlobalConstant.MAX_REAL_ID_LAST_DATA, null);
        if (!TextUtils.isEmpty(json)) {
            try {
                List<MaxCheckErrBean> newList = new Gson().fromJson(json, new TypeToken<List<MaxCheckErrBean>>() {
                }.getType());
                if (newList != null && mAdapter != null) {
//                    for (MaxCheckErrBean bean : newList) {
//                        bean.setSelect(true);
//                    }
                    for (int i = 0; i < newList.size(); i++) {
                        MaxCheckErrBean bean = newList.get(i);
                        bean.setSelect(true);
                        bean.setImgColorId(colors[i]);
                    }
                    mAdapter.replaceData(newList);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ???????????????
     */
    private void initLineChart() {
        ChartUtils.initLineChart(mContext, mLineChart, 0, "", true, R.color.grid_bg_white, true, R.color.grid_bg_white, true, R.color.note_bg_white, R.color.grid_bg_white, R.color.grid_bg_white, R.color.highLightColor, false, R.string.m4, R.string.m5, new OnEmptyListener() {
            @Override
            public void onEmpty(Entry e, Highlight highlight) {
//                LogUtil.i("x??????" + e.getX());
                getValuesByEntry(e);
            }
        });
        Description description = new Description();
        description.setText("X");
        description.setTextColor(ContextCompat.getColor(this, R.color.grid_bg_white));
        mLineChart.setDescription(description);
//        YAxis axisLeft = mLineChart.getAxisLeft();
//        LimitLine ll = new LimitLine(0, "X");
//        ll.setLineColor(ContextCompat.getColor(mContext,R.color.grid_bg_white));
//        ll.setTextColor(ContextCompat.getColor(mContext,R.color.grid_bg_white));
//        axisLeft.addLimitLine(ll);

    }

    private void getValuesByEntry(Entry e) {
        LineData lineData = mLineChart.getData();
        List<ILineDataSet> dataSets = lineData.getDataSets();
        if (dataSets != null) {
            for (int i = 0; i < dataSets.size(); i++) {
                float xPos = e.getX();
                //?????????????????????
                LineDataSet lineDataSet = (LineDataSet) dataSets.get(i);
                //??????????????????
                List<Entry> entries = lineDataSet.getValues();
                //??????????????????
                MaxCheckErrBean maxCheckIVBean = mList.get(i);
                //???????????????
                maxCheckIVBean.setxValue(String.valueOf((int) xPos));
                //????????????x???
                tvValueX.setText(String.format("X=%s", maxCheckIVBean.getxValue()));
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
                maxCheckIVBean.setyValue(String.valueOf((int) yPos));
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * ????????????
     */
    public void setLineChart() {
        MaxUtil.setLineChartDataSpe(mContext, mLineChart, dataList, colors, colors_a, dataList.size(), R.color.highLightColor, true);
        if (entryList != null) {
            getValuesByEntry(new Entry(1, 0));
            entryList = null;
        }
    }

    /**
     * ?????????????????????
     */
    private List<ArrayList<Entry>> entryList;

    public void initData() {
        dataList = new ArrayList<>();
        for (int i = 0; i < colors.length; i++) {
            ArrayList<Entry> list = new ArrayList<>();
            dataList.add(list);
        }


        mTotalBean = new MaxCheckErrorTotalBean();
        mTotalBean.setDataList(dataList);
        //??????????????????
        List<Double> newMuil = new ArrayList<>();
        int count = mAdapter.getItemCount() - mAdapter.getHeaderLayoutCount();
        for (int i = 0; i < count; i++) {
            double mult = mAdapter.getItem(i).getMultiple();
            newMuil.add(mult);
        }
        mTotalBean.setMults(newMuil);
    }

    private void initListener() {
        mAdapter.setOnItemChildClickListener(this);
    }

    private void initIntent() {
        Intent mIntent = getIntent();
        if (mIntent != null) {
            mTitle = mIntent.getStringExtra("title");
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
        setHeaderTitle(headerView, mTitle);

//        if (getLanguage() == 0) {
//            setHeaderTvTitle(headerView, getString(R.string.m??????), new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (Build.VERSION.SDK_INT >= 23) {
//                        if (EasyPermissions.hasPermissions(MaxCheckRealWaveActivity.this, PermissionCodeUtil.PERMISSION_EXTERNAL_STORAGE)) {
//                            share();
//                        } else {
//                            EasyPermissions.requestPermissions(MaxCheckRealWaveActivity.this, String.format(getString(R.string.m???????????????????????????), getString(R.string.m??????)), PermissionCodeUtil.PERMISSION_EXTERNAL_STORAGE_CODE, PermissionCodeUtil.PERMISSION_EXTERNAL_STORAGE);
//                        }
//                    } else {
//                        share();
//                    }
//                }
//            });
//        }
    }


//    private void share() {
//        String picName = "MaxCheckError.jpg";
//        new CircleDialog.Builder(MaxCheckRealWaveActivity.this)
//                .setTitle(getResources().getString(R.string.????????????))
//                .setText("?????????????????????")
//                .setNegative(getResources().getString(R.string.all_no), new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        ShareUtil.shareImage(MaxCheckRealWaveActivity.this, picName, mTitle, shareListener, false);
//                    }
//                })
//                .setPositive(getResources().getString(R.string.all_ok), new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        ShareUtil.shareImage(MaxCheckRealWaveActivity.this, picName, mTitle, shareListener, true);
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

    private void initView() {
        mWidth = CommenUtils.getScreenWidth(mContext);
        mViewParams = (FrameLayout.LayoutParams) mReadView.getLayoutParams();
        mTvLastTime.setText(String.format("%s%s", readStr3, SharedPreferencesUnit.getInstance(mContext).get(GlobalConstant.MAX_REAL_LAST_TIME, "")));
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        View header = LayoutInflater.from(mContext).inflate(R.layout.header_max_check_error_act, mRecyclerView, false);
        mList = new ArrayList<>();
        mAdapter = new MaxCheckErrAdapter(R.layout.item_max_check_real_wave_act, mList);
        mAdapter.addHeaderView(header);
        mRecyclerView.setAdapter(mAdapter);
        initRecyclerData(null);
    }

    public void initRecyclerData(List<MaxCheckErrBean> list) {
        if (list == null || list.size() == 0) {
            List<MaxCheckErrBean> newList = new ArrayList<>();
            for (int i = 0; i < colors.length; i++) {
                MaxCheckErrBean bean = new MaxCheckErrBean();
                bean.setImgColorId(colors[i]);
                bean.setTitle("ID:");
//                bean.setErrId(1001+i);
//                bean.setMultiple(i+1);
                bean.setMultiple(1);
                bean.setPreMult(bean.getMultiple());
                newList.add(bean);
            }
            mAdapter.replaceData(newList);
        } else {
            mAdapter.replaceData(list);
        }
    }

    @OnClick({R.id.tvStart})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvStart:
                //?????????????????????????????????
                boolean isEmpty = false;
                value = new int[5];
                value[4] = 1;
                for (int i = 0, size = mAdapter.getItemCount() - mAdapter.getHeaderLayoutCount(); i < size; i++) {
                    MaxCheckErrBean item = mAdapter.getItem(i);
                    if (item.getErrId() == -1) {
                        isEmpty = true;
                        break;
                    } else {
                        value[i] = item.getErrId();
                    }
                }
                if (isEmpty) {
                    toast(errorNum);
                } else {
                    nowTime = 0;
                    posTime = nowTime;
                    nextTime = readTimeOne;
                    initData();
                    if (!isReading) {
                        isReading = true;
                        if (isFirst) {
                            totalTime = waitTime + readTimeOne * (funs.length + 1);
                            writeRegisterValue();
                        } else {
                            totalTime = readTimeOne * funs.length;
                            connectServer();
                        }
                        mHandlerWrite.sendEmptyMessage(200);
                    } else {
                        reStartBtn();
                    }
                }
                break;
        }
    }

    /**
     * ??????????????????
     */
    public void reStartBtn() {
        isReading = false;
        count = 0;
        mHandlerWrite.removeMessages(200);
        mHandlerWrite.removeMessages(SOCKET_10_READ);
        mHandler.removeMessages(SocketClientUtil.SOCKET_SEND);
        SocketClientUtil.close(mClientUtilWriter);
        SocketClientUtil.close(mClientUtil);
        mViewParams.width = 0;
        mReadView.setLayoutParams(mViewParams);
        mTvStart.setText(readStr1);
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
    private int[] nowSet = {0x10, 0x0104, 0x0108};
    private int[] value;
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
                        sendBytes = SocketClientUtil.sendMsgToServer10(mClientUtilWriter, nowSet, value);
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
                            noticeUpdateTime(-1, true);
                        } else {
                            //??????????????????
                            noticeUpdateTime(-1, false);
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
                    noticeUpdateTime(0, true);
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
                    int width = (nowTime + posTime) * mWidth / totalTime;
                    mTvStart.setText(String.format("%s%d%%", readStr2, (nowTime + posTime) * 100 / totalTime));
                    if (nowTime == totalTime) {
                        width = 0;
                        mTvStart.setText(readStr1);
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
                        MyControl.showJumpWifiSet(MaxCheckRealWaveActivity.this);
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
            default:
                nowTime = nextTime;
                nextTime = nowTime + readTimeOne;
                break;
        }
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
     * ???????????????handle
     */
    private int count = 0;
    int[][] funs = {
            {0x14, 0x0dac, 0x0dac + 0x7d - 1}, {0x14, 0x0e29, 0x0e29 + 0x7d - 1}, {0x14, 0x0ea6, 0x0ea6 + 0x7d - 1}, {0x14, 0x0f23, 0x0f23 + 0x7d - 1}, {0x14, 0x0fa0, 0x0fa0 + 0x7d - 1},
            {0x14, 0x101d, 0x101d + 0x7d - 1}, {0x14, 0x109a, 0x109a + 0x7d - 1}, {0x14, 0x1117, 0x1117 + 0x7d - 1}, {0x14, 0x1194, 0x1194 + 0x7d - 1}, {0x14, 0x1211, 0x1211 + 0x7d - 1},
            {0x14, 0x128e, 0x128e + 0x7d - 1}, {0x14, 0x130b, 0x130b + 0x7d - 1}, {0x14, 0x1388, 0x1388 + 0x7d - 1}, {0x14, 0x1405, 0x1405 + 0x7d - 1}, {0x14, 0x1482, 0x1482 + 0x7d - 1},
            {0x14, 0x14ff, 0x14ff + 0x7d - 1}, {0x14, 0x157c, 0x157c + 0x7d - 1}, {0x14, 0x15f9, 0x15f9 + 0x7d - 1}, {0x14, 0x1676, 0x1676 + 0x7d - 1}, {0x14, 0x16f3, 0x16f3 + 0x7d - 1}
    };
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
                            RegisterParseUtil.parseMaxErr14(mTotalBean, bytes, count);
                            //??????ui
                            updateUi();
                        }
                        noticeUpdateTime(count + 1, true);
                        if (count < funs.length - 1) {
                            count++;
                            mHandler.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        } else {
                            //????????????
                            SqliteUtil.setListJson(GlobalConstant.MAX_REAL_LAST_DATA, MaxUtil.saveList(dataList));
                            //??????id
                            if (mList != null) {
                                SharedPreferencesUnit.getInstance(mContext).put(GlobalConstant.MAX_REAL_ID_LAST_DATA, new Gson().toJson(mList));
                            }
                            //??????ui
//                            refreshUI();
                            count = 0;
                            //????????????
                            SocketClientUtil.close(mClientUtil);
                            refreshFinish();
                            //??????????????????
                            SharedPreferencesUnit.getInstance(mContext).put(GlobalConstant.MAX_REAL_LAST_TIME, ChartUtils.getFormatDate(null, null));
                            mTvLastTime.setText(String.format("%s%s", readStr3, SharedPreferencesUnit.getInstance(mContext).get(GlobalConstant.MAX_REAL_LAST_TIME)));
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
                        MyControl.showJumpWifiSet(MaxCheckRealWaveActivity.this);
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
     * ????????????UI
     */
    private void updateUi() {
        if (mTotalBean != null) {
            MaxUtil.setLineChartDataSpe(mContext, mLineChart, mTotalBean.getDataList(), colors, colors_a, dataList.size(), R.color.highLightColor, true);
            //??????
//            if (mTotalBean.getTimes() != null && mTotalBean.getTimes().size() > 0) {
//                mTvTime.setText(mTotalBean.getTimes().get(0));
//            }
            //?????????
//            if (mTotalBean.getErrCodes() != null && mTotalBean.getErrCodes().size() > 0) {
//                mTvErrCode.setText(String.valueOf(mTotalBean.getErrCodes().get(0)));
//            }
            //adapter
//            List<Integer> ids = mTotalBean.getIds();
//            if (ids != null){
//                int itemCount = mAdapter.getItemCount()-mAdapter.getHeaderLayoutCount();
//                int idSize = ids.size();
//                for (int i=0;i<itemCount;i++){
//                    MaxCheckErrBean item = mAdapter.getItem(i);
//                    if (i < idSize){
//                        item.setErrId(ids.get(i));
//                    }else {
//                        item.setErrId(-1);
//                    }
//                }
//                mAdapter.notifyDataSetChanged();
//            }
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

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, final int position) {
        if (adapter == mAdapter) {
            switch (view.getId()) {
                case R.id.tvID:
                    CircleDialogUtils.showCommentInputDialog(this, getString(R.string.action_settings), "", "", true, new OnInputClickListener() {
                        @Override
                        public boolean onClick(String text, View v) {
                            LogUtil.i("v?????????" + v);
                            try {
                                int id = Integer.parseInt(text);
                                if (id <= 0) {
                                    toast(strNoteID);
                                    return true;
                                }
                                MaxCheckErrBean item1 = mAdapter.getItem(position);
                                item1.setErrId(id);
                                mAdapter.notifyDataSetChanged();
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                toast(strNoteID);
                            }
                            return true;
                        }
                    });
//                    InputMethodManager imm = (InputMethodManager) dialog.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
//                    InputMethodManager imm = ( InputMethodManager ) dialog.getContext( ).getSystemService( Context.INPUT_METHOD_SERVICE );
//
//                    imm.showSoftInput(dialog,InputMethodManager.SHOW_FORCED);

//                    dialog.setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
//                    dialog.getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    break;
                case R.id.llSelect:
                    final MaxCheckErrBean item = mAdapter.getItem(position);
                    item.setSelect(!item.isSelect());
                    mAdapter.notifyDataSetChanged();
                    if (mLineChart != null && mLineChart.getData() != null) {
                        int counts = mLineChart.getData().getDataSetCount();
                        if (counts > position) {
                            if (!item.isSelect()) {
                                MaxUtil.clearDataSetByIndex(mLineChart, position);
                            } else {
                                MaxUtil.replaceDataSet(mLineChart, dataList, position);
                            }
                            mLineChart.getData().notifyDataChanged();
                            mLineChart.notifyDataSetChanged();
                            MaxUtil.setMaxY(mLineChart, true);
                            mLineChart.invalidate();
                        }
                    }
                    break;
                case R.id.llErrMult:
                    CircleDialogUtils.showCustomInputDialog(MaxCheckRealWaveActivity.this, strNote2, strNote3,
                            "", "", false, Gravity.CENTER, getString(R.string.all_ok), new OnInputClickListener() {

                                @Override
                                public boolean onClick(String text, View v) {
                                    try {
                                        double nowMult = Double.parseDouble(text);
                                        if (nowMult <= 0) {
                                            toast(strNote1);
                                            return true;
                                        }
                                        MaxCheckErrBean item1 = mAdapter.getItem(position);
                                        item1.setPreMult(item1.getMultiple());
                                        item1.setMultiple(nowMult);
                                        item1.setSelect(true);
                                        mAdapter.notifyDataSetChanged();
                                        //????????????
                                        updateLineChart(position);
                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();
                                        toast(strNote1);
                                    }
                                    return true;
                                }
                            }, getString(R.string.all_no), new ConfigInput(){

                                @Override
                                public void onConfig(InputParams params) {
                                    params.padding = new int[]{5, 5, 5, 5};
                                    params.strokeColor = ContextCompat.getColor(MaxCheckRealWaveActivity.this, R.color.title_bg_white);
                                }
                            },null);
//                    .getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
//                        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
//                    new CircleDialog.Builder(MaxCheckRealWaveActivity.this).show()
//                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    break;
            }
        }
    }

    /**
     * ?????????????????????linechart??????
     *
     * @param pos ??????item??????
     */
    private void updateLineChart(int pos) {
        MaxCheckErrBean item = mAdapter.getItem(pos);
        LineData lineData = mLineChart.getData();
        if (lineData != null && lineData.getDataSetCount() > pos) {
            LineDataSet dataSet = (LineDataSet) lineData.getDataSetByIndex(pos);
            ArrayList<Entry> entries = dataList.get(pos);
            if (entries != null) {
                for (Entry entry : entries) {
                    float nowY = entry.getY();
                    try {
                        nowY = (float) Arith.div(Arith.mul(nowY, item.getMultiple()), item.getPreMult(), 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    entry.setY(nowY);
                }
                dataSet.setValues(entries);
                lineData.notifyDataChanged();
                mLineChart.notifyDataSetChanged();
                MaxUtil.setMaxY(mLineChart, true);
                mLineChart.invalidate();
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
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
