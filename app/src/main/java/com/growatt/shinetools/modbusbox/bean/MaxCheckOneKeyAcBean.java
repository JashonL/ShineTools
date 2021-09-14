package com.growatt.shinetools.modbusbox.bean;

/**
 * Created：2018/2/6 on 14:01
 * Author:gaideng on dg
 * Description:Max一键诊断  Ac曲线实体
 */

public class MaxCheckOneKeyAcBean {
    /**
     * 图片颜色id
     */
    private int imgColorId;
    /**
     * 标题
     */
    private String title;
    /**
     * 值
     */
    private String acRms;
    private String acF;
    private String acValue;
    private String acXValue;
    /**
     * 最值
     */
    private String maxXValue;
    private String maxYValue;

    /**
     * 是否选中
     */
    private boolean isSelect = true;
    /**
     * 文本颜色id
     */
    private int textColorId;

    public String getMaxXValue() {
        return maxXValue;
    }

    public void setMaxXValue(String maxXValue) {
        this.maxXValue = maxXValue;
    }

    public String getMaxYValue() {
        return maxYValue;
    }

    public void setMaxYValue(String maxYValue) {
        this.maxYValue = maxYValue;
    }

    public String getAcXValue() {
        return acXValue;
    }

    public void setAcXValue(String acXValue) {
        this.acXValue = acXValue;
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

    public String getAcRms() {
        return acRms;
    }

    public void setAcRms(String acRms) {
        this.acRms = acRms;
    }

    public String getAcF() {
        return acF;
    }

    public void setAcF(String acF) {
        this.acF = acF;
    }

    public String getAcValue() {
        return acValue;
    }

    public void setAcValue(String acValue) {
        this.acValue = acValue;
    }

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
}
