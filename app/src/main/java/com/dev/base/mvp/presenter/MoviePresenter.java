package com.dev.base.mvp.presenter;

import com.dev.base.mvp.model.entity.res.HttpResult;
import com.dev.base.mvp.model.entity.res.MovieRes;
import com.dev.base.mvp.model.entity.table.MovieCollect;
import com.dev.base.mvp.model.imodel.IMovieMoel;
import com.dev.base.mvp.presenter.base.BasePresenter;
import com.dev.base.mvp.view.iview.IMovieView;
import com.ljy.devring.DevRing;
import com.ljy.devring.http.support.observer.CommonObserver;
import com.ljy.devring.http.support.throwable.HttpThrowable;
import com.ljy.devring.other.RingLog;
import com.ljy.devring.util.RxLifecycleUtil;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.List;

/**
 * author:  ljy
 * date:    2017/9/27
 * description: 豆瓣电影页面的Presenter层
 */

public class MoviePresenter extends BasePresenter<IMovieView, IMovieMoel> {

    public MoviePresenter(IMovieView iMovieView, IMovieMoel iMovieMoel) {
        super(iMovieView, iMovieMoel);
    }

    /**
     * 获取正在上映的电影
     *
     * @param start 请求电影的起始位置
     * @param count 获取的电影数量
     * @param type  类型：初始化数据INIT、刷新数据REFRESH、加载更多数据LOADMORE
     */
    public void getPlayingMovie(int start, int count, final int type) {
        DevRing.httpManager().commonRequest(mIModel.getPlayingMovie(start, count), new CommonObserver<HttpResult<List<MovieRes>>>() {
            @Override
            public void onResult(HttpResult<List<MovieRes>> result) {
                RingLog.d("获取" + result.getTitle() + "成功");
                if (mIView != null) {
                    mIView.getMovieSuccess(result.getSubjects(), type);
                }
            }

            @Override
            public void onError(HttpThrowable throwable) {
                if (mIView != null) {
                    mIView.getMovieFail(throwable.errorType, throwable.message, type);
                }
            }
        }, RxLifecycleUtil.bindUntilEvent(mIView, FragmentEvent.DESTROY));
    }

    /**
     * 获取即将上映的电影
     *
     * @param start 请求电影的起始位置
     * @param count 获取的电影数量
     * @param type  类型：初始化数据INIT、刷新数据REFRESH、加载更多数据LOADMORE
     */
    public void getCommingMovie(int start, int count, final int type) {
        DevRing.httpManager().commonRequest(mIModel.getCommingMovie(start, count), new CommonObserver<HttpResult<List<MovieRes>>>() {
            @Override
            public void onResult(HttpResult<List<MovieRes>> result) {
                RingLog.d("获取" + result.getTitle() + "成功");
                if (mIView != null) {
                    mIView.getMovieSuccess(result.getSubjects(), type);
                }
            }

            @Override
            public void onError(HttpThrowable throwable) {
                if (mIView != null) {
                    mIView.getMovieFail(throwable.errorType, throwable.message, type);
                }
            }

        }, RxLifecycleUtil.bindUntilEvent(mIView, FragmentEvent.DESTROY));
    }


    //更新侧滑栏菜单项的收藏数量
    public void updateMenuCollectCount() {
        mIModel.updateMenuCollectCount();
    }

    //添加某个电影到“电影收藏”表
    public void addToMyCollect(MovieRes movieRes) {
        MovieCollect movieCollect = new MovieCollect();
        movieCollect.setId(Long.parseLong(movieRes.getId()));
        movieCollect.setMovieImage(movieRes.getImages().getMedium());
        movieCollect.setTitle(movieRes.getTitle());
        movieCollect.setYear(movieRes.getYear());
        mIModel.addToMyCollect(movieCollect);
    }

}
