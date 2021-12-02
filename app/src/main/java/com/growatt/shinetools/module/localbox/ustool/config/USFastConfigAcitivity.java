package com.growatt.shinetools.module.localbox.ustool.config;

import android.os.Handler;
import android.text.TextUtils;
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
import com.growatt.shinetools.R;
import com.growatt.shinetools.ShineToosApplication;
import com.growatt.shinetools.adapter.DeviceSettingAdapter;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.bean.UsSettingConstant;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.MaxWifiParseUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.module.localbox.max.bean.ALLSettingBean;
import com.growatt.shinetools.module.localbox.ustool.USWiFiConfigActivity;
import com.growatt.shinetools.socket.ConnectHandler;
import com.growatt.shinetools.socket.SocketManager;
import com.growatt.shinetools.utils.ActivityUtils;
import com.growatt.shinetools.utils.CircleDialogUtils;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.MyControl;
import com.growatt.shinetools.utils.MyToastUtils;
import com.growatt.shinetools.utils.Mydialog;
import com.growatt.shinetools.widget.GridDivider;
import com.mylhyl.circledialog.CircleDialog;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;

import static com.growatt.shinetools.constant.GlobalConstant.KEFU_USER;

public class USFastConfigAcitivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener,
        DeviceSettingAdapter.OnChildCheckLiseners, Toolbar.OnMenuItemClickListener, BaseQuickAdapter.OnItemChildClickListener {
    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.header_title)
    LinearLayout headerTitle;
    @BindView(R.id.rv_setting)
    RecyclerView rvSetting;


    private MenuItem item;
    private DeviceSettingAdapter usParamsetAdapter;
    private SocketManager manager;
    private int uuid = 0;//当前请求项
    private int type = 0;//0：读取  1：设置
    private List<String> models;

    //跳转到其他页面
    private boolean toOhterSetting = false;


    private String[][] modelToal;
    private int modelPos = -1;//当前model下标

    private int settingIndex = 0;//当前设置项
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private List<ALLSettingBean> settingList;

    private int user_type = KEFU_USER;

    @Override
    protected int getContentView() {
        return R.layout.layout_device_config;
    }

    @Override
    protected void initViews() {

        initToobar(toolbar);
        tvTitle.setText(R.string.android_key3091);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.inflateMenu(R.menu.comment_right_menu);
        item = toolbar.getMenu().findItem(R.id.right_action);
        item.setTitle(R.string.android_key816);
        toolbar.setOnMenuItemClickListener(this);
        String title = getIntent().getStringExtra("title");
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }

        rvSetting.setLayoutManager(new LinearLayoutManager(this));
        usParamsetAdapter = new DeviceSettingAdapter(new ArrayList<>(), this);
        int div = (int) getResources().getDimension(R.dimen.dp_1);
        GridDivider gridDivider = new GridDivider(ContextCompat.getColor(this, R.color.white), div, div);
        rvSetting.addItemDecoration(gridDivider);
        rvSetting.setAdapter(usParamsetAdapter);
        usParamsetAdapter.setOnItemClickListener(this);
        usParamsetAdapter.setOnItemChildClickListener(this);

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
        for (String[] strings : modelToal) {
            models.add(strings[1]);
        }
    }

    @Override
    protected void initData() {

        //快速设置项
        settingList
                = USConfigControl.getSettingList(USConfigControl.USSettingEnum.US_QUICK_SETTING, this);

        settingList.get(4).setValueStr(getString(R.string.android_key3045));

        user_type = ShineToosApplication.getContext().getUser_type();

        List<ALLSettingBean> newList = new ArrayList<>(settingList);

        if (user_type == KEFU_USER) {
            for (int i = 0; i < settingList.size(); i++) {
                if (i != 2) {
                    newList.add(settingList.get(i));
                }
            }
        }

        usParamsetAdapter.replaceData(newList);
        connetSocket();
    }

    private void connetSocket() {
        //初始化连接
        manager = new SocketManager(this);
        //设置连接监听
        manager.onConect(connectHandler);
        //开始连接TCP
        //延迟一下避免频繁操作
        new Handler().postDelayed(() -> manager.connectSocket(), 100);
    }


    ConnectHandler connectHandler = new ConnectHandler() {
        @Override
        public void connectSuccess() {
            getData(0);
        }

        @Override
        public void connectFail() {
            manager.disConnectSocket();
            MyControl.showJumpWifiSet(USFastConfigAcitivity.this);
        }

        @Override
        public void sendMsgFail() {
            manager.disConnectSocket();
            MyControl.showTcpDisConnect(USFastConfigAcitivity.this, getString(R.string.disconnet_retry),
                    () -> {
                        connetSocket();
                    }
            );
        }

        @Override
        public void sendMessage(String msg) {
            LogUtil.i("发送的消息:" + msg);
        }

        @Override
        public void receiveMessage(String msg) {
            LogUtil.i("接收的消息:" + msg);
        }

        @Override
        public void receveByteMessage(byte[] bytes) {
            if (type == 0) {
                //检测内容正确性
                boolean isCheck = ModbusUtil.checkModbus(bytes);
                if (isCheck) {
                    //接收正确，开始解析
                    parseMax(bytes);
                }
                switch (uuid) {
                    case 0:
                        getData(1);
                        break;
                    case 1:
                        getData(2);
                        break;
                    case 2://获取功率采集器
                        getData(5);
                        break;
                    case 5:
                        Mydialog.Dismiss();
                        break;
                }
            } else {//设置
                //检测内容正确性
                boolean isCheck = MaxUtil.checkReceiverFull(bytes);
                if (settingIndex == 2) {
                    isCheck = MaxUtil.checkReceiverFull10(bytes);
                }
                if (isCheck) {
                    Mydialog.Dismiss();
                    settingIndex = 0;
                    toast(R.string.android_key121);
                } else {
                    Mydialog.Dismiss();
                    toast(R.string.android_key3129);
                }
            }
        }
    };


    private void getData(int pos) {
        type = 0;
        uuid = pos;
        List<ALLSettingBean> data = usParamsetAdapter.getData();
        if (data.size() > pos) {
            ALLSettingBean bean = data.get(pos);
            int[] funs = bean.getFuns();
            manager.sendMsg(funs);
        }
    }


    /**
     * 根据传进来的mtype解析数据
     *
     * @param bytes
     */
    private void parseMax(byte[] bytes) {
        //移除外部协议
        byte[] bs = RegisterParseUtil.removePro17(bytes);
        //解析int值
        switch (uuid) {
            case 0://配网状态
                LogUtil.i("配网状态");
                //解析int值
                int value0 = MaxWifiParseUtil.obtainValueOne(bs);
                String status = "";
                if (value0 == 4) {
                    status = getString(R.string.already_equipped);
                } else {
                    status = getString(R.string.m未配网);
                }
                settingList.get(0).setValueStr(status);
                break;
            case 1://功率采集器
                //解析int值
                LogUtil.i("功率采集器");
                //解析int值
                int value2 = MaxWifiParseUtil.obtainValueOne(bs);
                //更新ui
                ALLSettingBean allSettingBean = settingList.get(1);
                String[] items = allSettingBean.getItems();
                String value = String.valueOf(value2);
                if (value2 < items.length) {
                    value = items[value2];
                }
                settingList.get(1).setValueStr(value);
                break;
            case 2://市电码
                LogUtil.i("市电码");
                //识别model
                byte[] valueBs = MaxWifiParseUtil.subBytesFull(bs, 118, 0, 4);
                ALLSettingBean allSettingBean2 = settingList.get(2);
                int[] setValues2 = allSettingBean2.getSetValues();
                int[] setValues = Arrays.copyOf(setValues2, setValues2.length);

                setValues[0] = MaxWifiParseUtil.obtainValueOne(bs, 118);
                setValues[1] = MaxWifiParseUtil.obtainValueOne(bs, 119);
                setValues[2] = MaxWifiParseUtil.obtainValueOne(bs, 120);
                setValues[3] = MaxWifiParseUtil.obtainValueOne(bs, 121);


                BigInteger big = new BigInteger(1, valueBs);
                long bigInteger = big.longValue();
                String modelS = MaxUtil.getDeviceModelSingleNew(bigInteger, 16) + MaxUtil.getDeviceModelSingleNew(bigInteger, 15);
                String readModel = String.format("S%s", modelS);
                //电压U位
                //解析120寄存器  U 电压 0-2bit
                int readVol = MaxWifiParseUtil.obtainValueOne(bs, 120) & 0x0007;
                //匹配
                boolean isFlag = false;
                for (int i = 0; i < modelToal.length; i++) {
                    String[] model = modelToal[i];
                    if (model[0].equals(readModel) && model[2].equals(String.valueOf(readVol))) {
                        isFlag = true;
                        modelPos = i;
                        allSettingBean2.setValueStr(model[1]);
                        ALLSettingBean allSettingBean3 = settingList.get(3);
                        allSettingBean3.setValueStr(model[3]);
                        break;
                    }
                }
                if (!isFlag) {
                    modelPos = -1;
                    allSettingBean2.setValueStr("");
                }

                //识别时间
                //解析int值
                int year = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 45, 0, 1));
                int month = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 46, 0, 1));
                int day = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 47, 0, 1));
                int hour = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 48, 0, 1));
                int min = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 49, 0, 1));
                int seconds = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 50, 0, 1));
                //更新ui
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
                ALLSettingBean allSettingBean7 = settingList.get(7);
                allSettingBean7.setValueStr(sb.toString());
                break;
            case 5://EMS
                //解析int值
                LogUtil.i("EMS");
                //解析int值
                int value5 = MaxWifiParseUtil.obtainValueOne(bs);
                //更新ui
                ALLSettingBean allSettingBean5 = settingList.get(5);
                String[] items5 = allSettingBean5.getItems();
                String value51 = String.valueOf(value5);
                if (value5 < items5.length) {
                    value51 = items5[value5];
                }
                allSettingBean5.setValueStr(value51);
                break;

            case 6:
                //解析int值
                LogUtil.i("电池诊断");
                //解析int值
                int value6 = MaxWifiParseUtil.obtainValueOne(bs);
                if (value6 == 0) {//提示异常
                    MyToastUtils.toast(R.string.no_battery_installed);
                } else {
                    ALLSettingBean allSettingBean1 = settingList.get(6);
                    String title = allSettingBean1.getTitle();
                    String hint = getString(R.string.input_battery_num);
                    CircleDialogUtils.showInputValueDialog(this, title,
                            hint, "", value1 -> {

                            });
                }
                break;

        }

        List<ALLSettingBean> newList = new ArrayList<>(settingList);
        if (user_type == KEFU_USER) {
            for (int i = 0; i < settingList.size(); i++) {
                if (i != 2) {
                    newList.add(settingList.get(i));
                }
            }
        }
        usParamsetAdapter.replaceData(newList);
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.right_action) {
            getData(0);
        }
        return true;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        ALLSettingBean allSettingBean = usParamsetAdapter.getData().get(position);
        int uuid = allSettingBean.getUuid();
        switch (uuid) {
            case 0://wifi配置
                toOhterSetting = true;
                ActivityUtils.gotoActivity(this, USWiFiConfigActivity.class, false);
                break;
            case 1://功率采集器
                setSelectItem(position);
                break;
            case 2://市电码
                new CircleDialog.Builder()
                        .setTitle(getString(R.string.android_key499))
                        .setWidth(0.7f)
                        .setGravity(Gravity.CENTER)
                        .setMaxHeight(0.5f)
                        .setItems(models, (parent, view1, pos, id) -> {
                            modelPos = pos;
                            usParamsetAdapter.getData().get(2).setValueStr(models.get(pos));
                            //设置电压
                            usParamsetAdapter.getData().get(3).setValueStr(modelToal[pos][3]);
                            usParamsetAdapter.notifyDataSetChanged();
                            setModelRegistValue();
                            return true;
                        })
                        .setNegative(getString(R.string.all_no), null)
                        .show(getSupportFragmentManager());
                break;
            case 5://ems
//                setSelectItem(position);
                break;

            case 6://电池
                getBdcStatus(6);
                break;

            case 7://时间

                break;
        }
    }


    private void getBdcStatus(int pos) {
        type = 0;
        uuid = pos;
        List<ALLSettingBean> data = usParamsetAdapter.getData();
        if (data.size() > pos) {
            ALLSettingBean bean = data.get(pos);
            int[] funs = bean.getFuns();
            manager.sendMsg(funs);
        }
    }


    /**
     * 设置市电码
     */
    private void setModelRegistValue() {
        String[] selectModels = modelToal[modelPos];
        ALLSettingBean allSettingBean = usParamsetAdapter.getData().get(2);
        int[] setValues = allSettingBean.getSetValues();
        int[] ints = Arrays.copyOf(setValues, setValues.length);
        int sModel = Integer.parseInt(selectModels[4], 16) << 8 | (ints[0] & 0x00FF);
        int uModel = Integer.parseInt(selectModels[2]) | ((ints[2] & 0b1111111111111000));
        ints[0] = sModel;
        ints[2] = uModel;
        settingIndex = 2;
        type = 1;
        int[] funSet = allSettingBean.getFunSet();
        manager.sendMsgToServer10(funSet, ints);
    }


    /**
     * 设置选择项
     */
    private void setSelectItem(int position) {
        List<ALLSettingBean> data = usParamsetAdapter.getData();
        if (data.size() > position) {
            ALLSettingBean bean = data.get(position);
            String[] items = bean.getItems();
            List<String> selects = new ArrayList<>(Arrays.asList(items));

            new CircleDialog.Builder()
                    .setTitle(getString(R.string.android_key499))
                    .setWidth(0.7f)
                    .setGravity(Gravity.CENTER)
                    .setMaxHeight(0.5f)
                    .setItems(selects, (parent, view1, pos, id) -> {
                        usParamsetAdapter.getData().get(position).setValueStr(selects.get(pos));
                        usParamsetAdapter.notifyDataSetChanged();
                        type = 1;
                        settingIndex = position;
                        int[] funs = bean.getFunSet();
                        funs[2] = pos;
                        manager.sendMsg(funs);
                        return true;
                    })
                    .setNegative(getString(R.string.all_no), null)
                    .show(getSupportFragmentManager());
        }

    }

    //设置时间
    private void setSystemTime() {
        ALLSettingBean allSettingBean = usParamsetAdapter.getData().get(7);
        String valueStr = allSettingBean.getValueStr();
        if (!TextUtils.isEmpty(valueStr)) {
            //设置时间参数
            try {
                Date date = sdf.parse(valueStr);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                int nowSecond = calendar.get(Calendar.SECOND);


                type = 1;

                int[] setValues = allSettingBean.getSetValues();
                int[] setfuns = Arrays.copyOf(setValues, setValues.length);

                setfuns[0] = year > 2000 ? year - 2000 : year;
                setfuns[1] = month;
                ;
                setfuns[2] = dayOfMonth;
                setfuns[3] = hourOfDay;
                setfuns[4] = minute;
                setfuns[5] = nowSecond;

                //更新ui
                StringBuilder sb = new StringBuilder()
                        .append(year).append("-");
                if (month < 10) {
                    sb.append("0");
                }
                sb.append(month).append("-");
                if (dayOfMonth < 10) {
                    sb.append("0");
                }
                sb.append(dayOfMonth).append(" ");
                if (hourOfDay < 10) {
                    sb.append("0");
                }
                sb.append(hourOfDay).append(":");
                if (minute < 10) {
                    sb.append("0");
                }
                sb.append(minute).append(":");
                if (nowSecond < 10) {
                    sb.append("0");
                }
                sb.append(nowSecond);
                usParamsetAdapter.getData().get(7).setValueStr(sb.toString());
                usParamsetAdapter.notifyDataSetChanged();

                type = 1;
                int[] funSet = allSettingBean.getFunSet();
                manager.sendMsgToServer10(funSet, setfuns);


            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        ALLSettingBean bean = usParamsetAdapter.getData().get(position);
        switch (view.getId()) {
            case R.id.tv_title:
                if (bean.getItemType() == UsSettingConstant.SETTING_TYPE_EXPLAIN) {
                    String title = bean.getTitle();
                    String content = getString(R.string.ems_explain);
                    CircleDialogUtils.showExplainDialog(USFastConfigAcitivity.this, title, content,
                            new CircleDialogUtils.OndialogClickListeners() {
                                @Override
                                public void buttonOk() {
                                }

                                @Override
                                public void buttonCancel() {
                                }
                            });
                }
                break;

        }
    }


    @Override
    public void oncheck(boolean check, int position) {

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (toOhterSetting) {
            toOhterSetting = false;
            connetSocket();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        manager.disConnectSocket();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
