package com.growatt.shinetools.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.shinetools.R;
import com.growatt.shinetools.bean.DryStartTimeBean;

import java.util.List;

public class DryPeriodAdapter extends BaseQuickAdapter<DryStartTimeBean, BaseViewHolder> {
    public DryPeriodAdapter(int layoutResId, @Nullable List<DryStartTimeBean> data) {
        super(layoutResId, data);
    }

    public DryPeriodAdapter(@Nullable List<DryStartTimeBean> data) {
        super(data);
    }

    public DryPeriodAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, DryStartTimeBean item) {
        helper.setText(R.id.tv_time_name,item.getTitle());
        //使能
        helper.setChecked(R.id.sw_enable,item.isEnable());
        //时间段
        helper.setText(R.id.tv_time_period,item.getTime());
    }
}
