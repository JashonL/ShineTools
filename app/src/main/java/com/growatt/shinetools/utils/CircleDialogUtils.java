package com.growatt.shinetools.utils;


import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.growatt.shinetools.R;
import com.mylhyl.circledialog.BaseCircleDialog;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigInput;
import com.mylhyl.circledialog.callback.ConfigText;
import com.mylhyl.circledialog.params.TextParams;
import com.mylhyl.circledialog.res.drawable.CircleDrawable;
import com.mylhyl.circledialog.res.values.CircleColor;
import com.mylhyl.circledialog.res.values.CircleDimen;
import com.mylhyl.circledialog.view.listener.OnCreateBodyViewListener;
import com.mylhyl.circledialog.view.listener.OnInputClickListener;
import com.mylhyl.circledialog.view.listener.OnLvItemClickListener;

import java.util.List;

import static com.growatt.shinetools.utils.MyToastUtils.toast;


public class CircleDialogUtils {
    /**
     * 公共自定义弹框
     *
     * @return
     */
    public static BaseCircleDialog showCommentBodyDialog(float width, float height, View bodyView,
                                                         FragmentManager fragmentManager,
                                                         OnCreateBodyViewListener listener, int gravity, boolean cancel) {
        CircleDialog.Builder builder = new CircleDialog.Builder();
        builder.setWidth(width);
        builder.setMaxHeight(height);
        builder.setBodyView(bodyView, listener);
        builder.setGravity(gravity);
        builder.setCancelable(cancel);
        return builder.show(fragmentManager);
    }


    /**
     * 公共输入框
     *
     * @param activity
     * @return
     */
    public static DialogFragment showCustomInputDialog(FragmentActivity activity, String title, String subTitle, String text, String hint,
                                                       boolean showKeyboard, int gravity, String positiveText, OnInputClickListener inputClickListener,
                                                       String negativeText, ConfigInput configInput, View.OnClickListener negativeListner) {
        CircleDialog.Builder builder = new CircleDialog.Builder();
        builder.setCanceledOnTouchOutside(true);
        builder.setCancelable(true);
        builder.setTitle(title);
        if (!TextUtils.isEmpty(subTitle)) {
            builder.setSubTitle(subTitle);
        }

        if (!TextUtils.isEmpty(text)) {
            builder.setInputText(text);
        }

        if (!TextUtils.isEmpty(hint)) {
            builder.setInputHint(hint);
        }
        builder.setWidth(0.8f);
        builder.setGravity(gravity);
        builder.setInputShowKeyboard(showKeyboard);
        builder.setInputCounter(1000, (maxLen, currentLen) -> "");
        if (configInput != null) {
            builder.configInput(configInput);
        }
   /*     builder.configInput(params -> {
//                            params.isCounterAllEn = true;
            params.padding = new int[]{5, 5, 5, 5};
            params.strokeColor = ContextCompat.getColor(activity, R.color.title_bg_white);
//                                params.inputBackgroundResourceId = R.drawable.bg_input;
//                                params.gravity = Gravity.CENTER;
            //密码
//                                params.inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
//                                        | InputType.TYPE_TEXT_FLAG_MULTI_LINE;
            //文字加粗
//            params.styleText = Typeface.BOLD;
        });*/

        if (negativeListner != null) {
            builder.setNegative(negativeText, negativeListner);
        }

        if (inputClickListener != null) {
            builder.setPositiveInput(positiveText, inputClickListener);
        }


        return builder.show(activity.getSupportFragmentManager());

    }


    /**
     * 公共提示框
     *
     * @param activity
     * @return
     */
    public static DialogFragment showCommentDialog(FragmentActivity activity, String title, String text, String positive, String negative, int TextGravity,
                                                   View.OnClickListener posiListener, View.OnClickListener negativeListener) {
        CircleDialog.Builder builder = new CircleDialog.Builder();
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }

        if (!TextUtils.isEmpty(text)) {
            builder.setText(text);
        }
        builder.configText(new ConfigText() {
            @Override
            public void onConfig(TextParams params) {
                params.gravity = TextGravity;
            }
        });

