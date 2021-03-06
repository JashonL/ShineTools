package com.growatt.shinetools.module.localbox.tlxh.config;

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
import com.growatt.shinetools.module.localbox.max.config.MaxActivePowerActivity;
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

public class TLXHSystemSettingActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener,
        DeviceSettingAdapter.OnChildCheckLiseners, Toolbar.OnMenuItemClickListener{
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
    private int currentPos = 1;//???????????????
    private int type = 0;//0?????????  1?????????
    private List<int[]> nowSetItem = new ArrayList<int[]>();
    private int nowIndex = 0;
    private int deviceType;


    //?????????????????????
    private boolean toOhterSetting = false;
/*    private String[] frequency;//????????????
    private String[] voltage;//????????????
    private String[] ctselect;//CT??????
    private String[] batterySelect;//????????????*/

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
        //???????????????
        List<ALLSettingBean> settingList
                = TLXHConfigControl.getSettingList(TLXHConfigControl.TlxSettingEnum.TLXH_SYSTEM_SETTING, this);
        usParamsetAdapter.replaceData(settingList);
    /*    frequency = new String[]{"50Hz", "60Hz"};
        voltage = new String[]{"230V", "208V", "240V"};
        ctselect=new String[]{"cWiredCT","cWirelessCT","METER"};
        batterySelect=new String[]{"Lithium","Lead-acid","other"};*/
        connetSocket();
    }



    private void connetSocket() {
        //???????????????
        manager = new SocketManager(this);
        //??????????????????
        manager.onConect(connectHandler);
        //????????????TCP
        //??????????????????????????????
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
            MyControl.showJumpWifiSet(TLXHSystemSettingActivity.this);
        }

        @Override
        public void sendMsgFail() {
            manager.disConnectSocket();
            MyControl.showTcpDisConnect(TLXHSystemSettingActivity.this, getString(R.string.disconnet_retry),
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
                if (currentPos < usParamsetAdapter.getData().size() - 1) {
                    getData(++currentPos);
                }else {
                    Mydialog.Dismiss();
                }

            } else {//??????
                //?????????????????????
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
     * ??????????????????mtype????????????
     *
     * @param bytes
     */
    private void parseMax(byte[] bytes) {
        //??????????????????
        byte[] bs = RegisterParseUtil.removePro17(bytes);
        //??????int???
        switch (currentPos) {
            case 0://???????????????
                //??????int???
                int value = MaxWifiParseUtil.obtainValueOne(bs);
                LogUtil.i("?????????????????????:"+value);
                usParamsetAdapter.getData().get(0).setValue(String.valueOf(value));
                break;
            case 1://?????????????????????
                //??????int???
                int content1 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3, 0, 1));
                LogUtil.i("???????????????????????????:"+content1);
                parserActiviPercent(content1);
                break;
            case 2://PV????????????
                int value2 = MaxWifiParseUtil.obtainValueOne(bs);
                LogUtil.i("??????pv????????????:"+value2);
                ALLSettingBean bean2 = usParamsetAdapter.getData().get(2);
                bean2.setValue(String.valueOf(value2));
                bean2.setValueStr(getReadValueReal(2,value2));
                break;
            case 3://???????????????

                break;
            case 4://N???PE??????????????????
                //??????int???
                int value4 = MaxWifiParseUtil.obtainValueOne(bs);
                LogUtil.i("??????N???PE??????????????????:"+value4);
                usParamsetAdapter.getData().get(4).setValue(String.valueOf(value4));
                break;
            case 5://???????????????
                //??????int???
                int value5 = MaxWifiParseUtil.obtainValueOne(bs);
                LogUtil.i("?????????????????????????????????:"+value5);
                ALLSettingBean bean5 = usParamsetAdapter.getData().get(5);
                bean5.setValue(String.valueOf(value5));
                bean5.setValueStr(getReadValueReal(5,value5));
                break;
            case 6://??????????????????
                //??????int???
                int value6 = MaxWifiParseUtil.obtainValueOne(bs);
                LogUtil.i("????????????????????????:"+value6);
                parserSafetyEnable(value6);
                break;

            case 7://???????????????????????????
                int value8 = MaxWifiParseUtil.obtainValueOne(bs);
                LogUtil.i("??????????????????????????????:"+value8);
                usParamsetAdapter.getData().get(7).setValue(String.valueOf(value8));
                break;
            case 8://ISLand??????
                //??????int???
                int value9 = MaxWifiParseUtil.obtainValueOne(bs);
                LogUtil.i("??????ISlland??????:"+value9);
                usParamsetAdapter.getData().get(8).setValue(String.valueOf(value9));
                break;


            case 9://??????????????????
                int value99 = MaxWifiParseUtil.obtainValueOne(bs);
                LogUtil.i("??????????????????:"+value99);
                ALLSettingBean bean99 = usParamsetAdapter.getData().get(9);
                bean99.setValue(String.valueOf(value99));
                bean99.setValueStr(getReadValueReal(9,value99));
                break;


            case 10://??????????????????
                int value10 = MaxWifiParseUtil.obtainValueOne(bs);
                LogUtil.i("??????????????????:"+value10);
                ALLSettingBean bean10 = usParamsetAdapter.getData().get(10);
                bean10.setValue(String.valueOf(value10));
                bean10.setValueStr(getReadValueReal(10,value10));
                break;





            case 11://????????????
                int value111 = MaxWifiParseUtil.obtainValueOne(bs);
                LogUtil.i("??????????????????:"+value111);
                ALLSettingBean bean111 = usParamsetAdapter.getData().get(11);
                bean111.setValue(String.valueOf(value111));
                bean111.setValueStr(getReadValueReal(11,value111));
                break;




            case 12://????????????
                //??????int???
                int value11 = MaxWifiParseUtil.obtainValueOne(bs);
                ALLSettingBean allSettingBean = usParamsetAdapter.getData().get(12);
                allSettingBean.setValue(String.valueOf(value11));
                String sValue = String.valueOf(value11);
                String[] frequency = allSettingBean.getItems();
                if (frequency.length > value11) {
                    sValue = frequency[value11];
                }
                allSettingBean.setValueStr(sValue);
                break;
            case 13://????????????
                //??????int???
                int value12 = MaxWifiParseUtil.obtainValueOne(bs);
                ALLSettingBean allSettingBean1 = usParamsetAdapter.getData().get(13);
                allSettingBean1.setValue(String.valueOf(value12));
                String[] voltage = allSettingBean1.getItems();
                String sValue12 = String.valueOf(value12);
                if (voltage.length > value12) {
                    sValue12 = voltage[value12];
                }
                allSettingBean1.setValueStr(sValue12);
                break;
   /*         case 14://CT??????
                //??????int???
                int value13 = MaxWifiParseUtil.obtainValueOne(bs);
                ALLSettingBean allSettingBean2 = usParamsetAdapter.getData().get(14);
                allSettingBean2.setValue(String.valueOf(value13));
                String sValue13 = String.valueOf(value13);
                String[] ctselect = allSettingBean2.getItems();
                if (ctselect.length > value13) {
                    sValue13 = ctselect[value13];
                }
                allSettingBean2.setValueStr(sValue13);
                break;*/

            case 14://????????????
                //??????int???
                int value14 = MaxWifiParseUtil.obtainValueOne(bs);
                ALLSettingBean allSettingBean3 = usParamsetAdapter.getData().get(14);
                allSettingBean3.setValue(String.valueOf(value14));
                String sValue14 = String.valueOf(value14);
                String[] batterySelect = allSettingBean3.getItems();
                if (batterySelect.length > value14) {
                    sValue14 = batterySelect[value14];
                }
                allSettingBean3.setValueStr(sValue14);
                break;


            case 15://????????????
                //??????int???
                int value15 = MaxWifiParseUtil.obtainValueOne(bs);
                ALLSettingBean allSettingBean15 = usParamsetAdapter.getData().get(15);
                allSettingBean15.setValue(String.valueOf(value15));
                String sValue15 = String.valueOf(value15);
                String[] workMode = allSettingBean15.getItems();
                if (workMode.length > value15) {
                    sValue15 = workMode[value15];
                }
                allSettingBean15.setValueStr(sValue15);
                break;


            case 16://????????????
                //??????int???
                int value16 = MaxWifiParseUtil.obtainValueOne(bs);
                ALLSettingBean allSettingBean16 = usParamsetAdapter.getData().get(16);
                allSettingBean16.setValue(String.valueOf(value16));
                String sValue16 = String.valueOf(value16);
                String[] gridType = allSettingBean16.getItems();
                if (gridType.length > value16) {
                    sValue16 = gridType[value16];
                }
                allSettingBean16.setValueStr(sValue16);
                break;
        }
        usParamsetAdapter.notifyDataSetChanged();
    }


    /**
     * ??????????????????????????????
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
        ALLSettingBean bean = usParamsetAdapter.getData().get(16);
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
            case 2: case 5: case 10:
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
            case 1://?????????????????????
                toOhterSetting=true;
                manager.disConnectSocket();
                Intent intent1 = new Intent(this, MaxActivePowerActivity.class);
                intent1.putExtra("title", bean.getTitle());
                ActivityUtils.startActivity(this, intent1, false);
                break;

            case 3://???????????????
                toOhterSetting=true;
                manager.disConnectSocket();
                Intent intent2 = new Intent(this, TLXHDryContactActivity.class);
                intent2.putExtra("title", bean.getTitle());
                ActivityUtils.startActivity(this, intent2, false);

                break;

            case 6://??????????????????
                setInputValue(position,title,hint);
                break;
            case 2://PV????????????
            case 5://???????????????????????????
            case 10:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
                setSelectItem(position,title);
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
        LogUtil.i("-------------------??????"+bean.getTitle()+"----------------");
        int[] funSet = bean.getFunSet();
        funSet[2]=value;
        manager.sendMsg(funSet);
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
                        //??????
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
        toOhterSetting = true;
        manager.disConnectSocket();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
