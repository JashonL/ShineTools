package com.growatt.shinetools.modbusbox.bean;

import java.util.List;

public class DatalogResponBean {
    //功能码
    private byte funcode;
    //采集器序列号
    private String datalogSerial;
    //参数个数
    private int paramNum;
    //状态码
    private int statusCode;
    //参数数据的长度
    private int len;
    //数据
    private int value;

   //有效数据
    private List<ParamBean> paramBeanList;


    public String getDatalogSerial() {
        return datalogSerial;
    }

    public void setDatalogSerial(String datalogSerial) {
        this.datalogSerial = datalogSerial;
    }

    public int getParamNum() {
        return paramNum;
    }

    public void setParamNum(int paramNum) {
        this.paramNum = paramNum;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public List<ParamBean> getParamBeanList() {
        return paramBeanList;
    }

    public void setParamBeanList(List<ParamBean> paramBeanList) {
        this.paramBeanList = paramBeanList;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public static class ParamBean{
        //参数编号
        private int num;
        //参数长度
        private int length;
        //参数对应数据
        private String value;
        //整型数据
        private int valueInter;

        //文件数据分包总数量
        private int totalLength;
        //当前数据包编号
        private int dataNum;
        //当前数据包接收状态码
        private int dataCode;


        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }


        public int getTotalLength() {
            return totalLength;
        }

        public void setTotalLength(int totalLength) {
            this.totalLength = totalLength;
        }

        public int getDataNum() {
            return dataNum;
        }

        public void setDataNum(int dataNum) {
            this.dataNum = dataNum;
        }

        public int getDataCode() {
            return dataCode;
        }

        public void setDataCode(int dataCode) {
            this.dataCode = dataCode;
        }

        public int getValueInter() {
            return valueInter;
        }

        public void setValueInter(int valueInter) {
            this.valueInter = valueInter;
        }
    }


    public byte getFuncode() {
        return funcode;
    }

    public void setFuncode(byte funcode) {
        this.funcode = funcode;
    }
}
