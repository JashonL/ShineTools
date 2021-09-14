
package com.growatt.shinetools.base;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.growatt.shinetools.R;
import com.growatt.shinetools.ShineToosApplication;
import com.growatt.shinetools.constant.GlobalConstant;
import com.growatt.shinetools.module.account.LoginActivity;
import com.growatt.shinetools.module.localbox.max.MaxCheckActivity;
import com.growatt.shinetools.utils.CommenUtils;
import com.growatt.shinetools.utils.MyToastUtils;
import com.growatt.shinetools.utils.Mydialog;
import com.growatt.shinetools.utils.PermissionCodeUtil;
import com.growatt.shinetools.utils.Position;
import com.gyf.immersionbar.ImmersionBar;
import com.mylhyl.circledialog.CircleDialog;



import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


import butterknife.ButterKnife;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * Baseclass of all Activities of the Demo Application.
 *
 * @author Philipp Jahoda
 */
public abstract class DemoBase extends SwipeBackActivity implements EasyPermissions.PermissionCallbacks {

    //    protected String[] mMonths = new String[] {
//            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
//    };

    Toast toast;
    protected Context mContext;
    /**
     * 是否继续询问权限
     */
    protected boolean isContinue = true;

    protected ImmersionBar mImmersionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //侧滑删除界面
        initSwipeBack();
        //沉浸式状态栏处理
        //初始化沉浸式
        initImmersionBar();
        //状态栏字体颜色设置
//        BarTextColorUtils.StatusBarLightMode(this);

        mContext = this;
//		if(VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        }
//        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        if (savedInstanceState != null) {
            savedInstanceState(savedInstanceState);
            return;
        }
