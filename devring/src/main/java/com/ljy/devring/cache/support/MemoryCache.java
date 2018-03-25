package com.ljy.devring.cache.support;

import android.text.TextUtils;
import android.util.LruCache;

import com.ljy.devring.other.RingLog;

/**
 * @Description: 内存缓存
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2016-12-19 15:08
 */
public class MemoryCache {
    private LruCache<String, Object> cache;

    public MemoryCache() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        cache = new LruCache(cacheSize);
    }

    public synchronized void put(String key, Object value) {
        if (TextUtils.isEmpty(key)) return;

        if (cache.get(key) != null) {
            cache.remove(key);
        }
        cache.put(key, value);
    }

    public Object get(String key) {
        return cache.get(key);
    }

    public synchronized <T> T get(String key, Class<T> clazz) {
        try {
            return (T) cache.get(key);
        } catch (Exception e) {
            e.printStackTrace();
            RingLog.e(e);
        }
        return null;
    }

    public void remove(String key) {
        if (cache.get(key) != null) {
            cache.remove(key);
        }
    }

    public boolean contains(String key) {
        return cache.get(key) != null;
    }

    public void clear() {
        cache.evictAll();
    }
}
