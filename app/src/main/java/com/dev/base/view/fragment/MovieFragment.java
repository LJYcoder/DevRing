package com.dev.base.view.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dev.base.R;
import com.dev.base.model.entity.res.MovieRes;
import com.dev.base.model.entity.table.MovieCollect;
import com.dev.base.presenter.MoviePresenter;
import com.dev.base.presenter.iview.IMovieView;
import com.dev.base.util.CollectionUtil;
import com.dev.base.util.ToastUtil;
import com.dev.base.view.adapter.MovieAdapter;
import com.dev.base.view.adapter.base.LoadMoreBaseAdapter;
import com.dev.base.view.adapter.base.OnLoadMoreScrollListener;
import com.dev.base.view.fragment.base.BaseFragment;
import com.dev.base.view.widget.MaterialDialog;
import com.dev.base.view.widget.loadlayout.OnLoadListener;
import com.dev.base.view.widget.loadlayout.State;

import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;

/**
 * author:  ljy
 * date:    2017/9/27
 * description:
 */

public class MovieFragment extends BaseFragment implements IMovieView, MovieAdapter.OnMovieClickListener {

    public static final int TYPE_PLAYING = 1;//“正在上映”
    public static final int TYPE_COMMING = 2;//“即将上映”
    private int mCurrentType;

    //请求电影时的不同触发时机
    private static final int INIT = 3;//初始化数据
    private static final int REFRESH = 4;//刷新数据
    private static final int LOADMORE = 5;//加载更多数据

    @BindView(R.id.srl_movie)
    SwipeRefreshLayout mSrlMovie;
    @BindView(R.id.rv_movie)
    RecyclerView mRvMovie;
    MaterialDialog mDialog;

    @BindColor(R.color.lite_blue)
    int colorBlue;
    @BindColor(R.color.green)
    int colorGreen;
    @BindColor(R.color.orange)
    int colorOrange;

    private MoviePresenter mMoviePresenter;//业务逻辑处理层
    private MovieAdapter mMovieAdapter;
    private MovieRes mMovieRes;
    private int mCount = 5;//每次请求的电影数量
    private int mStart = 0;//请求电影的起始点。初始化和刷新时该值为0，而加载更多时，该值需要加上mCount。
    private boolean isLoadingMore;//是否正在进行“加载更多”的操作，避免重复发起请求

    public static MovieFragment newInstance(int type) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        MovieFragment fragment = new MovieFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int setContentLayout() {
        return R.layout.fragment_movie;
    }

    @Override
    protected void initView() {
        //设置下拉刷新的圆环变化颜色，蓝绿橙
        mSrlMovie.setColorSchemeColors(colorBlue, colorGreen, colorOrange);
    }

    @Override
    protected void obtainData() {
        mCurrentType = getArguments().getInt("type", TYPE_PLAYING);
        mMoviePresenter = new MoviePresenter(this);
        mDialog = new MaterialDialog(mActivity);

        //设置“加载”状态时要做的事情
        getLoadLayout().setOnLoadListener(new OnLoadListener() {
            @Override
            public void onLoad() {
                getMovieData(0, INIT);
            }
        });
        //设置页面为“加载”状态
        getLoadLayout().setLayoutState(State.LOADING);
        //发送通知，刷新Toolbar右侧的收藏数量（demo中只有MovieActivity注册了事件接收）
        mMoviePresenter.updateToobarCount();
    }

