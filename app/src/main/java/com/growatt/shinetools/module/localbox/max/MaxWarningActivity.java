package com.growatt.shinetools.module.localbox.max;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.growatt.shinetools.R;
import com.growatt.shinetools.base.DemoBase;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.MaxWifiParseUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.modbusbox.bean.MaxErrorBean;
import com.growatt.shinetools.utils.BtnDelayUtil;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.Position;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MaxWarningActivity extends DemoBase {
    @BindView(R.id.headerView)
    View headerView;
    @BindView(R.id.tvErrCode)
    TextView mTvErrCode;
    @BindView(R.id.tvErrCodeContent)
    TextView mTvErrCodeContent;
    @BindView(R.id.tvError)
    TextView mTvError;
    @BindView(R.id.tvErrorContent)
    TextView mTvErrorContent;
    @BindView(R.id.tvWarm)
    TextView mTvWarm;
    @BindView(R.id.tvWarnContent)
    TextView mTvWarnContent;
    @BindView(R.id.tvTime)
    TextView mTvTime;
    @BindView(R.id.tvRight)
    TextView mTvRight;
    private String mTitle;
    private int errCode = -1;
    private int warmCode = -1;
    private int error1 = -1;
    private int error2 = -1;
    private String[] errorCodeStr;
    //读取命令功能码（功能码，开始寄存器，结束寄存器）
    private int[][] funs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_max_warning);
        ButterKnife.bind(this);
        initIntent();
        initHeaderView();
        initString();
        if (errCode != 99) {
            mHandlerRead.sendEmptyMessageDelayed(SocketClientUtil.SOCKET_AUTO_DELAY,500);
        }
    }

    private void initString() {
        mTvErrCode.setText(errCode + "");
        mTvError.setText(error1 + "");
        mTvWarm.setText(warmCode + "");
        //警告详情
        String warnString = "";
        switch (warmCode) {
            case 99:
                warnString = getString(R.string.m352无警告信息);
                break;
            case 100:
                warnString = getString(R.string.m343风扇异常);
                break;
            case 104:
                warnString = getString(R.string.m344DSP与M3版本不一致);
                break;
            case 106:
                warnString = getString(R.string.m345防雷器发生故障);
                break;
            case 107:
                warnString = getString(R.string.m346NE检测异常N线地线之间压差过大);
                break;
            case 108:
                warnString = getString(R.string.m347PV1至PV6短路异常);
                break;
            case 109:
                warnString = getString(R.string.m348直流升压驱动异常);
                break;
            case 110:
                warnString = getString(R.string.m349组串异常);
                break;
            case 111:
                warnString = getString(R.string.m350U盘过流保护);
                break;
            default:
                warnString = String.format("%s:%d",getString(R.string.警告),warmCode);
                break;
        }
        mTvWarnContent.setText(warnString);
        //错误详情
        String errorString = "";
        switch (errCode) {
            case 99:
                errorString = getString(R.string.m351无故障信息);
                break;
            case 101:
                errorString = getString(R.string.m327通信故障);
                break;
            case 102:
                errorString = getString(R.string.m328冗余采样异常保护);
                break;
            case 108:
                errorString = getString(R.string.m329主SPS供电异常);
                break;
            case 110:
                errorString = getString(R.string.m逆变电流过流);
                break;
            case 112:
                errorString = getString(R.string.m330AFCI拉弧故障);
                break;
            case 113:
                errorString = getString(R.string.m331IGBT驱动错误);
                break;
            case 114:
                errorString = getString(R.string.m332AFCI模块检测失败);
                break;
            case 117:
                errorString =getString(R.string.mAC侧继电器异常);
                break;
            case 119:
                errorString =getString(R.string.mGFCI模块损坏);
                break;
            case 121:
                errorString = getString(R.string.m334CPLD芯片检测异常);
                break;
            case 122:
                errorString = getString(R.string.m335BUS电压异常);
                break;
            case 124:
                errorString = getString(R.string.m336无电网连接);
                break;
            case 125:
                errorString = getString(R.string.m337PV绝缘阻抗低);
                break;
            case 126:
                errorString = getString(R.string.m338漏电流过高);
                break;
            case 127:
                errorString = getString(R.string.m339输出电流直流分量过高);
                break;
            case 128:
                errorString = getString(R.string.m340PV电压过高);
                break;
            case 129:
                errorString = getString(R.string.m341电网过欠压保护);
                break;
            case 130:
                errorString = getString(R.string.m342电网过欠频保护);
                break;
            case 131:
                errorString = getString(R.string.m过温保护);
                break;
            default:
                errorString = String.format("%s:%d",getString(R.string.all_Fault),errCode);
                break;
        }
        mTvErrCodeContent.setText(errorString);
        funs = new int[][]{
                {4, 501, 503}
        };
    }

    private void initHeaderView() {
        setHeaderImage(headerView, R.drawable.icon_return, Position.LEFT, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setHeaderTitle(headerView, mTitle);
        setHeaderTvTitle(headerView, getString(R.string.m325历史故障), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpTo(MaxErrorHistoryActivity.class,false);
            }
        });
    }

    private void initIntent() {
        Intent mIntent = getIntent();
        if (mIntent != null) {
            mTitle = mIntent.getStringExtra("title");
            errCode = mIntent.getIntExtra("errCode", -1);
            warmCode = mIntent.getIntExtra("warmCode", -1);
            error1 = mIntent.getIntExtra("error1", -1);
            error2 = mIntent.getIntExtra("error2", -1);
        }
        errCode = errCode + 99;
        warmCode = warmCode + 99;
    }

    //连接对象:用于读取数据
    private SocketClientUtil mClientUtilRead;

    //读取寄存器的值
    private void readRegisterValue() {
        mClientUtilRead = SocketClientUtil.connectServer(mHandlerRead);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //停止刷新；关闭socket
        SocketClientUtil.close(mClientUtilRead);
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
                    byte[] sendBytesR = SocketClientUtil.sendMsgToServer(mClientUtilRead, funs[0]);
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
                            if (bs == null || bs.length < 5) return;
                            //解析int值
                            int value1 = MaxWifiParseUtil.obtainRegistValueHOrL(0, bs[1]);
                            int value2 = MaxWifiParseUtil.obtainRegistValueHOrL(0, bs[2]);
                            int value3 = MaxWifiParseUtil.obtainRegistValueHOrL(0, bs[3]);
                            int value4 = MaxWifiParseUtil.obtainRegistValueHOrL(0, bs[4]);
                            MaxErrorBean item = new MaxErrorBean();
                            item.setErrMonth(value1);
                            item.setErrDay(value2);
                            item.setErrHour(value3);
                            item.setErrMin(value4);
                            mTvTime.setText(MaxUtil.getMaxErrTimeByErrBean(item));
//                            toast(R.string.all_success);
                        } else {
//                            toast(R.string.all_failed);
                        }
                        LogUtil.i("接收读取：" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //关闭tcp连接
                        SocketClientUtil.close(mClientUtilRead);
                        BtnDelayUtil.refreshFinish();
                    }
                    break;
                //延迟刷新；以防上一个界面刷新停止马上刷新导致失败
                case SocketClientUtil.SOCKET_AUTO_DELAY:
                    readRegisterValue();
                    break;
                default:
                    BtnDelayUtil.dealMaxBtn(this, what, mContext, mTvRight);
                    break;
            }
        }
    };
}
