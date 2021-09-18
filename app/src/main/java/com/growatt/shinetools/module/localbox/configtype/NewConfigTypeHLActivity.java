package com.growatt.shinetools.module.localbox.configtype;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.growatt.shinetools.R;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.modbusbox.Arith;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.MaxWifiParseUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.utils.BtnDelayUtil;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.Mydialog;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 单个寄存器输入设置
 */
public class NewConfigTypeHLActivity extends BaseActivity  implements Toolbar.OnMenuItemClickListener{

    String readStr;
    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.header_title)
    LinearLayout headerTitle;
    @BindView(R.id.tvTitle1)
    TextView tvTitle1;
    @BindView(R.id.etContent1)
    EditText etContent1;
    @BindView(R.id.tvContent1)
    TextView tvContent1;
    @BindView(R.id.btnSetting)
    Button btnSetting;
    private String mTitle;
    private int mType = -1;
    //读取命令功能码（功能码，开始寄存器，结束寄存器）
    private int[][] funs;
    //设置功能码集合（功能码，寄存器，数据）
    private int[][][] funsSet;
    private int[][] nowSet;
    private float[] mMultiples;//倍数集合
    private float mMul = 1;//当前倍数
    private String[] mUnits;
    private String mUnit = "";//当前单位

    @Override
    protected int getContentView() {
        return R.layout.activity_new_config_type_hl;
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initData() {
        initIntent();
        initHeaderView();
        initString();
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
        //读取命令功能码（功能码，开始寄存器，结束寄存器）
        funs = new int[][]{
                //参数设置
                {3, 28, 29}//14逆变器模块
                , {3, 7147, 7148}//修改总发电量
        };
        //需要设置的内容
        nowSet = new int[][]{
                {6, funs[mType][1], -1}
                , {6, funs[mType][2], -1}
        };
        tvTitle1.setText(mTitle);
        try {
            mMultiples = new float[]{
                    1, 0.1f
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mUnits = new String[]{
                    "", "kWh"
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

    private void initHeaderView() {
        initToobar(toolbar);
        tvTitle.setText(mTitle);
        toolbar.inflateMenu(R.menu.comment_right_menu);
        toolbar.getMenu().findItem(R.id.right_action).setTitle(R.string.android_key816);
        toolbar.setOnMenuItemClickListener(this);
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
    Handler mHandlerWrite = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    if (nowSet != null) {
                        if (nowSet[0][2] == -1 && nowSet[1][2] == -1) {
                            //关闭tcp连接
                            SocketClientUtil.close(mClientUtilWriter);
                            BtnDelayUtil.refreshFinish();
                        } else {
                            for (int len = nowSet.length, i = 0; i < len; i++) {
                                if (nowSet[i][2] != -1) {
                                    BtnDelayUtil.sendMessageWrite(this);
                                    sendBytes = SocketClientUtil.sendMsgToServer(mClientUtilWriter, nowSet[i]);
                                    LogUtil.i("发送写入" + (i + 1) + ":" + SocketClientUtil.bytesToHexString(sendBytes));
                                    //发送完将值设置为-1
                                    nowSet[i][2] = -1;
                                    break;
                                }
                            }
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
                            byte[] bs = RegisterParseUtil.removePro17Fun6(bytes);
                            //解析int值
//                            int value0 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,1,0,1));

                            toast(getString(R.string.all_success));
                            //继续发送设置命令
                            mHandlerWrite.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        } else {
                            toast(getString(R.string.all_failed));
                            //将内容设置为-1初始值
                            nowSet[0][2] = -1;
                            nowSet[1][2] = -1;
                            //关闭tcp连接
                            SocketClientUtil.close(mClientUtilWriter);
                            BtnDelayUtil.refreshFinish();
                        }
                        LogUtil.i("接收写入" + ":" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        //关闭tcp连接
                        SocketClientUtil.close(mClientUtilWriter);
                        BtnDelayUtil.refreshFinish();
                    }
                    break;
                default:
                    BtnDelayUtil.dealMaxBtnWrite(this, what, mContext, btnSetting, toolbar);
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
                            int value0 = MaxWifiParseUtil.obtainValueHAndL(bs);
                            try {
                                //更新ui
                                tvContent1.setText(readStr + ":" + getReadValueReal(value0));
                            } catch (Exception e) {
                                e.printStackTrace();
                                tvContent1.setText(readStr + ":" + value0);
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
                    BtnDelayUtil.dealMaxBtn(this, what, mContext, btnSetting, toolbar);
                    break;
            }
        }
    };

    public String getReadValueReal(int read) {
        String value = Arith.mul(read, mMul, 2) + mUnit;
        //其他特殊处理
        switch (mType) {
//            case 13:value = getWeek(read);break;//系统一周
        }
        return value;
    }

    @OnClick(R.id.btnSetting)
    public void onViewClicked() {
        //获取用户输入值
        String content = etContent1.getText().toString();
        if (TextUtils.isEmpty(content)) {
            toast(R.string.all_blank);
        } else {
            try {
                int low = getWriteValueReal(Double.parseDouble(content));
                int high = 0;
                if (low > 0xffff) {
                    high = low - 0xffff;
                    low = 0xffff;
                }
                nowSet[0][2] = high;
                nowSet[1][2] = low;
                writeRegisterValue();
            } catch (NumberFormatException e) {
                e.printStackTrace();
                toast(getString(R.string.m363设置失败));
                nowSet[0][2] = -1;
                nowSet[1][2] = -1;
            } finally {
                etContent1.setText("");
            }
        }
    }

    public int getWriteValueReal(double write) {
        try {
            return (int) Math.round(Arith.div(write, mMul));
        } catch (Exception e) {
            e.printStackTrace();
            return (int) write;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.right_action:
                //读取寄存器的值
                readRegisterValue();
                break;
        }
        return true;
    }

}
