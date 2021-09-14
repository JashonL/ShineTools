package com.growatt.shinetools.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class AppUtils {

    public static final String APP_USE_LOG_TIME0="freq_0";
    public static final String APP_USE_LOG_TIME_LONG0="time_0";

    public static final String APP_USE_LOG_TIME1="freq_1";
    public static final String APP_USE_LOG_TIME_LONG1="time_1";

    public static final String APP_USE_LOG_TIME1_2="freq_1_2";
    public static final String APP_USE_LOG_TIME_LONG1_2="time_1_2";


    public static final String APP_USE_LOG_TIME2="freq_2";
    public static final String APP_USE_LOG_TIME_LONG2="time_2";

    public static final String APP_USE_LOG_TIME3="freq_3";
    public static final String APP_USE_LOG_TIME_LONG3="time_3";
    public static final String APP_USE_LOG_TIME3_1="freq_3_1";
    public static final String APP_USE_LOG_TIME_LONG3_1="time_3_1";
    public static final String APP_USE_LOG_TIME3_2="freq_3_2";
    public static final String APP_USE_LOG_TIME_LONG3_2="time_3_2";
    public static final String APP_USE_LOG_TIME3_3="freq_3_3";
    public static final String APP_USE_LOG_TIME_LONG3_3="time_3_3";
    public static final String APP_USE_LOG_TIME3_4="freq_3_4";
    public static final String APP_USE_LOG_TIME_LONG3_4="time_3_4";

    public static final String APP_USE_LOG_TIME4="freq_4";
    public static final String APP_USE_LOG_TIME_LONG4="time_4";
    public static final String APP_USE_LOG_TIME4_1="freq_4_1";
    public static final String APP_USE_LOG_TIME_LONG4_1="time_4_1";
    public static final String APP_USE_LOG_TIME4_2="freq_4_2";
    public static final String APP_USE_LOG_TIME_LONG4_2="time_4_2";
    public static final String APP_USE_LOG_TIME4_3="freq_4_3";
    public static final String APP_USE_LOG_TIME_LONG4_3="time_4_3";
    public static final String APP_USE_LOG_TIME4_4="freq_4_4";
    public static final String APP_USE_LOG_TIME_LONG4_4="time_4_4";


    public static final String APP_USE_LOG_TIME5="freq_5";
    public static final String APP_USE_LOG_TIME_LONG5="time_5";
    public static final String APP_USE_LOG_TIME5_1="freq_5_1";
    public static final String APP_USE_LOG_TIME_LONG5_1="time_5_1";
    public static final String APP_USE_LOG_TIME5_2="freq_5_2";
    public static final String APP_USE_LOG_TIME_LONG5_2="time_5_2";
    public static final String APP_USE_LOG_TIME5_3="freq_5_3";
    public static final String APP_USE_LOG_TIME_LONG5_3="time_5_3";
    public static final String APP_USE_LOG_TIME5_4="freq_5_4";
    public static final String APP_USE_LOG_TIME_LONG5_4="time_5_4";
    public static final String APP_USE_LOG_TIME5_5="freq_5_5";
    public static final String APP_USE_LOG_TIME_LONG5_5="time_5_5";
    public static final String APP_USE_LOG_TIME5_6="freq_5_6";
    public static final String APP_USE_LOG_TIME_LONG5_6="time_5_6";
    public static final String APP_USE_LOG_TIME5_7="freq_5_7";
    public static final String APP_USE_LOG_TIME_LONG5_7="time_5_7";
    public static final String APP_USE_LOG_TIME5_8="freq_5_8";
    public static final String APP_USE_LOG_TIME_LONG5_8="time_5_8";


    public static final String APP_USE_LOG_TIME6="freq_6";
    public static final String APP_USE_LOG_TIME_LONG6="time_6";
    public static final String APP_USE_LOG_TIME6_1="freq_6_1";
    public static final String APP_USE_LOG_TIME_LONG6_1="time_6_1";
    public static final String APP_USE_LOG_TIME6_2="freq_6_2";
    public static final String APP_USE_LOG_TIME_LONG6_2="time_6_2";
    public static final String APP_USE_LOG_TIME6_3="freq_6_3";
    public static final String APP_USE_LOG_TIME_LONG6_3="time_6_3";


    public static final String APP_USE_LOG_NAME_KEY="accountName";
    public static final String APP_USE_LOG_PLATFORM_KEY="platform";
    public static final String APP_USE_LOG_SERVER_KEY="server";

    private static String s;
    public static int a = 600;



    public static int getScreenWidth(Activity act) {
        return getScreen(act).widthPixels;
    }

    public static int getScreenHeight(Activity act) {
        return getScreen(act).heightPixels;
    }

    public static DisplayMetrics getScreen(Activity act) {
        DisplayMetrics dm = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    public static String getVersion(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return "1.0";
    }


    public static String getCurrentTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        return createGmtOffsetString(true, true, tz.getRawOffset());
    }

    public static String createGmtOffsetString(boolean includeGmt,
                                               boolean includeMinuteSeparator, int offsetMillis) {
        int offsetMinutes = offsetMillis / 60000;
        char sign = '+';
        if (offsetMinutes < 0) {
            sign = '-';
            offsetMinutes = -offsetMinutes;
        }
        StringBuilder builder = new StringBuilder(9);
        if (includeGmt) {
            builder.append("GMT");
        }
        builder.append(sign);
        appendNumber(builder, 2, offsetMinutes / 60);
        if (includeMinuteSeparator) {
            builder.append(':');
        }
        appendNumber(builder, 2, offsetMinutes % 60);
        return builder.toString();
    }

    private static void appendNumber(StringBuilder builder, int count, int value) {
        String string = Integer.toString(value);
        for (int i = 0; i < count - string.length(); i++) {
            builder.append('0');
        }
        builder.append(string);
    }


    public static long newtime;

    public static Map<String, Object> Timemap(long l, long ll) {
        Map<String, Object> map = new HashMap<String, Object>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        newtime = l + ll;
        String sd = sdf.format(new Date(l + ll));
        System.out.println(sd);
        map.put("year", sd.substring(0, 4));
        map.put("month", sd.substring(5, 7));
        map.put("day", sd.substring(8, sd.length()));
        return map;
    }


    public static String validateWebbox(String serialNum) {
        if (serialNum == null || "".equals(serialNum.trim())) {
            return "";
        }
        byte[] snBytes = serialNum.getBytes();
        int sum = 0;
        for (byte snByte : snBytes) {
            sum += snByte;
        }
        int B = sum % 8;
        String text = Integer.toHexString(sum * sum);
        int length = text.length();
        String resultTemp = text.substring(0, 2) + text.substring(length - 2, length) + B;
        String result = "";
        char[] charArray = resultTemp.toCharArray();
        for (char c : charArray) {
            if (c == 0x30 || c == 0x4F || c == 0x6F) {
                c++;
            }
            result += c;
        }
        return result.toUpperCase();
    }


    public static String getFormat(String s) {
        if ("0".equals(s)) {
            return "0";
        }
        if (TextUtils.isEmpty(s)) {
            return "0";
        }
        if (s.contains(",")) {
            s.replace(",", ".");
        }
        try {
            double d = ((int) (Double.parseDouble(s) * 100 + 0.5)) / 100.0;
            return d + "";
        } catch (Exception e) {
            e.printStackTrace();
            return s;
        }
    }

    public static Bitmap comp(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 90, baos);

        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();

        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;

        float hh = 800f;
        float ww = 480f;

        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;

        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        if (isBm != null) {
            try {
                isBm.close();
                isBm = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (image != null && !image.isRecycled()) {
            image.recycle();
            image = null;
        }
        System.gc();
        return compressImage(bitmap, a);
    }

    private static Bitmap compressImage(Bitmap image, int in) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        int options = 90;
        while (baos.toByteArray().length / 1024 > in || options == 60) {
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 5;
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        if (image != null && !image.isRecycled()) {
            image.recycle();
            image = null;
        }
        if (isBm != null) {
            try {
                isBm.close();
                isBm = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.gc();
        return bitmap;
    }


    public static boolean deleteSDFile(File file) {

        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f : files) {
                    deleteSDFile(f);

                }
            }
            file.delete();
        }
        return true;
    }


    public static double getFileOrFilesSize(String filePath, int sizeType) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("��ȡ�ļ���С", "��ȡʧ��!");
        }
        return FormetFileSize(blockSize, sizeType);
    }



    public static String getAutoFileOrFilesSize(String filePath) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return FormetFileSize(blockSize);
    }


    private static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
            if (fis != null) {
                fis.close();
            }
        } else {
            file.createNewFile();
            Log.e("��ȡ�ļ���С", "�ļ�������!");
        }
        return size;
    }


    private static long getFileSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }


    private static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }


    private static double FormetFileSize(long fileS, int sizeType) {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        switch (sizeType) {
            case SIZETYPE_B:
                fileSizeLong = Double.valueOf(df.format((double) fileS));
                break;
            case SIZETYPE_KB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1024));
                break;
            case SIZETYPE_MB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1048576));
                break;
            case SIZETYPE_GB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1073741824));
                break;
            default:
                break;
        }
        return fileSizeLong;
    }

    public static final int SIZETYPE_B = 1;
    public static final int SIZETYPE_KB = 2;
    public static final int SIZETYPE_MB = 3;
    public static final int SIZETYPE_GB = 4;


    public static Map<String, Object> toHashMap(String json) {
        JSONObject jsonObject;
        Map<String, Object> data = new HashMap<String, Object>();
        try {
            jsonObject = new JSONObject(json);
            Iterator it = jsonObject.keys();
            while (it.hasNext()) {
                String key = String.valueOf(it.next());
                String value;
                value = (String) jsonObject.get(key);
                data.put(key, value);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return data;
    }




    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;

    }


    public static SimpleDateFormat sdf = null;

    public static String getVideoDuration(String mUri) {
        String duration = null;
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
        try {
            if (mUri != null) {
                mmr.setDataSource(mUri);
            }
            duration = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
            if (duration != null) {
                if (sdf == null) {
                    sdf = new SimpleDateFormat("mm:ss");
                }
                duration = sdf.format(Double.parseDouble(duration));
            }
        } catch (Exception ex) {
        } finally {
            mmr.release();
        }
        return duration;
    }


    public static List<String> getFilesList(String Path, String Extension,
                                            boolean IsIterative) // ����Ŀ¼����չ��(�жϵ��ļ����͵ĺ�׺��)���Ƿ�������ļ���
    {
        List<String> list = new ArrayList<String>(); // ��� List
        File[] files = new File(Path).listFiles();
        if (files == null) return list;
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            if (f.isFile()) {
                if (f.getPath()
                        .substring(f.getPath().length() - Extension.length())
                        .equals(Extension)) // �ж���չ��
                    list.add(f.getPath());
                if (!IsIterative)
                    break;  //����������Ӽ�Ŀ¼������
            } else if (f.isDirectory() && f.getPath().indexOf("/.") == -1) // ���Ե��ļ��������ļ�/�ļ��У�
                getFilesList(f.getPath(), Extension, IsIterative);  //����Ϳ�ʼ�ݹ���
        }
        return list;
    }


    public static Bitmap getVideoThumbnail(String videoPath, int width, int height,
                                           int kind) {
        Bitmap bitmap = null;
        // ��ȡ��Ƶ������ͼ  
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }


    public static boolean sdcardIsExist() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * [获取应用程序包名称信息]
     *
     * @param context
     * @return 当前应用的包名
     */
    public static synchronized String getPackageName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.packageName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断应用是否已经启动 * * @param context 上下文对象 * @param packageName 要判断应用的包名 * @return boolean
     */
    public static boolean isAppAlive(Context context, String packageName) {
        try {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();
            for (int i = 0; i < processInfos.size(); i++) {
                if (processInfos.get(i).processName.equals(packageName)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断本应用是否已经位于最前端
     *
     * @param context
     * @return 本应用已经位于最前端时，返回 true；否则返回 false
     */
    public static boolean isRunningForeground(Context context) {
        try {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appProcessInfoList = activityManager.getRunningAppProcesses();
            String processName = context.getApplicationInfo().processName;
            /**枚举进程*/
            for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfoList) {
                if (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    if (appProcessInfo.processName.equals(processName)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }








}