    @Override
    protected void initEvent() {
        //设置下拉刷新的监听
        mSrlMovie.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMovieData(0, REFRESH);
            }
        });

        //设置上拉加载更多的监听
        mRvMovie.addOnScrollListener(new OnLoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                //进行刷新操作时，屏蔽加载更多操作
                if(mSrlMovie.isRefreshing()) return;


                //如果正在加载更多数据，则不重复发起请求
                if (!isLoadingMore) {
                    isLoadingMore = true;

                    mMovieAdapter.setLoadState(LoadMoreBaseAdapter.LOADING);
                    mStart = mStart + mCount;
                    getMovieData(mStart, LOADMORE);
                }

            }
        });
    }

    /**
     * 请求电影数据
     * @param start 请求电影的起始位置
     * @param type 类型：初始化数据INIT、刷新数据REFRESH、加载更多数据LOADMORE
     */
    public void getMovieData(int start, int type) {
        if (mCurrentType == TYPE_PLAYING) {
            mMoviePresenter.getPlayingMovie(start, mCount, type);
        } else {
            mMoviePresenter.getCommingMovie(start, mCount, type);
        }
    }

    //获取电影数据成功的网络请求回调
    @Override
    public void getMovieSuccess(List<MovieRes> list, int type) {

        mSrlMovie.setRefreshing(false);

        switch (type) {
            //初始化数据
            case INIT:
                mStart = 0;
                if (CollectionUtil.isEmpty(list)) {
                    //数据为空则设置页面为“无数据”状态
                    getLoadLayout().setLayoutState(State.NO_DATA);
                } else {
                    //设置页面为“成功”状态，显示正文布局
                    getLoadLayout().setLayoutState(State.SUCCESS);

                    //传入列表展示
                    mMovieAdapter = new MovieAdapter(mActivity, list, this);
                    mRvMovie.setLayoutManager(new LinearLayoutManager(getContext()));
                    mRvMovie.setHasFixedSize(false);
                    mRvMovie.setAdapter(mMovieAdapter);
                }
                break;

            //刷新数据
            case REFRESH:
                mStart = 0;
                mMovieAdapter.replaceData(list);
                break;

            //加载更多数据
            case LOADMORE:
                isLoadingMore = false;
                if (CollectionUtil.isEmpty(list)) {
                    //全部数据加载完毕
                    mMovieAdapter.setLoadState(LoadMoreBaseAdapter.LOADING_END);
                }else{
                    mMovieAdapter.setLoadState(LoadMoreBaseAdapter.LOADING_COMPLETE);
                    mMovieAdapter.insertItems(list);
                }
                break;
        }

    }

    //获取电影数据失败的网络请求回调
    @Override
    public void getMovieFail(int status, String desc, int type) {

        mSrlMovie.setRefreshing(false);

        switch (type) {
            //初始化数据
            case INIT:
                //设置页面为“失败”状态
                getLoadLayout().setLayoutState(State.FAILED);
                break;

            //刷新数据
            case REFRESH:
                ToastUtil.show("刷新失败：" + desc);
                break;

            //加载更多数据
            case LOADMORE:
                mMovieAdapter.setLoadState(LoadMoreBaseAdapter.LOADING_COMPLETE);
                isLoadingMore = false;
                mStart = mStart - mCount;//请求失败时需回退mStart值，确保下次请求的数据正确
                ToastUtil.show("加载更多失败：" + desc);
                break;
        }

    }


    //点击列表项
    @Override
    public void onMovieClick(MovieRes movieRes) {
        mMovieRes = movieRes;

//        mDialog.setCanceledOnTouchOutside(true);
//        mDialog.setTitle("提示");
        mDialog.setMessage("确定将《" + movieRes.getTitle() + "》加入到我的收藏？");
        mDialog.setPositiveButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                //这里从全局变量mMovieRes获取信息来构建对象，
                //如果从movieRes获取的话，要声明为final类型，但是声明为final类型后，你在这个内部类里面获取到的movieRes都是不变的（一直是第一次获取到的那个,除非重新new Dialog）
                MovieCollect movieCollect = new MovieCollect();
                movieCollect.setId(Long.parseLong(mMovieRes.getId()));
                movieCollect.setMovieImage(mMovieRes.getImages().getMedium());
                movieCollect.setTitle(mMovieRes.getTitle());
                movieCollect.setYear(mMovieRes.getYear());
                //加入“电影收藏”表
                mMoviePresenter.addToMyCollect(movieCollect);
                //发送通知，刷新Toolbar右侧的收藏数量（demo中只有MovieActivity注册了事件接收）
                mMoviePresenter.updateToobarCount();
            }
        });
        mDialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }


    @Override
    public void onDestroy() {

        super.onDestroy();
        //销毁，避免内存泄漏
        if (mMoviePresenter != null) {
            mMoviePresenter.destroy();
        }
    }
}
