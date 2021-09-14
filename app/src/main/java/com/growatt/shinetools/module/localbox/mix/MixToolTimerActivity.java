package com.growatt.shinetools.module.localbox.mix;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.growatt.shinetools.R;
import com.growatt.shinetools.adapter.MixToolTimerAdapter;
import com.growatt.shinetools.base.DemoBase;
import com.growatt.shinetools.bean.TLXHChargePriorityBean;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.MaxWifiParseUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.module.TimerSetActivity;
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

public class MixToolTimerActivity extends DemoBase implements BaseQuickAdapter.OnItemChildClickListener {
    private final String splitStr = "~";
    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    @BindView(R.id.tvRight)
    TextView mTvRight;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    /*脚部*/
    private Button btnOK;
    private List<TLXHChargePriorityBean> mList;
    private MixToolTimerAdapter mAdapter;
    private final int mTimeNum = 9;
    private String[][] isEnbles;
    /**
     * 标题
     */
    private String mTitle;
    //读取命令功能码（功能码，开始寄存器，结束寄存器）
    private int[][] funs;
    private int mType = 0;
    //设置功能码 + 起始寄存器
//    private int[][][] nowSet = {
//            {{0x06, 3038, -1},{0x06,3039,-1}}
//            ,{{0x06, 3040, -1},{0x06,3041,-1}}
//            ,{{0x06, 3042, -1},{0x06,3043,-1}}
//            ,{{0x06, 3044, -1},{0x06,3045,-1}}
//            ,{{0x06, 3050, -1},{0x06,3051,-1}}
//            ,{{0x06, 3052, -1},{0x06,3053,-1}}
//            ,{{0x06, 3054, -1},{0x06,3055,-1}}
//            ,{{0x06, 3056, -1},{0x06,3057,-1}}
//            ,{{0x06, 3058, -1},{0x06,3059,-1}}
////            {0x06, 3040, 3041}
////            ,{0x06, 3042, 3043},{0x06, 3044, 3045}
////            ,{0x06, 3050, 3051},{0x06, 3052, 3053}
////            ,{0x06, 3054, 3055},{0x06, 3056, 3057}
////            ,{0x06, 3058, 3059}
//    };
    /**
     * 10 指令
     */
    private int[][] nowSet = {
            {0x10, 1080, 1082},{0x10, 1083, 1085}
            ,{0x10, 1086, 1088},{0x10, 1100, 1102}
            ,{0x10, 1103, 1105},{0x10, 1106, 1108}
            ,{0x10, 1110, 1112},{0x10, 1113, 1115}
            ,{0x10, 1116, 1118}
    };
    private int[] registerValues = {
            0,0,0
    };
    private int mIndex = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mix_tool_timer);
        ButterKnife.bind(this);
        initIntent();
        init();
    }
    private void initIntent() {
        Intent mIntent = getIntent();
        if (mIntent != null) {
            mTitle = mIntent.getStringExtra("title");
            mType = mIntent.getIntExtra("type", 0);
        }
        if (TextUtils.isEmpty(mTitle)){
            mTitle = getString(R.string.m设置充放电优先时间段);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null){
            mAdapter.notifyDataSetChanged();
        }
    }

    private void init() {
        //读取命令功能码（功能码，开始寄存器，结束寄存器）
        funs = new int[][]{
                {3, 1080, 1118}//优先时间
        };
        mTvTitle.setText(mTitle);
        mTvRight.setText(R.string.m370读取);
        isEnbles = new String[][]{
//                {getString(R.string.m208负载优先), getString(R.string.m209电池优先),getString(R.string.m210电网优先)}
                {getString(R.string.m210电网优先), getString(R.string.m209电池优先),getString(R.string.m208负载优先)}
                , {getString(R.string.m89禁止), getString(R.string.m88使能)}
        };
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mList = new ArrayList<>();
        mAdapter = new MixToolTimerAdapter(mList);
        mRecyclerView.setAdapter(mAdapter);
        /*脚部*/
        View footerView = LayoutInflater.from(this).inflate(R.layout.footer_tlxhcharge_priority, (ViewGroup) mRecyclerView.getParent(), false);
        btnOK = footerView.findViewById(R.id.btnOK);
        mAdapter.addFooterView(footerView);
        mAdapter.setOnItemChildClickListener(this);
        btnOK.setOnClickListener(this::onViewClicked);
        initData(null);
    }

    private void initData(List<TLXHChargePriorityBean> newList) {
        if (newList == null || newList.size() == 0) {
            newList = new ArrayList<>();
            for (int i = 0; i < mTimeNum; i++) {
                TLXHChargePriorityBean bean = new TLXHChargePriorityBean();
                bean.setTimeNum(i%isEnbles[0].length+ 1);
                bean.setItemTitle(isEnbles[0][i/isEnbles[0].length]);
                if (i%isEnbles[0].length == 0){
                    bean.setItemType(1);
                }
                bean.setIsEnableB(isEnbles[1][1]);
                bean.setIsEnableBIndex(1);
                bean.setTimePeriod("00:00"+splitStr+"00:00");
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
                //读取寄存器的值
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
        for (int i = 0; i < mAdapter.getData().size(); i++) {
            TLXHChargePriorityBean item = mAdapter.getItem(i);
            if (item.isCheck()){
                mIndex = i;
                String timePeriod = item.getTimePeriod();
                String[] split = timePeriod.split(splitStr);
                String[] startTime = split[0].split(":");
                String[] endTime = split[1].split(":");
                int startHour = Integer.parseInt(startTime[0]);
                int startMin = Integer.parseInt(startTime[1]);
                int endHour = Integer.parseInt(endTime[0]);
                int endMin = Integer.parseInt(endTime[1]);
//                int isEnableAIndex = item.getIsEnableAIndex();
                int isEnableBIndex = item.getIsEnableBIndex();
                //两个寄存器对应值
                int regist1 = (startMin) + (startHour << 8);
                int regist2 = (endMin) + (endHour << 8);
//                nowSet[mIndex][0][2] = regist1;
//                nowSet[mIndex][1][2] = regist2;
                registerValues[0] = regist1;
                registerValues[1] = regist2;
                registerValues[2] = isEnableBIndex;
                break;
            }
        }
        if (mIndex == -1){
            toast(R.string.m257请选择设置值);
            return;
        }
//        //判空 + 赋值
//        for (int i = 0,index = 0; i < mTimeNum; i++,index = i*2) {
//            TLXHChargePriorityBean item = mAdapter.getItem(i);
//            String timePeriod = item.getTimePeriod();
//            if (TextUtils.isEmpty(timePeriod)) {
//                toast(R.string.putin_on_data);
//                break;
//            }
//            String[] split = timePeriod.split(splitStr);
//            String[] startTime = split[0].split(":");
//            String[] endTime = split[1].split(":");
//            int startHour = Integer.parseInt(startTime[0]);
//            int startMin = Integer.parseInt(startTime[1]);
//            int endHour = Integer.parseInt(endTime[0]);
//            int endMin = Integer.parseInt(endTime[1]);
//            int isEnableAIndex = item.getIsEnableAIndex();
//            int isEnableBIndex = item.getIsEnableBIndex();
//            //两个寄存器对应值
////            int regist1 = (startMin << 8) + (startHour << 3) + (isEnableAIndex << 1) + isEnableBIndex;
////            int regist2 = (endMin << 8) + (endHour << 3);
//            int regist1 = (startMin) + (startHour << 8) + (isEnableAIndex << 13) + isEnableBIndex<<15;
//            int regist2 = (endMin) + (endHour << 8);
//            if (i < 4){
//                registerValues[0][index] = regist1;
//                registerValues[0][index+1] = regist2;
//            }else {
//                registerValues[1][index-8] = regist1;
//                registerValues[1][index-8+1] = regist2;
//            }
//        }
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
                    sendBytes = SocketClientUtil.sendMsgToServer10(mClientUtilW, nowSet[mIndex], registerValues);
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
                    }finally {
                        //关闭连接
                        SocketClientUtil.close(mClientUtilW);
                        Mydialog.Dismiss();
                    }
                    break;
                default:
                    BtnDelayUtil.dealTLXBtnWrite(this,what,mContext,mTvRight);
                    break;
            }
        }
    };
    //    Handler mHandlerW = new Handler(Looper.getMainLooper()) {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            int what = msg.what;
