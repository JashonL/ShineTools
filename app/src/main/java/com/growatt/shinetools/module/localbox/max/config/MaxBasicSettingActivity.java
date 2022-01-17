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
import com.growatt.shinetools.module.localbox.max.bean.ALLSettingBean;
import com.growatt.shinetools.module.localbox.mintool.TLXModeSetActivity;
import com.growatt.shinetools.socket.ConnectHandler;
import com.growatt.shinetools.socket.SocketManager;
import com.growatt.shinetools.utils.ActivityUtils;
import com.growatt.shinetools.utils.CircleDialogUtils;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.MyControl;
import com.growatt.shinetools.utils.Mydialog;
import com.growatt.shinetools.utils.OssUtils;
import com.growatt.shinetools.widget.GridDivider;
import com.mylhyl.circledialog.CircleDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

public class MaxBasicSettingActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener,
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
    private int currentPos = 0;//当前请求项
    private int type = 0;//0：读取  1：设置
    private SocketManager manager;
    //跳转到其他页面
    private boolean toOhterSetting = false;

    private String note1;
    private String note2;


    private List<int[]> nowSetItem = new ArrayList<>();
    private int nowIndex = 0;
    String rightTitle ;

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        ALLSettingBean bean = usParamsetAdapter.getData().get(position);
        String title = bean.getTitle();
        String hint = bean.getHint();
        float mul = bean.getMul();
        switch (position) {
            case 0:
                setComRate();
                break;
            case 1:
                setInputValue(position, title, hint, mul);
                break;
            case 2:

                break;
            case 3:
                OssUtils.circlerDialog(MaxBasicSettingActivity.this, note1, -1, false);
                break;
            case 4:
                OssUtils.circlerDialog(MaxBasicSettingActivity.this, note2, -1, false);
                break;
            case 5:
                setEnergyTotal(position, title, hint, mul);
                break;
            case 6:
                //断开连接
                //断开连接
                manager.disConnectSocket();
                Intent intent1 = new Intent(mContext, TLXModeSetActivity.class);
                intent1.putExtra("title",rightTitle);
                ActivityUtils.startActivity(this, intent1, false);
                break;

        }
    }


    private void setEnergyTotal(int position, String title, String hint, float mul) {
        CircleDialogUtils.showInputValueDialog(this, title,
                hint, "", value -> {
                    if (TextUtils.isEmpty(value)) {
                        toast(R.string.all_blank);
                    } else {
                        List<ALLSettingBean> data = usParamsetAdapter.getData();
                        if (data.size() > position) {
                            ALLSettingBean bean = data.get(position);
                            type = 1;
                            int[][] doubleFunset = bean.getDoubleFunset();
                            try {
                                int low = getWriteValueReal(Double.parseDouble(value), mul);
                                int high = 0;
                                if (low > 0xffff) {
                                    high = low - 0xffff;
                                    low = 0xffff;
                                }
                                doubleFunset[0][2] = high;
                                doubleFunset[1][2] = low;
                                double result = Double.parseDouble(value);
                                String pValue = value + "";
                                usParamsetAdapter.getData().get(position).setValueStr(pValue);
                                usParamsetAdapter.getData().get(position).setValue(String.valueOf(result));
                                usParamsetAdapter.notifyDataSetChanged();
                                nowSetItem.clear();
                                for (int[] ints : doubleFunset) {
                                    nowSetItem.add(ints);
                                }
                                nowIndex = 0;
                                int[] funs = doubleFunset[0];
                                manager.sendMsg(funs);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                toast(getString(R.string.m363设置失败));
                            }
                        }
                    }
                });


    }


    private void setComRate() {
        List<ALLSettingBean> data = usParamsetAdapter.getData();
        if (data.size() > 0) {
            ALLSettingBean bean = data.get(0);
            String[] items = bean.getItems();
            List<String> selects = new ArrayList<>(Arrays.asList(items));

            new CircleDialog.Builder()
                    .setTitle(getString(R.string.android_key499))
                    .setWidth(0.7f)
                    .setGravity(Gravity.CENTER)
                    .setMaxHeight(0.5f)
                    .setItems(selects, (parent, view1, pos, id) -> {
                        usParamsetAdapter.getData().get(0).setValueStr(selects.get(pos));
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


    private void setInputValue(int position, String title, String hint, float mul) {
        CircleDialogUtils.showInputValueDialog(this, title,
                hint, "", value -> {
                    double result = Double.parseDouble(value);
                    String pValue = value + "";
                    usParamsetAdapter.getData().get(position).setValueStr(pValue);
                    usParamsetAdapter.getData().get(position).setValue(String.valueOf(result));
                    usParamsetAdapter.notifyDataSetChanged();

                    List<ALLSettingBean> data = usParamsetAdapter.getData();
                    if (data.size() > position) {
                        ALLSettingBean bean = data.get(position);
                        //设置
                        type = 1;
                        int[] funs = bean.getFunSet();
                        funs[2] = getWriteValueReal(Double.parseDouble(value), mul);
                        manager.sendMsg(funs);
                    }
                });
    }


    public int getWriteValueReal(double write, float mul) {
        try {
            return (int) Math.round(Arith.div(write, mul));
        } catch (Exception e) {
            e.printStackTrace();
            return (int) write;
        }
    }


    @Override
    public void oncheck(boolean check, int position) {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_basic_max_setting;
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
        rightTitle = getString(R.string.m374设置Model);
        //系统设置项
        List<ALLSettingBean> settingList
                = MaxConfigControl.getSettingList(MaxConfigControl.MaxSettingEnum.MAX_BASIC_SETTING, this);
        usParamsetAdapter.replaceData(settingList);
        note1 = getString(R.string.m443该项暂不能设置请设置Model);
        note2 = getString(R.string.m444该项暂不能设置);
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
            MyControl.showJumpWifiSet(MaxBasicSettingActivity.this);
        }

        @Override
        public void sendMsgFail() {
            manager.disConnectSocket();
            MyControl.showTcpDisConnect(MaxBasicSettingActivity.this, getString(R.string.disconnet_retry),
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

                switch (currentPos) {
                    case 0:
                        getData(1);
                        break;
                    case 1:
                        getData(2);
                        break;
                    case 2:
                        getData(5);
                        break;
                    case 5:
                        Mydialog.Dismiss();
                        break;
                }


            } else {//设置
                //检测内容正确性
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
     * 根据传进来的mtype解析数据
     *
     * @param bytes
     */
    private void parseMax(byte[] bytes) {
        //移除外部协议
        byte[] bs = RegisterParseUtil.removePro17(bytes);
        //解析int值
        switch (currentPos) {
            case 0://通信波特率
                //解析int值
                int value = MaxWifiParseUtil.obtainValueOne(bs);
                LogUtil.i("通信波特率:" + value);
                ALLSettingBean bean = usParamsetAdapter.getData().get(0);
                String[] items = bean.getItems();
                if (items != null && items.length > value) {
                    String valueS = items[value];
                    bean.setValue(String.valueOf(value));
                    bean.setValueStr(String.valueOf(valueS));
                } else {
                    bean.setValue(String.valueOf(value));
                    bean.setValueStr(String.valueOf(value));
                }


                break;
            case 1://modbus版本
                //解析int值
                LogUtil.i("modbus版本:");
                parser(bs, 1);

                break;
            case 2://PV电压
                //解析int值
                LogUtil.i("PV电压:");
                parser(bs, 2);
                break;

            case 5://修改总发电量
                //解析int值
                LogUtil.i("修改总发电量:");
                int totalE = MaxWifiParseUtil.obtainRegistValueHOrL(1, bs[110], bs[111])
                        + MaxWifiParseUtil.obtainRegistValueHOrL(0, bs[112], bs[113]);
                ALLSettingBean bean5 = usParamsetAdapter.getData().get(5);
                float mul = bean5.getMul();
                String unit = "";
                bean5.setValue(String.valueOf(totalE));
                bean5.setValueStr(getReadValueReal(totalE, mul, unit));
                break;
        }
        usParamsetAdapter.notifyDataSetChanged();
    }


    private void parser(byte[] data, int pos) {
        int value1 = MaxWifiParseUtil.obtainValueOne(data);
        ALLSettingBean bean = usParamsetAdapter.getData().get(pos);
        float mul = bean.getMul();
        String unit = "";
        bean.setValueStr(getReadValueReal(value1, mul, unit));
    }

    public String getReadValueReal(int read, float mul, String unit) {
        boolean isNum = ((int) mul) == mul;
        String value;
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
            LogUtil.i("-------------------请求获取:" + bean.getTitle() + "----------------");
            int[] funs = bean.getFuns();
            manager.sendMsg(funs);
        }
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.right_action) {
            getData(0);
        }
        return true;
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
        toOhterSetting=true;
        manager.disConnectSocket();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
