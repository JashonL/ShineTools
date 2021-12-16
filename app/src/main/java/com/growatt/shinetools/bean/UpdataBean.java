package com.growatt.shinetools.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class UpdataBean implements MultiItemEntity {
    private String deviceName;
    private String currentVersion;
    private int type;
    private boolean checked;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }


    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public int getItemType() {
        return type;
    }
}
