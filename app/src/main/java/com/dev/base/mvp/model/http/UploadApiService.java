package com.dev.base.mvp.model.http;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * author:  ljy
 * date:    2018/3/22
 * description: retrofit的请求接口定义，用于上传请求
 * <a>https://www.jianshu.com/p/092452f287db</a>
 */

public interface UploadApiService {

    @Multipart
    @POST
    Observable<Object> upLoadFile(@Url String url, @Part("fileKey\"; filename=\"a.java") RequestBody requestBody);

    //上传单个文本和单个文件（如果报错，可以尝试把@Query换成@Field或@Part）
    @Multipart
    @POST("请求地址")
    Observable<Object> upLoadTextAndFile(@Query("textKey") String text,
                                             @Part("fileKey\"; filename=\"test.png") RequestBody fileBody);

    //上传多个文本和多个文件（如果报错，可以尝试把@QueryMap换成@FieldMap或@PartMap）
    @Multipart
    @POST("请求地址")
    Observable<Object> upLoadTextsAndFiles(@QueryMap Map<String, String> textMap,
                                               @PartMap Map<String, RequestBody> fileBodyMap);
}
