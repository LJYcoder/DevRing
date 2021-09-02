package com.ljy.devring.websocket.support;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
/**
 * @author:  XieYos
 * @date:    2021年9月2日
 * @description: 基础缓存池
 */
public abstract class BaseCachePool<T extends ICacheTarget<T>> implements ICachePool<T>, Comparator<CacheItem<T>> {
    /**
     * 缓存池
     */
    private ConcurrentHashMap<String, LinkedList<CacheItem<T>>> mPool;

    public BaseCachePool() {
        mPool = new ConcurrentHashMap<>(8);
    }

    @Override
    public T obtain(String cacheKey) {
        //缓存链
        LinkedList<CacheItem<T>> cacheChain;
        //没有缓存过，进行缓存
        if (!mPool.containsKey(cacheKey)) {
            cacheChain = new LinkedList<>();
        } else {
            cacheChain = mPool.get(cacheKey);
        }
        if (cacheChain == null) {
            throw new NullPointerException("cacheChain 缓存链创建失败");
        }
        //未满最大缓存数量，生成一个实例
        if (cacheChain.size() < onSetupMaxCacheCount()) {
            T cache = onCreateCache();
            CacheItem<T> cacheItem = new CacheItem<>(cache, System.currentTimeMillis());
            cacheChain.add(cacheItem);
            mPool.put(cacheKey, cacheChain);
            return cache;
        }
        //达到最大缓存数量。按最近的使用时间排序，最近使用的放后面，每次取只取最前面（最久没有使用的）
        Collections.sort(cacheChain, this);
        CacheItem<T> cacheItem = cacheChain.getFirst();
        cacheItem.setRecentlyUsedTime(System.currentTimeMillis());
        //重置所有属性
        T cacheTarget = cacheItem.getCacheTarget();
        cacheTarget = onObtainCacheAfter(cacheTarget);
        return cacheTarget;
    }

    @Override
    public T onObtainCacheAfter(ICacheTarget<T> cacheTarget) {
        //默认调用reset方法进行重置，如果有其他需求，子类再进行复写
        return cacheTarget.reset();
    }

    @Override
    public int compare(CacheItem<T> o1, CacheItem<T> o2) {
        return Long.compare(o1.getRecentlyUsedTime(), o2.getRecentlyUsedTime());
    }
}
