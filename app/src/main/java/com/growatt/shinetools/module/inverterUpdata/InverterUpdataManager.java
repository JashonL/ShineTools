package com.growatt.shinetools.module.inverterUpdata;

public class InverterUpdataManager {

    private static InverterUpdataManager mInstance = null;

    private InverterUpdataManager() {
    }

    public static InverterUpdataManager getInstance() {
        if (mInstance == null) {
            synchronized (InverterUpdataManager.class) {
                if (mInstance == null) {
                    mInstance = new InverterUpdataManager();
                }
            }
        }
        return mInstance;
    }



    //检测升级
    public boolean checkUpdata(int nowVersion){

        return false;
    }




    //去升级,下发升级文件





}
