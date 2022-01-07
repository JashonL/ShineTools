package com.growatt.shinetools.utils;


import com.growatt.shinetools.modbusbox.CRC16;
import com.growatt.shinetools.modbusbox.DatalogApUtil;
import com.growatt.shinetools.modbusbox.bean.CommandRequest19;

import static com.growatt.shinetools.utils.CommenUtils.int2Byte;

/**
 * 采集器0x18指令发送数据封装工具
 */
public class ModBusFunUtils19 {


    public static byte[] sendMsg03(int[] valus) {
        //modbus协议封装
        byte[] modbytes = parserModData03(valus);
        //数服协议封装
        byte[] numBytes = new byte[0];
        try {
            numBytes = numberServerPro03(modbytes, valus.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return numBytes;
    }


    public static byte[] parserModData03(int[] valus) {
        //设置数据
        byte[] dataByte = new byte[valus.length * 2];
        for (int i = 0; i < valus.length; i++) {
            int valus1 = valus[i];
            byte[] bytes1 = int2Byte(valus1);
            System.arraycopy(bytes1, 0, dataByte, bytes1.length * i, bytes1.length);
        }

        return dataByte;
    }


    public static byte[] numberServerPro03(byte[] modbytes, int num) throws Exception {
        CommandRequest19 comm = new CommandRequest19();

        //1.报文编号 默认00 01
        //2.报文标识
        comm.setMbap_pro_id(new byte[]{0x00, 0x03});


        //3.报文头数据长度=设备地址(1字节)+功能码(1字节)+数据采集器(10字节)+参数编号个数(2字节)+参数编号(每个编号2字节)
        int length = modbytes.length;
        int datalen = 1 + 1 + 10 + length;
        byte[] mBytesBapLen = int2Byte(datalen);
        comm.setMbap_len(mBytesBapLen);
//        Log.d("报文头长度：" + datalen + "16进制:" + CommenUtils.bytesToHexString(mBytesBapLen));
        //4.设备地址 默认01


        //5.功能码
        comm.setFun_code(DatalogApUtil.DATALOG_GETDATA_0X19);

        //6.数据区
        //6.1采集器序列号
        byte[] serialBytes = "0000000000".getBytes();


        //6.2 查询的编号=modbytes


        //7.数据拼接
        int alllen = serialBytes.length +  modbytes.length;
        byte[] allDataBytes = new byte[alllen];
        System.arraycopy(serialBytes, 0, allDataBytes, 0, serialBytes.length);
        System.arraycopy(modbytes, 0, allDataBytes, serialBytes.length , modbytes.length);
        comm.setData(allDataBytes);

        //如果是USBWIFI进入的话  直接返回数据
//        Log.i("发送原始命令：" + CommenUtils.bytesToHexString(comm.getBytes()));
        //获得整体发送数据
        byte[] datas = comm.getBytes();
        return datas;
    }





    public static byte[] sendMsg05(int[] valus) {
        //modbus协议封装
        byte[] modbytes = parserModData05(valus);
        //数服协议封装
        byte[] numBytes = new byte[0];
        try {
            numBytes = numberServerPro05(modbytes, valus.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return numBytes;
    }


    public static byte[] parserModData05(int[] valus) {
        //设置数据
        byte[] dataByte = new byte[valus.length * 2];
        for (int i = 0; i < valus.length; i++) {
            int valus1 = valus[i];
            byte[] bytes1 = int2Byte(valus1);
            System.arraycopy(bytes1, 0, dataByte, bytes1.length * i, bytes1.length);
        }

        return dataByte;
    }


    public static byte[] numberServerPro05(byte[] modbytes, int num) throws Exception {
        CommandRequest19 comm = new CommandRequest19();

        //1.报文编号 默认00 01
        //2.报文标识
        comm.setMbap_pro_id(new byte[]{0x00, 0x05});


        //3.报文头数据长度=设备地址(1字节)+功能码(1字节)+数据采集器(10字节)+参数编号个数(2字节)+参数编号(每个编号2字节)
        int length = modbytes.length;
        int datalen = 1 + 1 + 10 + 2+ length;
        byte[] mBytesBapLen = int2Byte(datalen);
        comm.setMbap_len(mBytesBapLen);
//        Log.d("报文头长度：" + datalen + "16进制:" + CommenUtils.bytesToHexString(mBytesBapLen));
        //4.设备地址 默认01


        //5.功能码
        comm.setFun_code(DatalogApUtil.DATALOG_GETDATA_0X19);

        //6.数据区
        //6.1采集器序列号
        byte[] serialBytes = "0000000000".getBytes();

        //6.2参数编号个数
        int paramNum = num;
        byte[] funNumByte = int2Byte(paramNum);

        //6.3 查询的编号=modbytes


        //7.数据拼接
        int alllen = serialBytes.length + funNumByte.length + modbytes.length;
        byte[] allDataBytes = new byte[alllen];
        System.arraycopy(serialBytes, 0, allDataBytes, 0, serialBytes.length);
        System.arraycopy(funNumByte, 0, allDataBytes, serialBytes.length, funNumByte.length);
        System.arraycopy(modbytes, 0, allDataBytes, serialBytes.length + funNumByte.length, modbytes.length);
        comm.setData(allDataBytes);
//        Log.i("发送原始命令：" + CommenUtils.bytesToHexString(comm.getBytes()));
        //如果是USBWIFI进入的话  直接返回数据
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
