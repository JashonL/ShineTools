package com.growatt.shinetools.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.shinetools.bean.UsBdcInfoBean;

import java.util.List;

public class UsTableInfoAdapter extends BaseQuickAdapter<UsBdcInfoBean, BaseViewHolder> {
    public UsTableInfoAdapter(int layoutResId, @Nullable List<UsBdcInfoBean> data) {
        super(layoutResId, data);
    }

    public UsTableInfoAdapter(@Nullable List<UsBdcInfoBean> data) {
        super(data);
    }

    public UsTableInfoAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, UsBdcInfoBean item) {

    }
}
