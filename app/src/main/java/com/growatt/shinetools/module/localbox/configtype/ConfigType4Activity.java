package com.growatt.shinetools.module.localbox.configtype;

import android.content.Context;
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

import static com.growatt.shinetools.utils.BtnDelayUtil.TIMEOUT_RECEIVE;


/**
 * 4个寄存器分别设置
 */
public class ConfigType4Activity extends BaseActivity implements Toolbar.OnMenuItemClickListener{

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
    @BindView(R.id.tvTitle2)
    TextView tvTitle2;
    @BindView(R.id.etContent2)
    EditText etContent2;
    @BindView(R.id.tvContent2)
    TextView tvContent2;
    @BindView(R.id.tvTitle3)
    TextView tvTitle3;
    @BindView(R.id.etContent3)
    EditText etContent3;
    @BindView(R.id.tvContent3)
    TextView tvContent3;
    @BindView(R.id.tvTitle4)
    TextView tvTitle4;
    @BindView(R.id.etContent4)
    EditText etContent4;
    @BindView(R.id.tvContent4)
    TextView tvContent4;
    @BindView(R.id.btnSetting)
    Button btnSetting;
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

    private Context mContext;
    @Override
    protected int getContentView() {
        mContext=this;
        return R.layout.activity_config_type4;
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


    private void initString() {
        readStr = getString(R.string.android_key811);
        //读取命令功能码（功能码，开始寄存器，结束寄存器）
        funs = new int[][]{
                {3, 110, 116}//28
                , {3, 111, 117}//29
                //参数设置
                , {3, 90, 96}//28  old inv
                , {3, 91, 97}//29
        };
        funSet = new int[][]{
                {110, 112, 114, 116}//28
                , {111, 113, 115, 117}//29
                , {90, 92, 94, 96}//28
                , {91, 93, 95, 97}//29
        };
        //内容标题显示容器
        titles = new String[][]{
                {String.format("%s%s", getString(R.string.android_key880), "1"),
                        String.format("%s%s", getString(R.string.android_key880), "2"),
                        String.format("%s%s", getString(R.string.android_key880), "3"),
                        String.format("%s%s", getString(R.string.android_key880), "4")
                },
                {String.format("%s%s", getString(R.string.android_key883), "1"),
                        String.format("%s%s", getString(R.string.android_key883), "2"),
                        String.format("%s%s", getString(R.string.android_key883), "3"),
                        String.format("%s%s", getString(R.string.android_key883), "4")
                },
                {String.format("%s%s", getString(R.string.android_key880), "1"),
                        String.format("%s%s", getString(R.string.android_key880), "2"),
                        String.format("%s%s", getString(R.string.android_key880), "3"),
                        String.format("%s%s", getString(R.string.android_key880), "4")
                },
                {String.format("%s%s", getString(R.string.android_key883), "1"),
                        String.format("%s%s", getString(R.string.android_key883), "2"),
                        String.format("%s%s", getString(R.string.android_key883), "3"),
                        String.format("%s%s", getString(R.string.android_key883), "4")
                }
//                {"PF限制负载百分比点1(110)", "PF限制负载百分比点2(112)", "PF限制负载百分比点3(114)", "PF限制负载百分比点4(116)"}
//                , {"PF限制功率因数1(111)", "PF限制功率因数2(113)", "PF限制功率因数3(115)", "PF限制功率因数4(117)"}
                //参数设置
        };
        tvTitle1.setText(titles[mType][0]);
        tvTitle2.setText(titles[mType][1]);
        tvTitle3.setText(titles[mType][2]);
        tvTitle4.setText(titles[mType][3]);
        //需要设置的内容
        nowSet = new int[][]{
                {6, funSet[mType][0], -1}
                , {6, funSet[mType][1], -1}
                , {6, funSet[mType][2], -1}
                , {6, funSet[mType][3], -1}
        };
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
//                                tvContent1.setText(content);
//                            } else if (nowPos == 1) {
//                                tvContent2.setText(content);
//                            } else if (nowPos == 2) {
//                                tvContent3.setText(content);
//                            } else if (nowPos == 3) {
//                                tvContent4.setText(content);
//                            }
                            toast(titles[mType][nowPos] + ":" + getString(R.string.android_key121));
                            //继续发送设置命令
                            mHandlerWrite.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        } else {
                            toast(titles[mType][nowPos] + ":" + getString(R.string.android_key3129));
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
                    BtnDelayUtil.dealMaxBtnWrite(this, what, mContext, btnSetting, toolbar);
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
                            tvContent1.setText(readStr + ":" + value1);
                            tvContent2.setText(readStr + ":" + value2);
                            tvContent3.setText(readStr + ":" + value3);
                            tvContent4.setText(readStr + ":" + value4);
                            toast(R.string.android_key121);
                        } else {
                            toast(R.string.android_key3129);
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
                    BtnDelayUtil.dealMaxBtn(this, what, mContext, btnSetting, toolbar);
                    break;
            }
        }
    };


    @OnClick(R.id.btnSetting)
    public void onViewClicked() {
        //获取用户输入内容
        String content1 = etContent1.getText().toString();
        String content2 = etContent2.getText().toString();
        String content3 = etContent3.getText().toString();
        String content4 = etContent4.getText().toString();
        if (TextUtils.isEmpty(content1)
                && TextUtils.isEmpty(content2)
                && TextUtils.isEmpty(content3)
                && TextUtils.isEmpty(content4)
        ) {
            toast(R.string.android_key1945);
        } else {
            try {
                if (!TextUtils.isEmpty(content1)) {
                    nowSet[0][2] = Integer.parseInt(content1);
                }
                if (!TextUtils.isEmpty(content2)) {
                    nowSet[1][2] = Integer.parseInt(content2);
                }
                if (!TextUtils.isEmpty(content3)) {
                    nowSet[2][2] = Integer.parseInt(content3);
                }
                if (!TextUtils.isEmpty(content4)) {
                    nowSet[3][2] = Integer.parseInt(content4);
                }
                writeRegisterValue();
            } catch (NumberFormatException e) {
                e.printStackTrace();
                toast(getString(R.string.android_key539));
                //初始化设置值
                initValue(nowSet);
            } finally {
                etContent1.setText("");
                etContent2.setText("");
                etContent3.setText("");
                etContent4.setText("");
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
