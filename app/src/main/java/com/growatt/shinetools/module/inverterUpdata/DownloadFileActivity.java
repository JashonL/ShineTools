package com.growatt.shinetools.module.inverterUpdata;

import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.growatt.shinetools.MainActivity;
import com.growatt.shinetools.R;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.module.WelcomeActivity;
import com.growatt.shinetools.utils.ActivityUtils;
import com.growatt.shinetools.utils.AppSystemUtils;
import com.growatt.shinetools.utils.CircleDialogUtils;
import com.growatt.shinetools.utils.DialogUtils;

import butterknife.BindView;

public class DownloadFileActivity extends BaseActivity {


    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.header_title)
    LinearLayout headerTitle;
    @BindView(R.id.iv_updata)
    ImageView ivUpdata;
    @BindView(R.id.tv_loading)
    TextView tvLoading;
    @BindView(R.id.bp_progress)
    ProgressBar bpProgress;
    @BindView(R.id.tv_progress)
    TextView tvProgress;
    @BindView(R.id.tv_tips)
    TextView tvTips;

    @Override
    protected int getContentView() {
        return R.layout.activity_download_file;
    }

    @Override
    protected void initViews() {
        tvTitle.setText("");
    }

    @Override
    protected void initData() {
        FileUpdataManager fileUpdataManager = CheckDownloadUtils.checkUpdata(this);
        fileUpdataManager.checkNewVersion(new FileCheckUpdataCallback() {
            @Override
            protected void hasNewVersion(FileDownBean updateApp, FileUpdataManager updateAppManager) {
                super.hasNewVersion(updateApp, updateAppManager);
                fileUpdataManager.startDownLoad(new FileDownLoadManager.DownloadCallback() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onProgress(float progress, int total, int current) {
                        current++;
                        int current_progress = Math.round(progress * 100);
                        String title = getString(R.string.android_key3130) + "(" + current + "/" + total + ")";
                        tvLoading.setText(title);

                        bpProgress.setProgress(current_progress);

                        if (tvProgress != null) {
                            tvProgress.setText(current_progress + "%");
                        }
                    }

                    @Override
                    public void setMax(long totalSize) {

                    }

                    @Override
                    public void onFinish() {
                        //如果是
                        boolean firstWelcome = AppSystemUtils.isFirstWelcome();
                        if (!firstWelcome) {
                            ActivityUtils.gotoActivity(DownloadFileActivity.this, WelcomeActivity.class, true);
                        } else {
                            ActivityUtils.gotoActivity(DownloadFileActivity.this, MainActivity.class, true);
                        }
                    }

                    @Override
                    public void onError(String msg) {
                        //弹出失败的框  并返回登录页面
                        CircleDialogUtils.showCommentDialog(DownloadFileActivity.this, getString(R.string.android_key2263),
                                getString(R.string.android_key2212), getString(R.string.android_key1935), getString(R.string.android_key2152), Gravity.CENTER, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        AppSystemUtils.logout(DownloadFileActivity.this);
                                    }
                                }, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                    }
                                });
                    }
                });
            }

            @Override
            protected void noNewVirsion(String error) {
                super.noNewVirsion(error);
                DialogUtils.getInstance().closeLoadingDialog();
            }

            @Override
            protected void onServerError() {
                super.onServerError();
                DialogUtils.getInstance().closeLoadingDialog();
            }
        });

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
