package com.growatt.shinetools.adapter;

import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.shinetools.R;
import com.growatt.shinetools.module.localbox.max.bean.MaxCheckIVBean;

import java.util.List;

/**
 * Created：2018/2/1 on 16:42
 * Author:gaideng on dg
 * Description:
 */

public class MaxCheckIVAdapter extends BaseQuickAdapter<MaxCheckIVBean,BaseViewHolder> {
    public MaxCheckIVAdapter(@LayoutRes int layoutResId, @Nullable List<MaxCheckIVBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MaxCheckIVBean item) {
        TextView tvTitle = helper.getView(R.id.tvTitle);
        TextView tvMaxContent = helper.getView(R.id.tvMaxContent);
        TextView tvRealContent = helper.getView(R.id.tvRealContent);
        //设置文本
        tvTitle.setText(item.getTitle());
        tvMaxContent.setText(String.format("(%s,%s)",
                TextUtils.isEmpty(item.getxMaxValue())?"--":item.getxMaxValue(),
                TextUtils.isEmpty(item.getyMaxValue())?"--":item.getyMaxValue())
        );
        tvRealContent.setText(String.format("(%s,%s)",
                TextUtils.isEmpty(item.getxValue())?"--":item.getxValue(),
                TextUtils.isEmpty(item.getyValue())?"--":item.getyValue())
        );
//        tvRealContent.setText(String.format("(%s)",
//                TextUtils.isEmpty(item.getyValue())?"--":item.getyValue())
//        );
        //设置颜色
        helper.setBackgroundColor(R.id.tvImg, ContextCompat.getColor(mContext,item.getImgColorId()));
        int color = 0;
        if (item.isSelect()){
            color = ContextCompat.getColor(mContext,R.color.content_bg_white);
        }else {
            color = ContextCompat.getColor(mContext,R.color.note_bg_white);
        }
        tvTitle.setTextColor(color);
        tvMaxContent.setTextColor(color);
        tvRealContent.setTextColor(color);
    }
}
