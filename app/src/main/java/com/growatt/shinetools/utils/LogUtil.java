package com.growatt.shinetools.utils;

public class LogUtil {



    public static String customTagPrefix = "x_log";

    private LogUtil() {
    }



    public static void d(String content) {
       Log.d(content);
    }



    public static void i(String content) {
        Log.i(content);
    }


    public static void e(String content) {
        Log.e(content);
    }



}
