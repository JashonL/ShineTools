package com.growatt.shinetools.module.localbox.tlxh;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.growatt.shinetools.R;
import com.growatt.shinetools.ShineToosApplication;
import com.growatt.shinetools.modbusbox.RegisterParseUtil;
import com.growatt.shinetools.module.inverterUpdata.DeviceManualUpdataActivity;
import com.growatt.shinetools.module.inverterUpdata.UpgradePath;
import com.growatt.shinetools.module.localbox.single.SingleBaseInfoActivity;

import static com.growatt.shinetools.constant.GlobalConstant.END_USER;

public class TLXHDeviceInfoActivity extends SingleBaseInfoActivity {
    @Override
    public int getHeaderView() {
        return R.layout.header_tlxh_device_info;
    }

    @Override
    public void setOther() {
        isOther=true;
    }


    @Override
    public void initVolFreCurString() {
        c1Title1 = new String[]{
                "PV1", "PV2", "PV3", "PV4", "PV5", "PV6", "PV7", "PV8"
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
                "+Bus", "-Bus"
                , getString(R.string.m降额模式)
        };
    }

    @Override
    public int[][] initGetDataArray() {
        return new int[][]{{3, 0, 124}, {3, 125, 249}, {4, 3000, 3124}, {4, 3125, 3249}};
    }

    @Override
    public String[] deratModes() {
        return new String[]{
                getString(R.string.m无降额), getString(R.string.PV高压降载), getString(R.string.老化固定功率降载),
                getString(R.string.电网高压降载), getString(R.string.过频降载), getString(R.string.DC源模式降载),
                getString(R.string.逆变模块过温降载), getString(R.string.有功设定限载), getString(R.string.m保留), getString(R.string.m保留)
                , getString(R.string.内部环境过温降载), getString(R.string.外部环境过温降载), getString(R.string.线路阻抗降载)
                , getString(R.string.并机防逆流降载), getString(R.string.单机防逆流降载), getString(R.string.负载优先模式降载), getString(R.string.检测CT错反接降载)
        };
    }

    @Override
    public void parserData(int count, byte[] bytes) {
        switch (count) {
            case 0:
                RegisterParseUtil.parseHold0T124(mMaxData, bytes);
                break;
            case 1:
//                RegisterParseUtil.parseHold125T249(mMaxData, bytes);
                RegisterParseUtil.parseTL3XH125T249(mMaxData, bytes);
                break;
//            case 2:
//                RegisterParseUtil.parseMax2(mMaxData, bytes);
//                break;
//            case 3:
//                RegisterParseUtil.parseMax3(mMaxData, bytes);
//                break;
            case 2:
                RegisterParseUtil.parseInput3kT3124(mMaxData, bytes);
                isBDC = true;
                break;
            case 3:
                RegisterParseUtil.parseInput3125T3249(mMaxData, bytes);
                break;
        }
    }

    @Override
    public void initUpdata() {
        super.initUpdata();
        View upTitle = header.findViewById(R.id.tvTitle_updata);
        TextView tvhead = upTitle.findViewById(R.id.tvHeadTitle);
        tvhead.setTextColor(ContextCompat.getColor(this, R.color.color_text_33));
        upTitle.setOnClickListener(view -> {
            if (END_USER != ShineToosApplication.getContext().getUser_type()) {
                Intent intent = new Intent(TLXHDeviceInfoActivity.this, DeviceManualUpdataActivity.class);
                intent.putExtra("path", UpgradePath.MIN_TL_XH_PATH);
                startActivity(intent);
            }else {
                toast(R.string.android_key2099);
            }
        });

    }


}
