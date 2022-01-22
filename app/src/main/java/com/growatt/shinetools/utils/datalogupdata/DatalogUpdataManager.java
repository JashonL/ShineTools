package com.growatt.shinetools.utils.datalogupdata;

import android.app.Activity;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.growatt.shinetools.R;
import com.growatt.shinetools.ShineToosApplication;
import com.growatt.shinetools.db.realm.RealmUtils;
import com.growatt.shinetools.okhttp.callback.StringCallback;
import com.growatt.shinetools.utils.CircleDialogUtils;
import com.growatt.shinetools.utils.Log;
import com.growatt.shinetools.utils.MyToastUtils;
import com.growatt.shinetools.utils.ShineToolsApi;
import com.mylhyl.circledialog.BaseCircleDialog;
import com.mylhyl.circledialog.view.listener.OnCreateBodyViewListener;

import org.json.JSONObject;

import java.io.File;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;

public class DatalogUpdataManager {
    private Map<String, String> mParams;
    // 是否忽略默认参数，解决
    private boolean mIgnoreDefParams = false;
    private Activity mActivity;
    private HttpManager mHttpManager;
    private String mUpdateUrl;
    private int mThemeColor;
    private @DrawableRes
    int mTopPic;
    private String mAppKey;
    private DatalogUpDateBean mUpdateApp;
    private String mTargetPath;
    private boolean isPost;
    private boolean mHideDialog;
    private boolean mShowIgnoreVersion;
    private boolean mDismissNotificationProgress;
    private boolean mOnlyWifi;
    private String datalogSn;
    //文件下载保存父目录
    private String fileDir;
    //文件具体保存位置
    private String xSavePath;
    private String sSavePath;
    private BaseCircleDialog dialogFragment;

    private BaseCircleDialog progressDialog;


    public DatalogUpdataManager(DatalogUpdataManager.Builder builder) {
        mActivity = builder.getActivity();
        mHttpManager = builder.getHttpManager();
        mUpdateUrl = builder.getUpdateUrl();

        mThemeColor = builder.getThemeColor();
        mTopPic = builder.getTopPic();

        mIgnoreDefParams = builder.isIgnoreDefParams();
        if (!mIgnoreDefParams) {
            mAppKey = builder.getAppKey();
        }
        mTargetPath = builder.getTargetPath();
        isPost = builder.isPost();
        mParams = builder.getParams();
        mHideDialog = builder.isHideDialog();
        mShowIgnoreVersion = builder.isShowIgnoreVersion();
        mDismissNotificationProgress = builder.isDismissNotificationProgress();
        mOnlyWifi = builder.isOnlyWifi();
        fileDir = builder.getmFileDir();
        datalogSn = builder.getDatalogSn();
    }


