package com.growatt.shinetools.module.localbox.ustool;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.growatt.shinetools.R;
import com.growatt.shinetools.adapter.WiFiListAdapter;
import com.growatt.shinetools.base.DemoBase;
import com.growatt.shinetools.bean.AccessPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.growatt.shinetools.constant.PermissionConstant.RC_LOCATION;


public class WiFiListActivity extends DemoBase implements BaseQuickAdapter.OnItemClickListener, EasyPermissions.PermissionCallbacks {

    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.ivLeft)
    ImageView ivLeft;
    @BindView(R.id.ivRight)
    ImageView ivRight;
    @BindView(R.id.relativeLayout1)
    RelativeLayout relativeLayout1;
    @BindView(R.id.headerView)
    LinearLayout headerView;
    @BindView(R.id.rv_wifi_list)
    RecyclerView rvWifiList;
    @BindView(R.id.sl_pull)
    SwipeRefreshLayout srlPull;

    private TextView tvSelectOhter;


    private WifiManager wifiManager;
    private NetworkInfo lastNetworkInfo;
    private WifiInfo lastWifiInfo;
    private WifiConfiguration lastWifiConfiguration;
    private List<AccessPoint> lastAccessPoints = new ArrayList<>();
    private int lastPortalNetworkId = AccessPoint.INVALID_NETWORK_ID;
    private WiFiListAdapter adapter;


    private Timer mTimer;//定时器
    private TimerTask timerTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_list);
        ButterKnife.bind(this);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            finish();
            return;
        }
        initViews();
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        wifiManager.setWifiEnabled(true);
        lastNetworkInfo = getActiveNetworkInfo();
        lastWifiInfo = wifiManager.getConnectionInfo();
        updateAccessPoints();
    }


    private void initViews() {
        //头部标题
        tvTitle.setText(R.string.wifi路由器列表);
        //列表控件
        rvWifiList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WiFiListAdapter(R.layout.item_wifi_info, new ArrayList<>());
        rvWifiList.setAdapter(adapter);
        View rvHeaderView = LayoutInflater.from(this).inflate(R.layout.item_wifi_header_view, rvWifiList, false);
        TextView tvHeaderTitle = rvHeaderView.findViewById(R.id.tv_title);
        tvHeaderTitle.setText(R.string.m225请选择);
        adapter.addHeaderView(rvHeaderView);
//        View rvFooterView = LayoutInflater.from(this).inflate(R.layout.item_wifi_foot_view, rvWifiList, false);
//        tvSelectOhter = rvFooterView.findViewById(R.id.tv_select_other);
//        adapter.addFooterView(rvFooterView);
        adapter.setOnItemClickListener(this);
    /*    tvSelectOhter.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
            startActivity(intent);
        });*/

        srlPull.setColorSchemeColors(ContextCompat.getColor(this, R.color.headerView));
        srlPull.setOnRefreshListener(this::updateAccessPoints);
    }


    @OnClick({R.id.ivLeft})
    public void onClickView(View view) {
        switch (view.getId()) {
            case R.id.ivLeft:
                finish();
                break;
        }

    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onStop() {
        super.onStop();
    }


    private void updateAccessPoints() {
        srlPull.setRefreshing(false);
        List<AccessPoint> accessPoints = new ArrayList<>();
        List<ScanResult> scanResults = wifiManager.getScanResults();
        if (lastWifiInfo != null && lastWifiInfo.getNetworkId() != AccessPoint.INVALID_NETWORK_ID) {
            lastWifiConfiguration = getWifiConfigurationForNetworkId(lastWifiInfo.getNetworkId());
        }
        if (scanResults != null) {

            for (ScanResult scanResult : scanResults) {
                if (TextUtils.isEmpty(scanResult.SSID)) {
                    continue;
                }
                AccessPoint accessPoint = new AccessPoint(this.getApplicationContext(), scanResult);
                if (accessPoints.contains(accessPoint)) {
                    continue;
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                }
                List<WifiConfiguration> wifiConfigurations = wifiManager.getConfiguredNetworks();
                if (wifiConfigurations != null) {
                    for (WifiConfiguration config : wifiConfigurations) {
                        if (accessPoint.getQuotedSSID().equals(config.SSID)) {
                            accessPoint.setWifiConfiguration(config);
                        }
                    }
                }
                if (lastWifiInfo != null && lastNetworkInfo != null) {
                    accessPoint.update(lastWifiConfiguration, lastWifiInfo, lastNetworkInfo);
                }
                int frequency = accessPoint.frequency;
                if ((frequency > 4900 && frequency < 5900) || accessPoint.ssid.toUpperCase().endsWith("5G")) {
                    continue;
                }
                accessPoints.add(accessPoint);

            }
        }
        Collections.sort(accessPoints);
        lastAccessPoints = accessPoints;
        adapter.replaceData(accessPoints);
    }





    /**
     * 根据 NetworkId 获取 WifiConfiguration 信息
     *
     * @param networkId 需要获取 WifiConfiguration 信息的 networkId
     * @return 指定 networkId 的 WifiConfiguration 信息
     */
    private WifiConfiguration getWifiConfigurationForNetworkId(int networkId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
        }
        final List<WifiConfiguration> configs = wifiManager.getConfiguredNetworks();
        if (configs != null) {
            for (WifiConfiguration config : configs) {
                if (lastWifiInfo != null && networkId == config.networkId) {
                    return config;
                }
            }
        }
        return null;
    }

    /**
     * 获取当前网络信息
     */
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            return cm.getActiveNetworkInfo();
        }
        return null;
    }




    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        AccessPoint accessPoint = this.adapter.getData().get(position);
        if (accessPoint != null){
//            showDialog(accessPoint);
            Intent i=new Intent();
            i.putExtra("wifiName",accessPoint.ssid);
            setResult(RESULT_OK,i);
            finish();
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
    private void checkCameraPermissions(){
        String[] perms = {Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {//有权限

        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.android_key2385),
                    RC_LOCATION, perms);
        }
    }


}
