package com.growatt.shinetools.module.localbox.ustool;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.growatt.shinetools.R;
import com.growatt.shinetools.adapter.ReceiverAdapter;
import com.growatt.shinetools.adapter.SendAdapter;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.module.ProtectParamActivity;
import com.growatt.shinetools.module.localbox.ProtectParam1500VActivity;
import com.growatt.shinetools.utils.ActivityUtils;
import com.growatt.shinetools.utils.BtnDelayUtil;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.MyToastUtils;
import com.growatt.shinetools.utils.Mydialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class USAdvanceSetActivity extends BaseActivity implements Toolbar.OnMenuItemClickListener{


    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.header_title)
    LinearLayout headerTitle;
    @BindView(R.id.etCommand)
    EditText etCommand;
    @BindView(R.id.etRegisterAddress)
    EditText etRegisterAddress;
    @BindView(R.id.textView24)
    TextView textView24;
    @BindView(R.id.etLengthData)
    EditText etLengthData;
    @BindView(R.id.btnStart)
    Button btnStart;
    @BindView(R.id.tvClear)
    TextView tvClear;
    @BindView(R.id.rvSend)
    RecyclerView rvSend;
    @BindView(R.id.rvReceiver)
    RecyclerView rvReceiver;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.scrollView1)
    ScrollView scrollView1;
    @BindView(R.id.btnStop)
    Button btnStop;


    private String mTitle;
    //发送recyclerview
    private List<String> mSendList = new ArrayList<>();
    private SendAdapter mSendAdapter;
    //接收recyclerview
    private List<String> mReceiverList = new ArrayList<>();
    private ReceiverAdapter mReceiverAdapter;
    private boolean btnClick = true;
    private boolean isReceiveSucc = false;
    //    读取命令功能码（功能码，开始寄存器，结束寄存器）
    private int[] funRead;
    //设置功能码集合（功能码，寄存器，数据）
    private int[] funWrite;

    private String deviceType;

    private   MenuItem item;

    @Override
    protected int getContentView() {
        return R.layout.activity_us_advance_set;
    }

    @Override
    protected void initViews() {
        initIntent();
        initHeaderView();
        initString();
        initRecyclerView();
    }

    @Override
    protected void initData() {


    }


    private void initString() {
        funRead = new int[]{
                -1, -1, -1
        };
        funWrite = new int[]{
                -1, -1, -1
        };
    }

    private void initIntent() {
        Intent mIntent = getIntent();
        if (mIntent != null) {
            deviceType = mIntent.getStringExtra("deviceType");
        }
    }

    private void initHeaderView() {

        initToobar(toolbar);
        String title = getIntent().getStringExtra("title");
        if (!TextUtils.isEmpty(title)){
            tvTitle.setText(title);
        }
        toolbar.inflateMenu(R.menu.comment_right_menu);
        item = toolbar.getMenu().findItem(R.id.right_action);
        item.setTitle("");
        toolbar.setOnMenuItemClickListener(this);
        boolean isShow = getIntent().getBooleanExtra("isShow", false);
        if (isShow && getLanguage() == 0) {
            item.setTitle(R.string.保护参数);
        }

    }

    private void initRecyclerView() {
        rvSend.setLayoutManager(new LinearLayoutManager(this));
        mSendAdapter = new SendAdapter(R.layout.item_recylerview_textview, mSendList);
        rvSend.setAdapter(mSendAdapter);

        rvReceiver.setLayoutManager(new LinearLayoutManager(this));
        mReceiverAdapter = new ReceiverAdapter(R.layout.item_recylerview_textview_read, mReceiverList);
        rvReceiver.setAdapter(mReceiverAdapter);
    }


    /**
     * 跳转到系统wifi界面
     */
    private void jumpWifiList() {

        ActivityUtils.toWifiSet(this);
    }


//    private void connectSendMsg() {
////        isReceiveSucc = false;
//        //1.解析输入命令
////        parseInputCommand(mEtCommand.getText().toString().trim());
//        //2.tcp连接服务器
//        connectServer();
////        //3.发送数据
////        sendMsg();
//        //清空adapter数据
//        mSendAdapter.setNewData(new ArrayList<String>());
//        mReceiverAdapter.setNewData(new ArrayList<String>());
//    }


    private void updateSendAdapter(String sendMsg) {
        if (TextUtils.isEmpty(sendMsg)) return;
        String[] newDates = sendMsg.split(" ");
        List<String> newList = new ArrayList<>();
        for (String str : newDates) {
            newList.add(str);
        }
        mSendAdapter.setNewData(newList);
    }

    private void updateReceiveAdapter(String receiveMsg) {
        if (TextUtils.isEmpty(receiveMsg)) return;
        String[] newDates = receiveMsg.split(" ");
        List<String> newList = new ArrayList<>();
        for (String str : newDates) {
            newList.add(str);
        }
        mReceiverAdapter.setNewData(newList);
    }

    public void toast(String msg, int duration) {
//        Toast.makeText(this, msg, duration).show();
        MyToastUtils.toast(msg);
    }

    public void toast(String msg) {
        toast(msg, Toast.LENGTH_SHORT);
    }

    //    private byte[] sendMsg() {
