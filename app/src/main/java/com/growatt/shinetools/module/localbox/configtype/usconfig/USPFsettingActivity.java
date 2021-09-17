package com.growatt.shinetools.module.localbox.configtype.usconfig;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import com.growatt.shinetools.ShineToosApplication;
import com.growatt.shinetools.adapter.UsSettingAdapter;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.bean.USDebugSettingBean;
import com.growatt.shinetools.bean.UsSettingConstant;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.MaxWifiParseUtil;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.utils.ActivityUtils;
import com.growatt.shinetools.utils.BtnDelayUtil;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.Mydialog;
import com.growatt.shinetools.widget.GridDivider;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;

import static com.growatt.shinetools.constant.GlobalConstant.END_USER;
import static com.growatt.shinetools.constant.GlobalConstant.KEFU_USER;
import static com.growatt.shinetools.constant.GlobalConstant.MAINTEAN_USER;
import static com.growatt.shinetools.modbusbox.SocketClientUtil.SOCKET_RECEIVE_BYTES;
import static com.growatt.shinetools.module.localbox.configtype.usconfig.USConfigTypeAllActivity.KEY_OF_ITEM_SETITEMSINDEX;

public class USPFsettingActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener,
        UsSettingAdapter.OnChildCheckLiseners, Toolbar.OnMenuItemClickListener {
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

    private UsSettingAdapter usParamsetAdapter;
    //设置项
    private String[] pfSetting;
    private String[] pfSettingRegister;

    //读取数据
    private int[][] funs;
    private boolean isReceiveSucc = false;
    private int count = 0;

    //设置功能码集合（功能码，寄存器，数据）
    private int[][][] funsSet;
    private int[] nowSet;

    private int user_type = KEFU_USER;

    @Override
    protected int getContentView() {
        return R.layout.activity_us_pf_setting;
    }

    @Override
    protected void initViews() {
        initToobar(toolbar);
        tvTitle.setText(R.string.android_key2924);
        toolbar.setOnMenuItemClickListener(this);

        rvSystem.setLayoutManager(new LinearLayoutManager(this));
        usParamsetAdapter = new UsSettingAdapter(new ArrayList<>(), this);
        int div = (int) getResources().getDimension(R.dimen.dp_1);
        GridDivider gridDivider = new GridDivider(ContextCompat.getColor(this, R.color.white), div, div);
        rvSystem.addItemDecoration(gridDivider);
        rvSystem.setAdapter(usParamsetAdapter);
        usParamsetAdapter.setOnItemClickListener(this);
    }

    @Override
    protected void initData() {
        user_type = ShineToosApplication.getContext().getUser_type();

        List<USDebugSettingBean> newlist = new ArrayList<>();

        if (user_type == END_USER || user_type == MAINTEAN_USER) {

            pfSetting = new String[]{getString(R.string.m392PF限制功率因数) + "1~4"};

            pfSettingRegister = new String[]{
                    "", "", "", "", "", "", "", "", ""};

            for (int i = 0; i < pfSetting.length; i++) {
                USDebugSettingBean bean = new USDebugSettingBean();
                bean.setTitle(pfSetting[i]);
                int itemType = UsSettingConstant.SETTING_TYPE_NEXT;
                bean.setItemType(itemType);
                bean.setRegister(pfSettingRegister[i]);
                newlist.add(bean);
            }


        } else {

            pfSetting = new String[]{getString(R.string.m405运行PF为1), getString(R.string.m402感性PF), getString(R.string.m399感性载率)
                    , getString(R.string.m400容性载率), getString(R.string.m401容性PF), getString(R.string.m默认PF曲线),
                    getString(R.string.m422无功曲线切入切出电压), getString(R.string.m391PF限制负载百分比点) + "1~4", getString(R.string.m392PF限制功率因数) + "1~4"};

            pfSettingRegister = new String[]{"", "", ""
                    , "", "", "", "", ""
                    , ""};

            for (int i = 0; i < pfSetting.length; i++) {
                USDebugSettingBean bean = new USDebugSettingBean();
                bean.setTitle(pfSetting[i]);
                int itemType = 0;
                switch (i) {
                    case 0://运行PF为1
                    case 5://默认PF曲线
                        itemType = UsSettingConstant.SETTING_TYPE_SWITCH;
                        break;
                    case 1://感性PF
                    case 2://感性载率
                    case 3://容性载率
                    case 4://容性PF
                    case 6://PF曲线切入/切出电压
                    case 7://PF限制负载百分比点1~4
                    case 8://PF限值1~4
                        itemType = UsSettingConstant.SETTING_TYPE_NEXT;
                        break;
                }

                bean.setItemType(itemType);
                bean.setRegister(pfSettingRegister[i]);
                newlist.add(bean);
            }


            funs = new int[][]{
                    {3, 89, 89},//开关机0
                    {3, 89, 89},//PV输入模式
            };

            //设置功能码集合（功能码，寄存器，数据）
            funsSet = new int[][][]{
                    {{6, 89, 0}, {6, 89, 1}}//开关机0
                    , {{6, 89, 0}, {6, 89, 1}}
            };
        }


        usParamsetAdapter.replaceData(newlist);


        if (funs == null || funs.length == 0) {

        } else {
            refresh();
        }
    }


    /**
     * 刷新界面
     */
    private void refresh() {
        Mydialog.Show(this);
        connectSendMsg();
    }

    /**
     * 真正的连接逻辑
     */
    private void connectSendMsg() {
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

    /**
     * 读取寄存器handle
     */
    /**
     * 连接处理器
     */
    StringBuilder sb = new StringBuilder();
    private String uuid;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
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
                    mHandler.sendEmptyMessageDelayed(100, 3000);

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
                        if (count < funs.length - 1) {
                            count++;
                            mHandler.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                        } else {
                            count = 0;
                            //关闭连接
                            SocketClientUtil.close(mClientUtil);
                            refreshFinish();
                        }

                        LogUtil.i("接收消息:" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        count = 0;
                        //关闭连接
                        SocketClientUtil.close(mClientUtil);
                        Mydialog.Dismiss();
                    }
                    break;
                case SocketClientUtil.SOCKET_CONNECT:
                    text = "socket已连接";
                    break;
                case SocketClientUtil.SOCKET_SEND:
                    if (count < funs.length) {
                        sendMsg(mClientUtil, funs[count]);
                    }
                    break;
                case 100://恢复按钮点击
                    String myuuid = (String) msg.obj;
                    if (uuid.equals(myuuid) && !isReceiveSucc) {
                        toast("接收消息超时，请重试");
                    }
                    refreshFinish();
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
     * 解析数据
     *
     * @param bytes
     * @param count
     */
    private void parseMax(byte[] bytes, int count) {
        //移除外部协议
        byte[] bs = RegisterParseUtil.removePro17(bytes);
        //解析int值
        int value = MaxWifiParseUtil.obtainValueOne(bs);
        switch (count) {
            case 0:
                usParamsetAdapter.getData().get(0).setValue(String.valueOf(value));
                break;
            case 1:
                usParamsetAdapter.getData().get(5).setValue(String.valueOf(value));
                break;
        }
        usParamsetAdapter.notifyDataSetChanged();
    }


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


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        int type = 0;
        if (user_type == END_USER || user_type == MAINTEAN_USER) {
            if (position == 0) {
                type = 6;
            }
        } else {
            switch (position) {
                case 1:
                    type = 0;
                    break;
                case 2:
                    type = 1;
                    break;
                case 3:
                    type = 2;
                    break;
                case 4:
                    type = 3;
                    break;
                case 6:
                    type = 4;
                    break;
                case 7:
                    type = 5;
                    break;
                case 8:
                    type = 6;
                    break;
            }
        }

        if (user_type != END_USER && user_type != MAINTEAN_USER) {
            if (position == 0 || position == 5) return;
        }

        String title = usParamsetAdapter.getData().get(position).getTitle();
        Intent intent = new Intent(USPFsettingActivity.this, USAllSettingItemActivity.class);
        intent.putExtra(KEY_OF_ITEM_SETITEMSINDEX, type);
        intent.putExtra("bartitle", title);
        ActivityUtils.startActivity(USPFsettingActivity.this, intent, false);

    }

    @Override
    public void oncheck(boolean check, int position) {
        int value = check ? 1 : 0;
        switch (position) {
            case 0://开关逆变器
                nowSet = funsSet[0][value];
                connectServerWrite();
                break;
            case 5:
                nowSet = funsSet[2][value];
                connectServerWrite();
                break;
        }

        usParamsetAdapter.getData().get(position).setValue(String.valueOf(value));
        usParamsetAdapter.notifyDataSetChanged();
    }

    //连接对象:用于写数据
    private SocketClientUtil mClientUtilW;

    private void connectServerWrite() {
        Mydialog.Show(mContext);
        mClientUtilW = SocketClientUtil.connectServer(mHandlerW);
    }

    /**
     * 写寄存器handle
     */
    //当前发送的字节数组
    private byte[] sendBytes;
    Handler mHandlerW = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessageWrite(this);
                    sendBytes = sendMsg(mClientUtilW, nowSet);
                    LogUtil.i("发送写入：" + SocketClientUtil.bytesToHexString(sendBytes));
                    break;
                //接收字节数组
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //检测内容正确性
                        boolean isCheck = MaxUtil.checkReceiverFull(bytes);
                        if (isCheck) {
                            //移除外部协议
//                            byte[] bs = RegisterParseUtil.removePro17Fun6(bytes);
//                            //解析int值
//                            int value0 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 1, 0, 1));
//                            //更新ui
//                            mTvContent1.setText(readStr + ":" + value0);
                            toast(R.string.all_success);
                        } else {
                            toast(R.string.all_failed);
                        }
                        LogUtil.i("接收写入：" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //关闭tcp连接
                        SocketClientUtil.close(mClientUtilW);
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
