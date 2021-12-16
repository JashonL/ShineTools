package com.growatt.shinetools.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.shinetools.R;
import com.growatt.shinetools.bean.UpdataBean;

import java.util.List;

public class ManualAdater extends BaseQuickAdapter<UpdataBean, BaseViewHolder> {
    public ManualAdater(int layoutResId, @Nullable List<UpdataBean> data) {
        super(layoutResId, data);
    }

    public ManualAdater(@Nullable List<UpdataBean> data) {
        super(data);
    }

    public ManualAdater(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, UpdataBean item) {
        String s=mContext.getString(R.string.android_key1990)+":"+item.getCurrentVersion();
        helper.setText(R.id.tv_current_version,s);
    }
}
