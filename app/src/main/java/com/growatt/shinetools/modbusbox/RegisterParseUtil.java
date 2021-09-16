package com.growatt.shinetools.modbusbox;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.growatt.shinetools.modbusbox.bean.MaxCheckErrorTotalBean;
import com.growatt.shinetools.modbusbox.bean.MaxCheckOneKeyAcBean;
import com.growatt.shinetools.modbusbox.bean.MaxCheckOneKeyISOBean;
import com.growatt.shinetools.modbusbox.bean.MaxCheckOneKeyRSTBean;
import com.growatt.shinetools.modbusbox.bean.MaxCheckOneKeyTHDVBean;
import com.growatt.shinetools.modbusbox.bean.MaxDataBean;
import com.growatt.shinetools.modbusbox.bean.MaxDataDeviceBean;
import com.growatt.shinetools.modbusbox.bean.MaxErrorBean;
import com.growatt.shinetools.modbusbox.bean.ToolStorageDataBean;
import com.growatt.shinetools.module.localbox.ustool.bean.BDCInfoBean;
import com.growatt.shinetools.utils.Log;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.growatt.shinetools.modbusbox.Arith.round;
import static com.growatt.shinetools.modbusbox.MaxWifiParseUtil.obtainValueHAndL;
import static com.growatt.shinetools.modbusbox.MaxWifiParseUtil.obtainValueOne;
import static com.growatt.shinetools.modbusbox.MaxWifiParseUtil.obtainValueTwo;
import static com.growatt.shinetools.modbusbox.MaxWifiParseUtil.usBdcObtainValueOne;
import static com.growatt.shinetools.modbusbox.MaxWifiParseUtil.usBdcObtainValueTwo;
import static com.growatt.shinetools.modbusbox.ModbusUtil.AP_MODE;


/**
 * Created by dg on 2017/10/25.
 */

/**
 *
 */
public class RegisterParseUtil {
    private static final double muilt = 0.1;
    // 解密采集器数据公共密钥 "Growatt"
    public static final String secretKey = "Growatt";

    /**
     * 解析03:0-99号寄存器 修改为0-124
     */
    public static void parseMax1(MaxDataBean been, byte[] bytes) {
        //移除外部协议
        byte[] bs = removePro17(bytes);
        Log.i("receive1", SocketClientUtil.bytesToHexString(bs));
        //额定功率
//        int power = MaxWifiParseUtil.obtainRegistValueHOrL(1,bs[12],bs[13])
//        + MaxWifiParseUtil.obtainRegistValueHOrL(0,bs[14],bs[15]);
        int power = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs, 6, 0, 2));
        //sn号
        String sn = MaxWifiParseUtil.obtainRegistValueAscii(
                MaxWifiParseUtil.subBytes(bs, 23, 0, 5)
        );
        //厂商信息
        String company = MaxWifiParseUtil.obtainRegistValueAscii(
                MaxWifiParseUtil.subBytes(bs, 34, 0, 8)
        );
        //固件外部版本
        String firOut = MaxWifiParseUtil.obtainRegistValueAscii(
                MaxWifiParseUtil.subBytes(bs, 9, 0, 6)
        );
        //固件内部版本
        String firIn = MaxWifiParseUtil.obtainRegistValueAscii(
                MaxWifiParseUtil.subBytes(bs, 82, 0, 6)
        );
        //model号
        int model = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs, 28, 0, 2));
        //设置值
        been.setTotalPower(power);
        MaxDataDeviceBean deviceBean = been.getDeviceBeen();
        deviceBean.setNormalPower(power);
        deviceBean.setSn(sn);
        deviceBean.setCompany(company);
        deviceBean.setFirmVersionOut(firOut);
        deviceBean.setFirmVersionIn(firIn);
        deviceBean.setModel(model);
    }

    /**
     * 解析03:0-124号寄存器
     */
    public static void parseHold0T124(MaxDataBean been, byte[] bytes) {
        //移除外部协议
        byte[] bs = removePro17(bytes);
        Log.i("receive1", SocketClientUtil.bytesToHexString(bs));
        //额定功率
//        int power = MaxWifiParseUtil.obtainRegistValueHOrL(1,bs[12],bs[13])
//        + MaxWifiParseUtil.obtainRegistValueHOrL(0,bs[14],bs[15]);
        int power = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs, 6, 0, 2));
        //sn号
        String sn = MaxWifiParseUtil.obtainRegistValueAscii(
                MaxWifiParseUtil.subBytes(bs, 23, 0, 5)
        );
        //厂商信息
        String company = MaxWifiParseUtil.obtainRegistValueAscii(
                MaxWifiParseUtil.subBytes(bs, 34, 0, 8)
        );
        //固件外部版本
        String firOut = MaxWifiParseUtil.obtainRegistValueAscii(
                MaxWifiParseUtil.subBytes(bs, 9, 0, 3)
        );
        //固件内部版本
        String firIn = MaxWifiParseUtil.obtainRegistValueAscii(
                MaxWifiParseUtil.subBytes(bs, 82, 0, 6)
        );
        ;
        if (firIn == null) firIn = "";
        //通信软件版本
        byte[] commSoftBs = MaxWifiParseUtil.subBytes(bs, 12, 0, 2);
        BigInteger bigIntegerComm = new BigInteger(1, commSoftBs);
        String commSoftVersion = "";
        if (bigIntegerComm.intValue() != 0) {
            commSoftVersion = MaxWifiParseUtil.obtainRegistValueAscii(
                    MaxWifiParseUtil.subBytes(bs, 12, 0, 2)
            );
        }
        //通讯版本值
        int commSoftVersionValue = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 14, 0, 1));
        //model号 先计算118-121寄存器 == 0 再使用28,29
        byte[] modelNewBytes = MaxWifiParseUtil.subBytesFull(bs, 118, 0, 4);
        BigInteger bigInteger = new BigInteger(1, modelNewBytes);
        int model = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs, 28, 0, 2));
        //设置值
        been.setTotalPower(power);
        MaxDataDeviceBean deviceBean = been.getDeviceBeen();
        deviceBean.setNormalPower(power);
        deviceBean.setSn(sn);
        deviceBean.setCompany(company);
        deviceBean.setFirmVersionOut(firOut);
        deviceBean.setFirmVersionIn(firIn);
        deviceBean.setCommSoftVersion(commSoftVersion);
        deviceBean.setCommSoftVersionValue(commSoftVersionValue);
        deviceBean.setModel(model);
        deviceBean.setNewModel(bigInteger.longValue());
    }

    /**
     * 解析03:0-124号寄存器
     */
    public static void parseHold0T124Mix(MaxDataBean been, byte[] bytes) {
        //移除外部协议
        byte[] bs = removePro17(bytes);
        Log.i("receive1", SocketClientUtil.bytesToHexString(bs));
        //sn号
        String sn = MaxWifiParseUtil.obtainRegistValueAscii(
                MaxWifiParseUtil.subBytes(bs, 23, 0, 5)
        );
        //厂商信息
        String company = MaxWifiParseUtil.obtainRegistValueAscii(
                MaxWifiParseUtil.subBytes(bs, 34, 0, 8)
        );
        //固件外部版本
        String firOut = MaxWifiParseUtil.obtainRegistValueAscii(
                MaxWifiParseUtil.subBytes(bs, 9, 0, 3)
        );
        //固件内部版本
        String firIn = MaxWifiParseUtil.obtainRegistValueAscii(
                MaxWifiParseUtil.subBytes(bs, 82, 0, 6)
        );
        ;
        if (firIn == null) firIn = "";
        //通信软件版本
        byte[] commSoftBs = MaxWifiParseUtil.subBytes(bs, 12, 0, 2);
        BigInteger bigIntegerComm = new BigInteger(1, commSoftBs);
        String commSoftVersion = "";
        if (bigIntegerComm.intValue() != 0) {
            commSoftVersion = MaxWifiParseUtil.obtainRegistValueAscii(
                    MaxWifiParseUtil.subBytes(bs, 12, 0, 2)
            );
        }
        //通讯版本值
        int commSoftVersionValue = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 14, 0, 1));
        //model号 先计算118-121寄存器 == 0 再使用28,29
        byte[] modelNewBytes = MaxWifiParseUtil.subBytesFull(bs, 118, 0, 4);
        BigInteger bigInteger = new BigInteger(1, modelNewBytes);
        int model = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs, 28, 0, 2));
        //设置值
        MaxDataDeviceBean deviceBean = been.getDeviceBeen();
        deviceBean.setSn(sn);
        deviceBean.setCompany(company);
        deviceBean.setFirmVersionOut(firOut);
        deviceBean.setFirmVersionIn(firIn);
        deviceBean.setCommSoftVersion(commSoftVersion);
        deviceBean.setCommSoftVersionValue(commSoftVersionValue);
        deviceBean.setModel(model);
        deviceBean.setNewModel(bigInteger.longValue());
    }

    /**
     * 解析03:0-124号寄存器
     */
    public static void parseHold0T124OldInv(MaxDataBean been, byte[] bytes) {
        //移除外部协议
        byte[] bs = removePro17(bytes);
        Log.i("receive1", SocketClientUtil.bytesToHexString(bs));
        //额定功率
//        int power = MaxWifiParseUtil.obtainRegistValueHOrL(1,bs[12],bs[13])
//        + MaxWifiParseUtil.obtainRegistValueHOrL(0,bs[14],bs[15]);
//       int  power = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs,6,0,2));
        //sn号
        String sn = MaxWifiParseUtil.obtainRegistValueAscii(
                MaxWifiParseUtil.subBytes(bs, 23, 0, 5)
        );
        //功率百分比
        int powerPer = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3, 0, 1));
        //厂商信息
        String company = MaxWifiParseUtil.obtainRegistValueAscii(
                MaxWifiParseUtil.subBytes(bs, 59, 0, 8)
        );
        //固件外部版本
        String firOut = MaxWifiParseUtil.obtainRegistValueAscii(
                MaxWifiParseUtil.subBytes(bs, 9, 0, 6)
        );
        //固件内部版本
        String firIn = MaxWifiParseUtil.obtainRegistValueAscii(
                MaxWifiParseUtil.subBytes(bs, 67, 0, 4)
        );
        ;
        if (firIn == null) firIn = "";
        //通信软件版本
        byte[] commSoftBs = MaxWifiParseUtil.subBytes(bs, 12, 0, 2);
        BigInteger bigIntegerComm = new BigInteger(1, commSoftBs);
        String commSoftVersion = "";
        if (bigIntegerComm.intValue() != 0) {
            commSoftVersion = MaxWifiParseUtil.obtainRegistValueAscii(
                    MaxWifiParseUtil.subBytes(bs, 12, 0, 2)
            );
        }
        //通讯版本值
//        int  commSoftVersionValue = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,14,0,1));
        //model号 先计算118-121寄存器 == 0 再使用28,29
//        byte[] modelNewBytes = MaxWifiParseUtil.subBytesFull(bs, 118, 0, 4);
//        BigInteger bigInteger = new BigInteger(1, modelNewBytes);
        int model = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs, 28, 0, 2));
        //设置值
//        been.setTotalPower(power);
        MaxDataDeviceBean deviceBean = been.getDeviceBeen();
//        deviceBean.setNormalPower(power);
        deviceBean.setSn(sn);
        deviceBean.setCompany(company);
        deviceBean.setFirmVersionOut(firOut);
        deviceBean.setFirmVersionIn(firIn);
        deviceBean.setCommSoftVersion(commSoftVersion);
        deviceBean.setRealOPowerPercent(powerPer);
//        deviceBean.setCommSoftVersionValue(commSoftVersionValue);
        deviceBean.setModel(model);
//        deviceBean.setNewModel(bigInteger.longValue());
    }

    public static void parse03Hold45T89OldInv(MaxDataBean been, byte[] bytes) {
        //移除外部协议
        byte[] bs = removePro17(bytes);
        Log.i("receive1", SocketClientUtil.bytesToHexString(bs));
        //额定功率
//        int power = MaxWifiParseUtil.obtainRegistValueHOrL(1,bs[12],bs[13])
//        + MaxWifiParseUtil.obtainRegistValueHOrL(0,bs[14],bs[15]);
//       int  power = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs,6,0,2));
        //sn号
//        String sn = MaxWifiParseUtil.obtainRegistValueAscii(
//               MaxWifiParseUtil.subBytes(bs,23,0,5)
//        );
//        //功率百分比
//        int  powerPer = obtainValueOne(MaxWifiParseUtil.subBytes125(bs,3,0,1));
        //厂商信息
        String company = MaxWifiParseUtil.obtainRegistValueAscii(
                MaxWifiParseUtil.subBytes45(bs, 59, 0, 8)
        );
//        //固件外部版本
//        String firOut = MaxWifiParseUtil.obtainRegistValueAscii(
//               MaxWifiParseUtil.subBytes(bs,9,0,6)
//        );
        //固件内部版本
        String firIn = MaxWifiParseUtil.obtainRegistValueAscii(
                MaxWifiParseUtil.subBytes45(bs, 67, 0, 4)
        );
        ;
        if (firIn == null) firIn = "";
        //通信软件版本
//        byte[] commSoftBs = MaxWifiParseUtil.subBytes(bs,12,0,2);
//        BigInteger bigIntegerComm = new BigInteger(1, commSoftBs);
//        String commSoftVersion = "";
//        if (bigIntegerComm.intValue() != 0){
//            commSoftVersion = MaxWifiParseUtil.obtainRegistValueAscii(
//                    MaxWifiParseUtil.subBytes(bs,12,0,2)
//            );
//        }
        //通讯版本值
//        int  commSoftVersionValue = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,14,0,1));
        //model号 先计算118-121寄存器 == 0 再使用28,29
//        byte[] modelNewBytes = MaxWifiParseUtil.subBytesFull(bs, 118, 0, 4);
//        BigInteger bigInteger = new BigInteger(1, modelNewBytes);
//        int  model = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs,28,0,2));
        //设置值
//        been.setTotalPower(power);
        MaxDataDeviceBean deviceBean = been.getDeviceBeen();
//        deviceBean.setNormalPower(power);
//        deviceBean.setSn(sn);
        deviceBean.setCompany(company);
//        deviceBean.setFirmVersionOut(firOut);
        deviceBean.setFirmVersionIn(firIn);
//        deviceBean.setCommSoftVersion(commSoftVersion);
//        deviceBean.setRealOPowerPercent(powerPer);
//        deviceBean.setCommSoftVersionValue(commSoftVersionValue);
//        deviceBean.setModel(model);
//        deviceBean.setNewModel(bigInteger.longValue());
    }

    public static void parse03Hold0T44OldInv(MaxDataBean been, byte[] bytes) {
        //移除外部协议
        byte[] bs = removePro17(bytes);
        Log.i("receive1", SocketClientUtil.bytesToHexString(bs));
        //解析DTC
        int dtc = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 43, 0, 1));
        been.setDeviceTypeCode(dtc);
        //额定功率
//        int power = MaxWifiParseUtil.obtainRegistValueHOrL(1,bs[12],bs[13])
//        + MaxWifiParseUtil.obtainRegistValueHOrL(0,bs[14],bs[15]);
        int power = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs, 6, 0, 2));
        //sn号
        String sn = MaxWifiParseUtil.obtainRegistValueAscii(
                MaxWifiParseUtil.subBytes(bs, 23, 0, 5)
        );
        //功率百分比
        int powerPer = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3, 0, 1));
        //厂商信息
//        String company = MaxWifiParseUtil.obtainRegistValueAscii(
//               MaxWifiParseUtil.subBytes(bs,59,0,8)
//        );
        //固件外部版本
        String firOut = MaxWifiParseUtil.obtainRegistValueAscii(
                MaxWifiParseUtil.subBytes(bs, 9, 0, 6)
        );
//        //固件内部版本
//        String firIn = MaxWifiParseUtil.obtainRegistValueAscii(
//                MaxWifiParseUtil.subBytes(bs,67,0,4)
//        );;
//        if (firIn == null) firIn = "";
        //通信软件版本
        byte[] commSoftBs = MaxWifiParseUtil.subBytes(bs, 12, 0, 2);
        BigInteger bigIntegerComm = new BigInteger(1, commSoftBs);
        String commSoftVersion = "";
        if (bigIntegerComm.intValue() != 0) {
            commSoftVersion = MaxWifiParseUtil.obtainRegistValueAscii(
                    MaxWifiParseUtil.subBytes(bs, 12, 0, 2)
            );
        }
        //通讯版本值
//        int  commSoftVersionValue = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,14,0,1));
        //model号 先计算118-121寄存器 == 0 再使用28,29
//        byte[] modelNewBytes = MaxWifiParseUtil.subBytesFull(bs, 118, 0, 4);
//        BigInteger bigInteger = new BigInteger(1, modelNewBytes);
        int model = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs, 28, 0, 2));
        //设置值
        been.setTotalPower(power);
        MaxDataDeviceBean deviceBean = been.getDeviceBeen();
//        deviceBean.setNormalPower(power);
        deviceBean.setSn(sn);
//        deviceBean.setCompany(company);
        deviceBean.setFirmVersionOut(firOut);
//        deviceBean.setFirmVersionIn(firIn);
        deviceBean.setCommSoftVersion(commSoftVersion);
        deviceBean.setRealOPowerPercent(powerPer);
//        deviceBean.setCommSoftVersionValue(commSoftVersionValue);
        deviceBean.setModel(model);
//        deviceBean.setNewModel(bigInteger.longValue());
    }

    /**
     * 解析03:125-249号寄存器
     */
    public static void parseHold125T249(MaxDataBean been, byte[] bytes) {
        //移除外部协议
        byte[] bs = removePro17(bytes);
        Log.i("receive1", SocketClientUtil.bytesToHexString(bs));
        MaxDataDeviceBean deviceBean = been.getDeviceBeen();
        //sn号
        byte[] snBytes = MaxWifiParseUtil.subBytes125(bs, 209, 0, 15);
        String sn30 = parseSn30(snBytes);
        if (!TextUtils.isEmpty(sn30)) {
            deviceBean.setSn(sn30);
        }
//        BigInteger bigInteger = new BigInteger(1, snBytes);
//        boolean hasSn = true;
//        try {
//            if (bigInteger.longValue() == 0){
//                hasSn = false;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            hasSn = true;
//        }
//        if (hasSn) {
//            String sn = MaxWifiParseUtil.obtainRegistValueAscii(snBytes);
//            deviceBean.setSn(sn);
//        }
        //机器型号
        String deviceType = MaxWifiParseUtil.obtainRegistValueAscii(
                MaxWifiParseUtil.subBytes125(bs, 125, 0, 8)
        );
        deviceBean.setDeviceType(deviceType);

        //解析int值
        int bdc = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 184, 0, 1));
        been.setBdcNumber(bdc);

    }

    /**
     * 解析03:125-249号寄存器
     */
    public static void parseHold125T249Mix(MaxDataBean been, byte[] bytes) {
        //移除外部协议
        byte[] bs = removePro17(bytes);
        Log.i("receive1", SocketClientUtil.bytesToHexString(bs));
        MaxDataDeviceBean deviceBean = been.getDeviceBeen();
        //sn号
        byte[] snBytes = MaxWifiParseUtil.subBytes125(bs, 209, 0, 15);
        String sn30 = parseSn30(snBytes);
        if (!TextUtils.isEmpty(sn30)) {
            deviceBean.setSn(sn30);
        }
        //机器型号
        String deviceType = MaxWifiParseUtil.obtainRegistValueAscii(
                MaxWifiParseUtil.subBytes125(bs, 125, 0, 8)
        );
        deviceBean.setDeviceType(deviceType);
    }

    /**
     * 解析30位序列号 16位等
     *
     * @param snBytes 30
     * @return
     */
    public static String parseSn30(byte[] snBytes) {
//        byte[] snBytes = MaxWifiParseUtil.subBytes125(bs, 209, 0, 15);
        String lastSn = "";
        try {
            String sn = MaxWifiParseUtil.obtainRegistValueAscii(snBytes);
            if (!TextUtils.isEmpty(sn) && sn.trim().length() == 30) {
                lastSn = sn;
            } else {
                sn = MaxWifiParseUtil.obtainRegistValueAscii(MaxWifiParseUtil.subBytes(snBytes, 0, 0, 8));
                String sn2 = MaxWifiParseUtil.obtainRegistValueAsciiYesNull(MaxWifiParseUtil.subBytes(snBytes, 8, 0, 7));
                if (!TextUtils.isEmpty(sn) && sn.trim().length() == 16 &&
                        (sn2.trim().length() == 0)) {
                    lastSn = sn;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lastSn;
    }

    /**
     * 解析03:125-249号寄存器
     */
    public static void parseHold180T224OldInv(MaxDataBean been, byte[] bytes) {
        //移除外部协议
        byte[] bs = removePro17(bytes);
        Log.i("receive1", SocketClientUtil.bytesToHexString(bs));
        MaxDataDeviceBean deviceBean = been.getDeviceBeen();
        //sn号
        byte[] snBytes = MaxWifiParseUtil.subBytes45(bs, 209, 0, 15);
        String sn30 = parseSn30(snBytes);
        if (!TextUtils.isEmpty(sn30)) {
            deviceBean.setSn(sn30);
        }
//        BigInteger bigInteger = new BigInteger(1, snBytes);
//        boolean hasSn = true;
//        try {
//            if (bigInteger.longValue() == 0){
//                hasSn = false;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            hasSn = true;
//        }
//        if (hasSn) {
//            String sn = MaxWifiParseUtil.obtainRegistValueAscii(snBytes);
//            deviceBean.setSn(sn);
//        }
//        //机器型号
//        String deviceType = MaxWifiParseUtil.obtainRegistValueAscii(
//                MaxWifiParseUtil.subBytes125(bs,125,0,8)
//        );
//        deviceBean.setDeviceType(deviceType);
    }

    /**
     * 解析03:125-249号寄存器
     */
    public static void parseHold3kT3124(MaxDataBean been, byte[] bytes) {
        //移除外部协议
        byte[] bs = removePro17(bytes);
        Log.i("receive1", SocketClientUtil.bytesToHexString(bs));
        //设备类型
        been.setDeviceTypeCode(obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3098, 0, 1)));
        String bdcVersion1 = MaxWifiParseUtil.obtainRegistValueAsciiYesNull(MaxWifiParseUtil.subBytes125(bs, 3099, 0, 2));
        int bdcVersion2 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3101, 0, 1));
        String bdcVersion = String.valueOf(bdcVersion2);
        if (!TextUtils.isEmpty(bdcVersion1)) {
            bdcVersion = String.format("%s-%d", bdcVersion1, bdcVersion2);

        }
        been.setBdcVervison(bdcVersion);

        been.setDeviceTypeCode(obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3105, 0, 1)));

        //sn号
        String sn = MaxWifiParseUtil.obtainRegistValueAscii(
                MaxWifiParseUtil.subBytes(bs, 3087, 0, 8)
        );
        //mode SxxBxxDxxTxxPxxUxxMxx
        byte[] modebs = MaxWifiParseUtil.subBytes125(bs, 3108, 0, 4);
        //解析int值
        BigInteger big = new BigInteger(1, modebs);
        long bigInteger = big.longValue();
        String deviceModelNew4 = MaxUtil.getDeviceModelNew4(bigInteger);

        //M3版本
        String m3Version1 = MaxWifiParseUtil.obtainRegistValueAsciiYesNull(MaxWifiParseUtil.subBytes(bs, 3096, 0, 2));
        int m3Version2 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3103, 0, 1));
        String m3Version = String.valueOf(m3Version2);
        if (!TextUtils.isEmpty(m3Version1)) {
            m3Version = String.format("%s-%d", m3Version1, m3Version2);
        }


        //BMS版本


        String bmsVersion = String.valueOf(obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3105, 0, 1)));

     /*   String bmsVersion = MaxWifiParseUtil.obtainRegistValueAscii(
                MaxWifiParseUtil.subBytes(bs, 3105, 0, 1)
        );*/


        //厂商信息
        String company = MaxWifiParseUtil.obtainRegistValueAscii(
                MaxWifiParseUtil.subBytes(bs, 3106, 0, 1)
        );


        //电池通信类型
        String batteryType = MaxWifiParseUtil.obtainRegistValueAscii(
                MaxWifiParseUtil.subBytes(bs, 3107, 0, 1)
        );


        int batteryVersion1 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3105, 0, 1));
        int batteryVersion2 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3106, 0, 1));
        String batVersion = String.valueOf(batteryVersion1) + String.valueOf(batteryVersion2);
        been.setBatVersion(batVersion);

        //解析bdc/电池信息
        BDCInfoBean bdcInfoBean = been.getBdcInfoBean();
        //dsp固件版本
        bdcInfoBean.setDsp_version(bdcVersion);
        //bdc序列号
        bdcInfoBean.setBdc_serialnumber(sn);
        //模式
        bdcInfoBean.setBdc_mode(deviceModelNew4);
        //M3版本
        bdcInfoBean.setM3_version(m3Version);
    /*    //状态
        bdcInfoBean.setStatus(status);
        //模式
        bdcInfoBean.setWorkMode(mode);*/

        bdcInfoBean.setBms_version(bmsVersion);

        bdcInfoBean.setBattery_company(company);
        bdcInfoBean.setBattery_type(batteryType);
    }

    /**
     * 解析04:3000-3124号寄存器
     */
    public static void parseInput3kT3124(@NonNull MaxDataBean been, byte[] bytes) {
        //移除外部协议
        byte[] bs = removePro17(bytes);
        Log.i("receive1", SocketClientUtil.bytesToHexString(bs));
        MaxDataDeviceBean deviceBean = been.getDeviceBeen();
        //发电量
        been.setTodayEnergy(obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs, 3049, 0, 2)));
        been.setTotalEnergy(obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs, 3051, 0, 2)));
        //用电消耗和并网电量
        been.setEtoGridToday(obtainValueTwo(bs, 3071));
        been.setEtoGridTotal(obtainValueTwo(bs, 3073));
        been.seteLoadToday(obtainValueTwo(bs, 3075));
        been.seteLoadTotal(obtainValueTwo(bs, 3077));
        //功率
        been.setNormalPower(obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs, 3023, 0, 2)));
