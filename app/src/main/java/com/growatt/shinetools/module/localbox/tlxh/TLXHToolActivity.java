package com.growatt.shinetools.module.localbox.tlxh;

import com.growatt.shinetools.R;
import com.growatt.shinetools.ShineToosApplication;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.module.inverterUpdata.InverterUpdataManager;
import com.growatt.shinetools.module.localbox.max.MaxAdvanceSetActivity;
import com.growatt.shinetools.module.localbox.max.MaxCheckActivity;
import com.growatt.shinetools.module.localbox.max.config.MaxBasicSettingActivity;
import com.growatt.shinetools.module.localbox.max.config.MaxGridCodeSettingActivity;
import com.growatt.shinetools.module.localbox.max.type.DeviceConstant;
import com.growatt.shinetools.module.localbox.mintool.TLXHAutoTestActivity;
import com.growatt.shinetools.module.localbox.tlx.base.TlxToolBaseActivity;
import com.growatt.shinetools.module.localbox.tlxh.config.TLXHChargeActivity;
import com.growatt.shinetools.module.localbox.tlxh.config.TLXHQuickSettingActivity;
import com.growatt.shinetools.module.localbox.tlxh.config.TLXHSystemSettingActivity;
import com.growatt.shinetools.module.localbox.ustool.bean.UsToolParamBean;

import java.io.File;

public class TLXHToolActivity extends TlxToolBaseActivity {


    @Override
    public void initStatusRes() {
        pidStatusStrs = new String[]{
                "", getString(R.string.all_Waiting), getString(R.string.all_Normal), getString(R.string.m故障)
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
                getString(R.string.android_key2019) + "\n" + "(kWh)",
                getString(R.string.android_key2371) + "\n(kWh)",
                getString(R.string.android_key2370) + "\n(kWh)",
                getString(R.string.android_key1319) + "\n(kWh)",
                getString(R.string.android_key1320) + "\n(kWh)",
        };
        eleResId = new int[]{
                R.drawable.tlxh_ele_fadian, R.drawable.tlxh_ele_chongdian,
                R.drawable.tlxh_ele_fangdian, R.drawable.tlxh_ele_bingwang, R.drawable.tlxh_ele_yonghushiyong
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
                R.drawable.tlxh_power_dangqian, R.drawable.tlxh_power_eding,
                R.drawable.tlxh_power_chongdian, R.drawable.tlxh_power_fangdian
        };
    }

    @Override
    public void initDeviceType() {
        deviceType = DeviceConstant.TLXH_INDEX;
    }

    @Override
    public void initGetDataArray() {
        funs = new int[][]{{3, 0, 124}, {3, 125, 249}, {4, 3000, 3124}, {4, 3125, 3249}};
        autoFun = new int[][]{{4, 3000, 3124}, {4, 3125, 3249}};
    }

    @Override
    public void initSetDataArray() {
        title = new String[]{
                getString(R.string.快速设置), getString(R.string.android_key3091), getString(R.string.android_key3056)
                , getString(R.string.android_key1308), getString(R.string.m285智能检测), getString(R.string.m284参数设置)
                , getString(R.string.m286高级设置), getString(R.string.m291设备信息)
        };
        res = new int[]{
                R.drawable.quickly, R.drawable.system_config, R.drawable.city_code,
                R.drawable.charge_manager, R.drawable.smart_check, R.drawable.param_setting,
                R.drawable.advan_setting, R.drawable.device_info
        };
    }

    @Override
    public void checkUpdata() {
        InverterUpdataManager.getInstance().checkUpdata(this,ShineToosApplication.INVERTER_UPDATA_FILE_DIR  +
                "Three Phase On-grid Inverter" + File.separator + "MIN TL-XH"+File.separator);

    }

    @Override
    public void toSettingActivity(int position) {
        UsToolParamBean item = usParamsetAdapter.getItem(position);
        final String title = item.getTitle();
        Class clazz = null;
        switch (position) {
            case 0:
                clazz = TLXHQuickSettingActivity.class;
                break;
            case 1:
                clazz = TLXHSystemSettingActivity.class;
                break;
            case 2:
                clazz = MaxGridCodeSettingActivity.class;
                break;
            case 3:
                clazz = TLXHChargeActivity.class;
                break;
            case 4://智能检测
                clazz = MaxCheckActivity.class;
                break;
            case 5://基本设置
                clazz = MaxBasicSettingActivity.class;
                break;

            case 6://高级设置
                clazz = MaxAdvanceSetActivity.class;
                break;
            case 7://设备信息
                clazz = TLXHDeviceInfoActivity.class;
                break;
            case 8:
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
                RegisterParseUtil.parseHold0T124(mMaxData, bytes);
                break;
            case 1:
                RegisterParseUtil.parseHold125T249(mMaxData, bytes);
                break;
            case 2:
                RegisterParseUtil.parseInput3kT3124(mMaxData, bytes);
                break;
            case 3:
                RegisterParseUtil.parseInput3125T3249(mMaxData, bytes);
                break;
        }
    }

    @Override
    public void parserMaxAuto(int count, byte[] bytes) {
        switch (count) {
            case 0:
                RegisterParseUtil.parseInput3kT3124(mMaxData, bytes);
                break;
            case 1:
                RegisterParseUtil.parseInput3125T3249(mMaxData, bytes);
                break;
        }
    }
}
