package com.growatt.shinetools.utils;


import com.growatt.shinetools.modbusbox.CRC16;
import com.growatt.shinetools.modbusbox.DatalogApUtil;
import com.growatt.shinetools.modbusbox.bean.CommandRequest19;

import static com.growatt.shinetools.modbusbox.ModbusUtil.USB_WIFI;
import static com.growatt.shinetools.modbusbox.ModbusUtil.localDebugMode;
import static com.growatt.shinetools.utils.CommenUtils.int2Byte;

/**
 * 采集器0x18指令发送数据封装工具
 */
public class ModBusFunUtils26 {


    public static byte[] sendMsg( int totalLength, int currNum, byte[] valus) {
        //modbus协议封装
        byte[] modbytes = parserModData(totalLength,currNum,valus);
        //数服协议封装
        byte[] numBytes = new byte[0];
        try {
            numBytes = numberServerPro(modbytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return numBytes;
    }


    public static byte[] parserModData(int totalLength, int currNum, byte[] valus) {
        //数据串的长度=文件数据分包总数量+当前数据包编号
        int length = 2+2+valus.length;

        //采集器序列号
        byte[] serialBytes = "0000000000".getBytes();
        //数据串长度
        byte[] lengthByte = int2Byte(length);
        //分包总数量
        byte[] totalBytes = int2Byte(totalLength);
        //当前编号
        byte[] currNumBytes = int2Byte(currNum);

        int alllen = serialBytes.length+ lengthByte.length + totalBytes.length+currNumBytes.length+valus.length;
        byte[] allDataBytes = new byte[alllen];
        System.arraycopy(serialBytes, 0, allDataBytes, 0, serialBytes.length);
        System.arraycopy(lengthByte, 0, allDataBytes, serialBytes.length, lengthByte.length);
        System.arraycopy(totalBytes, 0, allDataBytes, serialBytes.length + lengthByte.length, totalBytes.length);
        System.arraycopy(currNumBytes, 0, allDataBytes, serialBytes.length + lengthByte.length+totalBytes.length, currNumBytes.length);
        System.arraycopy(valus, 0, allDataBytes, serialBytes.length + lengthByte.length+totalBytes.length+currNumBytes.length, valus.length);

        return allDataBytes;
    }


    public static byte[] numberServerPro(byte[] modbytes) throws Exception {
        CommandRequest19 comm = new CommandRequest19();

        //1.报文编号 默认00 01
        //2.报文标识
        if (localDebugMode == USB_WIFI) {
            comm.setMbap_pro_id(new byte[]{0x00, 0x03});
        } else {
            comm.setMbap_pro_id(new byte[]{0x00, 0x05});
        }

        //3.报文头数据长度=设备地址(1字节)+功能码(1字节)+数据采集器(10字节)+参数编号个数(2字节)+参数编号(每个编号2字节)
        int length = modbytes.length;
        int datalen = 1 + 1 + 10 + 2+ length;
        byte[] mBytesBapLen = int2Byte(datalen);
        comm.setMbap_len(mBytesBapLen);
        Log.d("报文头长度：" + datalen + "16进制:" + CommenUtils.bytesToHexString(mBytesBapLen));
        //4.设备地址 默认01


        //5.功能码
        comm.setFun_code(DatalogApUtil.DATALOG_GETDATA_0X26);

        //6.数据区
        comm.setData(modbytes);

        //如果是USBWIFI进入的话  直接返回数据
        if (localDebugMode == USB_WIFI) {
            Log.i("发送原始命令：" + CommenUtils.bytesToHexString(comm.getBytes()));
            //获得整体发送数据
            byte[] datas = comm.getBytes();
            return datas;
        } else {//如果是WIFI-X方式  要加密还要CRC
            //对数据加密
            byte[] encryptedData = DatalogApUtil.getEnCode(comm.getBytes());
            comm.setEncryptedData(encryptedData);
            //获取crc效验
            int crc = CRC16.calcCrc16(encryptedData);
            byte[] crcBytes = DatalogApUtil.int2Byte(crc);
            comm.setCrcData(crcBytes);
            return comm.getBytesCRC();
        }
    }


}
