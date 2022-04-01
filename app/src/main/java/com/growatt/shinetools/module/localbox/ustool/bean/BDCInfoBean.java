package com.growatt.shinetools.module.localbox.ustool.bean;

public class BDCInfoBean {

    //电池类型
    private String batModel;
    //固件版本
    private String dsp_version;
    //bdc序列号
    private String bdc_serialnumber;
    //模式
    private String bdc_mode;
    //m3
    private String m3_version;
    //bms版本
    private String bms_version;

    //bms硬件版本
    private String bmsHwVersion;

    //bdc硬件版本
    private String bdcHwVersion;

    //状态
    private int status;
    //工作模式
    private int workMode;
    //电池充电功率
    private String battery_charge_power;
    //电池放电功率
    private String battery_dischage_power;
    //日充电量
    private String charge_dayly;
    //日放电量
    private String discharge_dayly;
    //总充电量
    private String chage_total;
    //总放电量
    private String discharge_total;
    //故障码
    private String errorcode;
    //告警码
    private String warncode;


    //上电感电流
    private String upper_current;
    //下电感电流
    private String low_current;
    //bat电压
    private String bat_voltage;
    //bat电流
    private String bat_current;
    //温度a
    private String temp_a;
    //温度b
    private String temp_b;
    //上bus电压
    private String up_busvoltage;
    //下bus电压
    private String down_busvoltage;
    //总bus电压
    private String total_busvoltage;


    //电池通信类型
    private String battery_type;
    //电池厂商
    private String battery_company;
    //电池工作模式
    private String battery_workmode;
    //电池状态
    private String battery_status;
    //电池电压
    private String battery_voltage;
    //电池电流
    private String battery_current;
    //最大充电电流
    private String battery_max_current;
    //最大放电电流
    private String battery_discharge_current;
    //soc
    private String battery_soc;
    //soh
    private String battery_soh;
    //cv电压
    private String battery_cv_voltage;
    //电池温度
    private String battery_temp;
    //电池保护信息
    private String battery_protect;

    //电池警告信息
    private String battery_warning;

    //电池故障信息
    private String battery_error;

    //电池数量
    private String battery_num;


    public String getBattery_num() {
        return battery_num;
    }

    public void setBattery_num(String battery_num) {
        this.battery_num = battery_num;
    }

    public String getBdcHwVersion() {
        return bdcHwVersion;
    }

    public void setBdcHwVersion(String bdcHwVersion) {
        this.bdcHwVersion = bdcHwVersion;
    }

    public String getDsp_version() {
        return dsp_version;
    }

    public void setDsp_version(String dsp_version) {
        this.dsp_version = dsp_version;
    }

    public String getBdc_serialnumber() {
        return bdc_serialnumber;
    }

    public void setBdc_serialnumber(String bdc_serialnumber) {
        this.bdc_serialnumber = bdc_serialnumber;
    }

    public String getBdc_mode() {
        return bdc_mode;
    }

    public void setBdc_mode(String bdc_mode) {
        this.bdc_mode = bdc_mode;
    }

    public String getM3_version() {
        return m3_version;
    }

    public void setM3_version(String m3_version) {
        this.m3_version = m3_version;
    }

    public String getBms_version() {
        return bms_version;
    }

