package com.growatt.shinetools.adapter;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.shinetools.R;
import com.growatt.shinetools.modbusbox.MaxUtil;
import com.growatt.shinetools.modbusbox.bean.MaxErrorBean;

import java.util.List;

/**
 * Created：2017/12/7 on 10:22
 * Author:gaideng on dg
 * Description:Max + tlx + tlxh 本地故障历史记录  兼容200以上故障
 */

public class OldInvErrorHisAdapter extends BaseQuickAdapter<MaxErrorBean,BaseViewHolder>{
    public OldInvErrorHisAdapter(@LayoutRes int layoutResId, @Nullable List<MaxErrorBean> data) {
        super(layoutResId, data);
    }

    public OldInvErrorHisAdapter(@Nullable List<MaxErrorBean> data) {
        super(data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MaxErrorBean item) {
        helper.setText(R.id.tvErrCode,item.getErrCode() + "");
//        String errStr = "";
//        int errCode = item.getErrCode();
//        if (errCode < 200){
//            errStr = MaxUtil.getErrContentByCode(mContext,errCode);
//        }else {
//            errStr = MaxUtil.getErrContentByCodeNew(mContext,errCode);
//        }
        helper.setText(R.id.tvErrContent,item.getErrCode() + "");
        helper.setText(R.id.tvTime, MaxUtil.getMaxErrTimeByErrBean(item));
    }
}
