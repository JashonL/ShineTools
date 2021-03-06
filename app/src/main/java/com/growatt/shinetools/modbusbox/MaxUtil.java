package com.growatt.shinetools.modbusbox;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ScrollView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.growatt.shinetools.MainActivity;
import com.growatt.shinetools.R;
import com.growatt.shinetools.constant.GlobalConstant;
import com.growatt.shinetools.listeners.OnEmptyListener;
import com.growatt.shinetools.modbusbox.bean.MaxCheckOneKeyISOBean;
import com.growatt.shinetools.modbusbox.bean.MaxCheckOneKeyRSTBean;
import com.growatt.shinetools.modbusbox.bean.MaxErrorBean;
import com.growatt.shinetools.modbusbox.listeners.OnHandlerStrListener;
import com.growatt.shinetools.utils.DialogUtils;
import com.growatt.shinetools.utils.Log;
import com.growatt.shinetools.utils.LogUtil;
import com.growatt.shinetools.utils.MyToastUtils;
import com.growatt.shinetools.utils.SharedPreferencesUnit;
import com.growatt.shinetools.utils.chartformatter.MaxMarkerView;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.view.listener.OnInputClickListener;
import com.mylhyl.circledialog.view.listener.OnLvItemClickListener;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created???2017/11/24 on 14:30
 * Author:gaideng on dg
 * Description:
 */

public class MaxUtil {
    public static SimpleDateFormat sdf = new SimpleDateFormat("MM.dd");
    /**
     * ?????????????????????????????????06??????????????????????????????0106??????
     * @param desc:??????????????????????????????????????????modbus??????
     * @return
     */
    public static boolean checkReceiverFull(byte[] desc){
        //??????????????????
        byte[] bs1 = RegisterParseUtil.removePro(desc);
        return checkReceiver(bs1);
    }
    /**
     * ?????????????????????????????????06??????????????????????????????0106??????
     * @param desc:??????????????????????????????????????????modbus??????
     * @return
     */
    public static boolean checkReceiverFull10(byte[] desc){
        //??????????????????
        byte[] bs1 = RegisterParseUtil.removePro(desc);
        return checkReceiver10(bs1);
    }

    /**
     * ?????????????????????06???????????????????????????
     * @param desc????????????????????????????????????modbus??????
     * @return
     */
    //???????????????????????????0106??????
    public static boolean checkReceiver(byte[] desc){
        if (desc == null || desc.length < 2){ return false;}
        if (desc[1] == 6){
            return true;
        }else {
            return false;
        }
    }
    //???????????????????????????0106??????
    public static boolean checkReceiver10(byte[] desc){
        if (desc == null || desc.length < 2){ return false;}
        if (desc[1] == 0x10){
            return true;
        }else {
            return false;
        }
    }

    /**
     * ????????????????????????
     *  //??????????????????
     //                            byte[] bs = RegisterParseUtil.removePro17Fun6(bytes);
     //??????int???
     //                            int value0 = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytes(bs, 1, 0, 1));
     //?????????ui
     //                            mTvContent1.setText(readStr + ":");
     */
    public static boolean isCheckFull(Context context, byte[] desc){
        boolean isCheck = checkReceiverFull(desc);
        if (isCheck) {
            MyToastUtils.toast(R.string.android_key121);
        } else {
            MyToastUtils.toast(R.string.android_key12);
        }
        return isCheck;
    }
    public static boolean isCheckFull10(Context context, byte[] desc){
        boolean isCheck = checkReceiverFull10(desc);
//        if (isCheck) {
//            T.make(context.getString(R.string.all_success),context);
//        } else {
//            T.make(context.getString(R.string.all_failed),context);
//        }
        return isCheck;
    }
    /**
     * ????????????????????????model
     * @param model
     * @return
     */
    public static String getDeviceModel(int model) {
        StringBuilder sb = new StringBuilder();
        sb.append("A").append(Integer.toHexString((model & 0xF0000000) >>> 28));
        sb.append("B").append(Integer.toHexString((model & 0x0F000000) >> 24));
        sb.append("D").append(Integer.toHexString((model & 0x00F00000) >> 20));
        sb.append("T").append(Integer.toHexString((model & 0x000F0000) >> 16));
        sb.append("P").append(Integer.toHexString((model & 0x0000F000) >> 12));
        sb.append("U").append(Integer.toHexString((model & 0x00000F00) >> 8));
        sb.append("M").append(Integer.toHexString((model & 0x000000F0) >> 4));
        sb.append("S").append(Integer.toHexString( model & 0x0000000F));
        return sb.toString().toUpperCase();
    }
    /**
     * ????????????????????????model new
     * @param model
     * @return
     */
    public static String getDeviceModelNew4(Long model) {
        StringBuilder sb = new StringBuilder();
        sb.append("S").append(getDeviceModelSingleNew(model,16)).append(getDeviceModelSingleNew(model,15));
        sb.append("B").append(getDeviceModelSingleNew(model,14)).append(getDeviceModelSingleNew(model,13));
        sb.append("D").append(getDeviceModelSingleNew(model,12)).append(getDeviceModelSingleNew(model,11));
        sb.append("T").append(getDeviceModelSingleNew(model,10)).append(getDeviceModelSingleNew(model,9));
        sb.append("P").append(getDeviceModelSingleNew(model,8)).append(getDeviceModelSingleNew(model,7));
        sb.append("U").append(getDeviceModelSingleNew(model,6)).append(getDeviceModelSingleNew(model,5));
        sb.append("M").append(getDeviceModelSingleNew(model,4)).append(getDeviceModelSingleNew(model,3)).append(getDeviceModelSingleNew(model,2)).append(getDeviceModelSingleNew(model,1));
        return sb.toString().toUpperCase();
    }
    /**
     * ????????????????????????model???????????????
     * @param value ??????????????????
     * @param type???1->1,2->4,3->8,4->12
     * @return
     */
    public static String getDeviceModelSingle(int value, int type) {
        int offset = type >= 1 ? (type -1)*4:type;
        int andSet = (int) (15 * Math.round(Math.pow(16,type-1)));
        return Integer.toHexString((value & andSet) >>> offset).toUpperCase();
    }
    /**
     * ????????????????????????model???????????????
     * @param value ??????????????????
     * @param type???1->1,2->4,3->8,4->12
     * @return
     */
    public static String getDeviceModelSingleNew(long value, int type) {
//        if (type > 16) return "0";
//        if (type > 4){
//            StringBuilder sb = new StringBuilder("0000000000000000");
//            int index = 16-type;
//            sb.replace(index,index+1,"F");
//            String hex = sb.toString();
//            BigInteger result = value.add(new BigInteger(hex, 16)).shiftRight(4 * (type - 1));
//            return result.toString(16);
//        }else {
//            String hex = "000000000000FFFF";
//            BigInteger result = value.add(new BigInteger(hex, 16));
//            return result.toString(16);
//        }
            int offset = type >= 1 ? (type -1)*4:type;
            long andSet  = 15 * Math.round(Math.pow(16, type - 1));
            return Long.toHexString((value & andSet) >>> offset).toUpperCase();
    }

