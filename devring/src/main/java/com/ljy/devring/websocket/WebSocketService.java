package com.ljy.devring.websocket;

import com.ljy.devring.websocket.support.HeartBeatGenerateCallback;
import com.ljy.devring.websocket.support.WebSocketInfo;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okio.ByteString;

/**
 * author:  XieYos
 * date:    2021年9月2日
 * description: WebSocket接口
 *
 */
public interface WebSocketService {
    /**
     * 获取连接，并返回观察对象
     * @param url WebSocket服务器地址
     * @return
     */
    Observable<WebSocketInfo> get(String url);

    /**
     * 设置一个超时时间，在指定时间内如果没有收到消息，会尝试重连
     * @param url WebSocket服务器地址
     * @param timeout  超时时间
     * @param timeUnit 超时时间单位
     */
    Observable<WebSocketInfo> get(String url, long timeout, TimeUnit timeUnit);

    /**
     * 同步 发送，url的WebSocket已打开的情况下使用，否则会抛出异常
     * @param url WebSocket服务器地址
     * @param msg 消息，看具体和后端协商的格式，一般为json
     */
    Observable<Boolean> send(String url, String msg);

    /**
     * 同步 发送，同上
     * @param url WebSocket服务器地址
     * @param byteString 信息类型为ByteString
     */
    Observable<Boolean> send(String url, ByteString byteString);

    /**
     * 异步 发送消息 不关心WebSocket是否连接，直接发送
     * @param url WebSocket服务器地址
     * @param msg 需要发送的消息
     */
    Observable<Boolean> asyncSend(String url, String msg);

    /**
     * 异步 发送消息 同上，只是消息类型为ByteString，直接发送
     * @param url WebSocket服务器地址
     * @param byteString 需要发送的消息
     */
    Observable<Boolean> asyncSend(String url, ByteString byteString);

    /**
     * 发送心跳包
     * @param url WebSocket服务器地址
     * @param period 发送间隔时间
     * @param unit 间隔时间单位
     * @param heartBeatGenerateCallback 发送心跳包内容
     */
    Observable<Boolean> heartBeat(String url, int period, TimeUnit unit,
                                  HeartBeatGenerateCallback heartBeatGenerateCallback);

    /**
     * 关闭指定Url的连接
     * @param url WebSocket服务器地址
     */
    Observable<Boolean> close(String url);

    /**
     * 马上关闭指定Url的连接
     * @param url WebSocket服务器地址
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
