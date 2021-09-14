package com.growatt.shinetools.module.localbox.max.bean;

/**
 * Created：2018/2/1 on 16:35
 * Author:gaideng on dg
 * Description:Max Iv图表实体
 */

public class MaxCheckIVBean {
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
     * 最大值：xy值
     */
    private String xMaxValue = "";
    private String yMaxValue = "";
    /**
     * 当前选择值：xy值
     */
    private String xValue = "";
    private String yValue = "";
    /**
     * xy值单位
     */
    private String xUnit = "V";
    private String yUnit = "A";

    /**
     * 是否选中
     */
    private boolean isSelect = true;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
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

    public String getxMaxValue() {
        return xMaxValue;
    }

    public void setxMaxValue(String xMaxValue) {
        this.xMaxValue = xMaxValue;
    }

    public String getyMaxValue() {
        return yMaxValue;
    }

    public void setyMaxValue(String yMaxValue) {
        this.yMaxValue = yMaxValue;
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

    public String getxUnit() {
        return xUnit;
    }

    public void setxUnit(String xUnit) {
        this.xUnit = xUnit;
    }

    public String getyUnit() {
        return yUnit;
    }

    public void setyUnit(String yUnit) {
        this.yUnit = yUnit;
    }
}
