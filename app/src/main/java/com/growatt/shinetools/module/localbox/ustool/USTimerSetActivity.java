package com.growatt.shinetools.module.localbox.ustool;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.growatt.shinetools.R;
import com.growatt.shinetools.adapter.USTimerAdapter;
import com.growatt.shinetools.base.DemoBase;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.MaxWifiParseUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.module.TimerSetActivity;
import com.growatt.shinetools.module.localbox.ustool.bean.USChargePriorityBean;
import com.growatt.shinetools.utils.BtnDelayUtil;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.Mydialog;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.view.listener.OnLvItemClickListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class USTimerSetActivity extends DemoBase implements BaseQuickAdapter.OnItemChildClickListener {
    private final String splitStr = "~";
    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    @BindView(R.id.tvRight)
    TextView mTvRight;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    /*脚部*/
    private Button btnOK;
    private List<USChargePriorityBean> mList;
    private USTimerAdapter mAdapter;
    private final int mTimeNum = 10;
    private String[][] isEnbles;
    private DialogFragment dialogFragment;

    //读取命令功能码（功能码，开始寄存器，结束寄存器）
    private int[][] funs;

    private int mIndex = -1;
    private int[][] nowSet = {
            {0x10, 3125, 3125}
    };
    private int[][] registerValues = {
            {0}//头部数据
            , {0, 0}//时间段数据
    };
    private String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ustimer_set);
        ButterKnife.bind(this);
        initIntent();
        //读取命令功能码（功能码，开始寄存器，结束寄存器）
        funs = new int[][]{
                {3, 3125, 3249}//优先时间
        };
        mTvRight.setText(R.string.m370读取);
        mTvTitle.setText(mTitle);
        isEnbles = new String[][]{
//                {getString(R.string.m208负载优先), getString(R.string.m209电池优先),getString(R.string.m210电网优先)}
                {getString(R.string.m208负载优先) + "(0)", getString(R.string.m209电池优先) + "(1)", getString(R.string.m210电网优先) + "(2)", getString(R.string.m防逆流) + "(3)"}
                , {getString(R.string.m89禁止) + "(0)", getString(R.string.m88使能) + "(1)"}
                , {getString(R.string.季度) + "1", getString(R.string.季度) + "2", getString(R.string.季度) + "3", getString(R.string.季度) + "4", getString(R.string.特殊日) + "1", getString(R.string.特殊日) + "2",}
                , {getString(R.string.周内) + "(0)", getString(R.string.周末) + "(1)", getString(R.string.week) + "(2)"}
        };
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mList = new ArrayList<>();
        mAdapter = new USTimerAdapter(mList);
        mRecyclerView.setAdapter(mAdapter);
        /*脚部*/
        View footerView = LayoutInflater.from(this).inflate(R.layout.footer_tlxhcharge_priority, (ViewGroup) mRecyclerView.getParent(), false);
        btnOK = footerView.findViewById(R.id.btnOK);
        mAdapter.addFooterView(footerView);
        mAdapter.setOnItemChildClickListener(this);
        btnOK.setOnClickListener(this::onViewClicked);
        initData(null);
    }

    private void initIntent() {
        Intent mIntent = getIntent();
        if (mIntent != null) {
            mTitle = mIntent.getStringExtra("title");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void initData(List<USChargePriorityBean> newList) {
        if (newList == null || newList.size() == 0) {
            newList = new ArrayList<>();
            for (int i = 0; i < mTimeNum; i++) {
                USChargePriorityBean bean = new USChargePriorityBean();
                if (i > 0) {
                    bean.setTimeNum(i);
                    bean.setIsEnableA(isEnbles[0][0]);
                    bean.setIsEnableB(isEnbles[1][1]);
                    bean.setIsEnableWeek(isEnbles[3][0]);
                    bean.setIsEnableAIndex(0);
                    bean.setIsEnableBIndex(1);
                    bean.setIsEnableWeekIndex(0);
                    bean.setTimePeriod("00:00" + splitStr + "00:00");
                    bean.setItemType(0);
                } else {
                    bean.setItemType(1);
                    bean.setIsEnableC(isEnbles[2][0]);
                    bean.setIsEnableCIndex(0);
                    bean.setIsEnableB(isEnbles[1][1]);
                    bean.setIsEnableBIndex(1);
                    bean.setStartTimeNote(getString(R.string.开始));
                    bean.setEndTimeNote(getString(R.string.结束));
                }
                newList.add(bean);
            }
        }
        mAdapter.replaceData(newList);
    }

    @OnClick({R.id.ivLeft, R.id.tvRight})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivLeft:
                finish();
                break;
            case R.id.tvRight:
                readRegisterValue();
                break;
            case R.id.btnOK:
                writeRegisterValue();
                break;
        }
    }

    /**
     * 写寄存器值
     */
    private void writeRegisterValue() {
        mIndex = -1;
        //获取第一个item
        USChargePriorityBean item0 = mAdapter.getItem(0);
        int isEnableCIndex = item0.getIsEnableCIndex();

        for (int i = 0; i < mAdapter.getData().size(); i++) {
            USChargePriorityBean item = mAdapter.getItem(i);
            if (item.isCheck()) {
                mIndex = i;
                if (i == 0) {
                    //判断空值并设置寄存器值
                    String startTime = item.getStartTime();
                    String endTime = item.getEndTime();
                    if (TextUtils.isEmpty(startTime)
                            || TextUtils.isEmpty(endTime)
                    ) {
                        toast(R.string.all_blank);
                        return;
                    }
                    int value = 0;
                    int startV = Integer.parseInt(startTime);
                    int endV = Integer.parseInt(endTime);
                    int enableB = item.getIsEnableBIndex();
                    if (isEnableCIndex < 4) {
                        value = (startV & 0xF) | ((endV & 0xF) << 4) | ((enableB & 1) << 8);
                    } else {
                        value = (endV & 0xFF) | ((startV & 0b1111111) << 8) | ((enableB & 1) << 15);
                    }
                    registerValues[0][0] = value;

                    //设置起始寄存器
                    int registPos = -1;
                    if (isEnableCIndex < 4) {
                        registPos = 3125 + isEnableCIndex;
                    } else if (isEnableCIndex == 4) {
                        registPos = 3201;
                    } else if (isEnableCIndex == 5) {
                        registPos = 3220;
                    }
                    nowSet[0][1] = nowSet[0][2] = registPos;
                } else {
                    //判断并设置寄存器值
                    int value1 = 0;
                    int value2 = 0;
                    String timePeriod = item.getTimePeriod();
                    String[] split = timePeriod.split(splitStr);
                    String[] startTime = split[0].split(":");
                    String[] endTime = split[1].split(":");
                    int enableA = item.getIsEnableAIndex();
                    int enableB = item.getIsEnableBIndex();
                    int enableW = item.getIsEnableWeekIndex();
                    int startHour = Integer.parseInt(startTime[0]);
                    int startMin = Integer.parseInt(startTime[1]);
                    int endHour = Integer.parseInt(endTime[0]);
                    int endMin = Integer.parseInt(endTime[1]);
                    value1 = (startMin & 0b01111111) | ((startHour & 0b00011111) << 7) | ((enableA & 0b111) << 12) | ((enableB & 1) << 15);
                    value2 = (endMin & 0b01111111) | ((endHour & 0b00011111) << 7);
                    if (isEnableCIndex < 4) {
                        value2 = value2 | ((enableW & 0b11) << 12);
                    }
                    registerValues[1][0] = value1;
                    registerValues[1][1] = value2;
                    //设置起始寄存器
                    int registPos = -1;
                    if (isEnableCIndex < 4) {
                        registPos = 3129 + isEnableCIndex * 18 + (i - 1) * 2;
                    } else if (isEnableCIndex == 4) {
                        registPos = 3202 + (i - 1) * 2;
                    } else if (isEnableCIndex == 5) {
                        registPos = 3221 + (i - 1) * 2;
                    }
                    nowSet[0][1] = registPos;
                    nowSet[0][2] = registPos + 1;
                }
                break;
            }
        }
        if (mIndex == -1) {
            toast(R.string.m257请选择设置值);
            return;
        }
        //写寄存器
        connectServerWrite();
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
    private int count = 0;
    Handler mHandlerW = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessageWrite(this);
                    sendBytes = SocketClientUtil.sendMsgToServer10(mClientUtilW, nowSet[0], registerValues[mIndex == 0 ? 0 : 1]);
                    LogUtil.i("发送写入：" + SocketClientUtil.bytesToHexString(sendBytes));
                    break;
                //接收字节数组
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //检测内容正确性
                        boolean isCheck = MaxUtil.checkReceiverFull10(bytes);
                        if (isCheck) {
                            toast(R.string.all_success);
                        } else {
                            toast(R.string.all_failed);
                        }
                        LogUtil.i("接收写入：" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //关闭连接
                        SocketClientUtil.close(mClientUtilW);
                        Mydialog.Dismiss();
                    }
                    break;
                default:
                    BtnDelayUtil.dealTLXBtnWrite(this, what, mContext, mTvRight);
                    break;
            }
        }
    };

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (adapter == mAdapter) {
            switch (view.getId()) {
                case R.id.tvSelect1:
                    showDialog(mAdapter, position, 2);
                    break;
                case R.id.tvSelect2:
                    showDialog(mAdapter, position, 1);
                    break;
//                case R.id.tvStart:
//                case R.id.tvEnd:
//                    showTimePickView(view,position);
//                    break;
                case R.id.tvWeek:
                    showDialog(mAdapter, position, 3);
                    break;
                case R.id.tvTimer:
                    EventBus.getDefault().postSticky(mAdapter.getItem(position));
                    jumpTo(TimerSetActivity.class, false);
                    break;
                case R.id.tvEnablea:
                    showDialog(mAdapter, position, 0);
                    break;
                case R.id.tvEnableb:
                    showDialog(mAdapter, position, 1);
                    break;
                case R.id.tvCheck:
                    USChargePriorityBean item = mAdapter.getItem(position);
                    if (!item.isCheck()) {
                        for (USChargePriorityBean bean : mAdapter.getData()) {
                            bean.setCheck(false);
                        }
                    }
                    item.setCheck(!item.isCheck());
                    mHandler.post(() -> mAdapter.notifyDataSetChanged());
                    break;
            }
        }
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
                    byte[] sendBytesR = sendMsg(mClientUtil, funs[0]);
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
                            refreshUI(bs);
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
                        BtnDelayUtil.refreshFinish();
                    }
                    break;
                default:
                    BtnDelayUtil.dealTLXBtn(this, what, mContext, mTvRight);
                    break;
            }
        }
    };

    /**
     * 根据读取值刷新UI
     *
     * @param bs i代表寄存器位置  其实寄存器3038
     *           3038寄存器
     *           bit0~7:分钟；
     *           bit8~12:小时；
     *           bit13~14,0:不强制;
     *           1:充电；
     *           2:放电；
     *           bit15,0:禁止; 1:使能;
     *           3039寄存器
     *           bit0~7:分钟；
     *           bit8~12:小时；
     *           bit13~15：预留；
     */
    private void refreshUI(byte[] bs) {
        //先判断选择的相关季度
        USChargePriorityBean bean = mAdapter.getItem(0);
        int isEnableCIndex = bean.getIsEnableCIndex();
        //设置第一个item数据 季度对应寄存器下标
        int startTime = 0;
        int endTime = 0;
        int isEnableB = 1;
        if (isEnableCIndex < 4) {//季度1-4
            int enableCRegistValue = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes125(bs, isEnableCIndex + 3125, 0, 1));
            startTime = enableCRegistValue & 0xF;
            endTime = enableCRegistValue >>> 4 & 0xF;
            isEnableB = enableCRegistValue >>> 8 & 1;
        } else if (isEnableCIndex == 4) {//特殊日1
            int enableCRegistValue = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3201, 0, 1));
            endTime = enableCRegistValue & 0xFF;
            startTime = enableCRegistValue >>> 8 & 0b01111111;
            isEnableB = enableCRegistValue >>> 15 & 1;
        } else if (isEnableCIndex == 5) {//特殊日2
            int enableCRegistValue = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3220, 0, 1));
            endTime = enableCRegistValue & 0xFF;
            startTime = enableCRegistValue >>> 8 & 0b01111111;
            isEnableB = enableCRegistValue >>> 15 & 1;
        }
        bean.setStartTime(String.valueOf(startTime));
        bean.setEndTime(String.valueOf(endTime));
        bean.setIsEnableBIndex(isEnableB);
        bean.setIsEnableB(isEnbles[1][isEnableB]);


        //设置其他item数据
        int timerStartR = 3129;//时间段起始寄存器
        if (isEnableCIndex < 4) {
            timerStartR = 3129 + isEnableCIndex * 18;
        } else if (isEnableCIndex == 4) {
            timerStartR = 3202;
        } else if (isEnableCIndex == 5) {
            timerStartR = 3221;
        }
        for (int i = 0; i < mTimeNum - 1; i++) {
            USChargePriorityBean item = mAdapter.getItem(i + 1);
            int value1 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes125(bs, timerStartR + i * 2, 0, 1));
            int value2 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes125(bs, timerStartR + i * 2 + 1, 0, 1));

            int startMin = value1 & 0b1111111;
            int startHour = value1 >>> 7 & 0b11111;
            int endMin = value2 & 0b1111111;
            int endHour = value2 >>> 7 & 0b11111;
            int enableAIndex = value1 >>> 12 & 0b111;
            int enableBIndex = value1 >>> 15 & 0b1;
            int enableWIndex = value2 >>> 12 & 0b11;
            item.setTimePeriodRead(String.format("%02d:%02d%s%02d:%02d", startHour, startMin, splitStr, endHour, endMin));
            item.setIsEnableAIndex(enableAIndex);
            item.setIsEnableA(isEnbles[0][enableAIndex]);

            item.setIsEnableBIndex(enableBIndex);
            item.setIsEnableB(isEnbles[1][enableBIndex]);

            item.setIsEnableWeekIndex(enableWIndex);
            item.setIsEnableWeek(isEnbles[3][enableWIndex]);
        }
        mAdapter.notifyDataSetChanged();

    }

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
            connectServer();
            return null;
        }
    }

    /**
     * @param mAdapter
     * @param type     0 代表 限制或不限制  1 代表禁止或使能；2代表季度选择;3:是否周末或周内
     */
    public void showDialog(@NonNull USTimerAdapter mAdapter, int position, int type) {
        USChargePriorityBean item = mAdapter.getItem(position);
        new CircleDialog.Builder()
                .setTitle(getString(R.string.m225请选择))
                .setGravity(Gravity.CENTER)
                .setWidth(0.7f)
                .setItems(isEnbles[type], new OnLvItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                        switch (type) {
                            case 0:
                                item.setIsEnableA(isEnbles[type][pos]);
                                item.setIsEnableAIndex(pos);
                                break;
                            case 1:
                                item.setIsEnableB(isEnbles[type][pos]);
                                item.setIsEnableBIndex(pos);
                                break;
                            case 2:
                                USChargePriorityBean item0 = mAdapter.getItem(0);
                                if (item0.getIsEnableCIndex() != pos) {
                                    item.setIsEnableC(isEnbles[type][pos]);
                                    item.setIsEnableCIndex(pos);
                                    mAdapter.setShowWeek(pos < 4);
                                    item0.setStartTime("");
                                    item0.setEndTime("");
                                    if (pos < 4) {
                                        item0.setStartTimeNote(getString(R.string.开始));
                                        item0.setEndTimeNote(getString(R.string.结束));
                                    } else {
                                        item0.setStartTimeNote(getString(R.string.all_time_month));
                                        item0.setEndTimeNote(getString(R.string.all_time_day));
                                    }
                                    //清除数据，设置其他数据为默认值
                                    item.setIsEnableBIndex(1);
                                    item.setIsEnableB(isEnbles[1][1]);
                                    for (int i = 0; i < mTimeNum; i++) {
                                        if (i > 0) {
                                            USChargePriorityBean bean = mAdapter.getItem(i);
                                            bean.setIsEnableA(isEnbles[0][0]);
                                            bean.setIsEnableB(isEnbles[1][1]);
                                            bean.setIsEnableWeek(isEnbles[3][0]);
                                            bean.setIsEnableAIndex(0);
                                            bean.setIsEnableBIndex(1);
                                            bean.setIsEnableWeekIndex(0);
                                            bean.setTimePeriod("00:00" + splitStr + "00:00");
                                        }
                                    }
                                    mAdapter.notifyDataSetChanged();
                                }


                                break;
                            case 3:
                                item.setIsEnableWeek(isEnbles[type][pos]);
                                item.setIsEnableWeekIndex(pos);
                                break;
                        }
                        mAdapter.notifyDataSetChanged();
                        return true;
                    }
                })
                .setNegative(getString(R.string.all_no), null)
                .show(getSupportFragmentManager());
    }


}
