package com.growatt.shinetools.module.inverterUpdata;

import com.growatt.shinetools.modbusbox.RegisterParseUtil;

public class UpdataUtils {

    /**
     * 检查返回内容是否正确：06：查看返回结构是否是0106格式
     * @param desc:完整返回内容，保留数服协议和modbus协议
     * @return
     */
    public static boolean checkReceiver0617(byte[] desc){
        //移除数服协议
        byte[] bs1 = RegisterParseUtil.removePro(desc);
        return checkReceiver(bs1);
    }



    /**
     * 高级设置检查是06判断成功：否则失败
     * @param desc：去除了数服协议，保留了modbus协议
     * @return
     */
    //查看返回结构是否是0106格式
    public static boolean checkReceiver(byte[] desc){
        if (desc == null || desc.length < 2){ return false;}
        if (desc[1] == 6||desc[1]==17){
            return true;
        }else {
            return false;
        }
    }


}
