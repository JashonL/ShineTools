package com.growatt.shinetools.module.localbox.tlx.bean;

/**
 * Created：2019/10/28 on 17:13
 * Author:gaideng on administratorr
 * Description:
 */

public class TLXParamCountryBean {
    private boolean isSave;//是否需要保存
    private String modelTitle;//需要保存对应的安规标识
    private int index;//需要保存对应的下标
    private String country;
    private String model;//当前安规

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public boolean isSave() {
        return isSave;
    }

    public void setSave(boolean save) {
        isSave = save;
    }

    public String getModelTitle() {
        return modelTitle;
    }

    public void setModelTitle(String modelTitle) {
        this.modelTitle = modelTitle;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
