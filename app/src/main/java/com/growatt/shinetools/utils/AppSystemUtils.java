package com.growatt.shinetools.utils;


import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.growatt.shinetools.R;
import com.growatt.shinetools.ShineToosApplication;
import com.growatt.shinetools.constant.GlobalConstant;
import com.growatt.shinetools.module.DeviceTypeActivity;
import com.growatt.shinetools.module.account.LoginActivity;
import com.mylhyl.circledialog.callback.ConfigInput;
import com.mylhyl.circledialog.view.listener.OnInputClickListener;

import java.lang.ref.WeakReference;
import java.util.List;

import static com.growatt.shinetools.constant.GlobalConstant.KEY_END_USER_PWD;

/**
 * APP系统工具类
 */
public class AppSystemUtils {


    public static final String KEY_APP_FIRST_INSTALL="key_app_first_install";

    /**
     * 判断是否第一次使用
     *
     * @return
     */
    public static boolean isFirstInstall() {
        SharedPreferencesUnit sharedPreferencesUnit = SharedPreferencesUnit.getInstance(ShineToosApplication.getContext());
        boolean isFirst = !sharedPreferencesUnit.getBoolean(KEY_APP_FIRST_INSTALL);
        if (isFirst){
            sharedPreferencesUnit.putBoolean(KEY_APP_FIRST_INSTALL,true);
        }
        return isFirst;
    }

    /**
     * 退出登录
     */

    public static void logout(Activity from){
        List<WeakReference<Activity>> activityStack = ShineToosApplication.getContext().getActivityList();
        for (WeakReference<Activity> activity : activityStack) {
            if (activity != null && activity.get() != null) {
                Activity activity1=activity.get();
                if (activity1 instanceof DeviceTypeActivity) continue;//这里要忽略掉，要不然会闪屏
                activity1.finish();
            }
        }
        activityStack.clear();
        //是否自动登录
        SharedPreferencesUnit.getInstance(from).putBoolean(GlobalConstant.KEY_AUTO_LOGIN, false);
        ActivityUtils.gotoActivity(from,LoginActivity.class,true);
    }


    public static void modifyPwd(Context context) {
        //1.弹出弹框
        ConfigInput configInput = params -> {
            params.padding = new int[]{5, 5, 5, 5};
            params.strokeColor = ContextCompat.getColor(context, R.color.color_text_33);
        };
        CircleDialogUtils.showCustomInputDialog((FragmentActivity) context, context.getString(R.string.android_key3054),
                context.getString(R.string.android_key3055),
                "", "", false, Gravity.CENTER, context.getString(R.string.android_key1935), new OnInputClickListener() {

                    @Override
                    public boolean onClick(String text, View v) {
                        if (TextUtils.isEmpty(text)) {
                            MyToastUtils.toast(R.string.android_key466);
                            return true;
                        }

                        String text1 = context.getString(R.string.android_key661) + ":" + text;

                        CircleDialogUtils.showCommentDialog((FragmentActivity) context, context.getString(R.string.android_key537),
                                text1, context.getString(R.string.android_key1935), context.getString(R.string.android_key2152),
                                Gravity.CENTER, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SharedPreferencesUnit.getInstance(context).put(KEY_END_USER_PWD, text);
                            }
                        }, null);


                        return true;
                    }
                }, context.getString(R.string.android_key2152), configInput, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
    }

}
