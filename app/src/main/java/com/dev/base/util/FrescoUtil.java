package com.dev.base.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;

import com.dev.base.R;
import com.dev.base.app.MyApplication;
import com.dev.base.app.constant.BaseConstants;
import com.dev.base.util.log.LogUtil;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.executors.UiThreadImmediateExecutorService;
import com.facebook.common.internal.Supplier;
import com.facebook.common.logging.FLog;
import com.facebook.common.memory.MemoryTrimType;
import com.facebook.common.memory.MemoryTrimmable;
import com.facebook.common.memory.MemoryTrimmableRegistry;
import com.facebook.common.memory.NoOpMemoryTrimmableRegistry;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.facebook.common.util.ByteConstants.MB;

/**
 * author：    ljy
 * date：      2017/9/15
 * description: Fresco图片加载辅助类，用于初始化Fresco
 *              http://www.jianshu.com/p/5b5625612f56
 */
public class FrescoUtil {

    private static final int DEFAULT_IMG_LOADING = R.mipmap.ic_image_load;//默认加载中图片
    private static final int DEFAULT_IMG_FAILURE = R.mipmap.ic_image_load;//默认加载失败图片
    private static final int DEFAULT_IMG_RETRY = R.mipmap.ic_image_load;//默认重新加载的图片

    private static final int MAX_DISK_CACHE_VERYLOW_SIZE = 10 * 1024 * 1024;//默认图极低磁盘空间缓存的最大值
    private static final int MAX_DISK_CACHE_LOW_SIZE = 30 * 1024 * 1024;//默认图低磁盘空间缓存的最大值
    private static final int MAX_DISK_CACHE_SIZE = 200 * MB;//磁盘缓存大小
//    private static final int MAX_MEMORY_CACHE_SIZE = 50;//内存缓存大小（目前取当前手机内存大小的五分之一）

    private static FrescoUtil instance;
    private Drawable retryImage;
    private Drawable failureImage;
    private Drawable placeholderImage;
    private final Resources res;

    private FrescoUtil() {
        res = MyApplication.getInstance().getResources();
        retryImage = ResourcesCompat.getDrawable(res, DEFAULT_IMG_RETRY, null);
        failureImage = ResourcesCompat.getDrawable(res, DEFAULT_IMG_FAILURE, null);
        placeholderImage = ResourcesCompat.getDrawable(res, DEFAULT_IMG_LOADING, null);
    }

    /**
     * 单例
     * @return FrescoUtil
     */
    public static FrescoUtil getInstance() {
        if (instance == null) {
            instance = new FrescoUtil();
        }
        return instance;
    }

    /**
     * 初始化
     * @param context 全局上下文
     */
    public void initializeFresco(Context context) {
            ImagePipelineConfig config = getImagePipelineConfig(context);
            Fresco.initialize(context, config);
    }


