package com.ljy.devring.websocket;

import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import com.ljy.devring.DevRing;
import com.ljy.devring.cache.support.MemoryCache;
import com.ljy.devring.http.HttpConfig;
import com.ljy.devring.http.support.RetryFunction;
import com.ljy.devring.http.support.body.ProgressListener;
import com.ljy.devring.http.support.observer.DownloadObserver;
import com.ljy.devring.logger.RingLog;
import com.ljy.devring.util.FileUtil;
import com.ljy.devring.util.NetworkUtil;
import com.ljy.devring.util.RxLifecycleUtil;
import com.ljy.devring.websocket.support.HeartBeatGenerateCallback;
import com.ljy.devring.websocket.support.ImproperCloseException;
import com.ljy.devring.websocket.support.WebSocketCloseEnum;
import com.ljy.devring.websocket.support.WebSocketInfo;
import com.ljy.devring.websocket.support.WebSocketInfoPool;
import com.trello.rxlifecycle3.LifecycleTransformer;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.Timed;
import io.reactivex.subjects.PublishSubject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import retrofit2.Retrofit;

/**
 * @author: XieYos
 * @date: 2021年9月2日
 * @description: WebSocket管理者
 */
@Singleton
public class WebSocketManager implements WebSocketService {
    private static final String TAG = WebSocketManager.class.getSimpleName();
    @Inject
    Provider<Retrofit> mProviderRetrofit;
    @Inject
    MemoryCache mMemoryCache;
    @Inject
    HttpConfig mHttpConfig;
    @Inject
    OkHttpClient okHttpClient;

    Retrofit mRetrofit;
    List<String> mListCacheKey;
    PublishSubject<String> mTagEmitter;//用于发射Tag来终止与该Tag绑定的网络请求
    /**
     * 缓存观察者对象，Url对应一个Observable
     */
    private Map<String, Observable<WebSocketInfo>> mObservableCacheMap;
    /**
     * 缓存Url和对应的WebSocket实例，同一个Url共享一个WebSocket连接
     */
    private Map<String, WebSocket> mWebSocketPool;
    /**
     * WebSocketInfo缓存池
     */
    private final WebSocketInfoPool mWebSocketInfoPool;

    @Inject
    public WebSocketManager(HttpConfig httpConfig) {
        mListCacheKey = new ArrayList<>();
        mTagEmitter = PublishSubject.create();
        this.mObservableCacheMap = new HashMap<>(16);
        this.mWebSocketPool = new HashMap<>(16);
        mWebSocketInfoPool = new WebSocketInfoPool(httpConfig.getWebSocketMaxCacheCount());
    }

