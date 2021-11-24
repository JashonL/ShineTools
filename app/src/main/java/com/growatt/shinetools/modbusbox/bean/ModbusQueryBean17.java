package com.growatt.shinetools.modbusbox.bean;

/**
 * Created by dg on 2017/9/26.
 * modbus查询实体：03,04命令
 */

public class ModbusQueryBean17 {
    private byte slaveAdd = 0x01;
    //功能码:1字节
    private byte funCode;
    //子功能码
    private byte subFunCode;

    //数据长度：2个字节
    private byte dataLen_H =0x00;
    private byte dataLen_L =0x04;


    //数据编号：子命令码是5的话需要需要发编号
    private byte dataNum_H;
    private byte dataNum_L;


    //数据
    private byte[] values;

    //crc校验
    private byte crc_H;
    private byte crc_L;


    public byte getSlaveAdd() {
        return slaveAdd;
    }

    public void setSlaveAdd(byte slaveAdd) {
        this.slaveAdd = slaveAdd;
    }

    public byte getFunCode() {
        return funCode;
    }

    public void setFunCode(byte funCode) {
        this.funCode = funCode;
    }

    public byte getSubFunCode() {
        return subFunCode;
    }

    public void setSubFunCode(byte subFunCode) {
        this.subFunCode = subFunCode;
    }

    public byte getDataLen_H() {
        return dataLen_H;
    }

    public void setDataLen_H(byte dataLen_H) {
        this.dataLen_H = dataLen_H;
    }

    public byte getDataLen_L() {
        return dataLen_L;
    }

    public void setDataLen_L(byte dataLen_L) {
        this.dataLen_L = dataLen_L;
    }

    public byte getDataNum_H() {
        return dataNum_H;
    }

    public void setDataNum_H(byte dataNum_H) {
        this.dataNum_H = dataNum_H;
    }

    public byte getDataNum_L() {
        return dataNum_L;
    }

    public void setDataNum_L(byte dataNum_L) {
        this.dataNum_L = dataNum_L;
    }

    public byte[] getValues() {
        return values;
    }

    public void setValues(byte[] values) {
        this.values = values;
    }

    public byte getCrc_H() {
        return crc_H;
    }

    public void setCrc_H(byte crc_H) {
        this.crc_H = crc_H;
    }

    public byte getCrc_L() {
        return crc_L;
    }

    public void setCrc_L(byte crc_L) {
        this.crc_L = crc_L;
    }

    public byte[] getBytes(){
        byte[] bytes = new byte[6];
        //逆变器地址
        bytes[0] = this.slaveAdd;
        //功能码
        bytes[1] = this.funCode;
        //子功能码
        bytes[2] = this.subFunCode;
        //数据长度
        bytes[3] = this.dataLen_H;
        bytes[4] = this.dataLen_L;

        //数据
        if (this.values != null){
            for (int i = 0,len = this.values.length;i<len;i++){
                bytes[5+i] = this.values[i];
            }
        }
        return bytes;
    }
    public byte[] getBytesCRC(){
        int length = 5 + (values==null?0:values.length) + 2;
        byte[] bytes = new byte[length];
        //逆变器地址
        bytes[0] = this.slaveAdd;
        //功能码
        bytes[1] = this.funCode;
        //子功能码
        bytes[2] = this.subFunCode;
        //数据长度
        bytes[3] = this.dataLen_H;
        bytes[4] = this.dataLen_L;

        //数据
        if (this.values != null){
            for (int i = 0,len = this.values.length;i<len;i++){
                bytes[5+i] = this.values[i];
            }
        }
        //crc
        bytes[length-2] = this.crc_H;
        bytes[length-1] = this.crc_L;
        return bytes;
    }




    public byte[] getDataNumBytes(){
        byte[] bytes = new byte[6];
        //逆变器地址
        bytes[0] = this.slaveAdd;
        //功能码
        bytes[1] = this.funCode;
        //子功能码
        bytes[2] = this.subFunCode;
        //数据长度
        bytes[3] = this.dataLen_H;
        bytes[4] = this.dataLen_L;

        //数据编号
        bytes[5] = this.dataNum_H;
        bytes[6] = this.dataNum_L;

        //数据
        if (this.values != null){
            for (int i = 0,len = this.values.length;i<len;i++){
                bytes[7+i] = this.values[i];
            }
        }
        return bytes;
    }
    public byte[] getDataNumBytesCRC(){
        int length = 5 + (values==null?0:values.length) + 2;
        byte[] bytes = new byte[length];
        //逆变器地址
        bytes[0] = this.slaveAdd;
        //功能码
        bytes[1] = this.funCode;
        //子功能码
        bytes[2] = this.subFunCode;
        //数据长度
        bytes[3] = this.dataLen_H;
        bytes[4] = this.dataLen_L;

        //数据编号
        bytes[5] = this.dataNum_H;
        bytes[6] = this.dataNum_L;

        //数据
        if (this.values != null){
            for (int i = 0,len = this.values.length;i<len;i++){
                bytes[7+i] = this.values[i];
            }
        }
        //crc
        bytes[length-2] = this.crc_H;
        bytes[length-1] = this.crc_L;
        return bytes;
    }


}
