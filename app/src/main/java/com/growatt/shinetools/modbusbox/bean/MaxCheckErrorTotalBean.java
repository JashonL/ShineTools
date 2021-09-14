package com.growatt.shinetools.modbusbox.bean;


import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created：2018/2/4 on 14:52
 * Author:gaideng on dg
 * Description:
 */

public class MaxCheckErrorTotalBean {
    /**
     * 当前故障序号
     */
    private int errNum;
    /**
     * 当前故障码
     */
    private List<Integer> errCodes = new ArrayList<>();
    /**
     * 当前波形时间:yy-MM-dd HH:mm:ss
     */
    private List<String> times = new ArrayList<>();
    /**
     * 波形图数据
     */
    private List<ArrayList<Entry>> dataList = new ArrayList<>();
    /**
     * 波形图id
     */
    private List<Integer> ids = new ArrayList<>();

    /**
     * 数据放大倍数
     */
    private List<Double> mults = new ArrayList<>();

    public List<Double> getMults() {
        return mults;
    }

    public void setMults(List<Double> mults) {
        this.mults = mults;
    }

    public int getErrNum() {
        return errNum;
    }

    public void setErrNum(int errNum) {
        this.errNum = errNum;
    }

    public List<Integer> getErrCodes() {
        return errCodes;
    }

    public void setErrCodes(List<Integer> errCodes) {
        this.errCodes = errCodes;
    }

    public List<String> getTimes() {
        return times;
    }

    public void setTimes(List<String> times) {
        this.times = times;
    }

    public List<ArrayList<Entry>> getDataList() {
        return dataList;
    }

    public void setDataList(List<ArrayList<Entry>> dataList) {
//        this.dataList = dataList;
        this.dataList.addAll(dataList);
    }

    public List<Integer> getIds() {
        return ids;
    }

    public void setIds(List<Integer> ids) {
        this.ids = ids;
    }
}
