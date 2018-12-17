package com.api.demo.bus.rxbus;

import com.api.demo.bus.rxbus.support.RxBus;
import com.ljy.devring.bus.support.IBusManager;

/**
 * author:  ljy
 * date:    2018/3/24
 * description:
 * 目的是来演示如何使用其他事件总线来替换DevRing中默认提供的EventBus。
 * 这里选了胡伟的RxBus来替换
 *
 * 替换步骤：
 * 1.添加相关依赖（由于是使用RxBus，Devring库中已有Rx相关的依赖，所以不用添加）
 * 2.实现IBusManager接口
 * 3.Application中通过DevRing.configureBus(new RxBusManager())方法传入
 *
 * 替换后一样是通过DevRing.busManager()来使用相关功能。
 *
 * 可在本类中添加IIBusManager接口以外的方法
 * ，然后通过DevRing.<RxBusManager>busManager()来调用。
 */

public class RxBusManager implements IBusManager {

    private RxBus mRxBus;

    public RxBusManager() {
        mRxBus = new RxBus();
    }

    @Override
    public void register(Object subscriber) {
        mRxBus.register(subscriber);
    }

    @Override
    public void unregister(Object subscriber) {
        mRxBus.unregister(subscriber);
    }

    @Override
    public void postEvent(Object event) {
        mRxBus.post(event);
    }

    @Override
    public void postStickyEvent(Object event) {
        mRxBus.postSticky(event);
    }
}
