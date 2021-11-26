package com.growatt.shinetools.module.localbox.sph;

import com.growatt.shinetools.R;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.module.localbox.max.MaxAdvanceSetActivity;
import com.growatt.shinetools.module.localbox.max.type.DeviceConstant;
import com.growatt.shinetools.module.localbox.mintool.TLXHAutoTestActivity;
import com.growatt.shinetools.module.localbox.mix.MixToolChargeManagerActivity;
import com.growatt.shinetools.module.localbox.sph.base.SPHSPABaseActivity;
import com.growatt.shinetools.module.localbox.sph.config.SPASPHQuickSettingActivity;
import com.growatt.shinetools.module.localbox.sph.config.SPHSPABasicSettingActivity;
import com.growatt.shinetools.module.localbox.sph.config.SPHSPAGridCodeSettingActivity;
import com.growatt.shinetools.module.localbox.sph.config.SphSpaSystemSettingActivity;
import com.growatt.shinetools.module.localbox.ustool.bean.UsToolParamBean;

public class SPHSPAToolActivity extends SPHSPABaseActivity {
    @Override
    public void initStatusRes() {

        pidStatusStrs = new String[]{
                "",getString(R.string.all_Waiting),getString(R.string.all_Normal),getString(R.string.m故障)
        };

        statusTitles = new String[]{
                getString(R.string.all_Waiting), getString(R.string.all_Normal),
                getString(R.string.m226升级中), getString(R.string.m故障)
        };

        statusColors = new int[]{
                R.color.color_status_wait, R.color.color_status_grid, R.color.color_status_fault, R.color.color_status_upgrade
        };

        drawableStatus = new int[]{
                R.drawable.circle_wait, R.drawable.circle_grid, R.drawable.circle_fault, R.drawable.circle_upgrade
        };
    }

    @Override
    public void initEleRes() {

        eleTitles = new String[]{
                getString(R.string.photovoltaic_generatingcapacity)+ "\n(kWh)",
                getString(R.string.m1261Charged)+ "\n(kWh)",
                getString(R.string.m1260Discharged)+ "\n(kWh)",
                getString(R.string.m214电网取电)+ "\n(kWh)",
                getString(R.string.m217馈回电网)+ "\n(kWh)",
                getString(R.string.m用户使用电量)+ "\n(kWh)"
        };
        eleResId = new int[]{
                -1,R.drawable.tlxh_ele_fadian,R.drawable.tlxh_ele_chongdian,
                R.drawable.tlxh_ele_fangdian,R.drawable.mix_export,R.drawable.mix_import,R.drawable.tlxh_ele_yonghushiyong
        };
    }

    @Override
    public void initPowerRes() {

        powerTitles = new String[]{
                getString(R.string.android_key1993),
                getString(R.string.额定功率),
                getString(R.string.android_key1807),
                getString(R.string.android_key1824)
        };
        powerResId = new int[]{
                R.drawable.tlxh_power_dangqian,
                R.drawable.tlxh_power_eding,
                R.drawable.tlxh_power_chongdian,R.drawable.tlxh_power_fangdian
        };
    }

    @Override
    public void initDeviceType() {
        deviceType = DeviceConstant.SPA_SPH_INDEX;
    }

    @Override
    public void initGetDataArray() {

        funs = new int[][] {{3, 0, 124},{3, 125, 249},{4,0,124},{4,1000,1124}};
        autoFun = new int[][]{{4,0,124},{4,1000,1124}};
    }

    @Override
    public void initSetDataArray() {

        title = new String[]{
                //快速设置、系统配置、市电码设置
                getString(R.string.快速设置), getString(R.string.android_key3091), getString(R.string.android_key3056)
                //充放电管理、基本设置
                , getString(R.string.android_key1308), getString(R.string.basic_setting)
                //高级设置、设备信息、自动测试
                , getString(R.string.m286高级设置), getString(R.string.m291设备信息), getString(R.string.android_key171)
        };
        res = new int[]{
                R.drawable.quickly, R.drawable.system_config, R.drawable.city_code,
                R.drawable.charge_manager,  R.drawable.param_setting,
                R.drawable.advan_setting, R.drawable.device_info, R.drawable.tlx_auto_test
        };
    }

    @Override
    public void toSettingActivity(int position) {

        UsToolParamBean item = usParamsetAdapter.getItem(position);
        final String title = item.getTitle();
        Class clazz = null;
        switch (position) {
            case 0://快速设置
                clazz = SPASPHQuickSettingActivity.class;
                break;
            case 1://系统设置
                clazz = SphSpaSystemSettingActivity.class;
                break;
            case 2://市电码设置
                clazz = SPHSPAGridCodeSettingActivity.class;
                break;
            case 3://充放电管理
                clazz = MixToolChargeManagerActivity.class;
                break;
            case 4://基本设置
                clazz = SPHSPABasicSettingActivity.class;
                break;

            case 5://高级设置
                clazz = MaxAdvanceSetActivity.class;
                break;
            case 6://设备信息
                clazz = SPHSPADeviceInfoActivity.class;
                break;
            case 7:
                clazz = TLXHAutoTestActivity.class;
                break;
            default:
                clazz = null;
                break;
        }


        if (clazz != null) {
            jumpMaxSet(clazz, title);
        }
    }

    @Override
    public void parserData(int count, byte[] bytes) {
        switch (count) {
            case 0:
                RegisterParseUtil.parseHold0T124Mix(mMaxData, bytes);
                break;
            case 1:
                RegisterParseUtil.parseHold125T249Mix(mMaxData, bytes);
                break;
            case 2:
                RegisterParseUtil.parseInput0T124Mix(mMaxData, bytes);
                isBDC = true;
                break;
            case 3:
                RegisterParseUtil.parseInput1kT1124Mix(mMaxData, bytes);
                break;
        }
    }

    @Override
    public void parserMaxAuto(int count, byte[] bytes) {

        switch (count) {
            case 0:
                RegisterParseUtil.parseInput0T124Mix(mMaxData,bytes);
                break;
            case 1:
                RegisterParseUtil.parseInput1kT1124Mix(mMaxData,bytes);
                break;
        }
    }
}
