package com.growatt.shinetools.module.localbox.max.bean;

import java.util.List;

/**
 * Created：2020/5/19 on 16:55
 * Author:gaideng on admin
 * Description:
 */
public class ProtectParamResultBean {

    /**
     * result : 1
     * obj : [{"content":"","unit":"V","title":"一级过压保护点","value":"280.0"},{"content":"","unit":"ms","title":"一级过压保护时间","value":"2000"},{"content":"","unit":"V","title":"二级过压保护点","value":"297.0"},{"content":"","unit":"ms","title":"二级过压保护时间","value":"50"},{"content":"","unit":"V","title":"一级欠压保护点","value":"187.0"},{"content":"","unit":"ms","title":"一级欠压保护时间","value":"2000"},{"content":"","unit":"V","title":"二级欠压保护点","value":"110.0"},{"content":"","unit":"ms","title":"二级欠压保护时间","value":"100"},{"content":"","unit":"Hz","title":"一级过频保护点","value":"50.20"},{"content":"","unit":"ms","title":"一级过频保护时间","value":"120000"},{"content":"","unit":"Hz","title":"二级过频保护点","value":"50.50"},{"content":"","unit":"ms","title":"二级过频保护时间","value":"200"},{"content":"","unit":"Hz","title":"一级欠频保护点","value":"49.50"},{"content":"","unit":"ms","title":"一级欠频保护时间","value":"600000"},{"content":"","unit":"Hz","title":"二级欠频保护点","value":"48.00"},{"content":"","unit":"ms","title":"二级欠频保护时间","value":"200"}]
     * msg :
     */

    private int result;
    private String msg;
    private List<ProtectParamBean> obj;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<ProtectParamBean> getObj() {
        return obj;
    }

    public void setObj(List<ProtectParamBean> obj) {
        this.obj = obj;
    }
}
