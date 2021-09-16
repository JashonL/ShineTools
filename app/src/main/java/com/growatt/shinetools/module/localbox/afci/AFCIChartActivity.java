package com.growatt.shinetools.module.localbox.afci;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.Barrier;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.growatt.shinetools.R;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.listeners.OnEmptyListener;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.MaxWifiParseUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.utils.BtnDelayUtil;
import com.growatt.shinetools.utils.ChartUtils;
import com.growatt.shinetools.utils.DialogUtils;
import com.growatt.shinetools.utils.Log;
import com.growatt.shinetools.utils.MyControl;
import com.growatt.shinetools.utils.MyToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.growatt.shinetools.modbusbox.SocketClientUtil.SOCKET_SERVER_SET;
import static com.growatt.shinetools.utils.BtnDelayUtil.TIMEOUT_RECEIVE;
import static com.growatt.shinetools.utils.BtnDelayUtil.refreshFinish;


public class AFCIChartActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, Toolbar.OnMenuItemClickListener,
        CompoundButton.OnCheckedChangeListener {


    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.header_title)
    LinearLayout headerTitle;
    @BindView(R.id.tvTitle1)
    TextView tvTitle1;

    @BindView(R.id.barrier)
    Barrier barrier;


    @BindView(R.id.tvTitle2)
    TextView tvTitle2;

    @BindView(R.id.tvTitle3)
    TextView tvTitle3;

    @BindView(R.id.tvAFCITitle)
    TextView tvAFCITitle;
    @BindView(R.id.rBtn1)
    RadioButton rBtn1;
    @BindView(R.id.rBtn2)
    RadioButton rBtn2;
    @BindView(R.id.rBtn3)
    RadioButton rBtn3;
    @BindView(R.id.rBtn4)
    RadioButton rBtn4;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.tvReadChart)
    TextView tvReadChart;
    @BindView(R.id.tvUnit1)
    TextView tvUnit1;
    @BindView(R.id.tvUnitTitle1)
    TextView tvUnitTitle1;
    @BindView(R.id.tvUnit1Note1)
    TextView tvUnit1Note1;
    @BindView(R.id.tvUnit1Note2)
    TextView tvUnit1Note2;
    @BindView(R.id.lineChart1)
    LineChart lineChart1;
    @BindView(R.id.tvUnit2)
    TextView tvUnit2;
    @BindView(R.id.tvUnitTitle2)
    TextView tvUnitTitle2;
    @BindView(R.id.tvUnit2Note1)
    TextView tvUnit2Note1;
    @BindView(R.id.tvUnit2Note2)
    TextView tvUnit2Note2;
    @BindView(R.id.lineChart2)
    LineChart lineChart2;
    @BindView(R.id.tvRead1)
    Switch tvRead1;
    @BindView(R.id.tvRead2)
    Switch tvRead2;
    @BindView(R.id.tvRead3)
    Switch tvRead3;
    private String[] afciSelect1s;
    private String[] afciSelect2s;
    private String[] afciSelect3s;
    //弹框选择item
    private String[][] items;
    //设置功能码集合（功能码，寄存器，数据）
    private int[][][] funsSet;
    private int mType = -1;
    private String[] nowItems;
    private int nowPos = -1;//当前选择下标
    private int[] nowSet1 = null;
    private int[] nowSet2 = null;
    private int[] nowSet3 = null;
    private int nowClick = -1;
    private int chartType = -1;
    private int waitTime = 10000;//ms  10s
    private int waitTimeZiJian = 5000;//ms  10s
    private int[] chartNowSet = null;
    private final int TIME_COUNT = 2;//超时重发次数 + 1
    private int nowTimeCount = 0;
    //曲线数据集
    private List<ArrayList<Entry>> dataList1;
    private List<ArrayList<Entry>> dataList2;
    int[] colors1 = {R.color.max_iv_graph_linechart1, R.color.max_iv_graph_linechart2};
    int[] colors1_a = {R.color.max_iv_graph_linechart1, R.color.max_iv_graph_linechart2};
    int[] colors2 = {R.color.max_iv_graph_linechart3, R.color.max_iv_graph_linechart4};
    int[] colors2_a = {R.color.max_iv_graph_linechart3, R.color.max_iv_graph_linechart4};
    private final int REAL_READ_CHART = 1208;
    private final int REAL_WAIT_TIME = 1209;
    //设置值
    private int mTypeValue0 = 0;
    private int mTypeValue1 = 0;
    private int mTypeValue2 = 0;
    int[] funsAllRead = {3, 541, 543};//无功功率百分比4;

    @Override
    protected int getContentView() {
        return R.layout.activity_afcichart;
    }

    @Override
    protected void initViews() {
        radioGroup.setOnCheckedChangeListener(this);
        tvRead3.setOnCheckedChangeListener(this);
        tvRead2.setOnCheckedChangeListener(this);
        tvRead1.setOnCheckedChangeListener(this);
    }

    @Override
    protected void initData() {
        initString();
        initLineChart();
    }


    /**
     * 初始化图表
     */
    private void initLineChart() {
        ChartUtils.initLineChart(this, lineChart1, 0, "", true,
                R.color.grid_bg_white, true, R.color.grid_bg_white, true,
                R.color.note_bg_white, R.color.grid_bg_white, R.color.grid_bg_white, R.color.highLightColor,
                false, R.string.android_key1183, R.string.android_key2223, new OnEmptyListener() {
                    @Override
                    public void onEmpty(Entry e, Highlight highlight) {

                    }
                });
        lineChart1.getAxisLeft().setAxisMinimum(0f);

        ChartUtils.initLineChart(this, lineChart2, 0, "", true, R.color.grid_bg_white, true, R.color.grid_bg_white, true, R.color.note_bg_white, R.color.grid_bg_white, R.color.grid_bg_white, R.color.highLightColor, false, R.string.android_key1183, R.string.android_key2223, new OnEmptyListener() {
            @Override
            public void onEmpty(Entry e, Highlight highlight) {
                Log.i("x位置" + e.getX());
//                getValuesByEntry(e);
            }
        });
        lineChart2.getAxisLeft().setAxisMinimum(0f);
    }

    private void initString() {
        tvTitle.setText(R.string.android_key2399);
        toolbar.inflateMenu(R.menu.main_menu);
        MenuItem item = toolbar.getMenu().findItem(R.id.action_mode_setting);
        item.setTitle(R.string.android_key816);
        initToobar(toolbar);
        toolbar.setOnMenuItemClickListener(this);

        rBtn1.setText(getString(R.string.android_key2400) + " " + getString(R.string.android_key2402));
        rBtn2.setText(getString(R.string.android_key2401) + " " + getString(R.string.android_key2402));
        rBtn3.setText(getString(R.string.android_key2400) + " FFT");
        rBtn4.setText(getString(R.string.android_key2401) + " FFT");
        tvUnitTitle2.setText("FFT " + getString(R.string.android_key2403));
        dataList1 = new ArrayList<>();
        for (int i = 0; i < colors1.length; i++) {
            ArrayList<Entry> list = new ArrayList<>();
            setDemo(list);
            dataList1.add(list);
        }
        dataList2 = new ArrayList<>();
        for (int i = 0; i < colors2.length; i++) {
            ArrayList<Entry> list = new ArrayList<>();
            setDemo(list);
            dataList2.add(list);
        }
        MaxUtil.setLineChartData(this, lineChart1, dataList1, colors1, colors1_a, dataList1.size(), R.color.highLightColor);
        MaxUtil.setLineChartData(this, lineChart1, dataList2, colors2, colors2_a, dataList2.size(), R.color.highLightColor);

        //弹框选择的数据
        items = new String[][]{
                {getString(R.string.android_key1686), getString(R.string.android_key1675)}
                , {getString(R.string.android_key1686), getString(R.string.android_key1675)}
                , {getString(R.string.android_key1686), getString(R.string.android_key1675)}

        };
        //设置功能码集合（功能码，寄存器，数据）
        funsSet = new int[][][]{
                {{6, 541, 0xA0}, {6, 541, 0xA5}}
                , {{6, 542, 0}, {6, 542, 1}}
                , {{6, 543, 0}, {6, 543, 1}}
                , {{6, 548, 1}, {6, 548, 3}, {6, 548, 2}, {6, 548, 4}}
        };
    }

    private void setDemo(ArrayList<Entry> list) {
        for (int i = 0; i < 1; i++) {
            Entry entry = new Entry();
            entry.setX(i + 1);
            entry.setY(0);
            list.add(entry);
        }
    }

    //连接对象:用于读取数据
    private SocketClientUtil mClientUtilRead;

    //读取寄存器的值
    private void readRegisterValue() {
        DialogUtils.getInstance().showLoadingDialog(this);
        mClientUtilRead = SocketClientUtil.connectServer(mHandlerRead);
    }

    /**
     * 读取寄存器handle
     */
    Handler mHandlerRead = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessage(this);
                    byte[] sendBytesR = SocketClientUtil.sendMsgToServer(mClientUtilRead, funsAllRead);
                    Log.i("发送读取：" + SocketClientUtil.bytesToHexString(sendBytesR));
                    break;
                //接收字节数组
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //检测内容正确性
                        boolean isCheck = ModbusUtil.checkModbus(bytes);
                        if (isCheck) {
                            //移除外部协议
                            byte[] bs = RegisterParseUtil.removePro17(bytes);
                            //解析int值
                            int value0 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 0, 0, 1));//541
                            int value1 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 1, 0, 1));//542
                            int value2 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 2, 0, 1));//543
                            //更新ui
                            if (value0 == 0xA0 || value0 == 0xA5) {
                                if (value0 == 0xA0) mTypeValue0 = 0;
                                if (value0 == 0xA5) mTypeValue0 = 1;
//                                String text = items[0][mTypeValue0] + "(" + value0 + ")";
                                tvRead1.setChecked(mTypeValue0==1);

                            }
                            if (value1 >= 0 && value1 <= 1) {
                                mTypeValue1 = value1;
//                                String text = items[1][mTypeValue1] + "(" + mTypeValue1 + ")";
//                                String text = items[1][mTypeValue1];
//                                btnSelect2.setText(text);
                                tvRead2.setChecked(mTypeValue1==1);
                            }
                            if (value2 >= 0 && value2 <= 1) {
                                mTypeValue2 = value2;
//                                String text = items[2][mTypeValue2] + "(" + mTypeValue2 + ")";
//                                String text = items[2][mTypeValue2];
//                                btnSelect3.setText(text);
                                tvRead3.setChecked(mTypeValue2==1);
                            }
                            MyToastUtils.toast(R.string.android_key121);
                        } else {
                            MyToastUtils.toast(R.string.android_key3129);
                        }
                        Log.i("接收读取：" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //关闭tcp连接
                        SocketClientUtil.close(mClientUtilRead);
                        refreshFinish();
                    }
                    break;
                default:
                    BtnDelayUtil.dealMaxBtn(this, what, AFCIChartActivity.this, toolbar);
                    break;

            }
        }
    };

    @OnClick({R.id.tvReadChart})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvReadChart:
                if (chartType == -1) {
                    MyToastUtils.toast(R.string.android_key499);
                    return;
                }
                chartNowSet = funsSet[3][chartType];
                writeRegisterValue();
