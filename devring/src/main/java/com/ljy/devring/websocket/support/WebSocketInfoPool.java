package com.ljy.devring.websocket.support;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Objects;

import okhttp3.WebSocket;

/**
 * @author Administrator
 */
public class WebSocketInfoPool extends BaseCachePool<WebSocketInfo> {

    private WebSocketInfo webSocketInfo;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public WebSocketInfo onCreateCache() {
        if (Objects.isNull(webSocketInfo)) {
            webSocketInfo = new WebSocketInfo();
        }
        return new WebSocketInfo();
    }

    @Override
    public int onSetupMaxCacheCount() {
        return 10;
    }
}
