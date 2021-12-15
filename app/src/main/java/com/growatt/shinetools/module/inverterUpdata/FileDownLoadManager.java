package com.growatt.shinetools.module.inverterUpdata;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.growatt.shinetools.R;
import com.growatt.shinetools.utils.MyToastUtils;
import com.growatt.shinetools.utils.datalogupdata.HttpManager;

import java.io.File;
import java.util.List;

public class FileDownLoadManager {
    private FileDownBean updateApp;
    private Context context;


    public FileDownLoadManager(FileDownBean updateApp, Context context) {
        this.updateApp = updateApp;
        this.context = context;
    }

    /**
     * 下载模块
     */
    public void startDownload(final DownloadCallback callback, int current) {
        List<FileDownBean.DownFileBean> downFileBeans = updateApp.getDownFileBeans();
        FileDownBean.DownFileBean downFileBean = downFileBeans.get(current);
        String downUrl = downFileBean.getDownUrl();
        String fileName = downFileBean.getFileName();
        String savePath = downFileBean.getSavePath();

        if (TextUtils.isEmpty(downUrl)) {
            return;
        }

        File appDir = new File(savePath);
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        Log.d("DownLoad","开始下载第："+current+"个,"+"路径："+downUrl+"\n保存路径:"+savePath+fileName);
        updateApp.getHttpManager().download(downUrl, savePath, fileName, new FileDownloadCallBack(callback,updateApp,current));
    }


    /**
     * 进度条回调接口
     */
    public interface DownloadCallback {
        /**
         * 开始
         */
        void onStart();

        /**
         * 进度
         *
         * @param progress  进度 0.00 -1.00 ，总大小
         * @param total 文件总数量,
         * @param current 当前是第几个
         */
        void onProgress(float progress, int total, int current);

        /**
         * 总大小
         *
         * @param totalSize 单位B
         */
        void setMax(long totalSize);

        /**
         * 下载完了
         *
         */
        void onFinish();

        /**
         * 下载异常
         *
         * @param msg 异常信息
         */
        void onError(String msg);


    }


    class FileDownloadCallBack implements HttpManager.FileCallback {
        private final DownloadCallback mCallBack;
        int oldRate = 0;
        private FileDownBean updateApp;
        private List<FileDownBean.DownFileBean> downFileBeans;
        private int current;

        public FileDownloadCallBack(@Nullable DownloadCallback callback, FileDownBean updateApp, int current) {
            super();
            this.mCallBack = callback;
            this.updateApp=updateApp;
            this.downFileBeans = updateApp.getDownFileBeans();
            this.current=current;
        }

        @Override
        public void onBefore() {
            Log.d("DownLoad","开始下载第："+current+"个");
            if (mCallBack != null) {
                mCallBack.onStart();
            }
        }

        @Override
        public void onProgress(float progress, long total) {
            //做一下判断，防止自回调过于频繁，造成更新通知栏进度过于频繁，而出现卡顿的问题。
            int rate = Math.round(progress * 100);
            if (oldRate != rate) {
                if (mCallBack != null) {
                    mCallBack.setMax(total);
                    mCallBack.onProgress(progress, downFileBeans.size(),current);
                }

                //重新赋值
                oldRate = rate;
            }
            Log.d("DownLoad","下载进度："+progress+"/"+total);

        }

        @Override
        public void onError(String error) {
            Log.d("DownLoad","开始下载出错");
            MyToastUtils.toast(context.getString(R.string.all_failed));
            //App前台运行
            if (mCallBack != null) {
                mCallBack.onError(error);
            }
        }

        @Override
        public void onResponse(File file) {
            Log.d("DownLoad","下载完成回调");
            try {
                if (current==downFileBeans.size()-1){//最后一个
                    Log.d("DownLoad","最后一个");
                    if (mCallBack != null) {
                        Log.d("DownLoad","下载完成回调1");
                        mCallBack.onFinish();
                    }
                    //下载完自杀
                }else {
                    startDownload( mCallBack,++current);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Log.d("DownLoad","关闭下载");
            }
        }
    }


}
