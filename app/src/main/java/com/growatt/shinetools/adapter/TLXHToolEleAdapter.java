package com.growatt.shinetools.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.shinetools.R;
import com.growatt.shinetools.module.localbox.max.bean.TLXHEleBean;
import com.growatt.shinetools.utils.CommenUtils;

import java.util.List;

/**
 * Created：2019/1/3 on 15:28
 * Author:gaideng on dg
 * Description:
 */

public class TLXHToolEleAdapter extends BaseQuickAdapter<TLXHEleBean,BaseViewHolder> {
    public TLXHToolEleAdapter(int layoutResId, @Nullable List<TLXHEleBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TLXHEleBean item) {
        TextView tvTitle = helper.getView(R.id.tvTitle);
        TextView tvToday = helper.getView(R.id.tvToday);
        TextView tvTotal = helper.getView(R.id.tvTotal);
        TextView tvUnitToday = helper.getView(R.id.tvUnitToday);
        TextView tvUnitTotal = helper.getView(R.id.tvUnitTotal);
        //icon
        ImageView ivIcon = helper.getView(R.id.ivIcon);
        if (item.getDrawableResId() == -1){
            CommenUtils.hideAllView(View.INVISIBLE,ivIcon);
        }else {
            CommenUtils.showAllView(ivIcon);
            ivIcon.setImageResource(item.getDrawableResId());
        }
        //值和字体颜色
        tvTitle.setText(item.getTitle());
        tvToday.setText(item.getTodayEle());
        tvTotal.setText(item.getTotalEle());
//        tvUnitToday.setText(item.getUnit());
        tvUnitToday.setText(mContext.getText(R.string.m今日));
//        tvUnitTotal.setText(item.getUnit());
        tvUnitTotal.setText(mContext.getText(R.string.m64));
        int contentColorId = ContextCompat.getColor(mContext,item.getContentColor());
        tvTitle.setTextColor(contentColorId);
        tvToday.setTextColor(contentColorId);
        tvTotal.setTextColor(contentColorId);
        int unitColorId = ContextCompat.getColor(mContext,item.getUnitColor());
        tvUnitToday.setTextColor(unitColorId);
        tvUnitTotal.setTextColor(unitColorId);
    }
}