//        been.setTotalPower(obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs,3028,0,2)));

        //顺逆流功率
        int ptouser = obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs, 3041, 0, 2));
        int ptogrid = obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs, 3043, 0, 2));
        been.setPtouser(ptouser);
        been.setPtogrid(ptogrid);
        been.setPusertogrid(ptouser - ptogrid);
        //干接点
        int dryStatus = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3119, 0, 1));
        been.setDryStatus(dryStatus);
        //bdc
        int bdcStatus = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3118, 0, 1));
        been.setBdcStatus(bdcStatus);
        //机器状态
        been.setStatus(obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3000, 0, 1)));
        //故障码
        been.setErrCode(obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3105, 0, 1)));
        been.setErrCodeSecond(obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3107, 0, 1)));
        //告警
        been.setWarmCode(obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3106, 0, 1)));
        been.setWarmCodeSecond(obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3108, 0, 1)));
        //并网倒计时
        deviceBean.setLastTime(obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3115, 0, 1)));
        //ipf
        deviceBean.setIpf(obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3100, 0, 1)));
        //电网频率
        deviceBean.setGridFre(obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3025, 0, 1)));
        //ac电压电流rst
        int vR = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3026, 0, 1));
        int aR = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3027, 0, 1));
        int pR = obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs, 3028, 0, 2));
        List<String> acValues = new ArrayList<>();
        acValues.add(Arith.mul(vR, muilt) + "");
        acValues.add(deviceBean.getGridFre());
        acValues.add(Arith.mul(aR, muilt) + "");
        acValues.add(Arith.mul(pR, muilt) + "");
        acValues.add(deviceBean.getIpf());
        been.setACList(acValues);
        //pv侧信息
        int vPv1 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3003, 0, 1));
        int vPv2 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3007, 0, 1));
        int vPv3 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3011, 0, 1));
        int aPv1 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3004, 0, 1));
        int aPv2 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3008, 0, 1));
        int aPv3 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3012, 0, 1));
        //pv值
        List<String> pvValues = new ArrayList<>();
        pvValues.add(Arith.mul(vPv1, muilt) + "");
        pvValues.add(Arith.mul(aPv1, muilt) + "");
        pvValues.add(Arith.mul(vPv2, muilt) + "");
        pvValues.add(Arith.mul(aPv2, muilt) + "");
        pvValues.add(Arith.mul(vPv3, muilt) + "");
        pvValues.add(Arith.mul(aPv3, muilt) + "");
        been.setPVList(pvValues);
        //内部信息
        //温度
//        int  envTemp = obtainValueOne(MaxWifiParseUtil.subBytes125(bs,3094,0,1));
//        int  envTemp2 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs,3096,0,1));
        int envTemp = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3097, 0, 1));
        int deviceTemp = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3093, 0, 1));
        int boostTemp = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3095, 0, 1));
        //iso
        int iso = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3087, 0, 1));
        //电压
        int pBusV = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3098, 0, 1));
        int nBusV = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3099, 0, 1));
        //功率百分比
        int powerPer = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3101, 0, 1));
        //降额模式
        int deratMode = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3104, 0, 1));
        int deratMode2 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3086, 0, 1));
        deviceBean.setDerateMode(deratMode);
        deviceBean.setDerateMode2(deratMode2);
        deviceBean.setRealOPowerPercent(powerPer);
        deviceBean.setDeviceTemp(deviceTemp);
        deviceBean.setEnvTemp(envTemp);
        deviceBean.setBoostTemp(boostTemp);
        deviceBean.setpBusV(pBusV);
        deviceBean.setnBusV(nBusV);
        deviceBean.setIso(iso);
    }

    /**
     * 解析04:3000-3124号寄存器 增加pv 到pv1-pv4
     */
    public static void parseInput3kT3124V2(@NonNull MaxDataBean been, byte[] bytes) {
        //移除外部协议
        byte[] bs = removePro17(bytes);
        Log.i("receive1", SocketClientUtil.bytesToHexString(bs));
        MaxDataDeviceBean deviceBean = been.getDeviceBeen();
        //发电量
        been.setTodayEnergy(obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs, 3049, 0, 2)));
        been.setTotalEnergy(obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs, 3051, 0, 2)));
        //用电消耗和并网电量
        been.setEtoGridToday(obtainValueTwo(bs, 3071));
        been.setEtoGridTotal(obtainValueTwo(bs, 3073));
        been.seteLoadToday(obtainValueTwo(bs, 3075));
        been.seteLoadTotal(obtainValueTwo(bs, 3077));
        //功率
        been.setNormalPower(obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs, 3023, 0, 2)));
//        been.setTotalPower(obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs,3028,0,2)));

        //顺逆流功率
        int ptouser = obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs, 3041, 0, 2));
        int ptogrid = obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs, 3043, 0, 2));
        been.setPtouser(ptouser);
        been.setPtogrid(ptogrid);
        been.setPusertogrid(ptouser - ptogrid);
        //干接点
        int dryStatus = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3119, 0, 1));
        been.setDryStatus(dryStatus);
        //bdc
        int bdcStatus = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3118, 0, 1));
        been.setBdcStatus(bdcStatus);
        //机器状态
        been.setStatus(obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3000, 0, 1)));
        //故障码
        been.setErrCode(obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3105, 0, 1)));
        been.setErrCodeSecond(obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3107, 0, 1)));
        //告警
        been.setWarmCode(obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3106, 0, 1)));
        been.setWarmCodeSecond(obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3108, 0, 1)));
        //并网倒计时
        deviceBean.setLastTime(obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3115, 0, 1)));
        //ipf
        deviceBean.setIpf(obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3100, 0, 1)));
        //电网频率
        deviceBean.setGridFre(obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3025, 0, 1)));
        //ac电压电流rst
        int vR = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3026, 0, 1));
        int aR = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3027, 0, 1));
        int pR = obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs, 3028, 0, 2));
        List<String> acValues = new ArrayList<>();
        acValues.add(Arith.mul(vR, muilt) + "");
        acValues.add(deviceBean.getGridFre());
        acValues.add(Arith.mul(aR, muilt) + "");
        acValues.add(Arith.mul(pR, muilt) + "");
        acValues.add(deviceBean.getIpf());
        been.setACList(acValues);
        //pv侧信息
        int vPv1 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3003, 0, 1));
        int vPv2 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3007, 0, 1));
        int vPv3 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3011, 0, 1));
        int vPv4 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3015, 0, 1));
        int aPv1 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3004, 0, 1));
        int aPv2 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3008, 0, 1));
        int aPv3 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3012, 0, 1));
        int aPv4 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3016, 0, 1));
        int pPv1 = obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs, 3005, 0, 2));
        int pPv2 = obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs, 3009, 0, 2));
        int pPv3 = obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs, 3013, 0, 2));
        int pPv4 = obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs, 3017, 0, 2));
        //pv值
        List<String> pvValues = new ArrayList<>();
        pvValues.add(Arith.mul(vPv1, muilt) + "");
        pvValues.add(Arith.mul(aPv1, muilt) + "");
        pvValues.add(Arith.mul(pPv1, muilt) + "");
        pvValues.add(Arith.mul(vPv2, muilt) + "");
        pvValues.add(Arith.mul(aPv2, muilt) + "");
        pvValues.add(Arith.mul(pPv2, muilt) + "");
        pvValues.add(Arith.mul(vPv3, muilt) + "");
        pvValues.add(Arith.mul(aPv3, muilt) + "");
        pvValues.add(Arith.mul(pPv3, muilt) + "");
        pvValues.add(Arith.mul(vPv4, muilt) + "");
        pvValues.add(Arith.mul(aPv4, muilt) + "");
        pvValues.add(Arith.mul(pPv4, muilt) + "");
        been.setPVList(pvValues);
        //内部信息
        //温度
//        int  envTemp = obtainValueOne(MaxWifiParseUtil.subBytes125(bs,3094,0,1));
//        int  envTemp2 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs,3096,0,1));
        int envTemp = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3097, 0, 1));
        int deviceTemp = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3093, 0, 1));
        int boostTemp = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3095, 0, 1));
        //iso
        int iso = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3087, 0, 1));
        //电压
        int pBusV = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3098, 0, 1));
        int nBusV = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3099, 0, 1));
        //功率百分比
        int powerPer = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3101, 0, 1));
        //降额模式
        int deratMode = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3104, 0, 1));
        int deratMode2 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3086, 0, 1));
        deviceBean.setDerateMode(deratMode);
        deviceBean.setDerateMode2(deratMode2);
        deviceBean.setRealOPowerPercent(powerPer);
        deviceBean.setDeviceTemp(deviceTemp);
        deviceBean.setEnvTemp(envTemp);
        deviceBean.setBoostTemp(boostTemp);
        deviceBean.setpBusV(pBusV);
        deviceBean.setnBusV(nBusV);
        deviceBean.setIso(iso);
    }

    /**
     * 解析04:1000-1124号寄存器
     */
    public static void parseInput1kT1124Mix(@NonNull MaxDataBean been, byte[] bytes) {
        //移除外部协议
        byte[] bs = removePro17(bytes);
        Log.i("receive1", SocketClientUtil.bytesToHexString(bs));
        MaxDataDeviceBean deviceBean = been.getDeviceBeen();
        ToolStorageDataBean storageBeen = been.getStorageBeen();
        //用电消耗和并网电量
        been.setEtoGridToday(obtainValueTwo(bs, 1048));
        been.setEtoGridTotal(obtainValueTwo(bs, 1050));
        been.seteLoadToday(obtainValueTwo(bs, 1060));
        been.seteLoadTotal(obtainValueTwo(bs, 1062));
        //日总充电、放电、取电电量
        storageBeen.seteChargeToday(obtainValueTwo(bs, 1056));
        storageBeen.seteChargeTotal(obtainValueTwo(bs, 1058));
        storageBeen.seteDischargeToday(obtainValueTwo(bs, 1052));
        storageBeen.seteDischargeTotal(obtainValueTwo(bs, 1054));

        been.seteObtainToday(obtainValueTwo(bs, 1044));
        been.seteObtainTotal(obtainValueTwo(bs, 1046));
        //解析mix独有的故障情况：1001-1008 轮询解析
//        been.setErrDetail(MyUtilsV2.getMixToolWarn(bs));
        been.setWarmDetail(MyUtilsV2.getMixToolWarn(bs));

        //添加取电、馈电功率
        been.setPacToUser(obtainValueTwo(bs, 1021));
        been.setPacToGrid(obtainValueTwo(bs, 1029));
        //添加充放电功率
        storageBeen.setpCharge(obtainValueTwo(bs, 1011));
        storageBeen.setpDischarge(obtainValueTwo(bs, 1009));
        List<String> acValues = been.getACList();
        if (acValues != null && acValues.size() >= 6) {
            acValues.set(4, Arith.mul(been.getPacToUser(), muilt) + "");
            acValues.set(5, Arith.mul(been.getPacToGrid(), muilt) + "");
        } else {
            acValues = new ArrayList<>();
            acValues.add("");
            acValues.add("");
            acValues.add("");
            acValues.add("");
            acValues.add(Arith.mul(been.getPacToUser(), muilt) + "");
            acValues.add(Arith.mul(been.getPacToGrid(), muilt) + "");
        }
        //内部参数相关
        been.setSysFaultWord(obtainValueOne(bs, 1001));
        been.setSysFaultWord1(obtainValueOne(bs, 1002));
        been.setSysFaultWord2(obtainValueOne(bs, 1003));
        been.setSysFaultWord3(obtainValueOne(bs, 1004));
        been.setSysFaultWord4(obtainValueOne(bs, 1005));
        been.setSysFaultWord5(obtainValueOne(bs, 1006));
        been.setSysFaultWord6(obtainValueOne(bs, 1007));
        been.setSysFaultWord7(obtainValueOne(bs, 1008));
        deviceBean.setnBusV(obtainValueOne(bs, 1042));
        deviceBean.setDspStatus(obtainValueOne(bs, 1000));
        deviceBean.setPriority(obtainValueOne(bs, 1044));
        //顺逆流功率
        int ptouser = obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs, 1015, 0, 2));
        int ptogrid = obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs, 1023, 0, 2));
        been.setPtouser(ptouser);
        been.setPtogrid(ptogrid);
        been.setPusertogrid(ptouser > 0 ? ptouser : -ptogrid);
        //离网参数
        storageBeen.setvAc1(obtainValueOne(bs, 1068));
        storageBeen.setvAc2(obtainValueOne(bs, 1072));
        storageBeen.setvAc3(obtainValueOne(bs, 1076));
        storageBeen.setaAc1(obtainValueOne(bs, 1069));
        storageBeen.setaAc2(obtainValueOne(bs, 1073));
        storageBeen.setaAc3(obtainValueOne(bs, 1077));
        storageBeen.setpAc1(obtainValueTwo(bs, 1070));
        storageBeen.setpAc2(obtainValueTwo(bs, 1072));
        storageBeen.setpAc3(obtainValueTwo(bs, 1078));
        storageBeen.setLoadPercent(obtainValueOne(bs, 1080));
        storageBeen.setPf(obtainValueOne(bs, 1081));
        storageBeen.setFacEPS(obtainValueOne(bs, 1067));
        //BDC参数
        storageBeen.setBdcBatV(obtainValueOne(bs, 1042));
        storageBeen.setBdcSoc(obtainValueOne(bs, 1014));
        storageBeen.setBdcPCharge(obtainValueTwo(bs, 1009));
        storageBeen.setBdcPDisCharge(obtainValueTwo(bs, 1011));
        storageBeen.setBdcVbatDsp(obtainValueOne(bs, 1013));

        //电池信息 一组
        //bms1
//        storageBeen.setBmsComType(obtainValueOne(bytes,3229));
//        storageBeen.setBatteryCompany(obtainValueOne(bytes,3224));
        storageBeen.setBmsStatus(obtainValueOne(bs, 1083));
        storageBeen.setBmsError(obtainValueOne(bs, 1085));
        storageBeen.setBmsWarm(obtainValueOne(bs, 1099));
        storageBeen.setvBms(obtainValueOne(bs, 1087));
        storageBeen.setaBms(obtainValueOne(bs, 1088));
        storageBeen.setSoc(obtainValueOne(bs, 1086));
        storageBeen.setTempBms(obtainValueOne(bs, 1089));
        storageBeen.setvCV(obtainValueOne(bs, 1097));
        storageBeen.setaChargeMax(obtainValueOne(bs, 1090));
        storageBeen.setaDischargeMax(obtainValueOne(bs, 1043));

    }

    /**
     * 解析04:0-124号寄存器
     */
    public static void parseInput0T124Mix(@NonNull MaxDataBean been, byte[] bytes) {
        //移除外部协议
        byte[] bs = removePro17(bytes);
        Log.i("receive1", SocketClientUtil.bytesToHexString(bs));
        MaxDataDeviceBean deviceBean = been.getDeviceBeen();
        //发电量
        been.setTodayEnergy(obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs, 53, 0, 2)));
        been.setTotalEnergy(obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs, 91, 0, 2)));
        //功率
        been.setNormalPower(obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs, 35, 0, 2)));
        //-----------------PV
        //pv侧信息
//        int  pPv = obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs,1,0,2));
        int vPv1 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3, 0, 1));
        int vPv2 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 7, 0, 1));
//        int  aPv1 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs,4,0,1));
//        int  aPv2 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs,8,0,1));
        int pPv1 = obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs, 5, 0, 2));
        int pPv2 = obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs, 9, 0, 2));
        //pv值
        List<String> pvValues = new ArrayList<>();
        pvValues.add(Arith.mul(vPv1, muilt) + "");
        pvValues.add(Arith.mul(pPv1, muilt) + "");
        pvValues.add(Arith.mul(vPv2, muilt) + "");
        pvValues.add(Arith.mul(pPv2, muilt) + "");
        been.setPVList(pvValues);
        //--------------------------------------AC
        //ipf
        deviceBean.setIpf(obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 100, 0, 1)));
        //电网频率
        deviceBean.setGridFre(obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 37, 0, 1)));
        //ac电压电流rst
        int vR = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 38, 0, 1));
        int aR = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 39, 0, 1));
        int pR = obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs, 35, 0, 2));
        //视在功率
        int pVA = obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs, 40, 0, 2));
        List<String> acValues = been.getACList();
        if (been.getACList() == null || been.getACList().size() == 0) {
            acValues = new ArrayList<>();
            acValues.add(Arith.mul(pR, muilt) + "");
            acValues.add(deviceBean.getGridFre());
            acValues.add(Arith.mul(vR, muilt) + "");
            acValues.add(Arith.mul(pVA, muilt) + "");
            acValues.add("");
            acValues.add("");
            been.setACList(acValues);
        } else {
            acValues.set(0, Arith.mul(pR, muilt) + "");
            acValues.set(1, deviceBean.getGridFre());
            acValues.set(2, Arith.mul(vR, muilt) + "");
            acValues.set(3, Arith.mul(pVA, muilt) + "");
        }

        //-------------------------------------内部参数
        int envTemp = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 94, 0, 1));
        int deviceTemp = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 93, 0, 1));
        int boostTemp = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 95, 0, 1));
        //iso
//        int  iso = obtainValueOne(MaxWifiParseUtil.subBytes125(bs,3087,0,1));
        //电压
        int pBusV = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 98, 0, 1));
        //功率百分比
        int powerPer = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 101, 0, 1));
        //降额模式
        int deratMode = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 104, 0, 1));
//        int  deratMode2 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs,3086,0,1));
        deviceBean.setDerateMode(deratMode);
//        deviceBean.setDerateMode2(deratMode2);
        deviceBean.setRealOPowerPercent(powerPer);
        deviceBean.setDeviceTemp(deviceTemp);
        deviceBean.setEnvTemp(envTemp);
        deviceBean.setBoostTemp(boostTemp);
        deviceBean.setpBusV(pBusV);
