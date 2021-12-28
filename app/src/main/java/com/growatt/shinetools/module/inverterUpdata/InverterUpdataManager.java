package com.growatt.shinetools.module.inverterUpdata;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.growatt.shinetools.R;
import com.growatt.shinetools.utils.CircleDialogUtils;
import com.growatt.shinetools.utils.FileUtils;
import com.growatt.shinetools.utils.LogUtil;
import com.mylhyl.circledialog.BaseCircleDialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class InverterUpdataManager {


    private FileUpdataSend fileUpdataSend;

    private static InverterUpdataManager mInstance = null;

    private InverterUpdataManager() {
    }

    public static InverterUpdataManager getInstance() {
        if (mInstance == null) {
            synchronized (InverterUpdataManager.class) {
                if (mInstance == null) {
                    mInstance = new InverterUpdataManager();
                }
            }
        }
        return mInstance;
    }


    //检测升级

    public void checkUpdata(FragmentActivity activity, String filePath) {
        File versionFile = new File(filePath);
        String zipPath = "";
        String zipTargetPath = "";
        if (versionFile.exists()) {
            File[] files = versionFile.listFiles();
            if (files == null) return;
            String version = "";
            for (File f : files) {
                String name = f.getName();
                if (name.endsWith(".zip")) {
                    version = name.substring(0, name.lastIndexOf("."));
                    zipPath = f.getAbsolutePath();
                    String dir = name.substring(0, name.lastIndexOf("."));
                    zipTargetPath = versionFile.getAbsolutePath() + File.separator + dir + File.separator;
                    break;
                }
            }

            //解压缩
            if (!TextUtils.isEmpty(version)) {
                String finalZipPath = zipPath;
                String finalZipTargetPath = zipTargetPath;
                checkUpdata(activity, version, new InverterCheckUpdataCallback() {
                    @Override
                    protected void hasNewVersion(String oldVersion, String newVersion) {
                        super.hasNewVersion(oldVersion, newVersion);
                        String title = activity.getString(R.string.reminder);
                        String subtitle = activity.getString(R.string.version_low);
                        CircleDialogUtils.showUpdataDialog(activity, title, subtitle, oldVersion,
                                newVersion, new CircleDialogUtils.OndialogClickListeners() {
                                    @Override
                                    public void buttonOk() {

                                        List<File> unzip = new ArrayList<>();
                                        //1.解压文件
                                        File zipParent = new File(finalZipTargetPath);
                                        if (!zipParent.exists()) {
                                            zipParent.mkdirs();
                                            try {
                                                unzip = FileUtils.unzip(finalZipPath, finalZipTargetPath);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            //先删除再解压
                                            FileUtils.removeDir(zipParent);
                                            try {
                                                unzip = FileUtils.unzip(finalZipPath, finalZipTargetPath);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            if (unzip == null) return;
                                        }


                                        //2.读取txt文件 获取升级顺序
                                        try {
                                            List<String> upFileName = new ArrayList<>();
                                            for (File f : unzip) {
                                                String name = f.getName();
                                                if (name.endsWith(".txt")) {
                                                    FileInputStream fileInputStream = new FileInputStream(f);
                                                    InputStreamReader isr = new InputStreamReader(fileInputStream);
                                                    BufferedReader br = new BufferedReader(isr);
                                                    String line;
                                                    while ((line = br.readLine()) != null) {
                                                        upFileName.add(line.trim());
                                                    }
                                                    break;
                                                }
                                            }
                                            //3.获取要下发的文件
                                            List<File> files = new ArrayList<>();
                                            for (String s : upFileName) {
                                                for (File f : unzip) {
                                                    String name = f.getName();
                                                    if (s.contains(name)) {
                                                        LogUtil.i("添加的文件名称：" + name);
                                                        files.add(f);
                                                    }
                                                }
                                            }
                                            //4.去升级
                                            if (files.size() > 0) {
                                                updata(activity, files);
                                            }

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }


                                    }

                                    @Override
                                    public void buttonCancel() {

                                    }
                                });

                    }
                });
            }

        }
    }


    public void checkUpdataByLocal(Context context, String currenVersion, String filePath, InverterCheckUpdataCallback callback) {
        File versionFile = new File(filePath);
        String fileName = "";
        if (TextUtils.isEmpty(currenVersion)) {
            callback.noNewVirsion(context.getString(R.string.soft_update_no));
            return;
        }
        if (versionFile.exists()) {
            File[] files = versionFile.listFiles();
            if (files == null) {
                callback.noNewVirsion(context.getString(R.string.soft_update_no));
                return;
            }
            String version = "";
            for (File f : files) {
                String name = f.getName();
                if (name.endsWith(".zip")) {
                    version = name.substring(0, name.lastIndexOf("."));
                    fileName = name;
                    break;
                }
            }

            if (TextUtils.isEmpty(version)) {
                callback.noNewVirsion(context.getString(R.string.no_newversion));
                return;
            }

            if (!currenVersion.equals(version)) {
                callback.hasNewVersion(currenVersion, fileName);
            } else {
                callback.noNewVirsion(context.getString(R.string.soft_update_no));
            }

        } else {
            callback.noNewVirsion(context.getString(R.string.soft_update_no));
        }
    }


    public void checkUpdata(Context context, String nowVersion, InverterCheckUpdataCallback callback) {
        CheckInvertUpdata checkInvertUpdata = new CheckInvertUpdata(context, nowVersion, callback);
        checkInvertUpdata.checkNewVersion();
    }


    //去升级,下发升级文件

    //检测升级
    public void updata(Context context, List<File> updataFile) {
        try {
            //------------------本地测试用--------------------
/*//                            List<ByteBuffer> fileByte1 = UpdateDatalogUtils.getFileByte2(this, "IFAB01_20200728.hex");
                            List<ByteBuffer> fileByte2 = UpdateDatalogUtils.getFileByte2((Activity) context, "UEAA-03.hex");
//                            List<ByteBuffer> fileByte3 = UpdateDatalogUtils.getFileByte2(this, "ZACA-03.bin");
//                            List<ByteBuffer> fileByte4 = UpdateDatalogUtils.getFileByte2(this, "ZACA02testCRC.bin");
//            List<ByteBuffer> fileByte5 = UpdateDatalogUtils.getFileByte2((Activity) context, "ZACA-03.bin");
//                            List<ByteBuffer> fileByte6 = UpdateDatalogUtils.getFileByte2(this, "ZACA03testCRC.bin");

            List<List<ByteBuffer>> list = new ArrayList<>();
//                            list.add(fileByte1);
//                            list.add(fileByte2);
//                            list.add(fileByte3);
//                            list.add(fileByte4);
            list.add(fileByte2);
//                            list.add(fileByte6);
             fileUpdataSend = new FileUpdataSend(context, list, new IUpdataListeners() {
                @Override
                public void preparing() {
                    showDialogFragment();
                }

                @Override
                public void sendFileProgress(int total, int current, int progress) {
                    String uptating = context.getString(R.string.android_key1148) + "(" + (current+1) + "/" + total + ")";
                    tvSubtext.setText(uptating);
                    tvProgress.setText(progress + "%");
                    pbar.setProgress(progress);
                }

                @Override
                public void updataUpdataProgress(int total, int current, int progress) {
                    String uptating = context.getString(R.string.installing) + "(" + current + "/" + total + ")";
                    tvSubtext.setText(uptating);
                    pbar.setProgress(progress);
                }

                @Override
                public void updataFail(String msg) {
                    if (dialogFragment != null) {
                        dialogFragment.dialogDismiss();
                    }
                    showUpdataError(msg);
                }

                @Override
                public void updataSuccess() {
                    if (dialogFragment != null) {
                        dialogFragment.dialogDismiss();
                    }
                    showUpdataSuccess();
                }
            });*/

            //------------------正式使用--------------------
            fileUpdataSend = new FileUpdataSend(context, updataFile, new IUpdataListeners() {
                @Override
                public void preparing(int total, int current) {
                    showDialogFragment(context, total, current);

                }

                @Override
                public void sendFileProgress(int total, int current, int progress) {
                    String uptating = context.getString(R.string.android_key1148) + "(" + (current + 1) + "/" + total + ")";
                    tvSubtext.setText(uptating);
                    tvProgress.setText(progress + "%");
                    pbar.setProgress(progress);
                }

                @Override
                public void updataUpdataProgress(int total, int current, int progress) {
                    String uptating = context.getString(R.string.installing) + "(" + current + "/" + total + ")";
                    tvSubtext.setText(uptating);
                    pbar.setProgress(progress);
                    tvProgress.setText(progress + "%");
                }

                @Override
                public void updataFail(String msg) {
                    if (dialogFragment != null) {
                        dialogFragment.dialogDismiss();
                        dialogFragment = null;
                    }
                    showUpdataError(context, msg);
                }

                @Override
                public void updataSuccess() {
                    if (dialogFragment != null) {
                        dialogFragment.dialogDismiss();
                        dialogFragment = null;
                    }
                    showUpdataSuccess(context);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private TextView tvSubtext;
    private ProgressBar pbar;
    private TextView tvProgress;
    private TextView tvUpdating;
    private BaseCircleDialog dialogFragment;

    /**
     * 显示更新提示
     */
    private void showDialogFragment(Context context, int total, int current) {
        if (dialogFragment == null) {
            View view = LayoutInflater.from(context).inflate(R.layout.layout_updata_dialog, null);
            tvSubtext = view.findViewById(R.id.tv_subtext);
            pbar = view.findViewById(R.id.loading_img);
            tvProgress = view.findViewById(R.id.tv_progress);
            tvUpdating = view.findViewById(R.id.uptating_tips);
            TextView tvCancel = view.findViewById(R.id.tv_cancel);
            String uptating = context.getString(R.string.upgrading_in_preparation) + "(" + (current + 1) + "/" + total + ")";
            tvSubtext.setText(uptating);
            tvCancel.setOnClickListener(view12 -> {
                fileUpdataSend.close();
                dialogFragment.dialogDismiss();
            });
            dialogFragment = CircleDialogUtils.showCommentBodyView(context, view, "", ((FragmentActivity) context).getSupportFragmentManager(), view1 -> {
            }, Gravity.CENTER, 0.8f, 0.5f, false);
        } else {
            String uptating = context.getString(R.string.upgrading_in_preparation) + "(" + (current + 1) + "/" + total + ")";
            tvSubtext.setText(uptating);
        }

    }


    private BaseCircleDialog dialog_success;

    /**
     * 升级成功
     */
    private void showUpdataSuccess(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_config_success, null);
        dialog_success = CircleDialogUtils.showCommentBodyView(context, view, "", ((FragmentActivity) context).getSupportFragmentManager(), view1 -> {
            TextView tvTitle = view1.findViewById(R.id.tv_title);
            TextView loadingTips = view1.findViewById(R.id.loading_tips);
            TextView tvCancel = view1.findViewById(R.id.tv_cancel);
            tvCancel.setOnClickListener(view2 -> {
                fileUpdataSend.close();
                dialog_success.dialogDismiss();
            });
            tvTitle.setText(R.string.android_key2969);
            loadingTips.setText(R.string.device_restart);

        }, Gravity.CENTER, 0.8f, 0.5f, true);
    }


    /**
     * 升级失败
     */
    private BaseCircleDialog dialog_error;

    private void showUpdataError(Context context, String error) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_config_error, null);
        dialog_error = CircleDialogUtils.showCommentBodyView(context, view, "", ((FragmentActivity) context).getSupportFragmentManager(), view1 -> {
            TextView tvTitle = view1.findViewById(R.id.tv_title);
            TextView loadingTips = view1.findViewById(R.id.loading_tips);
            TextView tvCancel = view1.findViewById(R.id.tv_cancel);
            tvCancel.setOnClickListener(view2 -> {
                fileUpdataSend.close();
                dialog_error.dialogDismiss();
            });

            tvTitle.setText(R.string.android_key2964);
            loadingTips.setText(error);

        }, Gravity.CENTER, 0.8f, 0.5f, true);
    }


}
