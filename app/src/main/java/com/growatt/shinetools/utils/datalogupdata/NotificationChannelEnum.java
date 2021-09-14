package com.growatt.shinetools.utils.datalogupdata;

import android.app.NotificationManager;

import com.growatt.shinetools.R;
import com.growatt.shinetools.ShineToosApplication;

/**
 * Created：2018/11/14 on 15:01
 * Author:gaideng on dg
 * Description:
 */

public enum NotificationChannelEnum {
    //重要通知
    MSG_IMPORT("channel_maintain", ShineToosApplication.getContext().getString(R.string.android_key588), NotificationManager.IMPORTANCE_MAX,88880),
    //app更新
    MSG_APP_UPDATE("channel_update", ShineToosApplication.getContext().getString(R.string.android_key588), NotificationManager.IMPORTANCE_MAX,88881),
    //报表
    MSG_MONTH_EXCEL("channel_report",ShineToosApplication.getContext().getString(R.string.android_key588), NotificationManager.IMPORTANCE_DEFAULT,88882),
    //故障
    MSG_WARM("channel_error",ShineToosApplication.getContext().getString(R.string.android_key588), NotificationManager.IMPORTANCE_HIGH,88883),
    //客服消息
    MSG_CUSTOM_QUESTION("channel_other",ShineToosApplication.getContext().getString(R.string.android_key226), NotificationManager.IMPORTANCE_DEFAULT,88884);
    private String id;
    private String name;
    private int important;
    private int notify_id;

    NotificationChannelEnum(String id, String name, int important, int notify_id) {
        this.id = id;
        this.name = name;
        this.important = important;
        this.notify_id = notify_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImportant() {
        return important;
    }

    public void setImportant(int important) {
        this.important = important;
    }

    public int getNotify_id() {
        return notify_id;
    }

    public void setNotify_id(int notify_id) {
        this.notify_id = notify_id;
    }
}
