package com.api.demo.http;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.api.demo.R;
import com.api.demo.bus.rxbus.support.Subscribe;
import com.ljy.devring.DevRing;
import com.ljy.devring.base.activity.ActivityLife;
import com.ljy.devring.base.activity.IBaseActivity;
import com.ljy.devring.http.support.body.ProgressInfo;
import com.ljy.devring.http.support.observer.CommonObserver;
import com.ljy.devring.http.support.observer.DownloadObserver;
import com.ljy.devring.http.support.observer.UploadObserver;
import com.ljy.devring.http.support.throwable.HttpThrowable;
import com.ljy.devring.logger.RingLog;
import com.ljy.devring.other.toast.RingToast;
import com.ljy.devring.util.FileUtil;
import com.ljy.devring.util.NetworkUtil;
import com.ljy.devring.util.RxLifecycleUtil;
import com.ljy.devring.websocket.support.HeartBeatGenerateCallback;
import com.ljy.devring.websocket.support.WebSocketInfo;
import com.trello.rxlifecycle3.android.ActivityEvent;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.Timed;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * author:  ljy
 * date:    2018/11/7
 * description: 演示网络模块API使用
 *
 * DevRing使用文档：<a>https://www.jianshu.com/p/abede6623c58</a>
 * Retrofit+RxJava博客介绍：<a>https://www.jianshu.com/p/092452f287db</a>
 */

public class HttpActivity extends AppCompatActivity implements IBaseActivity {

    @BindView(R.id.tv_result)
    TextView mTvResult;
    @BindView(R.id.pb_upload)
    ProgressBar mPbUpload;
    @BindView(R.id.tv_upload_speed)
    TextView mTvUploadSpeed;
    @BindView(R.id.tv_upload_length)
    TextView mTvUploadLength;
    @BindView(R.id.pb_download)
    ProgressBar mPbDownload;
    @BindView(R.id.tv_download_speed)
    TextView mTvDownloadSpeed;
    @BindView(R.id.tv_download_length)
    TextView mTvDownloadLength;
    @BindView(R.id.tv_websocket_receive_msg)
    TextView mTvWebSocketReceiveMsg;
    @BindView(R.id.et_websocket_send_msg)
    EditText mEtWebSocketSendMsg;
    @BindView(R.id.et_websocket_url)
    EditText mEtWebSocketUrl;

    File mFileUpload;//要上传的文件
    UploadObserver mUploadObserver;//上传请求的回调
    private static String apikey = "0df993c66c0c636e29ecbb5344252a4a";

