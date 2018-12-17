package com.ljy.devring.di.module;

import com.ljy.devring.bus.BusConfig;
import com.ljy.devring.cache.CacheConfig;
import com.ljy.devring.image.support.ImageConfig;
import com.ljy.devring.other.OtherConfig;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * author:  ljy
 * date:    2018/3/11
 * description: 各模块配置的供应Module
 */

@Module
public class ConfigModule {

    @Provides
    @Singleton
    BusConfig busConfig() {
        return new BusConfig();
    }

    @Provides
    @Singleton
    ImageConfig imageConfig() {
        ImageConfig imageConfig = new ImageConfig();
        imageConfig.setIsUseOkhttp(true);//默认使用okhttp3替换网络组件
        imageConfig.setIsUseMemoryCache(true);//默认使用内存缓存
        imageConfig.setIsUseDiskCache(true);//默认使用磁盘缓存
        return imageConfig;
    }

    @Provides
    @Singleton
    CacheConfig cacheConfig() {
        return new CacheConfig();
    }

    @Provides
    @Singleton
    OtherConfig crashDiaryConfig() {
        return new OtherConfig();
    }


}
