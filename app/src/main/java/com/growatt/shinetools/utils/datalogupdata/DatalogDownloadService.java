package com.growatt.shinetools.utils.datalogupdata;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;


import com.growatt.shinetools.R;
import com.growatt.shinetools.utils.MyToastUtils;

import java.io.File;
import java.util.List;


/**
 * 后台下载
 */
public class DatalogDownloadService extends Service {

    private static final int NOTIFY_ID = 0;
    private static final String TAG = DatalogDownloadService.class.getSimpleName();

    public static boolean isRunning = false;
    private NotificationManager mNotificationManager;
    private DownloadBinder binder = new DownloadBinder();
    private NotificationCompat.Builder mBuilder;
    //    /**
//     * 开启服务方法
//     *
//     * @param context
//     */
//    public static void startService(Context context) {
//        Intent intent = new Intent(context, DownloadService.class);
//        context.startService(intent);
//    }
    private boolean mDismissNotificationProgress = false;

    public static void bindService(Context context, ServiceConnection connection) {
        Intent intent = new Intent(context, DatalogDownloadService.class);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        } else {
            context.startService(intent);
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }

        isRunning = true;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        isRunning = false;
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // 返回自定义的DownloadBinder实例
        return binder;
    }

    @Override
    public void onDestroy() {
        mNotificationManager = null;
        super.onDestroy();
    }

    /**
     * 创建通知
     */
    private void setUpNotification() {
        if (mDismissNotificationProgress) {
            return;
        }


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NotificationChannelEnum.MSG_APP_UPDATE.getId(), NotificationChannelEnum.MSG_APP_UPDATE.getName(), NotificationChannelEnum.MSG_APP_UPDATE.getImportant());
            //设置绕过免打扰模式
