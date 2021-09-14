package com.growatt.shinetools.utils;

import com.hjq.toast.ToastUtils;

public class MyToastUtils {

    public static void toast(Object object) {
        ToastUtils.show(object);
    }

    public static void toast(int stringId){
        ToastUtils.show(stringId);
    }

}
