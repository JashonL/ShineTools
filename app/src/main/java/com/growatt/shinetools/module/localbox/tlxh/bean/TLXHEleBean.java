package com.growatt.shinetools.module.localbox.tlxh.bean;


import com.growatt.shinetools.R;

/**
 * Created：2019/1/2 on 10:43
 * Author:gaideng on dg
 * Description:TLXH主页面发电量bean
 */

public class TLXHEleBean {
    private int drawableResId = -1;
    private String title;
    //内容颜色
    private int contentColor;
    //单位颜色，为0就不显示单位
    private int unitColor = R.color.note_bg_white;
    private String todayEle;
    private String TotalEle;
    private String unit = "kWh";
    //功率内容
    private String content;


    private String todayTitle;
    private String totalTitle;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getDrawableResId() {
        return drawableResId;
    }

    public void setDrawableResId(int drawableResId) {
        this.drawableResId = drawableResId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getContentColor() {
        return contentColor;
    }

    public void setContentColor(int contentColor) {
        this.contentColor = contentColor;
    }

    public int getUnitColor() {
        return unitColor;
    }

    public void setUnitColor(int unitColor) {
        this.unitColor = unitColor;
    }

    public String getTodayEle() {
        return todayEle;
    }

    public void setTodayEle(String todayEle) {
        this.todayEle = todayEle;
    }

    public String getTotalEle() {
        return TotalEle;
    }

    public void setTotalEle(String totalEle) {
        TotalEle = totalEle;
    }


    public String getTodayTitle() {
        return todayTitle;
    }

    public void setTodayTitle(String todayTitle) {
        this.todayTitle = todayTitle;
    }

    public String getTotalTitle() {
        return totalTitle;
    }

    public void setTotalTitle(String totalTitle) {
        this.totalTitle = totalTitle;
    }
}
