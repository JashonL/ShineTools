package com.growatt.shinetools.modbusbox;

import java.io.UnsupportedEncodingException;

/**
 * Created by dg on 2017/10/23.
 */

public class MaxWifiParseUtil {
    /**
     * 用于电流超过0x8000负数判断
     */
    private static final int max = 0xffff;
    private static final int middle = 0x8000;

    /**
     * 寄存器高低位值移位运算
     *
     * @param resByte：字节
     * @param bite:左移位数
     * @return
     */
    public static int hexHighRegistToBit(int resByte, int bite) {
        return resByte << bite;
    }

    /**
     * 寄存器高低位值移位运算
     *
     * @param hexStr：十六进制字符串
     * @param bite:左移位数
     * @return
     */
    public static int hexHighRegistToBit(String hexStr, int bite) {
        byte resByte = Byte.parseByte(hexStr, 16);
        return resByte << bite;
    }

    /**
     * 获取多个寄存器的值
     *
     * @param startRegister：开始寄存器下标
     * @param endRegister：结束寄存器下标
     * @return
     */
    public static byte[] obtainRegistValues(int startRegister, int endRegister) {
        int lenth = endRegister - startRegister + 1;
        return null;
    }

    /**
     * 获取单个个寄存器的值
     *
     * @param register：寄存器位置
     * @return
     */
    public static byte[] obtainRegistValue(int register) {
        return obtainRegistValues(register, register);
    }

    /**
     * 获取寄存器值高低位
     *
     * @param registers
     * @param type:0:低位；1：高位
     * @return
     */
    public static int obtainRegistValueHOrL(byte[] registers, int type) {
        if (registers != null && registers.length >= 1) {
            int params1 = registers[0];
            if (params1 < 0) {
                params1 = params1 + 256;
            }
            //单字节
            if (registers.length == 1) {
                return params1;
            }
            //双字节
            int params2 = registers[1];
            if (params2 < 0) {
                params2 = params2 + 256;
            }
            if (type == 0) {
                return (params1 << 8) + params2;
            } else {
                return (params1 << 8 * 3) + (params2 << 8 * 2);
            }
        } else {
            return 0;
        }
    }

    /**
     * 获取寄存器值高低位
     *
     * @param registers
     * @param type:0:低位；1：高位
     * @return
     */
    public static int obtainRegistValueHOrL(int type, byte... registers) {
        return obtainRegistValueHOrL(registers, type);
    }

    /**
     * 获取高低位寄存器值
     *
     * @return
     */
    public static int obtainRegistValueHAndL(byte[] registerHs, byte[] registerLs) {
        return obtainRegistValueHOrL(registerHs, 1) + obtainRegistValueHOrL(registerLs, 0);
    }