    /**
     * 图片配置
     *
     * @param context 上下文
     * @return ImagePipelineConfig
     */
    public ImagePipelineConfig getImagePipelineConfig(Context context) {
        //配置管理者
        ImagePipelineConfig.Builder imagePipelineConfigBuilder = ImagePipelineConfig.newBuilder(context);

        //配置内存缓存（未解码部分的缓存）
//        imagePipelineConfigBuilder.setEncodedMemoryCacheParamsSupplier(new Supplier<MemoryCacheParams>() {
//            @Override
//            public MemoryCacheParams get() {
//                int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();
//                int MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 5;//取手机内存最大值的五分之一作为可用的最大内存数
//
//                LogUtil.e("当前图片内存分配总大小", MAX_MEMORY_CACHE_SIZE);
//                MemoryCacheParams bitmapCacheParams = new MemoryCacheParams( //
//                        // 可用最大内存数，以字节为单位
//                        MAX_MEMORY_CACHE_SIZE,
//                        // 内存中允许的最多图片数量
//                        Integer.MAX_VALUE,
//                        // 内存中准备清理但是尚未删除的总图片所可用的最大内存数，以字节为单位
//                        MAX_MEMORY_CACHE_SIZE,
//                        // 内存中准备清除的图片最大数量
//                        Integer.MAX_VALUE,
//                        // 内存中单图片的最大大小
//                        Integer.MAX_VALUE);
//                return bitmapCacheParams;
//            }
//        });

        //配置内存缓存（已解码部分的缓存）
        imagePipelineConfigBuilder.setBitmapMemoryCacheParamsSupplier(new Supplier<MemoryCacheParams>() {
            public MemoryCacheParams get() {
                int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();
                int MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 5;//取手机内存最大值的五分之一作为可用的最大内存数

                LogUtil.e("当前图片内存分配总大小", MAX_MEMORY_CACHE_SIZE);
                MemoryCacheParams bitmapCacheParams = new MemoryCacheParams( //
                        // 可用最大内存数，以字节为单位
                        MAX_MEMORY_CACHE_SIZE,
                        // 内存中允许的最多图片数量
                        Integer.MAX_VALUE,
                        // 内存中准备清理但是尚未删除的总图片所可用的最大内存数，以字节为单位
                        MAX_MEMORY_CACHE_SIZE,
                        // 内存中准备清除的图片最大数量
                        Integer.MAX_VALUE,
                        // 内存中单图片的最大大小
                        Integer.MAX_VALUE);
                return bitmapCacheParams;
            }
        });
        //配置磁盘缓存
        imagePipelineConfigBuilder.setMainDiskCacheConfig(DiskCacheConfig.newBuilder(context).setBaseDirectoryPath(context.getExternalCacheDir()).setBaseDirectoryName
                (BaseConstants.APP_IMAGE).setMaxCacheSize(MAX_DISK_CACHE_SIZE).build());

        MemoryTrimmableRegistry memoryTrimmableRegistry = NoOpMemoryTrimmableRegistry.getInstance();
        memoryTrimmableRegistry.registerMemoryTrimmable(new MemoryTrimmable() {
            @Override
            public void trim(MemoryTrimType trimType) {
                final double suggestedTrimRatio = trimType.getSuggestedTrimRatio();

                LogUtil.e(String.format("onCreate suggestedTrimRatio : %f", suggestedTrimRatio));
                if (MemoryTrimType.OnCloseToDalvikHeapLimit.getSuggestedTrimRatio() == suggestedTrimRatio || MemoryTrimType.OnSystemLowMemoryWhileAppInBackground
                        .getSuggestedTrimRatio() == suggestedTrimRatio || MemoryTrimType.OnSystemLowMemoryWhileAppInForeground.getSuggestedTrimRatio() == suggestedTrimRatio) {
                    ImagePipelineFactory.getInstance().getImagePipeline().clearMemoryCaches();
                }
            }
        });
        //当内存紧张时采取的措施
        imagePipelineConfigBuilder.setMemoryTrimmableRegistry(memoryTrimmableRegistry);
        /**
         * 必须和ImageRequest的ResizeOptions一起使用，作用就是在图片解码时根据ResizeOptions所设的宽高的像素进行解码，
         * 这样解码出来可以得到一个更小的Bitmap。ResizeOptions和DownsampleEnabled参数都不影响原图片的大小，影响的是EncodeImage的大小，
         * 进而影响Decode出来的Bitmap的大小，ResizeOptions须和此参数结合使用，
         * 是因为单独使用ResizeOptions的话只支持JPEG图，所以需支持png、jpg、webp需要先设置此参数。
         */
        imagePipelineConfigBuilder.setDownsampleEnabled(true);
        // 配置渐进式显示（使用默认效果），仅支持文件类型为JPEG的网络图片
        imagePipelineConfigBuilder.setProgressiveJpegConfig(new SimpleProgressiveJpegConfig());

        //设置调试时，显示图片加载的Log
        FLog.setMinimumLoggingLevel(FLog.VERBOSE);
        Set<RequestListener> requestListeners = new HashSet<>();
        requestListeners.add(new RequestLoggingListener());
        imagePipelineConfigBuilder.setRequestListeners(requestListeners);

//        imagePipelineConfigBuilder.setCacheKeyFactory(cacheKeyFactory);
        //配置线程
//        imagePipelineConfigBuilder.setExecutorSupplier(executorSupplier);
        //配置统计跟踪器
//        imagePipelineConfigBuilder.setImageCacheStatsTracker(imageCacheStatsTracker);
//        imagePipelineConfigBuilder.setNetworkFetchProducer(networkFetchProducer);
//        imagePipelineConfigBuilder.setPoolFactory(poolFactory);

//        imagePipelineConfigBuilder.setSmallImageDiskCacheConfig(smallImageDiskCacheConfig);
        return imagePipelineConfigBuilder.build();
    }

    //加载网络图片，包括动图
    public void loadNetImage(SimpleDraweeView simpleDraweeView, String url) {
        Uri uri = Uri.parse(url);
        loadImage(simpleDraweeView, uri, false, false);
    }