    /**
     * ?????????????????????wifi??????
     */
    public static void showJumpWifiSet(final FragmentActivity act, String str){
        DialogUtils.getInstance().closeLoadingDialog();
        new CircleDialog.Builder()
                .setWidth(0.7f)
                .setTitle(act.getString(R.string.android_key2263))
                .setText(str)
                .setNegative(act.getString(R.string.android_key2152), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        act.startActivity(new Intent(act,ToolSelectActivity.class));
                        act.startActivity(new Intent(act, MainActivity.class));
                    }
                })
                .setPositive(act.getString(R.string.android_key2154), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                        act.startActivityForResult(intent, 107);
                    }
                })
                .show(act.getSupportFragmentManager());
    }
    /**
     * ?????????????????????wifi??????
     */
    public static void showJumpWifiSetCancel(final FragmentActivity act, String str){
        DialogUtils.getInstance().closeLoadingDialog();
        new CircleDialog.Builder()
                .setWidth(0.7f)
                .setTitle(act.getString(R.string.android_key2263))
                .setText(str)
                .setNegative(act.getString(R.string.android_key2152), null)
                .setPositive(act.getString(R.string.android_key2154), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                        act.startActivityForResult(intent, 107);
                    }
                })
                .show(act.getSupportFragmentManager());
    }
    /**
     * ????????????wifi
     */
    public static void showJumpWifiSet(final FragmentActivity act, String title, String content){
        new CircleDialog.Builder()
                .setWidth(0.7f)
                .setTitle(title)
                .setText(content)
                .setNegative(act.getString(R.string.android_key2152),null)
                .setPositive(act.getString(R.string.android_key819), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                        act.startActivity(intent);
                    }
                })
                .show(act.getSupportFragmentManager());
    }
    /**
     * ????????????Max????????????
     * @param listener:"1"??????????????????
     */
    public static void showSetMaxPwd(final FragmentActivity act, final OnHandlerStrListener listener){
        new CircleDialog.Builder()
                .setWidth(0.8f)
                .setTitle(act.getString(R.string.android_key2263))
                .setSubTitle(act.getString(R.string.android_key2084))
                .setInputText("")
                .setInputShowKeyboard(true)
                .setNegative(act.getString(R.string.android_key2152),null)
                .setInputCounter(1000, (maxLen, currentLen) -> "")
                .setPositiveInput(act.getString(R.string.android_key2154), new OnInputClickListener() {
                    @Override
                    public boolean onClick(String text, View v) {
                        if (TextUtils.isEmpty(text)) {
                            listener.handlerDealStr("0");
                            return true;
                        }
                        //??????????????????
                        String pwd = SharedPreferencesUnit.getInstance(act).get(GlobalConstant.MAX_PWD);
                        if (text.equals(pwd)) {
                            listener.handlerDealStr("1");
                        } else {
                            listener.handlerDealStr("0");
                        }
                        return true;
                    }
                })
                .show(act.getSupportFragmentManager());
    }
    /**
     * ?????????????????????Max??????????????????
     */
    public static void getControlMaxPwd(FragmentActivity act, final OnHandlerStrListener listener){
        try {
            String pwd = SharedPreferencesUnit.getInstance(act).getDefNull(GlobalConstant.MAX_PWD);
            if (TextUtils.isEmpty(pwd)){
                new CircleDialog.Builder()
                        .setWidth(0.7f)
                        .setGravity(Gravity.CENTER)
                        .setTitle(act.getString(R.string.android_key629))
                        .setItems(new String[]{act.getString(R.string.android_key733), act.getString(R.string.android_key735)}, new OnLvItemClickListener() {
                            @Override
                            public boolean onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                listener.handlerDealStr(String.valueOf(position));
                                return true;
                            }
                        })
                        .setNegative(act.getString(R.string.android_key2152),null)
                        .show(act.getSupportFragmentManager());
            }else {
                listener.handlerDealStr("-1");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * ??????Max???????????????????????????
     */
    public static String getErrContentByCode(Context context, int code){
        String error = "";
        switch (code){
            case 1:error = "M3 Receive Main DSP SCI abnormal";break;
            case 2:error = "M3 Receive Slave DSP SCI abnormal";break;
            case 3:error = "Main DSP Receive M3 SCI abnormal";break;
            case 4:error = "Slave DSP Receive M3 SCI abnormal";break;
            case 5:error = "Main DSP Receive SPI abnormal";break;
            case 6:error = "Slave DSP Receive SPI abnormal";break;
            case 9:error = "SPS power fault";break;
            case 13:error = context.getString(R.string.android_key724);break;
            case 14:error = context.getString(R.string.android_key726);break;
            case 15:error = context.getString(R.string.android_key728);break;
            case 18:error = context.getString(R.string.android_key730);break;
            case 20:error = context.getString(R.string.android_key3121);break;
            case 22:error = context.getString(R.string.android_key3121);break;
            case 23:error = "Main DSP Bus abnormal";break;
            case 24:error = "Slave DSP Bus abnormal";break;
            case 25:error = context.getString(R.string.android_key737);break;
            case 26:error = context.getString(R.string.android_key738);break;
            case 27:error = context.getString(R.string.android_key740);break;
            case 28:error = context.getString(R.string.android_key743);break;
            case 29:error = context.getString(R.string.android_key745);break;
            case 30:error = context.getString(R.string.android_key747);break;
            case 31:error = context.getString(R.string.android_key749);break;
            case 33:error = "BUS Sample and PV sample inconsistent";break;
            case 34:error = "GFCI Sample inconsistent";break;
            case 35:error = "ISO Sample inconsistent";break;
            case 36:error = "BUS Sample inconsistent";break;
            case 37:error = "Grid Sample inconsistent";break;
            default:error = String.format("%s:%d",context.getString(R.string.android_key148),code);break;
        }
        return error;
    }
    /**
     * ??????tlx ??????????????????????????????
     */
    public static String getErrContentByCodeNew(Context context, int code){
        String error = "";
        switch (code){
            case 99:
                error = context.getString(R.string.android_key770);
                break;
            case 101:
                error = context.getString(R.string.android_key718);
                break;
            case 102:
                error = context.getString(R.string.android_key720);
                break;
            case 108:
                error = context.getString(R.string.android_key722);
                break;
            case 110:
                error = context.getString(R.string.android_key3122);
                break;
            case 112:
                error = context.getString(R.string.android_key724);
                break;
            case 113:
                error = context.getString(R.string.android_key726);
                break;
            case 114:
                error = context.getString(R.string.android_key728);
                break;
            case 117:
                error = context.getString(R.string.android_key730);
                break;
            case 119:
                error = context.getString(R.string.android_key3121);
                break;
            case 121:
                error = context.getString(R.string.android_key732);
                break;
            case 122:
                error = context.getString(R.string.android_key734);
                break;
            case 124:
                error = context.getString(R.string.android_key737);
                break;
            case 125:
                error = context.getString(R.string.android_key738);
                break;
            case 126:
                error = context.getString(R.string.android_key740);
                break;
            case 127:
                error = context.getString(R.string.android_key743);
                break;
            case 128:
                error = context.getString(R.string.android_key745);
                break;
            case 129:
                error = context.getString(R.string.android_key747);
                break;
            case 130:
                error = context.getString(R.string.android_key749);
                break;
            case 131:
               error = context.getString(R.string.android_key3123);
                break;
            case 200:error = context.getString(R.string.android_key801);break;
            case 201:error = context.getString(R.string.android_key802);break;
            case 202:error = context.getString(R.string.android_key807);break;
            case 203:error = context.getString(R.string.android_key808);break;
            case 300:error = context.getString(R.string.android_key812);break;
            case 301:error = context.getString(R.string.android_key815);break;
            case 302:error = context.getString(R.string.android_key820);break;
            case 303:error = context.getString(R.string.android_key823);break;
            case 304:error = context.getString(R.string.android_key825);break;
            case 305:error = context.getString(R.string.android_key829);break;
            case 306:error = context.getString(R.string.android_key831);break;
            case 307:error = context.getString(R.string.android_key834);break;
            case 308:error = context.getString(R.string.android_key837);break;
            case 400:error = context.getString(R.string.android_key841);break;
            case 401:error = context.getString(R.string.android_key844);break;
            case 402:error = context.getString(R.string.android_key848);break;
            case 403:error = context.getString(R.string.android_key851);break;
            case 404:error = context.getString(R.string.android_key853);break;
            case 405:error = context.getString(R.string.android_key856);break;
            case 406:error = context.getString(R.string.android_key859);break;
            case 407:error = context.getString(R.string.android_key863);break;
            case 408:error = context.getString(R.string.android_key865);break;
            case 409:error = context.getString(R.string.android_key867);break;
            case 410:error = context.getString(R.string.android_key870);break;
            case 411:error = context.getString(R.string.android_key874);break;
            case 412:error = context.getString(R.string.android_key878);break;
            case 413:error = context.getString(R.string.android_key881);break;
            case 414:error = context.getString(R.string.android_key884);break;
            case 415:error = context.getString(R.string.android_key887);break;
            case 416:error = context.getString(R.string.android_key890);break;
            case 417:error = context.getString(R.string.android_key892);break;
            case 418:error = context.getString(R.string.android_key896);break;
            case 419:error = context.getString(R.string.android_key899);break;
            case 420:error = context.getString(R.string.android_key902);break;
            case 421:error = context.getString(R.string.android_key904);break;
            case 422:error = context.getString(R.string.android_key911);break;
            case 423:error = context.getString(R.string.android_key912);break;
            case 424:error = context.getString(R.string.android_key915);break;
            case 425:error = context.getString(R.string.android_key918);break;
            case 500:error = context.getString(R.string.android_key921);break;
            case 501:error = context.getString(R.string.android_key925);break;
            case 502:error = context.getString(R.string.android_key927);break;
            case 503:error = context.getString(R.string.android_key930);break;
            case 504:error = context.getString(R.string.android_key935);break;
            case 505:error = context.getString(R.string.android_key937);break;
            case 506:error = context.getString(R.string.android_key942);break;
            case 507:error = context.getString(R.string.android_key945);break;
            case 508:error = context.getString(R.string.android_key946);break;
            case 600:error = context.getString(R.string.android_key951);break;
            case 601:error = context.getString(R.string.android_key953);break;
            case 602:error = context.getString(R.string.android_key957);break;
            case 603:error = context.getString(R.string.android_key958);break;
            case 604:error = context.getString(R.string.android_key963);break;
            case 605:error = context.getString(R.string.android_key965);break;
            default:error = String.format("%s:%d",context.getString(R.string.android_key148),code);break;
        }
        return error;
    }
    /**
     * ??????tlx ??????????????????????????????
     */
    public static String getWarmContentByCodeNew(Context context, int code){
        String error = "";
        switch (code){
            case 99:
                error = context.getString(R.string.android_key772);
                break;
            case 100:
                error = context.getString(R.string.android_key751);
                break;
            case 104:
                error = context.getString(R.string.android_key753);
                break;
            case 106:
                error = context.getString(R.string.android_key756);
                break;
            case 107:
                error = context.getString(R.string.android_key3124);
                break;
            case 108:
                error = context.getString(R.string.android_key3125);
                break;
            case 109:
                error = context.getString(R.string.android_key761);
                break;
            case 110:
                error = context.getString(R.string.android_key764);
                break;
            case 111:
                error = context.getString(R.string.android_key767);
                break;
            case 200:error = context.getString(R.string.android_key969);break;
            case 201:error = context.getString(R.string.android_key973);break;
            case 202:error = context.getString(R.string.android_key976);break;
            case 203:error = context.getString(R.string.android_key978);break;
            case 204:error = context.getString(R.string.android_key980);break;
            case 205:error = context.getString(R.string.android_key985);break;
            case 206:error = context.getString(R.string.android_key987);break;
            case 207:error = context.getString(R.string.android_key989);break;
            case 208:error = context.getString(R.string.android_key994);break;
            case 209:error = context.getString(R.string.android_key995);break;
            case 210:error = context.getString(R.string.android_key1000);break;
            case 300:error = context.getString(R.string.android_key1004);break;
            case 301:error = context.getString(R.string.android_key1007);break;
            case 302:error = context.getString(R.string.android_key1010);break;
            case 303:error = context.getString(R.string.android_key1013);break;
            case 304:error = context.getString(R.string.android_key1016);break;
            case 305:error = context.getString(R.string.android_key1017);break;
            case 306:error = context.getString(R.string.android_key1020);break;
            case 307:error = context.getString(R.string.android_key1024);break;
            case 400:error = context.getString(R.string.android_key1026);break;
            case 401:error = context.getString(R.string.android_key1030);break;
            case 402:error = context.getString(R.string.android_key1035);break;
            case 403:error = context.getString(R.string.android_key1038);break;
            case 404:error = context.getString(R.string.android_key1039);break;
            case 405:error = context.getString(R.string.android_key1043);break;
            case 406:error = context.getString(R.string.android_key1047);break;
            case 500:error = context.getString(R.string.android_key1048);break;
            case 501:error = context.getString(R.string.android_key1052);break;
            case 502:error = context.getString(R.string.android_key1054);break;
            case 503:error = context.getString(R.string.android_key1057);break;
            case 504:error = context.getString(R.string.android_key1060);break;
            case 505:error = context.getString(R.string.android_key1064);break;
            case 506:error = context.getString(R.string.android_key935);break;
            case 507:error = context.getString(R.string.android_key1070);break;
            case 508:error = context.getString(R.string.android_key1073);break;
            case 509:error = context.getString(R.string.android_key1077);break;
            case 600:error = context.getString(R.string.android_key1080);break;
            case 601:error = context.getString(R.string.android_key1084);break;
            case 602:error = context.getString(R.string.android_key1086);break;
            default:error = String.format("%s:%d",context.getString(R.string.android_key2072),code);break;
        }
        return error;
    }

    /**
     * ???????????????????????????????????????MM-dd HH:mm;
     * @param item
     * @return
     */
    public static String getMaxErrTimeByErrBean(MaxErrorBean item){
        if (item == null) return "";
        StringBuilder sb = new StringBuilder();
        //??????int???
        int value1 = item.getErrMonth();
        int value2 = item.getErrDay();
        int value3 = item.getErrHour();
        int value4 = item.getErrMin();
        //??????ui
        if (value1 < 10) {
            sb.append("0");
        }
        sb.append(value1).append("-");
        if (value2 < 10) {
            sb.append("0");
        }
        sb.append(value2).append(" ");
        if (value3 < 10) {
            sb.append("0");
        }
        sb.append(value3).append(":");
        if (value4 < 10) {
            sb.append("0");
        }
        sb.append(value4);
        return sb.toString();
    }


    /**
     * ??????crc??????????????????????????????
     * @param crcBytes????????????????????????crc:01 03 02 ?????? crc1 crc2
     * @return
     */
    public static boolean checkCRC(byte[] crcBytes){
        //??????crc??????
        if (crcBytes == null || crcBytes.length<2) return false;
        int lenAll = crcBytes.length;
        byte[] noCrcBytes = new byte[lenAll-2];
        System.arraycopy(crcBytes,0,noCrcBytes,0,lenAll-2);
        int crc = CRC16.calcCrc16(noCrcBytes);
        byte[] newCrcs = MyByte.hexStringToBytes(String.format("%04x", crc));
        if (newCrcs[1] == crcBytes[lenAll-2] && newCrcs[0] == crcBytes[lenAll-1]){
            return true;
        }else {
            return false;
        }
    }



    /**
     * ?????????????????????
     * @param context
     * @param lineChart?????????
     * @param dataList????????????
     * @param colors???????????????????????????
     * @param colors_a?????????????????????????????????
     * @param count???????????????
     * @param highLightColor???????????????
     */
    public static void setLineChartData(Context context, LineChart lineChart, List<ArrayList<Entry>> dataList, int[] colors, int[] colors_a, int count, int highLightColor) {
        setLineChartDataSpe(context,lineChart,dataList,colors,colors_a,count,highLightColor,false);
    }
    /**
     * ?????????????????????:?????????
     * @param context
     * @param lineChart?????????
     * @param dataList????????????
     * @param colors???????????????????????????
     * @param colors_a?????????????????????????????????
     * @param count???????????????
     * @param highLightColor???????????????
     *  @param flag:???????????????
     */
    public static void setLineChartDataSpe(Context context, LineChart lineChart, List<ArrayList<Entry>> dataList, int[] colors, int[] colors_a, int count, int highLightColor, boolean flag) {
        if (lineChart == null || dataList == null) return;
        List<ILineDataSet> dataSets = new ArrayList<>();
        LineData lineData = lineChart.getData();
        //?????????????????????
//        float minX = dataList.get(0).get(0).getX();
        float minX = 0f;
        if (lineData != null && lineData.getDataSetCount() >= count ){
            for (int i=0;i<count;i++){
                LineDataSet dataSet = (LineDataSet) lineData.getDataSetByIndex(i);
                dataSet.setValues(dataList.get(i));
            }
            lineChart.getXAxis().setAxisMinimum(minX);
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        }else {
            for (int i=0;i<count;i++){
                LineDataSet dataSet = new LineDataSet(dataList.get(i),"");
                dataSet.setDrawIcons(false);
                //????????????????????????
                dataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
                //??????????????????
                dataSet.setCubicIntensity(0.5f);
                // ?????????true???????????? LineDataSet ?????????????????????true???
                dataSet.setDrawCircles(false);
//            dataSet.enableDashedLine(10f, 5f, 0f);
//            dataSet.enableDashedHighlightLine(10f, 5f, 0f);
                dataSet.setDrawVerticalHighlightIndicator(true);
                dataSet.setDrawHorizontalHighlightIndicator(false);
                dataSet.setHighLightColor(ContextCompat.getColor(context,highLightColor));//?????????????????????
                dataSet.setColor(ContextCompat.getColor(context, colors[i]));
//            dataSet.setCircleColor(colors[i]);
                dataSet.setLineWidth(1.5f);
//            dataSet.setCircleRadius(3f);
                dataSet.setDrawCircleHole(false);
                dataSet.setValueTextSize(9f);
                dataSet.setDrawFilled(false);
                dataSet.setFormLineWidth(1f);
                dataSet.setDrawValues(false);
                dataSet.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
                dataSet.setFormSize(15.0f);
                dataSets.add(dataSet);
            }
            lineData = new LineData(dataSets);
            lineChart.setData(lineData);
            lineChart.getXAxis().setAxisMinimum(minX);
            lineData.notifyDataChanged();
            lineChart.notifyDataSetChanged();
        }
        setMaxY(lineChart, flag);
//        lineChart.animateX(500);
        lineChart.invalidate();
    }
    /**
     * ?????????????????????:?????????
     * @param context
     * @param lineChart?????????
     * @param dataList????????????
     * @param colors???????????????????????????
     * @param colors_a?????????????????????????????????
     * @param count???????????????
     * @param highLightColor???????????????
     *  @param flag:???????????????
     */
    public static void setLineChartDataSpeColor(Context context, LineChart lineChart, List<ArrayList<Entry>> dataList, String[] colors, String[] colors_a, int count, int highLightColor, boolean flag) {
        if (lineChart == null || dataList == null) return;
        List<ILineDataSet> dataSets = new ArrayList<>();
        LineData lineData = lineChart.getData();
        //?????????????????????
//        float minX = dataList.get(0).get(0).getX();
        float minX = 0f;
        if (lineData != null && lineData.getDataSetCount() >= count ){
            for (int i=0;i<count;i++){
                LineDataSet dataSet = (LineDataSet) lineData.getDataSetByIndex(i);
                dataSet.setValues(dataList.get(i));
            }
            lineChart.getXAxis().setAxisMinimum(minX);
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        }else {
            for (int i=0;i<count;i++){
                LineDataSet dataSet = new LineDataSet(dataList.get(i),"");
                dataSet.setDrawIcons(false);
                //????????????????????????
                dataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
                //??????????????????
                dataSet.setCubicIntensity(0.5f);
                // ?????????true???????????? LineDataSet ?????????????????????true???
                dataSet.setDrawCircles(false);
//            dataSet.enableDashedLine(10f, 5f, 0f);
//            dataSet.enableDashedHighlightLine(10f, 5f, 0f);
                dataSet.setDrawVerticalHighlightIndicator(true);
                dataSet.setDrawHorizontalHighlightIndicator(false);
                dataSet.setHighLightColor(ContextCompat.getColor(context,highLightColor));//?????????????????????
                dataSet.setColor(Color.parseColor(colors[i]));
//            dataSet.setCircleColor(colors[i]);
                dataSet.setLineWidth(1.5f);
//            dataSet.setCircleRadius(3f);
                dataSet.setDrawCircleHole(false);
                dataSet.setValueTextSize(9f);
                dataSet.setDrawFilled(false);
                dataSet.setFormLineWidth(1f);
                dataSet.setDrawValues(false);
                dataSet.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
                dataSet.setFormSize(15.0f);
                dataSets.add(dataSet);
            }
            lineData = new LineData(dataSets);
            lineChart.setData(lineData);
            lineChart.getXAxis().setAxisMinimum(minX);
            lineData.notifyDataChanged();
            lineChart.notifyDataSetChanged();
        }
        setMaxY(lineChart, flag);
//        lineChart.animateX(500);
        lineChart.invalidate();
    }

    public static void setMaxY(LineChart lineChart, boolean flag) {
        if (flag){
            float yMax = lineChart.getYMax() * 1.1f;
            YAxis axisLeft = lineChart.getAxisLeft();
            if (yMax > 0){
                axisLeft.setAxisMaximum(yMax);
                axisLeft.setAxisMinimum(-yMax);
            }else if (yMax < 0){
                axisLeft.setAxisMaximum(-yMax);
                axisLeft.setAxisMinimum(yMax);
            }
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
            lineChart.invalidate();
        }
    }

    public static float getValueByPos(Entry pos1,Entry pos2,float posX){
        float x1 = pos1.getX();
        float y1 = pos1.getY();
        float x2 = pos2.getX();
        float y2 = pos2.getY();
        float value  = (y2 - y1 )/x2-x1 * (posX - x1) + y1;
        return value;
    }
    public static float getValueByPos(float x1,float y1,float x2,float y2, float posX){
//        float value  = (y2 - y1 )/x2-x1 * (posX - x1) + y1;
        float x = x1-x2;
        if (x == 0){
            return 0;
        }
        float value  = (y1*posX - y2*posX + y2*x1 - y1*x2)/(x1-x2);
        return value;
    }

   public static float groupSpace = 0.19f;
    public static float barSpace = 0.02f; // x2 dataset
    public static float barWidth = 0.25f; // x2 dataset
    public static float barStart = 2.0f ;
    // (0.45 + 0.02) * 2 + 0.06 = 1.00 -> interval per "group"


    public static void replaceDataSet(LineChart lineChart, List<ArrayList<Entry>> dataList, int index) {
        LineData data = lineChart.getData();
        if (data != null) {
            LineDataSet set = (LineDataSet) data.getDataSetByIndex(index);
            if (dataList.size() > index){
                set.setValues(dataList.get(index));
            }
            data.notifyDataChanged();
            lineChart.notifyDataSetChanged();
            lineChart.invalidate();
        }
    }
    public static void clearDataSetByIndex(LineChart lineChart,int index) {
        LineData data = lineChart.getData();
        if (data != null) {
            LineDataSet set = (LineDataSet) data.getDataSetByIndex(index);
            ArrayList<Entry> entries = new ArrayList<Entry>();
            entries.add(new Entry(0,0));
            set.setValues(entries);
            data.notifyDataChanged();
            lineChart.notifyDataSetChanged();
            lineChart.invalidate();
        }
    }
    public static void clearDataSetByIndexIV(LineChart lineChart,int index) {
        LineData data = lineChart.getData();
        if (data != null) {
            LineDataSet set = (LineDataSet) data.getDataSetByIndex(index);
            ArrayList<Entry> entries = new ArrayList<Entry>();
            entries.add(new Entry(0,0));
            set.setValues(entries);
            data.notifyDataChanged();
            lineChart.notifyDataSetChanged();
            lineChart.invalidate();
        }
    }




    /**
     * ???????????????
     */
    /**
     * ??????????????????
     * @param lineChart??????????????????
     * @param format???y?????????????????????
     *              1???????????????????????????
     *              0??????????????????????????????????????????
     * @param unit:??????????????????
     * @param hasXGrid:?????????x????????????
     * @param xGridColor:x??????????????????
     * @param hasYAxis:?????????y???
     * @param yAxixColor:y?????????
     * @param isTouchEnable:??????????????????????????????
     * @param XYTextColorId:xy???????????????
     * @param XAxisLineColorId:x?????????
     * @param yGridLineColorId:y??????????????????
     * @param highTextColor:?????????????????????0??????????????????????????????????????????????????????
     * @param showXYName:??????????????????Xy??????
     * @param xTextId:??????X??????id
     * @param yTextId:??????Y??????id
     */
    public static void initLineChart(Context context, LineChart lineChart, int format, final String unit, boolean hasXGrid, int xGridColor, boolean hasYAxis, int yAxixColor, boolean isTouchEnable, int XYTextColorId, int XAxisLineColorId, int yGridLineColorId, int highTextColor, boolean showXYName, int xTextId, int yTextId, OnEmptyListener listener) {
        //????????????
        int mXYTextColorId = ContextCompat.getColor(context,XYTextColorId);
        int mXAxisLineColorId = ContextCompat.getColor(context,XAxisLineColorId);
        int myGridLineColorId = ContextCompat.getColor(context,yGridLineColorId);
        int myXGridColor = ContextCompat.getColor(context,xGridColor);
        int myYAxixColor = ContextCompat.getColor(context,yAxixColor);
        lineChart.setDrawGridBackground(false);
        // no description text
        lineChart.getDescription().setEnabled(false);
        // enable touch gestures
        lineChart.setTouchEnabled(isTouchEnable);
        // enable scaling and dragging
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setScaleXEnabled(true);
        lineChart.setScaleYEnabled(false);
        lineChart.getViewPortHandler().setMaximumScaleX(8f);
//        lineChart.setVisibleXRangeMaximum(2f);
        lineChart.setPinchZoom(true);//??????mark???
        if (isTouchEnable){
            if (highTextColor == 0){
                MaxMarkerView mv = new MaxMarkerView(context, R.layout.custom_marker_view);
                mv.setChartView(lineChart); // For bounds control
                lineChart.setMarker(mv); // Set the marker to the chart
            }else {
                if (showXYName){
                    MaxMarkerView mv = new MaxMarkerView(context, R.layout.custom_marker_view,highTextColor,showXYName,xTextId,yTextId);
                    mv.setChartView(lineChart); // For bounds control
                    lineChart.setMarker(mv); // Set the marker to the chart
                }else {
                    MaxMarkerView mv = new MaxMarkerView(context, R.layout.custom_marker_view,highTextColor,listener);
                    mv.setChartView(lineChart); // For bounds control
                    lineChart.setMarker(mv); // Set the marker to the chart
                }
            }
        }
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // ??????X????????????
        xAxis.setAxisMinimum(0f);
        xAxis.setEnabled(true);//???????????????????????? ??????????????????????????????????????????
        xAxis.setDrawAxisLine(true);//??????????????????
        xAxis.setDrawGridLines(hasXGrid);//??????x???????????????????????????
        xAxis.setGridColor(myXGridColor);
        xAxis.setDrawLabels(true);
        xAxis.setAxisLineWidth(1.0f);
        xAxis.setGridLineWidth(0.5f);
        xAxis.setTextColor(mXYTextColorId);//??????x???????????????
//        xAxis.setGridColor(colorId);
        xAxis.setAxisLineColor(mXAxisLineColorId);
//		xAxis.enableAxisLineDashedLine(10f,0f,0f);
//        xAxis.setValueFormatter(new IAxisValueFormatter() {
//            @Override
//            public String getFormattedValue(float value, AxisBase axis) {
////				return String.format("%.2f", value).replace",":");
//                return sdf_hm.format(new Date((long) (value * minTamp)));
//            }
//        });
        YAxis leftAxis = lineChart.getAxisLeft();
        if (format==1){
            leftAxis.setValueFormatter(new PercentFormatter());
            leftAxis.setAxisMaximum(100);
        }else if (format == 0){
            leftAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return (float) (Math.round(value * 10)) / 10 + unit;
                }
            });

        }
        leftAxis.setTextColor(mXYTextColorId);
