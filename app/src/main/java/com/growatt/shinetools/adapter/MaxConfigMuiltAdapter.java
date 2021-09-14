package com.growatt.shinetools.adapter;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.shinetools.R;
import com.growatt.shinetools.module.localbox.max.bean.MaxConfigBean;

import java.util.List;

/**
 * Created by dg on 2017/10/26.
 */

public class MaxConfigMuiltAdapter extends BaseQuickAdapter<MaxConfigBean,BaseViewHolder> {


    public MaxConfigMuiltAdapter(@LayoutRes int layoutResId, @Nullable List<MaxConfigBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MaxConfigBean item) {
        helper.setText(R.id.tvTitle,item.getTitle());
    }





    //        //公共部分
//        int resId = 0;
//        boolean isExpend = item.isExpend();
//        //图片
//        if (isExpend){
//            resId = R.drawable.max_up;
//        }else {
//            resId = R.drawable.max_down;
//        }
//        helper.setImageResource(R.id.ivExpand, resId);
//        //是否显示内容
//        LinearLayout llContent = helper.getView(R.id.llContent);
//        if (isExpend){
//            llContent.setVisibility(View.VISIBLE);
//        }else {
//            llContent.setVisibility(View.GONE);
//        }
//        //设置标题
//        helper.setText(R.id.tvTitle,item.getTitle());
//        int type = item.getType();
//        switch (type){
//            case 0:
//                break;
//            case 1:
//                break;
//            case 2:
//                helper.addOnClickListener(R.id.btnSelect);
//                break;
//        }
//        //设置子控件点击事件
//        helper.addOnClickListener(R.id.btnSet);
//        helper.addOnClickListener(R.id.llTitle);
}
