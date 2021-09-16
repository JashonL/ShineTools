package com.growatt.shinetools.module.localbox.configtype;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.growatt.shinetools.R;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.MaxWifiParseUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.utils.BtnDelayUtil;
import com.growatt.shinetools.utils.CircleDialogUtils;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.Mydialog;
import com.mylhyl.circledialog.view.listener.OnLvItemClickListener;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * 单个寄存器选择设置
 * pos==13设置国家进行修改逻辑
 */
public class ConfigTypeSelectActivity extends BaseActivity implements Toolbar.OnMenuItemClickListener{
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
    @BindView(R.id.btnSelect)
    Button btnSelect;
    @BindView(R.id.tvContent1)
    TextView tvContent1;
    @BindView(R.id.btnSetting)
    Button btnSetting;
    private String mTitle;
    private int mType = -1;
    //弹框选择item
    private String[][] items;
    private String[] nowItems;
    private int nowPos = -1;//当前选择下标
    //读取命令功能码（功能码，开始寄存器，结束寄存器）
    private int[][] funs;
    //设置功能码集合（功能码，寄存器，数据）
    private int[][][] funsSet;
    private int[] nowSet = null;
    //国家相关model
    private String[][][] modelToal;
    private Context mContext;
    @Override
    protected int getContentView() {
        mContext=this;
        return R.layout.activity_config_type_select;
    }

    @Override
    protected void initViews() {
        initIntent();
        initHeaderView();
    }

    @Override
    protected void initData() {
        initString();
    }


