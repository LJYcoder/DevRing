package com.ljy.devring.http.support.observer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ljy.devring.http.support.ExceptionHandler;
import com.ljy.devring.http.support.body.ProgressListener;


import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * author:  ljy
 * date:    2018/3/22
 * description:
 */

public abstract class UploadObserver<T> implements Observer<T>, ProgressListener {

    private String mUploadUrl;
    private String mQualifier;

    /**
     * 如果不需要监听进度，则使用此构造函数
     */
    public UploadObserver() {
    }

    /**
     * 如果是普通地监听某个上传的进度，则使用此构造函数
     * @param uploadUrl 上传的URL地址
     */
    public UploadObserver(@NonNull String uploadUrl) {
        this.mUploadUrl = uploadUrl;
    }

    /**
     * 如果是使用同一个URL但根据请求参数的不同而上传不同资源的情况，则使用此构造函数
     * @param uploadUrl 上传的URL地址
     * @param qualifier 用以区分的字符串
     */
    public UploadObserver(@NonNull String uploadUrl, @Nullable String qualifier) {
        this.mUploadUrl = uploadUrl;
        this.mQualifier = qualifier;
    }

    public String getUploadUrl() {
        return mUploadUrl;
    }

    public String getQualifier() {
        return mQualifier;
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onNext(T t) {
        onResult(t);
    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof Exception) {
            //访问获得对应的Exception
            ExceptionHandler.ResponseThrowable responseThrowable = ExceptionHandler.handleException(e);
            onError(0, responseThrowable.message);
        } else {
            //将Throwable 和 未知错误的status code返回
            ExceptionHandler.ResponseThrowable responseThrowable = new ExceptionHandler.ResponseThrowable(e, ExceptionHandler.ERROR.UNKNOWN);
            onError(0, responseThrowable.message);
        }
    }

    @Override
    public void onProgressError(long id, Exception e) {
        ExceptionHandler.ResponseThrowable responseThrowable = ExceptionHandler.handleException(e);
        onError(id, responseThrowable.message);
    }

    public abstract void onResult(T result);
    //如果progressInfoId为0，则为请求相关的异常，如果不为0，则为上传读写过程的异常
    public abstract void onError(long progressInfoId, String errMessage);

}
