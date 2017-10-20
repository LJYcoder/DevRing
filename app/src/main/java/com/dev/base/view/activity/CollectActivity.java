package com.dev.base.view.activity;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.dev.base.R;
import com.dev.base.model.entity.table.MovieCollect;
import com.dev.base.presenter.CollectPresenter;
import com.dev.base.util.CollectionUtil;
import com.dev.base.view.activity.base.ToolbarBaseActivity;
import com.dev.base.view.adapter.CollectAdapter;
import com.dev.base.view.widget.loadlayout.OnLoadListener;
import com.dev.base.view.widget.loadlayout.State;

import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * author:  ljy
 * date:    2017/9/28
 * description:
 */

public class CollectActivity extends ToolbarBaseActivity {

    @BindView(R.id.rv_collect)
    RecyclerView mRvCollect;

    private CollectPresenter mCollectPresenter;//电影收藏的业务逻辑处理层
    private CollectAdapter mCollectAdapter;

    @Override
    protected void setContentLayout() {
        setContentView(R.layout.activity_collect);
    }

    @Override
    protected void initView() {
        getToolbar().setTitle(getResourceString(R.string.my_collect));
    }

    @Override
    protected void obtainData() {
        mCollectPresenter = new CollectPresenter();

        //设置“加载”状态时要做的事情
        getLoadLayout().setOnLoadListener(new OnLoadListener() {
            @Override
            public void onLoad() {

                //先从数据库中取出收藏的电影，然后通过页面进行展示。
                //这里涉及从数据库中取数据，可能会是耗时操作，所以进行后台线程与UI线程的切换，使用了RxJava进行切换。
                //当然，这里数据库中数据很少，取数据速度很快，即使不进行线程切换也感受不到区别，但为了规范起见。
                Observable.create(new Observable.OnSubscribe<List<MovieCollect>>() {
                    @Override
                    public void call(Subscriber<? super List<MovieCollect>> subscriber) {
                        List<MovieCollect> list=mCollectPresenter.getAllCollect();
                        subscriber.onNext(list);
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<List<MovieCollect>>() {
                    @Override
                    public void call(List<MovieCollect> listCollect) {
                        if (CollectionUtil.isEmpty(listCollect)) {
                            //数据为空则设置页面为“无数据”状态
                            getLoadLayout().setLayoutState(State.NO_DATA);
                        } else {
                            //设置页面为“成功”状态，显示正文布局
                            getLoadLayout().setLayoutState(State.SUCCESS);
                            mCollectAdapter = new CollectAdapter(getContext(), listCollect);
                            mRvCollect.setLayoutManager(new GridLayoutManager(getContext(), 2));
                            mRvCollect.setHasFixedSize(false);
                            mRvCollect.setAdapter(mCollectAdapter);
                        }
                    }
                });

            }
        });
        //设置页面为“加载”状态
        getLoadLayout().setLayoutState(State.LOADING);

    }

    @Override
    protected void initEvent() {

    }

    public void deleteCollect(MovieCollect movieCollect) {
        //从“电影收藏”表中删除该电影
        mCollectPresenter.deleteFromMyCollect(movieCollect);
        //通知MovieActivity刷新ToolBar右侧的收藏数量
        mCollectPresenter.updateToobarCount();
        //如果全部收藏的电影都被用户删除了，则设置页面为“无数据”状态
        if (mCollectPresenter.getCollectCount() == 0) {
            getLoadLayout().setLayoutState(State.NO_DATA);
        }
    }
}
