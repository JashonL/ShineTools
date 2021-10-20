package com.growatt.shinetools.module.localbox.max;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.growatt.shinetools.R;
import com.growatt.shinetools.adapter.MaxCheckIV1500Adapter;
import com.growatt.shinetools.adapter.MaxCheckIVAdapter;
import com.growatt.shinetools.base.DemoBase;
import com.growatt.shinetools.constant.GlobalConstant;
import com.growatt.shinetools.db.SqliteUtil;
import com.growatt.shinetools.listeners.OnEmptyListener;
import com.growatt.shinetools.modbusbox.Arith;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.module.localbox.max.bean.MaxCheckIVBean;
import com.growatt.shinetools.utils.BtnDelayUtil;
import com.growatt.shinetools.utils.ChartUtils;
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
import static com.growatt.shinetools.utils.BtnDelayUtil.refreshFinish;


public class MaxCheckIV1500VActivity extends DemoBase implements RadioGroup.OnCheckedChangeListener {
    private String readStr1;
    private String readStr2;
    private String readStr3;
    @BindView(R.id.headerView)
    View headerView;
    @BindView(R.id.radioGroup)
    RadioGroup mRadioGroup;
    @BindView(R.id.lineChart)
    LineChart mLineChart;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.rBtn1)
    RadioButton mRBtn1;
    @BindView(R.id.rBtn2)
    RadioButton mRBtn2;
    @BindView(R.id.tvStart)
    TextView mTvStart;
    @BindView(R.id.tvLastTime)
    TextView mTvLastTime;
    @BindView(R.id.readView)
    View mReadView;
    @BindView(R.id.tvYUnit)
    TextView tvYUnit;
    @BindView(R.id.tvXUnit)
    TextView tvXUnit;


    private LinearLayoutManager mLayoutManager;
    private MaxCheckIVAdapter mAdapter;
    private List<MaxCheckIVBean> mList;
    private String mTitle;
    //曲线数据集
    private List<ArrayList<Entry>> dataList;
    private List<ArrayList<Entry>> newDataList;
