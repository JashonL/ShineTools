package com.growatt.shinetools.module.localbox.sph;

import com.growatt.shinetools.R;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.module.localbox.sph.base.BaseSphInfoActivity;

public class SPHSPADeviceInfoActivity extends BaseSphInfoActivity {
    @Override
    public int getHeaderView() {
        return R.layout.header_us_device_info;
    }


    @Override
    public void initVolFreCurString() {
        c1Title1 = new String[]{
                "PV1", "PV2"
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
                String.format("AC %s(W)",getString(R.string.m320功率)),
                String.format("AC %s(Hz)",getString(R.string.m321频率)),
                String.format("AC1 %s(V)",getString(R.string.m318电压)),
                String.format("AC1 %s(W)",getString(R.string.m320功率)),
                String.format("%s(W)",getString(R.string.m261取电功率)),
                String.format("%s(W)",getString(R.string.m262馈电功率))
        };
    }

    @Override
    public void initSVGAPFString() {
        c34Title1 = new String[]{
                "R", "S", "T"
        };
        c34Title2 = new String[]{
                String.format("%s(A)",getString(R.string.mCT侧电流)),
                String.format("%s(Var)",getString(R.string.mCT侧无功)),
                String.format("%s(A)",getString(R.string.mCT侧谐波量)),
                String.format("%s(Var)",getString(R.string.m补偿无功量)),
                String.format("%s(A)",getString(R.string.m补偿谐波量)),
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
                getString(R.string.m312厂商信息)
//                ,  getString(R.string.m313机器型号)
                , getString(R.string.dataloggers_list_serial),  getString(R.string.m314Model号),
                getString(R.string.m控制软件版本),  getString(R.string.m通信软件版本)
        };

    }

    @Override
    public void initInternalParamString() {
        c6Title1 = new String[]{
                "SysFaultWord","SysFaultWord1","SysFaultWord2",
                "SysFaultWord3","SysFaultWord4","SysFaultWord5",
                "SysFaultWord6","SysFaultWord7",
                "+Bus", "-Bus",
                "Status", "Priority"
        };
    }

    @Override
    public int[][] initGetDataArray() {
        return new int[][] {{3, 0, 124},{3, 125, 249},{4,0,124},{4,1000,1124}};
    }

    @Override
    public String[] deratModes() {
        return new String[]{
                getString(R.string.m无降额),getString(R.string.PV高压降载),getString(R.string.老化固定功率降载),
                getString(R.string.电网高压降载),getString(R.string.过频降载),getString(R.string.DC源模式降载),
                getString(R.string.逆变模块过温降载),getString(R.string.有功设定限载),getString(R.string.m保留),getString(R.string.m保留)
                ,getString(R.string.内部环境过温降载),getString(R.string.外部环境过温降载),getString(R.string.线路阻抗降载)
                , getString(R.string.并机防逆流降载),getString(R.string.单机防逆流降载),getString(R.string.负载优先模式降载),getString(R.string.检测CT错反接降载)
        };
    }

    @Override
    public void initStatus() {

        mixStatus = new String[]{
                getString(R.string.m201等待模式),getString(R.string.m202自检模式)
                ,"Reserved" ,"SysFault module" ,"Flash module" ,"PVBATOnline module"
                ,"BatOnline module" ,"PVOfflineMode module" ,"BatOfflineMode module"
        };
        mixPriority = new String[]{
                "Load","Battery","Grid"
        };
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
}
