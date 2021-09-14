package com.growatt.shinetools.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.gyf.immersionbar.ImmersionBar;

public abstract class BaseFragment extends Fragment {

    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void initData();

    protected Toolbar mToolBar;

    protected ImmersionBar mImmersionBar;

    protected Context mContext;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        View view = inflater.inflate(getLayoutId(),container,false);
        initView();
        initData();
        return view;
    }
}
