package com.dev.base.model.net;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

/**
 * author:  ljy
 * date:    2017/10/16
 * description:  订阅者封装（用于文件下载请求）
 * 作用：
 * 在onError中进行统一的异常处理，得到更直接详细的异常信息
 * 在onNext中将文件是否成功保存到本地的结果传递过去
 * 可在onSubscribe中进行请求前的操作，注意，onSubscribe是执行在 subscribe() 被调用时的线程，所以如果在onSubscribe里进行UI操作，就要保证subscribe()也是调用在UI线程里。
 */

public abstract class HttpFileObserver implements Observer<ResponseBody> {

    private boolean isFileSaveSuccess;//文件是否成功保存到本地

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
    public void onNext(ResponseBody responseBody) {
        onNext(isFileSaveSuccess);
    }

    public void setFileSaveSuccess(boolean fileSaveSuccess) {
        isFileSaveSuccess = fileSaveSuccess;
    }

    //具体实现下面两个方法，便可从中得到更直接详细的信息
    public abstract void onNext(boolean isFileSaveSuccess);
    public abstract void onError(int errType, String errMessage);
}
