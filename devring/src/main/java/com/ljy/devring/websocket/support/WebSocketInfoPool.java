package com.ljy.devring.websocket.support;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Objects;

import okhttp3.WebSocket;

/**
 * @author: XieYos
 * @date: 2021年9月2日
 * @description: WebSocket缓存池
 */
public class WebSocketInfoPool extends BaseCachePool<WebSocketInfo> {

    private int maxCacheCount;

    public WebSocketInfoPool() {
    }

    public WebSocketInfoPool(int maxCacheCount) {
        this.maxCacheCount = maxCacheCount;
    }

    public WebSocketInfo onCreateCache() {
        return new WebSocketInfo();
    }

    @Override
    public int onSetupMaxCacheCount() {
        return this.maxCacheCount;
    }
}
