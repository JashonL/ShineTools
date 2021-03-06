package com.growatt.shinetools.module.localbox.ustool;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.growatt.shinetools.R;
import com.growatt.shinetools.adapter.UsSettingAdapter;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.bean.DryStartTimeBean;
import com.growatt.shinetools.bean.USDebugSettingBean;
import com.growatt.shinetools.bean.UsSettingConstant;
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
import com.growatt.shinetools.widget.LinearDivider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

import static com.growatt.shinetools.modbusbox.SocketClientUtil.SOCKET_RECEIVE_BYTES;

public class DryFunctionActivity extends BaseActivity implements Toolbar.OnMenuItemClickListener,
        UsSettingAdapter.OnChildCheckLiseners, BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.header_title)
    LinearLayout headerTitle;
    @BindView(R.id.rv_dry)
    RecyclerView rvDry;

    private MenuItem item;
    private UsSettingAdapter usParamsetAdapter;
    //?????????
    private String[] titles;
    private int[] itemTypes;
    //????????????
    private int[] funs;

    //?????????????????????
    private String[] bCtrlMode;
    //?????????????????????
    private String[] bWaterHeaterOnType;
    //????????? ????????????
    private String[] bGeneratorOntype;
    //?????????????????????
    private String[] bRunStatus;

    private List<DryStartTimeBean> dryTimeList = new ArrayList<>();

    private int[][] nowSet;

    private int[] setItem;

    @Override
    protected int getContentView() {
        return R.layout.activity_dry_function;
    }

    @Override
    protected void initViews() {
        initToobar(toolbar);
        tvTitle.setText(R.string.android_key750);
        toolbar.inflateMenu(R.menu.comment_right_menu);
        item = toolbar.getMenu().findItem(R.id.right_action);
        item.setTitle(R.string.m370??????);
        toolbar.setOnMenuItemClickListener(this);


        rvDry.setLayoutManager(new LinearLayoutManager(this));
        usParamsetAdapter = new UsSettingAdapter(new ArrayList<>(), this);
        int div = (int) getResources().getDimension(R.dimen.dp_1);
        LinearDivider linearDivider = new LinearDivider(this, LinearLayoutManager.VERTICAL, div, ContextCompat.getColor(this, R.color.white));
        rvDry.addItemDecoration(linearDivider);
        rvDry.setAdapter(usParamsetAdapter);
        usParamsetAdapter.setOnItemClickListener(this);


    }

    @Override
    protected void initData() {
        bCtrlMode = new String[]{
                getString(R.string.android_key1686),//??????
                getString(R.string.android_key2157),//?????????
                getString(R.string.dynamo),//?????????
        };


        bWaterHeaterOnType = new String[]{
                getString(R.string.disable),//?????????
                getString(R.string.out_power),//???????????????
                getString(R.string.time_period),//????????????
                getString(R.string.mixed_mode)//????????????

        };


        bGeneratorOntype = new String[]{
                getString(R.string.disable),//?????????
                getString(R.string.android_key2763),//??????
                getString(R.string.m????????????),//???????????????
        };


        bRunStatus = new String[]{
                getString(R.string.android_key839),//?????????
                getString(R.string.water_heateing),//??????????????????
                getString(R.string.generator_working),//??????????????????
                getString(R.string.function_close)//????????????
        };


        titles = new String[]{
                getString(R.string.dry_workmode),//?????????????????????
                getString(R.string.water_heater_method),//?????????????????????
                getString(R.string.starting_power),//????????????
                getString(R.string.exit_power), //????????????
                getString(R.string.start_extension_time),//??????????????????
                getString(R.string.minimum_running_time), //??????????????????
                getString(R.string.dry_contact_setting),//????????????????????????
                getString(R.string.diesel_start_mode), //?????????????????????
                getString(R.string.start_soc),//??????SOC
                getString(R.string.close_soc), //??????SOC
                getString(R.string.start_voltage),//????????????
                getString(R.string.close_voltage),//????????????
                getString(R.string.dry_run_status),//?????????????????????
                getString(R.string.init_dry_setting) //??????????????????????????????

        };

        //item???????????????
        itemTypes = new int[]{
                UsSettingConstant.SETTING_TYPE_SELECT,
                UsSettingConstant.SETTING_TYPE_SELECT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_NEXT,
                UsSettingConstant.SETTING_TYPE_SELECT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_ONLYREAD,
                UsSettingConstant.SETTING_TYPE_NEXT,

        };


        funs = new int[]{3, 700, 725};

        nowSet = new int[][]{
                {6, 700, -1},//?????????????????????
                {6, 701, -1},//?????????????????????
                {6, 702, -1},//????????????
                {6, 703, -1},//??????????????????
                {6, 704, -1},//??????????????????
                {6, 705, -1},//????????????
                {6, 706, -1},//???????????????1
                {6, 707, -1},//???????????????1
                {6, 708, -1},//???????????????2
                {6, 709, -1},//???????????????2
                {6, 710, -1},//???????????????3
                {6, 711, -1},//???????????????3
                {6, 720, -1},//?????????????????????
                {6, 721, -1},//??????SOC
                {6, 722, -1},//??????SOC
                {6, 723, -1},//????????????
                {6, 724, -1},//????????????
                {6, 725, -1},//?????????????????????
                {6, 730, -1},//?????????????????????

        };


        List<USDebugSettingBean> newlist = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            USDebugSettingBean bean = new USDebugSettingBean();
            bean.setTitle(titles[i]);
            bean.setItemType(itemTypes[i]);
            newlist.add(bean);
        }
        usParamsetAdapter.replaceData(newlist);


        refresh();

    }


    /**
     * ????????????
     */
    private void refresh() {
        connectSendMsg();
    }


    /**
     * ?????????????????????
     */
    private void connectSendMsg() {
        Mydialog.Show(this);
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


    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            String text = "";
            int what = msg.what;
            switch (what) {
                case SocketClientUtil.SOCKET_CLOSE:
                    break;
                case SocketClientUtil.SOCKET_OPEN:
                    break;
                case SocketClientUtil.SOCKET_SEND_MSG:
                    BtnDelayUtil.sendMessage(this);
                    String sendMsg = (String) msg.obj;
                    LogUtil.i("????????????:" + sendMsg);
                    break;
                case SocketClientUtil.SOCKET_RECEIVE_MSG:
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
                            parseMax(bytes);
                        }
                        LogUtil.i("????????????:" + SocketClientUtil.bytesToHexString(bytes));
                        //????????????
                        SocketClientUtil.close(mClientUtil);
                        refreshFinish();
                    } catch (Exception e) {
                        e.printStackTrace();
                        //????????????
                        SocketClientUtil.close(mClientUtil);
                        Mydialog.Dismiss();
                    }
                    break;
                case SocketClientUtil.SOCKET_CONNECT:
                    break;
                case SocketClientUtil.SOCKET_SEND:
                    sendMsg(mClientUtil, funs);
                    break;
                case 100://??????????????????
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


    /**
     * ??????????????????mtype????????????
     *
     * @param bytes
     */
    private void parseMax(byte[] bytes) {
        //??????????????????
        byte[] bs = RegisterParseUtil.removePro17(bytes);
        //??????
        //?????????????????????
        int value = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 0, 0, 1));
        USDebugSettingBean bean = usParamsetAdapter.getData().get(0);
        bean.setValue(String.valueOf(value));
        if (value > bCtrlMode.length) {
            bean.setValueStr(String.valueOf(value));
        } else {
            bean.setValueStr(bCtrlMode[value]);
        }


        //?????????????????????
        value = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 1, 0, 1));
        USDebugSettingBean bean1 = usParamsetAdapter.getData().get(1);
        bean1.setValue(String.valueOf(value));
        if (value > bWaterHeaterOnType.length) {
            bean1.setValueStr(String.valueOf(value));
        } else {
            bean1.setValueStr(bWaterHeaterOnType[value]);
        }


        //????????????
        value = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 2, 0, 1));
        USDebugSettingBean bean2 = usParamsetAdapter.getData().get(2);
        bean2.setValue(String.valueOf(value));
        String s = value + "W";
        bean2.setValueStr(s);


        //??????????????????
        value = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 3, 0, 1));
        USDebugSettingBean bean3 = usParamsetAdapter.getData().get(3);
        bean3.setValue(String.valueOf(value));
        String s3 = value + "Min";
        bean3.setValueStr(s3);

        //??????????????????
        value = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 4, 0, 1));
        USDebugSettingBean bean4 = usParamsetAdapter.getData().get(4);
        bean4.setValue(String.valueOf(value));
        String s4 = value + "Min";
        bean4.setValueStr(s4);

        //????????????
        value = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 5, 0, 1));

        USDebugSettingBean bean5 = usParamsetAdapter.getData().get(5);
        bean5.setValue(String.valueOf(value));
        String s5 = value + "Min";
        bean4.setValueStr(s5);


        DryStartTimeBean dryStartTimeBean = new DryStartTimeBean();

        //????????????1
        value = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 6, 0, 1));
        dryStartTimeBean.setStartHour(value >> 8);
        dryStartTimeBean.setStartMin(value & 0b11111111);
        //????????? ??????
        String s_hour1 = CommenUtils.getDoubleNum(value >> 8);
        //????????? ??????
        String s_min1 = CommenUtils.getDoubleNum(value & 0b11111111);


        //????????????1
        value = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 7, 0, 1));
        dryStartTimeBean.setEndHour(value >> 8);
        dryStartTimeBean.setEndMin(value & 0b11111111);
        //????????? ??????
        String e_hour1 = CommenUtils.getDoubleNum(value >> 8);
        //????????? ??????
        String e_min1 = CommenUtils.getDoubleNum(value & 0b11111111);


        String startTime = s_hour1+":"+s_min1;
        String endTime = e_hour1+":"+e_min1;
        String time=startTime+"-"+endTime;

        dryStartTimeBean.setStartTime(startTime);
        dryStartTimeBean.setEndTime(endTime);
        dryStartTimeBean.setTime(time);

        dryTimeList.add(dryStartTimeBean);


        DryStartTimeBean dryStartTimeBean2 = new DryStartTimeBean();
        //????????????2
        value = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 8, 0, 1));
        dryStartTimeBean2.setStartHour(value >> 8);
        dryStartTimeBean2.setStartMin(value & 0b11111111);
        //????????? ??????
        String s_hour2 = CommenUtils.getDoubleNum(value >> 8);
        //????????? ??????
        String s_min2 = CommenUtils.getDoubleNum(value & 0b11111111);

        //????????????2
        value = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 9, 0, 1));
        dryStartTimeBean2.setEndHour(value >> 8);
        dryStartTimeBean2.setEndMin(value & 0b11111111);
        //????????? ??????
        String e_hour2 = CommenUtils.getDoubleNum(value >> 8);
        //????????? ??????
        String e_min2 = CommenUtils.getDoubleNum(value & 0b11111111);


        String startTime2 = s_hour2+":"+s_min2;
        String endTime2 = e_hour2+":"+e_min2;
        String time2=startTime2+"-"+endTime2;

        dryStartTimeBean2.setStartTime(startTime2);
        dryStartTimeBean2.setEndTime(endTime2);
        dryStartTimeBean2.setTime(time2);
        dryTimeList.add(dryStartTimeBean2);


        DryStartTimeBean dryStartTimeBean3 = new DryStartTimeBean();
        //????????????3
        value = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 10, 0, 1));
        dryStartTimeBean3.setStartHour(value >> 8);
        dryStartTimeBean3.setStartMin(value & 0b11111111);
        //????????? ??????
        String s_hour3 = CommenUtils.getDoubleNum(value >> 8);
        //????????? ??????
        String s_min3 = CommenUtils.getDoubleNum(value & 0b11111111);

        //????????????3
        value = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 11, 0, 1));
        dryStartTimeBean3.setEndHour(value >> 8);
        dryStartTimeBean3.setEndMin(value & 0b11111111);
        //????????? ??????
        String e_hour3 = CommenUtils.getDoubleNum(value >> 8);
        //????????? ??????
        String e_min3 = CommenUtils.getDoubleNum(value & 0b11111111);

        String startTime3 = s_hour3+":"+s_min3;
        String endTime3 = e_hour3+":"+e_min3;
        String time3=startTime3+"-"+endTime3;

        dryStartTimeBean3.setStartTime(startTime3);
        dryStartTimeBean3.setEndTime(endTime3);
        dryStartTimeBean3.setTime(time3);

        dryTimeList.add(dryStartTimeBean3);


        //?????????????????????
        value = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 20, 0, 1));
        USDebugSettingBean bean12 = usParamsetAdapter.getData().get(12);
        bean12.setValue(String.valueOf(value));
        if (value > bGeneratorOntype.length) {
            bean12.setValueStr(String.valueOf(value));
        } else {
            bean12.setValueStr(bGeneratorOntype[value]);
        }

        //??????SOC
        value = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 21, 0, 1));
        USDebugSettingBean bean13 = usParamsetAdapter.getData().get(13);
        bean13.setValue(String.valueOf(value));
        String s13 = value + "%";
        bean13.setValueStr(s13);


        //??????SOC
        value = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 22, 0, 1));
        USDebugSettingBean bean14 = usParamsetAdapter.getData().get(14);
        bean14.setValue(String.valueOf(value));
        String s14 = value + "%";
        bean14.setValueStr(s14);


        //????????????
        value = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 23, 0, 1));
        USDebugSettingBean bean15 = usParamsetAdapter.getData().get(15);
        bean15.setValue(String.valueOf(value));
        String s15 = value + "V";
        bean15.setValueStr(s15);


        //????????????
        value = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 24, 0, 1));
        USDebugSettingBean bean16 = usParamsetAdapter.getData().get(16);
        bean16.setValue(String.valueOf(value));
        String s16 = value + "V";
        bean15.setValueStr(s16);


        //?????????????????????
        value = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 25, 0, 1));
        USDebugSettingBean bean17 = usParamsetAdapter.getData().get(17);
        bean17.setValue(String.valueOf(value));
        if (value > bRunStatus.length) {
            bean17.setValueStr(String.valueOf(value));
        } else {
            bean17.setValueStr(bRunStatus[value]);
        }

        usParamsetAdapter.notifyDataSetChanged();
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.right_action:
                //?????????????????????
                refresh();
                break;
        }
        return true;
    }

    @Override
    public void oncheck(boolean check, int position) {

    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        USDebugSettingBean bean = usParamsetAdapter.getData().get(position);
        String title = bean.getTitle();
        int type = 0;

        switch (position) {
            case 0:
            case 1:
            case 7:
                type = 0;
                break;

            case 6:
                type = 1;
                break;

            case 12://?????????????????????

                break;

            case 13://??????????????????????????????
                type=3;
                break;

            default:
                type = 2;
                break;
        }

        if (type == 0) {
            List<String> list;
            if (position == 0) {
                list = Arrays.asList(bCtrlMode);
            } else {
                list = Arrays.asList(bWaterHeaterOnType);
            }

            List<String> finalList = list;
            CircleDialogUtils.showCommentItemDialog(this, getString(R.string.android_key1809),
                    list, Gravity.CENTER, (parent, view1, pos, id) -> {
                        if (finalList.size() > pos) {
                            String text = finalList.get(pos);
                            usParamsetAdapter.getData().get(position).setValueStr(text);
                            usParamsetAdapter.getData().get(position).setValue(String.valueOf(pos));
                            usParamsetAdapter.notifyDataSetChanged();
                            //?????????
                            setItem = nowSet[position];
                            setItem[2] = pos;
                            writeRegisterValue();
                        }
                        return true;
                    }, null);
        }


        if (type==1){
            String jsonArray = new Gson().toJson(dryTimeList);
            Intent intent=new Intent(DryFunctionActivity.this,DryPeriodSettingActivity.class);
            intent.putExtra("timelist",jsonArray);
            startActivity(intent);

        }



        if (type == 2) {
            String hint = "";
            String unit = "%";
            switch (position) {
                case 2://????????????
                    hint = "XE/XH:5~600  7-10k-X: 5~10000";
                    unit = "W";
                    break;

                case 3://????????????
                    hint = "4000~6000";
                    unit = "W";
                    break;

                case 4://??????????????????
                    hint = "0~99";
                    unit = "Min";
                    break;

                case 5://??????????????????
                    hint = "3~500";
                    unit = "Min";
                    break;

                case 8://??????SOC
                    hint = "20~89";
                    unit = "%";
                    break;
                case 9://??????SOC
                    hint = "30~99";
                    unit = "%";
                    break;
                case 10://????????????
                    hint = "11.5~13.5";
                    unit = "V";
                    break;
                case 11://????????????
                    hint = "13.0~15.0";
                    unit = "V";
                    break;
            }

            CircleDialogUtils.showInputValueDialog(DryFunctionActivity.this, title,
                    hint, unit, value -> {
//                        switch (position) {
//                            case 2://????????????
//
//                                break;
//                            case 3://????????????
//
//
//
//                                break;
//
//                            case 4://??????????????????
//                                break;
//
//                            case 5://??????????????????
//                                break;
//
//                            case 8://??????SOC
//                                break;
//                            case 9://??????SOC
//                                break;
//                            case 10://????????????
//                                break;
//                            case 11://????????????
//                                break;
//                        }

                        double result = Double.parseDouble(value);
                        String pValue = value + "";
                        usParamsetAdapter.getData().get(position).setValueStr(pValue);
                        usParamsetAdapter.getData().get(position).setValue(String.valueOf(result));
                        usParamsetAdapter.notifyDataSetChanged();

                        //??????
                        setItem = nowSet[position];
                        setItem[2] = (int) result;
                        writeRegisterValue();

                    });
        }


        if (type==3){
            CircleDialogUtils.showCommentDialog(this, getString(R.string.android_key2263),
                    getString(R.string.confirm_init_dry_setting), getString(R.string.android_key1935), getString(R.string.android_key2152), Gravity.CENTER, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //??????
                            setItem = nowSet[position];
                            setItem[2] = 0xA5;
                            writeRegisterValue();
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    });
        }

    }


    //????????????:??????????????????
    private SocketClientUtil mClientUtilWriter;

    //?????????????????????
    private void writeRegisterValue() {
        Mydialog.Show(mContext);
        mClientUtilWriter = SocketClientUtil.connectServer(mHandlerWrite);
    }


    private byte[] sendBytes;

    Handler mHandlerWrite = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //????????????
                case SocketClientUtil.SOCKET_SEND:
                    if (setItem != null && setItem[2] != -1) {
                        BtnDelayUtil.sendMessageWrite(this);
                        sendBytes = sendMsg(mClientUtilWriter, setItem);
                        LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(sendBytes));
                    }
                    break;
                //??????????????????
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;

                        LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(bytes));
                        //?????????????????????
                        boolean isCheck = MaxUtil.checkReceiverFull(bytes);
                        if (isCheck) {
                            toast(R.string.all_success);
                        } else {
                            toast(R.string.all_failed);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //??????tcp??????
                        SocketClientUtil.close(mClientUtilWriter);
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
