package com.dev.base.di.component.activity;

import com.dev.base.di.module.activity.UploadActivityModule;
import com.dev.base.mvp.view.activity.UploadActivity;
import com.ljy.devring.di.scope.ActivityScope;

import dagger.Component;

/**
 * author:  ljy
 * date:    2018/3/23
 * description: MovieActivity的Component
 */
@ActivityScope
@Component(modules = UploadActivityModule.class)
public interface UploadActivityComponent {
    void inject(UploadActivity uploadActivity);
}
