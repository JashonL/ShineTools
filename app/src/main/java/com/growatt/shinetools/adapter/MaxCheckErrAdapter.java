package com.growatt.shinetools.adapter;

import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.shinetools.R;
import com.growatt.shinetools.module.localbox.max.bean.MaxCheckErrBean;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created：2018/2/4 on 11:14
 * Author:gaideng on dg
 * Description:
 */

public class MaxCheckErrAdapter extends BaseQuickAdapter<MaxCheckErrBean,BaseViewHolder>{
    public MaxCheckErrAdapter(@LayoutRes int layoutResId, @Nullable List<MaxCheckErrBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MaxCheckErrBean item) {
        TextView tvTitle = helper.getView(R.id.tvTitle);
        TextView tvID = helper.getView(R.id.tvID);
        TextView tvErrMul = helper.getView(R.id.tvErrMul);
        TextView tvNowPos = helper.getView(R.id.tvNowPos);
        //设置文本
        tvTitle.setText(item.getTitle());
        tvID.setText(
                item.getErrId()==-1?"--":String.valueOf(item.getErrId())
        );
        tvErrMul.setText(
                item.getErrId()==-1?"--":new DecimalFormat("#.##").format(item.getMultiple())
        );
//        tvNowPos.setText(String.format("(%s,%s)",
//                TextUtils.isEmpty(item.getxValue())?"--":item.getxValue(),
//                TextUtils.isEmpty(item.getyValue())?"--":item.getyValue())
//        );
        tvNowPos.setText(String.format("%s",
                TextUtils.isEmpty(item.getyValue())?"--":item.getyValue())
        );


        try {
            //设置颜色
            helper.setBackgroundColor(R.id.tvImg, ContextCompat.getColor(mContext,item.getImgColorId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        int color = 0;
        if (item.isSelect()){
            color = ContextCompat.getColor(mContext,R.color.content_bg_white);
        }else {
            color = ContextCompat.getColor(mContext,R.color.note_bg_white);
        }
        tvTitle.setTextColor(color);
        tvID.setTextColor(color);
//        tvErrMul.setTextColor(color);
        tvNowPos.setTextColor(color);

        //设置点击事件
//        View llErrMult = helper.getView(R.id.llErrMult);
//        View llSelect = helper.getView(R.id.llSelect);
        helper.addOnClickListener(R.id.llErrMult);
        helper.addOnClickListener(R.id.llSelect);
        helper.addOnClickListener(R.id.tvID);
    }
}
