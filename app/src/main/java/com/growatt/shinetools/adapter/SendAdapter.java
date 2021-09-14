package com.growatt.shinetools.adapter;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.shinetools.R;

import java.util.List;

/**
 * Created by dg on 2017/9/26.
 */

public class SendAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public SendAdapter(@LayoutRes int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        int pos =  helper.getAdapterPosition();
        helper.setText(R.id.textview,pos + "--" + item);
    }
}
