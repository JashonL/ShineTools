package com.growatt.shinetools.module.localbox.tlx.config;

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
import com.growatt.shinetools.module.localbox.max.config.MaxActivePowerActivity;
import com.growatt.shinetools.module.localbox.tlxh.config.TLXHDryContactActivity;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

public class TLXSystemSettingActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener,
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
    private int currentPos = 1;//当前请求项
    private int type = 0;//0：读取  1：设置
    private List<int[]> nowSetItem = new ArrayList<int[]>();
    private int nowIndex = 0;
    private int deviceType;


    //跳转到其他页面
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
        //快速设置项
        List<ALLSettingBean> settingList
                = TLXConfigControl.getSettingList(TLXConfigControl.TlxSettingEnum.TLX_SYSTEM_SETTING, this);
        usParamsetAdapter.replaceData(settingList);
        connetSocket();
    }



    private void connetSocket() {
        //初始化连接
        manager = new SocketManager(this);
        //设置连接监听
        manager.onConect(connectHandler);
        //开始连接TCP
        //延迟一下避免频繁操作
        new Handler().postDelayed(() -> manager.connectSocket(),100);
    }


    ConnectHandler connectHandler = new ConnectHandler() {
        @Override
        public void connectSuccess() {
            getOnOff();
        }

        @Override
        public void connectFail() {
            manager.disConnectSocket();
            MyControl.showJumpWifiSet(TLXSystemSettingActivity.this);
        }

        @Override
        public void sendMsgFail() {
            manager.disConnectSocket();
            MyControl.showTcpDisConnect(TLXSystemSettingActivity.this, getString(R.string.disconnet_retry),
                    () -> {
                        connetSocket();
                    }
            );
        }

        @Override
        public void readTimeOut() {

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
                if (currentPos < usParamsetAdapter.getData().size() - 1) {
                    getData(++currentPos);
                }else {
                    Mydialog.Dismiss();
                }

            } else {//设置
                //检测内容正确性
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
     * 根据传进来的mtype解析数据
     *
     * @param bytes
     */
    private void parseMax(byte[] bytes) {
        //移除外部协议
        byte[] bs = RegisterParseUtil.removePro17(bytes);
        //解析int值
        switch (currentPos) {
            case 0://开关逆变器
                //解析int值
                int value = MaxWifiParseUtil.obtainValueOne(bs);
                LogUtil.i("解析开关逆变器:"+value);
                usParamsetAdapter.getData().get(0).setValue(String.valueOf(value));
                break;
            case 1://有功功率百分比
                //解析int值
                int content1 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3, 0, 1));
                LogUtil.i("解析有功功率百分比:"+content1);
                parserActiviPercent(content1);
                break;
            case 2://PV输入模式
                int value2 = MaxWifiParseUtil.obtainValueOne(bs);
                LogUtil.i("解析pv设置模式:"+value2);
                ALLSettingBean bean2 = usParamsetAdapter.getData().get(2);
                bean2.setValue(String.valueOf(value2));
                bean2.setValueStr(getReadValueReal(2,value2));
                break;
            case 3://干接点设置

                break;
            case 4://N至PE检测功能使能
                //解析int值
                int value4 = MaxWifiParseUtil.obtainValueOne(bs);
                LogUtil.i("解析N至PE检测功能使能:"+value4);
                usParamsetAdapter.getData().get(4).setValue(String.valueOf(value4));
                break;
            case 5://宽电网电压
                //解析int值
                int value5 = MaxWifiParseUtil.obtainValueOne(bs);
                LogUtil.i("解析宽电网电压范围使能:"+value5);
                ALLSettingBean bean5 = usParamsetAdapter.getData().get(5);
                bean5.setValue(String.valueOf(value5));
                bean5.setValueStr(getReadValueReal(5,value5));
                break;
            case 6://安规功能使能
                //解析int值
                int value6 = MaxWifiParseUtil.obtainValueOne(bs);
                LogUtil.i("解析安规功能使能:"+value6);
                parserSafetyEnable(value6);
                break;
            case 7://电网N线使能
                //解析int值
                int value7 = MaxWifiParseUtil.obtainValueOne(bs);
                LogUtil.i("解析电网N线使能:"+value7);
                usParamsetAdapter.getData().get(7).setValue(String.valueOf(value7));
                break;
            case 8://指定的规格设置使能
                int value8 = MaxWifiParseUtil.obtainValueOne(bs);
                LogUtil.i("解析指定规格设置使能:"+value8);
                usParamsetAdapter.getData().get(8).setValue(String.valueOf(value8));
                break;
            case 9://ISLand使能
                //解析int值
                int value9 = MaxWifiParseUtil.obtainValueOne(bs);
                LogUtil.i("解析ISlland使能:"+value9);
                usParamsetAdapter.getData().get(9).setValue(String.valueOf(value9));
                break;

            case 10://风扇检查
                int value10 = MaxWifiParseUtil.obtainValueOne(bs);
                LogUtil.i("风扇检查:"+value10);
                usParamsetAdapter.getData().get(10).setValue(String.valueOf(value10));
                break;
            case 11://检查固件

                break;
            case 12://PID工作模式
                int value12 = MaxWifiParseUtil.obtainValueOne(bs);
                LogUtil.i("解析PID工作模式:"+value12);
                ALLSettingBean bean13 = usParamsetAdapter.getData().get(12);
                bean13.setValue(String.valueOf(bean13));
                bean13.setValueStr(getReadValueReal(12,value12));
                break;
            case 13://PID开关
                //解析int值
                int value13 = MaxWifiParseUtil.obtainValueOne(bs);
                LogUtil.i("解析PID开关:"+value13);
                usParamsetAdapter.getData().get(13).setValue(String.valueOf(value13));
                break;

            case 14://PID工作电压选择
                //解析int值
                int value14 = MaxWifiParseUtil.obtainValueOne(bs);
                LogUtil.i("解析PID工作电压选择:"+value14);
                paserPidVoltgeSelect(value14);

                break;

        }
        usParamsetAdapter.notifyDataSetChanged();
    }








    /**
     * 请求获取逆变器的时间
     */
    private void getOnOff() {
        getData(0);
    }




    public void parserSafetyEnable(int read) {
        ALLSettingBean bean = usParamsetAdapter.getData().get(6);
//        bean.setValueStr(getReadValueReal(6,read));
        bean.setValue(String.valueOf(read));
    }



    public void parserActiviPercent(int read) {
        ALLSettingBean bean = usParamsetAdapter.getData().get(1);
        bean.setValueStr(String.valueOf(read));
    }


    public void paserPidVoltgeSelect(int read) {
        ALLSettingBean bean = usParamsetAdapter.getData().get(14);
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
            case 1://有功功率百分比
                toOhterSetting=true;
                manager.disConnectSocket();
                Intent intent1 = new Intent(this, MaxActivePowerActivity.class);
                intent1.putExtra("title", bean.getTitle());
                ActivityUtils.startActivity(this, intent1, false);
                break;
            case 2://PV输入模式
                setSelectItem(position,title);
                break;
            case 3://干接点设置
                toOhterSetting=true;
                manager.disConnectSocket();
                Intent intent2 = new Intent(this, TLXHDryContactActivity.class);
                intent2.putExtra("title", bean.getTitle());
                ActivityUtils.startActivity(this, intent2, false);

                break;

            case 5://宽电网电压范围使能
                setSelectItem(position,title);
                break;
            case 6://安规功能使能
//                setInputValue(position,title,hint);
                break;

            case 10://

                break;
            case 11:
                toOhterSetting=true;
                manager.disConnectSocket();
                Intent intent = new Intent(mContext, ConfigType2Activity.class);
                intent.putExtra("type", 5);
                intent.putExtra("title", String.format("%s%s",title,"233~234"));
                startActivity(intent);
                break;


            case 12:
                setSelectItem(position,title);
                break;
            case 13:
                break;
            case 14:
                setInputValue(position,title,hint);
                break;

        }
    }

    @Override
    public void oncheck(boolean check, int position) {
        ALLSettingBean bean = usParamsetAdapter.getData().get(position);
        int value = check ? 1 : 0;
        usParamsetAdapter.getData().get(position).setValue(String.valueOf(value));
        usParamsetAdapter.notifyDataSetChanged();
        type = 1;
        LogUtil.i("-------------------设置"+bean.getTitle()+"----------------");
        int[] funSet = bean.getFunSet();
        funSet[2]=value;
        manager.sendMsg(funSet);
    }


    /**
     * 设置逆变器时间
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
                        //更新ui
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
                        //设置
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
        manager.disConnectSocket();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