        if (negativeListener != null) {
            builder.setNegative(negative, negativeListener);
        }

        if (posiListener != null) {
            builder.setPositive(positive, posiListener);
        }

        builder.setWidth(0.8f);
        return builder.show(activity.getSupportFragmentManager());
    }


    /**
     * 公共自定义框
     *
     * @return
     */
    public static BaseCircleDialog showCommentBodyView(Context context, View bodyView, String title,
                                                     FragmentManager fragmentManager, OnCreateBodyViewListener listener,
                                                     int gravity, float width, float maxHeight, boolean cancelable) {
        CircleDialog.Builder builder = new CircleDialog.Builder();
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        builder.setBodyView(bodyView, listener);
        builder.setGravity(gravity);
        builder.setWidth(width);
        builder.setMaxHeight(maxHeight);
        builder.setCancelable(cancelable);
        BaseCircleDialog show = builder.show(fragmentManager);
        return show;
    }


    public static BaseCircleDialog showNotitleDialog(AppCompatActivity context, String text, OndialogClickListeners listeners) {
        View bodyView = LayoutInflater.from(context).inflate(R.layout.dialog_text_notitle, null);
        CircleDialog.Builder builder = new CircleDialog.Builder();
        builder.setBodyView(bodyView, view -> {
            TextView tvTips = view.findViewById(R.id.tv_tips);
            TextView tvCancel = view.findViewById(R.id.tv_cancel);
            if (!TextUtils.isEmpty(text)) {
                tvTips.setText(text);
            }
            tvCancel.setOnClickListener(view1 -> {
                listeners.buttonOk();
            });

        });
        builder.setWidth(0.7f);
        builder.setMaxHeight(0.6f);
        builder.setCancelable(true);
        BaseCircleDialog show = builder.show(context.getSupportFragmentManager());
        return show;
    }


    /**
     * 公共自定义弹框
     *
     * @return
     */
    public static BaseCircleDialog showExplainDialog(AppCompatActivity context, String title, String text, OndialogClickListeners listeners) {
        View bodyView = LayoutInflater.from(context).inflate(R.layout.explain_dialog, null);

        TextView tvTitle = bodyView.findViewById(R.id.tv_title);
        TextView tvContent = bodyView.findViewById(R.id.tv_content);
        ImageView ivClose = bodyView.findViewById(R.id.iv_close);

        CircleDialog.Builder builder = new CircleDialog.Builder();
        builder.setBodyView(bodyView, view -> {

            if (!TextUtils.isEmpty(title)) {
                tvTitle.setText(title);
            }
            if (!TextUtils.isEmpty(text)) {
                tvContent.setText(text);
            }



        });
        builder.setYoff(20);
        builder.setGravity(Gravity.BOTTOM);
        builder.setCancelable(true);
        BaseCircleDialog show = builder.show(context.getSupportFragmentManager());

        ivClose.setOnClickListener(view1 -> {
            show.dialogDismiss();
        });
        return show;
    }


    /**
     * 公共复选框
     *
     * @param activity
     * @return
     */
    public static DialogFragment showCommentItemDialog(FragmentActivity activity, String title, List<String> items, int gravity, OnLvItemClickListener listener, View.OnClickListener negativeListener) {
        DialogFragment itemsSelectDialog = new CircleDialog.Builder()
                .setTitle(title)
                .configTitle(params -> {
                    params.styleText = Typeface.BOLD;
                })
                .setItems(items, listener)
                .configItems(params -> {
                    params.dividerHeight = 0;
                    params.textColor = ContextCompat.getColor(activity, R.color.title_bg_white);
                })
                .setGravity(gravity)
                .setNegative(activity.getString(R.string.android_key1806), negativeListener)
                .setMaxHeight(0.5f)
                .setWidth(0.8f)
                .show(activity.getSupportFragmentManager());

        return itemsSelectDialog;
    }


    /**
     * 公共输入框
     *
     * @param activity
     * @return
     */
    public static DialogFragment showCommentInputDialog(FragmentActivity activity, String title, String text, String hint, boolean showKeyboard, OnInputClickListener listener) {
        DialogFragment inputDialog = new CircleDialog.Builder()
                .setCanceledOnTouchOutside(true)
                .setCancelable(true)
                .setTitle(title)
                .setInputText(text)
                .setInputHint(hint)
                .setWidth(0.8f)
                .setGravity(Gravity.CENTER)
                .setInputShowKeyboard(showKeyboard)
                .setInputCounter(1000, (maxLen, currentLen) -> "")
                .setPositiveInput(activity.getString(R.string.all_ok), listener)
                .configInput(params -> {
//                            params.isCounterAllEn = true;
                    params.padding = new int[]{5, 5, 5, 5};
                    params.strokeColor = ContextCompat.getColor(activity, R.color.title_bg_white);
//                                params.inputBackgroundResourceId = R.drawable.bg_input;
//                                params.gravity = Gravity.CENTER;
                    //密码
//                                params.inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
//                                        | InputType.TYPE_TEXT_FLAG_MULTI_LINE;
                    //文字加粗
//            params.styleText = Typeface.BOLD;
                })
                .setNegative(activity.getString(R.string.all_no), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .show(activity.getSupportFragmentManager());
        return inputDialog;


    }


   public interface OndialogComfirListener {
        void comfir(String value);
    }

    public static void showInputValueDialog(FragmentActivity context, String title, String subTitle,
                                            String unit, OndialogComfirListener listener) {
        showInputValueDialog(context,title,subTitle,"",unit,listener);
    }





    public static void showInputValueDialog(FragmentActivity context, String title, String subTitle,
                                           String hint, String unit, OndialogComfirListener listener) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_input_custom, null, false);
        TextView tvTitle = contentView.findViewById(R.id.tv_title);
        TextView tvSubTtile = contentView.findViewById(R.id.tv_sub_title);
        TextView tvUnit = contentView.findViewById(R.id.tv_unit);
        TextView tvCancel = contentView.findViewById(R.id.tv_button_cancel);
        TextView tvConfirm = contentView.findViewById(R.id.tv_button_confirm);
        TextView etInput = contentView.findViewById(R.id.et_input);
        tvCancel.setText(R.string.mCancel_ios);
        tvConfirm.setText(R.string.android_key1935);

        if (!TextUtils.isEmpty(hint)){
            etInput.setText(hint);
        }

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
        tvSubTtile.setText(subTitle);
        tvUnit.setText(unit);
        tvTitle.setText(title);

        tvCancel.setOnClickListener(view1 -> {
            show.dialogDismiss();
        });


        tvConfirm.setOnClickListener(view -> {
            String value = etInput.getText().toString();
            if (TextUtils.isEmpty(value)) {
                toast(R.string.android_key1945);
                return;
            }
            show.dialogDismiss();
            listener.comfir(value);
        });
    }





    public static void showUpdataDialog(FragmentActivity context, String title, String subTitle,
                                            String current, String newversion, OndialogClickListeners listener) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_inverter_updata, null, false);
        TextView tvTitle = contentView.findViewById(R.id.tv_title);
        TextView tvSubTtile = contentView.findViewById(R.id.tv_sub_title);
        TextView tvCurrentValue = contentView.findViewById(R.id.tv_current_value);
        TextView tvTargetValue = contentView.findViewById(R.id.tv_target_value);
        TextView tvCancel = contentView.findViewById(R.id.tv_button_cancel);
        TextView tvConfirm = contentView.findViewById(R.id.tv_button_confirm);

        if (!TextUtils.isEmpty(current)){
            tvCurrentValue.setText(current);
        }

        if (!TextUtils.isEmpty(newversion)){
            tvTargetValue.setText(newversion);
        }


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
        tvSubTtile.setText(subTitle);
        tvTitle.setText(title);

        tvCancel.setOnClickListener(view1 -> {
            show.dialogDismiss();
        });


        tvConfirm.setOnClickListener(view -> {
            show.dialogDismiss();
            listener.buttonOk();
        });
    }




    public interface OndialogClickListeners {
        void buttonOk();

        void buttonCancel();

    }


    public interface OnDialogInputClickListeners {
        void buttonOk(String input);

        void buttonCancel();

    }


}
