package com.growatt.shinetools.module.localbox.mix;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.growatt.shinetools.R;
import com.growatt.shinetools.base.DemoBase;
import com.growatt.shinetools.modbusbox.Arith;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.MaxWifiParseUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.utils.BtnDelayUtil;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.Mydialog;
import com.growatt.shinetools.utils.Position;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.growatt.shinetools.utils.BtnDelayUtil.TIMEOUT_RECEIVE;


/**
 * 4个寄存器分别设置
 */
public class MixConfigType4Activity extends DemoBase {

    String readStr;
    @BindView(R.id.tvTitle1)
    TextView mTvTitle1;
    @BindView(R.id.etContent1)
    EditText mEtContent1;
    @BindView(R.id.tvContent1)
    TextView mTvContent1;
    @BindView(R.id.tvTitle2)
    TextView mTvTitle2;
    @BindView(R.id.etContent2)
    EditText mEtContent2;
    @BindView(R.id.tvContent2)
    TextView mTvContent2;
    @BindView(R.id.btnSetting)
    Button mBtnSetting;
    @BindView(R.id.headerView)
    View headerView;
    @BindView(R.id.tvTitle3)
    TextView mTvTitle3;
    @BindView(R.id.etContent3)
    EditText mEtContent3;
    @BindView(R.id.tvContent3)
    TextView mTvContent3;
    @BindView(R.id.tvTitle4)
    TextView mTvTitle4;
    @BindView(R.id.etContent4)
    EditText mEtContent4;
    @BindView(R.id.tvContent4)
    TextView mTvContent4;
    @BindView(R.id.tvRight)
    TextView mTvRight;
    private String mTitle;
    private int mType = -1;
    //读取命令功能码（功能码，开始寄存器，结束寄存器）
    private int[][] funs;
    //内容标题显示容器
    private String[][] titles;
    //设置的内容
    private int[][] nowSet;
    //设置功能码
    private int[][] funSet;
    private float[][] mMultiples;//倍数集合
    private float[] mMul = new float[]{
            1,1,1,1
    };//当前倍数
    private String[][] mUnits;
    private String[] mUnit = new String[]{
            "","","",""
    };//当前单位
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mix_config_type4);
        ButterKnife.bind(this);
        initIntent();
        initString();
        initHeaderView();
    }

    private void initString() {
        readStr = getString(R.string.m369读取值);
        //读取命令功能码（功能码，开始寄存器，结束寄存器）
        funs = new int[][]{
                {3, 110, 116}//28
                , {3, 111, 117}//29
                //参数设置
        };
        funSet = new int[][]{
                {110, 112, 114, 116}//28
                , {111, 113, 115, 117}//29
        };
        //内容标题显示容器
        titles = new String[][]{
                {String.format("%s%s",getString(R.string.m391PF限制负载百分比点),"1(110)"),
                        String.format("%s%s",getString(R.string.m391PF限制负载百分比点),"2(112)"),
                        String.format("%s%s",getString(R.string.m391PF限制负载百分比点),"3(114)"),
                        String.format("%s%s",getString(R.string.m391PF限制负载百分比点),"4(116)")
                },
                {String.format("%s%s",getString(R.string.m392PF限制功率因数),"1(111)"),
                        String.format("%s%s",getString(R.string.m392PF限制功率因数),"2(113)"),
                        String.format("%s%s",getString(R.string.m392PF限制功率因数),"3(115)"),
                        String.format("%s%s",getString(R.string.m392PF限制功率因数),"4(117)")
                }
//                {"PF限制负载百分比点1(110)", "PF限制负载百分比点2(112)", "PF限制负载百分比点3(114)", "PF限制负载百分比点4(116)"}
//                , {"PF限制功率因数1(111)", "PF限制功率因数2(113)", "PF限制功率因数3(115)", "PF限制功率因数4(117)"}
                //参数设置
        };
        mTvTitle1.setText(titles[mType][0]);
        mTvTitle2.setText(titles[mType][1]);
        mTvTitle3.setText(titles[mType][2]);
        mTvTitle4.setText(titles[mType][3]);
        //需要设置的内容
        nowSet = new int[][]{
                {6, funSet[mType][0], -1}
                , {6, funSet[mType][1], -1}
                , {6, funSet[mType][2], -1}
                , {6, funSet[mType][3], -1}
        };
        try {
            mMultiples = new float[][]{
                    {1,1,1,1},{1,1,1,1}
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mUnits = new String[][]{
                    {"%","%","%","%"},{"","","",""}
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mMul = mMultiples[mType];
            mUnit = mUnits[mType];
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String getReadValueReal(int pos,int read){
        String value = "";
        //特殊处理
        switch (mType){
            case 1://PF限制功率因数1-4
                double div = Arith.div(read - 10000, 10000.0, 2);
                if (div == -1) div = 1;
                value =  new DecimalFormat("0.00").format(div)+ mUnit[pos];
                break;
        }
        if (TextUtils.isEmpty(value)){
            value = Arith.mul(read,mMul[pos],2) + mUnit[pos];
        }
        return value;
    }
    public int getWriteValueReal(int pos,float write){
        try {
            int value = -1;
            switch (mType){
                case 1://PF限制功率因数1-4
                    value = (int) Math.round(Arith.add(Arith.mul(write,10000),10000));
                    break;
            }
            if (value == -1){
                value = (int) Math.round(Arith.div(write,mMul[pos]));
            }
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            return Math.round(write);
        }
    }
    private void initIntent() {
        Intent mIntent = getIntent();
        if (mIntent != null) {
            mTitle = mIntent.getStringExtra("title");
            mType = mIntent.getIntExtra("type", -1);
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
                //刷新
                readRegisterValue();
            }
        });
    }

    //连接对象:用于读取数据
    private SocketClientUtil mClientUtilRead;
    //连接对象:用于设置数据
    private SocketClientUtil mClientUtilWriter;

    //读取寄存器的值
    private void readRegisterValue() {
        Mydialog.Show(mContext);
        mClientUtilRead = SocketClientUtil.connectServer(mHandlerRead);
    }

    //读取寄存器的值
    private void writeRegisterValue() {
        Mydialog.Show(mContext);
        mClientUtilWriter = SocketClientUtil.connectServer(mHandlerWrite);
    }

    /**
     * 写寄存器handle
     */
    //当前发送的字节数组
    private byte[] sendBytes;
    private boolean isWriteFinish;
    //当前设置的输入框：0:text1/1:text2
    private int nowPos = -1;
    Handler mHandlerWrite = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessageWrite(this);
                    if (nowSet != null) {
                        isWriteFinish = true;
                        for (int i = 0, len = nowSet.length; i < len; i++) {
                            if (nowSet[i][2] != -1) {
                                nowPos = i;
                                isWriteFinish = false;
                                sendBytes = SocketClientUtil.sendMsgToServer(mClientUtilWriter, nowSet[i]);
                                LogUtil.i("发送写入" + (i + 1) + ":" + SocketClientUtil.bytesToHexString(sendBytes));
                                //发送完将值设置为-1
                                nowSet[i][2] = -1;
                                break;
                            }
                        }
                        //关闭tcp连接;判断是否请求完毕
                        if (isWriteFinish) {
                            //移除接收超时
                            this.removeMessages(TIMEOUT_RECEIVE);
                            SocketClientUtil.close(mClientUtilWriter);
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
//                            //解析int值
//                            int value0 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 1, 0, 1));
//                            //更新ui
//                            String content = readStr + ":" + value0;
//                            if (nowPos == 0) {
//                                mTvContent1.setText(content);
//                            } else if (nowPos == 1) {
//                                mTvContent2.setText(content);
//                            } else if (nowPos == 2) {
//                                mTvContent3.setText(content);
//                            } else if (nowPos == 3) {
//                                mTvContent4.setText(content);
//                            }
                            toast(titles[mType][nowPos] + ":" + getString(R.string.all_success), Toast.LENGTH_SHORT);
                            //继续发送设置命令
                            mHandlerWrite.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        } else {
                            toast(titles[mType][nowPos] + ":" + getString(R.string.all_failed));
                            //将内容设置为-1初始值
                            initValue(nowSet);
                            //关闭tcp连接
                            SocketClientUtil.close(mClientUtilWriter);
                            BtnDelayUtil.refreshFinish();
                        }
                        LogUtil.i("接收写入" + (nowPos + 1) + ":" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        //关闭tcp连接
                        SocketClientUtil.close(mClientUtilWriter);
                        BtnDelayUtil.refreshFinish();
                    }
                    break;
                default:
                    BtnDelayUtil.dealTLXBtnWrite(this,what,mContext,mBtnSetting,mTvRight);
                    break;
            }
        }
    };

    public void initValue(int[][] nowValue) {
        //将内容设置为-1初始值
        for (int i = 0; i < nowSet.length; i++) {
            nowValue[i][2] = -1;
        }
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
                    byte[] sendBytesR = SocketClientUtil.sendMsgToServer(mClientUtilRead, funs[mType]);
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
                            int value1 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 0, 0, 1));
                            int value2 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 2, 0, 1));
                            int value3 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 4, 0, 1));
                            int value4 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 6, 0, 1));
                            //更新ui
                            try {
                                mTvContent1.setText(readStr + ":" + getReadValueReal(0,value1));
                            } catch (Exception e) {
                                e.printStackTrace();
                                mTvContent1.setText(readStr + ":" + value1);
                            }
                            try {
                                mTvContent2.setText(readStr + ":" + getReadValueReal(1,value2));
                            } catch (Exception e) {
                                e.printStackTrace();
                                mTvContent2.setText(readStr + ":" + value2);
                            }
                            try {
                                mTvContent3.setText(readStr + ":" + getReadValueReal(2,value3));
                            } catch (Exception e) {
                                e.printStackTrace();
                                mTvContent3.setText(readStr + ":" + value3);
                            }
                            try {
                                mTvContent4.setText(readStr + ":" + getReadValueReal(3,value4));
                            } catch (Exception e) {
                                e.printStackTrace();
                                mTvContent4.setText(readStr + ":" + value4);
                            }
                            toast(R.string.all_success);
                        } else {
                            toast(R.string.all_failed);
                        }
                        LogUtil.i("接收读取：" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        SocketClientUtil.close(mClientUtilRead);
                        BtnDelayUtil.refreshFinish();
                    }
                    break;
                default:
                    BtnDelayUtil.dealTLXBtn(this,what,mContext,mBtnSetting,mTvRight);
                    break;
            }
        }
    };


    @OnClick(R.id.btnSetting)
    public void onViewClicked() {
        //获取用户输入内容
        String content1 = mEtContent1.getText().toString();
        String content2 = mEtContent2.getText().toString();
        String content3 = mEtContent3.getText().toString();
        String content4 = mEtContent4.getText().toString();
        if (TextUtils.isEmpty(content1)
                && TextUtils.isEmpty(content2)
                && TextUtils.isEmpty(content3)
                && TextUtils.isEmpty(content4)
        ) {
            toast(R.string.all_blank);
        } else {
            try {
                if (!TextUtils.isEmpty(content1)) {
                    nowSet[0][2] = getWriteValueReal(0,Float.parseFloat(content1));
                }
                if (!TextUtils.isEmpty(content2)) {
                    nowSet[1][2] = getWriteValueReal(1,Float.parseFloat(content2));
                }
                if (!TextUtils.isEmpty(content3)) {
                    nowSet[2][2] = getWriteValueReal(2,Float.parseFloat(content3));
                }
                if (!TextUtils.isEmpty(content4)) {
                    nowSet[3][2] = getWriteValueReal(3,Float.parseFloat(content4));
                }
                writeRegisterValue();
            } catch (NumberFormatException e) {
                e.printStackTrace();
                toast(getString(R.string.m363设置失败));
                //初始化设置值
                initValue(nowSet);
            } finally {
                mEtContent1.setText("");
                mEtContent2.setText("");
                mEtContent3.setText("");
                mEtContent4.setText("");
            }
        }
    }
}
