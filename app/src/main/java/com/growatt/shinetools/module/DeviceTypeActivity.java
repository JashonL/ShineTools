package com.growatt.shinetools.module;

import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.growatt.shinetools.R;
import com.growatt.shinetools.ShineToosApplication;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.module.localbox.max.Max230KTL3HVToolActivity;
import com.growatt.shinetools.module.localbox.max.MaxMacModMidToolActivity;
import com.growatt.shinetools.module.localbox.max.MaxxToolActivity;
import com.growatt.shinetools.module.localbox.mix.MixToolMainActivity;
import com.growatt.shinetools.module.localbox.old.ToolMainOldInvActivity;
import com.growatt.shinetools.module.localbox.tlxh.TL3XHToolActivity;
import com.growatt.shinetools.module.localbox.tlxh.TLXHToolActivity;
import com.growatt.shinetools.module.localbox.tlx.TLXTLEToolActivity;
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

public class DeviceTypeActivity extends BaseActivity implements Toolbar.OnMenuItemClickListener {
    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.header_title)
    LinearLayout headerTitle;
    @BindView(R.id.tv_select)
    TextView tvSelect;
    @BindView(R.id.tv_max)
    TextView tvMax;
    @BindView(R.id.tv_type_max)
    TextView tvTypeMax;
    @BindView(R.id.ll_type_max)
    LinearLayout llTypeMax;
    @BindView(R.id.tv_type_230)
    TextView tvType230;
    @BindView(R.id.ll_type_230)
    LinearLayout llType230;
    @BindView(R.id.tv_type_max_x)
    TextView tvTypeMaxX;
    @BindView(R.id.ll_type_max_x)
    LinearLayout llTypeMaxX;
    @BindView(R.id.tv_type_tl3_xh)
    TextView tvTypeTl3Xh;
    @BindView(R.id.ll_type_tl3_xh)
    LinearLayout llTypeTl3Xh;
    @BindView(R.id.tv_min)
    TextView tvMin;
    @BindView(R.id.tv_type_tl_x)
    TextView tvTypeTlX;
    @BindView(R.id.ll_type_tl_x)
    LinearLayout llTypeTlX;
    @BindView(R.id.tv_tl_xh)
    TextView tvTlXh;
    @BindView(R.id.ll_tl_xh)
    LinearLayout llTlXh;
    @BindView(R.id.tv_type_tlx_us)
    TextView tvTypeTlxUs;
    @BindView(R.id.ll_type_tlx_us)
    LinearLayout llTypeTlxUs;
    @BindView(R.id.tv_type_olb)
    TextView tvTypeOlb;
    @BindView(R.id.ll_type_olb)
    LinearLayout llTypeOlb;
    @BindView(R.id.tv_type_sph)
    TextView tvTypeSph;
    @BindView(R.id.ll_type_sph)
    LinearLayout llTypeSph;

    private DialogFragment dialogFragment;
    private int user_type;

    @Override
    protected int getContentView() {
        return R.layout.activity_device_type;
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
                                AppSystemUtils.modifyPwd(DeviceTypeActivity.this);
                            }

                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SharedPreferencesUnit.getInstance(DeviceTypeActivity.this).putBoolean(KEY_MODIFY_PWD, true);
                            }
                        });

            }
        }

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_reset:
                AppSystemUtils.modifyPwd(DeviceTypeActivity.this);
                break;
            case R.id.menu_logout:
                dialogFragment = CircleDialogUtils.showCommentDialog(this, getString(R.string.android_key2263),
                        getString(R.string.android_key2212), getString(R.string.android_key1935), getString(R.string.android_key2152), Gravity.CENTER, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AppSystemUtils.logout(DeviceTypeActivity.this);
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





    @OnClick({R.id.ll_type_max, R.id.ll_type_230, R.id.ll_type_max_x, R.id.ll_type_tl3_xh, R.id.ll_type_tl_x, R.id.ll_tl_xh, R.id.ll_type_tlx_us, R.id.ll_type_olb, R.id.ll_type_sph})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_type_max:
//                ActivityUtils.gotoActivity(DeviceTypeActivity.this, MaxMainActivity.class,false);
                Intent intent=new Intent(this,MaxMacModMidToolActivity.class);
                intent.putExtra("title","MAX/MAC/MOD/MID");
                ActivityUtils.startActivity(DeviceTypeActivity.this,intent,false);
                break;
            case R.id.ll_type_230:

//                ActivityUtils.gotoActivity(DeviceTypeActivity.this, MaxMain1500VActivity.class,false);
                Intent intent1=new Intent(this,Max230KTL3HVToolActivity.class);
                intent1.putExtra("title","MAX 230KTL3 HV");
                ActivityUtils.startActivity(DeviceTypeActivity.this,intent1,false);
                break;
            case R.id.ll_type_max_x:
//                ActivityUtils.gotoActivity(DeviceTypeActivity.this, MaxXMainActivity.class,false);
                Intent intent2=new Intent(this,MaxxToolActivity.class);
                intent2.putExtra("title","MAX-X");
                ActivityUtils.startActivity(DeviceTypeActivity.this,intent2,false);
                break;
            case R.id.ll_type_tl3_xh:
//                ActivityUtils.gotoActivity(DeviceTypeActivity.this, TL3XHMainActivity.class,false);
                Intent intent3=new Intent(this, TL3XHToolActivity.class);
                intent3.putExtra("title","TL3-XH");
                ActivityUtils.startActivity(DeviceTypeActivity.this,intent3,false);
                break;
            case R.id.ll_type_tl_x:
//                ActivityUtils.gotoActivity(DeviceTypeActivity.this, TLXToolMainActivity.class,false);

                Intent intent4=new Intent(this, TLXTLEToolActivity.class);
                intent4.putExtra("title","TL-X/TL-E");
                ActivityUtils.startActivity(DeviceTypeActivity.this,intent4,false);
                break;
            case R.id.ll_tl_xh:
//                ActivityUtils.gotoActivity(DeviceTypeActivity.this, TLXHToolMainActivity.class,false);
                Intent intent5=new Intent(this, TLXHToolActivity.class);
                intent5.putExtra("title","TL-XH");
                ActivityUtils.startActivity(DeviceTypeActivity.this,intent5,false);
                break;
            case R.id.ll_type_tlx_us:
                ActivityUtils.gotoActivity(DeviceTypeActivity.this, USToolsMainActivityV2.class,false);
                break;
            case R.id.ll_type_olb:
                ActivityUtils.gotoActivity(DeviceTypeActivity.this, ToolMainOldInvActivity.class,false);
                break;
            case R.id.ll_type_sph:
                ActivityUtils.gotoActivity(DeviceTypeActivity.this, MixToolMainActivity.class,false);
                break;
        }
    }
}
