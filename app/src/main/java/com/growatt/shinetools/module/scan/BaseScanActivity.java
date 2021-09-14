package com.growatt.shinetools.module.scan;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.growatt.shinetools.R;
import com.gyf.immersionbar.ImmersionBar;
import com.king.zxing.CaptureActivity;

import butterknife.ButterKnife;

public abstract class BaseScanActivity extends AppCompatActivity {

    protected abstract int getContentView();

    protected ImmersionBar mImmersionBar;

    protected abstract void initViews();

    protected abstract void initData();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        ButterKnife.bind(this);
        //初始化沉浸式
        initImmersionBar();
        initViews();
        initData();
    }


    /**
     * 初始化沉浸式
     * Init immersion bar.
     */
    protected void initImmersionBar() {
        //设置共同沉浸式样式
        mImmersionBar=  ImmersionBar.with(this);
        mImmersionBar.statusBarDarkFont(true, 0.2f)//设置状态栏图片为深色，(如果android 6.0以下就是半透明)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.white)//这里的颜色，你可以自定义。
                .init();
    }


    public void initToobar(Toolbar toolbar){
        if (toolbar!=null){
            toolbar.setNavigationIcon(R.drawable.icon_return);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }


}
