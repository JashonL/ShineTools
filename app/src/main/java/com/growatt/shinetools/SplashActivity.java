package com.growatt.shinetools;

import android.content.Intent;
import android.os.Handler;

import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.module.account.LoginActivity;
import com.growatt.shinetools.utils.AppSystemUtils;


public class SplashActivity extends BaseActivity {



    @Override
    protected void initImmersionBar() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean firstUse = AppSystemUtils.isFirstInstall();
                if (firstUse){
                    startActivity(new Intent(SplashActivity.this,GuideActivity.class));
                    finish();
                }else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }

            }
        }, 800);

    }
}
