package com.growatt.shinetools.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.shinetools.R;
import com.growatt.shinetools.bean.BeanDebug;

import java.util.List;

public class DebugTypeAdapter extends BaseQuickAdapter<BeanDebug, BaseViewHolder> {
    public DebugTypeAdapter(int layoutResId, @Nullable List<BeanDebug> data) {
        super(layoutResId, data);
    }

    public DebugTypeAdapter(@Nullable List<BeanDebug> data) {
        super(data);
    }

    public DebugTypeAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, BeanDebug item) {
        int icon = item.getIcon();
        helper.setImageResource(R.id.iv_icon,icon);

        String title = item.getTitle();
        helper.setText(R.id.tv_title,title);

        String content = item.getContent();
        TextView tvContent = helper.getView(R.id.tv_content);

        helper.setText(R.id.tv_content,content);

        if (TextUtils.isEmpty(content)){
            tvContent.setVisibility(View.GONE);
        }else {
            tvContent.setVisibility(View.VISIBLE);
        }


    }
}
