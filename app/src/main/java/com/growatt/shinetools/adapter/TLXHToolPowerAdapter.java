package com.growatt.shinetools.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.shinetools.R;
import com.growatt.shinetools.module.localbox.tlxh.bean.TLXHEleBean;

import java.util.List;

/**
 * Created：2019/1/3 on 15:28
 * Author:gaideng on dg
 * Description:
 */

public class TLXHToolPowerAdapter extends BaseQuickAdapter<TLXHEleBean,BaseViewHolder> {
    public TLXHToolPowerAdapter(int layoutResId, @Nullable List<TLXHEleBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TLXHEleBean item) {
        helper.setImageResource(R.id.ivIcon,item.getDrawableResId());
        helper.setText(R.id.tvTitle,item.getTitle());
        helper.setText(R.id.tvPower,item.getContent());
    }
}
