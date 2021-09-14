package com.growatt.shinetools.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class USDebugSettingBean implements MultiItemEntity {

    private int itemType;
    private String title;
    private String register;
    private String value;
    private String valueStr;
    private String unit;

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

    @Override
    public int getItemType() {
        return itemType;
    }
}
