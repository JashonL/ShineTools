package com.growatt.shinetools.module.localbox.configtype.usconfig;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.growatt.shinetools.R;
import com.growatt.shinetools.ShineToosApplication;
import com.growatt.shinetools.adapter.UsSettingAdapter;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.bean.USDebugSettingBean;
import com.growatt.shinetools.bean.UsSettingConstant;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.MaxWifiParseUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.utils.ActivityUtils;
import com.growatt.shinetools.utils.BtnDelayUtil;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.Mydialog;
import com.growatt.shinetools.widget.GridDivider;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;

import static com.growatt.shinetools.constant.GlobalConstant.END_USER;
import static com.growatt.shinetools.constant.GlobalConstant.KEFU_USER;
import static com.growatt.shinetools.constant.GlobalConstant.MAINTEAN_USER;
import static com.growatt.shinetools.modbusbox.SocketClientUtil.SOCKET_RECEIVE_BYTES;
import static com.growatt.shinetools.module.localbox.configtype.usconfig.USConfigTypeAllActivity.KEY_OF_ITEM_SETITEMSINDEX;

public class USPFsettingActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener,
        UsSettingAdapter.OnChildCheckLiseners, Toolbar.OnMenuItemClickListener {
    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.header_title)
    LinearLayout headerTitle;
    @BindView(R.id.rv_system)
    RecyclerView rvSystem;

    private UsSettingAdapter usParamsetAdapter;
    //?????????
    private String[] pfSetting;
    private String[] pfSettingRegister;

    //????????????
    private int[][] funs;
    private boolean isReceiveSucc = false;
    private int count = 0;

    //?????????????????????????????????????????????????????????
    private int[][][] funsSet;
    private int[] nowSet;

    private int user_type = KEFU_USER;

    @Override
    protected int getContentView() {
        return R.layout.activity_us_pf_setting;
    }

    @Override
    protected void initViews() {
        initToobar(toolbar);
        tvTitle.setText(R.string.android_key2924);
        toolbar.setOnMenuItemClickListener(this);

        rvSystem.setLayoutManager(new LinearLayoutManager(this));
        usParamsetAdapter = new UsSettingAdapter(new ArrayList<>(), this);
        int div = (int) getResources().getDimension(R.dimen.dp_1);
        GridDivider gridDivider = new GridDivider(ContextCompat.getColor(this, R.color.white), div, div);
        rvSystem.addItemDecoration(gridDivider);
        rvSystem.setAdapter(usParamsetAdapter);
        usParamsetAdapter.setOnItemClickListener(this);
    }

    @Override
    protected void initData() {
        user_type = ShineToosApplication.getContext().getUser_type();

        List<USDebugSettingBean> newlist = new ArrayList<>();

        if (user_type == END_USER || user_type == MAINTEAN_USER) {

            pfSetting = new String[]{getString(R.string.m392PF??????????????????) + "1~4"};

            pfSettingRegister = new String[]{
                    "", "", "", "", "", "", "", "", ""};

            for (int i = 0; i < pfSetting.length; i++) {
                USDebugSettingBean bean = new USDebugSettingBean();
                bean.setTitle(pfSetting[i]);
                int itemType = UsSettingConstant.SETTING_TYPE_NEXT;
                bean.setItemType(itemType);
                bean.setRegister(pfSettingRegister[i]);
                newlist.add(bean);
            }


        } else {

            pfSetting = new String[]{getString(R.string.m405??????PF???1), getString(R.string.m402??????PF), getString(R.string.m399????????????)
                    , getString(R.string.m400????????????), getString(R.string.m401??????PF), getString(R.string.m??????PF??????),
                    getString(R.string.m422??????????????????????????????), getString(R.string.m391PF????????????????????????) + "1~4", getString(R.string.m392PF??????????????????) + "1~4"};

            pfSettingRegister = new String[]{"", "", ""
                    , "", "", "", "", ""
                    , ""};

            for (int i = 0; i < pfSetting.length; i++) {
                USDebugSettingBean bean = new USDebugSettingBean();
                bean.setTitle(pfSetting[i]);
                int itemType = 0;
                switch (i) {
                    case 0://??????PF???1
                    case 5://??????PF??????
                        itemType = UsSettingConstant.SETTING_TYPE_SWITCH;
                        break;
                    case 1://??????PF
                    case 2://????????????
                    case 3://????????????
                    case 4://??????PF
                    case 6://PF????????????/????????????
                    case 7://PF????????????????????????1~4
                    case 8://PF??????1~4
                        itemType = UsSettingConstant.SETTING_TYPE_NEXT;
                        break;
                }

                bean.setItemType(itemType);
                bean.setRegister(pfSettingRegister[i]);
                newlist.add(bean);
            }


            funs = new int[][]{
                    {3, 89, 89},//?????????0
                    {3, 89, 89},//PV????????????
            };

            //?????????????????????????????????????????????????????????
            funsSet = new int[][][]{
                    {{6, 89, 0}, {6, 89, 1}}//?????????0
                    , {{6, 89, 0}, {6, 89, 1}}
            };
        }


        usParamsetAdapter.replaceData(newlist);


        if (funs == null || funs.length == 0) {

        } else {
            refresh();
        }
    }


    /**
     * ????????????
     */
    private void refresh() {
        Mydialog.Show(this);
        connectSendMsg();
    }

    /**
     * ?????????????????????
     */
    private void connectSendMsg() {
        connectServer();
    }

    //????????????
    private SocketClientUtil mClientUtil;

    private void connectServer() {
        mClientUtil = SocketClientUtil.newInstance();
        if (mClientUtil != null) {
            mClientUtil.connect(mHandler);
        }
    }

    /**
     * ???????????????handle
     */
    /**
     * ???????????????
     */
    StringBuilder sb = new StringBuilder();
    private String uuid;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            String text = "";
            int what = msg.what;
            switch (what) {
                case SocketClientUtil.SOCKET_EXCETION_CLOSE:
                    String message = (String) msg.obj;
                    text = "???????????????" + message;
                    break;
                case SocketClientUtil.SOCKET_CLOSE:
                    text = "????????????";
                    break;
                case SocketClientUtil.SOCKET_OPEN:
                    text = "????????????";
                    break;
                case SocketClientUtil.SOCKET_SEND_MSG:
                    BtnDelayUtil.sendMessage(this);
                    //?????????????????????????????????????????????
                    uuid = UUID.randomUUID().toString();
                    Message msgSend = Message.obtain();
                    msgSend.what = 100;
                    msgSend.obj = uuid;
                    mHandler.sendEmptyMessageDelayed(100, 3000);

                    String sendMsg = (String) msg.obj;
                    LogUtil.i("????????????:" + sendMsg);
//                    text = "??????????????????";
////                    mEditText.setText("");
//                    sb.append("Client:<br>")
//                            .append(sendMsg)
//                            .append("<br><br>");
                    break;
                case SocketClientUtil.SOCKET_RECEIVE_MSG:
                    //????????????????????????
                    isReceiveSucc = true;
                    //????????????????????????
//                    mHandler.sendEmptyMessage(100);

                    String recMsg = (String) msg.obj;
                    text = "??????????????????";
                    sb.append("Server:<br>")
                            .append(recMsg)
                            .append("<br><br>");
//                    Log.i("receive" ,recMsg);
                    break;
                //??????????????????
                case SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //?????????????????????
                        boolean isCheck = ModbusUtil.checkModbus(bytes);
                        if (isCheck) {
                            //???????????????????????????
                            parseMax(bytes, count);
                        }
                        if (count < funs.length - 1) {
                            count++;
                            mHandler.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        } else {
                            count = 0;
                            //????????????
                            SocketClientUtil.close(mClientUtil);
                            refreshFinish();
                        }

                        LogUtil.i("????????????:" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        count = 0;
                        //????????????
                        SocketClientUtil.close(mClientUtil);
                        Mydialog.Dismiss();
                    }
                    break;
                case SocketClientUtil.SOCKET_CONNECT:
                    text = "socket?????????";
                    break;
                case SocketClientUtil.SOCKET_SEND:
                    if (count < funs.length) {
                        sendMsg(mClientUtil, funs[count]);
                    }
                    break;
                case 100://??????????????????
                    String myuuid = (String) msg.obj;
                    if (uuid.equals(myuuid) && !isReceiveSucc) {
                        toast(R.string.android_key1134);
                    }
//                    refreshFinish();
                    break;
                case 101:
                    connectSendMsg();
                    break;
                default:
                    BtnDelayUtil.dealTLXBtn(this, what, 2500, mContext, toolbar);
                    break;
            }
        }
    };


    /**
     * ????????????
     *
     * @param bytes
     * @param count
     */
    private void parseMax(byte[] bytes, int count) {
        //??????????????????
        byte[] bs = RegisterParseUtil.removePro17(bytes);
        //??????int???
        int value = MaxWifiParseUtil.obtainValueOne(bs);
        switch (count) {
            case 0:
                usParamsetAdapter.getData().get(0).setValue(String.valueOf(value));
                break;
            case 1:
                usParamsetAdapter.getData().get(5).setValue(String.valueOf(value));
                break;
        }
        usParamsetAdapter.notifyDataSetChanged();
    }


    /**
     * ????????????
     */
    private void refreshFinish() {
        Mydialog.Dismiss();
    }

    /**
     * ???????????????????????????????????????????????????
     *
     * @param clientUtil
     * @param sends
     * @return??????????????????????????????
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


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        int type = 0;
        if (user_type == END_USER || user_type == MAINTEAN_USER) {
            if (position == 0) {
                type = 6;
            }
        } else {
            switch (position) {
                case 1:
                    type = 0;
                    break;
                case 2:
                    type = 1;
                    break;
                case 3:
                    type = 2;
                    break;
                case 4:
                    type = 3;
                    break;
                case 6:
                    type = 4;
                    break;
                case 7:
                    type = 5;
                    break;
                case 8:
                    type = 6;
                    break;
            }
        }

        if (user_type != END_USER && user_type != MAINTEAN_USER) {
            if (position == 0 || position == 5) return;
        }

        String title = usParamsetAdapter.getData().get(position).getTitle();
        Intent intent = new Intent(USPFsettingActivity.this, USAllSettingItemActivity.class);
        intent.putExtra(KEY_OF_ITEM_SETITEMSINDEX, type);
        intent.putExtra("bartitle", title);
        ActivityUtils.startActivity(USPFsettingActivity.this, intent, false);

    }

    @Override
    public void oncheck(boolean check, int position) {
        int value = check ? 1 : 0;
        switch (position) {
            case 0://???????????????
                nowSet = funsSet[0][value];
                connectServerWrite();
                break;
            case 5:
                nowSet = funsSet[2][value];
                connectServerWrite();
                break;
        }

        usParamsetAdapter.getData().get(position).setValue(String.valueOf(value));
        usParamsetAdapter.notifyDataSetChanged();
    }

    //????????????:???????????????
    private SocketClientUtil mClientUtilW;

    private void connectServerWrite() {
        Mydialog.Show(mContext);
        mClientUtilW = SocketClientUtil.connectServer(mHandlerW);
    }

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
                    sendBytes = sendMsg(mClientUtilW, nowSet);
                    LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(sendBytes));
                    break;
                //??????????????????
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //?????????????????????
                        boolean isCheck = MaxUtil.checkReceiverFull(bytes);
                        if (isCheck) {
                            //??????????????????
//                            byte[] bs = RegisterParseUtil.removePro17Fun6(bytes);
//                            //??????int???
//                            int value0 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 1, 0, 1));
//                            //??????ui
//                            mTvContent1.setText(readStr + ":" + value0);
                            toast(R.string.all_success);
                        } else {
                            toast(R.string.all_failed);
                        }
                        LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //??????tcp??????
                        SocketClientUtil.close(mClientUtilW);
                        BtnDelayUtil.refreshFinish();
                    }
                    break;
                default:
                    BtnDelayUtil.dealTLXBtnWrite(this, what, mContext, toolbar);
                    break;
            }
        }
    };
}
