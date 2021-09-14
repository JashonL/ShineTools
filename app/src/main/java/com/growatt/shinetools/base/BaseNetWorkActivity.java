package com.growatt.shinetools.base;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.growatt.shinetools.okhttp.OkHttpUtils;

public abstract class BaseNetWorkActivity extends BaseActivity implements BaseView{
    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(this);
    }
}
