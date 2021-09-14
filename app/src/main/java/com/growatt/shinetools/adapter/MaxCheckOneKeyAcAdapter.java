package com.growatt.shinetools.adapter;

import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.shinetools.R;
import com.growatt.shinetools.modbusbox.bean.MaxCheckOneKeyAcBean;

import java.util.List;

/**
 * Created：2018/2/6 on 14:19
 * Author:gaideng on dg
 * Description:Max一键诊断  Ac曲线
 */

public class MaxCheckOneKeyAcAdapter extends BaseQuickAdapter<MaxCheckOneKeyAcBean,BaseViewHolder> {
    public MaxCheckOneKeyAcAdapter(@LayoutRes int layoutResId, @Nullable List<MaxCheckOneKeyAcBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MaxCheckOneKeyAcBean item) {
        TextView tvTitle = helper.getView(R.id.tvTitle);
        TextView tvRms = helper.getView(R.id.tvRms);
        TextView tvHz = helper.getView(R.id.tvHz);
        TextView tvValue = helper.getView(R.id.tvValue);

        //设置文本
        tvTitle.setText(item.getTitle());
//        tvRms.setText(TextUtils.isEmpty(item.getAcRms())?"--":item.getAcRms());
//        tvHz.setText(TextUtils.isEmpty(item.getAcF())?"--":item.getAcF());
        tvRms.setText(String.format("%s,%s",TextUtils.isEmpty(item.getAcRms())?"--":item.getAcRms(),TextUtils.isEmpty(item.getAcF())?"--":item.getAcF()));

        StringBuilder sb = new StringBuilder();
//        sb.append(
//                TextUtils.isEmpty(item.getAcXValue())?"--":item.getAcXValue()
//        ).append(",").append(TextUtils.isEmpty(item.getAcValue())?"--":item.getAcValue());
        sb.append(TextUtils.isEmpty(item.getAcValue())?"--":item.getAcValue());
        tvValue.setText(String.valueOf(sb));

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
        tvRms.setTextColor(color);
        tvHz.setTextColor(color);
        tvValue.setTextColor(color);

        //设置监听
        helper.addOnClickListener(R.id.llTotal);
    }
}
