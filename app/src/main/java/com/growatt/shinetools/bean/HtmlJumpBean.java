package com.growatt.shinetools.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created：2019/4/17 on 20:30
 * Author:gaideng on dg
 * Description:
 * title 标题
 * url 网址
 */

public class HtmlJumpBean implements Parcelable {
    private String title;
    private String url;
    private boolean flag;


    public HtmlJumpBean(String title, String url, boolean flag) {
        this.title = title;
        this.url = url;
        this.flag=flag;
    }

    public HtmlJumpBean() {
    }


    protected HtmlJumpBean(Parcel in) {
        title = in.readString();
        url = in.readString();
        flag = in.readByte() != 0;
    }

    public static final Creator<HtmlJumpBean> CREATOR = new Creator<HtmlJumpBean>() {
        @Override
        public HtmlJumpBean createFromParcel(Parcel in) {
            return new HtmlJumpBean(in);
        }

        @Override
        public HtmlJumpBean[] newArray(int size) {
            return new HtmlJumpBean[size];
        }
    };

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(url);
        dest.writeByte((byte) (flag ? 1 : 0));
    }
}
