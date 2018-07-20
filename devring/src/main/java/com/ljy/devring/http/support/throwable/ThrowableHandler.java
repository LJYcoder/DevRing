package com.ljy.devring.http.support.throwable;

import android.net.ParseException;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.HttpException;

/**
 * author:  ljy
 * date:    2017/9/14
 * description: 网络请求异常处理
 */
public class ThrowableHandler {

    /**
     * 处理异常从而得到异常类型以及异常提示
     */
    public static HttpThrowable handleThrowable(Throwable throwable) {
        if (throwable instanceof HttpException) {
            return new HttpThrowable(HttpThrowable.HTTP_ERROR, "网络(协议)异常", throwable);
        } else if (throwable instanceof JsonParseException || throwable instanceof JSONException || throwable instanceof ParseException) {
            return new HttpThrowable(HttpThrowable.PARSE_ERROR, "数据解析异常", throwable);
        } else if (throwable instanceof UnknownHostException) {
            return new HttpThrowable(HttpThrowable.NO_NET_ERROR, "网络连接失败，请稍后重试", throwable);
        } else if (throwable instanceof SocketTimeoutException) {
            return new HttpThrowable(HttpThrowable.TIME_OUT_ERROR, "连接超时", throwable);
        } else if (throwable instanceof ConnectException) {
            return new HttpThrowable(HttpThrowable.CONNECT_ERROR, "连接异常", throwable);
        } else if (throwable instanceof javax.net.ssl.SSLHandshakeException) {
            return new HttpThrowable(HttpThrowable.SSL_ERROR, "证书验证失败", throwable);
        } else {
            return new HttpThrowable(HttpThrowable.UNKNOWN, throwable.getMessage(), throwable);
        }
    }

    /**
     * 从HttpException类型的throwalbe中得到其响应实体并转换为指定格式
     *
     * @param throwable HttpException类型的throwalbe
     * @param clazz     响应实体对应的格式
     */
    public static <T> T fromJson(Throwable throwable, Class<T> clazz) {
        HttpException httpException = (HttpException) throwable;
        Gson gson = new Gson();
        T t = null;
        try {
            t = gson.fromJson(httpException.response().errorBody().string(), clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return t;
    }

}