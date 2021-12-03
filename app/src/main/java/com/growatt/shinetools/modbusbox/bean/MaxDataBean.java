package com.growatt.shinetools.modbusbox.bean;



import com.growatt.shinetools.modbusbox.Arith;
import com.growatt.shinetools.module.localbox.ustool.bean.BDCInfoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dg on 2017/10/25.
 * max、tlx 、tlxh主界面所有数据实体
 */

public class MaxDataBean {
    private final double muilt = 0.1;
    //设备类型code  DTC == 210 老机告警码特殊处理
    private int deviceTypeCode = -1;
    //逆变器状态：0:Waiting, 1:Normal, 2:Upgrade;3:Fault
    private int status = -1;
    //今日和累计电量，倍数0.1
    private int todayEnergy;
    private int totalEnergy;
    //当前功率和装机功率，*0.1
    private int normalPower;
    private int totalPower;
    //顺逆流 用户 电网功率，*0.1
    private int ptouser;
    private int ptogrid;
    private int pusertogrid;
    //干接点 0:turn off;1:turn on
    private int dryStatus;
    //BDC 0:not detected BDC;1:BDC1 2:bdc2  3:bdc1 + bdc2
    //BDC 0:not detected BDC;1:BDC detected
    private int bdcStatus;
    //主故障和告警
    private int errCode;
    private int warmCode;
    //故障详情数据 mix使用
    private String errDetail = "";
    private String warmDetail = "";
    // 副故障和告警
    private int errCodeSecond;
    private int warmCodeSecond;
    //故障位
    private int error1;
    private int error2;
    //mix部分
    private int pacToUser;//取电功率
    private int pacToGrid;//馈电功率
    private int sysFaultWord;
    private int sysFaultWord1;
    private int sysFaultWord2;
    private int sysFaultWord3;
    private int sysFaultWord4;
    private int sysFaultWord5;
    private int sysFaultWord6;
    private int sysFaultWord7;



    private String bdcVervison;

    private String batVersion;//电池版本

    private int bdcNumber;

    private int batteryNumber;//电池数量


    public int getBatteryNumber() {
        return batteryNumber;
    }

    public void setBatteryNumber(int batteryNumber) {
        this.batteryNumber = batteryNumber;
    }

    public String getBatVersion() {
        return batVersion;
    }

    public void setBatVersion(String batVersion) {
        this.batVersion = batVersion;
    }

    public String getBdcVervison() {
        return bdcVervison;
    }
    public void setBdcVervison(String bdcVervison) {
        this.bdcVervison = bdcVervison;
    }

    public int getSysFaultWord() {
        return sysFaultWord;
    }

    public void setSysFaultWord(int sysFaultWord) {
        this.sysFaultWord = sysFaultWord;
    }

    public int getSysFaultWord1() {
        return sysFaultWord1;
    }

    public void setSysFaultWord1(int sysFaultWord1) {
        this.sysFaultWord1 = sysFaultWord1;
    }

    public int getSysFaultWord2() {
        return sysFaultWord2;
    }

    public void setSysFaultWord2(int sysFaultWord2) {
        this.sysFaultWord2 = sysFaultWord2;
    }

    public int getSysFaultWord3() {
        return sysFaultWord3;
    }

    public void setSysFaultWord3(int sysFaultWord3) {
        this.sysFaultWord3 = sysFaultWord3;
    }

    public int getSysFaultWord4() {
        return sysFaultWord4;
    }

    public void setSysFaultWord4(int sysFaultWord4) {
        this.sysFaultWord4 = sysFaultWord4;
    }

    public int getSysFaultWord5() {
        return sysFaultWord5;
    }

    public void setSysFaultWord5(int sysFaultWord5) {
        this.sysFaultWord5 = sysFaultWord5;
    }

    public int getSysFaultWord6() {
        return sysFaultWord6;
    }

    public void setSysFaultWord6(int sysFaultWord6) {
        this.sysFaultWord6 = sysFaultWord6;
    }

    public int getSysFaultWord7() {
        return sysFaultWord7;
    }

    public void setSysFaultWord7(int sysFaultWord7) {
        this.sysFaultWord7 = sysFaultWord7;
    }

    public int getPacToUser() {
        return pacToUser;
    }

    public void setPacToUser(int pacToUser) {
        this.pacToUser = pacToUser;
    }

    public int getPacToGrid() {
        return pacToGrid;
    }

    public void setPacToGrid(int pacToGrid) {
        this.pacToGrid = pacToGrid;
    }

    //设备信息
    private MaxDataDeviceBean mDeviceBeen = new MaxDataDeviceBean();
    //储能信息
    private ToolStorageDataBean mStorageBeen = new ToolStorageDataBean();
    //pv信息
    private List<String> mPVList = new ArrayList<>();
    //PV串信息
    private List<String> mPVCList = new ArrayList<>();
    //ac信息
    private List<String> mACList = new ArrayList<>();
    //pid信息
    private List<String> mPIDList = new ArrayList<>();
    //SVG/APF Status
    private List<String> mSVGList = new ArrayList<>();

    public List<String> getSVGList() {
        return mSVGList;
    }

    /**
     * 今日馈回电网/并网电量 3071 3072
     */
    private int etoGridToday;
    /**
     * 累计馈回电网/并网电量 3073 3074
     */
    private int etoGridTotal;
    /**
     * 今日用电消耗 3075 3076
     */
    private int eLoadToday;
    /**
     * 累计用电消耗 3077 3078
     */
    private int eLoadTotal;
    /**
     * 日 总取电电量
     */
    private int eObtainToday;
    private int eObtainTotal;


    public void setSVGList(List<String> SVGList) {
        mSVGList = SVGList;
    }

    public double getTodayEnergy() {
        return Arith.mul(todayEnergy,muilt);
    }

