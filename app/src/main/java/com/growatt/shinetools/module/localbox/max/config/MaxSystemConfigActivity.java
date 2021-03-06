package com.growatt.shinetools.module.localbox.max.config;

import android.content.Intent;
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
import com.growatt.shinetools.adapter.DeviceSettingAdapter;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.modbusbox.Arith;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.MaxWifiParseUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.module.localbox.configtype.ConfigType2Activity;
import com.growatt.shinetools.module.localbox.max.bean.ALLSettingBean;
import com.growatt.shinetools.socket.ConnectHandler;
import com.growatt.shinetools.socket.SocketManager;
import com.growatt.shinetools.utils.ActivityUtils;
import com.growatt.shinetools.utils.CircleDialogUtils;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.MyControl;
import com.growatt.shinetools.utils.Mydialog;
import com.growatt.shinetools.widget.GridDivider;
import com.mylhyl.circledialog.CircleDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

public class MaxSystemConfigActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener,
        DeviceSettingAdapter.OnChildCheckLiseners, Toolbar.OnMenuItemClickListener {
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

    private DeviceSettingAdapter usParamsetAdapter;
    private MenuItem item;
    private int currentPos = 0;//???????????????
    private int type = 0;//0?????????  1?????????
    private SocketManager manager;

    //?????????????????????
    private boolean toOhterSetting = false;

    @Override
    protected int getContentView() {
        return R.layout.activity_max_system_config;
    }

    @Override
    protected void initViews() {
        initToobar(toolbar);
        tvTitle.setText(R.string.android_key3091);
        toolbar.inflateMenu(R.menu.comment_right_menu);
        item = toolbar.getMenu().findItem(R.id.right_action);
        item.setTitle(R.string.android_key816);
        toolbar.setOnMenuItemClickListener(this);


        rvSystem.setLayoutManager(new LinearLayoutManager(this));
        usParamsetAdapter = new DeviceSettingAdapter(new ArrayList<>(), this);
        int div = (int) getResources().getDimension(R.dimen.dp_1);
        GridDivider gridDivider = new GridDivider(ContextCompat.getColor(this, R.color.white), div, div);
        rvSystem.addItemDecoration(gridDivider);
        rvSystem.setAdapter(usParamsetAdapter);
        usParamsetAdapter.setOnItemClickListener(this);


        String title = getIntent().getStringExtra("title");
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }
    }

    @Override
    protected void initData() {
        //???????????????
        List<ALLSettingBean> settingList
                = MaxConfigControl.getSettingList(MaxConfigControl.MaxSettingEnum.MAX_SYSTEM_SETTING, this);
        usParamsetAdapter.replaceData(settingList);

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
            getOnOff();
        }

        @Override
        public void connectFail() {
            manager.disConnectSocket();
            MyControl.showJumpWifiSet(MaxSystemConfigActivity.this);
        }

        @Override
        public void sendMsgFail() {
            manager.disConnectSocket();
            MyControl.showTcpDisConnect(MaxSystemConfigActivity.this, getString(R.string.disconnet_retry),
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
                }
                if (currentPos < usParamsetAdapter.getData().size() - 1) {
                    getData(++currentPos);
                }else {
                    Mydialog.Dismiss();
                }

            } else {//??????
                //?????????????????????
                boolean isCheck = MaxUtil.checkReceiverFull(bytes);
                if (isCheck) {
                    Mydialog.Dismiss();
                    toast(R.string.all_success);
                } else {
                    Mydialog.Dismiss();
                    toast(R.string.android_key3129);
                }
            }
        }
    };


    /**
     * ??????????????????mtype????????????
     *
     * @param bytes
     */
    private void parseMax(byte[] bytes) {
        //??????????????????
        byte[] bs = RegisterParseUtil.removePro17(bytes);
        //??????int???
        switch (currentPos) {
            case 0://???????????????
                //??????int???
                int value = MaxWifiParseUtil.obtainValueOne(bs);
                LogUtil.i("?????????????????????:"+value);
                usParamsetAdapter.getData().get(0).setValue(String.valueOf(value));
                break;
            case 1://??????????????????
                //??????int???
                int value0 = MaxWifiParseUtil.obtainValueOne(bs);
                LogUtil.i("????????????????????????:"+value0);
                parserSafetyEnable(value0);
                break;
            case 2://?????????????????????
                //??????int???
                int content1 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3, 0, 1));
                LogUtil.i("???????????????????????????:"+content1);
                parserActiviPercent(content1);
                break;
            case 3://ISlland??????
                //??????int???
                int value3 = MaxWifiParseUtil.obtainValueOne(bs);
                LogUtil.i("??????ISlland??????:"+value3);
                usParamsetAdapter.getData().get(3).setValue(String.valueOf(value3));
                break;
            case 4://????????????
                //??????int???
                int value4 = MaxWifiParseUtil.obtainValueOne(bs);
                LogUtil.i("??????????????????:"+value4);
                usParamsetAdapter.getData().get(4).setValue(String.valueOf(value4));
                break;
            case 5://??????N?????????
                //??????int???
                int value5 = MaxWifiParseUtil.obtainValueOne(bs);
                LogUtil.i("????????????N?????????:"+value5);
                usParamsetAdapter.getData().get(5).setValue(String.valueOf(value5));
                break;
            case 6://N???PE??????????????????
                //??????int???
                int value6 = MaxWifiParseUtil.obtainValueOne(bs);
                LogUtil.i("??????N???PE??????????????????:"+value6);
                usParamsetAdapter.getData().get(6).setValue(String.valueOf(value6));
                break;
            case 7://???????????????????????????
                //??????int???
                int value7 = MaxWifiParseUtil.obtainValueOne(bs);
                LogUtil.i("?????????????????????????????????:"+value7);
                ALLSettingBean bean7 = usParamsetAdapter.getData().get(7);
                bean7.setValue(String.valueOf(value7));
                bean7.setValueStr(getReadValueReal(7,value7));
                break;
            case 8://????????????????????????
                int value8 = MaxWifiParseUtil.obtainValueOne(bs);
                LogUtil.i("??????????????????????????????:"+value8);
                usParamsetAdapter.getData().get(8).setValue(String.valueOf(value8));
                break;
            case 9://pv????????????
                int value9 = MaxWifiParseUtil.obtainValueOne(bs);
                LogUtil.i("??????pv????????????:"+value9);
                ALLSettingBean bean9 = usParamsetAdapter.getData().get(9);
                bean9.setValue(String.valueOf(value9));
                bean9.setValueStr(getReadValueReal(9,value9));
                break;
            case 10://????????????
                LogUtil.i("??????????????????");
                break;
            case 11://GPRS/4G/PLC??????
                int value11 = MaxWifiParseUtil.obtainValueOne(bs);
                LogUtil.i("??????GPRS/4G/PLC??????:"+value11);
                usParamsetAdapter.getData().get(11).setValue(String.valueOf(value11));
                usParamsetAdapter.getData().get(11).setValueStr(String.valueOf(value11));
                break;
            case 12://??????SVG????????????
                int value12 = MaxWifiParseUtil.obtainValueOne(bs);
                LogUtil.i("??????SVG????????????:"+value12);
                ALLSettingBean bean12 = usParamsetAdapter.getData().get(12);
                bean12.setValue(String.valueOf(bean12));
                bean12.setValueStr(getReadValueReal(12,value12));
                break;
            case 13://PID????????????
                int value13 = MaxWifiParseUtil.obtainValueOne(bs);
                LogUtil.i("??????PID????????????:"+value13);
                ALLSettingBean bean13 = usParamsetAdapter.getData().get(13);
                bean13.setValue(String.valueOf(bean13));
                bean13.setValueStr(getReadValueReal(13,value13));
                break;
            case 14://PID??????
                int value14 = MaxWifiParseUtil.obtainValueOne(bs);
                LogUtil.i("??????PID??????:"+value14);
                usParamsetAdapter.getData().get(14).setValue(String.valueOf(value14));
                break;
            case 15://PID??????????????????
                //??????int???
                int value15 = MaxWifiParseUtil.obtainValueOne(bs);
                LogUtil.i("??????PID??????????????????:"+value15);
                paserPidVoltgeSelect(value15);
                break;
        }
        usParamsetAdapter.notifyDataSetChanged();
    }

    /**
     * ??????????????????????????????
     */
    private void getOnOff() {
        getData(0);
    }


    private void getData(int pos) {
        type = 0;
        currentPos = pos;
        List<ALLSettingBean> data = usParamsetAdapter.getData();
        if (data.size() > pos) {
            ALLSettingBean bean = data.get(pos);
            LogUtil.i("-------------------????????????:"+bean.getTitle()+"----------------");
            int[] funs = bean.getFuns();
            manager.sendMsg(funs);
        }
    }


    public void parserSafetyEnable(int read) {
        ALLSettingBean bean = usParamsetAdapter.getData().get(1);
        bean.setValueStr(getReadValueReal(1,read));

    }

    public void parserActiviPercent(int read) {
        ALLSettingBean bean = usParamsetAdapter.getData().get(2);
        bean.setValueStr(String.valueOf(read));
    }


    public void paserPidVoltgeSelect(int read) {
        ALLSettingBean bean = usParamsetAdapter.getData().get(15);
        bean.setValueStr(String.valueOf(read));
    }


    public String getReadValueReal(int position,int read) {
        ALLSettingBean bean = usParamsetAdapter.getData().get(position);
        String[] items = bean.getItems();
        String value=String.valueOf(read);
        float mul=bean.getMul();
        String unit=bean.getUnit();
        switch (position){
            case 1:
                boolean isNum = ((int) mul) == mul;
                if (isNum) {
                    value = read * ((int) mul) + unit;
                } else {
                    value = Arith.mul(read, mul, 2) + unit;
                }
                break;
            case 7: case 9: case 12: case 13:
                if (read<items.length){
                    value = items[read];
                }
                break;
        }

        return value;
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.right_action) {
            getOnOff();
        }
        return true;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        ALLSettingBean bean = usParamsetAdapter.getData().get(position);
        String title = bean.getTitle();
        String hint = bean.getHint();
        switch (position){
            case 10:
                toOhterSetting=true;
                manager.disConnectSocket();
                Intent intent = new Intent(mContext, ConfigType2Activity.class);
                intent.putExtra("type", 5);
                intent.putExtra("title", String.format("%s%s",title,""));
                startActivity(intent);
                break;
            case 11:

                break;

            case 1: case 15:
                setInputValue(position,title,hint);
                break;
            case 7: case 9:  case 13:
                setSelectItem(position,title);
                break;

            case 2:
                toOhterSetting=true;
                manager.disConnectSocket();
                Intent intent1 = new Intent(this, MaxActivePowerActivity.class);
                intent1.putExtra("title", bean.getTitle());
                ActivityUtils.startActivity(this, intent1, false);
                break;

        }
    }



    private void setSelectItem(int pos,String title) {
        List<ALLSettingBean> data = usParamsetAdapter.getData();
        if (data.size() > pos) {
            ALLSettingBean bean = data.get(pos);
            String[] items = bean.getItems();
            List<String> selects = new ArrayList<>(Arrays.asList(items));

            new CircleDialog.Builder()
                    .setTitle(title)
                    .setWidth(0.7f)
                    .setGravity(Gravity.CENTER)
                    .setMaxHeight(0.5f)
                    .setItems(selects, (parent, view1, position, id) -> {
                        usParamsetAdapter.getData().get(pos).setValueStr(selects.get(position));
                        usParamsetAdapter.notifyDataSetChanged();
                        type = 1;
                        int[] funs = bean.getFunSet();
                        funs[2] = position;
                        manager.sendMsg(funs);
                        return true;
                    })
                    .setNegative(getString(R.string.all_no), null)
                    .show(getSupportFragmentManager());
        }

    }




    private void setInputValue(int pos,String title, String hint) {
        CircleDialogUtils.showInputValueDialog(this, title,
                hint, "", value -> {
                    double result = Double.parseDouble(value);
                    String pValue = value + "";
                    usParamsetAdapter.getData().get(pos).setValueStr(pValue);
                    usParamsetAdapter.getData().get(pos).setValue(String.valueOf(result));
                    usParamsetAdapter.notifyDataSetChanged();

                    List<ALLSettingBean> data = usParamsetAdapter.getData();
                    if (data.size() > pos) {
                        ALLSettingBean bean = data.get(pos);
                        //??????
                        type = 1;
                        int[] funs = bean.getFunSet();
                        funs[2] = getWriteValueReal(Double.parseDouble(value));
                        manager.sendMsg(funs);
                    }
                });
    }



    public int getWriteValueReal(double write) {
        try {
            return (int) Math.round(Arith.div(write, 1));
        } catch (Exception e) {
            e.printStackTrace();
            return (int) write;
        }
    }


    @Override
    public void oncheck(boolean check, int position) {
        ALLSettingBean bean = usParamsetAdapter.getData().get(position);
        int value = check ? 1 : 0;
        usParamsetAdapter.getData().get(position).setValue(String.valueOf(value));
        usParamsetAdapter.notifyDataSetChanged();
        type = 1;
        LogUtil.i("-------------------??????"+bean.getTitle()+"----------------");
        int[] funSet = bean.getFunSet();
        funSet[2]=value;
        manager.sendMsg(funSet);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (toOhterSetting){
            toOhterSetting=false;
            connetSocket();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        toOhterSetting=true;
        manager.disConnectSocket();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
