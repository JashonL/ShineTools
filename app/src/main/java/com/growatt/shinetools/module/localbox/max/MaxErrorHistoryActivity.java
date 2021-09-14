package com.growatt.shinetools.module.localbox.max;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.growatt.shinetools.R;
import com.growatt.shinetools.adapter.MaxErrorHisAdapter;
import com.growatt.shinetools.base.DemoBase;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.modbusbox.bean.MaxErrorBean;
import com.growatt.shinetools.utils.BtnDelayUtil;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.Mydialog;
import com.growatt.shinetools.utils.Position;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MaxErrorHistoryActivity extends DemoBase {
    @BindView(R.id.headerView)
    View headerView;
    @BindView(R.id.tvRight)
    TextView mTvRight;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private List<MaxErrorBean> mList;
    private MaxErrorHisAdapter mAdapter;
    int[][] autoFun = {{4,500,624},{4,625,749}};
    int autoCount = 0;
    //解析数据存储
    private List<MaxErrorBean> parseList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_max_error_history);
        ButterKnife.bind(this);
        initHeaderView();
        initRecyclerView();
        autoReadRegisterValue();
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mList = new ArrayList<>();
        mAdapter = new MaxErrorHisAdapter(R.layout.item_max_error_history,mList);
        mRecyclerView.setAdapter(mAdapter);
    }
    private void initHeaderView() {
        setHeaderImage(headerView, -1, Position.LEFT, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setHeaderTitle(headerView, getString(R.string.m325历史故障));
        setHeaderTvTitle(headerView, getString(R.string.wifilist_refresh), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoReadRegisterValue();
            }
        });
    }

    //连接对象:用于读取数据
    private SocketClientUtil mClientUtilRead;
    //读取寄存器的值
    private void autoReadRegisterValue() {
        Mydialog.Show(this);
        mClientUtilRead = SocketClientUtil.connectServerAuto(mHandlerReadAuto);
    }
    /**
     * 读取寄存器handle
     */
    Handler mHandlerReadAuto = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    if (autoCount < autoFun.length) {
                        BtnDelayUtil.sendMessage(this);
                        byte[] sendBytesR = SocketClientUtil.sendMsgToServer(mClientUtilRead, autoFun[autoCount]);
                        LogUtil.i("发送读取：" + SocketClientUtil.bytesToHexString(sendBytesR));
                    }
                    break;
                //接收字节数组
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessage(this);
                    try {
                        byte[] bytes = (byte[]) msg.obj;
                        //检测内容正确性
                        boolean isCheck = ModbusUtil.checkModbus(bytes);
                        if (isCheck) {
                            //接收正确，开始解析
                            parseMax(bytes, autoCount);
                            if (autoCount < autoFun.length - 1) {
                                autoCount++;
                                this.sendEmptyMessage(SocketClientUtil.SOCKET_SEND);
                            } else {
                                stopSocket();
                                //更新ui
                                if (parseList != null && parseList.size()>0){
                                    mAdapter.setNewData(parseList);
                                }
                            }
                        }else {//错误后停止
                            stopSocket();
                            toast(R.string.all_failed);
                        }
                        LogUtil.i("接收读取：" + SocketClientUtil.bytesToHexString(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        stopSocket();
                    }
                    break;
                default:
                    BtnDelayUtil.dealMaxBtn(this, what, mContext, mTvRight);
                    break;
            }
        }
    };

    /**
     * 停止socket,更新位置
     */
    public void stopSocket(){
        //获取位置更新为0
        autoCount = 0;
        //关闭tcp连接
        SocketClientUtil.close(mClientUtilRead);
    }
    /**
     * 解析数据
     *
     * @param bytes
     * @param count
     */

    private void parseMax(byte[] bytes, int count) {
        List<MaxErrorBean> newList = RegisterParseUtil.parseMax4T500T750(bytes);
        switch (count) {
            case 0:
                parseList.clear();
                parseList.addAll(newList);
                break;
            case 1:
                parseList.addAll(newList);
                break;
        }
    }
}
