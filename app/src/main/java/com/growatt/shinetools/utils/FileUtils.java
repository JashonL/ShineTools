package com.growatt.shinetools.utils;

import java.io.File;

public class FileUtils {


    /**
     * 清空一个文件夹
     */
    public static void removeDir(File dir){
        File[] files = dir.listFiles();
        if (files==null)return;
        for (File file:files){
            if (file.isDirectory()){
                removeDir(file);
            }else {
                file.delete();
            }
        }
    }






}
