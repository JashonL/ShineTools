package com.growatt.shinetools.modbusbox.bean;


import com.growatt.shinetools.modbusbox.Arith;

import java.text.DecimalFormat;

/**
 * Created：2019/1/11 on 14:48
 * Author:gaideng on dg
 * Description:tlxh 储能相关信息
 */

public class ToolStorageDataBean {
    private final double muilt10 = 0.1;
    private final double muilt100 = 0.01;
    /*-----------------BDC相关--------------------*/
    /**
     * BDC固件版本
     */
    private String fwVersionBDC;
    /**
     * BDC序列号
     */
    private String snBDC;
    /**
     * BDC状态:3166 低八位
     * 低8位表示状态；
     0：待机状态；
     1：正常状态；
     2：故障状态；
     3：升级状态；
     */
    private int statusBDC = -1;
    /**
     * BDC工作模式:3166 高八位
     */
    private int workModeBDC = -1;

    private int statusBDC02 = -1;
    /**
     * BDC工作模式:3166 高八位
     */
    private int workModeBDC02 = -1;
    /**
     * BDC1+BDC2充电功率
     */
    private int pCharge ;
    /**
     * BDC1+BDC2放电功率:
     */
    private int pDischarge ;
    /**
     * 充电功率BDC1:3180-3181
     */
    private int pChargeBDC1 ;
    /**
     * 放电功率BDC1:3178-3179
     */
    private int pDischargeBDC1 ;
    /**
     * 充电功率BDC2:3203-3204
     */
    private int pChargeBDC2 ;
    /**
     * 放电功率BDC2:3201-3202
     */
    private int pDischargeBDC2 ;
    /**
     * 当天充电量:3129-3130
     */
    private int eChargeToday ;
    /**
     * 当天放电量:3125-3126
     */
    private int eDischargeToday ;
    /**
     * 总充电量:3131-3132
     */
    private int eChargeTotal ;
    /**
     * 总放电量:3127-3128
     */
    private int eDischargeTotal ;
    /**
     * 储能故障码 3167
     */
    private int errorStorage ;
    /**
     * 储能告警码 3168
     */
    private int warmStorage ;
    /**
     * 储能故障码 3190
     */
    private int errorStorage02 ;
    /**
     * 储能告警码 3191
     */
    private int warmStorage02 ;
    /**
     * 储能 子故障码 3187 bit12-15
     */
    private int error2Storage ;
    /**
     * 储能 子告警码 3187 bit8-11
     */
    private int warm2Storage ;
    /**
     * 储能 子故障码 3210 bit12-15
     */
    private int error2Storage02 ;
    /**
     * 储能 子告警码 3210 bit8-11
     */
    private int warm2Storage02 ;
    /*-----------------电池相关--------------------*/
    /**
     * BMS通信接口类型 3229
     0: RS485;
     1: CAN;
     */
    private int bmsComType = -1 ;
    /**
     * 电池厂商 3224
     */
    private int batteryCompany;
    /**
     * 电池工作模式 3211
     * 0：不充放电；
     1：充电；
     2：放电；
     */
    private int bmsWorkType;
    /**
     * 电池状态 3212
     */
    private int bmsStatus;
    /**
     * 电池故障 3213
     */
    private int bmsError;
    /**
     * 电池告警 3214
     */
    private int bmsWarm;
    /**
     * 电池电压 3216
     */
    private int vBms;
    /**
     * 电池电流 3217
     */
    private int aBms;
    /**
     * 电池soc 3215
     */
    private int soc;
    /**
     * 电池温度 3218
     */
    private int tempBms;
    /**
     * cv电压 3223
     */
    private int vCV;
    /**
     * 最大充电电流 3219
     */
    private int aChargeMax;



