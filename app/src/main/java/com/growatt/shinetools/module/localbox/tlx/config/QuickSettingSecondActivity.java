package com.growatt.shinetools.module.localbox.tlx.config;

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
import com.growatt.shinetools.bean.UsSettingConstant;
import com.growatt.shinetools.modbusbox.Arith;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.MaxWifiParseUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.module.localbox.max.bean.ALLSettingBean;
import com.growatt.shinetools.module.localbox.max.type.DeviceConstant;
import com.growatt.shinetools.socket.ConnectHandler;
import com.growatt.shinetools.socket.SocketManager;
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

public class QuickSettingSecondActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener,
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
    private int currentPos = 0;//???????????????
    private int type = 0;//0?????????  1?????????
    private SocketManager manager;
    //?????????????????????
    private boolean toOhterSetting = false;

    private String deviceType;

    private List<int[]> nowSetItem = new ArrayList<>();
    private int nowIndex = 0;

    @Override
    protected int getContentView() {
        return R.layout.activity_tlx_quick_setting;
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
        deviceType=getIntent().getStringExtra(DeviceConstant.KEY_DEVICE_TYPE);


        List<ALLSettingBean> list = new ArrayList<>();

        String[] titls = new String[]{getString(R.string.???????????????),
                getString(R.string.m????????????????????????),
                getString(R.string.android_key748)};
        String[] registers = new String[]{"(122)", "(123)", "(3000)"};

        int[] itemTypes = new int[]{
                UsSettingConstant.SETTING_TYPE_SELECT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
        };
        String[] hints = new String[]{
                "",
                "",
                ""
        };
        String[] register = new String[]{
                "",
                "",
                ""
        };

        float[] multiples = new float[]{
                1, 1, 1
        };

        String[] units = new String[]{
                "",
                "",
                ""
        };


        //?????????????????????????????????????????????????????????
        int powerLimitReg=3000;
        if (DeviceConstant.MOD_MID_MAC.equals(deviceType)){
            powerLimitReg=304;
        }
        int[][] funs = new int[][]{
                {3, 122, 122},//???????????????
                {3, 123, 123},//????????????????????????
                {3, powerLimitReg, powerLimitReg}//???????????????????????????????????????
        };


        int[][] funset = new int[][]{
                {6, 122, 0},
                {6, 123, 0},
                {6, powerLimitReg, 0}
        };




        String[][] items = new String[][]{
                {getString(R.string.??????????????????), getString(R.string.??????485???????????????),getString(R.string.??????CT?????????)},
                {},
                {}

        };
        if(DeviceConstant.MIC_MIN_TL_X_XE.equals(deviceType)){
            items = new String[][]{
                    {getString(R.string.??????????????????), getString(R.string.??????485???????????????),getString(R.string.??????CT?????????)},
                    {},
                    {}

            };
        }
        int[][] doubleFunset = new int[][]{
                {6, 45, -1},
                {6, 46, -1},
                {6, 47, -1}
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
            bean.setMul(multiples[i]);
            list.add(bean);
        }
        usParamsetAdapter.replaceData(list);
        connetSocket();
    }

    private void connetSocket() {
        //???????????????
        manager = new SocketManager(this);
        //??????????????????
        manager.onConect(connectHandler);
        //????????????TCP
        //??????????????????????????????
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
            MyControl.showJumpWifiSet(QuickSettingSecondActivity.this);
        }

        @Override
        public void sendMsgFail() {
            manager.disConnectSocket();
            MyControl.showTcpDisConnect(QuickSettingSecondActivity.this, getString(R.string.disconnet_retry),
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
                getNextData();
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



    //????????????????????????
    private void getNextData() {
        if (currentPos < usParamsetAdapter.getData().size() - 1) {
            getData(++currentPos);
        } else {
            Mydialog.Dismiss();
        }
    }

    private void getData(int pos) {
        type = 0;
        currentPos = pos;
        List<ALLSettingBean> data = usParamsetAdapter.getData();
        if (data.size() > pos) {
            ALLSettingBean bean = data.get(pos);
            LogUtil.i("-------------------????????????:" + bean.getTitle() + "----------------");
            int[] funs = bean.getFuns();
            manager.sendMsg(funs);
        }
    }



    /**
     * ??????????????????mtype????????????
     *
     * @param bytes
     */
    private void parseMax(byte[] bytes) {
        //??????????????????
        byte[] bs = RegisterParseUtil.removePro17(bytes);
        //??????int???
        switch (currentPos){
            case 0:
                ALLSettingBean bean = usParamsetAdapter.getData().get(0);
                String[] items = bean.getItems();
                int value1 = MaxWifiParseUtil.obtainValueOne(bs);
                bean.setValueStr(String.valueOf(value1));
                if (value1<items.length){
                    bean.setValueStr(items[value1]);
                }
                if (value1==3){
                    bean.setValueStr(items[2]);
                }

                break;
            case 1:
                parser(bs,1);
                break;
            case 2:
                parser(bs,2);
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



    @Override
    public boolean onMenuItemClick(MenuItem item) {
        getData(0);
        return true;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        ALLSettingBean bean = usParamsetAdapter.getData().get(position);
        String title = bean.getTitle();
        String hint = bean.getHint();
        float mul = bean.getMul();
        switch (position){
            case 0:
                setSelectItem(position,title);
                break;
            case 1:
            case 2:
                setInputValue(position, title, hint, mul);
                break;
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

                        int value=position;
                        if (position==2)value=position+1;
                        int[] funs = bean.getFunSet();
                        funs[2] = value;
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
                        //??????
                        type = 1;
                        int[] funs = bean.getFunSet();
                        funs[2] = Integer.parseInt(value);
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