//            switch (what) {
//                //发送信息
//                case SocketClientUtil.SOCKET_SEND:
//                    BtnDelayUtil.sendMessageWrite(this);
//                    if (count < nowSet[mIndex].length) {
//                        sendBytes = SocketClientUtil.sendMsgToServer(mClientUtilW, nowSet[mIndex][count]);
//                        LogUtil.i("发送写入：" + SocketClientUtil.bytesToHexString(sendBytes));
//                    }
//                    break;
//                //接收字节数组
//                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
//                    BtnDelayUtil.receiveMessage(this);
//                    try {
//                        byte[] bytes = (byte[]) msg.obj;
//                        //检测内容正确性
//                        boolean isCheck = MaxUtil.checkReceiverFull(bytes);
//                        if (isCheck) {
//                            toast(R.string.all_success);
//                        } else {
//                            toast(R.string.all_failed);
//                        }
//                        if (count < nowSet[mIndex].length - 1) {
//                            count++;
//                            this.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
//                        } else {
//                            count = 0;
//                            //关闭连接
//                            SocketClientUtil.close(mClientUtilW);
//                        }
//                        LogUtil.i("接收写入：" + SocketClientUtil.bytesToHexString(bytes));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        count = 0;
//                        //关闭连接
//                        SocketClientUtil.close(mClientUtilW);
//                        Mydialog.Dismiss();
//                    }
//                    break;
//                default:
//                    BtnDelayUtil.dealTLXBtnWrite(this,what,mContext,mTvRight);
//                    break;
//            }
//        }
//    };
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
                    BtnDelayUtil.dealTLXBtn(this,what,mContext,mTvRight);
                    break;
            }
        }
    };

    /**
     * 根据读取值刷新UI
     * @param bs
     * i代表寄存器位置  其实寄存器3038
     * 3038寄存器
    bit0~7:分钟；
    bit8~12:小时；
    bit13~14,0:不强制;
    1:充电；
    2:放电；
    bit15,0:禁止; 1:使能;
    3039寄存器
    bit0~7:分钟；
    bit8~12:小时；
    bit13~15：预留；
     */
    private void refreshUI(byte[] bs) {
        for (int pos=0,i=0;pos<mTimeNum;pos++){
            if (pos/3 == 0){
                i = pos*3;
            }
            if (pos/3 == 1){
                i = pos*3 + 11;
            }
            if (pos/3 == 2){
                i = pos*3 + 12;
            }
            TLXHChargePriorityBean item = mAdapter.getItem(pos);
            //两个寄存器值
            byte[] register1 = MaxWifiParseUtil.subBytes(bs, i, 0, 1);
            byte[] register2 = MaxWifiParseUtil.subBytes(bs, i+1, 0, 1);
            byte[] register3 = MaxWifiParseUtil.subBytes(bs, i+2, 0, 1);
            //获取第一个寄存器的低8位 开始分钟
            int startMin = MaxWifiParseUtil.obtainValueOne(register1) & 0b0000000011111111;
            //获取第一个寄存器高8位 开始小时
            int startHour = MaxWifiParseUtil.obtainValueOne(register1)>>8;
            //使能
            int enableB = MaxWifiParseUtil.obtainValueOne(register3);
            //获取第二个寄存器的低8位 end分钟
            int endMin = MaxWifiParseUtil.obtainValueOne(register2) & 0b0000000011111111;
            //获取第二个寄存器高八位 end 小时
            int endHour = MaxWifiParseUtil.obtainValueOne(register2)>>8;
            //赋值操作
            item.setTimePeriodRead(String.format("%02d:%02d%s%02d:%02d",startHour,startMin,splitStr,endHour,endMin));
            item.setIsEnableBIndexRead(enableB);
//            item.setIsEnableARead(isEnbles[0][pos/isEnbles[0].length]);
            item.setIsEnableBRead(isEnbles[1][enableB]);
            item.setIsEnableB(item.getIsEnableBRead());
        }
        mAdapter.notifyDataSetChanged();
    }
