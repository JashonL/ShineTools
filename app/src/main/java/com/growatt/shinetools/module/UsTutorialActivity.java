package com.growatt.shinetools.module;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.growatt.shinetools.R;
import com.growatt.shinetools.ShineToosApplication;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.module.localbox.ustool.USToolMainActivity;
import com.growatt.shinetools.module.localbox.ustool.USToolsMainActivityV2;
import com.growatt.shinetools.utils.ActivityUtils;
import com.growatt.shinetools.utils.AppSystemUtils;
import com.growatt.shinetools.utils.CircleDialogUtils;
import com.growatt.shinetools.utils.CommenUtils;
import com.growatt.shinetools.utils.SharedPreferencesUnit;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.growatt.shinetools.constant.GlobalConstant.END_USER;
import static com.growatt.shinetools.constant.GlobalConstant.KEY_END_USER_PWD;
import static com.growatt.shinetools.constant.GlobalConstant.KEY_MODIFY_PWD;
import static com.growatt.shinetools.constant.PermissionConstant.RC_LOCATION;

public class UsTutorialActivity extends BaseActivity implements Toolbar.OnMenuItemClickListener, EasyPermissions.PermissionCallbacks  {
    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.header_title)
    LinearLayout headerTitle;
    @BindView(R.id.tv_current_wifi)
    TextView tvCurrentWifi;
    @BindView(R.id.tv_wifi_ssid)
    AppCompatTextView tvWifiSsid;
    @BindView(R.id.tv_fresh)
    TextView tvFresh;
    @BindView(R.id.tv_arrow)
    TextView tvArrow;
    @BindView(R.id.card_ok)
    CardView cardOk;

    private DialogFragment dialogFragment;
    private DialogFragment gpsDialogFragment;

    private int user_type;

    @Override
    protected int getContentView() {
        return R.layout.activity_us_turial;
    }

    //wifi名称
    private String currentSSID;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, final Intent intent) {

            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case ConnectivityManager.CONNECTIVITY_ACTION:
                    case "android.net.wifi.CONFIGURED_NETWORKS_CHANGE":
                    case "android.net.wifi.LINK_CONFIGURATION_CHANGED":
                    case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                    case WifiManager.SUPPLICANT_STATE_CHANGED_ACTION:
                        checkWifiNetworkStatus();
                        break;
                }
            }
        }
    };


    @Override
    protected void initViews() {
        user_type = ShineToosApplication.getContext().getUser_type();
        initToobar(toolbar);
        tvTitle.setText("WLAN");
        if (user_type == END_USER) {
            toolbar.inflateMenu(R.menu.end_user_menu);
        } else {
            toolbar.inflateMenu(R.menu.maintain_login_menu);
        }
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.tittle_more));
        user_type = ShineToosApplication.getContext().getUser_type();
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
                                AppSystemUtils.modifyPwd(UsTutorialActivity.this);
                            }

                        }, view -> SharedPreferencesUnit.getInstance(UsTutorialActivity.this).putBoolean(KEY_MODIFY_PWD, true));

            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        initWifi();
        checkCameraPermissions();
    }

    /**
     * 广播接收器，接收连接wifi的广播
     */
    private void initWifi() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
//        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction("android.net.wifi.CONFIGURED_NETWORKS_CHANGE");
        filter.addAction("android.net.wifi.LINK_CONFIGURATION_CHANGED");
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        registerReceiver(mBroadcastReceiver, filter);
    }


    public void checkWifiNetworkStatus() {
        try {
            if (CommenUtils.isWiFi(this)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//8.1
                    gpsStatus();
                } else {
                    currentSSID = CommenUtils.getWifiSsid(this);
                }
                setWiFiName();
            } else {
                currentSSID = null;
                tvWifiSsid.setText(R.string.android_key2259);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 判断Gps是否打开
     */
    private void gpsStatus() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (lm != null) {
            boolean ok = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (ok) {
                try {
                    currentSSID = CommenUtils.getWifiSsid(this);
                    setWiFiName();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                showGpsDialog();
            }
        }
    }


    /**
     * 开启GPS弹框
     */

    private void showGpsDialog() {
        if (gpsDialogFragment == null) {
            gpsDialogFragment = CircleDialogUtils.showCommentDialog(this,
                    getString(R.string.android_key2263), getString(R.string.android_key2258),
                    getString(R.string.android_key1935), getString(R.string.android_key2152), Gravity.CENTER, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            gpsDialogFragment.dismiss();
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    });
        }

    }


    private void setWiFiName() {
        if (TextUtils.isEmpty(currentSSID))
            tvWifiSsid.setText(R.string.android_key2259);
        else tvWifiSsid.setText(currentSSID);
    }


    private void toConnect() {
        Intent intent1 = new Intent(this, USToolMainActivity.class);
        ActivityUtils.startActivity(this, intent1, false);
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_reset:
                AppSystemUtils.modifyPwd(UsTutorialActivity.this);
                break;
            case R.id.menu_logout:
                dialogFragment = CircleDialogUtils.showCommentDialog(this, getString(R.string.android_key2263),
                        getString(R.string.android_key2212), getString(R.string.android_key1935), getString(R.string.android_key2152), Gravity.CENTER, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AppSystemUtils.logout(UsTutorialActivity.this);
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



    @OnClick({R.id.card_ok,R.id.tv_fresh,R.id.tv_arrow})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.card_ok:
                ActivityUtils.gotoActivity(UsTutorialActivity.this, USToolsMainActivityV2.class,false);
                break;
            case R.id.tv_fresh:
            case R.id.tv_arrow:
                ActivityUtils.toWifiSet(this);
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    /**
     * 检测拍摄权限
     */
    @AfterPermissionGranted(RC_LOCATION)
    private void checkCameraPermissions() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {//有权限
            checkWifiNetworkStatus();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.android_key3127),
                    RC_LOCATION, perms);
        }
    }
}
