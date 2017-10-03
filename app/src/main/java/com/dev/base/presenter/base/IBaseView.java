package com.dev.base.presenter.base;

import com.dev.base.model.net.LifeCycleEvent;

import rx.subjects.PublishSubject;

/**
 * author:  ljy
 * date:    2017/9/15
 * description:
 */

public interface IBaseView {
    PublishSubject<LifeCycleEvent> getLifeSubject();
}