    private int aDischargeMax;
    /*-----------------电池相关 2  + 19--------------------*/
    /**
     * BMS通信接口类型 3229
     0: RS485;
     1: CAN;
     */
    private int bmsComType02 = -1 ;
    /**
     * 电池厂商 3224
     */
    private int batteryCompany02;
    /**
     * 电池工作模式 3212
     * 0：不充放电；
     1：充电；
     2：放电；
     */
    private int bmsWorkType02;
    /**
     * 电池状态 3212
     */
    private int bmsStatus02;
    /**
     * 电池故障 3213
     */
    private int bmsError02;
    /**
     * 电池告警 3214
     */
    private int bmsWarm02;
    /**
     * 电池电压 3216
     */
    private int vBms02;
    /**
     * 电池电流 3217
     */
    private int aBms02;
    /**
     * 电池soc 3215
     */
    private int soc02;
    /**
     * 电池温度 3218
     */
    private int tempBms02;
    /**
     * cv电压 3223
     */
    private int vCV02;
    /**
     * 最大充电电流 3219
     */
    private int aChargeMax02;
    private int aDischargeMax02;
    /*-----------------离网信息--------------------*/
    /**
     * 离网电压3146 3150 3154
     */
    private int vAc1;
    private int vAc2;
    private int vAc3;
    /**
     * 离网电流 3147 3151 3155
     */
    private int aAc1;
    private int aAc2;
    private int aAc3;
    /**
     * 离网功率 3148/3149 3152/3153 3156/3157
     */
    private int pAc1;
    private int pAc2;
    private int pAc3;
    /**
     * 离网总输出功率3158 3159
     */
    private int pEPSTotal;
    /**
     * 离网频率 3145
     */
    private int facEPS;
    /**
     * 负载量 3160
     */
    private int loadPercent;
    /**
     * 3161
     */
    private int pf;
/*-------------内部信息--------------------*/
    /**
     * bat电压 3169
     */
    private int vBat;
    /**
     * bus1电压 3172
     */
    private int vBus1;
    /**
     * bus2电压 3173
     */
    private int vBus2;
    /**
     * bus电压3188
     */
    private int vBus3;


    /**
     * bat电流 3170
     */
    private int aBat;
    /**
     * bb电流 3174
     */
    private int aBB;
    /**
     * llc电流 3175
     */
    private int aLLC;
    /**
     * 温度A 3176
     */
    private int tempA;
    /**
     * 温度B 3177
     */
    private int tempB;
/*-------------内部信息2 寄存器+ 23--------------------*/
    /**
     * bat电压 3169
     */
    private int vBat02;
    /**
     * bus1电压 3172
     */
    private int vBus102;
    /**
     * bus2电压 3173
     */
    private int vBus202;
    /**
     * bat电流 3170
     */
    private int aBat02;
    /**
     * bb电流 3174
     */
    private int aBB02;
    /**
     * llc电流 3175
     */
    private int aLLC02;
    /**
     * 温度A 3176
     */
    private int tempA02;
    /**
     * 温度B 3177
     */
    private int tempB02;

    /**
     * 电池保护信息
     */
    private int bmsProtect1;
    private int bmsProtect2;
    private int bmsProtect3;
    /**
     * 电池告警信息
     */
    private int bmsWarining;
    private int bmsWarining2;
    private int bmsWarining3;
    /**
     * 电池故障信息
     */
    private int bmsError1;
    private int bmsError2;
    /**
     * 电池soc 3222
     */
    private int soh;


    /*-------------bdc相关 mix--------------------*/
    private int bdcBatV;//电池电压 1013
    private int bdcSoc;//电池电压 1014
    private int bdcPCharge;//充电功率 1009-1010
    private int bdcPDisCharge;//放电功率 1011-1012
    private int bdcVbatDsp;//电池电压 1042

    public double getBdcBatV() {
        return Arith.mul(bdcBatV,muilt10);
    }
    public void setBdcBatV(int bdcBatV) {
        this.bdcBatV = bdcBatV;
    }

    public String getBdcSoc() {
        return bdcSoc + "%";
    }
    public void setBdcSoc(int bdcSoc) {
        this.bdcSoc = bdcSoc;
    }

    public double getBdcPCharge() {
        return Arith.mul(bdcPCharge,muilt10);
    }
    public void setBdcPCharge(int bdcPCharge) {
        this.bdcPCharge = bdcPCharge;
    }
    public double getBdcPDisCharge() {
        return Arith.mul(bdcPDisCharge,muilt10);
    }

    public void setBdcPDisCharge(int bdcPDisCharge) {
        this.bdcPDisCharge = bdcPDisCharge;
    }

    public double getBdcVbatDsp() {
        return Arith.mul(bdcVbatDsp,muilt10);
    }

