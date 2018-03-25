package com.dev.base.di.module.activity;

import android.os.Bundle;

import com.dev.base.mvp.presenter.base.BasePresenter;
import com.dev.base.mvp.view.fragment.MovieFragment;
import com.ljy.devring.di.scope.ActivityScope;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * author:  ljy
 * date:    2018/3/21
 * description: 对MovieActivity中的相关变量进行初始化
 */

@Module
public class MovieActivityModule {

    @ActivityScope
    @Provides
    @Named("playing")
    MovieFragment playingMovieFragment() {
        Bundle bundle = new Bundle();
        bundle.putInt("type", MovieFragment.TYPE_PLAYING);
        MovieFragment fragment = new MovieFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @ActivityScope
    @Provides
    @Named("comming")
    MovieFragment commingMovieFragment() {
        Bundle bundle = new Bundle();
        bundle.putInt("type", MovieFragment.TYPE_COMMING);
        MovieFragment fragment = new MovieFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @ActivityScope
    @Provides
    BasePresenter basePresenter() {
        return new BasePresenter() {
        };
    }
}
