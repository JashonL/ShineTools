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
import com.growatt.shinetools.module.localbox.max.config.MaxConfigControl;
import com.growatt.shinetools.module.localbox.max.config.MaxGridCodeThirdActivity;
import com.growatt.shinetools.module.localbox.pfsetting.CapacitiveActivity;
import com.growatt.shinetools.module.localbox.pfsetting.InductiveActivity;
import com.growatt.shinetools.module.localbox.pfsetting.InductivePFActivity;
import com.growatt.shinetools.module.localbox.pfsetting.PFCurveActivity;
import com.growatt.shinetools.module.localbox.pfsetting.PFLimitActivity;
import com.growatt.shinetools.module.localbox.pfsetting.PFLimitLoadPointActivity;
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

public class SPHSPApfSettingActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener,
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

   private SPHSPAConfigControl.SphSpaSettingEnum enum_item;

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
        enum_item=SPHSPAConfigControl.SphSpaSettingEnum.SPA_SPH_PF_SETTING;
        //系统设置项
        List<ALLSettingBean> settingList
                = SPHSPAConfigControl.getSettingList(enum_item, this);
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
            MyControl.showJumpWifiSet(SPHSPApfSettingActivity.this);
        }

        @Override
        public void sendMsgFail() {
            manager.disConnectSocket();
            MyControl.showTcpDisConnect(SPHSPApfSettingActivity.this, getString(R.string.disconnet_retry),
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
                if (currentPos==0){
                    getData(1);
                }else {
                    Mydialog.Dismiss();
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
        parserPfSetting(bs);

        usParamsetAdapter.notifyDataSetChanged();
    }


    /**
     *PF设置读取解析
     */
    private void parserPfSetting(byte[] bytes){
        switch (currentPos){
            case 0:
                //解析int值
                LogUtil.i("运行PF为1:");
                parserItems(bytes, 0);
                break;
            case 1:
                //解析int值
                LogUtil.i("运行PF为1:");
                parserItems(bytes, 1);
                break;
        }

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
        pfSetting(position);
    }

    /**
     * PF设置
     * @param pos
     */

    private void pfSetting(int pos){
        ALLSettingBean bean = usParamsetAdapter.getData().get(pos);
        String title = bean.getTitle();
        String[] items = bean.getItems();
        if (pos != 0&&pos!=1) {
            toOhterSetting = true;
            Class zz=null;
            switch (pos){
                case 2://感性载率
                    zz= InductiveActivity.class;
                    break;
                case 3://容性载率
                    zz= CapacitiveActivity.class;
                    break;
                case 4://容性PF
                    zz= CapacitiveActivity.class;
                    break;
                case 5://感性PF
                    zz= InductivePFActivity.class;
                    break;
                case 6://PF曲线切入电压
                    zz= PFCurveActivity.class;
                    break;
                case 7://PF限制负载百分比点
                    zz= PFLimitLoadPointActivity.class;
                    break;
                case 8://PF限值
                    zz= PFLimitActivity.class;
                    break;

            }
            if (zz==null)return;
            Intent intent = new Intent(this, zz);
            intent.putExtra("title", title);
            intent.putExtra("curpos", pos);
            ActivityUtils.startActivity(this, intent, false);
        }else {
            CircleDialogUtils.showCommentDialog(this, getString(R.string.android_key2263),
                    title, getString(R.string.android_key1935), getString(R.string.android_key2152),
                    Gravity.CENTER, v -> {
                        int value = pos==0?0:2;
                        usParamsetAdapter.getData().get(0).setValueStr(items[value]);
                        usParamsetAdapter.getData().get(1).setValueStr(items[value]);
                        usParamsetAdapter.notifyDataSetChanged();
                        type = 1;
                        LogUtil.i("-------------------设置"+bean.getTitle()+"----------------");
                        int[] funSet = bean.getFunSet();
                        funSet[2]=value;
                        manager.sendMsg(funSet);
                    }, v -> {});
        }
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
