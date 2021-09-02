package com.ljy.devring.websocket.support;

/**
 * @author: XieYos
 * @date: 2021年9月2日
 * @description: 缓存池接口
 */
public interface ICachePool<T extends ICacheTarget<T>> {
    /**
     * 创建缓存时回调
     */
    T onCreateCache();

    /**
     * 设置缓存对象的最大个数
     */
    int onSetupMaxCacheCount();

    /**
     * 获取一个缓存对象
     *
     * @param cacheKey 缓存Key，为了应对多个观察者同时获取缓存使用
     */
    T obtain(String cacheKey);

    /**
     * 当获取一个缓存后回调，一般在该回调中重置对象的所有字段
     *
     * @param cacheTarget 缓存对象
     */
    T onObtainCacheAfter(ICacheTarget<T> cacheTarget);
}