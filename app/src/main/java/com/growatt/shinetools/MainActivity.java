package com.growatt.shinetools;


import android.content.Intent;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.growatt.shinetools.adapter.DebugTypeAdapter;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.bean.BeanDebug;
import com.growatt.shinetools.constant.DebugConstant;
import com.growatt.shinetools.constant.GlobalConstant;
import com.growatt.shinetools.modbusbox.ModbusUtil;
import com.growatt.shinetools.module.ConnetTutorialActivity;
import com.growatt.shinetools.module.UsTutorialActivity;
import com.growatt.shinetools.utils.MyToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.growatt.shinetools.modbusbox.ModbusUtil.AP_MODE;
import static com.growatt.shinetools.modbusbox.ModbusUtil.USB_WIFI;

public class MainActivity extends BaseActivity implements Toolbar.OnMenuItemClickListener, BaseQuickAdapter.OnItemClickListener {

    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.header_title)
    LinearLayout headerTitle;
    @BindView(R.id.rlv_mode)
    RecyclerView rlvMode;


    private DebugTypeAdapter adapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        tvTitle.setVisibility(View.GONE);
        toolbar.inflateMenu(R.menu.main_menu);
        toolbar.setOnMenuItemClickListener(this);
        //列表
        rlvMode.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DebugTypeAdapter(R.layout.item_debug_type, new ArrayList<>());
        rlvMode.setAdapter(adapter);

        adapter.setOnItemClickListener(this);

    }

    @Override
    protected void initData() {
        String[] title = new String[]{"USB/232-WIFI", "ShineWiFi-S/X", "Direct WiFi"};
        String[] content = new String[]{"", getString(R.string.android_key3077), "(MIN TL-XH-US)"};
        int[] iconres = new int[]{R.drawable.usb, R.drawable.direct, R.drawable.debug_wifi};
        List<BeanDebug> debugs = new ArrayList<>();
        for (int i = 0; i < title.length; i++) {
            BeanDebug beanDebug = new BeanDebug();
            beanDebug.setIcon(iconres[i]);
            beanDebug.setTitle(title[i]);
            beanDebug.setContent(content[i]);
            debugs.add(beanDebug);
        }
        adapter.replaceData(debugs);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_mode_setting:
                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra(WebViewActivity.WEB_URL, GlobalConstant.web_url);
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        switch (position) {
            case 0:
                ModbusUtil.setLocalDebugMode(USB_WIFI);
                Intent intent = new Intent(this, ConnetTutorialActivity.class);
                intent.putExtra(DebugConstant.KEY_WIFI_TYPE, position);
                startActivity(intent);
                break;
            case 1:
                ModbusUtil.setLocalDebugMode(AP_MODE);
                Intent intent1 = new Intent(this, ConnetTutorialActivity.class);
                intent1.putExtra(DebugConstant.KEY_WIFI_TYPE, position);
                startActivity(intent1);
                break;
            case 2:
                ModbusUtil.setLocalDebugMode(USB_WIFI);
                Intent intent2 = new Intent(this, UsTutorialActivity.class);
                intent2.putExtra(DebugConstant.KEY_WIFI_TYPE, position);
                startActivity(intent2);

                break;
        }
    }


    private long mExitTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                MyToastUtils.toast(R.string.android_key3128);
                mExitTime = System.currentTimeMillis();
            } else {
                ShineToosApplication.getContext().exit();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