    public void setBdcVbatDsp(int bdcVbatDsp) {
        this.bdcVbatDsp = bdcVbatDsp;
    }

    public void setFwVersionBDC(String fwVersionBDC) {
        this.fwVersionBDC = fwVersionBDC;
    }

    public void setSnBDC(String snBDC) {
        this.snBDC = snBDC;
    }

    public void setStatusBDC(int statusBDC) {
        this.statusBDC = statusBDC;
    }

    public void setWorkModeBDC(int workModeBDC) {
        this.workModeBDC = workModeBDC;
    }

    public void setpCharge(int pCharge) {
        this.pCharge = pCharge;
    }

    public void setpDischarge(int pDischarge) {
        this.pDischarge = pDischarge;
    }

    public void seteChargeToday(int eChargeToday) {
        this.eChargeToday = eChargeToday;
    }

    public void seteDischargeToday(int eDischargeToday) {
        this.eDischargeToday = eDischargeToday;
    }

    public void seteChargeTotal(int eChargeTotal) {
        this.eChargeTotal = eChargeTotal;
    }

    public void seteDischargeTotal(int eDischargeTotal) {
        this.eDischargeTotal = eDischargeTotal;
    }

    public void setErrorStorage(int errorStorage) {
        this.errorStorage = errorStorage;
    }

    public void setWarmStorage(int warmStorage) {
        this.warmStorage = warmStorage;
    }

    public void setBmsComType(int bmsComType) {
        this.bmsComType = bmsComType;
    }

    public void setBatteryCompany(int batteryCompany) {
        this.batteryCompany = batteryCompany;
    }

    public void setBmsWorkType(int bmsWorkType) {
        this.bmsWorkType = bmsWorkType;
    }

    public void setBmsStatus(int bmsStatus) {
        this.bmsStatus = bmsStatus;
    }

    public void setBmsError(int bmsError) {
        this.bmsError = bmsError;
    }

    public void setBmsWarm(int bmsWarm) {
        this.bmsWarm = bmsWarm;
    }

    public void setvBms(int vBms) {
        this.vBms = vBms;
    }

    public void setaBms(int aBms) {
        this.aBms = aBms;
    }

    public void setSoc(int soc) {
        this.soc = soc;
    }

    public void setTempBms(int tempBms) {
        this.tempBms = tempBms;
    }

    public void setvCV(int vCV) {
        this.vCV = vCV;
    }

    public void setaChargeMax(int aChargeMax) {
        this.aChargeMax = aChargeMax;
    }

    public void setaDischargeMax(int aDischargeMax) {
        this.aDischargeMax = aDischargeMax;
    }

    public void setvAc1(int vAc1) {
        this.vAc1 = vAc1;
    }

    public void setvAc2(int vAc2) {
        this.vAc2 = vAc2;
    }

    public void setvAc3(int vAc3) {
        this.vAc3 = vAc3;
    }

    public void setaAc1(int aAc1) {
        this.aAc1 = aAc1;
    }

    public void setaAc2(int aAc2) {
        this.aAc2 = aAc2;
    }

    public void setaAc3(int aAc3) {
        this.aAc3 = aAc3;
    }

    public void setpAc1(int pAc1) {
        this.pAc1 = pAc1;
    }

    public void setpAc2(int pAc2) {
        this.pAc2 = pAc2;
    }

    public void setpAc3(int pAc3) {
        this.pAc3 = pAc3;
    }

    public void setpEPSTotal(int pEPSTotal) {
        this.pEPSTotal = pEPSTotal;
    }

    public void setFacEPS(int facEPS) {
        this.facEPS = facEPS;
    }

    public void setLoadPercent(int loadPercent) {
        this.loadPercent = loadPercent;
    }

    public void setPf(int pf) {
        this.pf = pf;
    }

    public void setpChargeBDC1(int pChargeBDC1) {
        this.pChargeBDC1 = pChargeBDC1;
    }

    public void setpDischargeBDC1(int pDischargeBDC1) {
        this.pDischargeBDC1 = pDischargeBDC1;
    }

    public void setpChargeBDC2(int pChargeBDC2) {
        this.pChargeBDC2 = pChargeBDC2;
    }

    public void setpDischargeBDC2(int pDischargeBDC2) {
        this.pDischargeBDC2 = pDischargeBDC2;
    }

