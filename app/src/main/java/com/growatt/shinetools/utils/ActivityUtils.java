package com.growatt.shinetools.utils;

import android.app.Activity;
import android.app.LauncherActivity;
import android.content.Intent;
import android.provider.Settings;




public class ActivityUtils {


    public static void gotoActivity(Activity from, Class<? extends Activity> clazz, boolean finished) {
        if (clazz == null) return;
        Intent intent = new Intent();
        intent.setClass(from, clazz);
        startActivity(from, intent, finished);
    }





    public static void startActivity(Activity activity, Intent intent, boolean finishLastActivity) {
        if (activity == null) return;
        activity.startActivity(intent);
        if (finishLastActivity) activity.finish();
    }

    public static void startActivityForResult(Activity activity, Intent intent, int backCode, boolean finishLastActivity) {
        if (activity == null) return;
        activity.startActivityForResult(intent, backCode);
        if (finishLastActivity) activity.finish();
    }

    public static void back(Activity activity) {
        activity.finish();
    }

    public static void back(Activity activity, int direction) {
        activity.finish();
    }



    /**
     * 提示是否调整到wifi界面
     */
    public static void toWifiSet(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        activity.startActivity(intent);
    }


}
