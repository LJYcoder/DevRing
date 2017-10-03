package com.dev.base.model.entity.eventbus;

/**
 * author:  ljy
 * date:    2017/9/28
 * description: EventBus通信的事件类
 */

public class MovieEvent {
    private int count;

    public MovieEvent(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
