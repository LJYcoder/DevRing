package com.ljy.devring.image.support;

import java.io.File;

/**
 * author:  ljy
 * date:    2018/3/13
 * description: 全局的图片加载配置
 */

public class ImageConfig {

    private int mLoadingResId;
    private int mErrorResId;
    private int mMemoryCacheSize;
    private int mBitmapPoolSize;
    private int mDiskCacheSize;
    private boolean mIsDiskCacheExternal;
    private File mDiskCacheFile;
    private boolean mIsShowTransition;
    private boolean mIsUseOkhttp;
    private boolean mIsUseMemoryCache;
    private boolean mIsUseDiskCache;

    public int getLoadingResId() {
        return mLoadingResId;
    }

    //设置“加载中”状态时显示的图片
    public ImageConfig setLoadingResId(int loadingResId) {
        this.mLoadingResId = loadingResId;
        return this;
    }

    public int getErrorResId() {
        return mErrorResId;
    }

    //设置“加载失败”状态时显示的图片
    public ImageConfig setErrorResId(int errorResId) {
        this.mErrorResId = errorResId;
        return this;
    }

    public int getMemoryCacheSize() {
        return mMemoryCacheSize;
    }

    //设置内存缓存大小，不建议设置，使用框架默认设置的大小即可
    public ImageConfig setMemoryCacheSize(int memoryCacheSize) {
        this.mMemoryCacheSize = memoryCacheSize;
        return this;
    }

    public int getBitmapPoolSize() {
        return mBitmapPoolSize;
    }

    //设置Bitmap池大小，设置内存缓存大小的话一般这个要一起设置，不建议设置，使用框架默认设置的大小即可
    public ImageConfig setBitmapPoolSize(int bitmapPoolSize) {
        this.mBitmapPoolSize = bitmapPoolSize;
        return this;
    }

    public int getDiskCacheSize() {
        return mDiskCacheSize;
    }

    //设置磁盘缓存大小，单位byte，默认250M
    public ImageConfig setDiskCacheSize(int diskCacheSize) {
        this.mDiskCacheSize = diskCacheSize;
        return this;
    }

    public boolean isDiskCacheExternal() {
        return mIsDiskCacheExternal;
    }

    //设置磁盘缓存地址是否在外部存储中，默认false
    public ImageConfig setIsDiskCacheExternal(boolean isDiskCacheExternal) {
        this.mIsDiskCacheExternal = isDiskCacheExternal;
        return this;
    }

    public File getDiskCacheFile() {
        return mDiskCacheFile;
    }

    //设置具体的磁盘缓存地址，传入的file需为文件夹
    public ImageConfig setDiskCacheFile(File diskCacheFile) {
        this.mDiskCacheFile = diskCacheFile;
        return this;
    }

    public boolean isShowTransition() {
        return mIsShowTransition;
    }

    //设置是否开启状态切换时的过渡动画，默认false
    public ImageConfig setIsShowTransition(boolean isShowTransition) {
        this.mIsShowTransition = isShowTransition;
        return this;
    }

    public boolean isUseOkhttp() {
        return mIsUseOkhttp;
    }

    //设置是否使用okhttp3作为网络组件，默认true
    public ImageConfig setIsUseOkhttp(boolean isUseOkhttp) {
        this.mIsUseOkhttp = isUseOkhttp;
        return this;
    }

    public boolean isUseMemoryCache() {
        return mIsUseMemoryCache;
    }

    //设置是否使用内存缓存，默认true
    public ImageConfig setIsUseMemoryCache(boolean isUseMemoryCache) {
        this.mIsUseMemoryCache = isUseMemoryCache;
        return this;
    }

    public boolean isUseDiskCache() {
        return mIsUseDiskCache;
    }

    //设置是否使用磁盘缓存，默认true
    public ImageConfig setIsUseDiskCache(boolean isUseDiskCache) {
        this.mIsUseDiskCache = isUseDiskCache;
        return this;
    }
}
