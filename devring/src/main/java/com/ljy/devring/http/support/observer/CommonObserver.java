package com.ljy.devring.http.support.observer;

import com.ljy.devring.http.support.throwable.HttpThrowable;
import com.ljy.devring.http.support.throwable.ThrowableHandler;

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
    public void onError(Throwable throwable) {
        if (throwable instanceof Exception) {
            onError(ThrowableHandler.handleThrowable(throwable));
        } else {
            onError(new HttpThrowable(HttpThrowable.UNKNOWN,"未知错误",throwable));
        }
    }

    @Override
    public void onNext(T t) {
        onResult(t);
    }

    public abstract void onResult(T result);

    public abstract void onError(HttpThrowable httpThrowable);
}
