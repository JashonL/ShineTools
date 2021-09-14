package com.growatt.shinetools.module.localbox.old;

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
import com.growatt.shinetools.widget.AutoFitTextView;

import butterknife.BindView;
import butterknife.ButterKnife;


public class OldInvWarningActivity extends DemoBase {
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
    private int errCodeSecond = -1;
    private int warmCodeSecond = -1;
    private int error1 = -1;
    private int error2 = -1;
    private String[] errorCodeStr;
    //读取命令功能码（功能码，开始寄存器，结束寄存器）
    private int[][] funs;
    /**
     * 新老协议：0：代表副故障码是两个寄存器 取后两位；1：代表新副故障码一个寄存器，直接显示
     */
    private int type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_inv_warning);
        ButterKnife.bind(this);
        ModbusUtil.setComAddressOldInv();
        initIntent();
        initHeaderView();
        initString();
        if (errCode != 0) {
            mHandlerRead.sendEmptyMessageDelayed(SocketClientUtil.SOCKET_AUTO_DELAY,500);
        }
    }

    private void initString() {
        //添加副码
        String errCodeStr = "";
        String warnCodeStr = "";
        if (errCode < 200){
//             errCodeStr = String.format("%d(%02d)",errCode,errCodeSecond<100?errCodeSecond:errCodeSecond%100);
//             warnCodeStr = String.format("%d(%02d)",warmCode,warmCodeSecond<100?warmCodeSecond:warmCodeSecond%100);
            errCodeStr = String.valueOf(errCode);
        }else{
            errCodeStr = String.format("%d (%02d)",errCode,errCodeSecond);
        }
        if (warmCode < 200){
            warnCodeStr = String.valueOf(warmCode);
        }else {
            warnCodeStr = String.format("%d (%02d)",warmCode,warmCodeSecond);
        }
        mTvErrCode.setText(errCodeStr);
        mTvWarm.setText(warnCodeStr);
        mTvError.setText(error1 + "");
//        //警告详情
//        mTvWarnContent.setText(MaxUtil.getWarmContentByCodeNew(this,warmCode));
//        //错误详情
//        mTvErrCodeContent.setText(MaxUtil.getErrContentByCodeNew(this,errCode));
        //暂时不显示详情数据
        //警告详情
        mTvWarnContent.setText(String.valueOf(warmCode));
        //错误详情
        mTvErrCodeContent.setText(String.valueOf(errCode));
        if (warmCode == 100) {
            funs = new int[][]{
                    {4, 91, 93},
                    {4, 229, 229}
            };
        }else {
            funs = new int[][]{
                    {4, 91, 93}
            };
        }
    }

    private void initHeaderView() {
        setHeaderImage(headerView, -1, Position.LEFT, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setHeaderTitle(headerView, mTitle);
        setHeaderTvTitle(headerView, getString(R.string.m325历史故障), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpTo(OldInvErrorHistoryActivity.class,false);
            }
        });
    }

    private void initIntent() {
        Intent mIntent = getIntent();
        if (mIntent != null) {
            mTitle = mIntent.getStringExtra("title");
            errCode = mIntent.getIntExtra("errCode", -1);
            warmCode = mIntent.getIntExtra("warmCode", -1);
            type = mIntent.getIntExtra("type", 0);
            errCodeSecond = mIntent.getIntExtra("errCodeSecond", -1);
            warmCodeSecond = mIntent.getIntExtra("warmCodeSecond", -1);
            error1 = mIntent.getIntExtra("error1", -1);
            error2 = mIntent.getIntExtra("error2", -1);
        }
        if (errCode < 200 && errCode>0) errCode+=99;
        if (warmCode < 200 && warmCode >0) warmCode+=99;
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
    private int nowCount = 0;
    Handler mHandlerRead = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessage(this);
                    if (nowCount < funs.length) {
                        byte[] sendBytesR = SocketClientUtil.sendMsgToServerOldInv(mClientUtilRead, funs[nowCount]);
                        LogUtil.i("发送读取：" + SocketClientUtil.bytesToHexString(sendBytesR));
                    }
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
                            //刷新时间
                            if (nowCount == 0){
                                if (bs != null && bs.length > 4) {
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
                                }
                                //刷新风扇异常位置位置
                            }else if (nowCount == 1){
                                int fan = MaxWifiParseUtil.obtainRegistValueHOrL(0,bs[1]);
                                int fan1 = fan & 0x01;
                                int fan2 = fan & 0x02;
                                int fan3 = fan & 0x04;
                                int fan4 = fan & 0x08;
                                //拼接故障详情
                                StringBuilder sb = new StringBuilder(mTvErrCodeContent.getText());
                                if (fan1 != 0) sb.append("Fan1 ");
                                if (fan2 != 0) sb.append("Fan2 ");
                                if (fan3 != 0) sb.append("Fan3 ");
                                if (fan4 != 0) sb.append("Fan4 ");
                                if (fan != 0) sb.append("fault");
                                mTvErrCodeContent.setText(String.valueOf(sb));
                            }
                            if (nowCount < funs.length -1){
                                nowCount++;
                                this.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                            }else {
                                nowCount = 0;
                                //关闭连接
                                SocketClientUtil.close(mClientUtilRead);
                            }
                        } else {
                            //关闭连接
                            SocketClientUtil.close(mClientUtilRead);
                        }
                        LogUtil.i("接收读取：" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        nowCount = 0;
                        //关闭连接
                        SocketClientUtil.close(mClientUtilRead);
                    }
                    break;
                //延迟刷新；以防上一个界面刷新停止马上刷新导致失败
                case SocketClientUtil.SOCKET_AUTO_DELAY:
                    readRegisterValue();
                    break;
                default:
                    BtnDelayUtil.dealTLXBtn(this, what, mContext, mTvRight);
                    break;
            }
        }
    };
}
