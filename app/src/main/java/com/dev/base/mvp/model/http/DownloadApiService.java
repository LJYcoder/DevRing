package com.dev.base.mvp.model.http;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * author:  ljy
 * date:    2018/3/22
 * description: retrofit的请求接口定义，用于下载请求
 * <a>https://www.jianshu.com/p/092452f287db</a>
 */

public interface DownloadApiService {
    //下载大文件时，请加上@Streaming，否则容易出现IO异常
    //目前使用@Streaming进行下载的话，需添加Log拦截器(且LEVEL为BODY)才不会报错，但是网上又说添加Log拦截器后进行下载容易OOM，
    //所以这一块还很懵，具体原因也不清楚，有知道的朋友可以告诉下我）
//    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String downloadUrl);
}
