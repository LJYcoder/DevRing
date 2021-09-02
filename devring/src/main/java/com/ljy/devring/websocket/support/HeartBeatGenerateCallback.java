package com.ljy.devring.websocket.support;

/**
 * @author: XieYos
 * @date: 2021年9月2日
 * @description: 生成心跳信息
 */
public interface HeartBeatGenerateCallback {
    /**
     * 当需要生成心跳信息时回调
     *
     * @param timestamp 当前时间戳
     * @return 要发送的心跳信息
     */
    String onGenerateHeartBeatMsg(long timestamp);
}