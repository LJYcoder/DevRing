package com.dev.base.presenter;

import com.dev.base.model.MovieModel;
import com.dev.base.model.entity.res.MovieRes;
import com.dev.base.model.entity.table.MovieCollect;
import com.dev.base.model.net.HttpSubscriber;
import com.dev.base.presenter.base.BasePresenter;
import com.dev.base.presenter.iview.IMovieView;
import com.dev.base.util.log.LogUtil;

import java.util.List;

/**
 * author:  ljy
 * date:    2017/9/27
 * description: 电影页面的业务逻辑处理层
 */

public class MoviePresenter extends BasePresenter<IMovieView> {

    private static final String TAG= MoviePresenter.class.getSimpleName();
    private MovieModel mMovieModel;

    public MoviePresenter(IMovieView iMovieView) {
        super(iMovieView);
        mMovieModel = MovieModel.getInstance();
    }

    /**
     * 获取正在上映的电影
     * @param count 获取的电影数量
     */
    public void getPlayingMovie(int count) {
        mMovieModel.getPlayingMovie(count, new HttpSubscriber<List<MovieRes>>() {
            @Override
            public void onNext(String title, List<MovieRes> list) {
                LogUtil.d(TAG,"获取"+title+"成功");
                if (mIView != null) {
                    mIView.getMovieSuccess(list);
                }
            }

            @Override
            public void onError(int errType, String errMessage) {
                if (mIView != null) {
                    mIView.getMovieFail(errType, errMessage);
                }
            }
        }, mIView.getLifeSubject());
    }

    /**
     * 获取即将上映的电影
     * @param count 获取的电影数量
     */
    public void getCommingMovie(int count) {
        mMovieModel.getCommingMovie(count, new HttpSubscriber<List<MovieRes>>() {
            @Override
            public void onNext(String title, List<MovieRes> list) {
                LogUtil.d(TAG,"获取"+title+"成功");
                if (mIView != null) {
                    mIView.getMovieSuccess(list);
                }
            }

            @Override
            public void onError(int errType, String errMessage) {
                if (mIView != null) {
                    mIView.getMovieFail(errType, errMessage);
                }
            }
        },mIView.getLifeSubject());
    }

    //添加某个电影到“电影收藏”表
    public void addToMyCollect(MovieCollect movieCollect) {
        mMovieModel.addToMyCollect(movieCollect);
    }

    //从“电影收藏表”中获取收藏电影的数量
    public int getCollectCount() {
        return mMovieModel.getCollectCount();
    }
}
