package com.growatt.shinetools.module.inverterUpdata;

import android.app.Activity;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.growatt.shinetools.R;
import com.growatt.shinetools.ShineToosApplication;
import com.growatt.shinetools.module.localbox.max.type.DeviceConstant;
import com.growatt.shinetools.okhttp.callback.StringCallback;
import com.growatt.shinetools.utils.FileUtils;
import com.growatt.shinetools.utils.MyToastUtils;
import com.growatt.shinetools.utils.ShineToolsApi;
import com.growatt.shinetools.utils.datalogupdata.HttpManager;

import org.json.JSONObject;

import java.io.File;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;

public class FileUpdataManager {
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
    private FileDownBean mUpdateApp;
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
    private DialogFragment dialogFragment;

    private DialogFragment progressDialog;


    public FileUpdataManager(FileUpdataManager.Builder builder) {
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
    public void checkNewVersion(final FileCheckUpdataCallback callback) {
        if (callback == null) {
            return;
        }
        //网络请求
        ShineToolsApi.getUpdataFileZip(mActivity, mUpdateUrl, new StringCallback() {

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                callback.onBefore();
            }

            @Override
            public void onError(Call call, Exception e, int id) {

                String errorMsg = ShineToosApplication.getContext().getString(R.string.android_key2030);
                if (e != null) {
                    if (e instanceof SocketTimeoutException) {
                        errorMsg = ShineToosApplication.getContext().getString(R.string.android_key1853);
                    }
                    if (e instanceof ConnectException) {
                        errorMsg = ShineToosApplication.getContext().getString(R.string.android_key1922);
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
                    parserData(json, callback);
                }
            }


            @Override
            public void onAfter(int id) {
                super.onAfter(id);
            }
        });

    }


    private void parserData(String result, @NonNull FileCheckUpdataCallback callback) {
        try {
            mUpdateApp = new FileDownBean();
            JSONObject jsonResult = new JSONObject(result);
            int result1 = jsonResult.optInt("result", 0);
            if (result1 == 1) {
                JSONObject jsonObject1 = jsonResult.optJSONObject("obj");
                if (jsonObject1 == null) return;
                List<String> urls = new ArrayList<>();
                List<FileDownBean.DownFileBean> downFileBeans = new ArrayList<>();

                JSONObject threeObject = jsonObject1.optJSONObject(DeviceConstant.THREE_PHASE);
                parserFile(threeObject, urls, downFileBeans, 1, DeviceConstant.THREE_PHASE);

                JSONObject spaObject = jsonObject1.optJSONObject(DeviceConstant.SPH_SPA);
                parserFile(spaObject, urls, downFileBeans, 2, DeviceConstant.SPH_SPA);

                JSONObject singleObject = jsonObject1.optJSONObject(DeviceConstant.SINGLE_PHASE);
                parserFile(singleObject, urls, downFileBeans, 3, DeviceConstant.SINGLE_PHASE);


                JSONObject oldObject = jsonObject1.optJSONObject(DeviceConstant.S_MTL_S_TL3_S);
                parserFile(oldObject, urls, downFileBeans, 4, DeviceConstant.S_MTL_S_TL3_S);

                mUpdateApp.setUpdate(downFileBeans.size() > 0);
                mUpdateApp.setFile_urls(urls);
                mUpdateApp.setDownFileBeans(downFileBeans);

                if (mUpdateApp.isUpdate()) {
                    callback.hasNewVersion(mUpdateApp, this);
                } else {
                    callback.noNewVirsion(mActivity.getString(R.string.soft_update_no));
                }
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
            callback.onServerError();
        }
    }


    private void parserFile(JSONObject jsonObject, List<String> urls,
                            List<FileDownBean.DownFileBean> downFileBeans,
                            int type, String sort
    ) {
        if (jsonObject != null) {
            //循环遍历
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                //1.获取最新升级包下载地址
                String value = jsonObject.optString(key);
                //2.如果不为空 判断是否已经存在该升级包
                if (!TextUtils.isEmpty(value)) {
                    String name = value.substring(value.lastIndexOf("/"));

                    String sort_dir = sort.replaceAll(" ", "_")
                            .replaceAll("/", "_")
                            .replaceAll("-", "_");

                    String device_dir = key.replaceAll(" ", "_")
                            .replaceAll("/", "_")
                            .replaceAll("-", "_");


                    String filePath = fileDir + File.separator + sort_dir + File.separator + device_dir;
                    String fileParentPath = fileDir + File.separator + sort_dir + File.separator + device_dir + File.separator;


                    switch (type) {
                        case 1:
                            switch (key) {
                                case DeviceConstant.MOD_TL3_XH:
                                    UpgradePath.MOD_TL3_XH_PATH = fileParentPath;
                                    break;
                                case DeviceConstant.MAX_TL3LV_MV:
                                    UpgradePath.MAX_TL3LV_MV_PATH = fileParentPath;
                                    break;
                                case DeviceConstant.MOD_MID_MAC:
                                    UpgradePath.MOD_MID_MAC_PATH = fileParentPath;
                                    break;
                                case DeviceConstant.MAX_TL3_X_HV:
                                    UpgradePath.MAX_TL3_X_HV_PATH = fileParentPath;
                                    break;
                            }

                            break;
                        case 2:
                            switch (key) {
                                case DeviceConstant.SPH_TL_BL_US:
                                    UpgradePath.SPH_TL_BL_US_PATH = fileParentPath;
                                    break;
                                case DeviceConstant.SPA_TL3_BH:
                                    UpgradePath.SPA_TL3_BH_PATH = fileParentPath;
                                    break;
                                case DeviceConstant.SPH:
                                    UpgradePath.SPH_PATH = fileParentPath;
                                    break;
                                case DeviceConstant.SPH_TL3_BH:
                                    UpgradePath.SPH_TL3_BH_PATH = fileParentPath;
                                    break;
                                case DeviceConstant.SPA_TL_BL:
                                    UpgradePath.SPA_TL_BL_PATH = fileParentPath;
                                    break;
                            }
                            break;
                        case 3:
                            switch (key) {
                                case DeviceConstant.MIN_TL_XH:
                                    UpgradePath.MIN_TL_XH_PATH = fileParentPath;
                                    break;
                                case DeviceConstant.MIC_MIN_TL_X_XE:
                                    UpgradePath.MIC_MIN_TL_X_XE_PATH = fileParentPath;
                                    break;
                                case DeviceConstant.MIN_TL_XH_US:
                                    UpgradePath.MIN_TL_XH_US_PATH = fileParentPath;
                                    break;
                            }

                            break;
                        case 4:
                            if (DeviceConstant.S_MTL_S_TL3_S.equals(key)) {
                                UpgradePath._S_MIL_S_TL3_S_PATH = fileParentPath;
                            }
                            break;
                    }


                    File versionFile = new File(filePath, name);
                    if (!versionFile.exists()) {
                        //3.判断是否存在旧版，先清空，保证只有一个最新版本 节省内存
                        File fileParent = new File(fileParentPath);
                        if (fileParent.exists()) {
                            FileUtils.removeDir(fileParent);
                        } else {
                            fileParent.mkdirs();
                        }
                        urls.add(value);
                        FileDownBean.DownFileBean downFileBean = new FileDownBean.DownFileBean();
                        downFileBean.setDownUrl(value);
                        downFileBean.setFileName(name);
                        downFileBean.setSavePath(fileParent.getAbsolutePath());
                        downFileBeans.add(downFileBean);

                    }
                }
            }
        }
    }


    /**
     * 隐藏更新提示框
     */
    public void dissmissDialog() {
        if (dialogFragment != null) {
            dialogFragment.dismiss();
            dialogFragment = null;
        }
    }


    /**
     * 跳转到更新页面
     */
    TextView tvLoadTitle;
    ProgressBar pbProgress;
    TextView tvProgress;


    public void setProgress(float progress, int total, int current) {
        int current_progress = Math.round(progress * 100);

        if (tvLoadTitle != null) {
//            String title = mActivity.getString(R.string.init_install_pack) + "(" + current + "/" + total + ")";
//            tvLoadTitle.setText(title);
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
            progressDialog.dismiss();
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
        public FileUpdataManager.Builder setParams(Map<String, String> params) {
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
        public FileUpdataManager.Builder setIgnoreDefParams(boolean ignoreDefParams) {
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
        public FileUpdataManager.Builder setPost(boolean post) {
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
        public FileUpdataManager.Builder setTargetPath(String targetPath) {
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
        public FileUpdataManager.Builder setAppKey(String appKey) {
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
        public FileUpdataManager.Builder setActivity(Activity activity) {
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
        public FileUpdataManager.Builder setHttpManager(HttpManager httpManager) {
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
        public FileUpdataManager.Builder setUpdateUrl(String updateUrl) {
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
        public FileUpdataManager.Builder setThemeColor(int themeColor) {
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
        public FileUpdataManager.Builder setTopPic(int topPic) {
            mTopPic = topPic;
            return this;
        }


        public String getmFileDir() {
            return mFileDir;
        }

        public FileUpdataManager.Builder setmFileDir(String mFileDir) {
            this.mFileDir = mFileDir;
            return this;
        }

        public String getDatalogSn() {
            return datalogSn;
        }

        public FileUpdataManager.Builder setDatalogSn(String datalogSn) {
            this.datalogSn = datalogSn;
            return this;
        }

        /**
         * @return 生成app管理器
         */
        public FileUpdataManager build() {
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
            return new FileUpdataManager(this);
        }

        /**
         * 是否隐藏对话框下载进度条
         *
         * @return Builder
         */
        public FileUpdataManager.Builder hideDialogOnDownloading() {
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
        public FileUpdataManager.Builder showIgnoreVersion() {
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
        public FileUpdataManager.Builder dismissNotificationProgress() {
            dismissNotificationProgress = true;
            return this;
        }

        public boolean isDismissNotificationProgress() {
            return dismissNotificationProgress;
        }

        public FileUpdataManager.Builder setOnlyWifi() {
            mOnlyWifi = true;
            return this;
        }

        public boolean isOnlyWifi() {
            return mOnlyWifi;
        }

    }


}