    /**
     * 普通的网络api请求
     *
     * @param observable  请求
     * @param observer    请求回调
     * @param transformer 生命周期控制，可通过RxLifecycleUtil获取。如果为null，则不进行生命周期控制，
     */
    public void commonRequest(Observable observable, Observer observer, LifecycleTransformer transformer) {
        handleLife(handleThread(observable), transformer).subscribe(observer);
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


    public void refreshInstance() {
        mRetrofit = null;
        mRetrofit = mProviderRetrofit.get();
        for (String key : mListCacheKey) {
            mMemoryCache.remove(key);
        }
        mListCacheKey.clear();
    }


    @Override
    public Observable<WebSocketInfo> get(String url, LifecycleTransformer transformer) {
        return getWebSocketInfo(url, transformer);
    }

    @Override
    public Observable<WebSocketInfo> get(String url, long timeout, TimeUnit timeUnit, LifecycleTransformer transformer) {
        return getWebSocketInfo(url, timeout, timeUnit, transformer);
    }

    @Override
    public Observable<Boolean> send(String url, String msg, LifecycleTransformer transformer) {
        return handleLife(handleThread(Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                WebSocket webSocket = mWebSocketPool.get(url);
                if (webSocket == null) {
                    emitter.onError(new IllegalStateException("The WebSocket not open"));
                } else {
                    emitter.onNext(webSocket.send(msg));
                }
            }
        })), transformer);
    }

    @Override
    public Observable<Boolean> send(String url, ByteString byteString, LifecycleTransformer transformer) {
        return handleLife(handleThread(Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                WebSocket webSocket = mWebSocketPool.get(url);
                if (webSocket == null) {
                    emitter.onError(new IllegalStateException("The WebSocket not open"));
                } else {
                    emitter.onNext(webSocket.send(byteString));
                }
            }
        })), transformer);
    }

    @Override
    public Observable<Boolean> asyncSend(String url, String msg, LifecycleTransformer transformer) {
        return getWebSocket(url, transformer)
                .take(1)
                .map(new Function<WebSocket, Boolean>() {
                    @Override
                    public Boolean apply(WebSocket webSocket) throws Exception {
                        return webSocket.send(msg);
                    }
                });
    }

    @Override
    public Observable<Boolean> asyncSend(String url, ByteString byteString, LifecycleTransformer transformer) {
        return getWebSocket(url, transformer)
                .take(1)
                .map(new Function<WebSocket, Boolean>() {
                    @Override
                    public Boolean apply(WebSocket webSocket) throws Exception {
                        return webSocket.send(byteString);
                    }
                });
    }

    @Override
    public Observable<Boolean> close(String url, LifecycleTransformer transformer) {
        return handleLife(handleThread(Observable.create(new ObservableOnSubscribe<WebSocket>() {
            @Override
            public void subscribe(ObservableEmitter<WebSocket> emitter) throws Exception {
                WebSocket webSocket = mWebSocketPool.get(url);
                if (webSocket == null) {
                    emitter.onError(new NullPointerException("url:" + url + " WebSocket must be not null"));
                } else {
                    emitter.onNext(webSocket);
                }
            }
        }).map(new Function<WebSocket, Boolean>() {
            @Override
            public Boolean apply(WebSocket webSocket) throws Exception {
                return closeWebSocket(webSocket);
            }
        })), transformer);
    }

    @Override
    public boolean closeNow(String url) {
        return closeWebSocket(mWebSocketPool.get(url));
    }

    @Override
    public Observable<List<Boolean>> closeAll(LifecycleTransformer transformer) {
        return handleLife(handleThread(Observable
                .just(mWebSocketPool)
                .map(new Function<Map<String, WebSocket>, Collection<WebSocket>>() {
                    @Override
                    public Collection<WebSocket> apply(Map<String, WebSocket> webSocketMap) throws Exception {
                        return webSocketMap.values();
                    }
                })
                .concatMap(new Function<Collection<WebSocket>, ObservableSource<WebSocket>>() {
                    @Override
                    public ObservableSource<WebSocket> apply(Collection<WebSocket> webSockets) throws Exception {
                        return Observable.fromIterable(webSockets);
                    }
                }).map(new Function<WebSocket, Boolean>() {
                    @Override
                    public Boolean apply(WebSocket webSocket) throws Exception {
                        return closeWebSocket(webSocket);
                    }
                }).collect(new Callable<List<Boolean>>() {
                    @Override
                    public List<Boolean> call() throws Exception {
                        return new ArrayList<>();
                    }
                }, new BiConsumer<List<Boolean>, Boolean>() {
                    @Override
                    public void accept(List<Boolean> list, Boolean isCloseSuccess) throws Exception {
                        list.add(isCloseSuccess);
                    }
                }).toObservable()), transformer);
    }

    @Override
    public void closeAllNow() {
        for (Map.Entry<String, WebSocket> entry : mWebSocketPool.entrySet()) {
            closeWebSocket(entry.getValue());
        }
    }

    /**
     * 是否有连接
     */
    private boolean hasWebSocketConnection(String url) {
        return mWebSocketPool.get(url) != null;
    }

    /**
     * 关闭WebSocket连接
     */
    private boolean closeWebSocket(WebSocket webSocket) {
        if (webSocket == null) {
            return false;
        }
        WebSocketCloseEnum normalCloseEnum = WebSocketCloseEnum.USER_EXIT;
        boolean result = webSocket.close(normalCloseEnum.getCode(), normalCloseEnum.getReason());
        if (result) {
            removeUrlWebSocketMapping(webSocket);
        }
        return result;
    }

    /**
     * 移除Url和WebSocket的映射
     */
    private void removeUrlWebSocketMapping(WebSocket webSocket) {
        for (Map.Entry<String, WebSocket> entry : mWebSocketPool.entrySet()) {
            if (entry.getValue() == webSocket) {
                String url = entry.getKey();
                mObservableCacheMap.remove(url);
                mWebSocketPool.remove(url);
            }
        }
    }

    private void removeWebSocketCache(WebSocket webSocket) {
        for (Map.Entry<String, WebSocket> entry : mWebSocketPool.entrySet()) {
            if (entry.getValue() == webSocket) {
                String url = entry.getKey();
                mWebSocketPool.remove(url);
            }
        }
    }

    public Observable<WebSocket> getWebSocket(String url, LifecycleTransformer transformer) {
        return getWebSocketInfo(url, transformer)
                .filter(new Predicate<WebSocketInfo>() {
                    @Override
                    public boolean test(WebSocketInfo webSocketInfo) throws Exception {
                        return webSocketInfo.getWebSocket() != null;
                    }
                })
                .map(new Function<WebSocketInfo, WebSocket>() {
                    @Override
                    public WebSocket apply(WebSocketInfo webSocketInfo) throws Exception {
                        return webSocketInfo.getWebSocket();
                    }
                });
    }

    public Observable<WebSocketInfo> getWebSocketInfo(String url,LifecycleTransformer transformer) {
        return getWebSocketInfo(url, 5, TimeUnit.SECONDS, transformer);
    }

    public synchronized Observable<WebSocketInfo> getWebSocketInfo(final String url, final long timeout, final TimeUnit timeUnit, LifecycleTransformer transformer) {
        //先从缓存中取
        Observable<WebSocketInfo> observable = mObservableCacheMap.get(url);
        if (observable == null) {
            //缓存中没有，新建
            observable = Observable
                    .create(new WebSocketOnSubscribe(url))
                    .retry()
                    //因为有share操作符，只有当所有观察者取消注册时，这里才会回调
                    .doOnDispose(new Action() {
                        @Override
                        public void run() throws Exception {
                            //所有都不注册了，关闭连接
                            closeNow(url);
                            Log.d(TAG, "所有观察者都取消注册，关闭连接...");
                        }
                    })
                    //Share操作符，实现多个观察者对应一个数据源
                    .share();
            observable = handleLife(handleThread(observable), transformer);
            //将数据源缓存
            mObservableCacheMap.put(url, observable);
        } else {
            //缓存中有，从连接池中取出
            WebSocket webSocket = mWebSocketPool.get(url);
            if (webSocket != null) {
                observable = observable.startWith(createConnect(url, webSocket));
            }
        }
        return observable;
    }

    /**
     * 组装数据源
     */
    private final class WebSocketOnSubscribe implements ObservableOnSubscribe<WebSocketInfo> {
        private String mWebSocketUrl;
        private WebSocket mWebSocket;
        private boolean isReconnecting = false;

        public WebSocketOnSubscribe(String webSocketUrl) {
            this.mWebSocketUrl = webSocketUrl;
        }

        @Override
        public void subscribe(ObservableEmitter<WebSocketInfo> emitter) throws Exception {
            //因为retry重连不能设置延时，所以只能这里延时，降低发送频率
            if (mWebSocket == null && isReconnecting) {
                if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
                    long millis = mHttpConfig.getReconnectIntervalTimeUnit().toMillis(mHttpConfig.getReconnectInterval());
                    if (millis == 0) {
                        millis = 1000;
                    }
                    SystemClock.sleep(millis);
                }
            }
            initWebSocket(emitter);
        }

        private Request createRequest(String url) {
            return new Request.Builder().get().url(url).build();
        }

        /**
         * 初始化WebSocket
         */
        private synchronized void initWebSocket(ObservableEmitter<WebSocketInfo> emitter) {
            if (mWebSocket == null) {
                mWebSocket = okHttpClient.newWebSocket(createRequest(mWebSocketUrl), new WebSocketListener() {
                    @Override
                    public void onOpen(WebSocket webSocket, Response response) {
                        super.onOpen(webSocket, response);
                        //连接成功
                        if (!emitter.isDisposed()) {
                            mWebSocketPool.put(mWebSocketUrl, mWebSocket);
                            //重连成功
                            if (isReconnecting) {
                                emitter.onNext(createReconnect(mWebSocketUrl, webSocket));
                            } else {
                                emitter.onNext(createConnect(mWebSocketUrl, webSocket));
                            }
                        }
                        isReconnecting = false;
                    }

                    @Override
                    public void onMessage(WebSocket webSocket, String text) {
                        super.onMessage(webSocket, text);
                        //收到消息
                        if (!emitter.isDisposed()) {
                            emitter.onNext(createReceiveStringMsg(mWebSocketUrl, webSocket, text));
                        }
                    }

                    @Override
                    public void onMessage(WebSocket webSocket, ByteString bytes) {
                        super.onMessage(webSocket, bytes);
                        //收到消息
                        if (!emitter.isDisposed()) {
                            emitter.onNext(createReceiveByteStringMsg(mWebSocketUrl, webSocket, bytes));
                        }
                    }

                    @Override
                    public void onClosed(WebSocket webSocket, int code, String reason) {
                        super.onClosed(webSocket, code, reason);
                        if (!emitter.isDisposed()) {
                            emitter.onNext(createClose(mWebSocketUrl));
                        }
                    }

                    @Override
                    public void onFailure(WebSocket webSocket, Throwable throwable, Response response) {
                        super.onFailure(webSocket, throwable, response);
                        isReconnecting = true;
                        mWebSocket = null;
                        //移除WebSocket缓存，retry重试重新连接
                        removeWebSocketCache(webSocket);
                        if (!emitter.isDisposed()) {
                            emitter.onNext(createPrepareReconnect(mWebSocketUrl));
                            //失败发送onError，让retry操作符重试
                            emitter.onError(new ImproperCloseException());
                        }
                    }
                });
            }
        }
    }

    @Override
    public Observable<Boolean> heartBeat(String url, int period, TimeUnit unit,
                                         HeartBeatGenerateCallback heartBeatGenerateCallback, LifecycleTransformer transformer) {
        if (heartBeatGenerateCallback == null) {
            return Observable.error(new NullPointerException("heartBeatGenerateCallback == null"));
        }
        return handleLife(handleThread(Observable
                .interval(period, unit)
                .subscribeOn(AndroidSchedulers.mainThread())
                //timestamp操作符，给每个事件加一个时间戳
                .timestamp()
                .retry()
                .flatMap(new Function<Timed<Long>, ObservableSource<Boolean>>() {
                    @Override
                    public ObservableSource<Boolean> apply(Timed<Long> timed) throws Exception {
                        long timestamp = timed.time();
                        //判断网络，存在网络才发消息，否则直接返回发送心跳失败
                        if (NetworkUtil.isNetWorkAvailable(DevRing.application().getBaseContext())) {
                            String heartBeatMsg = heartBeatGenerateCallback.onGenerateHeartBeatMsg(timestamp);
                            RingLog.d(TAG, "发送心跳消息: " + heartBeatMsg);
                            if (hasWebSocketConnection(url)) {
                                return send(url, heartBeatMsg, transformer);
                            } else {
                                //这里必须用异步发送，如果切断网络，再重连，缓存的WebSocket会被清除，此时再重连网络
                                //是没有WebSocket连接可用的，所以就需要异步连接完成后，再发送
                                return asyncSend(url, heartBeatMsg, transformer);
                            }
                        } else {
                            RingLog.d(TAG, "无网络连接，不发送心跳，下次网络连通时，再次发送心跳");
                            return Observable.create(new ObservableOnSubscribe<Boolean>() {
                                @Override
                                public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                                    emitter.onNext(false);
                                }
                            });
                        }
                    }
                })), transformer);
    }

    private WebSocketInfo createConnect(String url, WebSocket webSocket) {
        return mWebSocketInfoPool.obtain(url)
                .setWebSocket(webSocket)
                .setConnect(true);
    }

    private WebSocketInfo createReconnect(String url, WebSocket webSocket) {
        return mWebSocketInfoPool.obtain(url)
                .setWebSocket(webSocket)
                .setReconnect(true);
    }

    private WebSocketInfo createPrepareReconnect(String url) {
        return mWebSocketInfoPool.obtain(url)
                .setPrepareReconnect(true);
    }

    private WebSocketInfo createReceiveStringMsg(String url, WebSocket webSocket, String stringMsg) {
        return mWebSocketInfoPool.obtain(url)
                .setConnect(true)
                .setWebSocket(webSocket)
                .setStringMsg(stringMsg);
    }

    private WebSocketInfo createReceiveByteStringMsg(String url, WebSocket webSocket, ByteString byteMsg) {
        return mWebSocketInfoPool.obtain(url)
                .setConnect(true)
                .setWebSocket(webSocket)
                .setByteStringMsg(byteMsg);
    }

    private WebSocketInfo createClose(String url) {
        return mWebSocketInfoPool.obtain(url);
    }
}
