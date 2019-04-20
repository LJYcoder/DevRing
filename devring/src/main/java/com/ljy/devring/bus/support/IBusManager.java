package com.ljy.devring.bus.support;

/**
 * author:  ljy
 * date:    2018/3/11
 * description: 事件总线接口
 */

public interface IBusManager {

    void register(Object subscriber);//注册。
    //注意：我把注册事件放在了onStart中进行（为了兼容EventBus的粘性事件），
    //所以在注册前请先判断是否已注册过，没注册过才进行注册操作，
    //避免页面切换时，多次触发onStart进行重复的注册，最后导致发送一次事件收到多次回调。

    void unregister(Object subscriber);//注销

    void postEvent(Object event);//发送事件

    void postStickyEvent(Object event);//发送粘性事件
}
