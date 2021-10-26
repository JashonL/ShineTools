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
        MAX_SYSTEM_SETTING,
        MAX_BASIC_SETTING,
        MAX_GRID_CODE_PARAMETERS_SETTING;
    }


    public static List<MaxSettingBean> getSettingList(MaxSettingEnum maxSettingEnum, Context context) {
        List<MaxSettingBean> list = new ArrayList<>();
        String[] titls = new String[0];
        int[] itemTypes = new int[0];
        String[] register = new String[0];
        String[] units = new String[0];
        float[] multiples = new float[0];//倍数集合
        int[][] funs = new int[0][];
        int[][] funset = new int[0][];
        int[][] doubleFunset = new int[0][];
        int[] items = new int[0];
        String[] hints = new String[0];
        switch (maxSettingEnum) {
            case MAX_QUICK_SETTING:
                titls = new String[]{
                        context.getString(R.string.m国家安规),
                        context.getString(R.string.android_key663),
                        context.getString(R.string.android_key1416),
                        context.getString(R.string.android_key982),
                        context.getString(R.string.android_key2922),
                };

                hints = new String[]{
                        "",
                        "",
                        "",
                        "",
                        ""
                };

                itemTypes = new int[]{
                        UsSettingConstant.SETTING_TYPE_NEXT,
                        UsSettingConstant.SETTING_TYPE_INPUT,
                        UsSettingConstant.SETTING_TYPE_INPUT,
                        UsSettingConstant.SETTING_TYPE_INPUT,
                        UsSettingConstant.SETTING_TYPE_INPUT
                };

                register = new String[]{
                        "",
                        "",
                        "",
                        "",
                        ""
                };
                multiples = new float[]{
                        1, 1, 1, 1, 1
                };
                units = new String[]{
                        "",
                        "",
                        "",
                        "",
                        ""
                };
                funs = new int[][]{
                        {3, 0, 124},
                        {3, 45, 50},
                        {3, 15, 15},
                        {3, 30, 30},
                        {3, 0, 124}
                };
                funset = new int[][]{
                        {0x10, 118, 121},
                        {0x10, 118, 121},
                        {6, 15, 0},
                        {6, 30, 0},
                        {0x10, 118, 121}
                };


                doubleFunset = new int[][]{{6, 45, -1}, {6, 46, -1}, {6, 47, -1}, {6, 48, -1}, {6, 49, -1}, {6, 50, -1}};


                items = new int[]{
                        R.string.android_key1417,
                        R.string.android_key1418,
                        R.string.android_key1420,
                        R.string.android_key1421,
                        R.string.android_key1422,
                        R.string.android_key1423,
                        R.string.android_key186
                };

                break;

            case MAX_AFCI_FUNCTION:
                String tips = context.getString(R.string.android_key3048) + ":" + "0~65000" + "(" + context.getString(R.string.AFCI阈值) + 1
                        + "<" + context.getString(R.string.AFCI阈值) + 2 + "<" + context.getString(R.string.AFCI阈值) + 3 + ")";
                String tips1 = context.getString(R.string.android_key3048) + ":" + "0~255";
                titls = new String[]{
                        context.getString(R.string.AFCI阈值) + 1,
                        context.getString(R.string.AFCI阈值) + 2,
                        context.getString(R.string.AFCI阈值) + 3,
                        context.getString(R.string.FFT最大累计次数),
                        context.getString(R.string.AFCI曲线扫描),
                };

                itemTypes = new int[]{
                        UsSettingConstant.SETTING_TYPE_INPUT,
                        UsSettingConstant.SETTING_TYPE_INPUT,
                        UsSettingConstant.SETTING_TYPE_INPUT,
                        UsSettingConstant.SETTING_TYPE_INPUT,
                        UsSettingConstant.SETTING_TYPE_INPUT
                };

                register = new String[]{
                        "",
                        "",
                        "",
                        "",
                        ""
                };
                multiples = new float[]{
                        1, 1, 1, 1, 1
                };
                units = new String[]{
                        "",
                        "",
                        "",
                        "",
                        ""
                };
                hints = new String[]{tips, tips, tips, tips, tips1};
                funs = new int[][]{
                         {3, 544, 544}, {3, 545, 545}, {3, 546, 546}, {3, 547, 547},{3,0,0}
                };
                funset = new int[][]{
                         {6, 544, -1}, {6, 545, -1}, {6, 546, -1}, {6, 547, -1},{3,0,0}
                };
                break;


            case MAX_BASIC_SETTING:


                break;

            case MAX_SYSTEM_SETTING:
                break;

            case MAX_GRID_CODE_PARAMETERS_SETTING:
                break;

        }

        for (int i = 0; i < titls.length; i++) {
            MaxSettingBean bean = new MaxSettingBean();
            bean.setTitle(titls[i]);
            bean.setItemType(itemTypes[i]);
            bean.setRegister(register[i]);
            bean.setUnit(units[i]);
            bean.setFuns(funs[i]);
            bean.setFunSet(funset[i]);
            bean.setItems(items);
            bean.setHint(hints[i]);
            bean.setDoubleFunset(doubleFunset);
            bean.setMul(multiples[i]);
            list.add(bean);
        }

        return list;
    }


}
