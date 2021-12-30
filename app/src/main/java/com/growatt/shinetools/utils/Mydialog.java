package com.growatt.shinetools.utils;

import android.content.Context;
import android.os.Handler;

public class Mydialog {

    private static final int DEFAULT_DELAY_MILLIS = 15000;
    private static Handler mDelayHandler = new Handler();
    private static Runnable runnableDelay = Mydialog::Dismiss;

    public static void Show(Context context) {
        DialogUtils.getInstance().showLoadingDialog(context);
        delayDismissDialog(DEFAULT_DELAY_MILLIS);
    }

    public static void Dismiss() {
        DialogUtils.getInstance().closeLoadingDialog();
        delayDismissDialog(DEFAULT_DELAY_MILLIS);
    }


    public static void delayDismissDialog(long delayMillis) {
        mDelayHandler.removeCallbacks(runnableDelay);
        mDelayHandler.postDelayed(runnableDelay,delayMillis);
    }

}