    public void setvBat(int vBat) {
        this.vBat = vBat;
    }

    public void setvBus1(int vBus1) {
        this.vBus1 = vBus1;
    }

    public void setvBus2(int vBus2) {
        this.vBus2 = vBus2;
    }

    public double getvBus3() {
        return Arith.mul(vBus3,muilt10);
    }

    public void setvBus3(int vBus3) {
        this.vBus3 = vBus3;
    }

    public void setaBat(int aBat) {
        this.aBat = aBat;
    }

    public void setaBB(int aBB) {
        this.aBB = aBB;
    }

    public void setaLLC(int aLLC) {
        this.aLLC = aLLC;
    }

    public void setTempA(int tempA) {
        this.tempA = tempA;
    }

    public void setTempB(int tempB) {
        this.tempB = tempB;
    }

    public void setvBat02(int vBat02) {
        this.vBat02 = vBat02;
    }

    public void setvBus102(int vBus102) {
        this.vBus102 = vBus102;
    }

    public void setvBus202(int vBus202) {
        this.vBus202 = vBus202;
    }

    public void setaBat02(int aBat02) {
        this.aBat02 = aBat02;
    }

    public void setaBB02(int aBB02) {
        this.aBB02 = aBB02;
    }

    public void setaLLC02(int aLLC02) {
        this.aLLC02 = aLLC02;
    }

    public void setTempA02(int tempA02) {
        this.tempA02 = tempA02;
    }

    public void setTempB02(int tempB02) {
        this.tempB02 = tempB02;
    }

    public void setErrorStorage02(int errorStorage02) {
        this.errorStorage02 = errorStorage02;
    }

    public void setWarmStorage02(int warmStorage02) {
        this.warmStorage02 = warmStorage02;
    }

    public String getFwVersionBDC() {
        return fwVersionBDC;
    }

    public String getSnBDC() {
        return snBDC;
    }

    public int getStatusBDC() {
        return statusBDC;
    }

    public int getWorkModeBDC() {
        return workModeBDC;
    }

    public double getpCharge() {
        return Arith.mul(pChargeBDC1 + 0,muilt10);
    }
    public double getpCharge2() {
        return Arith.mul(pCharge,muilt10);
    }

    public double getpDischarge() {
        return Arith.mul(pDischargeBDC1 + 0,muilt10);
    }
    public double getpDischarge2() {
        return Arith.mul(pDischarge,muilt10);
    }

    public double geteChargeToday() {
        return Arith.mul(eChargeToday,muilt10);
    }

    public double geteDischargeToday() {
        return Arith.mul(eDischargeToday,muilt10);
    }

    public double geteChargeTotal() {
        return Arith.mul(eChargeTotal,muilt10);
    }

    public double geteDischargeTotal() {
        return Arith.mul(eDischargeTotal,muilt10);
    }

    public int getErrorStorage() {
        return errorStorage;
    }

    public int getWarmStorage() {
        return warmStorage;
    }

    public int getBmsComType() {
        return bmsComType;
    }

    public int getBatteryCompany() {
        return batteryCompany;
    }

    public int getBmsWorkType() {
        return bmsWorkType;
    }

    public int getBmsStatus() {
        return bmsStatus;
    }

    public int getBmsError() {
        return bmsError;
    }

    public int getBmsWarm() {
        return bmsWarm;
    }

    public double getvBms() {
        return Arith.mul(vBms,muilt100);
    }
    public double getvBms2() {
        return Arith.mul(vBms,muilt10);
    }

    public double getaBms() {
        if (aBms > 32767){
            return Arith.mul(aBms-65536,muilt100);
        }else {
            return Arith.mul(aBms,muilt100);
        }
    }

    public String getSoc() {
        return soc + "%";
    }

    public String getTempBms() {
        return Arith.mul((short)tempBms,muilt10) + "℃";
    }

    public double getvCV() {
        return Arith.mul(vCV,muilt100);
    }

    public int getaChargeMax() {
        return aChargeMax;
    }
    public double getaChargeMax2() {
        return Arith.mul(aChargeMax,muilt100);
    }



    public double getaDischargeMax() {
        return Arith.mul(aDischargeMax,muilt100);
    }