//        //modbus协议封装
//        byte[] modbytes = modbusPro();
//        //数服协议封装
//        byte[] numBytes = numberServerPro(modbytes);
//        return numBytes;
//    }
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
                    BtnDelayUtil.sendMessageWrite(this);
                    sendBytes = SocketClientUtil.sendMsgToServer(mClientUtilWriter, funWrite);
                    //移除外部协议
                    byte[] bs = RegisterParseUtil.removePro(sendBytes);
                    String receiveMsg = SocketClientUtil.bytesToHexString(bs);
                    updateSendAdapter(receiveMsg);
                    //原始数据
                    String realS = SocketClientUtil.bytesToHexString(sendBytes);
                    LogUtil.i("发送写入：" + realS);
                    sb.append("Send write:<br>")
                            .append(realS)
                            .append("<br><br>");
                    textView.setText(Html.fromHtml(sb.toString()));
                    break;
                //接收字节数组
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
//                        boolean isCheck = Arrays.equals(sendBytes, bytes);
                        //移除外部协议
                        byte[] bs1 = RegisterParseUtil.removePro(bytes);
                        //检测内容正确性:06
                        if (MaxUtil.checkReceiver(bs1)) {
                            toast(R.string.all_success);
                            //移除modbus协议,只留数据06
                            byte[] contentBytes = RegisterParseUtil.removeFullPro(bytes);
                            String receiveMsg1 = SocketClientUtil.bytesToRegisterValueStr(contentBytes);
                            mReceiverAdapter.setStartRegister(funWrite[1]);
                            updateReceiveAdapter(receiveMsg1);
                        } else {
                            toast(R.string.all_failed);
                            String receiveMsg1 = SocketClientUtil.bytesToHexString(bs1);
                            mReceiverAdapter.setStartRegister(-1);
                            updateReceiveAdapter(receiveMsg1);
                        }

                        //原始数据
                        String realW = SocketClientUtil.bytesToHexString(bytes);
                        sb.append("接收写入:<br>")
                                .append(realW)
                                .append("<br><br>");
                        textView.setText(Html.fromHtml(sb.toString()));
                        LogUtil.i("Receive write：" + realW);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //关闭tcp连接
                        SocketClientUtil.close(mClientUtilWriter);
                        BtnDelayUtil.refreshFinish();
                    }
                    break;
                default:
                    BtnDelayUtil.dealMaxBtnWrite(this, what, mContext, btnStart);
                    break;
            }
        }
    };
    /**
     * 读取寄存器handle
     */
    StringBuilder sb = new StringBuilder();
    Handler mHandlerRead = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessage(this);
                    byte[] sendBytesR = SocketClientUtil.sendMsgToServer(mClientUtilRead, funRead);
                    String sendMsgFull = SocketClientUtil.bytesToHexString(sendBytesR);
                    String sendMsgRemove = SocketClientUtil.bytesToHexString(RegisterParseUtil.removePro(sendBytesR));
                    updateSendAdapter(sendMsgRemove);
                    LogUtil.i("发送读取：" + sendMsgFull);
                    //原始数据
                    sb.append("Send read:<br>")
                            .append(sendMsgFull)
                            .append("<br><br>");
                    textView.setText(Html.fromHtml(sb.toString()));
                    break;
                //接收字节数组
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //检测内容正确性
                        boolean isCheck = ModbusUtil.checkModbus(bytes);
                        //移除外部协议
                        byte[] bs = RegisterParseUtil.removePro(bytes);
                        if (isCheck) {
                            toast(R.string.all_success);
                            //移除modbus协议,只留数据06
                            byte[] contentBytes = RegisterParseUtil.removeFullPro(bytes);
                            String receiveMsg1 = SocketClientUtil.bytesToRegisterValueStr(contentBytes);
                            mReceiverAdapter.setStartRegister(funRead[1]);
                            updateReceiveAdapter(receiveMsg1);
                        } else {
                            toast(R.string.all_failed);
                            String receiveMsg = SocketClientUtil.bytesToHexString(bs);
                            mReceiverAdapter.setStartRegister(-1);
                            updateReceiveAdapter(receiveMsg);
                        }
                        //原始数据
                        String realW = SocketClientUtil.bytesToHexString(bytes);
                        sb.append("Receive read:<br>")
                                .append(realW)
                                .append("<br><br>");
                        textView.setText(Html.fromHtml(sb.toString()));
                        LogUtil.i("接收写入：" + realW);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //关闭tcp连接
                        SocketClientUtil.close(mClientUtilRead);
                        BtnDelayUtil.refreshFinish();
                    }
                    break;
                default:
                    BtnDelayUtil.dealMaxBtn(this, what, mContext, btnStart);
                    break;
            }
        }
    };
