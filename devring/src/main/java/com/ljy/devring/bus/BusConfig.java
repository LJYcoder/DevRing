package com.ljy.devring.bus;

import org.greenrobot.eventbus.meta.SubscriberInfoIndex;

/**
 * author:  ljy
 * date:    2018/3/10
 * description: 事件总线配置
 */

public class BusConfig {

    private boolean mIsUseIndex;
    private SubscriberInfoIndex mIndex;

    //设置用于加速的Index
    public BusConfig setIndex(SubscriberInfoIndex index) {
        mIndex = index;
        return this;
    }

    //设置是否使用index进行加速
    public BusConfig setIsUseIndex(boolean isUseIndex) {
        mIsUseIndex = isUseIndex;
        return this;
    }

    protected SubscriberInfoIndex getIndex() {
        return mIndex;
    }

    protected boolean isUseIndex() {
        return mIsUseIndex;
    }
}
