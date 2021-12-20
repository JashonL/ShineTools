package com.growatt.shinetools.module.localbox.tlxh.config;

import android.content.Context;

import com.growatt.shinetools.R;
import com.growatt.shinetools.bean.UsSettingConstant;
import com.growatt.shinetools.module.localbox.max.bean.ALLSettingBean;

import java.util.ArrayList;
import java.util.List;

public class TLXHConfigControl {

    public enum TlxSettingEnum {
        TLXH_QUICK_SETTING,//TLX快速设置
        TLXH_SYSTEM_SETTING,//系统设置
        TLXH_DRY_CONTACT,//干接点设置
        TLXH_CHARGE_MANAGER,//充放电管理
        TLXH_BASIC_SETTING,//基本设置


        //------------第二层级设置--------------

        //------------------第三层级设置---------------------

    }


    public static List<ALLSettingBean> getSettingList(TLXHConfigControl.TlxSettingEnum maxSettingEnum, Context context) {
        List<ALLSettingBean> list = new ArrayList<>();
        switch (maxSettingEnum) {
            case TLXH_QUICK_SETTING:
                list = getTlxhQuickSetList(context);
                break;
            case TLXH_SYSTEM_SETTING:
                list = getTlxhSystemSetList(context);
                break;
            case TLXH_DRY_CONTACT:
                list = getTlxhDrySetList(context);
                break;
            case TLXH_BASIC_SETTING:
                list=getTlxhBasicSettingList(context);
                break;
            case TLXH_CHARGE_MANAGER:
                list = getChargeMangerSetList(context);
                break;
        }
        return list;
    }

