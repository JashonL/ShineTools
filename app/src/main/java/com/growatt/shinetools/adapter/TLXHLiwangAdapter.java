package com.growatt.shinetools.adapter;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.shinetools.R;
import com.growatt.shinetools.module.localbox.max.bean.TLXHLiwangBean;

import java.util.List;

/**
 * Created：2019/1/3 on 19:37
 * Author:gaideng on dg
 * Description:离网参数信息适配器
 */

public class TLXHLiwangAdapter extends BaseMultiItemQuickAdapter<TLXHLiwangBean,BaseViewHolder>{
    public final static int ITEM1 = 0;
    public final static int ITEM2 = 1;
    public TLXHLiwangAdapter(List<TLXHLiwangBean> data) {
        super(data);
        addItemType(ITEM1, R.layout.item_tlxh_tool_liwang);
        addItemType(ITEM2, R.layout.item_tlxh_tool_liwang_one);
    }

    @Override
    protected void convert(BaseViewHolder helper, TLXHLiwangBean item) {
        int type = item.getItemType();
        switch (type){
            case ITEM1:
                helper.setText(R.id.tvTitle,item.getTitle());
                helper.setText(R.id.tvRContent,item.getrContent());
                helper.setText(R.id.tvSContent,item.getsContent());
                helper.setText(R.id.tvTContent,item.gettContent());
                break;
            case ITEM2:
                helper.setText(R.id.tvTitle,item.getTitle());
                helper.setText(R.id.tvContent,item.getrContent());
                break;
        }
    }
}
