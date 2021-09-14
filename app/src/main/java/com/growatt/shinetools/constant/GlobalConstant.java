package com.growatt.shinetools.constant;

import android.content.Context;

public class GlobalConstant {


    //终端用户
    public static final int END_USER = 1;
    //运维用户
    public static final int MAINTEAN_USER = 2;
    //终端用户密码
    public static final String KEY_END_USER_PWD = "end_user_pwd_key";
    //自动登录
    public static final String KEY_AUTO_LOGIN = "autologin_key";
    //记录登录用户的类型
    public static final String KEY_USER_TYPE = "key_user_type";
    //使用手册
    public static final String web_url = "http://oss.growatt.com:80/common/knowledgeShareH5No?lang=cn&type=";
    public final static String MAX_PWD = "max_password";

    //扫码结果
    public final static String SCAN_RESULT = "scan_result";

    //数据库名称
    public final static String REAM_NAME = "ShineTools_Realm.realm";

    //是否修改密码
    public final static String KEY_MODIFY_PWD = "modify_pwd_key";


    public final static String MAX_NEED_PWD = "max_need_pwd";
    public final static String MAX_NEED_PWD_TRUE = "max_need_pwd_true";
    public final static String MAX_NEED_PWD_FALSE = "max_need_pwd_false";

    public final static String MAX_ONEKEY_LAST_TIME = "max_onekey_last_time";

    public final static String MAX_ONEKEY_LAST_DATA1 = "max_onekey_last_data1";
    public final static String MAX_ONEKEY_LAST_DATA2 = "max_onekey_last_data2";

    public static int num = 0;//
    public final static String MAX_ONEKEY_LIST_LAST_DATA2 = "max_onekey_list_last_data2";
    public final static String MAX_ONEKEY_LIST_LAST_DATA3 = "max_onekey_list_last_data3";

    public final static String MAX_ONEKEY_LAST_DATA3 = "max_onekey_last_data3";

    //数据库版本
    public static String getSqliteName(Context context) {

        return "sqldata2.db";
    }


    /**
     * max本地iv曲线最后更新时间常量：MaxCheckIVActivity
     */
    public final static String MAX_IV_LAST_TIME = "max_iv_last_time";
    public final static String MAX_REAL_LAST_TIME = "max_real_last_time";
    public final static String MAX_ERR_LAST_TIME = "max_err_last_time";


    /**
     * max本地工具数据表
     */
    public final static String MAX_TOOLS_SQLITE = "max_tools_sqlite";
    /**
     * max本地曲线最后更新数据常量：MaxCheckIVActivity
     */
    public final static String MAX_IV_LAST_DATA = "max_iv_last_data";
    public final static String MAX_REAL_LAST_DATA = "max_real_last_data";
    public final static String MAX_REAL_ID_LAST_DATA = "max_real_id_last_data";
    public final static String MAX_ERR_LAST_DATA = "max_err_last_data";
    public final static String MAX_ERR_ID_LAST_DATA = "max_err_id_last_data";
    public final static String MAX_ERR_OTHER_LAST_DATA = "max_err_other_last_data";



    public final static String MAX_ONEKEY_LAST_DATA4 = "max_onekey_last_data4";
    public final static String MAX_ONEKEY_LAST_DATA5 = "max_onekey_last_data5";


    public static String TOOL_TLX_MODEL_COUNTRY = "tool_tlx_model_country";//定位坐标系
    //记录登录用户的类型
    public static final String KEY_JSON = "KEY_JSON";
    public static final String PERIOD_JSON = "PERIOD_JSON";
    public static final String BYTE_SOCKET_RESPON="BYTE_SOCKET_RESPON";

}
