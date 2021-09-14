package com.growatt.shinetools.module;

import android.Manifest;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.growatt.shinetools.R;
import com.growatt.shinetools.adapter.ComenStringAdapter;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.constant.DebugConstant;
import com.growatt.shinetools.constant.GlobalConstant;
import com.growatt.shinetools.utils.ActivityUtils;
import com.growatt.shinetools.utils.CircleDialogUtils;
import com.growatt.shinetools.utils.MyToastUtils;
import com.growatt.shinetools.utils.ShineToolsApi;
import com.growatt.shinetools.utils.datalogupdata.DataLogUpdateCallback;
import com.growatt.shinetools.utils.datalogupdata.DatalogUpDateBean;
import com.growatt.shinetools.utils.datalogupdata.DatalogUpdataManager;
import com.growatt.shinetools.utils.datalogupdata.DatalogUpdataUtils;
import com.growatt.shinetools.utils.datalogupdata.FileDownLoadManager;
import com.growatt.shinetools.widget.CommonPopupWindow;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.growatt.shinetools.constant.DebugConstant.TYPE_USB_WIFI;
import static com.growatt.shinetools.constant.PermissionConstant.RC_LOCATION;

public class ManulInputActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {
    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_host_name)
    TextView tvHostName;
    @BindView(R.id.ll_server_hostname)
    LinearLayout llServerHostName;
    @BindView(R.id.header_title)
    LinearLayout headerTitle;
    @BindView(R.id.iv_ewm)
    ImageView ivEwm;
    @BindView(R.id.et_input_sn)
    EditText etInputSn;
    @BindView(R.id.ll_scan)
    LinearLayout llScan;
    @BindView(R.id.ll_input_number)
    LinearLayout llInputNumber;
    @BindView(R.id.btnNext)
    Button btnNext;
    @BindView(R.id.tv_host_value)
    TextView tvHostValue;


    private int type;

    private CommonPopupWindow wifiWindow;

    @Override
    protected int getContentView() {
        return R.layout.activity_manul_input;
    }

    @Override
    protected void initViews() {
        initToobar(toolbar);
        tvTitle.setText(R.string.android_key1180);
//        openWifi();
    }


    @Override
    protected void initData() {
        Intent intent = getIntent();
        type = intent.getIntExtra(DebugConstant.KEY_WIFI_TYPE, 0);
        String serialnum = intent.getStringExtra(GlobalConstant.SCAN_RESULT);
        if (!TextUtils.isEmpty(serialnum)) {
            etInputSn.setText(serialnum);
        }

        if (type == TYPE_USB_WIFI) {
            tvHostName.setVisibility(View.GONE);
            llServerHostName.setVisibility(View.GONE);
        } else {
            tvHostName.setVisibility(View.VISIBLE);
            llServerHostName.setVisibility(View.VISIBLE);
        }
    }


    @OnClick({R.id.ll_scan, R.id.btnNext, R.id.ll_server_hostname})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_scan:
                Intent intent = new Intent(this, CustomScanActivity.class);
                intent.putExtra(DebugConstant.KEY_WIFI_TYPE, type);
                ActivityUtils.startActivity(this, intent, true);
                break;
            case R.id.btnNext:
                if (type == TYPE_USB_WIFI) {
                    checkCameraPermissions();
                } else {
                    String sn = etInputSn.getText().toString();
                    if (TextUtils.isEmpty(sn)) {
                        MyToastUtils.toast(R.string.android_key1728);
                        return;
                    }
                    String host = tvHostValue.getText().toString();
                    if (TextUtils.isEmpty(host)) {
                        MyToastUtils.toast(R.string.android_key3071);
                        return;
                    }

                    String url = "https://" + repalceApi(host) + ShineToolsApi.DATALOG_DETAIL;
                    checkUpdata(url, sn);

                }

                break;
            case R.id.ll_server_hostname:
                setSsid(llServerHostName);
                break;
        }
    }


    private String repalceApi(String url) {
        String sPlace = url;
        if ("server-cn.growatt.com".equals(url)) {
            sPlace = "server-cn-api.growatt.com";
        } else if ("server.growatt.com".equals(url)) {
            sPlace = "server-api.growatt.com";
        }else if ("server.smten.com".equals(url)){
            sPlace = "server-api.smten.com";
        }
        return sPlace;

    }


    /*制冷弹框*/
    private void setSsid(View dropView) {
        if (wifiWindow == null) {
            wifiWindow = new CommonPopupWindow(this, R.layout.popuwindow_comment_list_layout, dropView.getWidth(), LinearLayout.LayoutParams.WRAP_CONTENT) {
                @Override
                protected void initView() {
                    String[] servers = {"server-cn.growatt.com", "server.growatt.com", "server-us.growatt.com","server.smten.com"};
                    List<String> datas = Arrays.asList(servers);
                    View view = getContentView();
                    RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
                    recyclerView.setLayoutManager(new LinearLayoutManager(ManulInputActivity.this, LinearLayoutManager.VERTICAL, false));
                    ComenStringAdapter adapter = new ComenStringAdapter(R.layout.item_text, datas);
                    recyclerView.setAdapter(adapter);
                    adapter.setOnItemClickListener((adapter1, view1, position) -> {
                        String item = adapter.getItem(position);
                        tvHostValue.setText(item);
                        wifiWindow.getPopupWindow().dismiss();
                    });
                }

                @Override
                protected void initEvent() {
                }
            };
        }
        int[] location = new int[2];
        dropView.getLocationOnScreen(location);
        wifiWindow.showAsDropDown(dropView, 0, 0);
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
            toConnect();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.android_key3127),
                    RC_LOCATION, perms);
        }
    }


    private void toConnect() {
        String sn = etInputSn.getText().toString();
        Intent intent1 = new Intent(this, ConnetApHostActivity.class);
        intent1.putExtra(DebugConstant.KEY_WIFI_TYPE, type);
        intent1.putExtra(GlobalConstant.SCAN_RESULT, sn);
        ActivityUtils.startActivity(this, intent1, false);
    }


    private void checkUpdata(String url, String datalogSn) {
        //检测下载升级包
        DatalogUpdataManager datalogUpdataManager = DatalogUpdataUtils.checkUpdata(ManulInputActivity.this, url, datalogSn);
        datalogUpdataManager.checkNewVersion(new DataLogUpdateCallback() {
            @Override
            protected void onBefore() {
                super.onBefore();
                datalogUpdataManager.showDialogFragment();
            }

            @Override
            protected void hasNewVersion(DatalogUpDateBean updateApp, DatalogUpdataManager updateAppManager) {
                super.hasNewVersion(updateApp, updateAppManager);

                datalogUpdataManager.dissmissDialog();

                CircleDialogUtils.showCommentDialog(ManulInputActivity.this, getString(R.string.android_key3031), getString(R.string.android_key3032), getString(R.string.android_key1935), getString(R.string.android_key2152), Gravity.CENTER, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        datalogUpdataManager.startDownLoad(new FileDownLoadManager.DownloadCallback() {
                            @Override
                            public void onStart() {
                                datalogUpdataManager.showProgressFragment();
                            }

                            @Override
                            public void onProgress(float progress, int total, int current) {
                                current++;
                                datalogUpdataManager.setProgress(progress, total, current);
                            }


                            @Override
                            public void setMax(long totalSize) {

                            }

                            @Override
                            public void onFinish() {
                                datalogUpdataManager.dissmissDialog();
                                datalogUpdataManager.dissmissDownDialog();
                                checkCameraPermissions();
                            }


                            @Override
                            public void onError(String msg) {
                                MyToastUtils.toast(msg);
                                datalogUpdataManager.dissmissDialog();
                                datalogUpdataManager.dissmissDownDialog();
                            }

                        });


                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });




            }


            @Override
            protected void noNewVirsion(String error) {
                super.noNewVirsion(error);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        datalogUpdataManager.dissmissDialog();
                        checkCameraPermissions();
                    }
                }, 1000);
            }

            @Override
            protected void onAfter() {
                super.onAfter();
            }

            @Override
            protected void onServerError() {
                super.onServerError();
                datalogUpdataManager.dissmissDialog();
//                CircleDialogUtils.showCommentDialog(ManulInputActivity.this, getString(R.string.android_key2263), getString(R.string.android_key809), getString(R.string.android_key1935), "", Gravity.CENTER, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                    }
//                }, null);

            }
        });
    }


}
