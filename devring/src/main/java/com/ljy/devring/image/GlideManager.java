package com.ljy.devring.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.ljy.devring.DevRing;
import com.ljy.devring.image.support.CircleBorderTransformation;
import com.ljy.devring.image.support.IImageManager;
import com.ljy.devring.image.support.ImageConfig;
import com.ljy.devring.image.support.ImageListener;
import com.ljy.devring.image.support.LoadOption;
import com.ljy.devring.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.GrayscaleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * author:  ljy
 * date:    2018/3/14
 * description: Glide图片加载管理者
 *
 * https://www.jianshu.com/p/2942a57401eb
 */

public class GlideManager implements IImageManager {

    private Context mContext;
    private ImageConfig mImageConfig;
    private ExecutorService cacheThreadPool;

    @Override
    public void init(Context context, ImageConfig imageConfig) {
        mContext = context;
        mImageConfig = imageConfig;
    }

    @Override
    public void loadNet(String url, ImageView imageView) {
        load(Glide.with(imageView.getContext()).load(url), null).into(imageView);
    }

    @Override
    public void loadNet(String url, ImageView imageView, LoadOption loadOption) {
        load(Glide.with(imageView.getContext()).load(url), loadOption).into(imageView);
    }

    @Override
    public void loadRes(int resId, ImageView imageView) {
        load(Glide.with(imageView.getContext()).load(resId), null).into(imageView);
    }

    @Override
    public void loadRes(int resId, ImageView imageView, LoadOption loadOption) {
        load(Glide.with(imageView.getContext()).load(resId), loadOption).into(imageView);
    }

    @Override
    public void loadAsset(String assetName, ImageView imageView) {
        load(Glide.with(imageView.getContext()).load("file:///android_asset/" + assetName), null).into(imageView);
    }

    @Override
    public void loadAsset(String assetName, ImageView imageView, LoadOption loadOption) {
        load(Glide.with(imageView.getContext()).load("file:///android_asset/" + assetName), loadOption).into(imageView);
    }

    @Override
    public void loadFile(File file, ImageView imageView) {
        load(Glide.with(imageView.getContext()).load(file), null).into(imageView);
    }

    @Override
    public void loadFile(File file, ImageView imageView, LoadOption loadOption) {
        load(Glide.with(imageView.getContext()).load(file), loadOption).into(imageView);
    }

    @Override
    public void preLoad(String url) {
        Glide.with(mContext).load(url).preload();
    }

    @Override
    public void getBitmap(Context context, String url, final ImageListener<Bitmap> imageListener) {
        Glide.with(context).asBitmap().load(url).listener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                if (imageListener != null) {
                    imageListener.onFail(e);
                }
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                if (imageListener != null) {
                    imageListener.onSuccess(resource);
                }
                return false;
            }
        }).into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
    }

    @Override
    public void downLoadImage(final Context context, final String url, final File targetFile, final ImageListener<File> imageListener) {
        if (cacheThreadPool == null) {
            cacheThreadPool = Executors.newCachedThreadPool();
        }

        cacheThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    File sourceFile = Glide.with(context).asFile().load(url).submit().get();
                    if (FileUtil.copyFile(sourceFile, targetFile) && imageListener != null) {
                        imageListener.onSuccess(targetFile);//回调在后台线程
                    }
                } catch (Exception exception) {
                    if (imageListener != null) {
                        imageListener.onFail(exception);//回调在后台线程
                    }
                }
            }
        });
    }

    @Override
    public void clearMemoryCache() {
        //Glide要求清除内存缓存需在主线程执行
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Glide.get(mContext).clearMemory();
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Glide.get(mContext).clearMemory();
                }
            });
        }
    }

    @Override
    public void clearDiskCache() {
        //Glide要求清除内存缓存需在后台程执行
        if (Looper.myLooper() == Looper.getMainLooper()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Glide.get(mContext).clearDiskCache();
                }
            }).start();
        } else {
            Glide.get(mContext).clearDiskCache();
        }
    }

    private RequestBuilder load(RequestBuilder requestBuilder, LoadOption loadOption) {

        RequestOptions requestOptions = new RequestOptions();

        mImageConfig = DevRing.ringComponent().imageConfig();
        //使用全局的配置进行设置
        if (loadOption == null) {
            if (mImageConfig.isShowTransition()) {
                requestBuilder.transition(DrawableTransitionOptions.withCrossFade(600));
            }

            if (mImageConfig.getLoadingResId() > 0) {
                requestOptions.placeholder(mImageConfig.getLoadingResId());
            }

            if (mImageConfig.getErrorResId() > 0) {
                requestOptions.error(mImageConfig.getErrorResId());
            }

            requestOptions.skipMemoryCache(!mImageConfig.isUseMemoryCache());
            if (mImageConfig.isUseDiskCache()) {
                requestOptions.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
            }else {
                requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
            }
        }
        //使用临时的配置进行设置
        else {
            if (loadOption.isShowTransition()) {
                requestBuilder.transition(DrawableTransitionOptions.withCrossFade(600));
            }

            if (loadOption.getLoadingResId() > 0) {
                requestOptions.placeholder(loadOption.getLoadingResId());
            }

            if (loadOption.getErrorResId() > 0) {
                requestOptions.error(loadOption.getErrorResId());
            }

            requestOptions.skipMemoryCache(!loadOption.isUseMemoryCache());
            if (loadOption.isUseDiskCache()) {
                requestOptions.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
            }else {
                requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
            }

            CircleBorderTransformation circleTransformation = null;
//            CropCircleTransformation circleTransformation = null;
            RoundedCornersTransformation roundedCornersTransformation = null;
            BlurTransformation blurTransformation = null;
            GrayscaleTransformation grayscaleTransformation = null;

            if (loadOption.isCircle()) {
//                circleTransformation = new CropCircleTransformation();
                int borderWidth = loadOption.getBorderWidth();
                int borderColor = loadOption.getBorderColor();
                if (borderWidth > 0 && borderColor != 0) {
                    circleTransformation = new CircleBorderTransformation(borderWidth, borderColor);
                }else{
                    circleTransformation = new CircleBorderTransformation();
                }
            } else if (loadOption.getRoundRadius() > 0) {
                roundedCornersTransformation = new RoundedCornersTransformation(loadOption.getRoundRadius(), 0);
            }

            if (loadOption.getBlurRadius() > 0) {
                blurTransformation = new BlurTransformation(loadOption.getBlurRadius());
            }

            if (loadOption.isGray()) {
                grayscaleTransformation = new GrayscaleTransformation();
            }

            MultiTransformation multiTransformation = getMultiTransformation(circleTransformation, roundedCornersTransformation, blurTransformation, grayscaleTransformation);
            if (multiTransformation != null) requestOptions.transform(multiTransformation);
        }
        return requestBuilder.apply(requestOptions);
    }

    private MultiTransformation getMultiTransformation(Transformation... transformations) {
        List<Transformation> list = new ArrayList<>();

        for (int i = 0; i < transformations.length; i++) {
            if (transformations[i] != null) list.add(transformations[i]);
        }

        if (list.isEmpty()) {
            return null;
        } else {
            return new MultiTransformation(list);
        }
    }


}
