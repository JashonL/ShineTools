package com.growatt.shinetools.module.inverterUpdata;

import android.app.Activity;

import com.growatt.shinetools.ShineToosApplication;
import com.growatt.shinetools.utils.ShineToolsApi;
import com.growatt.shinetools.utils.datalogupdata.DatalogUpDataHttpUtil;

public class CheckDownloadUtils {

    public static FileUpdataManager checkUpdata(Activity act) {
        FileUpdataManager build = new FileUpdataManager.Builder()
                .setActivity(act)
                .setUpdateUrl(ShineToolsApi.getFileDownLoadUrl())
                .setPost(true)
                //实现httpManager接口的对象
                .setHttpManager(new DatalogUpDataHttpUtil())
                .setmFileDir(ShineToosApplication.INVERTER_UPDATA_FILE_DIR)
                .build();
        return build;

    }
}
