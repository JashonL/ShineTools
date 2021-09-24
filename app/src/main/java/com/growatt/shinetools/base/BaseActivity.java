package com.growatt.shinetools.base;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.growatt.shinetools.MainActivity;
import com.growatt.shinetools.R;
import com.growatt.shinetools.ShineToosApplication;
import com.growatt.shinetools.WebViewActivity;
import com.growatt.shinetools.module.account.LoginActivity;
import com.growatt.shinetools.utils.CommenUtils;
import com.growatt.shinetools.utils.MyToastUtils;
import com.gyf.immersionbar.ImmersionBar;

import java.lang.ref.WeakReference;

import butterknife.ButterKnife;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public abstract class BaseActivity extends SwipeBackActivity {


    public Context   mContext;


    protected abstract int getContentView();

    protected ImmersionBar mImmersionBar;

    protected abstract void initViews();

    protected abstract void initData();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        ButterKnife.bind(this);
        mContext=this;
        ShineToosApplication.getContext().addActivity(new WeakReference<>(this));
        //初始化沉浸式
        initImmersionBar();
        initViews();
        initData();
        //侧滑删除界面
        initSwipeBack();
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


    public void toast(String text) {
        MyToastUtils.toast(text);
    }

    public void toast(int text) {
        MyToastUtils.toast(text);
    }


    public int getLanguage() {
        return CommenUtils.getLanguageNew1();
    }



    /**
     * @param editTexts
     */

    public boolean isEmpty(TextView... editTexts) {

        for (TextView et : editTexts) {
            String content = et.getText().toString();
            if (TextUtils.isEmpty(content) || TextUtils.isEmpty(content.trim())) {
                toast(getString(R.string.all_blank));
                return true;
            }
        }

        return false;
    }


    /**
     * 初始化侧滑返回
     */
    private SwipeBackLayout mSwipeBackLayout;

    protected void initSwipeBack() {
        //是否允许滑动
        if (this instanceof MainActivity
                || this instanceof LoginActivity
                ||this instanceof WebViewActivity
//                || this instanceof OssJKActivity
//                || this instanceof MaxMain2Activity
        ) {
            setSwipeBackEnable(false);
        } else {
            setSwipeBackEnable(true);
        }
        mSwipeBackLayout = getSwipeBackLayout();
        //设置滑动方向
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        //设置滑动范围
//		mSwipeBackLayout.setEdgeSize(200);
        //设置窗体透明度
//		<item name="android:windowIsTranslucent">true</item>
    }

}
