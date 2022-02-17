package com.growatt.shinetools.module.localbox.tlxh;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.growatt.shinetools.R;
import com.growatt.shinetools.adapter.TLXHToolAutoTestAdapter;
import com.growatt.shinetools.base.DemoBase;
import com.growatt.shinetools.modbusbox.Arith;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.MaxWifiParseUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.modbusbox.bean.MaxDataBean;
import com.growatt.shinetools.modbusbox.bean.MaxDataDeviceBean;
import com.growatt.shinetools.module.localbox.tlxh.bean.TLXHAutoTestRegistBean;
import com.growatt.shinetools.module.localbox.tlxh.bean.TLXHAutoTestReportBean;
import com.growatt.shinetools.module.localbox.tlxh.bean.TLXHToolAutoTestBean;
import com.growatt.shinetools.utils.BtnDelayUtil;
import com.growatt.shinetools.utils.ChartUtils;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.MyControl;
import com.growatt.shinetools.utils.Mydialog;
import com.growatt.shinetools.utils.OssUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TLXHAutoTestOldInvActivity extends DemoBase {

    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.btnOK)
    Button mBtnOK;
    private TLXHToolAutoTestAdapter mAdapter;
    private List<TLXHToolAutoTestBean> mList;
    private String[] titles;
    private int mType = -1;
    //读取命令功能码（功能码，开始寄存器，结束寄存器）
    private int[][] funs;
    private int[][] funsProcess;
    private int[] nowSet = new int[3];
    private int[] nowReadSn = {3,0,124};
    //测试步骤和测试状态值
    private String[] stepStrs;
    private String[] inputPointUnit;
    private float[] inputPointMuilt;
    private int[] inputPointScale;
    private String[] statusStrs;
    private TLXHAutoTestRegistBean mRegistBean;
    private TLXHAutoTestReportBean mReportBean;
    private boolean isFirst = true;
    private MaxDataBean mMaxDataBean = new MaxDataBean();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tlxhauto_test);
        ButterKnife.bind(this);
//        ModbusUtil.setComAddressOldInv();
        initString();
        mTvTitle.setText(R.string.m自动测试);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mList = new ArrayList<>();
        mAdapter = new TLXHToolAutoTestAdapter(R.layout.item_tlxh_tool_autotest, mList);
        mRecyclerView.setAdapter(mAdapter);
        List<TLXHToolAutoTestBean> newList = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            TLXHToolAutoTestBean bean = new TLXHToolAutoTestBean();
            bean.setTitle(titles[i]);
            bean.setContent("");
            newList.add(bean);
        }
        mAdapter.replaceData(newList);
    }

    private void initString() {
//        stepStrs = new String[]{
//                "",
//                getString(R.string.一阶高压),
//                getString(R.string.一阶低压),
//                getString(R.string.一阶高频),
//                getString(R.string.一阶低频),
//                getString(R.string.二阶高压),
//                getString(R.string.二阶低压),
//                getString(R.string.二阶高频),
//                getString(R.string.二阶低频)
//        };
        stepStrs = new String[]{
                "",
                getString(R.string.二阶高压),
                getString(R.string.一阶低压),
                getString(R.string.二阶高频),
                getString(R.string.二阶低频),
                getString(R.string.一阶高压),
                getString(R.string.二阶低压),
                getString(R.string.一阶高频),
                getString(R.string.一阶低频)
        };
        statusStrs = new String[]{
                getString(R.string.开始),
                getString(R.string.测试中),
                getString(R.string.步骤通过),
                getString(R.string.步骤停止),
                getString(R.string.步骤失败),
                getString(R.string.全部步骤通过)
        };
        inputPointUnit = new String[]{
                "",
                "V","V","Hz","Hz", "V","V","Hz","Hz"
        };
        inputPointMuilt = new float[]{
                1,
                0.1f,0.1f,0.01f,0.01f,0.1f,0.1f,0.01f,0.01f
        };
        inputPointScale = new int[]{
                1,
                1,1,2,2,1,1,2,2
        };
        titles = new String[]{
                getString(R.string.m测试进程), getString(R.string.m测试状态), getString(R.string.m459序列号), getString(R.string.inverterdevicedata_pattern),
                getString(R.string.inverterdevicedata_property), getString(R.string.m保护限值), getString(R.string.m测试数据), getString(R.string.m触发结果)
        };
        //读取命令功能码（功能码，开始寄存器，结束寄存器）
        funs = new int[][]{
                {3,0,44},
                {3,45,89},
                {3,180,224},
        };
        funsProcess = new int[][]{
                {4,135,179}
        };
        nowSet = new int[3];
        nowSet[0] = 6;
        nowSet[1] = 34;
        nowSet[2] = 1;
    }

    @OnClick({R.id.ivLeft, R.id.tvRight, R.id.btnOK})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivLeft:
                finish();
                break;
            case R.id.tvRight:
                SocketClientUtil.close(mClientUtilRead);
                readRegisterValue();
                Mydialog.Dismiss();
