package com.ljy.devring.http;

import android.text.TextUtils;

import com.ljy.devring.cache.support.MemoryCache;
import com.ljy.devring.http.support.RetryFunction;
import com.ljy.devring.http.support.body.ProgressListener;
import com.ljy.devring.http.support.interceptor.HttpProgressInterceptor;
import com.ljy.devring.http.support.observer.DownloadObserver;
import com.ljy.devring.http.support.observer.UploadObserver;
import com.ljy.devring.util.FileUtil;
import com.ljy.devring.util.RxLifecycleUtil;
import com.trello.rxlifecycle2.LifecycleTransformer;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

/**
 * author:  ljy
 * date:    2018/3/20
 * description: 网络请求管理者
 * <p>
 * <a>https://www.jianshu.com/p/092452f287db</a>
 */
@Singleton
public class HttpManager {
    @Inject
    HttpProgressInterceptor mProgressInterceptor;
    @Inject
    Provider<Retrofit> mProviderRetrofit;
    @Inject
    MemoryCache mMemoryCache;
    @Inject
    HttpConfig mHttpConfig;

    Retrofit mRetrofit;
    List<String> mListCacheKey;
    PublishSubject<String> mTagEmitter;//用于发射Tag来终止与该Tag绑定的网络请求

    @Inject
    public HttpManager() {
        mListCacheKey = new ArrayList<>();
        mTagEmitter = PublishSubject.create();
    }

    /**
     * 获取指定的网络请求Api接口
     *
     * @param serviceClass ApiService的类型
     * @return 相应的ApiService
     */
    public <T> T getService(Class<T> serviceClass) {
        if (mRetrofit == null) {
            mRetrofit = mProviderRetrofit.get();
        }

        String cacheKey = serviceClass.getCanonicalName();
        T service = (T) mMemoryCache.get(cacheKey);
        if (service == null) {
            service = mRetrofit.create(serviceClass);
            mMemoryCache.put(cacheKey, service);
            if (!mListCacheKey.contains(cacheKey)) {
                mListCacheKey.add(cacheKey);
            }
        }
        return service;
    }

    /**
     * 普通的网络api请求
     *
     * @param observable  请求
     * @param observer    请求回调
     * @param transformer 生命周期控制，可通过RxLifecycleUtil获取。如果为null，则不进行生命周期控制，
     */
    public void commonRequest(Observable observable, Observer observer, LifecycleTransformer transformer) {
        handleRetry(handleLife(handleThread(observable), transformer), mHttpConfig.isUseRetryWhenError(), mHttpConfig.getTimeRetryDelay(), mHttpConfig.getMaxRetryCount())
                .subscribe(observer);
    }

    /**
     * 普通的网络api请求
     *
     * @param observable 请求
     * @param observer   请求回调
     * @param lifeTag    用于终止请求的tag，当要终止与该tag绑定的请求时，可调用 stopRequestByTag(tag) 方法
     */
    public void commonRequest(Observable observable, Observer observer, String lifeTag) {
        commonRequest(observable, observer, lifeTag != null ? RxLifecycleUtil.RxBindUntilEvent(mTagEmitter, lifeTag) : null);
    }

    /**
     * 涉及上传的网络请求
     *
     * @param observable     请求
     * @param uploadObserver 请求回调（包含了上传进度的回调）
     *                       如果不需要监听进度，则使用空的构造函数
     *                       如果是普通地监听某个上传的进度，则使用一个参数的构造函数，并传入上传的URL地址
     *                       如果是使用同一个URL但根据请求参数的不同而上传不同资源的情况，则使用两个参数的构造函数，第一个参数传入上传的URL地址，第二参数传入自定义的字符串加以区分。
     * @param transformer    生命周期控制，如果为null，则不进行生命周期控制
     */
    public void uploadRequest(Observable observable, UploadObserver uploadObserver, LifecycleTransformer transformer) {
        if (!TextUtils.isEmpty(uploadObserver.getUploadUrl())) {
            if (TextUtils.isEmpty(uploadObserver.getQualifier())) {
                addRequestListener(uploadObserver.getUploadUrl(), uploadObserver);
            } else {
                addDiffRequestListenerOnSameUrl(uploadObserver.getUploadUrl(), uploadObserver.getQualifier(), uploadObserver);
            }
        }
        handleRetry(handleLife(handleThread(observable), transformer), mHttpConfig.isUseRetryWhenError(), mHttpConfig.getTimeRetryDelay(), mHttpConfig.getMaxRetryCount())
                .subscribe(uploadObserver);
    }

