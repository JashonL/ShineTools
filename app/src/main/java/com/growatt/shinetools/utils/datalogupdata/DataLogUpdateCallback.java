package com.growatt.shinetools.utils.datalogupdata;

public class DataLogUpdateCallback {



    /**
     * 有新版本
     *
     * @param updateApp        新版本信息
     * @param updateAppManager app更新管理器
     */
    protected void hasNewVersion(DatalogUpDateBean updateApp, DatalogUpdataManager updateAppManager) {
        updateAppManager.showDialogFragment();
    }

    /**
     * 网路请求之后
     */
    protected void onAfter() {
    }


    /**
     * 没有新版本
     * @param error HttpManager实现类请求出错返回的错误消息，交给使用者自己返回，有可能不同的应用错误内容需要提示给客户
     */
    protected void noNewVirsion(String error) {
    }

    /**
     * 网络请求之前
     */
    protected void onBefore() {
    }

    /**
     * 网络请求失败
     */
    protected void onServerError() {
    }

}
