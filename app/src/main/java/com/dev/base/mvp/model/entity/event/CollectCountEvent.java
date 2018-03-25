package com.dev.base.mvp.model.entity.event;

/**
 * author:  ljy
 * date:    2017/9/28
 * description: EventBus通信的事件类
 */

public class CollectCountEvent {
    private int count;

    public CollectCountEvent(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
