package com.growatt.shinetools.adapter;

import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.shinetools.R;
import com.growatt.shinetools.bean.UpdataBean;

import java.util.List;

public class InvererManualAdater extends BaseMultiItemQuickAdapter<UpdataBean, BaseViewHolder> {

    public static final int CURRENT_VERSION = 0;
    public static final int CHOOSE_PACKAGE = 1;


    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public InvererManualAdater(List<UpdataBean> data) {
        super(data);
        addItemType(CURRENT_VERSION, R.layout.item_updata_device);
        addItemType(CHOOSE_PACKAGE, R.layout.item_choose_package);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, UpdataBean item) {
        int itemType = item.getItemType();
        if (itemType == CURRENT_VERSION) {
            String s = mContext.getString(R.string.android_key1990) + ":" + item.getCurrentVersion();
            helper.setText(R.id.tv_current_version, s);
        } else {
            ImageView ivCheck = helper.getView(R.id.cb_check);
            boolean checked = item.isChecked();
            ivCheck.setImageResource(checked ? R.drawable.checkbox_checked : R.drawable.checkbox_uncheck);
            helper.addOnClickListener(R.id.tv_other_package);
            String s = item.getCurrentVersion()+"";
            helper.setText(R.id.tv_current_version, s);
        }
    }
}
