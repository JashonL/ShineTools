package com.growatt.shinetools.module;

import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.growatt.shinetools.R;
import com.growatt.shinetools.ShineToosApplication;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.module.localbox.max.Max230KTL3HVToolActivity;
import com.growatt.shinetools.module.localbox.max.MaxMacModMidToolActivity;
import com.growatt.shinetools.module.localbox.old.ToolMainOldInvActivity;
import com.growatt.shinetools.module.localbox.sph.SPHSPAToolActivity;
import com.growatt.shinetools.module.localbox.tlx.TLXTLEToolActivity;
import com.growatt.shinetools.module.localbox.tlxh.TL3XHToolActivity;
import com.growatt.shinetools.module.localbox.tlxh.TLXHToolActivity;
import com.growatt.shinetools.module.localbox.ustool.USToolsMainActivityV2;
import com.growatt.shinetools.utils.ActivityUtils;
import com.growatt.shinetools.utils.AppSystemUtils;
import com.growatt.shinetools.utils.CircleDialogUtils;
import com.growatt.shinetools.utils.SharedPreferencesUnit;

import butterknife.BindView;
import butterknife.OnClick;

import static com.growatt.shinetools.constant.GlobalConstant.END_USER;
import static com.growatt.shinetools.constant.GlobalConstant.KEY_END_USER_PWD;
import static com.growatt.shinetools.constant.GlobalConstant.KEY_MODIFY_PWD;

public class DeviceTypeChooseActivity extends BaseActivity implements Toolbar.OnMenuItemClickListener {


    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.header_title)
    LinearLayout headerTitle;


    private DialogFragment dialogFragment;
    private int user_type;

    @Override
    protected int getContentView() {
        return R.layout.activity_device_choose_type;
    }

    @Override
    protected void initViews() {
        initToobar(toolbar);
        tvTitle.setText(R.string.android_key1322);
        user_type = ShineToosApplication.getContext().getUser_type();
        if (user_type == END_USER) {
            toolbar.inflateMenu(R.menu.end_user_menu);
        } else {
            toolbar.inflateMenu(R.menu.maintain_login_menu);
        }
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.tittle_more));
    }

    @Override
    protected void initData() {
        if (user_type == END_USER) {
            //弹出是否要修改密码
            String pwd = SharedPreferencesUnit.getInstance(this).get(KEY_END_USER_PWD);
            boolean isShow = SharedPreferencesUnit.getInstance(this).getBoolean(KEY_MODIFY_PWD);
            if (TextUtils.isEmpty(pwd) && !isShow) {
                CircleDialogUtils.showCommentDialog(this, getString(R.string.android_key2263),
                        getString(R.string.android_key3112), getString(R.string.android_key1935),
                        getString(R.string.android_key2152), Gravity.CENTER, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AppSystemUtils.modifyPwd(DeviceTypeChooseActivity.this);
                            }

                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SharedPreferencesUnit.getInstance(DeviceTypeChooseActivity.this).putBoolean(KEY_MODIFY_PWD, true);
                            }
                        });

            }
        }

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_reset:
                AppSystemUtils.modifyPwd(DeviceTypeChooseActivity.this);
                break;
            case R.id.menu_logout:
                dialogFragment = CircleDialogUtils.showCommentDialog(this, getString(R.string.android_key2263),
                        getString(R.string.android_key2212), getString(R.string.android_key1935), getString(R.string.android_key2152), Gravity.CENTER, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AppSystemUtils.logout(DeviceTypeChooseActivity.this);
                                dialogFragment.dismiss();
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogFragment.dismiss();
                            }
                        });


                break;
        }
        return true;
    }




    @OnClick({R.id.ll_type_mic_min, R.id.ll_type_min_tl_xh, R.id.ll_type_min_tl_xh_us, R.id.ll_type_mod_mid_mac,
            R.id.ll_mod_tl3_xh, R.id.ll_type_max_tl3_lvmv, R.id.ll_type_max_tl3_x_hv, R.id.ll_type_olb,
            R.id.ll_type_spa_tl_bl, R.id.ll_spa_tl3_bh, R.id.ll_type_sph, R.id.ll_type_sph_tl3_bh,
            R.id.ll_type_sph_tl_bl_us})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_type_mic_min:
                //                ActivityUtils.gotoActivity(DeviceTypeChooseActivity.this, TLXToolMainActivity.class,false);

                Intent intent4=new Intent(this, TLXTLEToolActivity.class);
                intent4.putExtra("title","MIC、MIN TL-X/XE");
                ActivityUtils.startActivity(DeviceTypeChooseActivity.this,intent4,false);
                break;
            case R.id.ll_type_min_tl_xh:
                //                ActivityUtils.gotoActivity(DeviceTypeChooseActivity.this, TLXHToolMainActivity.class,false);
                Intent intent5=new Intent(this, TLXHToolActivity.class);
                intent5.putExtra("title","MIN TL-XH");
                ActivityUtils.startActivity(DeviceTypeChooseActivity.this,intent5,false);
                break;
            case R.id.ll_type_min_tl_xh_us:
                ActivityUtils.gotoActivity(DeviceTypeChooseActivity.this, USToolsMainActivityV2.class,false);
                break;
            case R.id.ll_type_max_tl3_lvmv:
                Intent intent7=new Intent(this, MaxMacModMidToolActivity.class);
                intent7.putExtra("title","MOD MID MAC");
                ActivityUtils.startActivity(DeviceTypeChooseActivity.this,intent7,false);
                break;
            case R.id.ll_type_mod_mid_mac:
                //                ActivityUtils.gotoActivity(DeviceTypeChooseActivity.this, MaxMainActivity.class,false);
                Intent intent=new Intent(this, MaxMacModMidToolActivity.class);
                intent.putExtra("title","MOD TL3-XH");
                ActivityUtils.startActivity(DeviceTypeChooseActivity.this,intent,false);
                break;
            case R.id.ll_mod_tl3_xh:
                //                ActivityUtils.gotoActivity(DeviceTypeChooseActivity.this, TL3XHMainActivity.class,false);
                Intent intent3=new Intent(this, TL3XHToolActivity.class);
                intent3.putExtra("title","MAX TL3 LV/MV");
                ActivityUtils.startActivity(DeviceTypeChooseActivity.this,intent3,false);
                break;


            case R.id.ll_type_max_tl3_x_hv:

//                ActivityUtils.gotoActivity(DeviceTypeChooseActivity.this, MaxMain1500VActivity.class,false);
                Intent intent1=new Intent(this, Max230KTL3HVToolActivity.class);
                intent1.putExtra("title","MAX TL3-X HV");
                ActivityUtils.startActivity(DeviceTypeChooseActivity.this,intent1,false);
                break;
            case R.id.ll_type_olb:
                ActivityUtils.gotoActivity(DeviceTypeChooseActivity.this, ToolMainOldInvActivity.class,false);
                break;
            case R.id.ll_type_spa_tl_bl:
            case R.id.ll_spa_tl3_bh:
            case R.id.ll_type_sph:
            case R.id.ll_type_sph_tl3_bh:
            case R.id.ll_type_sph_tl_bl_us:
                //                ActivityUtils.gotoActivity(DeviceTypeChooseActivity.this, MixToolMainActivity.class,false);
                Intent intent8=new Intent(this, SPHSPAToolActivity.class);
                intent8.putExtra("title","SPH/SPA");
                ActivityUtils.startActivity(DeviceTypeChooseActivity.this,intent8,false);
                break;
        }
    }
}
