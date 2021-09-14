package com.growatt.shinetools.modbusbox;




import com.growatt.shinetools.modbusbox.bean.DatalogResponBean;
import com.growatt.shinetools.utils.CommenUtils;
import com.growatt.shinetools.utils.Log;

import java.util.ArrayList;
import java.util.List;

public class DataLogApDataParseUtil {

    //wifi名称
    public static final int WIFI_SSID = 56;
    //wifi密码
    public static final int WIFI_PASSWORD = 57;
    //dhcp
    public static final int NET_DHCP = 71;
    //路由器ip
    public static final int REMOTE_IP = 17;
    //网关
    public static final int DEFAULT_GATEWAY = 26;
    //掩码
    public static final int SUBNET_MASK = 25;
    //时间间隔
    public static final int DATA_INTERVAL = 4;
    //采集器时间
    public static final int SYSTEM_TIME = 31;
    //服务器域名
    public static final int REMOTE_URL = 19;
    //采集器IP
    public static final int LOCAL_IP = 14;
    //服务器端口
    public static final int REMOTE_PORT = 18;
    //采集器序列号
    public static final int DATALOGGER_SN = 8;
    //MAC地址
    public static final int LOCAL_MAC = 16;
    //采集器设备类型
    public static final int DATALOGGER_TYPE = 13;
    //软件版本号
    public static final int FIRMWARE_VERSION = 21;
    //升级的文件类型
    public static final int FOTA_FILE_TYPE=65;
    //采集器重启命令
    public static final int DATALOGGER_RESTART=32;
    //WIFI连接状态
    public static final int LINK_STATUS=60;

    /**
     * 去除外部协议，只留实际内容,0x18命令
     */
    public static byte[] removePro(byte[] bytes) throws Exception {
        if (bytes == null) return null;
        int len = bytes.length;
        if (len > 8) {
            byte[] bs = new byte[len - 8];
            System.arraycopy(bytes, 8, bs, 0, len - 8);
            Log.d(CommenUtils.bytesToHexString(bytes));
            byte[] noCrc=new byte[bs.length-2];
            System.arraycopy(bs, 0, noCrc, 0, noCrc.length);
            return noCrc;
        }
        return bytes;
    }


    /**
     * @param bytes
     * @return
     * @throws Exception
     */
    public static DatalogResponBean paserData(byte type, byte[] bytes) throws Exception {
        if (bytes == null || bytes.length < 8) return null;
        DatalogResponBean bean;
        if (type == DatalogApUtil.DATALOG_GETDATA_0X18) {
            bean = parserfun0x18(bytes);
            bean.setFuncode(DatalogApUtil.DATALOG_GETDATA_0X18);
        } else if (type == DatalogApUtil.DATALOG_GETDATA_0X19) {
            bean = parserfun0x19(bytes);
            bean.setFuncode(DatalogApUtil.DATALOG_GETDATA_0X19);
        } else {
            bean = parserfun0x26(bytes);
            bean.setFuncode(DatalogApUtil.DATALOG_GETDATA_0X26);
        }
        return bean;
    }


    /**
     * 解析0x19应答
     *
     * @param bytes
     * @return
     */

    public static DatalogResponBean parserfun0x19(byte[] bytes) {
        if (bytes == null || bytes.length < 13) return null;
        DatalogResponBean bean = new DatalogResponBean();
        //设备序列号10个字节
        byte[] serialNumByte = new byte[10];
        System.arraycopy(bytes, 0, serialNumByte, 0, serialNumByte.length);
        String dataLogSerial = CommenUtils.ByteToString(serialNumByte);
        bean.setDatalogSerial(dataLogSerial);
        Log.d("设备序列号：" + dataLogSerial);
        //参数编号个数2个字节
        byte[] paramNumByte = new byte[2];
        System.arraycopy(bytes, serialNumByte.length, paramNumByte, 0, paramNumByte.length);
        int paramNum = bytes2Int(paramNumByte);
        bean.setParamNum(paramNum);
        Log.d("参数编号个数：" + paramNum);
        //状态码1个字节
        byte[] statusCodeByte = new byte[1];
        System.arraycopy(bytes, serialNumByte.length + paramNumByte.length, statusCodeByte, 0, statusCodeByte.length);
        int statusCode = statusCodeByte[0];
        bean.setStatusCode(statusCode);
        Log.d("状态码：" + statusCode);
        //参数编号对应的数据
        byte[] datas = new byte[bytes.length - 13];
        System.arraycopy(bytes, serialNumByte.length + paramNumByte.length + statusCodeByte.length, datas, 0, datas.length);

        List<DatalogResponBean.ParamBean> datalist = new ArrayList<>();

        //记录当前长度
        int allLenth = 0;
        for (int i = 0; i < paramNum; i++) {
            //参数编号2个字节
            byte[] numByte = new byte[2];
            System.arraycopy(datas, allLenth, numByte, 0, numByte.length);
            int num = bytes2Int(numByte);
            Log.d("参数编号：" + num);
            //参数长度2个字节
            byte[] lengthByte = new byte[2];
            System.arraycopy(datas, allLenth + numByte.length, lengthByte, 0, lengthByte.length);
            int length = bytes2Int(lengthByte);
            Log.d("数据长度：" + length);
            //数据
            byte[] valueByte = new byte[length];
            System.arraycopy(datas, allLenth + numByte.length + lengthByte.length, valueByte, 0, length);
            String value="";
        /*    if (num==FOTA_FILE_TYPE){//转成int
                 value=String.valueOf(bytes2Int(valueByte));
            }else {
                 value = CommenUtils.ByteToString(valueByte);
            }*/
            value = CommenUtils.ByteToString(valueByte);
            Log.d("对应数据：" + value);

            DatalogResponBean.ParamBean paramBean = new DatalogResponBean.ParamBean();
            paramBean.setLength(length);
            paramBean.setNum(num);
            paramBean.setValue(value);
            datalist.add(paramBean);


            allLenth += 4 + length;

        }
        bean.setParamBeanList(datalist);
        return bean;
    }


