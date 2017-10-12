package com.dev.base.model.net;

import com.dev.base.model.entity.res.HttpResult;

import rx.Subscriber;

/**
 * author:  ljy
 * date:    2017/9/27
 * description:  订阅者封装，作用：
 * 在onError中进行统一的异常处理，得到更直接详细的异常信息
 * 在onNext中进行统一操作，如请求回来后，先判断token是否失效，如果失效则直接跳转登录页面
 * 在onNext中对返回的结果进行处理，得到更直接的数据信息
 * 可在onStart中进行请求前的操作
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
        //做一些回调后需统一处理的事情
        //如请求回来后，先判断token是否失效
        //如果失效则直接跳转登录页面
        //...

        //如果没失效，则正常回调
        onNext(httpResult.getTitle(), httpResult.getSubjects());
    }

    //具体实现下面两个方法，便可从中得到更直接详细的信息
    public abstract void onNext(String title, T t);
    public abstract void onError(int errType, String errMessage);
}
