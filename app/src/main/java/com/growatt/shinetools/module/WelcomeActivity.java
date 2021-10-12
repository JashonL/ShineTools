package com.growatt.shinetools.module;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.growatt.shinetools.MainActivity;
import com.growatt.shinetools.R;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.utils.ActivityUtils;
import com.growatt.shinetools.utils.SharedPreferencesUnit;

import butterknife.BindView;
import butterknife.OnClick;

import static com.growatt.shinetools.utils.AppSystemUtils.KEY_APP_WELCOME;

public class WelcomeActivity extends BaseActivity {
    @BindView(R.id.iv_pic)
    ImageView ivPic;
    @BindView(R.id.tv_text_title)
    TextView tvTextTitle;
    @BindView(R.id.tv_text_content)
    TextView tvTextContent;
    @BindView(R.id.cb_no_again)
    CheckBox cbNoAgain;
    @BindView(R.id.btn_login)
    Button btnLogin;

    @Override
    protected int getContentView() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void initViews() {
        
    }

    @Override
    protected void initData() {

    }


    @OnClick(R.id.btn_login)
    public void onViewClicked() {
        SharedPreferencesUnit.getInstance(this).putBoolean(KEY_APP_WELCOME, cbNoAgain.isChecked());
        ActivityUtils.gotoActivity(this, MainActivity.class, true);
    }
}
