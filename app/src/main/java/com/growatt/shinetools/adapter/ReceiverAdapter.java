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

public class ReceiverAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    private int startRegister;//开始寄存器
    public ReceiverAdapter(@LayoutRes int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        int pos =  helper.getLayoutPosition();
        if (startRegister >= 0){
            pos = pos + startRegister;
        }
        helper.setText(R.id.textview,pos  + "--" + item);
    }

    public int getStartRegister() {
        return startRegister;
    }

    public void setStartRegister(int startRegister) {
        this.startRegister = startRegister;
    }
}

