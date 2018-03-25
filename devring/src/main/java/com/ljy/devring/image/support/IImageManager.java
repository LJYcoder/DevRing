package com.ljy.devring.image.support;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import java.io.File;

/**
 * author:  ljy
 * date:    2018/3/13
 * description: 图片加载管理者的接口
 */

public interface IImageManager {

    void init(Context context, ImageConfig imageConfig);//进行初始化操作，并可从这获取到在Application中配置的图片配置

    void loadNet(String url, ImageView imageView);//加载网络图片到控件

    void loadNet(String url, ImageView imageView, LoadOption loadOption);//加载网络图片到控件，LoadOption提供具体的加载要求

    void loadRes(int resId, ImageView imageView);//加载res资源图片到控件

    void loadRes(int resId, ImageView imageView, LoadOption loadOption);//加载res资源图片到控件，LoadOption提供具体的加载要求

    void loadAsset(String assetName, ImageView imageView);//加载asset资源图片到控件

    void loadAsset(String assetName, ImageView imageView, LoadOption loadOption);//加载asset资源图片到控件，LoadOption提供具体的加载要求

    void loadFile(File file, ImageView imageView);//加载本地图片文件到控件

    void loadFile(File file, ImageView imageView, LoadOption loadOption);//加载本地图片文件到控件，LoadOption提供具体的加载要求

    void preLoad(String url);//预加载网络图片

    void getBitmap(Context context, String url, ImageListener<Bitmap> imageListener);//加载网络图片，并通过imageListener回调返回Bitmap，回调在主线程

    void downLoadImage(Context context, String url, File saveFile, ImageListener<File> imageListener);//下载网络图片，并通过imageListener回调返回下载得到的图片文件，回调在后台线程

    void clearMemoryCache();//清空内存缓存

    void clearDiskCache();//清空磁盘缓存
}
