package com.dev.base.mvp.model.http;

import com.dev.base.app.constant.UrlConstants;
import com.dev.base.mvp.model.entity.res.HttpResult;
import com.dev.base.mvp.model.entity.res.MovieRes;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * author:  ljy
 * date:    2017/9/14
 * description: retrofit的请求接口定义，用于豆瓣电影请求
 * <a>https://www.jianshu.com/p/092452f287db</a>
 */

public interface MovieApiService {

    @GET(UrlConstants.GET_PLAYING_MOVIE)
    Observable<HttpResult<List<MovieRes>>> getPlayingMovie(@Query("start") int start, @Query("count") int count);

    @GET(UrlConstants.GET_COMMING_MOVIE)
    Observable<HttpResult<List<MovieRes>>> getCommingMovie(@Query("start") int start, @Query("count") int count);

    //请求参数一次性传入（通过Map来存放key-value）
    @GET("")
    Observable<HttpResult> getPlayingMovie(@QueryMap Map<String, String> map);

    //请求参数逐个传入
    @FormUrlEncoded
    @POST("请求地址")
    Observable<HttpResult> getInfo(@Field("token") String token, @Field("id") int id);

    //请求参数一次性传入（通过Map来存放参数名和参数值）
    @FormUrlEncoded
    @POST("请求地址")
    Observable<HttpResult> getInfo(@FieldMap Map<String, String> map);


}
