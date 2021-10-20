package com.growatt.shinetools.module.localbox.max.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class MaxSettingBean implements MultiItemEntity {

    //选项的类型，下拉、弹框等
    private int itemType;
    //设置项标题
    private String title;
    //寄存器地址
    private String register;
    //设置项的真实值
    private String value;
    //设置项的显示内容
    private String valueStr;
    //设置项单位
    private String unit;
    //设置项弹框提示内容
    private String hint;
    //查询该项的寄存器
    private int[] funs;
    //设置该项的寄存器
    private int [] funSet;



    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRegister() {
        return register;
    }

    public void setRegister(String register) {
        this.register = register;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValueStr() {
        return valueStr;
    }

    public void setValueStr(String valueStr) {
        this.valueStr = valueStr;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public int[] getFuns() {
        return funs;
    }

    public void setFuns(int[] funs) {
        this.funs = funs;
    }

    public int[] getFunSet() {
        return funSet;
    }

    public void setFunSet(int[] funSet) {
        this.funSet = funSet;
    }


    @Override
    public int getItemType() {
        return itemType;
    }
}


