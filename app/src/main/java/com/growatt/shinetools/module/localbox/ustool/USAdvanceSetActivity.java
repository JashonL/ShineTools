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
    //??????recyclerview
    private List<String> mSendList = new ArrayList<>();
    private SendAdapter mSendAdapter;
    //??????recyclerview
    private List<String> mReceiverList = new ArrayList<>();
    private ReceiverAdapter mReceiverAdapter;
    private boolean btnClick = true;
    private boolean isReceiveSucc = false;
    //    ????????????????????????????????????????????????????????????????????????
    private int[] funRead;
    //?????????????????????????????????????????????????????????
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
            item.setTitle(R.string.????????????);
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
     * ???????????????wifi??????
     */
    private void jumpWifiList() {

        ActivityUtils.toWifiSet(this);
    }


//    private void connectSendMsg() {
////        isReceiveSucc = false;
//        //1.??????????????????
////        parseInputCommand(mEtCommand.getText().toString().trim());
//        //2.tcp???????????????
//        connectServer();
////        //3.????????????
////        sendMsg();
//        //??????adapter??????
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
//        //modbus????????????
//        byte[] modbytes = modbusPro();
//        //??????????????????
//        byte[] numBytes = numberServerPro(modbytes);
//        return numBytes;
//    }
    //????????????:??????????????????
    private SocketClientUtil mClientUtilRead;
    //????????????:??????????????????
    private SocketClientUtil mClientUtilWriter;

    //?????????????????????
    private void readRegisterValue() {
        Mydialog.Show(mContext);
        mClientUtilRead = SocketClientUtil.connectServer(mHandlerRead);
    }

    //?????????????????????
    private void writeRegisterValue() {
        Mydialog.Show(mContext);
        mClientUtilWriter = SocketClientUtil.connectServer(mHandlerWrite);
    }

    /**
     * ????????????handle
     */
    //???????????????????????????
    private byte[] sendBytes;
    Handler mHandlerWrite = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //????????????
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessageWrite(this);
                    sendBytes = SocketClientUtil.sendMsgToServer(mClientUtilWriter, funWrite);
                    //??????????????????
                    byte[] bs = RegisterParseUtil.removePro(sendBytes);
                    String receiveMsg = SocketClientUtil.bytesToHexString(bs);
                    updateSendAdapter(receiveMsg);
                    //????????????
                    String realS = SocketClientUtil.bytesToHexString(sendBytes);
                    LogUtil.i("???????????????" + realS);
                    sb.append("Send write:<br>")
                            .append(realS)
                            .append("<br><br>");
                    textView.setText(Html.fromHtml(sb.toString()));
                    break;
                //??????????????????
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
//                        boolean isCheck = Arrays.equals(sendBytes, bytes);
                        //??????????????????
                        byte[] bs1 = RegisterParseUtil.removePro(bytes);
                        //?????????????????????:06
                        if (MaxUtil.checkReceiver(bs1)) {
                            toast(R.string.all_success);
                            //??????modbus??????,????????????06
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

                        //????????????
                        String realW = SocketClientUtil.bytesToHexString(bytes);
                        sb.append("????????????:<br>")
                                .append(realW)
                                .append("<br><br>");
                        textView.setText(Html.fromHtml(sb.toString()));
                        LogUtil.i("Receive write???" + realW);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //??????tcp??????
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
     * ???????????????handle
     */
    StringBuilder sb = new StringBuilder();
    Handler mHandlerRead = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //????????????
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessage(this);
                    byte[] sendBytesR = SocketClientUtil.sendMsgToServer(mClientUtilRead, funRead);
                    String sendMsgFull = SocketClientUtil.bytesToHexString(sendBytesR);
                    String sendMsgRemove = SocketClientUtil.bytesToHexString(RegisterParseUtil.removePro(sendBytesR));
                    updateSendAdapter(sendMsgRemove);
                    LogUtil.i("???????????????" + sendMsgFull);
                    //????????????
                    sb.append("Send read:<br>")
                            .append(sendMsgFull)
                            .append("<br><br>");
                    textView.setText(Html.fromHtml(sb.toString()));
                    break;
                //??????????????????
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //?????????????????????
                        boolean isCheck = ModbusUtil.checkModbus(bytes);
                        //??????????????????
                        byte[] bs = RegisterParseUtil.removePro(bytes);
                        if (isCheck) {
                            toast(R.string.all_success);
                            //??????modbus??????,????????????06
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
                        //????????????
                        String realW = SocketClientUtil.bytesToHexString(bytes);
                        sb.append("Receive read:<br>")
                                .append(realW)
                                .append("<br><br>");
                        textView.setText(Html.fromHtml(sb.toString()));
                        LogUtil.i("???????????????" + realW);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //??????tcp??????
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
//        //????????????????????????
//        byte[] datas = comm.getBytes();
//        return datas;
//    }
//
//    private byte[] modbusPro() {
//        ModbusQueryBean mod = new ModbusQueryBean();
//        String function = mEtCommand.getText().toString().trim();
//        String startRegis = mEtRegisterAddress.getText().toString().trim();
//        String dataLen = mEtLengthData.getText().toString().trim();
//        //?????????
//        function = isLenOne(function);
//        mod.setFunCode(MyByte.hexStringToByte(function));
//        //???????????????
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
//        //???????????????
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
//        //??????crc??????modbus??????
//        byte[] datas = mod.getBytes();
//        //??????crc??????
//        int crc = CRC16.calcCrc16(datas);
//        byte[] crcBytes = MyByte.hexStringToBytes(String.format("%04x", crc));
//        //??????crc
//        mod.setCrc_H(crcBytes[1]);
//        mod.setCrc_L(crcBytes[0]);
//        //????????????modbus???????????????crc??????
//        return mod.getBytesCRC();
//    }
//
//    /**
//     * 10?????????16
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
//                    toast("?????????????????????????????????");
//                    str = "ffff";
//                }
//            }else {
//                str = "00";
//            }
//            return str;
//        }catch (Exception e){
//            e.printStackTrace();
//            str = "00";
//            toast("???????????????");
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
//                    text = "???????????????" + message;
//                    break;
//                case SocketClientUtil.SOCKET_CLOSE:
//                    text = "????????????";
//                    break;
//                case SocketClientUtil.SOCKET_OPEN:
//                    text = "????????????";
//                    break;
//                case SocketClientUtil.SOCKET_SEND_MSG:
//                    //??????????????????
//                    startTime = System.currentTimeMillis();
//                    //????????????
//                    btnClick = false;
//                    mHandler.sendEmptyMessageDelayed(100,3000);
//
//                    String sendMsg = (String) msg.obj;
//                    text = "??????????????????";
////                    mEditText.setText("");
//                    sb.append("Client:<br>")
//                            .append(sendMsg)
//                            .append("<br><br>");
//                    mTextView.setText(Html.fromHtml(sb.toString()));
//                    //??????adapter
//                    updateSendAdapter(sendMsg);
//                    break;
//                case SocketClientUtil.SOCKET_RECEIVE_MSG:
//                    //????????????????????????
//                    isReceiveSucc = true;
//                    //????????????????????????
//                    mHandler.sendEmptyMessage(100);
//
//
//                    String recMsg = (String) msg.obj;
//                    text = "??????????????????";
//                    sb.append("Server:<br>")
//                            .append(recMsg)
//                            .append("<br><br>");
//                    mTextView.setText(Html.fromHtml(sb.toString()));
//                    //????????????adapter
//                    updateReceiveAdapter(recMsg);
//                    break;
//                //??????????????????
//                case SOCKET_RECEIVE_BYTES:
//                    byte[] bytes = (byte[]) msg.obj;
//                    //????????????adapter
//                    break;
//                case SocketClientUtil.SOCKET_CONNECT:
//                    text = "socket?????????";
//                    break;
//                case SocketClientUtil.SOCKET_SEND:
//                    if (mClientUtil != null) {
//                        mClientUtil.sendMsg(sendMsg());
//                    }else {
//                        connectServer();
//                    }
//                    break;
//                case 100://??????????????????
//                    btnClick = true;
//                    if (!isReceiveSucc){
//                        toast("???????????????????????????????????????");
//                    }
//                    break;
//                case 101:
//                    connectSendMsg();
//                    break;
//                case SocketClientUtil.SOCKET_SERVER_SET://?????????wifi??????
//                    jumpWifiList();
//                    break;
//            }
//            if (msg.what != SocketClientUtil.SOCKET_SEND && msg.what !=100 && msg.what != 101 && what!=6 && what != SocketClientUtil.SOCKET_RECEIVE_BYTES) {
//                toast(text);
//            }
//        }
//    };
//    //????????????
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
                //??????sb???????????????????????????
                if (sb != null) {
                    String sbStr = sb.toString();
                    if (!TextUtils.isEmpty(sbStr) && sbStr.length() > 1 * 100 * 1024) {
                        textView.setText("");
                        sb = new StringBuilder();
                    }
                }
//                connectSendMsg();
                //????????????????????????
                clearSetData(funRead, funWrite);
                //??????adapter??????
                mSendAdapter.setNewData(new ArrayList<String>());
                mReceiverAdapter.setStartRegister(-1);
                mReceiverAdapter.setNewData(new ArrayList<String>());
                //??????
                if (isEmpty(etCommand, etLengthData, etRegisterAddress)) return;
                try {
                    //????????????
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
                    toast(getString(R.string.m363????????????));
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
