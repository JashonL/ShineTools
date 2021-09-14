package com.growatt.shinetools.module;

import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.growatt.shinetools.R;
import com.growatt.shinetools.constant.DebugConstant;
import com.growatt.shinetools.constant.GlobalConstant;
import com.growatt.shinetools.module.scan.BaseScanActivity;
import com.growatt.shinetools.utils.ActivityUtils;
import com.growatt.shinetools.utils.CircleDialogUtils;
import com.growatt.shinetools.utils.MyToastUtils;
import com.king.zxing.CaptureHelper;
import com.king.zxing.OnCaptureCallback;
import com.king.zxing.ViewfinderView;
import com.mylhyl.circledialog.res.drawable.CircleDrawable;
import com.mylhyl.circledialog.res.values.CircleColor;
import com.mylhyl.circledialog.res.values.CircleDimen;
import com.mylhyl.circledialog.view.listener.OnCreateBodyViewListener;

import butterknife.BindView;
import butterknife.OnClick;

import static com.growatt.shinetools.constant.DebugConstant.TYPE_USB_WIFI;

public class CustomScanActivity extends BaseScanActivity implements OnCaptureCallback, Toolbar.OnMenuItemClickListener {
    @BindView(R.id.surfaceView)
    SurfaceView surfaceView;
    @BindView(R.id.viewfinderView)
    ViewfinderView viewfinderView;
    @BindView(R.id.ivFlash)
    ImageView ivFlash;
    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.header_title)
    LinearLayout headerTitle;
    @BindView(R.id.iv_ewm)
    ImageView ivEwm;
    @BindView(R.id.tv_ewm)
    AppCompatTextView tvEwm;
    @BindView(R.id.ll_ewm)
    LinearLayout llEwm;
    @BindView(R.id.iv_sd)
    ImageView ivSd;
    @BindView(R.id.tv_sdsr)
    AppCompatTextView tvSdsr;
    @BindView(R.id.ll_sd)
    LinearLayout llSd;
    @BindView(R.id.bottomLayout)
    LinearLayoutCompat bottomLayout;
    @BindView(R.id.tv_find_serialnum)
    AppCompatTextView tvFindSerialnum;


    private CaptureHelper mCaptureHelper;
    private int type;

    @Override
    protected int getContentView() {
        return R.layout.activity_custom_scan;
    }

    @Override
    protected void initViews() {
        initToobar(toolbar);
        tvTitle.setText(R.string.android_key1262);

        String hint=getString(R.string.android_key2979)+"/"+getString(R.string.android_key2978);
        tvEwm.setText(hint);

    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        type = intent.getIntExtra(DebugConstant.KEY_WIFI_TYPE, 0);
        initUI();
    }


    private void initUI() {
        if (type == TYPE_USB_WIFI) {
            toolbar.inflateMenu(R.menu.scan_menu);
            toolbar.setOnMenuItemClickListener(this);
        }


        mCaptureHelper = new CaptureHelper(this, surfaceView, viewfinderView, ivFlash);
        mCaptureHelper.setOnCaptureCallback(this);
        mCaptureHelper.onCreate();
        mCaptureHelper.vibrate(true)
                .fullScreenScan(true)//全屏扫码
                .supportVerticalCode(true)//支持扫垂直条码，建议有此需求时才使用。
                .supportLuminanceInvert(true)//是否支持识别反色码（黑白反色的码），增加识别率
                .continuousScan(true);

    }


    @Override
    public boolean onResultCallback(String result) {
//        MyToastUtils.toast(result);

        if (TextUtils.isEmpty(result)) {
            MyToastUtils.toast(R.string.android_key3126);
        } else {
            int len = result.length();
            boolean isDatalog = (len == 10 || len == 16 || len == 30);
            if (!isDatalog) {
                MyToastUtils.toast(R.string.android_key2146);
            } else {
                Intent intent = new Intent(this, ManulInputActivity.class);
                intent.putExtra(DebugConstant.KEY_WIFI_TYPE, type);
                intent.putExtra(GlobalConstant.SCAN_RESULT, result);
                ActivityUtils.startActivity(this, intent, true);
            }
        }

        return false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        mCaptureHelper.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCaptureHelper.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCaptureHelper.onDestroy();
    }


    @OnClick({R.id.ll_sd, R.id.tv_find_serialnum})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_sd:
                Intent intent = new Intent(this, ManulInputActivity.class);
                intent.putExtra(DebugConstant.KEY_WIFI_TYPE, type);
                ActivityUtils.startActivity(this, intent, true);
                break;
            case R.id.tv_find_serialnum:
                View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_scan_guide, null, false);
                DialogFragment dialogFragment = CircleDialogUtils.showCommentBodyDialog(1f, 0.8f, contentView, getSupportFragmentManager(), new OnCreateBodyViewListener() {
                    @Override
                    public void onCreateBodyView(View view) {
                        CircleDrawable bgCircleDrawable = new CircleDrawable(CircleColor.DIALOG_BACKGROUND
                                , CircleDimen.DIALOG_RADIUS, CircleDimen.DIALOG_RADIUS, 0, 0);
                        view.setBackground(bgCircleDrawable);
                        ImageView ivGuide = view.findViewById(R.id.iv_guide1);
                        ImageView ivGuide2 = view.findViewById(R.id.iv_guide2);

                        if (type== TYPE_USB_WIFI){
                            ivGuide.setImageResource(R.drawable.scan_guide_usb);
                            ivGuide2.setVisibility(View.GONE);
                        }else {
                            ivGuide.setImageResource(R.drawable.shinewifi_x);
                            ivGuide2.setImageResource(R.drawable.shinewifi_s);
                        }
                    }
                }, Gravity.BOTTOM,true);
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.right_action:
                ActivityUtils.gotoActivity(this, DeviceTypeActivity.class, true);
                break;
        }
        return true;
    }
}
