package com.growatt.shinetools.module.localbox.max.config;

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
import com.growatt.shinetools.adapter.MaxSettingAdapter;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.MaxWifiParseUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.module.localbox.max.bean.MaxSettingBean;
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

public class MaxActivePowerActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener,
        MaxSettingAdapter.OnChildCheckLiseners, Toolbar.OnMenuItemClickListener {

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


    private MaxSettingAdapter usParamsetAdapter;
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
        MaxSettingBean bean = usParamsetAdapter.getData().get(position);
        String title = bean.getTitle();
        String hint = bean.getHint();
        if (position == 0) {
            setInputValue(title,hint);
        }
    }




    private void setInputValue(String title, String hint) {
        CircleDialogUtils.showInputValueDialog(MaxActivePowerActivity.this, title,
                hint, "", value -> {
                    double result = Double.parseDouble(value);
                    String pValue = value + "";
                    usParamsetAdapter.getData().get(0).setValueStr(pValue);
                    usParamsetAdapter.getData().get(0).setValue(String.valueOf(result));
                    usParamsetAdapter.notifyDataSetChanged();

                    List<MaxSettingBean> data = usParamsetAdapter.getData();
                    if (data.size() > 0) {
                        MaxSettingBean bean = data.get(0);
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
        MaxSettingBean bean = usParamsetAdapter.getData().get(position);
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
        usParamsetAdapter = new MaxSettingAdapter(new ArrayList<>(), this);
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
        List<MaxSettingBean> settingList
                = MaxConfigControl.getSettingList(MaxConfigControl.MaxSettingEnum.MAX_ACTIVE0POWER_SETTING, this);
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
            MyControl.showJumpWifiSet(MaxActivePowerActivity.this);
        }

        @Override
        public void sendMsgFail() {
            manager.disConnectSocket();
            MyControl.showTcpDisConnect(MaxActivePowerActivity.this, getString(R.string.disconnet_retry),
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
            case 0://有功功率百分比
                //解析int值
                int value = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3, 0, 1));
                MaxSettingBean bean = usParamsetAdapter.getData().get(0);
                bean.setValue(String.valueOf(value));
                String s = value + "";
                bean.setValueStr(s);

                int value1 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 2, 0, 1));
                MaxSettingBean bean1 = usParamsetAdapter.getData().get(1);
                bean1.setValue(String.valueOf(value1));
                bean1.setValueStr(s);

                break;
        }
        usParamsetAdapter.notifyDataSetChanged();
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
        List<MaxSettingBean> data = usParamsetAdapter.getData();
        if (data.size() > pos) {
            MaxSettingBean bean = data.get(pos);
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