    public void setBms_version(String bms_version) {
        this.bms_version = bms_version;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getWorkMode() {
        return workMode;
    }

    public void setWorkMode(int workMode) {
        this.workMode = workMode;
    }

    public String getBattery_charge_power() {
        return battery_charge_power;
    }

    public void setBattery_charge_power(String battery_charge_power) {
        this.battery_charge_power = battery_charge_power;
    }

    public String getBattery_dischage_power() {
        return battery_dischage_power;
    }

    public void setBattery_dischage_power(String battery_dischage_power) {
        this.battery_dischage_power = battery_dischage_power;
    }

    public String getCharge_dayly() {
        return charge_dayly;
    }

    public void setCharge_dayly(String charge_dayly) {
        this.charge_dayly = charge_dayly;
    }

    public String getDischarge_dayly() {
        return discharge_dayly;
    }

    public void setDischarge_dayly(String discharge_dayly) {
        this.discharge_dayly = discharge_dayly;
    }

    public String getChage_total() {
        return chage_total;
    }

    public void setChage_total(String chage_total) {
        this.chage_total = chage_total;
    }

    public String getDischarge_total() {
        return discharge_total;
    }

    public void setDischarge_total(String discharge_total) {
        this.discharge_total = discharge_total;
    }

    public String getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(String errorcode) {
        this.errorcode = errorcode;
    }

    public String getWarncode() {
        return warncode;
    }

    public void setWarncode(String warncode) {
        this.warncode = warncode;
    }

    public String getUpper_current() {
        return upper_current;
    }

    public void setUpper_current(String upper_current) {
        this.upper_current = upper_current;
    }

    public String getLow_current() {
        return low_current;
    }

    public void setLow_current(String low_current) {
        this.low_current = low_current;
    }

    public String getBat_voltage() {
        return bat_voltage;
    }

    public void setBat_voltage(String bat_voltage) {
        this.bat_voltage = bat_voltage;
    }

    public String getBat_current() {
        return bat_current;
    }

    public void setBat_current(String bat_current) {
        this.bat_current = bat_current;
    }

    public String getTemp_a() {
        return temp_a;
    }

    public void setTemp_a(String temp_a) {
        this.temp_a = temp_a;
    }

    public String getTemp_b() {
        return temp_b;
    }

    public void setTemp_b(String temp_b) {
        this.temp_b = temp_b;
    }

    public String getUp_busvoltage() {
        return up_busvoltage;
    }

    public void setUp_busvoltage(String up_busvoltage) {
        this.up_busvoltage = up_busvoltage;
    }

    public String getDown_busvoltage() {
        return down_busvoltage;
    }

    public void setDown_busvoltage(String down_busvoltage) {
        this.down_busvoltage = down_busvoltage;
    }

    public String getTotal_busvoltage() {
        return total_busvoltage;
    }

    public void setTotal_busvoltage(String total_busvoltage) {
        this.total_busvoltage = total_busvoltage;
    }

    public String getBattery_type() {
        return battery_type;
    }

    public void setBattery_type(String battery_type) {
        this.battery_type = battery_type;
    }

    public String getBattery_company() {
        return battery_company;
    }

    public void setBattery_company(String battery_company) {
        this.battery_company = battery_company;
    }

    public String getBattery_workmode() {
        return battery_workmode;
    }

    public void setBattery_workmode(String battery_workmode) {
        this.battery_workmode = battery_workmode;
    }

    public String getBattery_status() {
        return battery_status;
    }

    public void setBattery_status(String battery_status) {
        this.battery_status = battery_status;
    }

    public String getBattery_voltage() {
        return battery_voltage;
    }

    public void setBattery_voltage(String battery_voltage) {
        this.battery_voltage = battery_voltage;
    }

    public String getBattery_current() {
        return battery_current;
    }

    public void setBattery_current(String battery_current) {
        this.battery_current = battery_current;
    }

    public String getBattery_max_current() {
        return battery_max_current;
    }

    public void setBattery_max_current(String battery_max_current) {
        this.battery_max_current = battery_max_current;
    }

    public String getBattery_discharge_current() {
        return battery_discharge_current;
    }

    public void setBattery_discharge_current(String battery_discharge_current) {
        this.battery_discharge_current = battery_discharge_current;
    }

    public String getBattery_soc() {
        return battery_soc;
    }

    public void setBattery_soc(String battery_soc) {
        this.battery_soc = battery_soc;
    }

    public String getBattery_soh() {
        return battery_soh;
    }

    public void setBattery_soh(String battery_soh) {
        this.battery_soh = battery_soh;
    }

    public String getBattery_cv_voltage() {
        return battery_cv_voltage;
    }

    public void setBattery_cv_voltage(String battery_cv_voltage) {
        this.battery_cv_voltage = battery_cv_voltage;
    }

    public String getBattery_temp() {
        return battery_temp;
    }

    public void setBattery_temp(String battery_temp) {
        this.battery_temp = battery_temp;
    }

    public String getBattery_protect() {
        return battery_protect;
    }

    public void setBattery_protect(String battery_protect) {
        this.battery_protect = battery_protect;
    }

    public String getBattery_warning() {
        return battery_warning;
    }

    public void setBattery_warning(String battery_warning) {
        this.battery_warning = battery_warning;
    }

    public String getBattery_error() {
        return battery_error;
    }

    public void setBattery_error(String battery_error) {
        this.battery_error = battery_error;
    }

    public String getBatModel() {
        return batModel;
    }

    public void setBatModel(String batModel) {
        this.batModel = batModel;
    }

    public String getBmsHwVersion() {
        return bmsHwVersion;
    }

    public void setBmsHwVersion(String bmsHwVersion) {
        this.bmsHwVersion = bmsHwVersion;
    }
}
