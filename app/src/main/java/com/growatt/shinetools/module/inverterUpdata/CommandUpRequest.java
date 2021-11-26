package com.growatt.shinetools.module.inverterUpdata;

import com.growatt.shinetools.utils.CommenUtils;

/**
 * Created by dg on 2017/9/26.
 */

public class CommandUpRequest {
    //通讯编号
    private byte[] comNumber = new byte[]{0x00,0x01};
    //协议标识
    private byte[] proId = new byte[]{0x00,0x03};
    //数服协议数据长度：2字节
    private byte No_dataH;
    private byte No_dataL;
    //逆变器地址
    private byte address = 0x01;
    //功能码
    private byte funCode = 0x17;
    //采集器序列号10字节
    private byte[] datalogSn  = "0000000000".getBytes();
    //透传数据长度
    private byte Modbus_dataH;
    private byte Modbus_dataL;
    //透传数据区域
    private byte[] datas;


    //经过加密的数据
    private byte[] encryptedData;
    //校验区（CRC16）
    private byte[] crcData;


    public byte[] getComNumber() {
        return comNumber;
    }

    public void setComNumber(byte[] comNumber) {
        this.comNumber = comNumber;
    }

    public byte[] getProId() {
        return proId;
    }

    public void setProId(byte[] proId) {
        this.proId = proId;
    }

    public byte getNo_dataH() {
        return No_dataH;
    }

    public void setNo_dataH(byte no_dataH) {
        No_dataH = no_dataH;
    }

    public byte getNo_dataL() {
        return No_dataL;
    }

    public void setNo_dataL(byte no_dataL) {
        No_dataL = no_dataL;
    }

    public byte getAddress() {
        return address;
    }

    public void setAddress(byte address) {
        this.address = address;
    }

    public byte getFunCode() {
        return funCode;
    }

    public void setFunCode(byte funCode) {
        this.funCode = funCode;
    }

    public byte[] getDatalogSn() {
        return datalogSn;
    }

    public void setDatalogSn(byte[] datalogSn) {
        this.datalogSn = datalogSn;
    }

    public byte getModbus_dataH() {
        return Modbus_dataH;
    }

    public void setModbus_dataH(byte modbus_dataH) {
        Modbus_dataH = modbus_dataH;
    }

    public byte getModbus_dataL() {
        return Modbus_dataL;
    }

    public void setModbus_dataL(byte modbus_dataL) {
        Modbus_dataL = modbus_dataL;
    }

    public byte[] getDatas() {
        return datas;
    }

    public void setDatas(byte[] datas) {
        this.datas = datas;
    }
    public byte[] getBytes(){
        int i1 = CommenUtils.byte2Int(new byte[]{this.No_dataH, this.No_dataL});
        int len = i1+datas.length + 6;

        byte[] bytes = new byte[len];
        bytes[0] = this.comNumber[0];
        bytes[1] = this.comNumber[1];
        bytes[2] = this.proId[0];
        bytes[3] = this.proId[1];
        bytes[4] = this.No_dataH;
        bytes[5] = this.No_dataL;
        bytes[6] = this.address;
        bytes[7] = this.funCode;
        for (int i = 0;i< datalogSn.length;i++){
            bytes[8+i] = datalogSn[i];
        }
        bytes[18] = Modbus_dataH;
        bytes[19] = Modbus_dataL;
        for (int i = 0;i< datas.length;i++){
            bytes[20+i] = datas[i];
        }
        return bytes;
    }
    public byte[] getBytesV2(){
        int dateL = this.No_dataL&0xff;
        int len = dateL + 6;
        byte[] bytes = new byte[len];
        bytes[0] = this.comNumber[0];
        bytes[1] = this.comNumber[1];
        bytes[2] = this.proId[0];
        bytes[3] = this.proId[1];
        bytes[4] = this.No_dataH;
        bytes[5] = this.No_dataL;
        bytes[6] = this.address;
        bytes[7] = this.funCode;
        for (int i = 0;i< datalogSn.length;i++){
            bytes[8+i] = datalogSn[i];
        }
        bytes[18] = Modbus_dataH;
        bytes[19] = Modbus_dataL;
        for (int i = 0;i< datas.length;i++){
            bytes[20+i] = datas[i];
        }
        return bytes;
    }


    public byte[] getEncryptedData() {
        return encryptedData;
    }

    public void setEncryptedData(byte[] encryptedData) {
        this.encryptedData = encryptedData;
    }

    public byte[] getCrcData() {
        return crcData;
    }

    public void setCrcData(byte[] crcData) {
        this.crcData = crcData;
    }



    public byte[] getBytesCRC(){
        int length=getEncryptedData().length+getCrcData().length;
        byte[]data=new byte[length];
        if (getEncryptedData() != null){
            System.arraycopy(getEncryptedData(),0,data,0,getEncryptedData().length);
        }
        if (getCrcData() != null){
            System.arraycopy(getCrcData(),0,data,getEncryptedData().length,getCrcData().length);
        }
        return data;
    }

}
