package com.growatt.shinetools.module;

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
import com.growatt.shinetools.bean.UsSettingConstant;
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

public class ChekFirmWareActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener,
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
        switch (position){
            case 0://有功功率百分比
            case 1://PV输入模式
                setInputValue(position,title,hint);
                break;


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
    public void oncheck(boolean check, int position) {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_check_firmware;
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
        List<ALLSettingBean> settingList = getCheckData();
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
            getData(0);
        }

        @Override
        public void connectFail() {
            manager.disConnectSocket();
            MyControl.showJumpWifiSet(ChekFirmWareActivity.this);
        }

        @Override
        public void sendMsgFail() {
            manager.disConnectSocket();
            MyControl.showTcpDisConnect(ChekFirmWareActivity.this, getString(R.string.disconnet_retry),
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
            case 0://检查固件1
                //解析int值
                int value0 = MaxWifiParseUtil.obtainValueOne(bs);
                LogUtil.i("检查固件1:"+value0);
                ALLSettingBean bean = usParamsetAdapter.getData().get(0);
                bean.setValue(String.valueOf(value0));
                bean.setValueStr(getReadValueReal(0,value0));
                break;
            case 1://检查固件2
                //解析int值
                int value1 = MaxWifiParseUtil.obtainValueOne(bs);
                LogUtil.i("检查固件2:"+value1);
                ALLSettingBean bean1 = usParamsetAdapter.getData().get(1);
                bean1.setValue(String.valueOf(value1));
                bean1.setValueStr(getReadValueReal(1,value1));
                break;
        }
        usParamsetAdapter.notifyDataSetChanged();
    }




    public String getReadValueReal(int position,int read) {
        ALLSettingBean bean = usParamsetAdapter.getData().get(position);
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



    private List<ALLSettingBean> getCheckData() {

        List<ALLSettingBean> list = new ArrayList<>();
        String tips = "";
        String tips1 = getString(R.string.android_key3048) + ":" + "0~255";
        String[] titls = new String[]{
                getString(R.string.android_key873) + 1,
                getString(R.string.android_key873) + 2,
        };

        int[] itemTypes = new int[]{
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT
        };

        String[] register = new String[]{
                "", ""
        };
        float[] multiples = new float[]{
                1, 1
        };
        String[] units = new String[]{"", ""
        };
        String[] hints = new String[]{
                "", ""
        };
        int[][] funs = new int[][]{
                {3, 233, 233},
                {3, 234, 234}
        };
        int[][] funset = new int[][]{
                {6, 233, 0},
                {6, 234, 0},
        };

        String[][] items = new String[][]{
                {},

                {},
        };
        int[][] doubleFunset = new int[][]{
                {6, 7147, -1}, {6, 7148, -1}

        };
        int[][][] threeFunset = new int[][][]{
                {{6, 2, 1}, {6, 3, -1}},//有功功率百分比设置
                {{6, 233, -1}, {6, 234, -1}}//检查固件
        };
        for (int i = 0; i < titls.length; i++) {
            ALLSettingBean bean = new ALLSettingBean();
            bean.setTitle(titls[i]);
            bean.setItemType(itemTypes[i]);
            bean.setRegister(register[i]);
            bean.setUnit(units[i]);
            bean.setFuns(funs[i]);
            bean.setFunSet(funset[i]);
            bean.setItems(items[i]);
            bean.setHint(hints[i]);
            bean.setDoubleFunset(doubleFunset);
            bean.setThreeFunSet(threeFunset);
            bean.setMul(multiples[i]);
            list.add(bean);
        }
        return list;
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