//        deviceBean.setIso(iso);
        //-------------------------------------关于设备
    }

    /**
     * 解析04:3125-3249号寄存器
     */
    public static void parseInput3125T3249(@NonNull MaxDataBean been, byte[] bs) {
        //移除外部协议
        byte[] bytes = removePro17(bs);
        Log.i("receive1", SocketClientUtil.bytesToHexString(bytes));
        MaxDataDeviceBean deviceBean = been.getDeviceBeen();
        ToolStorageDataBean storageBeen = been.getStorageBeen();
        BDCInfoBean bdcInfoBean = been.getBdcInfoBean();

        /*BDC信息*/
        //bdc状态
        storageBeen.setpChargeBDC1(obtainValueTwo(bytes, 3180));
        storageBeen.setpDischargeBDC1(obtainValueTwo(bytes, 3178));
//        storageBeen.setpChargeBDC2(obtainValueTwo(bytes, 3203));
        storageBeen.setpDischargeBDC2(obtainValueTwo(bytes, 3201));
        storageBeen.seteChargeToday(obtainValueTwo(bytes, 3129));
        storageBeen.seteDischargeToday(obtainValueTwo(bytes, 3125));
        storageBeen.seteChargeTotal(obtainValueTwo(bytes, 3131));
        storageBeen.seteDischargeTotal(obtainValueTwo(bytes, 3127));
        //bms1
        storageBeen.setBmsComType(obtainValueOne(bytes, 3229));
        storageBeen.setBatteryCompany(obtainValueOne(bytes, 3224));
        storageBeen.setBmsWorkType(obtainValueOne(bytes, 3211));
        storageBeen.setBmsStatus(obtainValueOne(bytes, 3212));
        storageBeen.setBmsError(obtainValueOne(bytes, 3213));
        storageBeen.setBmsWarm(obtainValueOne(bytes, 3214));
        storageBeen.setvBms(obtainValueOne(bytes, 3216));
        storageBeen.setaBms(obtainValueOne(bytes, 3217));
        storageBeen.setSoc(obtainValueOne(bytes, 3215));
        storageBeen.setSoh(obtainValueOne(bytes, 3222));
        storageBeen.setTempBms(obtainValueOne(bytes, 3218));
        storageBeen.setvCV(obtainValueOne(bytes, 3223));
        storageBeen.setaChargeMax(obtainValueOne(bytes, 3219));


        storageBeen.setaDischargeMax(obtainValueOne(bytes, 3220));
        //bms2 + 19
        storageBeen.setBmsComType02(obtainValueOne(bytes, 3248));
        storageBeen.setBatteryCompany02(obtainValueOne(bytes, 3243));
        storageBeen.setBmsStatus02(obtainValueOne(bytes, 3231));
        storageBeen.setBmsError02(obtainValueOne(bytes, 3232));
        storageBeen.setBmsWarm02(obtainValueOne(bytes, 3233));
        storageBeen.setvBms02(obtainValueOne(bytes, 3235));
        storageBeen.setaBms02(obtainValueOne(bytes, 3236));
        storageBeen.setSoc02(obtainValueOne(bytes, 3234));
        storageBeen.setTempBms02(obtainValueOne(bytes, 3237));
        storageBeen.setvCV02(obtainValueOne(bytes, 3242));
        storageBeen.setaChargeMax02(obtainValueOne(bytes, 3238));
        storageBeen.setaDischargeMax02(obtainValueOne(bytes, 3238));

        storageBeen.setvAc1(obtainValueOne(bytes, 3146));
        storageBeen.setvAc2(obtainValueOne(bytes, 3150));
        storageBeen.setvAc3(obtainValueOne(bytes, 3154));
        storageBeen.setaAc1(obtainValueOne(bytes, 3147));
        storageBeen.setaAc2(obtainValueOne(bytes, 3151));
        storageBeen.setaAc3(obtainValueOne(bytes, 3155));
        storageBeen.setpAc1(obtainValueTwo(bytes, 3148));
        storageBeen.setpAc2(obtainValueTwo(bytes, 3152));
        storageBeen.setpAc3(obtainValueTwo(bytes, 3156));
        storageBeen.setpEPSTotal(obtainValueTwo(bytes, 3158));
        storageBeen.setFacEPS(obtainValueOne(bytes, 3145));
        storageBeen.setLoadPercent(obtainValueOne(bytes, 3160));
        storageBeen.setPf(obtainValueOne(bytes, 3161));
        //bdc1相关
        storageBeen.setStatusBDC(obtainValueOne(bytes, 3166) & 0b11111111);
        storageBeen.setWorkModeBDC(obtainValueOne(bytes, 3166) >> 8);
        storageBeen.setvBat(obtainValueOne(bytes, 3169));
        storageBeen.setvBus1(obtainValueOne(bytes, 3172));
        storageBeen.setvBus2(obtainValueOne(bytes, 3173));
        storageBeen.setvBus3(obtainValueOne(bytes, 3188));
        storageBeen.setaBat(obtainValueOne(bytes, 3170));
        storageBeen.setaBB(obtainValueOne(bytes, 3174));
        storageBeen.setaLLC(obtainValueOne(bytes, 3175));
        storageBeen.setTempA(obtainValueOne(bytes, 3176));
        storageBeen.setTempB(obtainValueOne(bytes, 3177));
        storageBeen.setErrorStorage(obtainValueOne(bytes, 3167));
        storageBeen.setWarmStorage(obtainValueOne(bytes, 3168));
        storageBeen.setError2Storage(obtainValueOne(bytes, 3187) >> 12);
        storageBeen.setWarm2Storage(obtainValueOne(bytes, 3187) >> 8 & 0b00001111);
        //bdc1相关 + 23
        storageBeen.setStatusBDC02(obtainValueOne(bytes, 3189) & 0b11111111);
        storageBeen.setWorkModeBDC02(obtainValueOne(bytes, 3189) >> 8);
        storageBeen.setvBat02(obtainValueOne(bytes, 3192));
        storageBeen.setvBus102(obtainValueOne(bytes, 3195));
        storageBeen.setvBus202(obtainValueOne(bytes, 3196));
        storageBeen.setaBat02(obtainValueOne(bytes, 3193));
        storageBeen.setaBB02(obtainValueOne(bytes, 3197));
        storageBeen.setaLLC02(obtainValueOne(bytes, 3198));
        storageBeen.setTempA02(obtainValueOne(bytes, 3199));
        storageBeen.setTempB02(obtainValueOne(bytes, 3200));
        storageBeen.setErrorStorage02(obtainValueOne(bytes, 3190));
        storageBeen.setWarmStorage02(obtainValueOne(bytes, 3191));
        storageBeen.setError2Storage02(obtainValueOne(bytes, 3210) >> 12);
        storageBeen.setWarm2Storage02(obtainValueOne(bytes, 3210) >> 8 & 0b00001111);
        //电池保护信息
        storageBeen.setBmsProtect1(obtainValueOne(bytes, 3202));
        storageBeen.setBmsProtect2(storageBeen.getBmsError());
        storageBeen.setBmsProtect3(obtainValueOne(bytes, 3226));
        //电池告警信息
        storageBeen.setBmsWarining(obtainValueOne(bytes, 3203));
        storageBeen.setBmsWarining2(storageBeen.getBmsWarm());
        storageBeen.setBmsWarining3(obtainValueOne(bytes, 3225));
        //电池故障信息
        storageBeen.setBmsError1(obtainValueOne(bytes, 3204));
        storageBeen.setBmsError2(obtainValueOne(bytes, 3205));

        //降额模式
        int deratMode = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 3165, 0, 1));
        deviceBean.setDerateMode(deratMode);

        //新的bdc信息解析
        bdcInfoBean.setStatus(storageBeen.getStatusBDC());
        //模式
        bdcInfoBean.setWorkMode(storageBeen.getWorkModeBDC());
        //充电功率
        double pChargeBDC1 = storageBeen.getpChargeBDC1();
        double mul = Arith.mul(pChargeBDC1 + 0, 1);
        bdcInfoBean.setBattery_charge_power(String.valueOf(mul));
        //放电功率
        double pDisChargeBDC1 = storageBeen.getpDischargeBDC1();
        double mul1 = Arith.mul(pDisChargeBDC1 + 0, 1);
        bdcInfoBean.setBattery_dischage_power(String.valueOf(mul1));
        //总充电量3184-3185
        int allCharge = obtainValueTwo(bytes, 3184);
        double mul2 = Arith.mul(allCharge + 0, 0.1);
        bdcInfoBean.setChage_total(String.valueOf(mul2));
        //总放电量3182-3183
        int allDisCharge = obtainValueTwo(bytes, 3182);
        double mul3 = Arith.mul(allDisCharge + 0, 0.1);
        bdcInfoBean.setDischarge_total(String.valueOf(mul3));

//        //故障码
//        int errorCode = obtainValueOne(bytes, 3167);
//        //故障附码
//        int errorCode2 = obtainValueOne(bytes, 3187) >> 12;
//        //告警码
//        int warningCode = obtainValueOne(bytes, 3168);
//        //告警附码
//        int warningCode2 = obtainValueOne(bytes, 3187) >> 8 & 0b00001111;


    }

    /**
     * 解析04:0-99号寄存器(与04:0-124共用方法)
     */
    public static void parseMax2(MaxDataBean been, byte[] bytes) {
        //移除外部协议
        byte[] bs = removePro17(bytes);
        getMaxDataDeviceBean(been, bs);
    }

    /**
     * 解析04:0-99号寄存器(与04:0-124共用方法)
     */
    public static void parseMax1500V2(MaxDataBean been, byte[] bytes) {
        //移除外部协议
        byte[] bs = removePro17(bytes);
        getMax1500VDataDeviceBean(been, bs);
    }

    /**
     * 解析04:0-125号寄存器(与04:0-124共用方法)
     */
    public static void parseMax2OldInv(MaxDataBean been, byte[] bytes) {
        //移除外部协议
        byte[] bs = removePro17(bytes);
        getMaxDataDeviceBeanOldInv(been, bs);
    }

    /**
     * 解析04:100-199号寄存器
     */
    public static void parseMax3(MaxDataBean been, byte[] bytes) {
        //移除外部协议
        byte[] bs = removePro17(bytes);
        //最大输出功率
        int maxPower = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs, 102, 0, 2));
        int powerPer = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 113, 0, 1));
        int derateMode = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 104, 0, 1));
        int mismatchC = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 174, 0, 1));
        int unblanceC = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 175, 0, 1));
        int disconnC = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 176, 0, 1));
        int pidCode = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 177, 0, 1));
        int pidStatus = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 141, 0, 1));
        int ipf = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 100, 0, 1));
        int lastTime = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 114, 0, 1));
        //故障和警告
        int errCode = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 105, 0, 1));
        int warmCode = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 112, 0, 2));
        //副故障和警告
        int errCodeSecond = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs, 106, 0, 1));
        int warmCodeSecond = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs, 110, 0, 2));
        //故障位
        int error1 = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs, 106, 0, 2));
        int error2 = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs, 108, 0, 2));
        //pv串
        int vPvc1 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 142, 0, 1));
        int vPvc2 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 144, 0, 1));
        int vPvc3 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 146, 0, 1));
        int vPvc4 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 148, 0, 1));
        int vPvc5 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 150, 0, 1));
        int vPvc6 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 152, 0, 1));
        int vPvc7 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 154, 0, 1));
        int vPvc8 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 156, 0, 1));
        int vPvc9 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 158, 0, 1));
        int vPvc10 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 160, 0, 1));
        int vPvc11 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 162, 0, 1));
        int vPvc12 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 164, 0, 1));
        int vPvc13 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 166, 0, 1));
        int vPvc14 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 168, 0, 1));
        int vPvc15 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 170, 0, 1));
        int vPvc16 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 172, 0, 1));

        int aPvc1 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 143, 0, 1));
        int aPvc2 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 145, 0, 1));
        int aPvc3 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 147, 0, 1));
        int aPvc4 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 149, 0, 1));
        int aPvc5 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 151, 0, 1));
        int aPvc6 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 153, 0, 1));
        int aPvc7 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 155, 0, 1));
        int aPvc8 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 157, 0, 1));
        int aPvc9 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 159, 0, 1));
        int aPvc10 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 161, 0, 1));
        int aPvc11 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 163, 0, 1));
        int aPvc12 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 165, 0, 1));
        int aPvc13 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 167, 0, 1));
        int aPvc14 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 169, 0, 1));
        int aPvc15 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 171, 0, 1));
        int aPvc16 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 173, 0, 1));
        //pid
        int vPid1 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 125, 0, 1));
        int vPid2 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 127, 0, 1));
        int vPid3 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 129, 0, 1));
        int vPid4 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 131, 0, 1));
        int vPid5 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 133, 0, 1));
        int vPid6 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 135, 0, 1));
        int vPid7 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 137, 0, 1));
        int vPid8 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 139, 0, 1));

        int aPid1 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 126, 0, 1));
        int aPid2 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 128, 0, 1));
        int aPid3 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 130, 0, 1));
        int aPid4 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 132, 0, 1));
        int aPid5 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 134, 0, 1));
        int aPid6 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 136, 0, 1));
        int aPid7 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 138, 0, 1));
        int aPid8 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 140, 0, 1));

        //设置值
//        if (errCode != 0){
//            errCode = errCode + 99;
//        }
//        if (warmCode != 0){
//            warmCode = warmCode + 99;
//        }
        been.setErrCode(errCode);
        been.setWarmCode(warmCode);
        been.setErrCodeSecond(errCodeSecond);
        been.setWarmCodeSecond(warmCodeSecond);
        been.setError1(error1);
        been.setError2(error2);
        MaxDataDeviceBean deviceBean = been.getDeviceBeen();
        deviceBean.setMaxOutPower(maxPower);
        deviceBean.setRealOPowerPercent(powerPer);
        deviceBean.setDerateMode(derateMode);
        deviceBean.setMismatchC(mismatchC);
        deviceBean.setUnblanceC(unblanceC);
        deviceBean.setDisConnectC(disconnC);
        deviceBean.setPidErrCode(pidCode);
        deviceBean.setPidStatus(pidStatus);
        deviceBean.setIpf(ipf);
        deviceBean.setLastTime(lastTime);
        //pv串
        List<String> pvcValues = new ArrayList<>();
        pvcValues.add(Arith.mul(vPvc1, muilt) + "");
        pvcValues.add(Arith.mul(aPvc1, muilt) + "");
        pvcValues.add(Arith.mul(vPvc2, muilt) + "");
        pvcValues.add(Arith.mul(aPvc2, muilt) + "");
        pvcValues.add(Arith.mul(vPvc3, muilt) + "");
        pvcValues.add(Arith.mul(aPvc3, muilt) + "");
        pvcValues.add(Arith.mul(vPvc4, muilt) + "");
        pvcValues.add(Arith.mul(aPvc4, muilt) + "");
        pvcValues.add(Arith.mul(vPvc5, muilt) + "");
        pvcValues.add(Arith.mul(aPvc5, muilt) + "");
        pvcValues.add(Arith.mul(vPvc6, muilt) + "");
        pvcValues.add(Arith.mul(aPvc6, muilt) + "");
        pvcValues.add(Arith.mul(vPvc7, muilt) + "");
        pvcValues.add(Arith.mul(aPvc7, muilt) + "");
        pvcValues.add(Arith.mul(vPvc8, muilt) + "");
        pvcValues.add(Arith.mul(aPvc8, muilt) + "");
        pvcValues.add(Arith.mul(vPvc9, muilt) + "");
        pvcValues.add(Arith.mul(aPvc9, muilt) + "");
        pvcValues.add(Arith.mul(vPvc10, muilt) + "");
        pvcValues.add(Arith.mul(aPvc10, muilt) + "");
        pvcValues.add(Arith.mul(vPvc11, muilt) + "");
        pvcValues.add(Arith.mul(aPvc11, muilt) + "");
        pvcValues.add(Arith.mul(vPvc12, muilt) + "");
        pvcValues.add(Arith.mul(aPvc12, muilt) + "");
        pvcValues.add(Arith.mul(vPvc13, muilt) + "");
        pvcValues.add(Arith.mul(aPvc13, muilt) + "");
        pvcValues.add(Arith.mul(vPvc14, muilt) + "");
        pvcValues.add(Arith.mul(aPvc14, muilt) + "");
        pvcValues.add(Arith.mul(vPvc15, muilt) + "");
        pvcValues.add(Arith.mul(aPvc15, muilt) + "");
        pvcValues.add(Arith.mul(vPvc16, muilt) + "");
        pvcValues.add(Arith.mul(aPvc16, muilt) + "");
        been.setPVCList(pvcValues);
        //pid值
        List<String> pidValues = new ArrayList<>();
        pidValues.add(Arith.mul(vPid1, muilt) + "");
        pidValues.add(Arith.mul(aPid1, muilt) + "");
        pidValues.add(Arith.mul(vPid2, muilt) + "");
        pidValues.add(Arith.mul(aPid2, muilt) + "");
        pidValues.add(Arith.mul(vPid3, muilt) + "");
        pidValues.add(Arith.mul(aPid3, muilt) + "");
        pidValues.add(Arith.mul(vPid4, muilt) + "");
        pidValues.add(Arith.mul(aPid4, muilt) + "");
        pidValues.add(Arith.mul(vPid5, muilt) + "");
        pidValues.add(Arith.mul(aPid5, muilt) + "");
        pidValues.add(Arith.mul(vPid6, muilt) + "");
        pidValues.add(Arith.mul(aPid6, muilt) + "");
        pidValues.add(Arith.mul(vPid7, muilt) + "");
        pidValues.add(Arith.mul(aPid7, muilt) + "");
        pidValues.add(Arith.mul(vPid8, muilt) + "");
        pidValues.add(Arith.mul(aPid8, muilt) + "");
        been.setPIDList(pidValues);
    }

    /**
     * 解析04:100-199号寄存器
     */
    public static void parseMax1500V3(MaxDataBean been, byte[] bytes) {
        //移除外部协议
        byte[] bs = removePro17(bytes);
        //最大输出功率
        int maxPower = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs, 102, 0, 2));
        int powerPer = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 113, 0, 1));
        int derateMode = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 104, 0, 1));
        int mismatchC = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 174, 0, 1));
        int unblanceC = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 175, 0, 1));
        int disconnC = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 176, 0, 1));
        int pidCode = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 177, 0, 1));
        int pidStatus = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 141, 0, 1));
        int ipf = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 100, 0, 1));
        int lastTime = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 114, 0, 1));
        //故障和警告
        int errCode = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 105, 0, 1));
        int warmCode = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 112, 0, 2));
        //副故障和警告
        int errCodeSecond = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs, 106, 0, 1));
        int warmCodeSecond = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs, 110, 0, 2));
        //故障位
        int error1 = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs, 106, 0, 2));
        int error2 = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs, 108, 0, 2));
        //pv串
        int vPvc1 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 142, 0, 1));
        int vPvc2 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 144, 0, 1));
        int vPvc3 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 146, 0, 1));
        int vPvc4 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 148, 0, 1));
        int vPvc5 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 150, 0, 1));
        int vPvc6 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 152, 0, 1));
        int vPvc7 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 154, 0, 1));
        int vPvc8 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 156, 0, 1));
        int vPvc9 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 158, 0, 1));
        int vPvc10 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 160, 0, 1));
        int vPvc11 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 162, 0, 1));
        int vPvc12 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 164, 0, 1));
        int vPvc13 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 166, 0, 1));
        int vPvc14 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 168, 0, 1));
        int vPvc15 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 170, 0, 1));
        int vPvc16 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 172, 0, 1));

        int aPvc1 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 143, 0, 1));
        int aPvc2 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 145, 0, 1));
        int aPvc3 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 147, 0, 1));
        int aPvc4 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 149, 0, 1));
        int aPvc5 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 151, 0, 1));
        int aPvc6 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 153, 0, 1));
        int aPvc7 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 155, 0, 1));
        int aPvc8 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 157, 0, 1));
        int aPvc9 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 159, 0, 1));
        int aPvc10 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 161, 0, 1));
        int aPvc11 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 163, 0, 1));
        int aPvc12 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 165, 0, 1));
        int aPvc13 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 167, 0, 1));
        int aPvc14 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 169, 0, 1));
        int aPvc15 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 171, 0, 1));
        int aPvc16 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 173, 0, 1));
        //pid
        int vPid1 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 125, 0, 1));
        int vPid2 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 127, 0, 1));
        int vPid3 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 129, 0, 1));
        int vPid4 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 131, 0, 1));
        int vPid5 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 133, 0, 1));
        int vPid6 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 135, 0, 1));
        int vPid7 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 137, 0, 1));
        int vPid8 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 139, 0, 1));

        int aPid1 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 126, 0, 1));
        int aPid2 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 128, 0, 1));
        int aPid3 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 130, 0, 1));
        int aPid4 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 132, 0, 1));
        int aPid5 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 134, 0, 1));
        int aPid6 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 136, 0, 1));
        int aPid7 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 138, 0, 1));
        int aPid8 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 140, 0, 1));

        //设置值
