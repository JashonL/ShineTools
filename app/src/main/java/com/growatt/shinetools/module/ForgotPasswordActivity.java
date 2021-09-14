package com.growatt.shinetools.module;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.growatt.shinetools.R;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.utils.CommenUtils;
import com.growatt.shinetools.utils.MyToastUtils;
import com.growatt.shinetools.utils.SharedPreferencesUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.growatt.shinetools.constant.GlobalConstant.KEY_END_USER_PWD;
import static com.growatt.shinetools.utils.UIWigetUtils.clickPasswordSwitch;

public class ForgotPasswordActivity extends BaseActivity {
    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.header_title)
    LinearLayout headerTitle;
    @BindView(R.id.et_old_password)
    EditText etOldPassword;
    @BindView(R.id.iv_old_password_view)
    ImageView ivOldPasswordView;
    @BindView(R.id.ll_old_pwd)
    LinearLayout llOldPwd;
    @BindView(R.id.et_new_password)
    EditText etNewPassword;
    @BindView(R.id.iv_new_password_view)
    ImageView ivNewPasswordView;
    @BindView(R.id.ll_new_pwd)
    LinearLayout llNewPwd;
    @BindView(R.id.et_comfir_password)
    EditText etComfirPassword;
    @BindView(R.id.iv_comfir_password_view)
    ImageView ivComfirPasswordView;
    @BindView(R.id.ll_comfir_pwd)
    LinearLayout llComfirPwd;
    @BindView(R.id.btn_next)
    Button btnNext;

    private boolean initialPasswordOn;
    private boolean newPasswordOn;
    private boolean secendPasswordOn;


    @Override
    protected int getContentView() {
        return R.layout.activity_forgot_password;
    }

    @Override
    protected void initViews() {
        initToobar(toolbar);
        tvTitle.setText(R.string.android_key2292);
    }

    @Override
    protected void initData() {

    }


    @OnClick({R.id.iv_old_password_view, R.id.iv_new_password_view, R.id.iv_comfir_password_view, R.id.btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_old_password_view:
                initialPasswordOn = !initialPasswordOn;
                clickPasswordSwitch(ivOldPasswordView, etOldPassword, initialPasswordOn);

                break;
            case R.id.iv_new_password_view:
                newPasswordOn = !newPasswordOn;
                clickPasswordSwitch(ivNewPasswordView, etNewPassword, newPasswordOn);
                break;
            case R.id.iv_comfir_password_view:
                secendPasswordOn = !secendPasswordOn;
                clickPasswordSwitch(ivComfirPasswordView, etComfirPassword, secendPasswordOn);
                break;
            case R.id.btn_next:
                editInitialPassWord();
                break;
        }
    }


    private void editInitialPassWord() {
        String olb_pwd = etOldPassword.getText().toString();
        String new_pwd = etNewPassword.getText().toString();
        String second_pwd = etComfirPassword.getText().toString();


        //判空处理
        if (TextUtils.isEmpty(olb_pwd)) {
            MyToastUtils.toast(R.string.android_key3062);
            return;
        }

        if (TextUtils.isEmpty(new_pwd)) {
            MyToastUtils.toast(R.string.android_key3062);
            return;
        }

        if (TextUtils.isEmpty(second_pwd)) {
            MyToastUtils.toast(R.string.android_key3064);
            return;
        }


        //判断密码正确性 初始密码始终为当天日期
        String nowData = "oss"+CommenUtils.getNowData(CommenUtils.DataType.YMD);
        if (!olb_pwd.equals(nowData)) {
            MyToastUtils.toast(R.string.android_key3065);
            return;
        }
        if (!new_pwd.equals(second_pwd)) {
            MyToastUtils.toast(R.string.android_key28);
            return;
        }
        SharedPreferencesUnit.getInstance(this).put(KEY_END_USER_PWD, new_pwd);
        MyToastUtils.toast(R.string.android_key845);
        finish();
    }


}
