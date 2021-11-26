package com.growatt.shinetools.module.inverterUpdata;

import com.growatt.shinetools.modbusbox.RegisterParseUtil;

import static com.growatt.shinetools.modbusbox.ModbusUtil.modbusPro;
import static com.growatt.shinetools.modbusbox.ModbusUtil.modbusPro17;
import static com.growatt.shinetools.modbusbox.ModbusUtil.modbusPro1705;
import static com.growatt.shinetools.modbusbox.ModbusUtil.modbusProgress;
import static com.growatt.shinetools.modbusbox.ModbusUtil.numberServerPro;
import static com.growatt.shinetools.modbusbox.ModbusUtil.numberServerProUpdata;

public class UpdataUtils {

    /**
     * 检查返回内容是否正确：06：查看返回结构是否是0106格式
     *
     * @param desc:完整返回内容，保留数服协议和modbus协议
     * @return
     */
    public static boolean checkReceiver0617(byte[] desc) {
        //移除数服协议
        byte[] bs1 = RegisterParseUtil.removePro(desc);
        return checkReceiver(bs1);
    }


    /**
     * 高级设置检查是06判断成功：否则失败
     *
     * @param desc：去除了数服协议，保留了modbus协议
     * @return
     */
    //查看返回结构是否是0106格式
    public static boolean checkReceiver(byte[] desc) {
        if (desc == null || desc.length < 2) {
            return false;
        }
        if (desc[1] == 6 || desc[1] == 0x17) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 升级不走modbus协议
     *
     * @return
     */
    public static byte[] sendMsg(int fun, int start, int end) {
        //modbus协议封装
        byte[] bytes = modbusPro(fun, start, end);
        //数服协议封装
        byte[] numBytes = new byte[0];
        try {
            numBytes = numberServerPro(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return numBytes;
    }


    /**
     * 适用于功能码17
     *
     * @param fun：功能码
     * @param subfun：子功能码
     * @param values：数据
     * @return
     */
    public static byte[] sendMsg17(int fun, int subfun, byte[] values) {
        //modbus协议封装
        byte[] bytes = modbusPro17(fun, subfun, values);
        //数服协议封装
        byte[] numBytes = new byte[0];
        try {
            numBytes = numberServerPro(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return numBytes;
    }


    /**
     * 发送升级进度查询
     *
     * @param fun：功能码
     * @param cmd：指令
     * @param data：数据
     * @return
     */
    public static byte[] sendMsgProgress(int fun, int cmd, int data) {
        //modbus协议封装
        byte[] bytes =  modbusProgress(fun, cmd, data);
        //数服协议封装
        byte[] numBytes = new byte[0];
        try {
            numBytes = numberServerPro(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return numBytes;
    }


    /**
     * 适用于功能码17
     *
     * @param fun：功能码
     * @param subfun：子功能码
     * @param values：数据
     * @return
     */
    public static byte[] sendMsg1705(int fun, int subfun, int num, byte[] values) {
        //modbus协议封装
        byte[] bytes =  modbusPro1705(fun, subfun, num, values);
        //数服协议封装
        byte[] numBytes = new byte[0];
        try {
            numBytes = numberServerProUpdata(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return numBytes;
    }
}
