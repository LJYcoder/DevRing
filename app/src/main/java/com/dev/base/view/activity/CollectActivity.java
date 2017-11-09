package com.dev.base.view.activity;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.dev.base.R;
import com.dev.base.model.entity.table.MovieCollect;
import com.dev.base.model.net.LifeCycleEvent;
import com.dev.base.presenter.CollectPresenter;
import com.dev.base.util.CollectionUtil;
import com.dev.base.view.activity.base.ToolbarBaseActivity;
import com.dev.base.view.adapter.CollectAdapter;
import com.dev.base.view.widget.loadlayout.OnLoadListener;
import com.dev.base.view.widget.loadlayout.State;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * author:  ljy
 * date:    2017/9/28
 * description: 电影收藏页面
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
                Observable.create(new ObservableOnSubscribe<List<MovieCollect>>() {
                    @Override
                    public void subscribe(ObservableEmitter<List<MovieCollect>> emitter) throws Exception {
                        List<MovieCollect> list=mCollectPresenter.getAllCollect();
                        emitter.onNext(list);
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<MovieCollect>>() {
                    @Override
                    public void accept(List<MovieCollect> listCollect) throws Exception {
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


        //测试内存泄漏
        Observable.interval(2, TimeUnit.SECONDS)
                .compose(this.<Long>controlLife(LifeCycleEvent.DESTROY))//页面销毁时取消订阅，不加这一句则会导致内存泄漏。
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Long aLong) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });


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
