package com.growatt.shinetools.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.shinetools.R;
import com.growatt.shinetools.bean.USVThroughBean;
import com.growatt.shinetools.bean.UsSettingConstant;

import java.util.List;

public class UsThroughAdapter extends BaseMultiItemQuickAdapter<USVThroughBean, BaseViewHolder> {
    private OnChildCheckLiseners onChildCheckLiseners;


    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     */
    public UsThroughAdapter(List<USVThroughBean> data, OnChildCheckLiseners liseners){
        super(data);
        addItemType(UsSettingConstant.SETTING_TYPE_INPUT_UNIT, R.layout.item_us_vthrough_new);
        addItemType(UsSettingConstant.SETTING_TYPE_INPUT_UNIT_EXPLAIN, R.layout.item_us_vthrough_new_explain);
        addItemType(UsSettingConstant.SETTING_TYPE_SWITCH, R.layout.item_setting_switch);
        this.onChildCheckLiseners=liseners;
    }



    @Override
    protected void convert(@NonNull BaseViewHolder helper, USVThroughBean item) {
        int itemType = item.getItemType();
        if (itemType==UsSettingConstant.SETTING_TYPE_INPUT_UNIT){
            helper.setText(R.id.tvTitle,item.getTitle());
            helper.setText(R.id.tvValue,item.getShowValue());
            helper.setText(R.id.tvUnit,item.getUnit());
        }else if (itemType==UsSettingConstant.SETTING_TYPE_INPUT_UNIT_EXPLAIN){
            helper.setText(R.id.tvTitle,item.getTitle());
            helper.setText(R.id.tvValue,item.getShowValue());
            helper.setText(R.id.tvUnit,item.getUnit());
            helper.addOnClickListener(R.id.tvTitle);
        }else if (itemType==UsSettingConstant.SETTING_TYPE_SWITCH){
            helper.setText(R.id.tv_title,item.getTitle());
            String value = item.getShowValue();
            helper.setChecked(R.id.sw_switch,"1".equals(value));
            helper.setOnCheckedChangeListener(R.id.sw_switch, (compoundButton, b) -> {
                if (compoundButton.isPressed()){
                    onChildCheckLiseners.oncheck(b,helper.getAdapterPosition());

                }
            });
        }
    }


    public interface OnChildCheckLiseners{
        void oncheck(boolean check, int position);
    }

}
