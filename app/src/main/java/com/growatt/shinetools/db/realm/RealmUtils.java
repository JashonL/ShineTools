package com.growatt.shinetools.db.realm;

import android.text.TextUtils;

import androidx.annotation.NonNull;


import com.growatt.shinetools.bean.OssUrlBean;
import com.growatt.shinetools.constant.Cons;
import com.growatt.shinetools.utils.Log;
import com.growatt.shinetools.utils.LogUtil;
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



    /**
     * 存储用户服务器地址
     */
    public static void addOssUrl(String url) {
        try {
            Realm mRealm = Realm.getDefaultInstance();
            mRealm.executeTransaction(realm -> {
                OssUrlBean bean = new OssUrlBean();
                bean.setUrl(url);
                realm.insertOrUpdate(bean);
            });
            Cons.setOssRealUrl(url);
       /*     if (url.contains("-cn")) Cons.setCountryCode(Cons.CHINA_AREA_CODE);
            else Cons.setCountryCode(Cons.EUROPE_AREA_CODE);*/
            LogUtil.i("addUrl:" + url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 查询：获取OSS url
     */
    public static String queryOssUrl() {
        if (!TextUtils.isEmpty(Cons.getOssRealUrl())) {
            LogUtil.i("queryUrl缓存:" + Cons.getOssRealUrl());
            return Cons.getOssRealUrl();
        }
        try {
            Realm mRealm = Realm.getDefaultInstance();
            OssUrlBean urlBean = mRealm.where(OssUrlBean.class).equalTo("primaryKey", 0).findFirst();
            if (urlBean==null){
                return "";
            }else {
                LogUtil.i("queryUrl数据库:" + urlBean.toString());
                return urlBean.getUrl();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
