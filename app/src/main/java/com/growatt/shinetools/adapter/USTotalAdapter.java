package com.growatt.shinetools.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.shinetools.R;
import com.growatt.shinetools.bean.USVThroughBean;

import java.util.List;

/**
 * Created：2020/4/13 on 19:33
 * Author:gaideng on admin
 * Description:
 */
public class USTotalAdapter extends BaseMultiItemQuickAdapter<USVThroughBean, BaseViewHolder> {
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public USTotalAdapter(List<USVThroughBean> data) {
        super(data);
        addItemType(0, R.layout.item_us_vthrough);
        addItemType(1, R.layout.item_us_select);
    }

    @Override
    protected void convert(BaseViewHolder helper, USVThroughBean item) {
        helper.setText(R.id.tvTitle,item.getTitle());
        helper.setText(R.id.tvUnit,item.getUnit());
        switch (item.getItemType()){
            case 0:
                helper.setText(R.id.etValue,item.getShowValue());
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
                break;
            case 1:
                helper.addOnClickListener(R.id.btnValue);
                helper.setText(R.id.btnValue,item.getShowValue());
                break;
        }
    }
}
