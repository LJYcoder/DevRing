package com.ljy.devring.image.support;

/**
 * author:  ljy
 * date:    2018/3/13
 * description: 临时的图片加载配置
 */

public class LoadOption {
    private int mLoadingResId;//加载中状态显示的图片
    private int mErrorResId;//加载失败状态显示的图片
    private boolean mIsShowTransition;//是否开启状态切换时的过渡动画
    private boolean mIsCircle;//是否加载为圆形图片
    private int mBorderWidth;//边框粗细，单位dp，仅在圆形模式下有效
    private int mBorderColor;//边框颜色，仅在圆形模式下有效
    private int mRoundRadius;//加载为圆角图片的圆角值
    private int mBlurRadius;//加载为模糊图片的模糊值
    private boolean mIsGray;//是否加载为灰白图片
    private boolean mIsUseMemoryCache;//是否使用内存缓存
    private boolean mIsUseDiskCache;//是否使用磁盘缓存

    public LoadOption() {
    }

    public LoadOption(int loadingResId, int errorResId) {
        mLoadingResId = loadingResId;
        mErrorResId = errorResId;
    }

    public int getLoadingResId() {
        return mLoadingResId;
    }

    public LoadOption setLoadingResId(int loadingResId) {
        this.mLoadingResId = loadingResId;
        return this;
    }

    public int getErrorResId() {
        return mErrorResId;
    }

    public LoadOption setErrorResId(int errorResId) {
        this.mErrorResId = errorResId;
        return this;
    }

    public boolean isShowTransition() {
        return mIsShowTransition;
    }

    public LoadOption setIsShowTransition(boolean isShowTransition) {
        this.mIsShowTransition = isShowTransition;
        return this;
    }

    public boolean isCircle() {
        return mIsCircle;
    }

    public LoadOption setIsCircle(boolean isCircle) {
        this.mIsCircle = isCircle;
        return this;
    }

    public int getRoundRadius() {
        return mRoundRadius;
    }

    public LoadOption setRoundRadius(int roundRadius) {
        this.mRoundRadius = roundRadius;
        return this;
    }

    public int getBlurRadius() {
        return mBlurRadius;
    }

    public LoadOption setBlurRadius(int blurRadius) {
        this.mBlurRadius = blurRadius;
        return this;
    }

    public boolean isGray() {
        return mIsGray;
    }

    public LoadOption setIsGray(boolean isGray) {
        this.mIsGray = isGray;
        return this;
    }

    public int getBorderWidth() {
        return mBorderWidth;
    }

    public LoadOption setBorderWidth(int borderWidth) {
        this.mBorderWidth = borderWidth;
        return this;
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public LoadOption setBorderColor(int borderColor) {
        this.mBorderColor = borderColor;
        return this;
    }

    public boolean isUseMemoryCache() {
        return mIsUseMemoryCache;
    }

    public LoadOption setIsUseMemoryCache(boolean isUseMemoryCache) {
        this.mIsUseMemoryCache = isUseMemoryCache;
        return this;
    }

    public boolean isUseDiskCache() {
        return mIsUseDiskCache;
    }

    public LoadOption setIsUseDiskCache(boolean isUseDiskCache) {
        this.mIsUseDiskCache = isUseDiskCache;
        return this;
    }
}
