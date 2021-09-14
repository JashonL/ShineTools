package com.growatt.shinetools.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.shinetools.R;
import com.growatt.shinetools.bean.TutorialBean;

import java.util.List;

public class TutorialAdapter extends BaseQuickAdapter<TutorialBean, BaseViewHolder> {

    public TutorialAdapter(int layoutResId, @Nullable List<TutorialBean> data) {
        super(layoutResId, data);
    }

    public TutorialAdapter(@Nullable List<TutorialBean> data) {
        super(data);
    }

    public TutorialAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, TutorialBean item) {

        helper.setText(R.id.tv_title,item.getTitle());
        helper.setText(R.id.tv_content,item.getContent());
        helper.setImageResource(R.id.iv_pic,item.getPic());

    }
}
