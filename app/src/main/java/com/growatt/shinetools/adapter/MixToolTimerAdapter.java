package com.growatt.shinetools.adapter;

import android.widget.CheckBox;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.shinetools.R;
import com.growatt.shinetools.bean.TLXHChargePriorityBean;

import java.util.List;

/**
 * Created：2020/4/29 on 17:45
 * Author:gaideng on admin
 * Description:
 */
public class MixToolTimerAdapter extends BaseMultiItemQuickAdapter<TLXHChargePriorityBean, BaseViewHolder> {
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public MixToolTimerAdapter(List<TLXHChargePriorityBean> data) {
        super(data);
        addItemType(0, R.layout.item_timer_mixset1);
        addItemType(1, R.layout.item_timer_mixset2);
    }

    @Override
    protected void convert(BaseViewHolder helper, TLXHChargePriorityBean item) {
        helper.setText(R.id.tvTimerNum,String.valueOf(item.getTimeNum()));
        helper.setText(R.id.tvTimer,item.getTimePeriod());
        helper.setText(R.id.tvEnableb,item.getIsEnableB());
        helper.setText(R.id.tvTimerRead,item.getTimePeriodRead());
        CheckBox cb = helper.getView(R.id.cbTime);
        cb.setChecked(item.isCheck());
        helper.addOnClickListener(R.id.tvEnableb);
        helper.addOnClickListener(R.id.tvTimer);
        helper.addOnClickListener(R.id.tvCheck);
        if (item.getItemType() == 1){
            helper.setText(R.id.tvTitle1,item.getItemTitle());
        }
    }
}