//                showDemoChart();
                break;
   /*         case R.id.tvRead1:
                //设置
                if (mTypeValue0 == -1) {
                    MyToastUtils.toast(R.string.android_key187);
                    return;
                }
                nowClick = 0;
                nowSet1 = funsSet[0][mTypeValue0];
                connectServerWrite();
                break;
            case R.id.tvRead2:
                if (mTypeValue1 == -1) {
                    MyToastUtils.toast(R.string.android_key187);
                    return;
                }
                nowClick = 1;
                nowSet2 = funsSet[1][mTypeValue1];
                if (mTypeValue1 == 1) {
                    count2 = 0;
                    connectServerWrite2();
                } else {
                    connectServerWrite();
                }
                break;
            case R.id.tvRead3:
                if (mTypeValue2 == -1) {
                    MyToastUtils.toast(R.string.android_key187);
                    return;
                }
                nowClick = 2;
                nowSet3 = funsSet[2][mTypeValue2];
                connectServerWrite();
                break;*/
        }
    }

    //连接对象:用于设置数据
    private SocketClientUtil mClientUtilWriterChart;

    //设置寄存器的值
    private void writeRegisterValue() {

        DialogUtils.getInstance().showLoadingDialog(this);

        mClientUtilWriterChart = SocketClientUtil.connectServer(mHandlerWriteChart);

//        byte[] chatSendBytes = ModbusUtil.sendMsg(chartNowSet[0], chartNowSet[1], chartNowSet[2]);
//        Log.i("发送写入测试：" + SocketClientUtil.bytesToHexString(chatSendBytes));
//
//        byte[] sendBytes = ModbusUtil.sendMsg(funs[count][0], funs[count][1], funs[count][2]);
//        Log.i("发送读取曲线测试：" + SocketClientUtil.bytesToHexString(sendBytes));
    }

    /**
     * 写寄存器handle
     */
    //当前发送的字节数组
    private byte[] chatSendBytes;
    Handler mHandlerWriteChart = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessageWrite(this);
                    if (chartNowSet != null && chartNowSet[2] != -1) {
                        chatSendBytes = SocketClientUtil.sendMsgToServer(mClientUtilWriterChart, chartNowSet);
                        Log.i("发送写入：" + SocketClientUtil.bytesToHexString(chatSendBytes));
                    } else {
                        MyToastUtils.toast(R.string.android_key2271);
                    }
                    break;
                //接收字节数组
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //检测内容正确性
                        boolean isCheck = MaxUtil.isCheckFull(AFCIChartActivity.this, bytes);
                        //关闭tcp连接
                        SocketClientUtil.close(mClientUtilWriterChart);
                        refreshFinish();
                        if (isCheck) {
                            //开始读取
                            mHandlerTime.sendEmptyMessageDelayed(REAL_WAIT_TIME, 3000);
                            mHandlerTime.sendEmptyMessageDelayed(REAL_READ_CHART, waitTime);
                        } else {
                            MyToastUtils.toast(R.string.android_key539);
                        }
                        Log.i("接收写入：" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        //关闭tcp连接
                        SocketClientUtil.close(mClientUtilWriterChart);
                        refreshFinish();
                    }
                    break;
                default:
                    BtnDelayUtil.dealMaxBtnWrite(this, what, AFCIChartActivity.this, tvReadChart);
                    break;
            }
        }
    };

    //连接对象
    private SocketClientUtil mClientUtil;

    private void connectServer() {
        DialogUtils.getInstance().closeLoadingDialog();
        DialogUtils.getInstance().showLoadingDialog(this);
        mClientUtil = SocketClientUtil.newInstance();
        if (mClientUtil != null) {
            mClientUtil.connect(mHandler);
        }
    }

    Handler mHandlerTime = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REAL_WAIT_TIME:
                    DialogUtils.getInstance().showLoadingDialog(AFCIChartActivity.this);
                    break;
                case REAL_READ_CHART:
                    connectServer();
                    break;
                case 9980:
                    readRegisterValue2();
                    break;
            }
        }
    };
    /**
     * 读取寄存器handle
     */
    private int count;
    int[][] funs = {{0x1E, 0, 0x7d - 1}, {0x1E, 0x7d, 0x7d + 2}};
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
                    Log.i("发送消息:" + sendMsg);
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
                            switch (chartType) {
                                case 0:
                                case 1:
                                    parseChart(dataList1, bytes, count, chartType);
                                    MaxUtil.setLineChartData(AFCIChartActivity.this, lineChart1, dataList1, colors1, colors1_a, dataList1.size(), R.color.highLightColor);
                                    break;
                                case 2:
                                case 3:
                                    parseChart(dataList2, bytes, count, chartType);
                                    MaxUtil.setLineChartData(AFCIChartActivity.this, lineChart2, dataList2, colors2, colors2_a, dataList2.size(), R.color.highLightColor);
                                    break;
                            }
                        }
                        if (count < funs.length - 1) {
                            count++;
                            mHandler.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        } else {
                            //保存数据
//                            SqliteUtil.setListJson(Constant.MAX_IV_LAST_DATA, MaxUtil.saveList(dataList));
//                            Log.i("最后数据：" + SqliteUtil.getListJson(Constant.MAX_IV_LAST_DATA));
                            //更新ui
//                            refreshUI();
                            count = 0;
                            //关闭连接
                            SocketClientUtil.close(mClientUtil);
                            refreshFinish();
                            //保存更新时间
//                            SharedPreferencesUnit.getInstance(AFCIChartActivity.this).put(Constant.MAX_IV_LAST_TIME, MyUtils.getFormatDate(null, null));
                        }
                        Log.i("接收消息:" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        count = 0;
                        //关闭连接
                        SocketClientUtil.close(mClientUtil);
                        DialogUtils.getInstance().closeLoadingDialog();
                    }
                    break;
                case TIMEOUT_RECEIVE://接收超时
                    if (nowTimeCount < TIME_COUNT) {
                        Log.e("重连nowTimecount:" + nowTimeCount);
                        BtnDelayUtil.receiveMessage(this);
                        //重新发送命令
                        this.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        nowTimeCount++;
                        break;
                    }
                    nowTimeCount = 0;
                case SOCKET_SERVER_SET://跳转到wifi列表
                    try {
                        MyControl.showJumpWifiSet(AFCIChartActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case SocketClientUtil.SOCKET_CLOSE:
                case SocketClientUtil.SOCKET_EXCETION_CLOSE:
                    count = 0;
                    SocketClientUtil.close(mClientUtil);
                    break;
                default:
                    BtnDelayUtil.dealMaxBtn(this, what, AFCIChartActivity.this);
                    break;
            }
        }
    };

    public void showDemoChart() {
        //接收正确，开始解析
        switch (chartType) {
            case 0:
            case 1:
//                parseChart(dataList1,bytes,count,chartType);
                MaxUtil.setLineChartData(this, lineChart1, dataList1, colors1, colors1_a, dataList1.size(), R.color.highLightColor);
                break;
            case 2:
            case 3:
//                parseChart(dataList2,bytes,count,chartType);
                MaxUtil.setLineChartData(this, lineChart2, dataList2, colors2, colors2_a, dataList2.size(), R.color.highLightColor);
                break;
        }
    }

    /**
     * 解析14:读取寄存器125个:IV图表
     */
    public static void parseChart(List<ArrayList<Entry>> list, byte[] bytes, int count, int chartType) {
        //移除外部协议
        byte[] bs = RegisterParseUtil.removePro17(bytes);
        //测试数据
//        if (count == 0){
//
//        }else {
//
//        }

        if (list != null) {
            int size = list.size();
            int pos = chartType % 2;//第几条曲线
            if (size > pos && bs != null) {
                ArrayList<Entry> entries = list.get(pos);
                int len = bs.length / 2;
                if (count == 0) {
                    entries.clear();
                }
                int start = entries.size();
                for (int i = 0; i < len; i++) {
                    int posX = start + i + 1;
                    int posY = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, i, 0, 1));
                    Entry entry = new Entry();
                    entry.setX(posX);
                    entry.setY(posY);
                    entries.add(entry);
                }

            }
        }
    }
