package com.growatt.shinetools.module.localbox.MainsCodeSetting;

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
import com.growatt.shinetools.module.localbox.max.config.MaxConfigControl;
import com.growatt.shinetools.socket.ConnectHandler;
import com.growatt.shinetools.socket.SocketManager;
import com.growatt.shinetools.utils.CircleDialogUtils;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.MyControl;
import com.growatt.shinetools.utils.Mydialog;
import com.growatt.shinetools.widget.GridDivider;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class VoltageReactiveActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener,
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



    private List<int[]> nowSetItem = new ArrayList<>();
    private int nowIndex = 0;

    private MaxConfigControl.MaxSettingEnum enum_item;

    @Override
    protected int getContentView() {
        return R.layout.activity_max_grid_code_second;
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
        int curpos = getIntent().getIntExtra("curpos", 0);
        enum_item=MaxConfigControl.MaxSettingEnum.MAX_GRID_SECOND_VOLTAGE_SETTING;
        //系统设置项
        List<ALLSettingBean> settingList
                = MaxConfigControl.getSettingList(enum_item, this);
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
            MyControl.showJumpWifiSet(VoltageReactiveActivity.this);
        }

        @Override
        public void sendMsgFail() {
            manager.disConnectSocket();
            MyControl.showTcpDisConnect(VoltageReactiveActivity.this, getString(R.string.disconnet_retry),
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

                getNextData();



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



    //请求获取感性载率
    private void getNextData() {
        if (currentPos < usParamsetAdapter.getData().size() - 1) {
            getData(++currentPos);
        } else {
            Mydialog.Dismiss();
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
        parserVoltage(bs);
        usParamsetAdapter.notifyDataSetChanged();
    }








    /**
     * 电压无功
     */
    private void parserVoltage(byte[] bytes) {
        //解析int值
        parser(bytes, currentPos);

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
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        setVoltage(position);
    }





    /**
     * 电压无功
     * @param pos
     */

    private void setVoltage(int pos) {
        ALLSettingBean bean = usParamsetAdapter.getData().get(pos);
        String title = bean.getTitle();
        float mul = bean.getMul();
        String hint = bean.getHint();
        String unit = bean.getUnit();
        setCommenInputValue(pos, title, hint, mul,unit);
    }



    /**
     *
     * @param position
     * @param title
     * @param hint
     * @param mul
     */

    private void setCommenInputValue(int position, String title, String hint, float mul,String unit) {
        CircleDialogUtils.showInputValueDialog(this, title,
                hint, unit, value -> {
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
