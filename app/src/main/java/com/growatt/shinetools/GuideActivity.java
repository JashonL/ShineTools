package com.growatt.shinetools;

import android.content.Intent;
import android.widget.TextView;

import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.module.account.LoginActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class GuideActivity extends BaseActivity {


    @BindView(R.id.tv_welcome)
    TextView tvWelcome;
    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.tv_try_now)
    TextView tvTryNow;

    @Override
    protected int getContentView() {
        return R.layout.activity_guide;
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initData() {

    }


    @OnClick(R.id.tv_try_now)
    public void onViewClicked() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }


}
