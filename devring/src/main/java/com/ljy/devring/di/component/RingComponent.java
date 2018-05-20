package com.ljy.devring.di.component;

import android.app.Application;
import android.support.v4.util.SimpleArrayMap;

import com.ljy.devring.DevRing;
import com.ljy.devring.base.activity.ActivityLifeCallback;
import com.ljy.devring.base.fragment.FragmentLifeCallback;
import com.ljy.devring.bus.BusConfig;
import com.ljy.devring.bus.support.IBusManager;
import com.ljy.devring.cache.CacheConfig;
import com.ljy.devring.cache.CacheManager;
import com.ljy.devring.db.support.ITableManger;
import com.ljy.devring.di.module.ConfigModule;
import com.ljy.devring.di.module.OtherModule;
import com.ljy.devring.di.module.RingModule;
import com.ljy.devring.http.HttpConfig;
import com.ljy.devring.http.HttpManager;
import com.ljy.devring.image.support.IImageManager;
import com.ljy.devring.image.support.ImageConfig;
import com.ljy.devring.other.ActivityListManager;
import com.ljy.devring.other.CrashDiary;
import com.ljy.devring.other.OtherConfig;
import com.ljy.devring.other.PermissionManager;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import okhttp3.OkHttpClient;

/**
 * author:  ljy
 * date:    2018/3/10
 * description: 全局、单例、核心的Component
 *
 * https://www.jianshu.com/p/08b1fd6fb53b
 */
@Singleton
@Component(modules = {RingModule.class, OtherModule.class, ConfigModule.class})
public interface RingComponent {

    Application application();//提供Application


    BusConfig busConfig();//提供事件总线配置

    ImageConfig imageConfig();//提供图片加载配置

    CacheConfig cacheConfig();//提供缓存配置

    HttpConfig httpConfig();//提供网络请求配置

    OtherConfig otherConfig();//提供其他模块的配置


    IBusManager busManager();//提供事件总线管理者

    IImageManager imageManager();//提供图片加载管理者

    CacheManager cacheManager();//提供缓存管理者

    HttpManager httpManager();//提供网络请求的管理者

    CrashDiary crashDiary();//提供崩溃日志管理者

    ActivityListManager activityListManager();

    PermissionManager permissionManager();//提供权限管理的管理者

    OkHttpClient okHttpClient();//提供OkHttpClient

    SimpleArrayMap<Object, ITableManger> mapTableManager();//提供存放表管理者的map

    ActivityLifeCallback activityLifeCallback();

    FragmentLifeCallback fragmentLifeCallback();

    void inject(DevRing devRing);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        RingComponent build();
    }
}
