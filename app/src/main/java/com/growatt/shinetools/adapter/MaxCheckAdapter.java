package com.growatt.shinetools.adapter;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.shinetools.R;
import com.growatt.shinetools.module.localbox.max.bean.MaxCheckBean;

import java.util.List;

/**
 * Created：2018/1/24 on 16:57
 * Author:gaideng on dg
 * Description:智能检测适配器
 */

public class MaxCheckAdapter extends BaseQuickAdapter<MaxCheckBean, BaseViewHolder> {
    public MaxCheckAdapter(@LayoutRes int layoutResId, @Nullable List<MaxCheckBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MaxCheckBean item) {
        helper.setImageResource(R.id.ivIcon,item.getImgId());
        helper.setText(R.id.tvTitle,item.getTitle());
        helper.setText(R.id.tvContent,item.getContent());
    }
}
