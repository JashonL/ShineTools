package com.growatt.shinetools.module.localbox.ustool;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.growatt.shinetools.R;
import com.growatt.shinetools.adapter.ComenStringAdapter;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.MaxWifiParseUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.timer.CustomTimer;
import com.growatt.shinetools.utils.BtnDelayUtil;
import com.growatt.shinetools.utils.CircleDialogUtils;
import com.growatt.shinetools.utils.CommenUtils;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.MyControl;
import com.growatt.shinetools.utils.Mydialog;
import com.mylhyl.circledialog.view.listener.OnCreateBodyViewListener;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class USWiFiConfigActivity extends BaseActivity {
    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.header_title)
    LinearLayout headerTitle;
    @BindView(R.id.tv_choose_type)
    TextView tvChooseType;
    @BindView(R.id.iv_type_wifi)
    ImageView ivTypeWifi;
    @BindView(R.id.tv_type_wifi)
    AppCompatTextView tvTypeWifi;
    @BindView(R.id.ll_type_wifi)
    LinearLayout llTypeWifi;
    @BindView(R.id.iv_type_lan)
    ImageView ivTypeLan;
    @BindView(R.id.tv_type_lan)
    AppCompatTextView tvTypeLan;
    @BindView(R.id.ll_type_lan)
    LinearLayout llTypeLan;
    @BindView(R.id.iv_wifi)
    ImageView ivWifi;
    @BindView(R.id.tv_ssid)
    EditText tvSsid;
    @BindView(R.id.iv_switch_wifi)
    ImageView ivSwitchWifi;
    @BindView(R.id.v_line_ssid)
    View vLineSsid;
    @BindView(R.id.iv_password)
    ImageView ivPassword;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.iv_switch_password)
    ImageView ivSwitchPassword;
    @BindView(R.id.v_line_password)
    View vLinePassword;
    @BindView(R.id.tv_cable_tips)
    TextView tvCableTips;
    @BindView(R.id.cl_select)
    ConstraintLayout clSelect;
    @BindView(R.id.tv_server_url)
    TextView tvServerUrl;
    @BindView(R.id.etHost)
    EditText etHost;
    @BindView(R.id.iv_host_pull)
    ImageView ivHostPull;
    @BindView(R.id.vHost)
    View vHost;
    @BindView(R.id.etServer)
    EditText etServer;
    @BindView(R.id.iv_server_pull)
    ImageView ivServerPull;
    @BindView(R.id.vServer)
    View vServer;
    @BindView(R.id.cl_server)
    ConstraintLayout clServer;
    @BindView(R.id.btn_next)
    Button btnNext;


    private int flag = 1;//1.wifi 2.lan
    private DialogFragment dialogFragment;
    private DialogFragment errorDialog;
    private String[] servers;
    private boolean passwordOn = false;


    //读取
    private int[][] funs;
    private byte[] registerValues = new byte[198];
    private boolean configing = false;
    //是否查询第八个bit位
    private boolean isCheckStatus = false;


    private int[] funsCable = {3, 20114, 20114};


    //倒计时
    private int current = 0;
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

    //定时刷新的计时器
    private CustomTimer mTimer;

    private int currenStep = 1;
    private boolean isReset = false;

    @Override
    protected int getContentView() {
        return R.layout.activity_us_tool_config;
    }

    @Override
    protected void initViews() {
        initToobar(toolbar);
        tvTitle.setText(R.string.android_key2930);
        setSeletPowerType(1);

    }

    @Override
    protected void initData() {
        servers = new String[]{"server-us.growatt.com", "server.growatt.com", "server.smten.com"};
        //读取命令功能码（功能码，开始寄存器，结束寄存器）
        funs = new int[][]{
                {3, 20000, 20000},
        };
        connectSendMsg();
    }


    //连接对象
    /*建立TCP连接*/
    private void connectSendMsg() {
        Mydialog.Show(this);
        connectServer();
    }


    private SocketClientUtil mClientUtil;

    private void connectServer() {
        mClientUtil = SocketClientUtil.newInstance();
        if (mClientUtil != null) {
            mClientUtil = SocketClientUtil.connectServer(mHandler);
        }
    }


    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            String text = "";
            int what = msg.what;
            switch (what) {
                case SocketClientUtil.SOCKET_OPEN:
                    Mydialog.Dismiss();
                    break;
                case SocketClientUtil.SOCKET_SEND_MSG:
                    break;
                case SocketClientUtil.SOCKET_RECEIVE_MSG:
                    break;
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    byte[] bytes = (byte[]) msg.obj;
                    //解析数据
                    parserData(bytes);
                    break;
                case SocketClientUtil.SOCKET_CONNECT:
                    break;
                case SocketClientUtil.SOCKET_SEND:
                    break;
                case SocketClientUtil.SOCKET_SERVER_SET:
                    MyControl.showJumpWifiSet(USWiFiConfigActivity.this);
                    break;
                case SocketClientUtil.SOCKET_EXCETION_CLOSE:
                    Mydialog.Dismiss();
                    stopFreshTimer();
                    if (dialogFragment != null) {
                        dialogFragment.dismiss();
                        dialogFragment = null;
                        configing = false;
                    }
                    connectSendMsg();
                    break;
                case 100://恢复按钮点击
                    break;
                case 101:
                    break;
                case 102:
                    break;

            }

        }
    };


    /**
     * 解析数据
     *
     * @param bytes
     */
    private void parserData(byte[] bytes) {
        if (currenStep == 1) {//执行第一步
            try {
                //检测内容正确性
                boolean isCheck = MaxUtil.checkReceiverFull10(bytes);
                LogUtil.i("=============检测内容正确开始写2000=============");
                if (isCheck) {
                    //20000写1
                    if (configing) {
                        sendWriteComand();
                    }
                } else {
                    configError();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (currenStep == 2) {
            try {
                //检测内容正确性
                boolean isCheck = MaxUtil.checkReceiverFull(bytes);
                if (isCheck) {
                    //往2000写1成功，开始读取连接状态
                    if (configing) {
                        LogUtil.i("=======2000下发成功开始读状态=============");
                        if (flag == 1) {
                            readStatus();
                        } else {
                            send20144(true);
                        }
                    }
                } else {
                    configError();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (currenStep == 3) {
            try {
                //检测内容正确性
                boolean isCheck = ModbusUtil.checkModbus(bytes);
                if (isCheck) {
                    //移除外部协议
                    byte[] bs = RegisterParseUtil.removePro17(bytes);
                    //状态是5失败  4是成功  其余状态继续读取
                    int status = MaxWifiParseUtil.obtainValueOne(bs);
                    LogUtil.i("接收status状态==" + status);
                    if (status == 4 || status == 5) {
                        if (status == 4) {//成功
                            showConfigSuccess();
                        } else {//失败
                            readError();
                        }
                    } else {
                        //继续读状态
                        if (configing) {
                            //读取间隔
                            int readTime = 1000;
                            mHandler.postDelayed(this::readStatus, readTime);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (currenStep == 4) {
            try {
                //检测内容正确性
                boolean isCheck = ModbusUtil.checkModbus(bytes);
                if (isCheck) {
                    //移除外部协议
                    byte[] bs = RegisterParseUtil.removePro17(bytes);
                    int status = MaxWifiParseUtil.obtainValueOne(bs);
                    LogUtil.i("接收status状态==" + status);
                    if (status == 0) {//成功
                        showConfigSuccess();
                    } else {//失败
                        configError();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (currenStep == 5) {//读取20144的值
            try {
                //检测内容正确性
                boolean isCheck = ModbusUtil.checkModbus(bytes);
                if (isCheck) {
                    //移除外部协议
                    byte[] bs = RegisterParseUtil.removePro17(bytes);


                    //网线连接状态
                    int status_cable = MaxWifiParseUtil.obtainValueOne(bs);
                    LogUtil.i("返回值：" + status_cable);

                    String s = Integer.toBinaryString(status_cable);
                    char[] chars = s.toCharArray();
                    LogUtil.i("反转之前：" + Arrays.toString(chars));
                    int len = chars.length;
                    char[] reverse = CommenUtils.reverse(chars, len);

                    LogUtil.i("反转之后：" + Arrays.toString(reverse));
                    String b5 = String.valueOf(reverse[5]);
                    String b6 = String.valueOf(reverse[6]);
                    String b8 = String.valueOf(reverse[8]);


/*
                    byte cable_byte = (byte) status_cable;
                    //取第5和第6个bit位
                    String b5 = (byte) ((cable_byte >> 5) & 0x1) + "";
                    String b6 = (byte) ((cable_byte >> 6) & 0x1) + "";
                    String b8 = (byte) ((cable_byte >> 8) & 0x1) + "";*/


                    if (!isCheckStatus) {
                        if (!"1".equals(b5)) {
                            cableConfigError(5);
                            return;
                        }
                        if (!"1".equals(b6)) {
                            cableConfigError(6);
                            return;
                        }
                        //下发服务器地址
                        sendSetComand();
                    } else {
                        if ("1".equals(b8)) {
                            showConfigSuccess();
                        } else {
                            //继续读状态
                            if (configing) {
                                //读取间隔
                                int readTime = 1000;
                                mHandler.postDelayed(() -> send20144(true), readTime);
                            }
                        }
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

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
                CommenUtils.showAllView(ivWifi, tvSsid, ivSwitchWifi, vLineSsid, ivPassword, etPassword, ivSwitchPassword, vLinePassword);
                tvCableTips.setVisibility(View.GONE);
                break;
            case 2://馈电
                ivTypeWifi.setImageResource(R.drawable.shape_circle_gray_ring);
                ivTypeLan.setImageResource(R.drawable.shape_circle_blue_ring);
                tvTypeWifi.setTextColor(ContextCompat.getColor(this, R.color.content_bg_white));
                tvTypeLan.setTextColor(ContextCompat.getColor(this, R.color.title_bg_white));
                CommenUtils.hideAllView(View.GONE, ivWifi, tvSsid, ivSwitchWifi, vLineSsid, ivPassword, etPassword, ivSwitchPassword, vLinePassword);
                tvCableTips.setVisibility(View.VISIBLE);
                break;
        }
    }


    @OnClick({R.id.ll_type_wifi, R.id.ll_type_lan, R.id.iv_switch_password, R.id.iv_switch_wifi,
            R.id.iv_host_pull, R.id.iv_server_pull, R.id.btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_type_wifi:
                setSeletPowerType(1);
                break;
            case R.id.ll_type_lan:
                setSeletPowerType(2);
                break;
            case R.id.iv_switch_password:
                clickPasswordSwitch();
                break;
            case R.id.iv_switch_wifi:
                toGetssid();
                break;
            case R.id.iv_host_pull:
                setHost(1);
                break;
            case R.id.iv_server_pull:
                setHost(2);
                break;

            case R.id.btn_next:
                config();
                break;

        }
    }


    private void config() {
        String ssid = tvSsid.getText().toString();
        String pwd = etPassword.getText().toString();
        String host = etHost.getText().toString();
        String server = etServer.getText().toString();


        if (flag == 1) {
            if (TextUtils.isEmpty(ssid)) {
                toast(getString(R.string.android_key1896));
                return;
            }
            if (TextUtils.isEmpty(pwd)) {
                toast(getString(R.string.android_key1897));
                return;
            }
            if (TextUtils.isEmpty(host)) {
                toast(getString(R.string.请输入Hostname));
                return;
            }
            if (TextUtils.isEmpty(server)) {
                toast(getString(R.string.请输入Server));
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
                toast(getString(R.string.请输入Hostname));
                return;
            }
            if (TextUtils.isEmpty(server)) {
                toast(getString(R.string.请输入Server));
                return;
            }
        }


        //设置数据 ssid 32
        byte[] ssids = ssid.getBytes();
        int ssidLen = ssids.length;
        for (int i = 0; i < 32; i++) {
            if (i < ssidLen) {
                registerValues[i] = ssids[i];
            } else {
                registerValues[i] = 0;
            }
        }
        //设置数据 key  16
        byte[] pwds = pwd.getBytes();
        int pwdLen = pwds.length;
        for (int i = 0; i < 16; i++) {
            if (i < pwdLen) {
                registerValues[i + 32] = pwds[i];
            } else {
                registerValues[i + 32] = 0;
            }
        }
        //设置数据 host 100
        byte[] hosts = host.getBytes();
        int hostLen = hosts.length;
        for (int i = 0; i < 100; i++) {
            if (i < hostLen) {
                registerValues[i + 32 + 16] = hosts[i];
            } else {
                registerValues[i + 32 + 16] = 0;
            }
        }
        //设置数据 server 50
        byte[] servers = server.getBytes();
        int serverLen = servers.length;
        for (int i = 0; i < 50; i++) {
            if (i < serverLen) {
                registerValues[i + 32 + 16 + 100] = servers[i];
            } else {
                registerValues[i + 32 + 16 + 100] = 0;
            }
        }


        //显示倒计时弹框
        showDialogFragment();
        //发送设置指令
        if (flag == 1) {//wifi配网
            sendSetComand();
        } else {//网线
            send20144(false);
        }
    }

    private TextView tvTime;
    private TextView tvTips;
    private TextView btnCancel;

    /**
     * 显示计时
     */
    public void showDialogFragment() {
        if (dialogFragment == null) {
            View view = LayoutInflater.from(this).inflate(R.layout.dialog_config_datalog, null);
            dialogFragment = CircleDialogUtils.showCommentBodyView(this, view, "", getSupportFragmentManager(), new OnCreateBodyViewListener() {
                @Override
                public void onCreateBodyView(View view) {
                    tvTime = view.findViewById(R.id.tv_time);
                    tvTips = view.findViewById(R.id.loading_tips);
                    btnCancel = view.findViewById(R.id.tv_cancel);
                    btnCancel.setOnClickListener(view1 -> {
                        //取消配置
                        dialogFragment.dismiss();
                        dialogFragment = null;

                        configing = false;
                        stopFreshTimer();
                    });

                }
            }, Gravity.CENTER, 0.8f, 0.5f, false);
        }
        //开启倒计时
        startFreshTimer();
    }

    /**
     * 取消倒计时
     */

    private void dialogDissmiss() {
        //
        try {
            if (dialogFragment != null) {
                dialogFragment.dismiss();
                dialogFragment = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 显示配网失败
     */
    public void showErrorDialog() {
        if (errorDialog == null) {
            View view = LayoutInflater.from(this).inflate(R.layout.dialog_config_error, null);
            errorDialog = CircleDialogUtils.showCommentBodyView(this, view, "", getSupportFragmentManager(), new OnCreateBodyViewListener() {
                @Override
                public void onCreateBodyView(View view) {
                    btnCancel = view.findViewById(R.id.tv_cancel);
                    btnCancel.setText(R.string.all_no);
                    TextView tvTitle = view.findViewById(R.id.tv_title);
                    tvTitle.setText(R.string.配网失败);
                    TextView tvTips = view.findViewById(R.id.loading_tips);
                    tvTips.setText(R.string.请检查配置页面中输入的路由器用户名和密码是否正确);


                    btnCancel.setOnClickListener(view1 -> {
                        //取消配置
                        errorDialog.dismiss();
                        errorDialog = null;
                    });

                }
            }, Gravity.CENTER, 0.8f, 0.5f, false);
        }
    }


    /**
     * 显示配网失败
     */
    public void showCableErrorDialog(String error) {
        if (errorDialog == null) {
            View view = LayoutInflater.from(this).inflate(R.layout.dialog_config_error, null);
            errorDialog = CircleDialogUtils.showCommentBodyView(this, view, "", getSupportFragmentManager(), new OnCreateBodyViewListener() {
                @Override
                public void onCreateBodyView(View view) {
                    btnCancel = view.findViewById(R.id.tv_cancel);
                    btnCancel.setText(R.string.all_ok);
                    TextView tvTitle = view.findViewById(R.id.tv_title);
                    tvTitle.setText(R.string.配网失败);
                    TextView tvTips = view.findViewById(R.id.loading_tips);
                    tvTips.setText(error);
                    btnCancel.setOnClickListener(view1 -> {
                        //取消配置
                        errorDialog.dismiss();
                        errorDialog = null;
                    });

                }
            }, Gravity.CENTER, 0.8f, 0.5f, false);
        }
    }


    /**
     * 显示配网成功
     */
    public void showSuccessDialog() {
        if (errorDialog == null) {
            View view = LayoutInflater.from(this).inflate(R.layout.dialog_config_success, null);
            errorDialog = CircleDialogUtils.showCommentBodyView(this, view, "", getSupportFragmentManager(), new OnCreateBodyViewListener() {
                @Override
                public void onCreateBodyView(View view) {
                    btnCancel = view.findViewById(R.id.tv_cancel);
                    btnCancel.setText(R.string.all_ok);
                    TextView tvTitle = view.findViewById(R.id.tv_title);
                    tvTitle.setText(R.string.all_success);
                    TextView tvTips = view.findViewById(R.id.loading_tips);
                    tvTips.setText(R.string.android_key2994);
                    btnCancel = view.findViewById(R.id.tv_cancel);
                    btnCancel.setOnClickListener(view1 -> {
                        //取消配置
                        errorDialog.dismiss();
                        errorDialog = null;
                    });

                }
            }, Gravity.CENTER, 0.8f, 0.5f, false);
        }
    }


    /*发送wifi设置命令*/
    private void sendSetComand() {
//        Mydialog.Show(this);
        try {
            currenStep = 1;
            BtnDelayUtil.sendMessageWrite(mHandler);
            byte[] bytes = SocketClientUtil.sendMsgToServerByte10(mClientUtil, nowSet, registerValues);
            LogUtil.i("sendSetComand：" + SocketClientUtil.bytesToHexString(bytes));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*设置完发送2000，然后再读取*/
    private void sendWriteComand() {
        BtnDelayUtil.sendMessageWrite(mHandler);
        if (nowSet != null && nowSet[2] != -1) {
            currenStep = 2;
            byte[] bytes = SocketClientUtil.sendMsgToServer(mClientUtil, nowSet2);
            LogUtil.i("sendWriteComand：" + SocketClientUtil.bytesToHexString(bytes));
        }
    }

    /**
     * 读取连接状态
     */
    private void readStatus() {
        BtnDelayUtil.sendMessageWrite(mHandler);
        currenStep = 3;
        byte[] bytes = SocketClientUtil.sendMsgToServer(mClientUtil, funs[0]);
        LogUtil.i("readStatus：" + SocketClientUtil.bytesToHexString(bytes));
    }


    /*读取20144的值*/
    private void send20144(boolean ischeck) {
//        Mydialog.Show(this);
        try {
            isCheckStatus = ischeck;
            currenStep = 5;
            BtnDelayUtil.sendMessageWrite(mHandler);
            byte[] bytes = SocketClientUtil.sendMsgToServer(mClientUtil, funsCable);
            LogUtil.i("send20144：" + SocketClientUtil.bytesToHexString(bytes));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 读取失败原因
     */
    private void readError() {
        BtnDelayUtil.sendMessage(mHandler);
        currenStep = 4;
        SocketClientUtil.sendMsgToServer(mClientUtil, nowSet3);
    }


    /**
     * 固定一分钟刷新一次
     */

    private void startFreshTimer() {
        //初始化一个定时器并立即执行
        mTimer = new CustomTimer(() -> {
            try {
                current++;
                if (current > 99) {
                    configing = false;
                    configError();
                } else {
                    configing = true;
                    String time = current + "%";
                    tvTime.setText(time);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 1200, 0);

        mTimer.timerStart();
    }


    private void stopFreshTimer() {
        current = 0;
        if (mTimer != null) {
            mTimer.timerDestroy();
            mTimer = null;
        }
    }


    /**
     * 配置失败
     */
    private void configError() {
        configing = false;
        //停止倒计时
        stopFreshTimer();
        //取消弹框
        dialogDissmiss();
        //弹出配置失败弹框
        showErrorDialog();
    }


    /**
     * 配置失败
     */
    private void cableConfigError(int type) {
        configing = false;
        //停止倒计时
        stopFreshTimer();
        //取消弹框
        dialogDissmiss();
        //弹出配置失败弹框
        String text = getString(R.string.配网失败);
        if (type == 6) {
            text = getString(R.string.配网失败);
        }
        showCableErrorDialog(text);
    }


    /**
     * 配置成功
     */
    private void showConfigSuccess() {
        configing = false;
        //停止倒计时
        stopFreshTimer();
        //取消弹框
        dialogDissmiss();
        //弹出配置成功弹框
        showSuccessDialog();
    }


    /*制冷弹框*/
    private void setHost(int type) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.item_us_server, null);
        if (dialogFragment == null) {
            dialogFragment = CircleDialogUtils.showCommentBodyView(this, dialogView, "", getSupportFragmentManager(), view -> {
                List<String> ssids = Arrays.asList(servers);
                RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
                recyclerView.setLayoutManager(new LinearLayoutManager(USWiFiConfigActivity.this,
                        LinearLayoutManager.VERTICAL, false));
                ComenStringAdapter adapter = new ComenStringAdapter(R.layout.item_server_url, ssids);
                recyclerView.setAdapter(adapter);
                adapter.setOnItemClickListener((adapter1, view1, position) -> {
                    String item = adapter.getItem(position);
                    if (type == 1) {
                        etHost.setText(item);
                    } else {
                        etServer.setText(item);
                    }
                    dialogFragment.dismiss();
                    dialogFragment = null;
                });
            }, Gravity.CENTER, 0.8f, 0.5f, true);
        }


    }

    //获取wifi名称
    private void toGetssid() {
        Intent intent = new Intent(this, WiFiListActivity.class);
        myLauncher.launch(intent);
    }

    private ActivityResultLauncher myLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        Intent data = result.getData();
        if (result.getResultCode() == RESULT_OK) {
            if (data != null) {
                tvSsid.setText(data.getStringExtra("wifiName"));
            }
        }
    });


    public void clickPasswordSwitch() {
        passwordOn = !passwordOn;
        if (passwordOn) {
            ivSwitchPassword.setImageResource(R.drawable.icon_signin_see);
            etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            ivSwitchPassword.setImageResource(R.drawable.icon_signin_see_not);
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        if (etPassword.getText().length() > 0) {
            etPassword.setSelection(etPassword.getText().length());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //释放handler
        SocketClientUtil.close(mClientUtil);
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