//                EventBus.getDefault().postSticky(mReportBean);
//                jumpTo(TLXHToolAutoTestReportActivity.class,false);
                break;
            case R.id.btnOK:
                MyControl.circlerDialog(this,getString(R.string.自动测试中途不可停止),-1,() -> {
                    mRegistBean = new TLXHAutoTestRegistBean();
                    mReportBean = new TLXHAutoTestReportBean();
                    mReportBean.setStartDate(ChartUtils.getFormatDate("yyyy.MM.dd",new Date()));
                    mReportBean.setStartTime(ChartUtils.getFormatDate("HH:mm:ss",new Date()));
                    writeRegisterValue();
                });
                break;
        }
    }

    public void setBtnEnable(boolean isFlag){
        mBtnOK.setEnabled(isFlag);
    }
    //连接对象:用于读取数据
    private SocketClientUtil mClientUtilRead;

    //读取寄存器的值
    private void readRegisterValue() {
        Mydialog.Show(mContext);
        setBtnEnable(false);
        mClientUtilRead = SocketClientUtil.connectServer(mHandlerRead);
    }

    //连接对象:用于设置数据
    private SocketClientUtil mClientUtilWriter;

    //设置寄存器的值
    private void writeRegisterValue() {
        Mydialog.Show(mContext);
        mClientUtilWriter = SocketClientUtil.connectServer(mHandlerWrite);
    }

    /**
     * 写寄存器handle
     */
    //当前发送的字节数组
    private byte[] sendBytes;
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
                        sendBytes = SocketClientUtil.sendMsgToServerOldInv(mClientUtilWriter, nowSet);
                        LogUtil.i("发送写入：" + SocketClientUtil.bytesToHexString(sendBytes));
                    } else {
                        toast(R.string.dataloggers_add_no_server);
                    }
                    break;
                //接收字节数组
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //检测内容正确性
                        boolean isCheck = MaxUtil.isCheckFull(mContext, bytes);
                        //关闭tcp连接
                        SocketClientUtil.close(mClientUtilWriter);
                        BtnDelayUtil.refreshFinish();
                        if (isCheck){
                            //开始读取
                            writeRegisterValueSN();
                        }else {
                            toast(R.string.m244设置失败);
                        }
                        LogUtil.i("接收写入：" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        //关闭tcp连接
                        SocketClientUtil.close(mClientUtilWriter);
                        BtnDelayUtil.refreshFinish();
                    }
                    break;
                default:
                    BtnDelayUtil.dealMaxBtnWrite(this, what, mContext, mBtnOK);
                    break;
            }
        }
    };
    //连接对象:用于设置数据
    private SocketClientUtil mClientUtilWriterSN;

    //设置寄存器的值
    private void writeRegisterValueSN() {
        Mydialog.Show(mContext);
        mClientUtilWriterSN = SocketClientUtil.connectServer(mHandlerWriteSN);
    }

    /**
     * 写寄存器handle
     */
    //当前发送的字节数组
    private byte[] sendBytesSN;
    private int nowReadPos;
    Handler mHandlerWriteSN = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessageWrite(this);
                    if (nowReadPos < funs.length){
                        sendBytesSN = SocketClientUtil.sendMsgToServerOldInv(mClientUtilWriterSN, funs[nowReadPos]);
                        LogUtil.i("发送读取：" + SocketClientUtil.bytesToHexString(sendBytesSN));
                    }
                    break;
                //接收字节数组
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //检测内容正确性
                        boolean isCheck = ModbusUtil.checkModbus(bytes);
                        if (isCheck){
                            switch (nowReadPos){
                                case 0:
                                    RegisterParseUtil.parse03Hold0T44OldInv(mMaxDataBean,bytes);
                                    break;
                                case 1:
                                    RegisterParseUtil.parse03Hold45T89OldInv(mMaxDataBean,bytes);
                                    break;
                                case 2:
                                    RegisterParseUtil.parseHold180T224OldInv(mMaxDataBean,bytes);
                                    //接收正确，开始解析
//                                    RegisterParseUtil.parseHold0T124(mMaxDataBean, bytes);
                                    MaxDataDeviceBean deviceBeen = mMaxDataBean.getDeviceBeen();
                                    StringBuilder sbFirm = new StringBuilder()
                                            .append("(")
                                            .append(deviceBeen.getFirmVersionOut())
                                            .append(")")
                                            .append(deviceBeen.getFirmVersionIn());
                                    mReportBean.setDeviceSn(deviceBeen.getSn());
                                    mReportBean.setVersion(sbFirm.toString());
                                    int model = deviceBeen.getModel();
                                    mReportBean.setModel(MaxUtil.getDeviceModel(model));
                                    break;
                            }
                        }
                        if (nowReadPos < funs.length-1) {
                            nowReadPos++;
                            sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        }else {
                            //关闭连接
                            nowReadPos = 0;
                            SocketClientUtil.close(mClientUtilWriterSN);
                            BtnDelayUtil.refreshFinish();
                            //开始读取
                            readRegisterValue();
                        }
                        LogUtil.i("接收写入：" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        nowReadPos = 0;
                        //关闭tcp连接
                        SocketClientUtil.close(mClientUtilWriterSN);
                        BtnDelayUtil.refreshFinish();
                    }
                    break;