//
//    private byte[] numberServerPro(byte[] modbytes) {
//        CommandRequest17 comm = new CommandRequest17();
//        comm.setDatas(modbytes);
//        comm.setModbus_dataL((byte) modbytes.length);
//        comm.setNo_dataL((byte) (modbytes.length + 14));
//        //获得整体发送数据
//        byte[] datas = comm.getBytes();
//        return datas;
//    }
//
//    private byte[] modbusPro() {
//        ModbusQueryBean mod = new ModbusQueryBean();
//        String function = mEtCommand.getText().toString().trim();
//        String startRegis = mEtRegisterAddress.getText().toString().trim();
//        String dataLen = mEtLengthData.getText().toString().trim();
//        //功能码
//        function = isLenOne(function);
//        mod.setFunCode(MyByte.hexStringToByte(function));
//        //起始寄存器
//        startRegis= isLenOne(startRegis);
//        byte[] startBytes = MyByte.hexStringToBytes(startRegis);
//        if (startBytes != null && startBytes.length > 0){
//            if (startBytes.length > 1){
//                mod.setStartAdd_H(startBytes[0]);
//                mod.setStartAdd_L(startBytes[1]);
//            }else {
//                mod.setStartAdd_L(startBytes[0]);
//            }
//        }
//        //寄存器长度
//        dataLen= isLenOne(dataLen);
//        byte[] regLenBytes = MyByte.hexStringToBytes(dataLen);
//        if (regLenBytes != null && regLenBytes.length > 0){
//            if (regLenBytes.length > 1){
//                mod.setDataLen_H(regLenBytes[0]);
//                mod.setDataLen_L(regLenBytes[1]);
//            }else {
//                mod.setDataLen_L(regLenBytes[0]);
//            }
//        }
//        //获取crc之外modbus数据
//        byte[] datas = mod.getBytes();
//        //获取crc效验
//        int crc = CRC16.calcCrc16(datas);
//        byte[] crcBytes = MyByte.hexStringToBytes(String.format("%04x", crc));
//        //设置crc
//        mod.setCrc_H(crcBytes[1]);
//        mod.setCrc_L(crcBytes[0]);
//        //返回整个modbus数据，包含crc校验
//        return mod.getBytesCRC();
//    }
//
//    /**
//     * 10进制转16
//     */
//    public String isLenOne(String str){
//        try {
//            str= Integer.toHexString(Integer.parseInt(str));
//            if (!TextUtils.isEmpty(str)){
//                int len = str.length();
//                if (len == 1){
//                    str = "0" + str;
//                }else if (len == 2){
//                }else if (len == 3){
//                    str = "0" + str;
//                }else if (len == 4){
//                }else {
//                    toast("输入数据内容超范围。。");
//                    str = "ffff";
//                }
//            }else {
//                str = "00";
//            }
//            return str;
//        }catch (Exception e){
//            e.printStackTrace();
//            str = "00";
//            toast("请输入整数");
//            return str;
//        }
//    }

