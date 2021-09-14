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

public class TLXHBDCActivity extends DemoBase {

    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    @BindView(R.id.waveview)
    WaveView mWaveview;
    @BindView(R.id.tvStatus)
    TextView mTvStatus;
    @BindView(R.id.tvErrCode)
    TextView mTvErrCode;
    @BindView(R.id.tvWarnCode)
    TextView mTvWarnCode;
    @BindView(R.id.vBus)
    View mVBus;
    @BindView(R.id.vCurrent)
    View mVCurrent;
    @BindView(R.id.tvTemA)
    TextView mTvTemA;
    @BindView(R.id.tvTemB)
    TextView mTvTemB;
    @BindView(R.id.radioGroup)
    RadioGroup mRadioGroup;
    private TextView tvVBat, tvVBus1, tvVBus2, tvABat, tvABB, tvALLC;
    private WaveHelper waveHelper;
    /**
     * 跳转信息
     */
    private MaxDataBean mBean;
    //读取命令功能码（功能码，开始寄存器，结束寄存器）
    private int[][] funs = {{4,3125,3249}};
    private int mBDC = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tlxhbdc);
        ButterKnife.bind(this);
        initString();
        EventBus.getDefault().register(this);
        if (mBean == null) mBean = new MaxDataBean();
        readRegisterValue();
        mRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId){
                case R.id.btnBDC1:
                    mBDC = 0;
                    readRegisterValue();
                    break;
           /*     case R.id.btnBDC2:
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
        tvVBat.setText(String.valueOf(storageBeen.getvBat()));
        tvVBus1.setText(String.valueOf(storageBeen.getvBus1()));
        tvVBus2.setText(String.valueOf(storageBeen.getvBus2()));
        tvABat.setText(String.valueOf(storageBeen.getaBat()));
        tvABB.setText(String.valueOf(storageBeen.getaBB()));
        tvALLC.setText(String.valueOf(storageBeen.getaLLC()));
        mTvTemA.setText(String.valueOf(storageBeen.getTempA()));
        mTvTemB.setText(String.valueOf(storageBeen.getTempB()));
        //故障告警
        int errCode = storageBeen.getErrorStorage();
        int warmCode = storageBeen.getWarmStorage();
        int errCodeSecond = storageBeen.getError2Storage();
        int warmCodeSecond = storageBeen.getWarm2Storage();
        //添加副码
        String errCodeStr = "";
        String warnCodeStr = "";
        if (errCode >= 200 || warmCode >= 200){
            errCodeStr = String.format("%d(%02d)",errCode,errCodeSecond);
            warnCodeStr = String.format("%d(%02d)",warmCode,warmCodeSecond);
        }else {
            if (errCode == 0){
                errCodeStr = getString(R.string.m351无故障信息);
            }else {
                errCode+=99;
                errCodeStr = String.format("%d(%02d)",errCode,errCodeSecond<100?errCodeSecond:errCodeSecond%100);
            }
            if (warmCode == 0){
                warnCodeStr = getString(R.string.m352无警告信息);
            }else {
                warmCode+=99;
                warnCodeStr = String.format("%d(%02d)",warmCode,warmCodeSecond<100?warmCodeSecond:warmCodeSecond%100);
            }
        }
        mTvErrCode.setText(errCodeStr);
        mTvWarnCode.setText(warnCodeStr);
        //bdc状态
        int bdcStatus = storageBeen.getStatusBDC();
        mTvStatus.setText(getTextByStatus(bdcStatus));
        int color = -1;
        if (bdcStatus == 2){
            color = R.color.wave_status_f_fault;
        }else {
            color = R.color.wave_blue;
        }
        if (color != -1) {
            mWaveview.setWaveColor(ContextCompat.getColor(mContext, color), ContextCompat.getColor(mContext, color));
        }
        waveHelper=new WaveHelper(mWaveview,100,100);
        waveHelper.start();
    }
    private void refreshUIBDC2() {
        ToolStorageDataBean storageBeen = mBean.getStorageBeen();
        tvVBat.setText(String.valueOf(storageBeen.getvBat02()));
        tvVBus1.setText(String.valueOf(storageBeen.getvBus102()));
        tvVBus2.setText(String.valueOf(storageBeen.getvBus202()));
        tvABat.setText(String.valueOf(storageBeen.getaBat02()));
        tvABB.setText(String.valueOf(storageBeen.getaBB02()));
        tvALLC.setText(String.valueOf(storageBeen.getaLLC02()));
        mTvTemA.setText(String.valueOf(storageBeen.getTempA02()));
        mTvTemB.setText(String.valueOf(storageBeen.getTempB02()));
        //告警
        //故障告警
        int errCode = storageBeen.getErrorStorage02();
        int warmCode = storageBeen.getWarmStorage02();
        int errCodeSecond = storageBeen.getError2Storage02();
        int warmCodeSecond = storageBeen.getWarm2Storage02();
        //添加副码
        String errCodeStr = "";
        String warnCodeStr = "";
        if (errCode >= 200 || warmCode >= 200){
            errCodeStr = String.format("%d(%02d)",errCode,errCodeSecond);
            warnCodeStr = String.format("%d(%02d)",warmCode,warmCodeSecond);
        }else {
            if (errCode == 0){
                errCodeStr = getString(R.string.m351无故障信息);
            }else {
                errCode+=99;
                errCodeStr = String.format("%d(%02d)",errCode,errCodeSecond<100?errCodeSecond:errCodeSecond%100);
            }
            if (warmCode == 0){
                warnCodeStr = getString(R.string.m352无警告信息);
            }else {
                warmCode+=99;
                warnCodeStr = String.format("%d(%02d)",warmCode,warmCodeSecond<100?warmCodeSecond:warmCodeSecond%100);
            }
        }
        mTvErrCode.setText(errCodeStr);
        mTvWarnCode.setText(warnCodeStr);
        //bdc状态
        int bdcStatus = storageBeen.getStatusBDC02();
        mTvStatus.setText(getTextByStatus(bdcStatus));
        int color = -1;
        if (bdcStatus == 2){
            color = R.color.wave_status_f_fault;
        }else {
            color = R.color.wave_blue;
        }
        if (color != -1) {
            mWaveview.setWaveColor(ContextCompat.getColor(mContext, color), ContextCompat.getColor(mContext, color));
        }
        waveHelper=new WaveHelper(mWaveview,100,100);
        waveHelper.start();
    }
//BDC、电池状态未检测到显示N/A   检测到电压没有电流是显示待机   有点压和电流时：显示工作模式
    public String getTextByStatus(int status){
        String text = String.valueOf(status);
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
        }
        return text;
    }
    private void initString() {
        mWaveview.setWaterLevelRatio(0.79f);
        mWaveview.setAmplitudeRatio(0.2f);
        mWaveview.setWaveLengthRatio(0.5f);
        int color = R.color.wave_blue;
        mWaveview.setWaveColor(ContextCompat.getColor(mContext, color), ContextCompat.getColor(mContext, color));
        mTvTitle.setText(R.string.mBDC参数);
        tvVBat = mVBus.findViewById(R.id.tvRContent);
        tvVBus1 = mVBus.findViewById(R.id.tvSContent);
        tvVBus2 = mVBus.findViewById(R.id.tvTContent);
        TextView tvVBusTitle = mVBus.findViewById(R.id.tvTitle);
        TextView tvVBusTitle1 = mVBus.findViewById(R.id.tvRContentT);
        TextView tvVBusTitle2 = mVBus.findViewById(R.id.tvSContentT);
        TextView tvVBusTitle3 = mVBus.findViewById(R.id.tvTContentT);
        tvVBusTitle.setText(R.string.all_power_v);
        tvVBusTitle1.setText("BAT");
        tvVBusTitle2.setText("BUS1");
        tvVBusTitle3.setText("BUS2");

        tvABat = mVCurrent.findViewById(R.id.tvRContent);
        tvABB = mVCurrent.findViewById(R.id.tvSContent);
        tvALLC = mVCurrent.findViewById(R.id.tvTContent);
        TextView tvCBusTitle = mVCurrent.findViewById(R.id.tvTitle);
        TextView tvCBusTitle1 = mVCurrent.findViewById(R.id.tvRContentT);
        TextView tvCBusTitle2 = mVCurrent.findViewById(R.id.tvSContentT);
        TextView tvCBusTitle3 = mVCurrent.findViewById(R.id.tvTContentT);
        tvCBusTitle.setText(R.string.all_power_a);
        tvCBusTitle1.setText("BAT");
        tvCBusTitle2.setText("BB");
        tvCBusTitle3.setText("LLC");
    }


    @OnClick({R.id.ivLeft, R.id.vErr})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivLeft:
                finish();
                break;
            case R.id.vErr:
//                Intent intent = new Intent(mContext, TLXWarningActivity.class);
//                intent.putExtra("title", tvWarningStr.getText().toString());
//                intent.putExtra("errCode", mMaxData.getErrCode());
//                intent.putExtra("warmCode", mMaxData.getWarmCode());
//                intent.putExtra("errCodeSecond", mMaxData.getErrCodeSecond());
//                intent.putExtra("warmCodeSecond", mMaxData.getWarmCodeSecond());
//                intent.putExtra("type", 1);
//                intent.putExtra("error1", mMaxData.getError1());
//                intent.putExtra("error2", mMaxData.getError2());
//                jumpTo(intent, false);
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
