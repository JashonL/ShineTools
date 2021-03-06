package com.growatt.shinetools.module.localbox.ustool;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.growatt.shinetools.R;
import com.growatt.shinetools.adapter.ComenStringAdapter;
import com.growatt.shinetools.base.DemoBase;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.MaxWifiParseUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.utils.BtnDelayUtil;
import com.growatt.shinetools.utils.CommenUtils;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.PermissionCodeUtil;
import com.growatt.shinetools.widget.CommonPopupWindow;
import com.growatt.shinetools.widget.RippleBackground;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;


public class ToolWifiConfigActivity extends DemoBase {
    @BindView(R.id.tv_ssid)
    TextView tvSsid;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.iv_switch_password)
    ImageView ivSwitchPassword;

    @BindView(R.id.etHost)
    EditText mEtHost;
    @BindView(R.id.etServer)
    EditText mEtServer;
    @BindView(R.id.btn_next)
    Button mBtnNext;
    @BindView(R.id.tvTitle)
    TextView mTvTitle;

    private final int TIME_COUNT = 300;
    @BindView(R.id.content)
    RippleBackground rippleBackground;
    @BindView(R.id.centerImage)
    TextView tvTime;
    @BindView(R.id.ivLeft)
    ImageView ivLeft;
    @BindView(R.id.ivRight)
    ImageView ivRight;
    @BindView(R.id.relativeLayout1)
    RelativeLayout relativeLayout1;
    @BindView(R.id.headerView)
    LinearLayout headerView;


    @BindView(R.id.iv_wifi)
    ImageView ivWifi;
    @BindView(R.id.iv_switch_wifi)
    ImageView ivSwitchWifi;
    @BindView(R.id.v_line_ssid)
    View vLineSsid;
    @BindView(R.id.iv_password)
    ImageView ivPassword;
    @BindView(R.id.v_line_password)
    View vLinePassword;
    @BindView(R.id.vIP)
    View vIP;
    @BindView(R.id.vHost)
    View vHost;
    @BindView(R.id.vServer)
    View vServer;


    @BindView(R.id.group_router)
    Group groupRouter;
    @BindView(R.id.iv_type_wifi)
    ImageView ivTypeWifi;
    @BindView(R.id.tv_type_wifi)
    TextView tvTypeWifi;

    @BindView(R.id.group_host)
    Group groupHost;
    @BindView(R.id.iv_type_lan)
    ImageView ivTypeLan;
    @BindView(R.id.tv_type_lan)
    TextView tvTypeLan;

    private boolean passwordOn = false;
    //wifi??????
    private String ssid = "";
    //wifi??????
    private String password;
    private WifiManager mWifiManager;
    private int getSsidType = 1;
    private String mConnectedSsid;
    private String mConnectedPassword;
    /**
     * 20002~20017 WIFI SSID
     * 20018~20025 WIFI key
     * 20026~20075 2030.5server address
     * 20076~20100 Growatt Server
     */
    private int[] nowSet = {
            0x10, 20002, 20100
    };
    private int[] nowSet2 = {
            6, 20000, 1
    };

    private int[] nowSet3 = {
            3, 20122, 20122
    };

    //????????????????????????????????????????????????????????????????????????
    /**
     * 20000
     * 0?????????wifi????????????????????????/????????????????????????????????????SSID\Password\2030.5server address???..????????????
     * 1?????????wifi???????????????SSID\Password\IP Hostname???..????????????
     * 2???Wifi????????????????????????????????????
     * 3???Wifi???????????????????????????????????????
     * 4???Wifi????????????????????????
     * 5???Wifi????????????????????????
     */
    private int[][] funs;
    private String[] statusStrs;
    private byte[] registerValues = new byte[198];
    private final int readTime = 1000;//????????????
    private final int count = 300;//????????????
    private int nowCount = 0;//??????????????????
    @BindView(R.id.stutasText)
    TextView stutasText;
    int timeCount = TIME_COUNT;

    private int flag = 1;//1.wifi 2.lan
    private CommonPopupWindow wifiWindow;
    private String[] servers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool_wifi_config);
        ButterKnife.bind(this);
        mTvTitle.setText(R.string.wifilist_configuration);
        setSeletPowerType(1);
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        getSsidType = 1;
        initWifiManager();
        //????????????????????????????????????????????????????????????????????????
        funs = new int[][]{
                {3, 20000, 20000},
        };
        statusStrs = new String[]{
                "??????wifi????????????????????????", "??????wifi?????????????????????", "Wifi?????????????????????????????????", "Wifi????????????????????????????????????", "Wifi?????????????????????", "Wifi?????????????????????",
        };

        servers = new String[]{"server-us.growatt.com", "server.growatt.com", "192.168.3.35:8081"};
    }

    public void checkWifiNetworkStatus() {
        if (CommenUtils.isWiFi(this)) {
            if (Build.VERSION.SDK_INT >= 27) {//8.1
                if (EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
//                    String ssid = mWifiAdmin.getWifiConnectedSsid9();
                    String ssid = CommenUtils.getWifiSsid(this);
                    setSsidViews(ssid);
                } else {
                    EasyPermissions.requestPermissions(this, String.format("%s:%s", getString(R.string.m???????????????????????????), getString(R.string.??????)), PermissionCodeUtil.PERMISSION_LOCATION_CODE, Manifest.permission.ACCESS_FINE_LOCATION);
                }
            } else {
//                String ssid = mWifiAdmin.getWifiConnectedSsid9();
                String ssid = CommenUtils.getWifiSsid(this);
                setSsidViews(ssid);
            }
        }
    }

    Handler timeHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
