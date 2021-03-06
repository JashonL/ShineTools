package com.growatt.shinetools.module.localbox.ustool;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.growatt.shinetools.R;
import com.growatt.shinetools.ShineToosApplication;
import com.growatt.shinetools.base.DemoBase;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.MaxWifiParseUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.utils.BtnDelayUtil;
import com.growatt.shinetools.utils.CircleDialogUtils;
import com.growatt.shinetools.utils.CommenUtils;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.Mydialog;
import com.mylhyl.circledialog.BaseCircleDialog;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.view.listener.OnLvItemClickListener;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.growatt.shinetools.constant.GlobalConstant.END_USER;
import static com.growatt.shinetools.constant.GlobalConstant.KEFU_USER;

public class USFastSetActivity extends DemoBase implements CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    @BindView(R.id.tvRight)
    TextView mTvRight;
    @BindView(R.id.tvCode)
    TextView mTvCode;
    @BindView(R.id.tvVol)
    TextView mTvVol;
    @BindView(R.id.tvTime)
    TextView mTvTime;
    @BindView(R.id.swTurn)
    Switch mSwTurn;
    @BindView(R.id.tvCodeNote)
    TextView mTvCodeNote;
    @BindView(R.id.tvTimeNote)
    TextView mTvTimeNote;
    @BindView(R.id.tvConfigWifi)
    TextView mTvConfigWifi;
    @BindView(R.id.btnOK)
    Button mBtnOK;
    @BindView(R.id.tvDyNamometer1)
    TextView tvDyNamometer1;
    @BindView(R.id.tvEmsInput)
    TextView tvEmsInput;
    @BindView(R.id.tvDynamometer)
    TextView tvDyNam;
    @BindView(R.id.vCode)
    View vCode;
    @BindView(R.id.viewV)
    View viewV;
    @BindView(R.id.ivCodeNext)
    ImageView ivCodeNext;


    //????????????????????????????????????????????????????????????????????????
    private int[][] funs;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String[][] modelToal;
    private List<String> models;
    private List<String> dyNamometer;
    private int modelPos = -1;//??????model??????
    private int dyNamometerPos = -1;//?????????????????????
    private List<Integer> setIndexList = new ArrayList<>();//???????????????????????????


    private int[][] nowSet = {
            {0x10, 45, 50}, {0x10, 118, 121}, {6, 533, -1}
    };
    private int[][] registerValues = {
            {-1, -1, -1, -1, -1, -1}, {-1, -1, -1, -1}
    };
    //    private boolean isFirst = true;
    private String[] noteTitles;
//    private boolean isSetModel = false;

    //????????????item
    private String[][] items;

    private int user_type = KEFU_USER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usfast_set);
        ButterKnife.bind(this);
        user_type = ShineToosApplication.getContext().getUser_type();

        if (user_type == END_USER) {

            CommenUtils.hideAllView(View.GONE,vCode,mTvCodeNote,viewV,mTvCode,ivCodeNext);
        }

        initString();