//        if (errCode != 0){
//            errCode = errCode + 99;
//        }
//        if (warmCode != 0){
//            warmCode = warmCode + 99;
//        }
        been.setErrCode(errCode);
        been.setWarmCode(warmCode);
        been.setErrCodeSecond(errCodeSecond);
        been.setWarmCodeSecond(warmCodeSecond);
        been.setError1(error1);
        been.setError2(error2);
        MaxDataDeviceBean deviceBean = been.getDeviceBeen();
        deviceBean.setMaxOutPower(maxPower);
        deviceBean.setRealOPowerPercent(powerPer);
        deviceBean.setDerateMode(derateMode);
        deviceBean.setMismatchC(mismatchC);
        deviceBean.setUnblanceC(unblanceC);
        deviceBean.setDisConnectC(disconnC);
        deviceBean.setPidErrCode(pidCode);
        deviceBean.setPidStatus(pidStatus);
        deviceBean.setIpf(ipf);
        deviceBean.setLastTime(lastTime);
        //pv串
        List<String> pvcValues = been.getPVCList();
        if (pvcValues == null || pvcValues.size() < 32) {
            pvcValues = new ArrayList<>();
            pvcValues.add(Arith.mul(vPvc1, muilt) + "");
            pvcValues.add(Arith.mul(aPvc1, muilt) + "");
            pvcValues.add(Arith.mul(vPvc2, muilt) + "");
            pvcValues.add(Arith.mul(aPvc2, muilt) + "");
            pvcValues.add(Arith.mul(vPvc3, muilt) + "");
            pvcValues.add(Arith.mul(aPvc3, muilt) + "");
            pvcValues.add(Arith.mul(vPvc4, muilt) + "");
            pvcValues.add(Arith.mul(aPvc4, muilt) + "");
            pvcValues.add(Arith.mul(vPvc5, muilt) + "");
            pvcValues.add(Arith.mul(aPvc5, muilt) + "");
            pvcValues.add(Arith.mul(vPvc6, muilt) + "");
            pvcValues.add(Arith.mul(aPvc6, muilt) + "");
            pvcValues.add(Arith.mul(vPvc7, muilt) + "");
            pvcValues.add(Arith.mul(aPvc7, muilt) + "");
            pvcValues.add(Arith.mul(vPvc8, muilt) + "");
            pvcValues.add(Arith.mul(aPvc8, muilt) + "");
            pvcValues.add(Arith.mul(vPvc9, muilt) + "");
            pvcValues.add(Arith.mul(aPvc9, muilt) + "");
            pvcValues.add(Arith.mul(vPvc10, muilt) + "");
            pvcValues.add(Arith.mul(aPvc10, muilt) + "");
            pvcValues.add(Arith.mul(vPvc11, muilt) + "");
            pvcValues.add(Arith.mul(aPvc11, muilt) + "");
            pvcValues.add(Arith.mul(vPvc12, muilt) + "");
            pvcValues.add(Arith.mul(aPvc12, muilt) + "");
            pvcValues.add(Arith.mul(vPvc13, muilt) + "");
            pvcValues.add(Arith.mul(aPvc13, muilt) + "");
            pvcValues.add(Arith.mul(vPvc14, muilt) + "");
            pvcValues.add(Arith.mul(aPvc14, muilt) + "");
            pvcValues.add(Arith.mul(vPvc15, muilt) + "");
            pvcValues.add(Arith.mul(aPvc15, muilt) + "");
            pvcValues.add(Arith.mul(vPvc16, muilt) + "");
            pvcValues.add(Arith.mul(aPvc16, muilt) + "");
            been.setPVCList(pvcValues);
        } else {
            pvcValues.set(0, Arith.mul(vPvc1, muilt) + "");
            pvcValues.set(1, Arith.mul(aPvc1, muilt) + "");
            pvcValues.set(2, Arith.mul(vPvc2, muilt) + "");
            pvcValues.set(3, Arith.mul(aPvc2, muilt) + "");
            pvcValues.set(4, Arith.mul(vPvc3, muilt) + "");
            pvcValues.set(5, Arith.mul(aPvc3, muilt) + "");
            pvcValues.set(6, Arith.mul(vPvc4, muilt) + "");
            pvcValues.set(7, Arith.mul(aPvc4, muilt) + "");
            pvcValues.set(8, Arith.mul(vPvc5, muilt) + "");
            pvcValues.set(9, Arith.mul(aPvc5, muilt) + "");
            pvcValues.set(10, Arith.mul(vPvc6, muilt) + "");
            pvcValues.set(11, Arith.mul(aPvc6, muilt) + "");
            pvcValues.set(12, Arith.mul(vPvc7, muilt) + "");
            pvcValues.set(13, Arith.mul(aPvc7, muilt) + "");
            pvcValues.set(14, Arith.mul(vPvc8, muilt) + "");
            pvcValues.set(15, Arith.mul(aPvc8, muilt) + "");
            pvcValues.set(16, Arith.mul(vPvc9, muilt) + "");
            pvcValues.set(17, Arith.mul(aPvc9, muilt) + "");
            pvcValues.set(18, Arith.mul(vPvc10, muilt) + "");
            pvcValues.set(19, Arith.mul(aPvc10, muilt) + "");
            pvcValues.set(20, Arith.mul(vPvc11, muilt) + "");
            pvcValues.set(21, Arith.mul(aPvc11, muilt) + "");
            pvcValues.set(22, Arith.mul(vPvc12, muilt) + "");
            pvcValues.set(23, Arith.mul(aPvc12, muilt) + "");
            pvcValues.set(24, Arith.mul(vPvc13, muilt) + "");
            pvcValues.set(25, Arith.mul(aPvc13, muilt) + "");
            pvcValues.set(26, Arith.mul(vPvc14, muilt) + "");
            pvcValues.set(27, Arith.mul(aPvc14, muilt) + "");
            pvcValues.set(28, Arith.mul(vPvc15, muilt) + "");
            pvcValues.set(29, Arith.mul(aPvc15, muilt) + "");
            pvcValues.set(30, Arith.mul(vPvc16, muilt) + "");
            pvcValues.set(31, Arith.mul(aPvc16, muilt) + "");
        }
        //pid值
        List<String> pidValues = been.getPIDList();
        if (pidValues == null || pidValues.size() < 16) {
            pidValues = new ArrayList<>();
            pidValues.add(Arith.mul(vPid1, muilt) + "");
            pidValues.add(Arith.mul(aPid1, muilt) + "");
            pidValues.add(Arith.mul(vPid2, muilt) + "");
            pidValues.add(Arith.mul(aPid2, muilt) + "");
            pidValues.add(Arith.mul(vPid3, muilt) + "");
            pidValues.add(Arith.mul(aPid3, muilt) + "");
            pidValues.add(Arith.mul(vPid4, muilt) + "");
            pidValues.add(Arith.mul(aPid4, muilt) + "");
            pidValues.add(Arith.mul(vPid5, muilt) + "");
            pidValues.add(Arith.mul(aPid5, muilt) + "");
            pidValues.add(Arith.mul(vPid6, muilt) + "");
            pidValues.add(Arith.mul(aPid6, muilt) + "");
            pidValues.add(Arith.mul(vPid7, muilt) + "");
            pidValues.add(Arith.mul(aPid7, muilt) + "");
            pidValues.add(Arith.mul(vPid8, muilt) + "");
            pidValues.add(Arith.mul(aPid8, muilt) + "");
            been.setPIDList(pidValues);
        } else {
            pidValues.set(0, Arith.mul(vPid1, muilt) + "");
            pidValues.set(1, Arith.mul(aPid1, muilt) + "");
            pidValues.set(2, Arith.mul(vPid2, muilt) + "");
            pidValues.set(3, Arith.mul(aPid2, muilt) + "");
            pidValues.set(4, Arith.mul(vPid3, muilt) + "");
            pidValues.set(5, Arith.mul(aPid3, muilt) + "");
            pidValues.set(6, Arith.mul(vPid4, muilt) + "");
            pidValues.set(7, Arith.mul(aPid4, muilt) + "");
            pidValues.set(8, Arith.mul(vPid5, muilt) + "");
            pidValues.set(9, Arith.mul(aPid5, muilt) + "");
            pidValues.set(10, Arith.mul(vPid6, muilt) + "");
            pidValues.set(11, Arith.mul(aPid6, muilt) + "");
            pidValues.set(12, Arith.mul(vPid7, muilt) + "");
            pidValues.set(13, Arith.mul(aPid7, muilt) + "");
            pidValues.set(14, Arith.mul(vPid8, muilt) + "");
            pidValues.set(15, Arith.mul(aPid8, muilt) + "");
        }
    }

    /**
     * 去除外部协议，只留实际内容,0x17命令 23+2---适合功能码03、04
     */
    public static byte[] removePro17(byte[] bytes) {
        if (bytes == null) return null;
        //1.判断是否需要先解密然后再移除外部协议
        byte[] desCode = new byte[0];
        //2.不能直接解密源数据，可能会出现重复加密的问题
        byte[] copyData = new byte[bytes.length];
        System.arraycopy(bytes, 0, copyData, 0, bytes.length);
        if (ModbusUtil.getLocalDebugMode() == AP_MODE) {
            try {
                byte[] dataByte = DatalogApUtil.getEnCode(copyData);
                int len = dataByte.length;
                // 获取数据
                desCode = Arrays.copyOfRange(dataByte, 0, len - 2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            desCode = bytes;
        }

        int len = desCode.length;
        if (len > 25) {
            byte[] bs = new byte[len - 25];
            for (int i = 0; i < len; i++) {
                if (i > 22 && i < len - 2) {
                    bs[i - 23] = desCode[i];
                }
            }
            return bs;
        }
        return bytes;
    }

    /**
     * 去除外部协议，只留实际内容,0x17命令 20+2+1+...+2(数服协议+地址功能码+数据长度+...+crc)
     * , @param bytes:功能码6
     */
    public static byte[] removePro17Fun6(byte[] bytes) {
        if (bytes == null) return null;

        //1.AP模式需要解密，不要直接拿源数据解密，要copy一份出来
        byte[] desCode = new byte[0];

        byte[] copyData = new byte[bytes.length];
        System.arraycopy(bytes, 0, copyData, 0, bytes.length);

        if (ModbusUtil.getLocalDebugMode() == AP_MODE) {
            try {
                byte[] dataByte = DatalogApUtil.getEnCode(copyData);
                int len = dataByte.length;
                // 获取数据
                desCode = Arrays.copyOfRange(dataByte, 0, len - 2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            desCode = bytes;
        }

        int len = desCode.length;
        if (len > 24) {
            byte[] bs = new byte[len - 24];
            for (int i = 0; i < len; i++) {
                if (i > 21 && i < len - 2) {
                    bs[i - 22] = desCode[i];
                }
            }
            return bs;
        }
        return bytes;
    }

    /**
     * 保留modbus协议，移除外部
     */
    public static byte[] removePro(byte[] bytes) {
        if (bytes == null) return null;
        //1.判断是否需要先解密然后再移除外部协议
        byte[] desCode = new byte[0];
        byte[] copyData = new byte[bytes.length];
        System.arraycopy(bytes, 0, copyData, 0, bytes.length);
        if (ModbusUtil.getLocalDebugMode() == AP_MODE) {
            try {
                byte[] dataByte = DatalogApUtil.getEnCode(copyData);
                int len = dataByte.length;
                // 获取数据
                desCode = Arrays.copyOfRange(dataByte, 0, len - 2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            desCode = bytes;
        }

        int pos = 20;
        int len = desCode.length;
        if (len > pos) {
            byte[] bs = new byte[len - pos];
            for (int i = pos; i < len; i++) {
                bs[i - pos] = desCode[i];
            }
            return bs;
        }
        return bytes;
    }

    /**
     * 移除所有协议，只留数据;
     */
    public static byte[] removeFullPro(byte[] bytes) {
        byte[] modbusBytes = removePro(bytes);
        if (modbusBytes == null || modbusBytes.length < 4) return null;
        if (modbusBytes[1] == 6) {
            return Arrays.copyOfRange(modbusBytes, 4, modbusBytes.length - 2);
        } else {
            return Arrays.copyOfRange(modbusBytes, 3, modbusBytes.length - 2);
        }
    }

    /**
     * 解析04:0-125号寄存器:自动刷新使用
     */
    public static void parseMax4T0T125(MaxDataBean been, byte[] bytes) {
        //移除外部协议
        byte[] bs = removePro17(bytes);
        MaxDataDeviceBean deviceBean = getMaxDataDeviceBean(been, bs);

        //100-125
        int ipf = obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 100, 0, 1));
        int maxPower = obtainValueHAndL(MaxWifiParseUtil.subBytesFull(bs, 102, 0, 2));
        int derateMode = obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 104, 0, 1));
        int errCode = obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 105, 0, 1));
        int error1 = obtainValueHAndL(MaxWifiParseUtil.subBytesFull(bs, 106, 0, 2));
        int error2 = obtainValueHAndL(MaxWifiParseUtil.subBytesFull(bs, 108, 0, 2));
        int warmCode = obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 112, 0, 1));
        int powerPer = obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 113, 0, 1));
        int lastTime = obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 114, 0, 1));
        //100-125值设置
        deviceBean.setIpf(ipf);
        deviceBean.setMaxOutPower(maxPower);
        deviceBean.setDerateMode(derateMode);
//        if (errCode != 0){
//            errCode = errCode + 99;
//        }
//        if (warmCode != 0){
//            warmCode = warmCode + 99;
//        }
        been.setErrCode(errCode);
        been.setWarmCode(warmCode);
        been.setError1(error1);
        been.setError2(error2);
        deviceBean.setRealOPowerPercent(powerPer);
        deviceBean.setLastTime(lastTime);
    }

    /**
     * 解析04:0-125号寄存器:自动刷新使用
     */
    public static void parseMax1500V4T0T125(MaxDataBean been, byte[] bytes) {
        //移除外部协议
        byte[] bs = removePro17(bytes);
        MaxDataDeviceBean deviceBean = getMaxDataDeviceBean(been, bs);

        //100-125
        int ipf = obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 100, 0, 1));
        int maxPower = obtainValueHAndL(MaxWifiParseUtil.subBytesFull(bs, 102, 0, 2));
        int derateMode = obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 104, 0, 1));
        int errCode = obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 105, 0, 1));
        int error1 = obtainValueHAndL(MaxWifiParseUtil.subBytesFull(bs, 106, 0, 2));
        int error2 = obtainValueHAndL(MaxWifiParseUtil.subBytesFull(bs, 108, 0, 2));
        int warmCode = obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 112, 0, 1));
        int powerPer = obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 113, 0, 1));
        int lastTime = obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 114, 0, 1));
        //100-125值设置
        deviceBean.setIpf(ipf);
        deviceBean.setMaxOutPower(maxPower);
        deviceBean.setDerateMode(derateMode);
