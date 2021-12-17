package com.growatt.shinetools.module.inverterUpdata;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.growatt.shinetools.R;
import com.growatt.shinetools.adapter.ManualAdater;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.bean.UpdataBean;
import com.growatt.shinetools.widget.GridDivider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class DeviceManualUpdataActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener, Toolbar.OnMenuItemClickListener {
    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.header_title)
    LinearLayout headerTitle;
    @BindView(R.id.rv_device)
    RecyclerView rvDevice;


    private ManualAdater manualAdater;

    private MenuItem item;
    private String currentVersion;
    private String path;
    private String localVersion;


    @Override
    protected int getContentView() {
        return R.layout.activity_device_manual_updata;
    }

    @Override
    protected void initViews() {


        initToobar(toolbar);
        tvTitle.setText(R.string.android_key252);

        toolbar.inflateMenu(R.menu.comment_right_menu);
        item = toolbar.getMenu().findItem(R.id.right_action);
        item.setTitle(R.string.android_key816);
        toolbar.setOnMenuItemClickListener(this);


        rvDevice.setLayoutManager(new LinearLayoutManager(this));
        manualAdater = new ManualAdater(R.layout.item_updata_device, new ArrayList<>());
        int div = (int) getResources().getDimension(R.dimen.dp_1);
        GridDivider gridDivider = new GridDivider(ContextCompat.getColor(this, R.color.white), div, div);
        rvDevice.addItemDecoration(gridDivider);
        rvDevice.setAdapter(manualAdater);
        manualAdater.setOnItemClickListener(this);
    }

    @Override
    protected void initData() {

        path = getIntent().getStringExtra("path");
        //本地保存的最新版本
        localVersion="";
        File versionFile = new File(path);
        if (versionFile.exists()) {
            File[] files = versionFile.listFiles();
            if (files == null) return;
            for (File f : files) {
                String name = f.getName();
                if (name.endsWith(".zip")) {
                    localVersion = name.substring(0, name.lastIndexOf("."));
                    break;
                }
            }
        }


        String[] items = new String[]{getString(R.string.inverter_upgrade)};
        String[] values = new String[]{getString(R.string.android_key1990) + ":"};

        List<UpdataBean> updataItems = new ArrayList<>();
        for (int i = 0; i < items.length; i++) {
            UpdataBean bean = new UpdataBean();
            bean.setDeviceName(items[i]);
            bean.setCurrentVersion(values[i]);
            updataItems.add(bean);
        }
        manualAdater.replaceData(updataItems);
        getVersion(localVersion);
    }

    private void getVersion(String localVersion) {
        InverterUpdataManager.getInstance().checkUpdata(this,localVersion, new InverterCheckUpdataCallback() {
            @Override
            protected void hasNewVersion(String oldVersion, String newVersion) {
                super.hasNewVersion(oldVersion, newVersion);
                currentVersion = oldVersion;
                manualAdater.getData().get(0).setCurrentVersion(oldVersion);
            }

            @Override
            protected void noNewVirsion(String error) {
                super.noNewVirsion(error);
            }
        });
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = new Intent(this, ManualChioseUpdataActivity.class);
        intent.putExtra("version", currentVersion);
        intent.putExtra("path", path);
        startActivity(intent);
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.right_action) {
            getVersion(localVersion);
        }
        return true;
    }
}
