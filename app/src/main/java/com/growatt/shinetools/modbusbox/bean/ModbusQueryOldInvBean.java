package com.growatt.shinetools.modbusbox.bean;

/**
 * Created by dg on 2017/9/26.
 * modbus查询实体：03,04命令
 */

public class ModbusQueryOldInvBean {
    private static byte slaveAdd = 0x00;
    //功能码:1字节
    private byte funCode;
    //开始寄存器地址:2字节
    private byte startAdd_H;
    private byte startAdd_L;
    //数据长度：2字节
    private byte dataLen_H ;
    private byte dataLen_L ;
    //crc校验
    private byte crc_H;
    private byte crc_L;

    //10指令增加
    /**
     *字节数
     */
    private byte byteCount;

    /**
     * 设置数据
     */
    private byte[] values;

    public byte getByteCount() {
        return byteCount;
    }

    public void setByteCount(byte byteCount) {
        this.byteCount = byteCount;
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

    public static byte getSlaveAdd() {
        return slaveAdd;
    }

    public static void setSlaveAdd(byte slaveAdd) {
        ModbusQueryOldInvBean.slaveAdd = slaveAdd;
    }

    public byte getFunCode() {
        return funCode;
    }

    public void setFunCode(byte funCode) {
        this.funCode = funCode;
    }

    public byte getStartAdd_H() {
        return startAdd_H;
    }

    public void setStartAdd_H(byte startAdd_H) {
        this.startAdd_H = startAdd_H;
    }

    public byte getStartAdd_L() {
        return startAdd_L;
    }

    public void setStartAdd_L(byte startAdd_L) {
        this.startAdd_L = startAdd_L;
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
    public byte[] getBytes(){
        byte[] bytes = new byte[6];
        bytes[0] = this.slaveAdd;
        bytes[1] = this.funCode;
        bytes[2] = this.startAdd_H;
        bytes[3] = this.startAdd_L;
        bytes[4] = this.dataLen_H;
        bytes[5] = this.dataLen_L;
        return bytes;
    }
    public byte[] getBytesCRC(){
        byte[] bytes = new byte[8];
        bytes[0] = this.slaveAdd;
        bytes[1] = this.funCode;
        bytes[2] = this.startAdd_H;
        bytes[3] = this.startAdd_L;
        bytes[4] = this.dataLen_H;
        bytes[5] = this.dataLen_L;
        bytes[6] = this.crc_H;
        bytes[7] = this.crc_L;
        return bytes;
    }
    public byte[] getBytes10(){
        byte[] bytes = new byte[7 + (values==null?0:values.length)];
        bytes[0] = this.slaveAdd;
        bytes[1] = this.funCode;
        bytes[2] = this.startAdd_H;
        bytes[3] = this.startAdd_L;
        bytes[4] = this.dataLen_H;
        bytes[5] = this.dataLen_L;
        bytes[6] = this.byteCount;
        if (this.values != null){
            for (int i = 0,len = this.values.length;i<len;i++){
                bytes[7+i] = this.values[i];
            }
        }
        return bytes;
    }
    public byte[] getBytesCRC10(){
        int length = 7 + (values==null?0:values.length) + 2;
        byte[] bytes = new byte[length];
        bytes[0] = this.slaveAdd;
        bytes[1] = this.funCode;
        bytes[2] = this.startAdd_H;
        bytes[3] = this.startAdd_L;
        bytes[4] = this.dataLen_H;
        bytes[5] = this.dataLen_L;
        bytes[6] = this.byteCount;
        if (this.values != null){
            for (int i = 0,len = this.values.length;i<len;i++){
                bytes[7+i] = this.values[i];
            }
        }
        bytes[length-2] = this.crc_H;
        bytes[length-1] = this.crc_L;
        return bytes;
    }
}
