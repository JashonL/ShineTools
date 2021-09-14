package com.growatt.shinetools.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created：2019/1/9 on 19:31
 * Author:gaideng on dg
 * Description:
 */

public class TLXHChargePriorityBean implements MultiItemEntity {
    private int itemType = 0;
    private String itemTitle;
    private int timeNum;
    private String timePeriod;
    private boolean isCheck;//是否选中
    //时段读取值
    private String timePeriodRead;
    /*不强制  充电  放电 文本及下标 + 读取值和下标*/
    private String isEnableA;
    private int isEnableAIndex;
    private String isEnableARead;
    private int isEnableAIndexRead;
    /*禁止使能 文本及下标*/
    private String isEnableBRead;
    private int isEnableBIndexRead;
    private String isEnableB;
    private int isEnableBIndex;

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public int getTimeNum() {
        return timeNum;
    }

    public void setTimeNum(int timeNum) {
        this.timeNum = timeNum;
    }

    public String getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }

    public String getIsEnableA() {
        return isEnableA;
    }

    public void setIsEnableA(String isEnableA) {
        this.isEnableA = isEnableA;
    }

    public String getIsEnableB() {
        return isEnableB;
    }

    public void setIsEnableB(String isEnableB) {
        this.isEnableB = isEnableB;
    }

    public int getIsEnableAIndex() {
        return isEnableAIndex;
    }

    public void setIsEnableAIndex(int isEnableAIndex) {
        this.isEnableAIndex = isEnableAIndex;
    }

    public int getIsEnableBIndex() {
        return isEnableBIndex;
    }

    public void setIsEnableBIndex(int isEnableBIndex) {
        this.isEnableBIndex = isEnableBIndex;
    }

    public String getTimePeriodRead() {
        return timePeriodRead;
    }

    public void setTimePeriodRead(String timePeriodRead) {
        this.timePeriodRead = timePeriodRead;
    }

    public String getIsEnableARead() {
        return isEnableARead;
    }

    public void setIsEnableARead(String isEnableARead) {
        this.isEnableARead = isEnableARead;
    }

    public int getIsEnableAIndexRead() {
        return isEnableAIndexRead;
    }

    public void setIsEnableAIndexRead(int isEnableAIndexRead) {
        this.isEnableAIndexRead = isEnableAIndexRead;
    }

    public String getIsEnableBRead() {
        return isEnableBRead;
    }

    public void setIsEnableBRead(String isEnableBRead) {
        this.isEnableBRead = isEnableBRead;
    }

    public int getIsEnableBIndexRead() {
        return isEnableBIndexRead;
    }

    public void setIsEnableBIndexRead(int isEnableBIndexRead) {
        this.isEnableBIndexRead = isEnableBIndexRead;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