    //加载网络图片，先加载小图，待大图加载完成后替换
    public void loadNetImageSmallToBig(SimpleDraweeView simpleDraweeView, String smallUrl, String bigUrl) {
        Uri smallUri = Uri.parse(smallUrl);
        Uri bigUri = Uri.parse(bigUrl);
        loadImageSmallToBig(simpleDraweeView, smallUri, bigUri);
    }

    //加载本地文件图片
    public void loadLocalImage(SimpleDraweeView simpleDraweeView, String fileName) {
        Uri uri = Uri.parse("file://" + fileName);
        loadImage(simpleDraweeView, uri, false, false);
    }

    //加载res下资源图片
    public void loadResourceImage(SimpleDraweeView simpleDraweeView, @DrawableRes int resId) {
        Uri uri = Uri.parse("res:///" + resId);
        loadImage(simpleDraweeView, uri, false, false);
    }

    //加载contentProvider下的图片
    public void loadContentProviderImage(SimpleDraweeView simpleDraweeView, int resId) {
        Uri uri = Uri.parse("content:///" + resId);
        loadImage(simpleDraweeView, uri, false, false);
    }

    //加载asset下的图片
    public void loadAssetImage(SimpleDraweeView simpleDraweeView, int resId) {
        Uri uri = Uri.parse("asset:///" + resId);
        loadImage(simpleDraweeView, uri, false, false);
    }

    /**
     * 加载图片核心方法
     *
     * @param simpleDraweeView            图片加载控件
     * @param uri                         图片加载地址
     * @param progressiveRenderingEnabled 是否开启渐进式加载
     * @param blurEnable                  是否开启高斯模糊效果
     */
    public void loadImage(SimpleDraweeView simpleDraweeView, Uri uri, boolean progressiveRenderingEnabled, boolean blurEnable) {
        setHierarchay(simpleDraweeView.getHierarchy());
        ImageRequest imageRequest = getImageRequest(uri, progressiveRenderingEnabled, blurEnable, simpleDraweeView);
        DraweeController draweeController = getController(imageRequest, simpleDraweeView.getController());
        simpleDraweeView.setController(draweeController);
    }

    //加载图片，先小图再大图
    public void loadImageSmallToBig(SimpleDraweeView simpleDraweeView, Uri smallUri, Uri bigUri) {
        setHierarchay(simpleDraweeView.getHierarchy());
        ImageRequest smallRequest = getImageRequest(smallUri, false, false, simpleDraweeView);
        ImageRequest bigRequest = getImageRequest(bigUri, false, false, simpleDraweeView);
        DraweeController draweeController = getSmallToBigController(smallRequest, bigRequest, simpleDraweeView.getController());
        simpleDraweeView.setController(draweeController);
    }

    //暂停图片加载
    public static void imagePause() {
        Fresco.getImagePipeline().pause();
    }

    //恢复图片加载
    public static void imageResume() {
        Fresco.getImagePipeline().resume();
    }

    //清空内存缓存（包括Bitmap缓存和未解码图片的缓存）
    public static void clearMemoryCaches() {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.clearMemoryCaches();
    }

