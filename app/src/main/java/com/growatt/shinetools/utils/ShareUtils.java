package com.growatt.shinetools.utils;

import static com.blankj.utilcode.util.ActivityUtils.startActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.shinetools.R;
import com.growatt.shinetools.ShineToosApplication;
import com.growatt.shinetools.adapter.ScreenShootBaseAdapter;
import com.mylhyl.circledialog.CircleDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ShareUtils {
    public static final String authority =  AppUtils.getPackageName(ShineToosApplication.getContext()) + ".fileProvider";

    public static void share(FragmentActivity act, String picName,  View headerVeiw, View scrollView,
                             ScreenShootBaseAdapter adapter, boolean isShare) {
        new CircleDialog.Builder()
                .setTitle(act.getString(R.string.温馨提示))
                .setText(act.getString(R.string.m截图保存到相册))
                .setNegative(act.getString(R.string.all_no), v -> {
                    shareLongPicture(act, picName, headerVeiw, scrollView, adapter,
                            false, isShare);
                })
                .setPositive(act.getString(R.string.all_ok), v -> {
                    shareLongPicture(act, picName, headerVeiw,scrollView, adapter,
                            true, isShare);
                }).show(act.getSupportFragmentManager());
    }


    /**
     * 分享长图
     *
     * @param activity      需要分享的Activity
     * @param picName       分享图片名称
     * @param scrollview    滚动控件
     * @param isSaveToAlbum 是否保存到相册
     */

    public static void shareLongPicture(Activity activity, String picName,
                                        View headerView,
                                        View scrollview,
                                        ScreenShootBaseAdapter adapter,
                                        boolean isSaveToAlbum, boolean isShare) {
        String parentPath = ShineToosApplication.getContext().getFilesDir().getPath();
        File appDir = new File(parentPath, activity.getString(R.string.app_name) + "/screenshot");
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        File pictureFile = new File(appDir, picName);
        if (pictureFile.exists()) {
            pictureFile.delete();
        }

        //获取头部截图
        Bitmap bitmap = convertViewToBitmap(headerView);
        try {
            screenShotRecycleView(activity, (RecyclerView) scrollview, adapter, pictureFile, bitmap1 -> {
                try {
                    Bitmap bitmap3 = cropBitmap(bitmap1, bitmap);
                    Bitmap bitmap2 = combineBitmapsIntoOnlyOne(bitmap, bitmap3, activity);
                    Uri uri = savePic(activity, bitmap2, pictureFile);
                    if (isSaveToAlbum) {
                        // 其次把文件插入到系统图库
                        insertAlbum(activity, picName, pictureFile);
                    }
                    if (isShare) {
                        sharePic(uri);
                    } else {
                        bitmap1.recycle();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 截取scrollview的屏幕
     * 返回bitmap
     **/
    public static Uri getScrollViewBitmap(Activity activity, NestedScrollView scrollView, File picfile) {
        int h = 0;
        Bitmap bitmap;
        // 获取recyclerview实际高度
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
        }
        if (h > 12000) h = 12000;
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(scrollView.getWidth(), h,
                Bitmap.Config.ARGB_8888);
        //压缩图片
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        bitmap = ImagePathUtil.qualityCompress1(bitmap, cacheSize);
        final Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(ContextCompat.getColor(activity, R.color.white1));
        scrollView.draw(canvas);
        Uri uri = savePic(activity, bitmap, picfile);
        return uri;
    }


    // 保存到sdcard
    public static Uri savePic(Activity activity, Bitmap b, File strFileName) {
        Uri fileUri = null;

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(strFileName);
            b.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            fileUri = FileProvider.getUriForFile(
                    activity,
                    authority,
                    strFileName);
        } catch (IllegalArgumentException e) {
            Log.e("File Selector",
                    "The selected file can't be shared: " + strFileName.toString());
        }
        return fileUri;
    }

    /**
     * 将Bitmap插入到图库
     *
     * @param activity
     * @param picName
     * @param pictureFile
     */
    private static void insertAlbum(Activity activity, String picName, File pictureFile) throws Exception{
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    String insertImage = MediaStore.Images.Media.insertImage(activity.getContentResolver(),
                            pictureFile.getAbsolutePath(), picName, null);
                    //获得插入图库后的图片路径
                    String[] proj = {MediaStore.Images.Media.DATA};
                    Cursor cursor = activity.getContentResolver().query(Uri.parse(insertImage), proj, null, null, null);
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    String fileStr = cursor.getString(column_index);
                    cursor.close();
                    // 最后通知图库更新
                    File file = new File(fileStr);
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri uri = Uri.fromFile(file);
                    intent.setData(uri);
                    activity.sendBroadcast(intent);//这个广播的目的就是更新图库，发了这个广播进入相册就可以找到你保存的图片了！，记得要传你更新的file哦
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    public static void screenShotRecycleView(Activity activity,  RecyclerView recyclerview,
                                             ScreenShootBaseAdapter adapter, File picfile,
                                             RecycleViewRecCallback callBack) throws Exception{
        try {
            if (adapter != null && adapter.getItemCount() > 0) {
                final int oneScreenHeight = recyclerview.getMeasuredHeight();
                final Paint paint = new Paint();
                int headerSize = adapter.getHeaderLayoutCount();
                int footerSize = adapter.getFooterLayoutCount();
                int dataSize = adapter.getData().size();
                int count = headerSize + dataSize + footerSize;
                int shotHeight = 0;
                for (int i = 0; i < count; i++) {
//                    BaseViewHolder holder = (BaseViewHolder) adapter.createViewHolder(recyclerview, adapter.getItemViewType(i));
                    BaseViewHolder holder = adapter.onCreateViewHolder(recyclerview, adapter.getItemViewType(i));
                    if (i >= headerSize && i < count - footerSize - 1)
                        adapter.startConvert(holder, adapter.getData().get(i - headerSize));
                    holder.itemView.measure(
                            View.MeasureSpec.makeMeasureSpec(recyclerview.getWidth(), View.MeasureSpec.EXACTLY),
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                    holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight());
                    holder.itemView.setDrawingCacheEnabled(true);
                    holder.itemView.buildDrawingCache();
                    shotHeight += holder.itemView.getMeasuredHeight();
                }
                if (shotHeight > 12000) {
                    //设置截图最大值
                    shotHeight = 12000;
                }
                //返回到顶部
                while (recyclerview.canScrollVertically(-1)) {
                    recyclerview.scrollBy(0, -oneScreenHeight);
                }
                //绘制截图的背景
                final Bitmap bigBitmap = Bitmap.createBitmap(recyclerview.getMeasuredWidth(), shotHeight, Bitmap.Config.ARGB_8888);
                final Canvas bigCanvas = new Canvas(bigBitmap);
                Drawable lBackground = recyclerview.getBackground();
                if (lBackground instanceof ColorDrawable) {
                    ColorDrawable lColorDrawable = (ColorDrawable) lBackground;
                    int lColor = lColorDrawable.getColor();
                    bigCanvas.drawColor(lColor);
                }
                final int[] drawOffset = {0};
                final Canvas canvas = new Canvas();
                if (shotHeight <= oneScreenHeight) {
                    //仅有一页
                    Bitmap bitmap = Bitmap.createBitmap(recyclerview.getWidth(), recyclerview.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                    canvas.setBitmap(bitmap);
                    recyclerview.draw(canvas);
                    if (callBack != null)
                        callBack.onRecFinished(bitmap);

                } else {
                    //超过一页
                    final int finalShotHeight = shotHeight;
                    recyclerview.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if ((drawOffset[0] + oneScreenHeight <= finalShotHeight)) {
                                //超过一屏
                                Bitmap bitmap = Bitmap.createBitmap(recyclerview.getWidth(), oneScreenHeight, Bitmap.Config.ARGB_8888);
                                canvas.setBitmap(bitmap);
                                recyclerview.draw(canvas);
                                bigCanvas.drawBitmap(bitmap, 0, drawOffset[0], paint);
                                drawOffset[0] += oneScreenHeight;
                                //计算滚动的距离
                                if ((drawOffset[0] + oneScreenHeight <= finalShotHeight)) {
                                    recyclerview.scrollBy(0, oneScreenHeight);
                                }

                                try {
                                    bitmap.recycle();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                                recyclerview.postDelayed(this, 10);
                            } else {
                                //不足一屏时的处理
                                int leftHeight = finalShotHeight - drawOffset[0];
                                recyclerview.scrollBy(0, leftHeight);
                                int top = oneScreenHeight - (finalShotHeight - drawOffset[0]);
                                if (top > 0 && leftHeight > 0) {
                                    Bitmap bitmap = Bitmap.createBitmap(recyclerview.getWidth(), recyclerview.getHeight(), Bitmap.Config.ARGB_8888);
                                    canvas.setBitmap(bitmap);
                                    recyclerview.draw(canvas);
                                    //截图,只要补足的那块图
                                    bitmap = Bitmap.createBitmap(bitmap, 0, top, bitmap.getWidth(), leftHeight, null, false);
                                    bigCanvas.drawBitmap(bitmap, 0, drawOffset[0], paint);
                                    try {
                                        bitmap.recycle();
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                                if (callBack != null)
                                    callBack.onRecFinished(bigBitmap);
                            }
                        }
                    }, 10);
                }
            } else {
                Bitmap bigBitmap = takeScreenShot(activity, picfile);
                if (callBack != null)
                    callBack.onRecFinished(bigBitmap);
            }
        } catch (OutOfMemoryError e) {
            MyToastUtils.toast(R.string.m262保存图片失败);
            e.printStackTrace();
        }
    }


    public interface RecycleViewRecCallback {
        void onRecFinished(Bitmap bitmap);
    }


    // 获取指定Activity的截屏，保存到png文件
    public static Bitmap takeScreenShot(Activity activity, File picpath) {
        // View是你需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();

    /*    // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;*/

        // 获取屏幕长和高
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;
        // 去掉标题栏
        Bitmap b = Bitmap.createBitmap(b1, 0, 0, width, height);
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // 压缩图片
        final int cacheSize = maxMemory / 8;
        b = ImagePathUtil.qualityCompress1(b, cacheSize);
        Canvas canvas = new Canvas(b);
        canvas.drawColor(ContextCompat.getColor(activity, R.color.white1));
        view.draw(canvas);
        view.setDrawingCacheEnabled(false);
        view.destroyDrawingCache();
        savePic(activity,b, picpath);
        return b;
    }


    /**
     * 使用友盟分享图片
     *
     * @param bitmap    分享的bitmap对象
     */

    private static void sharePic(Uri bitmap) throws Exception{
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        // 比如发送文本形式的数据内容
// 指定发送的内容
//        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
// 指定发送内容的类型
        sendIntent.setType("text/plain");
        // 比如发送二进制文件数据流内容（比如图片、视频、音频文件等等）
// 指定发送的内容 (EXTRA_STREAM 对于文件 Uri )
        sendIntent.putExtra(Intent.EXTRA_STREAM, bitmap);
// 指定发送内容的类型 (MIME type)
        sendIntent.setType("image/jpeg");

        startActivity(Intent.createChooser(sendIntent, "share too"));
    }



    public static Bitmap convertViewToBitmap(View view){

        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();  //启用DrawingCache并创建位图
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache()); //创建一个DrawingCache的拷贝，因为DrawingCache得到的位图在禁用后会被回收
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }



    /**
     * @param pic1 图一
     * @param pic2 图二
     * @return only_bitmap
     */
    public static Bitmap combineBitmapsIntoOnlyOne(Bitmap pic1, Bitmap pic2, Activity context) throws Exception{
        // 获取屏幕长和高
        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int w_total = metrics.widthPixels;
//        int w_total = pic2.getWidth();
        int h_total = pic1.getHeight() + pic2.getHeight();
        int h_pic1 = pic1.getHeight();
        Bitmap only_bitmap = Bitmap.createBitmap(w_total, h_total, Bitmap.Config.ARGB_8888);

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;
        only_bitmap = ImagePathUtil.qualityCompress1(only_bitmap, cacheSize);
        Canvas canvas = new Canvas(only_bitmap);
        canvas.drawColor(ContextCompat.getColor(context, R.color.white1));
        canvas.drawBitmap(pic1, 0, 0, null);
        canvas.drawBitmap(pic2, 0, h_pic1, null);
        return only_bitmap;
    }



    /**
     * 裁剪
     *
     * @param bitmap 原图
     * @return 裁剪后的图像
     */
    private static Bitmap cropBitmap(Bitmap bitmap,Bitmap pic1) throws Exception {

        int height = pic1.getHeight();

        int cropWidth =bitmap.getWidth();
        int cropHeight = bitmap.getHeight()-pic1.getHeight();
        return Bitmap.createBitmap(bitmap, 0, height, cropWidth, cropHeight, null, false);
    }

}
