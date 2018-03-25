package com.ljy.devring.image.support;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.ljy.devring.DevRing;

import java.io.File;
import java.io.InputStream;

/**
 * author:  ljy
 * date:    2018/3/14
 * description: Glide配置类，在这里配置缓存以及网络组件等
 * 因为该类已经继承了AppGlideModule，所以主项目中不能再有类继承AppGlideModule来做自定义模块，否则会报
 * com.android.dex.DexException: Multiple dex files define Lcom/bumptech/glide/GeneratedAppGlideModuleImpl异常，
 * 但允许有多个继承LibraryGlideModule自定义registerComponents模块。
 */
@GlideModule
public class GlideConfigModule extends AppGlideModule {

    private ImageConfig mImageConfig = DevRing.configureImage();

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        //磁盘缓存配置
        if (mImageConfig.getDiskCacheFile() != null) {
            builder.setDiskCache(new DiskLruCacheFactory(new DiskLruCacheFactory.CacheDirectoryGetter() {
                @Override
                public File getCacheDirectory() {
                    return mImageConfig.getDiskCacheFile();
                }
            }, mImageConfig.getDiskCacheSize() > 0 ? mImageConfig.getDiskCacheSize() : DiskCache.Factory.DEFAULT_DISK_CACHE_SIZE));
        } else if (mImageConfig.isDiskCacheExternal()) {
            builder.setDiskCache(new ExternalCacheDiskCacheFactory(context, mImageConfig.getDiskCacheSize() > 0 ? mImageConfig.getDiskCacheSize() : DiskCache.Factory
                    .DEFAULT_DISK_CACHE_SIZE));
        }
        //内存缓存配置
        if (mImageConfig.getMemoryCacheSize() > 0) {
            builder.setMemoryCache(new LruResourceCache(mImageConfig.getMemoryCacheSize()));
        }
        if (mImageConfig.getBitmapPoolSize() > 0) {
            builder.setBitmapPool(new LruBitmapPool(mImageConfig.getBitmapPoolSize()));
        }
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        //替换网络组件为okhttp3
        if (mImageConfig.isUseOkhttp()) {
            registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory());
        }
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
