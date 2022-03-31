package com.growatt.shinetools.module.localbox.sph.config;

import android.content.Context;

import com.growatt.shinetools.R;
import com.growatt.shinetools.bean.UsSettingConstant;
import com.growatt.shinetools.module.localbox.max.bean.ALLSettingBean;

import java.util.ArrayList;
import java.util.List;

public class SPHSPAConfigControl {


    public enum SphSpaSettingEnum {
        SPH_SPA_QUICK_SETTING,//SPH快速设置
        SPH_SPA_SYYSTEM_SETTING,//SPH系统设置
        SPH_SPA_GRID_CODE_SETTING,//SPH市电码设置
        SPH_SPA_BASIC_SETTING//SPH基本设置
        //------------第二层级设置--------------

        //------------------第三层级设置---------------------

    }

    public static List<ALLSettingBean> getSettingList(SPHSPAConfigControl.SphSpaSettingEnum spaSettingEnum, Context context) {
        List<ALLSettingBean> list = new ArrayList<>();
        switch (spaSettingEnum) {
            case SPH_SPA_QUICK_SETTING:
                list = getSphSpaQuickSetList(context);
                break;
            case SPH_SPA_SYYSTEM_SETTING:
                list = getSphSpaSystemSetList(context);
                break;
            case SPH_SPA_GRID_CODE_SETTING:
                list = getSphSpaGridCodeList(context);
                break;
            case SPH_SPA_BASIC_SETTING:
                list = getSphSpaBasicSettingList(context);
                break;
        }
        return list;
    }