    public double getAdisChargeMax(){

        return aDischargeMax;
    }


    public double getvAc1() {
        return Arith.mul(vAc1,muilt10);
    }

    public double getvAc2() {
        return Arith.mul(vAc2,muilt10);
    }

    public double getvAc3() {
        return Arith.mul(vAc3,muilt10);
    }

    public double getaAc1() {
        return Arith.mul(aAc1,muilt10);
    }

    public double getaAc2() {
        return Arith.mul(aAc2,muilt10);
    }

    public double getaAc3() {
        return Arith.mul(aAc3,muilt10);
    }

    public double getpAc1() {
        return Arith.mul(pAc1,muilt10);
    }

    public double getpAc2() {
        return Arith.mul(pAc2,muilt10);
    }

    public double getpAc3() {
        return Arith.mul(pAc3,muilt10);
    }

    public double getpEPSTotal() {
        return Arith.mul(pEPSTotal,muilt10);
    }

    public double getFacEPS() {
        return Arith.mul(facEPS,muilt100);
    }

    public String getLoadPercent() {
        return Arith.mul(loadPercent,muilt10) + "%";
    }

    public String getPf() {
        //        this.ipf = Arith.div(ipf - 10000,10000.0,4) + "";
        double div = Arith.div(pf - 10000, 10000.0, 2);
        if (div == -1) div = 1;
        return new DecimalFormat("0.00").format(div);
    }

    public double getpChargeBDC1() {
        return Arith.mul(pChargeBDC1,muilt10);
    }

    public double getpDischargeBDC1() {
        return Arith.mul(pDischargeBDC1,muilt10);
    }

    public double getpChargeBDC2() {
        return Arith.mul(pChargeBDC2,muilt10);
    }

    public double getpDischargeBDC2() {
        return Arith.mul(pDischargeBDC2,muilt10);
    }

    public double getvBat() {
        return Arith.mul(vBat,muilt100);
    }

    public double getvBus1() {
        return Arith.mul(vBus1,muilt10);
    }

    public double getvBus2() {
        return Arith.mul(vBus2,muilt10);
    }

    public double getaBat() {
        return Arith.mul((short)aBat,muilt10);
    }

    public double getaBB() {
        return Arith.mul((short)aBB,muilt10);
    }

    public double getaLLC() {
        return Arith.mul((short)aLLC,muilt10);
    }

    public double getTempA() {
        return Arith.mul((short)tempA,muilt10);
    }

    public double getTempB() {
        return Arith.mul((short)tempB,muilt10);

    }
    public double getvBat02() {
        return Arith.mul(vBat02,muilt100);
    }

    public double getvBus102() {
        return Arith.mul(vBus102,muilt10);
    }

    public double getvBus202() {
        return Arith.mul(vBus202,muilt10);
    }

    public double getaBat02() {
        return Arith.mul((short)aBat02,muilt10);
    }

    public double getaBB02() {
        return Arith.mul((short)aBB02,muilt10);
    }

    public double getaLLC02() {
        return Arith.mul((short)aLLC02,muilt10);
    }

    public double getTempA02() {
        return Arith.mul((short)tempA02,muilt10);
    }

    public double getTempB02() {
        return Arith.mul((short)tempB02,muilt10);
    }

    public int getErrorStorage02() {
        return errorStorage02;
    }

    public int getWarmStorage02() {
        return warmStorage02;
    }

    public int getStatusBDC02() {
        return statusBDC02;
    }

    public void setStatusBDC02(int statusBDC02) {
        this.statusBDC02 = statusBDC02;
    }

    public int getWorkModeBDC02() {
        return workModeBDC02;
    }

    public void setWorkModeBDC02(int workModeBDC02) {
        this.workModeBDC02 = workModeBDC02;
    }

    public void setBmsComType02(int bmsComType02) {
        this.bmsComType02 = bmsComType02;
    }

    public void setBatteryCompany02(int batteryCompany02) {
        this.batteryCompany02 = batteryCompany02;
    }

    public void setBmsWorkType02(int bmsWorkType02) {
        this.bmsWorkType02 = bmsWorkType02;
    }

    public void setBmsStatus02(int bmsStatus02) {
        this.bmsStatus02 = bmsStatus02;
    }

