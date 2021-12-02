package com.growatt.shinetools.utils;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.growatt.shinetools.R;
import com.mylhyl.circledialog.BaseCircleDialog;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.res.drawable.CircleDrawable;
import com.mylhyl.circledialog.res.values.CircleColor;
import com.mylhyl.circledialog.res.values.CircleDimen;

public class BatteryCheckDialogUtils {


    public static BaseCircleDialog showCheckStartDialog(FragmentActivity context, String title) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_diagnosis_start, null, false);
        TextView tvTitle = contentView.findViewById(R.id.tv_title);
        CircleDialog.Builder builder = new CircleDialog.Builder();
        builder.setWidth(0.75f);
        builder.setMaxHeight(0.8f);
        builder.setBodyView(contentView, view -> {
            CircleDrawable bgCircleDrawable = new CircleDrawable(CircleColor.DIALOG_BACKGROUND
                    , CircleDimen.DIALOG_RADIUS, CircleDimen.DIALOG_RADIUS, CircleDimen.DIALOG_RADIUS, CircleDimen.DIALOG_RADIUS);
            view.setBackground(bgCircleDrawable);
        });
        builder.setGravity(Gravity.CENTER);
        builder.setCancelable(true);
        BaseCircleDialog show = builder.show(context.getSupportFragmentManager());
        tvTitle.setText(title);
        return show;
    }


    public static void showCheckSuccessDialog(FragmentActivity context, String title) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_diagnosis_success, null, false);
        TextView tvTitle = contentView.findViewById(R.id.tv_title);
        TextView btnOk = contentView.findViewById(R.id.btn_ok);

        CircleDialog.Builder builder = new CircleDialog.Builder();
        builder.setWidth(0.75f);
        builder.setMaxHeight(0.8f);
        builder.setBodyView(contentView, view -> {
            CircleDrawable bgCircleDrawable = new CircleDrawable(CircleColor.DIALOG_BACKGROUND
                    , CircleDimen.DIALOG_RADIUS, CircleDimen.DIALOG_RADIUS, CircleDimen.DIALOG_RADIUS, CircleDimen.DIALOG_RADIUS);
            view.setBackground(bgCircleDrawable);
        });
        builder.setGravity(Gravity.CENTER);
        builder.setCancelable(true);
        BaseCircleDialog show = builder.show(context.getSupportFragmentManager());
        tvTitle.setText(title);
        btnOk.setOnClickListener(view -> {
            show.dialogDismiss();
        });
    }



    public static BaseCircleDialog showCheckErrorDialog(FragmentActivity context, String title,String tips1,String tips2,OnConfirmListeners listeners) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_diagnosis_error, null, false);
        TextView tvTitle = contentView.findViewById(R.id.tv_title);

        TextView tvTips1 = contentView.findViewById(R.id.tv_button_tips);
        TextView tvTips2 = contentView.findViewById(R.id.tv_button_tips2);
        TextView tvCancel = contentView.findViewById(R.id.tv_button_cancel);
        TextView tvConfirm = contentView.findViewById(R.id.tv_button_confirm);




        CircleDialog.Builder builder = new CircleDialog.Builder();
        builder.setWidth(0.75f);
        builder.setMaxHeight(0.8f);
        builder.setBodyView(contentView, view -> {
            CircleDrawable bgCircleDrawable = new CircleDrawable(CircleColor.DIALOG_BACKGROUND
                    , CircleDimen.DIALOG_RADIUS, CircleDimen.DIALOG_RADIUS, CircleDimen.DIALOG_RADIUS, CircleDimen.DIALOG_RADIUS);
            view.setBackground(bgCircleDrawable);
        });
        builder.setGravity(Gravity.CENTER);
        builder.setCancelable(true);
        BaseCircleDialog show = builder.show(context.getSupportFragmentManager());
        tvTitle.setText(title);

        tvTips1.setText(tips1);
        tvTips2.setText(tips2);
        tvCancel.setOnClickListener(view -> {
            show.dialogDismiss();
        });

        tvConfirm.setOnClickListener(view -> {
            listeners.confirListener();
            show.dialogDismiss();
        });
        return show;
    }


   public interface OnConfirmListeners{
        void  confirListener();
    }
}
