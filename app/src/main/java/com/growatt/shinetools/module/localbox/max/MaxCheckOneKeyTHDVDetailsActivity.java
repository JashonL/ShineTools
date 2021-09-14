package com.growatt.shinetools.module.localbox.max;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.growatt.shinetools.R;
import com.growatt.shinetools.adapter.MaxCheckOneKTHDVDetailAdapter;
import com.growatt.shinetools.base.DemoBase;
import com.growatt.shinetools.constant.GlobalConstant;
import com.growatt.shinetools.db.SqliteUtil;
import com.growatt.shinetools.modbusbox.bean.MaxCheckOneKTHDVDetailBean;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.Position;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Max本地：电网电压谐波详情
 */
public class MaxCheckOneKeyTHDVDetailsActivity extends DemoBase {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.headerView)
    View headerView;


    private List<MaxCheckOneKTHDVDetailBean> mList;
    private MaxCheckOneKTHDVDetailAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_max_check_one_key_thdvdetails);
        ButterKnife.bind(this);
        initHeaderView();
        initRecyclerView();
        initData();
    }
    private void initHeaderView() {
        setHeaderImage(headerView, R.drawable.icon_return, Position.LEFT, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setHeaderTitle(headerView, getString(R.string.m448一键诊断));
    }
    private void initData() {
        String listJson = SqliteUtil.getListJson(GlobalConstant.MAX_ONEKEY_LAST_DATA3);
        LogUtil.i("thdv:"+listJson);
        if (!TextUtils.isEmpty(listJson)){
            List<MaxCheckOneKTHDVDetailBean> newList = new ArrayList<>();
            String[] entriesStrs = listJson.split("_");
            for (int i=0;i<entriesStrs.length;i++){
                String[] entryStrs = entriesStrs[i].split(",");
                for (int j=0,len = entryStrs.length;j<len;j++){
                    MaxCheckOneKTHDVDetailBean bean = null;
                    if (i == 0){
                        bean = new MaxCheckOneKTHDVDetailBean();
                    }else {
                        if (newList.size() > j) {
                            bean = newList.get(j);
                        }
                    }
                    String[] split = entryStrs[j].split(":");
                    if (bean == null) return;
                    switch (i){
                        case 0:
                            bean.setNum(String.valueOf(j*2+3));
                            bean.setrStr(split[1]);
                            newList.add(bean);
                            break;
                        case 1:
                            bean.setsStr(split[1]);
                            break;
                        case 2:
                            bean.settStr(split[1]);
                            break;
                    }

                }
            }
            MaxCheckOneKTHDVDetailBean pos1 = new MaxCheckOneKTHDVDetailBean();
            pos1.setNum("1");pos1.setrStr("100");pos1.setsStr("100");pos1.settStr("100");
            newList.add(0,pos1);
            mAdapter.replaceData(newList);
        }

    }

    private void initRecyclerView() {
        mList = new ArrayList<>();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MaxCheckOneKTHDVDetailAdapter(R.layout.item_maxcheck_onekthdv_detail,mList);
        mRecyclerView.setAdapter(mAdapter);
    }
}
