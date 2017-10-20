package com.dev.base.presenter;

import com.dev.base.model.MovieModel;
import com.dev.base.model.entity.eventbus.MovieEvent;
import com.dev.base.model.entity.table.MovieCollect;
import com.dev.base.presenter.base.BasePresenter;
import com.dev.base.util.EventBusUtil;

import java.util.List;

/**
 * author:  ljy
 * date:    2017/9/28
 * description: 电影收藏的业务逻辑处理层
 */

public class CollectPresenter extends BasePresenter {

    private MovieModel mMovieModel;

    public CollectPresenter() {
        mMovieModel = MovieModel.getInstance();
    }

    public void updateToobarCount() {
        EventBusUtil.postMovieEvent(new MovieEvent(getCollectCount()));
    }

    //从“电影收藏”表中删除某个电影
    public void deleteFromMyCollect(MovieCollect movieCollect) {
        mMovieModel.deleteFromMyCollect(movieCollect);
    }

    //从“电影收藏”表中获取所有电影
    public List<MovieCollect> getAllCollect() {
        return mMovieModel.getAllCollect();
    }

    //从“电影收藏表”中获取收藏电影的数量
    public int getCollectCount() {
        return mMovieModel.getCollectCount();
    }

}
