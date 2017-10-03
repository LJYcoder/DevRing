package com.dev.base.model.net;

import com.dev.base.app.constant.UrlConstants;
import com.dev.base.model.entity.res.HttpResult;
import com.dev.base.model.entity.res.MovieRes;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * author:  ljy
 * date:    2017/9/14
 * description: 
 */

public interface ApiService {
    @GET(UrlConstants.GET_PLAYING_MOVIE)
    Observable<HttpResult<List<MovieRes>>> getPlayingMovie(@Query("count") int count);

    @GET(UrlConstants.GET_COMMING_MOVIE)
    Observable<HttpResult<List<MovieRes>>> getCommingMovie(@Query("count") int count);




    @GET("")
    Observable<HttpResult> getOnlineCourse();

    @Headers({"Accept: application/vnd.yourapi.v1.full+json", "User-Agent: Your-App-Name"})
    @GET("")
    Observable<HttpResult> getCourseGet(@Query("token") int token, @Query("name") String name);

    @Headers("Cache-Control: max-age=640000")
    @GET("")
    Observable<HttpResult> getCourseGet(@QueryMap Map<String, String> map);

    @FormUrlEncoded
    @POST("")
    Observable<HttpResult> getCoursePost(@Header("token") String token, @Field("id") int id);

    @FormUrlEncoded
    @POST("")
    Observable<HttpResult> getCoursePost(@FieldMap Map<String, String> map);

    @Multipart
    @POST("")
    Observable<HttpResult> upLoadTextAndImage(@Part("text") String text, @PartMap Map<String, RequestBody> images);
}