//    private void refreshUI(byte[] bs) {
//        //时间1
//        TLXHChargePriorityBean item = mAdapter.getItem(0);
//        byte[] register01 = MaxWifiParseUtil.subBytes(bs, 0, 0, 1);
//        byte[] register02 = MaxWifiParseUtil.subBytes(bs, 1, 0, 1);
//        byte[] register03 = MaxWifiParseUtil.subBytes(bs, 2, 0, 1);
//        //获取第一个寄存器的高低8位 start HH：mm
//        int register01H = MaxWifiParseUtil.obtainRegistValueHOrL(1,register01);
//        int register01L = MaxWifiParseUtil.obtainRegistValueHOrL(0,register01);
//        //获取第2个寄存器的高低8位 stop HH：mm
//        int register02H = MaxWifiParseUtil.obtainRegistValueHOrL(1,register02);
//        int register02L = MaxWifiParseUtil.obtainRegistValueHOrL(0,register02);
//        mAdapter.notifyDataSetChanged();
//    }

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
    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (adapter == mAdapter) {
            switch (view.getId()) {
                case R.id.tvTimer:
                    EventBus.getDefault().postSticky(mAdapter.getItem(position));
                    jumpTo(TimerSetActivity.class,false);
                    break;
                case R.id.tvEnablea:
//                    showDialog(mAdapter, position, 0);
                    break;
                case R.id.tvEnableb:
                    showDialog(mAdapter, position, 1);
                    break;
                case R.id.tvCheck:
                    TLXHChargePriorityBean item = mAdapter.getItem(position);
                    if (!item.isCheck()){
                        for (TLXHChargePriorityBean bean : mAdapter.getData()) {
                            bean.setCheck(false);
                        }
                    }
                    item.setCheck(!item.isCheck());
                    mHandler.post(() -> mAdapter.notifyDataSetChanged());
                    break;
            }
        }
    }

    /**
     * @param mAdapter
     * @param type     0 代表 限制或不限制  1 代表禁止或使能
     */
    public void showDialog(@NonNull MixToolTimerAdapter mAdapter, int position, int type) {
        TLXHChargePriorityBean item = mAdapter.getItem(position);
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
                        }
                        mAdapter.notifyDataSetChanged();
                        return true;
                    }
                })
                .setNegative(getString(R.string.all_no), null)
                .show(getSupportFragmentManager());
    }
}
