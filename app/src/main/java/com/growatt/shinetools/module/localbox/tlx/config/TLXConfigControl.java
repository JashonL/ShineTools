package com.growatt.shinetools.module.localbox.tlx.config;

import android.content.Context;

import com.growatt.shinetools.R;
import com.growatt.shinetools.bean.UsSettingConstant;
import com.growatt.shinetools.module.localbox.max.bean.ALLSettingBean;

import java.util.ArrayList;
import java.util.List;

public class TLXConfigControl {

    public enum TlxSettingEnum {
        TLX_SYSTEM_SETTING,//TLX系统设置
        TLX_BASIC_SETTING,//TLX基本设置

    }




    public static List<ALLSettingBean> getSettingList(TLXConfigControl.TlxSettingEnum maxSettingEnum, Context context) {
        List<ALLSettingBean> list = new ArrayList<>();
        switch (maxSettingEnum) {
            case TLX_SYSTEM_SETTING:
                list= getTlxSystemSetList(context);
                break;
            case TLX_BASIC_SETTING:
                list= getTlxBasicSettingList(context);
                break;
        }
        return list;
    }



    private static List<ALLSettingBean> getTlxSystemSetList(Context context) {
        List<ALLSettingBean> list = new ArrayList<>();
        String[] titls = new String[]{
                context.getString(R.string.android_key895),//开关逆变器  0
                context.getString(R.string.android_key903),//有功功率百分比 2
                context.getString(R.string.android_key961),//PV输入模式 9
                context.getString(R.string.android_key2925),//干接点设置 11
                context.getString(R.string.m414N至GND监测功能使能),//N至PE监测功能使能 6
                context.getString(R.string.m415非标准电网电压范围使能),//宽电网电压范围使能 7
                context.getString(R.string.android_key898),//安规功能使能 1
                context.getString(R.string.android_key950),//电网N线使能 5
                context.getString(R.string.android_key959),//指定的规格设置使能 8
                context.getString(R.string.m411Island使能),//ISland使能 3

                //增加项
                context.getString(R.string.m412风扇检查),//风扇检查 4
                context.getString(R.string.android_key873),//检查固件 10

                context.getString(R.string.android_key1008),//PID工作模式 12
                context.getString(R.string.android_key1011),//PID开关  13
                context.getString(R.string.android_key1014),//PID工作电压选择  14

        };
        String[] hints = new String[]{
                "",
                "",
                "1~99",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
        };
        int[] itemTypes = new int[]{
                UsSettingConstant.SETTING_TYPE_SWITCH,//开关逆变器
                UsSettingConstant.SETTING_TYPE_NEXT,//有功功率百分比
                UsSettingConstant.SETTING_TYPE_SELECT,//PV输入模式
                UsSettingConstant.SETTING_TYPE_NEXT,//干接点设置
                UsSettingConstant.SETTING_TYPE_SWITCH,//N至PE监测功能使能
                UsSettingConstant.SETTING_TYPE_SELECT,//宽电网电压范围使能
                UsSettingConstant.SETTING_TYPE_SWITCH,//安规功能使能
                UsSettingConstant.SETTING_TYPE_SWITCH,//电网N线使能
                UsSettingConstant.SETTING_TYPE_SWITCH,//指定的规格设置使能
                UsSettingConstant.SETTING_TYPE_SWITCH,//ISland使能




                UsSettingConstant.SETTING_TYPE_SWITCH,//风扇检查
                UsSettingConstant.SETTING_TYPE_NEXT,//检查固件

                UsSettingConstant.SETTING_TYPE_SELECT,//PID工作模式
                UsSettingConstant.SETTING_TYPE_SWITCH,//PID开关
                UsSettingConstant.SETTING_TYPE_INPUT,//PID工作电压选择
        };
        String[] register = new String[]{
                "", "", "", "", "", "", "", "",
                "", "", "", "", "", "", ""
        };
        float[] multiples = new float[]{
                1, 1, 1, 1, 1, 1, 1,
                1, 1, 1, 1, 1, 1, 1,
                1
        };
        String[] units = new String[]{
                "", "", "", "", "", "", "","",
                "", "", "", "", "", "", "",
        };
        int[][] funs = new int[][]{
                {3, 0, 0},//开关逆变器
                {3, 0, 5},//有功功率百分比
                {3, 399, 399},//PV输入模式
                {3, 3016, 3019},//干接点设置
                {3, 235, 235},//N至PE检测功能使能
                {3, 236, 236},//宽电网电压范围使能
                {3, 1, 1},//安规功能使能
                {3, 232, 232},//电网N线使能
                {3, 237, 237},//指定的规格设置使能
                {3, 230, 230},//ISLand使能



                {3, 231, 231},//风扇检查
                {3, 233, 234},//检查固件

                {3, 201, 201},//PID工作模式
                {3, 202, 202},//PID开关
                {3, 203, 203},//PID工作电压选择
        };
        int[][] funset = new int[][]{
                {6, 0, 0},//开关逆变器
                {6, 0, 0},//有功功率百分比
                {6, 399, 0},//PV输入模式
                {6, 3016, 0},//干接点设置
                {6, 235, 0},//N至PE检测功能使能
                {6, 236, 0},//宽电网电压范围使能
                {6, 1, 0},//安规功能使能
                {6, 232, 0},//电网N线使能
                {6, 237, 0},//指定的规格设置使能
                {6, 230, 0},//ISLand使能



                {6, 3070, 0},//风扇检查
                {6, 3070, 0},//检查固件

                {3, 231, 0},//PID工作模式
                {3, 233, 0},//PID开关
                {3, 203, 0},//PID工作电压选择
        };

        int[][] doubleFunset = new int[][]{
                {6, 45, -1},
                {6, 46, -1},
                {6, 47, -1},
                {6, 48, -1},
                {6, 49, -1},
                {6, 50, -1},
                {6, 48, -1},
                {6, 49, -1},
                {6, 45, -1},
                {6, 46, -1},
                {6, 47, -1},
                {6, 48, -1},
                {6, 49, -1},
                {6, 50, -1},



                {6, 48, -1},
        };

        String[][] items = new String[][]{
                {},
                {},
                {
                   "0","1","2"
                },
                {},
                {context.getString(R.string.android_key1427),context.getString(R.string.m484电表)},
                { "0","1","2"},
                {},
                {},
                {},
                {},
                {},
                {},
                {context.getString(R.string.m438自动), context.getString(R.string.Continual),context.getString(R.string.Overnight)},//PID工作模式
                {},
                {},


        };

        for (int i = 0; i < titls.length; i++) {
            ALLSettingBean bean = new ALLSettingBean();
            bean.setTitle(titls[i]);
            bean.setItemType(itemTypes[i]);
            bean.setRegister(register[i]);
            bean.setUnit(units[i]);
            bean.setFuns(funs[i]);
            bean.setFunSet(funset[i]);
            bean.setItems(items[i]);
            bean.setHint(hints[i]);
            bean.setDoubleFunset(doubleFunset);
            bean.setMul(multiples[i]);
            list.add(bean);
        }
        return list;
    }


