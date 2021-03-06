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
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.module.localbox.max.bean.ALLSettingBean;
import com.growatt.shinetools.module.localbox.ustool.USWiFiConfigActivity;
import com.growatt.shinetools.socket.ConnectHandler;
import com.growatt.shinetools.socket.SocketManager;
import com.growatt.shinetools.utils.ActivityUtils;
import com.growatt.shinetools.utils.BatteryCheckDialogUtils;
import com.growatt.shinetools.utils.CircleDialogUtils;
import com.growatt.shinetools.utils.CommenUtils;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.MyControl;
import com.growatt.shinetools.utils.MyToastUtils;
import com.growatt.shinetools.utils.Mydialog;
import com.growatt.shinetools.widget.GridDivider;
import com.mylhyl.circledialog.BaseCircleDialog;
import com.mylhyl.circledialog.CircleDialog;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

import static com.growatt.shinetools.constant.GlobalConstant.END_USER;
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
    private int uuid = 0;//???????????????
    private int setIndex = 0;
    private int type = 0;//0?????????  1?????????
    private List<String> models;

    //?????????????????????
    private boolean toOhterSetting = false;


    private String[][] modelToal;
    private int modelPos = -1;//??????model??????

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private List<ALLSettingBean> settingList;

    private int user_type = KEFU_USER;
    private BaseCircleDialog startDialog;
    private int inputNum = 0;
    private int bdcNum = 0;

    private int batter_4041 = 0;
    private int batter_4149 = 0;


    //???????????????   ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
