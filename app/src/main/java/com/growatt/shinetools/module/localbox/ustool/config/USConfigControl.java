package com.growatt.shinetools.module.localbox.ustool.config;

import android.content.Context;

import com.growatt.shinetools.R;
import com.growatt.shinetools.bean.UsSettingConstant;
import com.growatt.shinetools.module.localbox.max.bean.ALLSettingBean;

import java.util.ArrayList;
import java.util.List;

public class USConfigControl {

    public enum USSettingEnum {
        US_QUICK_SETTING,//US快速设置

    }



    public static List<ALLSettingBean> getSettingList(USConfigControl.USSettingEnum maxSettingEnum, Context context) {
        List<ALLSettingBean> list = new ArrayList<>();
        switch (maxSettingEnum) {
            case US_QUICK_SETTING:
                list=getUSQuickSetList(context);
                break;
        }
        return list;
    }




    private static List<ALLSettingBean> getUSQuickSetList(Context context) {
        List<ALLSettingBean> list = new ArrayList<>();
        String[] titls = new String[]{
                context.getString(R.string.wifi配置),//wifi配置
                context.getString(R.string.android_key256),//功率采集器
                context.getString(R.string.市电码),//市电码
                context.getString(R.string.电压等级),//电压等级
                context.getString(R.string.输出模式),//输出模式
                context.getString(R.string.android_key145),//AC couple功能使能
                context.getString(R.string.EMS),//EMS
                context.getString(R.string.battery_diagnosis),//电池诊断
                context.getString(R.string.m4),//时间
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
        };
        int[] itemTypes = new int[]{
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_SELECT,
                UsSettingConstant.SETTING_TYPE_SELECT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_SWITCH,
                UsSettingConstant.SETTING_TYPE_EXPLAIN,
                UsSettingConstant.SETTING_TYPE_INPUT,
                UsSettingConstant.SETTING_TYPE_INPUT,
        };
        String[] register = new String[]{
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
        float[] multiples = new float[]{
                1, 1, 1, 1, 1, 1, 1,1,1
        };
        String[] units = new String[]{
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
        int[][] funs = new int[][]{
                {3, 20000, 20000},//配网状态
                {3, 533, 533},//功率采集器
                {3, 0, 124},//市电码、时间、电压等级
                {3, 0, 124},//电压等级
                {3, 0, 124},//输出模式  固定（裂相）
                {3,1,1},//AC couple功能使能
                 {3, 3144, 3144},//EMS:只读取
                 {4, 3118, 3118},//电池诊断
                 {3, 0, 124},//时间
        };
        int[][] funset = new int[][]{
                {6, 20000, 0},//配网状态
                {6, 533, 0},//功率采集器
                {0x10, 118, 121},//市电码
                {6, 118, 0},//电压等级
                {6, 118, 0},//输出模式  固定（裂相）
                {6,1,1},//AC couple功能使能
                {6, 3144, 0},//EMS:只读取
                {6, 3118, 0},//电池诊断
                {0x10, 45, 50},//时间
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
                {6, 49, -1},
                {6, 49, -1},
        };


        int[][] setValueFuns = new int[][]{
                {0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0},
                {-1, -1, -1, -1},//市电码
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {-1, -1, -1, -1, -1, -1},
        };

        String[][] items = new String[][]{
                {},
                {
                        context.getString(R.string.android_key1427),
                        context.getString(R.string.m484电表)
                },
                {
                        context.getString(R.string.意大利),
                        context.getString(R.string.英语),
                },
                {},
                {},
                {},
                {
                        context.getString(R.string.m209电池优先),context.getString(R.string.m208负载优先)

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
            bean.setSetValues(setValueFuns[i]);
            bean.setMul(multiples[i]);
            bean.setUuid(i);
            list.add(bean);
        }
        return list;
    }

}
