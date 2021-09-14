package com.growatt.shinetools.db.realm;

import android.text.TextUtils;

import androidx.annotation.NonNull;


import com.growatt.shinetools.utils.Log;
import com.growatt.shinetools.utils.datalogupdata.FilePathBean;



import io.realm.Realm;
import io.realm.RealmResults;


public class RealmUtils {

    /**
     * 增加：采集器升级文件路径保存
     */
    public static void addFilePathBean(@NonNull FilePathBean extraBean) {
        try {
            Realm mRealm = Realm.getDefaultInstance();
            mRealm.executeTransaction(realm -> realm.insertOrUpdate(extraBean));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 查询：获取升级文件
     */
    public static FilePathBean queryFilePathList() {
        try {
            Realm mRealm = Realm.getDefaultInstance();
            RealmResults<FilePathBean> all = mRealm.where(FilePathBean.class).findAll();
            Log.d("升级文件数量："+all.size());
            if (all.size()==0)return null;
            return all.first();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
