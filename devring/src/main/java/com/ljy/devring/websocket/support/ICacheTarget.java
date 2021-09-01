package com.ljy.devring.websocket.support;

public interface ICacheTarget<T> {
    /**
     * 重置方法
     *
     * @return 重置后的对象
     */
    T reset();
}