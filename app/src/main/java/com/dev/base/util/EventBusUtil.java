package com.dev.base.util;

import com.dev.base.MyEventBusIndex;
import com.dev.base.model.entity.eventbus.MovieEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by ljy on 2017/10/20.
 * EventBus工具类
 */

public class EventBusUtil {

    //开启Index加速
    public static void openIndex() {
        EventBus.builder().addIndex(new MyEventBusIndex()).installDefaultEventBus();
    }

    //订阅事件
    public static void register(Object subscriber) {
        if(!EventBus.getDefault().isRegistered(subscriber)){
            EventBus.getDefault().register(subscriber);
        }
    }

    //取消订阅
    public static void unregister(Object subscriber) {
        if (EventBus.getDefault().isRegistered(subscriber)) {
            EventBus.getDefault().unregister(subscriber);
        }
    }

    //终止事件继续传递
    public static void cancelDelivery(Object event) {
        EventBus.getDefault().cancelEventDelivery(event);
    }

    //获取保存起来的粘性事件
    public static <T> T getStickyEvent(Class<T> classType){
        return EventBus.getDefault().getStickyEvent(classType);
    }

    //删除保存中的粘性事件
    public static void removeStickyEvent(Object event) {
        EventBus.getDefault().removeStickyEvent(event);
    }

    //发送事件
    public static void postEvent(Object event){
        EventBus.getDefault().post(event);
    }

    //发送粘性事件
    public static void postStickyEvent(Object event) {
        EventBus.getDefault().postSticky(event);
    }

    //发送电影事件
    public static void postMovieEvent(MovieEvent movieEvent) {
        postEvent(movieEvent);
    }
}
