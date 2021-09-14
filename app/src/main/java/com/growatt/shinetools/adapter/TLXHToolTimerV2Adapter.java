package com.growatt.shinetools.adapter;

import android.widget.CheckBox;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.shinetools.R;
import com.growatt.shinetools.bean.TLXHChargePriorityBean;

import java.util.List;

/**
 * Created：2019/1/9 on 19:33
 * Author:gaideng on dg
 * Description:
 */

public class TLXHToolTimerV2Adapter extends BaseQuickAdapter<TLXHChargePriorityBean,BaseViewHolder> {
    public TLXHToolTimerV2Adapter(int layoutResId, @Nullable List<TLXHChargePriorityBean> data) {
        super(layoutResId, data);
    }
    @Override
    protected void convert(BaseViewHolder helper, TLXHChargePriorityBean item) {
        helper.setText(R.id.tvTimerNum,String.valueOf(item.getTimeNum()));
        helper.setText(R.id.tvTimer,item.getTimePeriod());
        helper.setText(R.id.tvEnablea,item.getIsEnableA());
        helper.setText(R.id.tvEnableb,item.getIsEnableB());
        helper.setText(R.id.tvTimerRead,item.getTimePeriodRead());
        helper.setText(R.id.tvEnableReada,item.getIsEnableARead());
        helper.setText(R.id.tvEnableReadb,item.getIsEnableBRead());
        CheckBox cb = helper.getView(R.id.cbTime);
        cb.setChecked(item.isCheck());
        helper.addOnClickListener(R.id.tvEnablea);
        helper.addOnClickListener(R.id.tvEnableb);
        helper.addOnClickListener(R.id.tvTimer);
        helper.addOnClickListener(R.id.tvCheck);
    }
}