    /**
     * 获取字符串ASCII:为空时用"/"表示
     *
     * @return
     */
    public static String obtainRegistValueAscii(byte... registers) {
        String result = "";
        try {
            result = new String(registers, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            result = new String(registers);
        } finally {
            if (result == null || result.trim().length() == 0) {
                result = "/";
            }
            return result;
        }
    }

    /**
     * 获取字符串ASCII:为空时用""表示
     *
     * @return
     */
    public static String obtainRegistValueAsciiYesNull(byte... registers) {
        String result = "";
        try {
            result = new String(registers, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            result = new String(registers);
        } finally {
            if (result == null || result.trim().length() == 0) {
                result = "";
            }
            return result;
        }
    }

    /**
     * 获取字符串ASCII:为空时用"/"表示
     *
     * @return
     */
    public static String obtainRegistValueAscii2(byte... registers) {
        String result = "";
        try {
            result = new String(registers, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            result = new String(registers);
        } finally {
            if (result == null || result.trim().length() == 0) {
                result = "/";
            }
            return result;
        }
    }

    /**
     * 最初版本没设计好
     * 截取指定起始位置数组:开始寄存器对100取余
     *
     * @param src:原数组
     * @param srcPos：原起始寄存器号:0开头,对100取余
     * @param destPos：目标位置
     * @param len：寄存器长度
     * @return
     */
    public static byte[] subBytes(byte[] src, int srcPos, int destPos, int len) {
        srcPos = srcPos % 100;
        byte[] newBytes = new byte[len * 2];
        System.arraycopy(src, srcPos * 2, newBytes, destPos, len * 2);
        return newBytes;
    }

    /**
     * 截取指定起始位置数组:开始寄存器不对100取余
     *
     * @param src:原数组
     * @param srcPos：原起始寄存器号:0开头~无穷
     * @param destPos：目标起始位置
     * @param len：寄存器长度
     * @return
     */
    public static byte[] subBytesFull(byte[] src, int srcPos, int destPos, int len) {
        byte[] newBytes = new byte[len * 2];
        System.arraycopy(src, srcPos * 2, newBytes, destPos, len * 2);
        return newBytes;
    }

    /**
     * 截取指定起始位置数组:开始寄存器对125取余
     *
     * @param src:原数组
     * @param srcPos：原起始寄存器号:0开头~无穷
     * @param destPos：目标起始位置
     * @param len：寄存器长度
     * @return
     */
    public static byte[] subBytes125(byte[] src, int srcPos, int destPos, int len) {
        srcPos = srcPos % 125;
        byte[] newBytes = new byte[len * 2];
        System.arraycopy(src, srcPos * 2, newBytes, destPos, len * 2);
        return newBytes;
    }




    /**
     * 截取指定起始位置数组:开始寄存器对125取余
     *
     * @param src:原数组
     * @param srcPos：原起始寄存器号:0开头~无穷
     * @param destPos：目标起始位置
     * @param len：寄存器长度
     * @return
     */
    public static byte[] subBytes45(byte[] src, int srcPos, int destPos, int len) {
        srcPos = srcPos % 45;
        byte[] newBytes = new byte[len * 2];
        System.arraycopy(src, srcPos * 2, newBytes, destPos, len * 2);
        return newBytes;
    }


    /**
     * 获取高低位寄存器值
     *
     * @param registers:2个寄存器，数组长度为4
     * @return
     */
    public static int obtainValueHAndL(byte[] registers) {
        if (registers != null && registers.length == 4) {
            return obtainRegistValueHOrL(1, registers[0], registers[1]) +
                    obtainRegistValueHOrL(0, registers[2], registers[3]);
        } else {
            return 0;
        }
    }

    /**
     * 获取高低位寄存器值
     *
     * @param registers:1个寄存器，数组长度为2
     * @return
     */
    public static int obtainValueOne(byte[] registers) {
        if (registers != null && registers.length == 2) {
            return obtainRegistValueHOrL(0, registers[0], registers[1]);
        } else {
            return 0;
        }
    }

    /**
     * 获取单寄存器值 125取余
     *
     * @param register 寄存器号
     * @return
     */
    public static int obtainValueOne(byte[] bs, int register) {
        return obtainValueOne(subBytes125(bs, register, 0, 1));
    }

    /**
     * 获取双寄存器值  125取余
     *
     * @param register 寄存器号
     * @return
     */
    public static int obtainValueTwo(byte[] bs, int register) {
        return obtainValueHAndL(subBytes125(bs, register, 0, 2));
    }

    /**
     * @param bs    源数据
     * @param register 下标
     * @return
     */
    public static int usBdcObtainValueOne(byte[] bs, int register) {
        return obtainValueOne(usbdcParallelRegister(bs, register, 0, 1));
    }


    /**
     * @param bs 源数据
     * @return
     */
    public static int usBdcObtainValueTwo(byte[] bs, int register) {
        return obtainValueHAndL(usbdcParallelRegister(bs, register, 0, 2));
    }


    /**
     * 03数据:
     * 1.bdc并机之后，bdc的信息是从5000开始的  共40个寄存器
     * 2.算法是3085(原寄存器地址)+1915+40*selectBDC(当前是第几个bdc)
     * 3.根据原寄存器地址找到并机之后的寄存器地址
     * 4.将旧的寄存器对应上新的寄存器
     * 5.根据起始寄存器获取当前处于寄存器的哪个位置截取数据
     * 6.解析
     */
    public static byte[] usbdcParallelRegister(byte[] src, int srcPos, int destPos, int len) {
        //截取数据
        byte[] newBytes = new byte[len * 2];
        System.arraycopy(src, srcPos * 2, newBytes, destPos, len * 2);
        return newBytes;
    }


    /**
     * 获取电流量，当有负数可能时
     *
     * @param registers:1个寄存器，数组长度为2
     * @return
     */
    public static int obtainValueCurrent(byte[] registers) {
        if (registers != null && registers.length == 2) {
            int value = obtainRegistValueHOrL(0, registers[0], registers[1]);
            if (value > middle) {
                value = -(max - value + 1);
            }
            return value;
        } else {
            return 0;
        }
    }
}