//        ShineApplication.getInstance().addActivity(this);
        ShineToosApplication.getContext().addActivity(new WeakReference<>(this));

    }
    /**
     * 隐藏软键盘-点击空白处隐藏键盘
     */
    protected void hideSoftInputClickOut() {
        try {
            //点击空白处隐藏键盘
            ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0).setOnClickListener(view -> {
                if (view.getWindowToken() != null) {
                    InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (manager != null) {
                        manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 通过不同类型activity请求
     */
    public void requestWindowTitleByActivity() {
        if (this instanceof AppCompatActivity) {
            ((AppCompatActivity) this).supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        } else {
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
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



    public void savedInstanceState(Bundle b) {
        Intent intent = new Intent(ShineToosApplication.getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ShineToosApplication.getContext().startActivity(intent);
    }



    public void initToobar(Toolbar toolbar){
        if (toolbar!=null){
            toolbar.setNavigationIcon(R.drawable.icon_return);
            toolbar.setNavigationOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }
    /**
     * 初始化侧滑返回
     */
    private SwipeBackLayout mSwipeBackLayout;

    protected void initSwipeBack() {
        //是否允许滑动
        if (this instanceof MaxCheckActivity
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
    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        //Log.d("save", "from onsaveinstancestate");
        outState.putInt("num", GlobalConstant.num);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
    }

    @Override
    protected void onDestroy() {
        Mydialog.Dismiss();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * ��ȡ��ǰ���
     *
     * @return
     */
    public static int getCurrentYear() {

        return Calendar.getInstance().get(Calendar.YEAR);
    }

    /**
     * ��ȡϵͳ����
     *
     * @return
     */
    public int getLanguage() {
        return CommenUtils.getLanguageNew1();
    }

    /**
     * @return
     */
    public int smarthomeGetLanguage() {
        int lan;
        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.toLowerCase().contains("zh")) {
            lan = 0;
        } else if (language.toLowerCase().contains("en")) {
            lan = 1;
        } else {
            lan = 2;
        }
        return lan;
    }



    public String getLanguageStr() {
        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.toLowerCase().contains("zh")) {
            language = "zh_cn";
        } else if (language.toLowerCase().contains("en")) {
            language = "en";
        } else if (language.toLowerCase().contains("fr")) {
            language = "fr";
        } else if (language.toLowerCase().contains("ja")) {
            language = "ja";
        } else if (language.toLowerCase().contains("it")) {
            language = "it";
        } else if (language.toLowerCase().contains("ho")) {
            language = "ho";
        } else if (language.toLowerCase().contains("tk")) {
            language = "tk";
        } else if (language.toLowerCase().contains("pl")) {
            language = "pl";
        } else if (language.toLowerCase().contains("gk")) {
            language = "gk";
        } else if (language.toLowerCase().contains("gm")) {
            language = "gm";
        } else if (language.toLowerCase().contains("hu")) {
            language = "hu";
        } else if (language.toLowerCase().contains("hk")) {
            language = "hk";
        } else {
            language = "en";
        }
        return language;
    }

    public TextView setHeaderTitle(View headerView, String title, Position position) {
        TextView tv = (TextView) headerView.findViewById(R.id.tvTitle);

        if (title == null) {
            tv.setText("TITLE");
        } else {
            tv.setText(title);
        }

        switch (position) {
            case LEFT:
                tv.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                break;

            default:
                tv.setGravity(Gravity.CENTER);
                break;
        }
        return tv;

    }

    public TextView setHeaderTvTitle(View headerView, String title, OnClickListener listener) {
        TextView tv = (TextView) headerView.findViewById(R.id.tvRight);

        if (title == null) {
            tv.setText("");
        } else {
            tv.setText(title);
        }
        if (listener != null) {
            tv.setOnClickListener(listener);
        }
        return tv;
    }



    public TextView setHeaderTvRight(View headerView, String title, View.OnClickListener listener, int textColor) {
        TextView tv = (TextView) headerView.findViewById(R.id.tvRight);
        tv.setTextColor(textColor);
        if (title == null) {
            tv.setText("");
        } else {
            tv.setText(title);
        }
        if (listener != null) {
            tv.setOnClickListener(listener);
        }
        return tv;
    }
    public TextView setHeaderTvRight(View headerView, String title, View.OnClickListener listener) {
        TextView tv = (TextView) headerView.findViewById(R.id.tvRight);
        if (title == null) {
            tv.setText("");
        } else {
            tv.setText(title);
        }
        if (listener != null) {
            tv.setOnClickListener(listener);
        }
        return tv;
    }

    public void setHeaderTitle(View headerView, String title) {
        setHeaderTitle(headerView, title, Position.CENTER);
    }

    /**
     * @param headerView
     * @param resId
     * @param listener
     */
    public ImageView setHeaderImage(View headerView, int resId, Position position, OnClickListener listener) {
        ImageView iv = null;
        switch (position) {
            case LEFT:
                iv = (ImageView) headerView.findViewById(R.id.ivLeft);
                break;

            default:
                iv = (ImageView) headerView.findViewById(R.id.ivRight);
                break;
        }
        if ((resId != -1) && (resId != R.drawable.icon_return)) {
            iv.setImageResource(resId);
        }
//		iv.setColorFilter(Color.WHITE,Mode.SRC_ATOP);
        if (listener != null) {
            iv.setOnClickListener(listener);
        }
        return iv;
    }

    public void setHeaderImage(View headerView, int resId, Position position) {
        setHeaderImage(headerView, resId, position, null);
    }

    public void setHeaderImage(View headerView, int resId) {
        setHeaderImage(headerView, resId, Position.LEFT);
    }


    public void toast(String text) {
        toast(text, Toast.LENGTH_LONG);
    }

    public void toast(String text, int len) {
        MyToastUtils.toast(text);
//        ToastUtils.show(text);
//        if (TextUtils.isEmpty(text)) {
//            return;
//        }
//        if (MyControl.isNotificationEnabled(this)) {
//            Toast.makeText(this, text, len).show();
//        } else {
//            EToast.makeText(this, text, len).show();
//        }
    }

    public void toast(int resId) {
        toast(resId, Toast.LENGTH_LONG);
    }

    public void toast(int resId, int len) {
        MyToastUtils.toast(resId);
//        ToastUtils.show(resId);
//        String text = getString(resId);
//        if (TextUtils.isEmpty(text)) {
//            return;
//        }
//        if (MyControl.isNotificationEnabled(this)) {
//            Toast.makeText(this, text, len).show();
//        } else {
//            EToast.makeText(this, text, len).show();
//        }
    }

    public void log(String log) {
        Log.d("TAG", this.getClass().getSimpleName() + ": " + log);
    }

    public void toastAndLog(String text, String log) {
        toast(text);
        log(log);
    }

    public void jumpTo(Class<?> clazz, boolean isFinish) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
        if (isFinish) {
            finish();
        }
    }

    public void jumpTo(Intent intent, boolean isFinish) {
        startActivity(intent);
        if (isFinish) {
            finish();
        }
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
     * ������λС��
     *
     * @param str ԭʼ����
     * @param num ����С��λ��
     * @return
     */
    public String getNumberFormat(String str, int num) {
        BigDecimal bd = new BigDecimal(str);
        return bd.setScale(num, BigDecimal.ROUND_HALF_UP) + "";
    }

    //获取屏幕密度
    public float getDensity() {
        return getResources().getDisplayMetrics().density;
    }

    public void onStart() {
        super.onStart();
    }

    protected void onResume() {
        super.onResume();
        //点击外部隐藏软键盘
//        hideSoftInput();
    }

    protected void onPause() {
        super.onPause();
    }

    public void onStop() {
        super.onStop();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    public void onPermissionsDenied(int requestCode, List<String> perms, String permission) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            if (requestCode == PermissionCodeUtil.PERMISSION_EXTERNAL_STORAGE_INSTALL_CODE){
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    new CircleDialog.Builder()
                            .setTitle(getString(R.string.m权限请求))
                            .setGravity(Gravity.CENTER)
                            .setWidth(0.8f)
                            .setText(String.format("%s:%s", permission,getString(R.string.m权限请求步骤)))
                            .setNegative(getString(R.string.all_no), null)
                            .setPositive(getString(R.string.all_ok), view -> {
                                startInstallPermissionSettingActivity();
                            })
                            .show(getSupportFragmentManager());
                }
            }else {
                if (getLanguage() == 0) {
                    new AppSettingsDialog
                            .Builder(this)
                            .setTitle(R.string.m权限请求)
                            .setRationale(String.format("%s:%s", permission,getString(R.string.m权限请求步骤)))
                            .setPositiveButton(R.string.all_ok)
                            .setRequestCode(requestCode)
                            .setNegativeButton(R.string.all_no)
                            .build()
                            .show();
                } else {
                    new AppSettingsDialog.Builder(this).setRequestCode(requestCode).build().show();
                }
            }
        }
    }

    public void onPermissionsDenied(int requestCode, List<String> perms, int permissionResId) {
        onPermissionsDenied(requestCode, perms, getString(permissionResId));
    }
    /**
     * 跳转到设置-允许安装未知来源-页面
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startInstallPermissionSettingActivity() {
        //后面跟上包名，可以直接跳转到对应APP的未知来源权限设置界面。使用startActivityForResult 是为了在关闭设置界面之后，获取用户的操作结果，然后根据结果做其他处理
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, PermissionCodeUtil.PERMISSION_EXTERNAL_STORAGE_INSTALL_CODE);
    }
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        /**
         * 全局判断权限
         */
        switch (requestCode) {
            case PermissionCodeUtil.PERMISSION_EXTERNAL_STORAGE_CODE:
                if (EasyPermissions.hasPermissions(this, PermissionCodeUtil.PERMISSION_EXTERNAL_STORAGE)) {
                    //更新apk

                }
                break;
            case PermissionCodeUtil.PERMISSION_EXTERNAL_STORAGE_INSTALL_CODE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (getPackageManager().canRequestPackageInstalls()) {
                        //更新apk

                    }
                }
                break;
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        switch (requestCode) {
            case PermissionCodeUtil.PERMISSION_EXTERNAL_STORAGE_CODE:
                onPermissionsDenied(requestCode, perms, getString(R.string.m存储));
                break;
            case PermissionCodeUtil.PERMISSION_CAMERA_CODE:
                onPermissionsDenied(requestCode, perms, getString(R.string.m相机));
                break;
            case PermissionCodeUtil.PERMISSION_CAMERA_ONE_CODE:
                onPermissionsDenied(requestCode, perms, getString(R.string.m相机单));
                break;
            case PermissionCodeUtil.PERMISSION_LOCATION_CODE:
                onPermissionsDenied(requestCode, perms, getString(R.string.m位置权限));
                break;
            case PermissionCodeUtil.PERMISSION_EXTERNAL_STORAGE_INSTALL_CODE:
                onPermissionsDenied(requestCode, perms, getString(R.string.about_update));
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode >= PermissionCodeUtil.PERMISSION_CAMERA_CODE) {
            this.onPermissionsGranted(requestCode, null);
        }
    }
}
