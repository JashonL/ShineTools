package com.growatt.shinetools.utils;

import com.growatt.shinetools.R;
import com.growatt.shinetools.ShineToosApplication;
import com.growatt.shinetools.base.BaseView;
import com.growatt.shinetools.okhttp.callback.Callback;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class RequestCallback extends Callback<String> {

    private BaseView baseView;


    public RequestCallback(BaseView baseView) {
        this.baseView = baseView;
    }

    @Override
    public void onBefore(Request request, int id) {
        super.onBefore(request, id);
        baseView.startView();
    }

    @Override
    public String parseNetworkResponse(Response response, int id) throws Exception {
        return response.body().string();
    }

    @Override
    public void onError(Call call, Exception error, int id) {
        Mydialog.Dismiss();
        String errorMsg= ShineToosApplication.getContext().getString(R.string.android_key2030);
        if (error != null) {
            if (error instanceof SocketTimeoutException) {
                 errorMsg= ShineToosApplication.getContext().getString(R.string.android_key1853);
            } if (error instanceof ConnectException) {
                errorMsg= ShineToosApplication.getContext().getString(R.string.android_key1922);
            }
        }
        MyToastUtils.toast(errorMsg);
        baseView.errorView(errorMsg);

    }

    @Override
    public void onResponse(String response, int id) {
        baseView.successView(response);
    }

    @Override
    public void onAfter(int id) {
        super.onAfter(id);
    }
}
