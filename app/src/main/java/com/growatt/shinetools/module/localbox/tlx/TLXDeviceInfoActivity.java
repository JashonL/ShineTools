package com.growatt.shinetools.module.localbox.tlx;

import com.growatt.shinetools.R;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.module.localbox.single.SingleBaseInfoActivity;

public class TLXDeviceInfoActivity extends SingleBaseInfoActivity {
    @Override
    public void initACVolCurString() {
        c1Title1 = new String[]{
                "PV1", "PV2"
        };

        c1Title2 = new String[]{
                String.format("%s(V)",getString(R.string.m318电压)),
                String.format("%s(A)",getString(R.string.m319电流))
        };
    }

    @Override
    public void initStringVolCurString() {

      c2Title1 = new String[]{
                "Str1", "Str2", "Str3", "Str4", "Str5", "Str6", "Str7", "Str8",
                "Str9", "Str10", "Str11", "Str12", "Str13", "Str14", "Str15", "Str16"
        };
        c2Title2 = new String[]{
                String.format("%s(V)",getString(R.string.m318电压)),
                String.format("%s(A)",getString(R.string.m319电流))
        };
    }

    @Override
    public void initVolFreCurString() {

        c3Title1 =new String[] {
                "R", "S", "T"
        };

        c3Title2 = new String[]{
                String.format("%s(V)",getString(R.string.m318电压)),
                String.format("%s(Hz)",getString(R.string.m321频率)),
                String.format("%s(A)",getString(R.string.m319电流)),
                String.format("%s(W)",getString(R.string.m320功率)),
                "PF"
        };

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
    public void initSVGAPFString() {

        c5Title1 = new String[]{
                getString(R.string.m312厂商信息),  getString(R.string.m313机器型号),
                getString(R.string.dataloggers_list_serial),  getString(R.string.m314Model号),
                getString(R.string.m控制软件版本),  getString(R.string.m通信软件版本)
        };
    }

    @Override
    public void initPIDString() {

         c4Title1 = new String[]{
                "PID1", "PID2", "PID3", "PID4", "PID5", "PID6", "PID7", "PID8"
        };


        c4Title2 = new String[]{
                String.format("%s(V)",getString(R.string.m318电压)),
                String.format("%s(mA)",getString(R.string.m319电流))
        };

    }

    @Override
    public void initAboutDeviceString() {

        c5Title1 = new String[]{
                getString(R.string.m312厂商信息),  getString(R.string.m313机器型号),
                getString(R.string.dataloggers_list_serial),  getString(R.string.m314Model号),
                getString(R.string.m控制软件版本),  getString(R.string.m通信软件版本)
        };
    }

    @Override
    public void initInternalParamString() {
        c6Title1 = new String[]{
                getString(R.string.m305并网倒计时),  getString(R.string.m306功率百分比),
                "ISO",
                getString(R.string.m307内部环境温度),  getString(R.string.m308Boost温度),
                getString(R.string.m309INV温度),
                "+Bus", "-Bus"
                , getString(R.string.m降额模式)
        };
    }

    @Override
    public int[][] initGetDataArray() {
        return new int[][] {{3, 0, 124},{3, 125, 249},{4,3000,3124}};
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
    public int getHeaderView() {
        return R.layout.header_tlxe_device_info;
    }

    @Override
    public void setOther() {
        isOther=false;
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
        }
    }
}
