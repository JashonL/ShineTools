package com.growatt.shinetools.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.shinetools.R;
import com.growatt.shinetools.bean.USDebugSettingBean;
import com.growatt.shinetools.bean.UsSettingConstant;

import java.util.List;

public class UsSettingAdapter extends BaseMultiItemQuickAdapter<USDebugSettingBean, BaseViewHolder> {
    private OnChildCheckLiseners onChildCheckLiseners;


    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     */
    public UsSettingAdapter(List<USDebugSettingBean> data,OnChildCheckLiseners liseners){
        super(data);
        addItemType(UsSettingConstant.SETTING_TYPE_SELECT, R.layout.item_setting_next);
        addItemType(UsSettingConstant.SETTING_TYPE_INPUT, R.layout.item_setting_input);
        addItemType(UsSettingConstant.SETTING_TYPE_SWITCH, R.layout.item_setting_switch);
        addItemType(UsSettingConstant.SETTING_TYPE_NEXT, R.layout.item_setting_next);
        addItemType(UsSettingConstant.SETTING_TYPE_EXPLAIN, R.layout.item_setting_explain);
        addItemType(UsSettingConstant.SETTING_TYPE_ONLYREAD, R.layout.item_setting_only);
        this.onChildCheckLiseners=liseners;
    }



    @Override
    protected void convert(@NonNull BaseViewHolder helper, USDebugSettingBean item) {
        if (item.getItemType()==UsSettingConstant.SETTING_TYPE_SELECT){
            helper.setText(R.id.tv_title,item.getTitle());
            helper.setText(R.id.tv_value,item.getValueStr());
        }else if (item.getItemType()==UsSettingConstant.SETTING_TYPE_SWITCH){
            helper.setText(R.id.tv_title,item.getTitle());
            String value = item.getValue();
            helper.setChecked(R.id.sw_switch,"1".equals(value));
            helper.setOnCheckedChangeListener(R.id.sw_switch, (compoundButton, b) -> {
                if (compoundButton.isPressed()){
                    onChildCheckLiseners.oncheck(b,helper.getAdapterPosition());

                }
            });



        }else if (item.getItemType()==UsSettingConstant.SETTING_TYPE_INPUT){
            helper.setText(R.id.tv_title,item.getTitle());
            helper.setText(R.id.tv_value,item.getValueStr());
            helper.setText(R.id.tv_unit,item.getUnit());
        }else if (item.getItemType()==UsSettingConstant.SETTING_TYPE_EXPLAIN){
            helper.setText(R.id.tv_title,item.getTitle());
            helper.setText(R.id.tv_value,item.getValueStr());
            helper.addOnClickListener(R.id.tv_title);
        }else if (item.getItemType()== UsSettingConstant.SETTING_TYPE_NEXT){
            helper.setText(R.id.tv_title,item.getTitle());
            helper.setText(R.id.tv_value,item.getValueStr());
        }else if (item.getItemType()==UsSettingConstant.SETTING_TYPE_ONLYREAD){
            helper.setText(R.id.tv_title,item.getTitle());
            helper.setText(R.id.tv_value,item.getValueStr());
        }
    }


    public interface OnChildCheckLiseners{
        void oncheck(boolean check,int position);
    }

}
