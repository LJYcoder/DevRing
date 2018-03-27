package com.ljy.devring.cache;

import java.io.File;

/**
 * author:  ljy
 * date:    2018/3/16
 * description: 缓存配置
 */

public class CacheConfig {

    private File mDiskCacheFolder;
    private int mDiskCacheMaxSize;//单位byte
    private int mDiskCacheMaxCount;

    public File getDiskCacheFolder() {
        return mDiskCacheFolder;
    }

    //配置磁盘缓存的地址，传入的File需为文件夹，默认保存在/data/user/0/com.xxx.xxx/cache下
    public CacheConfig setDiskCacheFolder(File diskCacheFolder) {
        this.mDiskCacheFolder = diskCacheFolder;
        return this;
    }

    public int getDiskCacheMaxSize() {
        return mDiskCacheMaxSize;
    }

    //设置磁盘缓存最大缓存大小，单位为byte，默认无上限
    public CacheConfig setDiskCacheMaxSize(int diskCacheMaxSize) {
        this.mDiskCacheMaxSize = diskCacheMaxSize;
        return this;
    }

    public int getDiskCacheMaxCount() {
        return mDiskCacheMaxCount;
    }

    //设置磁盘缓存的文件夹数量上限，默认无上限
    public CacheConfig setDiskCacheMaxCount(int diskCacheMaxCount) {
        this.mDiskCacheMaxCount = diskCacheMaxCount;
        return this;
    }
}
