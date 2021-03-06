package com.growatt.shinetools.module.localbox.max.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class ALLSettingBean implements MultiItemEntity {

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
    private int[] funSet;
    //选项卡
    private String[] items;
    //数据的倍数
    private float mul;
    //二维设置数组
    private int[][] doubleFunset;
    //三维数组
    private int[][][]threeFunSet;

    //0x10设置值数组
    private int [] setValues;

    //该设置项唯一标识
    private int uuid;



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

    public int[][] getDoubleFunset() {
        return doubleFunset;
    }

    public void setDoubleFunset(int[][] doubleFunset) {
        this.doubleFunset = doubleFunset;
    }

    public void setFunSet(int[] funSet) {
        this.funSet = funSet;
    }

    public String[] getItems() {
        return items;
    }

    public void setItems(String[] items) {
        this.items = items;
    }

    public float getMul() {
        return mul;
    }

    public void setMul(float mul) {
        this.mul = mul;
    }

    public int[][][] getThreeFunSet() {
        return threeFunSet;
    }

    public void setThreeFunSet(int[][][] threeFunSet) {
        this.threeFunSet = threeFunSet;
    }

    public int[] getSetValues() {
        return setValues;
    }

    public void setSetValues(int[] setValues) {
        this.setValues = setValues;
    }

    public int getUuid() {
        return uuid;
    }

    public void setUuid(int uuid) {
        this.uuid = uuid;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}