    private static List<ALLSettingBean> getSphSpaQuickSetList(Context context) {
        List<ALLSettingBean> list = new ArrayList<>();
        String[] titls = new String[]{
                context.getString(R.string.m国家安规),//国家安规
                context.getString(R.string.android_key663),//逆变器时间
                context.getString(R.string.android_key1416),//语言
                context.getString(R.string.m423通信地址),//通信地址

                context.getString(R.string.android_key746),//防逆流设置
        };
        String[] hints = new String[]{
                "",
                "",
                "1~99",
                "",
                "",
        };
        int[] itemTypes = new int[]{
                UsSettingConstant.SETTING_TYPE_NEXT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_SELECT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_NEXT,
        };
        String[] register = new String[]{
                "",
                "",
                "",
                "",
                "",
        };
        float[] multiples = new float[]{
                1, 1, 1, 1, 1
        };
        String[] units = new String[]{
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
                {3, 30, 30},
        };
        int[][] funset = new int[][]{
                {0x10, 118, 121},
                {0x10, 118, 121},
                {6, 15, 0},
                {6, 30, 0},
                {6, 30, 0},
        };

        int[][] doubleFunset = new int[][]{
                {6, 45, -1},
                {6, 46, -1},
                {6, 47, -1},
                {6, 48, -1},
                {6, 49, -1},
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


    private static List<ALLSettingBean> getSphSpaSystemSetList(Context context) {

        List<ALLSettingBean> list = new ArrayList<>();
        String[] titls = new String[]{
                context.getString(R.string.android_key895),//开关逆变器 0
                context.getString(R.string.android_key903),//有功功率百分比 1
                context.getString(R.string.android_key961),//PV输入模式 2
                context.getString(R.string.android_key898),//安规功能使能 3
                context.getString(R.string.m手动离网使能),//手动离网使能 4
                context.getString(R.string.android_key558),//离网频率 6
                context.getString(R.string.android_key560),//离网电压 5
                context.getString(R.string.android_key1343),//CT选择 7
                context.getString(R.string.android_key1344),//电池类型选择 8

                //增加项
                context.getString(R.string.android_key2391),//使能SPI 9
                context.getString(R.string.android_key2392),//使能电压穿越 10
                context.getString(R.string.m389检查固件),//检查固件 11
                context.getString(R.string.m412风扇检查),//风扇检查 12
                context.getString(R.string.m432PID工作模式),//PID工作模式 13
                context.getString(R.string.m433PID开关),//PID开关 14
                context.getString(R.string.m434PID工作电压),//PID工作电压选择 15

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


                //增加项
                "",//使能SPI 9
                "",//使能电压穿越 10
                "",//检查固件 11
                "",//风扇检查 12
                "",//PID工作模式 13
                "",//PID开关 14
                "",//PID工作电压选择 15
        };
        int[] itemTypes = new int[]{
                UsSettingConstant.SETTING_TYPE_SWITCH,//开关逆变器
                UsSettingConstant.SETTING_TYPE_NEXT,//有功功率百分比
                UsSettingConstant.SETTING_TYPE_SELECT,//PV输入模式
                UsSettingConstant.SETTING_TYPE_INPUT,//安规功能使能
                UsSettingConstant.SETTING_TYPE_SWITCH,//手动离网使能
                UsSettingConstant.SETTING_TYPE_INPUT,//离网频率
                UsSettingConstant.SETTING_TYPE_INPUT,//离网电压
                UsSettingConstant.SETTING_TYPE_SELECT,//CT选择
                UsSettingConstant.SETTING_TYPE_SELECT,//电池类型选择

                //增加项
                UsSettingConstant.SETTING_TYPE_SWITCH,//使能SPI 9
                UsSettingConstant.SETTING_TYPE_SWITCH,//使能电压穿越 10
                UsSettingConstant.SETTING_TYPE_NEXT,//检查固件 11
                UsSettingConstant.SETTING_TYPE_SWITCH,//风扇检查 12
                UsSettingConstant.SETTING_TYPE_SELECT,//PID工作模式 13
                UsSettingConstant.SETTING_TYPE_SWITCH,//PID开关 14
                UsSettingConstant.SETTING_TYPE_INPUT,//PID工作电压选择 15

        };
        String[] register = new String[]{
                "", "", "", "", "", "", "", "",
                "", "", "", "", "", "", "",""
        };
        float[] multiples = new float[]{
                1, 1, 1, 1, 1, 1, 1,
                1, 1, 1, 1, 1, 1, 1,
                1,
                1
        };
        String[] units = new String[]{
                "", "", "", "", "", "", "","",
                "", "", "", "", "", "", "",
                ""
        };
        int[][] funs = new int[][]{
                {3, 0, 0},//开关逆变器
                {3, 0, 5},//有功功率百分比
                {3, 399, 399},//PV输入模式
                {3, 1, 1},//安规功能使能
                {3, 3021, 3021},//手动离网使能
                {3, 3081, 3081},//离网频率
                {3, 3080, 3080},//离网电压
                {3, 3068, 3068},//CT选择
                {3, 3070, 3070},//电池类型选择


                //增加项
                {3, 1, 1},
                {3, 3070, 3070},//使能电压穿越 10
                {3, 3070, 3070},//检查固件 11
                {3, 3070, 3070},//风扇检查 12
                {3, 3070, 3070},//PID工作模式 13
                {3, 3070, 3070},//PID开关 14
                {3, 3070, 3070},//PID工作电压选择 15

        };
        int[][] funset = new int[][]{
                {6, 0, 0},//开关逆变器
                {6, 0, 0},//有功功率百分比
                {6, 399, 0},//PV输入模式
                {6, 1, 0},//安规功能使能
                {6, 3021, 0},//手动离网使能
                {6, 3081, 0},//离网频率
                {6, 3080, 0},//离网电压
                {6, 3068, 0},//CT选择
                {6, 3070, 0},//电池类型选择

                //增加项
                {3, 3, 0},//使能SPI 9
                {3, 3070, 3070},//使能电压穿越 10
                {3, 3070, 3070},//检查固件 11
                {3, 3070, 3070},//风扇检查 12
                {3, 3070, 3070},//PID工作模式 13
                {3, 3070, 3070},//PID开关 14
                {3, 3070, 3070},//PID工作电压选择 15
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


                //增加项
                {3, 3070, 3070},//使能SPI 9
                {3, 3070, 3070},//使能电压穿越 10
                {3, 3070, 3070},//检查固件 11
                {3, 3070, 3070},//风扇检查 12
                {3, 3070, 3070},//PID工作模式 13
                {3, 3070, 3070},//PID开关 14
                {3, 3070, 3070},//PID工作电压选择 15
        };

        String[][] items = new String[][]{
                {},
                {},
                {
                        context.getString(R.string.Independent), context.getString(R.string.dc_source), context.getString(R.string.Parallel)
                },
                {},
                {},
                {},
                {},
                {"cable CT","SP-CT","ThreePhaseMeter"},
                {"lead-acod","Lithium"},
                {},
                {},
                {},
                {},
                {},
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


    private static List<ALLSettingBean> getSphSpaGridCodeList(Context context) {

        List<ALLSettingBean> list = new ArrayList<>();
        String tips = "0.9Vn~1.08Vn("+context.getString(R.string.vn_is_rated_vol)+")";
        String[] titls = new String[]{
                context.getString(R.string.pf_setting),
                context.getString(R.string.频率有功),
                context.getString(R.string.android_key2438),
                context.getString(R.string.上升斜率),
                context.getString(R.string.ac_voltage_protect),
                context.getString(R.string.ac_frency_protect),
                context.getString(R.string.并网范围),
                context.getString(R.string.m429AC电压10分钟保护值),
                context.getString(R.string.android_key1002),
        };

        int[] itemTypes = new int[]{
                UsSettingConstant.SETTING_TYPE_NEXT,
                UsSettingConstant.SETTING_TYPE_NEXT,
                UsSettingConstant.SETTING_TYPE_NEXT,
                UsSettingConstant.SETTING_TYPE_NEXT,
                UsSettingConstant.SETTING_TYPE_NEXT,
                UsSettingConstant.SETTING_TYPE_NEXT,
                UsSettingConstant.SETTING_TYPE_NEXT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
        };

        String[] register = new String[]{
                "", "",
                "", "",
                "", "",
                "", "",
                ""
        };
        float[] multiples = new float[]{
                1, 1, 0.1f, 1, 1, 0.1f, 1, 0.1f, 0.1f
        };
        String[] units = new String[]{"", "", "", "", "", "", "", "V", "V"
        };
        String[] hints = new String[]{
                tips, "", tips, "", tips, "", tips, "", ""
        };
        int[][] funs = new int[][]{
                {3, 22, 22},//PF设置
                {3, 88, 88},//频率有功
                {3, 8, 8},//电压无功
                {3, 8, 8},//电源启动/重启斜率
                {3, 8, 8},//AC电压保护
                {4, 0, 99},//AC频率保护
                {3, 22, 22},//并网范围
                {3, 80, 80},//10分钟平均AC电压保护值
                {3, 81, 81},//PV过压保护点
        };
        int[][] funset = new int[][]{
                {6, 22, 0},//PF设置
                {6, 88, 0},//频率有功
                {6, 8, 0},//电压无功
                {6, 8, 0},//电源启动/重启斜率
                {6, 8, 0},//AC电压保护
                {6, 0, 0},//AC频率保护
                {6, 22, 0},//并网范围
                {6, 80, -1},//10分钟平均AC电压保护值
                {6, 81, -1},//PV过压保护点
        };

        String[][] items = new String[][]{
                {},//PF设置
                {},//频率有功
                {},//电压无功
                {},//电源启动/重启斜率
                {},//AC电压保护
                {},//AC频率保护
                {},//并网范围
                {},//10分钟平均AC电压保护值
                {},//PV过压保护点
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

    private static List<ALLSettingBean> getSphSpaBasicSettingList(Context context) {

        List<ALLSettingBean> list = new ArrayList<>();
        String tips = context.getString(R.string.android_key3048) + ":" + "0~65000" + "(" + context.getString(R.string.AFCI阈值) + 1
                + "<" + context.getString(R.string.AFCI阈值) + 2 + "<" + context.getString(R.string.AFCI阈值) + 3 + ")";
        String tips1 = context.getString(R.string.android_key3048) + ":" + "0~255";
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

                UsSettingConstant.SETTING_TYPE_SELECT,//清除历史数据
                UsSettingConstant.SETTING_TYPE_SELECT,//恢复出厂设置
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
                tips, "", tips, "", tips, "", tips,"","","",""
        };
        int[][] funs = new int[][]{
                {3, 22, 22},//选择通信波特率
                {3, 88, 88},//MODBUS版本
                {3, 8, 8},//PV电压
                {3, 8, 8},//逆变器模块
                {3, 8, 8},//逆变器经纬度
                {4, 0, 99},//修改总发电量
                {3, 22, 22},//设置Model

                {4, 32, 32},//清除历史数据
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

                {6, 32, 1},//清除历史数据
                {6, 33, 1},//恢复出厂设置
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