//		leftAxis.enableGridDashedLine(10f,10f,0f);//??????
        leftAxis.setDrawAxisLine(hasYAxis);
        leftAxis.setAxisLineColor(myYAxixColor);
//        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisLineWidth(1.0f);
        leftAxis.setGridLineWidth(0.5f);
        leftAxis.setGridColor(myGridLineColorId);
//		leftAxis.setAxisLineColor(colorId);
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);
////        xAxis.setTypeface(mTf); // ????????????
//        xAxis.setEnabled(false);
//        // ??????????????????????????????false,????????????????????????????????????true???????????????AxisLine
//        xAxis.setDrawAxisLine(true);
//
//        // ??????xAxis.setEnabled(false);??????????????????Grid?????????"?????????"??????X????????????
//        xAxis.setDrawGridLines(true); // ???????????????
        lineChart.animateX(2000);
        Legend mLegend = lineChart.getLegend(); // ??????????????????????? ?????????
        mLegend.setEnabled(false);
    }



    /**Android???????????????*/
    /**
     * ??????scrollview?????????
     * **/
    public static Bitmap getScrollViewBitmap(ScrollView scrollView, String picpath) {
        int h = 0;
        Bitmap bitmap;
        // ??????listView????????????
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
        }
        // ?????????????????????bitmap
        bitmap = Bitmap.createBitmap(scrollView.getWidth(), h,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
        // ????????????
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(picpath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (null != out) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            }
        } catch (IOException e) {
        }
        return bitmap;
    }


    /**
     *  ??????listview
     * **/
    public static Bitmap getListViewBitmap(ListView listView, String picpath) {
        int h = 0;
        Bitmap bitmap;
        // ??????listView????????????
        for (int i = 0; i < listView.getChildCount(); i++) {
            h += listView.getChildAt(i).getHeight();
        }
        // ?????????????????????bitmap
        bitmap = Bitmap.createBitmap(listView.getWidth(), h,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        listView.draw(canvas);
        // ????????????
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(picpath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (null != out) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            }
        } catch (IOException e) {
        }
        return bitmap;
    }
    // ???????????? ??????ScrollView
    public static void shootScrollView(ScrollView scrollView, String picpath) {
        savePic(getScrollViewBitmap(scrollView, picpath), picpath);
    }

    // ???????????? ??????ListView
    public static void shootListView(ListView listView, String picpath) {
        savePic(getListViewBitmap(listView,picpath), picpath);
    }
    // ?????????sdcard
    public static void savePic(Bitmap b, String strFileName) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(strFileName);
            if (null != fos) {
                b.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String getDiskCacheDir(Context context) {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }

    public static float getMaxY(LineChart chart){
        float max = 0f;
        if (chart != null){
            List<ILineDataSet> dataSets = chart.getData().getDataSets();
            for (int i =0;i<dataSets.size();i++){
                float y = dataSets.get(i).getYMax();
                if (y>max){
                    max = y;
                }
            }
        }
        return max;
    }

    public static String saveList(List<ArrayList<Entry>> list){
        if (null == list && list.size() == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<list.size();i++){
            ArrayList<Entry> entries = list.get(i);
            Log.i("????????????:" + list.size() + ";????????????" + entries.size());
            for (int j=0,size = entries.size();j<size;j++){
                Entry entry = entries.get(j);
                sb.append(entry.getX())
                        .append(":")
                        .append(entry.getY())
                        .append(",");
            }
            sb.deleteCharAt(sb.lastIndexOf(","));
            sb.append("_");
        }
        sb.deleteCharAt(sb.lastIndexOf("_"));
        return String.valueOf(sb);
    }
    public static String saveBarList(List<List<BarEntry>> list){
        if (null == list && list.size() == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<list.size();i++){
            List<BarEntry> entries = list.get(i);
            for (int j=0,size = entries.size();j<size;j++){
                BarEntry entry = entries.get(j);
                sb.append(entry.getX())
                        .append(":")
                        .append(entry.getY())
                        .append(",");
            }
            sb.deleteCharAt(sb.lastIndexOf(","));
            sb.append("_");
        }
        sb.deleteCharAt(sb.lastIndexOf("_"));
        return String.valueOf(sb);
    }

    public static List<ArrayList<Entry>> getEntryList(String value){
        if (TextUtils.isEmpty(value)) return null;
        List<ArrayList<Entry>> list = new ArrayList<>();
        String[] entriesStrs = value.split("_");
        try {
            for (String entriesStr : entriesStrs){
                ArrayList<Entry> entries = new ArrayList<>();
                String[] entryStrs = entriesStr.split(",");
                for (String entryStr : entryStrs){
                    String[] split = entryStr.split(":");
                    Entry entry = new Entry(Float.parseFloat(split[0]), Float.parseFloat(split[1]));
                    entries.add(entry);
                }
                list.add(entries);
            }
            Log.i("????????????:" + list.size() + ";????????????" + list.get(0).size());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return list;
    }
    public static List<List<BarEntry>> getBarEntryList(String value){
        if (TextUtils.isEmpty(value)) return null;
        List<List<BarEntry>> list = new ArrayList<>();
        String[] entriesStrs = value.split("_");
        try {
            for (String entriesStr : entriesStrs){
                List<BarEntry> entries = new ArrayList<>();
                String[] entryStrs = entriesStr.split(",");
                for (String entryStr : entryStrs){
                    String[] split = entryStr.split(":");
                    BarEntry entry = new BarEntry(Float.parseFloat(split[0]), Float.parseFloat(split[1]));
                    entries.add(entry);
                }
                list.add(entries);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return list;
    }
    public static List<MaxCheckOneKeyRSTBean> getRSTBean (String value){
        if (TextUtils.isEmpty(value)) return null;
        return new Gson().fromJson(value,
                new TypeToken<List<MaxCheckOneKeyRSTBean>>() {
                }.getType());
    }
    public static String saveRSTBean (List<MaxCheckOneKeyRSTBean> list){
        if (null == list && list.size() == 0) return "";
        return new Gson().toJson(list);
    }
    public static MaxCheckOneKeyISOBean getISOBean (String value){
        if (TextUtils.isEmpty(value)) return null;
        return new Gson().fromJson(value,
                new TypeToken<MaxCheckOneKeyISOBean>() {
                }.getType());
    }
    public static String saveISOBean (MaxCheckOneKeyISOBean isoBean){
        if (null == isoBean ) return "";
        return new Gson().toJson(isoBean);
    }



    public static void initBarChart(Context context, BarChart mChart, final String unit, boolean isTouchEnable, int XYTextColorId, int XAxisLineColorId, int yGridLineColorId, boolean hasYAxis, int yAxisColor, boolean hasXGrid, int xGridColor, int heighLightColor, OnEmptyListener listener) {
        //????????????
        int mXYTextColorId = ContextCompat.getColor(context,XYTextColorId);
        int mXAxisLineColorId = ContextCompat.getColor(context,XAxisLineColorId);
        int myGridLineColorId = ContextCompat.getColor(context,yGridLineColorId);
        int myYAxisColor = ContextCompat.getColor(context,yAxisColor);
        int myXGridColor = ContextCompat.getColor(context,xGridColor);
        mChart.getDescription().setEnabled(false);
        mChart.setTouchEnabled(isTouchEnable);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);
        mChart.setPinchZoom(false);
        mChart.setDrawBarShadow(false);
        mChart.setDrawGridBackground(false);
        if (isTouchEnable){
            MaxMarkerView mv = new MaxMarkerView(context, R.layout.custom_marker_view,heighLightColor,false,listener);
            mv.setChartView(mChart); // For bounds control
            mChart.setMarker(mv); // Set the marker to the chart
        }
        mChart.animateY(1500);
        Legend l = mChart.getLegend();
        l.setEnabled(false);
        XAxis xAxis = mChart.getXAxis();
        xAxis.setDrawGridLines(hasXGrid);
        xAxis.setGridColor(myXGridColor);
        xAxis.setAxisLineWidth(1.0f);
        xAxis.setGridLineWidth(0.5f);
        xAxis.setGranularity(1f);
//		xAxis.setAxisMinimum(1f);
        xAxis.setLabelCount(9);
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // ??????X????????????
        xAxis.setTextColor(mXYTextColorId);//??????x???????????????
        xAxis.setAxisLineColor(mXAxisLineColorId);
        xAxis.setDrawLabels(true);

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                String day = ((((int) value)-1) * 2 +1) +"";
                LogUtil.i("day-->"+day);
                return day;
            }
        });


        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setAxisMinimum(0.0f);//??????0?????????????????????

        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (float)(Math.round(value*10))/10 + unit;
            }
        });

        leftAxis.setDrawAxisLine(hasYAxis);
        leftAxis.setAxisLineColor(myYAxisColor);
        leftAxis.setAxisLineWidth(1.0f);
        leftAxis.setGridLineWidth(0.5f);
