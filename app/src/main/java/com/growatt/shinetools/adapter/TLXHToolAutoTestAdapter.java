package com.growatt.shinetools.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.shinetools.R;
import com.growatt.shinetools.module.localbox.max.bean.TLXHToolAutoTestBean;

import java.util.List;

/**
 * Created：2019/6/6 on 15:32
 * Author:gaideng on administratorr
 * Description:
 */

public class TLXHToolAutoTestAdapter extends BaseQuickAdapter<TLXHToolAutoTestBean,BaseViewHolder> {
    public TLXHToolAutoTestAdapter(int layoutResId, @Nullable List<TLXHToolAutoTestBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TLXHToolAutoTestBean item) {
        helper.setText(R.id.tvTitle,item.getTitle());
        helper.setText(R.id.tvContent,item.getContent());
    }
}
