package com.ljy.devring.http.support.observer;

import com.ljy.devring.http.support.ExceptionHandler;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


/**
 * author:  ljy
 * date:    2017/9/27
 * description:  普通的api请求回调
 */

public abstract class CommonObserver<T> implements Observer<T> {

    @Override
    public void onSubscribe(Disposable d) {
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof Exception) {
            //访问获得对应的Exception
            ExceptionHandler.ResponseThrowable responseThrowable = ExceptionHandler.handleException(e);
            onError(responseThrowable.code, responseThrowable.message);
        } else {
            //将Throwable 和 未知错误的status code返回
            ExceptionHandler.ResponseThrowable responseThrowable = new ExceptionHandler.ResponseThrowable(e, ExceptionHandler.ERROR.UNKNOWN);
            onError(responseThrowable.code, responseThrowable.message);
        }
    }

    @Override
    public void onNext(T t) {
        onResult(t);
    }

    public abstract void onResult(T result);

    public abstract void onError(int errType, String errMessage);
}