/*
    private void selectItem() {
        CircleDialogUtils.showCommentItemDialog(this, getString(R.string.android_key1809),
                Arrays.asList(nowItems), Gravity.CENTER, new OnLvItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (nowItems != null && nowItems.length > position) {
                            String text = nowItems[position] + "(" + position + ")";
                            switch (mType) {
                                case 0:
                                    btnSelect1.setText(text);
                                    mTypeValue0 = position;
                                    break;
                                case 1:
                                    btnSelect2.setText(text);
                                    mTypeValue1 = position;
                                    break;
                                case 2:
                                    btnSelect3.setText(text);
                                    mTypeValue2 = position;
                                    break;
                            }
                        }
                        return true;
                    }
                }, null);

    }*/

    //连接对象:用于写数据
    private SocketClientUtil mClientUtilW;

    private void connectServerWrite() {
        DialogUtils.getInstance().showLoadingDialog(AFCIChartActivity.this);
        mClientUtilW = SocketClientUtil.connectServer(mHandlerW);
    }

    //连接对象:用于写数据
    private SocketClientUtil mClientUtilW2;

    private void connectServerWrite2() {
        DialogUtils.getInstance().showLoadingDialog(AFCIChartActivity.this);
        mClientUtilW2 = SocketClientUtil.connectServer(mHandlerW2);
    }

    /**
     * 写寄存器handle
     */
    //当前发送的字节数组
    private byte[] sendBytes;
    Handler mHandlerW = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessageWrite(this);
                    int[] nowSet = null;
                    if (nowClick == 0) {
                        nowSet = nowSet1;
                    } else if (nowClick == 1) {
                        nowSet = nowSet2;
                    } else if (nowClick == 2) {
                        nowSet = nowSet3;
                    }
                    if (nowSet == null) return;
                    sendBytes = sendMsg(mClientUtilW, nowSet);
                    Log.i("发送写入：" + SocketClientUtil.bytesToHexString(sendBytes));
                    break;
                //接收字节数组
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //检测内容正确性
                        boolean isCheck = MaxUtil.checkReceiverFull(bytes);
                        if (isCheck) {
                            MyToastUtils.toast(R.string.android_key121);
                        } else {
                            MyToastUtils.toast(R.string.android_key3129);
                        }
                        Log.i("接收写入：" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //关闭tcp连接
                        SocketClientUtil.close(mClientUtilW);
                        refreshFinish();
                    }
                    break;
                default:
                    BtnDelayUtil.dealMaxBtnWrite(this, what, AFCIChartActivity.this);
                    break;
            }
        }
    };

    /**
     * 写寄存器handle
     */
    //当前发送的字节数组
    private byte[] sendBytes2;
    Handler mHandlerW2 = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessageWrite(this);
                    sendBytes2 = sendMsg(mClientUtilW2, nowSet2);
                    Log.i("发送写入：" + SocketClientUtil.bytesToHexString(sendBytes2));
                    break;
                //接收字节数组
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //检测内容正确性
                        boolean isCheck = MaxUtil.checkReceiverFull(bytes);
                        if (isCheck) {
                            //关闭tcp连接
                            SocketClientUtil.close(mClientUtilW2);
                            refreshFinish();
                            //开始读取
                            mHandlerTime.sendEmptyMessageDelayed(REAL_WAIT_TIME, 3000);
                            mHandlerTime.sendEmptyMessageDelayed(9980, waitTimeZiJian);
                        } else {
                            MyToastUtils.toast(R.string.android_key3129);
                        }
                        Log.i("接收写入：" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //关闭tcp连接
                        SocketClientUtil.close(mClientUtilW2);
                        refreshFinish();
                    }
                    break;
                default:
                    BtnDelayUtil.dealMaxBtnWrite(this, what, AFCIChartActivity.this);
                    break;
            }
        }
    };


    //连接对象:用于读取数据
    private SocketClientUtil mClientUtilRead2;

    //读取寄存器的值
    private void readRegisterValue2() {
        DialogUtils.getInstance().closeLoadingDialog();
        DialogUtils.getInstance().showLoadingDialog(this);
        mClientUtilRead2 = SocketClientUtil.connectServer(mHandlerRead2);
    }

    int[] funs2 = {4, 105, 105};
    int count2 = 0;
    Handler mHandlerRead2 = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessage(this, 8000);
                    byte[] sendBytesR = SocketClientUtil.sendMsgToServer(mClientUtilRead2, funs2);
                    Log.i("发送读取：" + SocketClientUtil.bytesToHexString(sendBytesR));
                    break;
                //接收字节数组
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        Log.i("接收消息:" + SocketClientUtil.bytesToHexString(bytes));
                        //检测内容正确性
                        boolean isCheck = ModbusUtil.checkModbus(bytes);
                        //先判断，再次读取
                        if (isCheck) {
                            //解析int值
                            //移除外部协议
                            byte[] bs = RegisterParseUtil.removePro17(bytes);
                            int value = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 0, 0, 1));//541
                            count2++;
                            this.sendEmptyMessageDelayed(SocketClientUtil.SOCKET_SEND, 5000);
                            if (count2 > 5) {
                                MyToastUtils.toast(R.string.android_key2412);
                                SocketClientUtil.close(mClientUtilRead2);
                                DialogUtils.getInstance().closeLoadingDialog();
                                this.removeMessages(SocketClientUtil.SOCKET_SEND);
                            } else {
                                if (value == 15 || value == 425) {
                                    MyToastUtils.toast(R.string.android_key2411);
                                    SocketClientUtil.close(mClientUtilRead2);
                                    DialogUtils.getInstance().closeLoadingDialog();
                                    this.removeMessages(SocketClientUtil.SOCKET_SEND);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        SocketClientUtil.close(mClientUtilRead2);
                        DialogUtils.getInstance().closeLoadingDialog();
                    }
                    break;
                case SocketClientUtil.SOCKET_CLOSE:
//                    toast("Socket 关闭");
                    this.removeMessages(SocketClientUtil.SOCKET_SEND);
                    break;
                case TIMEOUT_RECEIVE:
                    this.removeMessages(SocketClientUtil.SOCKET_SEND);
                    this.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                    break;
            }
        }
    };

    /**
     * 根据命令以及起始寄存器发送查询命令
     *
     * @param clientUtil
     * @param sends
     * @return：返回发送的字节数组
     */
    private byte[] sendMsg(SocketClientUtil clientUtil, int[] sends) {
        if (clientUtil != null) {
            byte[] sendBytes = ModbusUtil.sendMsg(sends[0], sends[1], sends[2]);
            clientUtil.sendMsg(sendBytes);
            return sendBytes;
        } else {
            connectServerWrite();
            return null;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        if (radioGroup == radioGroup) {
            switch (checkedId) {
                case R.id.rBtn1:
                    chartType = 0;
                    break;
                case R.id.rBtn2:
                    chartType = 1;
                    break;
                case R.id.rBtn3:
                    chartType = 2;
                    break;
                case R.id.rBtn4:
                    chartType = 3;
                    break;
            }
        }
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_mode_setting:
                //读取寄存器的值
                readRegisterValue();
                break;
        }
        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton.isPressed()) {
            if (compoundButton ==tvRead1){
                mTypeValue0=b?1:0;
                nowClick = 0;
                nowSet1 = funsSet[0][mTypeValue0];
                connectServerWrite();
            }else if (compoundButton==tvRead2){
                mTypeValue1=b?1:0;
                nowClick = 1;
                nowSet2 = funsSet[1][mTypeValue1];
                if (mTypeValue1 == 1) {
                    count2 = 0;
                    connectServerWrite2();
                } else {
                    connectServerWrite();
                }
            }else if (compoundButton==tvRead3){
                mTypeValue2=b?1:0;
                nowClick = 2;
                nowSet3 = funsSet[2][mTypeValue2];
                connectServerWrite();
            }

        }
    }

}
