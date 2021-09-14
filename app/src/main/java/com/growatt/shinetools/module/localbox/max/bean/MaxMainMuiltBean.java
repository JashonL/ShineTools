package com.growatt.shinetools.module.localbox.max.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

/**
 * Created by dg on 2017/10/21.
 */

public class MaxMainMuiltBean implements MultiItemEntity {
    private int id;
    //主列表头部标题
    private String title;
    //主列表头部右侧单位
    private String unit;
    //重要状态
    private int status;
    //右侧图片
    private int resId;
    //打开或关闭
    private boolean isOpend;
    //数据源
    private List<MaxChildBean> datas;
    //item类型
    private int itemType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public boolean isOpend() {
        return isOpend;
    }

    public void setOpend(boolean opend) {
        isOpend = opend;
    }

    public List<MaxChildBean> getDatas() {
        return datas;
    }

    public void setDatas(List<MaxChildBean> datas) {
        this.datas = datas;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }
}
