package com.growatt.shinetools.adapter;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.shinetools.R;
import com.growatt.shinetools.module.localbox.max.bean.MaxChildBean;

import java.util.List;

/**
 * Created by dg on 2017/10/20.
 */

public class MaxMainChildAdapter extends BaseQuickAdapter<MaxChildBean, BaseViewHolder> {
    public MaxMainChildAdapter(@LayoutRes int layoutResId, @Nullable List<MaxChildBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MaxChildBean item) {
        helper.setText(R.id.tvTitle,item.getTitle());
        helper.setText(R.id.tvContent,item.getContent());
    }
}
