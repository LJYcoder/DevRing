package com.ljy.devring.websocket.support;

import java.io.Serializable;

import okhttp3.WebSocket;
import okio.ByteString;

public class WebSocketInfo implements Serializable, ICacheTarget<WebSocketInfo> {
    private static final long serialVersionUID = -880481254453932113L;

    private WebSocket mWebSocket;
    private String mStringMsg;
    private ByteString mByteStringMsg;
    /**
     * 连接成功
     */
    private boolean isConnect;
    /**
     * 重连成功
     */
    private boolean isReconnect;
    /**
     * 准备重连
     */
    private boolean isPrepareReconnect;


    /**
     * 重置
     */
    @Override
    public WebSocketInfo reset() {
        this.mWebSocket = null;
        this.mStringMsg = null;
        this.mByteStringMsg = null;
        this.isConnect = false;
        this.isReconnect = false;
        this.isPrepareReconnect = false;
        return this;
    }

    public WebSocket getWebSocket() {
        return mWebSocket;
    }

    public WebSocketInfo setWebSocket(WebSocket mWebSocket) {
        this.mWebSocket = mWebSocket;
        return this;
    }

    public String getStringMsg() {
        return mStringMsg;
    }

    public WebSocketInfo setStringMsg(String mStringMsg) {
        this.mStringMsg = mStringMsg;
        return this;
    }

    public ByteString getByteStringMsg() {
        return mByteStringMsg;
    }


    public WebSocketInfo setByteStringMsg(ByteString mByteStringMsg) {
        this.mByteStringMsg = mByteStringMsg;
        return this;
    }

    public boolean isConnect() {
        return isConnect;
    }

    public WebSocketInfo setConnect(boolean connect) {
        isConnect = connect;
        return this;
    }

    public boolean isReconnect() {
        return isReconnect;
    }

    public WebSocketInfo setReconnect(boolean reconnect) {
        isReconnect = reconnect;
        return this;
    }

    public boolean isPrepareReconnect() {
        return isPrepareReconnect;
    }

    public WebSocketInfo setPrepareReconnect(boolean prepareReconnect) {
        isPrepareReconnect = prepareReconnect;
        return this;
    }
}