package com.growatt.shinetools.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.shinetools.R;
import com.growatt.shinetools.module.localbox.max.bean.ProtectParamBean;

import java.util.List;

/**
 * Created：2020/4/24 on 10:20
 * Author:gaideng on admin
 * Description:
 */
public class ProtectParamAdapter extends BaseQuickAdapter<ProtectParamBean, BaseViewHolder> {
    public ProtectParamAdapter(int layoutResId, @Nullable List<ProtectParamBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ProtectParamBean item) {
        helper.setText(R.id.tvTitle,item.getTitle());
        helper.setText(R.id.tvUnit,item.getUnit());
        helper.setText(R.id.tvValue,item.getContent());
    }
}