//		leftAxis.enableGridDashedLine(10f,10f,0f);
        leftAxis.setTextColor(mXYTextColorId);
        leftAxis.setGridColor(myGridLineColorId);
        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
    }


    public static void setBarChartData(Context context, BarChart mChart, List<List<BarEntry>> barYList, int[] colors, int[] colorHights, int count) {
        if (mChart == null || barYList == null) return;
        List<IBarDataSet> barSetDatas = new ArrayList<>();
        BarData barData = mChart.getBarData();
        //?????????????????????
//        float minX = barYList.get(0).get(0).getX();
        if (barData != null && barData.getDataSetCount() >= count){
            for ( int i = 0;i<count;i++){
                BarDataSet dataSet = (BarDataSet) barData.getDataSetByIndex(i);
                dataSet.setValues(barYList.get(i));
            }
//			mChart.getXAxis().setAxisMaximum(minX);
            mChart.getXAxis().setAxisMaximum(barStart + mChart.getBarData().getGroupWidth(groupSpace, barSpace) * 9);
            mChart.groupBars(barStart,groupSpace,barSpace);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        }else {
            for (int i=0;i<count;i++){
                BarDataSet barSet = new BarDataSet(barYList.get(i),"");
                barSet.setColor(ContextCompat.getColor(context,colors[i]));
                if (colorHights[i] != -1) {
                    barSet.setHighLightColor(ContextCompat.getColor(context, colorHights[i]));
                }
                barSet.setDrawValues(false);
//                barSet.setValueTextColor(colorId);
                barSetDatas.add(barSet);
            }
            BarData data = new BarData(barSetDatas);
            mChart.setData(data);
            mChart.setFitBars(true);
            mChart.getBarData().setBarWidth(barWidth);
            mChart.getXAxis().setAxisMinimum(barStart);
            mChart.getXAxis().setAxisMaximum(barStart + mChart.getBarData().getGroupWidth(groupSpace, barSpace) * 9);
            mChart.groupBars(barStart,groupSpace,barSpace);
//			mChart.getXAxis().setAxisMinimum(minX);
        }
        mChart.animateY(3000);
        mChart.invalidate();
    }

}
