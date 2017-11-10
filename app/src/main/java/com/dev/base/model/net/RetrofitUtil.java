package com.dev.base.model.net;

import android.content.Context;

import com.dev.base.app.constant.UrlConstants;
import com.dev.base.model.entity.FileEntity;
import com.dev.base.util.NetworkUtil;
import com.dev.base.util.log.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * author:  ljy
 * date:    2017/9/14
 * description: Retrofit+Rxjava工具类，用于配置、封装等
 *              http://www.jianshu.com/p/092452f287db
 */

public class RetrofitUtil {

    private static Context mContext;
    private static ApiService mApiService;//提供各种具体的网络请求
    private static final int DEFAULT_TIMEOUT = 15;//请求超时时长，单位秒

    public static void init(Context context) {
        mContext = context;
    }

    public static ApiService getApiService() {
        if (mApiService == null) {
            OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
            //设置请求超时时长
            okHttpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            //启用Log日志
            okHttpClientBuilder.addInterceptor(getHttpLoggingInterceptor());
            //设置缓存方式、时长、地址
//            okHttpClientBuilder.addNetworkInterceptor(getCacheInterceptor());
//            okHttpClientBuilder.addInterceptor(getCacheInterceptor());
//            okHttpClientBuilder.cache(getCache());
            //设置https访问(验证证书)
//            okHttpClientBuilder.sslSocketFactory(getSSLSocketFactory(mContext, new int[]{R.raw.tomcat}));//请把服务器给的证书文件放在R.raw文件夹下
//            okHttpClientBuilder.hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            //设置统一的header
//            okHttpClientBuilder.addInterceptor(getHeaderInterceptor());

            Retrofit retrofit = new Retrofit.Builder()
                    //服务器地址
                    .baseUrl(UrlConstants.HOST_SITE_HTTPS)
                    //配置转化库，采用Gson
                    .addConverterFactory(GsonConverterFactory.create())
                    //配置回调库，采用RxJava
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    //设置OKHttpClient为网络客户端
                    .client(okHttpClientBuilder.build()).build();

            mApiService = retrofit.create(ApiService.class);
            return mApiService;
        } else {
            return mApiService;
        }
    }

