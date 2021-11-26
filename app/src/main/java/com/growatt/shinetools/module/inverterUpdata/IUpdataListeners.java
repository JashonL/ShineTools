package com.growatt.shinetools.module.inverterUpdata;

public interface IUpdataListeners {

    void updataStart(String msg);

    void updataProgress(int total,int current,int progress);

    void updataFail(String msg);

    void updataEnd();

}
