package com.growatt.shinetools.utils.datalogupdata;

import androidx.annotation.NonNull;

import com.growatt.shinetools.R;
import com.growatt.shinetools.ShineToosApplication;
import com.growatt.shinetools.okhttp.OkHttpUtils;
import com.growatt.shinetools.okhttp.callback.FileCallBack;
import com.growatt.shinetools.utils.MyToastUtils;

import java.io.File;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;

public class DatalogUpDataHttpUtil implements HttpManager {
    @Override
    public void asyncGet(@NonNull String url, @NonNull Map<String, String> params, @NonNull Callback callBack) {

    }

    @Override
    public void asyncPost(@NonNull String url, @NonNull Map<String, String> params, @NonNull Callback callBack) {

    }

    @Override
    public void download(@NonNull String url, @NonNull String path, @NonNull String fileName, @NonNull FileCallback callback) {
        OkHttpUtils.get()
                .url(url)
                .build()
                .execute(new FileCallBack(path, fileName) {
                             @Override
                             public void onError(Call call, Exception e, int id) {

                                 String errorMsg = ShineToosApplication.getContext().getString(R.string.android_key2030);
                                 if (e != null) {
                                     if (e instanceof SocketTimeoutException) {
                                         errorMsg = ShineToosApplication.getContext().getString(R.string.android_key1853);
                                     }
                                     if (e instanceof ConnectException) {
                                         errorMsg = ShineToosApplication.getContext().getString(R.string.android_key1922);
                                     }
                                 }
                                 MyToastUtils.toast(errorMsg);
                             }

                             @Override
                             public void onResponse(File response, int id) {
                                 callback.onResponse(response);
                             }

                             @Override
                             public void onBefore(Request request, int id) {
                                 super.onBefore(request, id);
                                 callback.onBefore();
                             }

                             @Override
                             public void inProgress(float progress, long total, int id) {
                                 callback.onProgress(progress, total);
                             }


                         }


                );
    }
}
