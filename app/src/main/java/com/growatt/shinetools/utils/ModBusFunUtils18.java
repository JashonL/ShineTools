package com.growatt.shinetools.utils;


import com.growatt.shinetools.modbusbox.CRC16;
import com.growatt.shinetools.modbusbox.DatalogApUtil;
import com.growatt.shinetools.modbusbox.bean.CommandRequest18;
import com.growatt.shinetools.modbusbox.bean.DatalogAPSetParam;

import static com.growatt.shinetools.modbusbox.ModbusUtil.USB_WIFI;
import static com.growatt.shinetools.modbusbox.ModbusUtil.localDebugMode;
import static com.growatt.shinetools.utils.CommenUtils.int2Byte;

/**
 * 采集器0x18指令发送数据封装工具
 */
public class ModBusFunUtils18 {


    public static byte[] sendMsg(DatalogAPSetParam bean) {
        //modbus协议封装
        byte[] modbytes = parserModData(bean);
        //数服协议封装
        byte[] numBytes = new byte[0];
        try {
            numBytes = numberServerPro(modbytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return numBytes;
    }


    public static byte[] parserModData(DatalogAPSetParam bean) {

        //1.将数据集合的转成数组集合
        //2.记录所有数据的byte长度
        //3.将集合中的数据转成byte数组，然后保存
        String value = bean.getValue();
        int paramnum = bean.getParamnum();
        byte[] paramBytes = int2Byte(paramnum);
        byte[] valueBytes = value.getBytes();
        int length = valueBytes.length;
        byte[] lenBytes = int2Byte(length);

        byte[] bytes = new byte[paramBytes.length + valueBytes.length + lenBytes.length];
        System.arraycopy(paramBytes, 0, bytes, 0, paramBytes.length);
        System.arraycopy(lenBytes, 0, bytes, paramBytes.length, lenBytes.length);
        System.arraycopy(valueBytes, 0, bytes, paramBytes.length + lenBytes.length, valueBytes.length);

        return bytes;
    }


    public static byte[] numberServerPro(byte[] modbytes) throws Exception {
        CommandRequest18 comm = new CommandRequest18();

        //1.报文编号 默认00 01
        //2.报文标识
        if (localDebugMode == USB_WIFI) {
            comm.setMbap_pro_id(new byte[]{0x00, 0x03});
        } else {
            comm.setMbap_pro_id(new byte[]{0x00, 0x05});
        }

        //3.报文头数据长度=设备地址+功能码+数据采集器序列号+参数编号+数据长度+参数数据
        int length = modbytes.length;
        int datalen = 1 + 1 + 10 + 2 + 2 + length;
        byte[] mBytesBapLen = int2Byte(datalen);
        comm.setMbap_len(mBytesBapLen);
        Log.d("报文头长度：" + datalen + "16进制:" + CommenUtils.bytesToHexString(mBytesBapLen));
        //4.设备地址 默认01


        //5.功能码
        comm.setFun_code(DatalogApUtil.DATALOG_GETDATA_0X18);

        //6.数据区=采集器序列号+参数编号+参数长度+参数数据
        //6.1 采集器序列号
        byte[] serialBytes = "0000000000".getBytes();
        //6.2 参数编号+参数长度+参数数据=serialBytes

        //7.数据拼接
        int alllen = serialBytes.length + length;
        byte[] allDataBytes = new byte[alllen];
        System.arraycopy(serialBytes, 0, allDataBytes, 0, serialBytes.length);
        System.arraycopy(modbytes, 0, allDataBytes, serialBytes.length , modbytes.length);
        comm.setData(allDataBytes);

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
