package com.growatt.shinetools.modbusbox;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.ViewConfiguration;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;


import com.growatt.shinetools.R;

import java.util.Locale;

/**
 * Created：2017/11/28 on 10:28
 * Author:gaideng on dg
 * Description:
 */

public class MyUtilsV2 {
    /**
     * 根据服务器url获取标示id
     * @param url
     * @return
     */
    public static int getUrlIdByUrl(String url){
        int id = 1;
        if ("server.growatt.com".equals(url)){
            id = 1;
        }
        else if ("server-cn.growatt.com".equals(url)){
            id = 2;
        }
        else if ("server.smten.com".equals(url)){
            id = 3;
        }
        return id;
    }
    public static String getLanguage(Context context){
        Locale locale = context.getResources().getConfiguration().locale;
        String language=locale.getLanguage().toLowerCase();
        return language;
    }

    /**
     *根据和风天气状态码获取天气图标id
     * @param condCode
     * @return
     */
    public static int getWeatherIconByCondCode(String condCode){
        if (TextUtils.isEmpty(condCode)) condCode = "999";
        int iconId = 0;
        switch (condCode){
            case "100":
                iconId = R.drawable.ov_weather_100_forecast;break;//100
            case "101":case "102":case "104":
                iconId = R.drawable.ov_weather_101_forecast;break;//101
            case "103":
                iconId = R.drawable.ov_weather_103_forecast;break;//103
            case "203":case "204":
                iconId = R.drawable.ov_weather_200_forecast;break;//200
            case "200":case "201":case "202":
                iconId = R.drawable.ov_weather_201_forecast;break;//201
            case "205":case "206":case "207":
                iconId = R.drawable.ov_weather_205_forecast;break;//205
            case "208":case "209":case "210":case "211":case "212":case "213":
                iconId = R.drawable.ov_weather_208_forecast;break;//208
            case "300":case "301":
                iconId = R.drawable.ov_weather_300_forecast;break;//300
            case "302":case "303":case "304":
                iconId = R.drawable.ov_weather_302_forecast;break;//302
            case "305":case "309":
                iconId = R.drawable.ov_weather_305_forecast;break;//305
            case "306":case "307":case "308":case "314":case "315":case "399":
                iconId = R.drawable.ov_weather_306_forecast;break;//306
            case "310":case "311":case "312":case "316":case "317":case "318":
                iconId = R.drawable.ov_weather_310_forecast;break;//310
            case "313":
                iconId = R.drawable.ov_weather_313_forecast;break;//313
            case "400": case "407":
                iconId = R.drawable.ov_weather_400_forecast;break;//400
            case "401": case "408": case "499":
                iconId = R.drawable.ov_weather_401_forecast;break;//401
            case "402": case "403": case "409": case "410":
                iconId = R.drawable.ov_weather_402_forecast;break;//402
            case "404": case "405": case "406":
                iconId = R.drawable.ov_weather_404_forecast;break;//404
            case "500":
                iconId = R.drawable.ov_weather_500_forecast;break;//500
            case "501":case "509":case "510":case "514":case "515":
                iconId = R.drawable.ov_weather_501_forecast;break;//501
            case "502":case "511":case "512":case "513":
                iconId = R.drawable.ov_weather_502_forecast;break;//502
            case "503":case "504":
                iconId = R.drawable.ov_weather_503_forecast;break;//503
            case "508":case "507":
                iconId = R.drawable.ov_weather_508_forecast;break;//508
            case "900":
                iconId = R.drawable.ov_weather_900_forecast;break;//900
            case "901":
                iconId = R.drawable.ov_weather_901_forecast;break;//901
            case "999":default:
                iconId = R.drawable.ov_weather_999_forecast;break;//999
        }
        return iconId;
    }
    /**
     */
//    public static void showPermissionsDenied(Context context, int requestCode, List<String> perms) {
//        switch (requestCode){
//            case PermissionCodeUtil.PERMISSION_EXTERNAL_STORAGE_CODE:
//                onPermissionsDenied(context,requestCode,perms,context.getString(R.string.m存储));
//                break;
//            case PermissionCodeUtil.PERMISSION_CAMERA_CODE:
//                onPermissionsDenied(context,requestCode,perms,context.getString(R.string.m相机));
//                break;
//            case PermissionCodeUtil.PERMISSION_CAMERA_ONE_CODE:
//                onPermissionsDenied(context,requestCode,perms,context.getString(R.string.m相机单));
//                break;
//            case PermissionCodeUtil.PERMISSION_LOCATION_CODE:
//                onPermissionsDenied(context,requestCode,perms,context.getString(R.string.m位置权限));
//                break;
//        }
//    }
//
//    public static void onPermissionsDenied(Object context,int requestCode, List<String> perms,String permission) {
////        if (context instanceof BaseFragment){
////
////        }
////        if (EasyPermissions.somePermissionPermanentlyDenied(context, perms)) {
////            if (MyUtils.getLanguage(context) == 0) {
////                new AppSettingsDialog
////                        .Builder(this)
////                        .setTitle(R.string.m权限请求)
////                        .setRationale(String.format(context.getString(R.string.m权限请求步骤), permission, permission))
////                        .setRequestCode(requestCode)
////                        .setPositiveButton(R.string.all_ok)
////                        .setNegativeButton(R.string.all_no)
////                        .build()
////                        .show();
////            }else {
////                new AppSettingsDialog.Builder(this).setRequestCode(requestCode).build().show();
////            }
////        }
//    }
//    public void onPermissionsDenied(Context context,int requestCode, List<String> perms,int permissionResId) {
//        onPermissionsDenied(context,requestCode,perms,context.getString(permissionResId));
//    }