    //提供Log日志插值器
    public static HttpLoggingInterceptor getHttpLoggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return loggingInterceptor;
    }

    //提供缓存插值器
    public static Interceptor getCacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                //对request的设置是用来指定有网/无网下所走的方式
                //对response的设置是用来指定有网/无网下的缓存时长

                Request request = chain.request();
                if (!NetworkUtil.isNetWorkAvailable(mContext)) {
                    //无网络下强制使用缓存，无论缓存是否过期,此时该请求实际上不会被发送出去。
                    //有网络时则根据缓存时长来决定是否发出请求
                    request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
                }

                Response response = chain.proceed(request);
                if (NetworkUtil.isNetWorkAvailable(mContext)) {
                    //有网络情况下，根据请求接口的设置，配置缓存。
//                    String cacheControl = request.cacheControl().toString();

                    //有网络情况下，超过1分钟，则重新请求，否则直接使用缓存数据
                    int maxAge = 60; //缓存一分钟
                    String cacheControl = "public,max-age=" + maxAge;
                    //当然如果你想在有网络的情况下都直接走网络，那么只需要
                    //将其超时时间maxAge设为0即可
                    return response.newBuilder().header("Cache-Control", cacheControl).removeHeader("Pragma").build();
                } else {
                    //无网络时直接取缓存数据，该缓存数据保存1周
                    int maxStale = 60 * 60 * 24 * 7 * 1;  //1周
                    return response.newBuilder().header("Cache-Control", "public,only-if-cached,max-stale=" + maxStale).removeHeader("Pragma").build();
                }

            }
        };
    }

    public static Interceptor getHeaderInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                Request.Builder builder = originalRequest.newBuilder();

                builder.header("timestamp", System.currentTimeMillis() + "");

                Request.Builder requestBuilder = builder.method(originalRequest.method(), originalRequest.body());
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        };
    }

    //配置缓存
    public static Cache getCache() {
        File cacheFile = new File(mContext.getExternalCacheDir(), "HttpCache");//缓存地址
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 50); //大小50Mb
        return cache;
    }

    //设置https证书
    protected static SSLSocketFactory getSSLSocketFactory(Context context, int[] certificates) {

        if (context == null) {
            throw new NullPointerException("context == null");
        }

        //CertificateFactory用来证书生成
        CertificateFactory certificateFactory;
        try {
            certificateFactory = CertificateFactory.getInstance("X.509");
            //Create a KeyStore containing our trusted CAs
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);

            for (int i = 0; i < certificates.length; i++) {
                //读取本地证书
                InputStream is = context.getResources().openRawResource(certificates[i]);
                keyStore.setCertificateEntry(String.valueOf(i), certificateFactory.generateCertificate(is));

                if (is != null) {
                    is.close();
                }
            }
            //Create a TrustManager that trusts the CAs in our keyStore
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            //Create an SSLContext that uses our TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
            return sslContext.getSocketFactory();

        } catch (Exception e) {

        }
        return null;
    }

    /**
     * 对observable进行统一转换（用于非文件下载请求）
     *
     * @param observable         被订阅者
     * @param observer           订阅者
     * @param lifecycleSubject   生命周期事件发射者
     */
    public static void composeToSubscribe(Observable observable, Observer observer, PublishSubject<LifeCycleEvent> lifecycleSubject) {
        //默认在进入DESTROY状态时发射一个事件来终止网络请求
        composeToSubscribe(observable, observer, LifeCycleEvent.DESTROY, lifecycleSubject);
    }

    /**
     * 对observable进行统一转换（用于非文件下载请求）
     *
     * @param observable         被订阅者
     * @param observer           订阅者
     * @param event              生命周期中的某一个状态，比如传入DESTROY，则表示在进入destroy状态时lifecycleSubject会发射一个事件从而终止请求
     * @param lifecycleSubject   生命周期事件发射者
     */
    public static void composeToSubscribe(Observable observable, Observer observer, LifeCycleEvent event, PublishSubject<LifeCycleEvent> lifecycleSubject) {
        observable.compose(getTransformer(event, lifecycleSubject)).subscribe(observer);
    }


    /**
     * 获取统一转换用的Transformer（用于非文件下载请求）
     *
     * @param event               生命周期中的某一个状态，比如传入DESTROY，则表示在进入destroy状态时
     *                            lifecycleSubject会发射一个事件从而终止请求
     * @param lifecycleSubject    生命周期事件发射者
     */
    public static <T> ObservableTransformer<T, T> getTransformer(final LifeCycleEvent event, final PublishSubject<LifeCycleEvent> lifecycleSubject) {
        return new ObservableTransformer() {
            @Override
            public ObservableSource apply(Observable upstream) {

                //当lifecycleObservable发射事件时，终止操作。
                //统一在请求时切入io线程，回调后进入ui线程
                //加入失败重试机制（延迟3秒开始重试，重试3次）
                return upstream
                        .takeUntil(getLifeCycleObservable(event, lifecycleSubject))
                        .retryWhen(new RetryFunction(3,3))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     * 对observable进行统一转换（用于文件下载请求）
     *
     * @param observable         被订阅者
     * @param observer           订阅者
     * @param lifecycleSubject   生命周期事件发射者
     * @param file               目标文件，下载的电影将保存到该文件中
     */
    public static void composeToSubscribeForDownload(Observable observable, HttpFileObserver observer, PublishSubject<LifeCycleEvent> lifecycleSubject, File file) {
        //默认在进入DESTROY状态时发射一个事件来终止网络请求
        composeToSubscribeForDownload(observable, observer, LifeCycleEvent.DESTROY, lifecycleSubject, file);
    }

    /**
     * 对observable进行统一转换（用于文件下载请求）
     *
     * @param observable         被订阅者
     * @param observer           订阅者
     * @param event              生命周期中的某一个状态，比如传入DESTROY，则表示在进入destroy状态时lifecycleSubject会发射一个事件从而终止请求
     * @param lifecycleSubject   生命周期事件发射者
     * @param file               目标文件，下载的电影将保存到该文件中
     */
    public static void composeToSubscribeForDownload(Observable observable, HttpFileObserver observer, LifeCycleEvent event, PublishSubject<LifeCycleEvent> lifecycleSubject, File file) {
        observable.compose(getTransformerForDownload(event, lifecycleSubject, observer, file)).subscribe(observer);
    }

    /**
     * 获取统一转换用的Transformer（用于文件下载请求）
     *
     * @param event              生命周期中的某一个状态，比如传入DESTROY，则表示在进入destroy状态时
     *                           lifecycleSubject会发射一个事件从而终止请求
     * @param lifecycleSubject   生命周期事件发射者
     * @param observer           订阅者
     * @param file               目标文件，下载的电影将保存到该文件中
     */
    public static <T> ObservableTransformer<T, T> getTransformerForDownload(final LifeCycleEvent event, final PublishSubject<LifeCycleEvent> lifecycleSubject, final HttpFileObserver observer, final File file) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {

                //当lifecycleObservable发射事件时，终止操作。
                //在请求时切入io线程，回调后先在io线程中下载并保存文件到本地，最后再进入ui线程
                return upstream
                        .takeUntil(getLifeCycleObservable(event,lifecycleSubject))
                        .observeOn(Schedulers.io()) //指定doOnNext的操作在io后台线程进行
                        .doOnNext((Consumer<? super T>) new Consumer<ResponseBody>() {

                            //doOnNext里的方法执行完毕，subscriber里的onNext、onError等方法才会执行。
                            @Override
                            public void accept(ResponseBody body) throws Exception {
                                //下载文件，保存到本地
                                boolean isSuccess = downloadAndSave(body, file);
                                //将“文件是否成功保存到本地”的结果传递给订阅者
                                observer.setFileSaveSuccess(isSuccess);
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }


    //下载文件，并保存到目标文件中
    public static boolean downloadAndSave(ResponseBody body, File fileSave) {

        try {
//            File fileSave = FileUtil.generateFile(FileUtil.getFilesDir(), "test.apk");
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();//文件总长度
                long fileSizeDownloaded = 0;//已下载的文件长度

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(fileSave);

                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;

                    //这里可以把已下载的文件长度实时传给页面进度条更新显示，注意切换线程
                    LogUtil.d("download progress: " + fileSizeDownloaded + "/" + fileSize);
                }
                outputStream.flush();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } finally {
                if (inputStream != null)
                    inputStream.close();

                if (outputStream != null)
                    outputStream.close();
            }

        } catch (IOException  e) {
            e.printStackTrace();
            return false;
        }
    }

    //获取用于控制声明周期的Observable
    public static Observable<LifeCycleEvent> getLifeCycleObservable(final LifeCycleEvent event, PublishSubject<LifeCycleEvent> lifecycleSubject) {
        return lifecycleSubject.filter(new Predicate<LifeCycleEvent>() {
            @Override
            public boolean test(LifeCycleEvent lifeCycleEvent) throws Exception {
                //当生命周期为event状态时，发射事件
                return lifeCycleEvent.equals(event);
            }
        }).take(1);
    }


    //请求失败重试机制
    public static class RetryFunction implements Function<Observable<Throwable>, ObservableSource<?>> {

        private int retryDelaySeconds;//延迟重试的时间
        private int retryCount;//记录当前重试次数
        private int retryCountMax;//最大重试次数

        public RetryFunction(int retryDelaySeconds, int retryCountMax) {
            this.retryDelaySeconds = retryDelaySeconds;
            this.retryCountMax = retryCountMax;
        }

        @Override
        public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
            //方案一：使用zip控制重试次数，重试3次后不再重试（会隐式回调onComplete结束请求，但我需要的是回调onError，所以没采用方案一）
//            return Observable.zip(throwableObservable,Observable.range(1, retryCountMax),new BiFunction<Throwable, Integer, Throwable>() {
//                @Override
//                public Throwable apply(Throwable throwable, Integer integer) throws Exception {
//                    LogUtil.e("ljy",""+integer);
//                    return throwable;
//                }
//            }).flatMap(new Function<Throwable, ObservableSource<?>>() {
//                @Override
//                public ObservableSource<?> apply(Throwable throwable) throws Exception {
//                    if (throwable instanceof UnknownHostException) {
//                        return Observable.error(throwable);
//                    }
//                    return Observable.timer(retryDelaySeconds, TimeUnit.SECONDS);
//                }
//            });



            //方案二：使用全局变量来控制重试次数，重试3次后不再重试，通过代码显式回调onError结束请求
            return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                @Override
                public ObservableSource<?> apply(Throwable throwable) throws Exception {
                    //如果失败的原因是UnknownHostException（DNS解析失败，当前无网络），则没必要重试，直接回调error结束请求即可
                    if (throwable instanceof UnknownHostException) {
                        return Observable.error(throwable);
                    }

                    //没超过最大重试次数的话则进行重试
                    if (++retryCount <= retryCountMax) {
                        //延迟retryDelaySeconds后开始重试
                        return Observable.timer(retryDelaySeconds, TimeUnit.SECONDS);
                    }

                    return Observable.error(throwable);
                }
            });
        }
    }




    /**
     * 生成上传文件用的RequestBody
     *
     * @param fileEntity 文件实体
     * @param mediaType 文件类型
     * @return 请求用的实体
     */
    public static RequestBody fileToPart(FileEntity fileEntity, final MediaType mediaType) {
        return RequestBody.create(mediaType, fileEntity.getFile());
    }

    /**
     * 生成上传文件用的map参数（单个文件）
     *
     * @param fileEntity 文件实体
     * @param mediaType  文件类型
     * @return 请求用的实体
     */
    public static Map<String, RequestBody> fileToPartMap(final FileEntity fileEntity, final MediaType mediaType) {
        List<FileEntity> fileEntities = new ArrayList<>();
        fileEntities.add(fileEntity);
        return filesToPartMap(fileEntities, mediaType);
    }

    /**
     * 生成上传文件用的map参数（多个文件）
     *
     * @param fileEntities 文件实体列表
     * @param mediaType    文件类型
     * @return 请求用的实体
     */
    public static Map<String, RequestBody> filesToPartMap(final List<FileEntity> fileEntities, final MediaType mediaType) {
        final Map<String, RequestBody> bodyMap = new HashMap<>();
        for (FileEntity entity : fileEntities) {
            bodyMap.put(entity.getName() + "\"; filename=\"" + entity.getFile().getName(), RequestBody.create(mediaType, entity.getFile()));
        }
        return bodyMap;
    }

}