//            Mydialog.Dismiss();
            switch (msg.what) {

                case 105:
                    timeCount--;
                    if (timeCount < 0) {
                        stutasText.setText(getString(R.string.all_failed));
                        timeHandler.sendEmptyMessage(106);

                        if (timeCount < 0) {
                            tvTime.setVisibility(View.INVISIBLE);

                        }
                    } else {
                        timeHandler.sendEmptyMessageDelayed(105, 1000);
                        tvTime.setText(timeCount + getString(R.string.WifiNewtoolAct_time_s));
                    }

                    break;
                case 106:
                    timeCount = TIME_COUNT;


                    try {
                        nowCount = count + 1;

                        //  Mydialog.Dismiss();
                        stopTheAnimal();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    private void initWifiManager() {
        if (mWifiManager.isWifiEnabled()) {
            checkWifiNetworkStatus();
        }
    }

    @OnClick({R.id.ivLeft, R.id.btn_next, R.id.iv_switch_wifi, R.id.iv_switch_password,
            R.id.ll_type_wifi, R.id.ll_type_lan, R.id.iv_host_pull, R.id.iv_server_pull})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivLeft:
                finish();
                break;
            case R.id.btn_next:
                config();
                break;
            case R.id.iv_switch_wifi:
                toGetssid();
                break;
            case R.id.iv_switch_password:
                clickPasswordSwitch();
                break;
            case R.id.ll_type_wifi:
                setSeletPowerType(1);
                break;
            case R.id.ll_type_lan:
                setSeletPowerType(2);
                break;
            case R.id.iv_host_pull:
                setHost(mEtHost);
                break;
            case R.id.iv_server_pull:
                setHost(mEtServer);
                break;

        }
    }

    Handler mHandlerRead = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //????????????
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessage(this);
                    byte[] sendBytesR = SocketClientUtil.sendMsgToServer(mClientUtilRead, funs[0]);
                    LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(sendBytesR));
                    break;
                //??????????????????
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        LogUtil.i("????????????:nowcount==" + nowCount + SocketClientUtil.bytesToHexString(bytes));
                        //?????????????????????
                        boolean isCheck = ModbusUtil.checkModbus(bytes);
                        //????????????????????????
                        if (nowCount < count) {
                            nowCount++;
                            this.sendEmptyMessageDelayed(SocketClientUtil.SOCKET_SEND, readTime);
                        }
                        if (isCheck) {
                            //??????????????????
                            byte[] bs = RegisterParseUtil.removePro17(bytes);
                            int status = MaxWifiParseUtil.obtainValueOne(bs);
                            LogUtil.i("??????status??????==" + status);
                            try {
                                if (nowCount >= count) {
                                    //   toast("????????????");
                                    this.removeMessages(SocketClientUtil.SOCKET_SEND);
                                    SocketClientUtil.close(mClientUtilRead);
                                    //  Mydialog.Dismiss();
                                    stopTheAnimal();
                                } else {
//                               //     Mydialog.Show(mContext, statusStrs[status]);
                                    //    Mydialog.Show(mContext, "Setting...");
                                }
//                                toast(statusStrs[status]);
                            } catch (Exception e) {
                                e.printStackTrace();
                                //   Mydialog.Show(mContext, String.valueOf(status));
                            }
                            if (status == 4 || status == 5) {
                                if (status == 4) {
                                    stutasText.setText("Set successfully");

                                    //Mydialog.Show(mContext, "Set successfully");
                                } else if (status == 5) {
//                                    stutasText.setText("Set failed");
                                    connectServerError();
                                    //  Mydialog.Show(mContext, "Setting failed");
                                }
                                this.removeMessages(SocketClientUtil.SOCKET_SEND);
                                SocketClientUtil.close(mClientUtilRead);
                                //Mydialog.Dismiss();
                                stopTheAnimal();
                                timeHandler.sendEmptyMessage(106);

                                return;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        //????????????
//                        toast("?????????" + e.toString());
                        SocketClientUtil.close(mClientUtilRead);
                        // Mydialog.Dismiss();
                    }
                    break;
                case SocketClientUtil.SOCKET_CLOSE:
//                    toast("Socket ??????");
                    this.removeMessages(SocketClientUtil.SOCKET_SEND);
                    break;
                case BtnDelayUtil.TIMEOUT_RECEIVE:
                    this.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                    break;
            }
        }
    };

    //????????????:???????????????
    private SocketClientUtil mClientUtilW;
    /**
     * ????????????handle
     */
    Handler mHandlerWrite = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //????????????
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessageWrite(this);
                    if (nowSet != null && nowSet[2] != -1) {
                        sendBytes = SocketClientUtil.sendMsgToServer(mClientUtilWriter, nowSet2);
                        LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(sendBytes));
                    } else {
                        toast("??????????????????????????????");
                    }
                    break;
                //??????????????????
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        //??????tcp??????
                        SocketClientUtil.close(mClientUtilWriter);
                        BtnDelayUtil.refreshFinish();
                        byte[] bytes = (byte[]) msg.obj;
                        LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(bytes));
                        //?????????????????????