    public void setBmsError02(int bmsError02) {
        this.bmsError02 = bmsError02;
    }

    public void setBmsWarm02(int bmsWarm02) {
        this.bmsWarm02 = bmsWarm02;
    }

    public void setvBms02(int vBms02) {
        this.vBms02 = vBms02;
    }

    public void setaBms02(int aBms02) {
        this.aBms02 = aBms02;
    }

    public void setSoc02(int soc02) {
        this.soc02 = soc02;
    }

    public void setTempBms02(int tempBms02) {
        this.tempBms02 = tempBms02;
    }

    public void setvCV02(int vCV02) {
        this.vCV02 = vCV02;
    }

    public void setaChargeMax02(int aChargeMax02) {
        this.aChargeMax02 = aChargeMax02;
    }

    public void setaDischargeMax02(int aDischargeMax02) {
        this.aDischargeMax02 = aDischargeMax02;
    }
    public double getvBms02() {
        return Arith.mul(vBms02,muilt100);
    }

    public double getaBms02() {
        if (aBms02 > 32767){
            return Arith.mul(aBms02-65536,muilt100);
        }else {
            return Arith.mul(aBms02,muilt100);
        }
    }

    public String getSoc02() {
        return soc02 + "%";
    }

    public String getTempBms02() {
        return Arith.mul((short)tempBms02,muilt10) + "℃";
    }

    public double getvCV02() {
        return Arith.mul(vCV02,muilt100);
    }

    public int getBmsComType02() {
        return bmsComType02;
    }

    public int getBatteryCompany02() {
        return batteryCompany02;
    }

    public int getBmsWorkType02() {
        return bmsWorkType02;
    }

    public int getBmsStatus02() {
        return bmsStatus02;
    }

    public int getBmsError02() {
        return bmsError02;
    }

    public int getBmsWarm02() {
        return bmsWarm02;
    }

    public int getaChargeMax02() {
        return aChargeMax02;
    }

    public int getaDischargeMax02() {
        return aDischargeMax02;
    }

    public int getError2Storage() {
        return error2Storage;
    }

    public void setError2Storage(int error2Storage) {
        this.error2Storage = error2Storage;
    }

    public int getWarm2Storage() {
        return warm2Storage;
    }

    public void setWarm2Storage(int warm2Storage) {
        this.warm2Storage = warm2Storage;
    }

    public int getError2Storage02() {
        return error2Storage02;
    }

    public void setError2Storage02(int error2Storage02) {
        this.error2Storage02 = error2Storage02;
    }

    public int getWarm2Storage02() {
        return warm2Storage02;
    }

    public void setWarm2Storage02(int warm2Storage02) {
        this.warm2Storage02 = warm2Storage02;
    }


    public int getBmsProtect1() {
        return bmsProtect1;
    }

    public void setBmsProtect1(int bmsProtect1) {
        this.bmsProtect1 = bmsProtect1;
    }

    public int getBmsProtect2() {
        return bmsProtect2;
    }

    public void setBmsProtect2(int bmsProtect2) {
        this.bmsProtect2 = bmsProtect2;
    }

    public int getBmsProtect3() {
        return bmsProtect3;
    }

    public void setBmsProtect3(int bmsProtect3) {
        this.bmsProtect3 = bmsProtect3;
    }

    public int getBmsWarining() {
        return bmsWarining;
    }

    public void setBmsWarining(int bmsWarining) {
        this.bmsWarining = bmsWarining;
    }

    public int getBmsWarining2() {
        return bmsWarining2;
    }

    public void setBmsWarining2(int bmsWarining2) {
        this.bmsWarining2 = bmsWarining2;
    }

    public int getBmsWarining3() {
        return bmsWarining3;
    }

    public void setBmsWarining3(int bmsWarining3) {
        this.bmsWarining3 = bmsWarining3;
    }

    public int getBmsError1() {
        return bmsError1;
    }

    public void setBmsError1(int bmsError1) {
        this.bmsError1 = bmsError1;
    }

    public int getBmsError2() {
        return bmsError2;
    }

    public void setBmsError2(int bmsError2) {
        this.bmsError2 = bmsError2;
    }

    public int getSoh() {
        return soh;
    }

    public void setSoh(int soh) {
        this.soh = soh;
    }
}
