package com.ljy.devring.http;

import java.io.File;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Lazy;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * author:  ljy
 * date:    2018/3/20
 * description: 网络请求配置
 */
@Singleton
public class HttpConfig {
    @Inject
    Lazy<OkHttpClient.Builder> mOkHttpClientBuilder;
    @Inject
    Lazy<Retrofit.Builder> mRetrofitBuilder;

    private String mBaseUrl;
    private int mConnectTimeout;
    private boolean mIsUseLog;
    private boolean mIsUseCache;
    private File mCacheFolder;
    private int mCacheSize;
    private int mCacheTimeWithNet;
    private int mCacheTimeWithoutNet;
    private Map<String, String> mMapHeader;
    private boolean mIsUseRetryWhenError;
    private int mTimeRetryDelay = -1;
    private int mMaxRetryCount;
    private boolean mIsCookiePersistent;
    private boolean mIsUseCookie;

    @Inject
    public HttpConfig() {
    }

    public String getBaseUrl() {
        return mBaseUrl == null ? "" : mBaseUrl;
    }

    //设置BaseUrl
    public HttpConfig setBaseUrl(String baseUrl) {
        this.mBaseUrl = baseUrl;
        return this;
    }

    public int getConnectTimeout() {
        return mConnectTimeout;
    }

    //设置请求超时时长，单位秒
    public HttpConfig setConnectTimeout(int connectTimeout) {
        this.mConnectTimeout = connectTimeout;
        return this;
    }

    public boolean isUseLog() {
        return mIsUseLog;
    }

    //设置是否开启Log，默认不开启
    public HttpConfig setIsUseLog(boolean useLog) {
        mIsUseLog = useLog;
        return this;
    }

    public boolean isUseCache() {
        return mIsUseCache;
    }

    //设置是否启用缓存，默认不启用
    public HttpConfig setIsUseCache(boolean useCache) {
        mIsUseCache = useCache;
        return this;
    }

    public File getCacheFolder() {
        return mCacheFolder;
    }

    //设置缓存地址，传入的file需为文件夹，默认保存在/storage/emulated/0/Android/data/com.xxx.xxx/cache/retrofit_http_cache下
    public HttpConfig setCacheFolder(File cacheFolder) {
        this.mCacheFolder = cacheFolder;
        return this;
    }

    public int getCacheSize() {
        return mCacheSize;
    }

    //设置缓存大小，单位byte，默认20M
    public HttpConfig setCacheSize(int cacheSize) {
        this.mCacheSize = cacheSize;
        return this;
    }

    public int getCacheTimeWithNet() {
        return mCacheTimeWithNet;
    }

    //设置有网络时缓存保留时长，单位秒，默认60秒
    public HttpConfig setCacheTimeWithNet(int cacheTimeWithNet) {
        this.mCacheTimeWithNet = cacheTimeWithNet;
        return this;
    }

    public int getCacheTimeWithoutNet() {
        return mCacheTimeWithoutNet;
    }

    //设置无网络时缓存保留时长，单位秒，默认一周
    public HttpConfig setCacheTimeWithoutNet(int cacheTimeWithoutNet) {
        this.mCacheTimeWithoutNet = cacheTimeWithoutNet;
        return this;
    }

    public Map<String, String> getMapHeader() {
        return mMapHeader;
    }

    //设置全局的header信息
    public HttpConfig setMapHeader(Map<String, String> mapHeader) {
        this.mMapHeader = mapHeader;
        return this;
    }

    public boolean isUseRetryWhenError() {
        return mIsUseRetryWhenError;
    }

    //设置是否开启失败重试功能，目前仅支持普通的网络请求，上传下载不支持。默认不开启
    public HttpConfig setIsUseRetryWhenError(boolean useRetryWhenError) {
        mIsUseRetryWhenError = useRetryWhenError;
        return this;
    }

    public int getTimeRetryDelay() {
        return mTimeRetryDelay;
    }

    //设置失败后重试的延迟时长，单位秒，默认3秒
    public HttpConfig setTimeRetryDelay(int timeRetryDelay) {
        this.mTimeRetryDelay = timeRetryDelay;
        return this;
    }

    public int getMaxRetryCount() {
        return mMaxRetryCount;
    }

    //设置失败后重试的最大次数，默认3次
    public HttpConfig setMaxRetryCount(int maxRetryCount) {
        this.mMaxRetryCount = maxRetryCount;
        return this;
    }

    public boolean isCookiePersistent() {
        return mIsCookiePersistent;
    }

    //设置cookie是否为持久化类型
    public HttpConfig setIsCookiePersistent(boolean isCookiePersistent) {
        this.mIsCookiePersistent = isCookiePersistent;
        return this;
    }

    //是否使用Cookie
    public boolean isUseCookie() {
        return mIsUseCookie;
    }

    public HttpConfig setIsUseCookie(boolean mIsUseCookie) {
        this.mIsUseCookie = mIsUseCookie;
        return this;
    }

    //获取builder进行你的个人定制
    public OkHttpClient.Builder getOkHttpClientBuilder() {
        return mOkHttpClientBuilder.get();
    }

    //获取builder进行你的个人定制
    public Retrofit.Builder getRetrofitBuilder() {
        return mRetrofitBuilder.get();
    }
}
