package com.growatt.shinetools.adapter;

import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.shinetools.R;
import com.growatt.shinetools.module.localbox.max.bean.MaxContentBean;

import java.util.List;

/**
 * Created by dg on 2017/10/25.
 */

public class MaxContentAdapter extends BaseQuickAdapter<MaxContentBean, BaseViewHolder> {
    public MaxContentAdapter(@LayoutRes int layoutResId, @Nullable List<MaxContentBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MaxContentBean item) {
        TextView tv = helper.getView(R.id.tvGridContent);
        String content = item.getText();
        if (!TextUtils.isEmpty(content) && content.contains("/")){
            content = content.replace("/","\n");
        }
        tv.setText(content);
        int resId = 0;
        switch (item.getStatus()){
            case 1://蓝色
                resId = R.color.mainColor;
                break;
            case 2://内容颜色
                resId = R.color.max_main_text_content;
                break;
            default:
                resId = R.color.max_main_gray;
                break;
        }
        tv.setTextColor(ContextCompat.getColor(mContext,resId));
    }
}
