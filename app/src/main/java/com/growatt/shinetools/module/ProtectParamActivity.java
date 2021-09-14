package com.growatt.shinetools.module;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.growatt.shinetools.R;
import com.growatt.shinetools.adapter.ProtectParamAdapter;
import com.growatt.shinetools.base.BaseNetWorkActivity;
import com.growatt.shinetools.module.localbox.max.bean.ProtectParamBean;
import com.growatt.shinetools.module.localbox.max.bean.ProtectParamResultBean;
import com.growatt.shinetools.modbusbox.SocketClientUtil;
import com.growatt.shinetools.module.account.LoginActivity;
import com.growatt.shinetools.utils.BtnDelayUtil;
import com.growatt.shinetools.utils.InternetUtils;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.Mydialog;
import com.growatt.shinetools.utils.RequestCallback;
import com.growatt.shinetools.utils.ShineToolsApi;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ProtectParamActivity extends BaseNetWorkActivity implements Toolbar.OnMenuItemClickListener{
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.header_title)
    LinearLayout headerTitle;



    private String[] titles;
    private String[] values;
    private String[] units;
    private ProtectParamAdapter mAdapter;
    private List<ProtectParamBean> mList;
    private int nowRead = 0;
    private int jumpType = 0;//默认是本地调试；0：本地调试；1：远程设置
    //读取命令功能码（功能码，开始寄存器，结束寄存器）
    private int[][] funs;
    private   MenuItem item;

    @Override
    protected int getContentView() {
        return R.layout.activity_protect_param;
    }

    @Override
    protected void initViews() {
        initToobar(toolbar);

        toolbar.inflateMenu(R.menu.comment_right_menu);
        item = toolbar.getMenu().findItem(R.id.right_action);
        item.setTitle(R.string.android_key816);
        toolbar.setOnMenuItemClickListener(this);
    }

    @Override
    protected void initData() {
        init();

    }


    private void init() {
        jumpType = getIntent().getIntExtra("jumpType", 0);
        tvTitle.setText(R.string.保护参数);
        toolbar.inflateMenu(R.menu.comment_right_menu);
        toolbar.getMenu().findItem(R.id.right_action).setTitle(R.string.m370读取);
        funs = new int[][]{
                {3, 1, 1}
        };
        titles = new String[]{
                "一级过压保护点"
                , "一级过压保护时间"
                , "二级过压保护点"
                , "二级过压保护时间"
                , "一级欠压保护点"
                , "一级欠压保护时间"
                , "二级欠压保护点"
                , "二级欠压保护时间"
                , "一级过频保护点"
                , "一级过频保护时间"
                , "二级过频保护点"
                , "二级过频保护时间"
                , "一级欠频保护点"
                , "一级欠频保护时间"
                , "二级欠频保护点"
                , "二级欠频保护时间"

        };
        values = new String[]{
                "280.0", "2000", "297.0", "50"
                , "187.0", "2000", "110.0", "100"
                , "50.20", "120000", "50.50", "200"
                , "49.50", "600000", "48.00", "200"
        };
        units = new String[]{
                "V", "ms", "V", "ms"
                , "V", "ms", "V", "ms"
                , "Hz", "ms", "Hz", "ms"
                , "Hz", "ms", "Hz", "ms"
        };
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mList = new ArrayList<>();
        mAdapter = new ProtectParamAdapter(R.layout.item_protect_param, mList);
        mRecyclerView.setAdapter(mAdapter);
        if (jumpType == 0) {
            List<ProtectParamBean> newList = new ArrayList<>();
            for (int i = 0; i < titles.length; i++) {
                ProtectParamBean bean = new ProtectParamBean();
                bean.setTitle(titles[i]);
                bean.setUnit(units[i]);
                bean.setValue(values[i]);
                newList.add(bean);
            }
            mAdapter.replaceData(newList);
        } else {
            getProtectionParameters();
        }
    }

    public void getProtectionParameters() {


        ShineToolsApi.getProtectionParameters(mContext, String.valueOf(getLanguage()),  new RequestCallback((LoginActivity) mContext));

    }




    //连接对象:用于读取数据
    private SocketClientUtil mClientUtilRead;

    //读取寄存器的值
    private void readRegisterValue() {
        Mydialog.Show(mContext);
        mClientUtilRead = SocketClientUtil.connectServer(mHandlerRead);
    }

    /**
     * 读取寄存器handle
     */
    Handler mHandlerRead = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                //发送信息
                case SocketClientUtil.SOCKET_SEND:
                    BtnDelayUtil.sendMessageNoClick(this, 3000);
                    byte[] sendBytesR = SocketClientUtil.sendMsgToServer(mClientUtilRead, funs[0]);
                    LogUtil.i("发送读取：" + SocketClientUtil.bytesToHexString(sendBytesR));
                    break;
                //接收字节数组
                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    BtnDelayUtil.receiveMessageNoClick(this);
                    byte[] bytes = (byte[]) msg.obj;
                    LogUtil.i("接收读取：" + SocketClientUtil.bytesToHexString(bytes));
                    //设置值
                    ProtectParamBean item = mAdapter.getItem(nowRead);
                    item.setContent(item.getValue());
                    mAdapter.notifyDataSetChanged();
                    nowRead++;
                    if (nowRead < mAdapter.getItemCount()) {
                        this.sendEmptyMessageDelayed(SocketClientUtil.SOCKET_SEND, 1000);
                    } else {
                        Mydialog.Dismiss();
                        toast(R.string.all_success);
                        //关闭连接
                        SocketClientUtil.close(mClientUtilRead);
                        BtnDelayUtil.refreshFinish();
                    }
                    break;
                default:
                    BtnDelayUtil.dealTLXBtn(this, what, mContext, toolbar);
                    break;

            }
        }
    };
    /**
     * 读取寄存器handle
     */
    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100:
                    //设置值
                    ProtectParamBean item = mAdapter.getItem(nowRead);
                    item.setContent(item.getValue());
                    mAdapter.notifyDataSetChanged();
                    nowRead++;
                    if (nowRead < mAdapter.getItemCount()) {
                        if (!InternetUtils.isNetworkAvailable(ProtectParamActivity.this)) {
                            nowRead = 0;
                            Mydialog.Dismiss();
                            toast(R.string.Xutil_network_no_open);
                            return;
                        }
                        mHandler.sendEmptyMessageDelayed(100, 1000);
                    } else {
                        Mydialog.Dismiss();
                        toast(R.string.all_success);
                    }
                    break;
            }
        }
    };





    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.right_action:
            if (jumpType == 1) {
                if (mAdapter.getItemCount() == 0) {
                    return true;
                }
                //判断有无网络连接
                if (!InternetUtils.isNetworkAvailable(ProtectParamActivity.this)) {
                    toast(R.string.Xutil_network_no_open);
                    return true;
                } else {
                    nowRead = 0;
                    Mydialog.Show(this);
                    mHandler.sendEmptyMessageDelayed(100, 1000);
                }
            }
            if (jumpType == 0) {
                //判断能否通信
                nowRead = 0;
                readRegisterValue();
            }
            break;
        }
        return true;
    }

    @Override
    public void startView() {
        Mydialog.Show(this);
    }

    @Override
    public void loadingView() {

    }

    @Override
    public void successView(String json) {
        try {
            ProtectParamResultBean bean = new Gson().fromJson(json, ProtectParamResultBean.class);
            if (bean != null && bean.getObj() != null && bean.getObj().size() > 0) {
                mAdapter.replaceData(bean.getObj());
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void errorView(String errorMsg) {

    }
}
