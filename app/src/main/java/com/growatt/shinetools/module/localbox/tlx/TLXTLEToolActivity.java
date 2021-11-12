package com.growatt.shinetools.module.localbox.tlx;

import com.growatt.shinetools.R;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.module.localbox.TlxToolBaseActivity;
import com.growatt.shinetools.module.localbox.max.MaxCheckActivity;
import com.growatt.shinetools.module.localbox.ustool.bean.UsToolParamBean;
import com.growatt.shinetools.module.localbox.max.type.DeviceConstant;
import com.growatt.shinetools.module.localbox.mintool.TLXToolConfigActivity;
import com.growatt.shinetools.module.localbox.mintool.TLXToolParamSetActivity;
import com.growatt.shinetools.module.localbox.ustool.USAdvanceSetActivity;

public class TLXTLEToolActivity  extends TlxToolBaseActivity {

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
                getString(R.string.android_key2019) + "\n" + "(kWh)"
        };


        eleResId = new int[]{
                R.drawable.tlxh_ele_fadian
        };
    }

    @Override
    public void initPowerRes() {
        powerTitles = new String[]{
                getString(R.string.android_key1993),
                getString(R.string.额定功率),

        };
        powerResId = new int[]{
                R.drawable.tlxh_power_dangqian, R.drawable.tlxh_power_eding,
        };
    }

    @Override
    public void initDeviceType() {
        deviceType = DeviceConstant.TLXH_INDEX;
    }

    @Override
    public void initGetDataArray() {
        funs = new int[][]{{3, 0, 124},{3, 125, 249},{4,3000,3124}};
        autoFun = new int[][]{{4,3000,3124}};
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
    public void toSettingActivity(int position) {
        UsToolParamBean item = usParamsetAdapter.getItem(position);
        final String title = item.getTitle();
        final Class clazz;
        switch (position){
            case 0:
                clazz = null;
                break;
            case 1:
                clazz = TLXToolConfigActivity.class;
                break;
            case 2:
                clazz = TLXToolParamSetActivity.class;
                break;
            case 3:
                clazz = MaxCheckActivity.class;
                break;
            case 4:
                clazz = USAdvanceSetActivity.class;
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
        if (count == 0) {
            RegisterParseUtil.parseInput3kT3124(mMaxData, bytes);
        }
    }
}