    /**
     * 涉及上传的网络请求
     *
     * @param observable     请求
     * @param uploadObserver 请求回调（包含了上传进度的回调）
     *                       如果不需要监听进度，则使用空的构造函数
     *                       如果是普通地监听某个上传的进度，则使用一个参数的构造函数，并传入上传的URL地址
     *                       如果是使用同一个URL但根据请求参数的不同而上传不同资源的情况，则使用两个参数的构造函数，第一个参数传入上传的URL地址，第二参数传入自定义的字符串加以区分。
     * @param lifeTag        用于终止请求的tag，当要终止与该tag绑定的请求时，可调用 stopRequestByTag(tag) 方法
     */
    public void uploadRequest(Observable observable, UploadObserver uploadObserver, String lifeTag) {
        uploadRequest(observable, uploadObserver, lifeTag != null ? RxLifecycleUtil.RxBindUntilEvent(mTagEmitter, lifeTag) : null);
    }

    /**
     * 涉及下载的网络请求
     *
     * @param fileSave         下载后的内容将保存至该file
     * @param observable       请求
     * @param downloadObserver 请求回调（包含了下载进度的回调）
     *                         如果不需要监听进度，则使用空的构造函数
     *                         如果是普通地监听某个下载的进度，则使用一个参数的构造函数，并传入下载的URL地址
     *                         如果是使用同一个URL但根据请求参数的不同而下载不同资源的情况，则使用两个参数的构造函数，第一个参数传入下载的URL地址，第二参数传入自定义的字符串加以区分。
     * @param transformer      生命周期控制，如果为null，则不进行生命周期控制
     */
    public void downloadRequest(File fileSave, Observable observable, DownloadObserver downloadObserver, LifecycleTransformer transformer) {
        if (!TextUtils.isEmpty(downloadObserver.getDownloadUrl())) {
            if (TextUtils.isEmpty(downloadObserver.getQualifier())) {
                addResponseListener(downloadObserver.getDownloadUrl(), downloadObserver);
            } else {
                addDiffResponseListenerOnSameUrl(downloadObserver.getDownloadUrl(), downloadObserver.getQualifier(), downloadObserver);
            }
        }
        handleRetry(handleLife(handleThreadForDownload(fileSave, observable, downloadObserver), transformer), mHttpConfig.isUseRetryWhenError(), mHttpConfig.getTimeRetryDelay(),
                mHttpConfig.getMaxRetryCount()).subscribe(downloadObserver);
    }

    /**
     * 涉及下载的网络请求
     *
     * @param fileSave         下载后的内容将保存至该file
     * @param observable       请求
     * @param downloadObserver 请求回调（包含了下载进度的回调）
     *                         如果不需要监听进度，则使用空的构造函数
     *                         如果是普通地监听某个下载的进度，则使用一个参数的构造函数，并传入下载的URL地址
     *                         如果是使用同一个URL但根据请求参数的不同而下载不同资源的情况，则使用两个参数的构造函数，第一个参数传入下载的URL地址，第二参数传入自定义的字符串加以区分。
     * @param lifeTag          用于终止请求的tag，当要终止与该tag绑定的请求时，可调用 stopRequestByTag(tag) 方法
     */
    public void downloadRequest(File fileSave, Observable observable, DownloadObserver downloadObserver, String lifeTag) {
        downloadRequest(fileSave, observable, downloadObserver, lifeTag != null ? RxLifecycleUtil.RxBindUntilEvent(mTagEmitter, lifeTag) : null);
    }

    /**
     * 发射Tag来终止与该Tag绑定的网络请求
     * 终止前请确保已通过相关的 commonRequest()/uploadRequest()/downloadRequest() 方法将请求与tag绑定
     */
    public void stopRequestByTag(String tag) {
        mTagEmitter.onNext(tag);
    }

    //处理网络请求的生命周期控制
    private Observable handleLife(Observable observable, LifecycleTransformer transformer) {
        if (transformer != null) {
            return observable.compose(transformer);
        }
        return observable;
    }

