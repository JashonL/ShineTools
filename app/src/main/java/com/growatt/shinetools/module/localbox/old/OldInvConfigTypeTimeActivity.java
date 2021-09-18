package com.growatt.shinetools.module.localbox.old;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.growatt.shinetools.R;
import com.growatt.shinetools.base.DemoBase;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.MaxWifiParseUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.utils.BtnDelayUtil;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.Mydialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.growatt.shinetools.utils.BtnDelayUtil.TIMEOUT_RECEIVE;


/**
 * 单个寄存器选择设置
 */
public class OldInvConfigTypeTimeActivity extends DemoBase  implements Toolbar.OnMenuItemClickListener {
    String readStr;
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
    @BindView(R.id.btnSelect)
    Button btnSelect;
    @BindView(R.id.tvContent1)
    TextView tvContent1;
    @BindView(R.id.btnSetting)
    Button btnSetting;


    private String mTitle;
    private int mType = -1;
    private int nowPos = -1;//当前选择下标
    //读取命令功能码（功能码，开始寄存器，结束寄存器）
    private int[][] funs;
    //设置功能码集合（功能码，寄存器，数据）
    private int[][][] funsSet;
    private int[] nowSet = null;
    //当前秒
    private int nowSecond;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_type_time);
        ButterKnife.bind(this);
        ModbusUtil.setComAddressOldInv();
        initIntent();
        initString();
        initHeaderView();
    }


    private void initString() {
        readStr = getString(R.string.m369读取值);
        //读取命令功能码（功能码，开始寄存器，结束寄存器）
        funs = new int[][]{
                {3, 45, 50}//6时间
        };
        //设置功能码集合（功能码，寄存器，数据）
        funsSet = new int[][][]{
                {{6, 45, -1}, {6, 46, -1}, {6, 47, -1}, {6, 48, -1}, {6, 49, -1}, {6, 50, -1}}
        };
        tvTitle.setText(mTitle);
        //设置初始化时间
        btnSelect.setText(sdf.format(new Date()));
        Calendar c1 = Calendar.getInstance();
        int year = c1.get(Calendar.YEAR);
        int month = c1.get(Calendar.MONTH);
        int dayOfMonth = c1.get(Calendar.DAY_OF_MONTH);
        int hourOfDay = c1.get(Calendar.HOUR_OF_DAY);
        int minute = c1.get(Calendar.MINUTE);
        int nowSecond = c1.get(Calendar.SECOND);
        if (year > 2000) {
            funsSet[mType][0][2] = year - 2000;
        } else {
            funsSet[mType][0][2] = year;
        }
        funsSet[mType][1][2] = month + 1;
        funsSet[mType][2][2] = dayOfMonth;
        funsSet[mType][3][2] = hourOfDay;
        funsSet[mType][4][2] = minute;
        funsSet[mType][5][2] = nowSecond;

    }


    private void initIntent() {
        Intent mIntent = getIntent();
        if (mIntent != null) {
            mTitle = mIntent.getStringExtra("title");
            mType = mIntent.getIntExtra("type", -1);
        }
    }

    private void initHeaderView() {
        initToobar(toolbar);
        tvTitle.setText(mTitle);
        toolbar.inflateMenu(R.menu.comment_right_menu);
        toolbar.getMenu().findItem(R.id.right_action).setTitle(R.string.m370读取);
        toolbar.setOnMenuItemClickListener(this);
    }

    //读取寄存器的值
    private void readRegisterValue() {
        connectServer();
    }

    //连接对象:用于读取数据
    private SocketClientUtil mClientUtil;

    private void connectServer() {
        Mydialog.Show(mContext);
        mClientUtil = SocketClientUtil.connectServer(mHandler);
    }

    //连接对象:用于写数据
    private SocketClientUtil mClientUtilW;

    private void connectServerWrite() {
        Mydialog.Show(mContext);
        mClientUtilW = SocketClientUtil.connectServer(mHandlerW);
    }

    /**
     * 写寄存器handle
     */
    //当前发送的字节数组
    private byte[] sendBytes;
    private boolean isWriteFinish;
    Handler mHandlerW = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessageWrite(this);
                    if (funsSet != null) {
                        isWriteFinish = true;
                        for (int i = 0, len = funsSet[mType].length; i < len; i++) {
                            if (funsSet[mType][i][2] != -1) {
                                isWriteFinish = false;
                                sendBytes = SocketClientUtil.sendMsgToServerOldInv(mClientUtilW, funsSet[mType][i]);
                                LogUtil.i("发送写入" + (i + 1) + ":" + SocketClientUtil.bytesToHexString(sendBytes));
                                //发送完将值设置为-1
                                funsSet[mType][i][2] = -1;
                                break;
                            }
                        }
                        //关闭tcp连接;判断是否请求完毕
                        if (isWriteFinish) {
                            //移除接收超时
                            this.removeMessages(TIMEOUT_RECEIVE);
                            SocketClientUtil.close(mClientUtilW);
                            BtnDelayUtil.refreshFinish();
                        }
                    }
                    break;
                //接收字节数组
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //检测内容正确性
                        boolean isCheck = MaxUtil.checkReceiverFull(bytes);
                        if (isCheck) {
                            //移除外部协议
//                            byte[] bs = RegisterParseUtil.removePro17Fun6(bytes);
                            //解析int值
//                            int value0 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,1,0,1));

                            toast(getString(R.string.all_success), Toast.LENGTH_SHORT);

                            //继续发送设置命令
                            mHandlerW.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        } else {
                            toast(getString(R.string.all_failed));
                            //将内容设置为-1初始值
                            funsSet[mType][0][2] = -1;
                            //关闭tcp连接
                            SocketClientUtil.close(mClientUtilW);
                            BtnDelayUtil.refreshFinish();
                        }
                        LogUtil.i("接收写入 " + ":" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        //关闭tcp连接
                        SocketClientUtil.close(mClientUtilW);
                        BtnDelayUtil.refreshFinish();
                    }
                    break;
                default:
                    BtnDelayUtil.dealMaxBtnWrite(this, what, mContext, btnSetting, toolbar);
                    break;
            }
        }
    };
    /**
     * 读取寄存器handle
     */
    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessage(this);
                    byte[] sendBytesR = sendMsg(mClientUtil, funs[mType]);
                    LogUtil.i("发送读取：" + SocketClientUtil.bytesToHexString(sendBytesR));
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
                            int year = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 0, 0, 1));
                            int month = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 1, 0, 1));
                            int day = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 2, 0, 1));
                            int hour = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 3, 0, 1));
                            int min = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 4, 0, 1));
                            int seconds = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 5, 0, 1));
                            //更新ui
                            StringBuilder sb = new StringBuilder()
                                    .append(year).append("-")
                                    .append(month).append("-")
                                    .append(day).append(" ")
                                    .append(hour).append(":");
                            if (min < 10) {
                                sb.append("0");
                            }
                            sb.append(min).append(":");
                            if (seconds < 10) {
                                sb.append("0");
                            }
                            sb.append(seconds);
                            tvContent1.setText(readStr + ":" + sb.toString());
                            toast(R.string.all_success);
                        } else {
                            toast(R.string.all_failed);
                        }
                        LogUtil.i("接收读取：" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //关闭tcp连接
                        SocketClientUtil.close(mClientUtil);
                    }
                    break;
                default:
                    BtnDelayUtil.dealMaxBtn(this, what, mContext, btnSetting, toolbar);
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
            byte[] sendBytes = ModbusUtil.sendMsgOldInv(sends[0], sends[1], sends[2]);
            clientUtil.sendMsg(sendBytes);
            return sendBytes;
        } else {
            connectServer();
            return null;
        }
    }

    @OnClick({R.id.btnSelect, R.id.btnSetting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnSelect:
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(
                        mContext
                        , new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year, final int month, final int dayOfMonth) {
                        // 创建一个TimePickerDialog实例，并把它显示出来
                        Calendar c2 = Calendar.getInstance();
                        new TimePickerDialog(mContext,
                                // 绑定监听器
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        if (year > 2000) {
                                            funsSet[mType][0][2] = year - 2000;
                                        } else {
                                            funsSet[mType][0][2] = year;
                                        }
                                        funsSet[mType][1][2] = month + 1;
                                        funsSet[mType][2][2] = dayOfMonth;
                                        funsSet[mType][3][2] = hourOfDay;
                                        funsSet[mType][4][2] = minute;
                                        nowSecond = Calendar.getInstance().get(Calendar.SECOND);
                                        funsSet[mType][5][2] = nowSecond;
                                        //更新ui
                                        StringBuilder sb = new StringBuilder()
                                                .append(year).append("-");
                                        if (month + 1 < 10) {
                                            sb.append("0");
                                        }
                                        sb.append(month + 1).append("-");
                                        if (dayOfMonth < 10) {
                                            sb.append("0");
                                        }
                                        sb.append(dayOfMonth).append(" ");
                                        if (hourOfDay < 10) {
                                            sb.append("0");
                                        }
                                        sb.append(hourOfDay).append(":");
                                        if (minute < 10) {
                                            sb.append("0");
                                        }
                                        sb.append(minute).append(":");
                                        if (nowSecond < 10) {
                                            sb.append("0");
                                        }
                                        sb.append(nowSecond);
                                        btnSelect.setText(sb.toString());
                                    }
                                }
                                // 设置初始时间
                                , c2.get(Calendar.HOUR_OF_DAY)
                                , c2.get(Calendar.MINUTE),
                                // true表示采用24小时制
                                true).show();
                    }
                }
                        , c.get(Calendar.YEAR)
                        , c.get(Calendar.MONTH)
                        , c.get(Calendar.DAY_OF_MONTH)
                ).show();
                break;
            case R.id.btnSetting:
                if (funsSet[mType][0][2] == -1) {
                    toast(getString(R.string.m257请选择设置值));
                } else {
//                    connectServerWrite();
                    readRegisterValueCom();
                }
        }
    }

    //连接对象:用于读取数据
    private SocketClientUtil mClientUtilReadCom;
    private int[] funCom = {3, 0, 40};

    //读取寄存器的值
    private void readRegisterValueCom() {
        Mydialog.Show(mContext);
        mClientUtilReadCom = SocketClientUtil.connectServer(mHandlerReadCom);
    }

    /**
     * 读取寄存器handle
     */
    Handler mHandlerReadCom = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessage(this);
                    byte[] sendBytesR = SocketClientUtil.sendMsgToServerOldInv(mClientUtilReadCom, funCom);
                    LogUtil.i("发送读取：" + SocketClientUtil.bytesToHexString(sendBytesR));
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
                            //解析读取值，设置com地址以及model
                            int comAddress = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 30, 0, 1));
                            ModbusUtil.setComAddressOldInv(comAddress);
                            //关闭tcp连接
                            SocketClientUtil.close(mClientUtilReadCom);
                            BtnDelayUtil.refreshFinish();
                            //设置值
                            connectServerWrite();
                        } else {
                            toast(R.string.all_failed);
                            //关闭tcp连接
                            SocketClientUtil.close(mClientUtilReadCom);
                            BtnDelayUtil.refreshFinish();
                        }
                        LogUtil.i("接收读取：" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        //关闭tcp连接
                        SocketClientUtil.close(mClientUtilReadCom);
                        BtnDelayUtil.refreshFinish();
                    }
                    break;
                default:
                    BtnDelayUtil.dealTLXBtn(this, what, mContext, toolbar);
                    break;
            }
        }
    };

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.right_action:
                //读取寄存器的值
                readRegisterValue();
                break;
        }
        return true;
    }
}
