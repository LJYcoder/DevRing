package com.ljy.devring.bus.support;

/**
 * author:  ljy
 * date:    2018/3/11
 * description: 事件总线接口
 */

public interface IBusManager {

    void register(Object subscriber);//订阅

    void unregister(Object subscriber);//解除订阅

    void postEvent(Object event);//发送事件

    void postStickyEvent(Object event);//发送粘性事件
}