    private static List<ALLSettingBean> getTlxhQuickSetList(Context context) {
        List<ALLSettingBean> list = new ArrayList<>();
        String[] titls = new String[]{
                context.getString(R.string.m国家安规),
                context.getString(R.string.android_key663),
                context.getString(R.string.android_key1416),
                context.getString(R.string.m423通信地址),
                context.getString(R.string.android_key256),
                "AFCI",
                context.getString(R.string.android_key746),
        };
        String[] hints = new String[]{
                "",
                "",
                "1~99",
                "",
                "",
                "",
                "",
        };
        int[] itemTypes = new int[]{
                UsSettingConstant.SETTING_TYPE_NEXT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_SELECT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_SELECT,
                UsSettingConstant.SETTING_TYPE_NEXT,
                UsSettingConstant.SETTING_TYPE_NEXT,
        };
        String[] register = new String[]{
                "",
                "",
                "",
                "",
                "",
                "",
                "",
        };
        float[] multiples = new float[]{
                1, 1, 1, 1, 1, 1, 1,
        };
        String[] units = new String[]{
                "",
                "",
                "",
                "",
                "",
                "",
                "",
        };
        int[][] funs = new int[][]{
                {3, 0, 124},//国家和安规
                {3, 45, 50},//逆变器时间
                {3, 15, 15},//语言
                {3, 30, 30},//通信地址
                {3, 533, 533},//功率采集器
                {3, 541, 543},//AFCI使能
                {3, 30, 30},
        };
        int[][] funset = new int[][]{
                {0x10, 118, 121},
                {0x10, 118, 121},
                {6, 15, 0},
                {6, 30, 0},
                {0x10, 118, 121},
                {6, 15, 0},
                {6, 30, 0},
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
        };

        String[][] items = new String[][]{
                {},
                {},
                {
                        context.getString(R.string.意大利),
                        context.getString(R.string.英语),
                        context.getString(R.string.德语),
                        context.getString(R.string.西班牙语),
                        context.getString(R.string.法语),
                        context.getString(R.string.匈牙利语),
                        context.getString(R.string.土耳其语),
                        context.getString(R.string.波兰语),
                        context.getString(R.string.葡萄牙语)
                },
                {},
                {context.getString(R.string.android_key1427), context.getString(R.string.m484电表)},
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


    private static List<ALLSettingBean> getTlxhSystemSetList(Context context) {
        List<ALLSettingBean> list = new ArrayList<>();
        String[] titls = new String[]{
                context.getString(R.string.android_key895),
                context.getString(R.string.android_key903),
                context.getString(R.string.android_key961),
                context.getString(R.string.android_key2925),
                context.getString(R.string.m414N至GND监测功能使能),
                context.getString(R.string.m415非标准电网电压范围使能),
                context.getString(R.string.android_key898),
                context.getString(R.string.android_key950),
                context.getString(R.string.android_key959),
                context.getString(R.string.m411Island使能),
                context.getString(R.string.m手动离网使能),
                context.getString(R.string.android_key558),
                context.getString(R.string.android_key560),
                context.getString(R.string.android_key1343),
                context.getString(R.string.android_key1344),
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
                UsSettingConstant.SETTING_TYPE_SWITCH,//手动离网使能
                UsSettingConstant.SETTING_TYPE_SELECT,//离网频率
                UsSettingConstant.SETTING_TYPE_SELECT,//离网电压
                UsSettingConstant.SETTING_TYPE_SELECT,//CT选择
                UsSettingConstant.SETTING_TYPE_SELECT,//电池类型选择
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
                "", "", "", "", "", "", "", "",
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
                {3, 3021, 3021},//手动离网使能
                {3, 3081, 3081},//离网频率
                {3, 3080, 3080},//离网电压
                {3, 3068, 3068},//CT选择
                {3, 3070, 3070},//电池类型选择
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
                {6, 3021, 0},//手动离网使能
                {6, 3081, 0},//离网频率
                {6, 3080, 0},//离网电压
                {6, 3068, 0},//CT选择
                {6, 3070, 0},//电池类型选择
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
                        context.getString(R.string.Independent), context.getString(R.string.dc_source), context.getString(R.string.Parallel)
                },
                {},
                {context.getString(R.string.android_key1427), context.getString(R.string.m484电表)},
                {"0", "1", "2"},
                {},
                {},
                {},
                {},
                {},
                {"50Hz", "60Hz"},
                {"230V", "208V", "240V"},
                {"cWiredCT", "cWirelessCT", "METER"},
                {"Lithium", "Lead-acid", "other"},

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


    private static List<ALLSettingBean> getTlxhDrySetList(Context context) {
        List<ALLSettingBean> list = new ArrayList<>();
        String[] titls = new String[]{
                context.getString(R.string.m干接点状态)
                , context.getString(R.string.m干接点开通的功率百分比), context.getString(R.string.m干接点关闭功率百分比)
        };
        String[] hints = new String[]{
                "(3016)", "(3017)", "(3019)"
        };
        int[] itemTypes = new int[]{
                UsSettingConstant.SETTING_TYPE_SWITCH,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT
        };
        String[] register = new String[]{
                "", "", ""
        };
        float[] multiples = new float[]{
                1, 1, 1
        };
        String[] units = new String[]{
                "", "", ""
        };
        int[][] funs = new int[][]{
                {3, 3016, 3016},//干接点状态
                {3, 3017, 3017},//干接点开通的功率百分比
                {3, 3019, 3019}//干接点关闭功率百分比
        };
        int[][] funset = new int[][]{
                {6, 3016, 0},//干接点状态
                {6, 3017, 0},//干接点开通的功率百分比
                {6, 3019, 0}//干接点关闭功率百分比
        };

        int[][] doubleFunset = new int[][]{
                {6, 45, -1},
                {6, 46, -1},
                {6, 47, -1},
        };

        String[][] items = new String[][]{
                {},
                {},
                {}
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


    private static List<ALLSettingBean> getChargeMangerSetList(Context context) {
        List<ALLSettingBean> list = new ArrayList<>();
        String[] titls = new String[]{
                context.getString(R.string.m设置充放电优先时间段),
                context.getString(R.string.mCV电压),
                context.getString(R.string.mCC电流),
                context.getString(R.string.android_key260),
                context.getString(R.string.android_key1346),
                context.getString(R.string.m充电停止SOC),
                context.getString(R.string.m放电功率百分比),
                context.getString(R.string.android_key502)
        };
        String[] hints = new String[]{
                "(3038~3059)",
                context.getString(R.string.android_key3048)+":"+ "38.0-58.0V",
                context.getString(R.string.android_key3048)+":"+ "0.0-60.0A",
                "",
                "",
                "",
                "",
                ""
        };
        int[] itemTypes = new int[]{
                UsSettingConstant.SETTING_TYPE_NEXT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_SELECT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT
        };
        String[] register = new String[]{
                "", "", "", "", "", "", "", ""
        };
        float[] multiples = new float[]{
                1, 1, 1, 1, 1, 1, 1, 1
        };
        String[] units = new String[]{
                "", "", "", "", "", "", "", ""
        };
        int[][] funs = new int[][]{
                {3, 3038, 3059},//设置充放电优先时间段
                {3, 3030, 3030},//cv电压
                {3, 3024, 3024},//干接点关闭功率百分比
                {3, 3049, 3049},//干接点状态
                {3, 3047, 3047},//干接点开通的功率百分比
                {3, 3048, 3048},//干接点关闭功率百分比
                {3, 3036, 3036},//干接点状态
                {3, 3037, 3037},//干接点开通的功率百分比
        };
        int[][] funset = new int[][]{
                {6, 3016, -1},//干接点状态
                {6, 3030, 0},//干接点开通的功率百分比
                {6, 3024, 0},//干接点关闭功率百分比
                {6, 3049, 0},//干接点状态
                {6, 3047, 0},//干接点开通的功率百分比
                {6, 3048, 0},//干接点关闭功率百分比
                {6, 3036, 0},//干接点状态
                {6, 3037, 0},//干接点开通的功率百分比
        };

        int[][] doubleFunset = new int[][]{
                {6, 45, -1},
                {6, 46, -1},
                {6, 47, -1},
                {6, 45, -1},
                {6, 46, -1},
                {6, 47, -1},
                {6, 45, -1},
                {6, 46, -1},
        };

        String[][] items = new String[][]{
                {},
                {},
                {},
                {},
                {},
                {},
                {},
                {}
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



    private static List<ALLSettingBean> getTlxhBasicSettingList(Context context) {

        List<ALLSettingBean> list = new ArrayList<>();
        String tips ="";
        String tips1 ="";
        String[] titls = new String[]{
                context.getString(R.string.m404选择通信波特率),
                context.getString(R.string.m431Modbus版本),
                context.getString(R.string.m403PV电压),
                context.getString(R.string.m435逆变器模块),
                context.getString(R.string.m436逆变器经纬度),
                context.getString(R.string.修改总发电量),
                context.getString(R.string.android_key828),

                context.getString(R.string.清除历史数据),
                context.getString(R.string.android_key1415)
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
                UsSettingConstant.SETTING_TYPE_NEXT,

        };

        String[] register = new String[]{
                "", "",
                "", "",
                "", "",
                "","",""
        };
        float[] multiples = new float[]{
                1, 1, 0.1f, 1, 1, 0.1f, 1,1,1
        };
        String[] units = new String[]{"", "", "", "", "", "kWh", "","",""
        };
        String[] hints = new String[]{
                tips, "", tips, "", tips, "", tips,tips,tips
        };
        int[][] funs = new int[][]{
                {3, 22, 22},//选择通信波特率
                {3, 88, 88},//MODBUS版本
                {3, 8, 8},//PV电压
                {3, 8, 8},//逆变器模块
                {3, 8, 8},//逆变器经纬度
                {4, 0, 99},//修改总发电量
                {3, 22, 22},//设置Model
                {3, 32, 32},//清除历史数据
                {3, 33, 33},//恢复出厂设置
        };
        int[][] funset = new int[][]{
                {6, 22, 0},
                {6, 88, 1},
                {6, 8, -1},
                {6, 2, 1},
                {6, 3, -1},
                {6, 7147, 1},
                {6, 3, -1},
                {6, 32, 1},
                {6, 33, 1},
        };

        String[][] items = new String[][]{
                {"9600bps", "38400bps", "115200bps"},//选择通信波特率
                {},//MODBUS版本
                {},//PV电压
                {},//逆变器模块
                {},//逆变器经纬度
                {},//修改总发电量
                {},//设置Model
                {},//清除历史数据
                {},//恢复出厂设置
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
