package com.growatt.shinetools.adapter;


import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.shinetools.R;
import com.growatt.shinetools.bean.AccessPoint;

import java.util.List;

public class WiFiListAdapter extends BaseQuickAdapter<AccessPoint, BaseViewHolder> {

    public WiFiListAdapter(int layoutResId, @Nullable List<AccessPoint> data) {
        super(layoutResId, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, AccessPoint item) {
        helper.setText(R.id.tv_wifi_name, item.ssid);
        if (item.security == AccessPoint.SECURITY_NONE) {
            helper.setVisible(R.id.iv_wifi_lock, false);
        } else {
            helper.setVisible(R.id.iv_wifi_lock, true);
        }
    }
}