    /**
     * 解析0x18应答
     *
     * @param bytes
     * @return
     */

    public static DatalogResponBean parserfun0x18(byte[] bytes) {
        if (bytes == null || bytes.length < 13) return null;
        DatalogResponBean bean = new DatalogResponBean();
        //设备序列号10个字节
        byte[] serialNumByte = new byte[10];
        System.arraycopy(bytes, 0, serialNumByte, 0, serialNumByte.length);
        String dataLogSerial = CommenUtils.ByteToString(serialNumByte);
        bean.setDatalogSerial(dataLogSerial);
        Log.d("设备序列号：" + dataLogSerial);
        //参数编号个数2个字节
        byte[] paramNumByte = new byte[2];
        System.arraycopy(bytes, serialNumByte.length, paramNumByte, 0, paramNumByte.length);
        int paramNum = bytes2Int(paramNumByte);
        bean.setParamNum(paramNum);
        Log.d("参数编号个数：" + paramNum);
        //状态码1个字节
        byte[] statusCodeByte = new byte[1];
        System.arraycopy(bytes, serialNumByte.length + paramNumByte.length, statusCodeByte, 0, statusCodeByte.length);
        int statusCode = statusCodeByte[0];
        bean.setStatusCode(statusCode);
        Log.d("状态码：" + statusCode);
        return bean;
    }


    /**
     * 解析0x26应答
     *
     * @param bytes
     * @return
     */

    public static DatalogResponBean parserfun0x26(byte[] bytes) {
        if (bytes == null || bytes.length < 10) return null;
        DatalogResponBean bean = new DatalogResponBean();
        //设备序列号10个字节
        byte[] serialNumByte = new byte[10];
        System.arraycopy(bytes, 0, serialNumByte, 0, serialNumByte.length);
        String dataLogSerial = CommenUtils.ByteToString(serialNumByte);
        bean.setDatalogSerial(dataLogSerial);
        Log.d("设备序列号：" + dataLogSerial);


        byte[] datas = new byte[bytes.length - 10];
        System.arraycopy(bytes, serialNumByte.length , datas, 0, datas.length);
        //参数编号个数2个字节
        List<DatalogResponBean.ParamBean> datalist = new ArrayList<>();
        //记录当前长度
        //文件数据分包总数量
        byte[] totalByte = new byte[2];
        System.arraycopy(datas, 0, totalByte, 0, totalByte.length);
        int total = bytes2Int(totalByte);
        Log.d("文件数据分包总数量：" + total);
        //当前数据包编号
        byte[] numByte = new byte[2];
        System.arraycopy(datas,  totalByte.length, numByte, 0, numByte.length);
        int num = bytes2Int(numByte);
        Log.d("当前数据包编号：" + num);
        //当前数据包接收状态码
        //状态码1个字节
        byte[] statusCodeByte = new byte[1];
        System.arraycopy(datas,totalByte.length+numByte.length , statusCodeByte, 0, statusCodeByte.length);
        int statusCode = statusCodeByte[0];
        Log.d("状态码：" + statusCode);

        DatalogResponBean.ParamBean paramBean = new DatalogResponBean.ParamBean();
        paramBean.setTotalLength(total);
        paramBean.setDataNum(num);
        paramBean.setDataCode(statusCode);
        datalist.add(paramBean);

        bean.setParamBeanList(datalist);
        return bean;
    }


    public static int bytes2Int(byte[] bytes) {
        int result;
        //将每个byte依次搬运到int相应的位置
        result = bytes[0] & 0xff;
        result = result << 8 | bytes[1] & 0xff;
        return result;
    }


}
