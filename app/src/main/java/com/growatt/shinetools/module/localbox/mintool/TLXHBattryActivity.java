package com.growatt.shinetools.module.localbox.mintool;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.growatt.shinetools.R;
import com.growatt.shinetools.base.DemoBase;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.modbusbox.bean.MaxDataBean;
import com.growatt.shinetools.modbusbox.bean.ToolStorageDataBean;
import com.growatt.shinetools.utils.BtnDelayUtil;
import com.growatt.shinetools.utils.CommenUtils;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.Mydialog;
import com.growatt.shinetools.widget.WaveHelper;
import com.growatt.shinetools.widget.WaveView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TLXHBattryActivity extends DemoBase {

    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    @BindView(R.id.waveview)
    WaveView mWaveview;
    @BindView(R.id.tvStatus)
    TextView mTvStatus;
    @BindView(R.id.radioGroup)
    RadioGroup mRadioGroup;
    @BindView(R.id.tvErrCode)
    TextView mTvErrCode;
    @BindView(R.id.tvWarnCode)
    TextView mTvWarnCode;
    @BindView(R.id.tvBus)
    TextView mTvBus;
    @BindView(R.id.tvCurrent)
    TextView mTvCurrent;
    @BindView(R.id.tvSOC)
    TextView mTvSOC;
    @BindView(R.id.tvTemp)
    TextView mTvTemp;
    @BindView(R.id.tvCV)
    TextView mTvCV;
    private WaveHelper waveHelper;
    /**
     * 跳转信息
     */
    private MaxDataBean mBean;
    //读取命令功能码（功能码，开始寄存器，结束寄存器）
    private int[][] funs = {{4,3125,3249}};
    private int mBDC = 0;

    private boolean isTlxhus;//如果是US电池状态取寄存器地址3166

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tlxhbattry);
        ButterKnife.bind(this);
        initString();
        EventBus.getDefault().register(this);
        if (mBean == null) mBean = new MaxDataBean();
        isTlxhus=getIntent().getBooleanExtra("isTlxhus",false);
        readRegisterValue();
        mRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId){
                case R.id.btnBDC1:
                    mBDC = 0;
                    readRegisterValue();
                    break;
         /*       case R.id.btnBDC2:
                    mBDC = 0;
                    readRegisterValue();
                    break;*/
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMin(@NonNull MaxDataBean bean) {
        this.mBean = bean;
        refreshUI();
    }
    private void refreshUI() {
        int bdcStatus = mBean.getBdcStatus();
        if (bdcStatus == 1){
            mBDC = 0;
        }else if (bdcStatus == 2){
            mBDC = 0;
        }else if (bdcStatus == 3){
            mBDC = 0;
            CommenUtils.showAllView(mRadioGroup);
        }else {
            mBDC = 0;
            CommenUtils.showAllView(mRadioGroup);
        }
        if (mBDC == 1 ){
            refreshUIBDC2();
        }else {
            refreshUIBDC1();
        }
    }
    private void refreshUIBDC1() {
        ToolStorageDataBean storageBeen = mBean.getStorageBeen();
        mTvBus.setText(String.valueOf(storageBeen.getvBms()));
        mTvCurrent.setText(String.valueOf(storageBeen.getaBms()));
        mTvSOC.setText(storageBeen.getSoc());
        mTvTemp.setText(String.valueOf(storageBeen.getTempBms()));
        mTvCV.setText(String.valueOf(storageBeen.getvCV()));
        //告警
        int errCode = storageBeen.getBmsError();
        int warmCode = storageBeen.getBmsWarm();
        if (errCode == 0){
            mTvErrCode.setText(R.string.m351无故障信息);
        }else {
            if (errCode < 200) errCode+=99;
            mTvErrCode.setText(String.valueOf(errCode));
        }
        if (warmCode == 0){
            mTvWarnCode.setText(R.string.m352无警告信息);
        }else {
            if (warmCode < 200) warmCode+=99;
            mTvWarnCode.setText(String.valueOf(warmCode));
        }
        //bms状态
        int bmsStatus = storageBeen.getBmsStatus();
        //bdc状态
        int bdcStatus = storageBeen.getStatusBDC();

        if (isTlxhus){
            mTvStatus.setText(getTextByBdcStatus(bdcStatus));
        }else {
            mTvStatus.setText(getTextByStatus(bmsStatus));
        }
        int color = -1;
        if (bmsStatus == 1){
            color = R.color.wave_charge;
        }else if (bmsStatus == 2){
            color = R.color.wave_discharge;
        }else {
            color = R.color.oss_status_oprating;
        }
        if (color != -1) {
            mWaveview.setWaveColor(ContextCompat.getColor(mContext, color), ContextCompat.getColor(mContext, color));
        }
        waveHelper=new WaveHelper(mWaveview,100,100);
        waveHelper.start();
    }
    private void refreshUIBDC2() {
        ToolStorageDataBean storageBeen = mBean.getStorageBeen();
        mTvBus.setText(String.valueOf(storageBeen.getvBms02()));
        mTvCurrent.setText(String.valueOf(storageBeen.getaBms02()));
        mTvSOC.setText(storageBeen.getSoc02());
        mTvTemp.setText(String.valueOf(storageBeen.getTempBms02()));
        mTvCV.setText(String.valueOf(storageBeen.getvCV02()));
        //告警
        int errCode = storageBeen.getBmsError02();
        int warmCode = storageBeen.getBmsWarm02();
        if (errCode == 0){
            mTvErrCode.setText(R.string.m351无故障信息);
        }else {
            if (errCode < 200) errCode+=99;
            mTvErrCode.setText(String.valueOf(errCode));
        }
        if (warmCode == 0){
            mTvWarnCode.setText(R.string.m352无警告信息);
        }else {
            if (warmCode < 200) warmCode+=99;
            mTvWarnCode.setText(String.valueOf(warmCode));
        }
        //bms状态
        int bmsStatus = storageBeen.getBmsStatus02();
        mTvStatus.setText(getTextByStatus(bmsStatus));
        int color = -1;
        if (bmsStatus == 1){
            color = R.color.wave_charge;
        }else if (bmsStatus == 2){
            color = R.color.wave_discharge;
        }else {
            color = R.color.oss_status_oprating;
        }
        if (color != -1) {
            mWaveview.setWaveColor(ContextCompat.getColor(mContext, color), ContextCompat.getColor(mContext, color));
        }
        waveHelper=new WaveHelper(mWaveview,100,100);
        waveHelper.start();
    }
    public String getTextByStatus(int status){
        String text = String.valueOf(status);
        switch (status){
            case 0:
                text = getString(R.string.all_Standby);
                break;
            case 1:
                text = getString(R.string.all_Charge);
                break;
            case 2:
                text = getString(R.string.all_Discharge);
                break;
        }
        return text;
    }



    //BDC、电池状态未检测到显示N/A   检测到电压没有电流是显示待机   有点压和电流时：显示工作模式
    public String getTextByBdcStatus(int status){
        String text;
        switch (status){
            case 0:
                text = getString(R.string.all_Waiting);
                break;
            case 1:
                text = getString(R.string.all_Normal);
                break;
            case 2:
                text = getString(R.string.all_Fault);
                break;
            case 3:
                text = getString(R.string.m226升级中);
                break;
            default:
                text = getString(R.string.all_Fault);
                break;
        }
        return text;
    }


    private void initString() {
        mTvTitle.setText(R.string.m电池参数);
        mWaveview.setWaterLevelRatio(0.79f);
        mWaveview.setAmplitudeRatio(0.2f);
        mWaveview.setWaveLengthRatio(0.5f);
        int color = R.color.wave_blue;
        mWaveview.setWaveColor(ContextCompat.getColor(mContext, color), ContextCompat.getColor(mContext, color));
        waveHelper=new WaveHelper(mWaveview,100,100);
        waveHelper.start();
    }


    @OnClick({R.id.ivLeft, R.id.vErr})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivLeft:
                finish();
                break;
            case R.id.vErr:
                break;
        }
    }
    @Override
    public void onStop() {
        if(waveHelper!=null){
            waveHelper.cancel();
        }
        super.onStop();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(waveHelper!=null){
            waveHelper.start();
        }
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
                            toast(R.string.all_success);
                            RegisterParseUtil.parseInput3125T3249(mBean, bytes);
                            refreshUI();
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
                    BtnDelayUtil.dealMaxBtn(this,what,mContext);
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
            connectServer();
            return null;
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
}
