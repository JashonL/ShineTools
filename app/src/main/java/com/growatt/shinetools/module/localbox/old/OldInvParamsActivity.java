package com.growatt.shinetools.module.localbox.old;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.growatt.shinetools.R;
import com.growatt.shinetools.adapter.MaxConfigMuiltAdapter;
import com.growatt.shinetools.base.DemoBase;
import com.growatt.shinetools.module.localbox.max.bean.MaxConfigBean;
import com.growatt.shinetools.module.localbox.mintool.OldInvConfigTypeSelectActivity;
import com.growatt.shinetools.utils.MyControl;
import com.growatt.shinetools.utils.Position;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OldInvParamsActivity extends DemoBase {
    String rightTitle ;
    private String note1;
    private String note2 ;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.headerView)
    View headerView;
    private List<MaxConfigBean> mList;
    private MaxConfigMuiltAdapter mAdapter;
    private String[] titles ;
    private String mTitle;
    private String[] registers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_max_param_set);
        ButterKnife.bind(this);
        initString();
        initIntent();
        initHeaderView();
        initRecyclerView();
        initListener();
    }
    private void initIntent() {
        Intent mIntent = getIntent();
        if (mIntent != null){
            mTitle = mIntent.getStringExtra("title");
        }
    }
    private void initHeaderView() {
        setHeaderImage(headerView, R.drawable.icon_return, Position.LEFT, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setHeaderTitle(headerView, mTitle);
        setHeaderTvTitle(headerView, rightTitle, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,OldInvModeSetActivity.class);
                intent.putExtra("title",rightTitle);
                jumpTo(intent,false);
            }
        });
    }
    private void initString() {
        rightTitle = getString(R.string.m374设置Model);
        note1 = getString(R.string.m443该项暂不能设置请设置Model);
        note2 = getString(R.string.m444该项暂不能设置);
        registers = new String[]{
                "", "(45~50)", "(15)", "(30)", "(17)",
                "(18)", "(119)", "(72)", "(112)", "(183)",
                "(73)", "(87)", "(88)", "(89)", "(28~29)",
                "(19/20)", "(21/22)", "(35/36)", "(37/38)",
                "(184/185)", "(188/189)", "(39/40)", "(41/42)", "(51/52)",
                "(53/54)", "(186/187)", "(55/56)", "(57/58)", "(190/191)"
                , "(7147~7148)"
        };
        titles = new String[]{
                getString(R.string.m国家安规),getString(R.string.mlocal逆变器时间),getString(R.string.m427语言),
                getString(R.string.m423通信地址),getString(R.string.m424启动电压),getString(R.string.m425启动时间),
                getString(R.string.m426故障恢复后重启延迟时间),getString(R.string.m428系统一周),getString(R.string.m429AC电压10分钟保护值),
                getString(R.string.m430PV电压高故障),getString(R.string.m431Modbus版本),getString(R.string.m432PID工作模式),
                getString(R.string.m433PID开关),getString(R.string.m434PID工作电压),getString(R.string.m435逆变器模块),
                "AC1" +getString(R.string.m437限制电压低高),"AC1" +getString(R.string.m438频率限制低高),
                "AC2" +getString(R.string.m437限制电压低高),"AC2" +getString(R.string.m438频率限制低高), "AC3" +getString(R.string.m437限制电压低高),
                "AC3" +getString(R.string.m438频率限制低高),getString(R.string.m439并网电压限制低高),getString(R.string.m440并网频率限制低高),
                String.format("AC%s1%s/%s",getString(R.string.m441电压限制时间),getString(R.string.m373低),getString(R.string.m372高)),
                String.format("AC%s2%s/%s",getString(R.string.m441电压限制时间),getString(R.string.m373低),getString(R.string.m372高)),
                String.format("AC%s3%s/%s",getString(R.string.m441电压限制时间),getString(R.string.m373低),getString(R.string.m372高)),
                String.format("AC%s1%s/%s",getString(R.string.m442频率限制时间),getString(R.string.m373低),getString(R.string.m372高)),
                String.format("AC%s2%s/%s",getString(R.string.m442频率限制时间),getString(R.string.m373低),getString(R.string.m372高)),
                String.format("AC%s3%s/%s",getString(R.string.m442频率限制时间),getString(R.string.m373低),getString(R.string.m372高))
                ,getString(R.string.修改总发电量)
        };
    }

    private void initListener() {
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            Class clazz = null;
            int type = -1;
            int pos = -1;
            switch (position){
                case 0:type = 5;pos = 0;break;
                case 1:type = 4;pos = 0;break;
                case 2:type = 0;pos = 12;break;
                case 3:type = 1;pos = 12;break;
                case 4:type = 1;pos = 9;break;
                case 5:type = 1;pos = 10;break;
                case 6:type = 1;pos = 11;break;
                case 7:type = 1;pos = 13;break;
                case 8:type = 1;pos = 14;break;
                case 9:type = 1;pos = 15;break;
                case 10:type = 1;pos = 16;break;
                case 11:type = 0;pos = 14;break;
                case 12:type = 0;pos = 15;break;
                case 13:type = 1;pos = 17;break;
//                case 14:type = 3;pos = 0;break;

                case 15:type = 2;pos = 7;break;
                case 16:type = 2;pos = 8;break;
                case 17:type = 2;pos = 9;break;
                case 18:type = 2;pos = 10;break;
                case 19:type = 2;pos = 11;break;
                case 20:type = 2;pos = 12;break;
                case 21:type = 2;pos = 13;break;
                case 22:type = 2;pos = 14;break;
                case 23:type = 2;pos = 15;break;
                case 24:type = 2;pos = 16;break;
                case 25:type = 2;pos = 17;break;
                case 26:type = 2;pos = 18;break;
                case 27:type = 2;pos = 19;break;
                case 28:type = 2;pos = 20;break;
                case 29:type = 3;pos = 1;break;
            }
            switch (type){
                case 0:
                    clazz = OldInvConfigTypeSelectActivity.class;
                    break;
                case 1:
                    clazz = OldInvConfigType1Activity.class;
                    break;
                case 2:
                    clazz = OldInvConfigType2Activity.class;
                    break;
                case 3:
                    clazz = OldInvConfigTypeHLActivity.class;
                    break;
                case 4:
                    clazz = OldInvConfigTypeTimeActivity.class;
                    break;
                case 5:
                    clazz = OldInvParamCountryActivity.class;
                    break;
            }
            if (clazz != null) {
                Intent intent = new Intent(mContext, clazz);
                intent.putExtra("type", pos);
                intent.putExtra("title", String.format("%s%s",titles[position],registers[position]));
                jumpTo(intent, false);
            }else {
                MyControl.circlerDialog(this,getString(R.string.该项暂不能进入),-1,false);
            }
        });

    }

    /**
     * 多布局定义
     */
    private void initRecyclerView() {
        mList = new ArrayList<>();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MaxConfigMuiltAdapter(R.layout.item_maxconfig_type0,mList);
        mRecyclerView.setAdapter(mAdapter);
        initData(titles,mAdapter);
    }

    private void initData(String[] titles, MaxConfigMuiltAdapter adapter) {
        List<MaxConfigBean> newList = new ArrayList<>();
        for (int i = 0;i < titles.length ;i++){
            MaxConfigBean bean = new MaxConfigBean();
//            bean.setTitle(i + "." +titles[i]);
            bean.setTitle(String.format("%d.%s%s",i+1,titles[i],registers[i]));
            newList.add(bean);
        }
        adapter.setNewData(newList);
    }
}
