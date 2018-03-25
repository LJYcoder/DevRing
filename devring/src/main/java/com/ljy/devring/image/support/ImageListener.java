package com.ljy.devring.image.support;

/**
 * author:  ljy
 * date:    2018/3/13
 * description: 图片回调，用于获取bitmap或下载图片
 */

public interface ImageListener<T> {

    void onSuccess(T result);

    void onFail(Throwable throwable);
}