    //对Hierarchy进行设置，如各种状态下显示的图片
    public void setHierarchay(GenericDraweeHierarchy hierarchy) {
        if (hierarchy != null) {
//            hierarchy.setFadeDuration(300);     //设置由进度条和占位符图片渐变过渡到加载完成的图片所使用的时间间隔
            hierarchy.setRetryImage(retryImage);  //重新加载图片
            hierarchy.setFailureImage(failureImage, ScalingUtils.ScaleType.CENTER_CROP); //加载失败的图片
            hierarchy.setPlaceholderImage(placeholderImage, ScalingUtils.ScaleType.CENTER_CROP);
            hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);

            ProgressBarDrawable progressBarDrawable = new ProgressBarDrawable();
            progressBarDrawable.setBackgroundColor(Color.BLACK);
            progressBarDrawable.setColor(Color.BLUE);
            hierarchy.setProgressBarImage(progressBarDrawable);//显示加载进度
        }
    }

    /**
     * 构建、获取ImageRequest
     * @param uri 加载路径
     * @param progressiveRenderingEnabled 是否开启渐进式加载
     * @param blurEnable 是否开启高斯模糊
     * @param simpleDraweeView 加载的图片控件
     * @return ImageRequest
     */
    public ImageRequest getImageRequest(Uri uri, boolean progressiveRenderingEnabled, boolean blurEnable, SimpleDraweeView simpleDraweeView) {
        int width;
        int height;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            width = simpleDraweeView.getWidth();
            height = simpleDraweeView.getHeight();
        } else {
            width = simpleDraweeView.getMaxWidth();
            height = simpleDraweeView.getMaxHeight();
        }

        //根据加载路径生成ImageRequest的构造者
        ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(uri);
        //调整解码图片的大小
        if (width > 0 && height > 0) {
            builder.setResizeOptions(new ResizeOptions(width, height));
        }
        //是否开启渐进式加载，仅支持JPEG图片
        builder.setProgressiveRenderingEnabled(progressiveRenderingEnabled);
        //是否开启高斯模糊效果
        if (blurEnable) {
            builder.setPostprocessor(new BasePostprocessor() {
                @Override
                public String getName() {
                    return "blurPostprocessor";
                }

                @Override
                public void process(Bitmap bitmap) {
                    BlurUtil.rsBlur(MyApplication.getInstance(), bitmap, 15);
//                    BlurUtil.javaBlur(bitmap, 15, true);
                }
            });
        }
        return builder.build();
    }

    /**
     * 构建、获取Controller
     * @param request
     * @param oldController
     * @return
     */
    public DraweeController getController(ImageRequest request, @Nullable DraweeController oldController) {
        PipelineDraweeControllerBuilder builder = Fresco.newDraweeControllerBuilder();
        builder.setImageRequest(request);
        builder.setTapToRetryEnabled(false);//设置是否允许加载失败时点击再次加载
        builder.setAutoPlayAnimations(true);//设置是否允许动画图自动播放
        builder.setOldController(oldController);
        return builder.build();
    }

    /**
     * 构建、获取Controller（先小图再大图）
     * @param smallRequest
     * @param bigRequest
     * @param oldController
     * @return
     */
    public DraweeController getSmallToBigController(ImageRequest smallRequest, ImageRequest bigRequest, @Nullable DraweeController oldController) {
        PipelineDraweeControllerBuilder builder = Fresco.newDraweeControllerBuilder();
        builder.setLowResImageRequest(smallRequest);//小图的图片请求
        builder.setImageRequest(bigRequest);//大图的图片请求
        builder.setTapToRetryEnabled(false);//设置是否允许加载失败时点击再次加载
        builder.setAutoPlayAnimations(true);//设置是否允许动画图自动播放
        builder.setOldController(oldController);
        return builder.build();
    }

    //加载图片，在FrescoBitmapCallback里获取返回的Bitmap
    public final void loadImageBitmap(String url, FrescoBitmapCallback<Bitmap> callback) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        try {
            fetch(Uri.parse(url), callback);
        } catch (Exception e) {
            //oom风险.
            e.printStackTrace();
            callback.onFailure(Uri.parse(url), e);
        }
    }

    private void fetch(final Uri uri, final FrescoBitmapCallback<Bitmap> callback) throws Exception {
        ImageRequestBuilder requestBuilder = ImageRequestBuilder.newBuilderWithSource(uri);
        ImageRequest imageRequest = requestBuilder.build();
        DataSource<CloseableReference<CloseableImage>> dataSource = ImagePipelineFactory.getInstance().getImagePipeline().fetchDecodedImage(imageRequest, null);
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            public void onNewResultImpl(@Nullable final Bitmap bitmap) {
                if (callback == null) return;
                if (bitmap != null && !bitmap.isRecycled()) {
                    //后台线程中加载
                    executeBackgroundTask.submit(new Callable<Bitmap>() {
                        @Override
                        public Bitmap call() throws Exception {
                            final Bitmap resultBitmap = bitmap.copy(bitmap.getConfig(), bitmap.isMutable());
                            if (resultBitmap != null && !resultBitmap.isRecycled()) {
                                //回调UI线程中去
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onSuccess(uri, resultBitmap);
                                    }
                                });
                            }
                            return resultBitmap;
                        }
                    });
                }
            }

            @Override
            public void onCancellation(DataSource<CloseableReference<CloseableImage>> dataSource) {
                super.onCancellation(dataSource);
                if (callback == null) return;
                callback.onCancel(uri);
            }

            @Override
            public void onFailureImpl(DataSource dataSource) {
                if (callback == null) return;
                Throwable throwable = null;
                if (dataSource != null) {
                    throwable = dataSource.getFailureCause();
                }
                callback.onFailure(uri, throwable);
            }
        }, UiThreadImmediateExecutorService.getInstance());
    }

    private ExecutorService executeBackgroundTask = Executors.newSingleThreadExecutor();

    public interface FrescoBitmapCallback<T> {

        void onSuccess(Uri uri, T result);

        void onFailure(Uri uri, Throwable throwable);

        void onCancel(Uri uri);
    }
}
