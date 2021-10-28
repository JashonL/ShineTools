package com.growatt.shinetools.module.localbox.max.config;

import android.content.Intent;
import android.os.Handler;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.growatt.shinetools.R;
import com.growatt.shinetools.adapter.MaxSettingAdapter;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.modbusbox.Arith;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.MaxWifiParseUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.module.eventMsg.EventFreshMsg;
import com.growatt.shinetools.module.localbox.max.bean.MaxSettingBean;
import com.growatt.shinetools.module.localbox.mintool.TLXParamCountry2Activity;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.BindView;

public class MaxQuicksettingActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener,
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

    private MenuItem item;
    private MaxSettingAdapter usParamsetAdapter;
    private SocketManager manager;
    private int currentPos = 1;//当前请求项
    private int type = 0;//0：读取  1：设置
    private List<int[]> nowSetItem = new ArrayList<int[]>();
    private int nowIndex = 0;


    @Override
    protected int getContentView() {
        return R.layout.activity_max_quick_set;
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

    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        //快速设置项
        List<MaxSettingBean> settingList
                = MaxConfigControl.getSettingList(MaxConfigControl.MaxSettingEnum.MAX_QUICK_SETTING, this);
        usParamsetAdapter.replaceData(settingList);
        connetSocket();

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMin(@NonNull EventFreshMsg bean) {
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
            getInvdate();
        }

        @Override
        public void connectFail() {
            manager.disConnectSocket();
            MyControl.showJumpWifiSet(MaxQuicksettingActivity.this);
        }

        @Override
        public void sendMsgFail() {
            manager.disConnectSocket();
            MyControl.showTcpDisConnect(MaxQuicksettingActivity.this, getString(R.string.disconnet_retry),
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
                switch (currentPos) {
                    case 1:
                        getLCDLanguge();
                        break;
                    case 2:
                        getAddress();
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
            case 1://逆变器时间
                LogUtil.i("解析时间");
                parserTimeData(bs);
                break;
            case 2://LCD语言
                //解析int值
                LogUtil.i("解析语言");
                int value0 = MaxWifiParseUtil.obtainValueOne(bs);
                parserLcdData(value0);
                break;
            case 3://通信地址
                LogUtil.i("解析通信地址");
                int value1 = MaxWifiParseUtil.obtainValueOne(bs);
                parserAddress(value1);
                break;

        }
        usParamsetAdapter.notifyDataSetChanged();
    }


    /**
     * 请求获取逆变器的时间
     */
    private void getInvdate() {
        LogUtil.i("请求获取逆变器时间");
        getData(1);
    }

    /**
     * 解析逆变器时间
     *
     * @param bs
     */
    private void parserTimeData(byte[] bs) {
        try {
            int year = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 0, 0, 1));
            int month = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 1, 0, 1));
            int day = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 2, 0, 1));
            int hour = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 3, 0, 1));
            int min = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 4, 0, 1));
            int seconds = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 5, 0, 1));
            //更新ui
            StringBuilder sb = new StringBuilder()
                    .append(year).append("-")
                    .append(month).append("-")
                    .append(day).append(" ")
                    .append(hour).append(":")
                    .append(min).append(":")
                    .append(seconds);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timeS = sb.toString();
            Date date = sdf.parse(timeS);
            String timeStr = sdf.format(date);
            usParamsetAdapter.getData().get(1).setValueStr(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    /**
     * 请求获取LCD语言
     */
    private void getLCDLanguge() {
        LogUtil.i("请求获取语言");
        getData(2);
    }


    /**
     * 获取读取的值并解析，部分特殊处理
     *
     * @param value
     * @return
     */
    public void parserLcdData(int value) {
        List<MaxSettingBean> data = usParamsetAdapter.getData();
        if (data.size() > 2) {
            MaxSettingBean bean = data.get(2);
            String[] items = bean.getItems();
            if (items != null && items.length > value) {
                String lcd = items[value];
                usParamsetAdapter.getData().get(2).setValueStr(lcd);
            } else {
                usParamsetAdapter.getData().get(2).setValueStr(String.valueOf(value));
            }
        }

    }


    /**
     * 请求获取通信地址
     */
    private void getAddress() {
        LogUtil.i("请求获取通信地址");
        getData(3);
    }


    public void parserAddress(int read) {
        List<MaxSettingBean> data = usParamsetAdapter.getData();
        if (data.size() > 3) {
            MaxSettingBean bean = data.get(3);
            float mMul = bean.getMul();
            String unit = bean.getUnit();
            String value = read * ((int) mMul) + unit;
            usParamsetAdapter.getData().get(3).setValueStr(value);
        }

    }


    private void getData(int pos) {
        type = 0;
        currentPos = pos;
        List<MaxSettingBean> data = usParamsetAdapter.getData();
        if (data.size() > pos) {
            MaxSettingBean bean = data.get(pos);
            int[] funs = bean.getFuns();
            manager.sendMsg(funs);
        }
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.right_action) {
            getInvdate();
        }
        return true;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        MaxSettingBean bean = usParamsetAdapter.getData().get(position);
        switch (position) {
            case 0:
                Intent intent = new Intent(mContext, TLXParamCountry2Activity.class);
                intent.putExtra("type", 0);
                intent.putExtra("title", String.format("%s%s", getString(R.string.m国家安规), ""));
                ActivityUtils.startActivity(this, intent, false);
                break;
            case 1:
                setInvTime();
                break;
            case 2:
                setLcd();
                break;
            case 3:
                setInputValue(bean.getTitle(), "1~254");
                break;
            case 4:
                //断开连接
                manager.disConnectSocket();
                Intent intent1 = new Intent(mContext, MaxAFCIAcitivity.class);
                intent1.putExtra("type", 0);
                intent1.putExtra("title", bean.getTitle());
                ActivityUtils.startActivity(this, intent1, false);
                break;

        }
    }

    @Override
    public void oncheck(boolean check, int position) {

    }


    /**
     * 设置逆变器时间
     */
    private void setInvTime() {
        try {
            DateUtils.showTotalTime(this, new DateUtils.SeletctTimeListeners() {
                @Override
                public void seleted(String date) {

                }

                @Override
                public void ymdHms(int year, int month, int day, int hour, int min, int second) {

                    List<MaxSettingBean> data = usParamsetAdapter.getData();
                    if (data.size() > 1) {
                        MaxSettingBean bean = data.get(1);
                        type = 1;
                        int[][] doubleFunset = bean.getDoubleFunset();
                        if (year > 2000) {
                            doubleFunset[0][2] = year - 2000;
                        } else {
                            doubleFunset[0][2] = year;
                        }
                        doubleFunset[1][2] = month + 1;
                        doubleFunset[2][2] = day;
                        doubleFunset[3][2] = hour;
                        doubleFunset[4][2] = min;
                        doubleFunset[5][2] = second;
                        //更新ui
                        StringBuilder sb = new StringBuilder()
                                .append(year).append("-");
                        if (month + 1 < 10) {
                            sb.append("0");
                        }
                        sb.append(month + 1).append("-");
                        if (day < 10) {
                            sb.append("0");
                        }
                        sb.append(day).append(" ");
                        if (hour < 10) {
                            sb.append("0");
                        }
                        sb.append(hour).append(":");
                        if (min < 10) {
                            sb.append("0");
                        }
                        sb.append(min).append(":");
                        if (second < 10) {
                            sb.append("0");
                        }
                        sb.append(second);
                        usParamsetAdapter.getData().get(1).setValueStr(sb.toString());
                        usParamsetAdapter.notifyDataSetChanged();


                        nowSetItem.clear();
                        nowSetItem.addAll(Arrays.asList(doubleFunset));
                        nowIndex = 0;


                        type = 1;
                        int[] funs = doubleFunset[0];
                        manager.sendMsg(funs);

                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     *
     */


    private void setLcd() {
        List<MaxSettingBean> data = usParamsetAdapter.getData();
        if (data.size() > 2) {
            MaxSettingBean bean = data.get(2);
            String[] items = bean.getItems();
            List<String> selects = new ArrayList<>(Arrays.asList(items));

            new CircleDialog.Builder()
                    .setTitle(getString(R.string.android_key499))
                    .setWidth(0.7f)
                    .setGravity(Gravity.CENTER)
                    .setMaxHeight(0.5f)
                    .setItems(selects, (parent, view1, pos, id) -> {
                        usParamsetAdapter.getData().get(2).setValueStr(selects.get(pos));
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

    private void setInputValue(String title, String hint) {
        CircleDialogUtils.showInputValueDialog(MaxQuicksettingActivity.this, title,
                hint, "", value -> {
                    double result = Double.parseDouble(value);
                    String pValue = value + "";
                    usParamsetAdapter.getData().get(3).setValueStr(pValue);
                    usParamsetAdapter.getData().get(3).setValue(String.valueOf(result));
                    usParamsetAdapter.notifyDataSetChanged();

                    List<MaxSettingBean> data = usParamsetAdapter.getData();
                    if (data.size() > 3) {
                        MaxSettingBean bean = data.get(3);
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
    protected void onDestroy() {
        super.onDestroy();
        manager.disConnectSocket();
        EventBus.getDefault().unregister(this);
    }
}
