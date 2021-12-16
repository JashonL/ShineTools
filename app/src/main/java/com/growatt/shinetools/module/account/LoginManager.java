package com.growatt.shinetools.module.account;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.growatt.shinetools.MainActivity;
import com.growatt.shinetools.R;
import com.growatt.shinetools.ShineToosApplication;
import com.growatt.shinetools.bean.User;
import com.growatt.shinetools.constant.GlobalConstant;
import com.growatt.shinetools.db.DataBaseManager;
import com.growatt.shinetools.module.WelcomeActivity;
import com.growatt.shinetools.module.inverterUpdata.CheckDownloadUtils;
import com.growatt.shinetools.module.inverterUpdata.DownloadFileActivity;
import com.growatt.shinetools.module.inverterUpdata.FileCheckUpdataCallback;
import com.growatt.shinetools.module.inverterUpdata.FileDownBean;
import com.growatt.shinetools.module.inverterUpdata.FileUpdataManager;
import com.growatt.shinetools.utils.ActivityUtils;
import com.growatt.shinetools.utils.AppSystemUtils;
import com.growatt.shinetools.utils.CommenUtils;
import com.growatt.shinetools.utils.DialogUtils;
import com.growatt.shinetools.utils.Log;
import com.growatt.shinetools.utils.MyToastUtils;
import com.growatt.shinetools.utils.RequestCallback;
import com.growatt.shinetools.utils.SharedPreferencesUnit;
import com.growatt.shinetools.utils.ShineToolsApi;

import java.util.List;

import static com.growatt.shinetools.constant.GlobalConstant.END_USER;
import static com.growatt.shinetools.constant.GlobalConstant.KEFU_USER;
import static com.growatt.shinetools.constant.GlobalConstant.KEY_END_USER_PWD;
import static com.growatt.shinetools.constant.GlobalConstant.MAINTEAN_USER;

public class LoginManager {
    private Context context;
    private int userType = GlobalConstant.END_USER;
    private String endUserPwd;
    private DataBaseManager dataBaseManager;


    public LoginManager(Context context) {
        this.context = context;
        //获取保存的终端用户密码
        endUserPwd = SharedPreferencesUnit.getInstance(context).get(KEY_END_USER_PWD);
        Log.i("获取个性存储中的密码:" + endUserPwd);
        //如果没有保存过那么密码为今天的日期
        if (TextUtils.isEmpty(endUserPwd)) {
            endUserPwd = "oss" + CommenUtils.getNowData(CommenUtils.DataType.YMD);
        }

        dataBaseManager = new DataBaseManager(context);

    }


    /**
     * 终端用户登录
     */
    public void endUserLogin(String pwd, boolean auto) {
        if (TextUtils.isEmpty(pwd)) return;
        if (pwd.equals(endUserPwd)) {
            loginSuccess(END_USER, auto, "", pwd);
        }else {
            MyToastUtils.toast(R.string.android_key3065);
        }
    }


    /**
     * 运维人员登录
     */
    public void maintainUserLogin(String username, String password) {
        ShineToolsApi.login(context, username, password, new RequestCallback((LoginActivity) context));
    }


    /**
     * 登录成功之后的处理
     *
     * @param userType 用户类型
     * @param auto     是否自动登录
     */
    public void loginSuccess(int userType, boolean auto, String username, String password) {
        //全局保存用户类型
        ShineToosApplication.getContext().setUser_type(userType);
        //是否自动登录
        SharedPreferencesUnit.getInstance(context).putBoolean(GlobalConstant.KEY_AUTO_LOGIN, auto);
        //保存登录的用户类型
        SharedPreferencesUnit.getInstance(context).putInt(GlobalConstant.KEY_USER_TYPE, userType);
        //运维用户用SQLite保存密码
        if (MAINTEAN_USER == userType||KEFU_USER==userType) {
            dataBaseManager.save(new User("1",username,password));
        }



        //检测是否要下载文件
        FileUpdataManager fileUpdataManager = CheckDownloadUtils.checkUpdata((Activity) context);
        fileUpdataManager.checkNewVersion(new FileCheckUpdataCallback(){
            @Override
            protected void onBefore() {
                super.onBefore();
                DialogUtils.getInstance().showLoadingDialog((Activity) context);
            }

            @Override
            protected void hasNewVersion(FileDownBean updateApp, FileUpdataManager updateAppManager) {
                super.hasNewVersion(updateApp, updateAppManager);
                DialogUtils.getInstance().closeLoadingDialog();
                ActivityUtils.gotoActivity((Activity) context, DownloadFileActivity.class, true);
            }

            @Override
            protected void noNewVirsion(String error) {
                super.noNewVirsion(error);
                DialogUtils.getInstance().closeLoadingDialog();
                //如果是
                boolean firstWelcome = AppSystemUtils.isFirstWelcome();
                if (!firstWelcome){
                    ActivityUtils.gotoActivity((Activity) context, WelcomeActivity.class, true);
                }else {
                    ActivityUtils.gotoActivity((Activity) context, MainActivity.class, true);
                }
            }

            @Override
            protected void onServerError() {
                super.onServerError();
                DialogUtils.getInstance().closeLoadingDialog();
                //如果是
                boolean firstWelcome = AppSystemUtils.isFirstWelcome();
                if (!firstWelcome){
                    ActivityUtils.gotoActivity((Activity) context, WelcomeActivity.class, true);
                }else {
                    ActivityUtils.gotoActivity((Activity) context, MainActivity.class, true);
                }
            }
        });


    }


    public User getLoginUser() {
        List<User> query = dataBaseManager.query();
        if (query != null && query.size() > 0) {
            return query.get(0);
        }
        return null;
    }


}
