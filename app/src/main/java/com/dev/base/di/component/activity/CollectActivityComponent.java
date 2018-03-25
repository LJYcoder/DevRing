package com.dev.base.di.component.activity;

import com.dev.base.di.module.activity.CollectActivityModule;
import com.dev.base.mvp.view.activity.CollectActivity;
import com.ljy.devring.di.scope.ActivityScope;

import dagger.Component;

/**
 * author:  ljy
 * date:    2018/3/21
 * description: CollectActivity的Component
 */
@ActivityScope
@Component(modules = CollectActivityModule.class)
public interface CollectActivityComponent {
    void inject(CollectActivity collectActivity);
}
