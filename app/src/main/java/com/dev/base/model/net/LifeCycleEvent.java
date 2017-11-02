package com.dev.base.model.net;

/**
 * author:  ljy
 * date:    2017/9/15
 * description: 用于Retrofit监控当前View层生命周期的状态值
 */

public enum LifeCycleEvent {
    CREATE,
    START,
    RESUME,
    PAUSE,
    STOP,
    DESTROY
}
