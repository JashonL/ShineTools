package com.growatt.shinetools.module.localbox.max;

import com.growatt.shinetools.R;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.module.localbox.max.base.BaseMaxToolActivity;
import com.growatt.shinetools.module.localbox.max.bean.UsToolParamBean;
import com.growatt.shinetools.module.localbox.max.config.MaxBasicSettingActivity;
import com.growatt.shinetools.module.localbox.max.config.MaxGridCodeSettingActivity;
import com.growatt.shinetools.module.localbox.max.config.MaxSystemConfigActivity;
import com.growatt.shinetools.module.localbox.tlx.TlxFastConfigActivity;
import com.growatt.shinetools.module.localbox.ustool.USAdvanceSetActivity;

public class MaxMacModMidToolActivity extends BaseMaxToolActivity {
    @Override
    public void initStatusRes() {
        pidStatusStrs = new String[]{
                "",getString(R.string.all_Waiting),
                getString(R.string.all_Normal),
                getString(R.string.m故障)
        };
        statusTitles = new String[]{
                getString(R.string.all_Waiting), getString(R.string.all_Normal),
                getString(R.string.m226升级中), getString(R.string.m故障)
        };

        statusColors=new int[]{
                R.color.color_status_wait,
                R.color.color_status_grid,
                R.color.color_status_fault,
                R.color.color_status_upgrade
        };

        drawableStatus=new int[]{
                R.drawable.circle_wait,
                R.drawable.circle_grid,
                R.drawable.circle_fault,
                R.drawable.circle_upgrade
        };
    }

    @Override
    public void initEleRes() {
        eleTitles = new String[]{
                getString(R.string.android_key2019) + "\n" + "(kWh)",
                getString(R.string.m320功率) + "\n(kWh)",
        };


        eleResId = new int[]{
                R.drawable.tlxh_ele_fadian, R.drawable.ele_power,
        };

    }

    @Override
    public void initGetDataArray() {
        funs=new int[][] {{3, 0, 124}, {4, 0, 99}, {4, 100, 199}, {4, 875, 999}};
        autoFun=new int[][]{{4, 0, 124}, {4, 125, 249}, {4, 875, 999}};
        deviceTypeFun=new int[]{3,125,249};
    }

    @Override
    public void toSettingActivity(int position) {

        String title1 = "";
        Class clazz = null;

        switch (position) {
            case 0:
                clazz = TlxFastConfigActivity.class;
                break;
            case 1:
                clazz = MaxSystemConfigActivity.class;
                break;
            case 2:
                clazz = MaxBasicSettingActivity.class;
                break;
            case 3:
                clazz = MaxCheck1500VActivity.class;
                break;
            case 4:
                clazz = MaxGridCodeSettingActivity.class;
                break;
            case 5:
                clazz = USAdvanceSetActivity.class;
                title1 = getString(R.string.高级设置);
                break;

            case 6:
                clazz = Max230Ktl3HvtDeviceInfoActivity.class;
                break;
            default:
                clazz = null;
                break;
        }

        try {
            UsToolParamBean item = usParamsetAdapter.getItem(position);
            title1 = item.getTitle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final String title = title1;
        if (clazz == null) return;
        jumpMaxSet(clazz, title);
    }






    @Override
    public void parserData(int count, byte[] bytes) {
        switch (count) {
            case 0:
                RegisterParseUtil.parseHold0T124(mMaxData, bytes);
                break;
            case 1:
                RegisterParseUtil.parseMax1500V2(mMaxData, bytes);
                break;
            case 2:
                RegisterParseUtil.parseMax1500V3(mMaxData, bytes);
                break;
            case 3:
                RegisterParseUtil.parseMax04T875T999(mMaxData, bytes);
                break;
        }
    }

    @Override
    public void parserMaxAuto(int count, byte[] bytes) {
        switch (count) {
            case 0:
                RegisterParseUtil.parseMax1500V4T0T125(mMaxData,bytes);
                break;
            case 1:
                RegisterParseUtil.parseMax1500V4T125T250(mMaxData,bytes);
                break;
            case 2:
                RegisterParseUtil.parseMax04T875T999(mMaxData, bytes);
                break;
        }
    }
}