    File mFileSave;//下载内容将保存到此File中
    DownloadObserver mDownloadObserver;//下载请求的回调

    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http);
        ButterKnife.bind(this);
        setTitle("网络模块");
        initFile();//初始化用于上传和下载的文件
    }

    @OnClick({R.id.btn_common_request, R.id.btn_upload_request, R.id.btn_stop_upload, R.id.btn_download_request, R.id.btn_stop_download, R.id.btn_websocket_get, R.id.btn_websocket_send, R.id.btn_websocket_async_send, R.id.btn_websocket_heart_beat, R.id.btn_websocket_stop_heart_beat, R.id.btn_websocket_close_all, R.id.btn_refresh_manager})
    protected void onClick(View view) {
        switch (view.getId()) {

            //普通请求
            case R.id.btn_common_request:
                mTvResult.setText("请求中...");

                //获取请求
                Observable commonRequest = DevRing.httpManager().getService(ApiService.class).getPlayingMovie(apikey, 0, 5);

                //发起请求
                DevRing.httpManager().commonRequest(commonRequest, new CommonObserver<Result>() {
                    @Override
                    public void onResult(Result result) {
                        //请求成功回调
                        StringBuilder stringBuilder = new StringBuilder("豆瓣电影---正在上映：\n\n");
                        for (Result.Res res : result.getSubjects()) {
                            stringBuilder.append(res.getTitle() + "\n");
                        }
                        mTvResult.setText(stringBuilder.toString());
                    }

                    @Override
                    public void onError(HttpThrowable throwable) {
                        //请求失败回调
                        //异常信息通过throwable.message获取
                        mTvResult.setText(throwable.message);
                    }

                }, RxLifecycleUtil.bindUntilEvent(this, ActivityEvent.DESTROY));
                //第二个参数CommonObserver为请求回调，内部对异常信息进行了简单的封装

                //最后一个参数LifecycleTransformer用于控制请求的生命周期，可以通过RxLifecycleUtil来获取
                //比如这里的RxLifecycleUtil.bindUntilEvent(lifecycleEmitter, ActivityEvent.DESTROY)，表示在当前Activity Destroy时终止该请求。 要求lifecycleEmitter 实现 IBaseActivity接口
                //同理，RxLifecycleUtil.bindUntilEvent(lifecycleEmitter, FragmentEvent.DESTROY))表示在当前Fragment Destroy时终止该请求。 要求lifecycleEmitter 实现 IBaseFragment接口
                break;


            //上传请求
            case R.id.btn_upload_request:

                //获取请求
                Observable uploadRequest = DevRing.httpManager().getService(ApiService.class).upLoadFile("http://upload.qiniu.com/", RequestBody.create(MediaType.parse
                        ("multipart/form-data"), mFileUpload));

                //上传请求回调
                //为空时才初始化，避免创建了多个进度监听回调
                if (mUploadObserver == null) {
                    //UploadObserver构造函数传入要监听的上传地址
                    mUploadObserver = new UploadObserver("http://upload.qiniu.com/") {
                        @Override
                        public void onResult(Object result) {
                            //请求成功回调
                            //这里不可能回调，因为要上传成功还需要七牛云平台的token，本例子仅演示上传文件到请求实体，而不是到服务器中。
                        }

                        @Override
                        public void onError(long progressInfoId, HttpThrowable throwable) {
                            //请求失败回调
                            mTvUploadSpeed.setText("");
                            if (progressInfoId != 0) {
                                //上传文件至请求实体的过程中发生异常，一般是读写过程出错，重试即可
                                //手动终止未完成的上传请求也会回调这里
                                mPbUpload.setProgress(0);
                                mTvUploadLength.setText("");
                            } else {
                                //虽然成功上传文件至请求实体，但因为缺少必要参数（七牛云平台的token），所以请求结果会失败 {"error":"token not specified"}。
                            }
                        }

                        @Override
                        public void onProgress(ProgressInfo progressInfo) {
                            //上传进度回调
                            mPbUpload.setProgress(progressInfo.getPercent());
                            mTvUploadSpeed.setText("" + progressInfo.getSpeed() / 1024 + " KB/s");
                            mTvUploadLength.setText("" + progressInfo.getContentLength() / 1024 + " KB");
                            if (progressInfo.isFinish()) {
                                RingToast.show("成功上传进请求实体");
                                mTvUploadSpeed.setText("");
                            }
                        }
                    };
                }

                //发起新请求前，先手动终止之前的请求，避免发起多个相同的请求
                DevRing.httpManager().stopRequestByTag("upload");

                //发起请求
                DevRing.httpManager().uploadRequest(uploadRequest, mUploadObserver, "upload");
                //最后一个参数lifeTag用于控制终止请求，
                //比如这里将该请求与"upload"这个tag绑定，当要终止该请求时，则调用DevRing.httpManager().stopRequestByTag("upload")
                break;


            //终止上传请求
            case R.id.btn_stop_upload:
                DevRing.httpManager().stopRequestByTag("upload");
                break;


            //下载请求
            case R.id.btn_download_request:

                //获取请求
                Observable downloadRequest = DevRing.httpManager().getService(ApiService.class).downloadFile("http://ucan.25pp.com/Wandoujia_web_seo_baidu_homepage.apk");

                //下载请求回调
                //为空时才初始化，避免创建了多个进度监听回调
                if (mDownloadObserver == null) {
                    //DownloadObserver构造函数传入要要监听的下载地址
                    mDownloadObserver = new DownloadObserver("http://ucan.25pp.com/Wandoujia_web_seo_baidu_homepage.apk") {
                        @Override
                        public void onResult(boolean isSaveSuccess, String filePath) {
                            //请求成功回调
                            if (isSaveSuccess) {
                                RingToast.show("下载成功，已保存至： " + filePath);
                            } else {
                                RingToast.show("下载成功，保存失败");
                            }
                            mTvDownloadSpeed.setText("");
                        }

                        @Override
                        public void onError(long progressInfoId, HttpThrowable throwable) {
                            //请求失败回调
                            if (progressInfoId != 0) {
                                //下载文件过程中发生异常，一般时读写过程出错，重试即可
                                //手动终止未完成的下载请求也会回调这里
                                mPbDownload.setProgress(0);
                                mTvDownloadSpeed.setText("");
                                mTvDownloadLength.setText("");
                            } else {
                                //下载请求出错。
                                RingToast.show("下载请求失败");
                            }
                        }

                        @Override
                        public void onProgress(ProgressInfo progressInfo) {
                            //下载进度回调
                            mPbDownload.setProgress(progressInfo.getPercent());
                            mTvDownloadSpeed.setText("" + progressInfo.getSpeed() / 1024 + " KB/s");
                            mTvDownloadLength.setText("" + progressInfo.getContentLength() / 1024 + " KB");
                            if (progressInfo.isFinish()) {
                                mTvDownloadSpeed.setText("");
                            }
                        }
                    };
                }

                //发起新请求前，先手动终止之前的请求，避免发起多个相同的请求
                DevRing.httpManager().stopRequestByTag("download");

                //发起请求
                DevRing.httpManager().downloadRequest(mFileSave, downloadRequest, mDownloadObserver, "download");
                //最后一个参数lifeTag用于控制终止请求，
                //比如这里将该请求与"download"这个tag绑定，当要终止该请求时，则调用DevRing.httpManager().stopRequestByTag("download")
                break;


            //终止下载请求
            case R.id.btn_stop_download:
                DevRing.httpManager().stopRequestByTag("download");
                break;


            //调整并刷新网络模块的配置
            case R.id.btn_refresh_manager:
                Map<String, String> mapHeader = new HashMap<>();
                mapHeader.put("token", "your_token");

                //调整配置
                DevRing.configureHttp().setMapHeader(mapHeader);
                DevRing.configureHttp().setConnectTimeout(20);

                //刷新后新调整的配置才会生效
                DevRing.httpManager().refreshInstance();
                break;
            //WebSocket连接
            case R.id.btn_websocket_get:
                if (TextUtils.isEmpty(mEtWebSocketUrl.getText().toString())) {
                    RingToast.show("[WebSocket服务器地址]不能为空");
                    return;
                }
                DevRing.webSocketManager().get(mEtWebSocketUrl.getText().toString())
                        //切换到子线程去连接
                        .subscribeOn(Schedulers.newThread())
                        //绑定生命周期
//                        .as(RxLifecycleUtil.bindUntilEvent(this, ActivityEvent.DESTROY))
                        .subscribe(new Consumer<WebSocketInfo>() {
                            @Override
                            public void accept(WebSocketInfo webSocketInfo) throws Exception {
                                String json = webSocketInfo.getStringMsg();
                                if (!TextUtils.isEmpty(json)) {
                                    mTvWebSocketReceiveMsg.setText(json);
                                    RingLog.t("WebSocket接收消息").d(json);
                                }
                            }
                        });
                break;
            //WebSocket发送消息(同步)
            case R.id.btn_websocket_send:
                if (TextUtils.isEmpty(mEtWebSocketUrl.getText().toString())) {
                    RingToast.show("[WebSocket服务器地址]不能为空");
                    return;
                }
                if (TextUtils.isEmpty(mEtWebSocketSendMsg.getText().toString())) {
                    RingToast.show("[要发送的消息]不能为空");
                    return;
                }
                DevRing.webSocketManager().send(mEtWebSocketUrl.getText().toString(), mEtWebSocketSendMsg.getText().toString())
                        .subscribeOn(Schedulers.newThread())
//                        .as(RxLifecycleUtil.bindLifecycle(mLifecycleOwner))
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean isSuccess) throws Exception {
                                if (isSuccess) {//发送成功
                                    RingLog.t("WebSocket发送消息(同步)").d("发送成功");
                                } else {//发送失败
                                    RingLog.t("WebSocket发送消息(同步)").d("发送失败");
                                }
                            }
                        });
                break;
            //WebSocket发送消息(异步)
            case R.id.btn_websocket_async_send:
                if (TextUtils.isEmpty(mEtWebSocketUrl.getText().toString())) {
                    RingToast.show("[WebSocket服务器地址]不能为空");
                    return;
                }
                if (TextUtils.isEmpty(mEtWebSocketSendMsg.getText().toString())) {
                    RingToast.show("[要发送的消息]不能为空");
                    return;
                }
                DevRing.webSocketManager().asyncSend(mEtWebSocketUrl.getText().toString(), mEtWebSocketSendMsg.getText().toString())
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean isSuccess) throws Exception {
                                if (isSuccess) {//发送成功
                                    RingLog.t("WebSocket发送消息(异步)").d("发送成功");
                                } else {//发送失败
                                    RingLog.t("WebSocket发送消息(异步)").d("发送失败");
                                }
                            }
                        });
                break;
            //WebSocket发送心跳包
            case R.id.btn_websocket_heart_beat:
                if (TextUtils.isEmpty(mEtWebSocketUrl.getText().toString())) {
                    RingToast.show("[WebSocket服务器地址]不能为空");
                    return;
                }
                Observable<Boolean> observable = DevRing.webSocketManager().heartBeat(mEtWebSocketUrl.getText().toString(), 3, TimeUnit.SECONDS, new HeartBeatGenerateCallback() {
                    @Override
                    public String onGenerateHeartBeatMsg(long timestamp) {
                        //生成心跳Json，业务模块处理，例如后端需要秒值，我们除以1000换算为秒。
                        //后续可以在这里配置通用参数等
                        return String.valueOf(System.currentTimeMillis());
                    }
                });
                disposable = observable.subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        RingLog.t("WebSocket发送心跳包").d(String.valueOf(System.currentTimeMillis()));
                    }
                });
                break;
            case R.id.btn_websocket_stop_heart_beat:
                if (null != disposable && !disposable.isDisposed()) {
                    disposable.dispose();
                }else {
                    RingToast.show("无心跳连接");
                }
                break;
            //关闭所有WebSocket连接
            case R.id.btn_websocket_close_all:
                if (null != disposable && !disposable.isDisposed()) {
                    disposable.dispose();
                }
                DevRing.webSocketManager().closeAllNow();
                break;
        }
    }

    //初始化用于上传和下载的文件
    public void initFile() {
        mFileUpload = FileUtil.getFile(FileUtil.getExternalCacheDir(this), "upload_file.java");
        mFileSave = FileUtil.getFile(FileUtil.getExternalCacheDir(this), "wandoujia.apk");

        //复制assets文件到本地文件中
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream in = getAssets().open("upload_file.java");
                    FileOutputStream out = new FileOutputStream(mFileUpload);
                    byte[] buf = new byte[1024];
                    while ((in.read(buf)) != -1) {
                        out.write(buf, 0, buf.length);
                    }
                    in.close();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //页面销毁时终止所有网络请求，避免内存泄露以及回调异常

        //发射终止信号，终止上传请求
        DevRing.httpManager().stopRequestByTag("upload");
        //发射终止信号，终止下载请求
        DevRing.httpManager().stopRequestByTag("download");
        /**
         * 至于那个普通网络请求，由于生命周期是使用 RxLifecycleUtil.bindUntilEvent(this, ActivityEvent.DESTROY)控制，将会在Destroy时自动发射终止信号 {@link ActivityLi}
         */
    }

    /**
     *  {@link IBaseActivity}接口
     */
    @Override
    public boolean isUseEventBus() {
        return false;
    }

    /**
     *  {@link IBaseActivity}接口
     */
    @Override
    public boolean isUseFragment() {
        return false;
    }

}
