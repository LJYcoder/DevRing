package com.dev.base.model.net;

import com.dev.base.model.entity.res.HttpResult;

import rx.Subscriber;

/**
 * author:  ljy
 * date:    2017/9/27
 * description:  (用于豆瓣电影接口) 订阅者封装，主要是处理异常以及回调结果后返回更直接有用的信息，
 * 另外可以在onStart方法中做一些发起请求前要做的操作（非UI界面操作）
 * 也可以在onNext方法中做一些回调后需统一处理的事情
 */

public abstract class HttpSubscriber<T> extends Subscriber<HttpResult<T>>{

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof Exception) {
            //访问获得对应的Exception
            ExceptionHandler.ResponeThrowable responeThrowable = ExceptionHandler.handleException(e);
            onError(responeThrowable.code, responeThrowable.message);
        } else {
            //将Throwable 和 未知错误的status code返回
            ExceptionHandler.ResponeThrowable responeThrowable = new ExceptionHandler.ResponeThrowable(e, ExceptionHandler.ERROR.UNKNOWN);
            onError(responeThrowable.code, responeThrowable.message);
        }
    }

    @Override
    public void onNext(HttpResult<T> httpResult) {
        onNext(httpResult.getTitle(), httpResult.getSubjects());
    }

    //由子类实现
    public abstract void onNext(String title, T t);

    public abstract void onError(int errType, String errMessage);
}