//    int[] colors = {R.color.max_iv_graph_linechart1, R.color.max_iv_graph_linechart2, R.color.max_iv_graph_linechart3, R.color.max_iv_graph_linechart4,
//            R.color.max_iv_graph_linechart5, R.color.max_iv_graph_linechart6, R.color.max_iv_graph_linechart7, R.color.max_iv_graph_linechart8};
//    int[] colors_a = {R.color.max_iv_graph_linechart1, R.color.max_iv_graph_linechart2, R.color.max_iv_graph_linechart3, R.color.max_iv_graph_linechart4,
//            R.color.max_iv_graph_linechart5, R.color.max_iv_graph_linechart6, R.color.max_iv_graph_linechart7, R.color.max_iv_graph_linechart8};
    String[] colors = {
        "#60acfc","#27a1ea","#39b3ea","#35c5ea","#32d3eb","#4ebecd","#40cec7","#63d5b2"
        ,"#5bc49f","#9cdc82","#d4ec59","#ffda43","#feb64d","#ff9f69","#fa816d","#fb6e6c"
        ,"#ff7b7b","#e9668e","#d660a8","#b55cbd","#9287e7","#747be1","#6370de","#668ed6"
        ,"#ff7b7b","#e9668e","#d660a8","#b55cbd","#9287e7","#747be1","#6370de","#668ed6"
    };
    //时间属性
    /**
     * 设置后等待时间
     */
    private int waitTime = 60000;
    /**
     * 读取一条数据间隔
     */
    private int readTimeOne = 800;
    /**
     * 更新ui间隔
     */
    private int updataUiTime = 100;
    /**
     * 当前时间
     */
    private int nowTime = 0;
    /**
     * 进行到下一步时间
     */
    private int nextTime = 0;
    /**
     * 初始总时间
     */
    private int totalTime = waitTime + readTimeOne * colors.length;
    /**
     * 是否正在读取
     */
    private boolean isReading;

    /**
     * 第一次进入读取,先设置,变化值
     */
    private boolean isFirst = true;
    /**
     * 屏幕宽度
     */
    private int mWidth = 100;
    private FrameLayout.LayoutParams mViewParams;

    /**
     * 0:I-V
     * 1:P-V
     */
    private int mIPType = 0;
    private final int TIME_COUNT = 2;//超时重发次数 + 1
    private int nowTimeCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_max_check_iv1500_v);
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
        readStr1 = getString(R.string.m460开始);
        readStr2 = getString(R.string.m461读取数据中);
        readStr3 = String.format("%s:", getString(R.string.m267最后更新时间为));
        funs = new int[32][3];
        for (int i = 0; i < 32; i++) {
            int[] fun = funs[i];
            fun[0] = 0x14;
            fun[1] =  0x7d * (56 + i);
            fun[2] =  fun[1] + 124;
        }
    }

    private void initLastData() {
        entryList = MaxUtil.getEntryList(SqliteUtil.getListJson(GlobalConstant.MAX_IV_LAST_DATA));
        if (entryList != null) {
            dataList = entryList;
        }
    }

    private void initView() {
        tvXUnit.setText("V");
        tvYUnit.setText("A");
        mWidth = CommenUtils.getScreenWidth(mContext);
        mViewParams = (FrameLayout.LayoutParams) mReadView.getLayoutParams();
        mTvLastTime.setText(String.format("%s%s", readStr3, SharedPreferencesUnit.getInstance(mContext).get(GlobalConstant.MAX_IV_LAST_TIME, "")));
    }

    private void initListener() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                adapterCheck(position);
            }
        });
        mRadioGroup.setOnCheckedChangeListener(this);
    }

    public void initNoSelectData() {
        int count = mLineChart.getData().getDataSetCount();
        for (int i = 0, size = mAdapter.getItemCount() - mAdapter.getHeaderLayoutCount(); i < size; i++) {
            MaxCheckIVBean item = mAdapter.getItem(i);
            if (!item.isSelect() && count > i) {
                MaxUtil.clearDataSetByIndex(mLineChart, i);
            }
        }
    }

    private void adapterCheck(int position) {
        try {
            MaxCheckIVBean item = mAdapter.getItem(position);
            item.setSelect(!item.isSelect());
            mAdapter.notifyDataSetChanged();

            if (mLineChart != null && mLineChart.getData() != null) {
                int count = mLineChart.getData().getDataSetCount();
                if (count > position) {
                    LineDataSet dataSet = (LineDataSet) mLineChart.getData().getDataSets().get(position);
                    if (!item.isSelect()) {
                        MaxUtil.clearDataSetByIndex(mLineChart, position);
                    } else {
                        if (mIPType == 1 && newLists != null && newLists.size() > 0) {
                            MaxUtil.replaceDataSet(mLineChart, newLists, position);
                        } else {
                            MaxUtil.replaceDataSet(mLineChart, dataList, position);
                        }
                    }
                    //                        YAxis axisLeft = mLineChart.getAxisLeft();
                    //                        axisLeft.resetAxisMaximum();
                    LogUtil.i("MaxUtil.getMaxY:" + MaxUtil.getMaxY(mLineChart) + ";getYMax:" + mLineChart.getYMax() + ";getY:" + mLineChart.getY() + ";getScaleY:" + mLineChart.getScaleY());
                    //                        axisLeft.setAxisMaximum(mLineChart.getYMax() * 1.1f);

                    mLineChart.getData().notifyDataChanged();
                    mLineChart.notifyDataSetChanged();
                    mLineChart.invalidate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private TextView tvTitle1;
    private TextView tvTitle2;
    private TextView tvXValue;

    private void initRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mList = new ArrayList<>();
        mAdapter = new MaxCheckIV1500Adapter(R.layout.item_max_check_iv_act, mList);
        View headerView = LayoutInflater.from(mContext).inflate(R.layout.header_max_check_iv_act, mRecyclerView, false);
        tvXValue = (TextView) headerView.findViewById(R.id.tvXValue);
        tvTitle1 = (TextView) headerView.findViewById(R.id.tvTitle1);
        tvTitle2 = (TextView) headerView.findViewById(R.id.tvTitle2);
        mAdapter.addHeaderView(headerView);
        mRecyclerView.setAdapter(mAdapter);
        initRecyclerData(null);
    }

    public void initRecyclerData(List<MaxCheckIVBean> list) {
        if (list == null || list.size() == 0) {
            List<MaxCheckIVBean> newList = new ArrayList<>();
            for (int i = 0; i < colors.length; i++) {
                MaxCheckIVBean bean = new MaxCheckIVBean();
                bean.setImgColorId(Color.parseColor(colors[i]));
                bean.setTitle(String.valueOf(i + 1));
                newList.add(bean);
            }
            mAdapter.replaceData(newList);
        } else {
            mAdapter.replaceData(list);
        }
    }

    /**
     * 初始化图表
     */
    private void initLineChart() {
        ChartUtils.initLineChart(mContext, mLineChart, 0, "", true, R.color.grid_bg_white, true, R.color.grid_bg_white, true, R.color.note_bg_white, R.color.grid_bg_white, R.color.grid_bg_white, R.color.highLightColor, false, R.string.m4, R.string.m5, new OnEmptyListener() {
            @Override
            public void onEmpty(Entry e, Highlight highlight) {
                LogUtil.i("x位置" + e.getX());
                getValuesByEntry(e);
            }
        });
        mLineChart.getAxisLeft().setAxisMinimum(0f);
        Description description = new Description();
        description.setText("V(V)");
        description.setTextColor(ContextCompat.getColor(this, R.color.grid_bg_white));
        mLineChart.setDescription(description);
    }

    private void getValuesByEntry(Entry e) {
        LineData lineData = mLineChart.getData();
        List<ILineDataSet> dataSets = lineData.getDataSets();
        if (dataSets != null) {
            for (int i = 0; i < dataSets.size(); i++) {
                int xPos = (int) e.getX();
                //单条曲线数据集
                LineDataSet lineDataSet = (LineDataSet) dataSets.get(i);
                //单条曲线数据
                List<Entry> entries = lineDataSet.getValues();
                //单条曲线实体
                MaxCheckIVBean maxCheckIVBean = mList.get(i);
                //当前位置值
                maxCheckIVBean.setxValue(String.valueOf(xPos));
                //设置X坐标
//                tvXValue.setText(String.format("x=%s",maxCheckIVBean.getxValue()));
                //最大x值
                float xMax = lineDataSet.getXMax();
                //当前y值
                float yPos = 0;
                if (xPos > xMax) {
                    yPos = 0;
                } else {
                    List<Entry> posEntry = lineDataSet.getEntriesForXValue(xPos);
                    if (posEntry != null && posEntry.size() > 0) {
                        yPos = posEntry.get(0).getY();
                    } else {
                        //没有对应点集合，用线性xy处理
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
                //最大值y值
                setMax(lineDataSet, entries, maxCheckIVBean);
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置图表
     */
    public void setLineChart() {
        MaxUtil.setLineChartDataSpeColor(mContext, mLineChart, dataList, colors, colors, dataList.size(), R.color.highLightColor,false);
        hideLastData();
    }

    /**
     * 初始化数据集合
     */
    private List<ArrayList<Entry>> entryList;

    public void initData() {
        dataList = new ArrayList<>();
        for (int i = 0; i < colors.length; i++) {
            ArrayList<Entry> list = new ArrayList<>();
            dataList.add(list);
        }
    }

    /**
     * 初始化数据集合
     */
    public void initData(List<ArrayList<Entry>> lists) {
        lists = new ArrayList<>();
        for (int i = 0; i < colors.length; i++) {
            ArrayList<Entry> list = new ArrayList<>();
            lists.add(list);
        }
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

    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            reStartBtn();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        if (group == mRadioGroup) {
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
            //设置曲线图
            YAxis axisLeft = mLineChart.getAxisLeft();
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

    /**
     * 切换I-V/P-V曲线
     *
     * @param type：类型：
     * 0：I-V
     * 1：P-V
     */
    private List<ArrayList<Entry>> newLists;

    public void setIPVData(int type) {
        switch (type) {
            case 0://I-V
                MaxUtil.setLineChartDataSpeColor(mContext, mLineChart, dataList, colors, colors, dataList.size(), R.color.highLightColor,false);
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
                MaxUtil.setLineChartDataSpeColor(mContext, mLineChart, newLists, colors, colors, newLists.size(), R.color.highLightColor,false);
                break;
        }
    }

    @OnClick(R.id.tvStart)
    public void onViewClicked() {
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

    private void reStartBtn() {
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

    //连接对象:用于设置数据
    private SocketClientUtil mClientUtilWriter;

    //设置寄存器的值
    private void writeRegisterValue() {
        mClientUtilWriter = SocketClientUtil.connectServer(mHandlerWrite);
    }

    /**
     * 写寄存器handle
     */
    //当前发送的字节数组
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
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessageWrite(this);
                    if (nowSet != null && nowSet[2] != -1) {
                        sendBytes = SocketClientUtil.sendMsgToServer10(mClientUtilWriter, nowSet, values);
                        LogUtil.i("发送写入：" + SocketClientUtil.bytesToHexString(sendBytes));
                    } else {
                        toast("系统错误，请重启应用");
                    }
                    break;
                //接收字节数组
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    nowTimeCount = 0;
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //检测内容正确性
                        boolean isFlag = MaxUtil.isCheckFull10(mContext, bytes);
                        if (isFlag) {
                            //读取数据
                            this.sendEmptyMessageDelayed(SOCKET_10_READ, waitTime);
                            //更新时间
                            noticeUpdateTime(-1, true);
                        } else {
                            //通知读取失败
                            noticeUpdateTime(-1, false);
                            reStartBtn();
                        }
                        LogUtil.i("接收写入：" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //关闭tcp连接
                        SocketClientUtil.close(mClientUtilWriter);
                        refreshFinish();
                    }
                    break;
                case SOCKET_10_READ:
                    noticeUpdateTime(0, true);
                    connectServer();
                    break;
                case 200://动态更新
                    //时间差
                    int time = nextTime - nowTime;
                    if (posTime < time) {
                        posTime = posTime + time / updataUiTime;
                    } else {
                        posTime = time;
                    }
                    //当前进度
                    int width = (nowTime + posTime) * mWidth / totalTime;
                    mTvStart.setText(String.format("%s%d%%", readStr2, (nowTime + posTime) * 100 / totalTime));
                    if (nowTime == totalTime) {
                        width = 0;
                        mTvStart.setText(readStr1);
                    } else {
                        //时间差
                        this.sendEmptyMessageDelayed(200, time / updataUiTime);
                    }
                    mViewParams.width = width;
                    mReadView.setLayoutParams(mViewParams);
                    break;
                case TIMEOUT_RECEIVE://接收超时
                    if (nowTimeCount < TIME_COUNT){
                        LogUtil.e("重连nowTimecount:" + nowTimeCount);
                        BtnDelayUtil.receiveMessage(this);
                        //重新发送命令
                        this.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        nowTimeCount++;
                        break;
                    }
                    nowTimeCount = 0;
                case SOCKET_SERVER_SET://跳转到wifi列表
                    try {
                        MyControl.showJumpWifiSet(MaxCheckIV1500VActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    reStartBtn();
                    break;
                case SocketClientUtil.SOCKET_CLOSE:
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
     * 全局控制读取进度
     *
     * @param pos:当前位置：-1：设置值；0-6；读取值
     * @param isSuccess：是否成功：
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
            default:
                nowTime = nextTime;
                nextTime = nowTime + readTimeOne;
                break;
        }
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
     * 读取寄存器handle
     */
    private int count;
//    int[][] funs = {
//            {0x14, 0, 0x7d - 1}
//            , {0x14, 0x7d, 0x7d * 2 - 1}
//            , {0x14, 0x7d * 2, 0x7d * 3 - 1}
//            , {0x14, 0x7d * 3, 0x7d * 4 - 1}
//            , {0x14, 0x7d * 4, 0x7d * 5 - 1}
//            , {0x14, 0x7d * 5, 0x7d * 6 - 1}
//            , {0x14, 0x7d * 6, 0x7d * 7 - 1}
//            , {0x14, 0x7d * 7, 0x7d * 8 - 1}
//    };

    byte offset= (byte) 0x1B58;

    int[][] funs = {

            {0x14, offset, 0x7d - 1+offset}, {0x14, 0x7d, 0x7d * 2 - 1+offset}, {0x14, 0x7d * 2, 0x7d * 3 - 1+offset}, {0x14, 0x7d * 3, 0x7d * 4 - 1+offset}, {0x14, 0x7d * 4, 0x7d * 5 - 1+offset}, {0x14, 0x7d * 5, 0x7d * 6 - 1+offset}, {0x14, 0x7d * 6, 0x7d * 7 - 1+offset}, {0x14, 0x7d * 7, 0x7d * 8 - 1+offset},

            {0x14, 0x7d * 8, 0x7d * 9 - 1+offset}, {0x14, 0x7d * 9, 0x7d * 10 - 1+offset}, {0x14, 0x7d * 10, 0x7d * 11 - 1+offset}, {0x14, 0x7d * 11, 0x7d * 12 - 1+offset}, {0x14, 0x7d * 12, 0x7d * 13 - 1+offset}, {0x14, 0x7d * 13, 0x7d * 14 - 1+offset}, {0x14, 0x7d * 14, 0x7d * 15 - 1+offset}, {0x14, 0x7d * 15, 0x7d * 16 - 1+offset},

            {0x14, 0x7d * 16, 0x7d*17 - 1+offset}, {0x14, 0x7d*17, 0x7d * 18 - 1+offset}, {0x14, 0x7d * 18, 0x7d * 19 - 1+offset}, {0x14, 0x7d * 19, 0x7d * 20 - 1+offset}, {0x14, 0x7d * 20, 0x7d * 21 - 1+offset}, {0x14, 0x7d * 21, 0x7d * 22 - 1+offset}, {0x14, 0x7d * 22, 0x7d * 23 - 1+offset}, {0x14, 0x7d * 23, 0x7d * 24 - 1+offset},

            {0x14, 0x7d * 24, 0x7d*25 - 1+offset}, {0x14, 0x7d*25, 0x7d * 26 - 1+offset}, {0x14, 0x7d * 26, 0x7d * 27 - 1+offset}, {0x14, 0x7d * 27, 0x7d * 28 - 1+offset}, {0x14, 0x7d * 28, 0x7d * 29 - 1+offset}, {0x14, 0x7d * 29, 0x7d * 30 - 1+offset}, {0x14, 0x7d * 30, 0x7d * 31 - 1+offset}, {0x14, 0x7d * 31, 0x7d * 32 - 1+offset}


    };


    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    if (count < funs.length) {
                        sendMsg(mClientUtil, funs[count]);
                    }
                    break;
                case SocketClientUtil.SOCKET_SEND_MSG:
                    BtnDelayUtil.sendMessage(this);
                    //记录请求时间
                    //禁用按钮
                    //设置接收消息超时时间和唯一标示
                    Message msgSend = Message.obtain();
                    msgSend.what = 100;
                    mHandler.sendEmptyMessageDelayed(100, 3000);
                    String sendMsg = (String) msg.obj;
                    LogUtil.i("发送消息:" + sendMsg);
                    break;
                //接收字节数组
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    nowTimeCount = 0;
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //检测内容正确性
                        boolean isCheck = ModbusUtil.checkModbus(bytes);
                        if (isCheck) {
                            //接收正确，开始解析
                            RegisterParseUtil.parseMax14(dataList, bytes, count);
                            //刷新ui
                            setIPVData(mIPType);
//                            MaxUtil.setLineChartData(context, mLineChart, dataList, colors, colors_a, dataList.size(), R.color.highLightColor);
                            //更新最大值
                            updateMax(count);
                        }
                        noticeUpdateTime(count + 1, true);
                        if (count < funs.length - 1) {
                            count++;
                            mHandler.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        } else {
                            //保存数据
                            SqliteUtil.setListJson(GlobalConstant.MAX_IV_LAST_DATA, MaxUtil.saveList(dataList));
                            LogUtil.i("最后数据：" + SqliteUtil.getListJson(GlobalConstant.MAX_IV_LAST_DATA));
                            initNoSelectData();
                            //更新ui
//                            refreshUI();
                            count = 0;
                            //关闭连接
                            SocketClientUtil.close(mClientUtil);
                            refreshFinish();
                            //保存更新时间
                            SharedPreferencesUnit.getInstance(mContext).put(GlobalConstant.MAX_IV_LAST_TIME, ChartUtils.getFormatDate(null, null));
                            mTvLastTime.setText(String.format("%s%s", readStr3, SharedPreferencesUnit.getInstance(mContext).get(GlobalConstant.MAX_IV_LAST_TIME)));
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
                case TIMEOUT_RECEIVE://接收超时
                    if (nowTimeCount < TIME_COUNT){
                        LogUtil.e("重连nowTimecount:" + nowTimeCount);
                        BtnDelayUtil.receiveMessage(this);
                        //重新发送命令
                        this.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        nowTimeCount++;
                        break;
                    }
                    nowTimeCount = 0;
                case SOCKET_SERVER_SET://跳转到wifi列表
                    try {
                        MyControl.showJumpWifiSet(MaxCheckIV1500VActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    reStartBtn();
                    break;
                case SocketClientUtil.SOCKET_CLOSE:
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
     * 隐藏后面两组数据
     */
    private void hideLastData() {
//        adapterCheck(6);
//        adapterCheck(7);
        if (entryList != null) {
            getValuesByEntry(new Entry(0, 0));
            entryList = null;
        }
    }

    /**
     * 更新最大值
     */
    private void updateMax(int position) {
        LineData lineData = mLineChart.getData();
        List<ILineDataSet> dataSets = lineData.getDataSets();
        if (dataSets.size() > position) {
            //单条曲线数据集
            LineDataSet lineDataSet = (LineDataSet) dataSets.get(position);
            //单条曲线数据
            List<Entry> entries = lineDataSet.getValues();
            //单条曲线实体
            MaxCheckIVBean maxCheckIVBean = mList.get(position);
            //最大值y值
            setMax(lineDataSet, entries, maxCheckIVBean);
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
     * 解析数据
     *
     * @param bytes
     * @param count
     */
    private void parseMax(byte[] bytes, int count) {
//        switch (count) {
//            case 0:
//                RegisterParseUtil.parseMax1(mMaxData, bytes);
//                break;
//            case 1:
//                RegisterParseUtil.parseMax2(mMaxData, bytes);
//                break;
//            case 2:
//                RegisterParseUtil.parseMax3(mMaxData, bytes);
//                break;
//        }
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