    /**
     * 检测是否有新版本
     *
     * @param callback 更新回调
     */
    public void checkNewVersion(final DataLogUpdateCallback callback) {
        if (callback == null) {
            return;
        }
        //网络请求
        ShineToolsApi.getDatalogData(mActivity, mUpdateUrl, datalogSn, new StringCallback() {

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                callback.onBefore();
            }

            @Override
            public void onError(Call call, Exception e, int id) {

                String errorMsg= ShineToosApplication.getContext().getString(R.string.android_key2030);
                if (e != null) {
                    if (e instanceof SocketTimeoutException) {
                        errorMsg= ShineToosApplication.getContext().getString(R.string.android_key1853);
                    } if (e instanceof ConnectException) {
                        errorMsg= ShineToosApplication.getContext().getString(R.string.android_key1922);
                    }
                }
                MyToastUtils.toast(errorMsg);

                callback.onAfter();
                callback.onServerError();
            }

            @Override
            public void onResponse(String json, int id) {
                callback.onAfter();
                if (json != null) {
                    processData(json, callback);
                }
            }


            @Override
            public void onAfter(int id) {
                super.onAfter(id);
            }
        });

    }


    /**
     * 解析
     *
     * @param result
     * @param callback
     */
    private void processData(String result, @NonNull DataLogUpdateCallback callback) {
        try {
            mUpdateApp = new DatalogUpDateBean();
            JSONObject jsonResult = new JSONObject(result);
            int result1 = jsonResult.optInt("result", 0);
            if (result1 == 1) {
                JSONObject jsonObject1 = jsonResult.optJSONObject("obj");
                if (jsonObject1 == null) return;
                JSONObject jsonObject = jsonObject1.optJSONObject("datalogWifiFile");
                if (jsonObject != null) {
                    String xVersion = jsonObject.optString("wifi-x_version", "");

                    String wifix_1 = jsonObject.optString("wifi-x_1", "");
                    String wifix_2 = jsonObject.optString("wifi-x_2", "");

                    String sVersion = jsonObject.optString("wifi-s_version", "");
                    String wifis_1 = jsonObject.optString("wifi-s_1", "");
                    String wifis_2 = jsonObject.optString("wifi-s_2", "");


                    String name1 = wifix_1.substring(wifix_1.lastIndexOf("/"));
                    String name2 = wifix_2.substring(wifix_2.lastIndexOf("/"));
                    String name3 = wifis_1.substring(wifis_1.lastIndexOf("/"));
                    String name4 = wifis_2.substring(wifis_2.lastIndexOf("/"));

                    File versionFile_x1 = new File(fileDir + File.separator + "wifi-x_version" + File.separator + xVersion, name1);
                    File versionFile_x2 = new File(fileDir + File.separator + "wifi-x_version" + File.separator + xVersion, name2);
                    File versionFile_s1 = new File(fileDir + File.separator + "wifi-s_version" + File.separator + sVersion, name3);
                    File versionFile_s2 = new File(fileDir + File.separator + "wifi-s_version" + File.separator + sVersion, name4);


                    List<String> urls = new ArrayList<>();

                    FilePathBean pathBean=new FilePathBean();
                    pathBean.setId(1);
//                    FilePathBean pathBean = RealmUtils.queryFilePathList();
//                    if (pathBean == null) {
//                        pathBean = new FilePathBean();
//                        pathBean.setId(1);
//
//                    }
                    List<DatalogUpDateBean.DownFileBean> downFileBeans = new ArrayList<>();


                    if (!versionFile_x1.exists()) {//-x有更新
                        mUpdateApp.setUpdate(true);
                        urls.add(wifix_1);
                        versionFile_x1.getParentFile().mkdirs();//创建父目录
                        Log.d("-x有更新：" + versionFile_x1.getAbsolutePath() + versionFile_x1.getParentFile().exists());
                        xSavePath = versionFile_x1.getParent();
                        DatalogUpDateBean.DownFileBean downFileBean = new DatalogUpDateBean.DownFileBean();
                        downFileBean.setDownUrl(wifix_1);
                        downFileBean.setFileName(name1);
                        downFileBean.setSavePath(versionFile_x1.getParent());
                        downFileBeans.add(downFileBean);
                    }
                    pathBean.setShineX_user1(versionFile_x1.getAbsolutePath());
                    pathBean.setShineX_version(xVersion);


                    if (!versionFile_x2.exists()) {//-x有更新
                        mUpdateApp.setUpdate(true);
                        urls.add(wifix_2);
                        versionFile_x2.getParentFile().mkdirs();//创建父目录
                        Log.d("-x有更新：" + versionFile_x2.getAbsolutePath());
                        DatalogUpDateBean.DownFileBean downFileBean = new DatalogUpDateBean.DownFileBean();
                        downFileBean.setDownUrl(wifix_2);
                        downFileBean.setFileName(name2);
                        downFileBean.setSavePath(versionFile_x2.getParent());
                        downFileBeans.add(downFileBean);
                    }
                    pathBean.setShineX_user2(versionFile_x2.getAbsolutePath());

                    if (!versionFile_s1.exists()) {//-s有更新
                        mUpdateApp.setUpdate(true);
                        urls.add(wifix_1);
                        versionFile_s1.getParentFile().mkdirs();//创建父目录
                        Log.d("-s有更新：" + versionFile_s1.getAbsolutePath());
                        xSavePath = versionFile_x1.getParent();
                        DatalogUpDateBean.DownFileBean downFileBean = new DatalogUpDateBean.DownFileBean();
                        downFileBean.setDownUrl(wifis_1);
                        downFileBean.setFileName(name3);
                        downFileBean.setSavePath(versionFile_s1.getParent());
                        downFileBeans.add(downFileBean);
                    }
                    pathBean.setShineS_user1(versionFile_s1.getAbsolutePath());
                    pathBean.setShineS_version(sVersion);

                    if (!versionFile_s2.exists()) {//-s有更新
                        mUpdateApp.setUpdate(true);
                        urls.add(wifix_1);
                        versionFile_s2.getParentFile().mkdirs();//创建父目录
                        Log.d("-s有更新：" + versionFile_s2.getAbsolutePath());
                        DatalogUpDateBean.DownFileBean downFileBean = new DatalogUpDateBean.DownFileBean();
                        downFileBean.setDownUrl(wifis_2);
                        downFileBean.setFileName(name4);
                        downFileBean.setSavePath(versionFile_s2.getParent());
                        downFileBeans.add(downFileBean);
                    }
                    pathBean.setShineS_user2(versionFile_s2.getAbsolutePath());

                    mUpdateApp.setFile_urls(urls);
                    mUpdateApp.setDownFileBeans(downFileBeans);
//                    SharedPreferencesUnit.getInstance(mActivity).put(Constant.SHINE_X_VERSION,pathBean.getShineX_version());
//                    SharedPreferencesUnit.getInstance(mActivity).put(Constant.SHINE_X_PATH_1,pathBean.getShineX_user1());
//                    SharedPreferencesUnit.getInstance(mActivity).put(Constant.SHINE_X_PATH_2,pathBean.getShineX_user2());
//                    SharedPreferencesUnit.getInstance(mActivity).put(Constant.SHINE_S_VERSION,pathBean.getShineS_version());
//                    SharedPreferencesUnit.getInstance(mActivity).put(Constant.SHINE_S_PATH_1,pathBean.getShineS_user1());
//                    SharedPreferencesUnit.getInstance(mActivity).put(Constant.SHINE_S_PATH_2,pathBean.getShineS_user2());

                    RealmUtils.addFilePathBean(pathBean);
                }


                if (mUpdateApp.isUpdate()) {
                    callback.hasNewVersion(mUpdateApp, this);
                } else {
                    callback.noNewVirsion("没有新版本");
                }
            }

        } catch (Exception ignored) {
            ignored.printStackTrace();
//            callback.noNewVirsion(String.format("解析自定义更新配置消息出错[%s]", ignored.getMessage()));
            callback.onServerError();
        }
    }


    /**
     * 显示更新提示
     */
    public void showDialogFragment() {
        if (dialogFragment == null) {
            Log.i("显示弹框");
            View view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_layout, null);
            dialogFragment = CircleDialogUtils.showCommentBodyView(mActivity, view, "", ((FragmentActivity) mActivity).getSupportFragmentManager(), new OnCreateBodyViewListener() {
                @Override
                public void onCreateBodyView(View view) {

                }
            }, Gravity.CENTER, 0.8f, 0.5f,false);
        }
    }

    /**
     * 隐藏更新提示框
     */
    public void dissmissDialog() {
        if (dialogFragment != null) {
            Log.i("隐藏弹框");
            dialogFragment.dialogDismiss();
            dialogFragment = null;
        }
    }


    /**
     * 跳转到更新页面
     */
    TextView tvLoadTitle;
    ProgressBar pbProgress;
    TextView tvProgress;

    public void showProgressFragment() {
        Log.i("显示下载框");
        if (progressDialog == null) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_download_layout, null);
            progressDialog = CircleDialogUtils.showCommentBodyView(mActivity, view, "", ((FragmentActivity) mActivity).getSupportFragmentManager(), new OnCreateBodyViewListener() {
                @Override
                public void onCreateBodyView(View view) {
                    tvLoadTitle = view.findViewById(R.id.tv_loading_title);
                    pbProgress = view.findViewById(R.id.bp_progress);
                    tvProgress = view.findViewById(R.id.tv_progress);
                }
            }, Gravity.CENTER, 0.8f, 0.5f,true);
        }
    }


    public void setProgress(float progress, int total, int current) {
        int current_progress = Math.round(progress * 100);

        if (tvLoadTitle != null) {
            String title = mActivity.getString(R.string.android_key3131) + "(" + current + "/" + total + ")";
            tvLoadTitle.setText(title);
        }

        if (pbProgress != null) {
            pbProgress.setProgress(current_progress);
        }


        if (tvProgress != null) {
            tvProgress.setText(current_progress + "%");
        }

    }

    /**
     * 显示下载提示
     */
    public void dissmissDownDialog() {
        if (progressDialog != null) {
            progressDialog.dialogDismiss();
            progressDialog = null;
        }
    }


    /**
     * 去下载升级包
     */
    public void startDownLoad(FileDownLoadManager.DownloadCallback downloadCallback) {

        if (mUpdateApp == null) {
            throw new NullPointerException("updateApp 不能为空");
        }
        mUpdateApp.setTargetPath(mTargetPath);
        mUpdateApp.setHttpManager(mHttpManager);
        FileDownLoadManager fileDownLoadManager = new FileDownLoadManager(mUpdateApp, mActivity);
        fileDownLoadManager.startDownload(downloadCallback, 0);
        //使用service下载
   /*     DatalogDownloadService.bindService(mActivity.getApplicationContext(), new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                ((DatalogDownloadService.DownloadBinder) service).start(mUpdateApp, downloadCallback);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        });*/
    }


    public static class Builder {
        //必须有
        private Activity mActivity;
        //必须有
        private HttpManager mHttpManager;
        //必须有
        private String mUpdateUrl;

        private String mFileDir;


        //1，设置按钮，进度条的颜色
        private int mThemeColor = 0;
        //2，顶部的图片
        private
        @DrawableRes
        int mTopPic = 0;
        //3,唯一的appkey
        private String mAppKey;
        //4,apk的下载路径
        private String mTargetPath;
        //5,是否是post请求，默认是get
        private boolean isPost;
        //6,自定义参数
        private Map<String, String> params;
        // 是否忽略默认参数，解决
        private boolean mIgnoreDefParams = false;
        //7,是否隐藏对话框下载进度条
        private boolean mHideDialog = false;
        private boolean mShowIgnoreVersion;
        private boolean dismissNotificationProgress;
        private boolean mOnlyWifi;
        private IUpdateDialogFragmentListener mUpdateDialogFragmentListener;

        private String datalogSn;

        public Map<String, String> getParams() {
            return params;
        }

        /**
         * 自定义请求参数
         *
         * @param params 自定义请求参数
         * @return Builder
         */
        public DatalogUpdataManager.Builder setParams(Map<String, String> params) {
            this.params = params;
            return this;
        }

        public boolean isIgnoreDefParams() {
            return mIgnoreDefParams;
        }

        /**
         * @param ignoreDefParams 是否忽略默认的参数注入 appKey version
         * @return Builder
         */
        public DatalogUpdataManager.Builder setIgnoreDefParams(boolean ignoreDefParams) {
            this.mIgnoreDefParams = ignoreDefParams;
            return this;
        }

        public boolean isPost() {
            return isPost;
        }

        /**
         * 是否是post请求，默认是get
         *
         * @param post 是否是post请求，默认是get
         * @return Builder
         */
        public DatalogUpdataManager.Builder setPost(boolean post) {
            isPost = post;
            return this;
        }

        public String getTargetPath() {
            return mTargetPath;
        }

        /**
         * apk的下载路径，
         *
         * @param targetPath apk的下载路径，
         * @return Builder
         */
        public DatalogUpdataManager.Builder setTargetPath(String targetPath) {
            mTargetPath = targetPath;
            return this;
        }

        public String getAppKey() {
            return mAppKey;
        }

        /**
         * 唯一的appkey
         *
         * @param appKey 唯一的appkey
         * @return Builder
         */
        public DatalogUpdataManager.Builder setAppKey(String appKey) {
            mAppKey = appKey;
            return this;
        }

        public Activity getActivity() {
            return mActivity;
        }

        /**
         * 是否是post请求，默认是get
         *
         * @param activity 当前提示的Activity
         * @return Builder
         */
        public DatalogUpdataManager.Builder setActivity(Activity activity) {
            mActivity = activity;
            return this;
        }

        public HttpManager getHttpManager() {
            return mHttpManager;
        }

        /**
         * 设置网络工具
         *
         * @param httpManager 自己实现的网络对象
         * @return Builder
         */
        public DatalogUpdataManager.Builder setHttpManager(HttpManager httpManager) {
            mHttpManager = httpManager;
            return this;
        }

        public String getUpdateUrl() {
            return mUpdateUrl;
        }

        /**
         * 更新地址
         *
         * @param updateUrl 更新地址
         * @return Builder
         */
        public DatalogUpdataManager.Builder setUpdateUrl(String updateUrl) {
            mUpdateUrl = updateUrl;
            return this;
        }

        public int getThemeColor() {
            return mThemeColor;
        }

        /**
         * 设置按钮，进度条的颜色
         *
         * @param themeColor 设置按钮，进度条的颜色
         * @return Builder
         */
        public DatalogUpdataManager.Builder setThemeColor(int themeColor) {
            mThemeColor = themeColor;
            return this;
        }

        public int getTopPic() {
            return mTopPic;
        }

        /**
         * 顶部的图片
         *
         * @param topPic 顶部的图片
         * @return Builder
         */
        public DatalogUpdataManager.Builder setTopPic(int topPic) {
            mTopPic = topPic;
            return this;
        }

        public IUpdateDialogFragmentListener getUpdateDialogFragmentListener() {
            return mUpdateDialogFragmentListener;
        }

        /**
         * 设置默认的UpdateDialogFragment监听器
         *
         * @param updateDialogFragmentListener updateDialogFragmentListener 更新对话框关闭监听
         * @return Builder
         */
        public DatalogUpdataManager.Builder setUpdateDialogFragmentListener(IUpdateDialogFragmentListener updateDialogFragmentListener) {
            this.mUpdateDialogFragmentListener = updateDialogFragmentListener;
            return this;
        }


        public String getmFileDir() {
            return mFileDir;
        }

        public DatalogUpdataManager.Builder setmFileDir(String mFileDir) {
            this.mFileDir = mFileDir;
            return this;
        }

        public String getDatalogSn() {
            return datalogSn;
        }

        public DatalogUpdataManager.Builder setDatalogSn(String datalogSn) {
            this.datalogSn = datalogSn;
            return this;
        }

        /**
         * @return 生成app管理器
         */
        public DatalogUpdataManager build() {
            //校验
            if (getActivity() == null || getHttpManager() == null || TextUtils.isEmpty(getUpdateUrl())) {
                throw new NullPointerException("必要参数不能为空");
            }
            if (TextUtils.isEmpty(getTargetPath())) {
                //sd卡是否存在
                String path = "";
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || !Environment.isExternalStorageRemovable()) {
                    try {
                        path = getActivity().getExternalCacheDir().getAbsolutePath();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (TextUtils.isEmpty(path)) {
                        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                    }
                } else {
                    path = getActivity().getCacheDir().getAbsolutePath();
                }
                setTargetPath(path);
            }
            return new DatalogUpdataManager(this);
        }

        /**
         * 是否隐藏对话框下载进度条
         *
         * @return Builder
         */
        public DatalogUpdataManager.Builder hideDialogOnDownloading() {
            mHideDialog = true;
            return this;
        }

        /**
         * @return 是否影藏对话框
         */
        public boolean isHideDialog() {
            return mHideDialog;
        }

        /**
         * 显示忽略版本
         *
         * @return 是否忽略版本
         */
        public DatalogUpdataManager.Builder showIgnoreVersion() {
            mShowIgnoreVersion = true;
            return this;
        }

        public boolean isShowIgnoreVersion() {
            return mShowIgnoreVersion;
        }

        /**
         * 不显示通知栏进度条
         *
         * @return 是否显示进度条
         */
        public DatalogUpdataManager.Builder dismissNotificationProgress() {
            dismissNotificationProgress = true;
            return this;
        }

        public boolean isDismissNotificationProgress() {
            return dismissNotificationProgress;
        }

        public DatalogUpdataManager.Builder setOnlyWifi() {
            mOnlyWifi = true;
            return this;
        }

        public boolean isOnlyWifi() {
            return mOnlyWifi;
        }

        public DatalogUpdataManager.Builder handleException(ExceptionHandler exceptionHandler) {
            ExceptionHandlerHelper.init(exceptionHandler);
            return this;
        }

    }


}
