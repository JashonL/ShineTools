package com.growatt.shinetools.adapter;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.shinetools.R;
import com.growatt.shinetools.module.localbox.max.bean.MaxControlBean;

import java.util.List;

/**
 * Created：2018/1/24 on 15:13
 * Author:gaideng on dg
 * Description:
 */

public class MaxControlAdapter extends BaseQuickAdapter<MaxControlBean, BaseViewHolder> {
    public MaxControlAdapter(@LayoutRes int layoutResId, @Nullable List<MaxControlBean> data) {
        super(layoutResId, data);
    }

    public MaxControlAdapter(@Nullable List<MaxControlBean> data) {
        super(R.layout.item_max_tool_control,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MaxControlBean item) {
        helper.setText(R.id.tvName,item.getTitle());
        helper.setImageResource(R.id.ivIcon,item.getImgId());
    }
}
