package com.growatt.shinetools.module.localbox.configtype.usconfig;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;

import com.growatt.shinetools.R;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.MaxWifiParseUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.utils.BtnDelayUtil;
import com.growatt.shinetools.utils.CommenUtils;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.Mydialog;

import java.math.BigInteger;

import butterknife.BindView;
import butterknife.OnClick;

public class USModeSetActivity extends BaseActivity {
    @BindView(R.id.tvTitle)
    AppCompatTextView tvTitle;
    @BindView(R.id.ivLeft)
    ImageView ivLeft;
    @BindView(R.id.tvRight)
    AppCompatTextView tvRight;
    @BindView(R.id.relativeLayout1)
    RelativeLayout relativeLayout1;
    @BindView(R.id.headerView)
    LinearLayout headerView;
    @BindView(R.id.et1)
    EditText et1;
    @BindView(R.id.et2)
    EditText et2;
    @BindView(R.id.et3)
    EditText et3;
    @BindView(R.id.et4)
    EditText et4;
    @BindView(R.id.et5)
    EditText et5;
    @BindView(R.id.et6)
    EditText et6;
    @BindView(R.id.et7)
    EditText et7;
    @BindView(R.id.et8)
    EditText et8;
    @BindView(R.id.llModeTypeOld2)
    LinearLayout llModeTypeOld2;
    @BindView(R.id.et1New4)
    EditText et1New4;
    @BindView(R.id.et2New4)
    EditText et2New4;
    @BindView(R.id.et3New4)
    EditText et3New4;
    @BindView(R.id.et4New4)
    EditText et4New4;
    @BindView(R.id.et5New4)
    EditText et5New4;
    @BindView(R.id.et6New4)
    EditText et6New4;
    @BindView(R.id.et7New4)
    EditText et7New4;
    @BindView(R.id.llModeTypeNew4)
    LinearLayout llModeTypeNew4;
    @BindView(R.id.tvContent1)
    TextView tvContent1;
    @BindView(R.id.btnSetting)
    Button btnSetting;

    String readStr;
    private int mType = 1;
    //????????????????????????????????????????????????????????????????????????
    private int[][] funs;
    private int[][] nowSet;
    //edittenxt??????
    private EditText[] ets;
    private EditText[] etsNew;
    //????????????????????????
    private String[] contents = new String[8];
    /**
     * model?????????-1????????????0???????????????28-29???????????????1???????????????118-121????????????
     */
    private int modeType = -1;
    private int[][] funs2;
    private String mTitle;

    @Override
    protected int getContentView() {
        return R.layout.activity_us_mode_set;
    }

    @Override
    protected void initViews() {
        initString();
        initIntent();
        initHeaderView();
        //????????????????????????
        readRegisterValue();
    }

    @Override
    protected void initData() {

    }


    private void initString() {
        readStr = getString(R.string.m369?????????);
        //????????????????????????????????????????????????????????????????????????
        funs = new int[][]{
                //????????????
                {3, 0, 124}
        };
        funs2 = new int[][]{
                //????????????
                {3, 28, 29}//Model old
                , {3, 118, 121}//Model new
        };
        //?????????????????????
        nowSet = new int[][]{
                {6, funs2[mType][1], -1}
                , {6, funs2[mType][1] + 1, -1}
                , {6, funs2[mType][1] + 2, -1}
                , {6, funs2[mType][1] + 3, -1}
        };
        ets = new EditText[]{
                et1, et2, et3, et4, et5, et6, et7, et8
        };
        etsNew = new EditText[]{
                et1New4, et2New4, et3New4, et4New4, et5New4, et6New4, et7New4
        };
    }

    private void initIntent() {
        Intent mIntent = getIntent();
        if (mIntent != null) {
            mTitle = mIntent.getStringExtra("title");
        }
    }

