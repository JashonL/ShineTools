package com.growatt.shinetools.utils;

import android.content.Context;
import android.text.TextUtils;

import com.growatt.shinetools.db.SqliteUtil;
import com.growatt.shinetools.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

public class ShineToolsApi {

    public static String BASE_URL = "https://oss.growatt.com";
    public static String SERVER_BASE_URL = "https://server-cn-api.growatt.com";

//    public static String FILE_DOWNLOAD_BASE_URL ="https://server-api.growatt.com";
    public static String FILE_DOWNLOAD_BASE_URL ="https://server-api.growatt.com";
//    public static String FILE_DOWNLOAD_BASE_URL ="http://20.6.1.114:8081/ShineServer_2016";

    public static String DATALOG_DETAIL="/newTwoEicAPI.do?op=getDatalogData";


    public static String INVERT_FILE_DOWNLOAD_BASE_URL ="http://oss1.growatt.com";

    public static String getFileDownLoadUrl() {
        return INVERT_FILE_DOWNLOAD_BASE_URL + "/api/v3/userCenter?op=getOssFileUploadList";
    }





    public static String getUrl() {
        String u = SqliteUtil.inquiryOssurl();
        if (!TextUtils.isEmpty(u)) {
            return "https://" + u;
        } else {
            return BASE_URL;
        }
    }





    /**
     * 请求登录
     * @param context    上下文
     * @param userName   用户名
     * @param password   密码
     * @param callback   回调处理
     */
    public static void login(Context context, String userName, String password,RequestCallback callback) {
        String url = getUrl() + "/api/v2/login";
        Map<String, String> params = new HashMap<>();
        params.put("userName",userName);
        params.put("password",MD5andKL.encryptPassword(password));
        InternetUtils.asynPost(context, url, params,callback);
    }



    public static void getDatalogData(Context context, String url, String datalogSn, StringCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("datalogSn",datalogSn);
        Log.i(params.toString());
        InternetUtils.asynPost(context, url, params,callback);
    }

    public static void getProtectionParameters(Context context,String datalogSn, RequestCallback callback) {
        String url = BASE_URL + "/newCountryCityAPI.do?op=getProtectionParameters";
        Map<String, String> params = new HashMap<>();
        params.put("language",datalogSn);
        Log.i(params.toString());
        InternetUtils.asynPost(context, url, params,callback);
    }


    public static void getOssLoginServer(Context context,String userName, String password,RequestCallback callback) {
        String url = BASE_URL + "/newCountryCityAPI.do?op=getProtectionParameters";
        Map<String, String> params = new HashMap<>();
        params.put("userName", userName);
        params.put("password", password);
        Log.i(params.toString());
        InternetUtils.asynPost(context, url, params,callback);
    }



    public static void getUpdataFileZip(Context context,String url,StringCallback callback) {
        Map<String, String> params = new HashMap<>();
        Log.i(params.toString());
        InternetUtils.asynPost(context, url, params,callback);
    }

    public static void getPrivacyPolicyUrlShinetools(Context context,
                                            String appType,String language,
                                            StringCallback callback) {

        String url = SERVER_BASE_URL + "/newLoginAPI.do?op=getPrivacyPolicyUrlShinetools";
        Map<String, String> params = new HashMap<>();
        params.put("appType", appType);
        params.put("language", language);
        Log.i(params.toString());
        InternetUtils.asynPost(context, url, params,callback);
    }

}
