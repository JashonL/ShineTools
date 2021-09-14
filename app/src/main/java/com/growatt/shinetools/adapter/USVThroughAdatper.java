package com.growatt.shinetools.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.shinetools.R;
import com.growatt.shinetools.bean.USVThroughBean;

import java.util.List;

/**
 * Created：2020/4/10 on 16:32
 * Author:gaideng on admin
 * Description:
 */
public class  USVThroughAdatper extends BaseQuickAdapter<USVThroughBean, BaseViewHolder> {
    public USVThroughAdatper(int layoutResId, @Nullable List<USVThroughBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, USVThroughBean item) {
        helper.setText(R.id.tvTitle,item.getTitle());
        helper.setText(R.id.etValue,item.getShowValue());
        helper.setText(R.id.tvUnit,item.getUnit());
        EditText etValue = helper.getView(R.id.etValue);
        etValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                item.setShowValue(s.toString());
            }
        });
    }
}
