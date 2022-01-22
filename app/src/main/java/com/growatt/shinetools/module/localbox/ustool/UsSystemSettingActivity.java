package com.growatt.shinetools.module.localbox.ustool;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import com.growatt.shinetools.module.localbox.configtype.usconfig.USConfigTypeAllActivity;
import com.growatt.shinetools.utils.ActivityUtils;
import com.growatt.shinetools.utils.BtnDelayUtil;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.Mydialog;
import com.growatt.shinetools.widget.GridDivider;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.view.listener.OnLvItemClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;

import static com.growatt.shinetools.constant.GlobalConstant.END_USER;
import static com.growatt.shinetools.constant.GlobalConstant.KEFU_USER;
import static com.growatt.shinetools.modbusbox.SocketClientUtil.SOCKET_RECEIVE_BYTES;

public class UsSystemSettingActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener,
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
    //读取数据
    private int[][] funs;
    private boolean isReceiveSucc = false;
    private int count = 0;
    //设置数据
    //设置功能码集合（功能码，寄存器，数据）
    private int[][][] funsSet;
    private int[] nowSet;
    private String[] pvModel;

    private MenuItem item;


    private int user_type = KEFU_USER;


    @Override
    protected int getContentView() {
        return R.layout.activity_us_system_setting;
    }

    @Override
    protected void initViews() {
        initToobar(toolbar);
        tvTitle.setText(R.string.android_key3091);
        toolbar.inflateMenu(R.menu.comment_right_menu);
        item = toolbar.getMenu().findItem(R.id.right_action);
        item.setTitle(R.string.m370读取);
        toolbar.setOnMenuItemClickListener(this);


        //
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
        //1.获取用户类型
        user_type = ShineToosApplication.getContext().getUser_type();
        //初始化列表数据
        String[] titls = new String[]{getString(R.string.m396开关逆变器), getString(R.string.m398有功功率百分比), getString(R.string.android_key961),
                getString(R.string.m防逆流设置), getString(R.string.android_key750), getString(R.string.android_key952), getString(R.string.android_key3111),
                getString(R.string.AFCI功能)};

        String[] registers = new String[]{"", "", "",
                "", "", "", "", ""};
        //去掉 2，4
        if (user_type == END_USER) {
            titls = new String[]{getString(R.string.m396开关逆变器), getString(R.string.m398有功功率百分比),
                    getString(R.string.m防逆流设置), getString(R.string.android_key952), getString(R.string.android_key3111),
                    getString(R.string.AFCI功能)};

            registers = new String[]{"", "", "",
                    "", "", "", "", ""};
        }


        List<USDebugSettingBean> newlist = new ArrayList<>();
        for (int i = 0; i < titls.length; i++) {
            USDebugSettingBean bean = new USDebugSettingBean();
            bean.setTitle(titls[i]);
            bean.setItemType(UsSettingConstant.SETTING_TYPE_SELECT);
            bean.setRegister(registers[i]);
            newlist.add(bean);
        }
        newlist.get(0).setItemType(UsSettingConstant.SETTING_TYPE_SWITCH);


        //2.根据用户类型初始化
        if (user_type == END_USER) {
            newlist.get(3).setItemType(UsSettingConstant.SETTING_TYPE_SWITCH);
            funs = new int[][]{
                    {3, 0, 0},//开关机0
//                    {3, 399, 399},//PV输入模式
                    {3, 235, 235},//N至PE检测功能使能
            };
        } else {
            newlist.get(5).setItemType(UsSettingConstant.SETTING_TYPE_SWITCH);
            funs = new int[][]{
                    {3, 0, 0},//开关机0
                    {3, 399, 399},//PV输入模式
                    {3, 235, 235},//N至PE检测功能使能
            };

        }

        usParamsetAdapter.replaceData(newlist);


        //设置功能码集合（功能码，寄存器，数据）
        funsSet = new int[][][]{
                {{6, 0, 0}, {6, 0, 1}}//开关机0
                , {{6, 399, 0}, {6, 399, 1}, {6, 399, 2}}
                , {{6, 235, 0}, {6, 235, 1}}
        };


        pvModel = new String[]{getString(R.string.Independent), getString(R.string.dc_source), getString(R.string.Parallel)};

        refresh();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        int itemIndex = 0;
        if (user_type == END_USER) {
            switch (position) {
                case 1:
                    itemIndex = 0;
                    break;
                case 2:
                    itemIndex = 1;
                    break;
                case 4:
                    itemIndex = 3;
                    break;
                case 5:
                    itemIndex = 4;
                    break;
            }
        } else {
            switch (position) {
                case 1:
                    itemIndex = 0;
                    break;
                case 2:
                    setItems();
                    break;
                case 3:
                    itemIndex = 1;
                    break;
                case 4:
                    itemIndex = 2;
                    break;
                case 6:
                    itemIndex = 3;
                    break;
                case 7:
                    itemIndex = 4;
                    break;
            }
        }

        if (user_type == END_USER) {
            if (position == 3 || position == 0) return;

        } else {
            if (position == 2 || position == 0 || position == 5) return;

        }


        USDebugSettingBean bean = usParamsetAdapter.getData().get(position);


  /*      if (user_type != END_USER && position == 4) {
            Intent intent = new Intent(this, DryFunctionActivity.class);
            intent.putExtra(USConfigTypeAllActivity.KEY_OF_ITEM_SETITEMSINDEX, itemIndex);
            intent.putExtra("title", bean.getTitle());
            ActivityUtils.startActivity(this, intent, false);
            return;
        }*/

        Intent intent = new Intent(this, USConfigTypeAllActivity.class);
        intent.putExtra(USConfigTypeAllActivity.KEY_OF_ITEM_SETITEMSINDEX, itemIndex);
        intent.putExtra("title", bean.getTitle());
        ActivityUtils.startActivity(this, intent, false);

    }


    private void setItems() {
        new CircleDialog.Builder()
                .setWidth(0.7f)
                .setMaxHeight(0.5f)
                .setGravity(Gravity.CENTER)
                .setTitle(getString(R.string.countryandcity_first_country))
                .setItems(pvModel, new OnLvItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (pvModel != null && pvModel.length > position) {
                            try {
                                usParamsetAdapter.getData().get(2).setValue(String.valueOf(position));
                                usParamsetAdapter.notifyDataSetChanged();
                                nowSet = funsSet[1][position];
                                connectServerWrite();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        return true;
                    }
                })
                .setNegative(getString(R.string.all_no), null)
                .show(getSupportFragmentManager());
    }


    @Override
    public void oncheck(boolean check, int position) {
        int value = check ? 1 : 0;

        if (user_type == END_USER) {
            switch (position) {
                case 0://开关逆变器
                    nowSet = funsSet[0][value];
                    connectServerWrite();
                    break;
                case 3:
                    nowSet = funsSet[2][value];
                    connectServerWrite();
                    break;
            }
        } else {
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
        }


        usParamsetAdapter.getData().get(position).setValue(String.valueOf(value));
        usParamsetAdapter.notifyDataSetChanged();
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
                        toast(R.string.android_key1134);
                    }
//                    refreshFinish();
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

        if (user_type == END_USER) {
            switch (count) {
                case 0:
                    usParamsetAdapter.getData().get(0).setValue(String.valueOf(value));
                    break;
                case 1:
                    usParamsetAdapter.getData().get(3).setValue(String.valueOf(value));
                    break;
            }
        } else {
            switch (count) {
                case 0:
                    usParamsetAdapter.getData().get(0).setValue(String.valueOf(value));
                    break;
                case 1:
                    String str = pvModel[value];
                    usParamsetAdapter.getData().get(2).setValueStr(str);
                    usParamsetAdapter.getData().get(2).setValue(String.valueOf(value));
                    break;
                case 2:
                    usParamsetAdapter.getData().get(5).setValue(String.valueOf(value));
                    break;
          /*      case 3:
                    String s = MaxWifiParseUtil.obtainRegistValueAscii(MaxWifiParseUtil.subBytes125(bs, 0, 0, 7));
                    usParamsetAdapter.getData().get(6).setValueStr(s);

                    break;*/
            }
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
}
