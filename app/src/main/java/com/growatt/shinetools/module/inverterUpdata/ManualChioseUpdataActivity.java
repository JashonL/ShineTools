package com.growatt.shinetools.module.inverterUpdata;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.growatt.shinetools.R;
import com.growatt.shinetools.ShineToosApplication;
import com.growatt.shinetools.adapter.InvererManualAdater;
import com.growatt.shinetools.base.BaseActivity;
import com.growatt.shinetools.bean.UpdataBean;
import com.growatt.shinetools.utils.CircleDialogUtils;
import com.growatt.shinetools.utils.FileUtils;
import com.growatt.shinetools.widget.GridDivider;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

public class ManualChioseUpdataActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener,
        Toolbar.OnMenuItemClickListener {
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


    private InvererManualAdater manualAdater;
    private String version;
    private String path;

    private boolean isNewVersion = false;

    @Override
    protected int getContentView() {
        return R.layout.activity_manual_choise_updata;
    }

    @Override
    protected void initViews() {

        initToobar(toolbar);
        tvTitle.setText(R.string.android_key252);


        rvDevice.setLayoutManager(new LinearLayoutManager(this));
        manualAdater = new InvererManualAdater(new ArrayList<>());
        int div = (int) getResources().getDimension(R.dimen.dp_1);
        GridDivider gridDivider = new GridDivider(ContextCompat.getColor(this, R.color.white), div, div);
        rvDevice.addItemDecoration(gridDivider);
        rvDevice.setAdapter(manualAdater);
        manualAdater.setOnItemClickListener(this);
        manualAdater.setOnItemChildClickListener(this);
    }

    @Override
    protected void initData() {
        version = getIntent().getStringExtra("version");
        path = getIntent().getStringExtra("path");

        String[] items = new String[]{getString(R.string.inverter_upgrade), getString(R.string.choise_package)};
        String[] values = new String[]{version, ""};
        List<UpdataBean> updataItems = new ArrayList<>();
        for (int i = 0; i < items.length; i++) {
            UpdataBean bean = new UpdataBean();
            bean.setDeviceName(items[i]);
            bean.setCurrentVersion(values[i]);
            if (i == 0) {
                bean.setType(0);
            } else {
                bean.setType(1);
            }
            bean.setChecked(false);
            updataItems.add(bean);
        }
        manualAdater.replaceData(updataItems);


        InverterUpdataManager.getInstance().checkUpdataByLocal(this, version, path, new InverterCheckUpdataCallback() {
            @Override
            protected void hasNewVersion(String oldVersion, String newVersion) {
                UpdataBean bean = manualAdater.getData().get(1);
                bean.setCurrentVersion(newVersion);
                isNewVersion = true;
                manualAdater.notifyDataSetChanged();
            }

            @Override
            protected void noNewVirsion(String error) {
                UpdataBean bean = manualAdater.getData().get(1);
                bean.setCurrentVersion(error);
                isNewVersion = false;
                manualAdater.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        UpdataBean bean = manualAdater.getData().get(position);
        boolean checked = bean.isChecked();
        boolean b = !checked;
        bean.setChecked(b);
        manualAdater.notifyDataSetChanged();
        if (b) {
            if (isNewVersion) {
                toUpdata();
            } else {
                toast(R.string.soft_update_no);
            }
        }
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return true;
    }


    private void toUpdata() {
        File versionFile = new File(path);
        String zipPath = "";
        String zipTargetPath = "";
        if (versionFile.exists()) {
            File[] files = versionFile.listFiles();
            if (files == null) return;
            for (File f : files) {
                String name = f.getName();


                if (name.endsWith(".zip")) {
                    zipPath = f.getAbsolutePath();
                    String substring = name.substring(name.indexOf("-"));
                    zipTargetPath = versionFile.getAbsolutePath() +"/"+ substring;
                    break;
                }
            }
            if (TextUtils.isEmpty(zipPath)) return;
            List<File> unzip = new ArrayList<>();
            //1.解压文件
            File zipParent = new File(zipTargetPath);
            if (!zipParent.exists()) {
                zipParent.mkdirs();
                try {
                    unzip = FileUtils.unzip(zipPath, zipTargetPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                //先删除再解压
                FileUtils.removeDir(zipParent);
                try {
                    unzip = FileUtils.unzip(zipPath, zipTargetPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

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
                List<File> send_files = new ArrayList<>();
                for (String s : upFileName) {
                    for (File f : unzip) {
                        String name = f.getName();
                        if (s.contains(name)) {
                            send_files.add(f);
                        }
                    }
                }
                //4.去升级
                InverterUpdataManager.getInstance().updata(this, send_files);

            } catch (IOException e) {
                e.printStackTrace();
            }


        }


    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        switch (view.getId()) {
            case R.id.tv_other_package:
                launcher.launch(true);
                break;
        }
    }


    class ResultContract extends ActivityResultContract<Boolean, Intent> {
        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Boolean input) {
//            Intent intent = new Intent();
//            intent.setType("application/zip");
//            intent.setAction(Intent.ACTION_GET_CONTENT);
            //  Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            //文档需要是可以打开的
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            //指定文档的minitype为text类型
            intent.setType("application/zip");
            //是否支持多选，默认不支持
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);

            return intent;
        }

        @Override
        public Intent parseResult(int resultCode, @Nullable Intent intent) {
            return intent;
        }
    }


    ActivityResultLauncher launcher = registerForActivityResult(new ResultContract(), new ActivityResultCallback<Intent>() {
        @Override
        public void onActivityResult(Intent result) {
            if (result == null) {
                return;
            }
            handleOpenDocumentAction(result);
        }
    });


    private void handleOpenDocumentAction(Intent data) {
        if (data == null) {
            return;
        }
        //获取文档指向的uri,注意这里是指单个文件。
        Uri uri = data.getData();
        //根据该Uri可以获取该Document的信息，其数据列的名称和解释可以在DocumentsContact类的内部类Document中找到
        assert uri != null;
        Cursor cursor = getContentResolver().query(uri, null,
                null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            File otherDir = new File(ShineToosApplication.INVERTER_OTHER_FILE_DIR);
            if (!otherDir.exists()) {
                otherDir.mkdirs();
            } else {
                FileUtils.removeDir(otherDir);
            }

            File copyFile = new File(otherDir.getAbsolutePath(), name);

            //以下为直接从该uri中获取InputSteam，并读取出文本的内容的操作 写入指定文件夹中
            try {
                InputStream is = getContentResolver().openInputStream(uri);
                BufferedInputStream bufis = new BufferedInputStream(is);
                byte[] buffer = new byte[1024]; //数组大小可根据文件大小设置
                FileOutputStream fos = new FileOutputStream(copyFile.getAbsolutePath());
                BufferedOutputStream bufos = new BufferedOutputStream(fos);
                int len = 0;
                //写入
                while ((len = bufis.read(buffer)) != -1) {
                    bufos.write(buffer, 0, len);
                    bufos.flush();
                }
                bufos.close();
                bufis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //判断版本是否一样  弹框提示

            String title = getString(R.string.reminder);
            String text = getString(R.string.use_this_version) + ":" + name;
            String ok = getString(R.string.android_key1935);
            String cancel = getString(R.string.android_key1806);
            CircleDialogUtils.showCommentDialog(this, title, text, ok, cancel, Gravity.CENTER, view -> {

                String replace = name.replace(".zip", "");
                String zipTargetPath = copyFile.getParent() + File.separator + replace;


                List<File> unzip = new ArrayList<>();
                //1.解压文件
                File zipParent = new File(zipTargetPath);

                if (!zipParent.exists()) {
                    zipParent.mkdirs();
                    try {
                        unzip = FileUtils.unzip(copyFile.getAbsolutePath(), zipTargetPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    //先删除再解压
                    FileUtils.removeDir(zipParent);
                    try {
                        unzip = FileUtils.unzip(copyFile.getAbsolutePath(), zipTargetPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (unzip == null) return;
                }


                //2.读取txt文件 获取升级顺序
                try {
                    List<String> upFileName = new ArrayList<>();
                    for (File f : unzip) {
                        String name1 = f.getName();
                        if (name1.endsWith(".txt")) {
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
                            String name1 = f.getName();
                            if (s.contains(name1)) {
                                files.add(f);
                            }
                        }
                    }
                    //4.去升级
                    if (files.size() > 0) {
                        InverterUpdataManager.getInstance().updata(this, files);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }, view -> {

            });


        }


    }


}
