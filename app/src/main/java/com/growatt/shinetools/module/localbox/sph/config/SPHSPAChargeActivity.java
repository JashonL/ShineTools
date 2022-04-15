package com.growatt.shinetools.module.localbox.sph.config;

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
import com.growatt.shinetools.module.localbox.mintool.TLXHToolTimerActivity;
import com.growatt.shinetools.module.localbox.tlxh.config.TLXHConfigControl;
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

public class SPHSPAChargeActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener,
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
    private String[] frequency;//离网频率
    private String[] voltage;//离网电压
    private String[] ctselect;//CT选择
    private String[] batterySelect;//电池选择

    @Override
    protected int getContentView() {
        return R.layout.activity_all_setting;
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
                = SPHSPAConfigControl.getSettingList(SPHSPAConfigControl.SphSpaSettingEnum.SPH_CHARGE_MANAGER, this);
        usParamsetAdapter.replaceData(settingList);
        frequency = new String[]{"50Hz", "60Hz"};
        voltage = new String[]{"230V", "208V", "240V"};
        ctselect=new String[]{"cWiredCT","cWirelessCT","METER"};
        batterySelect=new String[]{"Lithium","Lead-acid","other"};
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
            getChageManager();
        }

        @Override
        public void connectFail() {
            manager.disConnectSocket();
            MyControl.showJumpWifiSet(SPHSPAChargeActivity.this);
        }

        @Override
        public void sendMsgFail() {
            manager.disConnectSocket();
            MyControl.showTcpDisConnect(SPHSPAChargeActivity.this, getString(R.string.disconnet_retry),
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
            case 0:
                break;
            case 1: case 2: case 3: case 4: case 5: //CV电压
                int value1 = MaxWifiParseUtil.obtainValueOne(bs);
                LogUtil.i("CV电压:"+value1);
                ALLSettingBean bean2 = usParamsetAdapter.getData().get(currentPos);
                bean2.setValue(String.valueOf(value1));
                bean2.setValueStr(getReadValueReal(currentPos,value1));
                break;
        }
        usParamsetAdapter.notifyDataSetChanged();
    }


    /**
     * 请求获取逆变器的时间
     */
    private void getChageManager() {
        getData(0);
    }








    public String getReadValueReal(int position,int read) {
        ALLSettingBean bean = usParamsetAdapter.getData().get(position);
        String[] items = bean.getItems();
        String value=String.valueOf(read);
        float mul=bean.getMul();
        String unit=bean.getUnit();
        boolean isNum = ((int) mul) == mul;
        if (isNum) {
            value = read * ((int) mul) + unit;
        } else {
            value = Arith.mul(read, mul, 2) + unit;
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
            getChageManager();
        }
        return true;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        ALLSettingBean bean = usParamsetAdapter.getData().get(position);
        String title = bean.getTitle();
        String hint = bean.getHint();
        if (position == 0) {//设置充放电
            toOhterSetting = true;
            manager.disConnectSocket();
            Intent intent1 = new Intent(this, TLXHToolTimerActivity.class);
//            Intent intent1 = new Intent(this, USChargeTimeActivity.class);
            intent1.putExtra("title", bean.getTitle());
            ActivityUtils.startActivity(this, intent1, false);
        }else {
            if (position!=1){
                setInputValue(position, title, hint);
            }
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