//        isSetModel = false;
        readRegisterValue();
    }

    private void initString() {
        noteTitles = new String[]{
                mTvTimeNote.getText().toString(),
                mTvCodeNote.getText().toString(),
                tvDyNam.getText().toString()

        };
        mTvTitle.setText(R.string.????????????);
        mTvRight.setText(R.string.m370??????);
   /*     mSwTurn.setOnCheckedChangeListener(this);
        mSwTurn.setChecked(true);*/
        mTvTime.setText(sdf.format(new Date()));
        modelToal = new String[][]{
                {"S25", "IEEE1547-208", "5", "208V", "25"}
                , {"S25", "IEEE1547-240", "1", "240V", "25"}
                , {"S31", "RULE 21-208", "5", "208V", "31"}
                , {"S31", "RULE 21-240", "1", "240V", "31"}
                , {"S32", "HECO-208", "5", "208V", "32"}
                , {"S32", "HECO-240", "1", "240V", "32"}
                , {"S35", "PRC-East-208", "5", "208V", "35"}
                , {"S35", "PRC-East-240", "1", "240V", "35"}
                , {"S36", "PRC-West-208", "5", "208V", "36"}
                , {"S36", "PRC-West-240", "1", "240V", "36"}
                , {"S37", "PRC-Quebec-208", "5", "208V", "37"}
                , {"S37", "PRC-Quebec-240", "1", "240V", "37"}
        };
        models = new ArrayList<>();
        for (int i = 0; i < modelToal.length; i++) {
            models.add(modelToal[i][1]);
        }
        //????????????????????????????????????????????????????????????????????????
        funs = new int[][]{
                {3, 0, 124}//????????????
                , {3, 20000, 20000}//????????????
                , {3, 533, 533}//???????????????
                , {3, 3144, 3144}//EMS:?????????
        };

        items = new String[][]{{getString(R.string.m657????????????????????????), getString(R.string.m??????)},
                {getString(R.string.m209????????????),getString(R.string.m208????????????)}

        };

        dyNamometer = Arrays.asList(items[0]);

    }


    private BaseCircleDialog explainDialog;


    @OnClick({R.id.ivLeft, R.id.tvRight, R.id.tvCode, R.id.tvTime, R.id.btnOK, R.id.tvConfigWifi, R.id.tvDyNamometer1,R.id.tvEms})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivLeft:
                finish();
                break;
            case R.id.tvConfigWifi:
                jumpTo(USWiFiConfigActivity.class, false);
                break;
            case R.id.tvRight:
//                isSetModel = false;
                readRegisterValue();
                break;
            case R.id.tvCode:
                new CircleDialog.Builder()
                        .setTitle(getString(R.string.android_key499))
                        .setWidth(0.7f)
                        .setGravity(Gravity.CENTER)
                        .setMaxHeight(0.5f)
                        .setItems(models, new OnLvItemClickListener() {
                            @Override
                            public boolean onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                                modelPos = pos;
                                mTvCode.setText(models.get(pos));
                                //????????????
                                mTvVol.setText(modelToal[pos][3]);
                                return true;
                            }
                        })
                        .setNegative(getString(R.string.all_no), null)
                        .show(getSupportFragmentManager());
                break;
            case R.id.tvTime:
//                DateUtils.showTotalTime(mContext, mTvTime);
                break;

            case R.id.tvDyNamometer1://???????????????
                new CircleDialog.Builder()
                        .setTitle(getString(R.string.android_key499))
                        .setWidth(0.7f)
                        .setGravity(Gravity.CENTER)
                        .setMaxHeight(0.5f)
                        .setItems(dyNamometer, new OnLvItemClickListener() {
                            @Override
                            public boolean onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                                dyNamometerPos = pos;
                                tvDyNamometer1.setText(dyNamometer.get(pos));
                                return true;
                            }
                        })
                        .setNegative(getString(R.string.all_no), null)
                        .show(getSupportFragmentManager());
                break;

            case R.id.btnOK:
                boolean isTime = TextUtils.isEmpty(String.valueOf(mTvTime.getText()));
                if (modelPos == -1 && isTime && dyNamometerPos == -1) {
                    toast(R.string.all_blank);
                    return;
                }
          /*      //???????????????????????????model
                if (modelPos != -1 && registerValues[1][1] == -1){
                    //?????????model
//                    isSetModel = true;
                    readRegisterValue();
                    return;
                }*/

                if (!isTime) {
                    //??????????????????
                    try {
                        Date date = sdf.parse(mTvTime.getText().toString());
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH) + 1;
                        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
                        int minute = calendar.get(Calendar.MINUTE);
                        int nowSecond = calendar.get(Calendar.SECOND);
                        registerValues[0][0] = year > 2000 ? year - 2000 : year;
                        registerValues[0][1] = month;
                        registerValues[0][2] = dayOfMonth;
                        registerValues[0][3] = hourOfDay;
                        registerValues[0][4] = minute;
                        registerValues[0][5] = nowSecond;
                        setIndexList.add(0);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if (modelPos != -1) {
                    setIndexList.add(1);
                    setModelRegistValue();
                }
                if (dyNamometerPos != -1) {
                    nowSet[2][2] = dyNamometerPos;
                    setIndexList.add(2);
                }

                if (setIndexList.size()>0)
                 setCount=0;
                connectServerWrite();
                break;

            case R.id.tvEms:
                String title = "EMS";
                String content=getString(R.string.ems_explain);
                explainDialog = CircleDialogUtils.showExplainDialog(USFastSetActivity.this, title,content ,
                        new CircleDialogUtils.OndialogClickListeners() {
                            @Override
                            public void buttonOk() {
                                explainDialog.dialogDismiss();
                            }
                            @Override
                            public void buttonCancel() {
                                explainDialog.dialogDismiss();
                            }
                        });
                break;

        }
    }

    private void setModelRegistValue() {
        String[] selectModels = modelToal[modelPos];
        int sModel = Integer.parseInt(selectModels[4], 16) << 8 | (registerValues[1][0] & 0x00FF);
        int uModel = Integer.parseInt(selectModels[2]) | ((registerValues[1][2] & 0b1111111111111000));
        registerValues[1][0] = sModel;
//                        registerValues[1][1] = 0;
        registerValues[1][2] = uModel;
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
    private int setCount = 0;//0??????????????????1?????????model;2?????????????????????
    Handler mHandlerW = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //????????????
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessageWrite(this);
                    int  pos = setIndexList.get(setCount);
                    if (pos == 2) {
                        sendBytes = sendMsg(mClientUtilW, nowSet[pos]);
                        LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(sendBytes));
                    } else {
                        if (pos==1){
                            setModelRegistValue();
                        }
                        sendBytes = SocketClientUtil.sendMsgToServer10(mClientUtilW, nowSet[pos], registerValues[pos]);
                        LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(sendBytes));
                    }

                    break;
                //??????????????????
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        int  pos1 = setIndexList.get(setCount);
                        boolean isCheck;
                        if (pos1==2){
                            //?????????????????????
                             isCheck = MaxUtil.checkReceiverFull(bytes);
                        }else {
                             isCheck = MaxUtil.checkReceiverFull10(bytes);
                        }
                        if (isCheck) {
                            toast(noteTitles[pos1] + ":" + getString(R.string.all_success));
                        } else {
                            toast(noteTitles[pos1] + ":" + getString(R.string.all_failed));
                        }
                        if (setCount < setIndexList.size()-1) {
                            setCount++;
                            this.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        } else {
                            setCount = 0;
                            //????????????
                            SocketClientUtil.close(mClientUtilW);
                            Mydialog.Dismiss();
                        }
                        LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        //????????????
                        SocketClientUtil.close(mClientUtilW);
                        Mydialog.Dismiss();
                    }
                    break;
                default:
                    BtnDelayUtil.dealTLXBtnWrite(this, what, mContext, mTvRight);
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
            connectServerWrite();
            return null;
        }
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


    private boolean isReceiveSucc = false;
    private int count = 0;//????????????????????????
    private String uuid;
    StringBuilder sb = new StringBuilder();
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
                    if (count < funs.length) {
                        byte[] sendBytesR = SocketClientUtil.sendMsgToServer(mClientUtil, funs[count]);
                        LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(sendBytesR));
                    }
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
                    sb.append("Server:<br>")
                            .append(recMsg)
                            .append("<br><br>");
