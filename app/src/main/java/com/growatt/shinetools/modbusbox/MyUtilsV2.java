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
