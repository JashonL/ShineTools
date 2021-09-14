package com.growatt.shinetools.utils.datalogupdata;


import java.util.List;

public class DatalogUpDateBean {

    //是否有新版本
    private boolean update;
    //新版本号
    private String new_version;
    //新app下载地址
    private String apk_file_url;
    //需要下载的文件链接集合
    private List<String> file_urls;

    //更新日志
    private String update_log;
    //配置默认更新dialog 的title
    private String update_def_dialog_title;
    //新app大小
    private String target_size;
    //是否强制更新
    private boolean constraint;
    //md5
    private String new_md5;
    //是否增量 暂时不用
    private boolean delta;
    //服务器端的原生返回数据（json）,方便使用者在hasNewApp自定义渲染dialog的时候可以有别的控制，比如：#issues/59
    private String origin_res;
    //下载的文件类
    private List<DownFileBean> downFileBeans;
    /**********以下是内部使用的数据**********/

    //网络工具，内部使用
    private HttpManager httpManager;
    private String targetPath;
    private boolean mHideDialog;
    private boolean mShowIgnoreVersion;
    private boolean mDismissNotificationProgress;
    private boolean mOnlyWifi;




    public String getNew_version() {
        return new_version;
    }

    public void setNew_version(String new_version) {
        this.new_version = new_version;
    }

    public String getApk_file_url() {
        return apk_file_url;
    }

    public void setApk_file_url(String apk_file_url) {
        this.apk_file_url = apk_file_url;
    }

    public String getUpdate_log() {
        return update_log;
    }

    public void setUpdate_log(String update_log) {
        this.update_log = update_log;
    }

    public String getUpdate_def_dialog_title() {
        return update_def_dialog_title;
    }

    public void setUpdate_def_dialog_title(String update_def_dialog_title) {
        this.update_def_dialog_title = update_def_dialog_title;
    }

    public String getTarget_size() {
        return target_size;
    }

    public void setTarget_size(String target_size) {
        this.target_size = target_size;
    }

    public boolean isConstraint() {
        return constraint;
    }

    public void setConstraint(boolean constraint) {
        this.constraint = constraint;
    }

    public String getNew_md5() {
        return new_md5;
    }

    public void setNew_md5(String new_md5) {
        this.new_md5 = new_md5;
    }

    public boolean isDelta() {
        return delta;
    }

    public void setDelta(boolean delta) {
        this.delta = delta;
    }

    public String getOrigin_res() {
        return origin_res;
    }

    public void setOrigin_res(String origin_res) {
        this.origin_res = origin_res;
    }

    public HttpManager getHttpManager() {
        return httpManager;
    }

    public void setHttpManager(HttpManager httpManager) {
        this.httpManager = httpManager;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    public boolean ismHideDialog() {
        return mHideDialog;
    }

    public void setmHideDialog(boolean mHideDialog) {
        this.mHideDialog = mHideDialog;
    }

    public boolean ismShowIgnoreVersion() {
        return mShowIgnoreVersion;
    }

    public void setmShowIgnoreVersion(boolean mShowIgnoreVersion) {
        this.mShowIgnoreVersion = mShowIgnoreVersion;
    }

    public boolean ismDismissNotificationProgress() {
        return mDismissNotificationProgress;
    }

    public void setmDismissNotificationProgress(boolean mDismissNotificationProgress) {
        this.mDismissNotificationProgress = mDismissNotificationProgress;
    }

    public boolean ismOnlyWifi() {
        return mOnlyWifi;
    }

    public void setmOnlyWifi(boolean mOnlyWifi) {
        this.mOnlyWifi = mOnlyWifi;
    }


    public List<String> getFile_urls() {
        return file_urls;
    }

    public void setFile_urls(List<String> file_urls) {
        this.file_urls = file_urls;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public boolean isUpdate() {
        return update;
    }

    public List<DownFileBean> getDownFileBeans() {
        return downFileBeans;
    }

    public void setDownFileBeans(List<DownFileBean> downFileBeans) {
        this.downFileBeans = downFileBeans;
    }



    public static class DownFileBean{
        private String downUrl;
        private String savePath;
        private String fileName;

        public String getDownUrl() {
            return downUrl;
        }

        public void setDownUrl(String downUrl) {
            this.downUrl = downUrl;
        }

        public String getSavePath() {
            return savePath;
        }

        public void setSavePath(String savePath) {
            this.savePath = savePath;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
    }

}
