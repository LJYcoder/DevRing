package com.ljy.devring.websocket;

import com.ljy.devring.websocket.support.HeartBeatGenerateCallback;
import com.ljy.devring.websocket.support.WebSocketInfo;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okio.ByteString;

public interface WebSocketService {
    /**
     * 获取连接，并返回观察对象
     */
    Observable<WebSocketInfo> get(String url);

    /**
     * 设置一个超时时间，在指定时间内如果没有收到消息，会尝试重连
     *
     * @param timeout  超时时间
     * @param timeUnit 超时时间单位
     */
    Observable<WebSocketInfo> get(String url, long timeout, TimeUnit timeUnit);

    /**
     * 发送，url的WebSocket已打开的情况下使用，否则会抛出异常
     *
     * @param msg 消息，看具体和后端协商的格式，一般为json
     */
    Observable<Boolean> send(String url, String msg);

    /**
     * 发送，同上
     *
     * @param byteString 信息类型为ByteString
     */
    Observable<Boolean> send(String url, ByteString byteString);

    /**
     * 不关心WebSocket是否连接，直接发送
     */
    Observable<Boolean> asyncSend(String url, String msg);

    /**
     * 同上，只是消息类型为ByteString
     */
    Observable<Boolean> asyncSend(String url, ByteString byteString);

    /**
     * 发送心跳包
     * @param url
     * @param period
     * @param unit
     * @param heartBeatGenerateCallback
     * @return
     */
    Observable<Boolean> heartBeat(String url, int period, TimeUnit unit,
                                  HeartBeatGenerateCallback heartBeatGenerateCallback);

    /**
     * 关闭指定Url的连接
     */
    Observable<Boolean> close(String url);

    /**
     * 马上关闭指定Url的连接
     */
    boolean closeNow(String url);

    /**
     * 关闭当前所有连接
     */
    Observable<List<Boolean>> closeAll();

    /**
     * 马上关闭所有连接
     */
    void closeAllNow();
}
