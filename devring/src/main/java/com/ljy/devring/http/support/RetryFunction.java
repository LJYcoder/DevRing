package com.ljy.devring.http.support;

import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * author:  ljy
 * date:    2018/3/20
 * description: 请求失败重试机制
 */

public class RetryFunction implements Function<Observable<Throwable>, ObservableSource<?>> {

    private int retryDelaySeconds;//延迟重试的时间
    private int retryCountMax;//最大重试次数
    private int retryCount;//记录当前重试次数

    public RetryFunction(int retryDelaySeconds, int retryCountMax) {
        this.retryDelaySeconds = retryDelaySeconds;
        this.retryCountMax = retryCountMax;
    }

    @Override
    public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
        //方案一：使用zip控制重试次数，重试3次后不再重试（会隐式回调onComplete结束请求，但我需要的是回调onError，所以没采用方案一）
//            return Observable.zip(throwableObservable,Observable.range(1, retryCountMax),new BiFunction<Throwable, Integer, Throwable>() {
//                @Override
//                public Throwable apply(Throwable throwable, Integer integer) throws Exception {
//                    RingLog.e("ljy",""+integer);
//                    return throwable;
//                }
//            }).flatMap(new Function<Throwable, ObservableSource<?>>() {
//                @Override
//                public ObservableSource<?> apply(Throwable throwable) throws Exception {
//                    if (throwable instanceof UnknownHostException) {
//                        return Observable.error(throwable);
//                    }
//                    return Observable.timer(retryDelaySeconds, TimeUnit.SECONDS);
//                }
//            });


        //方案二：使用全局变量来控制重试次数，重试3次后不再重试，通过代码显式回调onError结束请求
        return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Throwable throwable) throws Exception {
                //如果失败的原因是UnknownHostException（DNS解析失败，当前无网络），则没必要重试，直接回调error结束请求即可
                if (throwable instanceof UnknownHostException) {
                    return Observable.error(throwable);
                }

                //没超过最大重试次数的话则进行重试
                if (++retryCount <= retryCountMax) {
                    //延迟retryDelaySeconds后开始重试
                    return Observable.timer(retryDelaySeconds, TimeUnit.SECONDS);
                }

                return Observable.error(throwable);
            }
        });
    }
}