package com.growatt.shinetools.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

import com.growatt.shinetools.R;
import com.growatt.shinetools.listeners.OnCirclerDialogListener;
import com.mylhyl.circledialog.CircleDialog;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by dg on 2017/6/14.
 */

public class OssUtils {







    public static void circlerDialog(FragmentActivity act, String text, int result) {
        circlerDialog(act, text, result, true);
    }

    public static void circlerDialog(FragmentActivity act, String text, int result, boolean isFinish) {
        circlerDialog(act, text, result, isFinish, null);
    }

    public static void circlerDialog(FragmentActivity act, String text, int result, OnCirclerDialogListener circlerDialogListener) {
        circlerDialog(act, text, result, false, circlerDialogListener);
    }

    public static void circlerDialog(final FragmentActivity act, String text, int result, final boolean isFinish, final OnCirclerDialogListener circlerDialogListener) {
//        if (act.isFinishing() || act.isDestroyed()) return;
        try {
            new CircleDialog.Builder()
                    .setCancelable(false)
                    .setWidth(0.7f)
                    .setTitle(act.getString(R.string.温馨提示) + (result == -1 ? "" : ("(" + result + ")")))
                    .setText(text)
                    .setPositive(act.getString(R.string.all_ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isFinish) {
                                act.finish();
                            } else {
                                if (circlerDialogListener != null) {
                                    circlerDialogListener.onCirclerPositive();
                                }
                            }
                        }
                    })
                    .show(act.getSupportFragmentManager());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void circlerDialog(final FragmentActivity act, boolean isCancel, String text, int result, final boolean isFinish, final OnCirclerDialogListener circlerDialogListener) {
        try {
            new CircleDialog.Builder()
                    .setCancelable(isCancel)
                    .setWidth(0.7f)
                    .setTitle(act.getString(R.string.温馨提示) + (result == -1 ? "" : ("(" + result + ")")))
                    .setText(text)
                    .setPositive(act.getString(R.string.all_ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isFinish) {
                                act.finish();
                            } else {
                                if (circlerDialogListener != null) {
                                    circlerDialogListener.onCirclerPositive();
                                }
                            }
                        }
                    })
                    .show(act.getSupportFragmentManager());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void circlerDialogStr(final FragmentActivity act, String text, String result, final boolean isFinish, final OnCirclerDialogListener circlerDialogListener) {
//        if (act.isFinishing() || act.isDestroyed()) return;
        try {
            new CircleDialog.Builder()
                    .setCancelable(false)
                    .setWidth(0.7f)
                    .setTitle(act.getString(R.string.温馨提示) + (TextUtils.isEmpty(result) ? "" : ("(" + result + ")")))
                    .setText(text)
                    .setPositive(act.getString(R.string.all_ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isFinish) {
                                act.finish();
                            } else {
                                if (circlerDialogListener != null) {
                                    circlerDialogListener.onCirclerPositive();
                                }
                            }
                        }
                    })
                    .show(act.getSupportFragmentManager());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static boolean checkGpsIsOpen(Context context) {
        boolean isOpen;
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        isOpen = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isOpen;
    }

    private static boolean registerPermission(Activity context) {
        String[] locationPermission = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !EasyPermissions.hasPermissions(context, locationPermission)) {
            EasyPermissions.requestPermissions(context, String.format("%s:%s",context.getString(R.string.m权限获取某权限说明), context.getString(R.string.位置)), 11005, locationPermission);
        } else {
            return true;
        }
        return false;
    }




    /**
     * 根据区域id获取区域名
     *
     * @param groupId
     * @return
     */
    public static String getGroupById(Context context, int groupId) {
        String groupName = "";
        switch (groupId) {
            case 1:
                groupName = "中国";
                break;
            case 2:
                groupName = "欧洲";
                break;
            case 3:
                groupName = "亚洲";
                break;
            case 4:
                groupName = "泰国";
                break;
            case 5:
                groupName = "美洲";
                break;
            case 6:
                groupName = "非洲";
                break;
            case 7:
                groupName = "澳洲";
                break;
            case 8:
                groupName = "英国";
                break;
            case 9:
                groupName = "其他";
                break;
        }
        return groupName;
    }







    /**
     * 数组转换 arr
     *
     * @return
     */
    public static int[] stringToInt(String[] arrs) {
        int[] ints = new int[arrs.length];
        for (int i = 0; i < arrs.length; i++) {
            ints[i] = Integer.parseInt(arrs[i]);
        }
        return ints;
    }

    /**
     * 数组转换 arr
     *
     * @return
     */
    public static String[] intToString(int[] arrs) {
        String[] strs = new String[arrs.length];
        for (int i = 0; i < arrs.length; i++) {
            strs[i] = String.valueOf(arrs[i]);
        }
        return strs;
    }


}
