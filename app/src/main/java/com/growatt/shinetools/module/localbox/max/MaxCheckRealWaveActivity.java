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
    private String errorNum = "请选择ID号";
    private String strNoteID = "请输入正确的ID";
    private String strNote1 = "请输入正确的倍数";
    private String strNote2 = "设置倍数值";
    private String strNote3 = "请输入倍数";
    private String readStr3 = "最后更新时间为:";
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
     * 屏幕宽度
     */
    private int mWidth = 100;
    private FrameLayout.LayoutParams mViewParams;
    /**
     * 列表
     */
    private List<MaxCheckErrBean> mList;
    private MaxCheckErrAdapter mAdapter;
    int[] colors = {R.color.max_iv_graph_linechart1, R.color.max_iv_graph_linechart2, R.color.max_iv_graph_linechart3, R.color.max_iv_graph_linechart4};
    int[] colors_a = {R.color.max_iv_graph_linechart1, R.color.max_iv_graph_linechart2, R.color.max_iv_graph_linechart3, R.color.max_iv_graph_linechart4};
    //时间属性
    /**
     * 设置后等待时间
     */
    private int waitTime = 25000;
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
    private int totalTime = waitTime + readTimeOne * 5;
    /**
     * 是否正在读取
     */
    private boolean isReading;

    /**
     * 第一次进入读取,先设置,变化值
     */
    private boolean isFirst = true;
    //曲线数据集
    private List<ArrayList<Entry>> dataList;
    private List<ArrayList<Entry>> newDataList;

    /**
     * 界面总数据集实体
     */
    private MaxCheckErrorTotalBean mTotalBean;
    private final int TIME_COUNT = 2;//超时重发次数 + 1
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
        readStr1 = getString(R.string.m460开始);
        readStr2 = getString(R.string.m461读取数据中);
        errorNum = getString(R.string.m475请填写ID号);
        strNoteID = getString(R.string.m476请输入正确的ID号);
        strNote1 = getString(R.string.m363设置失败);
        strNote2 = getString(R.string.m293检查);
        strNote3 = getString(R.string.m473请填写曲线显示的倍数);
        readStr3 = String.format("%s:", getString(R.string.m267最后更新时间为));
    }

    private void initLastData() {
        entryList = MaxUtil.getEntryList(SqliteUtil.getListJson(GlobalConstant.MAX_REAL_LAST_DATA));
        if (entryList != null) {
            dataList = entryList;
        }
        //取实体
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
     * 初始化图表
     */
    private void initLineChart() {
        ChartUtils.initLineChart(mContext, mLineChart, 0, "", true, R.color.grid_bg_white, true, R.color.grid_bg_white, true, R.color.note_bg_white, R.color.grid_bg_white, R.color.grid_bg_white, R.color.highLightColor, false, R.string.m4, R.string.m5, new OnEmptyListener() {
            @Override
            public void onEmpty(Entry e, Highlight highlight) {
//                LogUtil.i("x位置" + e.getX());
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
                //单条曲线数据集
                LineDataSet lineDataSet = (LineDataSet) dataSets.get(i);
                //单条曲线数据
                List<Entry> entries = lineDataSet.getValues();
                //单条曲线实体
                MaxCheckErrBean maxCheckIVBean = mList.get(i);
                //当前位置值
                maxCheckIVBean.setxValue(String.valueOf((int) xPos));
                //设置当前x值
                tvValueX.setText(String.format("X=%s", maxCheckIVBean.getxValue()));
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
                maxCheckIVBean.setyValue(String.valueOf((int) yPos));
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置图表
     */
    public void setLineChart() {
        MaxUtil.setLineChartDataSpe(mContext, mLineChart, dataList, colors, colors_a, dataList.size(), R.color.highLightColor, true);
        if (entryList != null) {
            getValuesByEntry(new Entry(1, 0));
            entryList = null;
        }
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


        mTotalBean = new MaxCheckErrorTotalBean();
        mTotalBean.setDataList(dataList);
        //设置当前倍数
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
//            setHeaderTvTitle(headerView, getString(R.string.m分享), new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (Build.VERSION.SDK_INT >= 23) {
//                        if (EasyPermissions.hasPermissions(MaxCheckRealWaveActivity.this, PermissionCodeUtil.PERMISSION_EXTERNAL_STORAGE)) {
//                            share();
//                        } else {
//                            EasyPermissions.requestPermissions(MaxCheckRealWaveActivity.this, String.format(getString(R.string.m权限获取某权限说明), getString(R.string.m存储)), PermissionCodeUtil.PERMISSION_EXTERNAL_STORAGE_CODE, PermissionCodeUtil.PERMISSION_EXTERNAL_STORAGE);
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
//                .setTitle(getResources().getString(R.string.温馨提示))
//                .setText("截图保存到相册")
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
                //判断设置值是否全部输入
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
     * 重置按钮属性
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
    private int[] nowSet = {0x10, 0x0104, 0x0108};
    private int[] value;
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
                        sendBytes = SocketClientUtil.sendMsgToServer10(mClientUtilWriter, nowSet, value);
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
                    nowTimeCount = 0;
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //检测内容正确性
                        boolean isCheck = ModbusUtil.checkModbus(bytes);
                        if (isCheck) {
                            //接收正确，开始解析
                            RegisterParseUtil.parseMaxErr14(mTotalBean, bytes, count);
                            //刷新ui
                            updateUi();
                        }
                        noticeUpdateTime(count + 1, true);
                        if (count < funs.length - 1) {
                            count++;
                            mHandler.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        } else {
                            //保存数据
                            SqliteUtil.setListJson(GlobalConstant.MAX_REAL_LAST_DATA, MaxUtil.saveList(dataList));
                            //保存id
                            if (mList != null) {
                                SharedPreferencesUnit.getInstance(mContext).put(GlobalConstant.MAX_REAL_ID_LAST_DATA, new Gson().toJson(mList));
                            }
                            //更新ui
//                            refreshUI();
                            count = 0;
                            //关闭连接
                            SocketClientUtil.close(mClientUtil);
                            refreshFinish();
                            //保存更新时间
                            SharedPreferencesUnit.getInstance(mContext).put(GlobalConstant.MAX_REAL_LAST_TIME, ChartUtils.getFormatDate(null, null));
                            mTvLastTime.setText(String.format("%s%s", readStr3, SharedPreferencesUnit.getInstance(mContext).get(GlobalConstant.MAX_REAL_LAST_TIME)));
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
     * 更新界面UI
     */
    private void updateUi() {
        if (mTotalBean != null) {
            MaxUtil.setLineChartDataSpe(mContext, mLineChart, mTotalBean.getDataList(), colors, colors_a, dataList.size(), R.color.highLightColor, true);
            //时间
//            if (mTotalBean.getTimes() != null && mTotalBean.getTimes().size() > 0) {
//                mTvTime.setText(mTotalBean.getTimes().get(0));
//            }
            //故障码
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
    public void onItemChildClick(BaseQuickAdapter adapter, View view, final int position) {
        if (adapter == mAdapter) {
            switch (view.getId()) {
                case R.id.tvID:
                    CircleDialogUtils.showCommentInputDialog(this, getString(R.string.action_settings), "", "", true, new OnInputClickListener() {
                        @Override
                        public boolean onClick(String text, View v) {
                            LogUtil.i("v类型：" + v);
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
                                        //更新数据
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
     * 修改倍数后更新linechart数据
     *
     * @param pos 当前item位置
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
