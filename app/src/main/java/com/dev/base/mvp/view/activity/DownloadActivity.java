package com.dev.base.mvp.view.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dev.base.R;
import com.dev.base.di.component.activity.DaggerDownloadActivityComponent;
import com.dev.base.di.module.activity.DownloadActivityModule;
import com.dev.base.mvp.presenter.DownloadPresenter;
import com.dev.base.mvp.view.activity.base.BaseActivity;
import com.dev.base.mvp.view.iview.IDownloadView;
import com.ljy.devring.http.support.body.ProgressInfo;
import com.ljy.devring.util.FileUtil;
import com.ljy.devring.util.RingToast;
import com.ljy.devring.util.RxLifecycleUtil;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.io.File;

import butterknife.BindString;
import butterknife.BindView;


/**
 * author:  ljy
 * date:    2018/3/23
 * description:  下载页面
 */

public class DownloadActivity extends BaseActivity<DownloadPresenter> implements View.OnClickListener,IDownloadView {

    @BindView(R.id.base_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.btn_download)
    Button mBtnDownload;
    @BindView(R.id.pb_download)
    ProgressBar mPbDownload;
    @BindView(R.id.tv_speed)
    TextView mTvSpeed;
    @BindView(R.id.tv_length)
    TextView mTvLength;
    @BindString(R.string.download)
    String mStrTitle;
    @BindString(R.string.download_success)
    String mStrSuccess;

    File mFileSave;//下载内容将保存到此File中

    @Override
    protected int getContentLayout() {
        return R.layout.activity_download;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        //使用Dagger2对本类中相关变量进行初始化
        //如果提示找不到DaggerDownloadActivityComponent类，请重新编译下项目。
        DaggerDownloadActivityComponent.builder().downloadActivityModule(new DownloadActivityModule(this)).build().inject(this);

        mToolbar.setTitle("");
        this.setSupportActionBar(mToolbar);
        mToolbar.setTitle(mStrTitle);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void initEvent() {
        //点击toolbar左侧的返回图标则结束页面
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBtnDownload.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_download:
                //手动终止未完成的下载请求
                RxLifecycleUtil.getActivityLifeSubject(this.toString()).onNext(ActivityEvent.DESTROY);
                //开启新的下载请求
                if (mFileSave == null) {
                    mFileSave = FileUtil.getFile(FileUtil.getExternalCacheDir(this), "wandoujia.apk");
                }
                mPresenter.downloadFile(mFileSave);
                break;
        }
    }

    //下载进度的回调
    @Override
    public void onDownloading(ProgressInfo progressInfo) {
        mPbDownload.setProgress(progressInfo.getPercent());
        mTvSpeed.setText("" + progressInfo.getSpeed() / 1024 + " KB/s");
        mTvLength.setText("" + progressInfo.getContentLength() / 1024 + " KB");
        if (progressInfo.isFinish()) {
            mTvSpeed.setText("");
            mTvLength.setText("");
        }
    }

    @Override
    public void onDownloadSuccess(String filePath) {
        RingToast.show(mStrSuccess + filePath);
        mTvSpeed.setText("");
        mTvLength.setText("");
    }

    @Override
    public void onDownloadFail(long progressInfoId, String errMsg) {
        if (progressInfoId != 0) {
            //下载文件过程中发生异常，一般时读写过程出错，重试即可
            //手动终止未完成的上传请求也会回调这里
            mPbDownload.setProgress(0);
            mTvSpeed.setText("");
            mTvLength.setText("");
        } else {
            RingToast.show(errMsg);
            //下载请求出错。
        }
    }
}
