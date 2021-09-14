package com.growatt.shinetools.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.shinetools.R;
import com.growatt.shinetools.bean.UsBdcInfoBean;

import java.util.List;

public class UsInfoAdapter extends BaseQuickAdapter<UsBdcInfoBean, BaseViewHolder> {
    public UsInfoAdapter(int layoutResId, @Nullable List<UsBdcInfoBean> data) {
        super(layoutResId, data);
    }

    public UsInfoAdapter(@Nullable List<UsBdcInfoBean> data) {
        super(data);
    }

    public UsInfoAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, UsBdcInfoBean item) {
        helper.setText(R.id.tv_title,item.getTitle());
        helper.setText(R.id.tv_value,item.getValue());
    }
}
