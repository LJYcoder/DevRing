package com.ljy.devring.websocket.support;
/**
 * @author: XieYos
 * @date: 2021年9月2日
 * @description: 需要缓存的模型需要实现的接口
 */
public interface ICacheTarget<T> {
    /**
     * 重置方法
     *
     * @return 重置后的对象
     */
    T reset();
}