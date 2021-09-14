package com.growatt.shinetools.adapter;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.shinetools.module.localbox.max.bean.MaxMainMuiltBean;

import java.util.List;


/**
 * Created by dg on 2017/10/21.
 */

public class MaxMainMuiltAdapter extends BaseMultiItemQuickAdapter<MaxMainMuiltBean, BaseViewHolder> {
    private MaxMainChildAdapter childAdapter;
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public MaxMainMuiltAdapter(List<MaxMainMuiltBean> data) {
        super(data);
//        addItemType(0, R.layout.item_max_text1);
//        addItemType(1, R.layout.item_max_recyclerview);
    }



    @Override
    protected void convert(BaseViewHolder helper, MaxMainMuiltBean item) {
//        switch (helper.getItemViewType()){
//            case 0:
//                helper.setText(R.id.tvTitle,item.getTitle());
//                break;
//            case 1:
//                helper.setText(R.id.tvTitle,item.getTitle());
//                //子布局适配器
////                RecyclerView childRV = helper.getView(R.id.childRv);
//                childRV.setLayoutManager(new GridLayoutManager(mContext, 2));
//                if (childAdapter==null) {
//                    childAdapter = new MaxMainChildAdapter(R.layout.item_max_childrv, null);
//                }
//                childRV.setAdapter(childAdapter);
//                childAdapter.setNewData(item.getDatas());
//                break;
//           default:
//                helper.setText(R.id.tvTitle,item.getTitle());
//                break;
//        }
    }
}
