package com.growatt.shinetools.module.localbox.tlx;

import com.growatt.shinetools.R;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.module.localbox.max.MaxCheckActivity;
import com.growatt.shinetools.module.localbox.max.base.BaseMaxToolActivity;
import com.growatt.shinetools.module.localbox.max.type.DeviceConstant;
import com.growatt.shinetools.module.localbox.tlx.config.TLXGridCodeSettingActivity;
import com.growatt.shinetools.module.localbox.tlx.config.TLXQuickSettingActivity;
import com.growatt.shinetools.module.localbox.tlx.config.TLXSystemSettingActivity;
import com.growatt.shinetools.module.localbox.tlx.config.TlxBasicSettingActivity;
import com.growatt.shinetools.module.localbox.ustool.USAdvanceSetActivity;
import com.growatt.shinetools.module.localbox.ustool.bean.UsToolParamBean;

public class TLXTLEToolActivity  extends BaseMaxToolActivity {

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
    public boolean initIsUpstream() {
        return true;
    }

    @Override
    public void initEleRes() {
        eleTitles = new String[]{
                getString(R.string.android_key2019) + "\n" + "(kWh)",
                getString(R.string.m320功率) + "\n(W)",
        };


        eleResId = new int[]{
                R.drawable.tlxh_ele_fadian, R.drawable.ele_power,
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
        //快速设置   系统设置   基本设置   参数设置   智能检测   高级设置   设备信息
        title = new String[]{
                getString(R.string.快速设置),
                getString(R.string.系统设置),
                getString(R.string.android_key352),
                getString(R.string.android_key3056),
                getString(R.string.android_key625),
                getString(R.string.android_key626),
                getString(R.string.android_key637)
        };
        res = new int[]{
                R.drawable.quickly,
                R.drawable.system_config,
                R.drawable.param_setting,
                R.drawable.city_code,
                R.drawable.smart_check,
                R.drawable.advan_setting,
                R.drawable.device_info
        };
    }

    @Override
    public void toSettingActivity(int position) {
        UsToolParamBean item = usParamsetAdapter.getItem(position);
        final String title = item.getTitle();
        final Class clazz;

        switch (position) {
            case 0://快速设置
//                clazz = TLXHQuickSettingActivity.class;
                clazz = TLXQuickSettingActivity.class;
                break;
            case 1://系统设置
                clazz = TLXSystemSettingActivity.class;
                break;
            case 2://基本设置
                clazz = TlxBasicSettingActivity.class;
                break;
            case 3://参数设置
                clazz = TLXGridCodeSettingActivity.class;
                break;
            case 4://智能检测
                clazz = MaxCheckActivity.class;
                break;
            case 5://高级设置
                clazz = USAdvanceSetActivity.class;
                break;
            case 6://设备信息
                clazz = TLXDeviceInfoActivity.class;
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
