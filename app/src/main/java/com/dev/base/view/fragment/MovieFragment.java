package com.dev.base.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dev.base.R;
import com.dev.base.model.entity.res.MovieRes;
import com.dev.base.model.entity.table.MovieCollect;
import com.dev.base.presenter.MoviePresenter;
import com.dev.base.presenter.iview.IMovieView;
import com.dev.base.util.CollectionUtil;
import com.dev.base.view.activity.MovieActivity;
import com.dev.base.view.adapter.MovieAdapter;
import com.dev.base.view.fragment.base.BaseFragment;
import com.dev.base.view.widget.MaterialDialog;
import com.dev.base.view.widget.loadlayout.OnLoadListener;
import com.dev.base.view.widget.loadlayout.State;

import java.util.List;

import butterknife.BindView;

/**
 * author:  ljy
 * date:    2017/9/27
 * description:
 */

public class MovieFragment extends BaseFragment implements IMovieView, MovieAdapter.OnMovieClickListener {

    public static final int TYPE_PLAYING = 1;
    public static final int TYPE_COMMING = 2;
    private int mCurrentType;

    @BindView(R.id.rv_movie)
    RecyclerView mRvMovie;
    MaterialDialog mDialog;

    private MovieActivity mActivity;//所属的Activity
    private MoviePresenter mMoviePresenter;//业务逻辑处理层
    private MovieAdapter mMovieAdapter;
    private MovieRes mMovieRes;

    public static MovieFragment newInstance(int type) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        MovieFragment fragment = new MovieFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MovieActivity) context;
    }

    @Override
    protected int setContentLayout() {
        return R.layout.fragment_movie;
    }

    @Override
    protected void initView() {

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
                if (mCurrentType == TYPE_PLAYING) {
                    mMoviePresenter.getPlayingMovie(10);
                } else {
                    mMoviePresenter.getCommingMovie(10);
                }
            }
        });
        //设置页面为“加载”状态
        getLoadLayout().setLayoutState(State.LOADING);
        //发送通知，刷新Toolbar右侧的收藏数量（demo中只有MovieActivity注册了事件接收）
        mMoviePresenter.updateToobarCount();
    }

    @Override
    protected void initEvent() {

    }



    //网络请求成功的回调
    @Override
    public void getMovieSuccess(List<MovieRes> list) {

        if (CollectionUtil.isEmpty(list)) {
            //数据为空则设置页面为“无数据”状态
            getLoadLayout().setLayoutState(State.NO_DATA);
        }else{
            //设置页面为“成功”状态，显示正文布局
            getLoadLayout().setLayoutState(State.SUCCESS);

            //传入列表展示
            mMovieAdapter = new MovieAdapter(mActivity, list, this);
            mRvMovie.setLayoutManager(new LinearLayoutManager(getContext()));
            mRvMovie.setHasFixedSize(false);
            mRvMovie.setAdapter(mMovieAdapter);
        }

    }

    //网络请求失败的回调
    @Override
    public void getMovieFail(int status, String desc) {
        //设置页面为“失败”状态
        getLoadLayout().setLayoutState(State.FAILED);
    }

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
