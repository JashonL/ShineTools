package com.growatt.shinetools.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.fragment.app.FragmentActivity;

import com.growatt.shinetools.R;
import com.growatt.shinetools.ShineToosApplication;
import com.growatt.shinetools.bean.WifiList;
import com.mylhyl.circledialog.CircleDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommenUtils {

    //隐藏虚拟键盘
    public static void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive()) {
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        }
    }



    //获得今天的日期
    public enum DataType {
        YMD(1), YMD_HSM(2), HSM(3);

        DataType(int i) {
        }
    }


    public static String getNowData(DataType type) {
        String pattern = "yyyyMMdd";
        switch (type) {
            case YMD:
                pattern = "yyyyMMdd";
                break;
            case HSM:
                pattern = "hh:mm:ss";
                break;
            case YMD_HSM:
                pattern = "yyyyMMdd hh:mm:ss";
                break;
        }
        return new SimpleDateFormat(pattern).format(new Date());
    }


    /**
     * byte数组转为String
     */
    public static String bytesToHexString(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte aByte : bytes) {
            String hexString = Integer.toHexString(aByte & 0xFF);
            if (hexString.length() == 1) {
                hexString = '0' + hexString;
            }
            result.append(hexString.toUpperCase());
        }
        return result.toString();
    }


    /**
     * 判断是否为合法IP * @return the ip
     */
    public static boolean isboolIp(String ipAddress) {
        if (TextUtils.isEmpty(ipAddress))
            return false;
        Pattern pattern = Pattern.compile("^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$");
        Matcher matcher = pattern.matcher(ipAddress);
        return matcher.find();
    }


    public static String ByteToString(byte[] bytes) {

        if (bytes == null) return "";

        StringBuilder strBuilder = new StringBuilder();
        for (byte aByte : bytes) {
            if (aByte != 0) {
                strBuilder.append((char) aByte);
            } else {
                break;
            }

        }
        return strBuilder.toString();
    }


    //判断WiFi是否打开
    public static boolean isWiFi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Network activeNetwork = connectivityManager.getActiveNetwork();
            if (null == activeNetwork) {
                return false;
            }
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
            if (null == capabilities) {
                return false;
            }

            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true;
            }

        } else {
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            assert networkInfo != null;
            return networkInfo.isConnected();
        }
        return true;
    }


    public static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info= connectivityManager.getActiveNetworkInfo();
        if (info!= null
                && info.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }


    /**
     * 获取SSID
     *
     * @param activity 上下文
     * @return WIFI 的SSID
     */
    public static String getWifiSsid(Activity activity) {
        String ssid = "";
        ConnectivityManager manager = (ConnectivityManager) activity.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert manager != null;
        NetworkInfo.State wifi = Objects.requireNonNull(manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)).getState();
        if (wifi == null) {
            return null;
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
            WifiManager mWifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            assert mWifiManager != null;
            WifiInfo info = mWifiManager.getConnectionInfo();
            return info.getSSID().replace("\"", "");
        } else {
            WifiManager mWifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (mWifiManager != null) {
                WifiInfo connectionInfo = mWifiManager.getConnectionInfo();
                int networkId = connectionInfo.getNetworkId();
                ssid = connectionInfo.getSSID();
                if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    List<WifiConfiguration> configuredNetworks = mWifiManager.getConfiguredNetworks();
                    for (WifiConfiguration wificonf : configuredNetworks) {
                        if (wificonf.networkId == networkId) {
                            ssid = wificonf.SSID;
                            break;
                        }
                    }
                }
                if (TextUtils.isEmpty(ssid)) return ssid;
                if (ssid.contains("\"")) {
                    ssid = ssid.replace("\"", "");
                }
                if (ssid.toLowerCase().contains("unknown ssid")) {
                    ssid = "";
                }
                Log.d("ssid=" + ssid);
                return ssid;
            }
        }
        return ssid;
    }


    /**
     * 提示是否调整到wifi界面
     */
    public static void showJumpWifiSet(final FragmentActivity act) {
        showJumpWifiSet(act, act.getString(R.string.android_key809));
    }

    /**
     * 提示是否调整到wifi界面
     */
    public static void showJumpWifiSet(final FragmentActivity act, String str) {
        DialogUtils.getInstance().closeLoadingDialog();
        new CircleDialog.Builder()
                .setWidth(0.7f)
                .setTitle(act.getString(R.string.android_key2263))
                .setText(str)
                .setNegative(act.getString(R.string.android_key1806), null)
                .setPositive(act.getString(R.string.android_key641), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                        act.startActivityForResult(intent, WifiList.FIRSTACT_TO_WIFI);
                    }
                })
                .show(act.getSupportFragmentManager());
    }



    public interface ITcpDisConnectListener{
        void tryConnect();
    }

    /**
     * 提示wifi断开连接是否重连
     */
    public static void showTcpDisConnet(final FragmentActivity act, String str,ITcpDisConnectListener listener) {
        DialogUtils.getInstance().closeLoadingDialog();
        new CircleDialog.Builder()
                .setWidth(0.7f)
                .setTitle(act.getString(R.string.android_key2263))
                .setText(str)
                .setNegative(act.getString(R.string.android_key1806), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                })
                .setPositive(act.getString(R.string.重新连接), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.tryConnect();
                    }
                })
                .show(act.getSupportFragmentManager());
    }



    /**
     * 获取新语言
     *
     * @return
     */
    public static int getLanguageNew1() {
        int lan = 1;
        Locale locale = ShineToosApplication.getContext().getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.toLowerCase().contains("zh")) {
            language = "zh_cn";
            lan = 0;
            if (!locale.getCountry().toLowerCase().equals("cn")) {
                lan = 14;
            }
        }
        if (language.toLowerCase().contains("en")) {
            language = "en";
            lan = 1;
        }
        if (language.toLowerCase().contains("fr")) {
            language = "fr";
            lan = 2;
        }
        if (language.toLowerCase().contains("ja")) {
            language = "ja";
            lan = 3;
        }
        if (language.toLowerCase().contains("it")) {
            language = "it";
            lan = 4;
        }
        if (language.toLowerCase().contains("ho")) {
            language = "ho";
            lan = 5;
        }
        if (language.toLowerCase().contains("tk")) {
            language = "tk";
            lan = 6;
        }
        if (language.toLowerCase().contains("pl")) {
            language = "pl";
            lan = 7;
        }
        if (language.toLowerCase().contains("gk")) {
            language = "gk";
            lan = 8;
        }
        if (language.toLowerCase().contains("gm")) {
            language = "gm";
            lan = 9;
        }
        if (language.toLowerCase().contains("pt")) {
            language = "pt";
            lan = 10;
        }
        if (language.toLowerCase().contains("sp")) {
            language = "sp";
            lan = 11;
        }
        if (language.toLowerCase().contains("vn")) {
            language = "vn";
            lan = 12;
        }
        if (language.toLowerCase().contains("hu")) {
            language = "hu";
            lan = 13;
        }
        return lan;
    }



    /**
     * 隐藏所有的view
     *
     * @param views
     */
    public static void showAllView(View... views) {
        for (View view : views) {
            if (view != null && view.getVisibility() != View.VISIBLE) {
                view.setVisibility(View.VISIBLE);
            }
        }
    }


    /**
     * 隐藏所有的view
     *
     * @param visibity VISIBLE, INVISIBLE, or GONE
     * @param views
     */
    public static void hideAllView(int visibity, View... views) {
        for (View view : views) {
            if (view != null && view.getVisibility() == View.VISIBLE) {
                view.setVisibility(visibity);
            }
        }
    }

    public static int getScreenWidth(Context context) {
        return getScreenDisplayMetrics(context).widthPixels;
    }


    public static DisplayMetrics getScreenDisplayMetrics(Context context) {
//		DisplayMetrics dm = new DisplayMetrics();
//		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return context.getResources().getDisplayMetrics();
    }

    /**
     * EditText获取焦点并显示软键盘
     */
    public static void showSoftInputFromWindow(Activity activity, EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }


    /**
     * 获取时分双位数
     */

     public static String getDoubleNum(int value){
         String s=String.format("%02d",value);
         return s;
     }

}
