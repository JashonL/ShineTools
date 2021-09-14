package com.growatt.shinetools.adapter;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.shinetools.R;
import com.growatt.shinetools.modbusbox.bean.MaxCheckOneKTHDVDetailBean;

import java.util.List;

/**
 * Created：2018/3/21 on 15:04
 * Author:gaideng on dg
 * Description:
 */

public class MaxCheckOneKTHDVDetailAdapter extends BaseQuickAdapter<MaxCheckOneKTHDVDetailBean,BaseViewHolder> {
    public MaxCheckOneKTHDVDetailAdapter(@LayoutRes int layoutResId, @Nullable List<MaxCheckOneKTHDVDetailBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MaxCheckOneKTHDVDetailBean item) {
        helper.setText(R.id.tvNum,item.getNum());
        helper.setText(R.id.tvR,item.getrStr() + "%");
        helper.setText(R.id.tvS,item.getsStr() + "%");
        helper.setText(R.id.tvT,item.gettStr() + "%");
    }
}
