package com.ljy.devring;

import android.app.Application;

import com.ljy.devring.bus.BusConfig;
import com.ljy.devring.bus.EventBusManager;
import com.ljy.devring.bus.support.IBusManager;
import com.ljy.devring.cache.CacheConfig;
import com.ljy.devring.cache.CacheManager;
import com.ljy.devring.db.support.IDBManager;
import com.ljy.devring.db.support.ITableManger;
import com.ljy.devring.di.component.DaggerRingComponent;
import com.ljy.devring.di.component.RingComponent;
import com.ljy.devring.http.HttpConfig;
import com.ljy.devring.http.HttpManager;
import com.ljy.devring.image.support.IImageManager;
import com.ljy.devring.image.support.ImageConfig;
import com.ljy.devring.other.ActivityListManager;
import com.ljy.devring.other.OtherConfig;
import com.ljy.devring.other.PermissionManager;
import com.ljy.devring.other.RingLog;
import com.ljy.devring.util.Preconditions;
import com.ljy.devring.other.toast.RingToast;

/**
 * author:  ljy
 * date:    2018/3/10
 * description: DevRing库核心操作类
 *
 * DevRing详细使用说明：<a>https://www.jianshu.com/p/abede6623c58</a>
 */

public class DevRing {

    private static RingComponent mRingComponent;
    private static IDBManager mDBManager;
    private static IBusManager mBusManager;
    private static IImageManager mImageManager;

    /**
     * 初始化操作
     */
    public static void init(Application application) {
        mRingComponent = DaggerRingComponent.builder().application(application).build();//如果提示找不到DaggerRingComponent类，请重新编译下项目。
        application.registerActivityLifecycleCallbacks(mRingComponent.activityLifeCallback());
    }

    /**
     * 开始构建
     */
    public static void create() {
        //数据库模块的构建工作
        if (mDBManager != null) {
            mDBManager.init();
            mDBManager.putTableManager(mRingComponent.mapTableManager());
        }

        //事件总线模块的构建工作
        if (busManager() instanceof EventBusManager) {
            ((EventBusManager) busManager()).openIndex();
        }

        //图片加载模块的构建工作
        imageManager().init(mRingComponent.application(), mRingComponent.imageConfig());

        //其他模块的构建工作
        //崩溃日志
        if (mRingComponent.otherConfig().isUseCrashDiary()) {
            mRingComponent.crashDiary().init(mRingComponent.application(), mRingComponent.otherConfig().getCrashDiaryFolder());
        }
        //RingLog
        RingLog.init(mRingComponent.otherConfig().isShowRingLog());
        //RingToast
        RingToast.init(mRingComponent.application());
        RingToast.initStyle(mRingComponent.otherConfig().getIToastStyle());
    }

    /**
     * 获取RingComponent，从而获取RingComponent中提供的各对象。
     */
    public static RingComponent ringComponent() {
        return Preconditions.checkNotNull(mRingComponent, "RingComponent为空，请先在Application中调用DevRing.init(Application)方法进行初始化");
    }

    /**
     * 配置数据库模块
     */
    public static void configureDB(IDBManager dbManager) {
        mDBManager = dbManager;
    }

    /**
     * 获取数据库管理者
     */
    public static <T extends IDBManager> T dbManager() {
        return (T) Preconditions.checkNotNull(mDBManager, "请先在Application中调用DevRing.configureDB(IDBManager)方法设置数据库管理类");
    }

    /**
     * 获取数据表管理者
     */
    public static <T extends ITableManger> T tableManager(Object key) {
        return (T) Preconditions.checkNotNull(mRingComponent.mapTableManager().get(key), "没找到该Key值对应的数据表管理者，请检查IDBManager实现类中的putTableManager(Map<Object,ITableManager>)方法");
    }

    /**
     * 配置事件总线模块
     */
    public static BusConfig configureBus() {
        return mRingComponent.busConfig();
    }

    /**
     * 配置事件总线模块，用于替换默认的EventBus
     * @param busManager 要替换EventBus的事件总线管理者
     */
    public static void configureBus(IBusManager busManager) {
        mBusManager = busManager;
    }

    /**
     * 获取事件总线管理者
     */
    public static <T extends IBusManager> T busManager() {
        if (mBusManager != null) {
            return (T) mBusManager;
        }
        return (T) mRingComponent.busManager();
    }

    /**
     * 配置图片加载模块
     */
    public static ImageConfig configureImage() {
        return mRingComponent.imageConfig();
    }

    /**
     * 配置图片加载模块，用于替换默认的Glide
     * @param imageManager 要替换Glide的图片加载管理者
     */
    public static ImageConfig configureImage(IImageManager imageManager) {
        mImageManager = imageManager;
        return mRingComponent.imageConfig();
    }

    /**
     * 获取图片加载管理者
     */
    public static <T extends IImageManager> T imageManager() {
        if (mImageManager != null) {
            return (T) mImageManager;
        }
        return (T) mRingComponent.imageManager();
    }

    /**
     * 配置缓存模块
     */
    public static CacheConfig configureCache() {
        return mRingComponent.cacheConfig();
    }

    /**
     * 获取缓存管理者
     */
    public static CacheManager cacheManager() {
        return mRingComponent.cacheManager();
    }

    /**
     * 配置网络请求模块
     */
    public static HttpConfig configureHttp() {
        return mRingComponent.httpConfig();
    }

    /**
     * 获取网络请求管理者
     */
    public static HttpManager httpManager() {
        return mRingComponent.httpManager();
    }

    /**
     * 配置其他模块
     */
    public static OtherConfig configureOther() {
        return mRingComponent.otherConfig();
    }

    public static ActivityListManager activityListManager() {
        return mRingComponent.activityListManager();
    }

    /**
     * 获取权限管理者
     */
    public static PermissionManager permissionManager() {
        return mRingComponent.permissionManager();
    }

    /**
     * 获取Application
     */
    public static Application application() {
        return mRingComponent.application();
    }
}
