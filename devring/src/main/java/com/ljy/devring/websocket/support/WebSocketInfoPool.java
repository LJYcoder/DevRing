package com.ljy.devring.websocket.support;

import okhttp3.WebSocket;

public class WebSocketInfoPool extends BaseCachePool<WebSocketInfo> {

    private WebSocketInfo webSocketInfo;

    @Override
    public WebSocketInfo onCreateCache() {
        return webSocketInfo;
    }

    @Override
    public int onSetupMaxCacheCount() {
        return 0;
    }
}
