package com.growatt.shinetools.module;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

import com.growatt.shinetools.R;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.constant.GlobalConstant;
import com.growatt.shinetools.db.realm.RealmUtils;
import com.growatt.shinetools.modbusbox.DataLogApDataParseUtil;
import com.growatt.shinetools.modbusbox.DatalogApUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.modbusbox.bean.DatalogResponBean;
import com.growatt.shinetools.module.datalogUpdata.DatalogUpdataActivity;
import com.growatt.shinetools.utils.ActivityUtils;
import com.growatt.shinetools.utils.CircleDialogUtils;
import com.growatt.shinetools.utils.CommenUtils;
import com.growatt.shinetools.utils.DialogUtils;
import com.growatt.shinetools.utils.Log;
import com.growatt.shinetools.utils.MyToastUtils;
import com.growatt.shinetools.utils.WifiTypeEnum;
import com.growatt.shinetools.utils.datalogupdata.FilePathBean;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ConnetApHostActivity extends BaseActivity {
    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.header_title)
    LinearLayout headerTitle;
    @BindView(R.id.tv_sub_title)
    TextView tvSubTitle;
    @BindView(R.id.view_serial_background)
    View viewSerialBackground;
    @BindView(R.id.tv_serialnum)
    AppCompatTextView tvSerialnum;
    @BindView(R.id.tv_serialnum_value)
    AppCompatTextView tvSerialnumValue;
    @BindView(R.id.view_wifi_background)
    View viewWifiBackground;
    @BindView(R.id.tv_ssid)
    AppCompatTextView tvSsid;
    @BindView(R.id.tv_wifi_ssid)
    AppCompatTextView tvWifiSsid;
    @BindView(R.id.tv_fresh)
    TextView tvFresh;
    @BindView(R.id.tv_arrow)
    TextView tvArrow;
    @BindView(R.id.card_ok)
    CardView cardOk;


    private boolean isShow = false;
    //wifi名称
    private String currentSSID;
    private DialogFragment wifiSettingFragment;
    private DialogFragment gpsDialogFragment;
    private String serialnum = "0000000000";
    //wifi名称
    public String mIP = "192.168.10.100";//服务器地址
    public int mPort = 5280;//服务器端口号


    private boolean activitDisConnect = false;
    //版本
    private String version = "";
    //设备类型
    private String deviceType = "";
    //升级检测版本路径类
    private FilePathBean pathBean;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            String text = "";
            int what = msg.what;
            switch (what) {
                case SocketClientUtil.SOCKET_CLOSE:
                    text = "连接关闭";
                    if (!activitDisConnect) {
                        socketEceptionDialog();
                    }
                    break;
                case SocketClientUtil.SOCKET_OPEN:
                    text = "连接成功";
                    Log.d("liaojinsha", text);
                    DialogUtils.getInstance().closeLoadingDialog();
                    break;
                case SocketClientUtil.SOCKET_SEND_MSG:
                    text = "发送消息";
                    Log.d("liaojinsha", text);
                    break;
                case SocketClientUtil.SOCKET_RECEIVE_MSG:
                    text = "回应字符串消息";
                    String receiString = (String) msg.obj;
                    Log.d("liaojinsha", text + receiString);
                    break;

                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    DialogUtils.getInstance().closeLoadingDialog();
                    text = "回应字节消息";
                    byte[] receiByte = (byte[]) msg.obj;
                    //检测内容正确性

                    try {
                        boolean isCheck = DatalogApUtil.checkData(receiByte);
                        if (isCheck) {
                            //接收正确，开始解析
                            byte type = receiByte[7];

                            //1.去除头部包头
                            byte[] removePro = DataLogApDataParseUtil.removePro(receiByte);
                            if (removePro == null) {
                                MyToastUtils.toast(R.string.android_key7);
                                return;
                            }
                            Log.d("去除头部包头" + CommenUtils.bytesToHexString(removePro));
                            //2.解密
                            byte[] bytes = DatalogApUtil.desCode(removePro);
                            Log.d("解密" + CommenUtils.bytesToHexString(bytes));
                            //3.解析数据
                            parserData(type, bytes);
                        }
                    } catch (Exception e) {
                        MyToastUtils.toast(R.string.android_key7);
                        e.printStackTrace();
                    }

                    Log.d("liaojinsha", text);
                    break;
                case SocketClientUtil.SOCKET_CONNECT:
                    text = "socket已连接";
                    Log.d("liaojinsha", text);
                    break;
                case SocketClientUtil.SOCKET_SEND:
                    Log.d("liaojinsha", "socket已连接发送消息");
//                    this.postDelayed(() -> sendCmdConnect(), 3500);
                    //获取设备的类型和版本
                    sendCmdConnect();
                    break;
                case 100://恢复按钮点击

                    break;
                case 101:
//                    connectSendMsg();
                    break;

                case SocketClientUtil.SOCKET_SERVER_SET://请检查热点连接
                    if (!activitDisConnect) {
                        socketEceptionDialog();
                    }
                    break;

                default:

                    break;
            }

        }
    };


    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, final Intent intent) {

            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case ConnectivityManager.CONNECTIVITY_ACTION:
                    case "android.net.wifi.CONFIGURED_NETWORKS_CHANGE":
                    case "android.net.wifi.LINK_CONFIGURATION_CHANGED":
                    case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                    case WifiManager.SUPPLICANT_STATE_CHANGED_ACTION:
                        checkWifiNetworkStatus();
                        break;
                }
            }
        }
    };


    @Override
    protected int getContentView() {
        return R.layout.activity_connet_ap_host;
    }


    @OnClick({R.id.card_ok, R.id.tv_fresh})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.card_ok:
                searchDevice();
                break;
            case R.id.tv_fresh:
                ActivityUtils.toWifiSet(ConnetApHostActivity.this);
                break;
        }
    }


    private void searchDevice() {
        if (TextUtils.isEmpty(currentSSID)) {
            MyToastUtils.toast(R.string.android_key2618);
        } else {
            toSetWifiParams();
        }

    }


    private void toSetWifiParams() {
        boolean b = nextShowWiFiSetting();
        if (!b) return;
        //连接socket判断版本
        connectSendMsg();

    }


    private void sendCmdConnect() {
        //1.设备类型 2.软件版本号 3.
        int[] paramByte = new int[]{DataLogApDataParseUtil.DATALOGGER_TYPE, DataLogApDataParseUtil.FIRMWARE_VERSION, DataLogApDataParseUtil.FOTA_FILE_TYPE};
        byte[] bytes = new byte[0];
        try {
            bytes = DatalogApUtil.sendMsg(DatalogApUtil.DATALOG_GETDATA_0X19, serialnum, paramByte);
        } catch (Exception e) {
            e.printStackTrace();
        }
        DialogUtils.getInstance().showLoadingDialog(this);
        mClientUtil.sendMsg(bytes);
    }


    private void socketEceptionDialog() {
        String host = getString(R.string.android_key2965) + serialnum;

        CircleDialogUtils.showCommentDialog(this, getString(R.string.android_key2263),
                host, getString(R.string.android_key1935),
                getString(R.string.android_key2152), Gravity.CENTER, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                        startActivity(intent);
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
    }


    private boolean nextShowWiFiSetting() {
        if (!serialnum.equals(currentSSID)) {
            if (wifiSettingFragment == null) {
//                String remind = getString(R.string.当前连接的WiFi名称和采集器热点名称不一致) + getString(R.string.m270是否跳转连接WiFi) + ":" + serialnum;
                String remind = getString(R.string.android_key2981);
                wifiSettingFragment = CircleDialogUtils.showCommentDialog(this, getString(R.string.android_key2601),
                        remind, getString(R.string.android_key2982), getString(R.string.android_key2152), Gravity.CENTER, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityUtils.toWifiSet(ConnetApHostActivity.this);
                                wifiSettingFragment.dismiss();
                                wifiSettingFragment = null;
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                wifiSettingFragment = null;
                            }
                        });
            }
            return false;
        }
        return true;
    }


    /*建立TCP连接*/
    private void connectSendMsg() {
        DialogUtils.getInstance().showLoadingDialog(this);
        connectServer();
    }


    //连接对象
    private SocketClientUtil mClientUtil;

    private void connectServer() {
        mClientUtil = SocketClientUtil.newInstance();
        if (mClientUtil != null) {
            mClientUtil.connect(mHandler, mIP, mPort);
        }
    }


    @Override
    protected void initViews() {
        initToobar(toolbar);
        tvTitle.setText(R.string.android_key530);
    }

    @Override
    protected void initData() {
        pathBean = RealmUtils.queryFilePathList();
        if (pathBean != null) {
            Log.d("升级检测文件" + pathBean.toString());
        }

        serialnum = getIntent().getStringExtra(GlobalConstant.SCAN_RESULT);
        if (!TextUtils.isEmpty(serialnum)) {
            tvSerialnumValue.setText(serialnum);
        }
        checkWifiNetworkStatus();

    }


    @Override
    public void onStart() {
        super.onStart();
        initWifi();
    }

    /**
     * 广播接收器，接收连接wifi的广播
     */
    private void initWifi() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
