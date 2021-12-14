package com.growatt.shinetools.module.inverterUpdata;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.growatt.shinetools.R;
import com.growatt.shinetools.utils.CircleDialogUtils;
import com.growatt.shinetools.utils.datalogupdata.UpdateDatalogUtils;
import com.mylhyl.circledialog.BaseCircleDialog;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class InverterUpdataManager {

    private static Context context;

    private FileUpdataSend fileUpdataSend;

    private static InverterUpdataManager mInstance = null;

    private InverterUpdataManager(Context context) {
        this.context = context;
    }

    public static InverterUpdataManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (InverterUpdataManager.class) {
                if (mInstance == null) {
                    mInstance = new InverterUpdataManager(context);
                }
            }
        }
        return mInstance;
    }


    //检测升级
    public boolean checkUpdata(int nowVersion) {

        return false;
    }


    //去升级,下发升级文件

    //检测升级
    public void updata() {
        try {
//                            List<ByteBuffer> fileByte1 = UpdateDatalogUtils.getFileByte2(this, "IFAB01_20200728.hex");
//                            List<ByteBuffer> fileByte2 = UpdateDatalogUtils.getFileByte2(this, "UEAA-03.hex");
//                            List<ByteBuffer> fileByte3 = UpdateDatalogUtils.getFileByte2(this, "ZACA-03.bin");
//                            List<ByteBuffer> fileByte4 = UpdateDatalogUtils.getFileByte2(this, "ZACA02testCRC.bin");
            List<ByteBuffer> fileByte5 = UpdateDatalogUtils.getFileByte2((Activity) context, "ZACA04.bin");
//                            List<ByteBuffer> fileByte6 = UpdateDatalogUtils.getFileByte2(this, "ZACA03testCRC.bin");

            List<List<ByteBuffer>> list = new ArrayList<>();
//                            list.add(fileByte1);
//                            list.add(fileByte2);
//                            list.add(fileByte3);
//                            list.add(fileByte4);
            list.add(fileByte5);
//                            list.add(fileByte6);
             fileUpdataSend = new FileUpdataSend(context, list, new IUpdataListeners() {
                @Override
                public void preparing() {
                    showDialogFragment();
                }

                @Override
                public void sendFileProgress(int total, int current, int progress) {
                    String uptating = context.getString(R.string.android_key1148) + "(" + (current+1) + "/" + total + ")";
                    tvSubtext.setText(uptating);
                    tvProgress.setText(progress + "%");
                    pbar.setProgress(progress);
                }

                @Override
                public void updataUpdataProgress(int total, int current, int progress) {
                    String uptating = context.getString(R.string.installing) + "(" + current + "/" + total + ")";
                    tvSubtext.setText(uptating);
                    pbar.setProgress(progress);
                }

                @Override
                public void updataFail(String msg) {
                    if (dialogFragment != null) {
                        dialogFragment.dialogDismiss();
                    }
                    showUpdataError(msg);
                }

                @Override
                public void updataSuccess() {
                    if (dialogFragment != null) {
                        dialogFragment.dialogDismiss();
                    }
                    showUpdataSuccess();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private TextView tvSubtext;
    private ProgressBar pbar;
    private TextView tvProgress;
    private TextView tvUpdating;
    private BaseCircleDialog dialogFragment;

    /**
     * 显示更新提示
     */
    private void showDialogFragment() {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_updata_dialog, null);
        tvSubtext = view.findViewById(R.id.tv_subtext);
        pbar = view.findViewById(R.id.loading_img);
        tvProgress = view.findViewById(R.id.tv_progress);
        tvUpdating = view.findViewById(R.id.uptating_tips);
        tvSubtext.setText(R.string.upgrading_in_preparation);
        tvSubtext.setOnClickListener(view12 -> {
            fileUpdataSend.readTimeOut();
        });
        dialogFragment = CircleDialogUtils.showCommentBodyView(context, view, "", ((FragmentActivity) context).getSupportFragmentManager(), view1 -> {


        }, Gravity.CENTER, 0.8f, 0.5f, false);
    }


    private BaseCircleDialog dialog_success;

    /**
     * 升级成功
     */
    private void showUpdataSuccess() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_config_success, null);
        dialog_success = CircleDialogUtils.showCommentBodyView(context, view, "", ((FragmentActivity) context).getSupportFragmentManager(), view1 -> {
            TextView tvTitle = view1.findViewById(R.id.tv_title);
            TextView loadingTips = view1.findViewById(R.id.loading_tips);
            TextView tvCancel = view1.findViewById(R.id.tv_cancel);
            tvCancel.setOnClickListener(view2 -> {
                fileUpdataSend.close();
                dialog_success.dialogDismiss();
            });
            tvTitle.setText(R.string.android_key2969);
            loadingTips.setText(R.string.device_restart);

        }, Gravity.CENTER, 0.8f, 0.5f, false);
    }


    /**
     * 升级失败
     */
    private BaseCircleDialog dialog_error;
    private void showUpdataError(String error) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_config_error, null);
        dialog_error = CircleDialogUtils.showCommentBodyView(context, view, "", ((FragmentActivity) context).getSupportFragmentManager(), view1 -> {
            TextView tvTitle = view1.findViewById(R.id.tv_title);
            TextView loadingTips = view1.findViewById(R.id.loading_tips);
            TextView tvCancel = view1.findViewById(R.id.tv_cancel);
            tvCancel.setOnClickListener(view2 -> {
                fileUpdataSend.close();
                dialog_error.dialogDismiss();
            });

            tvTitle.setText(R.string.android_key2964);
            loadingTips.setText(error);

        }, Gravity.CENTER, 0.8f, 0.5f, true);
    }


}
