package com.dev.base.presenter;

import com.dev.base.model.MovieModel;
import com.dev.base.model.entity.eventbus.MovieEvent;
import com.dev.base.model.entity.res.MovieRes;
import com.dev.base.model.entity.table.MovieCollect;
import com.dev.base.model.net.HttpFileObserver;
import com.dev.base.model.net.HttpObserver;
import com.dev.base.presenter.base.BasePresenter;
import com.dev.base.presenter.iview.IMovieView;
import com.dev.base.util.EventBusUtil;
import com.dev.base.util.log.LogUtil;

import java.io.File;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * author:  ljy
 * date:    2017/9/27
 * description: 电影页面的业务逻辑处理层
 */

public class MoviePresenter extends BasePresenter<IMovieView> {

    private static final String TAG = MoviePresenter.class.getSimpleName();
    private MovieModel mMovieModel;

    public MoviePresenter(IMovieView iMovieView) {
        super(iMovieView);
        mMovieModel = MovieModel.getInstance();
    }

    /**
     * 获取正在上映的电影
     *
     * @param start 请求电影的起始位置
     * @param count 获取的电影数量
     * @param type 类型：初始化数据INIT、刷新数据REFRESH、加载更多数据LOADMORE
     */
    public void getPlayingMovie(int start, int count, final int type) {
        mMovieModel.getPlayingMovie(start, count, new HttpObserver<List<MovieRes>>() {
            @Override
            public void onNext(String title, List<MovieRes> list) {
                LogUtil.d(TAG, "获取" + title + "成功");
                if (mIView != null) {
                    mIView.getMovieSuccess(list, type);
                }
            }

            @Override
            public void onError(int errType, String errMessage) {
                if (mIView != null) {
                    mIView.getMovieFail(errType, errMessage, type);
                }
            }

        }, mIView.getLifeSubject());
    }

    /**
     * 获取即将上映的电影
     *
     * @param start 请求电影的起始位置
     * @param count 获取的电影数量
     * @param type 类型：初始化数据INIT、刷新数据REFRESH、加载更多数据LOADMORE
     */
    public void getCommingMovie(int start, int count, final int type) {
        mMovieModel.getCommingMovie(start, count, new HttpObserver<List<MovieRes>>() {
            @Override
            public void onNext(String title, List<MovieRes> list) {
                LogUtil.d(TAG, "获取" + title + "成功");
                if (mIView != null) {
                    mIView.getMovieSuccess(list, type);
                }
            }

            @Override
            public void onError(int errType, String errMessage) {
                if (mIView != null) {
                    mIView.getMovieFail(errType, errMessage, type);
                }
            }
        }, mIView.getLifeSubject());
    }

    public void updateToobarCount() {
        EventBusUtil.postMovieEvent(new MovieEvent(getCollectCount()));
    }

    //添加某个电影到“电影收藏”表
    public void addToMyCollect(MovieCollect movieCollect) {
        mMovieModel.addToMyCollect(movieCollect);
    }

    //从“电影收藏表”中获取收藏电影的数量
    public int getCollectCount() {
        return mMovieModel.getCollectCount();
    }


    /**
     * 下载文件（demo中并没实际运用到，仅供参考）
     *
     * @param file 目标文件，下载的电影将保存到该文件中
     */
    public void downloadFile(File file) {
        mMovieModel.downLoadFile(new HttpFileObserver() {

            //请求发起前进行的操作，onSubscribe是执行在subscribe()被调用时的线程
            //这里显示进度条属于UI操作，所以要保证subscribe()是在UI主线程里调用
            @Override
            public void onSubscribe(Disposable d) {
                super.onSubscribe(d);
                //显示下载进度条
            }

            //下载操作完成
            @Override
            public void onNext(boolean isFileSaveSuccess) {
                //隐藏下载进度条
                //文件成功保存到本地
                if (isFileSaveSuccess) {
                    //通知页面下载完成，
                }
                //文件保存到本地的过程中异常
                else {
                    //通知页面进行相应展示
                }

            }

            //请求异常
            @Override
            public void onError(int errType, String errMessage) {
                //通知页面进行相应展示，如隐藏进度条
            }
        }, mIView.getLifeSubject(), file);
    }


}