    private static List<ALLSettingBean> getTlxBasicSettingList(Context context) {

        List<ALLSettingBean> list = new ArrayList<>();
        String tips = "";
        String tips1 = context.getString(R.string.android_key3048) + ":" + "0~255";
        String[] titls = new String[]{
                context.getString(R.string.m404选择通信波特率),
                context.getString(R.string.m431Modbus版本),
                context.getString(R.string.m403PV电压),
                context.getString(R.string.m435逆变器模块),
                context.getString(R.string.m436逆变器经纬度),
                context.getString(R.string.修改总发电量),
                context.getString(R.string.android_key828),


                context.getString(R.string.android_key996),
        };

        int[] itemTypes = new int[]{
                UsSettingConstant.SETTING_TYPE_SELECT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_ONLYREAD,
                UsSettingConstant.SETTING_TYPE_ONLYREAD,
                UsSettingConstant.SETTING_TYPE_ONLYREAD,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_NEXT,

                UsSettingConstant.SETTING_TYPE_NEXT,
        };

        String[] register = new String[]{
                "", "",
                "", "",
                "", "",
                "",""
        };
        float[] multiples = new float[]{
                1, 1, 0.1f, 1, 1, 0.1f, 1,1
        };
        String[] units = new String[]{"", "", "", "", "", "kWh", "",""
        };
        String[] hints = new String[]{
                tips, "", tips, "", tips, "", tips,""
        };
        int[][] funs = new int[][]{
                {3, 22, 22},//选择通信波特率
                {3, 88, 88},//MODBUS版本
                {3, 8, 8},//PV电压
                {3, 8, 8},//逆变器模块
                {3, 8, 8},//逆变器经纬度
                {4, 0, 99},//修改总发电量
                {3, 22, 22},//设置Model

                {3, 51, 51},//系统/星期
        };
        int[][] funset = new int[][]{
                {6, 22, 0},
                {6, 88, 1},
                {6, 8, -1},
                {6, 2, 1},
                {6, 3, -1},
                {6, 7147, 1},
                {6, 3, -1},

                {6, 51, -1}//系统/星期
        };

        String[][] items = new String[][]{
                {"9600bps", "38400bps", "115200bps"},//选择通信波特率
                {},//MODBUS版本
                {},//PV电压
                {},//逆变器模块
                {},//逆变器经纬度
                {},//修改总发电量
                {},//设置Model

                {},//系统/星期
        };
        int[][] doubleFunset = new int[][]{
                {6, 7147, -1}, {6, 7148, -1}

        };
        int[][][] threeFunset = new int[][][]{
                {{6, 2, 1}, {6, 3, -1}},//有功功率百分比设置
                {{6, 233, -1}, {6, 234, -1}}//检查固件
        };
        for (int i = 0; i < titls.length; i++) {
            ALLSettingBean bean = new ALLSettingBean();
            bean.setTitle(titls[i]);
            bean.setItemType(itemTypes[i]);
            bean.setRegister(register[i]);
            bean.setUnit(units[i]);
            bean.setFuns(funs[i]);
            bean.setFunSet(funset[i]);
            bean.setItems(items[i]);
            bean.setHint(hints[i]);
            bean.setDoubleFunset(doubleFunset);
            bean.setThreeFunSet(threeFunset);
            bean.setMul(multiples[i]);
            list.add(bean);
        }
        return list;
    }

}