//                    Log.i("receive" ,recMsg);
                    break;

                //??????????????????
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //?????????????????????
                        boolean isCheck = ModbusUtil.checkModbus(bytes);
                        if (isCheck) {
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
                    } catch (Exception e) {
                        e.printStackTrace();
                        //??????tcp??????
                        count = 0;
                        SocketClientUtil.close(mClientUtil);
                        BtnDelayUtil.refreshFinish();
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
                    readRegisterValue();
                    break;
                default:
                    BtnDelayUtil.dealTLXBtn(this, what, mContext, mTvRight);
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
        switch (count) {
            case 0://?????????
                LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(bytes));
                //??????model
                byte[] valueBs = MaxWifiParseUtil.subBytesFull(bs, 118, 0, 4);
                registerValues[1][0] = MaxWifiParseUtil.obtainValueOne(bs, 118);
                registerValues[1][1] = MaxWifiParseUtil.obtainValueOne(bs, 119);
                registerValues[1][2] = MaxWifiParseUtil.obtainValueOne(bs, 120);
                registerValues[1][3] = MaxWifiParseUtil.obtainValueOne(bs, 121);
          /*      if (isFirst) {
                    isFirst = false;
                    //??????tcp??????
                    SocketClientUtil.close(mClientUtil);
                    BtnDelayUtil.refreshFinish();
                    return;
                }
                if (isSetModel) {
                    isSetModel = false;
                    SocketClientUtil.close(mClientUtil);
                    BtnDelayUtil.refreshFinish();
                    onViewClicked(mBtnOK);
                    return;
                }*/
                BigInteger big = new BigInteger(1, valueBs);
                long bigInteger = big.longValue();
                String modelS = MaxUtil.getDeviceModelSingleNew(bigInteger, 16) + MaxUtil.getDeviceModelSingleNew(bigInteger, 15);
                String readModel = String.format("S%s", modelS);
                //??????U???
                //??????120?????????  U ?????? 0-2bit
                int readVol = MaxWifiParseUtil.obtainValueOne(bs, 120) & 0x0007;
                //??????
                boolean isFlag = false;
                for (int i = 0; i < modelToal.length; i++) {
                    String[] model = modelToal[i];
                    if (model[0].equals(readModel) && model[2].equals(String.valueOf(readVol))) {
                        isFlag = true;
                        modelPos = i;
                        mTvCode.setText(model[1]);
                        mTvVol.setText(model[3]);
                        break;
                    }
                }
                if (!isFlag) {
                    modelPos = -1;
                    mTvCode.setText("");
                }

                //????????????
                //??????int???
                int year = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 45, 0, 1));
                int month = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 46, 0, 1));
                int day = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 47, 0, 1));
                int hour = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 48, 0, 1));
                int min = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 49, 0, 1));
                int seconds = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 50, 0, 1));
                //??????ui
                StringBuilder sb = new StringBuilder()
                        .append(year).append("-");
                if (month < 10) {
                    sb.append("0");
                }
                sb.append(month).append("-");
                if (day < 10) {
                    sb.append("0");
                }
                sb.append(day).append(" ");
                if (hour < 10) {
                    sb.append("0");
                }
                sb.append(hour).append(":");
                if (min < 10) {
                    sb.append("0");
                }
                sb.append(min).append(":");
                if (seconds < 10) {
                    sb.append("0");
                }
                sb.append(seconds);
                mTvTime.setText(sb.toString());
                break;

            case 1://????????????
                //??????int???
                int value0 = MaxWifiParseUtil.obtainValueOne(bs);
                try {
                    if (value0 == 4) {
                        mTvConfigWifi.setText(R.string.already_equipped);
                    } else {
                        mTvConfigWifi.setText(R.string.m?????????);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case 2://???????????????
                //??????int???
                int value2 = MaxWifiParseUtil.obtainValueOne(bs);
                //??????ui
                tvDyNamometer1.setText(getReadResult(value2, 2));
                break;
            case 3://EMS
                //??????int???
                int value3 = MaxWifiParseUtil.obtainValueOne(bs);
                //??????ui
                tvEmsInput.setText(getReadResult(value3, 3));
                break;

        }
    }


    /**
     * ????????????????????????????????????????????????
     *
     * @param value
     * @return
     */
    public String getReadResult(int value, int count) {
        String result = String.valueOf(value);
        switch (count) {
            case 2:
                switch (value) {
                    case 0:
                        result = items[0][0];
                        break;
                    case 1:
                        result = items[0][1];
                        break;
                }
                break;


            case 3:
                switch (value) {
                    case 0:
                        result = items[1][0];
                        break;
                    case 1:
                        result = items[1][1];
                        break;
                    case 2:
                        result = items[1][2];
                        break;
                    case 3:
                        result = items[1][3];
                        break;
                }

                break;
        }

        return result;
    }


    /**
     * ????????????
     */
    private void refreshFinish() {
        Mydialog.Dismiss();
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton == mSwTurn) {
            if (b) {
                mTvTime.setText(sdf.format(new Date()));
            } else {
                mTvTime.setText("");
            }
        }
    }
}
