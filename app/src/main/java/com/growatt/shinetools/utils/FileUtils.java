package com.growatt.shinetools.utils;

import com.blankj.utilcode.util.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileUtils {


    /**
     * 清空一个文件夹
     */
    public static void removeDir(File dir) {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isDirectory()) {
                removeDir(file);
            } else {
                file.delete();
            }
        }
    }


    /**
     * 解压zip
     */
    public static List<File> unzip(String path, String target) throws IOException {
        return ZipUtils.unzipFile(path, target);
    }



}
