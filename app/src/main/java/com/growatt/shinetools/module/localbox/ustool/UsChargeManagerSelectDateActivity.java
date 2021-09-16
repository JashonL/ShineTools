package com.growatt.shinetools.module.localbox.ustool;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.material.tabs.TabLayout;
import com.growatt.shinetools.R;
import com.growatt.shinetools.adapter.UsSettingAdapter;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.constant.GlobalConstant;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.MaxWifiParseUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.module.localbox.max.bean.USChargePriorityBean;
import com.growatt.shinetools.module.localbox.ustool.bean.UsChargeConfigMsg;
import com.growatt.shinetools.utils.BtnDelayUtil;
import com.growatt.shinetools.utils.CircleDialogUtils;
import com.growatt.shinetools.utils.DateUtils;
import com.growatt.shinetools.utils.Log;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.Mydialog;
import com.growatt.shinetools.widget.CustomPickView;
import com.mylhyl.circledialog.view.listener.OnLvItemClickListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.growatt.shinetools.utils.BtnDelayUtil.TIMEOUT_RECEIVE;

public class UsChargeManagerSelectDateActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener,
        UsSettingAdapter.OnChildCheckLiseners, Toolbar.OnMenuItemClickListener, TabLayout.OnTabSelectedListener, RadioGroup.OnCheckedChangeListener {

    private final String splitStr = "~";


    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.header_title)
    LinearLayout headerTitle;
    @BindView(R.id.tab_title)
    TabLayout tabTitle;
    @BindView(R.id.tv_all_year)
    TextView tvAllYear;
    @BindView(R.id.tv_ems)
    TextView tvEms;
    @BindView(R.id.tv_value)
    TextView tvValue;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.rb_q1)
    RadioButton rbQ1;
    @BindView(R.id.rb_q2)
    RadioButton rbQ2;
    @BindView(R.id.rb_q3)
    RadioButton rbQ3;
    @BindView(R.id.rb_q4)
    RadioButton rbQ4;
    @BindView(R.id.rg_quarterly)
    RadioGroup rgQuarterly;
    @BindView(R.id.et_month_start)
    TextView etMonthStart;
    @BindView(R.id.v_center)
    View vCenter;
    @BindView(R.id.et_month_end)
    TextView etMonthEnd;
    @BindView(R.id.rb_spcial1)
    RadioButton rbSpcial1;
    @BindView(R.id.rb_spcial2)
    RadioButton rbSpcial2;
    @BindView(R.id.rg_special)
    RadioGroup rgSpecial;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.sw_switch)
    Switch swSwitch;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.cl_special)
    ConstraintLayout clSpecial;
    @BindView(R.id.cl_all)
    ConstraintLayout clAll;
    @BindView(R.id.cl_quarterly)
    ConstraintLayout clQuarterly;
    @BindView(R.id.cl_ems)
    ConstraintLayout clEms;


    private List<USChargePriorityBean> datalist = new ArrayList<>();
    private List<USChargePriorityBean> periodList = new ArrayList<>();

    private byte[] responByte;
    private String[][] isEnbles;

