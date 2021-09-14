package com.growatt.shinetools.okhttp.builder;


import com.growatt.shinetools.okhttp.OkHttpUtils;
import com.growatt.shinetools.okhttp.request.OtherRequest;
import com.growatt.shinetools.okhttp.request.RequestCall;

/**
 * Created by zhy on 16/3/2.
 */
public class HeadBuilder extends GetBuilder
{
    @Override
    public RequestCall build()
    {
        return new OtherRequest(null, null, OkHttpUtils.METHOD.HEAD, url, tag, params, headers,id).build();
    }
}
