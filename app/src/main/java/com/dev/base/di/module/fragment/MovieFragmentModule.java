package com.dev.base.di.module.fragment;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

import com.dev.base.mvp.model.MovieModel;
import com.dev.base.mvp.model.imodel.IMovieMoel;
import com.dev.base.mvp.presenter.MoviePresenter;
import com.dev.base.mvp.view.iview.IMovieView;
import com.dev.base.mvp.view.widget.MaterialDialog;
import com.ljy.devring.di.scope.FragmentScope;

import dagger.Module;
import dagger.Provides;

/**
 * author:  ljy
 * date:    2018/3/21
 * description: 对MovieFragment中的相关变量进行初始化
 */
@Module
public class MovieFragmentModule {

    private IMovieView mIMovieView;
    private Context mContext;

    public MovieFragmentModule(IMovieView iMovieView, Context context) {
        mIMovieView = iMovieView;
        mContext = context;
    }

    @Provides
    @FragmentScope
    IMovieView iMovieView() {
        return mIMovieView;
    }

    @Provides
    @FragmentScope
    IMovieMoel iMovieMoel() {
        return new MovieModel();
    }

    @Provides
    @FragmentScope
    Context context() {
        return mContext;
    }

    @Provides
    @FragmentScope
    LinearLayoutManager linearLayoutManager(Context context) {
        return new LinearLayoutManager(context);
    }

    @Provides
    @FragmentScope
    MoviePresenter moviePresenter(IMovieView iMovieView, IMovieMoel iMovieMoel) {
        return new MoviePresenter(iMovieView, iMovieMoel);
    }

    @Provides
    @FragmentScope
    MaterialDialog materialDialog(Context context) {
        return new MaterialDialog(context).setCanceledOnTouchOutside(true).setTitle("提示");
    }
}
