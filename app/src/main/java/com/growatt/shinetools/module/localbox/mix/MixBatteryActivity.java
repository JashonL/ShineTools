package com.growatt.shinetools.module.localbox.mix;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.growatt.shinetools.R;
import com.growatt.shinetools.adapter.TLXHLiwangAdapter;
import com.growatt.shinetools.base.DemoBase;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.modbusbox.bean.MaxDataBean;
import com.growatt.shinetools.modbusbox.bean.ToolStorageDataBean;
import com.growatt.shinetools.module.localbox.max.bean.TLXHLiwangBean;
import com.growatt.shinetools.utils.BtnDelayUtil;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.Mydialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MixBatteryActivity extends DemoBase {

    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private List<TLXHLiwangBean> mList;
    private TLXHLiwangAdapter mAdapter;
    private String[] titles;
    /**
     * 跳转信息
     */
    private MaxDataBean mBean;
    //读取命令功能码（功能码，开始寄存器，结束寄存器）
    private int[][] funs = {{4,1000,1124}};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mix_battery);
        ButterKnife.bind(this);
        initRecyclerView();
        EventBus.getDefault().register(this);
        if (mBean == null) mBean = new MaxDataBean();
        readRegisterValue();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMin(@NonNull MaxDataBean bean) {
        this.mBean = bean;
        refreshUI();
    }

    private void refreshUI() {
        ToolStorageDataBean storageBeen = mBean.getStorageBeen();
        List<String> contents = new ArrayList<>();
        contents.add(String.valueOf(storageBeen.getBmsStatus()));
        contents.add(String.valueOf(storageBeen.getBmsError()));
        contents.add(storageBeen.getSoc());
        contents.add(String.valueOf(storageBeen.getvBms2()));
        contents.add(String.valueOf(storageBeen.getaBms()));
        contents.add(String.valueOf(storageBeen.getTempBms()));
        contents.add(String.valueOf(storageBeen.getaChargeMax2()));
        contents.add(String.valueOf(storageBeen.getaDischargeMax()));
        initData(contents);
    }
    private void initRecyclerView() {
        titles = new String[]{
               "BmsStatus", "BmsError", "BmsSOC", "BmsBatteryVolt(V)"
               ,"BmsBatteryCurr(A)", "BmsBatteryTemp(℃)", "BmsMaxChgCurr(A)", "BmsMaxDisChgCurr(A)"
        };
        mTvTitle.setText(R.string.m电池参数);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mList = new ArrayList<>();
        mAdapter = new TLXHLiwangAdapter(mList);
        mRecyclerView.setAdapter(mAdapter);
        initData(null);
    }

    private void initData(List<String> contents) {
        List<TLXHLiwangBean> list = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            TLXHLiwangBean bean = new TLXHLiwangBean();
            bean.setType(TLXHLiwangAdapter.ITEM2);
            if (contents != null && i < contents.size()){
                bean.setrContent(contents.get(i));
            }else {
                bean.setrContent("--");
            }
            bean.setTitle(titles[i]);
            list.add(bean);
        }
        mAdapter.replaceData(list);
    }


    @OnClick(R.id.ivLeft)
    public void onViewClicked() {
        finish();
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
                    byte[] sendBytesR = sendMsg(mClientUtil, funs[0]);
                    LogUtil.i("发送读取：" + SocketClientUtil.bytesToHexString(sendBytesR));
                    break;
                //接收字节数组
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //检测内容正确性
                        boolean isCheck = ModbusUtil.checkModbus(bytes);
                        if (isCheck) {
                            toast(R.string.all_success);
                            RegisterParseUtil.parseInput1kT1124Mix(mBean,bytes);
                            refreshUI();
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
                    }
                    break;
                default:
                    BtnDelayUtil.dealMaxBtn(this,what,mContext);
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
            connectServer();
            return null;
        }
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
}
