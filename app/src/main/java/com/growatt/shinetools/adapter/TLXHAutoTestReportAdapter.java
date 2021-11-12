package com.growatt.shinetools.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.shinetools.R;
import com.growatt.shinetools.module.localbox.tlxh.bean.TLXHToolAutoTestBean;

import java.util.List;

/**
 * Created：2019/7/22 on 17:38
 * Author:gaideng on administratorr
 * Description:
 */

public class TLXHAutoTestReportAdapter extends ScreenShootBaseAdapter<List<TLXHToolAutoTestBean>,BaseViewHolder> {
    public TLXHAutoTestReportAdapter(int layoutResId, @Nullable List<List<TLXHToolAutoTestBean>> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, List<TLXHToolAutoTestBean> tlxhToolAutoTestBeans) {
        for (int i = 0; i < tlxhToolAutoTestBeans.size(); i++) {
            TLXHToolAutoTestBean bean = tlxhToolAutoTestBeans.get(i);
            switch (i){
                case 0:
                    holder.setText(R.id.tvContent1,bean.getProcess());
                    break;
                case 1:
                    holder.setText(R.id.tvContent2,String.format("%s--%s",bean.getPoint(),bean.getPointTime()));
                    break;
                case 2:
                    holder.setText(R.id.tvContent3,String.format("%s--%s",bean.getPointValue(),bean.getPointValueTime()));
                    break;
                case 3:
                    holder.setText(R.id.tvContent4,bean.getStatus());
                    break;
            }
        }
    }
}