//            channel.setBypassDnd(false);
//            //检测是否绕过免打扰模式
//            channel.canBypassDnd();
//            //设置在锁屏界面上显示这条通知
//            channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
//            channel.setLightColor(Color.GREEN);
//            channel.setShowBadge(true);
//            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            channel.enableVibration(false);
            channel.enableLights(false);
            channel.setDescription(getString(R.string.android_key588));
            mNotificationManager.createNotificationChannel(channel);
        }


        mBuilder = new NotificationCompat.Builder(this, NotificationChannelEnum.MSG_APP_UPDATE.getId());
        mBuilder.setContentTitle(getString(R.string.android_key1365))
                .setContentText(getString(R.string.android_key1365))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(AppUpdateUtils.drawableToBitmap(AppUpdateUtils.getAppIcon(DatalogDownloadService.this)))
                .setOngoing(true)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis());
        mNotificationManager.notify(NOTIFY_ID, mBuilder.build());
        startForeground(1, mBuilder.build());

    }

    /**
     * 下载模块
     */
    private void startDownload(DatalogUpDateBean updateApp, final DownloadCallback callback,int current) {

        mDismissNotificationProgress = updateApp.ismDismissNotificationProgress();
        List<DatalogUpDateBean.DownFileBean> downFileBeans = updateApp.getDownFileBeans();
        DatalogUpDateBean.DownFileBean downFileBean = downFileBeans.get(current);
        String downUrl = downFileBean.getDownUrl();
        String fileName = downFileBean.getFileName();
        String savePath = downFileBean.getSavePath();

        if (TextUtils.isEmpty(downUrl)) {
            String contentText = getString(R.string.android_key3129);
            stop(contentText);
            return;
        }

        File appDir = new File(savePath);
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        Log.d("DownLoad","开始下载第："+current+"个,"+"路径："+downUrl+"\n保存路径:"+savePath+fileName);

        updateApp.getHttpManager().download(downUrl, savePath, fileName, new FileDownloadCallBack(callback,updateApp,current));
    }

    private void stop(String contentText) {
        if (mBuilder != null) {
            mBuilder.setContentTitle(AppUpdateUtils.getAppName(DatalogDownloadService.this))
                    .setContentText(contentText);
            Notification notification = mBuilder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            mNotificationManager.notify(NOTIFY_ID, notification);
        }
        close();
    }

    private void close() {
        stopSelf();
        isRunning = false;
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
         * @param totalSize 总大小 单位B
         */
        void onProgress(float progress, long totalSize);

        /**
         * 总大小
         *
         * @param totalSize 单位B
         */
        void setMax(long totalSize);

        /**
         * 下载完了
         *
         * @param file 下载的app
         * @return true ：下载完自动跳到安装界面，false：则不进行安装
         */
        boolean onFinish(File file);

        /**
         * 下载异常
         *
         * @param msg 异常信息
         */
        void onError(String msg);

        /**
         * 当应用处于前台，准备执行安装程序时候的回调，
         *
         * @param file 当前安装包
         * @return false 默认 false ,当返回时 true 时，需要自己处理 ，前提条件是 onFinish 返回 false 。
         */
        boolean onInstallAppAndAppOnForeground(File file);
    }

    /**
     * DownloadBinder中定义了一些实用的方法
     *
     * @author user
     */
    public class DownloadBinder extends Binder {
        /**
         * 开始下载
         *
         * @param updateApp 新app信息
         * @param callback  下载回调
         */
        public void start(DatalogUpDateBean updateApp, DownloadCallback callback) {
            //下载
            startDownload(updateApp, callback,0);
        }

        public void stop(String msg) {
            DatalogDownloadService.this.stop(msg);
        }
    }

    class FileDownloadCallBack implements HttpManager.FileCallback {
        private final DownloadCallback mCallBack;
        int oldRate = 0;
        private DatalogUpDateBean updateApp;
        private List<DatalogUpDateBean.DownFileBean> downFileBeans;
        private int current;

        public FileDownloadCallBack(@Nullable DownloadCallback callback, DatalogUpDateBean updateApp, int current) {
            super();
            this.mCallBack = callback;
            this.updateApp=updateApp;
            this.downFileBeans = updateApp.getDownFileBeans();
            this.current=current;
        }

        @Override
        public void onBefore() {
            Log.d("DownLoad","开始下载第："+current+"个");
            //初始化通知栏
            setUpNotification();
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
                    mCallBack.onProgress(progress, total);
                }

                if (mBuilder != null) {
                    mBuilder.setContentTitle(getString(R.string.android_key3130) + ":" + AppUpdateUtils.getAppName(DatalogDownloadService.this))
                            .setContentText(rate + "%")
                            .setProgress(100, rate, false)
                            .setWhen(System.currentTimeMillis());
                    Notification notification = mBuilder.build();
                    notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONLY_ALERT_ONCE;
                    mNotificationManager.notify(NOTIFY_ID, notification);
                }

                //重新赋值
                oldRate = rate;
            }
            Log.d("DownLoad","下载进度："+progress+"/"+total);

        }

        @Override
        public void onError(String error) {
            Log.d("DownLoad","开始下载出错");
//            Toast.makeText(DownloadService.this, getString(R.string.all_failed) + error, Toast.LENGTH_SHORT).show();
//            Toast.makeText(DownloadService.this, getString(R.string.all_failed), Toast.LENGTH_SHORT).show();
            MyToastUtils.toast(R.string.android_key3129);
            //App前台运行
            if (mCallBack != null) {
                mCallBack.onError(error);
            }
            try {
                mNotificationManager.cancel(NOTIFY_ID);
                close();
            } catch (Exception e1) {
                e1.printStackTrace();
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
                        if (!mCallBack.onFinish(file)) {
                            Log.d("DownLoad","下载完成回调2");
                            close();
                        }
                    }
                    //下载完自杀
                }else {
                    Log.d("DownLoad","下载第二个");
                    startDownload(updateApp, mCallBack,++current);
                }

           /*     if (AppUpdateUtils.isAppOnForeground(DatalogDownloadService.this) || mBuilder == null) {
                    //App前台运行
                    mNotificationManager.cancel(NOTIFY_ID);

                    if (mCallBack != null) {
                        boolean temp = mCallBack.onInstallAppAndAppOnForeground(file);
                        if (!temp) {
                            AppUpdateUtils.installApp(DatalogDownloadService.this, file);
                        }
                    } else {
                        AppUpdateUtils.installApp(DatalogDownloadService.this, file);
                    }


                } else {
                    //App后台运行
                    //更新参数,注意flags要使用FLAG_UPDATE_CURRENT
                    Intent installAppIntent = AppUpdateUtils.getInstallAppIntent(DatalogDownloadService.this, file);
                    PendingIntent contentIntent = PendingIntent.getActivity(DatalogDownloadService.this, 0, installAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(contentIntent)
                            .setContentTitle(AppUpdateUtils.getAppName(DatalogDownloadService.this))
                            .setContentText(getString(R.string.m点击安装))
                            .setProgress(0, 0, false)
                            //                        .setAutoCancel(true)
                            .setDefaults((Notification.DEFAULT_ALL));
                    Notification notification = mBuilder.build();
                    notification.flags = Notification.FLAG_AUTO_CANCEL;
                    mNotificationManager.notify(NOTIFY_ID, notification);
                }*/

            } catch (Exception e) {
                Log.d("DownLoad","trycatch"+e.toString());
                e.printStackTrace();
            } finally {
                Log.d("DownLoad","关闭下载");
                close();
            }
        }
    }
}