//                default:
//                    BtnDelayUtil.dealMaxBtnWrite(this, what, mContext, mBtnOK);
//                    break;
            }
        }
    };
    private int count;
    Handler mHandlerRead = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessage(this);
                    byte[] sendBytesR = SocketClientUtil.sendMsgToServerOldInv(mClientUtilRead, funsProcess[0]);
                    LogUtil.i("发送读取：" + SocketClientUtil.bytesToHexString(sendBytesR));
                    break;
                //接收字节数组
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        LogUtil.i("接收消息:" + SocketClientUtil.bytesToHexString(bytes));
                        //检测内容正确性
                        boolean isCheck = ModbusUtil.checkModbus(bytes);
                        //先判断，再次读取
                        this.sendEmptyMessageDelayed(SocketClientUtil.SOCKET_SEND,800);
                        if (isCheck) {
                            //移除外部协议
                            byte[] bs = RegisterParseUtil.removePro17(bytes);
                            mRegistBean.setInput276(MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes45(bs,138,0,1)));
//                            mRegistBean.setInput277(MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes45(bs,135,0,1)));
                            mRegistBean.setInput277(MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes45(bs,135,0,1)) & 0x00ff);
                            mRegistBean.setInput278(MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes45(bs,139,0,1)));
                            mRegistBean.setInput279(MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes45(bs,140,0,1)));
                            mRegistBean.setInput280(MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes45(bs,141,0,1)));
                            mRegistBean.setInput281(MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes45(bs,142,0,1)));
                            mRegistBean.setInput282(MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes45(bs,143,0,1)));
                            mRegistBean.setInput283(MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes45(bs,144,0,1)));
                            updateUI();
                            int input276 = mRegistBean.getInput276();
                            if (input276 == 3 || input276 == 4 || input276 == 5) {
                                String note = "";
                                switch (input276) {
                                    case 3:
                                        note = getString(R.string.步骤停止);
                                        break;
                                    case 4:
                                        note = getString(R.string.步骤失败);
                                        break;
                                    case 5:
                                        note = getString(R.string.全部步骤通过);
                                        break;
                                }
                                mReportBean.setTitle(note);
                                mReportBean.setFinishTime(ChartUtils.getFormatDate("HH:mm:ss", new Date()));
                                OssUtils.circlerDialog(TLXHAutoTestOldInvActivity.this, note, -1, false, () -> {
                                    EventBus.getDefault().postSticky(mReportBean);
                                    jumpTo(TLXHToolAutoTestReportActivity.class, false);
                                });
                                //关闭连接
//                                toast("结束测试");
                                SocketClientUtil.close(mClientUtilRead);
                                Mydialog.Dismiss();
                                return;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        //关闭连接
//                        toast("异常：" + e.toString());
                        SocketClientUtil.close(mClientUtilRead);
                        Mydialog.Dismiss();
                    }
                    break;
                case SocketClientUtil.SOCKET_CLOSE:
