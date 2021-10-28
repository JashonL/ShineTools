package com.growatt.shinetools.module.localbox.max.config;

import android.content.Context;

import com.growatt.shinetools.R;
import com.growatt.shinetools.bean.UsSettingConstant;
import com.growatt.shinetools.module.localbox.max.bean.MaxSettingBean;

import java.util.ArrayList;
import java.util.List;

public class MaxConfigControl {

    public enum MaxSettingEnum {
        MAX_QUICK_SETTING,//快速设置
        MAX_AFCI_FUNCTION,//AFCI设置
        MAX_SYSTEM_SETTING,//系统设置
        MAX_BASIC_SETTING,//基本设置
        MAX_GRID_CODE_PARAMETERS_SETTING,//安规参数设置
        MAX_ACTIVE0POWER_SETTING//有功功率设置

    }


    public static List<MaxSettingBean> getSettingList(MaxSettingEnum maxSettingEnum, Context context) {
        List<MaxSettingBean> list = new ArrayList<>();

        switch (maxSettingEnum) {
            case MAX_QUICK_SETTING:
                list = getQuickSetList(context);
                break;
            case MAX_AFCI_FUNCTION:
                list = getAFCISetList(context);
                break;
            case MAX_BASIC_SETTING:
                list = getMaxBasicSettingList(context);
                break;

            case MAX_SYSTEM_SETTING:
                list = getMaxSystemSetList(context);
                break;

            case MAX_GRID_CODE_PARAMETERS_SETTING:
                list = getMaxGridCodeSettingList(context);
                break;

            case MAX_ACTIVE0POWER_SETTING:
                list = getMaxActivePowerList(context);
                break;

        }


        return list;
    }