//                        boolean isCheck = MaxUtil.isCheckFull(mContext, bytes);
                        boolean isCheck = MaxUtil.checkReceiverFull(bytes);
                        if (isCheck) {
                            //  toast(getString(R.string.all_success));
                            //???????????? ?????????20000???1
                            readRegisterValue();
                        } else {
                            //toast(getString(R.string.all_failed));
                            stutasText.setText(getString(R.string.all_failed));
                            stopTheAnimal();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        //??????tcp??????
                        SocketClientUtil.close(mClientUtilWriter);
                        BtnDelayUtil.refreshFinish();
                    }
                    break;
                default:

                    if ((what == 6) || (what == -1)) {
                        stutasText.setText(getString(R.string.all_failed));
                        stopTheAnimal();
                    }


                    if ((what == 301) || (what == 302) || (what == 304)) {

                    } else {
                        BtnDelayUtil.dealTLXBtnWrite(this, what, mContext, mBtnNext);
                    }
                    break;
            }
        }
    };

    /**
     * ????????????handle
     */
    //???????????????????????????
    private byte[] sendBytes;
    Handler mHandlerW = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //????????????
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessageWrite(this);
                    sendBytes = SocketClientUtil.sendMsgToServerByte10(mClientUtilW, nowSet, registerValues);
                    LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(sendBytes));
                    break;
                //??????????????????
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(bytes));
                        //????????????
                        SocketClientUtil.close(mClientUtilW);
                        // Mydialog.Dismiss();
                        //?????????????????????
                        boolean isCheck = MaxUtil.checkReceiverFull10(bytes);
                        if (isCheck) {
//                            toast(getString(R.string.all_success));
                            //???????????? ?????????20000???1
                            writeRegisterValue();
                        } else {
                            stutasText.setText(getString(R.string.all_failed));

                            //toast(getString(R.string.all_failed));
                            stopTheAnimal();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        //????????????
                        SocketClientUtil.close(mClientUtilW);
                        //  Mydialog.Dismiss();
                    }
                case SocketClientUtil.SOCKET_EXCETION_CLOSE:
                    SocketClientUtil.close(mClientUtilW);

                    break;
                default:

                    if ((what == 6) || (what == -1)) {
                        stutasText.setText(getString(R.string.all_failed));
                        stopTheAnimal();
                    }


                    if ((what == 301) || (what == 302) || (what == 304)) {

                    } else {
                        BtnDelayUtil.dealTLXBtnWrite(this, what, mContext, mBtnNext);
                    }
                    break;
            }
        }
    };
    //????????????:??????????????????
    private SocketClientUtil mClientUtilWriter;

    private void setSsidViews(String apSsid) {
        if (getSsidType == 1) {
            if (apSsid != null) {
                tvSsid.setText(apSsid);
            } else {
                tvSsid.setText("");
            }
        } else {
            if (apSsid != null) {
                tvSsid.setText(apSsid);
                // toast(R.string.all_success);
            } else {
                tvSsid.setText("");
                // toast(R.string.all_failed);
            }
        }
    }

    private void config() {
        String ssid = tvSsid.getText().toString();
        String pwd = etPassword.getText().toString();
//        String ip = mEtIP.getText().toString();
        String host = mEtHost.getText().toString();
        String server = mEtServer.getText().toString();


        if (flag == 1) {
            if (TextUtils.isEmpty(ssid)) {
                toast(getString(R.string.??????????????????));
                return;
            }
            if (TextUtils.isEmpty(pwd)) {
                toast(getString(R.string.???????????????));
                return;
            }
            if (TextUtils.isEmpty(host)) {
                toast(getString(R.string.?????????Hostname));
                return;
            }
            if (TextUtils.isEmpty(server)) {
                toast(getString(R.string.?????????Server));
                return;
            }
        } else {
            if (TextUtils.isEmpty(ssid)) {
                ssid = "";
            }
            if (TextUtils.isEmpty(pwd)) {
                ssid = "";
            }
            if (TextUtils.isEmpty(host)) {
                toast(getString(R.string.?????????Hostname));
                return;
            }
            if (TextUtils.isEmpty(server)) {
                toast(getString(R.string.?????????Server));
                return;
            }
        }


//        if (TextUtils.isEmpty(ip)) {
//            toast("?????????ip");
//            return;
//        }

        //???????????? ssid 32
        byte[] ssids = ssid.getBytes();
        int ssidLen = ssids.length;
        for (int i = 0; i < 32; i++) {
            if (i < ssidLen) {
                registerValues[i] = ssids[i];
            } else {
                registerValues[i] = 0;
            }
        }
        //???????????? key  16
        byte[] pwds = pwd.getBytes();
        int pwdLen = pwds.length;
        for (int i = 0; i < 16; i++) {
            if (i < pwdLen) {
                registerValues[i + 32] = pwds[i];
            } else {
                registerValues[i + 32] = 0;
            }
        }
        //???????????? host 100
        byte[] hosts = host.getBytes();
        int hostLen = hosts.length;
        for (int i = 0; i < 100; i++) {
            if (i < hostLen) {
                registerValues[i + 32 + 16] = hosts[i];
            } else {
                registerValues[i + 32 + 16] = 0;
            }
        }
        //???????????? server 50
        byte[] servers = server.getBytes();
        int serverLen = servers.length;
        for (int i = 0; i < 50; i++) {
            if (i < serverLen) {
                registerValues[i + 32 + 16 + 100] = servers[i];
            } else {
                registerValues[i + 32 + 16 + 100] = 0;
            }
        }

        stutasText.setText("Setting...");
        rippleBackground.startRippleAnimation();

        timeCount = TIME_COUNT;
        tvTime.setVisibility(View.VISIBLE);
        timeHandler.sendEmptyMessageDelayed(105, 1000);

        mBtnNext.setEnabled(false);

        connectServerWrite();
    }

    //????????????:??????????????????
    private SocketClientUtil mClientUtilRead;

    private void connectServerWrite() {
        //  Mydialog.Show(mContext);
        mClientUtilW = SocketClientUtil.connectServer(mHandlerW);
    }

    //?????????????????????
    private void writeRegisterValue() {
        //   Mydialog.Show(mContext);
        mClientUtilWriter = SocketClientUtil.connectServer(mHandlerWrite);
    }

    private void stopTheAnimal() {
        timeHandler.removeMessages(105);
        rippleBackground.stopRippleAnimation();
        tvTime.setVisibility(View.INVISIBLE);
        mBtnNext.setEnabled(true);
    }

    //?????????????????????
    private void readRegisterValue() {
        nowCount = 0;
        // Mydialog.Show(mContext);
        mClientUtilRead = SocketClientUtil.connectServer(mHandlerRead);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 200) {
            tvSsid.setText(data.getStringExtra("wifiName"));
        }
    }

    @Override
    public void finish() {
        super.finish();
        //????????????
        try {
            if (mClientUtilRead != null) {
                SocketClientUtil.close(mClientUtilRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (mClientUtilW != null) {
                SocketClientUtil.close(mClientUtilW);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (mClientUtilWriter != null) {
                SocketClientUtil.close(mClientUtilWriter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clickPasswordSwitch() {
        passwordOn = !passwordOn;
        if (passwordOn) {
            ivSwitchPassword.setImageResource(R.drawable.icon_signin_see);
            etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            ivSwitchPassword.setImageResource(R.drawable.icon_signin_see);
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        if (etPassword.getText().length() > 0) {
            etPassword.setSelection(etPassword.getText().length());
        }
    }


    private void setSeletPowerType(int type) {
        flag = type;
        switch (type) {
            case 1:
                ivTypeWifi.setImageResource(R.drawable.shape_circle_blue_ring);
                ivTypeLan.setImageResource(R.drawable.shape_circle_gray_ring);
                tvTypeWifi.setTextColor(ContextCompat.getColor(this, R.color.title_bg_white));
                tvTypeLan.setTextColor(ContextCompat.getColor(this, R.color.content_bg_white));
                groupRouter.setVisibility(View.VISIBLE);
                groupHost.setVisibility(View.VISIBLE);
                break;
            case 2://??????
                ivTypeWifi.setImageResource(R.drawable.shape_circle_gray_ring);
                ivTypeLan.setImageResource(R.drawable.shape_circle_blue_ring);
                tvTypeWifi.setTextColor(ContextCompat.getColor(this, R.color.content_bg_white));
                tvTypeLan.setTextColor(ContextCompat.getColor(this, R.color.title_bg_white));
                groupRouter.setVisibility(View.GONE);
                groupHost.setVisibility(View.VISIBLE);
                break;
        }
    }


    private void toGetssid() {

        Intent intent = new Intent(this, WiFiListActivity.class);
        startActivityForResult(intent, 200);

//        startActivity(intent);

//            finish();

//        jumpTo(WiFiListActivity.class,false);

//        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo.State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
//        if (wifi != NetworkInfo.State.CONNECTED && wifi != NetworkInfo.State.CONNECTING) {
//            AlertDialog builder = new AlertDialog.Builder(ToolWifiConfigActivity.this).setTitle(R.string.all_prompt).setMessage(R.string.dataloggers_dialog_connectwifi).setPositiveButton(R.string.all_ok, new DialogInterface.OnClickListener() {
//
//                @Override
//                public void onClick(DialogInterface arg0, int arg1) {
//                    arg0.dismiss();
//                }
//            }).create();
//            builder.show();
//        } else {
//            getSsidType = 2;
//            checkWifiNetworkStatus();
//        }

    }


    /*????????????*/
    private void setHost(View dropView) {
        if (wifiWindow == null) {
            wifiWindow = new CommonPopupWindow(this, R.layout.popuwindow_comment_list_layout, dropView.getWidth(), LinearLayout.LayoutParams.WRAP_CONTENT) {
                @Override
                protected void initView() {
                    List<String> ssids = Arrays.asList(servers);
                    View view = getContentView();
                    RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
                    recyclerView.setLayoutManager(new LinearLayoutManager(ToolWifiConfigActivity.this, LinearLayoutManager.VERTICAL, false));
                    ComenStringAdapter adapter = new ComenStringAdapter(R.layout.item_text, ssids);
                    recyclerView.setAdapter(adapter);
                    adapter.setOnItemClickListener((adapter1, view1, position) -> {
                        String item = adapter.getItem(position);
                        ((EditText) dropView).setText(item);
                        wifiWindow.getPopupWindow().dismiss();
                        wifiWindow = null;
                    });
                }

                @Override
                protected void initEvent() {
                }
            };
        }
        int[] location = new int[2];
        dropView.getLocationOnScreen(location);
        wifiWindow.showAsDropDown(dropView, 0, 0);
    }


    //????????????:????????????????????????
    private SocketClientUtil mClientErrorRead;


    private void connectServerError() {
        //  Mydialog.Show(mContext);
        mClientErrorRead = SocketClientUtil.connectServer(mHandlerReadError);
    }

    /**
     * ????????????handle
     */
    Handler mHandlerReadError = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //????????????
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessage(this);
                    byte[] sendBytesR = SocketClientUtil.sendMsgToServer(mClientErrorRead, nowSet3);
                    LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(sendBytesR));
                    break;
                //??????????????????
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        LogUtil.i("????????????:nowcount==" + nowCount + SocketClientUtil.bytesToHexString(bytes));
                        //?????????????????????
                        boolean isCheck = ModbusUtil.checkModbus(bytes);
                        if (isCheck) {
                            //??????????????????
                            byte[] bs = RegisterParseUtil.removePro17(bytes);
                            int status = MaxWifiParseUtil.obtainValueOne(bs);
                            LogUtil.i("??????status??????==" + status);
                            if (status == 0) {
                                stutasText.setText("Set successfully");
                            } else {
                                stutasText.setText("Set failed");
                            }
                            SocketClientUtil.close(mClientErrorRead);
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        SocketClientUtil.close(mClientErrorRead);
                    }
                    break;
                case SocketClientUtil.SOCKET_CLOSE:
                    this.removeMessages(SocketClientUtil.SOCKET_SEND);
                    break;
                case BtnDelayUtil.TIMEOUT_RECEIVE:
                    this.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                    break;
            }
        }
    };


}
