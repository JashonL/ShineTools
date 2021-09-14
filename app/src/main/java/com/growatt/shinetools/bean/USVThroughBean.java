package com.growatt.shinetools.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created：2020/4/10 on 16:12
 * Author:gaideng on admin
 * Description:
 */
public class USVThroughBean implements MultiItemEntity {
    private int type;//0,代表输入；1，代表选择
    private int vol = 100;//电压
    private String title;
    private String unit;
    private double muilt;//缩放倍数
    private int registValue = -1;//实际寄存器设置值
    private String showValue ="";//用户输入或读取设置值
    private int registPos = -1;//当前寄存器号

    private int itemPos = 0;//type==1时使用

    public int getItemPos() {
        return itemPos;
    }

    public void setItemPos(int itemPos) {
        this.itemPos = itemPos;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getVol() {
        return vol;
    }

    public void setVol(int vol) {
        this.vol = vol;
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

    public double getMuilt() {
        return muilt;
    }

    public void setMuilt(double muilt) {
        this.muilt = muilt;
    }

    public int getRegistValue() {
        return registValue;
    }

    public void setRegistValue(int registValue) {
        this.registValue = registValue;
    }

    public String getShowValue() {
        return showValue;
    }

    public void setShowValue(String showValue) {
        this.showValue = showValue;
    }

    public int getRegistPos() {
        return registPos;
    }

    public void setRegistPos(int registPos) {
        this.registPos = registPos;
    }

    @Override
    public int getItemType() {
        return type;
    }
}
