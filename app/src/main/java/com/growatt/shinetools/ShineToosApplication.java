package com.growatt.shinetools;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.webkit.WebView;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.growatt.shinetools.constant.GlobalConstant;
import com.growatt.shinetools.db.realm.Migration;
import com.growatt.shinetools.okhttp.OkHttpUtils;
import com.growatt.shinetools.okhttp.https.HttpsUtils;
import com.growatt.shinetools.okhttp.log.LoggerInterceptor;
import com.growatt.shinetools.utils.Log;
import com.hjq.toast.ToastUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import okhttp3.OkHttpClient;

import static com.growatt.shinetools.constant.GlobalConstant.END_USER;


public class ShineToosApplication extends Application {

    public  static String DATALOGER_UPDATA_DIR;

    private static ShineToosApplication context = null;

    private int user_type = END_USER;

    //防止内存泄漏，使用软引用来存Activity
    private List<WeakReference<Activity>> activityList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        Log.isPrint = false;

        ClearableCookieJar cookieJar1 = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(getApplicationContext()));

        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);

//        CookieJarImpl cookieJar1 = new CookieJarImpl(new MemoryCookieStore());
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30000L, TimeUnit.MILLISECONDS)
                .readTimeout(30000, TimeUnit.MILLISECONDS)
                .addInterceptor(new LoggerInterceptor("TAG",true))
                .cookieJar(cookieJar1)
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .build();
        OkHttpUtils.initClient(okHttpClient);

        ToastUtils.init(this);


        //适配android 9  不可多进程使用同一个目录webView
        webviewSetPath(getApplicationContext());


        //数据库初始化
        Realm.init(this);
        RealmConfiguration configRealm = new RealmConfiguration.Builder()
                .name(GlobalConstant.REAM_NAME)
                .schemaVersion(1)
                .migration(new Migration())
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(configRealm);
        DATALOGER_UPDATA_DIR = getFilesDir().getPath() + File.separator + "datalog" + File.separator;//


//        Crasheye.init(context, "aada7e10");
    }


    public static ShineToosApplication getContext() {
        return context;
    }

    //Android P 以及之后版本不支持同时从多个进程使用具有相同数据目录的WebView
    //为其它进程webView设置目录
    public void webviewSetPath(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String processName = getProcessName(context);
            if (!this.getPackageName().equals(processName)) {//判断不等于默认进程名称
                WebView.setDataDirectorySuffix(processName);
            }
        }
    }

    public String getProcessName(Context context) {
        if (context == null) return null;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == android.os.Process.myPid()) {
                return processInfo.processName;
            }
        }
        return null;
    }


    public int getUser_type() {
        return user_type;
    }

    public void setUser_type(int user_type) {
        this.user_type = user_type;
    }


    public List<WeakReference<Activity>> getActivityList() {
        return activityList;
    }

    public void setActivityList(List<WeakReference<Activity>> activityList) {
        this.activityList = activityList;
    }

    //添加 Activity 的软引用到容器中
    public void addActivity(WeakReference<Activity> softReference) {
        activityList.add(softReference);
    }

    //遍历所有Activity并finish
    public void exit() {
        try {
            for (int i = 0; i < activityList.size(); i++) {
                Activity activity = activityList.get(i).get();
                if (activity != null) {
                    activity.finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }

    }
}
