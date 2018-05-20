package com.ljy.devring.cache;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Process;
import android.support.v4.util.SimpleArrayMap;

import com.ljy.devring.cache.support.DiskCache;
import com.ljy.devring.cache.support.MemoryCache;
import com.ljy.devring.cache.support.SpCache;
import com.ljy.devring.util.FileUtil;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Lazy;

/**
 * author:  ljy
 * date:    2018/3/16
 * description: 缓存管理者
 */
@Singleton
public class CacheManager {

    private static final long DEFAULT_DISK_CACHE_MAX_SIZE = Long.MAX_VALUE;
    private static final int DEFAULT_DISK_CACHE_MAX_COUNT = Integer.MAX_VALUE;
    private static final String DEFAULT_SP_NAME = "default_sp_name_";

    @Inject
    Application mContext;
    @Inject
    CacheConfig mCacheConfig;
    @Inject
    SimpleArrayMap<String, SpCache> mMapSpCache;
    @Inject
    SimpleArrayMap<String, DiskCache> mMapDiskCache;
    @Inject
    Lazy<MemoryCache> mMemoryCache;

    @Inject
    public CacheManager() {
    }

    public DiskCache diskCache(String cacheName) {
        File cacheDir;

        if (isSpace(cacheName)) cacheName = "cache_default";
        if (mCacheConfig.getDiskCacheFolder() != null && mCacheConfig.getDiskCacheFolder().isDirectory()) {
            cacheDir = new File(mCacheConfig.getDiskCacheFolder(), cacheName);
        } else {
            cacheDir = new File(FileUtil.getCacheDir(mContext), cacheName);
        }

        String cacheKey = cacheDir.getAbsoluteFile() + "_" + Process.myPid();
        DiskCache cache = mMapDiskCache.get(cacheKey);
        if (cache == null) {
            long maxSize = mCacheConfig.getDiskCacheMaxSize() > 0 ? mCacheConfig.getDiskCacheMaxSize() : DEFAULT_DISK_CACHE_MAX_SIZE;
            int maxCount = mCacheConfig.getDiskCacheMaxCount() > 0 ? mCacheConfig.getDiskCacheMaxCount() : DEFAULT_DISK_CACHE_MAX_COUNT;
            cache = new DiskCache(mContext, cacheDir, maxSize, maxCount);
            mMapDiskCache.put(cacheKey, cache);
        }
        return cache;
    }

    private boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public MemoryCache memoryCache() {
        return mMemoryCache.get();
    }

    /**
     * 返回默认提供的sp，Mode默认为Context.MODE_PRIVATE
     */
    public SpCache spCache() {
        return spCache(DEFAULT_SP_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 返回默认提供的sp，Mode为指定的类型
     */
    public SpCache spCache(int mode) {
        return spCache(DEFAULT_SP_NAME, mode);
    }

    /**
     * 返回指定的Sp，Mode默认为Context.MODE_PRIVATE
     */
    public SpCache spCache(String spName) {
        return spCache(spName, Context.MODE_PRIVATE);
    }

    /**
     * 返回指定的Sp，Mode为指定的类型
     */
    public SpCache spCache(String spName, int mode) {
        SpCache spCache = mMapSpCache.get(spName + mode);
        if (spCache == null) {
            SharedPreferences defaultSp = mContext.getSharedPreferences(spName, mode);
            spCache = new SpCache(defaultSp);
            mMapSpCache.put(spName + mode, spCache);
        }
        return spCache;
    }

}
