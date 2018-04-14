package com.dev.base.mvp.view.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.dev.base.R;
import com.dev.base.di.component.activity.DaggerCollectActivityComponent;
import com.dev.base.di.module.activity.CollectActivityModule;
import com.dev.base.mvp.model.entity.table.MovieCollect;
import com.dev.base.mvp.presenter.CollectPresenter;
import com.dev.base.mvp.view.activity.base.BaseActivity;
import com.dev.base.mvp.view.adapter.CollectAdapter;
import com.dev.base.mvp.view.iview.ICollectView;
import com.dev.base.mvp.view.widget.loadlayout.LoadLayout;
import com.dev.base.mvp.view.widget.loadlayout.OnLoadListener;
import com.dev.base.mvp.view.widget.loadlayout.State;
import com.ljy.devring.util.CollectionUtil;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;

/**
 * author:  ljy
 * date:    2017/9/28
 * description: 电影收藏页面
 */

public class CollectActivity extends BaseActivity<CollectPresenter> implements ICollectView {

    @BindView(R.id.ll_collect)
    LoadLayout mLoadLayout;//加载布局，可以显示各种状态的布局, 如加载中，加载成功, 加载失败, 无数据
    @BindView(R.id.base_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rv_collect)
    RecyclerView mRvCollect;
    @BindString(R.string.my_collect)
    String mStrTitle;
    @Inject
    GridLayoutManager mLayoutManager;
    //由于Adapter的初始化需要list数据源，使用dagger2的话个人感觉不太方便，所以在请求到数据后再初始化
    private CollectAdapter mCollectAdapter;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_collect;
    }

    @Override
    protected void initView(Bundle bundle) {
        //使用Dagger2对本类中相关变量进行初始化
        //如果提示找不到DaggerCollectActivityComponent类，请重新编译下项目。
        DaggerCollectActivityComponent.builder()
                .collectActivityModule(new CollectActivityModule(this, this))
                .build()
                .inject(this);

        //如果调用了setSupportActionBar，那就必须在setSupportActionBar之前将标题置为空字符串，否则设置具体标题会无效
        mToolbar.setTitle("");
        this.setSupportActionBar(mToolbar);
        mToolbar.setTitle(mStrTitle);
    }

    @Override
    protected void initData(Bundle bundle) {
        //设置“加载”状态时要做的事情
        mLoadLayout.setOnLoadListener(new OnLoadListener() {
            @Override
            public void onLoad() {
                mPresenter.getAllCollect();
            }
        });
        //设置页面为“加载”状态
        mLoadLayout.setLayoutState(State.LOADING);
    }

    @Override
    protected void initEvent() {
        //点击toolbar左侧的返回图标则结束页面
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void getCollectSuccess(List<MovieCollect> list) {
        if (CollectionUtil.isEmpty(list)) {
            //数据为空则设置页面为“无数据”状态
            mLoadLayout.setLayoutState(State.NO_DATA);
        } else {
            //设置页面为“成功”状态，显示正文布局
            mLoadLayout.setLayoutState(State.SUCCESS);
            mCollectAdapter = new CollectAdapter(list);
            mRvCollect.setLayoutManager(mLayoutManager);
            mRvCollect.setHasFixedSize(false);
            mRvCollect.setAdapter(mCollectAdapter);
        }
    }

    @Override
    public boolean isUseEventBus() {
        return true;
    }

    //接收事件总线发来的事件
    @org.greenrobot.eventbus.Subscribe //如果使用默认的EventBus则使用此@Subscribe
    @com.dev.base.mvp.model.bus.support.Subscribe //如果使用RxBus则使用此@Subscribe
    public void onDeleteMovie(MovieCollect movieCollect) {
        //从“电影收藏”表中删除该电影
        mPresenter.deleteFromMyCollect(movieCollect);
        //通知MovieActivity刷新ToolBar右侧的收藏数量
        mPresenter.updateMenuCollectCount();
        //如果全部收藏的电影都被用户删除了，则设置页面为“无数据”状态
        if (mPresenter.getCollectCount() == 0) {
            mLoadLayout.setLayoutState(State.NO_DATA);
        }
    }

}
