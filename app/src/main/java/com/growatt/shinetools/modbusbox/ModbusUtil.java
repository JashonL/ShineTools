package com.growatt.shinetools.modbusbox;

import android.text.TextUtils;


import com.growatt.shinetools.modbusbox.bean.CommandRequest17;
import com.growatt.shinetools.modbusbox.bean.ModbusQueryBean;
import com.growatt.shinetools.modbusbox.bean.ModbusQueryOldInvBean;
import com.growatt.shinetools.utils.CommenUtils;
import com.growatt.shinetools.utils.Log;


import java.util.Arrays;


/**
 * Created by dg on 2017/10/24.
 */

public class ModbusUtil {
    public static final int USB_WIFI=0;
    public static final int AP_MODE=1;

    public static int localDebugMode = USB_WIFI;

    public static int getLocalDebugMode() {
        return localDebugMode;
    }

    public static void setLocalDebugMode(int localDebugMode) {
        ModbusUtil.localDebugMode = localDebugMode;
    }

    /**
     * 适用于功能码3、4、6
     * @param fun：功能码
     * @param start：开始寄存器
     * @param end：fun=3/4时为寄存器长度，fun=6时为寄存器值
     * @return
     */
    public static byte[] sendMsg(int fun, int start, int end) {
        //modbus协议封装
        byte[] modbytes = modbusPro(fun, start, end);
        //数服协议封装
        byte[] numBytes = new byte[0];
        try {
            numBytes = numberServerPro(modbytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return numBytes;
    }
    /**
     * 适用于功能码3、4、6
     * @param fun：功能码
     * @param start：开始寄存器
     * @param end：fun=3/4时为寄存器长度，fun=6时为寄存器值
     * @return
     */
    public static byte[] sendMsgOldInv(int fun, int start, int end) {
        //modbus协议封装
        byte[] modbytes = modbusProOldInv(fun, start, end);
        //数服协议封装
        byte[] numBytes = new byte[0];
        try {
            numBytes = numberServerPro(modbytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return numBytes;
    }

    /**
     * 适用于功能码10:批量设置
     * @param fun：功能码
     * @param start：开始寄存器
     * @param end：寄存器长度
     *           中间包含字节数
     * @param values:寄存器设置值
     * @return
     */
    public static byte[] sendMsg10(int fun, int start, int end,int[] values) {
        //modbus协议封装
        byte[] modbytes = modbusPro10(fun, start, end,values);
        //数服协议封装
        byte[] numBytes = new byte[0];
        try {
            numBytes = numberServerPro(modbytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return numBytes;
    }
    public static byte[] sendMsgByte10(int fun, int start, int end,byte[] values) {
        //modbus协议封装
        byte[] modbytes = modbusProByte10(fun, start, end,values);
        //数服协议封装
        byte[] numBytes = new byte[0];
        try {
            numBytes = numberServerProV2(modbytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return numBytes;
    }

    public static byte[] numberServerPro(byte[] modbytes) throws Exception {
        CommandRequest17 comm = new CommandRequest17();
        if (localDebugMode==USB_WIFI){
            comm.setDatas(modbytes);
            comm.setModbus_dataL((byte) modbytes.length);
            comm.setNo_dataL((byte) (modbytes.length + 14));
            //获得整体发送数据
            byte[] datas = comm.getBytes();
            return datas;
        }else {
            comm.setDatas(modbytes);
            byte[] pro =new byte[]{0x00,0x05};
            comm.setProId(pro);
            comm.setModbus_dataL((byte) modbytes.length);
            comm.setNo_dataL((byte) (modbytes.length + 14));
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
    public static byte[] numberServerProV2(byte[] modbytes) throws Exception {
        CommandRequest17 comm = new CommandRequest17();
        if (localDebugMode==USB_WIFI){
            comm.setDatas(modbytes);
            comm.setModbus_dataL((byte) modbytes.length);
            comm.setNo_dataL((byte) (modbytes.length + 14));
            //获得整体发送数据
            byte[] datas = comm.getBytesV2();
            return datas;
        }else {
            comm.setDatas(modbytes);
            byte[] pro =new byte[]{0x00,0x05};
            comm.setProId(pro);
            comm.setModbus_dataL((byte) modbytes.length);
            comm.setNo_dataL((byte) (modbytes.length + 14));
            //对数据加密
            byte[] encryptedData = DatalogApUtil.getEnCode(comm.getBytesV2());
            comm.setEncryptedData(encryptedData);
            //获取crc效验
            int crc = CRC16.calcCrc16(encryptedData);
            byte[] crcBytes = DatalogApUtil.int2Byte(crc);
            comm.setCrcData(crcBytes);
            return comm.getBytesCRC();
        }


    }

    public static byte[] modbusPro(int fun, int start, int end) {
        ModbusQueryBean mod = new ModbusQueryBean();
//        String function = mEtCommand.getText().toString().trim();
//        String startRegis = mEtRegisterAddress.getText().toString().trim();
//        String dataLen = mEtLengthData.getText().toString().trim();
        //功能码
        String function = isLenOne(fun);
        mod.setFunCode(MyByte.hexStringToByte(function));
        //起始寄存器
        String startRegis = isLenOne(start);
        byte[] startBytes = MyByte.hexStringToBytes(startRegis);
        if (startBytes != null && startBytes.length > 0) {
            if (startBytes.length > 1) {
                mod.setStartAdd_H(startBytes[0]);
                mod.setStartAdd_L(startBytes[1]);
            } else {
                mod.setStartAdd_L(startBytes[0]);
            }
        }
        //寄存器长度或数据
        int realData = end - start + 1;
        //当功能码为6时end 为设置数据
        if (fun == 6){
            realData = end;
        }
        String dataLen = isLenOne(realData);
        byte[] regLenBytes = MyByte.hexStringToBytes(dataLen);
        if (regLenBytes != null && regLenBytes.length > 0) {
            if (regLenBytes.length > 1) {
                mod.setDataLen_H(regLenBytes[0]);
                mod.setDataLen_L(regLenBytes[1]);
            } else {
                mod.setDataLen_L(regLenBytes[0]);
            }
        }
        //获取crc之外modbus数据
        byte[] datas = mod.getBytes();
        //获取crc效验
        int crc = CRC16.calcCrc16(datas);
        byte[] crcBytes = MyByte.hexStringToBytes(String.format("%04x", crc));
        //设置crc
        mod.setCrc_H(crcBytes[1]);
        mod.setCrc_L(crcBytes[0]);
        //返回整个modbus数据，包含crc校验
        return mod.getBytesCRC();
    }
    public static byte[] modbusProOldInv(int fun, int start, int end) {
        ModbusQueryOldInvBean mod = new ModbusQueryOldInvBean();
//        String function = mEtCommand.getText().toString().trim();
//        String startRegis = mEtRegisterAddress.getText().toString().trim();
//        String dataLen = mEtLengthData.getText().toString().trim();
        //功能码
        String function = isLenOne(fun);
        mod.setFunCode(MyByte.hexStringToByte(function));
        //起始寄存器
        String startRegis = isLenOne(start);
        byte[] startBytes = MyByte.hexStringToBytes(startRegis);
        if (startBytes != null && startBytes.length > 0) {
            if (startBytes.length > 1) {
                mod.setStartAdd_H(startBytes[0]);
                mod.setStartAdd_L(startBytes[1]);
            } else {
                mod.setStartAdd_L(startBytes[0]);
            }
        }
        //寄存器长度或数据
        int realData = end - start + 1;
        //当功能码为6时end 为设置数据
        if (fun == 6){
            realData = end;
        }
        String dataLen = isLenOne(realData);
        byte[] regLenBytes = MyByte.hexStringToBytes(dataLen);
        if (regLenBytes != null && regLenBytes.length > 0) {
            if (regLenBytes.length > 1) {
                mod.setDataLen_H(regLenBytes[0]);
                mod.setDataLen_L(regLenBytes[1]);
            } else {
                mod.setDataLen_L(regLenBytes[0]);
            }
        }
        //获取crc之外modbus数据
        byte[] datas = mod.getBytes();
        //获取crc效验
        int crc = CRC16.calcCrc16(datas);
        byte[] crcBytes = MyByte.hexStringToBytes(String.format("%04x", crc));
        //设置crc
        mod.setCrc_H(crcBytes[1]);
        mod.setCrc_L(crcBytes[0]);
        //返回整个modbus数据，包含crc校验
        return mod.getBytesCRC();
    }

    public static byte[] modbusPro10(int fun, int start, int end,int[] values) {
        ModbusQueryBean mod = new ModbusQueryBean();
        //功能码
        String function = isLenOne(fun);
        mod.setFunCode(MyByte.hexStringToByte(function));
        //起始寄存器
        String startRegis = isLenOne(start);
        byte[] startBytes = MyByte.hexStringToBytes(startRegis);
        if (startBytes != null && startBytes.length > 0) {
            if (startBytes.length > 1) {
                mod.setStartAdd_H(startBytes[0]);
                mod.setStartAdd_L(startBytes[1]);
            } else {
                mod.setStartAdd_L(startBytes[0]);
            }
        }
        //寄存器长度或数据
        int realData = end - start + 1;
        //设置寄存器长度
        String dataLen = isLenOne(realData);
        byte[] regLenBytes = MyByte.hexStringToBytes(dataLen);
        if (regLenBytes != null && regLenBytes.length > 0) {
            if (regLenBytes.length > 1) {
                mod.setDataLen_H(regLenBytes[0]);
                mod.setDataLen_L(regLenBytes[1]);
            } else {
                mod.setDataLen_L(regLenBytes[0]);
            }
        }
        //数据的字节长度
        int valueLen = values==null?0:values.length * 2;
        byte[] valueCount = MyByte.hexStringToBytes(isLenOne(valueLen));
        if (valueCount != null && valueCount.length>0){
            mod.setByteCount(valueCount[0]);
        }
        if (values != null){
            byte[] valueBytes = new byte[values.length * 2];
            for (int i=0,len = values.length;i<len;i++){
                String valueI = isLenOne(values[i]);
                byte[] byteI = MyByte.hexStringToBytes(valueI);
                if (byteI != null && byteI.length > 0) {
                    if (byteI.length > 1) {
                        valueBytes[i*2] = byteI[0];
                        valueBytes[i*2+1] = byteI[1];
                    } else {
                        valueBytes[i*2] = 0x00;
                        valueBytes[i*2+1] = byteI[0];
                    }
                }
            }
            mod.setValues(valueBytes);
        }

        //获取crc之外modbus数据
        byte[] datas = mod.getBytes10();
        //获取crc效验
        int crc = CRC16.calcCrc16(datas);
        byte[] crcBytes = MyByte.hexStringToBytes(String.format("%04x", crc));
        //设置crc
        mod.setCrc_H(crcBytes[1]);
        mod.setCrc_L(crcBytes[0]);
        //返回整个modbus数据，包含crc校验
        return mod.getBytesCRC10();
    }
    public static byte[] modbusProByte10(int fun, int start, int end,byte[] values) {
        ModbusQueryBean mod = new ModbusQueryBean();
        //功能码
        String function = isLenOne(fun);
        mod.setFunCode(MyByte.hexStringToByte(function));
        //起始寄存器
        String startRegis = isLenOne(start);
        byte[] startBytes = MyByte.hexStringToBytes(startRegis);
        if (startBytes != null && startBytes.length > 0) {
            if (startBytes.length > 1) {
                mod.setStartAdd_H(startBytes[0]);
                mod.setStartAdd_L(startBytes[1]);
            } else {
                mod.setStartAdd_L(startBytes[0]);
            }
        }
        //寄存器长度或数据
        int realData = end - start + 1;
        //设置寄存器长度
        String dataLen = isLenOne(realData);
        byte[] regLenBytes = MyByte.hexStringToBytes(dataLen);
        if (regLenBytes != null && regLenBytes.length > 0) {
            if (regLenBytes.length > 1) {
                mod.setDataLen_H(regLenBytes[0]);
                mod.setDataLen_L(regLenBytes[1]);
            } else {
                mod.setDataLen_L(regLenBytes[0]);
            }
        }
        //数据的字节长度
        int valueLen = values==null?0:values.length ;
        byte[] valueCount = MyByte.hexStringToBytes(isLenOne(valueLen));
        if (valueCount != null && valueCount.length>0){
            mod.setByteCount(valueCount[0]);
        }
        if (values != null){
//            byte[] valueBytes = new byte[values.length * 2];
//            for (int i=0,len = values.length;i<len;i++){
//                String valueI = isLenOne(values[i]);
//                byte[] byteI = MyByte.hexStringToBytes(valueI);
//                if (byteI != null && byteI.length > 0) {
//                    if (byteI.length > 1) {
//                        valueBytes[i*2] = byteI[0];
//                        valueBytes[i*2+1] = byteI[1];
//                    } else {
//                        valueBytes[i*2] = 0x00;
//                        valueBytes[i*2+1] = byteI[0];
//                    }
//                }
//            }
//            mod.setValues(valueBytes);
            mod.setValues(values);
        }

        //获取crc之外modbus数据
        byte[] datas = mod.getBytes10();
        //获取crc效验
        int crc = CRC16.calcCrc16(datas);
        byte[] crcBytes = MyByte.hexStringToBytes(String.format("%04x", crc));
        //设置crc
        mod.setCrc_H(crcBytes[1]);
        mod.setCrc_L(crcBytes[0]);
        //返回整个modbus数据，包含crc校验
        return mod.getBytesCRC10();
    }

    /**
     * 10进制转16
     */
    public static String isLenOne(int value) {
        String str = "";
        try {
            str = Integer.toHexString(value);
            if (!TextUtils.isEmpty(str)) {
                int len = str.length();
                if (len == 1) {
                    str = "0" + str;
                } else if (len == 2) {
                } else if (len == 3) {
                    str = "0" + str;
                } else if (len == 4) {
                } else {
//                    toast("输入数据内容超范围。。");
                    str = "ffff";
                }
            } else {
                str = "00";
            }
            return str;
        } catch (Exception e) {
            e.printStackTrace();
            str = "00";
//            toast("请输入整数");
            return str;
        }
    }

    /**
     * 检测返回信息是否正确
     *  针对返回数据modbus中有数据长度类型，功能码3/4、5适用
     * @param bytes
     * @return
     */
    public static boolean checkModbus1(byte[] bytes) {
        if (bytes == null) return false;
        int len = bytes.length;
        if (len > 6) {
            //数服协议返回长度
            int receiveLen = MaxWifiParseUtil.obtainRegistValueHOrL(new byte[]{bytes[4], bytes[5]}, 0);
            if (receiveLen != len - 6) {
                return false;
            }
            if (len > 20) {
                //modbuse返回长度
                int receiveLen2 = MaxWifiParseUtil.obtainRegistValueHOrL(new byte[]{bytes[18], bytes[19]}, 0);
                if (receiveLen2 != len - 20) {
                    return false;
                }
                if (len > 23) {
                    //modbuse数据返回长度
                    int receiveLen3 = MaxWifiParseUtil.obtainRegistValueHOrL(new byte[]{bytes[22]}, 0);
                    if (receiveLen3 == len - 25) {
                        return true;
                    }
                }
            }
        }
            return false;
    }
    /**
     * 检测返回信息是否正确
     *  针对返回数据modbus中有数据长度类型，功能码3/4、5适用
     * @param bytes
     * @return
     */
    public static boolean checkModbus(byte[] bytes) {
        try {
            if (bytes == null) return false;
            byte[] desCode;
            if (ModbusUtil.getLocalDebugMode()== AP_MODE){
                byte[]copyData=new byte[bytes.length];
                System.arraycopy(bytes, 0, copyData, 0, bytes.length);
                //1.数服协议校验
                boolean b = checkApData(copyData);
                Log.d("数服协议校验："+b);
                if (!b)return false;
                //2.解密
                desCode=  DatalogApUtil.getEnCode(copyData);
                Log.d("解密数据："+ CommenUtils.bytesToHexString(desCode));
                //3.modbus协议校验
                int len = desCode.length;
                Log.d("解密数据的长度："+len);
                if (len > 22) {
                    //3.1 modbuse返回长度
                    int receiveLen2 = MaxWifiParseUtil.obtainRegistValueHOrL(new byte[]{desCode[18], desCode[19]}, 0);
                    Log.d("modbus长度："+receiveLen2);
                    if (receiveLen2 != len - 22) {
                        Log.d("modbuse返回长度：校验失败");
                        return false;
                    }
//                    if (len > 23) {
//                        //modbuse数据返回长度
//                        int receiveLen3 = MaxWifiParseUtil.obtainRegistValueHOrL(new byte[]{bytes[22]}, 0);
//                        Log.d("receiveLen3"+receiveLen3);
//                        return receiveLen3 == len - 25;
//                    }
                    return true;
                }

            }else {
                int len = bytes.length;
                if (len > 6) {
                    //数服协议返回长度
                    int receiveLen = MaxWifiParseUtil.obtainRegistValueHOrL(new byte[]{bytes[4], bytes[5]}, 0);
                    if (receiveLen != len - 6) {
                        return false;
                    }
                    if (len > 20) {
                        //modbuse返回长度
                        int receiveLen2 = MaxWifiParseUtil.obtainRegistValueHOrL(new byte[]{bytes[18], bytes[19]}, 0);
                        if (receiveLen2 != len - 20) {
                            return false;
                        }
                        if (len > 23) {
                            //modbuse数据返回长度
                            int receiveLen3 = MaxWifiParseUtil.obtainRegistValueHOrL(new byte[]{bytes[22]}, 0);
                            if (receiveLen3 == len - 25) {
                                //校验crc
                                byte[] crcBytes = RegisterParseUtil.removePro(bytes);
                                return MaxUtil.checkCRC(crcBytes);
                            }
                        }
                    }
                }
            }

            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    /**
     * 检测数服协议返回信息是否正确
     *
     * @param bytes
     * @return
     */
    public static boolean checkApData(byte[] bytes) throws Exception {
        try {
            if (bytes == null) return false;
            int len = bytes.length;
            if (len > 6) {
                //返回数据长度
                int receiveLen = DatalogApUtil.byte2Int(new byte[]{bytes[4], bytes[5]});
                if (receiveLen != len - 8) {
                    return false;
                }
                //crc校验
                //获取crc效验
                byte crcL = bytes[bytes.length - 1];
                byte crcH = bytes[bytes.length - 2];

                //获取CRC之外的数据
                byte[] originalByte = Arrays.copyOfRange(bytes, 0, bytes.length - 2);
                int crc = CRC16.calcCrc16(originalByte);
                byte[] crcBytes = DatalogApUtil.int2Byte(crc);
                return crcBytes[0] == crcH && crcBytes[1] == crcL;

            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }





    /**
     * 检测返回信息是否正确
     *  针对返回数据modbus中没有数据长度类型，功能码6适用:比较两个数字是否一样
     * @return
     */
    public static boolean checkModbusNoLen(byte[] resBytes,byte[] desBytes) {
        return Arrays.equals(resBytes,desBytes);
    }

    /**
     *
     * @param isFirst 是否为初始值
     * @param address
     */
    public static void setComAddressOldInv(boolean isFirst,int address){
        if (isFirst){
            ModbusQueryOldInvBean.setSlaveAdd((byte) 0);
        }else {
            ModbusQueryOldInvBean.setSlaveAdd((byte) address);
        }
    }
    public static void setComAddressOldInv(){
        setComAddressOldInv(true, 0);
    }
    public static void setComAddressOldInv(int address){
        setComAddressOldInv(false, address);
    }
}
