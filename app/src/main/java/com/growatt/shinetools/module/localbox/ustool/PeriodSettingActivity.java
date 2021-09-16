package com.growatt.shinetools.module.localbox.ustool;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.growatt.shinetools.R;
import com.growatt.shinetools.adapter.UsSettingAdapter;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.bean.USDebugSettingBean;
import com.growatt.shinetools.bean.UsSettingConstant;
import com.growatt.shinetools.constant.GlobalConstant;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.module.localbox.max.bean.USChargePriorityBean;
import com.growatt.shinetools.module.localbox.ustool.bean.TimeSelectBean;
import com.growatt.shinetools.module.localbox.ustool.bean.UsChargeConfigMsg;
import com.growatt.shinetools.utils.ActivityUtils;
import com.growatt.shinetools.utils.BtnDelayUtil;
import com.growatt.shinetools.utils.CircleDialogUtils;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.Mydialog;
import com.growatt.shinetools.widget.GridDivider;
import com.mylhyl.circledialog.BaseCircleDialog;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.view.listener.OnLvItemClickListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class PeriodSettingActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener,
        UsSettingAdapter.OnChildCheckLiseners, Toolbar.OnMenuItemClickListener, BaseQuickAdapter.OnItemChildClickListener {

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


    //设置项
    private String[] titles;
    private String[][] registers;
    private int[] itemTypes;

    //设置数据
    //设置功能码集合（功能码，寄存器，数据）
    private int[][] registerValues = {
            {0}//头部数据
            , {0, 0}//时间段数据
    };
    private int[][] nowSet = {
            {0x10, 3125, 3125}
    };
    //当前设置的时间段的位置
    private int currentPos;
    //当前选中的是哪个季度
    private int selectIndex;


    private String[][] isEnbles;

    private USChargePriorityBean setBean = new USChargePriorityBean();

    private Button btnNext;
    private boolean isAllYear;

    @Override
    protected int getContentView() {
        return R.layout.activity_period_setting;
    }

    @Override
    protected void initViews() {
        initToobar(toolbar);
        tvTitle.setText(R.string.android_key3094);
        toolbar.setOnMenuItemClickListener(this);

        rvSystem.setLayoutManager(new LinearLayoutManager(this));
        usParamsetAdapter = new UsSettingAdapter(new ArrayList<>(), this);
        int div = (int) getResources().getDimension(R.dimen.dp_1);
        GridDivider gridDivider = new GridDivider(ContextCompat.getColor(this, R.color.white), div, div);
        rvSystem.addItemDecoration(gridDivider);
        rvSystem.setAdapter(usParamsetAdapter);
        usParamsetAdapter.setOnItemClickListener(this);
        usParamsetAdapter.setOnItemChildClickListener(this);
        View footView = LayoutInflater.from(this).inflate(R.layout.foot_recycleview_button, rvSystem, false);
        btnNext = footView.findViewById(R.id.btn_next);
        btnNext.setText(R.string.m182保存);
        btnNext.setOnClickListener(view -> {
            saveParam();
        });
        usParamsetAdapter.addFooterView(footView);
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);

        selectIndex = getIntent().getIntExtra("selectIndex", 0);
        currentPos = getIntent().getIntExtra("currentPos", 0);
         isAllYear = getIntent().getBooleanExtra("isAllYear", false);
        if (selectIndex < 4) {
            titles = new String[]{getString(R.string.android_key3099), getString(R.string.m222时间段),
                    getString(R.string.android_key3100), getString(R.string.m88使能)};
            //item的显示类型
            itemTypes = new int[]{
                    UsSettingConstant.SETTING_TYPE_SELECT, UsSettingConstant.SETTING_TYPE_NEXT,
                    UsSettingConstant.SETTING_TYPE_EXPLAIN, UsSettingConstant.SETTING_TYPE_SWITCH
            };
        } else {
            titles = new String[]{getString(R.string.m222时间段),
                    getString(R.string.android_key3100), getString(R.string.m88使能)};
            //item的显示类型
            itemTypes = new int[]{
                    UsSettingConstant.SETTING_TYPE_NEXT,
                    UsSettingConstant.SETTING_TYPE_EXPLAIN, UsSettingConstant.SETTING_TYPE_SWITCH
            };
        }

        isEnbles = new String[][]{
                {getString(R.string.m208负载优先), getString(R.string.m209电池优先), getString(R.string.m210电网优先), getString(R.string.m防逆流)}
                , {getString(R.string.m89禁止) + "(0)", getString(R.string.m88使能) + "(1)"}
                , {getString(R.string.季度) + "1", getString(R.string.季度) + "2", getString(R.string.季度) + "3", getString(R.string.季度) + "4", getString(R.string.特殊日) + "1", getString(R.string.特殊日) + "2",}
                , {getString(R.string.周内), getString(R.string.周末), getString(R.string.week)}
        };


        //初始化设置项
        List<USDebugSettingBean> newlist = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            USDebugSettingBean bean = new USDebugSettingBean();
            bean.setTitle(titles[i]);
            bean.setItemType(itemTypes[i]);
            bean.setUnit("");
            switch (i) {
                case 0://周
                    bean.setValue(String.valueOf(0));
                    bean.setValueStr(isEnbles[3][0]);

                    setBean.setIsEnableWeekIndex(0);
                    setBean.setIsEnableWeek(isEnbles[3][0]);
                    break;
                case 1://时间段
                    bean.setValue(String.valueOf(0));
                    bean.setValueStr("00:00~00:00");

                    setBean.setTimePeriod("00:00~00:00");
                    break;
                case 2://ems状态
                    bean.setValue(String.valueOf(0));
                    bean.setValueStr(isEnbles[0][0]);

                    setBean.setIsEnableA(isEnbles[0][0]);
                    setBean.setIsEnableAIndex(0);
                    break;
                case 3://使能
                    bean.setValue(String.valueOf(1));
                    bean.setValueStr(isEnbles[1][0]);

                    setBean.setIsEnableB(isEnbles[1][0]);
                    setBean.setIsEnableBIndex(1);
                    break;
            }

            newlist.add(bean);
        }
        usParamsetAdapter.replaceData(newlist);


        String dataJson = getIntent().getStringExtra(GlobalConstant.KEY_JSON);
        if (TextUtils.isEmpty(dataJson)) {//添加
            tvTitle.setText(R.string.android_key2804);
        } else {//编辑
            tvTitle.setText(R.string.android_key3094);
            USChargePriorityBean bean = new Gson().fromJson(dataJson, USChargePriorityBean.class);
            setBean = bean;
            //类型
            int isEnableWeekIndex = bean.getIsEnableWeekIndex();
            String isEnableWeek = bean.getIsEnableWeek();
            usParamsetAdapter.getData().get(0).setValue(String.valueOf(isEnableWeekIndex));
            usParamsetAdapter.getData().get(0).setValueStr(isEnableWeek);
            //时间段
            String timePeriodRead = bean.getTimePeriod();
            usParamsetAdapter.getData().get(1).setValueStr(timePeriodRead);
            //EMS状态
            int isEnableAIndex = bean.getIsEnableAIndex();
            String isEnableA = bean.getIsEnableA();
            usParamsetAdapter.getData().get(2).setValueStr(isEnableA);
            usParamsetAdapter.getData().get(2).setValue(String.valueOf(isEnableAIndex));

            //使能
            if (selectIndex < 4) {
                int enableBIndex = bean.getIsEnableBIndex();
                String isEnableB = bean.getIsEnableB();
                usParamsetAdapter.getData().get(3).setValue(String.valueOf(enableBIndex));
                usParamsetAdapter.getData().get(3).setValueStr(isEnableB);

                usParamsetAdapter.notifyDataSetChanged();
            }


        }
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (isAllYear){
            if (position==0){
                toast(R.string.android_key3117);
                return;
            }
            if (position==1){
                toast(R.string.android_key3118);
                return;
            }
        }


        switch (position) {
            case 0:
                showSelectDialog(3);
                break;

            case 1:
                EventBus.getDefault().postSticky(setBean);
                ActivityUtils.gotoActivity(PeriodSettingActivity.this, USTimeSelectSetActivity.class, false);
                break;
            case 2:
                showSelectDialog(0);
                break;


        }
    }

    @Override
    public void oncheck(boolean check, int position) {
        int indexB = check ? 1 : 0;
        setBean.setIsEnableB(isEnbles[1][0]);
        setBean.setIsEnableBIndex(indexB);
    }


    /**
     * @param type 0 代表 限制或不限制  1 代表禁止或使能；2代表季度选择;3:是否周末或周内
     */
    public void showSelectDialog(int type) {
        new CircleDialog.Builder()
                .setTitle(getString(R.string.m225请选择))
                .setGravity(Gravity.CENTER)
                .setWidth(0.7f)
                .setItems(isEnbles[type], new OnLvItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                        switch (type) {
                            case 0:
                                usParamsetAdapter.getData().get(2).setValueStr(isEnbles[type][pos]);
                                usParamsetAdapter.getData().get(2).setValue(String.valueOf(pos));

                                setBean.setIsEnableA(isEnbles[type][pos]);
                                setBean.setIsEnableAIndex(pos);

                                break;
                            case 1:

                                break;
                            case 2:

                                break;
                            case 3:
                                usParamsetAdapter.getData().get(0).setValueStr(isEnbles[type][pos]);
                                usParamsetAdapter.getData().get(0).setValue(String.valueOf(pos));

                                setBean.setIsEnableWeekIndex(pos);
                                setBean.setIsEnableWeek(isEnbles[type][pos]);
                                break;
                        }
                        usParamsetAdapter.notifyDataSetChanged();
                        return true;
                    }
                })
                .setNegative(getString(R.string.all_no), null)
                .show(getSupportFragmentManager());
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventPriorityBean(@NonNull TimeSelectBean eventBean) {
        setBean.setTimePeriod(eventBean.getPeriod());
        usParamsetAdapter.getData().get(1).setValueStr(eventBean.getPeriod());
        usParamsetAdapter.notifyDataSetChanged();
    }


    private void saveParam() {
        List<USDebugSettingBean> data = usParamsetAdapter.getData();
        //判断时间值
        String valueStr = data.get(1).getValueStr();
        String[] time = valueStr.split("~");
        String startTime = time[0];
        String endTime = time[1];

        String[] start = startTime.split(":");
        String[] end = endTime.split(":");


        int start_time = Integer.parseInt(start[0]) + Integer.parseInt(start[1]) * 60;
        int start_end = Integer.parseInt(end[0]) + Integer.parseInt(end[1]) * 60;

        if (start_time == 0 && start_end == 0) {
            toast(R.string.android_key3101);
            return;
        }

        if (start_time - start_end >= 0) {
            toast(R.string.android_key3102);
            return;
        }


        //判断并设置寄存器值
        int value1 = 0;
        int value2 = 0;

        int enableA = setBean.getIsEnableAIndex();//EMS状态
        int enableB = setBean.getIsEnableBIndex();//使能
        int enableW = setBean.getIsEnableWeekIndex();//周末
        int startHour = Integer.parseInt(start[0]);
        int startMin = Integer.parseInt(start[1]);
        int endHour = Integer.parseInt(end[0]);
        int endMin = Integer.parseInt(end[1]);
        value1 = (startMin & 0b01111111) | ((startHour & 0b00011111) << 7) | ((enableA & 0b111) << 12) | ((enableB & 1) << 15);
        value2 = (endMin & 0b01111111) | ((endHour & 0b00011111) << 7);
        if (selectIndex < 4) {
            value2 = value2 | ((enableW & 0b11) << 12);
        }
        registerValues[1][0] = value1;
        registerValues[1][1] = value2;
        //设置起始寄存器
        int registPos = -1;
        if (selectIndex < 4) {
            registPos = 3129 + selectIndex * 18 + (currentPos) * 2;
        } else if (selectIndex == 4) {
            registPos = 3202 + (currentPos) * 2;
        } else if (selectIndex == 5) {
            registPos = 3221 + (currentPos) * 2;
        }
        nowSet[0][1] = registPos;
        nowSet[0][2] = registPos + 1;


        connectServerWrite();

    }

    //连接对象:用于写数据
    private SocketClientUtil mClientUtilW;

    private void connectServerWrite() {
        Mydialog.Show(mContext);
        mClientUtilW = SocketClientUtil.connectServer(mHandlerW);
    }

    /**
     * 写寄存器handle
     */
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
                    sendBytes = SocketClientUtil.sendMsgToServer10(mClientUtilW, nowSet[0], registerValues[1]);
                    LogUtil.i("发送写入：" + SocketClientUtil.bytesToHexString(sendBytes));
                    break;
                //接收字节数组
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //检测内容正确性
                        boolean isCheck = MaxUtil.checkReceiverFull10(bytes);
                        if (isCheck) {
                            //关闭连接
                            Mydialog.Dismiss();
                            SocketClientUtil.close(mClientUtilW);
                            CircleDialogUtils.showCommentDialog(PeriodSettingActivity.this,
                                    getString(R.string.android_key537),
                                    getString(R.string.m设置成功), getString(R.string.android_key1935),
                                    getString(R.string.android_key2152),
                                    Gravity.CENTER, v -> {
                                        EventBus.getDefault().post(new UsChargeConfigMsg(selectIndex));
                                        finish();
                                    }, null);


                        } else {
                            String title = getString(R.string.m363设置失败);
                            String text = getString(R.string.android_key3103) + "(" + getString(R.string.android_key3096) + ")";
                            CircleDialogUtils.showCommentDialog((FragmentActivity) PeriodSettingActivity.this, title,
                                    text, getString(R.string.android_key1935), getString(R.string.android_key2152),
                                    Gravity.CENTER, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    }, null);
                        }
                        LogUtil.i("接收写入：" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    BtnDelayUtil.dealTLXBtnWrite(this, what, mContext, btnNext);
                    break;
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    private BaseCircleDialog explainDialog;
    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        USDebugSettingBean bean = usParamsetAdapter.getData().get(position);
        switch (view.getId()) {
            case R.id.tv_title:
                if (bean.getItemType() == UsSettingConstant.SETTING_TYPE_EXPLAIN) {
                    String title = bean.getTitle();
                    String content=getString(R.string.ems_explain);
                    explainDialog = CircleDialogUtils.showExplainDialog(PeriodSettingActivity.this, title,content ,
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
                }
                break;

        }
    }
}
