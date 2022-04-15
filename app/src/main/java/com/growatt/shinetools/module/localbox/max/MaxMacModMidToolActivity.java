package com.growatt.shinetools.module.localbox.max;

import android.view.Gravity;
import android.view.View;

import com.growatt.shinetools.R;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.module.localbox.max.base.BaseMaxToolActivity;
import com.growatt.shinetools.module.localbox.tlx.base.BaseTLXEActivity;
import com.growatt.shinetools.module.localbox.tlxh.TLXHAutoTestOldInvActivity;
import com.growatt.shinetools.module.localbox.ustool.bean.UsToolParamBean;
import com.growatt.shinetools.module.localbox.max.config.MaxBasicSettingActivity;
import com.growatt.shinetools.module.localbox.max.config.MaxGridCodeSettingActivity;
import com.growatt.shinetools.module.localbox.max.config.MaxSystemConfigActivity;
import com.growatt.shinetools.module.localbox.max.type.DeviceConstant;
import com.growatt.shinetools.module.localbox.tlx.config.TLXQuickSettingActivity;
import com.growatt.shinetools.module.localbox.ustool.USAdvanceSetActivity;
import com.mylhyl.circledialog.CircleDialog;

public class MaxMacModMidToolActivity extends BaseTLXEActivity {
    @Override
    public void initStatusRes() {
        pidStatusStrs = new String[]{
                "", getString(R.string.all_Waiting),
                getString(R.string.all_Normal),
                getString(R.string.m故障)
        };
        statusTitles = new String[]{
                getString(R.string.all_Waiting), getString(R.string.all_Normal),
                getString(R.string.m226升级中), getString(R.string.m故障)
        };

        statusColors = new int[]{
                R.color.color_status_wait,
                R.color.color_status_grid,
                R.color.color_status_fault,
                R.color.color_status_upgrade
        };

        drawableStatus = new int[]{
                R.drawable.circle_wait,
                R.drawable.circle_grid,
                R.drawable.circle_fault,
                R.drawable.circle_upgrade
        };
    }

    @Override
    public boolean initIsUpstream() {
        return false;
    }

    @Override
    public void initEleRes() {
        eleTitles = new String[]{
                getString(R.string.android_key2019) + "\n" + "(kWh)",
                getString(R.string.m320功率) + "\n(W)",
        };

        eleItemTiles=new String[eleTitles.length][2];
        for (int i = 0; i < eleTitles.length; i++) {
            if (i==0){
                eleItemTiles[i][0]=getString(R.string.android_key408);
                eleItemTiles[i][1]=getString(R.string.android_key1912);
            }else {
                eleItemTiles[i][0]=getString(R.string.当前功率);
                eleItemTiles[i][1]=getString(R.string.m189额定功率);
            }
        }


        eleResId = new int[]{
                R.drawable.tlxh_ele_fadian, R.drawable.ele_power,
        };

    }

    @Override
    public void initDeviceType() {
        deviceType = DeviceConstant.MOD_MID_MAC;
    }

    @Override
    public void initGetDataArray() {
        funs = new int[][]{{3, 0, 124}, {4, 0, 99}, {4, 100, 199}, {4, 875, 999}};
        autoFun = new int[][]{{4, 0, 124}, {4, 125, 249}, {4, 875, 999}};
        deviceTypeFun = new int[]{3, 125, 249};
    }

    @Override
    public void initSetDataArray() {
        res = new int[]{
                R.drawable.quickly,
                R.drawable.system_config,
                R.drawable.param_setting,
                R.drawable.city_code,
                R.drawable.smart_check,
                R.drawable.advan_setting,
                R.drawable.tlx_auto_test,

                R.drawable.device_info
        };
        title = new String[]{
                getString(R.string.快速设置),
                getString(R.string.android_key3052),
                getString(R.string.m284参数设置),
                getString(R.string.市电码参数设置),
                getString(R.string.m285智能检测),
                getString(R.string.m286高级设置),
                getString(R.string.android_key171),
                getString(R.string.m291设备信息)
        };
    }

    @Override
    public void toSettingActivity(int position) {

        String title1 = "";
        Class clazz = null;

        switch (position) {
            case 0:
//                clazz = TlxFastConfigActivity.class;
                clazz = TLXQuickSettingActivity.class;
                break;
            case 1:
                clazz = MaxSystemConfigActivity.class;
                break;
            case 2:
                clazz = MaxBasicSettingActivity.class;
                break;
            case 3:
                clazz = MaxGridCodeSettingActivity.class;

                break;
            case 4:
                clazz = MaxCheckActivity.class;

                break;
            case 5:
                clazz = USAdvanceSetActivity.class;
                title1 = getString(R.string.高级设置);
                break;


            case 6:
                clazz= TLXHAutoTestOldInvActivity.class;
                break;

            case 7:
                clazz = Max230Ktl3HvtMaxInfoActivity.class;
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

        if (clazz != null) {
            if (position==6){
                Class finalClazz = clazz;
                new CircleDialog.Builder()
                        .setWidth(0.7f)
                        .setGravity(Gravity.CENTER)
                        .setTitle(getString(R.string.reminder))
                        .setText(getString(R.string.请确认是否为意大利机型))
                        .setNegative(getString(R.string.all_no), null)
                        .setPositive(getString(R.string.all_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                jumpMaxSet(finalClazz, title);
                            }
                        })
                        .show(getSupportFragmentManager());
            }else {
                jumpMaxSet(clazz, title);


            }

        }
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
                RegisterParseUtil.parseMax1500V4T0T125(mMaxData, bytes);
                break;
            case 1:
                RegisterParseUtil.parseMax1500V4T125T250(mMaxData, bytes);
                break;
            case 2:
                RegisterParseUtil.parseMax04T875T999(mMaxData, bytes);
                break;
        }
    }
}
