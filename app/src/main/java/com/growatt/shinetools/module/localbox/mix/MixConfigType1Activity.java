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

import com.growatt.shinetools.R;
import com.growatt.shinetools.base.DemoBase;
import com.growatt.shinetools.modbusbox.Arith;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.MaxWifiParseUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.utils.BtnDelayUtil;
import com.growatt.shinetools.utils.CommenUtils;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.Mydialog;
import com.growatt.shinetools.utils.Position;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 单个寄存器输入设置
 */
public class MixConfigType1Activity extends DemoBase {

    String readStr;
    String notSetStr ;
    @BindView(R.id.tvTitle1)
    TextView mTvTitle1;
    @BindView(R.id.etContent1)
    EditText mEtContent1;
    @BindView(R.id.tvContent1)
    TextView mTvContent1;
    @BindView(R.id.btnSetting)
    Button mBtnSetting;
    @BindView(R.id.headerView)
    View headerView;
    @BindView(R.id.tvRight)
    TextView mTvRight;
    private String mTitle;
    private int mType = -1;
    //读取命令功能码（功能码，开始寄存器，结束寄存器）
    private int[][] funs;
    //设置功能码集合（功能码，寄存器，数据）
    private int[][][] funsSet;
    private int[] nowSet = new int[3];
    private float[] mMultiples;//倍数集合
    private float mMul = 1;//当前倍数
    private String[] mUnits;
    private String mUnit = "";//当前单位
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mix_config_type1);
        ButterKnife.bind(this);
        initIntent();
        initString();
        initHeaderView();
        initUI();
    }

    private void initUI() {
        //屏蔽设置项
        switch (mType){
            case 13:
                CommenUtils.hideAllView(View.INVISIBLE,mBtnSetting);
                break;
        }
    }

    private void initIntent() {
        Intent mIntent = getIntent();
        if (mIntent != null) {
            mTitle = mIntent.getStringExtra("title");
            mType = mIntent.getIntExtra("type", -1);
        }
    }





    private void initString() {
        readStr = getString(R.string.m369读取值);
        notSetStr = getString(R.string.m393暂不允许设置);
        //读取命令功能码（功能码，开始寄存器，结束寄存器）
        funs = new int[][]{
                {3, 3, 3}//有功功率百分比3
                , {3, 4, 4}//无功功率百分比4
                , {3, 5, 5}//功率因子5
                , {3, 8, 8}//pv电压6
                , {3, 91, 91}//过频降额起点9 ----------4
                , {3, 92, 92}//负载限制10
                , {3, 107, 107}//无功延时11
                , {3, 108, 108}//过频降额延时12
                , {3, 109, 109}//曲线Q最大值13
                //参数设置
                , {3, 17, 17}//2启动电压 --------9
                , {3, 18, 18}//3启动时间
                , {3, 19, 19}//4故障回复后重启延迟时间
                , {3, 30, 30}//5通讯地址
                , {3, 51, 51}//7系统一周
                , {3, 80, 80}//8 ac 10分钟保护值 ------------14
                , {3, 81, 81}//9 pv 电压高故障
                , {3, 88, 88}//10 modbus版本
                , {3, 203, 203}//13 pid工作电压
                /*-----------------TLX设置 pos=18-------------------------*/
                , {3, 123, 123}//防逆流功率百分比
                , {3, 3000, 3000}//防逆流失效后默认百分比  ----------------19
                , {3, 3017, 3017}//干接点开通的功率百分比
                , {3, 3080, 3080}//离网电压
                , {3, 3081, 3081}//离网频率
                , {3, 3030, 3030}//cv电压23
                , {3, 3024, 3024}//cc电流24  ---------------24
                , {3, 3047, 3047}//充电功率百分比
                , {3, 3048, 3048}//充电停止soc
                , {3, 3036, 3036}//放电功率百分比
                , {3, 3037, 3037}//放电停止soc 28
                /*-----------------其他-------------------------*/
                , {3, 1, 1}//安规功能使能：max 和  tl-x   pos=29   --------------29
                , {3, 3019, 3019}//干接点关闭功率百分比
                /*--------------------MIX设置-------------------*/
                , {3, 152, 152}//mix 降载点设置
                , {3, 1045, 1045}//mix 充电功率百分比
                , {3, 1046, 1046}//mix 放电功率百分比

                , {3, 1007, 1007}//mix CV电压   --- index34
                , {3, 1000, 1000}//mix CC电流
                , {3, 1045, 1045}//mix 充电功率百分比
                , {3, 1091, 1091}//mix 充电停止SOC
                , {3, 1046, 1046}//mix 放电功率百分比
                , {3, 1071, 1071}//mix 放电停止SOC ---- index39
        };
        nowSet = new int[3];
        nowSet[0] = 6;
        nowSet[1] = funs[mType][1];
        nowSet[2] = -1;
        mTvTitle1.setText(mTitle);
        try {
            mMultiples = new float[]{
                    1,1,1,0.1f,0.01f
                    ,1,1,50,0.1f,0.1f
                    ,1,1,1,1,0.1f
                    ,0.1f,1,1,0.1f,0.1f
                    ,0.1f,1,1,0.01f,0.1f
                    ,1,1,1,1,1
                    ,0.1f,0.01f,1,1
                    ,0.01f,0.1f,1,1,1,1
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mUnits = new String[]{
                    "%","%","","V","Hz"
                    ,"","S","ms","%","V"
                    ,"S","S","","","V"
                    ,"V","","V","%","%"
                    ,"%","","","V","A"
                    ,"","","","",""
                    ,"","Hz","%","%"
                    ,"V","A","%","%","%","%"
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
    public String getReadValueReal(int read){
        String value = Arith.mul(read,mMul,2) + mUnit;
        //其他特殊处理
        switch (mType){
            case 13:value = getWeek(read);break;//系统一周
        }
        return value;
    }
    public String getWeek(int read){
        String value = String.valueOf(read);
        switch (read){
            case 0:value = getString(R.string.m41);break;
            case 1:value = getString(R.string.m35);break;
            case 2:value = getString(R.string.m36);break;
            case 3:value = getString(R.string.m37);break;
            case 4:value = getString(R.string.m38);break;
            case 5:value = getString(R.string.m39);break;
            case 6:value = getString(R.string.m40);break;
        }
        return value;
    }
    public int getWriteValueReal(double write){
        try {
            return (int) Math.round(Arith.div(write,mMul));
        } catch (Exception e) {
            e.printStackTrace();
            return (int) write;
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
                readRegisterValue();
            }
        });
    }

    //连接对象:用于读取数据
    private SocketClientUtil mClientUtilRead;
    //读取寄存器的值
    private void readRegisterValue() {
        Mydialog.Show(mContext);
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
                        sendBytes = SocketClientUtil.sendMsgToServer(mClientUtilWriter, nowSet);
                        LogUtil.i("发送写入：" + SocketClientUtil.bytesToHexString(sendBytes));
                    } else {
                        toast("系统错误，请重启应用");
                    }
                    break;
                //接收字节数组
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //检测内容正确性
                        MaxUtil.isCheckFull(mContext,bytes);
                        LogUtil.i("接收写入：" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
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
                            int value0 = MaxWifiParseUtil.obtainValueOne(bs);
                            try {
                                //更新ui
                                mTvContent1.setText(readStr + ":" + getReadValueReal(value0));
                            } catch (Exception e) {
                                e.printStackTrace();
                                mTvContent1.setText(readStr + ":" + value0);
                            }
                            toast(R.string.all_success);
                        } else {
                            toast(R.string.all_failed);
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
                default:
                    BtnDelayUtil.dealTLXBtn(this,what,mContext,mBtnSetting,mTvRight);
                    break;

            }
        }
    };

    @OnClick(R.id.btnSetting)
    public void onViewClicked() {
        if (mType == 3){
            toast(notSetStr);
            return;
        }
        //获取用户输入值
        String content = mEtContent1.getText().toString();
        if (TextUtils.isEmpty(content)) {
            toast(R.string.all_blank);
        } else {
            try {
                nowSet[2] = getWriteValueReal(Double.parseDouble(content));
                writeRegisterValue();
            } catch (NumberFormatException e) {
                e.printStackTrace();
                toast(getString(R.string.m363设置失败));
                nowSet[2] = -1;
            } finally {
                mEtContent1.setText("");
            }
        }
    }
}
