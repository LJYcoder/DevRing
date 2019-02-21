package com.api.demo.image;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.api.demo.R;
import com.ljy.devring.DevRing;
import com.ljy.devring.base.activity.IBaseActivity;
import com.ljy.devring.image.support.ImageListener;
import com.ljy.devring.image.support.LoadOption;
import com.ljy.devring.other.toast.RingToast;
import com.ljy.devring.util.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * author:  ljy
 * date:    2018/12/8
 * description: 演示图片加载模块的使用
 *
 * DevRing使用文档：<a>https://www.jianshu.com/p/abede6623c58</a>
 * Glide博客介绍：<a>https://www.jianshu.com/p/2942a57401eb</a>
 * Fresco博客介绍：<a>https://www.jianshu.com/p/5b5625612f56</a>
 */

public class ImageActivity extends AppCompatActivity implements IBaseActivity{

    @BindView(R.id.iv_common)
    ImageView mIvCommon;
    @BindView(R.id.iv_option)
    ImageView mIvOption;
    @BindView(R.id.iv_bitmap)
    ImageView mIvBitmap;
    @BindView(R.id.iv_download)
    ImageView mIvDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ButterKnife.bind(this);
        setTitle("图片模块");
        initFile();//复制assets中的图片到本地以便演示加载本地图片
    }

    @OnClick({R.id.btn_net, R.id.btn_res, R.id.btn_assets, R.id.btn_local, R.id.btn_circle, R.id.btn_round, R.id.btn_blur, R.id.btn_gray, R.id.btn_circle_border, R.id
            .btn_bitmap, R.id.btn_download, R.id.btn_preload, R.id.btn_memory_cache, R.id.btn_disk_cache})
    protected void onClick(View view) {
        switch (view.getId()) {
            //加载网络图片
            case R.id.btn_net:
                String url = "https://b-ssl.duitang.com/uploads/item/201301/11/20130111200721_h4Ba2.jpeg";
                DevRing.imageManager().loadNet(url, mIvCommon);
                break;

            //加载Res资源图片
            case R.id.btn_res:
                DevRing.imageManager().loadRes(R.mipmap.image4, mIvCommon);
                break;

            //加载assets资源图片
            case R.id.btn_assets:
                DevRing.imageManager().loadAsset("image1.jpg", mIvCommon);
                break;

            //加载本地图片
            case R.id.btn_local:
                File file = FileUtil.getFile(FileUtil.getExternalCacheDir(ImageActivity.this), "image2.jpg");
                DevRing.imageManager().loadFile(file, mIvCommon);
                break;

            //指定图片加载为圆形
            case R.id.btn_circle:
                DevRing.imageManager().loadRes(R.mipmap.image5, mIvOption, new LoadOption().setIsCircle(true));
                break;

            //指定图片加载为圆角
            case R.id.btn_round:
                DevRing.imageManager().loadRes(R.mipmap.image5, mIvOption, new LoadOption().setRoundRadius(15));
                break;

            //指定图片加载为模糊效果
            case R.id.btn_blur:
                DevRing.imageManager().loadRes(R.mipmap.image4, mIvOption, new LoadOption().setBlurRadius(10));
                break;

            //指定图片加载为灰白效果
            case R.id.btn_gray:
                DevRing.imageManager().loadRes(R.mipmap.image4, mIvOption, new LoadOption().setIsGray(true));
                break;

            //指定图片边框的粗细以及颜色（目前边框仅适用于圆形条件下）
            case R.id.btn_circle_border:
                LoadOption loadOption = new LoadOption();
                loadOption.setIsCircle(true).setBorderColor(getResources().getColor(R.color.colorPrimary)).setBorderWidth(4);
                DevRing.imageManager().loadRes(R.mipmap.image4, mIvOption, loadOption);
                break;

            //获取网络图片的Bitmap对象
            case R.id.btn_bitmap:
                String urlBitmap = "https://b-ssl.duitang.com/uploads/item/201212/26/20121226214400_Xzruw.jpeg";
                DevRing.imageManager().getBitmap(this, urlBitmap, new ImageListener<Bitmap>() {
                    @Override
                    public void onSuccess(Bitmap result) {
                        mIvBitmap.setImageBitmap(result);
                    }

                    @Override
                    public void onFail(Throwable throwable) {
                        RingToast.show(throwable.getMessage());
                    }
                });
                break;

            //下载图片到本地指定位置
            case R.id.btn_download:
                String urlDownload = "https://b-ssl.duitang.com/uploads/item/201410/06/20141006132058_2NFw4.jpeg";
                File fileDownload = FileUtil.getFile(FileUtil.getExternalCacheDir(this), "image_download.jpg");
                DevRing.imageManager().downLoadImage(this, urlDownload, fileDownload, new ImageListener<File>() {
                    @Override
                    public void onSuccess(final File result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DevRing.imageManager().loadFile(result, mIvDownload);
                            }
                        });
                    }

                    @Override
                    public void onFail(Throwable throwable) {
                        RingToast.show(throwable.getMessage());
                    }
                });
                break;

            //预加载图片
            case R.id.btn_preload:
                DevRing.imageManager().preLoad("https://b-ssl.duitang.com/uploads/item/201212/27/20121227182413_LfdWX.jpeg");
                break;

            //清空内存缓存
            case R.id.btn_memory_cache:
                DevRing.imageManager().clearMemoryCache();
                break;

            //清空磁盘缓存
            case R.id.btn_disk_cache:
                DevRing.imageManager().clearDiskCache();
                break;
        }
    }

    public void initFile() {
        //复制assets中的图片到本地
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = FileUtil.getFile(FileUtil.getExternalCacheDir(ImageActivity.this), "image2.jpg");
                    InputStream in = getAssets().open("image2.jpg");
                    FileOutputStream out = new FileOutputStream(file);
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
    public boolean isUseEventBus() {
        return false;
    }

    @Override
    public boolean isUseFragment() {
        return false;
    }
}