    private void initHeaderView() {
        if (!TextUtils.isEmpty(mTitle)) {
            tvTitle.setText(mTitle);
        }
        tvRight.setText(R.string.m370??????);
    }

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
                    if (nowSet != null) {
                        if ((mType == 0 && nowSet[0][2] == -1 && nowSet[1][2] == -1) ||
                                (mType == 1 && nowSet[0][2] == -1 && nowSet[1][2] == -1 && nowSet[2][2] == -1 && nowSet[3][2] == -1)
                        ) {
                            //??????tcp??????
                            SocketClientUtil.close(mClientUtilWriter);
                            BtnDelayUtil.refreshFinish();
                        } else {
                            for (int len = nowSet.length, i = 0; i < len; i++) {
                                if (nowSet[i][2] != -1) {
                                    BtnDelayUtil.sendMessageWrite(this);
                                    sendBytes = SocketClientUtil.sendMsgToServer(mClientUtilWriter, nowSet[i]);
                                    LogUtil.i("????????????" + (i + 1) + ":" + SocketClientUtil.bytesToHexString(sendBytes));
                                    //????????????????????????-1
                                    nowSet[i][2] = -1;
                                    break;
                                }
                            }
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
                            //??????????????????
                            byte[] bs = RegisterParseUtil.removePro17Fun6(bytes);
                            //??????int???
//                            int value0 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,1,0,1));

                            toast(getString(R.string.all_success));
                            //????????????????????????
                            mHandlerWrite.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        } else {
                            toast(getString(R.string.all_failed));
                            //??????????????????-1?????????
                            nowSet[0][2] = -1;
                            nowSet[1][2] = -1;
                            nowSet[2][2] = -1;
                            nowSet[3][2] = -1;
                            //??????tcp??????
                            SocketClientUtil.close(mClientUtilWriter);
                            BtnDelayUtil.refreshFinish();
                        }
                        LogUtil.i("????????????" + ":" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        //??????tcp??????
                        SocketClientUtil.close(mClientUtilWriter);
                        BtnDelayUtil.refreshFinish();
                    }
                    break;
                default:
                    BtnDelayUtil.dealTLXBtnWrite(this, what, mContext, btnSetting, tvRight);
                    break;
            }
        }
    };
    /**
     * ???????????????handle
     */
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
                        //?????????????????????
                        boolean isCheck = ModbusUtil.checkModbus(bytes);
                        if (isCheck) {
                            //??????????????????
                            byte[] bsTotal = RegisterParseUtil.removePro17(bytes);
                            byte[] bs = MaxWifiParseUtil.subBytesFull(bsTotal, 118, 0, 4);
                            BigInteger bigInteger = new BigInteger(1, bs);
                            if (bigInteger.longValue() == 0) {
                                //???????????????
                                modeType = 0;
                                mType = 0;
                                //??????????????????
                                bs = MaxWifiParseUtil.subBytesFull(bsTotal, 28, 0, 2);
                            } else {
                                modeType = 1;
                                mType = 1;
                            }
                            if (mType == 0) {
                                CommenUtils.showAllView(llModeTypeOld2);
                                CommenUtils.hideAllView(View.GONE, llModeTypeNew4);
                                parseOldModel(bs);
                            } else if (mType == 1) {
                                parseNewModel(bs);
                                CommenUtils.showAllView(llModeTypeNew4);
                                CommenUtils.hideAllView(View.GONE, llModeTypeOld2);
                            }
                            toast(R.string.all_success);
                        } else {
                            toast(R.string.all_failed);
                        }
                        LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //??????tcp??????
                        SocketClientUtil.close(mClientUtilRead);
                        BtnDelayUtil.refreshFinish();
                    }
                    break;
                default:
                    BtnDelayUtil.dealTLXBtn(this, what, mContext, btnSetting, tvRight);
                    break;
            }
        }
    };

    private void parseOldModel(byte[] bs) {
        //??????int???
        int value0 = MaxWifiParseUtil.obtainValueHAndL(bs);
        //??????ui
        tvContent1.setText(readStr + ":" + MaxUtil.getDeviceModel(value0));
        //??????model???
//                            mEt1.setText(Integer.toHexString((value0 & 0xF0000000) >>> 28).toUpperCase());
//                            mEt2.setText(Integer.toHexString((value0 & 0x0F000000) >>> 24).toUpperCase());
//                            mEt3.setText(Integer.toHexString((value0 & 0x00F00000) >>> 20).toUpperCase());
//                            mEt4.setText(Integer.toHexString((value0 & 0x000F0000) >>> 16).toUpperCase());
//                            mEt5.setText(Integer.toHexString((value0 & 0x0000F000) >>> 12).toUpperCase());
//                            mEt6.setText(Integer.toHexString((value0 & 0x00000F00) >>> 8).toUpperCase());
//                            mEt7.setText(Integer.toHexString((value0 & 0x000000F0) >>> 4).toUpperCase());
//                            mEt8.setText(Integer.toHexString(value0 & 0x0000000F).toUpperCase());
        et1.setText(MaxUtil.getDeviceModelSingle(value0, 8));
        et2.setText(MaxUtil.getDeviceModelSingle(value0, 7));
        et3.setText(MaxUtil.getDeviceModelSingle(value0, 6));
        et4.setText(MaxUtil.getDeviceModelSingle(value0, 5));
        et5.setText(MaxUtil.getDeviceModelSingle(value0, 4));
        et6.setText(MaxUtil.getDeviceModelSingle(value0, 3));
        et7.setText(MaxUtil.getDeviceModelSingle(value0, 2));
        et8.setText(MaxUtil.getDeviceModelSingle(value0, 1));
    }

    private void parseNewModel(byte[] bs) {
        //??????int???
        BigInteger big = new BigInteger(1, bs);
        long bigInteger = big.longValue();
        //??????ui
        tvContent1.setText(readStr + ":" + MaxUtil.getDeviceModelNew4(bigInteger));
        et1New4.setText(MaxUtil.getDeviceModelSingleNew(bigInteger, 16) + MaxUtil.getDeviceModelSingleNew(bigInteger, 15));
        et2New4.setText(MaxUtil.getDeviceModelSingleNew(bigInteger, 14) + MaxUtil.getDeviceModelSingleNew(bigInteger, 13));
        et3New4.setText(MaxUtil.getDeviceModelSingleNew(bigInteger, 12) + MaxUtil.getDeviceModelSingleNew(bigInteger, 11));
        et4New4.setText(MaxUtil.getDeviceModelSingleNew(bigInteger, 10) + MaxUtil.getDeviceModelSingleNew(bigInteger, 9));
        et5New4.setText(MaxUtil.getDeviceModelSingleNew(bigInteger, 8) + MaxUtil.getDeviceModelSingleNew(bigInteger, 7));
        et6New4.setText(MaxUtil.getDeviceModelSingleNew(bigInteger, 6) + MaxUtil.getDeviceModelSingleNew(bigInteger, 5));
        et7New4.setText(MaxUtil.getDeviceModelSingleNew(bigInteger, 4) + MaxUtil.getDeviceModelSingleNew(bigInteger, 3)
                + MaxUtil.getDeviceModelSingleNew(bigInteger, 2) + MaxUtil.getDeviceModelSingleNew(bigInteger, 1));
    }

    @OnClick(R.id.btnSetting)
    public void onViewClicked() {
        nowSet = new int[][]{
                {6, funs2[mType][1], -1}
                , {6, funs2[mType][1] + 1, -1}
                , {6, funs2[mType][1] + 2, -1}
                , {6, funs2[mType][1] + 3, -1}
        };
        switch (mType) {
            case 0:
                setOld();
                break;
            case 1:
                setNew();
                break;
        }

    }

    private void setOld() {
        //????????????????????????
        for (int i = 0, len = ets.length; i < len; i++) {
            String content = ets[i].getText().toString();
            if (TextUtils.isEmpty(content)) {
                toast(R.string.all_blank);
                return;
            }
            contents[i] = content;
        }
        try {
            StringBuilder sbHigh = new StringBuilder();
            StringBuilder sbLow = new StringBuilder();
            for (int i = 0, len = contents.length; i < len; i++) {
                if (i < 4) {
                    sbHigh.append(contents[i]);
                } else {
//                        sbHigh.append("0");
                    sbLow.append(contents[i]);
                }
            }
            int high = Integer.parseInt(sbHigh.toString(), 16);
            int low = Integer.parseInt(sbLow.toString(), 16);
            nowSet[0][2] = high;
            nowSet[1][2] = low;
            writeRegisterValue();
        } catch (Exception e) {
            e.printStackTrace();
            toast(getString(R.string.m363????????????));
            nowSet[0][2] = -1;
            nowSet[1][2] = -1;
        }
    }

    private void setNew() {
        //????????????????????????
        for (int i = 0, len = etsNew.length; i < len; i++) {
            String content = etsNew[i].getText().toString();
            if (TextUtils.isEmpty(content)) {
                toast(R.string.all_blank);
                return;
            }
            contents[i] = content;
        }
        try {
            nowSet[0][2] = Integer.parseInt(contents[0] + contents[1], 16);
            nowSet[1][2] = Integer.parseInt(contents[2] + contents[3], 16);
            nowSet[2][2] = Integer.parseInt(contents[4] + contents[5], 16);
            nowSet[3][2] = Integer.parseInt(contents[6], 16);
            writeRegisterValue();
        } catch (Exception e) {
            e.printStackTrace();
            toast(getString(R.string.m363????????????));
            nowSet[0][2] = -1;
            nowSet[1][2] = -1;
            nowSet[2][2] = -1;
            nowSet[3][2] = -1;
        }
    }



    @OnClick({R.id.ivLeft, R.id.tvRight})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivLeft:
                finish();
                break;
            case R.id.tvRight:
                //?????????????????????
                readRegisterValue();
                break;
        }
    }
}
