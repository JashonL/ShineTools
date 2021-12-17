package com.growatt.shinetools.module.inverterUpdata;

public interface IUpdataListeners {

    //准备中
    void preparing(int total, int current);
    //下发文件
    void sendFileProgress(int total,int current,int progress);
    //查询升级进度
    void updataUpdataProgress(int total,int current,int progress);

    void updataFail(String msg);

    void updataSuccess();

}
