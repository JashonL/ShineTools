package com.growatt.shinetools.module.localbox.mix;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
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
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.Mydialog;
import com.growatt.shinetools.utils.Position;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.view.listener.OnLvItemClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.growatt.shinetools.utils.BtnDelayUtil.TIMEOUT_RECEIVE;


public class MixConfigType1AndPFActivity extends DemoBase {
    String readStr ;
    @BindView(R.id.tvTitle1)
    TextView mTvTitle1;
    @BindView(R.id.etContent1)
    EditText mEtContent1;
    @BindView(R.id.tvContent1)
    TextView mTvContent1;
    @BindView(R.id.tvTitle2)
    TextView mTvTitle2;
    @BindView(R.id.btnSelect)
    Button mBtnSelect;
    @BindView(R.id.tvContent2)
    TextView mTvContent2;
    @BindView(R.id.btnSetting)
    Button mBtnSetting;
    @BindView(R.id.headerView)
    View headerView;
    @BindView(R.id.tvRight)
    TextView mTvRight;
    //传递数据
    private String mTitle;
    private int mType = -1;
    //弹框选择item
    private String[][] items;
    private String[] nowItems;
    //读取命令功能码（功能码，开始寄存器，结束寄存器）
    private int[][] funs;
    //设置的内容
    private int[][] nowSet;
    private int[][][] nowSetFull;
    //内容标题显示容器
    private String[][] titles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mix_config_type1_and_pf);
        ButterKnife.bind(this);
        initIntent();
        initString();
        initHeaderView();
    }

    private void initString() {
        readStr = getString(R.string.m369读取值);
        //需要设置的内容
        /*设置容性PF：先5写（10000+设置值*10000）；89(Hold)写1；
          设置感性PF：先5写（10000-设置值*10000）；89(Hold)写1；
          显示值=(读取值-10000)/10000
         */
        nowSetFull = new int[][][]{
                {{6, 2, 1}, {6, 3, -1}},
                {{6, 2, 1}, {6, 4, -1}, {6, 89, 5}},
                {{6, 2, 1}, {6, 4, -1}, {6, 89, 4}},
                {{6, 2, 1}, {6, 5, -1}, {6, 89, 1}},
                {{6, 2, 1}, {6, 5, -1}, {6, 89, 1}}
        };
        nowSet = nowSetFull[mType];
        //读取命令功能码（功能码，开始寄存器，结束寄存器）
        funs = new int[][]{
                {3, 0, 5}
        };
        //弹框选择的数据
        items = new String[][]{
                {getString(R.string.m394不记忆), getString(R.string.m378记忆)},
        };
        //内容标题显示容器
        titles = new String[][]{
                {getString(R.string.m377记忆使能),mTitle},
                { getString(R.string.m377记忆使能),mTitle,""},
                { getString(R.string.m377记忆使能),mTitle,""},
                { getString(R.string.m377记忆使能),mTitle,""},
                {getString(R.string.m377记忆使能),mTitle,""},
                { getString(R.string.m377记忆使能),mTitle,""}
        };
        mTvTitle1.setText(titles[0][1]);
        mTvTitle2.setText(titles[0][0]);
        nowItems = items[0];
        //设置默认使能值
        mBtnSelect.setText(nowItems[1] + "(1)");
        nowSet[0][2] = 1;
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
                //读取寄存器的值
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
                            //移除外部协议
                            byte[] bs = RegisterParseUtil.removePro17(bytes);
                            int content1 = 0;
//                            int content2 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes125(bs,2,0,1));
                            //更新ui
                            switch (mType){
                                case 0:
                                    content1 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes125(bs,3,0,1));
                                    mTvContent1.setText(readStr + ":" + content1);
                                    break;
                                case 1:case 2:
                                    content1 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes125(bs,4,0,1));
                                    mTvContent1.setText(readStr + ":" + content1);
                                    break;
                                case 3:case 4:
                                    content1 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes125(bs,5,0,1));
                                    double result2 = Arith.div(content1-10000,10000);
                                    if (result2 == -1) result2 = 1;
                                    mTvContent1.setText(readStr + ":" + result2);
                                    break;
                            }
//                            mTvContent2.setText(readStr + ":" + content2);
                            toast(R.string.all_success);
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
                    BtnDelayUtil.dealTLXBtn(this,what,mContext,mBtnSetting,mTvRight);
                    break;
            }
        }
    };
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
    //当前发送的字节数组:{6, 2, 1}, {6, 4, -1}, {6, 89, 5}
    private byte[] sendBytes;
    //当前设置项
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
                        if (nowPos >= nowSet.length-1) {
                            nowPos = -1;
                            //关闭tcp连接
                            if (mClientUtilWriter != null) {
                                mClientUtilWriter.closeSocket();
                                BtnDelayUtil.refreshFinish();
                                //移除接收超时
                                this.removeMessages(TIMEOUT_RECEIVE);
                            }
                        } else {
                            nowPos = nowPos + 1;
                            sendBytes = SocketClientUtil.sendMsgToServer(mClientUtilWriter, nowSet[nowPos]);
                            LogUtil.i("发送写入" + (nowPos + 1) + ":" + SocketClientUtil.bytesToHexString(sendBytes));
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
                            toast(titles[mType][nowPos] + ":" + getString(R.string.all_success));
                            //继续发送设置命令
                            mHandlerWrite.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        } else {
                            toast(titles[mType][nowPos] + ":" + getString(R.string.all_failed));
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
                new CircleDialog.Builder()
                        .setWidth(0.7f)
                        .setGravity(Gravity.CENTER)
                        .setTitle(getString(R.string.countryandcity_first_country))
                        .setItems(nowItems, new OnLvItemClickListener() {
                            @Override
                            public boolean onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                if (nowItems != null && nowItems.length > position) {
                                    mBtnSelect.setText(nowItems[position] + "(" + position + ")");
                                    nowSet[0][2] = position;
                                }
                                return true;
                            }
                        })
                        .show(getSupportFragmentManager());
                break;
            case R.id.btnSetting:
                nowPos = -1;
                //获取用户输入内容
                String content1 = mEtContent1.getText().toString();
                if (TextUtils.isEmpty(content1)){
                    toast(R.string.all_blank);
                    return;
                }
                try {
                    double result = Double.parseDouble(content1);
                    switch (mType){
                        case 3:
//                            result = Arith.add(10000,Arith.mul(result,10000));
                            result = Arith.sub(10000,Arith.mul(result,10000));
                            break;
                        case 4:
//                            result = Arith.sub(10000,Arith.mul(result,10000));
                            result = Arith.add(10000,Arith.mul(result,10000));
                            break;
                    }
                    nowSet[1][2] = (int) result;
                    writeRegisterValue();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    toast(getString(R.string.m363设置失败));
                }
                break;
        }
    }
}