//        if (errCode != 0){
//            errCode = errCode + 99;
//        }
//        if (warmCode != 0){
//            warmCode = warmCode + 99;
//        }
        been.setErrCode(errCode);
        been.setWarmCode(warmCode);
        been.setError1(error1);
        been.setError2(error2);
        deviceBean.setRealOPowerPercent(powerPer);
        deviceBean.setLastTime(lastTime);
    }

    /**
     * 解析04:125-250号寄存器:自动刷新使用
     */
    public static void parseMax4T125T250(MaxDataBean been, byte[] bytes) {
        //移除外部协议
        byte[] bs = removePro17(bytes);
        int mismatchC = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 174, 0, 1));
        int unblanceC = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 175, 0, 1));
        int disconnC = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 176, 0, 1));
        int pidCode = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 177, 0, 1));
        int pidStatus = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 141, 0, 1));
        int iso = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 200, 0, 1));
        int svgStatus = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 206, 0, 1));
        //pv串
        int vPvc1 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 142, 0, 1));
        int vPvc2 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 144, 0, 1));
        int vPvc3 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 146, 0, 1));
        int vPvc4 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 148, 0, 1));
        int vPvc5 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 150, 0, 1));
        int vPvc6 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 152, 0, 1));
        int vPvc7 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 154, 0, 1));
        int vPvc8 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 156, 0, 1));
        int vPvc9 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 158, 0, 1));
        int vPvc10 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 160, 0, 1));
        int vPvc11 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 162, 0, 1));
        int vPvc12 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 164, 0, 1));
        int vPvc13 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 166, 0, 1));
        int vPvc14 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 168, 0, 1));
        int vPvc15 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 170, 0, 1));
        int vPvc16 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 172, 0, 1));

        int aPvc1 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 143, 0, 1));
        int aPvc2 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 145, 0, 1));
        int aPvc3 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 147, 0, 1));
        int aPvc4 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 149, 0, 1));
        int aPvc5 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 151, 0, 1));
        int aPvc6 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 153, 0, 1));
        int aPvc7 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 155, 0, 1));
        int aPvc8 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 157, 0, 1));
        int aPvc9 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 159, 0, 1));
        int aPvc10 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 161, 0, 1));
        int aPvc11 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 163, 0, 1));
        int aPvc12 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 165, 0, 1));
        int aPvc13 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 167, 0, 1));
        int aPvc14 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 169, 0, 1));
        int aPvc15 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 171, 0, 1));
        int aPvc16 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 173, 0, 1));
        //pid
        int vPid1 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 125, 0, 1));
        int vPid2 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 127, 0, 1));
        int vPid3 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 129, 0, 1));
        int vPid4 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 131, 0, 1));
        int vPid5 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 133, 0, 1));
        int vPid6 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 135, 0, 1));
        int vPid7 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 137, 0, 1));
        int vPid8 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 139, 0, 1));

        int aPid1 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 126, 0, 1));
        int aPid2 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 128, 0, 1));
        int aPid3 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 130, 0, 1));
        int aPid4 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 132, 0, 1));
        int aPid5 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 134, 0, 1));
        int aPid6 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 136, 0, 1));
        int aPid7 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 138, 0, 1));
        int aPid8 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 140, 0, 1));
        //SVG/APF
        int ctiAR = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 207, 0, 1));
        int ctiAS = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 208, 0, 1));
        int ctiAT = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 209, 0, 1));

        int ctqVR = obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs, 210, 0, 2));
        int ctqVS = obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs, 212, 0, 2));
        int ctqVT = obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs, 214, 0, 2));

        int ctharAR = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 216, 0, 1));
        int ctharAS = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 217, 0, 1));
        int ctharAT = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 218, 0, 1));

        int compqVR = obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs, 219, 0, 2));
        int compqVS = obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs, 221, 0, 2));
        int compqVT = obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs, 223, 0, 2));

        int compharAR = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 225, 0, 1));
        int compharAS = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 226, 0, 1));
        int compharAT = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 227, 0, 1));

        MaxDataDeviceBean deviceBean = been.getDeviceBeen();
        deviceBean.setMismatchC(mismatchC);
        deviceBean.setUnblanceC(unblanceC);
        deviceBean.setDisConnectC(disconnC);
        deviceBean.setPidErrCode(pidCode);
        deviceBean.setPidStatus(pidStatus);
        deviceBean.setIso(iso);
        deviceBean.setSvgStatus(svgStatus);
        //pv串
        List<String> pvcValues = new ArrayList<>();
        pvcValues.add(Arith.mul(vPvc1, muilt) + "");
        pvcValues.add(Arith.mul(aPvc1, muilt) + "");
        pvcValues.add(Arith.mul(vPvc2, muilt) + "");
        pvcValues.add(Arith.mul(aPvc2, muilt) + "");
        pvcValues.add(Arith.mul(vPvc3, muilt) + "");
        pvcValues.add(Arith.mul(aPvc3, muilt) + "");
        pvcValues.add(Arith.mul(vPvc4, muilt) + "");
        pvcValues.add(Arith.mul(aPvc4, muilt) + "");
        pvcValues.add(Arith.mul(vPvc5, muilt) + "");
        pvcValues.add(Arith.mul(aPvc5, muilt) + "");
        pvcValues.add(Arith.mul(vPvc6, muilt) + "");
        pvcValues.add(Arith.mul(aPvc6, muilt) + "");
        pvcValues.add(Arith.mul(vPvc7, muilt) + "");
        pvcValues.add(Arith.mul(aPvc7, muilt) + "");
        pvcValues.add(Arith.mul(vPvc8, muilt) + "");
        pvcValues.add(Arith.mul(aPvc8, muilt) + "");
        pvcValues.add(Arith.mul(vPvc9, muilt) + "");
        pvcValues.add(Arith.mul(aPvc9, muilt) + "");
        pvcValues.add(Arith.mul(vPvc10, muilt) + "");
        pvcValues.add(Arith.mul(aPvc10, muilt) + "");
        pvcValues.add(Arith.mul(vPvc11, muilt) + "");
        pvcValues.add(Arith.mul(aPvc11, muilt) + "");
        pvcValues.add(Arith.mul(vPvc12, muilt) + "");
        pvcValues.add(Arith.mul(aPvc12, muilt) + "");
        pvcValues.add(Arith.mul(vPvc13, muilt) + "");
        pvcValues.add(Arith.mul(aPvc13, muilt) + "");
        pvcValues.add(Arith.mul(vPvc14, muilt) + "");
        pvcValues.add(Arith.mul(aPvc14, muilt) + "");
        pvcValues.add(Arith.mul(vPvc15, muilt) + "");
        pvcValues.add(Arith.mul(aPvc15, muilt) + "");
        pvcValues.add(Arith.mul(vPvc16, muilt) + "");
        pvcValues.add(Arith.mul(aPvc16, muilt) + "");
        been.setPVCList(pvcValues);
        //pid值
        List<String> pidValues = new ArrayList<>();
        pidValues.add(Arith.mul(vPid1, muilt) + "");
        pidValues.add(Arith.mul(aPid1, muilt) + "");
        pidValues.add(Arith.mul(vPid2, muilt) + "");
        pidValues.add(Arith.mul(aPid2, muilt) + "");
        pidValues.add(Arith.mul(vPid3, muilt) + "");
        pidValues.add(Arith.mul(aPid3, muilt) + "");
        pidValues.add(Arith.mul(vPid4, muilt) + "");
        pidValues.add(Arith.mul(aPid4, muilt) + "");
        pidValues.add(Arith.mul(vPid5, muilt) + "");
        pidValues.add(Arith.mul(aPid5, muilt) + "");
        pidValues.add(Arith.mul(vPid6, muilt) + "");
        pidValues.add(Arith.mul(aPid6, muilt) + "");
        pidValues.add(Arith.mul(vPid7, muilt) + "");
        pidValues.add(Arith.mul(aPid7, muilt) + "");
        pidValues.add(Arith.mul(vPid8, muilt) + "");
        pidValues.add(Arith.mul(aPid8, muilt) + "");
        been.setPIDList(pidValues);
        //svg
        List<String> svgValues = new ArrayList<>();
        svgValues.add(Arith.mul(ctiAR, muilt) + "");
        svgValues.add(Arith.mul(ctqVR, muilt) + "");
        svgValues.add(Arith.mul(ctharAR, muilt) + "");
        svgValues.add(Arith.mul(compqVR, muilt) + "");
        svgValues.add(Arith.mul(compharAR, muilt) + "");
        //显示状态
        int svgStatusNow = deviceBean.getSvgStatus();
        String[] statusStrs = {"None", "SVG Run", "APF Run", "SVG/APF Run"};
        try {
            svgValues.add(statusStrs[svgStatusNow]);
        } catch (Exception e) {
            e.printStackTrace();
            svgValues.add(statusStrs[0]);
        }

        svgValues.add(Arith.mul(ctiAS, muilt) + "");
        svgValues.add(Arith.mul(ctqVS, muilt) + "");
        svgValues.add(Arith.mul(ctharAS, muilt) + "");
        svgValues.add(Arith.mul(compqVS, muilt) + "");
        svgValues.add(Arith.mul(compharAS, muilt) + "");
        svgValues.add("");

        svgValues.add(Arith.mul(ctiAT, muilt) + "");
        svgValues.add(Arith.mul(ctqVT, muilt) + "");
        svgValues.add(Arith.mul(ctharAT, muilt) + "");
        svgValues.add(Arith.mul(compqVT, muilt) + "");
        svgValues.add(Arith.mul(compharAT, muilt) + "");
        svgValues.add("");

        been.setSVGList(svgValues);
    }

    /**
     * 解析04:125-250号寄存器:自动刷新使用
     */
    public static void parseMax1500V4T125T250(MaxDataBean been, byte[] bytes) {
        //移除外部协议
        byte[] bs = removePro17(bytes);
        int mismatchC = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 174, 0, 1));
        int unblanceC = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 175, 0, 1));
        int disconnC = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 176, 0, 1));
        int pidCode = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 177, 0, 1));
        int pidStatus = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 141, 0, 1));
        int iso = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 200, 0, 1));
        int svgStatus = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 206, 0, 1));
        //pv串
        int vPvc1 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 142, 0, 1));
        int vPvc2 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 144, 0, 1));
        int vPvc3 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 146, 0, 1));
        int vPvc4 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 148, 0, 1));
        int vPvc5 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 150, 0, 1));
        int vPvc6 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 152, 0, 1));
        int vPvc7 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 154, 0, 1));
        int vPvc8 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 156, 0, 1));
        int vPvc9 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 158, 0, 1));
        int vPvc10 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 160, 0, 1));
        int vPvc11 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 162, 0, 1));
        int vPvc12 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 164, 0, 1));
        int vPvc13 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 166, 0, 1));
        int vPvc14 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 168, 0, 1));
        int vPvc15 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 170, 0, 1));
        int vPvc16 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 172, 0, 1));

        int aPvc1 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 143, 0, 1));
        int aPvc2 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 145, 0, 1));
        int aPvc3 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 147, 0, 1));
        int aPvc4 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 149, 0, 1));
        int aPvc5 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 151, 0, 1));
        int aPvc6 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 153, 0, 1));
        int aPvc7 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 155, 0, 1));
        int aPvc8 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 157, 0, 1));
        int aPvc9 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 159, 0, 1));
        int aPvc10 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 161, 0, 1));
        int aPvc11 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 163, 0, 1));
        int aPvc12 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 165, 0, 1));
        int aPvc13 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 167, 0, 1));
        int aPvc14 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 169, 0, 1));
        int aPvc15 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 171, 0, 1));
        int aPvc16 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 173, 0, 1));
        //pid
        int vPid1 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 125, 0, 1));
        int vPid2 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 127, 0, 1));
        int vPid3 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 129, 0, 1));
        int vPid4 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 131, 0, 1));
        int vPid5 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 133, 0, 1));
        int vPid6 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 135, 0, 1));
        int vPid7 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 137, 0, 1));
        int vPid8 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 139, 0, 1));

        int aPid1 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 126, 0, 1));
        int aPid2 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 128, 0, 1));
        int aPid3 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 130, 0, 1));
        int aPid4 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 132, 0, 1));
        int aPid5 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 134, 0, 1));
        int aPid6 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 136, 0, 1));
        int aPid7 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 138, 0, 1));
        int aPid8 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 140, 0, 1));
        //SVG/APF
        int ctiAR = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 207, 0, 1));
        int ctiAS = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 208, 0, 1));
        int ctiAT = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 209, 0, 1));

        int ctqVR = obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs, 210, 0, 2));
        int ctqVS = obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs, 212, 0, 2));
        int ctqVT = obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs, 214, 0, 2));

        int ctharAR = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 216, 0, 1));
        int ctharAS = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 217, 0, 1));
        int ctharAT = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 218, 0, 1));

        int compqVR = obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs, 219, 0, 2));
        int compqVS = obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs, 221, 0, 2));
        int compqVT = obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs, 223, 0, 2));

        int compharAR = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 225, 0, 1));
        int compharAS = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 226, 0, 1));
        int compharAT = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 227, 0, 1));

        MaxDataDeviceBean deviceBean = been.getDeviceBeen();
        deviceBean.setMismatchC(mismatchC);
        deviceBean.setUnblanceC(unblanceC);
        deviceBean.setDisConnectC(disconnC);
        deviceBean.setPidErrCode(pidCode);
        deviceBean.setPidStatus(pidStatus);
        deviceBean.setIso(iso);
        deviceBean.setSvgStatus(svgStatus);
        //pv串
        List<String> pvcValues = new ArrayList<>();
        pvcValues.add(Arith.mul(vPvc1, muilt) + "");
        pvcValues.add(Arith.mul(aPvc1, muilt) + "");
        pvcValues.add(Arith.mul(vPvc2, muilt) + "");
        pvcValues.add(Arith.mul(aPvc2, muilt) + "");
        pvcValues.add(Arith.mul(vPvc3, muilt) + "");
        pvcValues.add(Arith.mul(aPvc3, muilt) + "");
        pvcValues.add(Arith.mul(vPvc4, muilt) + "");
        pvcValues.add(Arith.mul(aPvc4, muilt) + "");
        pvcValues.add(Arith.mul(vPvc5, muilt) + "");
        pvcValues.add(Arith.mul(aPvc5, muilt) + "");
        pvcValues.add(Arith.mul(vPvc6, muilt) + "");
        pvcValues.add(Arith.mul(aPvc6, muilt) + "");
        pvcValues.add(Arith.mul(vPvc7, muilt) + "");
        pvcValues.add(Arith.mul(aPvc7, muilt) + "");
        pvcValues.add(Arith.mul(vPvc8, muilt) + "");
        pvcValues.add(Arith.mul(aPvc8, muilt) + "");
        pvcValues.add(Arith.mul(vPvc9, muilt) + "");
        pvcValues.add(Arith.mul(aPvc9, muilt) + "");
        pvcValues.add(Arith.mul(vPvc10, muilt) + "");
        pvcValues.add(Arith.mul(aPvc10, muilt) + "");
        pvcValues.add(Arith.mul(vPvc11, muilt) + "");
        pvcValues.add(Arith.mul(aPvc11, muilt) + "");
        pvcValues.add(Arith.mul(vPvc12, muilt) + "");
        pvcValues.add(Arith.mul(aPvc12, muilt) + "");
        pvcValues.add(Arith.mul(vPvc13, muilt) + "");
        pvcValues.add(Arith.mul(aPvc13, muilt) + "");
        pvcValues.add(Arith.mul(vPvc14, muilt) + "");
        pvcValues.add(Arith.mul(aPvc14, muilt) + "");
        pvcValues.add(Arith.mul(vPvc15, muilt) + "");
        pvcValues.add(Arith.mul(aPvc15, muilt) + "");
        pvcValues.add(Arith.mul(vPvc16, muilt) + "");
        pvcValues.add(Arith.mul(aPvc16, muilt) + "");
        been.setPVCList(pvcValues);
        //pid值
        List<String> pidValues = new ArrayList<>();
        pidValues.add(Arith.mul(vPid1, muilt) + "");
        pidValues.add(Arith.mul(aPid1, muilt) + "");
        pidValues.add(Arith.mul(vPid2, muilt) + "");
        pidValues.add(Arith.mul(aPid2, muilt) + "");
        pidValues.add(Arith.mul(vPid3, muilt) + "");
        pidValues.add(Arith.mul(aPid3, muilt) + "");
        pidValues.add(Arith.mul(vPid4, muilt) + "");
        pidValues.add(Arith.mul(aPid4, muilt) + "");
        pidValues.add(Arith.mul(vPid5, muilt) + "");
        pidValues.add(Arith.mul(aPid5, muilt) + "");
        pidValues.add(Arith.mul(vPid6, muilt) + "");
        pidValues.add(Arith.mul(aPid6, muilt) + "");
        pidValues.add(Arith.mul(vPid7, muilt) + "");
        pidValues.add(Arith.mul(aPid7, muilt) + "");
        pidValues.add(Arith.mul(vPid8, muilt) + "");
        pidValues.add(Arith.mul(aPid8, muilt) + "");
        been.setPIDList(pidValues);
        //svg
        List<String> svgValues = new ArrayList<>();
        svgValues.add(Arith.mul(ctiAR, muilt) + "");
        svgValues.add(Arith.mul(ctqVR, muilt) + "");
        svgValues.add(Arith.mul(ctharAR, muilt) + "");
        svgValues.add(Arith.mul(compqVR, muilt) + "");
        svgValues.add(Arith.mul(compharAR, muilt) + "");
        //显示状态
        int svgStatusNow = deviceBean.getSvgStatus();
        String[] statusStrs = {"None", "SVG Run", "APF Run", "SVG/APF Run"};
        try {
            svgValues.add(statusStrs[svgStatusNow]);
        } catch (Exception e) {
            e.printStackTrace();
            svgValues.add(statusStrs[0]);
        }

        svgValues.add(Arith.mul(ctiAS, muilt) + "");
        svgValues.add(Arith.mul(ctqVS, muilt) + "");
        svgValues.add(Arith.mul(ctharAS, muilt) + "");
        svgValues.add(Arith.mul(compqVS, muilt) + "");
        svgValues.add(Arith.mul(compharAS, muilt) + "");
        svgValues.add("");

        svgValues.add(Arith.mul(ctiAT, muilt) + "");
        svgValues.add(Arith.mul(ctqVT, muilt) + "");
        svgValues.add(Arith.mul(ctharAT, muilt) + "");
        svgValues.add(Arith.mul(compqVT, muilt) + "");
        svgValues.add(Arith.mul(compharAT, muilt) + "");
        svgValues.add("");

        been.setSVGList(svgValues);
    }

    /**
     * 解析04:125-250号寄存器:自动刷新使用
     */
    public static void parseMax04T875T999(MaxDataBean been, byte[] bytes) {
        //移除外部协议
        byte[] bs = removePro17(bytes);
        //pv电压电流电量
        int vPv9 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 875, 0, 1));
        int vPv10 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 879, 0, 1));
        int vPv11 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 883, 0, 1));
        int vPv12 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 887, 0, 1));
        int vPv13 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 891, 0, 1));
        int vPv14 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 895, 0, 1));
        int vPv15 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 899, 0, 1));
        int vPv16 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 903, 0, 1));

        int aPv9 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 876, 0, 1));
        int aPv10 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 880, 0, 1));
        int aPv11 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 884, 0, 1));
        int aPv12 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 888, 0, 1));
        int aPv13 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 892, 0, 1));
        int aPv14 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 896, 0, 1));
        int aPv15 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 900, 0, 1));
        int aPv16 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 904, 0, 1));
        //pv串
        int vPvc17 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 955, 0, 1));
        int vPvc18 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 957, 0, 1));
        int vPvc19 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 959, 0, 1));
        int vPvc20 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 961, 0, 1));
        int vPvc21 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 963, 0, 1));
        int vPvc22 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 965, 0, 1));
        int vPvc23 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 967, 0, 1));
        int vPvc24 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 969, 0, 1));
        int vPvc25 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 971, 0, 1));
        int vPvc26 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 973, 0, 1));
        int vPvc27 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 975, 0, 1));
        int vPvc28 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 977, 0, 1));
        int vPvc29 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 979, 0, 1));
        int vPvc30 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 981, 0, 1));
        int vPvc31 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 983, 0, 1));
        int vPvc32 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 985, 0, 1));

        int aPvc17 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 956, 0, 1));
        int aPvc18 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 958, 0, 1));
        int aPvc19 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 960, 0, 1));
        int aPvc20 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 962, 0, 1));
        int aPvc21 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 964, 0, 1));
        int aPvc22 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 966, 0, 1));
        int aPvc23 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 968, 0, 1));
        int aPvc24 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 970, 0, 1));
        int aPvc25 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 972, 0, 1));
        int aPvc26 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 974, 0, 1));
        int aPvc27 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 976, 0, 1));
        int aPvc28 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 978, 0, 1));
        int aPvc29 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 980, 0, 1));
        int aPvc30 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 982, 0, 1));
        int aPvc31 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 984, 0, 1));
        int aPvc32 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 986, 0, 1));
        //pid
        int vPid9 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 939, 0, 1));
        int vPid10 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 941, 0, 1));
        int vPid11 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 943, 0, 1));
        int vPid12 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 945, 0, 1));
        int vPid13 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 947, 0, 1));
        int vPid14 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 949, 0, 1));
        int vPid15 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 951, 0, 1));
        int vPid16 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 953, 0, 1));

        int aPid9 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 940, 0, 1));
        int aPid10 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 942, 0, 1));
        int aPid11 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 944, 0, 1));
        int aPid12 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 946, 0, 1));
        int aPid13 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 948, 0, 1));
        int aPid14 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 950, 0, 1));
        int aPid15 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 952, 0, 1));
        int aPid16 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 954, 0, 1));
        //pv值
        List<String> pvValues = been.getPVList();
        if (pvValues == null || pvValues.size() < 16) {
            pvValues = new ArrayList<>();
            for (int i = 0; i < 32; i++) {
                pvValues.add("");
            }
        }
        if (pvValues.size() < 32) {
            pvValues.add(Arith.mul(vPv9, muilt) + "");
            pvValues.add(Arith.mul(aPv9, muilt) + "");
            pvValues.add(Arith.mul(vPv10, muilt) + "");
            pvValues.add(Arith.mul(aPv10, muilt) + "");
            pvValues.add(Arith.mul(vPv11, muilt) + "");
            pvValues.add(Arith.mul(aPv11, muilt) + "");
            pvValues.add(Arith.mul(vPv12, muilt) + "");
            pvValues.add(Arith.mul(aPv12, muilt) + "");
            pvValues.add(Arith.mul(vPv13, muilt) + "");
            pvValues.add(Arith.mul(aPv13, muilt) + "");
            pvValues.add(Arith.mul(vPv14, muilt) + "");
            pvValues.add(Arith.mul(aPv14, muilt) + "");
            pvValues.add(Arith.mul(vPv15, muilt) + "");
            pvValues.add(Arith.mul(aPv15, muilt) + "");
            pvValues.add(Arith.mul(vPv16, muilt) + "");
            pvValues.add(Arith.mul(aPv16, muilt) + "");
        } else {
            pvValues.set(16, Arith.mul(vPv9, muilt) + "");
            pvValues.set(17, Arith.mul(aPv9, muilt) + "");
            pvValues.set(18, Arith.mul(vPv10, muilt) + "");
            pvValues.set(19, Arith.mul(aPv10, muilt) + "");
            pvValues.set(20, Arith.mul(vPv11, muilt) + "");
            pvValues.set(21, Arith.mul(aPv11, muilt) + "");
            pvValues.set(22, Arith.mul(vPv12, muilt) + "");
            pvValues.set(23, Arith.mul(aPv12, muilt) + "");
            pvValues.set(24, Arith.mul(vPv13, muilt) + "");
            pvValues.set(25, Arith.mul(aPv13, muilt) + "");
            pvValues.set(26, Arith.mul(vPv14, muilt) + "");
            pvValues.set(27, Arith.mul(aPv14, muilt) + "");
            pvValues.set(28, Arith.mul(vPv15, muilt) + "");
            pvValues.set(29, Arith.mul(aPv15, muilt) + "");
            pvValues.set(30, Arith.mul(vPv16, muilt) + "");
            pvValues.set(31, Arith.mul(aPv16, muilt) + "");
        }
        been.setPVList(pvValues);
        //pv串
        List<String> pvcValues = been.getPVCList();
        if (pvcValues == null || pvcValues.size() < 32) {
            pvcValues = new ArrayList<>();
            for (int i = 0; i < 32; i++) {
                pvcValues.add("");
            }
        }
        if (pvcValues.size() < 64) {
            pvcValues.add(Arith.mul(vPvc17, muilt) + "");
            pvcValues.add(Arith.mul(aPvc17, muilt) + "");
            pvcValues.add(Arith.mul(vPvc18, muilt) + "");
            pvcValues.add(Arith.mul(aPvc18, muilt) + "");
            pvcValues.add(Arith.mul(vPvc19, muilt) + "");
            pvcValues.add(Arith.mul(aPvc19, muilt) + "");
            pvcValues.add(Arith.mul(vPvc20, muilt) + "");
            pvcValues.add(Arith.mul(aPvc20, muilt) + "");
            pvcValues.add(Arith.mul(vPvc21, muilt) + "");
            pvcValues.add(Arith.mul(aPvc21, muilt) + "");
            pvcValues.add(Arith.mul(vPvc22, muilt) + "");
            pvcValues.add(Arith.mul(aPvc22, muilt) + "");
            pvcValues.add(Arith.mul(vPvc23, muilt) + "");
            pvcValues.add(Arith.mul(aPvc23, muilt) + "");
            pvcValues.add(Arith.mul(vPvc24, muilt) + "");
            pvcValues.add(Arith.mul(aPvc24, muilt) + "");
            pvcValues.add(Arith.mul(vPvc25, muilt) + "");
            pvcValues.add(Arith.mul(aPvc25, muilt) + "");
            pvcValues.add(Arith.mul(vPvc26, muilt) + "");
            pvcValues.add(Arith.mul(aPvc26, muilt) + "");
            pvcValues.add(Arith.mul(vPvc27, muilt) + "");
            pvcValues.add(Arith.mul(aPvc27, muilt) + "");
            pvcValues.add(Arith.mul(vPvc28, muilt) + "");
            pvcValues.add(Arith.mul(aPvc28, muilt) + "");
            pvcValues.add(Arith.mul(vPvc29, muilt) + "");
            pvcValues.add(Arith.mul(aPvc29, muilt) + "");
            pvcValues.add(Arith.mul(vPvc30, muilt) + "");
            pvcValues.add(Arith.mul(aPvc30, muilt) + "");
            pvcValues.add(Arith.mul(vPvc31, muilt) + "");
            pvcValues.add(Arith.mul(aPvc31, muilt) + "");
            pvcValues.add(Arith.mul(vPvc32, muilt) + "");
            pvcValues.add(Arith.mul(aPvc32, muilt) + "");
        } else {
            pvcValues.set(32, Arith.mul(vPvc17, muilt) + "");
            pvcValues.set(33, Arith.mul(aPvc17, muilt) + "");
            pvcValues.set(34, Arith.mul(vPvc18, muilt) + "");
            pvcValues.set(35, Arith.mul(aPvc18, muilt) + "");
            pvcValues.set(36, Arith.mul(vPvc19, muilt) + "");
            pvcValues.set(37, Arith.mul(aPvc19, muilt) + "");
            pvcValues.set(38, Arith.mul(vPvc20, muilt) + "");
            pvcValues.set(39, Arith.mul(aPvc20, muilt) + "");
            pvcValues.set(40, Arith.mul(vPvc21, muilt) + "");
            pvcValues.set(41, Arith.mul(aPvc21, muilt) + "");
            pvcValues.set(42, Arith.mul(vPvc22, muilt) + "");
            pvcValues.set(43, Arith.mul(aPvc22, muilt) + "");
            pvcValues.set(44, Arith.mul(vPvc23, muilt) + "");
            pvcValues.set(45, Arith.mul(aPvc23, muilt) + "");
            pvcValues.set(46, Arith.mul(vPvc24, muilt) + "");
            pvcValues.set(47, Arith.mul(aPvc24, muilt) + "");
            pvcValues.set(48, Arith.mul(vPvc25, muilt) + "");
            pvcValues.set(49, Arith.mul(aPvc25, muilt) + "");
            pvcValues.set(50, Arith.mul(vPvc26, muilt) + "");
            pvcValues.set(51, Arith.mul(aPvc26, muilt) + "");
            pvcValues.set(52, Arith.mul(vPvc27, muilt) + "");
            pvcValues.set(53, Arith.mul(aPvc27, muilt) + "");
            pvcValues.set(54, Arith.mul(vPvc28, muilt) + "");
            pvcValues.set(55, Arith.mul(aPvc28, muilt) + "");
            pvcValues.set(56, Arith.mul(vPvc29, muilt) + "");
            pvcValues.set(57, Arith.mul(aPvc29, muilt) + "");
            pvcValues.set(58, Arith.mul(vPvc30, muilt) + "");
            pvcValues.set(59, Arith.mul(aPvc30, muilt) + "");
            pvcValues.set(60, Arith.mul(vPvc31, muilt) + "");
            pvcValues.set(61, Arith.mul(aPvc31, muilt) + "");
            pvcValues.set(62, Arith.mul(vPvc32, muilt) + "");
            pvcValues.set(63, Arith.mul(aPvc32, muilt) + "");
        }
        been.setPVCList(pvcValues);
        //pid值
        List<String> pidValues = been.getPIDList();
        if (pidValues == null || pidValues.size() < 16) {
            pidValues = new ArrayList<>();
            for (int i = 0; i < 16; i++) {
                pidValues.add("");
            }
        }
        if (pidValues.size() < 32) {
            pidValues.add(Arith.mul(vPid9, muilt) + "");
            pidValues.add(Arith.mul(aPid9, muilt) + "");
            pidValues.add(Arith.mul(vPid10, muilt) + "");
            pidValues.add(Arith.mul(aPid10, muilt) + "");
            pidValues.add(Arith.mul(vPid11, muilt) + "");
            pidValues.add(Arith.mul(aPid11, muilt) + "");
            pidValues.add(Arith.mul(vPid12, muilt) + "");
            pidValues.add(Arith.mul(aPid12, muilt) + "");
            pidValues.add(Arith.mul(vPid13, muilt) + "");
            pidValues.add(Arith.mul(aPid13, muilt) + "");
            pidValues.add(Arith.mul(vPid14, muilt) + "");
            pidValues.add(Arith.mul(aPid14, muilt) + "");
            pidValues.add(Arith.mul(vPid15, muilt) + "");
            pidValues.add(Arith.mul(aPid15, muilt) + "");
            pidValues.add(Arith.mul(vPid16, muilt) + "");
            pidValues.add(Arith.mul(aPid16, muilt) + "");
        } else {
            pidValues.set(16, Arith.mul(vPid9, muilt) + "");
            pidValues.set(17, Arith.mul(aPid9, muilt) + "");
            pidValues.set(18, Arith.mul(vPid10, muilt) + "");
            pidValues.set(19, Arith.mul(aPid10, muilt) + "");
            pidValues.set(20, Arith.mul(vPid11, muilt) + "");
            pidValues.set(21, Arith.mul(aPid11, muilt) + "");
            pidValues.set(22, Arith.mul(vPid12, muilt) + "");
            pidValues.set(23, Arith.mul(aPid12, muilt) + "");
            pidValues.set(24, Arith.mul(vPid13, muilt) + "");
            pidValues.set(25, Arith.mul(aPid13, muilt) + "");
            pidValues.set(26, Arith.mul(vPid14, muilt) + "");
            pidValues.set(27, Arith.mul(aPid14, muilt) + "");
            pidValues.set(28, Arith.mul(vPid15, muilt) + "");
            pidValues.set(29, Arith.mul(aPid15, muilt) + "");
            pidValues.set(30, Arith.mul(vPid16, muilt) + "");
            pidValues.set(31, Arith.mul(aPid16, muilt) + "");
        }
        been.setPIDList(pidValues);
    }

    @NonNull
    private static MaxDataDeviceBean getMaxDataDeviceBean(MaxDataBean been, byte[] bs) {
        //当前功率
        int normalPower = MaxWifiParseUtil.obtainRegistValueHOrL(1, bs[70], bs[71])
                + MaxWifiParseUtil.obtainRegistValueHOrL(0, bs[72], bs[73]);
        //今日发电
        int todayE = MaxWifiParseUtil.obtainRegistValueHOrL(1, bs[106], bs[107])
                + MaxWifiParseUtil.obtainRegistValueHOrL(0, bs[108], bs[109]);
        int totalE = MaxWifiParseUtil.obtainRegistValueHOrL(1, bs[110], bs[111])
                + MaxWifiParseUtil.obtainRegistValueHOrL(0, bs[112], bs[113]);
        //pv输出功率
        int pvInPower = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs, 1, 0, 2));
        //状态
        int status = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 0, 0, 1));
        //pv总电量
        int pvTotalE = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs, 91, 0, 2));
        //温度
        int deviceTemp = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 93, 0, 1));
        int envTemp = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 94, 0, 1));
        int boostTemp = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 95, 0, 1));
        //电压
        int pBusV = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 98, 0, 1));
        int nBusV = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 99, 0, 1));
        //总工作时长
        int totoaTime = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs, 57, 0, 2));


        //电网频率
        int gridFre = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 37, 0, 1));
        //pv电压电流电量
        int vPv1 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 3, 0, 1));
        int vPv2 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 7, 0, 1));
        int vPv3 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 11, 0, 1));
        int vPv4 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 15, 0, 1));
        int vPv5 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 19, 0, 1));
        int vPv6 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 23, 0, 1));
        int vPv7 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 27, 0, 1));
        int vPv8 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 31, 0, 1));

        int aPv1 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 4, 0, 1));
        int aPv2 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 8, 0, 1));
        int aPv3 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 12, 0, 1));
        int aPv4 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 16, 0, 1));
        int aPv5 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 20, 0, 1));
        int aPv6 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 24, 0, 1));
        int aPv7 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 28, 0, 1));
        int aPv8 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 32, 0, 1));

        int vR = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 50, 0, 1));
        int vS = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 51, 0, 1));
        int vT = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 52, 0, 1));

        int aR = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 39, 0, 1));
        int aS = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 43, 0, 1));
        int aT = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 47, 0, 1));

        int pR = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs, 40, 0, 2));
        int pS = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs, 44, 0, 2));
        int pT = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs, 48, 0, 2));
//设置值
        been.setNormalPower(normalPower);
        been.setTodayEnergy(todayE);
        been.setTotalEnergy(totalE);
        been.setStatus(status);
        //device值
        MaxDataDeviceBean deviceBean = been.getDeviceBeen();
        deviceBean.setPvInPower(pvInPower);
        deviceBean.setPvTotalEnergy(pvTotalE);
        deviceBean.setDeviceTemp(deviceTemp);
        deviceBean.setEnvTemp(envTemp);
        deviceBean.setBoostTemp(boostTemp);
        deviceBean.setpBusV(pBusV);
        deviceBean.setnBusV(nBusV);
        deviceBean.setTotalTime(totoaTime);
        deviceBean.setGridFre(gridFre);
        //pv值
        List<String> pvValues = new ArrayList<>();
        pvValues.add(Arith.mul(vPv1, muilt) + "");
        pvValues.add(Arith.mul(aPv1, muilt) + "");
        pvValues.add(Arith.mul(vPv2, muilt) + "");
        pvValues.add(Arith.mul(aPv2, muilt) + "");
        pvValues.add(Arith.mul(vPv3, muilt) + "");
        pvValues.add(Arith.mul(aPv3, muilt) + "");
        pvValues.add(Arith.mul(vPv4, muilt) + "");
        pvValues.add(Arith.mul(aPv4, muilt) + "");
        pvValues.add(Arith.mul(vPv5, muilt) + "");
        pvValues.add(Arith.mul(aPv5, muilt) + "");
        pvValues.add(Arith.mul(vPv6, muilt) + "");
        pvValues.add(Arith.mul(aPv6, muilt) + "");
        pvValues.add(Arith.mul(vPv7, muilt) + "");
        pvValues.add(Arith.mul(aPv7, muilt) + "");
        pvValues.add(Arith.mul(vPv8, muilt) + "");
        pvValues.add(Arith.mul(aPv8, muilt) + "");
        been.setPVList(pvValues);
        //Ac值
        List<String> acValues = new ArrayList<>();
        acValues.add(Arith.mul(vR, muilt) + "");
        acValues.add(deviceBean.getGridFre());
        acValues.add(Arith.mul(aR, muilt) + "");
        acValues.add(Arith.mul(pR, muilt) + "");
        acValues.add(deviceBean.getIpf());

        acValues.add(Arith.mul(vS, muilt) + "");
        acValues.add(deviceBean.getGridFre());
        acValues.add(Arith.mul(aS, muilt) + "");
        acValues.add(Arith.mul(pS, muilt) + "");
        acValues.add("");

        acValues.add(Arith.mul(vT, muilt) + "");
        acValues.add(deviceBean.getGridFre());
        acValues.add(Arith.mul(aT, muilt) + "");
        acValues.add(Arith.mul(pT, muilt) + "");
        acValues.add("");
        been.setACList(acValues);
        return deviceBean;
    }

    @NonNull
    private static MaxDataDeviceBean getMax1500VDataDeviceBean(MaxDataBean been, byte[] bs) {
        //当前功率
        int normalPower = MaxWifiParseUtil.obtainRegistValueHOrL(1, bs[70], bs[71])
                + MaxWifiParseUtil.obtainRegistValueHOrL(0, bs[72], bs[73]);
        //今日发电
        int todayE = MaxWifiParseUtil.obtainRegistValueHOrL(1, bs[106], bs[107])
                + MaxWifiParseUtil.obtainRegistValueHOrL(0, bs[108], bs[109]);
        int totalE = MaxWifiParseUtil.obtainRegistValueHOrL(1, bs[110], bs[111])
                + MaxWifiParseUtil.obtainRegistValueHOrL(0, bs[112], bs[113]);
        //pv输出功率
        int pvInPower = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs, 1, 0, 2));
        //状态
        int status = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 0, 0, 1));
        //pv总电量
        int pvTotalE = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs, 91, 0, 2));
        //温度
        int deviceTemp = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 93, 0, 1));
        int envTemp = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 94, 0, 1));
        int boostTemp = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 95, 0, 1));
        //电压
        int pBusV = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 98, 0, 1));
        int nBusV = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 99, 0, 1));
        //总工作时长
        int totoaTime = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs, 57, 0, 2));


        //电网频率
        int gridFre = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 37, 0, 1));
        //pv电压电流电量
        int vPv1 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 3, 0, 1));
        int vPv2 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 7, 0, 1));
        int vPv3 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 11, 0, 1));
        int vPv4 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 15, 0, 1));
        int vPv5 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 19, 0, 1));
        int vPv6 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 23, 0, 1));
        int vPv7 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 27, 0, 1));
        int vPv8 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 31, 0, 1));

        int aPv1 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 4, 0, 1));
        int aPv2 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 8, 0, 1));
        int aPv3 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 12, 0, 1));
        int aPv4 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 16, 0, 1));
        int aPv5 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 20, 0, 1));
        int aPv6 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 24, 0, 1));
        int aPv7 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 28, 0, 1));
        int aPv8 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 32, 0, 1));

        int vR = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 50, 0, 1));
        int vS = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 51, 0, 1));
        int vT = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 52, 0, 1));

        int aR = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 39, 0, 1));
        int aS = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 43, 0, 1));
        int aT = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 47, 0, 1));

        int pR = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs, 40, 0, 2));
        int pS = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs, 44, 0, 2));
        int pT = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs, 48, 0, 2));
