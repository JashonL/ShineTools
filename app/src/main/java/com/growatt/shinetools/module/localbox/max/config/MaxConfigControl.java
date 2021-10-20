package com.growatt.shinetools.module.localbox.max.config;

import com.growatt.shinetools.R;
import com.growatt.shinetools.ShineToosApplication;
import com.growatt.shinetools.bean.UsSettingConstant;
import com.growatt.shinetools.module.localbox.max.bean.MaxSettingBean;

import java.util.ArrayList;
import java.util.List;

public class MaxConfigControl {

    public enum MaxSettingEnum {
        MAX_QUICK_SETTING,//快速设置
        MAX_SYSTEM_SETTING,
        MAX_BASIC_SETTING,
        MAX_GRID_CODE_PARAMETERS_SETTING;
    }


    public static List<MaxSettingBean> getSettingList(MaxSettingEnum maxSettingEnum) {
        List<MaxSettingBean> list = new ArrayList<>();
        int[] titls = new int[0];
        int[] itemTypes = new int[0];
        String[] register = new String[0];
        String[] units = new String[0];
        int[][] funs = new int[0][];
        int[][] funset = new int[0][];
        switch (maxSettingEnum) {
            case MAX_QUICK_SETTING:
                titls = new int[]{
                        R.string.m国家安规,
                        R.string.android_key663,
                        R.string.android_key1416,
                        R.string.android_key982,
                        R.string.android_key2922};

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
                units = new String[]{
                        "",
                        "",
                        "",
                        "",
                        ""
                };
                funs = new int[][]{
                        {3, 0, 124},
                        {3, 0, 124},
                        {3, 0, 124},
                        {3, 0, 124},
                        {3, 0, 124}
                };
                funset = new int[][]{
                        {0x10, 118, 121},
                        {0x10, 118, 121},
                        {0x10, 118, 121},
                        {0x10, 118, 121},
                        {0x10, 118, 121}
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
            bean.setTitle(ShineToosApplication.getContext().getString(titls[i]));
            bean.setItemType(itemTypes[i]);
            bean.setRegister(register[i]);
            bean.setUnit(units[i]);
            bean.setFuns(funs[i]);
            bean.setFunSet(funset[i]);
            list.add(bean);
        }

        return list;
    }


}
