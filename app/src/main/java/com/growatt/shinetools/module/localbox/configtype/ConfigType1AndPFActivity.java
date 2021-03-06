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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.growatt.shinetools.R;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.modbusbox.Arith;
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

import static com.growatt.shinetools.utils.BtnDelayUtil.TIMEOUT_RECEIVE;


public class ConfigType1AndPFActivity extends BaseActivity implements Toolbar.OnMenuItemClickListener{
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
    @BindView(R.id.btnSelect)
    Button btnSelect;
    @BindView(R.id.tvContent2)
    TextView tvContent2;
    @BindView(R.id.btnSetting)
    Button btnSetting;

    //????????????
    private String mTitle;
    private int mType = -1;
    //????????????item
    private String[][] items;
    private String[] nowItems;
    //????????????????????????????????????????????????????????????????????????
    private int[][] funs;
    //???????????????
    private int[][] nowSet;
    private int[][][] nowSetFull;
    //????????????????????????
    private String[][] titles;

    private Context mContext;

    @Override
    protected int getContentView() {
        return R.layout.activity_config_type1_and_pf;
    }

    @Override
    protected void initViews() {
        mContext = this;
    }

    @Override
    protected void initData() {
        initIntent();
        initHeaderView();
        initString();
    }


    private void initString() {
        readStr = getString(R.string.android_key811);
        //?????????????????????
        /*????????????PF??????5??????10000+?????????*10000??????89(Hold)???1???
          ????????????PF??????5??????10000-?????????*10000??????89(Hold)???1???
          ?????????=(?????????-10000)/10000
         */
        nowSetFull = new int[][][]{
                {{6, 2, 1}, {6, 3, -1}},
                {{6, 2, 1}, {6, 4, -1}, {6, 89, 5}},
                {{6, 2, 1}, {6, 4, -1}, {6, 89, 4}},
                {{6, 2, 1}, {6, 5, -1}, {6, 89, 1}},
                {{6, 2, 1}, {6, 5, -1}, {6, 89, 1}}
        };
        nowSet = nowSetFull[mType];
        //????????????????????????????????????????????????????????????????????????
        funs = new int[][]{
                {3, 0, 5}
        };
        //?????????????????????
        items = new String[][]{
                {getString(R.string.android_key889), getString(R.string.android_key893)},
        };
        //????????????????????????
        titles = new String[][]{
                {getString(R.string.android_key836), mTitle},
                {getString(R.string.android_key836), mTitle, ""},
                {getString(R.string.android_key836), mTitle, ""},
                {getString(R.string.android_key836), mTitle, ""},
                {getString(R.string.android_key836), mTitle, ""},
                {getString(R.string.android_key836), mTitle, ""}
        };
        tvTitle1.setText(titles[0][1]);
        tvTitle2.setText(titles[0][0]);
        nowItems = items[0];
        //?????????????????????
        btnSelect.setText(nowItems[1]);
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
        initToobar(toolbar);
        tvTitle.setText(mTitle);
        toolbar.inflateMenu(R.menu.comment_right_menu);
        toolbar.getMenu().findItem(R.id.right_action).setTitle(R.string.android_key816);
        toolbar.setOnMenuItemClickListener(this);
    }


    //?????????????????????
    private void readRegisterValue() {
        connectServer();
    }

    //????????????:??????????????????
    private SocketClientUtil mClientUtil;

    private void connectServer() {
        Mydialog.Show(mContext);
        mClientUtil = SocketClientUtil.connectServer(mHandler);
    }


