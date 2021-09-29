package com.growatt.shinetools.module.localbox.ustool;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.growatt.shinetools.R;
import com.growatt.shinetools.adapter.UsInfoAdapter;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.bean.UsBdcInfoBean;
import com.growatt.shinetools.modbusbox.Arith;
import com.growatt.shinetools.modbusbox.MaxWifiParseUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.modbusbox.bean.MaxDataBean;
import com.growatt.shinetools.modbusbox.bean.MaxDataDeviceBean;
import com.growatt.shinetools.modbusbox.bean.ToolStorageDataBean;
import com.growatt.shinetools.module.localbox.ustool.bean.BDCInfoBean;
import com.growatt.shinetools.utils.BtnDelayUtil;
import com.growatt.shinetools.utils.CircleDialogUtils;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.Mydialog;
import com.mylhyl.circledialog.view.listener.OnLvItemClickListener;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;

import static com.growatt.shinetools.modbusbox.SocketClientUtil.SOCKET_RECEIVE_BYTES;

/**
 * 采集器根据03寄存器184号来判断当前并联的BDC台数，如果是0，
 * 仍然按照之前的字段范围进行读取并上传给服务器，如果大于0，
 * 则根据实际的台数来获取03寄存器5000开始的字段和04寄存器4000开始的字段
 */

