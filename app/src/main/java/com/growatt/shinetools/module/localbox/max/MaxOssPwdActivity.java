package com.growatt.shinetools.module.localbox.max;

import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.growatt.shinetools.R;
import com.growatt.shinetools.base.BaseNetWorkActivity;
import com.growatt.shinetools.constant.GlobalConstant;
import com.growatt.shinetools.module.account.LoginActivity;
import com.growatt.shinetools.utils.CircleDialogUtils;
import com.growatt.shinetools.utils.CommenUtils;
import com.growatt.shinetools.utils.MD5andKL;
import com.growatt.shinetools.utils.Mydialog;
import com.growatt.shinetools.utils.RequestCallback;
import com.growatt.shinetools.utils.SharedPreferencesUnit;
import com.growatt.shinetools.utils.ShineToolsApi;
import com.mylhyl.circledialog.CircleDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 登录Oss账号，设置控制密码
 */
public class MaxOssPwdActivity extends BaseNetWorkActivity implements View.OnFocusChangeListener, RadioButton.OnCheckedChangeListener {


    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rbCheck1)
    RadioButton rbCheck1;
    @BindView(R.id.llType1)
    LinearLayout llType1;
    @BindView(R.id.etUserName)
    EditText etUserName;
    @BindView(R.id.tvOssPwd)
    AppCompatTextView tvOssPwd;
    @BindView(R.id.etPwd)
    EditText etPwd;
    @BindView(R.id.llContainer1)
    LinearLayout llContainer1;
    @BindView(R.id.rbCheck2)
    RadioButton rbCheck2;
    @BindView(R.id.llType2)
    LinearLayout llType2;
    @BindView(R.id.etInitPwd)
    EditText etInitPwd;
    @BindView(R.id.llContainer2)
    LinearLayout llContainer2;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.etMaxPwd)
    EditText etMaxPwd;
    @BindView(R.id.btnSetting)
    Button btnSetting;
    @BindView(R.id.llSetContainer)
    LinearLayout llSetContainer;
    //提示是否连接互联网
    private boolean showInt = true;
    private String mType = "";

    @Override
    protected int getContentView() {
        return R.layout.activity_max_oss_pwd;
    }

    @Override
    protected void initViews() {
        initHeaderView();

    }

    @Override
    protected void initData() {
        initIntent();
        initListener();
    }


    private void initListener() {
        etUserName.setOnFocusChangeListener(this);
        etPwd.setOnFocusChangeListener(this);
        etInitPwd.setOnFocusChangeListener(this);
        rbCheck1.setOnCheckedChangeListener(this);
        rbCheck2.setOnCheckedChangeListener(this);
    }

    private void initIntent() {
        Intent intent = getIntent();
        mType = intent.getStringExtra("type");
        if (!TextUtils.isEmpty(mType)) {
            switch (mType) {
                case "0":
                    CommenUtils.hideAllView(View.GONE, llContainer2);
                    setRb1Check(true);
                    break;
                case "1":
                    CommenUtils.hideAllView(View.GONE, llContainer1);
                    setRb1Check(false);
                    break;
            }
        }
    }

    private void initHeaderView() {
        initToobar(toolbar);
        tvTitle.setText(R.string.m获取控制密码);

        tvOssPwd.setText(String.format("OSS %s", getString(R.string.register_xml_password)));
    }


    public void btnLogin() {
        if (TextUtils.isEmpty(etUserName.getText().toString())) {
            toast(R.string.DatalogCheckAct_username_pwd_empty);
            return;
        }
        if (TextUtils.isEmpty(etPwd.getText().toString())) {
            toast(R.string.DatalogCheckAct_username_pwd_empty);
            return;
        }
        //正式登录
        Mydialog.Show(this);
        ShineToolsApi.getOssLoginServer(mContext,etUserName.getText().toString().trim() ,MD5andKL.encryptPassword(etPwd.getText().toString().trim()),  new RequestCallback((LoginActivity) mContext));

    }

    /**
     * 设置rbCheck1 状态，以及rbCheck2相反状态
     *
     * @param isCheck
     */
    public void setRb1Check(boolean isCheck) {
        rbCheck2.setChecked(!isCheck);
        rbCheck1.setChecked(isCheck);
    }

    @OnClick({R.id.btnLogin, R.id.btnSetting, R.id.llType1, R.id.llType2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llType1:
                setRb1Check(true);
                break;
            case R.id.llType2:
                setRb1Check(false);
                break;
            case R.id.btnLogin:
                //判断哪种方式1：登录；2：初始密码
                boolean isRb1Check = rbCheck1.isChecked();
                if (isRb1Check) {
                    if (showInt) {
                        //提示连接互联网
                        CircleDialogUtils.showCommentDialog(MaxOssPwdActivity.this, getString(R.string.m连接互联网), getString(R.string.m是否切换网络), getString(R.string.m切换), getString(R.string.all_no), Gravity.CENTER, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                                startActivity(intent);
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                btnLogin();
                            }
                        });
                    } else {
                        btnLogin();
                    }
                    showInt = false;
                } else {
                    String initPwd = "oss" + new SimpleDateFormat("yyyyMMdd").format(new Date());
                    if (initPwd.equals(String.valueOf(etInitPwd.getText()))) {
                        llSetContainer.setVisibility(View.VISIBLE);
                    } else {
                        toast(R.string.all_failed);
                    }
                }
                break;
            case R.id.btnSetting:
                String maxPwd = etMaxPwd.getText().toString();
                if (TextUtils.isEmpty(maxPwd)) {
                    toast(R.string.Login_password_hint);
                    return;
                }
                //保存密码
                SharedPreferencesUnit.getInstance(mContext).put(GlobalConstant.MAX_PWD, maxPwd.trim());
                new CircleDialog.Builder()
                        .setWidth(0.7f)
                        .setTitle(getString(R.string.m设置成功))
                        .setText(getString(R.string.m本地调试工具密码为) + ":" + maxPwd.trim())
                        .setPositive(getString(R.string.all_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        })
                        .show(getSupportFragmentManager());
                break;
        }
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            if (v == etUserName || v == etPwd) {
                setRb1Check(true);
            } else {
                setRb1Check(false);
            }
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            if (buttonView == rbCheck1) {
                setRb1Check(true);
                if (etPwd.isFocusable() && etPwd.isFocused()) {
                    CommenUtils.showSoftInputFromWindow(this, etPwd);
                } else {
                    CommenUtils.showSoftInputFromWindow(this, etUserName);
                }
            }
            if (buttonView == rbCheck2) {
                setRb1Check(false);
                CommenUtils.showSoftInputFromWindow(this, etInitPwd);
            }
        }
    }

    @Override
    public void startView() {

    }

    @Override
    public void loadingView() {

    }

    @Override
    public void successView(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            int result = jsonObject.getInt("result");
            if (result == 1) {
                JSONObject obj = jsonObject.getJSONObject("obj");
                //用户类型
                int userType = obj.getInt("userType");
                if (userType != 0) {
                    toast(R.string.m登录成功);
                    llSetContainer.setVisibility(View.VISIBLE);
                } else {
                    toast(R.string.all_login_error);
                }
            } else {
                toast(R.string.all_login_error);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void errorView(String errorMsg) {

    }


}
