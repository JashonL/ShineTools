package com.growatt.shinetools.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.growatt.shinetools.R;

public class DialogUtils {
    public static DialogUtils mInstance;
    private static AlertDialog dlg;
    private static AlertDialog loginDlg;
    private static Animation operatingAnim;
    private static ImageView imageView;
    private static boolean isLoading;

    private DialogUtils() {
    }

    /**
     * 加载话框
     *
     * @param context
     */
    public void showLoadingDialog(Context context) {
        if(null == context || ((Activity)context).isFinishing())return;
        if(isLoading())return;
        dlg = new AlertDialog.Builder(context, R.style.dialogStyle).create();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
        Window window = dlg.getWindow();
        if(null != window){
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setWindowAnimations(R.style.dialogWindowAnim);
        }
        dlg.show();
        dlg.setCancelable(false);
        dlg.setCanceledOnTouchOutside(false);
        dlg.setContentView(view);
        isLoading = true;
    }


    /**
     * 关闭拨打电话返回信息对话框
     */
    public void closeCallMsgDialog() {
        if (dlg != null) {
            dlg.dismiss();
        }
    }

    /**
     * 加载状态
     */
    public boolean isLoading() {
        return isLoading;
    }

    /**
     * 开始旋转
     */
    public void openAnim() {
        if (operatingAnim != null) {
            imageView.startAnimation(operatingAnim);
        }
    }

    /**
     * 停止旋转
     */
    public void closeAnim() {
        if (operatingAnim != null) {
            imageView.clearAnimation();
        }
    }

    /**
     * 关闭请求对话框
     */
    public void closeLoadingDialog() {
        if (dlg != null) {
            closeAnim();
            try {
                dlg.dismiss();
            }catch (Throwable ignored){

            }
        }
        isLoading = false;
    }

    public interface PressCallBack {
        void onPressButton(int buttonIndex);
    }

    /**
     * 单一实例
     *
     * @return
     */
    public static DialogUtils getInstance() {
        if (mInstance == null) {
            synchronized (DialogUtils.class) {
                if (mInstance == null) {
                    mInstance = new DialogUtils();
                    return mInstance;
                }
            }
        }
        return mInstance;
    }


}