//    private int backFlow;


    //
    private char[] reverse = new char[]{
            '0', '0', '0', '0',
            '0', '0', '0', '0',
            '0', '0', '0', '0',
            '0', '0', '0', '0'};

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

        //???????????????
        settingList
                = USConfigControl.getSettingList(USConfigControl.USSettingEnum.US_QUICK_SETTING, this);

        settingList.get(5).setValueStr(getString(R.string.android_key3045));

        user_type = ShineToosApplication.getContext().getUser_type();

        List<ALLSettingBean> newList = new ArrayList<>(settingList);

        if (user_type == END_USER) {
            newList.clear();
            for (int i = 0; i < settingList.size(); i++) {
                if (i != 3) {
                    newList.add(settingList.get(i));
                }
            }
        }

        usParamsetAdapter.replaceData(newList);
        connetSocket();
    }

    private void connetSocket() {
        //???????????????
        manager = new SocketManager(this);
        //??????????????????
        manager.onConect(connectHandler);
        //????????????TCP
        //??????????????????????????????
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
        public void socketClose() {

        }

        @Override
        public void sendMessage(String msg) {
            LogUtil.i("???????????????:" + msg);
        }

        @Override
        public void receiveMessage(String msg) {
            LogUtil.i("???????????????:" + msg);
        }

        @Override
        public void receveByteMessage(byte[] bytes) {
            if (type == 0) {
                //?????????????????????
                boolean isCheck = ModbusUtil.checkModbus(bytes);
                if (isCheck) {
                    //???????????????????????????
                    parseMax(bytes);
                } else {
                    if (uuid == 3198 || uuid == 4041 || uuid == 4149) {
                        if (startDialog != null) {
                            startDialog.dialogDismiss();
                            startDialog = null;
                        }
                    }
                }
                switch (uuid) {
                    case 0://WIFI??????
                        getData(1);
                        break;
                    case 1://???????????????
                        getData(2);
                        break;
                    case 2://????????????
                        getData(3);
                        break;
                    case 3:
                        getData(5);
                        break;
                    case 5:
                        getData(6);
                        break;
                    case 6:
                        getData(7);
                        break;
                    case 7:
                    case 8:
                        Mydialog.Dismiss();
                        break;
                    case 4041:
                        get4149Num();
                        break;
                }
            } else {//??????
                //?????????????????????
                boolean isCheck = MaxUtil.checkReceiverFull(bytes);
                if (setIndex == 3) {
                    isCheck = MaxUtil.checkReceiverFull10(bytes);
                }
                if (isCheck) {
                    Mydialog.Dismiss();
                    toast(R.string.android_key121);
                } else {
                    Mydialog.Dismiss();
                    toast(R.string.android_key3129);
                }
            }
        }
    };


    private void getData(int id) {
        type = 0;
        uuid = id;
        ALLSettingBean bean = settingList.get(id);
        int[] funs = bean.getFuns();
        manager.sendMsg(funs);
    }


    /**
     * ??????????????????mtype????????????
     *
     * @param bytes
     */
    private void parseMax(byte[] bytes) {
        //??????????????????
        byte[] bs = RegisterParseUtil.removePro17(bytes);
        //??????int???
        switch (uuid) {
            case 0://????????????
                LogUtil.i("????????????");
                //??????int???
                int value0 = MaxWifiParseUtil.obtainValueOne(bs);
                String status = "";
                if (value0 == 4) {
                    status = getString(R.string.already_equipped);
                } else {
                    status = getString(R.string.m?????????);
                }
//                settingList.get(0).setValueStr("");
                settingList.get(0).setValueStr("");
                break;
            case 1://???????????????
                //??????int???
                LogUtil.i("???????????????");
                //??????int???
                int value2 = MaxWifiParseUtil.obtainValueOne(bs);
                //??????ui
                ALLSettingBean allSettingBean = settingList.get(1);
                String[] items = allSettingBean.getItems();
                String value = String.valueOf(value2);
                if (value2 < items.length) {
                    value = items[value2];
                }
                settingList.get(1).setValueStr(value);
                break;


            case 2://????????????
                //??????int???
                LogUtil.i("????????????");
                //??????int???
                int value_2 = MaxWifiParseUtil.obtainValueOne(bs);
                //??????ui
                ALLSettingBean allSettingBean_2 = settingList.get(2);
                String[] items_2 = allSettingBean_2.getItems();
                String value22 = String.valueOf(value_2);
                if (value_2 < items_2.length) {
                    value22 = items_2[value_2];
                }
                settingList.get(2).setValueStr(value22);
                break;


            case 3://?????????
                LogUtil.i("?????????");
                //??????model
                byte[] valueBs = MaxWifiParseUtil.subBytesFull(bs, 118, 0, 4);
                ALLSettingBean allSettingBean2 = settingList.get(3);
//                int[] setValues2 = allSettingBean2.getSetValues();
                int[] setValues = new int[4];

                setValues[0] = MaxWifiParseUtil.obtainValueOne(bs, 118);
                setValues[1] = MaxWifiParseUtil.obtainValueOne(bs, 119);
                setValues[2] = MaxWifiParseUtil.obtainValueOne(bs, 120);
                setValues[3] = MaxWifiParseUtil.obtainValueOne(bs, 121);
                allSettingBean2.setSetValues(setValues);


//                backFlow = MaxWifiParseUtil.obtainValueOne(bs, 122);


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
                        allSettingBean2.setValueStr(model[1]);
                        ALLSettingBean allSettingBean3 = settingList.get(4);
                        allSettingBean3.setValueStr(model[3]);
                        break;
                    }
                }
                if (!isFlag) {
                    modelPos = -1;
                    allSettingBean2.setValueStr("");
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
                ALLSettingBean allSettingBean7 = settingList.get(9);
                allSettingBean7.setValueStr(sb.toString());
                break;

            case 6://AC couple????????????
                LogUtil.i("AC couple????????????");
                //??????int???
                int value5 = MaxWifiParseUtil.obtainValueOne(bs);
                //??????bit11
                String s = CommenUtils.intToBinary(value5, 16);
                char[] chars = s.toCharArray();
                LogUtil.i("???????????????" + Arrays.toString(chars));
                int len = chars.length;
                reverse = CommenUtils.reverse(chars, len);
                String b11 = String.valueOf(reverse[11]);
                settingList.get(6).setValue(b11);
                break;


            case 7://EMS
                //??????int???
                LogUtil.i("EMS");
                //??????int???
                int value6 = MaxWifiParseUtil.obtainValueOne(bs);
                //??????ui
                ALLSettingBean allSettingBean5 = settingList.get(7);
                String[] items5 = allSettingBean5.getItems();
                String value51 = String.valueOf(value6);
                if (value6 < items5.length) {
                    value51 = items5[value6];
                }
                allSettingBean5.setValueStr(value51);
                break;

            case 8:
                //??????int???
                LogUtil.i("????????????");
                //??????int???
                int value7 = MaxWifiParseUtil.obtainValueOne(bs);
//                int value6 = 3;
                bdcNum = value7;
                if (value7 == 0) {//????????????
                    MyToastUtils.toast(R.string.no_battery_installed);
                } else {
                    ALLSettingBean allSettingBean1 = settingList.get(8);
                    String title = allSettingBean1.getTitle();
                    String hint = getString(R.string.input_battery_num);
                    CircleDialogUtils.showInputValueDialog(this, title,
                            hint, "", value1 -> {
                                if (TextUtils.isEmpty(value1)) {
                                    return;
                                }
                                inputNum = Integer.parseInt(value1);
                                startDialog = BatteryCheckDialogUtils.showCheckStartDialog(this, title);
                                if (value7 == 1 || value7 == 2) {
                                    get3198Num();
                                } else if (value7 == 3) {
                                    get4041Num();
                                }
                            });


                }
                break;


            case 3198:
                if (startDialog != null) {
                    startDialog.dialogDismiss();
                    startDialog = null;
                }
                //??????int???
                LogUtil.i("??????3198");
                //??????int???
                ALLSettingBean allSettingBean1 = settingList.get(7);
                String title = allSettingBean1.getTitle();
                int value3198 = MaxWifiParseUtil.obtainValueOne(bs);
                if (value3198 == inputNum) {//??????
                    BatteryCheckDialogUtils.showCheckSuccessDialog(this, title);
                } else {//??????
                    String tips1 = getString(R.string.battery_install_error);
                    if (bdcNum == 1) {
                        tips1 += "(BDC1:" + value3198 + ")";
                    } else {
                        tips1 += "(BDC2:" + value3198 + ")";
                    }

                    String tips2 = "1." + getString(R.string.battery_error_tips1) + "\n" + "2." + getString(R.string.battery_error_tips2);
                    BatteryCheckDialogUtils.showCheckErrorDialog(this, title, tips1, tips2,
                            () -> {
                                getBdcStatus(8);
                            });
                }
                break;

            case 4041:
                LogUtil.i("??????4041");
                //??????int???
                batter_4041 = MaxWifiParseUtil.obtainValueOne(bs);
                break;

            case 4149:
                batter_4149 = MaxWifiParseUtil.obtainValueOne(bs);
                if (startDialog != null) {
                    startDialog.dialogDismiss();
                    startDialog = null;
                }
                ALLSettingBean allSettingBean4149 = settingList.get(8);
                String title4149 = allSettingBean4149.getTitle();
                if (batter_4149 == inputNum && inputNum == batter_4041) {//??????
                    BatteryCheckDialogUtils.showCheckSuccessDialog(this, title4149);
                } else {//??????
                    String tips1 = getString(R.string.battery_install_error) + "(BDC1:" + batter_4041 + "  "
                            + "BDC2:" + batter_4149 + ")";

                    String tips2 = "1." + getString(R.string.battery_error_tips1) + "\n" + "2." + getString(R.string.battery_error_tips2);
                    BatteryCheckDialogUtils.showCheckErrorDialog(this, title4149, tips1, tips2,
                            () -> {
                                getBdcStatus(8);
                            });
                }


                break;

        }

        List<ALLSettingBean> newList = new ArrayList<>(settingList);
        if (user_type == END_USER) {
            newList.clear();
            for (int i = 0; i < settingList.size(); i++) {
                if (i != 3) {
                    newList.add(settingList.get(i));
                }
            }
        }
        usParamsetAdapter.replaceData(newList);
    }


    /**
     * ??????3198
     */
    private void get3198Num() {
        uuid = 3198;
        type = 0;
        int[] funs = new int[]{4, 3198, 3198};
        manager.sendMsgNoDialog(funs);
    }


    /**
     * ??????4041
     */
    private void get4041Num() {
        uuid = 4041;
        type = 0;
        int[] funs = new int[]{4, 4041, 4041};
        manager.sendMsgNoDialog(funs);
    }


    /**
     * ??????3198
     */
    private void get4149Num() {
        uuid = 4149;
        type = 0;
        int[] funs = new int[]{4, 4149, 4149};
        manager.sendMsgNoDialog(funs);
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
        setIndex = position;
        switch (uuid) {
            case 0://wifi??????
                toOhterSetting = true;
                ActivityUtils.gotoActivity(this, USWiFiConfigActivity.class, false);
                break;
            case 1:
            case 2://???????????????
                setSelectItem(position);
                break;

            case 3://?????????
                new CircleDialog.Builder()
                        .setTitle(getString(R.string.android_key499))
                        .setWidth(0.7f)
                        .setGravity(Gravity.CENTER)
                        .setMaxHeight(0.5f)
                        .setItems(models, (parent, view1, pos, id) -> {
                            modelPos = pos;
                            int i2 = uuidIndex(2);
                            usParamsetAdapter.getData().get(i2).setValueStr(models.get(pos));
                            //????????????
                            int i3 = uuidIndex(3);
                            usParamsetAdapter.getData().get(i3).setValueStr(modelToal[pos][3]);
                            usParamsetAdapter.notifyDataSetChanged();
                            setModelRegistValue();
                            return true;
                        })
                        .setNegative(getString(R.string.all_no), null)
                        .show(getSupportFragmentManager());
                break;


            case 7://ems
//                setSelectItem(position);
                break;

            case 8://??????
                getBdcStatus(8);
                break;

            case 9://??????

                break;
        }
    }


    private void getBdcStatus(int id) {
        type = 0;
        uuid = id;
        if (settingList.size() > id) {
            ALLSettingBean bean = settingList.get(id);
            int[] funs = bean.getFuns();
            manager.sendMsg(funs);
        }
    }


    /**
     * ???????????????
     */
    private void setModelRegistValue() {
        String[] selectModels = modelToal[modelPos];
        ALLSettingBean allSettingBean = settingList.get(3);
        int[] setValues = allSettingBean.getSetValues();
        int[] ints = Arrays.copyOf(setValues, setValues.length);
        int sModel = Integer.parseInt(selectModels[4], 16) << 8 | (ints[0] & 0x00FF);
        int uModel = Integer.parseInt(selectModels[2]) | ((ints[2] & 0b1111111111111000));
        ints[0] = sModel;
        ints[2] = uModel;
        type = 1;
        int[] funSet = allSettingBean.getFunSet();
        manager.sendMsgToServer10(funSet, ints);
    }


    /**
     * ???????????????
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
                        int[] funs = bean.getFunSet();
                        funs[2] = pos;
                        manager.sendMsg(funs);
                        return true;
                    })
                    .setNegative(getString(R.string.all_no), null)
                    .show(getSupportFragmentManager());
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
        int value = check ? 1 : 0;
        ALLSettingBean allSettingBean = usParamsetAdapter.getData().get(position);
        int uuid = allSettingBean.getUuid();
        allSettingBean.setValue(String.valueOf(value));
        allSettingBean.setValueStr(String.valueOf(value));
        type = 1;
        int[] funs = allSettingBean.getFunSet();
        if (uuid == 6) {
            reverse[11] = (char) (value + '0');
            ;
            //??????
            char[] reverse1 = CommenUtils.reverse(this.reverse, reverse.length);
            String value1 = String.valueOf(reverse1);
            value = Integer.valueOf(value1, 2);
        }
        funs[2] = value;
        manager.sendMsg(funs);
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
        toOhterSetting = true;
        manager.disConnectSocket();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    /*
    ???????????????????????????????????????
     */
    private int uuidIndex(int uuid) {
        int index = 0;
        List<ALLSettingBean> data = usParamsetAdapter.getData();
        for (int i = 0; i < data.size(); i++) {
            ALLSettingBean allSettingBean = data.get(i);
            int uuid1 = allSettingBean.getUuid();
            if (uuid == uuid1) {
                index = i;
                break;
            }
        }
        return index;

    }

}
