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


    @BindView(R.id.tvRight)
    TextView tvRight;
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
    //????????????????????????????????????????????????????????????????????????
    private int[][] funs;
    private int[][] funsProcess;
    private int[] nowSet = new int[3];
    private int[] nowReadSn = {3,0,124};
    //??????????????????????????????
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
        tvRight.setText(R.string.m370??????);
//        ModbusUtil.setComAddressOldInv();
        initString();
        mTvTitle.setText(R.string.m????????????);
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
//                getString(R.string.????????????),
//                getString(R.string.????????????),
//                getString(R.string.????????????),
//                getString(R.string.????????????),
//                getString(R.string.????????????),
//                getString(R.string.????????????),
//                getString(R.string.????????????),
//                getString(R.string.????????????)
//        };
        stepStrs = new String[]{
                "",
                getString(R.string.????????????),
                getString(R.string.????????????),
                getString(R.string.????????????),
                getString(R.string.????????????),
                getString(R.string.????????????),
                getString(R.string.????????????),
                getString(R.string.????????????),
                getString(R.string.????????????)
        };
        statusStrs = new String[]{
                getString(R.string.??????),
                getString(R.string.?????????),
                getString(R.string.????????????),
                getString(R.string.????????????),
                getString(R.string.????????????),
                getString(R.string.??????????????????)
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
                getString(R.string.m????????????), getString(R.string.m????????????), getString(R.string.m459?????????), getString(R.string.inverterdevicedata_pattern),
                getString(R.string.inverterdevicedata_property), getString(R.string.m????????????), getString(R.string.m????????????), getString(R.string.m????????????)
        };
        //????????????????????????????????????????????????????????????????????????
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
                MyControl.circlerDialog(this,getString(R.string.??????????????????????????????),-1,() -> {
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
    //????????????:??????????????????
    private SocketClientUtil mClientUtilRead;

    //?????????????????????
    private void readRegisterValue() {
        Mydialog.Show(mContext);
        setBtnEnable(false);
        mClientUtilRead = SocketClientUtil.connectServer(mHandlerRead);
    }

    //????????????:??????????????????
    private SocketClientUtil mClientUtilWriter;

    //?????????????????????
    private void writeRegisterValue() {
        Mydialog.Show(mContext);
        mClientUtilWriter = SocketClientUtil.connectServer(mHandlerWrite);
    }

    /**
     * ????????????handle
     */
    //???????????????????????????
    private byte[] sendBytes;
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
                        sendBytes = SocketClientUtil.sendMsgToServerOldInv(mClientUtilWriter, nowSet);
                        LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(sendBytes));
                    } else {
                        toast(R.string.dataloggers_add_no_server);
                    }
                    break;
                //??????????????????
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //?????????????????????
                        boolean isCheck = MaxUtil.isCheckFull(mContext, bytes);
                        //??????tcp??????
                        SocketClientUtil.close(mClientUtilWriter);
                        BtnDelayUtil.refreshFinish();
                        if (isCheck){
                            //????????????
                            writeRegisterValueSN();
                        }else {
                            toast(R.string.m244????????????);
                        }
                        LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        //??????tcp??????
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
    //????????????:??????????????????
    private SocketClientUtil mClientUtilWriterSN;

    //?????????????????????
    private void writeRegisterValueSN() {
        Mydialog.Show(mContext);
        mClientUtilWriterSN = SocketClientUtil.connectServer(mHandlerWriteSN);
    }

    /**
     * ????????????handle
     */
    //???????????????????????????
    private byte[] sendBytesSN;
    private int nowReadPos;
    Handler mHandlerWriteSN = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //????????????
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessageWrite(this);
                    if (nowReadPos < funs.length){
                        sendBytesSN = SocketClientUtil.sendMsgToServerOldInv(mClientUtilWriterSN, funs[nowReadPos]);
                        LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(sendBytesSN));
                    }
                    break;
                //??????????????????
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //?????????????????????
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
                                    //???????????????????????????
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
                            //????????????
                            nowReadPos = 0;
                            SocketClientUtil.close(mClientUtilWriterSN);
                            BtnDelayUtil.refreshFinish();
                            //????????????
                            readRegisterValue();
                        }
                        LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        nowReadPos = 0;
                        //??????tcp??????
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
                //????????????
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessage(this);
                    byte[] sendBytesR = SocketClientUtil.sendMsgToServerOldInv(mClientUtilRead, funsProcess[0]);
                    LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(sendBytesR));
                    break;
                //??????????????????
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        LogUtil.i("????????????:" + SocketClientUtil.bytesToHexString(bytes));
                        //?????????????????????
                        boolean isCheck = ModbusUtil.checkModbus(bytes);
                        //????????????????????????
                        this.sendEmptyMessageDelayed(SocketClientUtil.SOCKET_SEND,800);
                        if (isCheck) {
                            //??????????????????
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
                                        note = getString(R.string.????????????);
                                        break;
                                    case 4:
                                        note = getString(R.string.????????????);
                                        break;
                                    case 5:
                                        note = getString(R.string.??????????????????);
                                        break;
                                }
                                mReportBean.setTitle(note);
                                mReportBean.setFinishTime(ChartUtils.getFormatDate("HH:mm:ss", new Date()));
                                OssUtils.circlerDialog(TLXHAutoTestOldInvActivity.this, note, -1, false, () -> {
                                    EventBus.getDefault().postSticky(mReportBean);
                                    jumpTo(TLXHToolAutoTestReportActivity.class, false);
                                });
                                //????????????
//                                toast("????????????");
                                SocketClientUtil.close(mClientUtilRead);
                                Mydialog.Dismiss();
                                return;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        //????????????
//                        toast("?????????" + e.toString());
                        SocketClientUtil.close(mClientUtilRead);
                        Mydialog.Dismiss();
                    }
                    break;
                case SocketClientUtil.SOCKET_CLOSE:
//                    toast("Socket ??????");
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
                        content = getString(R.string.?????????) + ":" + getPoint(input277) + "  "+
                                getString(R.string.????????????) + ":" + getProTime(input277);
                        break;
                    case 6:
                        content = getString(R.string.????????????) + ":"+Arith.mul(mRegistBean.getInput280(),inputPointMuilt[input277],inputPointScale[input277]) +inputPointUnit[input277] + " "
                                + getString(R.string.????????????) + ":"+Arith.mul(mRegistBean.getInput281(),inputPointMuilt[input277],inputPointScale[input277]) +inputPointUnit[input277];
                        break;
                    case 7:
                        content = getString(R.string.?????????) + ":"+bean.getPointValue() + " "
                                + getString(R.string.????????????) + ":"+bean.getPointValueTime();
                        break;
                }
                bean.setContent(content);
                newList.add(bean);
            }
            //???????????????????????????
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
     * ??????point???
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
     * ?????????????????????
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
        //?????????????????????socket
        SocketClientUtil.close(mClientUtilRead);
    }
}