    /**
     * 设置字体颜色
     */
    public static void setTextColor(Context context, @ColorRes int resId, @NonNull TextView... tvs){
        for (TextView tv : tvs) {
            tv.setTextColor(ContextCompat.getColor(context,resId));
        }
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    public static void hideBottomUIMenu(Activity context){
//        try {
//            View decorView = activity.getWindow().getDecorView();
// ;
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
//                int  uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
////                    |
////                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE
//                        |View.SYSTEM_UI_FLAG_FULLSCREEN
//                        ;
//                decorView.setSystemUiVisibility(uiOptions);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
    /**
     * 检查是否存在虚拟按键栏
     * @param context
     * @return
     */
    public static boolean hasNavBar(Activity context) {
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("config_showNavigationBar", "bool", "android");
        if (resourceId != 0) {
            boolean hasNav = res.getBoolean(resourceId);
            return hasNav;
        } else { // fallback
            return !ViewConfiguration.get(context).hasPermanentMenuKey();
        }
    }
    /**
     * 获取mix本地调试告警码 1001-1008
     */
//    private static String[][] warnStrs = {
//            {"","","","","","","","","","","","","AC V Outrange","","","AC F Outrange"},
//            {"","","AC V Outrange","AC V Outrange","","","","","","","Output High DCI","","","","","No AC Connection"},
//            {"AC F Outrange","AC F Outrange","","","","","","","EPS Volt Low","","","","","","",""},
//            {"Battery reversed","Battery Open","Bat Voltage Low","","","","Bat Voltage High","","BMS Error:XXX","BMS COM Fault","CT LN Reversed","PairingTimeOut","Warning 401","","BAT NTC Open",""},
//            {"","","","","","","","","","","","","","","",""},
//            {"Error 303","PV Isolation Low","Error 405","Error:103","Error:105","Error 418","Error 411","Error 411","OP Short Fault","Error 407","Error 406","NTC Open","BusUnbalance","Error:408","",""},
//            {"PV Voltage High","PV Voltage High","Error:408","Error:408","","","Residual I High","","","","Warn:104","Warning 203","Warning 203","","",""},
//            {"","","","BAT NTC Open","","","Warning506","Over Load","","","BMS Warning:XXX","","BatLowPower","","",""},
//    };
    private static String[][] warnStrs = {
            {"","","","","","","","","","","","","AC V Outrange","","","AC F Outrange"},
            {"","","AC V Outrange","AC V Outrange","","","","","","","","","","","","No AC Connection"},
            {"AC F Outrange","AC F Outrange","","","","","","","EPS Volt Low","","","","","","",""},
            {"Battery reversed","Battery Open","Bat Voltage Low","","","","Bat Voltage High","","BMS Error:XXX","BMS COM Fault","CT LN Reversed","PairingTimeOut","Warning 401","","BAT NTC Open",""},
            {"","","","","","","","","","","","","","","",""},
            {"Output High DCI","PV Isolation Low","NTC Open","Error:103","PV Voltage High","Error 408","Error 408","Error 408","OP Short Fault","Error 407","Error 406","Error 405","BusUnbalance","Error:418","Warning 203","Warning 203"},
            {"Error 303","","Error 411","Error 411","","","Residual I High","","","","","","","","",""},
            {"","","","","","","Warning506","Over Load","","","BMS Warning:XXX","","BatLowPower","","",""},
    };
    public static String getMixToolWarn(byte[] bs) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            long value = MaxWifiParseUtil.obtainValueOne(bs, i + 1001);
            for (int j = 0; j < 16; j++) {
                if ((value & Math.round(Math.pow(2,j))) > 0){
                    if (!TextUtils.isEmpty(warnStrs[i][j])){
                        sb.append(warnStrs[i][j]).append("+");
                    }
                }
            }
        }
        if (sb.length() > 0){
            sb.deleteCharAt(sb.length()-1);
        }else {
            sb.append("--");
        }
        return sb.toString();
    }

}
