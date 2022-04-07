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
        TL3XH_SYSTEM_SETTING,//TL3XH系统设置
        TLXH_DRY_CONTACT,//干接点设置
        TLXH_CHARGE_MANAGER,//充放电管理
        TLXH_BASIC_SETTING,//基本设置

        TLXH_SYSTEM_SETTING,//TLXH系统设置
        //------------第二层级设置--------------

        TLXH_GRID_CODE_PARAMETERS_SETTING,
        TLXH_GRID_SECOND_PF_SETTING,
        TLXH_GRID_SECOND_FRENCY_WATT_SETTING,
        TLXH_GRID_SECOND_VOLTAGE_SETTING,
        TLXH_GRID_SECOND_POWERE_START_SETTING,
        TLXH_GRID_SECOND_AC_VOLTAGE_PROTECT,
        TLXH_GRID_SECOND_AC_FRENCY_PROTECT,
        TLXH_GRID_SECOND_SYNCHORNIZATION_RANGE,

        //------------------第三层级设置---------------------
        TLXH_GRID_THIRE_INDUCTIVE_REACTIVE_POWER,//感性载率
        TLXH_GRID_THIRE_CAPACITIVE_REACTIVE_POWER,//容性载率
        TLXH_GRID_THIRE_CAPACITIVE_PF,//容性PF
        TLXH_GRID_THIRE_INDUCTIVE_PF,//感性PF
        TLXH_GRID_THIRE_PF_CURVE_INOUT_VAC,//无功曲线切入/切出电压
        TLXH_GRID_THIRE_PF_CALIBRATION_FACTOR,//PF校准系数
        TLXH_GRID_THIRE_PF_LIMIT_POINT_OF_PF_LOAD,//PF限制负载百分比点
        TLXH_GRID_THIRE_LIMIT_POINT_OF_PF,//PF限值


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
            case TL3XH_SYSTEM_SETTING:
                list = getTl3xhSystemSetList(context);
                break;
            case TLXH_DRY_CONTACT:
                list = getTlxhDrySetList(context);
                break;
            case TLXH_BASIC_SETTING:
                list = getTlxhBasicSettingList(context);
                break;
            case TLXH_CHARGE_MANAGER:
                list = getChargeMangerSetList(context);
                break;

            case TLXH_GRID_CODE_PARAMETERS_SETTING:
                list=  getTlxGridCodeSettingList(context);
                break;


            case TLXH_GRID_SECOND_PF_SETTING:
                list = getMaxGridSecondPfSettingList(context);
                break;
            case TLXH_GRID_SECOND_FRENCY_WATT_SETTING:
                list = getMaxFrencyWattSettingList(context);
                break;
            case TLXH_GRID_SECOND_VOLTAGE_SETTING:
                list = getMaxVoltageSettingList(context);
                break;
            case TLXH_GRID_SECOND_POWERE_START_SETTING:
                list = getFrencyWattSetting(context);
                break;
            case TLXH_GRID_SECOND_AC_VOLTAGE_PROTECT:
                list = getACVoltageSetting(context);
                break;
            case TLXH_GRID_SECOND_AC_FRENCY_PROTECT:
                list = getACFrencySetting(context);
                break;
            case TLXH_GRID_SECOND_SYNCHORNIZATION_RANGE:
                list = getGridConnectSetting(context);
                break;


            case TLXH_GRID_THIRE_INDUCTIVE_REACTIVE_POWER://感性载率和容性载率一样
            case TLXH_GRID_THIRE_CAPACITIVE_REACTIVE_POWER:
                list = getThirdInductiveReactiveSettingList(context, maxSettingEnum);
                break;

            case TLXH_GRID_THIRE_INDUCTIVE_PF:
            case TLXH_GRID_THIRE_CAPACITIVE_PF:
                list = getCapacitivePf(context,maxSettingEnum);
                break;

            case TLXH_GRID_THIRE_PF_CURVE_INOUT_VAC:
                list = getCuveInouTVAC(context);
                break;

            case TLXH_GRID_THIRE_PF_CALIBRATION_FACTOR:
                list = getPfCalibaFactor(context);
                break;

            case TLXH_GRID_THIRE_PF_LIMIT_POINT_OF_PF_LOAD:
                list = getLimitPointOFload(context);
                break;

            case TLXH_GRID_THIRE_LIMIT_POINT_OF_PF:
                list = getPFLimitValue(context);
                break;
        }
        return list;
    }


    private static List<ALLSettingBean> getTlxGridCodeSettingList(Context context) {

        List<ALLSettingBean> list = new ArrayList<>();
        String tips = "";
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


    private static List<ALLSettingBean> getPFLimitValue(Context context) {


        List<ALLSettingBean> list = new ArrayList<>();
        String tips = context.getString(R.string.android_key3048) + ":" + " -1~-0.8,0.8~1";
        String[] titls = new String[]{
                context.getString(R.string.m392PF限制功率因数) + 1,//PF限值
                context.getString(R.string.m392PF限制功率因数) + 2,//PF限值
                context.getString(R.string.m392PF限制功率因数) + 3,//PF限值
                context.getString(R.string.m392PF限制功率因数) + 4,//PF限值
        };

        int[] itemTypes = new int[]{
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
        };

        String[] register = new String[]{
                "", "", "", ""
        };
        float[] multiples = new float[]{
                1, 1, 1, 1
        };
        String[] units = new String[]{"", "", "", ""};
        String[] hints = new String[]{
                tips, tips, tips, tips
        };
        int[][] funs = new int[][]{
                {3, 111, 117},//PF限值
                {3, 111, 117},//PF限值
                {3, 111, 117},//PF限值
                {3, 111, 117},//PF限值

        };

        int[][] funset = new int[][]{
                {6, 111, -1},//PF限制负载百分比点
                {6, 113, -1},//PF限制负载百分比点
                {6, 115, -1},//PF限制负载百分比点
                {6, 117, -1},//PF限制负载百分比点
        };

        String[][] items = new String[][]{
                {context.getString(R.string.PF值设置), context.getString(R.string.默认PF曲线)},//容性PF

                {},//记忆使能
                {},
                {}

        };
        int[][] doubleFunset = new int[][]{
                {6, 7147, -1}, {6, 7148, -1}

        };
        int[][][] threeFunset = new int[][][]{
                {
                        {6, 5, -1},//容性PF
                        {6, 2, -1},//记忆使能
                        {6, 89, 1}
                }
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


    private static List<ALLSettingBean> getLimitPointOFload(Context context) {


        List<ALLSettingBean> list = new ArrayList<>();
        String tips = context.getString(R.string.android_key3048) + ":" + "0~100%";
        String[] titls = new String[]{
                context.getString(R.string.m391PF限制负载百分比点) + 1,//PF限制负载百分比点
                context.getString(R.string.m391PF限制负载百分比点) + 2,//PF限制负载百分比点
                context.getString(R.string.m391PF限制负载百分比点) + 3,//PF限制负载百分比点
                context.getString(R.string.m391PF限制负载百分比点) + 4,//PF限制负载百分比点
        };

        int[] itemTypes = new int[]{
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
        };

        String[] register = new String[]{
                "", "", "", ""
        };
        float[] multiples = new float[]{
                1, 1, 1, 1
        };
        String[] units = new String[]{"", "", "", ""};
        String[] hints = new String[]{
                tips, tips, tips, tips
        };
        int[][] funs = new int[][]{
                {3, 110, 116},//PF限制负载百分比点
                {3, 110, 116},//PF限制负载百分比点
                {3, 110, 116},//PF限制负载百分比点
                {3, 110, 116},//PF限制负载百分比点
        };

        int[][] funset = new int[][]{
                {6, 110, -1},//PF限制负载百分比点
                {6, 112, -1},//PF限制负载百分比点
                {6, 114, -1},//PF限制负载百分比点
                {6, 116, -1},//PF限制负载百分比点
        };

        String[][] items = new String[][]{
                {context.getString(R.string.PF值设置), context.getString(R.string.默认PF曲线)},//容性PF

                {},//记忆使能
                {},
                {}

        };
        int[][] doubleFunset = new int[][]{
                {6, 7147, -1}, {6, 7148, -1}

        };
        int[][][] threeFunset = new int[][][]{
                {
                        {6, 5, -1},//容性PF
                        {6, 2, -1},//记忆使能
                        {6, 89, 1}
                }
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


    private static List<ALLSettingBean> getPfCalibaFactor(Context context) {

        List<ALLSettingBean> list = new ArrayList<>();
        String tips = context.getString(R.string.android_key3048) + ":" + "0.9~1.1";
        String[] titls = new String[]{
                context.getString(R.string.m390PF调整值) + 1,//PF校准系数1
                context.getString(R.string.m390PF调整值) + 2,//PF校准系数2
                context.getString(R.string.m390PF调整值) + 3,//PF校准系数3
                context.getString(R.string.m390PF调整值) + 4,//PF校准系数4
                context.getString(R.string.m390PF调整值) + 5,//PF校准系数5
                context.getString(R.string.m390PF调整值) + 6,//PF校准系数6
        };

        int[] itemTypes = new int[]{
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
        };

        String[] register = new String[]{
                "", "", "", "", "", "",
        };
        float[] multiples = new float[]{
                1, 1, 1, 1, 1, 1
        };
        String[] units = new String[]{"", "", "", "", "", ""};
        String[] hints = new String[]{
                "", "", "", "", "", ""
        };
        int[][] funs = new int[][]{
                {3, 101, 106},//PF校准系数1
                {3, 101, 106},//PF校准系数2
                {3, 101, 106},//PF校准系数3
                {3, 101, 106},//PF校准系数4
                {3, 101, 106},//PF校准系数5
                {3, 101, 106}//PF校准系数6
        };

        int[][] funset = new int[][]{
                {6, 101, -1},//PF校准系数1
                {6, 102, -1},//PF校准系数2
                {6, 103, -1},//PF校准系数3
                {6, 104, -1},//PF校准系数4
                {6, 105, -1},//PF校准系数5
                {6, 106, -1},//PF校准系数6
        };

        String[][] items = new String[][]{
                {context.getString(R.string.PF值设置), context.getString(R.string.默认PF曲线)},//容性PF

                {},//记忆使能
                {},
                {},
                {},
                {}, {}

        };
        int[][] doubleFunset = new int[][]{
                {6, 7147, -1}, {6, 7148, -1}

        };
        int[][][] threeFunset = new int[][][]{
                {
                        {6, 5, -1},//容性PF
                        {6, 2, -1},//记忆使能
                        {6, 89, 1}
                }
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


    private static List<ALLSettingBean> getCuveInouTVAC(Context context) {

        List<ALLSettingBean> list = new ArrayList<>();
        String tips = context.getString(R.string.android_key3048) + ":" + "207~248.4V";
        String tips2 = context.getString(R.string.android_key3048) + ":" + "207~248.4V";
        String[] titls = new String[]{
                context.getString(R.string.m387无功曲线切入电压),//无功曲线切入电压
                context.getString(R.string.m388无功曲线切出电压),//无功曲线切出电压
        };

        int[] itemTypes = new int[]{
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
        };

        String[] register = new String[]{
                "", "",
        };
        float[] multiples = new float[]{
                0.1f, 0.1f
        };
        String[] units = new String[]{"V", "V"};
        String[] hints = new String[]{
                tips, tips2
        };
        int[][] funs = new int[][]{
                {3, 99, 100},//无功曲线切入电压
                {3, 99, 100}//无功曲线切出电压
        };

        int[][] funset = new int[][]{
                {6, 99, -1},//无功曲线切入电压
                {6, 100, -1},//无功曲线切出电压
        };

        String[][] items = new String[][]{
                {context.getString(R.string.PF值设置), context.getString(R.string.默认PF曲线)},//容性PF

                {},//记忆使能

        };
        int[][] doubleFunset = new int[][]{
                {6, 7147, -1}, {6, 7148, -1}

        };
        int[][][] threeFunset = new int[][][]{
                {
                        {6, 5, -1},//容性PF
                        {6, 2, -1},//记忆使能
                        {6, 89, 1}
                }
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


    private static List<ALLSettingBean> getCapacitivePf(Context context, TLXHConfigControl.TlxSettingEnum
            maxSettingEnum) {

        List<ALLSettingBean> list = new ArrayList<>();
        String tips = "";
        String tips1 = context.getString(R.string.android_key3048) + ":" + "0~255";
        String[] titls = new String[]{
                context.getString(R.string.m401容性PF),//感性载率
                context.getString(R.string.m377记忆使能),//记忆使能
        };

        if (maxSettingEnum== TlxSettingEnum.TLXH_GRID_THIRE_INDUCTIVE_PF){
            titls = new String[]{
                    context.getString(R.string.m402感性PF),//感性载率
                    context.getString(R.string.m377记忆使能),//记忆使能
            };
        }

        int[] itemTypes = new int[]{
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_SWITCH,
        };

        String[] register = new String[]{
                "", "",
        };
        float[] multiples = new float[]{
                1, 1
        };
        String[] units = new String[]{"", ""};
        String[] hints = new String[]{
                tips, ""
        };
        int[][] funs = new int[][]{
                {3, 5, 5},//容性PF、感性PF
                {3, 2, 2}//记忆使能
        };

        int[][] funset = new int[][]{
                {6, 5, -1},//容性PF
                {6, 2, -1},//记忆使能
                {6, 89, 1}
        };

        String[][] items = new String[][]{
                {context.getString(R.string.PF值设置), context.getString(R.string.默认PF曲线)},//容性PF

                {},//记忆使能

        };
        int[][] doubleFunset = new int[][]{
                {6, 7147, -1}, {6, 7148, -1}

        };
        int[][][] threeFunset = new int[][][]{
                {
                        {6, 5, -1},//容性PF
                        {6, 2, -1},//记忆使能
                        {6, 89, 1}
                }
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


    private static List<ALLSettingBean> getThirdInductiveReactiveSettingList(Context context, TLXHConfigControl.TlxSettingEnum
            maxSettingEnum) {
        List<ALLSettingBean> list = new ArrayList<>();
        String tips = "";
        String[] titls = new String[]{
                context.getString(R.string.m399感性载率),//感性载率
                context.getString(R.string.m377记忆使能),//记忆使能
        };

        if (maxSettingEnum== TlxSettingEnum.TLXH_GRID_THIRE_CAPACITIVE_REACTIVE_POWER){
            titls = new String[]{
                    context.getString(R.string.m400容性载率),//感性载率
                    context.getString(R.string.m377记忆使能),//记忆使能
            };
        }



        int[] itemTypes = new int[]{
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_SWITCH,
        };

        String[] register = new String[]{
                "", "",
        };
        float[] multiples = new float[]{
                1, 1
        };
        String[] units = new String[]{"", ""};
        String[] hints = new String[]{
                tips, ""
        };
        int[][] funs = new int[][]{
                {3, 4, 4},//感性载率
                {3, 2, 2}//记忆使能
        };
        //需要设置的内容
        /*设置容性PF：先5写（10000+设置值*10000）；89(Hold)写1；
          设置感性PF：先5写（10000-设置值*10000）；89(Hold)写1；
          显示值=(读取值-10000)/10000
         */

        int value = 5;
        switch (maxSettingEnum) {
            case TLXH_GRID_THIRE_INDUCTIVE_REACTIVE_POWER://感性载率
                value = 5;
                break;
            case TLXH_GRID_THIRE_CAPACITIVE_REACTIVE_POWER://容性载率
                value = 4;
                break;
        }

        int[][] funset = new int[][]{
                {6, 4, -1},//感性载率
                {6, 2, -1},//记忆使能
                {6, 89, value}
        };

        String[][] items = new String[][]{
                {context.getString(R.string.PF值设置), context.getString(R.string.默认PF曲线)},//运行PF为1

                {},//感性载率

        };
        int[][] doubleFunset = new int[][]{
                {6, 7147, -1}, {6, 7148, -1}

        };
        int[][][] threeFunset = new int[][][]{
                {
                        {6, 4, -1},//感性载率
                        {6, 2, -1},//记忆使能
                        {6, 89, value}
                }
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


    private static List<ALLSettingBean> getGridConnectSetting(Context context) {


        List<ALLSettingBean> list = new ArrayList<>();
        String tips = context.getString(R.string.android_key3048) + ":" + "69~207V";
        String tips1 = context.getString(R.string.android_key3048) + ":" + "241.5~345V";
        String tips3 = context.getString(R.string.android_key3048) + ":" + "50~550V";
        String tips4 = context.getString(R.string.android_key3048) + ":" + "10s~15min";
        String tips5=context.getString(R.string.android_key3048)+":"+"45~50Hz";
        String tips6=context.getString(R.string.android_key3048)+":"+"50~55Hz";
        String[] titls = new String[]{
                context.getString(R.string.m424启动电压),//并网电压
                context.getString(R.string.m425启动时间),//并网时间
                context.getString(R.string.android_key990),//重启时间
                context.getString(R.string.grid_connetced_vol_limit) + "(" + context.getString(R.string.m373低)+")",//并网电压限制 低
                context.getString(R.string.grid_connetced_vol_limit) + "(" + context.getString(R.string.m372高)+")",//并网电压限制 高
                context.getString(R.string.grid_connetced_frency_limit) + "(" + context.getString(R.string.m373低)+")",//并网频率限制 低
                context.getString(R.string.grid_connetced_frency_limit) + "(" + context.getString(R.string.m372高)+")",//并网频率限制 高

        };

        int[] itemTypes = new int[]{
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
        };

        String[] register = new String[]{
                "", "", "", "", "", "", ""
        };
        float[] multiples = new float[]{
                1, 1, 1, 1, 1, 1, 1
        };
        String[] units = new String[]{"V", "min", "min", "V", "V", "Hz", "Hz"};
        String[] hints = new String[]{
                tips3, tips4, tips4, tips, tips1, tips5, tips6
        };
        int[][] funs = new int[][]{
                {3, 17, 17},//并网电压
                {3, 18, 18},//并网时间
                {3, 19, 19},//重启时间
                {3, 64, 64},//并网电压限制
                {3, 65, 65},//并网电压限制
                {3, 66, 66},//并网频率限制
                {3, 67, 67},//并网频率限制
        };

        int[][] funset = new int[][]{
                {6, 17, -1},//并网电压
                {6, 18, -1},//并网时间
                {6, 19, -1},//重启时间
                {6, 64, -1},//并网电压限制
                {6, 65, -1},//并网电压限制
                {6, 66, -1},//并网频率限制
                {6, 67, -1},//并网频率限制
        };

        String[][] items = new String[][]{
                {context.getString(R.string.PF值设置), context.getString(R.string.默认PF曲线)},//容性PF

                {},//记忆使能
                {},
                {},
                {},
                {},
                {}

        };
        int[][] doubleFunset = new int[][]{
                {6, 7147, -1}, {6, 7148, -1}

        };
        int[][][] threeFunset = new int[][][]{
                {
                        {6, 5, -1},//容性PF
                        {6, 2, -1},//记忆使能
                        {6, 89, 1}
                }
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


    private static List<ALLSettingBean> getACFrencySetting(Context context) {


        List<ALLSettingBean> list = new ArrayList<>();
        String tips = context.getString(R.string.android_key3048) + ":" + "45~50Hz";
        String tips1 = context.getString(R.string.android_key3048) + ":" + "50~55Hz";
        String tips2 = context.getString(R.string.android_key3048) + ":" + "20ms~10min";
        String[] titls = new String[]{
                "AC1 " + context.getString(R.string.频率限制)  + "(" + context.getString(R.string.m373低)+")",
                "AC1 " + context.getString(R.string.频率限制) + "(" + context.getString(R.string.m372高)+")"
                , "AC2 " + context.getString(R.string.频率限制) + "(" + context.getString(R.string.m373低)+")"
                , "AC2 " + context.getString(R.string.频率限制) + "(" + context.getString(R.string.m372高)+")"
                , "AC3 " + context.getString(R.string.频率限制) + "(" + context.getString(R.string.m373低)+")"
                , "AC3 " + context.getString(R.string.频率限制) + "(" + context.getString(R.string.m372高)+")"


                , "AC" + context.getString(R.string.m442频率限制时间) + "1"+ "("  + context.getString(R.string.m373低)+")"
                , "AC" + context.getString(R.string.m442频率限制时间) + "1" + "(" + context.getString(R.string.m372高)+")"
                , "AC" + context.getString(R.string.m442频率限制时间) + "2" + "(" + context.getString(R.string.m373低)+")"
                , "AC" + context.getString(R.string.m442频率限制时间) + "2" + "(" + context.getString(R.string.m372高)+")"
                , "AC" + context.getString(R.string.m442频率限制时间) + "3" + "(" + context.getString(R.string.m373低)+")"
                , "AC" + context.getString(R.string.m442频率限制时间) + "3" + "(" + context.getString(R.string.m372高)+")"
        };

        int[] itemTypes = new int[]{
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
        };

        String[] register = new String[]{
                "", "", "", "", "", "", "", "", "", "", "", ""
        };
        float[] multiples = new float[]{
                0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f
        };
        String[] units = new String[]{"Hz", "Hz", "Hz", "Hz", "Hz", "Hz", "Hz", "ms", "ms", "ms", "ms", "ms", "ms"};
        String[] hints = new String[]{
                tips, tips1, tips, tips1, tips, tips1, tips2, tips2, tips2, tips2, tips2, tips2
        };
        int[][] funs = new int[][]{
                {3, 54, 54},//AC1频率限制低
                {3, 55, 55},//AC1频率限制高
                {3, 58, 58},//AC2频率限制低
                {3, 59, 59},// AC2频率限制高
                {3, 62, 62},//AC3频率限制低
                {3, 63, 63},//AC3频率限制高

                {3, 72, 72},//AC频率限制时间1低
                {3, 73, 73},//AC频率限制时间1高
                {3, 74, 74},//AC频率限制时间2低
                {3, 75, 75},//AC频率限制时间2高
                {3, 78, 78},//AC频率限制时间3低
                {3, 79, 79}//AC频率限制时间3高
        };

        int[][] funset = new int[][]{
                {6, 54, -1},//AC1频率限制低
                {6, 55, -1},//AC1频率限制高
                {6, 58, -1},//AC2频率限制低
                {6, 59, -1},//AC2频率限制高
                {6, 62, -1},//AC3频率限制低
                {6, 63, -1},//AC3频率限制高

                {6, 72, -1},//AC频率限制时间1低
                {6, 73, -1},//AC频率限制时间1高
                {6, 74, -1},//AC频率限制时间2低
                {6, 75, -1},//AC频率限制时间2高
                {6, 78, -1},//AC频率限制时间3低
                {6, 79, -1},//AC频率限制时间3高
        };

        String[][] items = new String[][]{
                {context.getString(R.string.PF值设置), context.getString(R.string.默认PF曲线)},//AC1频率限制低

                {},//AC1频率限制高
                {},//AC2频率限制低
                {},//AC2频率限制高
                {},//AC3频率限制低
                {},//AC3频率限制高

                {},//AC频率限制时间1低
                {},//AC频率限制时间1高
                {},//AC频率限制时间2低
                {},//AC频率限制时间2高
                {},//AC频率限制时间3低
                {},//AC频率限制时间3高

        };
        int[][] doubleFunset = new int[][]{
                {6, 7147, -1}, {6, 7148, -1}

        };
        int[][][] threeFunset = new int[][][]{
                {
                        {6, 5, -1},//容性PF
                        {6, 2, -1},//记忆使能
                        {6, 89, 1}
                }
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

    private static List<ALLSettingBean> getACVoltageSetting(Context context) {


        List<ALLSettingBean> list = new ArrayList<>();
        String tips = context.getString(R.string.android_key3048) + ":" + "69~207V";
        String tips1 = context.getString(R.string.android_key3048) + ":" + "241.5~345V";
        String tips2 = context.getString(R.string.android_key3048) + ":" + "20ms~1min";
        String[] titls = new String[]{
                "AC1 " + context.getString(R.string.电压限制) + "(" + context.getString(R.string.m373低)+")",
                "AC1 " + context.getString(R.string.电压限制) + "(" + context.getString(R.string.m372高)+")"

                , "AC2 " + context.getString(R.string.电压限制) + "(" + context.getString(R.string.m373低)+")"
                , "AC2 " + context.getString(R.string.电压限制) + "(" + context.getString(R.string.m372高)+")"

                , "AC3 " + context.getString(R.string.电压限制) + "(" + context.getString(R.string.m373低)+")"
                , "AC3 " + context.getString(R.string.电压限制) + "(" + context.getString(R.string.m372高)+")"

                , "AC" + context.getString(R.string.m441电压限制时间) + "1" +"("+ context.getString(R.string.m373低)+")"
                , "AC" + context.getString(R.string.m441电压限制时间) + "1" + "("+context.getString(R.string.m372高)+")"
                , "AC" + context.getString(R.string.m441电压限制时间) + "2" +"("+ context.getString(R.string.m373低)+")"
                , "AC" + context.getString(R.string.m441电压限制时间) + "2" + "("+context.getString(R.string.m372高)+")"
                , "AC" + context.getString(R.string.m441电压限制时间) + "3" +"(" +context.getString(R.string.m373低)+")"
                , "AC" + context.getString(R.string.m441电压限制时间) + "3" +"(" +context.getString(R.string.m372高)+")"
        };

        int[] itemTypes = new int[]{
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
        };

        String[] register = new String[]{
                "", "", "", "", "", "", "", "", "", "", "", ""
        };
        float[] multiples = new float[]{
                0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 20, 20, 20, 20, 20, 20
        };
        String[] units = new String[]{"V", "V", "V", "V", "V", "V", "ms", "ms", "ms", "ms", "ms", "ms", "ms"};
        String[] hints = new String[]{
                tips, tips1, tips, tips1, tips, tips1, tips2, tips2, tips2, tips2, tips2, tips2
        };
        int[][] funs = new int[][]{
                {3, 52, 52},//AC1限制电压低
                {3, 53, 53},//AC1限制电压高
                {3, 56, 56},//AC2限制电压低
                {3, 57, 57},//AC2限制电压高
                {3, 60, 60},//AC3限制电压低
                {3, 61, 61},//AC3限制电压高
                {3, 68, 68},//AC电压限制时间1低
                {3, 69, 69},//AC电压限制时间1高
                {3, 70, 70},//AC电压限制时间2低
                {3, 71, 71},//AC电压限制时间2高
                {3, 76, 76},//AC电压限制时间3低
                {3, 77, 77}//AC电压限制时间3高
        };

        int[][] funset = new int[][]{
                {6, 52, -1},//AC1限制电压低
                {6, 53, -1},//AC1限制电压高
                {6, 56, -1},//AC2限制电压低
                {6, 57, -1},//AC2限制电压高
                {6, 60, -1},//AC3限制电压低
                {6, 61, -1},//AC3限制电压高
                {6, 68, -1},//AC电压限制时间1低
                {6, 69, -1},//AC电压限制时间1高
                {6, 70, -1},//AC电压限制时间2低
                {6, 71, -1},//AC电压限制时间2高
                {6, 76, -1},//AC电压限制时间3低
                {6, 77, -1},//AC电压限制时间3高
        };

        String[][] items = new String[][]{
                {context.getString(R.string.PF值设置), context.getString(R.string.默认PF曲线)},//AC1限制电压低

                {},//AC1限制电压高
                {},//AC2限制电压低
                {},//AC2限制电压高
                {},//AC3限制电压低
                {},//AC3限制电压高
                {},//AC电压限制时间1低
                {},//AC电压限制时间1高
                {},//AC电压限制时间2低
                {},//AC电压限制时间2高
                {},//AC电压限制时间2高
                {},//AC电压限制时间3低
                {},//AC电压限制时间3高

        };
        int[][] doubleFunset = new int[][]{
                {6, 7147, -1}, {6, 7148, -1}

        };
        int[][][] threeFunset = new int[][][]{
                {
                        {6, 5, -1},//容性PF
                        {6, 2, -1},//记忆使能
                        {6, 89, 1}
                }
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


    private static List<ALLSettingBean> getFrencyWattSetting(Context context) {


        List<ALLSettingBean> list = new ArrayList<>();
        String tips = context.getString(R.string.android_key3048) + ":" + "1%~6000%Pn/min";
        String[] titls = new String[]{
                context.getString(R.string.android_key843),//加载斜率
                context.getString(R.string.android_key846),//重启斜率
        };

        int[] itemTypes = new int[]{
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
        };

        String[] register = new String[]{
                "", ""
        };
        float[] multiples = new float[]{
                0.1f, 0.1f
        };
        String[] units = new String[]{"%", "%"};
        String[] hints = new String[]{
                tips, tips
        };
        int[][] funs = new int[][]{
                {3, 20, 21},//加载斜率
                {3, 20, 21}//重启斜率

        };

        int[][] funset = new int[][]{
                {6, 20, -1},//加载斜率
                {6, 21, -1}//重启斜率
        };

        String[][] items = new String[][]{
                {context.getString(R.string.PF值设置), context.getString(R.string.默认PF曲线)},//容性PF

                {},//记忆使能

        };
        int[][] doubleFunset = new int[][]{
                {6, 7147, -1}, {6, 7148, -1}

        };
        int[][][] threeFunset = new int[][][]{
                {
                        {6, 5, -1},//容性PF
                        {6, 2, -1},//记忆使能
                        {6, 89, 1}
                }
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


    private static List<ALLSettingBean> getMaxVoltageSettingList(Context context) {

        List<ALLSettingBean> list = new ArrayList<>();
        String tips = context.getString(R.string.android_key3048) + ":" + "195.5~276V";
        String tips1 = context.getString(R.string.android_key3048) + ":" + "0~100%";
        String tips2 = context.getString(R.string.android_key3048) + ":" + "0~20s";
        String[] titls = new String[]{
                context.getString(R.string.m381Qv切入高压),//m381Qv切入高压
                context.getString(R.string.m382Qv切出高压),//m382Qv切出高压
                context.getString(R.string.m383Qv切入低压),//m383Qv切入低压
                context.getString(R.string.m384Qv切出低压),//m384Qv切出低压
                context.getString(R.string.m385Qv切入功率),//m385Qv切入功率
                context.getString(R.string.m386Qv切出功率),//m386Qv切出功率
                context.getString(R.string.m408Qv无功延时),//m408Qv无功延时
                context.getString(R.string.m410Qv曲线Q最大值),//m410Qv曲线Q最大值
        };

        int[] itemTypes = new int[]{
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,

        };

        String[] register = new String[]{
                "", "", "",
                "", "", "",
                "", "",
        };
        float[] multiples = new float[]{
                1, 1, 1, 1, 1, 1, 1, 1
        };
        String[] units = new String[]{"", "", "",
                "", "", "",
                "", ""
        };
        String[] hints = new String[]{
                tips, tips, tips,
                tips, tips1, tips1, tips2,
                tips1
        };
        int[][] funs = new int[][]{
                {3, 93, 93},//m381Qv切入高压
                {3, 94, 94},//m382Qv切出高压
                {3, 95, 95},//m383Qv切入低压
                {3, 96, 96},//m384Qv切出低压
                {3, 97, 97},//m385Qv切入功率
                {3, 98, 98},//m386Qv切出功率
                {3, 107, 107},//m408Qv无功延时
                {3, 109, 109},//m410Qv曲线Q最大值

        };
        int[][] funset = new int[][]{
                {6, 93, -1},//m381Qv切入高压
                {6, 94, -1},//m382Qv切出高压
                {6, 95, -1},//m383Qv切入低压
                {6, 96, -1},//m384Qv切出低压
                {6, 97, -1},//m385Qv切入功率
                {6, 98, -1},//m386Qv切出功率
                {6, 107, -1},//m408Qv无功延时
                {6, 109, -1},//m410Qv曲线Q最大值

        };

        String[][] items = new String[][]{
                {"PF=1",
                        context.getString(R.string.PF值设置), context.getString(R.string.默认PF曲线),
                        context.getString(R.string.用户设定PF曲线), context.getString(R.string.滞后无功功率1),
                        context.getString(R.string.滞后无功功率2), context.getString(R.string.QV模式),
                        context.getString(R.string.正负无功值调节), context.getString(R.string.static_capacitive_QV_mode),
                        context.getString(R.string.static_perceptual_QV_mode)
                },//运行PF为1

                {},//感性载率
                {},//容性载率
                {},//容性PF
                {},//感性PF
                {},//无功曲线切入/切出电压
                {},//PF校准系数
                {},//PF限制负载百分比点
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

    private static List<ALLSettingBean> getMaxGridSecondPfSettingList(Context context) {

        List<ALLSettingBean> list = new ArrayList<>();
        String tips = context.getString(R.string.android_key3048) + ":" + " -1~-0.8,0.8~1";
        String tips1 = context.getString(R.string.android_key3048) + ":" + "0~60%";
        String[] titls = new String[]{
                context.getString(R.string.android_key926),//运行PF为1
                context.getString(R.string.m399感性载率),//感性载率
                context.getString(R.string.m400容性载率),//容性载率
                context.getString(R.string.m401容性PF),//容性PF
                context.getString(R.string.m402感性PF),//感性PF
                context.getString(R.string.m422无功曲线切入切出电压),//无功曲线切入/切出电压
//                context.getString(R.string.android_key877),//PF校准系数
                context.getString(R.string.android_key880),//PF限制负载百分比点
                context.getString(R.string.android_key883),//PF限值
        };

        int[] itemTypes = new int[]{
                UsSettingConstant.SETTING_TYPE_SWITCH,
                UsSettingConstant.SETTING_TYPE_NEXT,
                UsSettingConstant.SETTING_TYPE_NEXT,
                UsSettingConstant.SETTING_TYPE_NEXT,
                UsSettingConstant.SETTING_TYPE_NEXT,
                UsSettingConstant.SETTING_TYPE_NEXT,
//                UsSettingConstant.SETTING_TYPE_NEXT,
                UsSettingConstant.SETTING_TYPE_NEXT,
                UsSettingConstant.SETTING_TYPE_NEXT,
        };

        String[] register = new String[]{
                "", "",
                "", "",
                "", "",
                "",
                ""
        };
        float[] multiples = new float[]{
                1, 1, 1, 1, 1, 1, 1, 1
        };
        String[] units = new String[]{"", "", "", "", "", "", "V", "V"
        };
        String[] hints = new String[]{
                "", tips1, tips1, tips, tips, "", "", ""
        };
        int[][] funs = new int[][]{
                {3, 89, 89},//运行PF为1
                {3, 88, 88},//感性载率
                {3, 8, 8},//容性载率
                {3, 8, 8},//容性PF
                {3, 8, 8},//感性PF
                {4, 0, 99},//无功曲线切入/切出电压
//                {3, 22, 22},//PF校准系数
                {3, 80, 80},//PF限制负载百分比点
                {3, 22, 22},//PF限值
        };
        int[][] funset = new int[][]{
                {6, 89, 0},//运行PF为1
                {6, 88, 88},//感性载率
                {6, 8, 8},//容性载率
                {6, 8, 8},//容性PF
                {6, 8, 8},//感性PF
                {6, 0, 99},//无功曲线切入/切出电压
//                {6, 22, 22},//PF校准系数
                {6, 80, 80},//PF限制负载百分比点
                {6, 22, 22},//PF限值
        };

        String[][] items = new String[][]{
                {"PF=1",
                        context.getString(R.string.PF值设置), context.getString(R.string.默认PF曲线),
                        context.getString(R.string.用户设定PF曲线), context.getString(R.string.滞后无功功率1),
                        context.getString(R.string.滞后无功功率2), context.getString(R.string.QV模式),
                        context.getString(R.string.正负无功值调节), context.getString(R.string.static_capacitive_QV_mode),
                        context.getString(R.string.static_perceptual_QV_mode)
                },//运行PF为1

                {},//感性载率
                {},//容性载率
                {},//容性PF
                {},//感性PF
                {},//无功曲线切入/切出电压
//                {},//PF校准系数
                {},//PF限制负载百分比点
                {},//PF限值
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


    private static List<ALLSettingBean> getMaxFrencyWattSettingList(Context context) {

        List<ALLSettingBean> list = new ArrayList<>();
        String tips = "";
        String tips1 = "";
        String[] titls = new String[]{
                context.getString(R.string.m406过频降额起点),//过频降额起点
                context.getString(R.string.m407频率负载限制率),//恢复加载斜率
                context.getString(R.string.m409过频降额延时),//过频降额延时
        };

        int[] itemTypes = new int[]{
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
        };

        String[] register = new String[]{
                "", "",
                ""
        };
        float[] multiples = new float[]{
                1, 1, 1
        };
        String[] units = new String[]{"", "", ""
        };
        String[] hints = new String[]{
                tips, "", tips
        };
        int[][] funs = new int[][]{
                {3, 91, 91},//过频降额起点
                {3, 92, 92},//恢复加载斜率
                {3, 108, 108},//过频降额延时
        };
        int[][] funset = new int[][]{
                {6, 91, -1},//过频降额起点
                {6, 92, -1},//恢复加载斜率
                {6, 108, -1}//过频降额延时
        };

        String[][] items = new String[][]{
                {"PF=1",
                        context.getString(R.string.PF值设置), context.getString(R.string.默认PF曲线),
                        context.getString(R.string.用户设定PF曲线), context.getString(R.string.滞后无功功率1),
                        context.getString(R.string.滞后无功功率2), context.getString(R.string.QV模式),
                        context.getString(R.string.正负无功值调节), context.getString(R.string.static_capacitive_QV_mode),
                        context.getString(R.string.static_perceptual_QV_mode)
                },//运行PF为1

                {},//感性载率
                {},//容性载率
                {},//容性PF
                {},//感性PF
                {},//无功曲线切入/切出电压
                {},//PF校准系数
                {},//PF限制负载百分比点
                {},//PF限值
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
                        context.getString(R.string.android_key1423),
                        context.getString(R.string.波兰语),
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
//                context.getString(R.string.android_key950),
                context.getString(R.string.android_key959),
                context.getString(R.string.m411Island使能),

                context.getString(R.string.off_grid_box_enable),

                context.getString(R.string.m手动离网使能),

                context.getString(R.string.m离网使能),

                context.getString(R.string.android_key558),
                context.getString(R.string.android_key560),
//                context.getString(R.string.android_key1343),
                context.getString(R.string.android_key1344),
                context.getString(R.string.android_key2777),
                context.getString(R.string.phasewringmethord),
        };
        String[] hints = new String[]{
                "",
                "",
                "1~99",
                "",
                "",
                "",
                "",
//                "",
                "",
                "",
                "",
                "",
                "",
//                "",
                "",
                "",
                "",
                "",
                ""
        };
        int[] itemTypes = new int[]{
                UsSettingConstant.SETTING_TYPE_SWITCH,//开关逆变器
                UsSettingConstant.SETTING_TYPE_NEXT,//有功功率百分比
                UsSettingConstant.SETTING_TYPE_SELECT,//PV输入模式
                UsSettingConstant.SETTING_TYPE_NEXT,//干接点设置
                UsSettingConstant.SETTING_TYPE_SWITCH,//N至PE监测功能使能
                UsSettingConstant.SETTING_TYPE_SELECT,//宽电网电压范围使能
                UsSettingConstant.SETTING_TYPE_INPUT,//安规功能使能
//                UsSettingConstant.SETTING_TYPE_SWITCH,//电网N线使能
                UsSettingConstant.SETTING_TYPE_SWITCH,//指定的规格设置使能
                UsSettingConstant.SETTING_TYPE_SWITCH,//ISland使能
                UsSettingConstant.SETTING_TYPE_SWITCH,//ISland使能
                UsSettingConstant.SETTING_TYPE_SELECT,//手动离网使能
                UsSettingConstant.SETTING_TYPE_SWITCH,//ISland使能
                UsSettingConstant.SETTING_TYPE_SELECT,//离网频率
                UsSettingConstant.SETTING_TYPE_SELECT,//离网电压
//                UsSettingConstant.SETTING_TYPE_SELECT,//CT选择
                UsSettingConstant.SETTING_TYPE_SELECT,//电池类型选择
                UsSettingConstant.SETTING_TYPE_SELECT,//电池类型选择
                UsSettingConstant.SETTING_TYPE_SELECT//电池类型选择
        };
        String[] register = new String[]{
                "", "", "", "", "", "", "", "",
                "", "", "", "", "", "","","","",""
        };
        float[] multiples = new float[]{
                1, 1, 1, 1, 1, 1, 1,
                1, 1, 1, 1, 1, 1,1,1,1,1

        };
        String[] units = new String[]{
                "", "", "", "", "", "", "", "",
                "", "", "", "", "", "","","","",""
        };
        int[][] funs = new int[][]{
                {3, 0, 0},//开关逆变器
                {3, 0, 5},//有功功率百分比
                {3, 399, 399},//PV输入模式
                {3, 3016, 3019},//干接点设置
                {3, 235, 235},//N至PE检测功能使能
                {3, 236, 236},//宽电网电压范围使能
                {3, 1, 1},//安规功能使能
//                {3, 232, 232},//电网N线使能
                {3, 237, 237},//指定的规格设置使能
                {3, 230, 230},//ISLand使能

                {3, 3082, 3082},//离网盒子使能

                {3, 3021, 3021},//手动离网使能

                {3, 3079, 3079},//离网使能

                {3, 3081, 3081},//离网频率
                {3, 3080, 3080},//离网电压
//                {3, 3068, 3068},//CT选择
                {3, 3070, 3070},//电池类型选择
                {3, 612, 612},//工作模式
                {3, 613, 613},//电网类型
        };
        int[][] funset = new int[][]{
                {6, 0, 0},//开关逆变器
                {6, 0, 0},//有功功率百分比
                {6, 399, 0},//PV输入模式
                {6, 3016, 0},//干接点设置
                {6, 235, 0},//N至PE检测功能使能
                {6, 236, 0},//宽电网电压范围使能
                {6, 1, 0},//安规功能使能
//                {6, 232, 0},//电网N线使能
                {6, 237, 0},//指定的规格设置使能
                {6, 230, 0},//ISLand使能


                {6, 3082, 0},//离网盒子使能

                {6, 3021, 0},//手动离网使能

                {6, 3079, 0},//离网使能


                {6, 3081, 0},//离网频率
                {6, 3080, 0},//离网电压
//                {6, 3068, 0},//CT选择
                {6, 3070, 0},//电池类型选择
                {6, 612, 0},//工作模式
                {6, 613, 0},//工作模式
        };

        int[][] doubleFunset = new int[][]{
                {6, 45, -1},
                {6, 46, -1},
                {6, 47, -1},
                {6, 48, -1},
                {6, 49, -1},
                {6, 50, -1},
                {6, 48, -1},
//                {6, 49, -1},
                {6, 45, -1},
                {6, 46, -1},


                {6, 612, -1},



                {6, 47, -1},

                {6, 613, -1},


                {6, 48, -1},
                {6, 49, -1},
//                {6, 50, -1},
                {6, 48, -1},
                {6, 612, -1},
                {6, 613, -1}


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
//                {},
                {},
                {},

                {},
                {
                        context.getString(R.string.m89禁止), context.getString(R.string.m88使能),
                },
                {},

                {"50Hz", "60Hz"},
                {"230V", "208V", "240V"},
//                {context.getString(R.string.android_key161), context.getString(R.string.android_key162)},
                {context.getString(R.string.android_key355), context.getString(R.string.android_key356), context.getString(R.string.android_key1567)},
                {context.getString(R.string.mdefault), context.getString(R.string.system_retrofit), context.getString(R.string.multi_parallel)},
                {context.getString(R.string.android_key2798), context.getString(R.string.android_key2799), context.getString(R.string.裂相)},

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






    private static List<ALLSettingBean> getTl3xhSystemSetList(Context context) {
        List<ALLSettingBean> list = new ArrayList<>();
        String[] titls = new String[]{
                context.getString(R.string.android_key895),
                context.getString(R.string.android_key903),
                context.getString(R.string.android_key961),
                context.getString(R.string.android_key2925),
                context.getString(R.string.m414N至GND监测功能使能),
                context.getString(R.string.m415非标准电网电压范围使能),
                context.getString(R.string.android_key898),
//                context.getString(R.string.android_key950),
                context.getString(R.string.android_key959),
                context.getString(R.string.m411Island使能),


                context.getString(R.string.off_grid_box_enable),

                context.getString(R.string.m手动离网使能),

                context.getString(R.string.m离网使能),



                context.getString(R.string.android_key558),
                context.getString(R.string.android_key560),
//                context.getString(R.string.android_key1343),
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
//                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
//                "",
                "",
        };
        int[] itemTypes = new int[]{
                UsSettingConstant.SETTING_TYPE_SWITCH,//开关逆变器
                UsSettingConstant.SETTING_TYPE_NEXT,//有功功率百分比
                UsSettingConstant.SETTING_TYPE_SELECT,//PV输入模式
                UsSettingConstant.SETTING_TYPE_NEXT,//干接点设置
                UsSettingConstant.SETTING_TYPE_SWITCH,//N至PE监测功能使能
                UsSettingConstant.SETTING_TYPE_SELECT,//宽电网电压范围使能
                UsSettingConstant.SETTING_TYPE_INPUT,//安规功能使能
//                UsSettingConstant.SETTING_TYPE_SWITCH,//电网N线使能
                UsSettingConstant.SETTING_TYPE_SWITCH,//指定的规格设置使能
                UsSettingConstant.SETTING_TYPE_SWITCH,//ISland使能

                UsSettingConstant.SETTING_TYPE_SWITCH,//离网盒子使能
                UsSettingConstant.SETTING_TYPE_SELECT,//手动离网使能
                UsSettingConstant.SETTING_TYPE_SWITCH,//离网使能


                UsSettingConstant.SETTING_TYPE_SELECT,//离网频率
                UsSettingConstant.SETTING_TYPE_SELECT,//离网电压
//                UsSettingConstant.SETTING_TYPE_SELECT,//CT选择
                UsSettingConstant.SETTING_TYPE_SELECT,//电池类型选择
        };
        String[] register = new String[]{
                "", "", "", "", "", "", "", "",
                "", "", "", "", "", "","",""
        };
        float[] multiples = new float[]{
                1, 1, 1, 1, 1, 1, 1,
                1, 1, 1, 1, 1, 1,1,1

        };
        String[] units = new String[]{
                "", "", "", "", "", "", "", "",
                "", "", "", "", "", "","",""
        };
        int[][] funs = new int[][]{
                {3, 0, 0},//开关逆变器
                {3, 0, 5},//有功功率百分比
                {3, 399, 399},//PV输入模式
                {3, 3016, 3019},//干接点设置
                {3, 235, 235},//N至PE检测功能使能
                {3, 236, 236},//宽电网电压范围使能
                {3, 1, 1},//安规功能使能
//                {3, 232, 232},//电网N线使能
                {3, 237, 237},//指定的规格设置使能
                {3, 230, 230},//ISLand使能


                {3, 3082, 3082},//离网盒子使能

                {3, 3021, 3021},//手动离网使能

                {3, 3079, 3079},//离网使能


                {3, 3081, 3081},//离网频率
                {3, 3080, 3080},//离网电压
//                {3, 3068, 3068},//CT选择
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
//                {6, 232, 0},//电网N线使能
                {6, 237, 0},//指定的规格设置使能
                {6, 230, 0},//ISLand使能

                {6, 3082, 0},//离网盒子使能

                {6, 3021, 0},//手动离网使能

                {6, 3079, 0},//离网使能

                {6, 3081, 0},//离网频率
                {6, 3080, 0},//离网电压
//                {6, 3068, 0},//CT选择
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
//                {6, 49, -1},
                {6, 45, -1},
                {6, 46, -1},
                {6, 50, -1},
                {6, 47, -1},
                {6, 51, -1},
                {6, 48, -1},
                {6, 49, -1},
//                {6, 50, -1},
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
//                {},
                {},
                {},
                {},
                {
                        context.getString(R.string.m89禁止), context.getString(R.string.m88使能),
                },
                {},

                {"50Hz", "60Hz"},
                {"230V", "208V", "240V"},
//                {context.getString(R.string.android_key161), context.getString(R.string.android_key162)},
                {context.getString(R.string.android_key355), context.getString(R.string.android_key356), context.getString(R.string.android_key1567)},

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
                "", "", ""
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
                context.getString(R.string.android_key3048) + ":" + "38.0-58.0V",
                context.getString(R.string.android_key3048) + ":" + "0.0-60.0A",
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
                UsSettingConstant.SETTING_TYPE_SWITCH,
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
        String tips = "";
        String tips1 = "";
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
                UsSettingConstant.SETTING_TYPE_ONLYREAD,
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
                "", "", ""
        };
        float[] multiples = new float[]{
                1, 1, 0.1f, 1, 1, 0.1f, 1, 1, 1
        };
        String[] units = new String[]{"", "", "", "", "", "kWh", "", "", ""
        };
        String[] hints = new String[]{
                tips, "", tips, "", tips, "", tips, tips, tips
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