    /**
     * ???????????????handle
     */
    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //????????????
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessage(this);
                    byte[] sendBytesR = sendMsg(mClientUtil, funs[0]);
                    LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(sendBytesR));
                    break;
                //??????????????????
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //?????????????????????
                        boolean isCheck = ModbusUtil.checkModbus(bytes);
                        if (isCheck) {
                            //??????????????????
                            byte[] bs = RegisterParseUtil.removePro17(bytes);
                            int content1 = 0;
//                            int content2 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes125(bs,2,0,1));
                            //??????ui
                            switch (mType) {
                                case 0:
                                    content1 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3, 0, 1));
                                    tvContent1.setText(readStr + ":" + content1);
                                    break;
                                case 1:
                                case 2:
                                    content1 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 4, 0, 1));
                                    tvContent1.setText(readStr + ":" + content1);
                                    break;
                                case 3:
                                case 4:
                                    content1 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 5, 0, 1));
                                    double result2 = Arith.div(content1 - 10000, 10000);
                                    if (result2 == -1) result2 = 1;
                                    tvContent1.setText(readStr + ":" + result2);
                                    break;
                            }
//                            mTvContent2.setText(readStr + ":" + content2);
                            toast(R.string.android_key121);
                        } else {
                            toast(R.string.android_key3129);
                        }
                        LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //??????tcp??????
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
    //????????????:??????????????????
    private SocketClientUtil mClientUtilWriter;

    //?????????????????????
    private void writeRegisterValue() {
        Mydialog.Show(mContext);
        mClientUtilWriter = SocketClientUtil.connectServer(mHandlerWrite);
    }

    /**
     * ????????????handle
     */
    //???????????????????????????:{6, 2, 1}, {6, 4, -1}, {6, 89, 5}
    private byte[] sendBytes;
    //???????????????
    private int nowPos = -1;
    Handler mHandlerWrite = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //????????????
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessageWrite(this);
                    if (nowSet != null) {
                        if (nowPos >= nowSet.length - 1) {
                            nowPos = -1;
                            //??????tcp??????
                            if (mClientUtilWriter != null) {
                                mClientUtilWriter.closeSocket();
                                BtnDelayUtil.refreshFinish();
                                //??????????????????
                                this.removeMessages(TIMEOUT_RECEIVE);
                            }
                        } else {
                            nowPos = nowPos + 1;
                            sendBytes = SocketClientUtil.sendMsgToServer(mClientUtilWriter, nowSet[nowPos]);
                            LogUtil.i("????????????" + (nowPos + 1) + ":" + SocketClientUtil.bytesToHexString(sendBytes));
                        }
                    }
                    break;
                //??????????????????
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //?????????????????????
                        boolean isCheck = MaxUtil.checkReceiverFull(bytes);
                        if (isCheck) {
                            toast(titles[mType][nowPos] + ":" + getString(R.string.android_key121));
                            //????????????????????????
                            mHandlerWrite.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        } else {
                            toast(titles[mType][nowPos] + ":" + getString(R.string.android_key3129));
                            //??????tcp??????
                            SocketClientUtil.close(mClientUtilWriter);
                            BtnDelayUtil.refreshFinish();
                        }
                        LogUtil.i("????????????" + (nowPos + 1) + ":" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        //??????tcp??????
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

    @OnClick({R.id.btnSelect, R.id.btnSetting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnSelect:
                CircleDialogUtils.showCommentItemDialog(this, getString(R.string.android_key1809), Arrays.asList(nowItems), Gravity.CENTER, new OnLvItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (nowItems != null && nowItems.length > position) {
                            btnSelect.setText(nowItems[position]);
                            nowSet[0][2] = position;
                        }
                        return true;
                    }
                }, null);
                break;
            case R.id.btnSetting:
                nowPos = -1;
                //????????????????????????
                String content1 = etContent1.getText().toString();
                if (TextUtils.isEmpty(content1)) {
                    toast(R.string.android_key1945);
                    return;
                }
                try {
                    double result = Double.parseDouble(content1);
                    switch (mType) {
                        case 3:
//                            result = Arith.add(10000,Arith.mul(result,10000));
                            result = Arith.sub(10000, Arith.mul(result, 10000));
                            break;
                        case 4:
//                            result = Arith.sub(10000,Arith.mul(result,10000));
                            result = Arith.add(10000, Arith.mul(result, 10000));
                            break;
                    }
                    nowSet[1][2] = (int) result;
                    writeRegisterValue();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    toast(getString(R.string.android_key539));
                }
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.right_action:
                //?????????????????????
                readRegisterValue();
                break;
        }
        return true;
    }
}