//        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction("android.net.wifi.CONFIGURED_NETWORKS_CHANGE");
        filter.addAction("android.net.wifi.LINK_CONFIGURATION_CHANGED");
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        registerReceiver(mBroadcastReceiver, filter);
    }


    public void checkWifiNetworkStatus() {
        try {
            if (CommenUtils.isWiFi(this)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//8.1
                    gpsStatus();
                } else {
                    currentSSID = CommenUtils.getWifiSsid(this);
                }
                setWiFiName();
                showToWiFiSetting();
            } else {
                currentSSID = null;
                tvWifiSsid.setText(R.string.android_key2259);
                showToWiFiSetting();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void setWiFiName() {
        if (TextUtils.isEmpty(currentSSID))
            tvWifiSsid.setText(R.string.android_key2259);
        else tvWifiSsid.setText(currentSSID);
    }


    /**
     * 判断Gps是否打开
     */
    private void gpsStatus() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (lm != null) {
            boolean ok = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (ok) {
                try {
                    currentSSID = CommenUtils.getWifiSsid(this);
                    setWiFiName();
                    showToWiFiSetting();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                showGpsDialog();
            }
        }
    }


    private void showToWiFiSetting() {
        if (!serialnum.equals(currentSSID) && !isShow) {
            if (wifiSettingFragment == null) {
                isShow = true;
                String remind = getString(R.string.android_key300) + getString(R.string.android_key593) + ":" + serialnum;
                wifiSettingFragment = CircleDialogUtils.showCommentDialog(this, getString(R.string.android_key2601), remind, getString(R.string.android_key1935), getString(R.string.android_key2152), Gravity.CENTER, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityUtils.toWifiSet(ConnetApHostActivity.this);
                        wifiSettingFragment = null;
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        wifiSettingFragment = null;
                    }
                });
            }
        }

    }


    /**
     * 开启GPS弹框
     */

    private void showGpsDialog() {
        if (gpsDialogFragment == null) {
            gpsDialogFragment = CircleDialogUtils.showCommentDialog(this,
                    getString(R.string.android_key2263), getString(R.string.android_key2258),
                    getString(R.string.android_key1935), getString(R.string.android_key2152), Gravity.CENTER, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            gpsDialogFragment.dismiss();
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    });
        }

    }


    /**
     * 解析数据
     *
     * @param bytes
     */
    private void parserData(byte type, byte[] bytes) {
        try {
            //1.字节数组成bean
            DatalogResponBean bean = DataLogApDataParseUtil.paserData(type, bytes);
            if (bean.getFuncode() == DatalogApUtil.DATALOG_GETDATA_0X19) {
                int statusCode = bean.getStatusCode();
                if (statusCode == 1) {
                    MyToastUtils.toast(R.string.android_key3129);
                }
                List<DatalogResponBean.ParamBean> paramBeanList = bean.getParamBeanList();
                for (int i = 0; i < paramBeanList.size(); i++) {
                    DatalogResponBean.ParamBean paramBean = paramBeanList.get(i);
                    int num = paramBean.getNum();
                    String value = paramBean.getValue();
                    if (TextUtils.isEmpty(value)) continue;
                    switch (num) {
                        case DataLogApDataParseUtil.DATALOGGER_TYPE:
                            deviceType = value;
                            break;
                        case DataLogApDataParseUtil.FIRMWARE_VERSION:
                            version = value;
                            break;
                    }
                }
                getFilePath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void getFilePath() {
        if (pathBean == null) {
            toConfig();
            return;
        }
        boolean needUpdata = false;
        //判断采集器类型
        Log.d("获取文件类型" + deviceType);
        if (String.valueOf(WifiTypeEnum.SHINE_WIFI_X).equals(deviceType)) {
            String xVersion = pathBean.getShineX_version();
            int equels = xVersion.compareTo(version);
            Log.d("当前文件版本" + xVersion + "文件对比结果：" + equels);
            if (equels > 0) {//需要升级
                needUpdata = true;
            }
        } else {
            String sVersion = pathBean.getShineS_version();
            int equels = sVersion.compareTo(version);
            if (equels > 0) {
                needUpdata = true;
            }
        }

        if (needUpdata) {
            toUpdata();
        } else {
            activitDisConnect = true;
            SocketClientUtil.close(mClientUtil);
            toConfig();
        }
    }

    private void toConfig() {
        ActivityUtils.gotoActivity(this, DeviceTypeActivity.class, true);

    }


    private void toUpdata(){
        CircleDialogUtils.showCommentDialog(this, getString(R.string.android_key2991),
                getString(R.string.android_key2992), getString(R.string.android_key1935), "", Gravity.CENTER, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConnetApHostActivity.this, DatalogUpdataActivity.class);
                intent.putExtra("ip", mIP);
                intent.putExtra("port", mPort);
                intent.putExtra("devId", serialnum);
                ActivityUtils.startActivity(ConnetApHostActivity.this,intent,false);
            }

        }, null);


    }



    private void unRegisterWifiReceiver() {
        try {
            if (mBroadcastReceiver != null) {
                unregisterReceiver(mBroadcastReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        unRegisterWifiReceiver();
        super.onDestroy();
    }


}