//设置值
        been.setNormalPower(normalPower);
        been.setTodayEnergy(todayE);
        been.setTotalEnergy(totalE);
        been.setStatus(status);
        //device值
        MaxDataDeviceBean deviceBean = been.getDeviceBeen();
        deviceBean.setPvInPower(pvInPower);
        deviceBean.setPvTotalEnergy(pvTotalE);
        deviceBean.setDeviceTemp(deviceTemp);
        deviceBean.setEnvTemp(envTemp);
        deviceBean.setBoostTemp(boostTemp);
        deviceBean.setpBusV(pBusV);
        deviceBean.setnBusV(nBusV);
        deviceBean.setTotalTime(totoaTime);
        deviceBean.setGridFre(gridFre);
        //pv值
        List<String> pvValues = been.getPVList();
        if (pvValues == null || pvValues.size() < 16) {
            pvValues = new ArrayList<>();
            pvValues.add(Arith.mul(vPv1, muilt) + "");
            pvValues.add(Arith.mul(aPv1, muilt) + "");
            pvValues.add(Arith.mul(vPv2, muilt) + "");
            pvValues.add(Arith.mul(aPv2, muilt) + "");
            pvValues.add(Arith.mul(vPv3, muilt) + "");
            pvValues.add(Arith.mul(aPv3, muilt) + "");
            pvValues.add(Arith.mul(vPv4, muilt) + "");
            pvValues.add(Arith.mul(aPv4, muilt) + "");
            pvValues.add(Arith.mul(vPv5, muilt) + "");
            pvValues.add(Arith.mul(aPv5, muilt) + "");
            pvValues.add(Arith.mul(vPv6, muilt) + "");
            pvValues.add(Arith.mul(aPv6, muilt) + "");
            pvValues.add(Arith.mul(vPv7, muilt) + "");
            pvValues.add(Arith.mul(aPv7, muilt) + "");
            pvValues.add(Arith.mul(vPv8, muilt) + "");
            pvValues.add(Arith.mul(aPv8, muilt) + "");
            been.setPVList(pvValues);
        } else {
            pvValues.set(0, Arith.mul(vPv1, muilt) + "");
            pvValues.set(1, Arith.mul(aPv1, muilt) + "");
            pvValues.set(2, Arith.mul(vPv2, muilt) + "");
            pvValues.set(3, Arith.mul(aPv2, muilt) + "");
            pvValues.set(4, Arith.mul(vPv3, muilt) + "");
            pvValues.set(5, Arith.mul(aPv3, muilt) + "");
            pvValues.set(6, Arith.mul(vPv4, muilt) + "");
            pvValues.set(7, Arith.mul(aPv4, muilt) + "");
            pvValues.set(8, Arith.mul(vPv5, muilt) + "");
            pvValues.set(9, Arith.mul(aPv5, muilt) + "");
            pvValues.set(10, Arith.mul(vPv6, muilt) + "");
            pvValues.set(11, Arith.mul(aPv6, muilt) + "");
            pvValues.set(12, Arith.mul(vPv7, muilt) + "");
            pvValues.set(13, Arith.mul(aPv7, muilt) + "");
            pvValues.set(14, Arith.mul(vPv8, muilt) + "");
            pvValues.set(15, Arith.mul(aPv8, muilt) + "");
        }

        //Ac值
        List<String> acValues = new ArrayList<>();
        acValues.add(Arith.mul(vR, muilt) + "");
        acValues.add(deviceBean.getGridFre());
        acValues.add(Arith.mul(aR, muilt) + "");
        acValues.add(Arith.mul(pR, muilt) + "");
        acValues.add(deviceBean.getIpf());

        acValues.add(Arith.mul(vS, muilt) + "");
        acValues.add(deviceBean.getGridFre());
        acValues.add(Arith.mul(aS, muilt) + "");
        acValues.add(Arith.mul(pS, muilt) + "");
        acValues.add("");

        acValues.add(Arith.mul(vT, muilt) + "");
        acValues.add(deviceBean.getGridFre());
        acValues.add(Arith.mul(aT, muilt) + "");
        acValues.add(Arith.mul(pT, muilt) + "");
        acValues.add("");
        been.setACList(acValues);
        return deviceBean;
    }

    @NonNull
    private static MaxDataDeviceBean getMaxDataDeviceBeanOldInv(MaxDataBean been, byte[] bs) {
        //当前功率
        int normalPower = obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs, 11, 0, 2));
        int power = obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs, 67, 0, 2));
        //今日发电
        int todayE = obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs, 26, 0, 2));
        int totalE = obtainValueHAndL(MaxWifiParseUtil.subBytes125(bs, 28, 0, 2));
        //pv输出功率
//        int  pvInPower = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs,1,0,2));
        //状态
        int status = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 0, 0, 1));
        //故障和警告
        int errCode = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 40, 0, 1));
        int warmCode = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 64, 0, 1));
        //pv总电量
//        int  pvTotalE = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs,91,0,2));
        //温度
//        int  envTemp = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,93,0,1));
        int deviceTemp = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 32, 0, 1));
//        int  boostTemp = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,95,0,1));
        //电压
        int pBusV = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 42, 0, 1));
        int nBusV = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 43, 0, 1));
        int pidErr = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 89, 0, 1));
        int pidStatus = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 117, 0, 1));
        int ipf = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 45, 0, 1));
        //总工作时长
//        int  totoaTime = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs,57,0,2));


        //电网频率
        int gridFre = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 13, 0, 1));
        //pv电压电流电量
        int vPv1 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 3, 0, 1));
        int vPv2 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 7, 0, 1));
        int vPv3 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 120, 0, 1));

        int aPv1 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 4, 0, 1));
        int aPv2 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 8, 0, 1));
        int aPv3 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 121, 0, 1));
        //ac电压、电流、功率
        int vR = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 14, 0, 1));
        int vS = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 18, 0, 1));
        int vT = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 22, 0, 1));

        int aR = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 15, 0, 1));
        int aS = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 19, 0, 1));
        int aT = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 23, 0, 1));

        int pR = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs, 16, 0, 2));
        int pS = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs, 20, 0, 2));
        int pT = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs, 24, 0, 2));
        //pv串
        int vPvc1 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 70, 0, 1));
        int vPvc2 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 72, 0, 1));
        int vPvc3 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 74, 0, 1));
        int vPvc4 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 76, 0, 1));
        int vPvc5 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 78, 0, 1));
        int vPvc6 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 80, 0, 1));
        int vPvc7 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 82, 0, 1));
        int vPvc8 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 84, 0, 1));

        int aPvc1 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 71, 0, 1));
        int aPvc2 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 73, 0, 1));
        int aPvc3 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 75, 0, 1));
        int aPvc4 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 77, 0, 1));
        int aPvc5 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 79, 0, 1));
        int aPvc6 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 81, 0, 1));
        int aPvc7 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 83, 0, 1));
        int aPvc8 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 85, 0, 1));
        //pid
        int vPid1 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 115, 0, 1));
        int vPid2 = obtainValueOne(MaxWifiParseUtil.subBytes125(bs, 118, 0, 1));

        int aPid1 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 116, 0, 1));
        int aPid2 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes125(bs, 119, 0, 1));
//设置值
        been.setNormalPower(normalPower);
        been.setTotalPower(power);
        been.setTodayEnergy(todayE);
        been.setTotalEnergy(totalE);
        been.setStatus(status);
        been.setErrCode(errCode);
        been.setWarmCode(warmCode);
        //device值
        MaxDataDeviceBean deviceBean = been.getDeviceBeen();
//        deviceBean.setPvInPower(pvInPower);
//        deviceBean.setPvTotalEnergy(pvTotalE);
        deviceBean.setDeviceTemp(deviceTemp);
//        deviceBean.setEnvTemp(envTemp);
//        deviceBean.setBoostTemp(boostTemp);
        deviceBean.setpBusV(pBusV);
        deviceBean.setnBusV(nBusV);
//        deviceBean.setTotalTime(totoaTime);
        deviceBean.setGridFre(gridFre);
        deviceBean.setPidErrCode(pidErr);
        deviceBean.setPidStatus(pidStatus);
        deviceBean.setIpf(ipf);
        //pv值
        List<String> pvValues = new ArrayList<>();
        pvValues.add(Arith.mul(vPv1, muilt) + "");
        pvValues.add(Arith.mul(aPv1, muilt) + "");
        pvValues.add(Arith.mul(vPv2, muilt) + "");
        pvValues.add(Arith.mul(aPv2, muilt) + "");
        pvValues.add(Arith.mul(vPv3, muilt) + "");
        pvValues.add(Arith.mul(aPv3, muilt) + "");
        been.setPVList(pvValues);
        //pv串
        List<String> pvcValues = new ArrayList<>();
        pvcValues.add(Arith.mul(vPvc1, muilt) + "");
        pvcValues.add(Arith.mul(aPvc1, muilt) + "");
        pvcValues.add(Arith.mul(vPvc2, muilt) + "");
        pvcValues.add(Arith.mul(aPvc2, muilt) + "");
        pvcValues.add(Arith.mul(vPvc3, muilt) + "");
        pvcValues.add(Arith.mul(aPvc3, muilt) + "");
        pvcValues.add(Arith.mul(vPvc4, muilt) + "");
        pvcValues.add(Arith.mul(aPvc4, muilt) + "");
        pvcValues.add(Arith.mul(vPvc5, muilt) + "");
        pvcValues.add(Arith.mul(aPvc5, muilt) + "");
        pvcValues.add(Arith.mul(vPvc6, muilt) + "");
        pvcValues.add(Arith.mul(aPvc6, muilt) + "");
        pvcValues.add(Arith.mul(vPvc7, muilt) + "");
        pvcValues.add(Arith.mul(aPvc7, muilt) + "");
        pvcValues.add(Arith.mul(vPvc8, muilt) + "");
        pvcValues.add(Arith.mul(aPvc8, muilt) + "");
        been.setPVCList(pvcValues);
        //Ac值
        List<String> acValues = new ArrayList<>();
        acValues.add(Arith.mul(vR, muilt) + "");
        acValues.add(deviceBean.getGridFre());
        acValues.add(Arith.mul(aR, muilt) + "");
        acValues.add(Arith.mul(pR, muilt) + "");
        acValues.add(deviceBean.getIpf());

        acValues.add(Arith.mul(vS, muilt) + "");
        acValues.add(deviceBean.getGridFre());
        acValues.add(Arith.mul(aS, muilt) + "");
        acValues.add(Arith.mul(pS, muilt) + "");
        acValues.add("");

        acValues.add(Arith.mul(vT, muilt) + "");
        acValues.add(deviceBean.getGridFre());
        acValues.add(Arith.mul(aT, muilt) + "");
        acValues.add(Arith.mul(pT, muilt) + "");
        acValues.add("");
        been.setACList(acValues);
        //pid值
        List<String> pidValues = new ArrayList<>();
        pidValues.add(Arith.mul(vPid1, muilt) + "");
        pidValues.add(Arith.mul(aPid1, muilt) + "");
        pidValues.add(Arith.mul(vPid2, muilt) + "");
        pidValues.add(Arith.mul(aPid2, muilt) + "");
        been.setPIDList(pidValues);
        return deviceBean;
    }

    public static MaxDataDeviceBean parse04Input0T44OldInv(MaxDataBean been, byte[] bytes) {
        //移除外部协议
        byte[] bs = removePro17(bytes);
        //当前功率
        int normalPower = obtainValueHAndL(MaxWifiParseUtil.subBytes45(bs, 11, 0, 2));
//        int power = obtainValueHAndL(MaxWifiParseUtil.subBytes45(bs,67,0,2));
        //今日发电
        int todayE = obtainValueHAndL(MaxWifiParseUtil.subBytes45(bs, 26, 0, 2));
        int totalE = obtainValueHAndL(MaxWifiParseUtil.subBytes45(bs, 28, 0, 2));
        //pv输出功率
//        int  pvInPower = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs,1,0,2));
        //状态
        int status = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 0, 0, 1));
        //故障和警告
        int errCode = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 40, 0, 1));
//        int  warmCode = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,64,0,1));
        //pv总电量
//        int  pvTotalE = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs,91,0,2));
        //温度
//        int  envTemp = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,93,0,1));
        int deviceTemp = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 32, 0, 1));
//        int  boostTemp = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,95,0,1));
        //电压
        int pBusV = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 42, 0, 1));
        int nBusV = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 43, 0, 1));
//        int  pidErr = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,89,0,1));
//        int  pidStatus = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes45(bs,117,0,1));
//        int ipf = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,45,0,1));
        //总工作时长
//        int  totoaTime = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs,57,0,2));


        //电网频率
        int gridFre = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 13, 0, 1));
        //pv电压电流电量
        int vPv1 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 3, 0, 1));
        int vPv2 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 7, 0, 1));
//        int  vPv3 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes45(bs,120,0,1));

        int aPv1 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 4, 0, 1));
        int aPv2 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 8, 0, 1));
//        int  aPv3 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes45(bs,121,0,1));
        //ac电压、电流、功率
        int vR = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 14, 0, 1));
        int vS = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 18, 0, 1));
        int vT = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 22, 0, 1));

        int aR = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 15, 0, 1));
        int aS = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 19, 0, 1));
        int aT = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 23, 0, 1));

        int pR = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs, 16, 0, 2));
        int pS = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs, 20, 0, 2));
        int pT = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs, 24, 0, 2));
        //pv串
//        int vPvc1 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,70,0,1));
//        int vPvc2 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,72,0,1));
//        int vPvc3 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,74,0,1));
//        int vPvc4 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,76,0,1));
//        int vPvc5 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,78,0,1));
//        int vPvc6 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,80,0,1));
//        int vPvc7 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,82,0,1));
//        int vPvc8 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,84,0,1));

//        int aPvc1 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs,71,0,1));
//        int aPvc2 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs,73,0,1));
//        int aPvc3 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs,75,0,1));
//        int aPvc4 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs,77,0,1));
//        int aPvc5 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs,79,0,1));
//        int aPvc6 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs,81,0,1));
//        int aPvc7 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs,83,0,1));
//        int aPvc8 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs,85,0,1));
        //pid
//        int vPid1 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes45(bs,115,0,1));
//        int vPid2 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes45(bs,118,0,1));

//        int aPid1 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes45(bs,116,0,1));
//        int aPid2 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes45(bs,119,0,1));
//设置值
        been.setNormalPower(normalPower);
//        been.setTotalPower(power);
        been.setTodayEnergy(todayE);
        been.setTotalEnergy(totalE);
        been.setStatus(status);
        been.setErrCode(errCode);
//        been.setWarmCode(warmCode);
        //device值
        MaxDataDeviceBean deviceBean = been.getDeviceBeen();
//        deviceBean.setPvInPower(pvInPower);
//        deviceBean.setPvTotalEnergy(pvTotalE);
        deviceBean.setDeviceTemp(deviceTemp);
//        deviceBean.setEnvTemp(envTemp);
//        deviceBean.setBoostTemp(boostTemp);
        deviceBean.setpBusV(pBusV);
        deviceBean.setnBusV(nBusV);
//        deviceBean.setTotalTime(totoaTime);
        deviceBean.setGridFre(gridFre);
//        deviceBean.setPidErrCode(pidErr);
//        deviceBean.setPidStatus(pidStatus);
//        deviceBean.setIpf(ipf);
        //pv值
        List<String> pvValues = been.getPVList();
        if (pvValues == null || pvValues.size() < 6) {
            pvValues = new ArrayList<>();
            pvValues.add(Arith.mul(vPv1, muilt) + "");
            pvValues.add(Arith.mul(aPv1, muilt) + "");
            pvValues.add(Arith.mul(vPv2, muilt) + "");
            pvValues.add(Arith.mul(aPv2, muilt) + "");
            pvValues.add(Arith.mul(0, muilt) + "");
            pvValues.add(Arith.mul(0, muilt) + "");
            been.setPVList(pvValues);
        } else {
            pvValues.set(0, Arith.mul(vPv1, muilt) + "");
            pvValues.set(1, Arith.mul(aPv1, muilt) + "");
            pvValues.set(2, Arith.mul(vPv2, muilt) + "");
            pvValues.set(3, Arith.mul(aPv2, muilt) + "");
        }

        //pv串
//        List<String> pvcValues = new ArrayList<>();
//        pvcValues.add(Arith.mul(vPvc1 , muilt) + "");pvcValues.add(Arith.mul(aPvc1 , muilt) + "");
//        pvcValues.add(Arith.mul(vPvc2 , muilt) + "");pvcValues.add(Arith.mul(aPvc2 , muilt) + "");
//        pvcValues.add(Arith.mul(vPvc3 , muilt) + "");pvcValues.add(Arith.mul(aPvc3 , muilt) + "");
//        pvcValues.add(Arith.mul(vPvc4 , muilt) + "");pvcValues.add(Arith.mul(aPvc4 , muilt) + "");
//        pvcValues.add(Arith.mul(vPvc5 , muilt) + "");pvcValues.add(Arith.mul(aPvc5 , muilt) + "");
//        pvcValues.add(Arith.mul(vPvc6 , muilt) + "");pvcValues.add(Arith.mul(aPvc6 , muilt) + "");
//        pvcValues.add(Arith.mul(vPvc7 , muilt) + "");pvcValues.add(Arith.mul(aPvc7 , muilt) + "");
//        pvcValues.add(Arith.mul(vPvc8 , muilt) + "");pvcValues.add(Arith.mul(aPvc8 , muilt) + "");
//        been.setPVCList(pvcValues);
        //Ac值
        List<String> acValues = new ArrayList<>();
        acValues.add(Arith.mul(vR, muilt) + "");
        acValues.add(deviceBean.getGridFre());
        acValues.add(Arith.mul(aR, muilt) + "");
        acValues.add(Arith.mul(pR, muilt) + "");
        acValues.add(deviceBean.getIpf());

        acValues.add(Arith.mul(vS, muilt) + "");
        acValues.add(deviceBean.getGridFre());
        acValues.add(Arith.mul(aS, muilt) + "");
        acValues.add(Arith.mul(pS, muilt) + "");
        acValues.add("");

        acValues.add(Arith.mul(vT, muilt) + "");
        acValues.add(deviceBean.getGridFre());
        acValues.add(Arith.mul(aT, muilt) + "");
        acValues.add(Arith.mul(pT, muilt) + "");
        acValues.add("");
        been.setACList(acValues);
        //pid值
//        List<String> pidValues = new ArrayList<>();
//        pidValues.add(Arith.mul(vPid1 , muilt) + "");pidValues.add(Arith.mul(aPid1 , muilt) + "");
//        pidValues.add(Arith.mul(vPid2 , muilt) + "");pidValues.add(Arith.mul(aPid2 , muilt) + "");
//        been.setPIDList(pidValues);
        return deviceBean;
    }

    public static MaxDataDeviceBean parse04Input45T89OldInv(MaxDataBean been, byte[] bytes) {
        //移除外部协议
        byte[] bs = removePro17(bytes);
        //当前功率
//        int normalPower = obtainValueHAndL(MaxWifiParseUtil.subBytes45(bs,11,0,2));
//        int power = obtainValueHAndL(MaxWifiParseUtil.subBytes45(bs,67,0,2));
        //今日发电
//        int todayE = obtainValueHAndL(MaxWifiParseUtil.subBytes45(bs,26,0,2));
//        int totalE = obtainValueHAndL(MaxWifiParseUtil.subBytes45(bs,28,0,2));
        //pv输出功率
//        int  pvInPower = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs,1,0,2));
        //状态
//        int  status = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,0,0,1));
        //故障和警告
//        int errCode = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,40,0,1));
        int warmCode = obtainValueOne(MaxWifiParseUtil.subBytes45(bs, 64, 0, 1));
        //pv总电量
//        int  pvTotalE = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs,91,0,2));
        //温度
//        int  envTemp = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,93,0,1));
//        int  deviceTemp = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,32,0,1));
//        int  boostTemp = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,95,0,1));
        //电压
//        int  pBusV = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,42,0,1));
//        int  nBusV = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,43,0,1));
        int pidErr = obtainValueOne(MaxWifiParseUtil.subBytes45(bs, 89, 0, 1));
//        int  pidStatus = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes45(bs,117,0,1));
        int ipf = obtainValueOne(MaxWifiParseUtil.subBytes45(bs, 45, 0, 1));
        //总工作时长
//        int  totoaTime = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs,57,0,2));


        //电网频率
//        int  gridFre = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,13,0,1));
        //pv电压电流电量
//        int  vPv1 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,3,0,1));
//        int  vPv2 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,7,0,1));
//        int  vPv3 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes45(bs,120,0,1));

//        int  aPv1 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,4,0,1));
//        int  aPv2 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,8,0,1));
//        int  aPv3 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes45(bs,121,0,1));
        //ac电压、电流、功率
//        int  vR = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,14,0,1));
//        int  vS = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,18,0,1));
//        int  vT = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,22,0,1));
//
//        int  aR = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,15,0,1));
//        int  aS = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,19,0,1));
//        int  aT = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,23,0,1));
//
//        int pR = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs,16,0,2));
//        int pS = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs,20,0,2));
//        int pT = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs,24,0,2));
        //pv串
        int vPvc1 = obtainValueOne(MaxWifiParseUtil.subBytes45(bs, 70, 0, 1));
        int vPvc2 = obtainValueOne(MaxWifiParseUtil.subBytes45(bs, 72, 0, 1));
        int vPvc3 = obtainValueOne(MaxWifiParseUtil.subBytes45(bs, 74, 0, 1));
        int vPvc4 = obtainValueOne(MaxWifiParseUtil.subBytes45(bs, 76, 0, 1));
        int vPvc5 = obtainValueOne(MaxWifiParseUtil.subBytes45(bs, 78, 0, 1));
        int vPvc6 = obtainValueOne(MaxWifiParseUtil.subBytes45(bs, 80, 0, 1));
        int vPvc7 = obtainValueOne(MaxWifiParseUtil.subBytes45(bs, 82, 0, 1));
        int vPvc8 = obtainValueOne(MaxWifiParseUtil.subBytes45(bs, 84, 0, 1));

        int aPvc1 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes45(bs, 71, 0, 1));
        int aPvc2 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes45(bs, 73, 0, 1));
        int aPvc3 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes45(bs, 75, 0, 1));
        int aPvc4 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes45(bs, 77, 0, 1));
        int aPvc5 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes45(bs, 79, 0, 1));
        int aPvc6 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes45(bs, 81, 0, 1));
        int aPvc7 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes45(bs, 83, 0, 1));
        int aPvc8 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes45(bs, 85, 0, 1));
        //pid
//        int vPid1 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes45(bs,115,0,1));
//        int vPid2 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes45(bs,118,0,1));

