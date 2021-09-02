package com.ljy.devring.websocket;

import android.util.Log;

import com.ljy.devring.logger.LoggerConfig;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import dagger.Lazy;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * author:  ljy
 * date:    2018/3/20
 * description: 网络请求配置
 */
@Singleton
public class WebSocketConfig {
    @Inject
    Lazy<OkHttpClient.Builder> mOkHttpClientBuilder;
    @Inject
    Lazy<Retrofit.Builder> mRetrofitBuilder;

    private String mBaseUrl;
    private int mConnectTimeout;
    private int mReadTimeout;
    private boolean mIsUseLog;//是否使用日志
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
    /**
     * 支持SSL
     */
    private SSLSocketFactory mSslSocketFactory;
    private X509TrustManager mTrustManager;
    /**
     * 重连间隔时间
     */
    private long mReconnectInterval;
    /**
     * 重连间隔时间的单位
     */
    private TimeUnit mReconnectIntervalTimeUnit;


    @Inject
    public WebSocketConfig() {
    }

    public String getBaseUrl() {
        return mBaseUrl == null ? "" : mBaseUrl;
    }

    //设置BaseUrl
    public WebSocketConfig setBaseUrl(String baseUrl) {
        this.mBaseUrl = baseUrl;
        return this;
    }

    public int getConnectTimeout() {
        return mConnectTimeout;
    }

    //设置请求超时时长，单位秒
    public WebSocketConfig setConnectTimeout(int connectTimeout) {
        this.mConnectTimeout = connectTimeout;
        return this;
    }

    public int getReadTimeout() {
        return mReadTimeout;
    }

    //设置请求结果读取超时时间，单位秒
    public WebSocketConfig setReadTimeout(int readTimeout) {
        this.mReadTimeout = readTimeout;
        return this;
    }

    public boolean isUseLog() {
        return mIsUseLog;
    }

    //设置是否开启Log，默认不开启
    public WebSocketConfig setIsUseLog(boolean useLog) {
        mIsUseLog = useLog;
        return this;
    }

    public boolean isUseCache() {
        return mIsUseCache;
    }

    //设置是否启用缓存，默认不启用
    public WebSocketConfig setIsUseCache(boolean useCache) {
        mIsUseCache = useCache;
        return this;
    }

    public File getCacheFolder() {
        return mCacheFolder;
    }

    //设置缓存地址，传入的file需为文件夹，默认保存在/storage/emulated/0/Android/data/com.xxx.xxx/cache/retrofit_http_cache下
    public WebSocketConfig setCacheFolder(File cacheFolder) {
        this.mCacheFolder = cacheFolder;
        return this;
    }

    public int getCacheSize() {
        return mCacheSize;
    }

    //设置缓存大小，单位byte，默认20M
    public WebSocketConfig setCacheSize(int cacheSize) {
        this.mCacheSize = cacheSize;
        return this;
    }

    public int getCacheTimeWithNet() {
        return mCacheTimeWithNet;
    }

    //设置有网络时缓存保留时长，单位秒，默认60秒
    public WebSocketConfig setCacheTimeWithNet(int cacheTimeWithNet) {
        this.mCacheTimeWithNet = cacheTimeWithNet;
        return this;
    }

    public int getCacheTimeWithoutNet() {
        return mCacheTimeWithoutNet;
    }

    //设置无网络时缓存保留时长，单位秒，默认一周
    public WebSocketConfig setCacheTimeWithoutNet(int cacheTimeWithoutNet) {
        this.mCacheTimeWithoutNet = cacheTimeWithoutNet;
        return this;
    }

    public Map<String, String> getMapHeader() {
        return mMapHeader;
    }

    //设置全局的header信息
    public WebSocketConfig setMapHeader(Map<String, String> mapHeader) {
        this.mMapHeader = mapHeader;
        return this;
    }

    public boolean isUseRetryWhenError() {
        return mIsUseRetryWhenError;
    }

    //设置是否开启失败重试功能，目前仅支持普通的网络请求，上传下载不支持。默认不开启
    public WebSocketConfig setIsUseRetryWhenError(boolean useRetryWhenError) {
        mIsUseRetryWhenError = useRetryWhenError;
        return this;
    }

    public int getTimeRetryDelay() {
        return mTimeRetryDelay;
    }

    //设置失败后重试的延迟时长，单位秒，默认3秒
    public WebSocketConfig setTimeRetryDelay(int timeRetryDelay) {
        this.mTimeRetryDelay = timeRetryDelay;
        return this;
    }

    public int getMaxRetryCount() {
        return mMaxRetryCount;
    }

    //设置失败后重试的最大次数，默认3次
    public WebSocketConfig setMaxRetryCount(int maxRetryCount) {
        this.mMaxRetryCount = maxRetryCount;
        return this;
    }

    public boolean isCookiePersistent() {
        return mIsCookiePersistent;
    }

    //设置cookie是否为持久化类型
    public WebSocketConfig setIsCookiePersistent(boolean isCookiePersistent) {
        this.mIsCookiePersistent = isCookiePersistent;
        return this;
    }

    //是否使用Cookie
    public boolean isUseCookie() {
        return mIsUseCookie;
    }

    public WebSocketConfig setIsUseCookie(boolean mIsUseCookie) {
        this.mIsUseCookie = mIsUseCookie;
        return this;
    }

    public SSLSocketFactory getmSslSocketFactory() {
        return mSslSocketFactory;
    }

    public void setmSslSocketFactory(SSLSocketFactory mSslSocketFactory) {
        this.mSslSocketFactory = mSslSocketFactory;
    }

    public X509TrustManager getmTrustManager() {
        return mTrustManager;
    }

    public void setmTrustManager(X509TrustManager mTrustManager) {
        this.mTrustManager = mTrustManager;
    }

    public long getmReconnectInterval() {
        return mReconnectInterval;
    }

    public void setmReconnectInterval(long mReconnectInterval) {
        this.mReconnectInterval = mReconnectInterval;
    }

    public TimeUnit getmReconnectIntervalTimeUnit() {
        return mReconnectIntervalTimeUnit;
    }

    public void setmReconnectIntervalTimeUnit(TimeUnit mReconnectIntervalTimeUnit) {
        this.mReconnectIntervalTimeUnit = mReconnectIntervalTimeUnit;
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
