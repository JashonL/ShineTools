package com.growatt.shinetools.module.localbox.tlxh.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created：2019/7/22 on 16:00
 * Author:gaideng on administratorr
 * Description:
 */

public class TLXHAutoTestReportBean {
    private String finishTime = "";
    private String startTime = "";
    private String startDate = "";
    private String deviceSn;
    private String version;
    private String model;
    private String title;
    private List<List<TLXHToolAutoTestBean>> mList = new ArrayList<>();

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getDeviceSn() {
        return deviceSn;
    }

    public void setDeviceSn(String deviceSn) {
        this.deviceSn = deviceSn;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<List<TLXHToolAutoTestBean>> getList() {
        return mList;
    }

    public void setList(List<List<TLXHToolAutoTestBean>> list) {
        mList = list;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