//    private Handler mHandler = new Handler(Looper.getMainLooper()) {
//        @Override
//        public void handleMessage(Message msg) {
//            String text = "";
//            int what = msg.what;
//            switch (what) {
//                case SocketClientUtil.SOCKET_EXCETION_CLOSE:
//                    String message = (String) msg.obj;
//                    text = "异常退出：" + message;
//                    break;
//                case SocketClientUtil.SOCKET_CLOSE:
//                    text = "连接关闭";
//                    break;
//                case SocketClientUtil.SOCKET_OPEN:
//                    text = "连接成功";
//                    break;
//                case SocketClientUtil.SOCKET_SEND_MSG:
//                    //记录请求时间
//                    startTime = System.currentTimeMillis();
//                    //禁用按钮
//                    btnClick = false;
//                    mHandler.sendEmptyMessageDelayed(100,3000);
//
//                    String sendMsg = (String) msg.obj;
//                    text = "发送消息成功";
////                    mEditText.setText("");
//                    sb.append("Client:<br>")
//                            .append(sendMsg)
//                            .append("<br><br>");
//                    mTextView.setText(Html.fromHtml(sb.toString()));
//                    //更新adapter
//                    updateSendAdapter(sendMsg);
//                    break;
//                case SocketClientUtil.SOCKET_RECEIVE_MSG:
//                    //设置接收消息成功
//                    isReceiveSucc = true;
//                    //设置请求按钮可用
//                    mHandler.sendEmptyMessage(100);
//
//
//                    String recMsg = (String) msg.obj;
//                    text = "接收消息成功";
//                    sb.append("Server:<br>")
//                            .append(recMsg)
//                            .append("<br><br>");
//                    mTextView.setText(Html.fromHtml(sb.toString()));
//                    //更新接收adapter
//                    updateReceiveAdapter(recMsg);
//                    break;
//                //接收字节数组
//                case SOCKET_RECEIVE_BYTES:
//                    byte[] bytes = (byte[]) msg.obj;
//                    //更新接收adapter
//                    break;
//                case SocketClientUtil.SOCKET_CONNECT:
//                    text = "socket已连接";
//                    break;
//                case SocketClientUtil.SOCKET_SEND:
//                    if (mClientUtil != null) {
//                        mClientUtil.sendMsg(sendMsg());
//                    }else {
//                        connectServer();
//                    }
//                    break;
//                case 100://恢复按钮点击
//                    btnClick = true;
//                    if (!isReceiveSucc){
//                        toast("接收消息失败，请重试。。。");
//                    }
//                    break;
//                case 101:
//                    connectSendMsg();
//                    break;
//                case SocketClientUtil.SOCKET_SERVER_SET://跳转到wifi列表
//                    jumpWifiList();
//                    break;
//            }
//            if (msg.what != SocketClientUtil.SOCKET_SEND && msg.what !=100 && msg.what != 101 && what!=6 && what != SocketClientUtil.SOCKET_RECEIVE_BYTES) {
//                toast(text);
//            }
//        }
//    };
//    //连接对象
//    private SocketClientUtil mClientUtil;
//    private void connectServer() {
//        mClientUtil = SocketClientUtil.connectServer(mHandler);
//    }


    @OnClick({R.id.tvClear, R.id.btnStop, R.id.btnStart})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvClear:
                if (sb != null) {
                    sb = new StringBuilder();
                }
                if (textView != null) {
                    textView.setText("");
                }
                break;
            case R.id.btnStop:
//                if (mClientUtil != null){
//                    mClientUtil.closeSocket();
//                }
                break;
            case R.id.btnStart:
                //判断sb数据大小，清空处理
                if (sb != null) {
                    String sbStr = sb.toString();
                    if (!TextUtils.isEmpty(sbStr) && sbStr.length() > 1 * 100 * 1024) {
                        textView.setText("");
                        sb = new StringBuilder();
                    }
                }
//                connectSendMsg();
                //清空命令设置数据
                clearSetData(funRead, funWrite);
                //清空adapter数据
                mSendAdapter.setNewData(new ArrayList<String>());
                mReceiverAdapter.setStartRegister(-1);
                mReceiverAdapter.setNewData(new ArrayList<String>());
                //判空
                if (isEmpty(etCommand, etLengthData, etRegisterAddress)) return;
                try {
                    //解析数据
                    String commType = etCommand.getText().toString();
                    String startRegister = etRegisterAddress.getText().toString();
                    String lenOrData = etLengthData.getText().toString();
                    int funType = Integer.parseInt(commType);
                    int funRegist = Integer.parseInt(startRegister);
                    int funLenOrData = Integer.parseInt(lenOrData);
                    if (funType == 6) {
                        funWrite[0] = funType;
                        funWrite[1] = funRegist;
                        funWrite[2] = funLenOrData;
                        writeRegisterValue();
                    } else {
                        funRead[0] = funType;
                        funRead[1] = funRegist;
                        funRead[2] = funLenOrData + funRegist - 1;
                        readRegisterValue();
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    toast(getString(R.string.m363设置失败));
                    clearSetData(funRead, funWrite);
                }
                break;
        }
    }

    private void clearSetData(int[]... datas) {
        if (datas != null) {
            for (int i = 0; i < datas.length; i++) {
                int[] data = datas[i];
                if (data != null) {
                    for (int j = 0; j < data.length; j++) {
                        data[j] = -1;
                    }
                }
            }
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.right_action:
                if ("MAX 230KTL3 HV".equals(deviceType)) {
                    ActivityUtils.gotoActivity(this, ProtectParam1500VActivity.class,false);
                } else {
                    ActivityUtils.gotoActivity(this, ProtectParamActivity.class,false);
                }
                break;
        }
        return true;
    }
}