//    private int quartely_index = 0;

    private int[][][] fullSet = {{{0x10, 3125, 3125}, {0x10, 3125, 3125}}, {{0x10, 3125, 3125}}};
    private int[][] nowSet = {{0x10, 3125, 3125}, {0x10, 3125, 3125}};
    private int[][][] fullValues = {{{0}, {0, 0}}, {{0, 0}}};
    private int[][] registerValues = {{0}, {0, 0}};

    private int[] closeSet = {0x10, 3125, 3125};


    private int poriotyIndex = 0;
    private int nowPos = 0;//当前设置项的下标
    private String[] months = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};

    private String spMonth;
    private String spDate;

    //记录tablayout选中的项
    private int tabseleted = 0;
    //记录季度选中的项
    private int quartely_pos = 0;
    //特殊日选中的项
    private int special_pos = 0;

    private int[]itemValues={0};

    private int currenSelect = 0;

    private int currenPos = -1;


    @Override
    protected int getContentView() {
        return R.layout.activity_charge_manager_date;
    }

    @Override
    protected void initViews() {
        initToobar(toolbar);
        tvTitle.setText(R.string.android_key3092);
        toolbar.setOnMenuItemClickListener(this);


        String[] titles = new String[]{getString(R.string.android_key3097), getString(R.string.季度),
                getString(R.string.特殊日)};
        tabTitle.removeAllTabs();
        for (String title : titles) {
            TabLayout.Tab tab = tabTitle.newTab();
            tab.setText(title);
            tabTitle.addTab(tab);
        }


        String s = getString(R.string.android_key2409) + 1;
        rbQ1.setText(s);
        s = getString(R.string.android_key2409) + 2;
        rbQ2.setText(s);
        s = getString(R.string.android_key2409) + 3;
        rbQ3.setText(s);
        s = getString(R.string.android_key2409) + 4;
        rbQ4.setText(s);

        String s1 = getString(R.string.android_key2413) + 1;
        rbSpcial1.setText(s1);
        s1 = getString(R.string.android_key2413) + 2;
        rbSpcial2.setText(s1);

        String s2 = getString(R.string.all_time_month) + "/" + getString(R.string.all_time_day);
        tvDate.setHint(s2);

        //设置监听
        tabTitle.addOnTabSelectedListener(this);
        rgQuarterly.setOnCheckedChangeListener(this);
        rgSpecial.setOnCheckedChangeListener(this);

    }

    @Override
    protected void initData() {
        isEnbles = new String[][]{
//                {getString(R.string.m208负载优先), getString(R.string.m209电池优先),getString(R.string.m210电网优先)}
                {getString(R.string.m208负载优先), getString(R.string.m209电池优先), getString(R.string.m210电网优先), getString(R.string.m防逆流)}
                , {getString(R.string.m89禁止) + "(0)", getString(R.string.m88使能) + "(1)"}
                , {getString(R.string.季度) + "1", getString(R.string.季度) + "2", getString(R.string.季度) + "3", getString(R.string.季度) + "4", getString(R.string.特殊日) + "1", getString(R.string.特殊日) + "2",}
                , {getString(R.string.周内), getString(R.string.周末), getString(R.string.week)}
        };


        //原始数据
        responByte = getIntent().getByteArrayExtra(GlobalConstant.BYTE_SOCKET_RESPON);
        currenPos = getIntent().getIntExtra("selectIndex",-1);

        if (responByte != null) {
            datalist.clear();
            //设置第一个item数据 季度对应寄存器下标
            int startTime = 0;
            int endTime = 0;
            int isEnableB = 0;
            //解析6个数据
            for (int index = 0; index < 6; index++) {
                USChargePriorityBean bean = new USChargePriorityBean();
                if (index < 4) {//季度1-4
                    int enableCRegistValue = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes125(responByte, index + 3125, 0, 1));
                    startTime = enableCRegistValue & 0xF;
                    endTime = enableCRegistValue >>> 4 & 0xF;
                    isEnableB = enableCRegistValue >>> 8 & 1;
                } else if (index == 4) {
                    int enableCRegistValue = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes125(responByte, 3201, 0, 1));
                    endTime = enableCRegistValue & 0xFF;
                    startTime = enableCRegistValue >>> 8 & 0b01111111;
                    isEnableB = enableCRegistValue >>> 15 & 1;
                } else if (index == 5) {
                    int enableCRegistValue = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes125(responByte, 3220, 0, 1));
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
            refreshUI(responByte);
            //设置Tab选中项
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

            //获取当前显示的bean类
            USChargePriorityBean selectBean = datalist.get(select);
            String selectStartTime = selectBean.getStartTime();
            String selectEndTime = selectBean.getEndTime();
            int enable1 = selectBean.getIsEnableBIndex();

            int tabIndex;//tablayout下标
            if (select < 4) {
                if ("1".equals(selectStartTime) && "12".equals(selectEndTime)) {
                    tabIndex = 0;
                    tabseleted = 0;
                } else {
                    tabIndex = 1;
                    tabseleted = 1;
                }
            } else {
                tabIndex = 2;
                tabseleted = 2;
            }


      /*      //设置监听
            tabTitle.addOnTabSelectedListener(this);
            rgQuarterly.setOnCheckedChangeListener(this);
            rgSpecial.setOnCheckedChangeListener(this);*/

            rgQuarterly.check(R.id.rb_q1);//默认选中季度1
            rgSpecial.check(R.id.rb_spcial1);//默认选中特殊日1

            tabTitle.getTabAt(tabIndex).select();
            swSwitch.setChecked(enable1 == 1);
            //设置RadioButton选中项
            switch (select) {
                case 0:
                    rgQuarterly.check(R.id.rb_q1);
                    break;
                case 1:
                    rgQuarterly.check(R.id.rb_q2);
                    break;
                case 2:
                    rgQuarterly.check(R.id.rb_q3);
                    break;
                case 3:
                    rgQuarterly.check(R.id.rb_q4);
                    break;
                case 4:
                    rgSpecial.check(R.id.rb_spcial1);
                    break;
                case 5:
                    rgSpecial.check(R.id.rb_spcial2);
                    break;
            }

        }

    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

    }

    @Override
    public void oncheck(boolean check, int position) {

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        //根据下标显示控件
        int position = tab.getPosition();
        clAll.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
        clQuarterly.setVisibility(position == 1 ? View.VISIBLE : View.GONE);
        clSpecial.setVisibility(position == 2 ? View.VISIBLE : View.GONE);
        tabseleted = position;
        //根据下标设置数据
        if (datalist.size() > 5) {
            switch (position) {
                case 0://全年
                    if (periodList != null && periodList.size() > 0) {
                        int enableA = periodList.get(0).getIsEnableAIndex();
                        int enableBIndex = periodList.get(0).getIsEnableBIndex();
                        swSwitch.setChecked(1 == enableBIndex);
                        tvValue.setText(isEnbles[0][enableA]);
                    }
                    break;
                case 1:
                    if (datalist.size() > quartely_pos) {
                        int enableBIndex1 = datalist.get(quartely_pos).getIsEnableBIndex();
                        swSwitch.setChecked(1 == enableBIndex1);
                    }
                    break;
                case 2://季度
                    if (datalist.size() > special_pos) {
                        int enableBIndex1 = datalist.get(special_pos).getIsEnableBIndex();
                        swSwitch.setChecked(1 == enableBIndex1);
                    }
                    break;

            }
        }


    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    //取全年的优先设置
    private void refreshUI(byte[] bs) {


        periodList.clear();

        int timerStartR = 3129;//时间段起始寄存器
        for (int i = 0; i < 9; i++) {
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

            Log.i("开始时间：" + startHour + " 结束时间：" + startMin + " 开始时间：" + endHour + " 结束时间：" + endMin);

            if (startMin == 0 && startHour == 0 && endMin == 0 && endHour == 0) continue;
            item.setTimePeriod(String.format("%02d:%02d%s%02d:%02d", startHour, startMin, "~", endHour, endMin));
            item.setIsEnableAIndex(enableAIndex);
            item.setIsEnableA(isEnbles[0][enableAIndex]);

            item.setIsEnableBIndex(enableBIndex);
            item.setIsEnableB(isEnbles[1][enableBIndex]);

            item.setIsEnableWeekIndex(enableWIndex);
            item.setIsEnableWeek(isEnbles[3][enableWIndex]);
            periodList.add(item);
        }

        if (periodList.size() > 0) {
            int isEnableAIndex = periodList.get(0).getIsEnableAIndex();
            if (isEnableAIndex < 4) {
                poriotyIndex = isEnableAIndex;
                String sItem = isEnbles[0][isEnableAIndex];
                tvValue.setText(sItem);
            }

        }

    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int id) {
        if (radioGroup == rgQuarterly) {
            switch (id) {
                case R.id.rb_q1:
                    quartely_pos = 0;
                    break;
                case R.id.rb_q2:
                    quartely_pos = 1;
                    break;
                case R.id.rb_q3:
                    quartely_pos = 2;
                    break;
                case R.id.rb_q4:
                    quartely_pos = 3;
                    break;
            }
            if (datalist != null && datalist.size() > 0) {
                String startTime = datalist.get(quartely_pos).getStartTime();
                String endTime = datalist.get(quartely_pos).getEndTime();
                int enableBIndex = datalist.get(quartely_pos).getIsEnableBIndex();
                if (!TextUtils.isEmpty(startTime)) {
                    etMonthStart.setText(startTime);
                }
                if (!TextUtils.isEmpty(endTime)) {
                    etMonthEnd.setText(endTime);
                }
                swSwitch.setChecked(1 == enableBIndex);
            }

        } else if (radioGroup == rgSpecial) {
            switch (id) {
                case R.id.rb_spcial1:
                    special_pos = 4;
                    break;
                case R.id.rb_spcial2:
                    special_pos = 5;
                    break;
            }
            if (datalist != null && datalist.size() > 0) {
                String startTime = datalist.get(special_pos).getStartTime();
                String endTime = datalist.get(special_pos).getEndTime();
                int enableBIndex = datalist.get(special_pos).getIsEnableBIndex();
                if (!TextUtils.isEmpty(startTime) && !TextUtils.isEmpty(endTime)) {
                    String s = startTime + "/" + endTime;
                    tvDate.setText(s);
                }
                swSwitch.setChecked(1 == enableBIndex);
            }


        }
    }


    @OnClick({R.id.cl_ems, R.id.btn_save, R.id.et_month_start, R.id.et_month_end, R.id.tv_date})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cl_ems:
                CircleDialogUtils.showCommentItemDialog(this, getString(R.string.m225请选择),
                        Arrays.asList(isEnbles[0]), Gravity.CENTER, new OnLvItemClickListener() {
                            @Override
                            public boolean onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                poriotyIndex = position;
                                String sItem = isEnbles[0][position];
                                tvValue.setText(sItem);
                                return true;
                            }
                        }, null);
                break;

            case R.id.et_month_start:
                CustomPickView.showPickView(UsChargeManagerSelectDateActivity.this, Arrays.asList(months),
                        (options1, options2, options3, v) -> {
                            etMonthStart.setText(months[options1]);
                        }, getString(R.string.m359月_ios));
                break;

            case R.id.et_month_end:
                CustomPickView.showPickView(UsChargeManagerSelectDateActivity.this, Arrays.asList(months),
                        (options1, options2, options3, v) -> {
                            etMonthEnd.setText(months[options1]);
                        }, getString(R.string.m359月_ios));
                break;


            case R.id.tv_date:
                try {
                    DateUtils.showTimepickViews(UsChargeManagerSelectDateActivity.this, null, new DateUtils.ImplSelectTimeListener() {
                        @Override
                        public void seletedListener(String date) {
                            String[] split = date.split("-");
                            spMonth = split[1];
                            spDate = split[2];
                            String s = spMonth + "/" + spDate;
                            tvDate.setText(s);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;


            case R.id.btn_save:
                //头部选中项
                switch (tabseleted) {
                    case 0://全年
                        setCloseData();
                        closeOtherWrite();//设置全年  要关闭其他季度的使能
                        break;
                    case 1://季度
                        //设置头部数据
                        String startTime1 = etMonthStart.getText().toString();
                        String endTime1 = etMonthEnd.getText().toString();

                        if (TextUtils.isEmpty(startTime1)
                                || TextUtils.isEmpty(endTime1)
                        ) {
                            toast(R.string.all_blank);
                            return;
                        }

                        int startV2 = Integer.parseInt(startTime1);
                        int endV2 = Integer.parseInt(endTime1);
                        int enableB2 = swSwitch.isChecked() ? 1 : 0;
                        int value2 = (startV2 & 0xF) | ((endV2 & 0xF) << 4) | ((enableB2 & 1) << 8);
                        registerValues = fullValues[0];
                        registerValues[0][0] = value2;
                        //设置起始寄存器
                        //设置起始寄存器
                        int registPos2 = -1;
                        registPos2 = 3125 + quartely_pos;
                        nowSet = fullSet[1];
                        nowSet[0][1] = nowSet[0][2] = registPos2;
                        nowPos = 0;
                        connectServerWrite();
                        break;
                    case 2:
                        //设置头部数据
                        String s = tvDate.getText().toString();
                        if (TextUtils.isEmpty(s)) {
                            toast(R.string.all_blank);
                            return;
                        }
                        spMonth = String.valueOf(1);
                        spDate = String.valueOf(1);

                        String startTime2 = spMonth;
                        String endTime2 = spDate;
                        try {
                            String[] split1 = s.split("/");
                            startTime2 = split1[0];
                            endTime2 = split1[1];
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        int startV3 = Integer.parseInt(startTime2);
                        int endV3 = Integer.parseInt(endTime2);
                        int enableB3 = swSwitch.isChecked() ? 1 : 0;

                        int value3 = (endV3 & 0xFF) | ((startV3 & 0b1111111) << 8) | ((enableB3 & 1) << 15);
                        registerValues = fullValues[0];
                        registerValues[0][0] = value3;
                        //设置起始寄存器
                        int registPos3 = -1;
                        if (special_pos == 4) {
                            registPos3 = 3201;
                        } else if (special_pos == 5) {
                            registPos3 = 3220;
                        }
                        nowSet = fullSet[1];
                        nowSet[0][1] = nowSet[0][2] = registPos3;
                        nowPos = 0;
                        connectServerWrite();
                        break;
                }



                break;
        }
    }


    /**
     * 设置全年
     */
    private void setAllYearData() {
        //设置头部数据
        String startTime = "1";
        String endTime = "12";
        int startV = Integer.parseInt(startTime);
        int endV = Integer.parseInt(endTime);
        int enableB = swSwitch.isChecked() ? 1 : 0;
        int value = (startV & 0xF) | ((endV & 0xF) << 4) | ((enableB & 1) << 8);
        registerValues = fullValues[0];
        registerValues[0][0] = value;
        //设置起始寄存器
        int registPos = 3125;
        nowSet = fullSet[0];
        nowSet[0][1] = nowSet[0][2] = registPos;


        //设置时间段数据
        int value01 = 0;
        int value02 = 0;
        String timePeriod = "00:00~23:59";
        String[] split = timePeriod.split(splitStr);
        String[] startTime02 = split[0].split(":");
        String[] endTime02 = split[1].split(":");
        int enableA = poriotyIndex;
        int enableB02 = swSwitch.isChecked() ? 1 : 0;
        int enableW = 2;//代表全周
        int startHour = Integer.parseInt(startTime02[0]);
        int startMin = Integer.parseInt(startTime02[1]);
        int endHour = Integer.parseInt(endTime02[0]);
        int endMin = Integer.parseInt(endTime02[1]);
        value01 = (startMin & 0b01111111) | ((startHour & 0b00011111) << 7) | ((enableA & 0b111) << 12) | ((enableB02 & 1) << 15);
        value02 = (endMin & 0b01111111) | ((endHour & 0b00011111) << 7);
        value02 = value02 | ((enableW & 0b11) << 12);
        registerValues[1][0] = value01;
        registerValues[1][1] = value02;
        //设置起始寄存器
        int registPos02 = 3129;
        nowSet[1][1] = registPos02;
        nowSet[1][2] = registPos02 + 1;

        nowPos = 0;
        connectServerWrite();
    }


    //----------------------


    //连接对象:用于写数据
    private SocketClientUtil otherUtils;

    private void closeOtherWrite() {
        Mydialog.Show(mContext);
        otherUtils = SocketClientUtil.connectServer(handlerOther);
    }

    Handler handlerOther = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessageWrite(this);
                    byte[] bytes1 = SocketClientUtil.sendMsgToServer10(otherUtils, closeSet, itemValues);
                    LogUtil.i("发送写入：" + SocketClientUtil.bytesToHexString(bytes1));
                    break;
                //接收字节数组
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //检测内容正确性
                        boolean isCheck = MaxUtil.checkReceiverFull10(bytes);
                        if (isCheck) {
                            if (currenSelect <3){
                                currenSelect++;
                                setCloseData();
                                handlerOther.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                            }else {
                                currenSelect =0;
                                //关闭tcp连接
                                SocketClientUtil.close(otherUtils);
                                BtnDelayUtil.refreshFinish();
                                this.postDelayed(() -> setAllYearData(),500);
                            }
                        } else {
                            currenSelect =0;
                            //关闭tcp连接
                            SocketClientUtil.close(otherUtils);
                            //无论成功还是失败 都请求设置全年
                            this.postDelayed(() -> setAllYearData(),500);
                        }

                        LogUtil.i("接收写入：" + SocketClientUtil.bytesToHexString(bytes));
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





    private void setCloseData(){
        USChargePriorityBean bean = datalist.get(currenSelect);
        //设置头部数据
        String startTime = bean.getStartTime();
        String endTime = bean.getEndTime();
        int startV = Integer.parseInt(startTime);
        int endV = Integer.parseInt(endTime);
        int enableB = 0;
        int value = 0;
        if (currenSelect < 4) {
            value = (startV & 0xF) | ((endV & 0xF) << 4) | ((enableB & 1) << 8);
        } else {
            value = (endV & 0xFF) | ((startV & 0b1111111) << 8) | ((enableB & 1) << 15);
        }
        itemValues[0] = value;
        //设置起始寄存器
        int registPos = -1;
        if (currenSelect < 4) {
            registPos = 3125 + currenSelect;
        } else if (currenSelect == 4) {
            registPos = 3201;
        } else if (currenSelect == 5) {
            registPos = 3220;
        }
        closeSet[1] = closeSet[2] = registPos;


    }


    //-------------------












//连接对象:用于写数据
    private SocketClientUtil mClientUtilW;

    private void connectServerWrite() {
        Mydialog.Show(mContext);
        mClientUtilW = SocketClientUtil.connectServer(mHandlerW);
    }

    //当前发送的字节数组
    private byte[] sendBytes;
    Handler mHandlerW = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessageWrite(this);
                    sendBytes = SocketClientUtil.sendMsgToServer10(mClientUtilW, nowSet[nowPos], registerValues[nowPos]);
                    LogUtil.i("发送写入" + (nowPos) + ":" + SocketClientUtil.bytesToHexString(sendBytes));

               /*     if (nowSet != null) {
                        if (nowPos >= nowSet.length - 1) {
                            nowPos = -1;
                            //关闭tcp连接
                            if (mClientUtilW != null) {
                                mClientUtilW.closeSocket();
                                BtnDelayUtil.refreshFinish();
                                //移除接收超时
                                this.removeMessages(TIMEOUT_RECEIVE);
                            }
                        } else {
                            nowPos = nowPos + 1;
                            sendBytes = SocketClientUtil.sendMsgToServer10(mClientUtilW, nowSet[nowPos], registerValues[nowPos]);
                            LogUtil.i("发送写入" + (nowPos + 1) + ":" + SocketClientUtil.bytesToHexString(sendBytes));
                        }
                    }*/

                    break;
                //接收字节数组
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //检测内容正确性
                        boolean isCheck = MaxUtil.checkReceiverFull10(bytes);
                        if (isCheck) {
                            if (nowPos < nowSet.length - 1) {
                                nowPos++;
                                //继续发送设置命令
                                mHandlerW.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                            } else {
                                this.removeMessages(TIMEOUT_RECEIVE);
                                //关闭连接
                                SocketClientUtil.close(mClientUtilW);
                                BtnDelayUtil.refreshFinish();
                                CircleDialogUtils.showCommentDialog(UsChargeManagerSelectDateActivity.this,
                                        getString(R.string.android_key537),
                                        getString(R.string.m设置成功), getString(R.string.android_key1935),
                                        getString(R.string.android_key2152),
                                        Gravity.CENTER, v -> {
                                            int pos = 0;
                                            if (tabseleted == 1) {
                                                pos = quartely_pos;
                                            } else if (tabseleted == 2) {
                                                pos = special_pos;
                                            }
                                            EventBus.getDefault().post(new UsChargeConfigMsg(pos));
                                            finish();
                                        }, null);
                            }

                        } else {
                            nowPos = 0;
                            BtnDelayUtil.refreshFinish();
                            //关闭tcp连接
                            SocketClientUtil.close(mClientUtilW);
                            String text = getString(R.string.android_key3103) + "(" + getString(R.string.previous_time_period) + ")";
                            CircleDialogUtils.showCommentDialog(UsChargeManagerSelectDateActivity.this,
                                    getString(R.string.android_key539),
                                    text, getString(R.string.android_key1935), getString(R.string.android_key2152),
                                    Gravity.CENTER, v -> {

                                    }, null);

                        }
                        LogUtil.i("接收写入：" + SocketClientUtil.bytesToHexString(bytes));
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
        mHandlerW.removeCallbacksAndMessages(null);
    }
}
