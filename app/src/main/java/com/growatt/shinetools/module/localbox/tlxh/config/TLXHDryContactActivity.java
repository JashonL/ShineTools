package com.growatt.shinetools.module.localbox.tlxh.config;

import android.os.Handler;
import android.text.TextUtils;
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

public class TLXHDryContactActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener,
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


    private DeviceSettingAdapter usParamsetAdapter;
    private MenuItem item;
    private SocketManager manager;
    private int type = 0;//0：读取  1：设置
    private int currentPos = 0;//当前请求项

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.right_action) {
            getData(0);
        }
        return true;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        ALLSettingBean bean = usParamsetAdapter.getData().get(position);
        String title = bean.getTitle();
        String hint = bean.getHint();
        if (position != 0) {
            setInputValue(title,hint);
        }
    }




    private void setInputValue(String title, String hint) {
        CircleDialogUtils.showInputValueDialog(TLXHDryContactActivity.this, title,
                hint, "", value -> {
                    double result = Double.parseDouble(value);
                    String pValue = value + "";
                    usParamsetAdapter.getData().get(0).setValueStr(pValue);
                    usParamsetAdapter.getData().get(0).setValue(String.valueOf(result));
                    usParamsetAdapter.notifyDataSetChanged();

                    List<ALLSettingBean> data = usParamsetAdapter.getData();
                    if (data.size() > 0) {
                        ALLSettingBean bean = data.get(0);
                        //设置
                        type = 1;
                        int[] funs = bean.getFunSet();
                        funs[2] = (int) result;
                        manager.sendMsg(funs);
                    }
                });
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
    protected int getContentView() {
        return R.layout.activity_max_active_power;
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

        rvSetting.setLayoutManager(new LinearLayoutManager(this));
        usParamsetAdapter = new DeviceSettingAdapter(new ArrayList<>(), this);
        int div = (int) getResources().getDimension(R.dimen.dp_1);
        GridDivider gridDivider = new GridDivider(ContextCompat.getColor(this, R.color.white), div, div);
        rvSetting.addItemDecoration(gridDivider);
        rvSetting.setAdapter(usParamsetAdapter);
        usParamsetAdapter.setOnItemClickListener(this);


        String title = getIntent().getStringExtra("title");
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }

    }

    @Override
    protected void initData() {
        //系统设置项
        List<ALLSettingBean> settingList
                = TLXHConfigControl.getSettingList(TLXHConfigControl.TlxSettingEnum.TLXH_DRY_CONTACT, this);
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
            getValue();
        }

        @Override
        public void connectFail() {
            manager.disConnectSocket();
            MyControl.showJumpWifiSet(TLXHDryContactActivity.this);
        }

        @Override
        public void sendMsgFail() {
            manager.disConnectSocket();
            MyControl.showTcpDisConnect(TLXHDryContactActivity.this, getString(R.string.disconnet_retry),
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
                Mydialog.Dismiss();


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
            case 0://干接点状态
                //解析int值
                int value0 = MaxWifiParseUtil.obtainValueOne(bs);
                usParamsetAdapter.getData().get(0).setValue(String.valueOf(value0));
                break;
            case 1://干接点开通的功率百分比
                //解析int值

                int value1 = MaxWifiParseUtil.obtainValueOne(bs);
                ALLSettingBean bean = usParamsetAdapter.getData().get(1);
                float mul = bean.getMul();
                String unit = "";
                usParamsetAdapter.getData().get(1).setValue(String.valueOf(value1));
                usParamsetAdapter.getData().get(1).setValueStr(getReadValueReal(value1,mul,unit));
                break;
            case 2://干接点关闭功率百分比
                int value2 = MaxWifiParseUtil.obtainValueOne(bs);
                ALLSettingBean bean2 = usParamsetAdapter.getData().get(1);
                float mul2 = bean2.getMul();
                String unit2 = "";
                usParamsetAdapter.getData().get(2).setValue(String.valueOf(value2));
                usParamsetAdapter.getData().get(2).setValueStr(getReadValueReal(value2,mul2,unit2));
                break;
        }
        usParamsetAdapter.notifyDataSetChanged();
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


    /**
     * 请求获取逆变器的时间
     */
    private void getValue() {
        getData(0);
    }

    private void getData(int pos) {
        type = 0;
        currentPos = pos;
        List<ALLSettingBean> data = usParamsetAdapter.getData();
        if (data.size() > pos) {
            ALLSettingBean bean = data.get(pos);
            LogUtil.i("-------------------请求获取:"+bean.getTitle()+"----------------");
            int[] funs = bean.getFuns();
            manager.sendMsg(funs);
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