public class USBDCParamActivity extends BaseActivity implements Toolbar.OnMenuItemClickListener{


    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.header_title)
    LinearLayout headerTitle;
    @BindView(R.id.tv_bdc_name)
    TextView tvBdcName;
    @BindView(R.id.tv_system_info)
    TextView tvSystemInfo;
    @BindView(R.id.tv_status_info)
    TextView tvStatusInfo;
    @BindView(R.id.tv_inner_info)
    TextView tvInnerInfo;
    @BindView(R.id.tv_battery_info)
    TextView tvBatteryInfo;

    @BindView(R.id.rlv_system)
    RecyclerView rlvSystem;
    @BindView(R.id.rlv_status_info)
    RecyclerView rlvStatusInfo;
    @BindView(R.id.rlv_inner_system)
    RecyclerView rlvInnerSystem;
    @BindView(R.id.rlv_battery_system)
    RecyclerView rlvBatterySystem;

   /* @BindView(R.id.tv_error)
    TextView tvError;*/




    private UsInfoAdapter mSystemAdapter;//系统信息
    private UsInfoAdapter mStatusAdapter;//状态信息
    private UsInfoAdapter mInnerAdapter;//内部信息
    private UsInfoAdapter mBatteryAdapter;//电池信息


    private int[] funs184 = {3, 184, 184};

    //BDC数量只有0的时候读取03寄存器
    private int[][] funs = {{3, 3085, 3124}, {4, 3165, 3233}};


    private List<String> bcdList = new ArrayList<>();

    private int nowPos=0;

    /**
     * 降额模式
     */
    private String[] deratModes;

    private MenuItem item;

    @Override
    protected int getContentView() {
        return R.layout.activity_us_bdc_param;
    }

    @Override
    protected void initViews() {
        initToobar(toolbar);
        tvTitle.setText(R.string.android_key1315);
        toolbar.inflateMenu(R.menu.comment_right_menu);
        item = toolbar.getMenu().findItem(R.id.right_action);
        item.setTitle(R.string.android_key816);
        toolbar.setOnMenuItemClickListener(this);

        //系统信息
        rlvSystem.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mSystemAdapter = new UsInfoAdapter(R.layout.item_bdc_info_h, new ArrayList<>());
        rlvSystem.setAdapter(mSystemAdapter);
        //状态信息
        rlvStatusInfo.setLayoutManager(new GridLayoutManager(this, 2));
        mStatusAdapter = new UsInfoAdapter(R.layout.item_bdc_info, new ArrayList<>());
        rlvStatusInfo.setAdapter(mStatusAdapter);
        //内部信息
        rlvInnerSystem.setLayoutManager(new GridLayoutManager(this, 2));
        mInnerAdapter = new UsInfoAdapter(R.layout.item_bdc_info, new ArrayList<>());
        rlvInnerSystem.setAdapter(mInnerAdapter);

        //电池信息
        rlvBatterySystem.setLayoutManager(new GridLayoutManager(this, 2));
        mBatteryAdapter = new UsInfoAdapter(R.layout.item_bdc_info, new ArrayList<>());
        rlvBatterySystem.setAdapter(mBatteryAdapter);

        String hint=getString(R.string.android_key1001)+"1";
        tvBdcName.setText(hint);
    }

    @Override
    protected void initData() {
        //系统信息
        String[] systemInfo = {getString(R.string.android_key2043), getString(R.string.android_key2296),
                getString(R.string.m模式), getString(R.string.M3版本), getString(R.string.BMS版本)
        };

        //状态信息
        String[] statusInfo = {getString(R.string.android_key1195), getString(R.string.android_key2777),
                getString(R.string.m电池充电功率), getString(R.string.m电池放电功率),
                getString(R.string.android_key414), getString(R.string.android_key2346),
                getString(R.string.m324故障码), getString(R.string.android_key716)
        };
        //状态信息
        String[] innerInfo = {getString(R.string.上电感电流), getString(R.string.下电感电流),
                getString(R.string.BAT电压), getString(R.string.BAT电流),
                getString(R.string.温度A), getString(R.string.温度B),
                getString(R.string.上BUS电压), getString(R.string.下BUS电压),
                getString(R.string.总BUS电压), getString(R.string.BDC降额模式)
        };

        //状态信息
        String[] batteryInfo = {getString(R.string.电池通信类型), getString(R.string.电池厂商),
//                getString(R.string.电池工作模式), getString(R.string.电池状态),
                getString(R.string.android_key233), getString(R.string.电池电流),
                getString(R.string.android_key336), getString(R.string.最大放电电流),
                "SOC", "SOH",
                getString(R.string.android_key1298), getString(R.string.m电池温度),
                getString(R.string.电池故障信息), getString(R.string.电池警告信息),
                getString(R.string.电池保护信息)
        };

        deratModes = new String[]{
                getString(R.string.android_key231), getString(R.string.系统故障), getString(R.string.系统告警),
                getString(R.string.电池最大充电电流), getString(R.string.充电NTC高温), getString(R.string.m保留),
                getString(R.string.充电SOC限制), getString(R.string.充电电池低温), getString(R.string.充电BUS电压过高),
                getString(R.string.电池充满)
                , getString(R.string.充电系统告警), getString(R.string.充电上位机设置), "", "", "", ""
                , getString(R.string.电池最大放电电流), getString(R.string.电池放电使能), getString(R.string.放电BUS电压过高),
                getString(R.string.放电NTC高温), getString(R.string.放电系统告警), getString(R.string.放电上位机设置), "", "", "", ""
        };


        List<UsBdcInfoBean> systemList = new ArrayList<>();
        for (String s : systemInfo) {
            UsBdcInfoBean bdcInfoBean = new UsBdcInfoBean();
            bdcInfoBean.setTitle(s);
            bdcInfoBean.setValue("");
            systemList.add(bdcInfoBean);
        }
        mSystemAdapter.replaceData(systemList);

        List<UsBdcInfoBean> statusList = new ArrayList<>();
        for (String s : statusInfo) {
            UsBdcInfoBean bdcInfoBean = new UsBdcInfoBean();
            bdcInfoBean.setTitle(s);
            bdcInfoBean.setValue("");
            statusList.add(bdcInfoBean);
        }
        mStatusAdapter.replaceData(statusList);


        List<UsBdcInfoBean> innerList = new ArrayList<>();
        for (String s : innerInfo) {
            UsBdcInfoBean bdcInfoBean = new UsBdcInfoBean();
            bdcInfoBean.setTitle(s);
            bdcInfoBean.setValue("");
            innerList.add(bdcInfoBean);
        }
        mInnerAdapter.replaceData(innerList);


        List<UsBdcInfoBean> batteryList = new ArrayList<>();
        for (String s : batteryInfo) {
            UsBdcInfoBean bdcInfoBean = new UsBdcInfoBean();
            bdcInfoBean.setTitle(s);
            bdcInfoBean.setValue("");
            batteryList.add(bdcInfoBean);
        }
        mBatteryAdapter.replaceData(batteryList);
        readRegisterValue();
    }

    //读取寄存器的值
    private void readRegisterValue() {
        connectServer();
    }

    //连接对象:用于读取数据
    private SocketClientUtil mClientUtil;

    private void connectServer() {
        Mydialog.Show(mContext);
        mClientUtil = SocketClientUtil.connectServer(mHandler);
    }

    /**
     * 读取寄存器handle
     */
    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessage(this);
                    byte[] sendBytesR = sendMsg(mClientUtil, funs184);
                    LogUtil.i("发送读取：" + SocketClientUtil.bytesToHexString(sendBytesR));
                    break;
                //接收字节数组
                case SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //检测内容正确性
                        boolean isCheck = ModbusUtil.checkModbus(bytes);
                        if (isCheck) {
                            //移除外部协议
                            byte[] bs = RegisterParseUtil.removePro17(bytes);
                            //解析int值
                            int value2 = MaxWifiParseUtil.obtainValueOne(bs);
                            LogUtil.i("bdc个数：" + value2);
                            initBdcList(value2);
                        } else {
                            toast(R.string.all_failed);
                        }
                        LogUtil.i("接收读取：" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //关闭tcp连接
                        SocketClientUtil.close(mClientUtil);
                        BtnDelayUtil.refreshFinish();


                        //默认读第0个
                        this.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setReadFuns(nowPos);
                            }
                        },1000);
                    }
                    break;
                default:
                    BtnDelayUtil.dealMaxBtn(this, what, mContext);
                    break;
            }
        }
    };


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
            return null;
        }
    }

    //BDC数量
    private void initBdcList(int num) {
        bcdList.clear();
        String name = getString(R.string.android_key1001)+"1";
        if (num == 0) {
            bcdList.add(name);
        } else {
            for (int i = 0; i < num; i++) {
                name = getString(R.string.android_key1001) + (i + 1);
                bcdList.add(name);
            }
        }


        //读取电池和bdc的数据
//        readBdcValue();
    }

    private void setReadFuns(int pos) {
        nowPos = pos;
        if (bcdList.size() > 1) {
            //读取03寄存器
            funs[0][1] = 3085 + 1915 + 40 * pos;
            funs[0][2] = 3124 + 1915 + 40 * pos;
            //读取04寄存器
            funs[1][1] = 3165 + 843 + 108 * pos;
            funs[1][2] = 3233 + 843 + 108 * pos;
        } else {
            //读取03寄存器
            funs[0][1] = 3000;
            funs[0][2] = 3124;
            //读取04寄存器
            funs[1][1] = 3125;
            funs[1][2] = 3249;
        }
        //去读取数据
        readBdcValue();
    }


    //读取寄存器的值
    private void readBdcValue() {
        toReadBdc();
    }

    //连接对象:用于读取数据
    private SocketClientUtil mReadBdcUtil;

    private void toReadBdc() {
        Mydialog.Show(mContext);
        mReadBdcUtil = SocketClientUtil.connectServer(bdcHadler);
    }

    /**
     * 读取寄存器handle
     */
    StringBuilder sb = new StringBuilder();
    private String uuid;
    private boolean isReceiveSucc = false;
    private int count = 0;//当前设置项的下标

    Handler bdcHadler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            String text = "";
            int what = msg.what;
            switch (what) {
                case SocketClientUtil.SOCKET_EXCETION_CLOSE:
                    String message = (String) msg.obj;
                    text = "异常退出：" + message;
                    break;
                case SocketClientUtil.SOCKET_CLOSE:
                    text = "连接关闭";
                    break;
                case SocketClientUtil.SOCKET_OPEN:
                    text = "连接成功";
                    break;
                case SocketClientUtil.SOCKET_SEND_MSG:
                    BtnDelayUtil.sendMessage(this);
                    //设置接收消息超时时间和唯一标示
                    uuid = UUID.randomUUID().toString();
                    Message msgSend = Message.obtain();
                    msgSend.what = 100;
                    msgSend.obj = uuid;
                    bdcHadler.sendEmptyMessageDelayed(100, 3000);

                    String sendMsg = (String) msg.obj;
                    LogUtil.i("发送消息:" + sendMsg);
//                    text = "发送消息成功";
////                    mEditText.setText("");
//                    sb.append("Client:<br>")
//                            .append(sendMsg)
//                            .append("<br><br>");
                    break;
                case SocketClientUtil.SOCKET_RECEIVE_MSG:
                    //设置接收消息成功
                    isReceiveSucc = true;
                    //设置请求按钮可用
//                    mHandler.sendEmptyMessage(100);

                    String recMsg = (String) msg.obj;
                    text = "接收消息成功";
                    sb.append("Server:<br>")
                            .append(recMsg)
                            .append("<br><br>");
//                    Log.i("receive" ,recMsg);
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
                            parseMax(bytes, count);
                        }

