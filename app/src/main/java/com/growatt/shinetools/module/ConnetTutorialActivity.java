package com.growatt.shinetools.module;

import android.Manifest;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.growatt.shinetools.R;
import com.growatt.shinetools.adapter.TutorialAdapter;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.bean.TutorialBean;
import com.growatt.shinetools.constant.DebugConstant;
import com.growatt.shinetools.utils.ActivityUtils;
import com.growatt.shinetools.widget.LinearDivider;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.growatt.shinetools.constant.PermissionConstant.RC_CAMERA;

public class ConnetTutorialActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks{
    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.header_title)
    LinearLayout headerTitle;
    @BindView(R.id.rlv_tutorial)
    RecyclerView rlvTutorial;

    private TutorialAdapter adapter;


    private int type;

    @Override
    protected int getContentView() {
        return R.layout.activity_connet_tutorial;
    }

    @Override
    protected void initViews() {
        initToobar(toolbar);
        tvTitle.setText(R.string.android_key3066);
        //列表
        rlvTutorial.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TutorialAdapter(R.layout.item_tutorial_connet, new ArrayList<>());
        rlvTutorial.setAdapter(adapter);

        View footView = LayoutInflater.from(this).inflate(R.layout.foot_recycleview_button, rlvTutorial,false);
        Button btnNext = footView.findViewById(R.id.btn_next);
        btnNext.setOnClickListener(view -> {
            checkCameraPermissions();
        });
        adapter.addFooterView(footView);
        LinearDivider linearDivider = new LinearDivider(this, LinearLayoutManager.VERTICAL, 50, ContextCompat.getColor(this, R.color.white));
        rlvTutorial.addItemDecoration(linearDivider);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        type = intent.getIntExtra(DebugConstant.KEY_WIFI_TYPE, 0);


        String[] titles;
        int[] pics;
        String[] contents;

        if (type == DebugConstant.TYPE_USB_WIFI) {
            titles = new String[]{getString(R.string.android_key267) + " 1/2", getString(R.string.android_key267) + " 2/2"};
            contents = new String[]{getString(R.string.android_key3069), getString(R.string.android_key3070)};
            if (getLanguage()==0){
                pics = new int[]{R.drawable.image01, R.drawable.image02};
            }else {
                pics = new int[]{R.drawable.image01, R.drawable.image02_en};
            }
        } else {
            titles = new String[]{getString(R.string.android_key267) + " 1/4", getString(R.string.android_key267) + " 2/4",
                    getString(R.string.android_key267) + " 3/4", getString(R.string.android_key267) + " 4/4"};
            contents = new String[]{getString(R.string.android_key3067), getString(R.string.android_key3068), getString(R.string.android_key3069), getString(R.string.android_key3070)};
            if (getLanguage()==0){
                pics = new int[]{R.drawable.image_shinex01, R.drawable.image_shinex02, R.drawable.image_shinex03, R.drawable.image_shinex04};
            }else {
                pics = new int[]{R.drawable.image_shinex01, R.drawable.image_shinex02, R.drawable.image_shinex03, R.drawable.image02_en};
            }
        }

        List<TutorialBean> list = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            TutorialBean bean = new TutorialBean();
            bean.setContent(contents[i]);
            bean.setPic(pics[i]);
            bean.setTitle(titles[i]);
            list.add(bean);
        }

        adapter.replaceData(list);

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
    @AfterPermissionGranted(RC_CAMERA)
    private void checkCameraPermissions(){
        String[] perms = {Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {//有权限
            toScanSerial();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.android_key2385),
                    RC_CAMERA, perms);
        }
    }


    private void toScanSerial(){
        Intent intent =new Intent(this,CustomScanActivity.class);
        intent.putExtra(DebugConstant.KEY_WIFI_TYPE,type);
        ActivityUtils.startActivity(this,intent,true);
    }

}
