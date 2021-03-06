package com.growatt.shinetools.module.localbox.tlxh.config;

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
import com.growatt.shinetools.module.localbox.max.bean.ALLSettingBean;
import com.growatt.shinetools.module.localbox.max.config.MaxAFCIAcitivity;
import com.growatt.shinetools.module.localbox.mintool.TLXParamCountry2Activity;
import com.growatt.shinetools.module.localbox.tlx.config.QuickSettingSecondActivity;
import com.growatt.shinetools.module.localbox.tlxh.TLXHAutoTestOldInvActivity;
import com.growatt.shinetools.socket.ConnectHandler;
import com.growatt.shinetools.socket.SocketManager;
import com.growatt.shinetools.utils.ActivityUtils;
import com.growatt.shinetools.utils.CircleDialogUtils;
import com.growatt.shinetools.utils.DateUtils;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.MyControl;
import com.growatt.shinetools.utils.Mydialog;
import com.growatt.shinetools.widget.GridDivider;
import com.mylhyl.circledialog.CircleDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.BindView;

public class TLXHQuickSettingActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener,
        DeviceSettingAdapter.OnChildCheckLiseners, Toolbar.OnMenuItemClickListener {
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
    private int currentPos = 1;//???????????????
    private int type = 0;//0?????????  1?????????
    private List<int[]> nowSetItem = new ArrayList<int[]>();
    private int nowIndex = 0;
    private int deviceType;


    //?????????????????????
    private boolean toOhterSetting = false;


    @Override
    protected int getContentView() {
        return R.layout.activity_tlx_quick_set;
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
        if (!TextUtils.isEmpty(title)){
            tvTitle.setText(title);
        }

        rvSetting.setLayoutManager(new LinearLayoutManager(this));
        usParamsetAdapter = new DeviceSettingAdapter(new ArrayList<>(), this);
        int div = (int) getResources().getDimension(R.dimen.dp_1);
        GridDivider gridDivider = new GridDivider(ContextCompat.getColor(this, R.color.white), div, div);
        rvSetting.addItemDecoration(gridDivider);
        rvSetting.setAdapter(usParamsetAdapter);
        usParamsetAdapter.setOnItemClickListener(this);
    }

    @Override
    protected void initData() {
        deviceType=getIntent().getIntExtra("deviceType",0);
        //???????????????
        List<ALLSettingBean> settingList
                = TLXHConfigControl.getSettingList(TLXHConfigControl.TlxSettingEnum.TLXH_QUICK_SETTING, this);
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
        new Handler().postDelayed(() -> manager.connectSocket(),100);
    }


    ConnectHandler connectHandler = new ConnectHandler() {
        @Override
        public void connectSuccess() {
            getInvdate();
        }

        @Override
        public void connectFail() {
            manager.disConnectSocket();
            MyControl.showJumpWifiSet(TLXHQuickSettingActivity.this);
        }

        @Override
        public void sendMsgFail() {
            manager.disConnectSocket();
            MyControl.showTcpDisConnect(TLXHQuickSettingActivity.this, getString(R.string.disconnet_retry),
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
                switch (currentPos) {
                    case 1:
                        getLCDLanguge();
                        break;
                    case 2:
                        getAddress();
                        break;
                    case 3://?????????????????????
                        getDynamometer();
                        break;
                    case 4:
                        Mydialog.Dismiss();
                        break;
                }
            } else {//??????
                //?????????????????????
                boolean isCheck = MaxUtil.checkReceiverFull(bytes);
                if (isCheck) {
                    if (nowIndex < nowSetItem.size() - 1) {
                        manager.sendMsg(nowSetItem.get(++nowIndex));
                    } else {
                        nowSetItem.clear();
                        Mydialog.Dismiss();
                        nowIndex = 0;
                        toast(R.string.android_key121);
                    }

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
            case 1://???????????????
                LogUtil.i("????????????");
                parserTimeData(bs);
                break;
            case 2://LCD??????
                //??????int???
                LogUtil.i("????????????");
                int value0 = MaxWifiParseUtil.obtainValueOne(bs);
                parserLcdData(value0);
                break;
            case 3://????????????
                LogUtil.i("??????????????????");
                int value1 = MaxWifiParseUtil.obtainValueOne(bs);
                parserAddress(value1);
                break;
            case 4://???????????????
                LogUtil.i("?????????????????????");
                int value4 = MaxWifiParseUtil.obtainValueOne(bs);
                parserDynamometer(value4);
                break;

        }
        usParamsetAdapter.notifyDataSetChanged();
    }




    /**
     * ??????????????????????????????
     */
    private void getInvdate() {
        LogUtil.i("???????????????????????????");
        getData(1);
    }



    /**
     * ?????????????????????
     *
     * @param bs
     */
    private void parserTimeData(byte[] bs) {
        try {
            int year = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 0, 0, 1));
            int month = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 1, 0, 1));
            int day = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 2, 0, 1));
            int hour = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 3, 0, 1));
            int min = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 4, 0, 1));
            int seconds = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 5, 0, 1));
            //??????ui
            StringBuilder sb = new StringBuilder()
                    .append(year).append("-")
                    .append(month).append("-")
                    .append(day).append(" ")
                    .append(hour).append(":")
                    .append(min).append(":")
                    .append(seconds);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timeS = sb.toString();
            Date date = sdf.parse(timeS);
            String timeStr = sdf.format(date);
            usParamsetAdapter.getData().get(1).setValueStr(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    /**
     * ????????????LCD??????
     */
    private void getLCDLanguge() {
        LogUtil.i("??????????????????");
        getData(2);
    }


    /**
     * ????????????????????????????????????????????????
     *
     * @param value
     * @return
     */
    public void parserLcdData(int value) {
        List<ALLSettingBean> data = usParamsetAdapter.getData();
        if (data.size() > 2) {
            ALLSettingBean bean = data.get(2);
            String[] items = bean.getItems();
            if (items != null && items.length > value) {
                String lcd = items[value];
                usParamsetAdapter.getData().get(2).setValueStr(lcd);
            } else {
                usParamsetAdapter.getData().get(2).setValueStr(String.valueOf(value));
            }
        }

    }




    /**
     * ????????????????????????
     */
    private void getAddress() {
        LogUtil.i("????????????????????????");
        getData(3);
    }



    /**
     * ???????????????????????????
     */
    private void getDynamometer() {
        LogUtil.i("???????????????????????????");
        getData(4);
    }


    public void parserAddress(int read) {
        List<ALLSettingBean> data = usParamsetAdapter.getData();
        if (data.size() > 3) {
            ALLSettingBean bean = data.get(3);
            float mMul = bean.getMul();
            String unit = bean.getUnit();
            String value = read * ((int) mMul) + unit;
            usParamsetAdapter.getData().get(3).setValueStr(value);
        }

    }



    private void parserDynamometer(int read){
        List<ALLSettingBean> data = usParamsetAdapter.getData();
        if (data.size() > 4) {
            ALLSettingBean bean = data.get(4);
            String[] items = bean.getItems();
            if (read<items.length){
                String value=items[read];
                usParamsetAdapter.getData().get(4).setValueStr(value);
            }else {
                usParamsetAdapter.getData().get(4).setValueStr(String.valueOf(read));
            }


        }
    }




    private void getData(int pos) {
        type = 0;
        currentPos = pos;
        List<ALLSettingBean> data = usParamsetAdapter.getData();
        if (data.size() > pos) {
            ALLSettingBean bean = data.get(pos);
            int[] funs = bean.getFuns();
            manager.sendMsg(funs);
        }
    }



    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.right_action) {
            getInvdate();
        }
        return true;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

        ALLSettingBean bean = usParamsetAdapter.getData().get(position);
        switch (position) {
            case 0:
                manager.disConnectSocket();
                toOhterSetting=true;
                Intent intent = new Intent(mContext, TLXParamCountry2Activity.class);
                intent.putExtra("type", 0);
                intent.putExtra("title", String.format("%s%s", getString(R.string.m????????????), ""));
                ActivityUtils.startActivity(this, intent, false);
                break;
            case 1:
                setInvTime();
                break;
            case 2:
            case 4:
                setSelectItem(position);
                break;
            case 3:
                setInputValue(bean.getTitle(), bean.getHint());
                break;
            case 5:
                //????????????
                manager.disConnectSocket();
                toOhterSetting=true;
/*                Intent intent1 = new Intent(mContext, AFCIChartActivity.class);
                intent1.putExtra("type", 36);
                intent1.putExtra("title", String.format("%s%s",getString(R.string.AFCI????????????),""));
                ActivityUtils.startActivity(this,intent1,false);*/

                Intent intent1 = new Intent(mContext, MaxAFCIAcitivity.class);
                intent1.putExtra("type", 36);
                intent1.putExtra("title", bean.getTitle());
                ActivityUtils.startActivity(this, intent1, false);

                break;
            case 6://???????????????
                //????????????
                manager.disConnectSocket();
                toOhterSetting=true;
                Intent intent6 = new Intent(mContext, QuickSettingSecondActivity.class);
                intent6.putExtra("type", 0);
                intent6.putExtra("title", bean.getTitle());
                intent6.putExtra("deviceType",deviceType);
                ActivityUtils.startActivity(this, intent6, false);
                break;
            case 7://????????????
                //????????????
                manager.disConnectSocket();
                toOhterSetting=true;
                Intent intent7 = new Intent(mContext, TLXHAutoTestOldInvActivity.class);
                intent7.putExtra("type", 0);
                intent7.putExtra("title", bean.getTitle());
                intent7.putExtra("deviceType",deviceType);
                ActivityUtils.startActivity(this, intent7, false);
                break;

        }
    }

    @Override
    public void oncheck(boolean check, int position) {

    }


    /**
     * ?????????????????????
     */
    private void setInvTime() {
        try {
            DateUtils.showTotalTime(this, new DateUtils.SeletctTimeListeners() {
                @Override
                public void seleted(String date) {

                }

                @Override
                public void ymdHms(int year, int month, int day, int hour, int min, int second) {

                    List<ALLSettingBean> data = usParamsetAdapter.getData();
                    if (data.size() > 1) {
                        ALLSettingBean bean = data.get(1);
                        type = 1;
                        int[][] doubleFunset = bean.getDoubleFunset();
                        if (year > 2000) {
                            doubleFunset[0][2] = year - 2000;
                        } else {
                            doubleFunset[0][2] = year;
                        }
                        doubleFunset[1][2] = month + 1;
                        doubleFunset[2][2] = day;
                        doubleFunset[3][2] = hour;
                        doubleFunset[4][2] = min;
                        doubleFunset[5][2] = second;
                        //??????ui
                        StringBuilder sb = new StringBuilder()
                                .append(year).append("-");
                        if (month + 1 < 10) {
                            sb.append("0");
                        }
                        sb.append(month + 1).append("-");
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
                        if (second < 10) {
                            sb.append("0");
                        }
                        sb.append(second);
                        usParamsetAdapter.getData().get(1).setValueStr(sb.toString());
                        usParamsetAdapter.notifyDataSetChanged();


                        nowSetItem.clear();
                        for (int[] ints : doubleFunset) {
                            nowSetItem.add(ints);
                        }
                        nowIndex = 0;


                        type = 1;
                        int[] funs = doubleFunset[0];
                        manager.sendMsg(funs);

                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }




    /**
     *???????????????
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



    private void setInputValue(String title, String hint) {
        CircleDialogUtils.showInputValueDialog(this, title,
                hint, "", value -> {
                    double result = Double.parseDouble(value);
                    String pValue = value + "";
                    usParamsetAdapter.getData().get(3).setValueStr(pValue);
                    usParamsetAdapter.getData().get(3).setValue(String.valueOf(result));
                    usParamsetAdapter.notifyDataSetChanged();

                    List<ALLSettingBean> data = usParamsetAdapter.getData();
                    if (data.size() > 3) {
                        ALLSettingBean bean = data.get(3);
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

}