    private void initString() {
//        国家相关选择Map
        //安规大类{黄色安规号，蓝色安规号，绿色安规号、灰色安规号，#红色安规号(无)}
        // --》{具体安规号+安规值}
        modelToal = new String[][][]{
                {{"A0S1", "VDE0126"}, {"A0S4", "Italy"}, {"A0S7", "Germany"}, {"A0SB", "EN50438_Standard"}, {"A0SD", "Belgium"}, {"A1S3", "Demark"}, {"A1S4", "EN50438_Sweden"}, {"A1S5", "EN50438_Norway"}, {"A1S7", "France"}, {"A1SA", "CEI0-16"}, {"A1SB", "DEWA"}, {"A1SC", "BDEW"}},
                {{"A0S2", "UK_G59"}, {"A0S8", "UK_G83"}, {"A0S9", "EN50438_Ireland"}},
                {{"A0S3", "AS4777_Australia"}, {"A1S0", "AS4777_Newzealand"}},
                {{"A0SE", "MEA"}, {"A0SF", "PEA"}}
        };
        readStr = getString(R.string.android_key811);
        //读取命令功能码（功能码，开始寄存器，结束寄存器）
        funs = new int[][]{
                {3, 0, 0}//开关机0
                , {3, 1, 1}//安规使能1
                , {3, 2, 2}//记忆使能2
                , {3, 22, 22}//通讯波特率7
                , {3, 89, 89}//pf模式8  ----------4
                , {3, 230, 230}//island使能14
                , {3, 231, 231}//风扇检查15
                , {3, 232, 232}//n线使能16
                , {3, 235, 235}//监控功能使能17
                , {3, 236, 236}//电压范围使能18 ---------9
                , {3, 237, 237}//island使能19
                , {3, 399, 399}//MPPT使能20
                //参数设置
                , {3, 15, 15}//语言0
                , {3, 16, 16}//国家1
                , {3, 201, 201}//pid模式11 --------14
                , {3, 202, 202}//pid开关12
                /*-----------------TLX设置16-------------------------*/
                , {3, 122, 122}//防逆流使能
                , {3, 3016, 3016}//干接点使能
                , {3, 3068, 3068}//ct种类选择
                , {3, 3070, 3070}//电池种类 ---------19
                , {3, 3079, 3079}//离网功能使能
                , {3, 3049, 3049}//AC充电使能21
                , {3, 533, 533}//功率采集
                , {3, 3021, 3021}//手动离网使能
                , {3, 141, 141}//夜间SVG功能使能----24
        };
        //弹框选择的数据
        items = new String[][]{
                {getString(R.string.android_key2047), getString(R.string.android_key2074)}
                , {getString(R.string.android_key2045), getString(R.string.android_key2073)}
                , {getString(R.string.android_key2045), getString(R.string.android_key2073)}
                , {"9600bps", "38400bps", "115200bps"}
                , {"PF=1", getString(R.string.PF值设置), getString(R.string.android_key760), getString(R.string.用户设定PF曲线), getString(R.string.滞后无功功率1), getString(R.string.滞后无功功率2), getString(R.string.QV模式), getString(R.string.正负无功值调节)}
                , {getString(R.string.android_key2073), getString(R.string.android_key2045)}
                , {getString(R.string.android_key2045), getString(R.string.android_key2073)}
                , {getString(R.string.android_key2045), getString(R.string.android_key2073)}
                , {getString(R.string.android_key2045), getString(R.string.android_key2073)}
                , {"0", "1", "2"}
                , {getString(R.string.android_key2045), getString(R.string.android_key2073)}
                , {"0", "1", "2"}
                //参数设置
//                , {"0", "1", "2", "3", "4", "5"}
                , {getString(R.string.android_key1417), getString(R.string.android_key1418), getString(R.string.android_key1420), getString(R.string.android_key1421),
                getString(R.string.android_key1422), getString(R.string.android_key1423), getString(R.string.android_key186)}
                , {"Need to Select", "Have Selected"}
                , {"Automatic", "Continual", "Overnight"}
                , {getString(R.string.android_key2073), getString(R.string.android_key2045)}
                /*-----------------TLX设置-------------------------*/
                , {getString(R.string.android_key2885), getString(R.string.android_key2886), getString(R.string.android_key2887), getString(R.string.android_key2888)}
                , {getString(R.string.android_key1686), getString(R.string.android_key1675)}
                , {"cWiredCT", "cWirelessCT", "METER"}
                , {"Lithium", "Lead-acid", "other"}
                , {getString(R.string.android_key1686), getString(R.string.android_key1675)}
                , {getString(R.string.android_key1686), getString(R.string.android_key1675)}
                , {getString(R.string.android_key3085), getString(R.string.android_key3086), "CT"}//不使能0，电表值是1  ct3
                , {getString(R.string.android_key1686), getString(R.string.android_key1675)}
                , {getString(R.string.android_key1686), getString(R.string.android_key1675)}
        };
        //设置功能码集合（功能码，寄存器，数据）
        funsSet = new int[][][]{
                {{6, 0, 0}, {6, 0, 1}}
                , {{6, 1, 0}, {6, 1, 1}}
                , {{6, 2, 0}, {6, 2, 1}}
                , {{6, 22, 0}, {6, 22, 1}, {6, 22, 2}}
                , {{6, 89, 0}, {6, 89, 1}, {6, 89, 2}, {6, 89, 3}, {6, 89, 4}, {6, 89, 5}, {6, 89, 6}, {6, 89, 7}}
                , {{6, 230, 0}, {6, 230, 1}}
                , {{6, 231, 0}, {6, 231, 1}}
                , {{6, 232, 0}, {6, 232, 1}}
                , {{6, 235, 0}, {6, 235, 1}}
                , {{6, 236, 0}, {6, 236, 1}, {6, 236, 2}}//-----9
                , {{6, 237, 0}, {6, 237, 1}}
                , {{6, 399, 0}, {6, 399, 1}, {6, 399, 2}}
                //参数设置
                , {{6, 15, 0}, {6, 15, 1}, {6, 15, 2}, {6, 15, 3}, {6, 15, 4}, {6, 15, 5}, {6, 15, 6}}
                , {{6, 16, 0}, {6, 16, 1}}
                , {{6, 201, 0}, {6, 201, 1}, {6, 201, 2}}
                , {{6, 202, 0}, {6, 202, 1}}
                /*-----------------TLX设置-------------------------*/
                , {{6, 122, 0}, {6, 122, 1}, {6, 122, 2}, {6, 122, 3}}
                , {{6, 3016, 0}, {6, 3016, 1}}
                , {{6, 3068, 0}, {6, 3068, 1}, {6, 3068, 2}}
                , {{6, 3070, 0}, {6, 3070, 1}, {6, 3070, 2}}//---19
                , {{6, 3079, 0}, {6, 3079, 1}}
                , {{6, 3049, 0}, {6, 3049, 1}}
                , {{6, 533, 0}, {6, 533, 1}, {6, 533, 3}}
                , {{6, 3021, 0}, {6, 3021, 1}}
                , {{6, 141, 0}, {6, 141, 1}}
        };
        nowItems = items[mType];
        tvTitle1.setText(mTitle);
        //有些设置进行隐藏
        if (mType == 4) {
            btnSelect.setVisibility(View.INVISIBLE);
            nowPos = 0;
            nowSet = funsSet[mType][nowPos];
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
        initToobar(toolbar);
        tvTitle.setText(mTitle);
        toolbar.inflateMenu(R.menu.comment_right_menu);
        toolbar.getMenu().findItem(R.id.right_action).setTitle(R.string.android_key816);
        toolbar.setOnMenuItemClickListener(this);
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
                    sendBytes = sendMsg(mClientUtilW, nowSet);
                    LogUtil.i("发送写入：" + SocketClientUtil.bytesToHexString(sendBytes));
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
//                            tvContent1.setText(readStr + ":" + value0);
                            toast(R.string.android_key121);
                        } else {
                            toast(R.string.android_key3129	);
                        }
                        LogUtil.i("接收写入：" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //关闭tcp连接
                        SocketClientUtil.close(mClientUtilW);
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
    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessage(this);
                    byte[] sendBytesR = sendMsg(mClientUtil, funs[mType]);
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
                            //更新ui
//                            tvContent1.setText(readStr + ":" + value0);
                            tvContent1.setText(getReadResult(value0));
                            toast(R.string.android_key121);
                        } else {
                            toast(R.string.android_key3129);
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
                    BtnDelayUtil.dealMaxBtn(this, what, mContext, btnSetting, toolbar);
                    break;
            }
        }
    };

    /**
     * 获取读取的值并解析，部分特殊处理
     *
     * @param value
     * @return
     */
    public String getReadResult(int value) {
        String result = "";
        switch (mType) {
            case 22:
                if (value == 3) value = 2;
                break;
        }
        try {
            if (TextUtils.isEmpty(result)) {
                result = readStr + ":" + items[mType][value];
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = readStr + ":" + value;
        }
        return result;
    }

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

    @OnClick({R.id.btnSelect, R.id.btnSetting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnSelect:

                CircleDialogUtils.showCommentItemDialog(this, getString(R.string.android_key1809), Arrays.asList(nowItems), Gravity.CENTER, new OnLvItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (nowItems != null && nowItems.length > position) {
                            try {
                                btnSelect.setText(nowItems[position] + "(" + funsSet[mType][position][2] + ")");
                            } catch (Exception e) {
                                e.printStackTrace();
                                btnSelect.setText(nowItems[position] + "(" + position + ")");
                            }
                            nowPos = position;
                        }
                        return true;
                    }
                }, null);
                break;
            case R.id.btnSetting:
                if (nowPos == -1) {
                    toast(getString(R.string.android_key565));
                } else {
                    nowSet = funsSet[mType][nowPos];
                    connectServerWrite();
                }
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
