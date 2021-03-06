package com.growatt.shinetools.module.localbox.configtype.usconfig;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.growatt.shinetools.R;
import com.growatt.shinetools.adapter.USchargePeriodAdapter;
import com.growatt.shinetools.adapter.UsSettingAdapter;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.constant.GlobalConstant;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.MaxWifiParseUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.module.localbox.ustool.bean.USChargePriorityBean;
import com.growatt.shinetools.module.localbox.ustool.PeriodSettingActivity;
import com.growatt.shinetools.module.localbox.ustool.UsChargeManagerSelectDateActivity;
import com.growatt.shinetools.module.localbox.ustool.bean.UsChargeConfigMsg;
import com.growatt.shinetools.utils.ActivityUtils;
import com.growatt.shinetools.utils.BtnDelayUtil;
import com.growatt.shinetools.utils.CircleDialogUtils;
import com.growatt.shinetools.utils.Log;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.Mydialog;
import com.growatt.shinetools.widget.LinearDivider;
import com.mylhyl.circledialog.BaseCircleDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class USChargeTimeActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener,
        UsSettingAdapter.OnChildCheckLiseners,USchargePeriodAdapter.OnChildCheckLiseners, Toolbar.OnMenuItemClickListener, CompoundButton.OnCheckedChangeListener {


    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.header_title)
    LinearLayout headerTitle;


    @BindView(R.id.rv_priority)
    RecyclerView rvPriority;

    private TextView tvPeriodExplain;


    private Switch swSwitch;
    private TextView tvAdd;
    private TextView tvAddText;
    private ConstraintLayout clDate;
    private AppCompatTextView tvValue;

    private USchargePeriodAdapter mAdapter;
    private MenuItem item;

    //????????????????????????????????????????????????????????????????????????
    private int[][] funs;
    private String[][] isEnbles;

    private List<USChargePriorityBean> datalist = new ArrayList<>();
    private List<USChargePriorityBean> newList = new ArrayList<>();

    private byte[] responByte;
    private int currenPos = -1;

    private int[] nowSet = {0x10, 3125, 3125};
    private int[][] registerValues = {{0} , {0, 0}};
    private int[]itemValues={0};

    private boolean isAllyear=false;

    private BaseCircleDialog explainDialog;

    @Override
    protected int getContentView() {
        return R.layout.activity_us_charge_time;
    }

    @Override
    protected void initViews() {
        initToobar(toolbar);
        tvTitle.setText(R.string.???????????????);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.inflateMenu(R.menu.comment_right_menu);
        item = toolbar.getMenu().findItem(R.id.right_action);
        item.setTitle(R.string.m370??????);
        toolbar.setOnMenuItemClickListener(this);


        rvPriority.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new USchargePeriodAdapter(R.layout.item_us_charge_time,this);
        rvPriority.setAdapter(mAdapter);


        //??????
        View headerView = LayoutInflater.from(mContext).inflate(R.layout.us_charge_time_head, rvPriority, false);
        View clChoiseDate = headerView.findViewById(R.id.cl_choise_date);
        tvValue = headerView.findViewById(R.id.tv_value);
        swSwitch = headerView.findViewById(R.id.sw_switch);
        tvAdd = headerView.findViewById(R.id.add);
        tvAddText = headerView.findViewById(R.id.tv_add_text);
        swSwitch.setOnCheckedChangeListener(this);
        tvPeriodExplain = headerView.findViewById(R.id.tv_period_explain);
        String hint = getString(R.string.android_key3096);
        tvPeriodExplain.setOnClickListener(view -> {
            explainDialog = CircleDialogUtils.showNotitleDialog(USChargeTimeActivity.this, hint, new CircleDialogUtils.OndialogClickListeners() {
                @Override
                public void buttonOk() {
                    explainDialog.dialogDismiss();
                }
                @Override
                public void buttonCancel() {
                    explainDialog.dialogDismiss();
                }
            });

        });

        View.OnClickListener onClickListener = view -> {
            int size = mAdapter.getData().size();
            if (size >= 9) {
                toast(R.string.android_key3093);
                return;
            }

            Intent intent = new Intent(USChargeTimeActivity.this, PeriodSettingActivity.class);
            intent.putExtra("isAllYear",isAllyear);
            intent.putExtra("selectIndex", currenPos);
            intent.putExtra("currentPos", size);
            ActivityUtils.startActivity(USChargeTimeActivity.this, intent, false);
        };
        tvAdd.setOnClickListener(onClickListener);
        tvAddText.setOnClickListener(onClickListener);

        clChoiseDate.setOnClickListener(view -> {
            Intent intent = new Intent(USChargeTimeActivity.this, UsChargeManagerSelectDateActivity.class);
            intent.putExtra(GlobalConstant.BYTE_SOCKET_RESPON, responByte);
            intent.putExtra("selectIndex", currenPos);
            ActivityUtils.startActivity(USChargeTimeActivity.this, intent, false);
          /* else {
                readRegisterValue();
            }*/
        });
        mAdapter.addHeaderView(headerView);


        LinearDivider linearDivider = new LinearDivider(this, LinearLayoutManager.VERTICAL, 50, ContextCompat.getColor(this, R.color.gray_eeeeeee));
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        //????????????????????????????????????????????????????????????????????????
        funs = new int[][]{
                {3, 3125, 3249}//????????????
        };


        isEnbles = new String[][]{
//                {getString(R.string.m208????????????), getString(R.string.m209????????????),getString(R.string.m210????????????)}
                {getString(R.string.m208????????????), getString(R.string.m209????????????), getString(R.string.m210????????????), getString(R.string.m?????????)}
                , {getString(R.string.m89??????) + "(0)", getString(R.string.m88??????) + "(1)"}
                , {getString(R.string.??????) + "1", getString(R.string.??????) + "2", getString(R.string.??????) + "3", getString(R.string.??????) + "4", getString(R.string.?????????) + "1", getString(R.string.?????????) + "2",}
                , {getString(R.string.??????), getString(R.string.??????), getString(R.string.week)}
        };


        readRegisterValue();
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
                            refreshUI(bs);
                            toast(R.string.all_success);
                        } else {
                            toast(R.string.all_failed);
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
                    BtnDelayUtil.dealTLXBtn(this, what, mContext, toolbar);
                    break;
            }
        }
    };


    /**
     * ?????????????????????UI
     *
     * @param bs i?????????????????????  ???????????????3038
     *           3038?????????
     *           bit0~7:?????????
     *           bit8~12:?????????
     *           bit13~14,0:?????????;
     *           1:?????????
     *           2:?????????
     *           bit15,0:??????; 1:??????;
     *           3039?????????
     *           bit0~7:?????????
     *           bit8~12:?????????
     *           bit13~15????????????
     */
    private void refreshUI(byte[] bs) {
        responByte = bs;
        //???????????????item?????? ???????????????????????????
        int startTime = 0;
        int endTime = 0;
        int isEnableB = 0;
        //???????????????1-??????????????????  ??????????????????????????????
        datalist.clear();
        for (int index = 0; index < 6; index++) {
            USChargePriorityBean bean = new USChargePriorityBean();
            if (index < 4) {//??????1-4
                int enableCRegistValue = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes125(bs, index + 3125, 0, 1));
                startTime = enableCRegistValue & 0xF;
                endTime = enableCRegistValue >>> 4 & 0xF;
                isEnableB = enableCRegistValue >>> 8 & 1;
            } else if (index == 4) {
                int enableCRegistValue = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3201, 0, 1));
                endTime = enableCRegistValue & 0xFF;
                startTime = enableCRegistValue >>> 8 & 0b01111111;
                isEnableB = enableCRegistValue >>> 15 & 1;
            } else if (index == 5) {
                int enableCRegistValue = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3220, 0, 1));
                endTime = enableCRegistValue & 0xFF;
                startTime = enableCRegistValue >>> 8 & 0b01111111;
                isEnableB = enableCRegistValue >>> 15 & 1;
            }

            bean.setStartTime(String.valueOf(startTime));
            bean.setEndTime(String.valueOf(endTime));
            bean.setIsEnableBIndex(isEnableB);
            bean.setIsEnableB(isEnbles[1][isEnableB]);
            datalist.add(bean);
        }

        //??????????????????????????????
        int select = 0;
        if (currenPos == -1) {
            for (int i = 0; i < datalist.size(); i++) {
                USChargePriorityBean usChargePriorityBean = datalist.get(i);
                int enable1 = usChargePriorityBean.getIsEnableBIndex();
                if (enable1 == 1) {
                    if (!"0".equals(usChargePriorityBean.getStartTime()) && !"0".equals(usChargePriorityBean.getEndTime())) {
                        select = i;
                        break;
                    }
                }
            }
            currenPos = select;
        } else {
            select = currenPos;
        }

        if (select==0){
            String startTime1 = datalist.get(0).getStartTime();
            String endTime1 = datalist.get(0).getEndTime();
            if ("1".equals(startTime1)&&"12".equals(endTime1)){
                tvAdd.setVisibility(View.GONE);
                tvAddText.setVisibility(View.GONE);
                isAllyear=true;
            }else {
                isAllyear=false;
                tvAdd.setVisibility(View.VISIBLE);
                tvAddText.setVisibility(View.VISIBLE);
            }
        }else {
            isAllyear=false;
            tvAdd.setVisibility(View.VISIBLE);
            tvAddText.setVisibility(View.VISIBLE);
        }


        USChargePriorityBean selectBean = datalist.get(select);
        String selectStartTime = selectBean.getStartTime();
        String selectEndTime = selectBean.getEndTime();
        int enable1 = selectBean.getIsEnableBIndex();
        swSwitch.setChecked(enable1 == 1);
        //????????????item??????
        int timerStartR = 3129;//????????????????????????
        if (select < 4) {
            timerStartR = 3129 + select * 18;
//            timerStartR = 3129;
            tvValue.setText(selectStartTime + "~" + selectEndTime);
        } else if (select == 4) {
            timerStartR = 3202;
            tvValue.setText(selectStartTime + "/" + selectEndTime);
        } else if (select == 5) {
            timerStartR = 3221;
            tvValue.setText(selectStartTime + "/" + selectEndTime);
        }

        newList.clear();

        int count =9;
        if (isAllyear)count=1;
        for (int i = 0; i < count; i++) {
            USChargePriorityBean item = new USChargePriorityBean();
            int value1 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes125(bs, timerStartR + i * 2, 0, 1));
            int value2 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes125(bs, timerStartR + i * 2 + 1, 0, 1));

            int startMin = value1 & 0b1111111;
            int startHour = value1 >>> 7 & 0b11111;
            int endMin = value2 & 0b1111111;
            int endHour = value2 >>> 7 & 0b11111;
            int enableAIndex = value1 >>> 12 & 0b111;
            int enableBIndex = value1 >>> 15 & 0b1;
            int enableWIndex = value2 >>> 12 & 0b11;

            Log.i("???????????????" + startHour + " ???????????????" + startMin + " ???????????????" + endHour + " ???????????????" + endMin);

            if (startMin == 0 && startHour == 0 && endMin == 0 && endHour == 0) continue;
            item.setTimePeriod(String.format("%02d:%02d%s%02d:%02d", startHour, startMin, "~", endHour, endMin));
            item.setIsEnableAIndex(enableAIndex);
            item.setIsEnableA(isEnbles[0][enableAIndex]);

            item.setIsEnableBIndex(enableBIndex);
            item.setIsEnableB(isEnbles[1][enableBIndex]);

            item.setIsEnableWeekIndex(enableWIndex);
            item.setIsEnableWeek(isEnbles[3][enableWIndex]);

            item.setSpecial(select>=4);
            newList.add(item);
        }
        mAdapter.replaceData(newList);

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
        switch (item.getItemId()) {
            case R.id.right_action:
                readRegisterValue();
                break;
        }
        return false;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        USChargePriorityBean bean = mAdapter.getData().get(position);
        String json = new Gson().toJson(bean);
        Intent intent = new Intent(USChargeTimeActivity.this, PeriodSettingActivity.class);
        intent.putExtra(GlobalConstant.KEY_JSON, json);
        intent.putExtra("isAllYear",isAllyear);
        intent.putExtra("selectIndex", currenPos);
        intent.putExtra("currentPos", position);
        ActivityUtils.startActivity(USChargeTimeActivity.this, intent, false);
    }

    @Override
    public void oncheck(boolean check, int position) {
        USChargePriorityBean bean = mAdapter.getData().get(position-1);
        int enableBindex=check?1:0;
        bean.setIsEnableBIndex(enableBindex);
        bean.setIsEnableB(isEnbles[1][enableBindex]);
        mAdapter.notifyDataSetChanged();

        //???????????????????????????
        int value1 = 0;
        int value2 = 0;

        int enableA = bean.getIsEnableAIndex();//EMS??????
        int enableB = bean.getIsEnableBIndex();//??????
        int enableW = bean.getIsEnableWeekIndex();//??????
        String timePeriod = bean.getTimePeriod();
        String[] time = timePeriod.split("~");
        String startTime = time[0];
        String endTime = time[1];

        String[] start = startTime.split(":");
        String[] end = endTime.split(":");


        int startHour = Integer.parseInt(start[0]);
        int startMin = Integer.parseInt(start[1]);
        int endHour = Integer.parseInt(end[0]);
        int endMin = Integer.parseInt(end[1]);
        value1 = (startMin & 0b01111111) | ((startHour & 0b00011111) << 7) | ((enableA & 0b111) << 12) | ((enableB & 1) << 15);
        value2 = (endMin & 0b01111111) | ((endHour & 0b00011111) << 7);
        if (currenPos < 4) {
            value2 = value2 | ((enableW & 0b11) << 12);
        }
        registerValues[1][0] = value1;
        registerValues[1][1] = value2;
        itemValues=registerValues[1];
        //?????????????????????
        int registPos = -1;
        if (currenPos < 4) {
            registPos = 3129 + currenPos * 18 + (position-1) * 2;
        } else if (currenPos == 4) {
            registPos = 3202 + (position-1) * 2;
        } else if (currenPos == 5) {
            registPos = 3221 + (position-1) * 2;
        }
        nowSet[1] = registPos;
        nowSet[2] = registPos + 1;


        connectServerWrite();

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

        if (compoundButton.isPressed()) {
            //??????????????????
            if (currenPos == -1) return;
            USChargePriorityBean bean = datalist.get(currenPos);

            //??????????????????
            String startTime = bean.getStartTime();
            String endTime = bean.getEndTime();
            int startV = Integer.parseInt(startTime);
            int endV = Integer.parseInt(endTime);
            int enableB = swSwitch.isChecked() ? 1 : 0;
            int value = 0;
            if (currenPos < 4) {
                value = (startV & 0xF) | ((endV & 0xF) << 4) | ((enableB & 1) << 8);
            } else {
                value = (endV & 0xFF) | ((startV & 0b1111111) << 8) | ((enableB & 1) << 15);
            }
            registerValues[0][0] = value;
            itemValues=registerValues[0];
            //?????????????????????
            int registPos = -1;
            if (currenPos < 4) {
                registPos = 3125 + currenPos;
            } else if (currenPos == 4) {
                registPos = 3201;
            } else if (currenPos == 5) {
                registPos = 3220;
            }
            nowSet[1] = nowSet[2] = registPos;
            //??????
            connectServerWrite();
        }


    }


    /**
     * ????????????
     *
     * @param bean
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventRoomBean(@NonNull UsChargeConfigMsg bean) {
        currenPos = bean.getPosition();
        readRegisterValue();
    }


    //????????????:???????????????
    private SocketClientUtil mClientUtilW;

    private void connectServerWrite() {
        Mydialog.Show(mContext);
        mClientUtilW = SocketClientUtil.connectServer(mHandlerW);
    }

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
                    sendBytes = SocketClientUtil.sendMsgToServer10(mClientUtilW, nowSet, itemValues);
                    LogUtil.i("????????????" + SocketClientUtil.bytesToHexString(sendBytes));
                    break;
                //??????????????????
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    Mydialog.Dismiss();
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //?????????????????????
                        boolean isCheck = MaxUtil.checkReceiverFull10(bytes);
                        if (isCheck) {
                            toast(R.string.all_success);
                            //????????????
                            SocketClientUtil.close(mClientUtilW);
                            this.postDelayed(() -> readRegisterValue(),500);
                        } else {
                            toast(R.string.all_failed);
                            //??????tcp??????
                            SocketClientUtil.close(mClientUtilW);
                            BtnDelayUtil.refreshFinish();
                        }

                        LogUtil.i("???????????????" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    BtnDelayUtil.dealTLXBtnWrite(this, what, mContext, toolbar);
                    break;
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
