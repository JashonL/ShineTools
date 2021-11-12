package com.growatt.shinetools.module.localbox.tlxh.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created：2019/1/3 on 19:35
 * Author:gaideng on dg
 * Description:离网参数bean
 */

public class TLXHLiwangBean implements MultiItemEntity {
    private String title;
    private String rContent;
    private String sContent;
    private String tContent;
    private int type;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getrContent() {
        return rContent;
    }

    public void setrContent(String rContent) {
        this.rContent = rContent;
    }

    public String getsContent() {
        return sContent;
    }

    public void setsContent(String sContent) {
        this.sContent = sContent;
    }

    public String gettContent() {
        return tContent;
    }

    public void settContent(String tContent) {
        this.tContent = tContent;
    }

    @Override
    public int getItemType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
