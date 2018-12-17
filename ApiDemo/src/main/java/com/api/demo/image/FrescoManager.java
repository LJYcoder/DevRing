package com.api.demo.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.executors.UiThreadImmediateExecutorService;
import com.facebook.common.internal.Supplier;
import com.facebook.common.logging.FLog;
import com.facebook.common.memory.MemoryTrimType;
import com.facebook.common.memory.MemoryTrimmable;
import com.facebook.common.memory.MemoryTrimmableRegistry;
import com.facebook.common.memory.NoOpMemoryTrimmableRegistry;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.memory.PooledByteBufferInputStream;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.animated.base.AnimatedImageResult;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpNetworkFetcher;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig;
import com.facebook.imagepipeline.image.CloseableAnimatedImage;
import com.facebook.imagepipeline.image.CloseableBitmap;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.ljy.devring.DevRing;
import com.ljy.devring.image.support.IImageManager;
import com.ljy.devring.image.support.ImageConfig;
import com.ljy.devring.image.support.ImageListener;
import com.ljy.devring.image.support.LoadOption;
import com.ljy.devring.other.RingLog;
import com.ljy.devring.util.DensityUtil;
import com.ljy.devring.util.FileUtil;
import com.ljy.devring.util.Preconditions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;

import jp.wasabeef.fresco.processors.BlurPostprocessor;
import jp.wasabeef.fresco.processors.CombinePostProcessors;
import jp.wasabeef.fresco.processors.GrayscalePostprocessor;
import okhttp3.OkHttpClient;

/**
 * author:  ljy
 * date:    2018/3/15
 * description:
 * 目的是来演示如何使用其他图片框架来替换DevRing中默认提供的Glide。
 * 选了Fresco来替换，Fresco的相关使用可以参考 <a>https://www.jianshu.com/p/5b5625612f56</a>
 * <p>
 * 替换步骤：
 * 1.gradle中添加其他图片框架的依赖，去除被替换的框架的依赖以减小包体积
 * 2.实现IImageManager接口
 * 3.Application中通过DevRing.configureImage(new FrescoManager())方法传入，该方法返回ImageConfig，可以进行相关的全局配置
 * 4.把加载图片的ImageView更换为Fresco要求的SimpleDraweeView
 * <p>
 * 替换后一样是通过DevRing.imageManager()来使用相关功能。
 * <p>
 * 可在本类中添加IImageManager接口以外的方法(比如Fresco独特的渐进式加载、先加载小图再加载大图等功能。)
 * ，然后通过DevRing.<FrescoManager>imageManager()来调用。
 * <p>
 * <a>https://www.jianshu.com/p/5b5625612f56</a>
 */

public class FrescoManager implements IImageManager {

    private Context mContext;
    private ImageConfig mImageConfig;

