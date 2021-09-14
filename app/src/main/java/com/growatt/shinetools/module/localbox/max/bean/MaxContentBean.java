package com.growatt.shinetools.module.localbox.max.bean;

import androidx.annotation.NonNull;

/**
 * Created by dg on 2017/10/25.
 */

public class MaxContentBean {
    //文本内容
    private String text;
    //文本颜色状态
    private int status;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @NonNull
    @Override
    public String toString() {
        return "[文本内容: text=]"+text +"文本状态：status="+status+"]";
    }
}