    //处理线程调度
    private Observable handleThread(Observable observable) {
        return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    //处理线程调度，下载文件的话则在这将内容保存至本地file中
    private Observable handleThreadForDownload(final File fileSave, Observable observable, final DownloadObserver downloadObserver) {
        return observable.observeOn(Schedulers.io()) //指定doOnNext的操作在io后台线程进行
                .doOnNext(new Consumer<ResponseBody>() {
                    //doOnNext里的方法执行完毕，subscriber里的onNext、onError等方法才会执行。
                    @Override
                    public void accept(ResponseBody body) throws Exception {
                        //下载文件，保存到本地
                        boolean isSuccess = FileUtil.saveFile(body.byteStream(), new FileOutputStream(fileSave));
                        //将“文件是否成功保存到本地”的结果传递给订阅者
                        downloadObserver.setResult(isSuccess, fileSave.getAbsolutePath());
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    //处理失败重试机制
    private Observable handleRetry(Observable observable, boolean isUseRetry, int timeRetryDelay, int maxRetryCount) {
        if (isUseRetry) {
            return observable.retryWhen(new RetryFunction(timeRetryDelay >= 0 ? timeRetryDelay : 3, maxRetryCount > 0 ? maxRetryCount : 3));
        } else {
            return observable;
        }
    }

    //添加上传进度监听
    public void addRequestListener(String url, ProgressListener listener) {
        List<WeakReference<ProgressListener>> progressListeners;
        synchronized (HttpManager.class) {
            progressListeners = mProgressInterceptor.getRequestListeners().get(url);
            if (progressListeners == null) {
                progressListeners = new LinkedList<>();
                mProgressInterceptor.getRequestListeners().put(url, progressListeners);
            }
        }
        if (!isListenerExist(progressListeners, listener)) {
            progressListeners.add(new WeakReference<>(listener));
        }
    }

    //添加上传进度监听，应对同一个URL但根据请求参数的不同而上传不同资源的情况
    public String addDiffRequestListenerOnSameUrl(String originUrl, String qualifier, ProgressListener listener) {
        String newUrl = originUrl + HttpProgressInterceptor.IDENTIFICATION_NUMBER + qualifier;
        addRequestListener(newUrl, listener);
        return newUrl;
    }

    //添加下载进度监听
    public void addResponseListener(String url, ProgressListener listener) {
        List<WeakReference<ProgressListener>> progressListeners;
        synchronized (HttpManager.class) {
            progressListeners = mProgressInterceptor.getResponseListeners().get(url);
            if (progressListeners == null) {
                progressListeners = new LinkedList<>();
                mProgressInterceptor.getResponseListeners().put(url, progressListeners);
            }
        }
        if (!isListenerExist(progressListeners, listener)) {
            progressListeners.add(new WeakReference<>(listener));
        }
    }

    //添加下载进度监听，应对同一个URL但根据请求参数的不同而下载不同资源的情况
    public String addDiffResponseListenerOnSameUrl(String originUrl, String qualifier, ProgressListener listener) {
        String newUrl = originUrl + HttpProgressInterceptor.IDENTIFICATION_NUMBER + qualifier;
        addResponseListener(newUrl, listener);
        return newUrl;
    }

    //判断某个监听是否已经存在了，如果已经存在，则不重复添加
    private boolean isListenerExist(List<WeakReference<ProgressListener>> progressListeners, ProgressListener listener) {
        //因为想在list遍历时移除为null的元素，所以用的迭代器而不是for循环。
        Iterator<WeakReference<ProgressListener>> iterator = progressListeners.iterator();
        while (iterator.hasNext()) {
            WeakReference<ProgressListener> progressListener = iterator.next();
            if (progressListener.get() == null) {
                iterator.remove();
            } else if (progressListener.get().equals(listener)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 用于生成 上传多个文件用的Map<String, RequestBody>
     *
     * @param map       保存了filekey和file的map
     * @param mediaType 上传文件的MediaType
     * @return 上传多个文件用的Map<String, RequestBody>
     */
    public Map<String, RequestBody> getRequestBodyMap(Map<String, File> map, MediaType mediaType) {
        final Map<String, RequestBody> bodyMap = new HashMap<>();
        for (Map.Entry<String, File> entry : map.entrySet()) {
            bodyMap.put(entry.getKey() + "\"; filename=\"" + entry.getValue().getName(), RequestBody.create(mediaType, entry.getValue()));
        }
        return bodyMap;
    }

    public void refreshInstance() {
        mRetrofit = null;
        mRetrofit = mProviderRetrofit.get();
        for (String key : mListCacheKey) {
            mMemoryCache.remove(key);
        }
        mListCacheKey.clear();
    }
}
