package com.dev.base.mvp.view.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dev.base.R;
import com.dev.base.di.component.fragment.DaggerMovieFragmentComponent;
import com.dev.base.di.module.fragment.MovieFragmentModule;
import com.dev.base.mvp.model.entity.res.MovieRes;
import com.dev.base.mvp.presenter.MoviePresenter;
import com.dev.base.mvp.view.adapter.MovieAdapter;
import com.dev.base.mvp.view.adapter.base.LoadMoreBaseAdapter;
import com.dev.base.mvp.view.adapter.base.OnLoadMoreScrollListener;
import com.dev.base.mvp.view.fragment.base.BaseFragment;
import com.dev.base.mvp.view.iview.IMovieView;
import com.dev.base.mvp.view.widget.MaterialDialog;
import com.dev.base.mvp.view.widget.loadlayout.LoadLayout;
import com.dev.base.mvp.view.widget.loadlayout.OnLoadListener;
import com.dev.base.mvp.view.widget.loadlayout.State;
import com.ljy.devring.util.CollectionUtil;
import com.ljy.devring.other.toast.RingToast;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindColor;
import butterknife.BindView;
import dagger.Lazy;

/**
 * author:  ljy
 * date:    2017/9/27
 * description:
 */

public class MovieFragment extends BaseFragment<MoviePresenter> implements IMovieView {

    //当前Fragment页面的类型
    public static final int TYPE_PLAYING = 1;//“正在上映”
    public static final int TYPE_COMMING = 2;//“即将上映”
    private int mCurrentType;

    //请求电影时的不同触发时机
    private static final int INIT = 3;//初始化数据
    private static final int REFRESH = 4;//刷新数据
    private static final int LOADMORE = 5;//加载更多数据

    @BindView(R.id.ll_base)
    LoadLayout mLoadLayout;//加载布局，可以显示各种状态的布局, 如加载中，加载成功, 加载失败, 无数据
    @BindView(R.id.srl_movie)
    SwipeRefreshLayout mSrlMovie;
    @BindView(R.id.rv_movie)
    RecyclerView mRvMovie;
    @BindColor(R.color.lite_blue)
    int colorBlue;
    @BindColor(R.color.green)
    int colorGreen;
    @BindColor(R.color.orange)
    int colorOrange;

    @Inject
    MaterialDialog mDialog;
    @Inject
    Lazy<LinearLayoutManager> mLayoutManager;
    //由于Adapter的初始化需要list数据源，使用dagger2的话个人感觉不太方便，所以在请求到数据后再初始化
    private MovieAdapter mMovieAdapter;
    private MovieRes mMovieRes;
    private int mCount = 5;//每次请求的电影数量
    private int mStart = 0;//请求电影的起始点。初始化和刷新时该值为0，而加载更多时，该值需要加上mCount。
    private boolean isLoadingMore;//是否正在进行“加载更多”的操作，避免重复发起请求

    @Override
    protected boolean isLazyLoad() {
        //使用懒加载
        return true;
    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_movie;
    }

    @Override
    protected void initView() {
        //使用Dagger2对本类中相关变量进行初始化
        //如果提示找不到DaggerMovieFragmentComponent类，请重新编译下项目。
        DaggerMovieFragmentComponent.builder().movieFragmentModule(new MovieFragmentModule(this, mActivity)).build().inject(this);

        //设置下拉刷新的圆环变化颜色，蓝绿橙
        mSrlMovie.setColorSchemeColors(colorBlue, colorGreen, colorOrange);
    }

    @Override
    protected void initData() {
        //获取当前Fragment页面的类型
        mCurrentType = getArguments().getInt("type", TYPE_PLAYING);

        //设置“加载”状态时要做的事情
        mLoadLayout.setOnLoadListener(new OnLoadListener() {
            @Override
            public void onLoad() {
                getMovieData(0, INIT);
            }
        });
        //设置页面为“加载”状态
        mLoadLayout.setLayoutState(State.LOADING);
        //发送通知，刷新Toolbar右侧的收藏数量（demo中只有MovieActivity注册了事件接收）
        mPresenter.updateMenuCollectCount();
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
                if (mSrlMovie.isRefreshing()) return;

                //如果正在加载更多数据，则不重复发起请求
                if (!isLoadingMore) {
                    isLoadingMore = true;

                    mMovieAdapter.setLoadState(LoadMoreBaseAdapter.LOADING);
                    mStart = mStart + mCount;
                    getMovieData(mStart, LOADMORE);
                }
            }
        });

        mDialog.setPositiveButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                //加入“电影收藏”表
                mPresenter.addToMyCollect(mMovieRes);
                //发送事件，刷新Toolbar右侧的收藏数量
                mPresenter.updateMenuCollectCount();
            }
        });
        mDialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
    }

    /**
     * 请求电影数据
     *
     * @param start 请求电影的起始位置
     * @param type  类型：初始化数据INIT、刷新数据REFRESH、加载更多数据LOADMORE
     */
    public void getMovieData(int start, int type) {
        if (mCurrentType == TYPE_PLAYING) {
            mPresenter.getPlayingMovie(start, mCount, type);
        } else {
            mPresenter.getCommingMovie(start, mCount, type);
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
                    mLoadLayout.setLayoutState(State.NO_DATA);
                } else {
                    //设置页面为“成功”状态，显示正文布局
                    mLoadLayout.setLayoutState(State.SUCCESS);

                    //传入列表展示
                    mMovieAdapter = new MovieAdapter(mCurrentType, list);
                    mRvMovie.setLayoutManager(mLayoutManager.get());
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
                } else {
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
                mLoadLayout.setLayoutState(State.FAILED);
                break;

            //刷新数据
            case REFRESH:
                RingToast.show("刷新失败：" + desc);
                break;

            //加载更多数据
            case LOADMORE:
                mMovieAdapter.setLoadState(LoadMoreBaseAdapter.LOADING_COMPLETE);
                isLoadingMore = false;
                mStart = mStart - mCount;//请求失败时需回退mStart值，确保下次请求的数据正确
                RingToast.show("加载更多失败：" + desc);
                break;
        }
    }


    @Override
    public boolean isUseEventBus() {
        return true;
    }

    //接收事件总线发来的事件
    @org.greenrobot.eventbus.Subscribe //如果使用默认的EventBus则使用此@Subscribe
    @com.dev.base.mvp.model.bus.support.Subscribe //如果使用RxBus则使用此@Subscribe
    public void onCollectMovie(MovieRes movieRes) {
        //由于“最近上映”和“即将上映”共用一个Fragment，所以需要判断类型来区分事件
        if (movieRes.getMovieType() == mCurrentType) {
            mMovieRes = movieRes;
            mDialog.setMessage("确定将《" + mMovieRes.getTitle() + "》加入到我的收藏？");
            mDialog.show();
        }
    }

}
