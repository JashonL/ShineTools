package com.growatt.shinetools.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CheckBox;
import android.widget.EditText;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.shinetools.R;
import com.growatt.shinetools.module.localbox.ustool.bean.USChargePriorityBean;

import java.util.List;

/**
 * Created：2020/4/20 on 15:34
 * Author:gaideng on admin
 * Description:
 */
public class USTimerAdapter extends BaseMultiItemQuickAdapter<USChargePriorityBean, BaseViewHolder> {
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    private boolean isShowWeek = true;//是否显示周末
    public USTimerAdapter(List<USChargePriorityBean> data) {
        super(data);
        addItemType(0, R.layout.item_us_timer_set_1);
        addItemType(1, R.layout.item_us_timer_set_2);
    }

    @Override
    protected void convert(BaseViewHolder helper, USChargePriorityBean item) {
        CheckBox cb = helper.getView(R.id.cbTime);
        cb.setChecked(item.isCheck());
        helper.addOnClickListener(R.id.tvCheck);
        switch (item.getItemType()){
            case 0:
                helper.setText(R.id.tvTimerNum,String.valueOf(item.getTimeNum()));
                helper.setText(R.id.tvTimer,item.getTimePeriod());
                helper.setText(R.id.tvEnablea,item.getIsEnableA());
                helper.setText(R.id.tvEnableb,item.getIsEnableB());
                helper.setText(R.id.tvTimerRead,item.getTimePeriodRead());
                helper.setText(R.id.tvEnableReada,item.getIsEnableARead());
                helper.setText(R.id.tvEnableReadb,item.getIsEnableBRead());
                helper.setText(R.id.tvWeek,item.getIsEnableWeek());
                helper.addOnClickListener(R.id.tvEnablea);
                helper.addOnClickListener(R.id.tvEnableb);
                helper.addOnClickListener(R.id.tvTimer);
                helper.addOnClickListener(R.id.tvWeek);
                helper.setVisible(R.id.tvWeek,isShowWeek);
                break;
            case 1:
                helper.addOnClickListener(R.id.tvSelect1);
                helper.addOnClickListener(R.id.tvSelect2);
//                helper.addOnClickListener(R.id.tvStart);
//                helper.addOnClickListener(R.id.tvEnd);
                helper.setText(R.id.tvSelect1,item.getIsEnableC());
                helper.setText(R.id.tvSelect2,item.getIsEnableB());
                helper.setText(R.id.tvStart,item.getStartTime());
                helper.setText(R.id.tvEnd,item.getEndTime());
                EditText etStart = helper.getView(R.id.tvStart);
                EditText etEnd = helper.getView(R.id.tvEnd);
                etStart.setHint(item.getStartTimeNote());
                etEnd.setHint(item.getEndTimeNote());
                etStart.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        item.setStartTime(editable.toString());
                    }
                });
                etEnd.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        item.setEndTime(editable.toString());
                    }
                });
                break;
        }
    }

    public boolean isShowWeek() {
        return isShowWeek;
    }

    public void setShowWeek(boolean showWeek) {
        isShowWeek = showWeek;
    }
}
