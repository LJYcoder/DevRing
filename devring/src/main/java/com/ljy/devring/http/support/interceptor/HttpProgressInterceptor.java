package com.ljy.devring.http.support.interceptor;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.SimpleArrayMap;
import android.text.TextUtils;

import com.ljy.devring.http.support.body.ProgressListener;
import com.ljy.devring.http.support.body.ProgressRequestBody;
import com.ljy.devring.http.support.body.ProgressResponseBody;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.http2.Header;

/**
 * author:  ljy
 * date:    2018/3/22
 * description: 基本参考自JessYan的ProgressManager <a>https://github.com/JessYanCoding/ProgressManager</a>
 */
@Singleton
public class HttpProgressInterceptor implements Interceptor {
    public static final int DEFAULT_REFRESH_TIME = 150;
    public static final String IDENTIFICATION_NUMBER = "?DevRing=";
    public static final String IDENTIFICATION_HEADER = "DevRing";
    public static final String LOCATION_HEADER = "Location";
    private final Handler mHandler; //所有监听器在 Handler 中被执行,所以可以保证所有监听器在主线程中被执行
    @Inject
    SimpleArrayMap<String, List<WeakReference<ProgressListener>>> mRequestListeners;//采用弱引用，避免内存泄漏
    @Inject
    SimpleArrayMap<String, List<WeakReference<ProgressListener>>> mResponseListeners;//采用弱引用，避免内存泄漏

    @Inject
    public HttpProgressInterceptor() {
        this.mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        return wrapResponseBody(chain.proceed(wrapRequestBody(chain.request())));
    }

    /**
     * 将 {@link Request} 传入,配置一些本框架需要的参数,常用于自定义 {@link Interceptor}
     *
     * @param request 原始的 {@link Request}
     * @return
     */
    public Request wrapRequestBody(Request request) {
        if (request == null)
            return request;

        String key = request.url().toString();
        request = pruneIdentification(key, request);

        if (request.body() == null)
            return request;
        if (mRequestListeners.containsKey(key)) {
            List<WeakReference<ProgressListener>> listeners = mRequestListeners.get(key);
            return request.newBuilder()
                    .method(request.method(), new ProgressRequestBody(mHandler, request.body(), listeners, DEFAULT_REFRESH_TIME))
                    .build();
        }
        return request;
    }

    /**
     * 如果 {@code url} 中含有加入的标识符,则将加入标识符
     * 从 {code url} 中删除掉,继续使用 {@code originUrl} 进行请求
     *
     * @param url     {@code url} 地址
     * @param request 原始 {@link Request}
     * @return 返回可能被修改过的 {@link Request}
     */
    private Request pruneIdentification(String url, Request request) {
        boolean needPrune = url.contains(IDENTIFICATION_NUMBER);
        if (!needPrune)
            return request;
        return request.newBuilder()
                .url(url.substring(0, url.indexOf(IDENTIFICATION_NUMBER))) //删除掉标识符
                .header(IDENTIFICATION_HEADER, url) //将有标识符的 url 加入 header中, 便于wrapResponseBody(Response) 做处理
                .build();
    }

    /**
     * 将 {@link Response} 传入,配置一些本框架需要的参数,常用于自定义 {@link Interceptor}
     *
     * @param response 原始的 {@link Response}
     * @return
     */
    public Response wrapResponseBody(Response response) {
        if (response == null)
            return response;

        String key = response.request().url().toString();
        if (!TextUtils.isEmpty(response.request().header(IDENTIFICATION_HEADER))) { //从 header 中拿出有标识符的 url
            key = response.request().header(IDENTIFICATION_HEADER);
        }

        if (response.isRedirect()) {
            resolveRedirect(mRequestListeners, response, key);
            String location = resolveRedirect(mResponseListeners, response, key);
            response = modifyLocation(response, location);
            return response;
        }

        if (response.body() == null)
            return response;

        key = removePort(key);

        if (mResponseListeners.containsKey(key)) {
            List<WeakReference<ProgressListener>> listeners = mResponseListeners.get(key);
            return response.newBuilder()
                    .body(new ProgressResponseBody(mHandler, response.body(), listeners, DEFAULT_REFRESH_TIME))
                    .build();
        }
        return response;
    }

    /**
     * 2018/3/23
     * add by ljy
     * 发现有些重定向的请求，它LOCATION_HEADER里的地址与重定向后的请求地址端口号不一致（location和key值其中一个没有端口号）
     * 所以同意把端口号移除，避免这种情况下重定向请求无法监听进度
     * @param target 要处理的字符串
     * @return 该地址如果有端口号，则移除，如果没有，则返回原样
     */
    private String removePort(String target) {
        Pattern pattern = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+)\\:(\\d+)");
        Matcher matcher = pattern.matcher(target);
        if (matcher.find()) {
            target = target.replace(":" + matcher.group(2), "");
        }
        return target;
    }

    /**
     * 查看 location 是否被加入了标识符, 如果是, 则放入 {@link Header} 中重新定义 {@link Response}
     *
     * @param response 原始的 {@link Response}
     * @param location {@code location} 重定向地址
     * @return 返回可能被修改过的 {@link Response}
     */
    private Response modifyLocation(Response response, String location) {
        if (!TextUtils.isEmpty(location) && location.contains(IDENTIFICATION_NUMBER)) { //将被加入标识符的新的 location 放入 header 中
            response = response.newBuilder()
                    .header(LOCATION_HEADER, location)
                    .build();
        }
        return response;
    }

    /**
     * 解决请求地址重定向后的兼容问题
     *
     * @param map
     * @param response 原始的 {@link Response}
     * @param url      {@code url} 地址
     */
    String location;
    private String resolveRedirect(SimpleArrayMap<String, List<WeakReference<ProgressListener>>> map, Response response, String url) {
        location = null;
        List<WeakReference<ProgressListener>> progressListeners = map.get(url); //查看此重定向 url ,是否已经注册过监听器
        if (progressListeners != null && progressListeners.size() > 0) {
            location = response.header(LOCATION_HEADER);// 重定向地址
            location = removePort(location);

            if (!TextUtils.isEmpty(location)) {
                if (url.contains(IDENTIFICATION_NUMBER) && !location.contains(IDENTIFICATION_NUMBER)) { //如果 url 有标识符,那也将标识符加入用于重定向的 location
                    location += url.substring(url.indexOf(IDENTIFICATION_NUMBER), url.length());
                }
                if (!map.containsKey(location)) {
                    map.put(location, progressListeners); //将需要重定向地址的监听器,提供给重定向地址,保证重定向后也可以监听进度
                } else {
                    List<WeakReference<ProgressListener>> locationListener = map.get(location);
                    for (WeakReference<ProgressListener> listener : progressListeners) {
                        if (!locationListener.contains(listener)) {
                            locationListener.add(listener);
                        }
                    }
                }
            }
        }
        return location;
    }

    public SimpleArrayMap<String, List<WeakReference<ProgressListener>>> getRequestListeners() {
        return mRequestListeners;
    }

    public SimpleArrayMap<String, List<WeakReference<ProgressListener>>> getResponseListeners() {
        return mResponseListeners;
    }
}
