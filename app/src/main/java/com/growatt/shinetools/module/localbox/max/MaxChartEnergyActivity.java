package com.growatt.shinetools.module.localbox.max;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.IdRes;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.growatt.shinetools.R;
import com.growatt.shinetools.base.DemoBase;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.utils.BtnDelayUtil;
import com.growatt.shinetools.utils.ChartUtils;
import com.growatt.shinetools.utils.Log;
import com.growatt.shinetools.utils.Mydialog;
import com.growatt.shinetools.utils.Position;
import com.growatt.shinetools.utils.chartformatter.MaxChartFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MaxChartEnergyActivity extends DemoBase {
    @BindView(R.id.headerView)
    View headerView;
    @BindView(R.id.radio_button1)
    RadioButton mRadioButton1;
    @BindView(R.id.radio_button2)
    RadioButton mRadioButton2;
    @BindView(R.id.radio_button3)
    RadioButton mRadioButton3;
    @BindView(R.id.radio_button4)
    RadioButton mRadioButton4;
    @BindView(R.id.radioGroup)
    RadioGroup mRadioGroup;
    @BindView(R.id.barChart)
    BarChart mBarChart;
    @BindView(R.id.tvContent)
    TextView mTvContent;
    @BindView(R.id.tvXUnit)
    TextView mTvXUnit;
    private String mTitle;
    private int nowPos = 0;
    //需要查询的寄存器地址（功能码，开始寄存器，结束寄存器）
    private int[][] funReads;
    //柱状图数据
    private List<List<BarEntry>> dataListBar;
    private String[] xUnits ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_max_chart_energy);
        ButterKnife.bind(this);
        initIntent();
        initString();
        initHeaderView();
        initBarChart();
        initListenr();
        mHandlerRead.sendEmptyMessageDelayed(SocketClientUtil.SOCKET_AUTO_DELAY,500);
    }

    private void initString() {
        funReads = new int[][]{
                {4, 284, 331}
                , {4, 332, 345}
                , {4, 346, 369}
                , {4, 375, 414}
        };
        xUnits = new String[]{
            String.format("(%s/%s)",getString(R.string.m358日),getString(R.string.m353时)),
            String.format("(%s-%s)",getString(R.string.m359月),getString(R.string.m358日)),
            String.format("(%s-%s)",getString(R.string.m360年),getString(R.string.m359月)),
            String.format("(%s)",getString(R.string.m360年))
        };
        mTvXUnit.setText(xUnits[0]);
    }

    private void initBarChart() {
        ChartUtils.initBarChart(this, mBarChart, "", true, R.color.note_bg_white,
                R.color.grid_bg_white, R.color.grid_bg_white, true, R.color.grid_bg_white,
                true, R.color.grid_bg_white, R.color.highLightColor);
    }

    private void initListenr() {
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                switch (checkedId) {
                    case R.id.radio_button1:
                        nowPos = 0;
                        updateTitle(getString(R.string.m354最近24小时发电量));
                        break;
                    case R.id.radio_button2:
                        nowPos = 1;
                        updateTitle(getString(R.string.m355最近7天发电量));
                        break;
                    case R.id.radio_button3:
                        nowPos = 2;
                        updateTitle(getString(R.string.m356最近12个月发电量));
                        break;
                    case R.id.radio_button4:
                        nowPos = 3;
                        updateTitle(getString(R.string.m357最近20年发电量));
                        break;
                }
                mTvXUnit.setText(xUnits[nowPos]);
                readRegisterValue();
            }
        });
    }

    /**
     * 读取数据进行封装
     */
    //连接对象:用于读取数据
    private SocketClientUtil mClientUtilRead;

    //读取寄存器的值
    private void readRegisterValue() {
        Mydialog.Show(mContext);
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
                    byte[] sendBytesR = SocketClientUtil.sendMsgToServer(mClientUtilRead, funReads[nowPos]);
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
                            //解析数据
                            parseData(bs);
//                            int value1 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 0, 0, 1));
                            toast(R.string.all_success);
                        } else {
                            toast(R.string.all_failed);
                        }
                        Log.i("接收读取：" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        SocketClientUtil.close(mClientUtilRead);
                        BtnDelayUtil.refreshFinish();
                    }
                    break;
                //延迟刷新；以防上一个界面刷新停止马上刷新导致失败
                case SocketClientUtil.SOCKET_AUTO_DELAY:
                    readRegisterValue();
                    break;
                default:
                    BtnDelayUtil.dealMaxBtn(this, what, mContext, mRadioButton1, mRadioButton2, mRadioButton3, mRadioButton4);
                    break;
            }
        }
    };

    /**
     * 解析并设置数据
     *
     * @param bs
     */
    private Calendar calendar;
    private SimpleDateFormat spfYear = new SimpleDateFormat("yyyy");
    private SimpleDateFormat spfMonth = new SimpleDateFormat("yy-MM");
    private SimpleDateFormat spfDay = new SimpleDateFormat("MM-dd");
    private SimpleDateFormat spfHour = new SimpleDateFormat("dd/HH");

    public void parseData(byte[] bs) {
        try {
            dataListBar = new ArrayList<>();
            dataListBar.add(new ArrayList<BarEntry>());
            dataListBar = ChartUtils.parseBarChartRegister(dataListBar, bs, nowPos);
            XAxis xAxis = mBarChart.getXAxis();
            //设置转换方式
            xAxis.setValueFormatter(new MaxChartFormatter(nowPos));
            ChartUtils.setBarChartData(this, mBarChart, dataListBar, new int[]{R.color.chart_green_normal}, new int[]{R.color.chart_green_click}, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateTitle(String content) {
        mTvContent.setText(content);
    }

    private void initHeaderView() {
        setHeaderImage(headerView, R.drawable.icon_return, Position.LEFT, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setHeaderTitle(headerView, mTitle);
    }

    private void initIntent() {
        Intent mIntent = getIntent();
        if (mIntent != null) {
            mTitle = mIntent.getStringExtra("title");
        }
    }
}
