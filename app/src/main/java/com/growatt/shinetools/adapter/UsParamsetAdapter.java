package com.growatt.shinetools.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.shinetools.R;
import com.growatt.shinetools.module.localbox.ustool.bean.UsToolParamBean;

import java.util.List;

public class UsParamsetAdapter extends BaseQuickAdapter<UsToolParamBean, BaseViewHolder> {

    public UsParamsetAdapter(int layoutResId, @Nullable List<UsToolParamBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, UsToolParamBean item) {
        helper.setImageResource(R.id.iv_icon, item.getIcon());
        helper.setText(R.id.tv_name, item.getTitle());
    }
}