//        int aPid1 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes45(bs,116,0,1));
//        int aPid2 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes45(bs,119,0,1));
//设置值
//        been.setNormalPower(normalPower);
//        been.setTotalPower(power);
//        been.setTodayEnergy(todayE);
//        been.setTotalEnergy(totalE);
//        been.setStatus(status);
//        been.setErrCode(errCode);
//        been.setWarmCode(warmCode);
        been.setWarmCode(parseDTC210Warncode(been.getDeviceTypeCode(), warmCode));
        //device值
        MaxDataDeviceBean deviceBean = been.getDeviceBeen();
//        deviceBean.setPvInPower(pvInPower);
//        deviceBean.setPvTotalEnergy(pvTotalE);
//        deviceBean.setDeviceTemp(deviceTemp);
//        deviceBean.setEnvTemp(envTemp);
//        deviceBean.setBoostTemp(boostTemp);
//        deviceBean.setpBusV(pBusV);
//        deviceBean.setnBusV(nBusV);
//        deviceBean.setTotalTime(totoaTime);
//        deviceBean.setGridFre(gridFre);
        deviceBean.setPidErrCode(pidErr);
//        deviceBean.setPidStatus(pidStatus);
        deviceBean.setIpf(ipf);
        //pv值
//        List<String> pvValues = new ArrayList<>();
//        pvValues.add(Arith.mul(vPv1,muilt) + "");
//        pvValues.add(Arith.mul(aPv1, muilt) + "");
//        pvValues.add(Arith.mul(vPv2, muilt) + "");
//        pvValues.add(Arith.mul(aPv2, muilt) + "");
//        pvValues.add(Arith.mul(vPv3, muilt) + "");
//        pvValues.add(Arith.mul(aPv3, muilt) + "");
//        been.setPVList(pvValues);
        //pv串
        List<String> pvcValues = new ArrayList<>();
        pvcValues.add(Arith.mul(vPvc1, muilt) + "");
        pvcValues.add(Arith.mul(aPvc1, muilt) + "");
        pvcValues.add(Arith.mul(vPvc2, muilt) + "");
        pvcValues.add(Arith.mul(aPvc2, muilt) + "");
        pvcValues.add(Arith.mul(vPvc3, muilt) + "");
        pvcValues.add(Arith.mul(aPvc3, muilt) + "");
        pvcValues.add(Arith.mul(vPvc4, muilt) + "");
        pvcValues.add(Arith.mul(aPvc4, muilt) + "");
        pvcValues.add(Arith.mul(vPvc5, muilt) + "");
        pvcValues.add(Arith.mul(aPvc5, muilt) + "");
        pvcValues.add(Arith.mul(vPvc6, muilt) + "");
        pvcValues.add(Arith.mul(aPvc6, muilt) + "");
        pvcValues.add(Arith.mul(vPvc7, muilt) + "");
        pvcValues.add(Arith.mul(aPvc7, muilt) + "");
        pvcValues.add(Arith.mul(vPvc8, muilt) + "");
        pvcValues.add(Arith.mul(aPvc8, muilt) + "");
        been.setPVCList(pvcValues);
        //Ac值
//        List<String> acValues = new ArrayList<>();
//        acValues.add(Arith.mul(vR , muilt) + "");
//        acValues.add(deviceBean.getGridFre());
//        acValues.add(Arith.mul(aR , muilt) + "");
//        acValues.add(Arith.mul(pR , muilt) + "");
//        acValues.add(deviceBean.getIpf());
//
//        acValues.add(Arith.mul(vS , muilt) + "");
//        acValues.add(deviceBean.getGridFre());
//        acValues.add(Arith.mul(aS , muilt) + "");
//        acValues.add(Arith.mul(pS , muilt) + "");
//        acValues.add("");
//
//        acValues.add(Arith.mul(vT , muilt) + "");
//        acValues.add(deviceBean.getGridFre());
//        acValues.add(Arith.mul(aT , muilt) + "");
//        acValues.add(Arith.mul(pT , muilt) + "");
//        acValues.add("");
//        been.setACList(acValues);
        //pid值
//        List<String> pidValues = new ArrayList<>();
//        pidValues.add(Arith.mul(vPid1 , muilt) + "");pidValues.add(Arith.mul(aPid1 , muilt) + "");
//        pidValues.add(Arith.mul(vPid2 , muilt) + "");pidValues.add(Arith.mul(aPid2 , muilt) + "");
//        been.setPIDList(pidValues);
        return deviceBean;
    }

    /**
     * 针对老机单项  dtc == 210
     *
     * @param warmCode
     * @return
     */
    private static int parseDTC210Warncode(int dtc, int warmCode) {
        int realCode = warmCode;
        switch (dtc) {
            case 210:
                for (int i = 0; i < 16; i++) {
                    if ((warmCode & ((int) Math.pow(2, i))) != 0) {
                        realCode = i + 1;
                        break;
                    }
                }
                break;
        }
        return realCode;
    }

    public static MaxDataDeviceBean parse04Input90T134OldInv(MaxDataBean been, byte[] bytes) {
        //移除外部协议
        byte[] bs = removePro17(bytes);
        //当前功率
//        int normalPower = obtainValueHAndL(MaxWifiParseUtil.subBytes45(bs,11,0,2));
//        int power = obtainValueHAndL(MaxWifiParseUtil.subBytes45(bs,67,0,2));
        //今日发电
//        int todayE = obtainValueHAndL(MaxWifiParseUtil.subBytes45(bs,26,0,2));
//        int totalE = obtainValueHAndL(MaxWifiParseUtil.subBytes45(bs,28,0,2));
        //pv输出功率
//        int  pvInPower = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs,1,0,2));
        //状态
//        int  status = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,0,0,1));
        //故障和警告
//        int errCode = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,40,0,1));
//        int  warmCode = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,64,0,1));
        //pv总电量
//        int  pvTotalE = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs,91,0,2));
        //温度
//        int  envTemp = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,93,0,1));
//        int  deviceTemp = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,32,0,1));
//        int  boostTemp = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,95,0,1));
        //电压
//        int  pBusV = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,42,0,1));
//        int  nBusV = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,43,0,1));
//        int  pidErr = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,89,0,1));
//        int  pidStatus = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes45(bs,117,0,1));
//        int ipf = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,45,0,1));
        //总工作时长
//        int  totoaTime = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs,57,0,2));


        //电网频率
//        int  gridFre = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,13,0,1));
        //pv电压电流电量
//        int  vPv1 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,3,0,1));
//        int  vPv2 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,7,0,1));
        int vPv3 = obtainValueOne(MaxWifiParseUtil.subBytes45(bs, 120, 0, 1));

//        int  aPv1 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,4,0,1));
//        int  aPv2 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,8,0,1));
        int aPv3 = obtainValueOne(MaxWifiParseUtil.subBytes45(bs, 121, 0, 1));
        //ac电压、电流、功率
//        int  vR = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,14,0,1));
//        int  vS = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,18,0,1));
//        int  vT = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,22,0,1));
//
//        int  aR = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,15,0,1));
//        int  aS = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,19,0,1));
//        int  aT = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,23,0,1));
//
//        int pR = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs,16,0,2));
//        int pS = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs,20,0,2));
//        int pT = obtainValueHAndL(MaxWifiParseUtil.subBytes(bs,24,0,2));
        //pv串
//        int vPvc1 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,70,0,1));
//        int vPvc2 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,72,0,1));
//        int vPvc3 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,74,0,1));
//        int vPvc4 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,76,0,1));
//        int vPvc5 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,78,0,1));
//        int vPvc6 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,80,0,1));
//        int vPvc7 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,82,0,1));
//        int vPvc8 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs,84,0,1));
//
//        int aPvc1 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs,71,0,1));
//        int aPvc2 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs,73,0,1));
//        int aPvc3 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs,75,0,1));
//        int aPvc4 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs,77,0,1));
//        int aPvc5 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs,79,0,1));
//        int aPvc6 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs,81,0,1));
//        int aPvc7 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs,83,0,1));
//        int aPvc8 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs,85,0,1));
        //pid
        int vPid1 = obtainValueOne(MaxWifiParseUtil.subBytes45(bs, 115, 0, 1));
        int vPid2 = obtainValueOne(MaxWifiParseUtil.subBytes45(bs, 118, 0, 1));

        int aPid1 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes45(bs, 116, 0, 1));
        int aPid2 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes45(bs, 119, 0, 1));
//设置值
//        been.setNormalPower(normalPower);
//        been.setTotalPower(power);
//        been.setTodayEnergy(todayE);
//        been.setTotalEnergy(totalE);
//        been.setStatus(status);
//        been.setErrCode(errCode);
//        been.setWarmCode(warmCode);
        //device值
        MaxDataDeviceBean deviceBean = been.getDeviceBeen();
//        deviceBean.setPvInPower(pvInPower);
//        deviceBean.setPvTotalEnergy(pvTotalE);
//        deviceBean.setDeviceTemp(deviceTemp);
//        deviceBean.setEnvTemp(envTemp);
//        deviceBean.setBoostTemp(boostTemp);
//        deviceBean.setpBusV(pBusV);
//        deviceBean.setnBusV(nBusV);
//        deviceBean.setTotalTime(totoaTime);
//        deviceBean.setGridFre(gridFre);
//        deviceBean.setPidErrCode(pidErr);
//        deviceBean.setPidStatus(pidStatus);
//        deviceBean.setIpf(ipf);
        //pv值
        List<String> pvValues = been.getPVList();
        if (pvValues != null && pvValues.size() >= 6) {
            pvValues.set(4, Arith.mul(vPv3, muilt) + "");
            pvValues.set(5, Arith.mul(aPv3, muilt) + "");
        }
//        pvValues.add(Arith.mul(vPv1,muilt) + "");
//        pvValues.add(Arith.mul(aPv1, muilt) + "");
//        pvValues.add(Arith.mul(vPv2, muilt) + "");
//        pvValues.add(Arith.mul(aPv2, muilt) + "");
//        pvValues.add(Arith.mul(vPv3, muilt) + "");
//        pvValues.add(Arith.mul(aPv3, muilt) + "");
//        been.setPVList(pvValues);
        //pv串
//        List<String> pvcValues = new ArrayList<>();
//        pvcValues.add(Arith.mul(vPvc1 , muilt) + "");pvcValues.add(Arith.mul(aPvc1 , muilt) + "");
//        pvcValues.add(Arith.mul(vPvc2 , muilt) + "");pvcValues.add(Arith.mul(aPvc2 , muilt) + "");
//        pvcValues.add(Arith.mul(vPvc3 , muilt) + "");pvcValues.add(Arith.mul(aPvc3 , muilt) + "");
//        pvcValues.add(Arith.mul(vPvc4 , muilt) + "");pvcValues.add(Arith.mul(aPvc4 , muilt) + "");
//        pvcValues.add(Arith.mul(vPvc5 , muilt) + "");pvcValues.add(Arith.mul(aPvc5 , muilt) + "");
//        pvcValues.add(Arith.mul(vPvc6 , muilt) + "");pvcValues.add(Arith.mul(aPvc6 , muilt) + "");
//        pvcValues.add(Arith.mul(vPvc7 , muilt) + "");pvcValues.add(Arith.mul(aPvc7 , muilt) + "");
//        pvcValues.add(Arith.mul(vPvc8 , muilt) + "");pvcValues.add(Arith.mul(aPvc8 , muilt) + "");
//        been.setPVCList(pvcValues);
        //Ac值
//        List<String> acValues = new ArrayList<>();
//        acValues.add(Arith.mul(vR , muilt) + "");
//        acValues.add(deviceBean.getGridFre());
//        acValues.add(Arith.mul(aR , muilt) + "");
//        acValues.add(Arith.mul(pR , muilt) + "");
//        acValues.add(deviceBean.getIpf());
//
//        acValues.add(Arith.mul(vS , muilt) + "");
//        acValues.add(deviceBean.getGridFre());
//        acValues.add(Arith.mul(aS , muilt) + "");
//        acValues.add(Arith.mul(pS , muilt) + "");
//        acValues.add("");
//
//        acValues.add(Arith.mul(vT , muilt) + "");
//        acValues.add(deviceBean.getGridFre());
//        acValues.add(Arith.mul(aT , muilt) + "");
//        acValues.add(Arith.mul(pT , muilt) + "");
//        acValues.add("");
//        been.setACList(acValues);
        //pid值
        List<String> pidValues = new ArrayList<>();
        pidValues.add(Arith.mul(vPid1, muilt) + "");
        pidValues.add(Arith.mul(aPid1, muilt) + "");
        pidValues.add(Arith.mul(vPid2, muilt) + "");
        pidValues.add(Arith.mul(aPid2, muilt) + "");
        been.setPIDList(pidValues);
        return deviceBean;
    }

    /**
     * 解析14:读取寄存器125个:IV图表
     */
    public static void parseMax14(List<ArrayList<Entry>> list, byte[] bytes, int count) {
        //移除外部协议
        byte[] bs = removePro17(bytes);
        if (list != null) {
            int size = list.size();
            if (size > count && bs != null) {
                ArrayList<Entry> entries = list.get(count);
                entries.clear();

                int len = bs.length / 4;
                if (len > 51) {
                    len = 51;
                }
                for (int i = 0; i < len; i++) {
//                for (int i =0,len = 51;i<len;i++){
                    int posX = obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, i * 2, 0, 1));
                    int posY = obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, i * 2 + 1, 0, 1));
//                    if (posX == 0xffff || posY == 0xffff){
//                        continue;
//                    }
                    Entry entry = new Entry();
                    com.growatt.shinetools.utils.Log.i("x=" + posX + " ;y=" + posY);
                    //取整
                    entry.setX((int) posX / 10);
                    entry.setY((float) Arith.mul(posY, 0.1));