    private static List<MaxSettingBean> getQuickSetList(Context context) {
        List<MaxSettingBean> list = new ArrayList<>();
        String[] titls = new String[]{
                context.getString(R.string.m国家安规),
                context.getString(R.string.android_key663),
                context.getString(R.string.android_key1416),
                context.getString(R.string.android_key982),
                context.getString(R.string.android_key2922),
        };
        String[] hints = new String[]{
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
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT
        };
        String[] register = new String[]{
                "",
                "",
                "",
                "",
                ""
        };
        float[] multiples = new float[]{
                1, 1, 1, 1, 1
        };
        String[] units = new String[]{
                "",
                "",
                "",
                "",
                ""
        };
        int[][] funs = new int[][]{
                {3, 0, 124},
                {3, 45, 50},
                {3, 15, 15},
                {3, 30, 30},
                {3, 0, 124}
        };
        int[][] funset = new int[][]{
                {0x10, 118, 121},
                {0x10, 118, 121},
                {6, 15, 0},
                {6, 30, 0},
                {0x10, 118, 121}
        };

        int[][] doubleFunset = new int[][]{{6, 45, -1}, {6, 46, -1}, {6, 47, -1}, {6, 48, -1}, {6, 49, -1}, {6, 50, -1}};

        String[][] items = new String[][]{
                {},
                {},
                {
                        context.getString(R.string.android_key1417),
                        context.getString(R.string.android_key1418),
                        context.getString(R.string.android_key1420),
                        context.getString(R.string.android_key1421),
                        context.getString(R.string.android_key1422),
                        context.getString(R.string.android_key1423),
                        context.getString(R.string.android_key186),
                },
                {},
                {}

        };

        for (int i = 0; i < titls.length; i++) {
            MaxSettingBean bean = new MaxSettingBean();
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


    private static List<MaxSettingBean> getAFCISetList(Context context) {
        List<MaxSettingBean> list = new ArrayList<>();
        String tips = context.getString(R.string.android_key3048) + ":" + "0~65000" + "(" + context.getString(R.string.AFCI阈值) + 1
                + "<" + context.getString(R.string.AFCI阈值) + 2 + "<" + context.getString(R.string.AFCI阈值) + 3 + ")";
        String tips1 = context.getString(R.string.android_key3048) + ":" + "0~255";
        String[] titls = new String[]{
                context.getString(R.string.AFCI阈值) + 1,
                context.getString(R.string.AFCI阈值) + 2,
                context.getString(R.string.AFCI阈值) + 3,
                context.getString(R.string.FFT最大累计次数),
                context.getString(R.string.AFCI曲线扫描),
        };

        int[] itemTypes = new int[]{
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT
        };

        String[] register = new String[]{
                "",
                "",
                "",
                "",
                ""
        };
        float[] multiples = new float[]{
                1, 1, 1, 1, 1
        };
        String[] units = new String[]{
                "",
                "",
                "",
                "",
                ""
        };
        String[] hints = new String[]{tips, tips, tips, tips, tips1};
        int[][] funs = new int[][]{
                {3, 544, 544}, {3, 545, 545}, {3, 546, 546}, {3, 547, 547}, {3, 0, 0}
        };
        int[][] funset = new int[][]{
                {6, 544, -1}, {6, 545, -1}, {6, 546, -1}, {6, 547, -1}, {3, 0, 0}
        };

        String[][] items = new String[][]{
                {}, {}, {}, {}, {}
        };
        int[][] doubleFunset = new int[0][];
        for (int i = 0; i < titls.length; i++) {
            MaxSettingBean bean = new MaxSettingBean();
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


    private static List<MaxSettingBean> getMaxSystemSetList(Context context) {
        List<MaxSettingBean> list = new ArrayList<>();
        String tips = context.getString(R.string.android_key3048) + ":" + "0~65000" + "(" + context.getString(R.string.AFCI阈值) + 1
                + "<" + context.getString(R.string.AFCI阈值) + 2 + "<" + context.getString(R.string.AFCI阈值) + 3 + ")";
        String tips1 = context.getString(R.string.android_key3048) + ":" + "0~255";
        String[] titls = new String[]{
                context.getString(R.string.m396开关逆变器),
                context.getString(R.string.m397安规功能使能),
                context.getString(R.string.m398有功功率百分比),
                context.getString(R.string.m411Island使能),
                context.getString(R.string.m412风扇检查),
                context.getString(R.string.m413电网N线使能),
                context.getString(R.string.m414N至GND监测功能使能),
                context.getString(R.string.m415非标准电网电压范围使能),
                context.getString(R.string.m416指定的规格设置使能),
                context.getString(R.string.m417MPPT使能),
                context.getString(R.string.m389检查固件),
                context.getString(R.string.mGPRS4GPLC状态),
                context.getString(R.string.夜间SVG功能使能),
                context.getString(R.string.m432PID工作模式),
                context.getString(R.string.m433PID开关),
                context.getString(R.string.m434PID工作电压),

        };

        int[] itemTypes = new int[]{
                UsSettingConstant.SETTING_TYPE_SWITCH,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_NEXT,
                UsSettingConstant.SETTING_TYPE_SWITCH,
                UsSettingConstant.SETTING_TYPE_SWITCH,
                UsSettingConstant.SETTING_TYPE_SWITCH,
                UsSettingConstant.SETTING_TYPE_SWITCH,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_SWITCH,
                UsSettingConstant.SETTING_TYPE_SELECT,
                UsSettingConstant.SETTING_TYPE_NEXT,
                UsSettingConstant.SETTING_TYPE_ONLYREAD,
                UsSettingConstant.SETTING_TYPE_SELECT,
                UsSettingConstant.SETTING_TYPE_SELECT,
                UsSettingConstant.SETTING_TYPE_SWITCH,
                UsSettingConstant.SETTING_TYPE_INPUT
        };

        String[] register = new String[]{"", "", "", "", "",
                "", "", "", "", "", "", "", "", "", "", ""
        };
        float[] multiples = new float[]{
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1
        };
        String[] units = new String[]{"", "", "", "", "", "", "", "",
                "", "", "", "", "", "", "", ""
        };
        String[] hints = new String[]{
                tips, "", tips, tips, tips1,
                tips, tips, tips, tips, tips1,
                tips, tips, tips, tips, tips1,
                tips
        };
        int[][] funs = new int[][]{
                {3, 0, 0},//开关逆变器
                {3, 1, 1},//安规功能使能
                {3, 0, 5},//有功功率百分比
                {3, 230, 230},//IsLand使能
                {3, 231, 231},//风扇检查
                {3, 232, 232},//电网N线使能
                {3, 235, 235},//N至PE检测功能使能
                {3, 236, 236},//电压范围使能
                {3, 237, 237},//指定的规格使能
                {3, 399, 399},//PV输入模式
                {3, 233, 234},//检查固件
                {3, 310, 310},//GPRS状态
                {3, 141, 141},//夜间SVG功能使能
                {3, 201, 201},//PID工作模式
                {3, 202, 202},//PID开关
                {3, 203, 203}//PID工作电压选择
        };
        int[][] funset = new int[][]{
                {6, 0, -1}, {6, 1, -1}, {-1, -1, -1}, {6, 230, -1}, {6, 231, -1},
                {6, 232, -1}, {6, 235, -1}, {6, 236, -1}, {6, 237, -1}, {6, 399, -1},
                {6, 233, -1}, {6, 310, -1}, {6, 141, -1}, {6, 201, -1}, {6, 202, 0},
                {6, 203, -1}
        };

        String[][] items = new String[][]{
                {},//开关逆变器
                {},//安规功能使能
                {},//有功功率百分比
                {},//IsLand使能
                {},//风扇检查
                {},//电网N线使能
                {},//N至PE检测功能使能
                {"0", "1", "2"},//电压范围使能
                {},//指定的规格使能
                {"0", "1", "2"},//PV输入模式
                {},//检查固件
                {},//GPRS状态
                {context.getString(R.string.m89禁止), context.getString(R.string.m88使能)},//夜间SVG功能使能  12
                {"Automatic", "Continaual", "Overnight"},//PID工作模式
                {},//PID开关
                {}//PID工作电压选择

        };
        int[][] doubleFunset = new int[0][];
        int[][][] threeFunset = new int[][][]{
                {{6, 2, 1}, {6, 3, -1}},//有功功率百分比设置
                {{6, 233, -1}, {6, 234, -1}},//检查固件
        };
        for (int i = 0; i < titls.length; i++) {
            MaxSettingBean bean = new MaxSettingBean();
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


    private static List<MaxSettingBean> getMaxActivePowerList(Context context) {
        List<MaxSettingBean> list = new ArrayList<>();
        String tips = context.getString(R.string.android_key3048) + ":" + "0~65000" + "(" + context.getString(R.string.AFCI阈值) + 1
                + "<" + context.getString(R.string.AFCI阈值) + 2 + "<" + context.getString(R.string.AFCI阈值) + 3 + ")";
        String tips1 = context.getString(R.string.android_key3048) + ":" + "0~255";
        String[] titls = new String[]{
                context.getString(R.string.m398有功功率百分比),
                context.getString(R.string.android_key836),

        };

        int[] itemTypes = new int[]{
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_SWITCH,
        };

        String[] register = new String[]{"", ""
        };
        float[] multiples = new float[]{
                1, 1
        };
        String[] units = new String[]{"%", ""
        };
        String[] hints = new String[]{
                tips, ""
        };
        int[][] funs = new int[][]{
                {3, 0, 5},//开关逆变器
                {3, 0, 5},//安规功能使能

        };
        int[][] funset = new int[][]{
                {6, 3, -1}, {6, 2, 1}
        };

        String[][] items = new String[][]{
                {},//开关逆变器
                {}//安规功能使能


        };
        int[][] doubleFunset = new int[0][];
        int[][][] threeFunset = new int[][][]{
                {{6, 2, 1}, {6, 3, -1}},//有功功率百分比设置
                {{6, 233, -1}, {6, 234, -1}}//检查固件
        };
        for (int i = 0; i < titls.length; i++) {
            MaxSettingBean bean = new MaxSettingBean();
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


    private static List<MaxSettingBean> getMaxBasicSettingList(Context context) {

        List<MaxSettingBean> list = new ArrayList<>();
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
                context.getString(R.string.android_key828)
        };

        int[] itemTypes = new int[]{
                UsSettingConstant.SETTING_TYPE_SELECT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_ONLYREAD,
                UsSettingConstant.SETTING_TYPE_ONLYREAD,
                UsSettingConstant.SETTING_TYPE_ONLYREAD,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_NEXT,
        };

        String[] register = new String[]{
                "", "",
                "", "",
                "", "",
                ""
        };
        float[] multiples = new float[]{
                1, 1, 0.1f, 1, 1, 0.1f, 1
        };
        String[] units = new String[]{"", "", "", "", "", "kWh", ""
        };
        String[] hints = new String[]{
                tips, "", tips, "", tips, "", tips
        };
        int[][] funs = new int[][]{
                {3, 22, 22},//选择通信波特率
                {3, 88, 88},//MODBUS版本
                {3, 8, 8},//PV电压
                {3, 8, 8},//逆变器模块
                {3, 8, 8},//逆变器经纬度
                {4, 0, 99},//修改总发电量
                {3, 22, 22},//设置Model
        };
        int[][] funset = new int[][]{
                {6, 22, 0},
                {6, 88, 1},
                {6, 8, -1},
                {6, 2, 1},
                {6, 3, -1},
                {6, 7147, 1},
                {6, 3, -1}
        };

        String[][] items = new String[][]{
                {"9600bps", "38400bps", "115200bps"},//选择通信波特率
                {},//MODBUS版本
                {},//PV电压
                {},//逆变器模块
                {},//逆变器经纬度
                {},//修改总发电量
                {},//设置Model
        };
        int[][] doubleFunset = new int[][]{
                {6, 7147, -1}, {6, 7148, -1}

        };
        int[][][] threeFunset = new int[][][]{
                {{6, 2, 1}, {6, 3, -1}},//有功功率百分比设置
                {{6, 233, -1}, {6, 234, -1}}//检查固件
        };
        for (int i = 0; i < titls.length; i++) {
            MaxSettingBean bean = new MaxSettingBean();
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


    private static List<MaxSettingBean> getMaxGridCodeSettingList(Context context) {

        List<MaxSettingBean> list = new ArrayList<>();
        String tips = context.getString(R.string.android_key3048) + ":" + "0~65000" + "(" + context.getString(R.string.AFCI阈值) + 1
                + "<" + context.getString(R.string.AFCI阈值) + 2 + "<" + context.getString(R.string.AFCI阈值) + 3 + ")";
        String tips1 = context.getString(R.string.android_key3048) + ":" + "0~255";
        String[] titls = new String[]{
                context.getString(R.string.pf_setting),
                context.getString(R.string.频率有功),
                context.getString(R.string.android_key2438),
                context.getString(R.string.m418电源启动重启斜率),
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
                UsSettingConstant.SETTING_TYPE_NEXT,
                UsSettingConstant.SETTING_TYPE_NEXT,
        };

        String[] register = new String[]{
                "", "",
                "", "",
                "", "",
                "","",
                ""
        };
        float[] multiples = new float[]{
                1, 1, 0.1f, 1, 1, 0.1f, 1,1,1
        };
        String[] units = new String[]{"", "", "", "", "", "kWh", "","",""
        };
        String[] hints = new String[]{
                tips, "", tips, "", tips, "", tips,"",""
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
                {3, 22, 22},//PV过压保护点
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
            MaxSettingBean bean = new MaxSettingBean();
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