//                        接收正确，开始解析
//                        parseMax(bytes, count);

                        if (count < funs.length - 1) {
                            count++;
                            bdcHadler.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        } else {
                            //更新ui
                            refreshUI();
                            count = 0;
                            //关闭连接
                            SocketClientUtil.close(mReadBdcUtil);
                            refreshFinish();
                        }

                        LogUtil.i("接收消息:" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        String stackTraceInfo = getStackTraceInfo(e);
                        count = 0;
                        //关闭连接
                        SocketClientUtil.close(mReadBdcUtil);
                        Mydialog.Dismiss();
                    }
                    break;
                case SocketClientUtil.SOCKET_CONNECT:
                    text = "socket已连接";
                    break;
                case SocketClientUtil.SOCKET_SEND:
                    if (count < funs.length) {
                        sendMsg(mReadBdcUtil, funs[count]);
//                        toast("读取数据："+ Arrays.toString( funs[count]));
                    }
                    break;
                case 100://恢复按钮点击
                    String myuuid = (String) msg.obj;
                    if (uuid.equals(myuuid) && !isReceiveSucc) {

                    }
//                    refreshFinish();
                    break;
                case 101:
                    toReadBdc();
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
     * 解析数据
     *
     * @param bytes
     * @param count
     */
    //所有本地获取数据集合
    private MaxDataBean mMaxData = new MaxDataBean();

    private void parseMax(byte[] bytes, int count) {

        if (bcdList.size() > 1) {

            int resRegister_03 = funs[0][1];
            int resRegister_04 = funs[1][1];

            switch (count) {
                case 0://解析03数据
                    RegisterParseUtil.parseUSHold3085T3124(mMaxData, bytes, resRegister_03, nowPos);
                    break;
                case 1://解析04数据
                    RegisterParseUtil.parseUSHold3165T3233(mMaxData, bytes, resRegister_04, nowPos);
                    break;

            }
        } else {
            switch (count) {
                case 0://解析03数据
                    RegisterParseUtil.parseHold3kT3124(mMaxData, bytes);
                    break;
                case 1://解析04数据
                    RegisterParseUtil.parseInput3125T3249(mMaxData, bytes);
                    break;

            }
        }


    }


    /**
     * 更新ui数据
     */
    private void refreshUI() {
        if (mMaxData != null) {
            //刷新系统信息
            List<UsBdcInfoBean> sysData = mSystemAdapter.getData();
            BDCInfoBean bdcInfoBean = mMaxData.getBdcInfoBean();
            ToolStorageDataBean storageBeen = mMaxData.getStorageBeen();
            MaxDataDeviceBean deviceBeen = mMaxData.getDeviceBeen();
            //固件版本
            String bdcVervison = mMaxData.getBdcVervison();
            sysData.get(0).setValue(bdcVervison);

            //sn
            String bdc_serialnumber = bdcInfoBean.getBdc_serialnumber();
            sysData.get(1).setValue(bdc_serialnumber);

            //模式
            String bdcMode = bdcInfoBean.getBdc_mode();
            sysData.get(2).setValue(bdcMode);
            //M3版本
            String m3_version = bdcInfoBean.getM3_version();
            sysData.get(3).setValue(m3_version);
            //BMS版本
            String bms_version = bdcInfoBean.getBms_version();
            sysData.get(4).setValue(bms_version);
            mSystemAdapter.notifyDataSetChanged();


            //刷新状态信息
            List<UsBdcInfoBean> statusData = mStatusAdapter.getData();
            //状态
            int status = storageBeen.getStatusBDC();
            statusData.get(0).setValue(getTextByStatus(status));
            //工作模式
            int workModeBDC = storageBeen.getWorkModeBDC();
            statusData.get(1).setValue(getTextByMode(workModeBDC));

            //充电功率
            String chargePower = bdcInfoBean.getBattery_charge_power()+"W";
            statusData.get(2).setValue(chargePower);
            //放电功率
            String dischagePower = bdcInfoBean.getBattery_dischage_power()+"W";
            statusData.get(3).setValue(dischagePower);
            //总充电量
            String chage_total = bdcInfoBean.getChage_total()+"kWh";
            statusData.get(4).setValue(chage_total);

            //总放电量
            String dischargeTotal = bdcInfoBean.getDischarge_total()+"kWh";
            statusData.get(5).setValue(dischargeTotal);


            //故障告警
            int errCode = storageBeen.getErrorStorage();
            int warmCode = storageBeen.getWarmStorage();
            int errCodeSecond = storageBeen.getError2Storage();
            int warmCodeSecond = storageBeen.getWarm2Storage();
            //添加副码
            String errCodeStr = String.format("%d(%02d)", errCode, errCodeSecond);;
            String warnCodeStr = String.format("%d(%02d)", warmCode, warmCodeSecond);

            statusData.get(6).setValue(errCodeStr);
            statusData.get(7).setValue(warnCodeStr);
            mStatusAdapter.notifyDataSetChanged();

            //内部信息
            List<UsBdcInfoBean> innerData = mInnerAdapter.getData();
            //上电感电流
            String aBB = storageBeen.getaBB() +"A";
            innerData.get(0).setValue(aBB);
            //下电感电流
            String allC = storageBeen.getaLLC() +"A";
            innerData.get(1).setValue(allC);
            //BAT电压
            String vBat = storageBeen.getvBat() +"V";
            innerData.get(2).setValue(vBat);
            //BAT电流
            String aBat = storageBeen.getaBat() +"A";
            innerData.get(3).setValue(aBat);
            //温度A
            String tempA = storageBeen.getTempA() +"℃";
            innerData.get(4).setValue(tempA);
            //温度B
            String tempB = storageBeen.getTempB() +"℃";
            innerData.get(5).setValue(tempB);
            //上BUS电压
            String vBus2 = storageBeen.getvBus2() +"V";
            innerData.get(6).setValue(vBus2);
            //下BUS电压
            String vBus3 = storageBeen.getvBus3() +"V";
            innerData.get(7).setValue(vBus3);
            //总BUS电压
            String vBus1 = storageBeen.getvBus1() +"V";
            innerData.get(8).setValue(vBus1);
            //BDC降额模式
            int derateMode = deviceBeen.getDerateMode();
            String de = String.valueOf(deviceBeen.getDerateMode());
            if (deratModes.length > derateMode) {
                de = deratModes[derateMode];
            }
            innerData.get(9).setValue(de);
            mInnerAdapter.notifyDataSetChanged();
            //电池信息
            List<UsBdcInfoBean> batteryData = mBatteryAdapter.getData();

            //电池通信类型
//            String battery_type = bdcInfoBean.getBattery_type();
            String battery_type = getString(R.string.CAN通信);
            batteryData.get(0).setValue(battery_type);

            //电池厂商
            String company = bdcInfoBean.getBattery_company();
            batteryData.get(1).setValue(company);

       /*     //电池工作模式
            int workType = storageBeen.getBmsWorkType();
            batteryData.get(2).setValue(String.valueOf(workType));

            //电池状态
            int bmsStatus = storageBeen.getBmsStatus();
            batteryData.get(3).setValue(getTextByBatteryStatus(bmsStatus));*/

            //电池电压
            double vBms = storageBeen.getvBms();
            String s = vBms +"V";
            batteryData.get(2).setValue(s);

            //电池电流
            double aBms = storageBeen.getaBms();
            String aBms_str = aBms +"A";
            batteryData.get(3).setValue(aBms_str);

            //最大充电电流
            double aChargeMax = storageBeen.getaChargeMax();
            if (aChargeMax>32767){
                double v = aChargeMax - 65535;
                aChargeMax= Arith.mul(v,0.01);
            }else {
                aChargeMax= Arith.mul(aChargeMax,0.01);
            }

            String aChargeMax_str = aChargeMax +"A";
            batteryData.get(4).setValue(aChargeMax_str);

            //最大放电电流
            double aDischargeMax = storageBeen.getAdisChargeMax();
            if (aDischargeMax>32767){
                double v = aDischargeMax - 65535;
                aDischargeMax= Arith.mul(v,0.01);
            }else {
                aDischargeMax= Arith.mul(aDischargeMax,0.01);
            }

            String aDischargeMax_str = aDischargeMax +"A";
            batteryData.get(5).setValue(aDischargeMax_str);
            //SOC
            String soc = storageBeen.getSoc();
            batteryData.get(6).setValue(String.valueOf(soc));

            //SOH
            int soh = storageBeen.getSoh();
            String soh_str=soh+"%";
            batteryData.get(7).setValue(soh_str);

            //Vcv
            double vCv = storageBeen.getvCV();
            String vCv_str=vCv+"V";
            batteryData.get(8).setValue(vCv_str);
            //电池温度
            String tempBms = storageBeen.getTempBms();
            batteryData.get(9).setValue(tempBms);

            //电池故障信息
            int bmsError1 = storageBeen.getBmsError1();
            int bmsError2 = storageBeen.getBmsError2();
            String bmsError = bmsError1 + "-" + bmsError2;
            batteryData.get(10).setValue(bmsError);
            //电池警告信息
            int bmsWarining = storageBeen.getBmsWarining();
            int bmsWarining2 = storageBeen.getBmsWarining2();
            int bmsWarining3 = storageBeen.getBmsWarining3();
            String bmsWarning = bmsWarining + "-" + bmsWarining2 + "-" + bmsWarining3;
            batteryData.get(11).setValue(bmsWarning);

            //电池保护
            int bmsProtect1 = storageBeen.getBmsProtect1();
            int bmsProtect2 = storageBeen.getBmsProtect2();
            int bmsProtect3 = storageBeen.getBmsProtect3();
            String protect = bmsProtect1 + "-" + bmsProtect2 + "-" + bmsProtect3;
            batteryData.get(12).setValue(protect);
            mBatteryAdapter.notifyDataSetChanged();
        }


    }


    /**
     * 获取工作状态
     *
     * @param status
     * @return
     */
    public String getTextByStatus(int status) {
        String text = String.valueOf(status);
        switch (status) {
            case 0:
                text = getString(R.string.all_Waiting);
                break;
            case 1:
                text = getString(R.string.all_Normal);
                break;
            case 2:
                text = getString(R.string.all_Fault);
                break;
            case 3:
                text = getString(R.string.m226升级中);
                break;
        }
        return text;
    }


    /**
     * 获取工作状态
     *
     * @param workMode
     * @return
     */
    public String getTextByMode(int workMode) {
        String text = String.valueOf(workMode);
        switch (workMode) {
            case 1:
                text = getString(R.string.all_Charge);
                break;
            case 2:
                text = getString(R.string.all_Discharge);
                break;
        }
        return text;
    }


    public String getTextByBatteryStatus(int status) {
        String text = String.valueOf(status);
        switch (status) {
            case 0:
                text = getString(R.string.all_Standby);
                break;
            case 1:
                text = getString(R.string.all_Charge);
                break;
            case 2:
                text = getString(R.string.all_Discharge);
                break;
        }
        return text;
    }



    @OnClick(R.id.ll_bdc_select)
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.ll_bdc_select:
                if (bcdList.size()>1){
                    CircleDialogUtils.showCommentItemDialog(this, getString(R.string.m225请选择),bcdList , Gravity.CENTER, new OnLvItemClickListener() {
                        @Override
                        public boolean onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String s = bcdList.get(position);
                            tvBdcName.setText(s);
                            setReadFuns(position);
                            return true;
                        }
                    }, null);
                }


                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.right_action:
                readRegisterValue();
                break;
        }
        return false;
    }





    public static String getStackTraceInfo(Exception e) {

        StringWriter sw = null;
        PrintWriter pw = null;

        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            e.printStackTrace(pw);//将出错的栈信息输出到printWriter中
            pw.flush();
            sw.flush();

            return sw.toString();
        } catch (Exception ex) {

            return "发生错误";
        } finally {
            if (sw != null) {
                try {
                    sw.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (pw != null) {
                pw.close();
            }
        }

    }

}
