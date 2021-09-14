package com.growatt.shinetools.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * RecyclerView截图需要继承这个adaper
 * Created by Administrator on 2018/11/26.
 */

public abstract class ScreenShootBaseAdapter<T, K extends BaseViewHolder> extends BaseQuickAdapter<T, K> {
    public ScreenShootBaseAdapter(int layoutResId, @Nullable List<T> data) {
        super(layoutResId, data);
    }

    public ScreenShootBaseAdapter(@Nullable List<T> data) {
        super(data);
    }

    public ScreenShootBaseAdapter(int layoutResId) {
        super(layoutResId);
    }

    /**
     * 用于对外暴露convert方法,构造缓存视图(截屏用)
     */
    public void startConvert(K helper, T item) {
        convert(helper, item);
    }

}