    public void setTodayEnergy(int todayEnergy) {
        this.todayEnergy = todayEnergy;
    }

    public double getTotalEnergy() {
        return Arith.mul(totalEnergy,muilt);
    }

    public void setTotalEnergy(int totalEnergy) {
        this.totalEnergy = totalEnergy;
    }

    public double getNormalPower() {
        return Arith.mul(normalPower,muilt);
    }
    public double getNormalPowerMix() {
        return Arith.mul(normalPower>32767?-(65534-normalPower):normalPower,muilt);
    }

    public void setNormalPower(int normalPower) {
        this.normalPower = normalPower;
    }

    public double getPtouser() {
        return Arith.mul(ptouser,muilt);
    }

    public void setPtouser(int ptouser) {
        this.ptouser = ptouser;
    }

    public double getPtogrid() {
        return Arith.mul(ptogrid,muilt);
    }

    public void setPtogrid(int ptogrid) {
        this.ptogrid = ptogrid;
    }

    public double getPusertogrid() {
        return Arith.mul(pusertogrid,muilt);
    }

    public void setPusertogrid(int pusertogrid) {
        this.pusertogrid = pusertogrid;
    }

    public int getDryStatus() {
        return dryStatus;
    }

    public void setDryStatus(int dryStatus) {
        this.dryStatus = dryStatus;
    }

    public int getBdcStatus() {
        return bdcStatus;
    }

    public void setBdcStatus(int bdcStatus) {
        this.bdcStatus = bdcStatus;
    }

    public double getTotalPower() {
        return Arith.mul(totalPower,muilt);
    }

    public void setTotalPower(int todayPower) {
        this.totalPower = todayPower;
    }

    public MaxDataDeviceBean getDeviceBeen() {
        return mDeviceBeen;
    }

    public void setDeviceBeen(MaxDataDeviceBean deviceBeen) {
        mDeviceBeen = deviceBeen;
    }

    public List<String> getPVList() {
        return mPVList;
    }

    public void setPVList(List<String> PVList) {
        mPVList = PVList;
    }

    public List<String> getPVCList() {
        return mPVCList;
    }

    public void setPVCList(List<String> PVCList) {
        mPVCList = PVCList;
    }

    public List<String> getACList() {
        return mACList;
    }

    public void setACList(List<String> ACList) {
        mACList = ACList;
    }

    public List<String> getPIDList() {
        return mPIDList;
    }

    public void setPIDList(List<String> PIDList) {
        mPIDList = PIDList;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public int getWarmCode() {
        return warmCode;
    }

    public void setWarmCode(int warmCode) {
        this.warmCode = warmCode;
    }

    public int getError1() {
        return error1;
    }

    public void setError1(int error1) {
        this.error1 = error1;
    }

    public int getError2() {
        return error2;
    }

    public void setError2(int error2) {
        this.error2 = error2;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getErrCodeSecond() {
        return errCodeSecond;
    }

    public void setErrCodeSecond(int errCodeSecond) {
        this.errCodeSecond = errCodeSecond;
    }

    public int getWarmCodeSecond() {
        return warmCodeSecond;
    }

    public void setWarmCodeSecond(int warmCodeSecond) {
        this.warmCodeSecond = warmCodeSecond;
    }

    public int getDeviceTypeCode() {
        return deviceTypeCode;
    }

    public void setDeviceTypeCode(int deviceTypeCode) {
        this.deviceTypeCode = deviceTypeCode;
    }

    public ToolStorageDataBean getStorageBeen() {
        return mStorageBeen;
    }

    public void setStorageBeen(ToolStorageDataBean storageBeen) {
        mStorageBeen = storageBeen;
    }

    public void setEtoGridToday(int etoGridToday) {
        this.etoGridToday = etoGridToday;
    }

    public void setEtoGridTotal(int etoGridTotal) {
        this.etoGridTotal = etoGridTotal;
    }

    public void seteLoadToday(int eLoadToday) {
        this.eLoadToday = eLoadToday;
    }

    public void seteLoadTotal(int eLoadTotal) {
        this.eLoadTotal = eLoadTotal;
    }

    public double getEtoGridToday() {
        return Arith.mul(etoGridToday,muilt);
    }

    public double getEtoGridTotal() {
        return Arith.mul(etoGridTotal,muilt);
    }

    public double geteLoadToday() {
        return Arith.mul(eLoadToday,muilt);
    }

    public double geteLoadTotal() {
        return Arith.mul(eLoadTotal,muilt);
    }

    public double geteObtainToday() {
        return Arith.mul(eObtainToday,muilt);
    }

    public void seteObtainToday(int eObtainToday) {
        this.eObtainToday = eObtainToday;
    }

    public double geteObtainTotal() {
        return Arith.mul(eObtainTotal,muilt);
    }

    public void seteObtainTotal(int eObtainTotal) {
        this.eObtainTotal = eObtainTotal;
    }

    public String getErrDetail() {
        return errDetail;
    }

    public void setErrDetail(String errDetail) {
        this.errDetail = errDetail;
    }

    public String getWarmDetail() {
        return warmDetail;
    }

    public void setWarmDetail(String warmDetail) {
        this.warmDetail = warmDetail;
    }

    /**
     * 储能信息
     */
    private BDCInfoBean bdcInfoBean =new BDCInfoBean();

    public BDCInfoBean getBdcInfoBean() {
        return bdcInfoBean;
    }

    public void setBdcInfoBean(BDCInfoBean bdcInfoBean) {
        this.bdcInfoBean = bdcInfoBean;
    }


    public int getBdcNumber() {
        return bdcNumber;
    }

    public void setBdcNumber(int bdcNumber) {
        this.bdcNumber = bdcNumber;
    }
}