//                    toast("Socket 关闭");
                    setBtnEnable(true);
                    this.removeMessages(SocketClientUtil.SOCKET_SEND);
                    break;
                case BtnDelayUtil.TIMEOUT_RECEIVE:
                    this.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                    break;
            }
        }
    };

    private void updateUI() {
        try {
            List<TLXHToolAutoTestBean> newList = new ArrayList<>();
            int input277 = mRegistBean.getInput277();
            int input276 = mRegistBean.getInput276();
            for (int i = 0; i < titles.length; i++) {
                TLXHToolAutoTestBean bean = new TLXHToolAutoTestBean();
                bean.setTitle(titles[i]);
                bean.setProcess(stepStrs[input277]);
                bean.setStatus(statusStrs[input276]);
                bean.setPoint(getPoint(input277));
                bean.setPointTime(getProTime(input277));
                bean.setPointValue(Arith.mul(mRegistBean.getInput282(),inputPointMuilt[input277],inputPointScale[input277]) +inputPointUnit[input277]);
                bean.setPointValueTime(mRegistBean.getInput283() +"ms");
                String content = "";
                switch (i){
                    case 0:
                        content = stepStrs[input277] + " " + statusStrs[input276];
                        break;
                    case 1:
                        content = statusStrs[input276];
                        break;
                    case 2:
                        content = mReportBean.getDeviceSn();
                        break;
                    case 3:
                        content = mReportBean.getModel();
                        break;
                    case 4:
                        content = mReportBean.getVersion();
                        break;
                    case 5:
                        content = getString(R.string.保护点) + ":" + getPoint(input277) + "  "+
                                getString(R.string.保护时间) + ":" + getProTime(input277);
                        break;
                    case 6:
                        content = getString(R.string.真实数据) + ":"+Arith.mul(mRegistBean.getInput280(),inputPointMuilt[input277],inputPointScale[input277]) +inputPointUnit[input277] + " "
                                + getString(R.string.模拟数据) + ":"+Arith.mul(mRegistBean.getInput281(),inputPointMuilt[input277],inputPointScale[input277]) +inputPointUnit[input277];
                        break;
                    case 7:
                        content = getString(R.string.触发值) + ":"+bean.getPointValue() + " "
                                + getString(R.string.保护时间) + ":"+bean.getPointValueTime();
                        break;
                }
                bean.setContent(content);
                newList.add(bean);
            }
            //判断上一个没有重复
            if (input276 == 2 || input276 == 3 || input276 == 4) {
                if (input277 > mReportBean.getList().size()) {
                    mReportBean.getList().add(newList);
                }
            }
            mAdapter.replaceData(newList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取point值
     * @param input277
     * @return
     */
    public String getPoint(int input277){
        int registValue = mRegistBean.getInput278();
//        switch (input277){
//            case 1:registValue = mRegistBean.getHold53();break;
//            case 2:registValue = mRegistBean.getHold52();break;
//            case 3:registValue = mRegistBean.getHold55();break;
//            case 4:registValue = mRegistBean.getHold54();break;
//            case 5:registValue = mRegistBean.getHold57();break;
//            case 6:registValue = mRegistBean.getHold56();break;
//            case 7:registValue = mRegistBean.getHold59();break;
//            case 8:registValue = mRegistBean.getHold58();break;
//        }
        return Arith.mul(registValue,inputPointMuilt[input277],inputPointScale[input277]) + inputPointUnit[input277];
    }
    /**
     * 获取保护时间值
     * @param input277
     * @return
     */
    public String getProTime(int input277){
        int registValue = mRegistBean.getInput279();
//        switch (input277){
//            case 1:registValue = mRegistBean.getHold69();break;
//            case 2:registValue = mRegistBean.getHold68();break;
//            case 3:registValue = mRegistBean.getHold73();break;
//            case 4:registValue = mRegistBean.getHold72();break;
//            case 5:registValue = mRegistBean.getHold71();break;
//            case 6:registValue = mRegistBean.getHold70();break;
//            case 7:registValue = mRegistBean.getHold75();break;
//            case 8:registValue = mRegistBean.getHold74();break;
//        }
        return registValue + "ms";
    }

    @Override
    public void finish() {
        super.finish();
        //停止刷新；关闭socket
        SocketClientUtil.close(mClientUtilRead);
    }
}
