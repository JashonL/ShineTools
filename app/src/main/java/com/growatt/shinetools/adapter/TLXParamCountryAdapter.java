package com.growatt.shinetools.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.shinetools.R;
import com.growatt.shinetools.module.localbox.max.bean.TLXParamCountryBean;

import java.util.List;

/**
 * Created：2019/10/28 on 17:12
 * Author:gaideng on administratorr
 * Description:
 */

public class TLXParamCountryAdapter extends BaseQuickAdapter<TLXParamCountryBean,BaseViewHolder> {
    public TLXParamCountryAdapter(int layoutResId, @Nullable List<TLXParamCountryBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TLXParamCountryBean item) {
        helper.setText(R.id.tvContent,item.getCountry());
    }
}
