package com.growatt.shinetools.module.localbox.pfsetting;

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

public class CapacitivePFActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener,
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
        return R.layout.activity_max_grid_code_third;
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
        enum_item = MaxConfigControl.MaxSettingEnum.MAX_GRID_THIRE_CAPACITIVE_PF;
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
            MyControl.showJumpWifiSet(CapacitivePFActivity.this);
        }

        @Override
        public void sendMsgFail() {
            manager.disConnectSocket();
            MyControl.showTcpDisConnect(CapacitivePFActivity.this, getString(R.string.disconnet_retry),
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

                getInductive();


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
    private void getInductive() {
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
        parserPF(bs);
        usParamsetAdapter.notifyDataSetChanged();
    }


    /**
     * 感性PF、容性PF
     */
    private void parserPF(byte[] bytes) {
        switch (currentPos) {
            case 0:
                //解析int值
                LogUtil.i("感性、容性PF");
                parserItems(bytes, 0);
                break;
            case 1:
                int value1 = MaxWifiParseUtil.obtainValueOne(bytes);
                LogUtil.i("记忆使能:" + value1);
                usParamsetAdapter.getData().get(1).setValue(String.valueOf(value1));
                usParamsetAdapter.getData().get(1).setValueStr(String.valueOf(value1));
                break;
        }

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


    private void parserItems(byte[] data, int pos) {
        ALLSettingBean bean = usParamsetAdapter.getData().get(pos);
        int value1 = MaxWifiParseUtil.obtainValueOne(data);
        bean.setValueStr(String.valueOf(value1));
        bean.setValue(String.valueOf(value1));
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
        inDuctiveReactive(position);
    }


    private void inDuctiveReactive(int pos) {
        ALLSettingBean bean = usParamsetAdapter.getData().get(pos);
        String title = bean.getTitle();
        float mul = bean.getMul();
        String hint = bean.getHint();
        switch (pos) {
            case 0:
                setInputValue(pos, title, hint, mul);
                break;
        }
    }


    private void setInputValue(int pos, String title, String hint, float mul) {
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
                        ALLSettingBean bean1 = data.get(1);
                        String value1 = bean1.getValue();
                        int remember = "1".equals(value1) ? 1 : 0;

                        LogUtil.i("-------------------设置" + bean.getTitle() + "----------------");

                        //设置
                        type = 1;
                        int[] funs = bean.getFunSet();
                        funs[2] = getWriteValueReal(Double.parseDouble(value), mul);

                        nowSetItem.clear();
                        int[][][] threeFunSet = bean.getThreeFunSet();
                        int[][] ints = threeFunSet[0];
                        ints[0] = funs;
                        ints[1][2] = remember;
                        for (int[] anInt : ints) {
                            nowSetItem.add(anInt);
                        }
                        manager.sendMsg(funs);
                    }
                });
    }




    @Override
    public void oncheck(boolean check, int position) {
        inductiveOncheck(check, position);
    }

    //感性载率
    private void inductiveOncheck(boolean check, int position) {
        ALLSettingBean bean = usParamsetAdapter.getData().get(position);
        int value = check ? 1 : 0;
        usParamsetAdapter.getData().get(position).setValue(String.valueOf(value));
        usParamsetAdapter.notifyDataSetChanged();
        type = 1;


        LogUtil.i("-------------------设置" + bean.getTitle() + "----------------");
        int[] funSet = bean.getFunSet();
        funSet[2] = value;


        ALLSettingBean bean1 = usParamsetAdapter.getData().get(0);
        String value1 = bean1.getValue();
        float mul = bean1.getMul();
        double v;
        try {
            v = Double.parseDouble(value1);
        } catch (NumberFormatException e) {
            v = 0d;
        }

        nowSetItem.clear();
        int[][][] threeFunSet = bean.getThreeFunSet();
        int[][] ints = threeFunSet[0];
        ints[0][2] = getWriteValueReal(v, mul);
        ints[1] = funSet;

        for (int[] anInt : ints) {
            nowSetItem.add(anInt);
        }
        manager.sendMsg(funSet);
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
