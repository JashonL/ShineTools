package com.growatt.shinetools.module.datalogUpdata;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.Group;

import com.growatt.shinetools.R;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.db.realm.RealmUtils;
import com.growatt.shinetools.modbusbox.DataLogApDataParseUtil;
import com.growatt.shinetools.modbusbox.DatalogApUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.modbusbox.bean.DatalogResponBean;
import com.growatt.shinetools.utils.CircleDialogUtils;
import com.growatt.shinetools.utils.CommenUtils;
import com.growatt.shinetools.utils.DialogUtils;
import com.growatt.shinetools.utils.Log;
import com.growatt.shinetools.utils.MyToastUtils;
import com.growatt.shinetools.utils.WifiTypeEnum;
import com.growatt.shinetools.utils.datalogupdata.FilePathBean;
import com.growatt.shinetools.utils.datalogupdata.UpdateDatalogUtils;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class DatalogUpdataActivity extends BaseActivity {


    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.header_title)
    LinearLayout headerTitle;
    @BindView(R.id.grop_updataing)
    Group gropUpdataing;
    @BindView(R.id.iv_updata)
    ImageView ivUpdata;
    @BindView(R.id.bp_progress)
    ProgressBar bpProgress;
    @BindView(R.id.tv_progress)
    TextView tvProgress;
    @BindView(R.id.tv_tips)
    TextView tvTips;
    @BindView(R.id.tv_tips2)
    TextView tvTips2;
    @BindView(R.id.grop_updata_error)
    Group gropUpdataError;
    @BindView(R.id.iv_updata_error)
    ImageView ivUpdataError;
    @BindView(R.id.tv_error_tittle)
    TextView tvErrorTittle;
    @BindView(R.id.tv_error_check)
    TextView tvErrorCheck;
    @BindView(R.id.tv_host_name)
    TextView tvHostName;
    @BindView(R.id.btn_retry)
    Button btnRetry;
    @BindView(R.id.btn_check_host)
    Button btnCheckHost;
    @BindView(R.id.grop_reseting)
    Group gropReseting;
    @BindView(R.id.iv_reseting)
    ImageView ivReseting;
    @BindView(R.id.bp_reseting)
    ProgressBar bpReseting;
    @BindView(R.id.tv_reseting)
    TextView tvReseting;
    @BindView(R.id.tv_reseting_tips1)
    TextView tvResetingTips1;
    @BindView(R.id.tv_reseting_tips2)
    TextView tvResetingTips2;

    private String ip;
    private int port;
    private String devId;


    //????????????
    private SocketClientUtil mClientUtil;
    //?????????????????????1??????
    private int currNum = 0;
    //??????
    private String version = "";
    //????????????
    private String deviceType = "";
    //????????????????????????
    private String fotaFileType = "1";

    //??????????????????
    private String path;

    //????????????
    private int errornum = 0;

    private boolean isUpdating = false;


    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            String text = "";
            int what = msg.what;
            switch (what) {
                case SocketClientUtil.SOCKET_CLOSE:
                    text = "????????????";
                    if (isUpdating) {
                        gropUpdataError.setVisibility(View.VISIBLE);
                        gropUpdataing.setVisibility(View.GONE);
                        gropReseting.setVisibility(View.GONE);
                    }
                    break;
                case SocketClientUtil.SOCKET_OPEN:
                    text = "????????????";
                    Log.i("liaojinsha", text);
                    DialogUtils.getInstance().showLoadingDialog(DatalogUpdataActivity.this);
                    break;
                case SocketClientUtil.SOCKET_SEND_MSG:
                    text = "????????????";
                    Log.i("liaojinsha", text);
                    break;
                case SocketClientUtil.SOCKET_RECEIVE_MSG:
                    text = "?????????????????????";
                    String receiString = (String) msg.obj;
                    Log.i("liaojinsha", text + receiString);
                    break;

                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    DialogUtils.getInstance().closeLoadingDialog();
                    text = "??????????????????";
                    byte[] receiByte = (byte[]) msg.obj;
                    //?????????????????????

                    try {
                        boolean isCheck = DatalogApUtil.checkData(receiByte);
                        if (isCheck) {
                            //???????????????????????????
                            byte type = receiByte[7];

                            //1.??????????????????
                            byte[] removePro = DataLogApDataParseUtil.removePro(receiByte);
                            if (removePro == null) {
                                MyToastUtils.toast(R.string.android_key7);
                                return;
                            }
                            Log.i("??????????????????" + CommenUtils.bytesToHexString(removePro));
                            //2.??????
                            byte[] bytes = DatalogApUtil.desCode(removePro);
                            Log.i("??????" + CommenUtils.bytesToHexString(bytes));
                            //3.????????????
                            parserData(type, bytes);
                        }
                    } catch (Exception e) {
                        MyToastUtils.toast(R.string.android_key7);
                        e.printStackTrace();
                    }

                    Log.i("liaojinsha", text);
                    break;
                case SocketClientUtil.SOCKET_CONNECT:
                    text = "socket?????????";
                    Log.i("liaojinsha", text);
                    break;
                case SocketClientUtil.SOCKET_SEND:
                    Log.i("liaojinsha", "socket?????????????????????");
//                    this.postDelayed(() -> sendCmdConnect(), 3500);
                    //??????????????????????????????
                    sendCmdConnect();
                    break;
                case 100://??????????????????

                    break;
                case 101:
//                    connectSendMsg();
                    break;

                case SocketClientUtil.SOCKET_SERVER_SET://?????????????????????
                    socketEceptionDialog();
                    break;

                default:

                    break;
            }

        }
    };
    private List<ByteBuffer> file;
    private FilePathBean pathBean;


    @Override
    protected int getContentView() {
        return R.layout.activity_datalog_updata;
    }

    @Override
    protected void initViews() {
        initToobar(toolbar);
        tvTitle.setText(R.string.android_key2936);
        gropUpdataing.setVisibility(View.VISIBLE);
        gropUpdataError.setVisibility(View.GONE);
        gropReseting.setVisibility(View.GONE);
    }

    @Override
    protected void initData() {
        ip = getIntent().getStringExtra("ip");
        port = getIntent().getIntExtra("port", -1);
        devId = getIntent().getStringExtra("devId");

        pathBean = RealmUtils.queryFilePathList();
    }


    /*??????TCP??????*/
    private void connectSendMsg() {
        DialogUtils.getInstance().showLoadingDialog(this);
        connectServer();
    }


    /**
     * ??????
     */

    private void connectServer() {
        mClientUtil = SocketClientUtil.newInstance();
        Socket socket = mClientUtil.getSocket();
        if (mClientUtil != null) {
            if (socket == null || !socket.isConnected()) {//??????TCP?????????
                mClientUtil.connect(mHandler, ip, port);
            } else {//????????????
                mClientUtil.switchHandler(mHandler);
                sendCmdConnect();
            }
        }
    }


    private void sendCmdConnect() {
        tvTitle.setText(R.string.android_key2936);
        tvTips.setText(R.string.android_key2937);
        tvTips2.setText(R.string.android_key2938);

        gropUpdataing.setVisibility(View.VISIBLE);
        gropUpdataError.setVisibility(View.GONE);
        gropReseting.setVisibility(View.GONE);
        //1.???????????? 2.??????????????? 3.
        int[] paramByte = new int[]{DataLogApDataParseUtil.DATALOGGER_TYPE, DataLogApDataParseUtil.FIRMWARE_VERSION, DataLogApDataParseUtil.FOTA_FILE_TYPE};
        byte[] bytes = new byte[0];
        try {
            bytes = DatalogApUtil.sendMsg(DatalogApUtil.DATALOG_GETDATA_0X19, devId, paramByte);
        } catch (Exception e) {
            e.printStackTrace();
        }
        DialogUtils.getInstance().showLoadingDialog(this);
        mClientUtil.sendMsg(bytes);
    }


    /**
     * ????????????
     *
     * @param bytes
     */
    private void parserData(byte type, byte[] bytes) {
        try {
            //1.???????????????bean
            DatalogResponBean bean = DataLogApDataParseUtil.paserData(type, bytes);
            if (bean==null)return;
            if (bean.getFuncode() == DatalogApUtil.DATALOG_GETDATA_0X26) {
                List<DatalogResponBean.ParamBean> paramBeanList = bean.getParamBeanList();
                for (int i = 0; i < paramBeanList.size(); i++) {
                    DatalogResponBean.ParamBean paramBean = paramBeanList.get(i);
                    int dataNum = paramBean.getDataNum();
                    int dataCode = paramBean.getDataCode();
                    switch (dataCode) {
                        case 0://??????,???????????????
                            if (dataNum != file.size() - 1) {
                                currNum = dataNum + 1;
                                senDataToLoger();
                            } else {//????????????
                                isUpdating = false;
                                gropUpdataError.setVisibility(View.GONE);
                                gropUpdataing.setVisibility(View.GONE);
                                gropReseting.setVisibility(View.VISIBLE);
                                bpReseting.setProgress(100);
                                tvReseting.setText(100 + "%");
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        CircleDialogUtils.showCommentDialog(DatalogUpdataActivity.this,
                                                getString(R.string.android_key2263), getString(R.string.android_key2969),
                                                getString(R.string.android_key2253), "", Gravity.CENTER, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                finish();
                                            }
                                        }, null);
                                    }
                                }, 4000);

//
//                                timerTask = new TimerTask() {
//                                    final int max = 60;
//                                    int progress = 0;
//
//                                    @Override
//                                    public void run() {
//                                        progress++;
//                                        DatalogStep2ApUpdataActivity.this.runOnUiThread(() -> {
//                                            if (progress >= max) {
//                                                timer.cancel();
//                                                CircleDialogUtils.showCommentDialog(DatalogStep2ApUpdataActivity.this, getString(R.string.????????????), getString(R.string.updata_success), getString(R.string.about_cache_ok), "", Gravity.CENTER, new View.OnClickListener() {
//                                                    @Override
//                                                    public void onClick(View v) {
//                                                        finish();
//                                                    }
//                                                }, null, null);
//                                            }
//                                            int currentProgress = progress * 100 / max;
//                                            String current = currentProgress + "%";
//                                            tvReseting.setText(current);
//                                            bpReseting.setProgress(currentProgress);
//                                        });
//                                    }
//                                };
//                                timer.schedule(timerTask, 0, 50);
                            }
                            //????????????
                            tvTips.setText(R.string.android_key2934);
                            tvTips2.setText(R.string.android_key2935);
                            int progress = (currNum + 1) * 100 / (file.size());
                            Log.i("????????????........?????????/??????" + currNum + "/" + file.size());
                            bpProgress.setProgress(progress);
                            tvProgress.setText(progress + "%");
                            errornum = 0;
                            break;
                        case 1://????????????????????????????????????
                            errornum++;
                            if (errornum > 3) {//??????????????????
                                updataError();
                            } else {
                                senDataToLoger();
                            }

                            break;
                        default://??????????????????????????????????????????????????????????????????
                            errornum++;
                            if (errornum > 3) {
                                updataError();
                            } else {
                                currNum = 0;
                                senDataToLoger();
                            }

                            break;
                    }


                }
            } else if (bean.getFuncode() == DatalogApUtil.DATALOG_GETDATA_0X19) {
                int statusCode = bean.getStatusCode();
                if (statusCode == 1) {
                    MyToastUtils.toast(R.string.android_key3129);
                    return;
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
                        case DataLogApDataParseUtil.FOTA_FILE_TYPE:
                            fotaFileType = value;
                            break;
                    }

                }
                getFilePath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void quiteDialog() {
        if (isUpdating) {
            CircleDialogUtils.showCommentDialog(this, getString(R.string.android_key2263),
                    getString(R.string.android_key2970), getString(R.string.android_key1935),
                    getString(R.string.android_key2152), Gravity.CENTER, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //2.??????TCP??????
                            SocketClientUtil.close(mClientUtil);
                            mClientUtil = null;
                            finish();
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    });
        } else {
            finish();
        }
    }


    //????????????
    private void updataError() {
        isUpdating = false;
        //???????????????
        errornum = 0;
        gropUpdataError.setVisibility(View.VISIBLE);
        gropUpdataing.setVisibility(View.GONE);
        gropReseting.setVisibility(View.GONE);
        //2.??????TCP??????
        SocketClientUtil.close(mClientUtil);
        mClientUtil = null;
    }


    private void getFilePath() {
        Log.i("??????????????????" + deviceType);
        if (pathBean == null) return;
        //?????????????????????
        if (String.valueOf(WifiTypeEnum.SHINE_WIFI_X).equals(deviceType)) {
//            String xVersion = SharedPreferencesUnit.getInstance(this).get(Constant.SHINE_X_VERSION);
            String xVersion = pathBean.getShineX_version();
            int equels = xVersion.compareTo(version);
            Log.i("??????????????????" + xVersion + "?????????????????????" + equels);
            if (equels > 0) {
                if ("1".equals(fotaFileType)) {
//                    path = SharedPreferencesUnit.getInstance(this).get(Constant.SHINE_X_PATH_1);
                    path = pathBean.getShineX_user1();
                } else {
//                    path = SharedPreferencesUnit.getInstance(this).get(Constant.SHINE_X_PATH_2);
                    path = pathBean.getShineX_user2();
                }
            }

        } else {
//            String sVersion = SharedPreferencesUnit.getInstance(this).get(Constant.SHINE_S_VERSION);
            String sVersion = pathBean.getShineS_version();
            int equels = sVersion.compareTo(version);
            if (equels > 0) {
                if ("1".equals(fotaFileType)) {
//                    path = SharedPreferencesUnit.getInstance(this).get(Constant.SHINE_S_PATH_1);
                    path = pathBean.getShineS_user1();
                } else {
//                    path = SharedPreferencesUnit.getInstance(this).get(Constant.SHINE_S_PATH_2);
                    path = pathBean.getShineS_user2();
                }
            }
        }
        if (TextUtils.isEmpty(path)) {
            //??????????????????
            return;
        }
        //?????????
        try {
            Log.i("??????????????????" + path);
            file = UpdateDatalogUtils.getFileByFis(path);
            Log.i("??????????????????" + file.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (file != null) {
            try {
                senDataToLoger();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    private void senDataToLoger() throws Exception {
        isUpdating = true;
        byte[] bytes = DatalogApUtil.updataDatalog(DatalogApUtil.DATALOG_GETDATA_0X26, devId, file.size(), currNum, file.get(currNum).array());
        mClientUtil.sendMsg(bytes);
    }


    @OnClick({R.id.btn_retry, R.id.btn_check_host})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_retry:
                connectSendMsg();
                break;
            case R.id.btn_check_host:
                tosettingWifi();
                break;
        }
    }


    private void tosettingWifi() {
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        startActivity(intent);
    }


    private void socketEceptionDialog() {
        String text = getString(R.string.android_key2965)+devId;
        CircleDialogUtils.showCommentDialog(this, getString(R.string.android_key2263),
                text, getString(R.string.android_key1935), getString(R.string.android_key2152),
                Gravity.CENTER, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tosettingWifi();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            quiteDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //??????handler
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }


}
