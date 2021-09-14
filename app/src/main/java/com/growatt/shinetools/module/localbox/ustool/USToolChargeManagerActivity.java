package com.growatt.shinetools.module.localbox.ustool;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.growatt.shinetools.R;
import com.growatt.shinetools.adapter.MaxConfigMuiltAdapter;
import com.growatt.shinetools.base.DemoBase;
import com.growatt.shinetools.module.localbox.max.bean.MaxConfigBean;
import com.growatt.shinetools.module.localbox.mintool.TLXConfigType1Activity;
import com.growatt.shinetools.module.localbox.mintool.TLXConfigTypeSelectActivity;
import com.growatt.shinetools.utils.Position;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * tlx  充放电管理界面
 */
public class USToolChargeManagerActivity extends DemoBase {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.headerView)
    View headerView;
    private List<MaxConfigBean> mList;
    private MaxConfigMuiltAdapter mAdapter;
    private String[] titles;
    private String mTitle;
    private String[] registers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ustool_charge_manager);
        ButterKnife.bind(this);
        initIntent();
        initHeaderView();
        initString();
        initRecyclerView();
        initListener();
    }
    private void initIntent() {
        Intent mIntent = getIntent();
        if (mIntent != null){
            mTitle = mIntent.getStringExtra("title");
        }
    }
    private void initString() {
//        titles = new String[]{
//                getString(R.string.m充电功率百分比)
//                ,getString(R.string.m充电停止SOC)
//                ,getString(R.string.m放电功率百分比)
//                ,getString(R.string.m放电停止SOC)
//                ,getString(R.string.m设置充放电优先时间段)
////                ,getString(R.string.mCV电压)
////                ,getString(R.string.mCC电流)
//                ,String.format("AC %s",getString(R.string.m84充电使能))
//        };
//        registers = new String[]{
//                "(3047)"
//                ,"(3048)"
//                ,"(3036)"
//                ,"(3037)"
//                ,"(3038~3058)"
////                ,"(3030)"
////                ,"(3024)"
//                ,"(3049)"
//        };
        titles = new String[]{
                getString(R.string.m设置充放电优先时间段)
//                ,getString(R.string.mCV电压)
//                ,getString(R.string.mCC电流)
                , String.format("AC %s",getString(R.string.m84充电使能))
                ,getString(R.string.m充电功率百分比)
                ,getString(R.string.m充电停止SOC)
                ,getString(R.string.m放电功率百分比)
                ,getString(R.string.m放电停止SOC)
        };
        registers = new String[]{
                "(3125~3249)"
//                ,"(3030)"
//                ,"(3024)"
                ,"(3049)"
                ,"(3047)"
                ,"(3048)"
                ,"(3036)"
                ,"(3037)"
        };
    }

    private void initHeaderView() {
        setHeaderImage(headerView, -1, Position.LEFT, v -> finish());
        setHeaderTitle(headerView, mTitle);
    }


    private void initListener() {
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            Intent intent = null;
            Class clazz = null;
            int type = -1;
            int pos = position;
            switch (position){
                case 0:type = 10;pos = 0;break;
//                case 1:type = 1;pos = 23;break;
//                case 2:type = 1;pos = 24;break;
                case 1:type = 0;pos = 21;break;
                case 2:type = 1;pos = 25;break;
                case 3:type = 1;pos = 26;break;
                case 4:type = 1;pos = 27;break;
                case 5:type = 1;pos = 28;break;
            }
            switch (type){
                case 0:
                    clazz = TLXConfigTypeSelectActivity.class;
                    break;
                case 1:
                    clazz = TLXConfigType1Activity.class;
                    break;
                case 10:
                    clazz = USTimerSetActivity.class;
                    break;
            }
            if (clazz != null) {
                intent = new Intent(mContext, clazz);
                intent.putExtra("type", pos);
                intent.putExtra("title", String.format("%s%s",titles[position],registers[position]));
                jumpTo(intent, false);
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
//            bean.setTitle(String.format("%d.%s%s",i+1,titles[i],registers[i]));
            bean.setTitle(String.format("%d.%s",i+1,titles[i]));
            newList.add(bean);
        }
        adapter.setNewData(newList);
    }
}
