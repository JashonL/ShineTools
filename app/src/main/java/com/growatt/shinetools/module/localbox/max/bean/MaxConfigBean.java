package com.growatt.shinetools.module.localbox.max.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by dg on 2017/10/26.
 */

public class MaxConfigBean implements MultiItemEntity {
    private int type;
    private boolean isExpend;
    private String title;
    private int registCode;
    //内容


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isExpend() {
        return isExpend;
    }

    public void setExpend(boolean expend) {
        isExpend = expend;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getRegistCode() {
        return registCode;
    }

    public void setRegistCode(int registCode) {
        this.registCode = registCode;
    }

    @Override
    public int getItemType() {
        return type;
    }
}
