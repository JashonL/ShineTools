package com.growatt.shinetools.modbusbox.bean;

/**
 * Created：2017/12/7 on 10:17
 * Author:gaideng on dg
 * Description:Max本地故障历史记录bean
 */

public class MaxErrorBean {
    //故障码
    private int errCode;
    //年
    private int errYear;
    private int errMonth;
    private int errDay;
    private int errHour;
    private int errMin;
    private int errSec;
    //故障描述
    private String errContent;

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public int getErrYear() {
        return errYear;
    }

    public void setErrYear(int errYear) {
        this.errYear = errYear;
    }

    public int getErrMonth() {
        return errMonth;
    }

    public void setErrMonth(int errMonth) {
        this.errMonth = errMonth;
    }

    public int getErrDay() {
        return errDay;
    }

    public void setErrDay(int errDay) {
        this.errDay = errDay;
    }

    public int getErrHour() {
        return errHour;
    }

    public void setErrHour(int errHour) {
        this.errHour = errHour;
    }

    public int getErrMin() {
        return errMin;
    }

    public void setErrMin(int errMin) {
        this.errMin = errMin;
    }

    public int getErrSec() {
        return errSec;
    }

    public void setErrSec(int errSec) {
        this.errSec = errSec;
    }

    public String getErrContent() {
        return errContent;
    }

    public void setErrContent(String errContent) {
        this.errContent = errContent;
    }
}
