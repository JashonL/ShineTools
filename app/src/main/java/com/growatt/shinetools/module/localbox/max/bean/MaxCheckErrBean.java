package com.growatt.shinetools.module.localbox.max.bean;

import com.growatt.shinetools.modbusbox.Arith;

/**
 * Created：2018/2/4 on 10:34
 * Author:gaideng on dg
 * Description:Max故障录波实体
 */

public class MaxCheckErrBean {
    /**
     * 文本颜色id
     */
    private int textColorId;
    /**
     * 图表折线颜色id
     */
    private int imgColorId;
    /**
     * 标题文本
     */
    private String title;
    /**
     * 是否选中
     */
    private boolean isSelect = true;
    /**
     * 当前倍数
     */
    private double multiple = 1.0;
    /**
     * 之前倍数
     */
    private double preMult = 1.0;
    /**
     * Id:Number
     */
    private int errId = -1;
    /**
     * 当前选择值：xy值
     */
    private String xValue = "";
    private String yValue = "";

    public double getPreMult() {
        return preMult;
    }

    public void setPreMult(double preMult) {
        this.preMult = preMult;
    }

    public int getTextColorId() {
        return textColorId;
    }

    public void setTextColorId(int textColorId) {
        this.textColorId = textColorId;
    }

    public int getImgColorId() {
        return imgColorId;
    }

    public void setImgColorId(int imgColorId) {
        this.imgColorId = imgColorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public double getMultiple() {
        return Arith.round(multiple,1);
    }

    public void setMultiple(double multiple) {
        this.multiple = multiple;
    }

    public int getErrId() {
        return errId;
    }

    public void setErrId(int errId) {
        this.errId = errId;
    }

    public String getxValue() {
        return xValue;
    }

    public void setxValue(String xValue) {
        this.xValue = xValue;
    }

    public String getyValue() {
        return yValue;
    }

    public void setyValue(String yValue) {
        this.yValue = yValue;
    }
}
