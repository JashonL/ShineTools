package com.growatt.shinetools.module.localbox.max;

import com.growatt.shinetools.R;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.module.localbox.max.base.BaseDeviceInfoActivity;

public class Max230Ktl3HvtDeviceInfoActivity extends BaseDeviceInfoActivity {

    @Override
    public void initVolFreCurString() {
        c1Title1 = new String[]{
                "PV1", "PV2", "PV3", "PV4", "PV5", "PV6", "PV7", "PV8"
                , "PV9", "PV10", "PV11", "PV12", "PV13", "PV14", "PV15", "PV16"
        };

        c1Title2 = new String[]{
                String.format("%s(V)", getString(R.string.m318电压)),
                String.format("%s(A)", getString(R.string.m319电流))
        };

    }

    @Override
    public void initStringVolCurString() {
        c2Title1 = new String[]{
                "Str1", "Str2", "Str3", "Str4", "Str5", "Str6", "Str7", "Str8",
                "Str9", "Str10", "Str11", "Str12", "Str13", "Str14", "Str15", "Str16"
                , "Str17", "Str18", "Str19", "Str20", "Str21", "Str22", "Str23", "Str24",
                "Str25", "Str26", "Str27", "Str28", "Str29", "Str30", "Str31", "Str32"
        };
        c2Title2 = new String[]{
                String.format("%s(V)", getString(R.string.m318电压)),
                String.format("%s(A)", getString(R.string.m319电流))
        };
    }

    @Override
    public void initACVolCurString() {
        c3Title1 = new String[]{
                "R", "S", "T"
        };

        c3Title2 = new String[]{
                String.format("%s(V)", getString(R.string.m318电压)),
                String.format("%s(Hz)", getString(R.string.m321频率)),
                String.format("%s(A)", getString(R.string.m319电流)),
                String.format("%s(W)", getString(R.string.m320功率)),
                "PF"
        };
    }

    @Override
    public void initSVGAPFString() {
        c34Title1 = new String[]{
                "R", "S", "T"
        };
        c34Title2 = new String[]{
                String.format("%s(A)", getString(R.string.mCT侧电流)),
                String.format("%s(Var)", getString(R.string.mCT侧无功)),
                String.format("%s(A)", getString(R.string.mCT侧谐波量)),
                String.format("%s(Var)", getString(R.string.m补偿无功量)),
                String.format("%s(A)", getString(R.string.m补偿谐波量)),
                getString(R.string.mSVG工作状态),
        };
    }

    @Override
    public void initPIDString() {
        c4Title1 = new String[]{
                "PID1", "PID2", "PID3", "PID4", "PID5", "PID6", "PID7", "PID8"
        };
        c4Title2 = new String[]{
                String.format("%s(V)", getString(R.string.m318电压)),
                String.format("%s(mA)", getString(R.string.m319电流))
        };
    }

    @Override
    public void initAboutDeviceString() {
        c5Title1 = new String[]{
                getString(R.string.m312厂商信息), getString(R.string.m313机器型号),
                getString(R.string.dataloggers_list_serial), getString(R.string.m314Model号),
//                getString(R.string.m315固件外部版本),  getString(R.string.m316固件内部版本)
                getString(R.string.m控制软件版本), getString(R.string.m通信软件版本)
        };


    }

    @Override
    public void initInternalParamString() {
        c6Title1 = new String[]{
                getString(R.string.m305并网倒计时), getString(R.string.m306功率百分比),
                "ISO",
                getString(R.string.m307内部环境温度), getString(R.string.m308Boost温度),
                getString(R.string.m309INV温度),
                "+Bus", "-Bus",
                getString(R.string.m310PID故障信息), getString(R.string.m311PID状态)
        };
    }

    @Override
    public int[][] initGetDataArray() {
        return new int[][]{{3, 0, 124}, {4, 0, 99}, {4, 100, 199}, {4, 875, 999}};
    }

    @Override
    public String[] deratModes() {
        return new String[]{
                "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
        };
    }

    @Override
    public int getHeaderView() {
        return R.layout.header_max_device_info;
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


}
