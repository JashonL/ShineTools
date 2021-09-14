package com.growatt.shinetools.module.localbox.mintool;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

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
import com.growatt.shinetools.utils.Position;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.view.listener.OnLvItemClickListener;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Max 本地调试国家以及安规
 */
public class USParamCountryActivity extends DemoBase {
    String readStr;
    @BindView(R.id.tvContent1)
    TextView mTvContent1;
    @BindView(R.id.btnSetting)
    Button mBtnSetting;
    @BindView(R.id.btnSelect)
    Button mBtnSelect;
    private String mTitle;
    @BindView(R.id.headerView)
    View headerView;
    @BindView(R.id.tvRight)
    TextView mTvRight;
    private List<String> models;
    private String[][] modelToal;
    //读取命令功能码（功能码，开始寄存器，结束寄存器）
    private int[][] funs;
    private int modelPos = -1;//当前model下标
    private int[][] nowSet = {
            {0x10, 118, 121}
    };
    private int[][] registerValues = {
           {-1, -1, -1, -1}
    };
    private boolean isFirst = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tlxparam_country);
        ButterKnife.bind(this);
        initString();
        initIntent();
        initHeaderView();
        readRegisterValue();
    }

    private void initString() {
        mTvRight.setText(R.string.m370读取);
        modelToal = new String[][]{
                {"S25", "IEEE1547-208", "5", "208V", "25"}
                , {"S25", "IEEE1547-240", "1", "240V", "25"}
                , {"S31", "RULE 21-208", "5", "208V", "31"}
                , {"S31", "RULE 21-240", "1", "240V", "31"}
                , {"S32", "HECO-208", "5", "208V", "32"}
                , {"S32", "HECO-240", "1", "240V", "32"}
                , {"S35", "PRC-East-208", "5", "208V", "35"}
                , {"S35", "PRC-East-240", "1", "240V", "35"}
                , {"S36", "PRC-West-208", "5", "208V", "36"}
                , {"S36", "PRC-West-240", "1", "240V", "36"}
                , {"S37", "PRC-Quebec-208", "5", "208V", "37"}
                , {"S37", "PRC-Quebec-240", "1", "240V", "37"}
        };
        models = new ArrayList<>();
        for (int i = 0; i < modelToal.length; i++) {
            models.add(modelToal[i][1]);
        }
        //读取命令功能码（功能码，开始寄存器，结束寄存器）
        funs = new int[][]{
                {3, 0, 124}//优先时间
        };
    }

    private void initIntent() {
        Intent mIntent = getIntent();
        if (mIntent != null) {
            mTitle = mIntent.getStringExtra("title");
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
        setHeaderTvTitle(headerView, getString(R.string.m370读取), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //读取寄存器的值
                isFirst = false;
                readRegisterValue();
            }
        });
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
                    byte[] sendBytesR = SocketClientUtil.sendMsgToServer(mClientUtil, funs[0]);
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
                            LogUtil.i("接收读取：" + SocketClientUtil.bytesToHexString(bytes));
                            //识别model
                            byte[] valueBs = MaxWifiParseUtil.subBytesFull(bs, 118, 0, 4);
                            registerValues[0][0] = MaxWifiParseUtil.obtainValueOne(bs, 118);
                            registerValues[0][1] = MaxWifiParseUtil.obtainValueOne(bs, 119);
                            registerValues[0][2] = MaxWifiParseUtil.obtainValueOne(bs, 120);
                            registerValues[0][3] = MaxWifiParseUtil.obtainValueOne(bs, 121);
                            if (isFirst) {
                                isFirst = false;
                                //关闭tcp连接
                                SocketClientUtil.close(mClientUtil);
                                BtnDelayUtil.refreshFinish();
                                return;
                            }
                            BigInteger big = new BigInteger(1, valueBs);
                            long bigInteger = big.longValue();
                            String modelS = MaxUtil.getDeviceModelSingleNew(bigInteger, 16) + MaxUtil.getDeviceModelSingleNew(bigInteger, 15);
                            String readModel = String.format("S%s", modelS);
                            //电压U位
                            //解析120寄存器  U 电压 0-2bit
                            int readVol = MaxWifiParseUtil.obtainValueOne(bs, 120) & 0x0007;
                            //匹配
                            boolean isFlag = false;
                            for (int i = 0; i < modelToal.length; i++) {
                                String[] model = modelToal[i];
                                if (model[0].equals(readModel) && model[2].equals(String.valueOf(readVol))) {
                                    isFlag = true;
                                    modelPos = i;
                                    mTvContent1.setText(model[1]);
                                    break;
                                }
                            }
                            if (!isFlag) {
                                modelPos = -1;
                                mTvContent1.setText("");
                            }
                        } else {
                            toast(R.string.all_failed);
                        }
                        //关闭tcp连接
                        SocketClientUtil.close(mClientUtil);
                        BtnDelayUtil.refreshFinish();
                    } catch (Exception e) {
                        e.printStackTrace();
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

    @OnClick({R.id.btnSelect, R.id.btnSetting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnSelect:
                new CircleDialog.Builder()
                        .setTitle(getString(R.string.android_key499))
                        .setWidth(0.7f)
                        .setGravity(Gravity.CENTER)
                        .setMaxHeight(0.5f)
                        .setItems(models, new OnLvItemClickListener() {
                            @Override
                            public boolean onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                                modelPos = pos;
                                mBtnSelect.setText(models.get(pos));
                                return true;
                            }
                        })
                        .setNegative(getString(R.string.all_no), null)
                        .show(getSupportFragmentManager());
                break;
            case R.id.btnSetting:
                if (modelPos == -1) {
                    toast(R.string.all_blank);
                    return;
                }
                if (registerValues[0][1] == -1){
                    toast(R.string.m请先读取值);
                    return;
                }
                setModelRegistValue();
                connectServerWrite();
                break;
        }
    }
    private void setModelRegistValue() {
        String[] selectModels = modelToal[modelPos];
        int sModel = Integer.parseInt(selectModels[4], 16) << 8 | (registerValues[0][0] & 0x00FF);
        int uModel = Integer.parseInt(selectModels[2]) | ((registerValues[0][2] & 0b1111111111111000));
        registerValues[0][0] = sModel;
//                        registerValues[1][1] = 0;
        registerValues[0][2] = uModel;
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
    Handler mHandlerW = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessageWrite(this);
                    sendBytes = SocketClientUtil.sendMsgToServer10(mClientUtilW, nowSet[0], registerValues[0]);
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
                            toast(getString(R.string.all_success));
                        } else {
                            toast(getString(R.string.all_failed));
                        }
                        //关闭连接
                        SocketClientUtil.close(mClientUtilW);
                        Mydialog.Dismiss();
                        LogUtil.i("接收写入：" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
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
}
