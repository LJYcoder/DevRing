package com.ljy.devring.http.support.interceptor;

import android.content.Context;

import com.ljy.devring.util.NetworkUtil;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * author:  ljy
 * date:    2018/3/20
 * description: 缓存拦截器
 */

public class HttpCacheInterceptor implements Interceptor {
    private Context mContext;
    private int mCacheTimeWithNet;
    private int mCacheTimeWithoutNet;

    public HttpCacheInterceptor(Context context, int cacheTimeWithNet, int cacheTimeWithoutNet) {
        this.mContext = context;
        this.mCacheTimeWithNet = cacheTimeWithNet;
        this.mCacheTimeWithoutNet = cacheTimeWithoutNet;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        //对request的设置是用来指定有网/无网下所走的方式
        //对response的设置是用来指定有网/无网下的缓存时长

        Request request = chain.request();
        if (!NetworkUtil.isNetWorkAvailable(mContext)) {
            //无网络下强制使用缓存，无论缓存是否过期,此时该请求实际上不会被发送出去。
            //有网络时则根据缓存时长来决定是否发出请求
            request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
        }

        Response response = chain.proceed(request);
        if (NetworkUtil.isNetWorkAvailable(mContext)) {
            //有网络情况下，根据请求接口的设置，配置缓存。
//                    String cacheControl = request.cacheControl().toString();

            //有网络情况下，超过1分钟，则重新请求，否则直接使用缓存数据
            int maxAge = mCacheTimeWithNet > 0 ? mCacheTimeWithNet : 60; //缓存一分钟
            String cacheControl = "public,max-age=" + maxAge;
            //当然如果你想在有网络的情况下都直接走网络，那么只需要
            //将其超时时间maxAge设为0即可
            return response.newBuilder().header("Cache-Control", cacheControl).removeHeader("Pragma").build();
        } else {
            //无网络时直接取缓存数据，该缓存数据保存1周
            int maxStale = mCacheTimeWithoutNet > 0 ? mCacheTimeWithoutNet : 60 * 60 * 24 * 7 * 1;  //1周
            return response.newBuilder().header("Cache-Control", "public,only-if-cached,max-stale=" + maxStale).removeHeader("Pragma").build();
        }
    }
}
