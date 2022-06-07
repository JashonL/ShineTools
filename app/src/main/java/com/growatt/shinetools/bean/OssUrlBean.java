package com.growatt.shinetools.bean;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class OssUrlBean extends RealmObject {

    @PrimaryKey
    private int primaryKey;
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "UrlBean{" +
                "primaryKey=" + primaryKey +
                ", url='" + url + '\'' +
                '}';
    }
}
