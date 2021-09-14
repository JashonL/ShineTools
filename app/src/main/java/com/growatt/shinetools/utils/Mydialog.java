package com.growatt.shinetools.utils;

import android.content.Context;

public class Mydialog {


    public static void Show(Context context) {
        DialogUtils.getInstance().showLoadingDialog(context);

    }

    public static void Dismiss() {
        DialogUtils.getInstance().closeLoadingDialog();
    }

}
