package com.growatt.shinetools.adapter;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.shinetools.R;
import com.growatt.shinetools.module.localbox.ustool.bean.USChargePriorityBean;

public class USchargePeriodAdapter extends BaseQuickAdapter<USChargePriorityBean, BaseViewHolder> {
    private OnChildCheckLiseners onChildCheckLiseners;


    public USchargePeriodAdapter(int layoutResId, OnChildCheckLiseners liseners) {
        super(layoutResId);
        this.onChildCheckLiseners=liseners;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, USChargePriorityBean item) {
        //时间段标题
        String time = mContext.getString(R.string.android_key492) + (helper.getAdapterPosition());
        helper.setText(R.id.tv_time_name,time);
        //时间段
        String timePeriod = item.getTimePeriod();
        if (!TextUtils.isEmpty(timePeriod)){
            helper.setText(R.id.tv_time_period,timePeriod);
        }
        //工作日|电网优先
        String isEnableWeek = item.getIsEnableWeek();
        String isEnableA = item.getIsEnableA();
        String content=isEnableWeek+"|"+isEnableA;
        if (item.isSpecial()){
            content=isEnableA;
        }
        helper.setText(R.id.tv_period_name,content);
        //使能
        int isEnableBIndex = item.getIsEnableBIndex();
        helper.setChecked(R.id.sw_enable,1==isEnableBIndex);

        helper.setOnCheckedChangeListener(R.id.sw_enable, (compoundButton, b) -> {
            if (compoundButton.isPressed()){
                onChildCheckLiseners.oncheck(b,helper.getAdapterPosition());

            }
        });
    }

    public interface OnChildCheckLiseners{
        void oncheck(boolean check,int position);
    }
}