//                    entry.setY((float) Arith.mul(posY + count * 3000,0.1));
                    entries.add(entry);
                    Collections.sort(entries, new Comparator<Entry>() {
                        @Override
                        public int compare(Entry o1, Entry o2) {
                            return o1.getX() > o2.getX() ? 1 : -1;
                        }
                    });
                }
            }
        }
    }

    /**
     * 解析14:读取寄存器125个:故障录波图表
     */
    public static void parseMaxErr14(MaxCheckErrorTotalBean bean, byte[] bytes, int count) {
        try {
            //移除外部协议
            byte[] bs = removePro17(bytes);
            if (bean != null && bean.getDataList() != null) {
                List<ArrayList<Entry>> list = bean.getDataList();
                //获取倍数
                double nowMult = bean.getMults().get(count / 5);
                if (list.size() > count / 5 && bs != null) {
                    ArrayList<Entry> entries = list.get(count / 5);
                    //当前曲线序号
                    int waveNum = count / 5;
                    //当前曲线下单条
                    int pos = count % 5;
                    //曲线开始位置
                    int start = 0;
                    if (pos == 0) {
                        start = 5;
                        entries.clear();
                        List<String> times = bean.getTimes();
                        List<Integer> ids = bean.getIds();
                        List<Integer> errCodes = bean.getErrCodes();
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < 6; i++) {
                            sb.append(MaxWifiParseUtil.obtainRegistValueHOrL(0, bs[i]));
                            if (i >= 0 && i <= 1) {
                                sb.append("-");
                            }
                            if (i == 2) {
                                sb.append(" ");
                            }
                            if (i >= 3 && i <= 4) {
                                sb.append(":");
                            }
                        }
                        times.add(String.valueOf(sb));
                        ids.add(obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 4, 0, 1)));
//                        errCodes.add(MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytesFull(bs,3,0,1)));
                        errCodes.add(MaxWifiParseUtil.obtainRegistValueHOrL(0, bs[6]));
                    }
                    //解析数据
                    for (int i = start, len = bs.length / 2; i < len; i++) {
                        if (pos == 4 && i == 5) break;
                        Entry entry = new Entry();
                        int posY = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytesFull(bs, i, 0, 1));
                        double muilY = Arith.mul(posY, nowMult);
                        entry.setX(entries.size() + 1);
                        entry.setY((float) round(muilY, 1));
                        Log.i("posY:" + posY + ";muilY:" + muilY + ";Y:" + entry.getY());
                        entries.add(entry);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析一键检测  Ac曲线
     *
     * @param listAc
     * @param bytes
     * @param count
     */
    public static void parseMaxErrAC(List<ArrayList<Entry>> list, List<MaxCheckOneKeyAcBean> listAc, byte[] bytes, int count) {
        try {
            //移除外部协议
            byte[] bs = removePro17(bytes);
            //解析RF值
            if (count == 0) {
                if (listAc != null && listAc.size() == 3) {
                    int posF = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytesFull(bs, 3, 0, 1));
                    String mPosF = String.valueOf(
                            round(Arith.mul(posF, 0.01), 2)
                    );
                    for (int i = 0; i < listAc.size(); i++) {
                        MaxCheckOneKeyAcBean rvBean = listAc.get(i);
                        int posY = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytesFull(bs, i, 0, 1));
                        rvBean.setAcRms(new DecimalFormat("0.0").format(Arith.div(posY, 10.0f, 1)));
                        rvBean.setAcF(mPosF);
                    }
                }
            } else {
                count = count - 1;
                int start = 0;
                int pos = count % 5;
                //当前曲线
                int nowAc = count / 5;
                //解析数据
                if (list != null && list.size() > nowAc) {
                    ArrayList<Entry> entries = list.get(nowAc);
                    if (pos == 0) {
                        entries.clear();
                        start = 5;
                    }
                    for (int i = start, len = bs.length / 2; i < len; i++) {
                        if (pos == 4 && i == 5) break;
                        Entry entry = new Entry();
                        int posY = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytesFull(bs, i, 0, 1));
                        double muilY = Arith.mul(posY, 0.1);
                        entry.setX(entries.size() + 1);
                        entry.setY((float) round(muilY, 1));
                        entries.add(entry);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析一键检测  THDV曲线
     *
     * @param bytes
     */
    public static void parseMaxErrTHDV(List<List<BarEntry>> dataListTHDV, List<MaxCheckOneKeyTHDVBean> listTHDV, byte[] bytes, int countTHDV) {
        int mNum = 11;
        try {
            //移除外部协议
            byte[] bs = removePro17(bytes);
            if (bs != null && bs.length > 0) {
                for (int i = 0; i < mNum * 3; i++) {
                    int value = obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, i, 0, 1));
                    if (i % mNum == 0) {
                        MaxCheckOneKeyTHDVBean rvBean = listTHDV.get(i / mNum);
                        rvBean.setValue(String.valueOf(Arith.div(value, 100.0f, 2)) + "%");
                    } else {
                        if (i % mNum == 1) {
                            continue;
                        }
                        List<BarEntry> barEntries = dataListTHDV.get(i / mNum);
                        int xValue = (i % mNum - 1) * 2 + 1;
                        BarEntry entry = new BarEntry(xValue, (float) Arith.div(value, 100.0f, 2));
                        barEntries.add(entry);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析一键检测  RST曲线
     *
     * @param bytes
     */
    public static void parseMaxErrRST(List<MaxCheckOneKeyRSTBean> listRSt, byte[] bytes, int countRST) {
        int mStart = 33;
        try {
            //移除外部协议
            byte[] bs = removePro17(bytes);
            if (bs != null && bs.length > 0) {
                for (int i = mStart; i < mStart + 3; i++) {
                    int value = obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, i, 0, 1));
                    String valueF = new DecimalFormat("0.00").format(Arith.div(value, 100.00f, 2));
                    MaxCheckOneKeyRSTBean rvBean = listRSt.get(i - mStart);
                    rvBean.setValue(valueF);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析一键检测  iso  6040  start6000
     *
     * @param bytes
     */
    public static void parseMaxErrISO(MaxCheckOneKeyISOBean isoBean, byte[] bytes, int countRST) {
        try {
            //移除外部协议
            byte[] bs = removePro17(bytes);
            if (bs != null && bs.length > 0) {
                int value = obtainValueOne(MaxWifiParseUtil.subBytesFull(bs, 40, 0, 1));
                isoBean.setIsoValue(String.valueOf(value));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析03(100-132):125-132号寄存器:读机器型号
     */
    public static void parseMax3T100T132(MaxDataBean been, byte[] bytes) {
        //移除外部协议
        byte[] bs = removePro17(bytes);
        //机器型号
        String deviceType = MaxWifiParseUtil.obtainRegistValueAscii(
                MaxWifiParseUtil.subBytes(bs, 125, 0, 8)
        );
        MaxDataDeviceBean deviceBeen = been.getDeviceBeen();
        deviceBeen.setDeviceType(deviceType);
    }

    /**
     * 解析03(125-249)::读机器型号 + 新sn等
     */
    public static void parseMax3T125T249(MaxDataBean been, byte[] bytes) {
        //移除外部协议
        byte[] bs = removePro17(bytes);
        //机器型号
        String deviceType = MaxWifiParseUtil.obtainRegistValueAscii(
                MaxWifiParseUtil.subBytes125(bs, 125, 0, 8)
        );
        MaxDataDeviceBean deviceBean = been.getDeviceBeen();
        deviceBean.setDeviceType(deviceType);
        //sn号
        byte[] snBytes = MaxWifiParseUtil.subBytes125(bs, 209, 0, 15);
        String sn30 = parseSn30(snBytes);
        if (!TextUtils.isEmpty(sn30)) {
            deviceBean.setSn(sn30);
        }
//        BigInteger bigInteger = new BigInteger(1, snBytes);
//        boolean hasSn = true;
//        try {
//            if (bigInteger.longValue() == 0){
//                hasSn = false;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            hasSn = true;
//        }
//        if (hasSn) {
//            String sn = MaxWifiParseUtil.obtainRegistValueAscii(snBytes);
//            deviceBeen.setSn(sn);
//        }
    }

    /**
     * 解析04(500-624):500-624号寄存器:
     * 解析04(625-749):625-749号寄存器:
     * 获取故障信息
     * 每次一组解析
     */
    public static List<MaxErrorBean> parseMax4T500T750(byte[] bytes) {
        List<MaxErrorBean> list = new ArrayList<>();
        //移除外部协议
        byte[] bs = removePro17(bytes);
        if (bs != null && bs.length >= 10) {
            for (int i = 0; i < bs.length / 10; i++) {
                MaxErrorBean bean = new MaxErrorBean();
                byte[] itemBytes = MaxWifiParseUtil.subBytesFull(bs, i * 5, 0, 5);
                //解析int值\
                int code = obtainValueOne(MaxWifiParseUtil.subBytes(itemBytes, 0, 0, 1));
                int month = MaxWifiParseUtil.obtainRegistValueHOrL(0, itemBytes[3]);
                int day = MaxWifiParseUtil.obtainRegistValueHOrL(0, itemBytes[4]);
                int hour = MaxWifiParseUtil.obtainRegistValueHOrL(0, itemBytes[5]);
                int min = MaxWifiParseUtil.obtainRegistValueHOrL(0, itemBytes[6]);
                bean.setErrCode(code);
                bean.setErrMonth(month);
                bean.setErrDay(day);
                bean.setErrHour(hour);
                bean.setErrMin(min);
                list.add(bean);
            }
        }
        return list;
    }

    /**
     * 解析04(90-114)
     * 获取故障信息
     * 每次一组解析
     */
    public static List<MaxErrorBean> parseOldInvHisErrInput90T114(byte[] bytes) {
        List<MaxErrorBean> list = new ArrayList<>();
        //移除外部协议
        byte[] bs = removePro17(bytes);
        if (bs != null && bs.length >= 10) {
            for (int i = 0; i < bs.length / 10; i++) {
                MaxErrorBean bean = new MaxErrorBean();
                byte[] itemBytes = MaxWifiParseUtil.subBytesFull(bs, i * 5, 0, 5);
                //解析int值\
                int code = obtainValueOne(MaxWifiParseUtil.subBytes(itemBytes, 0, 0, 1));
                int month = MaxWifiParseUtil.obtainRegistValueHOrL(0, itemBytes[3]);
                int day = MaxWifiParseUtil.obtainRegistValueHOrL(0, itemBytes[4]);
                int hour = MaxWifiParseUtil.obtainRegistValueHOrL(0, itemBytes[5]);
                int min = MaxWifiParseUtil.obtainRegistValueHOrL(0, itemBytes[6]);
                bean.setErrCode(code);
                bean.setErrMonth(month);
                bean.setErrDay(day);
                bean.setErrHour(hour);
                bean.setErrMin(min);
                list.add(bean);
            }
        }
        return list;
    }


    /**
     * 解析03:125-249号寄存器
     */
    public static void parseTL3XH125T249(MaxDataBean been, byte[] bytes) {
        //移除外部协议
        byte[] bs = removePro17(bytes);
        Log.i("receive1", SocketClientUtil.bytesToHexString(bs));
        MaxDataDeviceBean deviceBean = been.getDeviceBeen();
        //sn号
        byte[] snBytes = MaxWifiParseUtil.subBytes125(bs, 209, 0, 15);
        String sn30 = parseSn30(snBytes);
        if (!TextUtils.isEmpty(sn30)) {
            deviceBean.setSn(sn30);
        }
//        BigInteger bigInteger = new BigInteger(1, snBytes);
//        boolean hasSn = true;
//        try {
//            if (bigInteger.longValue() == 0){
//                hasSn = false;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            hasSn = true;
//        }
//        if (hasSn) {
//            String sn = MaxWifiParseUtil.obtainRegistValueAscii(snBytes);
//            deviceBean.setSn(sn);
//        }

        //pv串
        int vPvc1 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 142, 0, 1));
        int vPvc2 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 144, 0, 1));
        int vPvc3 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 146, 0, 1));
        int vPvc4 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 148, 0, 1));
        int vPvc5 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 150, 0, 1));
        int vPvc6 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 152, 0, 1));
        int vPvc7 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 154, 0, 1));
        int vPvc8 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 156, 0, 1));
        int vPvc9 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 158, 0, 1));
        int vPvc10 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 160, 0, 1));
        int vPvc11 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 162, 0, 1));
        int vPvc12 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 164, 0, 1));
        int vPvc13 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 166, 0, 1));
        int vPvc14 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 168, 0, 1));
        int vPvc15 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 170, 0, 1));
        int vPvc16 = obtainValueOne(MaxWifiParseUtil.subBytes(bs, 172, 0, 1));

        int aPvc1 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 143, 0, 1));
        int aPvc2 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 145, 0, 1));
        int aPvc3 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 147, 0, 1));
        int aPvc4 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 149, 0, 1));
        int aPvc5 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 151, 0, 1));
        int aPvc6 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 153, 0, 1));
        int aPvc7 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 155, 0, 1));
        int aPvc8 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 157, 0, 1));
        int aPvc9 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 159, 0, 1));
        int aPvc10 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 161, 0, 1));
        int aPvc11 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 163, 0, 1));
        int aPvc12 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 165, 0, 1));
        int aPvc13 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 167, 0, 1));
        int aPvc14 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 169, 0, 1));
        int aPvc15 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 171, 0, 1));
        int aPvc16 = MaxWifiParseUtil.obtainValueCurrent(MaxWifiParseUtil.subBytes(bs, 173, 0, 1));

        //pv串
        List<String> pvcValues = been.getPVCList();
        if (pvcValues == null || pvcValues.size() < 32) {
            pvcValues = new ArrayList<>();
            pvcValues.add(Arith.mul(vPvc1, muilt) + "");
            pvcValues.add(Arith.mul(aPvc1, muilt) + "");
            pvcValues.add(Arith.mul(vPvc2, muilt) + "");
            pvcValues.add(Arith.mul(aPvc2, muilt) + "");
            pvcValues.add(Arith.mul(vPvc3, muilt) + "");
            pvcValues.add(Arith.mul(aPvc3, muilt) + "");
            pvcValues.add(Arith.mul(vPvc4, muilt) + "");
            pvcValues.add(Arith.mul(aPvc4, muilt) + "");
            pvcValues.add(Arith.mul(vPvc5, muilt) + "");
            pvcValues.add(Arith.mul(aPvc5, muilt) + "");
            pvcValues.add(Arith.mul(vPvc6, muilt) + "");
            pvcValues.add(Arith.mul(aPvc6, muilt) + "");
            pvcValues.add(Arith.mul(vPvc7, muilt) + "");
            pvcValues.add(Arith.mul(aPvc7, muilt) + "");
            pvcValues.add(Arith.mul(vPvc8, muilt) + "");
            pvcValues.add(Arith.mul(aPvc8, muilt) + "");
            pvcValues.add(Arith.mul(vPvc9, muilt) + "");
            pvcValues.add(Arith.mul(aPvc9, muilt) + "");
            pvcValues.add(Arith.mul(vPvc10, muilt) + "");
            pvcValues.add(Arith.mul(aPvc10, muilt) + "");
            pvcValues.add(Arith.mul(vPvc11, muilt) + "");
            pvcValues.add(Arith.mul(aPvc11, muilt) + "");
            pvcValues.add(Arith.mul(vPvc12, muilt) + "");
            pvcValues.add(Arith.mul(aPvc12, muilt) + "");
            pvcValues.add(Arith.mul(vPvc13, muilt) + "");
            pvcValues.add(Arith.mul(aPvc13, muilt) + "");
            pvcValues.add(Arith.mul(vPvc14, muilt) + "");
            pvcValues.add(Arith.mul(aPvc14, muilt) + "");
            pvcValues.add(Arith.mul(vPvc15, muilt) + "");
            pvcValues.add(Arith.mul(aPvc15, muilt) + "");
            pvcValues.add(Arith.mul(vPvc16, muilt) + "");
            pvcValues.add(Arith.mul(aPvc16, muilt) + "");
            been.setPVCList(pvcValues);
        } else {
            pvcValues.set(0, Arith.mul(vPvc1, muilt) + "");
            pvcValues.set(1, Arith.mul(aPvc1, muilt) + "");
            pvcValues.set(2, Arith.mul(vPvc2, muilt) + "");
            pvcValues.set(3, Arith.mul(aPvc2, muilt) + "");
            pvcValues.set(4, Arith.mul(vPvc3, muilt) + "");
            pvcValues.set(5, Arith.mul(aPvc3, muilt) + "");
            pvcValues.set(6, Arith.mul(vPvc4, muilt) + "");
            pvcValues.set(7, Arith.mul(aPvc4, muilt) + "");
            pvcValues.set(8, Arith.mul(vPvc5, muilt) + "");
            pvcValues.set(9, Arith.mul(aPvc5, muilt) + "");
            pvcValues.set(10, Arith.mul(vPvc6, muilt) + "");
            pvcValues.set(11, Arith.mul(aPvc6, muilt) + "");
            pvcValues.set(12, Arith.mul(vPvc7, muilt) + "");
            pvcValues.set(13, Arith.mul(aPvc7, muilt) + "");
            pvcValues.set(14, Arith.mul(vPvc8, muilt) + "");
            pvcValues.set(15, Arith.mul(aPvc8, muilt) + "");
            pvcValues.set(16, Arith.mul(vPvc9, muilt) + "");
            pvcValues.set(17, Arith.mul(aPvc9, muilt) + "");
            pvcValues.set(18, Arith.mul(vPvc10, muilt) + "");
            pvcValues.set(19, Arith.mul(aPvc10, muilt) + "");
            pvcValues.set(20, Arith.mul(vPvc11, muilt) + "");
            pvcValues.set(21, Arith.mul(aPvc11, muilt) + "");
            pvcValues.set(22, Arith.mul(vPvc12, muilt) + "");
            pvcValues.set(23, Arith.mul(aPvc12, muilt) + "");
            pvcValues.set(24, Arith.mul(vPvc13, muilt) + "");
            pvcValues.set(25, Arith.mul(aPvc13, muilt) + "");
            pvcValues.set(26, Arith.mul(vPvc14, muilt) + "");
            pvcValues.set(27, Arith.mul(aPvc14, muilt) + "");
            pvcValues.set(28, Arith.mul(vPvc15, muilt) + "");
            pvcValues.set(29, Arith.mul(aPvc15, muilt) + "");
            pvcValues.set(30, Arith.mul(vPvc16, muilt) + "");
            pvcValues.set(31, Arith.mul(aPvc16, muilt) + "");
        }


        //机器型号
        String deviceType = MaxWifiParseUtil.obtainRegistValueAscii(
                MaxWifiParseUtil.subBytes125(bs, 125, 0, 8)
        );
        deviceBean.setDeviceType(deviceType);
    }


    /**
     * 加密或者解密
     *
     * @param data
     * @return
     */
    public static byte[] enCode(byte[] data) throws Exception {
        byte[] secretKeyByte = secretKey.getBytes();
        // 1.初始化加密新byte[]
        byte[] backData = new byte[data.length];
        // 2.初始化索引
        int index = 0;
        // 3.加密
        for (int i = 0; i < data.length; i++) {
            if (i % (secretKeyByte.length) == 0) {
                index = 0;
            }
            backData[i] = (byte) (data[i] ^ secretKeyByte[index++]);
        }
        return backData;
    }


    /**
     * 解析03:3085-3124号寄存器:40个寄存器
     */
    public static void parseUSHold3085T3124(MaxDataBean been, byte[] bytes, int resRester, int bdcPosition) {
        //移除外部协议
        byte[] bs = removePro17(bytes);
        Log.i("receive1", SocketClientUtil.bytesToHexString(bs));
        //设备类型
        been.setDeviceTypeCode(usBdcObtainValueOne(bs, bdc03SrcIndex(resRester, 3098, bdcPosition)));

        int srcPos = bdc03SrcIndex(resRester, 3099, bdcPosition);

        String bdcVersion1 = MaxWifiParseUtil.obtainRegistValueAsciiYesNull(MaxWifiParseUtil.usbdcParallelRegister(bs, srcPos, 0, 2));
//        MyToastUtils.toast("解析bdc数据："+"原始位置：3099"+"新的位置："+srcPos+"解析成字符串："+bdcVersion1);

        int bdcVersion2 = usBdcObtainValueOne(bs, bdc03SrcIndex(resRester, 3101, bdcPosition));
        String bdcVersion = String.format("%s-%d", bdcVersion1, bdcVersion2);
        been.setBdcVervison(bdcVersion);


        been.setDeviceTypeCode(usBdcObtainValueOne(bs, bdc03SrcIndex(resRester, 3105, bdcPosition)));

        //sn号
        srcPos = bdc03SrcIndex(resRester, 3087, bdcPosition);
        String sn = MaxWifiParseUtil.obtainRegistValueAscii(
                MaxWifiParseUtil.usbdcParallelRegister(bs, srcPos, 0, 8)
        );

        //mode SxxBxxDxxTxxPxxUxxMxx
        srcPos = bdc03SrcIndex(resRester, 3108, bdcPosition);
        byte[] modebs = MaxWifiParseUtil.usbdcParallelRegister(bs, srcPos, 0, 4);


        //解析int值
        BigInteger big = new BigInteger(1, modebs);
        long bigInteger = big.longValue();
        String deviceModelNew4 = MaxUtil.getDeviceModelNew4(bigInteger);

        //M3版本
        srcPos = bdc03SrcIndex(resRester, 3096, bdcPosition);
        String m3Version1 = MaxWifiParseUtil.obtainRegistValueAsciiYesNull(MaxWifiParseUtil.usbdcParallelRegister(bs, srcPos, 0, 2));


        int m3Version2 = usBdcObtainValueOne(bs, bdc03SrcIndex(resRester, 3103, bdcPosition));
        String m3Version = String.valueOf(m3Version2);
        if (!TextUtils.isEmpty(m3Version1)) {
            m3Version = String.format("%s-%d", m3Version1, m3Version2);
        }


        //BMS版本

        srcPos = bdc03SrcIndex(resRester, 3105, bdcPosition);
     /*   String bmsVersion = MaxWifiParseUtil.obtainRegistValueAscii(
                MaxWifiParseUtil.usbdcParallelRegister(bs, srcPos, 0, 1)
        );*/
        String bmsVersion = String.valueOf(MaxWifiParseUtil.usBdcObtainValueOne(bs, srcPos));


        //厂商信息

        srcPos = bdc03SrcIndex(resRester, 3106, bdcPosition);
        String company = MaxWifiParseUtil.obtainRegistValueAscii(
                MaxWifiParseUtil.usbdcParallelRegister(bs, srcPos, 0, 1)
        );


        //电池通信类型
        srcPos = bdc03SrcIndex(resRester, 3107, bdcPosition);
        String batteryType = MaxWifiParseUtil.obtainRegistValueAscii(
                MaxWifiParseUtil.usbdcParallelRegister(bs, srcPos, 0, 1)
        );

        //解析bdc/电池信息
        BDCInfoBean bdcInfoBean = been.getBdcInfoBean();
        //dsp固件版本
        bdcInfoBean.setDsp_version(bdcVersion);
        //bdc序列号
        bdcInfoBean.setBdc_serialnumber(sn);
        //模式
        bdcInfoBean.setBdc_mode(deviceModelNew4);
        //M3版本
        bdcInfoBean.setM3_version(m3Version);
    /*    //状态
        bdcInfoBean.setStatus(status);
        //模式
        bdcInfoBean.setWorkMode(mode);*/
        bdcInfoBean.setBms_version(bmsVersion);
        bdcInfoBean.setBattery_company(company);
        bdcInfoBean.setBattery_type(batteryType);
    }

    /**
     * 解析04:3165-3233号寄存器:69个寄存器
     */
    public static void parseUSHold3165T3233(MaxDataBean been, byte[] bs, int resRester, int bdcPosition) {
        //移除外部协议
        byte[] bytes = removePro17(bs);
        Log.i("receive1", SocketClientUtil.bytesToHexString(bytes));
        MaxDataDeviceBean deviceBean = been.getDeviceBeen();
        ToolStorageDataBean storageBeen = been.getStorageBeen();
        BDCInfoBean bdcInfoBean = been.getBdcInfoBean();

        /*BDC信息*/
        //bdc状态
        storageBeen.setpChargeBDC1(usBdcObtainValueTwo(bytes, bdc04SrcIndex(resRester, 3180, bdcPosition)));
        storageBeen.setpDischargeBDC1(usBdcObtainValueTwo(bytes, bdc04SrcIndex(resRester, 3178, bdcPosition)));
//        storageBeen.setpChargeBDC2(obtainValueTwo(bytes, 3203));
        storageBeen.setpDischargeBDC2(usBdcObtainValueTwo(bytes, bdc04SrcIndex(resRester, 3201, bdcPosition)));
//        storageBeen.seteChargeToday(usBdcObtainValueTwo(bytes, bdc04SrcIndex(resRester,3129,bdcPosition)));
//        storageBeen.seteDischargeToday(usBdcObtainValueTwo(bytes, bdc04SrcIndex(resRester,3125,bdcPosition)));
//        storageBeen.seteChargeTotal(usBdcObtainValueTwo(bytes, bdc04SrcIndex(resRester,3131,bdcPosition)));
//        storageBeen.seteDischargeTotal(usBdcObtainValueTwo(bytes, bdc04SrcIndex(resRester,3127,bdcPosition)));
        //bms1
        storageBeen.setBmsComType(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3229, bdcPosition)));
        storageBeen.setBatteryCompany(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3224, bdcPosition)));
        storageBeen.setBmsWorkType(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3211, bdcPosition)));
        storageBeen.setBmsStatus(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3212, bdcPosition)));
        storageBeen.setBmsError(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3213, bdcPosition)));
        storageBeen.setBmsWarm(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3214, bdcPosition)));
        storageBeen.setvBms(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3216, bdcPosition)));
        storageBeen.setaBms(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3217, bdcPosition)));
        storageBeen.setSoc(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3215, bdcPosition)));
        storageBeen.setSoh(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3222, bdcPosition)));
        storageBeen.setTempBms(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3218, bdcPosition)));
        storageBeen.setvCV(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3223, bdcPosition)));
        storageBeen.setaChargeMax(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3219, bdcPosition)));
        storageBeen.setaDischargeMax(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3220, bdcPosition)));


        //bms2 + 19
//        storageBeen.setBmsComType02(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester,3248,bdcPosition)));
//        storageBeen.setBatteryCompany02(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester,3243,bdcPosition)));
        storageBeen.setBmsStatus02(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3231, bdcPosition)));
        storageBeen.setBmsError02(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3232, bdcPosition)));
        storageBeen.setBmsWarm02(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3233, bdcPosition)));
//        storageBeen.setvBms02(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester,3235,bdcPosition)));
//        storageBeen.setaBms02(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester,3236,bdcPosition)));
//        storageBeen.setSoc02(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester,3234,bdcPosition)));
//        storageBeen.setTempBms02(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester,3237,bdcPosition)));
//        storageBeen.setvCV02(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester,3242,bdcPosition)));
//        storageBeen.setaChargeMax02(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester,3238,bdcPosition)));
//        storageBeen.setaDischargeMax02(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester,3238,bdcPosition)));
//        storageBeen.setvAc1(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester,3146,bdcPosition)));
//        storageBeen.setvAc2(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester,3150,bdcPosition)));
//        storageBeen.setvAc3(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester,3154,bdcPosition)));
//        storageBeen.setaAc1(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester,3147,bdcPosition)));
//        storageBeen.setaAc2(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester,3151,bdcPosition)));
//        storageBeen.setaAc3(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester,3155,bdcPosition)));
//        storageBeen.setpAc1(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester,3148,bdcPosition)));
//        storageBeen.setpAc2(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester,3152,bdcPosition)));
//        storageBeen.setpAc3(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester,3156,bdcPosition)));
//        storageBeen.setpEPSTotal(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester,3158,bdcPosition)));
//        storageBeen.setFacEPS(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester,3145,bdcPosition)));
//        storageBeen.setLoadPercent(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester,3160,bdcPosition)));
//        storageBeen.setPf(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester,3161,bdcPosition)));


        //bdc1相关
        storageBeen.setStatusBDC(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3166, bdcPosition)) & 0b11111111);
        storageBeen.setWorkModeBDC(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3166, bdcPosition)) >> 8);
        storageBeen.setvBat(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3169, bdcPosition)));
        storageBeen.setvBus1(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3172, bdcPosition)));
        storageBeen.setvBus2(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3173, bdcPosition)));
        storageBeen.setvBus3(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3188, bdcPosition)));
        storageBeen.setaBat(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3170, bdcPosition)));
        storageBeen.setaBB(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3174, bdcPosition)));
        storageBeen.setaLLC(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3175, bdcPosition)));
        storageBeen.setTempA(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3176, bdcPosition)));
        storageBeen.setTempB(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3177, bdcPosition)));
        storageBeen.setErrorStorage(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3167, bdcPosition)));
        storageBeen.setWarmStorage(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3168, bdcPosition)));
        storageBeen.setError2Storage(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3187, bdcPosition)) >> 12);
        storageBeen.setWarm2Storage(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3187, bdcPosition)) >> 8 & 0b00001111);

        //bdc1相关 + 23
        storageBeen.setStatusBDC02(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3189, bdcPosition)) & 0b11111111);
        storageBeen.setWorkModeBDC02(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3189, bdcPosition)) >> 8);
        storageBeen.setvBat02(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3192, bdcPosition)));
        storageBeen.setvBus102(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3195, bdcPosition)));
        storageBeen.setvBus202(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3196, bdcPosition)));
        storageBeen.setaBat02(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3193, bdcPosition)));
        storageBeen.setaBB02(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3197, bdcPosition)));
        storageBeen.setaLLC02(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3198, bdcPosition)));
        storageBeen.setTempA02(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3199, bdcPosition)));
        storageBeen.setTempB02(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3200, bdcPosition)));
        storageBeen.setErrorStorage02(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3190, bdcPosition)));
        storageBeen.setWarmStorage02(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3191, bdcPosition)));
        storageBeen.setError2Storage02(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3210, bdcPosition)) >> 12);
        storageBeen.setWarm2Storage02(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3210, bdcPosition)) >> 8 & 0b00001111);


        //电池保护信息
        storageBeen.setBmsProtect1(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3202, bdcPosition)));
        storageBeen.setBmsProtect2(storageBeen.getBmsError());
        storageBeen.setBmsProtect3(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3226, bdcPosition)));
        //电池告警信息
        storageBeen.setBmsWarining(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3203, bdcPosition)));
        storageBeen.setBmsWarining2(storageBeen.getBmsWarm());
        storageBeen.setBmsWarining3(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3225, bdcPosition)));
        //电池故障信息
        storageBeen.setBmsError1(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3204, bdcPosition)));
        storageBeen.setBmsError2(usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3205, bdcPosition)));

        //降额模式
        int deratMode = usBdcObtainValueOne(bytes, bdc04SrcIndex(resRester, 3165, bdcPosition));
        deviceBean.setDerateMode(deratMode);

        //新的bdc信息解析
        bdcInfoBean.setStatus(storageBeen.getStatusBDC());
        //模式
        bdcInfoBean.setWorkMode(storageBeen.getWorkModeBDC());
        //充电功率
        double pChargeBDC1 = storageBeen.getpChargeBDC1();
        bdcInfoBean.setBattery_charge_power(String.valueOf(pChargeBDC1));
        //放电功率
        double pDisChargeBDC1 = storageBeen.getpDischargeBDC1();
        double mul1 = Arith.mul(pDisChargeBDC1 + 0, 1);
        bdcInfoBean.setBattery_dischage_power(String.valueOf(mul1));
        //总充电量3184-3185
        int allCharge = usBdcObtainValueTwo(bytes, bdc04SrcIndex(resRester, 3184, bdcPosition));


        double mul2 = Arith.mul(allCharge + 0, 0.1);
        bdcInfoBean.setChage_total(String.valueOf(mul2));
        //总放电量3182-3183
        int allDisCharge = usBdcObtainValueTwo(bytes, bdc04SrcIndex(resRester, 3182, bdcPosition));
        double mul3 = Arith.mul(allDisCharge + 0, 0.1);
        bdcInfoBean.setDischarge_total(String.valueOf(mul3));

//        //故障码
//        int errorCode = obtainValueOne(bytes, 3167);
//        //故障附码
//        int errorCode2 = obtainValueOne(bytes, 3187) >> 12;
//        //告警码
//        int warningCode = obtainValueOne(bytes, 3168);
//        //告警附码
//        int warningCode2 = obtainValueOne(bytes, 3187) >> 8 & 0b00001111;
    }


    /**
     * bdc并机获取03数据截取下标
     *
     * @param resResgister 起始寄存器
     * @param olbRegister  原寄存器地址
     * @param bdcPosition  bdc position
     * @return
     */
    public static int bdc03SrcIndex(int resResgister, int olbRegister, int bdcPosition) {
        //将旧的寄存器对应上新的寄存器
        int newRegister = olbRegister + 1915 + 40 * bdcPosition;
        //根据起始寄存器获取当前处于寄存器的哪个位置
        return newRegister - resResgister;
    }

    /**
     * bdc并机获取04数据截取下标
     *
     * @param resResgister 起始寄存器
     * @param olbRegister  原寄存器地址
     * @param bdcPosition  bdc position
     * @return
     */
    public static int bdc04SrcIndex(int resResgister, int olbRegister, int bdcPosition) {
        //将旧的寄存器对应上新的寄存器
        int newRegister = olbRegister + 843 + 108 * bdcPosition;
        //根据起始寄存器获取当前处于寄存器的哪个位置
        return newRegister - resResgister;
    }
}
