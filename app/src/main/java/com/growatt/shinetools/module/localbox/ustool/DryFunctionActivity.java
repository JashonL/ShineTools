package com.growatt.shinetools.module.localbox.ustool;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import com.google.gson.Gson;
import com.growatt.shinetools.R;
import com.growatt.shinetools.adapter.UsSettingAdapter;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.bean.DryStartTimeBean;
import com.growatt.shinetools.bean.USDebugSettingBean;
import com.growatt.shinetools.bean.UsSettingConstant;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.MaxWifiParseUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.utils.BtnDelayUtil;
import com.growatt.shinetools.utils.CircleDialogUtils;
import com.growatt.shinetools.utils.CommenUtils;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.Mydialog;
import com.growatt.shinetools.widget.LinearDivider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

import static com.growatt.shinetools.modbusbox.SocketClientUtil.SOCKET_RECEIVE_BYTES;

public class DryFunctionActivity extends BaseActivity implements Toolbar.OnMenuItemClickListener,
        UsSettingAdapter.OnChildCheckLiseners, BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.header_title)
    LinearLayout headerTitle;
    @BindView(R.id.rv_dry)
    RecyclerView rvDry;

    private MenuItem item;
    private UsSettingAdapter usParamsetAdapter;
    //设置项
    private String[] titles;
    private int[] itemTypes;
    //读取数据
    private int[] funs;

    //干接点工作模式
    private String[] bCtrlMode;
    //热水器启动模式
    private String[] bWaterHeaterOnType;
    //柴油机 启动方式
    private String[] bGeneratorOntype;
    //干接点运行状态
    private String[] bRunStatus;

    private List<DryStartTimeBean> dryTimeList = new ArrayList<>();

    private int[][] nowSet;

    private int[] setItem;

    @Override
    protected int getContentView() {
        return R.layout.activity_dry_function;
    }

    @Override
    protected void initViews() {
        initToobar(toolbar);
        tvTitle.setText(R.string.android_key750);
        toolbar.inflateMenu(R.menu.comment_right_menu);
        item = toolbar.getMenu().findItem(R.id.right_action);
        item.setTitle(R.string.m370读取);
        toolbar.setOnMenuItemClickListener(this);


        rvDry.setLayoutManager(new LinearLayoutManager(this));
        usParamsetAdapter = new UsSettingAdapter(new ArrayList<>(), this);
        int div = (int) getResources().getDimension(R.dimen.dp_1);
        LinearDivider linearDivider = new LinearDivider(this, LinearLayoutManager.VERTICAL, div, ContextCompat.getColor(this, R.color.white));
        rvDry.addItemDecoration(linearDivider);
        rvDry.setAdapter(usParamsetAdapter);
        usParamsetAdapter.setOnItemClickListener(this);


    }

    @Override
    protected void initData() {
        bCtrlMode = new String[]{
                getString(R.string.android_key1686),//禁止
                getString(R.string.android_key2157),//热水器
                getString(R.string.dynamo),//发电机
        };


        bWaterHeaterOnType = new String[]{
                getString(R.string.disable),//不使能
                getString(R.string.out_power),//按输出功率
                getString(R.string.time_period),//按时间段
                getString(R.string.mixed_mode)//混合模式

        };


        bGeneratorOntype = new String[]{
                getString(R.string.disable),//不使能
                getString(R.string.android_key2763),//手动
                getString(R.string.m电池参数),//按电池参数
        };


        bRunStatus = new String[]{
                getString(R.string.android_key839),//等待中
                getString(R.string.water_heateing),//热水器工作中
                getString(R.string.generator_working),//发电机工作中
                getString(R.string.function_close)//功能关闭
        };


        titles = new String[]{
                getString(R.string.dry_workmode),//干接点工作模式
                getString(R.string.water_heater_method),//热水器启动方式
                getString(R.string.starting_power),//启动功率
                getString(R.string.exit_power), //退出功率
                getString(R.string.start_extension_time),//启动延长时间
                getString(R.string.minimum_running_time), //最小运行时间
                getString(R.string.dry_contact_setting),//干接点时间段设置
                getString(R.string.diesel_start_mode), //柴油机启动方式
                getString(R.string.start_soc),//启动SOC
                getString(R.string.close_soc), //关闭SOC
                getString(R.string.start_voltage),//启动电压
                getString(R.string.close_voltage),//关闭电压
                getString(R.string.dry_run_status),//干接点运行状态
                getString(R.string.init_dry_setting) //初始化干接点设置参数

        };

        //item的显示类型
        itemTypes = new int[]{
                UsSettingConstant.SETTING_TYPE_SELECT,
                UsSettingConstant.SETTING_TYPE_SELECT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_NEXT,
                UsSettingConstant.SETTING_TYPE_SELECT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_ONLYREAD,
                UsSettingConstant.SETTING_TYPE_NEXT,

        };


        funs = new int[]{3, 700, 725};

        nowSet = new int[][]{
                {6, 700, -1},//干接点工作模式
                {6, 701, -1},//热水器启动方式
                {6, 702, -1},//启动功率
                {6, 703, -1},//启动延时时间
                {6, 704, -1},//最小运行时间
                {6, 705, -1},//退出功率
                {6, 706, -1},//启动时间点1
                {6, 707, -1},//退出时间点1
                {6, 708, -1},//启动时间点2
                {6, 709, -1},//退出时间点2
                {6, 710, -1},//启动时间点3
                {6, 711, -1},//退出时间点3
                {6, 720, -1},//柴油机启动方式
                {6, 721, -1},//启动SOC
                {6, 722, -1},//关闭SOC
                {6, 723, -1},//启动电压
                {6, 724, -1},//关闭电压
                {6, 725, -1},//干接点运行状态
                {6, 730, -1},//参数保存并生效

        };


        List<USDebugSettingBean> newlist = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            USDebugSettingBean bean = new USDebugSettingBean();
            bean.setTitle(titles[i]);
            bean.setItemType(itemTypes[i]);
            newlist.add(bean);
        }
        usParamsetAdapter.replaceData(newlist);


        refresh();

    }


    /**
     * 刷新界面
     */
    private void refresh() {
        connectSendMsg();
    }


    /**
     * 真正的连接逻辑
     */
    private void connectSendMsg() {
        Mydialog.Show(this);
        connectServer();
    }


    //连接对象
    private SocketClientUtil mClientUtil;

    private void connectServer() {
        mClientUtil = SocketClientUtil.newInstance();
        if (mClientUtil != null) {
            mClientUtil.connect(mHandler);
        }
    }


    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            String text = "";
            int what = msg.what;
            switch (what) {
                case SocketClientUtil.SOCKET_CLOSE:
                    break;
                case SocketClientUtil.SOCKET_OPEN:
                    break;
                case SocketClientUtil.SOCKET_SEND_MSG:
                    BtnDelayUtil.sendMessage(this);
                    String sendMsg = (String) msg.obj;
                    LogUtil.i("发送消息:" + sendMsg);
                    break;
                case SocketClientUtil.SOCKET_RECEIVE_MSG:
                    break;
                //接收字节数组
                case SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //检测内容正确性
                        boolean isCheck = ModbusUtil.checkModbus(bytes);
                        if (isCheck) {
                            //接收正确，开始解析
                            parseMax(bytes);
                        }
                        LogUtil.i("接收消息:" + SocketClientUtil.bytesToHexString(bytes));
                        //关闭连接
                        SocketClientUtil.close(mClientUtil);
                        refreshFinish();
                    } catch (Exception e) {
                        e.printStackTrace();
                        //关闭连接
                        SocketClientUtil.close(mClientUtil);
                        Mydialog.Dismiss();
                    }
                    break;
                case SocketClientUtil.SOCKET_CONNECT:
                    break;
                case SocketClientUtil.SOCKET_SEND:
                    sendMsg(mClientUtil, funs);
                    break;
                case 100://恢复按钮点击
                    break;
                case 101:
                    connectSendMsg();
                    break;
                default:
                    BtnDelayUtil.dealTLXBtn(this, what, 2500, mContext, toolbar);
                    break;
            }
        }
    };

    /**
     * 刷新完成
     */
    private void refreshFinish() {
        Mydialog.Dismiss();
    }

    /**
     * 根据命令以及起始寄存器发送查询命令
     *
     * @param clientUtil
     * @param sends
     * @return：返回发送的字节数组
     */
    private byte[] sendMsg(SocketClientUtil clientUtil, int[] sends) {
        if (clientUtil != null) {
            byte[] sendBytes = ModbusUtil.sendMsg(sends[0], sends[1], sends[2]);
            clientUtil.sendMsg(sendBytes);
            return sendBytes;
        } else {
            connectServer();
            return null;
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
        //解析
        //干接点工作模式
        int value = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 0, 0, 1));
        USDebugSettingBean bean = usParamsetAdapter.getData().get(0);
        bean.setValue(String.valueOf(value));
        if (value > bCtrlMode.length) {
            bean.setValueStr(String.valueOf(value));
        } else {
            bean.setValueStr(bCtrlMode[value]);
        }


        //热水器启动模式
        value = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 1, 0, 1));
        USDebugSettingBean bean1 = usParamsetAdapter.getData().get(1);
        bean1.setValue(String.valueOf(value));
        if (value > bWaterHeaterOnType.length) {
            bean1.setValueStr(String.valueOf(value));
        } else {
            bean1.setValueStr(bWaterHeaterOnType[value]);
        }


        //启动功率
        value = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 2, 0, 1));
        USDebugSettingBean bean2 = usParamsetAdapter.getData().get(2);
        bean2.setValue(String.valueOf(value));
        String s = value + "W";
        bean2.setValueStr(s);


        //启动延时时间
        value = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 3, 0, 1));
        USDebugSettingBean bean3 = usParamsetAdapter.getData().get(3);
        bean3.setValue(String.valueOf(value));
        String s3 = value + "Min";
        bean3.setValueStr(s3);

        //最小运行时间
        value = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 4, 0, 1));
        USDebugSettingBean bean4 = usParamsetAdapter.getData().get(4);
        bean4.setValue(String.valueOf(value));
        String s4 = value + "Min";
        bean4.setValueStr(s4);

        //退出功率
        value = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 5, 0, 1));

        USDebugSettingBean bean5 = usParamsetAdapter.getData().get(5);
        bean5.setValue(String.valueOf(value));
        String s5 = value + "Min";
        bean4.setValueStr(s5);


        DryStartTimeBean dryStartTimeBean = new DryStartTimeBean();

        //启动时间1
        value = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 6, 0, 1));
        dryStartTimeBean.setStartHour(value >> 8);
        dryStartTimeBean.setStartMin(value & 0b11111111);
        //高八位 小时
        String s_hour1 = CommenUtils.getDoubleNum(value >> 8);
        //低八位 分钟
        String s_min1 = CommenUtils.getDoubleNum(value & 0b11111111);


        //退出时间1
        value = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 7, 0, 1));
        dryStartTimeBean.setEndHour(value >> 8);
        dryStartTimeBean.setEndMin(value & 0b11111111);
        //高八位 小时
        String e_hour1 = CommenUtils.getDoubleNum(value >> 8);
        //低八位 分钟
        String e_min1 = CommenUtils.getDoubleNum(value & 0b11111111);


        String startTime = s_hour1+":"+s_min1;
        String endTime = e_hour1+":"+e_min1;
        String time=startTime+"-"+endTime;

        dryStartTimeBean.setStartTime(startTime);
        dryStartTimeBean.setEndTime(endTime);
        dryStartTimeBean.setTime(time);

        dryTimeList.add(dryStartTimeBean);


        DryStartTimeBean dryStartTimeBean2 = new DryStartTimeBean();
        //启动时间2
        value = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 8, 0, 1));
        dryStartTimeBean2.setStartHour(value >> 8);
        dryStartTimeBean2.setStartMin(value & 0b11111111);
        //高八位 小时
        String s_hour2 = CommenUtils.getDoubleNum(value >> 8);
        //低八位 分钟
        String s_min2 = CommenUtils.getDoubleNum(value & 0b11111111);

        //退出时间2
        value = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 9, 0, 1));
        dryStartTimeBean2.setEndHour(value >> 8);
        dryStartTimeBean2.setEndMin(value & 0b11111111);
        //高八位 小时
        String e_hour2 = CommenUtils.getDoubleNum(value >> 8);
        //低八位 分钟
        String e_min2 = CommenUtils.getDoubleNum(value & 0b11111111);


        String startTime2 = s_hour2+":"+s_min2;
        String endTime2 = e_hour2+":"+e_min2;
        String time2=startTime2+"-"+endTime2;

        dryStartTimeBean2.setStartTime(startTime2);
        dryStartTimeBean2.setEndTime(endTime2);
        dryStartTimeBean2.setTime(time2);
        dryTimeList.add(dryStartTimeBean2);


        DryStartTimeBean dryStartTimeBean3 = new DryStartTimeBean();
        //启动时间3
        value = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 10, 0, 1));
        dryStartTimeBean3.setStartHour(value >> 8);
        dryStartTimeBean3.setStartMin(value & 0b11111111);
        //高八位 小时
        String s_hour3 = CommenUtils.getDoubleNum(value >> 8);
        //低八位 分钟
        String s_min3 = CommenUtils.getDoubleNum(value & 0b11111111);

        //退出时间3
        value = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 11, 0, 1));
        dryStartTimeBean3.setEndHour(value >> 8);
        dryStartTimeBean3.setEndMin(value & 0b11111111);
        //高八位 小时
        String e_hour3 = CommenUtils.getDoubleNum(value >> 8);
        //低八位 分钟
        String e_min3 = CommenUtils.getDoubleNum(value & 0b11111111);

        String startTime3 = s_hour3+":"+s_min3;
        String endTime3 = e_hour3+":"+e_min3;
        String time3=startTime3+"-"+endTime3;

        dryStartTimeBean3.setStartTime(startTime3);
        dryStartTimeBean3.setEndTime(endTime3);
        dryStartTimeBean3.setTime(time3);

        dryTimeList.add(dryStartTimeBean3);


        //柴油机启动方式
        value = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 20, 0, 1));
        USDebugSettingBean bean12 = usParamsetAdapter.getData().get(12);
        bean12.setValue(String.valueOf(value));
        if (value > bGeneratorOntype.length) {
            bean12.setValueStr(String.valueOf(value));
        } else {
            bean12.setValueStr(bGeneratorOntype[value]);
        }

        //启动SOC
        value = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 21, 0, 1));
        USDebugSettingBean bean13 = usParamsetAdapter.getData().get(13);
        bean13.setValue(String.valueOf(value));
        String s13 = value + "%";
        bean13.setValueStr(s13);


        //关闭SOC
        value = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 22, 0, 1));
        USDebugSettingBean bean14 = usParamsetAdapter.getData().get(14);
        bean14.setValue(String.valueOf(value));
        String s14 = value + "%";
        bean14.setValueStr(s14);


        //启动电压
        value = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 23, 0, 1));
        USDebugSettingBean bean15 = usParamsetAdapter.getData().get(15);
        bean15.setValue(String.valueOf(value));
        String s15 = value + "V";
        bean15.setValueStr(s15);


        //关闭电压
        value = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 24, 0, 1));
        USDebugSettingBean bean16 = usParamsetAdapter.getData().get(16);
        bean16.setValue(String.valueOf(value));
        String s16 = value + "V";
        bean15.setValueStr(s16);


        //干接点运行状态
        value = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 25, 0, 1));
        USDebugSettingBean bean17 = usParamsetAdapter.getData().get(17);
        bean17.setValue(String.valueOf(value));
        if (value > bRunStatus.length) {
            bean17.setValueStr(String.valueOf(value));
        } else {
            bean17.setValueStr(bRunStatus[value]);
        }

        usParamsetAdapter.notifyDataSetChanged();
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.right_action:
                //读取寄存器的值
                refresh();
                break;
        }
        return true;
    }

    @Override
    public void oncheck(boolean check, int position) {

    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        USDebugSettingBean bean = usParamsetAdapter.getData().get(position);
        String title = bean.getTitle();
        int type = 0;

        switch (position) {
            case 0:
            case 1:
            case 7:
                type = 0;
                break;

            case 6:
                type = 1;
                break;

            case 12://干接点运行状态

                break;

            case 13://初始化干接点设置参数
                type=3;
                break;

            default:
                type = 2;
                break;
        }

        if (type == 0) {
            List<String> list;
            if (position == 0) {
                list = Arrays.asList(bCtrlMode);
            } else {
                list = Arrays.asList(bWaterHeaterOnType);
            }

            List<String> finalList = list;
            CircleDialogUtils.showCommentItemDialog(this, getString(R.string.android_key1809),
                    list, Gravity.CENTER, (parent, view1, pos, id) -> {
                        if (finalList.size() > pos) {
                            String text = finalList.get(pos);
                            usParamsetAdapter.getData().get(position).setValueStr(text);
                            usParamsetAdapter.getData().get(position).setValue(String.valueOf(pos));
                            usParamsetAdapter.notifyDataSetChanged();
                            //去设置
                            setItem = nowSet[position];
                            setItem[2] = pos;
                            writeRegisterValue();
                        }
                        return true;
                    }, null);
        }


        if (type==1){
            String jsonArray = new Gson().toJson(dryTimeList);
            Intent intent=new Intent(DryFunctionActivity.this,DryPeriodSettingActivity.class);
            intent.putExtra("timelist",jsonArray);
            startActivity(intent);

        }



        if (type == 2) {
            String hint = "";
            String unit = "%";
            switch (position) {
                case 2://启动功率
                    hint = "XE/XH:5~600  7-10k-X: 5~10000";
                    unit = "W";
                    break;

                case 3://退出功率
                    hint = "4000~6000";
                    unit = "W";
                    break;

                case 4://启动延时时间
                    hint = "0~99";
                    unit = "Min";
                    break;

                case 5://最小运行时间
                    hint = "3~500";
                    unit = "Min";
                    break;

                case 8://启动SOC
                    hint = "20~89";
                    unit = "%";
                    break;
                case 9://关闭SOC
                    hint = "30~99";
                    unit = "%";
                    break;
                case 10://启动电压
                    hint = "11.5~13.5";
                    unit = "V";
                    break;
                case 11://关闭电压
                    hint = "13.0~15.0";
                    unit = "V";
                    break;
            }

            CircleDialogUtils.showInputValueDialog(DryFunctionActivity.this, title,
                    hint, unit, value -> {
//                        switch (position) {
//                            case 2://启动功率
//
//                                break;
//                            case 3://退出功率
//
//
//
//                                break;
//
//                            case 4://启动延时时间
//                                break;
//
//                            case 5://最小运行时间
//                                break;
//
//                            case 8://启动SOC
//                                break;
//                            case 9://关闭SOC
//                                break;
//                            case 10://启动电压
//                                break;
//                            case 11://关闭电压
//                                break;
//                        }

                        double result = Double.parseDouble(value);
                        String pValue = value + "";
                        usParamsetAdapter.getData().get(position).setValueStr(pValue);
                        usParamsetAdapter.getData().get(position).setValue(String.valueOf(result));
                        usParamsetAdapter.notifyDataSetChanged();

                        //设置
                        setItem = nowSet[position];
                        setItem[2] = (int) result;
                        writeRegisterValue();

                    });
        }


        if (type==3){
            CircleDialogUtils.showCommentDialog(this, getString(R.string.android_key2263),
                    getString(R.string.confirm_init_dry_setting), getString(R.string.android_key1935), getString(R.string.android_key2152), Gravity.CENTER, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //设置
                            setItem = nowSet[position];
                            setItem[2] = 0xA5;
                            writeRegisterValue();
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    });
        }

    }


    //连接对象:用于设置数据
    private SocketClientUtil mClientUtilWriter;

    //设置寄存器的值
    private void writeRegisterValue() {
        Mydialog.Show(mContext);
        mClientUtilWriter = SocketClientUtil.connectServer(mHandlerWrite);
    }


    private byte[] sendBytes;

    Handler mHandlerWrite = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    if (setItem != null && setItem[2] != -1) {
                        BtnDelayUtil.sendMessageWrite(this);
                        sendBytes = sendMsg(mClientUtilWriter, setItem);
                        LogUtil.i("发送写入：" + SocketClientUtil.bytesToHexString(sendBytes));
                    }
                    break;
                //接收字节数组
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;

                        LogUtil.i("接收写入：" + SocketClientUtil.bytesToHexString(bytes));
                        //检测内容正确性
                        boolean isCheck = MaxUtil.checkReceiverFull(bytes);
                        if (isCheck) {
                            toast(R.string.all_success);
                        } else {
                            toast(R.string.all_failed);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //关闭tcp连接
                        SocketClientUtil.close(mClientUtilWriter);
                        BtnDelayUtil.refreshFinish();
                    }
                    break;
                default:
                    BtnDelayUtil.dealTLXBtnWrite(this, what, mContext, toolbar);
                    break;
            }
        }
    };


}
