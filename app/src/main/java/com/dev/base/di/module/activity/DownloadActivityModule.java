package com.dev.base.di.module.activity;

import com.dev.base.mvp.model.DownloadModel;
import com.dev.base.mvp.model.imodel.IDownloadModel;
import com.dev.base.mvp.presenter.DownloadPresenter;
import com.dev.base.mvp.view.iview.IDownloadView;
import com.ljy.devring.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

/**
 * author:  ljy
 * date:    2018/3/23
 * description: 对DownloadActivity中的相关变量进行初始化
 */
@Module
public class DownloadActivityModule {
    private IDownloadView mIDownloadView;

    public DownloadActivityModule(IDownloadView iDownloadView) {
        mIDownloadView = iDownloadView;
    }

    @Provides
    @ActivityScope
    IDownloadView iDownloadView() {
        return mIDownloadView;
    }

    @Provides
    @ActivityScope
    IDownloadModel iDownloadModel() {
        return new DownloadModel();
    }

    @Provides
    @ActivityScope
    DownloadPresenter downloadPresenter(IDownloadView iDownloadView, IDownloadModel iDownloadModel) {
        return new DownloadPresenter(iDownloadView, iDownloadModel);
    }
}
