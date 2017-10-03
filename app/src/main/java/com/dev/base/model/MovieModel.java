package com.dev.base.model;

import com.dev.base.app.MyApplication;
import com.dev.base.model.db.MovieCollectDao;
import com.dev.base.model.entity.res.MovieRes;
import com.dev.base.model.entity.table.MovieCollect;
import com.dev.base.model.net.LifeCycleEvent;
import com.dev.base.model.net.HttpSubscriber;
import com.dev.base.model.net.RetrofitUtil;

import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * author:  ljy
 * date:    2017/9/27
 * description:  电影相关的数据处理/提供层
 */

public class MovieModel {

    private MovieCollectDao mMovieCollectDao;//电影收藏表操作类

    public static MovieModel getInstance() {
        return MovieModel.SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final MovieModel instance = new MovieModel();
    }

    private MovieModel() {
        mMovieCollectDao = MyApplication.getDaoSession().getMovieCollectDao();
    }

    /**
     * 获取正在上映的电影
     * @param count 获取的电影数量
     * @param subscriber 请求后的回调
     * @param lifecycleSubject 生命周期触发器
     */
    public void getPlayingMovie(int count, HttpSubscriber<List<MovieRes>> subscriber, PublishSubject<LifeCycleEvent> lifecycleSubject) {
        Observable observable = RetrofitUtil.getApiService().getPlayingMovie(count);//如果需要嵌套请求的话，则在后面加入flatMap进行处理
        RetrofitUtil.composeToSubscribe(observable, subscriber, lifecycleSubject);
    }

    /**
     * 获取即将上映的电影
     * @param count 获取的电影数量
     * @param subscriber 请求后的回调
     * @param lifecycleSubject 生命周期触发器
     */
    public void getCommingMovie(int count, HttpSubscriber<List<MovieRes>> subscriber, PublishSubject<LifeCycleEvent> lifecycleSubject) {
        Observable observable = RetrofitUtil.getApiService().getCommingMovie(count);
        RetrofitUtil.composeToSubscribe(observable, subscriber, lifecycleSubject);
    }

    //加入某部电影到“电影收藏”表
    public void addToMyCollect(MovieCollect movieCollect) {
        mMovieCollectDao.insertOrReplace(movieCollect);
    }

    //从“电影收藏”表中删除某部电影
    public void deleteFromMyCollect(MovieCollect movieCollect) {
        mMovieCollectDao.delete(movieCollect);
    }

    //从“电影收藏”表中获取收藏的数量
    public int getCollectCount() {
        return (int) mMovieCollectDao.count();
    }

    //从“电影收藏”表中获取所有收藏的电影
    public List<MovieCollect> getAllCollect() {
        return mMovieCollectDao.queryBuilder().list();
    }
}
