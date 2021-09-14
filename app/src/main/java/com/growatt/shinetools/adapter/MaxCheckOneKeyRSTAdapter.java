package com.growatt.shinetools.adapter;

import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.shinetools.R;
import com.growatt.shinetools.modbusbox.bean.MaxCheckOneKeyRSTBean;

import java.util.List;

/**
 * Created：2018/2/6 on 14:16
 * Author:gaideng on dg
 * Description:Max一键诊断  电网线路阻抗
 */

public class MaxCheckOneKeyRSTAdapter extends BaseQuickAdapter<MaxCheckOneKeyRSTBean,BaseViewHolder> {
    public MaxCheckOneKeyRSTAdapter(@LayoutRes int layoutResId, @Nullable List<MaxCheckOneKeyRSTBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MaxCheckOneKeyRSTBean item) {
        TextView tvTitle = helper.getView(R.id.tvTitle);
        TextView tvValue = helper.getView(R.id.tvValue);

        //设置文本
        tvTitle.setText(item.getTitle());
        tvValue.setText(TextUtils.isEmpty(item.getValue())?"--":item.getValue());

        //设置颜色
        int color = 0;
        if (item.isSelect()){
            color = ContextCompat.getColor(mContext,R.color.content_bg_white);
        }else {
            color = ContextCompat.getColor(mContext,R.color.note_bg_white);
        }
        tvTitle.setTextColor(color);
        tvValue.setTextColor(color);

        //设置监听
        helper.addOnClickListener(R.id.llTotal);
    }
}
