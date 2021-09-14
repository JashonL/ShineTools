package com.growatt.shinetools.utils.datalogupdata;

import android.app.Activity;

import com.growatt.shinetools.ShineToosApplication;


public class DatalogUpdataUtils {

    public static DatalogUpdataManager checkUpdata(Activity act,String updataUrl, String datalogSn) {
        DatalogUpdataManager build = new DatalogUpdataManager.Builder()
                .setActivity(act)
                .setUpdateUrl(updataUrl)
                .setPost(true)
                .handleException(e -> {
                })
                //实现httpManager接口的对象
                .setHttpManager(new DatalogUpDataHttpUtil())
                .setDatalogSn(datalogSn)
                .setmFileDir(ShineToosApplication.DATALOGER_UPDATA_DIR)
                .build();
        return build;
    }
}
