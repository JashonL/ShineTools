package com.growatt.shinetools.module.account;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.growatt.shinetools.R;
import com.growatt.shinetools.agreement.Agreement;
import com.growatt.shinetools.base.BaseNetWorkActivity;
import com.growatt.shinetools.bean.User;
import com.growatt.shinetools.constant.Cons;
import com.growatt.shinetools.db.SqliteUtil;
import com.growatt.shinetools.login.OssLoginUtils;
import com.growatt.shinetools.module.ForgotPasswordActivity;
import com.growatt.shinetools.utils.ActivityUtils;
import com.growatt.shinetools.utils.CommenUtils;
import com.growatt.shinetools.utils.DialogUtils;
import com.growatt.shinetools.utils.Log;
import com.growatt.shinetools.utils.MyToastUtils;
import com.growatt.shinetools.utils.OssUtils;
import com.growatt.shinetools.utils.SharedPreferencesUnit;
import com.growatt.shinetools.widget.EditTextWithDel;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

import static com.growatt.shinetools.constant.GlobalConstant.END_USER;
import static com.growatt.shinetools.constant.GlobalConstant.KEFU_USER;
import static com.growatt.shinetools.constant.GlobalConstant.KEY_AUTO_LOGIN;
import static com.growatt.shinetools.constant.GlobalConstant.KEY_END_USER_PWD;
import static com.growatt.shinetools.constant.GlobalConstant.KEY_USER_TYPE;
import static com.growatt.shinetools.constant.GlobalConstant.MAINTEAN_USER;
import static com.growatt.shinetools.utils.UIWigetUtils.clickPasswordSwitch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LoginActivity extends BaseNetWorkActivity implements RadioGroup.OnCheckedChangeListener {
    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;
    @BindView(R.id.rg_role)
    RadioGroup rgRole;
    @BindView(R.id.et_username)
    EditTextWithDel etUsername;
    @BindView(R.id.ll_user_username)
    LinearLayout llUserUsername;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.iv_password_view)
    ImageView ivPasswordView;
    @BindView(R.id.ll_pwd)
    LinearLayout llPwd;
    @BindView(R.id.tv_forgot_pwd)
    TextView tvForgotPwd;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.radio_end)
    RadioButton radioEnd;
    @BindView(R.id.radio_maintain)
    RadioButton radioMaintain;
    @BindView(R.id.cb_auto)
    CheckBox cbAuto;


    @BindView(R.id.et_end_password)
    EditText etEndPassword;
    @BindView(R.id.iv_end_password_view)
    ImageView ivEndPasswordView;
    @BindView(R.id.ll_end_pwd)
    LinearLayout llEndPwd;

    @BindView(R.id.tv_agreement)
    TextView tvAgreement;

    @BindView(R.id.cb_agreement)
    CheckBox cbAgreement;


    private boolean passwordOn;
    private boolean passwordEndOn;

    private LoginManager loginManager;


    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void initViews() {
        tvTitle.setVisibility(View.GONE);
        rgRole.setOnCheckedChangeListener(this);
        rgRole.check(R.id.radio_end);



        String pricy_and_agree = getString(R.string.read_agree) + "《" + getString(R.string.shinetools_agreement) + "》"
                + getString(R.string.and) + "《" + getString(R.string.shinetools_pricy) + "》";


        int language = CommenUtils.getLanguageNew1();
        SpannableStringBuilder spannableStringBuilder;
        if (language == 0) {
            spannableStringBuilder = updateTextStyle(pricy_and_agree, true);
        } else {
            spannableStringBuilder = updateTextStyle(pricy_and_agree, false);
        }
        tvAgreement.setMovementMethod(LinkMovementMethod.getInstance());
        tvAgreement.setText(spannableStringBuilder);



    }



    private SpannableStringBuilder updateTextStyle(String content, boolean chinese) {
        SpannableStringBuilder spannableString = new SpannableStringBuilder();
        spannableString.append(content);

        //使用ForegroundColorSpan添加点击事件会出现冲突
        UnderlineSpan colorSpan = new UnderlineSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(Color.parseColor("#009cff"));//设置颜色
            }
        };


        //使用UnderlineSpan很好的兼容这个问题
        UnderlineSpan colorSpan1 = new UnderlineSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(Color.parseColor("#009cff"));//设置颜色
                //   ds.setUnderlineText(false); //去掉下划线
            }
        };


        //用户协议
        int userIndex_start = content.indexOf("《");
        int userIndex_end = content.indexOf("》") + 1;

        //隐私政策
        int privacyBeginIndex = content.lastIndexOf("《");
        int privacyEndIndex = content.lastIndexOf("》") + 1;

        tvAgreement.setHighlightColor(ContextCompat.getColor(LoginActivity.this,R.color.nocolor));

        ClickableSpan privacyClickableSpan = new ClickableSpan() {

            @Override
            public void onClick(@NonNull View widget) {

                Agreement.getPrivacy(LoginActivity.this, 2);


            }


        };

        ClickableSpan protocolClickableSpan = new ClickableSpan() {

            @Override
            public void onClick(@NonNull View widget) {
                Agreement.getPrivacy(LoginActivity.this, 1);

            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
            }
        };

        spannableString.setSpan(protocolClickableSpan, userIndex_start, userIndex_end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableString.setSpan(privacyClickableSpan, privacyBeginIndex, privacyEndIndex, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        //字体颜色一定要放在点击事件后面，不然部分手机不会修改颜色
        spannableString.setSpan(colorSpan, userIndex_start, userIndex_end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableString.setSpan(colorSpan1, privacyBeginIndex, privacyEndIndex, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);


        return spannableString;
    }


    @Override
    protected void initData() {
        //登录辅助类
        loginManager = new LoginManager(this);

        //判断上次登录的用户类型
        int useType = SharedPreferencesUnit.getInstance(this).getInt(KEY_USER_TYPE);
        if (END_USER == useType) {
            rgRole.check(R.id.radio_end);
            String pwd = SharedPreferencesUnit.getInstance(this).get(KEY_END_USER_PWD);
            if (!TextUtils.isEmpty(pwd)) {
                etEndPassword.setText(pwd);
            } else {
                String password = "oss" + CommenUtils.getNowData(CommenUtils.DataType.YMD);
                etEndPassword.setText(password);
            }
        } else if (MAINTEAN_USER == useType||KEFU_USER==useType) {
            rgRole.check(R.id.radio_maintain);
            User loginUser = loginManager.getLoginUser();
            if (loginUser != null) {
                String username = loginUser.getUsername();
                String password = loginUser.getPassword();
                if (!TextUtils.isEmpty(username)) {
                    etUsername.setText(username);
                }
                if (!TextUtils.isEmpty(password)) {
                    etPassword.setText(password);
                }
            }
        }

        //获取是否自动登录
        boolean auto = SharedPreferencesUnit.getInstance(this).getBoolean(KEY_AUTO_LOGIN);
        Log.i("自动登录：" + auto);
        cbAuto.setChecked(auto);

        String username = etUsername.getText().toString();
        if (auto) {
            if (END_USER == useType) {//从存储中获取数据
                String pwd = etEndPassword.getText().toString();
                if (!TextUtils.isEmpty(pwd)) {
                    loginManager.endUserLogin(pwd, cbAuto.isChecked());
                }
            } else {//从数据库中获取
                String pwd = etPassword.getText().toString();
                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
                    loginManager.maintainUserLogin(username, pwd);
                }
            }
        }


    }


    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int viewId) {
        switch (viewId) {
            case R.id.radio_end:
                llUserUsername.setVisibility(View.GONE);
                tvForgotPwd.setVisibility(View.VISIBLE);
                llEndPwd.setVisibility(View.VISIBLE);
                llPwd.setVisibility(View.GONE);
                break;
            case R.id.radio_maintain:
                llUserUsername.setVisibility(View.VISIBLE);
                tvForgotPwd.setVisibility(View.GONE);
                llEndPwd.setVisibility(View.GONE);
                llPwd.setVisibility(View.VISIBLE);
                break;
        }
    }


    @OnClick({R.id.iv_end_password_view,R.id.iv_password_view, R.id.btn_login, R.id.tv_forgot_pwd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_password_view:
                passwordOn = !passwordOn;
                clickPasswordSwitch(ivPasswordView, etPassword, passwordOn);
                break;

            case R.id.iv_end_password_view:
                passwordEndOn = !passwordEndOn;
                clickPasswordSwitch(ivEndPasswordView, etEndPassword, passwordEndOn);
                break;

            case R.id.tv_forgot_pwd:
                ActivityUtils.gotoActivity(this, ForgotPasswordActivity.class, false);
                break;

            case R.id.btn_login:
                if (!cbAgreement.isChecked()){
                    toast(R.string.all_terms_message);
                    return;
                }

                if (radioEnd.isChecked()) {
                    String pwd = etEndPassword.getText().toString();
                    if (TextUtils.isEmpty(pwd)) {
                        MyToastUtils.toast(R.string.android_key1832);
                        return;
                    }
                    loginManager.endUserLogin(pwd, cbAuto.isChecked());
                } else {
                    String pwd = etPassword.getText().toString();
                    if (TextUtils.isEmpty(pwd)) {
                        MyToastUtils.toast(R.string.android_key1832);
                        return;
                    }
                    String username = etUsername.getText().toString();
                    if (TextUtils.isEmpty(pwd)) {
                        MyToastUtils.toast(R.string.android_key1833);
                        return;
                    }
                    loginManager.maintainUserLogin(username, pwd);
                }
                break;

        }
    }


    @Override
    public void startView() {
        CommenUtils.hideKeyboard(btnLogin);
        DialogUtils.getInstance().showLoadingDialog(this);
    }

    @Override
    public void loadingView() {
    }

    @Override
    public void successView(String json) {
        DialogUtils.getInstance().closeLoadingDialog();

        try {
            JSONObject jsonObject = new JSONObject(json);
            int result = jsonObject.getInt("result");
            JSONObject obj = jsonObject.optJSONObject("obj");
            if (result == 1) {
                //用户类型
                int userType = obj.getInt("userType");
                if (userType != 0) {
                    //登录成功的逻辑
                    MyToastUtils.toast(R.string.android_key665);

                    JSONObject jsonObject1 = obj.optJSONObject("user");
                    int role = jsonObject1.optInt("role", 2);

                  if (role==6||role==14||role==7||role==15||role==17||role==18||role==31||role==30||role==34){//分销商 安装商
                        loginManager.loginSuccess(MAINTEAN_USER, cbAuto.isChecked(),
                                etUsername.getText().toString(), etPassword.getText().toString());
                    }else {
                      loginManager.loginSuccess(KEFU_USER, cbAuto.isChecked(),
                              etUsername.getText().toString(), etPassword.getText().toString());
                  }



                } else {
                    MyToastUtils.toast(R.string.android_key527);
                }
            } else {
                MyToastUtils.toast(R.string.android_key527);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void errorView(String errorMsg) {

    }


}
