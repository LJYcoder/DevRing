package com.dev.base.model.net;

import com.dev.base.app.constant.UrlConstants;
import com.dev.base.model.entity.res.HttpResult;
import com.dev.base.model.entity.res.MovieRes;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;

/**
 * author:  ljy
 * date:    2017/9/14
 * description: retrofit的请求接口定义
 */

public interface ApiService {

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

    //上传单个文本和单个文件（如果报错，可以尝试把@Query换成@Field或@Part）
    @Multipart
    @POST("请求地址")
    Observable<HttpResult> upLoadTextAndFile(@Query("textKey") String text,
                                             @Part("fileKey\"; filename=\"test.png") RequestBody fileBody);

    //上传多个文本和多个文件（如果报错，可以尝试把@QueryMap换成@FieldMap或@PartMap）
    @Multipart
    @POST("请求地址")
    Observable<HttpResult> upLoadTextsAndFiles(@QueryMap Map<String, String> textMap,
                                              @PartMap Map<String, RequestBody> fileBodyMap);

    //下载大文件时，请加上@Streaming，否则容易出现IO异常
    @Streaming
    @GET("请求地址")
    Observable<ResponseBody> downloadFile();
}
