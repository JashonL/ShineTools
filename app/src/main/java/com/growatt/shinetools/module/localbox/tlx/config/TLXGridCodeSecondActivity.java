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


/**
 * 安规参数设置二级设置页面
 */

public class TLXGridCodeSecondActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener,
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

   private TLXConfigControl.TlxSettingEnum enum_item;

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
        enum_item = TLXConfigControl.TlxSettingEnum.TLX_GRID_CODE_PARAMETERS_SETTING;
        switch (curpos){
            case 0://PF设置
                enum_item=TLXConfigControl.TlxSettingEnum.TLX_GRID_SECOND_PF_SETTING;
                break;
            case 1://频率有功
                enum_item=TLXConfigControl.TlxSettingEnum.TLX_GRID_SECOND_FRENCY_WATT_SETTING;
                break;
            case 2://电压无功
                enum_item=TLXConfigControl.TlxSettingEnum.TLX_GRID_SECOND_VOLTAGE_SETTING;
                break;
            case 3://电源启动重启斜率
                enum_item=TLXConfigControl.TlxSettingEnum.TLX_GRID_SECOND_POWERE_START_SETTING;
                break;
            case 4://AC电压保护
                enum_item=TLXConfigControl.TlxSettingEnum.TLX_GRID_SECOND_AC_VOLTAGE_PROTECT;
                break;
            case 5://AC频率保护
                enum_item=TLXConfigControl.TlxSettingEnum.TLX_GRID_SECOND_AC_FRENCY_PROTECT;
                break;
            case 6://并网范围
                enum_item=TLXConfigControl.TlxSettingEnum.TLX_GRID_SECOND_SYNCHORNIZATION_RANGE;
                break;
        }
        //系统设置项
        List<ALLSettingBean> settingList
                = TLXConfigControl.getSettingList(enum_item, this);
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
            MyControl.showJumpWifiSet(TLXGridCodeSecondActivity.this);
        }

        @Override
        public void sendMsgFail() {
            manager.disConnectSocket();
            MyControl.showTcpDisConnect(TLXGridCodeSecondActivity.this, getString(R.string.disconnet_retry),
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

                switch (enum_item){
                    case TLX_GRID_SECOND_PF_SETTING://PF设置
                    case TLX_GRID_SECOND_POWERE_START_SETTING:
                        Mydialog.Dismiss();
                        break;
                    case TLX_GRID_SECOND_FRENCY_WATT_SETTING:
                    case TLX_GRID_SECOND_VOLTAGE_SETTING:
                    case TLX_GRID_SECOND_AC_VOLTAGE_PROTECT:
                    case TLX_GRID_SECOND_AC_FRENCY_PROTECT:
                    case TLX_GRID_SECOND_SYNCHORNIZATION_RANGE:
                        getNextData();
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
        switch (enum_item){
            case TLX_GRID_SECOND_PF_SETTING://PF设置
                parserPfSetting(bs);
                break;
            case TLX_GRID_SECOND_FRENCY_WATT_SETTING:
                parserFrencyWatt(bs);
                break;
            case TLX_GRID_SECOND_VOLTAGE_SETTING:
                parserVoltage(bs);
                break;
            case TLX_GRID_SECOND_POWERE_START_SETTING:
                parserRisingslope(bs);
                break;
            case TLX_GRID_SECOND_AC_VOLTAGE_PROTECT:
                parserAcVoltageProtect(bs);
                break;
            case TLX_GRID_SECOND_AC_FRENCY_PROTECT:
                parserAcFrencyProtect(bs);

                break;
            case TLX_GRID_SECOND_SYNCHORNIZATION_RANGE:
                parserGridConnected(bs);
                break;

        }

        usParamsetAdapter.notifyDataSetChanged();
    }


    /**
     *PF设置读取解析
     */
    private void parserPfSetting(byte[] bytes){
        if (currentPos == 0) {//运行PF为1
            //解析int值
            LogUtil.i("运行PF为1:");
            parserItems(bytes, 0);
        }
    }



    /**
     * 频率有功
     */
    private void parserFrencyWatt(byte[] bytes) {
        ALLSettingBean bean = usParamsetAdapter.getData().get(currentPos);
        //解析int值
        LogUtil.i("--------------解析:"+bean.getTitle()+"------------------");
        parser(bytes, currentPos);
    }



    /**
     * 电压无功
     */
    private void parserVoltage(byte[] bytes) {
        //解析int值
        parser(bytes, currentPos);

    }


    /**
     *上升斜率
     */
    private void parserRisingslope(byte[] bytes){
        //解析int值
        int value0 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bytes, 0, 0, 1));
        int value1 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bytes, 1, 0, 1));


        ALLSettingBean bean = usParamsetAdapter.getData().get(0);
        float mul = bean.getMul();
        String unit = "";
        bean.setValueStr(getReadValueReal(value0, mul, unit));

        ALLSettingBean bean1 = usParamsetAdapter.getData().get(1);
        float mul1 = bean1.getMul();
        String unit1 = "";
        bean1.setValueStr(getReadValueReal(value1, mul1, unit1));
    }


    /**
     * AC电压保护
     */
    private void parserAcVoltageProtect(byte[] bytes) {
        //解析int值
        parser(bytes, currentPos);

    }



    /**
     * AC频率保护
     */
    private void parserAcFrencyProtect(byte[] bytes) {
        //解析int值
        parser(bytes, currentPos);

    }


    /**
     * 并网范围
     */
    private void parserGridConnected(byte[] bytes) {
        //解析int值
        parser(bytes, currentPos);

    }



    private void parserItems(byte[] data, int pos){
        ALLSettingBean bean = usParamsetAdapter.getData().get(pos);
        int value1 = MaxWifiParseUtil.obtainValueOne(data);
        String[] items = bean.getItems();
        if (value1<items.length){
            String item = items[value1];
            bean.setValueStr(item);
            bean.setValue(String.valueOf(value1));
        }else {
            bean.setValueStr(String.valueOf(value1));
            bean.setValue(String.valueOf(value1));
        }
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
//            switch (enum_item){
//                case MAX_GRID_SECOND_PF_SETTING://PF设置
//                    getData(0);
//                    break;
//                case MAX_GRID_SECOND_FRENCY_WATT_SETTING:
//                    getData(0);
//                    break;
//                case MAX_GRID_SECOND_VOLTAGE_SETTING:
//                    break;
//                case MAX_GRID_SECOND_POWERE_START_SETTING:
//                    break;
//                case MAX_GRID_SECOND_AC_VOLTAGE_PROTECT:
//                    break;
//                case MAX_GRID_SECOND_AC_FRENCY_PROTECT:
//                    break;
//                case MAX_GRID_SECOND_SYNCHORNIZATION_RANGE:
//                    break;
//
//            }
            getData(0);
        }
        return true;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        switch (enum_item){
            case TLX_GRID_SECOND_PF_SETTING://PF设置
                pfSetting(position);
                break;
            case TLX_GRID_SECOND_FRENCY_WATT_SETTING://频率有功
                setFrencyWatt(position);
                break;
            case TLX_GRID_SECOND_VOLTAGE_SETTING://电压无功
                setVoltage(position);
                break;
            case TLX_GRID_SECOND_POWERE_START_SETTING:
                setRisingSlope(position);//上升斜率
                break;
            case TLX_GRID_SECOND_AC_VOLTAGE_PROTECT:
                setACVoltageProtect(position);//AC电压保护
                break;
            case TLX_GRID_SECOND_AC_FRENCY_PROTECT:
                setACFrencyProtect(position); //AC频率保护
                break;
            case TLX_GRID_SECOND_SYNCHORNIZATION_RANGE:
                setGridConnected(position);//并网范围
                break;

        }
    }

    /**
     * PF设置
     * @param pos
     */

    private void pfSetting(int pos){
        ALLSettingBean bean = usParamsetAdapter.getData().get(pos);
        String title = bean.getTitle();
        if (pos == 0) {
            setSelectItem(pos, title);
        } else {
            toOhterSetting = true;
            Intent intent = new Intent(this, TLXGridCodeThirdActivity.class);
            intent.putExtra("title", title);
            intent.putExtra("curpos", pos);
            ActivityUtils.startActivity(this, intent, false);
        }
    }


    /**
     * 频率有功
     * @param pos
     */

    private void setFrencyWatt(int pos) {
        ALLSettingBean bean = usParamsetAdapter.getData().get(pos);
        String title = bean.getTitle();
        float mul = bean.getMul();
        String hint = bean.getHint();
        setCommenInputValue(pos, title, hint, mul);
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
        setCommenInputValue(pos, title, hint, mul);
    }




    /**
     * 上升斜率设置
     * @param pos
     */

    private void setRisingSlope(int pos) {
        ALLSettingBean bean = usParamsetAdapter.getData().get(pos);
        String title = bean.getTitle();
        float mul = bean.getMul();
        String hint = bean.getHint();
        setCommenInputValue(pos, title, hint, mul);
    }



    /**
     * 上升斜率设置
     * @param pos
     */

    private void setACVoltageProtect(int pos) {
        ALLSettingBean bean = usParamsetAdapter.getData().get(pos);
        String title = bean.getTitle();
        float mul = bean.getMul();
        String hint = bean.getHint();
        setCommenInputValue(pos, title, hint, mul);
    }



    /**
     * AC频率保护
     * @param pos
     */

    private void setACFrencyProtect(int pos) {
        ALLSettingBean bean = usParamsetAdapter.getData().get(pos);
        String title = bean.getTitle();
        float mul = bean.getMul();
        String hint = bean.getHint();
        setCommenInputValue(pos, title, hint, mul);
    }



    /**
     * AC频率保护
     * @param pos
     */

    private void setGridConnected(int pos) {
        ALLSettingBean bean = usParamsetAdapter.getData().get(pos);
        String title = bean.getTitle();
        float mul = bean.getMul();
        String hint = bean.getHint();
        setCommenInputValue(pos, title, hint, mul);
    }



    /**
     *
     * @param position
     * @param title
     * @param hint
     * @param mul
     */

    private void setCommenInputValue(int position, String title, String hint, float mul) {
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





    @Override
    public void oncheck(boolean check, int position) {

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
