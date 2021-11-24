package com.growatt.shinetools.module.inverterUpdata;

public class ModbusCheckProgreesBean {

    private byte slaveAdd = 0x01;
    //功能码:1字节
    private byte funCode;
    //开始寄存器地址:2字节
    private byte cmd_H;
    private byte cmd_L;
    //数据长度：2字节
    private byte dataLen_H ;
    private byte dataLen_L ;
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

    public byte getCmd_H() {
        return cmd_H;
    }

    public void setCmd_H(byte cmd_H) {
        this.cmd_H = cmd_H;
    }

    public byte getCmd_L() {
        return cmd_L;
    }

    public void setCmd_L(byte cmd_L) {
        this.cmd_L = cmd_L;
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
        bytes[0] = this.slaveAdd;
        bytes[1] = this.funCode;
        bytes[2] = this.cmd_H;
        bytes[3] = this.cmd_L;
        bytes[4] = this.dataLen_H;
        bytes[5] = this.dataLen_L;
        return bytes;
    }
    public byte[] getBytesCRC(){
        byte[] bytes = new byte[8];
        bytes[0] = this.slaveAdd;
        bytes[1] = this.funCode;
        bytes[2] = this.cmd_H;
        bytes[3] = this.cmd_L;
        bytes[4] = this.dataLen_H;
        bytes[5] = this.dataLen_L;
        bytes[6] = this.crc_H;
        bytes[7] = this.crc_L;
        return bytes;
    }

}
