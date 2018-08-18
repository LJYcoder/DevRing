package com.ljy.devring.di.module;

import android.app.Application;
import android.support.v4.util.SimpleArrayMap;
import android.text.TextUtils;

import com.ljy.devring.bus.EventBusManager;
import com.ljy.devring.bus.support.IBusManager;
import com.ljy.devring.cache.support.DiskCache;
import com.ljy.devring.cache.support.MemoryCache;
import com.ljy.devring.cache.support.SpCache;
import com.ljy.devring.db.support.ITableManger;
import com.ljy.devring.http.HttpConfig;
import com.ljy.devring.http.support.body.ProgressListener;
import com.ljy.devring.http.support.interceptor.HttpCacheInterceptor;
import com.ljy.devring.http.support.interceptor.HttpHeaderInterceptor;
import com.ljy.devring.http.support.interceptor.HttpLoggingInterceptor;
import com.ljy.devring.http.support.interceptor.HttpProgressInterceptor;
import com.ljy.devring.image.GlideManager;
import com.ljy.devring.image.support.IImageManager;
import com.ljy.devring.util.CollectionUtil;
import com.ljy.devring.util.FileUtil;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * author:  ljy
 * date:    2018/3/10
 * description: 核心模块（网络请求，数据库，图片加载，事件总线，缓存）的供应Module
 */

@Module
public class RingModule {

    //══════════════════════图片加载模块开始══════════════════════
    @Singleton
    @Provides
    IImageManager imageManager() {
        return new GlideManager();
    }
    //══════════════════════图片加载模块结束══════════════════════


    //══════════════════════事件总线模块开始══════════════════════
    @Singleton
    @Provides
    IBusManager busManager(EventBusManager eventBusManager) {
        return eventBusManager;
    }

//    @Binds
//    abstract IBusManager busManager(EventBusManager eventBusManager);//此时使用@Binds可稍微简化写法，但需为抽象类。效果同上面
    //══════════════════════事件总线模块结束══════════════════════


    //══════════════════════数据库模块开始══════════════════════
    @Singleton
    @Provides
    SimpleArrayMap<Object, ITableManger> mapTableManager() {
        return new SimpleArrayMap<>();
    }
    //══════════════════════数据库模块结束══════════════════════


    //══════════════════════网络模块开始══════════════════════
//    @Singleton
    @Provides
    Retrofit retrofit(Retrofit.Builder builder, OkHttpClient okHttpClient, HttpConfig httpConfig) {
        if (!TextUtils.isEmpty(httpConfig.getBaseUrl())) {
            builder.baseUrl(httpConfig.getBaseUrl());
        }
        //配置转化库，采用Gson
        builder.addConverterFactory(GsonConverterFactory.create());
        //配置回调库，采用RxJava
        builder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        //设置OKHttpClient为网络客户端
        builder.client(okHttpClient);
//        builder.callbackExecutor(Executors.newFixedThreadPool(1));
        return builder.build();
    }

    @Singleton
    @Provides
    Retrofit.Builder retrofitBuilder() {
        return new Retrofit.Builder();
    }

//    @Singleton
    @Provides
    OkHttpClient okHttpClient(Application application, OkHttpClient.Builder builder, HttpConfig httpConfig, HttpProgressInterceptor progressInterceptor) {
        if (httpConfig.getConnectTimeout() > 0) {
            builder.connectTimeout(httpConfig.getConnectTimeout(), TimeUnit.SECONDS);
        }
        if (httpConfig.isUseLog()) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
        }
        if (httpConfig.isUseCache()) {
            HttpCacheInterceptor cacheInterceptor = new HttpCacheInterceptor(application, httpConfig.getCacheTimeWithNet(), httpConfig.getCacheTimeWithoutNet());
            File cacheFile;//缓存目录
            if (httpConfig.getCacheFolder() != null && httpConfig.getCacheFolder().isDirectory()) {
                cacheFile = httpConfig.getCacheFolder();
            } else {
                cacheFile = FileUtil.getDirectory(FileUtil.getExternalCacheDir(application), "retrofit_http_cache");
            }
            Cache cache = new Cache(cacheFile, httpConfig.getCacheSize() > 0 ? httpConfig.getCacheSize() : 1024 * 1024 * 20); //大小默认20Mb
            builder.addInterceptor(cacheInterceptor);
            builder.addNetworkInterceptor(cacheInterceptor);
            builder.cache(cache);
        }
        if (!CollectionUtil.isEmpty(httpConfig.getMapHeader())) {
            HttpHeaderInterceptor headerInterceptor = new HttpHeaderInterceptor(httpConfig.getMapHeader());
            builder.addInterceptor(headerInterceptor);
        }
        builder.addNetworkInterceptor(progressInterceptor);
        return builder.build();
    }

    @Singleton
    @Provides
    OkHttpClient.Builder okClientBuilder() {
        return new OkHttpClient.Builder();
    }

    @Provides
    SimpleArrayMap<String, List<WeakReference<ProgressListener>>> listeners() {
        return new SimpleArrayMap<>();
    }
    //══════════════════════网络模块结束══════════════════════


    //══════════════════════缓存模块开始══════════════════════
    @Singleton
    @Provides
    SimpleArrayMap<String, SpCache> spCaches() {
        return new SimpleArrayMap<>();
    }

    @Singleton
    @Provides
    SimpleArrayMap<String, DiskCache> diskCaches() {
        return new SimpleArrayMap<>();
    }

    @Singleton
    @Provides
    MemoryCache memoryCache() {
        return new MemoryCache();
    }
    //══════════════════════缓存模块结束══════════════════════

}
