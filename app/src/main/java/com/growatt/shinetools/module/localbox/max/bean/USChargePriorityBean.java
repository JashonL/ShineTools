package com.growatt.shinetools.module.localbox.max.bean;

import com.growatt.shinetools.bean.TLXHChargePriorityBean;

/**
 * Created：2020/4/20 on 15:36
 * Author:gaideng on admin
 * Description:
 */
public class USChargePriorityBean extends TLXHChargePriorityBean{


    private String isEnableWeek;
    private int isEnableWeekIndex;//是否是周末
    private boolean isAllWeek;//是否选择全周


    private String isEnableC;//是否是季度
    private int isEnableCIndex;

    private String startTime;
    private String endTime;
    private String startTimeNote;
    private String endTimeNote;

    //是否特殊日
    private boolean isSpecial;




    public String getStartTimeNote() {
        return startTimeNote;
    }

    public void setStartTimeNote(String startTimeNote) {
        this.startTimeNote = startTimeNote;
    }

    public String getEndTimeNote() {
        return endTimeNote;
    }

    public void setEndTimeNote(String endTimeNote) {
        this.endTimeNote = endTimeNote;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getIsEnableC() {
        return isEnableC;
    }

    public void setIsEnableC(String isEnableC) {
        this.isEnableC = isEnableC;
    }

    public int getIsEnableCIndex() {
        return isEnableCIndex;
    }

    public void setIsEnableCIndex(int isEnableCIndex) {
        this.isEnableCIndex = isEnableCIndex;
    }

    public String getIsEnableWeek() {
        return isEnableWeek;
    }

    public void setIsEnableWeek(String isEnableWeek) {
        this.isEnableWeek = isEnableWeek;
    }

    public int getIsEnableWeekIndex() {
        return isEnableWeekIndex;
    }

    public void setIsEnableWeekIndex(int isEnableWeekIndex) {
        this.isEnableWeekIndex = isEnableWeekIndex;
    }


    public boolean isAllWeek() {
        return isAllWeek;
    }

    public void setAllWeek(boolean allWeek) {
        isAllWeek = allWeek;
    }

    public boolean isSpecial() {
        return isSpecial;
    }

    public void setSpecial(boolean special) {
        isSpecial = special;
    }
}
