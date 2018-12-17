package com.api.demo.http;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * author:  ljy
 * date:    2018/11/27
 * description: retrofit的请求接口定义
 * <a>https://www.jianshu.com/p/092452f287db</a>
 */

public interface ApiService {

    /**
     * 获取豆瓣正在上映电影的接口
     * @param start 起始页码
     * @param count 获取的电影数量
     * @return
     */
    @GET("v2/movie/in_theaters")
    Observable<Result> getPlayingMovie(@Query("start") int start, @Query("count") int count);

    /**
     * 模拟上传文件的接口
     * @param uploadUrl 上传地址
     * @param requestBody 上传实体
     * @return
     */
    @Multipart
    @POST
    Observable<Object> upLoadFile(@Url String uploadUrl, @Part("fileKey\"; filename=\"upload.java") RequestBody requestBody);

    //下载大文件时，请加上@Streaming，否则容易出现IO异常
    //目前使用@Streaming进行下载的话，需添加Log拦截器(且LEVEL为BODY)才不会报错，但是网上又说添加Log拦截器后进行下载容易OOM，
    //所以这一块还很懵，具体原因也不清楚，有知道的朋友可以告诉下我）
//    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String downloadUrl);
}
