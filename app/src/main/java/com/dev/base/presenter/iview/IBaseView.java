package com.dev.base.presenter.iview;

import com.dev.base.model.net.LifeCycleEvent;

import io.reactivex.subjects.PublishSubject;


/**
 * author:  ljy
 * date:    2017/9/15
 * description: 里面定义一些View层总是需要实现的抽象方法
 */

public interface IBaseView {
    /**
     * 每次网络请求，P层都需要V层提供其生命周期发射者（Activity基类中提到的PublishSubject）。
     * 如果每次都将该发射者通作为P层方法的参数传入，会显得比较繁琐。
     * 那么我们可以在IView基类中加入一个抽象方法getLifeSubject()，然后在Activity/Fragment基类中加入该方法的实现（前面Activity基类中已实现），
     * 这样，P层就可以通过IVew.getLifeSubject()来直接获取V层的生命周期发射者了
     */
    PublishSubject<LifeCycleEvent> getLifeSubject();
}
