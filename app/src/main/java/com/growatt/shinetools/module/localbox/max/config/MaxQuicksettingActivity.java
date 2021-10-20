package com.growatt.shinetools.module.localbox.max.config;

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
import com.growatt.shinetools.adapter.MaxSettingAdapter;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.module.localbox.max.bean.MaxSettingBean;
import com.growatt.shinetools.socket.ConnectHandler;
import com.growatt.shinetools.socket.SocketManager;
import com.growatt.shinetools.utils.DateUtils;
import com.growatt.shinetools.widget.GridDivider;

import java.util.ArrayList;
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
        //快速设置项
        List<MaxSettingBean> settingList
                = MaxConfigControl.getSettingList(MaxConfigControl.MaxSettingEnum.MAX_QUICK_SETTING);
        usParamsetAdapter.replaceData(settingList);

        //初始化连接
        manager = new SocketManager();
        //设置连接监听
        manager.onConect(connectHandler);
        //开始连接TCP
        manager.connectSocket();
    }


    ConnectHandler connectHandler = new ConnectHandler() {
        @Override
        public void connectSuccess() {
            //发送请求获取数据
        }

        @Override
        public void connectFail() {

        }

        @Override
        public void disconnect() {

        }

        @Override
        public void sendMessage(String msg) {

        }

        @Override
        public void receiveMessage(String msg) {

        }

        @Override
        public void receveByteMessage(byte[] bytes) {

        }
    };


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        switch (position) {
            case 0:

                break;
            case 1:
                setInvTime();
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
            DateUtils.showTotalTime(this, date -> {

            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