    @Override
    public void init(Context context, final ImageConfig imageConfig) {
        mContext = context;
        mImageConfig = imageConfig;
        //配置管理者
        ImagePipelineConfig.Builder imagePipelineConfigBuilder = ImagePipelineConfig.newBuilder(context);

        //配置内存缓存（已解码部分的缓存）
        if (imageConfig.getMemoryCacheSize() > 0) {
            imagePipelineConfigBuilder.setBitmapMemoryCacheParamsSupplier(new Supplier<MemoryCacheParams>() {
                public MemoryCacheParams get() {
                    MemoryCacheParams bitmapCacheParams = new MemoryCacheParams( //
                            // 可用最大内存数，以字节为单位
                            imageConfig.getMemoryCacheSize(),
                            // 内存中允许的最多图片数量
                            Integer.MAX_VALUE,
                            // 内存中准备清理但是尚未删除的总图片所可用的最大内存数，以字节为单位
                            imageConfig.getMemoryCacheSize(),
                            // 内存中准备清除的图片最大数量
                            Integer.MAX_VALUE,
                            // 内存中单图片的最大大小
                            Integer.MAX_VALUE);
                    return bitmapCacheParams;
                }
            });
        }

        //配置磁盘缓存
        if (imageConfig.getDiskCacheFile() != null) {
            DiskCacheConfig.Builder diakBuilder = DiskCacheConfig.newBuilder(context);
            diakBuilder.setBaseDirectoryPath(imageConfig.getDiskCacheFile().getParentFile());// 磁盘缓存目录路径
            diakBuilder.setBaseDirectoryName(imageConfig.getDiskCacheFile().getName());// 磁盘缓存目录名
            if (imageConfig.getDiskCacheSize() > 0) diakBuilder.setMaxCacheSize(imageConfig.getDiskCacheSize());// 磁盘缓存大小
            imagePipelineConfigBuilder.setMainDiskCacheConfig(diakBuilder.build());
        } else if (imageConfig.isDiskCacheExternal()) {
            DiskCacheConfig.Builder diakBuilder = DiskCacheConfig.newBuilder(context);
            diakBuilder.setBaseDirectoryPath(context.getExternalCacheDir());// 磁盘缓存目录路径
            diakBuilder.setBaseDirectoryName("fresco_image_cache");// 磁盘缓存目录名
            if (imageConfig.getDiskCacheSize() > 0) diakBuilder.setMaxCacheSize(imageConfig.getDiskCacheSize());// 磁盘缓存大小
            imagePipelineConfigBuilder.setMainDiskCacheConfig(diakBuilder.build());
        }

        if (imageConfig.isUseOkhttp()) imagePipelineConfigBuilder.setNetworkFetcher(new OkHttpNetworkFetcher(new OkHttpClient()));

        //当内存紧张时采取的措施
        MemoryTrimmableRegistry memoryTrimmableRegistry = NoOpMemoryTrimmableRegistry.getInstance();
        memoryTrimmableRegistry.registerMemoryTrimmable(new MemoryTrimmable() {
            @Override
            public void trim(MemoryTrimType trimType) {
                final double suggestedTrimRatio = trimType.getSuggestedTrimRatio();

                RingLog.e(String.format("onCreate suggestedTrimRatio : %f", suggestedTrimRatio));
                if (MemoryTrimType.OnCloseToDalvikHeapLimit.getSuggestedTrimRatio() == suggestedTrimRatio || MemoryTrimType.OnSystemLowMemoryWhileAppInBackground
                        .getSuggestedTrimRatio() == suggestedTrimRatio || MemoryTrimType.OnSystemLowMemoryWhileAppInForeground.getSuggestedTrimRatio() == suggestedTrimRatio) {
                    ImagePipelineFactory.getInstance().getImagePipeline().clearMemoryCaches();
                }
            }
        });
        imagePipelineConfigBuilder.setMemoryTrimmableRegistry(memoryTrimmableRegistry);

        /**
         * 在图片解码时根据ResizeOptions所设的宽高的像素进行解码，这样解码出来可以得到一个更小的Bitmap。
         * 必须和ImageRequest的ResizeOptions一起使用，ResizeOptions和DownsampleEnabled参数都不影响原图片的大小，影响的是EncodeImage的大小，
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

        //进行初始化
        Fresco.initialize(context, imagePipelineConfigBuilder.build());
    }

    @Override
    public void loadNet(String url, ImageView imageView) {
        Uri uri = Uri.parse(url);
        load(uri, imageView, null);
    }

    @Override
    public void loadNet(String url, ImageView imageView, LoadOption loadOption) {
        Uri uri = Uri.parse(url);
        load(uri, imageView, loadOption);
    }

    @Override
    public void loadRes(int resId, ImageView imageView) {
        Uri uri = Uri.parse("res:///" + resId);
        load(uri, imageView, null);
    }

    @Override
    public void loadRes(int resId, ImageView imageView, LoadOption loadOption) {
        Uri uri = Uri.parse("res:///" + resId);
        load(uri, imageView, loadOption);
    }

    @Override
    public void loadAsset(String assetName, ImageView imageView) {
        Uri uri = Uri.parse("asset:///" + assetName);
        load(uri, imageView, null);
    }

    @Override
    public void loadAsset(String assetName, ImageView imageView, LoadOption loadOption) {
        Uri uri = Uri.parse("asset:///" + assetName);
        load(uri, imageView, loadOption);
    }

    @Override
    public void loadFile(File file, ImageView imageView) {
        Uri uri = FileUtil.getUriForFile(mContext, file);
        load(uri, imageView, null);
    }

    @Override
    public void loadFile(File file, ImageView imageView, LoadOption loadOption) {
        Uri uri = FileUtil.getUriForFile(mContext, file);
        load(uri, imageView, loadOption);
    }

    @Override
    public void preLoad(String url) {
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(Preconditions.checkNotNull(url, "预加载的图片路径不能为空"))).build();
        Fresco.getImagePipeline().prefetchToBitmapCache(imageRequest, null);
        Fresco.getImagePipeline().prefetchToDiskCache(imageRequest, null);
    }

    @Override
    public void getBitmap(Context context, String url, final ImageListener<Bitmap> imageListener) {
        //参考自https://github.com/hpdx/fresco-helper/blob/master/fresco-helper/src/main/java/com/facebook/fresco/helper/ImageLoader.java
        Uri uri = Uri.parse(url);
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(uri);
        ImageRequest imageRequest = builder.build();
        DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(imageRequest, context);
        dataSource.subscribe(new BaseDataSubscriber<CloseableReference<CloseableImage>>() {
            @Override
            public void onNewResultImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                if (!dataSource.isFinished()) {
                    return;
                }

                CloseableReference<CloseableImage> imageReference = dataSource.getResult();
                if (imageReference != null) {
                    final CloseableReference<CloseableImage> closeableReference = imageReference.clone();
                    try {
                        CloseableImage closeableImage = closeableReference.get();
                        //动图处理
                        if (closeableImage instanceof CloseableAnimatedImage) {
                            AnimatedImageResult animatedImageResult = ((CloseableAnimatedImage) closeableImage).getImageResult();
                            if (animatedImageResult != null && animatedImageResult.getImage() != null) {
                                int imageWidth = animatedImageResult.getImage().getWidth();
                                int imageHeight = animatedImageResult.getImage().getHeight();

                                Bitmap.Config bitmapConfig = Bitmap.Config.ARGB_8888;
                                Bitmap bitmap = Bitmap.createBitmap(imageWidth, imageHeight, bitmapConfig);
                                animatedImageResult.getImage().getFrame(0).renderFrame(imageWidth, imageHeight, bitmap);
                                if (imageListener != null) {
                                    imageListener.onSuccess(bitmap);
                                }
                            }
                        }
                        //非动图处理
                        else if (closeableImage instanceof CloseableBitmap) {
                            CloseableBitmap closeableBitmap = (CloseableBitmap) closeableImage;
                            Bitmap bitmap = closeableBitmap.getUnderlyingBitmap();
                            if (bitmap != null && !bitmap.isRecycled()) {
                                // https://github.com/facebook/fresco/issues/648
                                final Bitmap tempBitmap = bitmap.copy(bitmap.getConfig(), false);
                                if (imageListener != null) {
                                    imageListener.onSuccess(tempBitmap);
                                }
                            }
                        }
                    } finally {
                        imageReference.close();
                        closeableReference.close();
                    }
                }
            }

            @Override
            public void onFailureImpl(DataSource dataSource) {
                Throwable throwable = dataSource.getFailureCause();
                if (imageListener != null) {
                    imageListener.onFail(throwable);
                }
            }
        }, UiThreadImmediateExecutorService.getInstance());
    }

    @Override
    public void downLoadImage(Context context, String url, final File saveFile, final ImageListener<File> imageListener) {
        //参考自https://github.com/hpdx/fresco-helper/blob/master/fresco-helper/src/main/java/com/facebook/fresco/helper/ImageLoader.java
        Uri uri = Uri.parse(url);
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(uri);
        ImageRequest imageRequest = builder.build();

        // 获取未解码的图片数据
        DataSource<CloseableReference<PooledByteBuffer>> dataSource = imagePipeline.fetchEncodedImage(imageRequest, context);
        dataSource.subscribe(new BaseDataSubscriber<CloseableReference<PooledByteBuffer>>() {
            @Override
            public void onNewResultImpl(DataSource<CloseableReference<PooledByteBuffer>> dataSource) {
                if (!dataSource.isFinished()) {
                    return;
                }

                CloseableReference<PooledByteBuffer> imageReference = dataSource.getResult();
                if (imageReference != null) {

                    final CloseableReference<PooledByteBuffer> closeableReference = imageReference.clone();
                    try {
                        PooledByteBuffer pooledByteBuffer = closeableReference.get();
                        InputStream inputStream = new PooledByteBufferInputStream(pooledByteBuffer);
                        OutputStream outputStream = new FileOutputStream(saveFile);

                        if (FileUtil.saveFile(inputStream, outputStream) && imageListener != null) {
                            imageListener.onSuccess(saveFile);
                        }
                    } catch (Exception e) {
                        if (imageListener != null) {
                            imageListener.onFail(e);
                        }
                        e.printStackTrace();
                    } finally {
                        imageReference.close();
                        closeableReference.close();
                    }
                }
            }

            @Override
            public void onProgressUpdate(DataSource<CloseableReference<PooledByteBuffer>> dataSource) {
                int progress = (int) (dataSource.getProgress() * 100);
                RingLog.d("fresco下载图片进度：" + progress);
            }

            @Override
            public void onFailureImpl(DataSource dataSource) {
                Throwable throwable = dataSource.getFailureCause();
                if (imageListener != null) {
                    imageListener.onFail(throwable);
                }
            }
        }, Executors.newSingleThreadExecutor());
    }

    @Override
    public void clearMemoryCache() {
        Fresco.getImagePipeline().clearMemoryCaches();
    }

    @Override
    public void clearDiskCache() {
        Fresco.getImagePipeline().clearDiskCaches();
    }

    private void load(Uri uri, ImageView imageView, LoadOption loadOption) {
        Preconditions.checkNotNull(imageView, "加载图片的控件不能为空！");

        if (imageView instanceof SimpleDraweeView) {
            SimpleDraweeView simpleDraweeView = (SimpleDraweeView) imageView;
            setHierarchay(simpleDraweeView.getHierarchy(), loadOption);
            ImageRequest imageRequest = getImageRequest(uri, simpleDraweeView, loadOption);
            DraweeController draweeController = getController(imageRequest, simpleDraweeView.getController());
            simpleDraweeView.setController(draweeController);
        } else {
            throw new IllegalArgumentException("Fresco加载图片的控件需为SimpleDraweeView");
        }
    }

    //对Hierarchy进行设置，如各种状态下显示的图片
    private void setHierarchay(GenericDraweeHierarchy hierarchy, LoadOption loadOption) {
        mImageConfig = DevRing.ringComponent().imageConfig();
        if (loadOption == null) {
            if (mImageConfig.isShowTransition()) {
                hierarchy.setFadeDuration(600);     //设置由进度条和占位符图片渐变过渡到加载完成的图片所使用的时间间隔
            } else {
                hierarchy.setFadeDuration(0);
            }

            if (mImageConfig.getErrorResId() > 0) {
                hierarchy.setRetryImage(mImageConfig.getErrorResId());  //重新加载图片
                hierarchy.setFailureImage(mImageConfig.getErrorResId(), ScalingUtils.ScaleType.CENTER_CROP); //加载失败的图片
            }

            if (mImageConfig.getLoadingResId() > 0) {
                hierarchy.setPlaceholderImage(mImageConfig.getLoadingResId(), ScalingUtils.ScaleType.CENTER_CROP);//加载中的图片
            }
        } else {
            if (loadOption.isShowTransition()) {
                hierarchy.setFadeDuration(600);     //设置由进度条和占位符图片渐变过渡到加载完成的图片所使用的时间间隔
            } else {
                hierarchy.setFadeDuration(0);
            }

            if (loadOption.getErrorResId() > 0) {
                hierarchy.setRetryImage(loadOption.getErrorResId());  //重新加载图片
                hierarchy.setFailureImage(loadOption.getErrorResId(), ScalingUtils.ScaleType.CENTER_CROP); //加载失败的图片
            }

            if (loadOption.getLoadingResId() > 0) {
                hierarchy.setPlaceholderImage(loadOption.getLoadingResId(), ScalingUtils.ScaleType.CENTER_CROP);//加载中的图片
            }

            if (loadOption.isCircle()) {
                int borderWidth = loadOption.getBorderWidth();
                int borderColor = loadOption.getBorderColor();
                if (borderWidth > 0 && borderColor != 0) {
                    hierarchy.setRoundingParams(RoundingParams.asCircle().setBorder(borderColor, DensityUtil.dp2px(mContext, borderWidth)));
                } else {
                    hierarchy.setRoundingParams(RoundingParams.asCircle());
                }
            } else if (loadOption.getRoundRadius() > 0) {
                hierarchy.setRoundingParams(RoundingParams.fromCornersRadius(loadOption.getRoundRadius()));
            }
        }
        hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);

//        ProgressBarDrawable progressBarDrawable = new ProgressBarDrawable();
//        progressBarDrawable.setBackgroundColor(Color.BLACK);
//        progressBarDrawable.setColor(Color.BLUE);
//        hierarchy.setProgressBarImage(progressBarDrawable);//显示加载进度
    }

    /**
     * 构建、获取ImageRequest
     *
     * @param uri              加载路径
     * @param simpleDraweeView 加载的图片控件
     * @param loadOption       临时加载选项
     * @return ImageRequest
     */
    private ImageRequest getImageRequest(Uri uri, SimpleDraweeView simpleDraweeView, LoadOption loadOption) {

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
        builder.setProgressiveRenderingEnabled(true);



        if (loadOption != null) {
            //图片变换处理
            CombinePostProcessors.Builder processorBuilder = new CombinePostProcessors.Builder();
            if (loadOption.getBlurRadius() > 0) {
                processorBuilder.add(new BlurPostprocessor(mContext, loadOption.getBlurRadius()));
            }
            if (loadOption.isGray()) {
                processorBuilder.add(new GrayscalePostprocessor());
            }
            builder.setPostprocessor(processorBuilder.build());

            if (!loadOption.isUseMemoryCache()) {
                //Fresco貌似不支持禁用内存缓存？
            }
            if (!loadOption.isUseDiskCache()) {
                builder.disableDiskCache();
            }
        }else {
            if (!mImageConfig.isUseMemoryCache()) {
                //Fresco貌似不支持禁用内存缓存？
            }
            if (!mImageConfig.isUseDiskCache()) {
                builder.disableDiskCache();
            }
        }

        return builder.build();
    }

    /**
     * 构建、获取Controller
     *
     * @param request
     * @param oldController
     * @return
     */
    private DraweeController getController(ImageRequest request, @Nullable DraweeController oldController) {
        PipelineDraweeControllerBuilder builder = Fresco.newDraweeControllerBuilder();
        builder.setImageRequest(request);
        builder.setTapToRetryEnabled(false);//设置是否允许加载失败时点击再次加载
        builder.setAutoPlayAnimations(true);//设置是否允许动画图自动播放
        builder.setOldController(oldController);
        return builder.build();
    }

}
