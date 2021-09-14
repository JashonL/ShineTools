package com.growatt.shinetools.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.growatt.shinetools.R;
import com.growatt.shinetools.ShineToosApplication;
import com.growatt.shinetools.okhttp.OkHttpUtils;
import com.growatt.shinetools.okhttp.callback.Callback;


import java.util.Map;


public class InternetUtils {


    /**
     * get请求
     * @param context  上下文
     * @param url      地址
     * @param params   参数
     * @param callback 请求回调
     */

    public static void asynGet(Context context, String url, Map<String, String> params, Callback callback) {
        if (!isNetworkAvailable(context)) {
            MyToastUtils.toast(R.string.android_key2997);
            return;
        }
        OkHttpUtils
                .get()
                .url(url)
                .tag(context)
                .params(params)
                .build()
                .execute(callback);
    }

    /**
     * post请求
     * @param context  上下文
     * @param url      地址
     * @param params   参数
     * @param callback 请求回调
     */
    public static void asynPost(Context context, String url, Map<String, String> params, Callback callback) {
        if (!isNetworkAvailable(context)) {
            MyToastUtils.toast(R.string.android_key2997);
            return;
        }
        OkHttpUtils
                .post()
                .url(url)
                .tag(context)
                .params(params)
                .build()
                .execute(callback);
    }






    private static ConnectivityManager connectivityManager;


    /**
     * 检查当前网络是否可用
     *
     * @return
     */

    public static boolean isNetworkAvailable(Context context) {
        if (context == null) {
            context = ShineToosApplication.getContext();
        }
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        if (connectivityManager == null) {
            connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }

        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {

                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }



}
