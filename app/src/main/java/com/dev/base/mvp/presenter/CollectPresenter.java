package com.dev.base.mvp.presenter;

import com.dev.base.mvp.model.entity.table.MovieCollect;
import com.dev.base.mvp.model.imodel.ICollectModel;
import com.dev.base.mvp.presenter.base.BasePresenter;
import com.dev.base.mvp.view.iview.ICollectView;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * author:  ljy
 * date:    2017/9/28
 * description: 电影收藏的Presenter层
 */

public class CollectPresenter extends BasePresenter<ICollectView, ICollectModel> {

    public CollectPresenter(ICollectView iCollectView, ICollectModel iCollectModel) {
        super(iCollectView, iCollectModel);
    }

    //从“电影收藏”表中获取所有电影
    public void getAllCollect() {
        //这里涉及从数据库中取数据，可能会是耗时操作，所以进行后台线程与UI线程的切换，使用了RxJava进行切换。
        //当然，这里数据库中数据很少，取数据速度很快，即使不进行线程切换也感受不到区别，但为了规范起见。
        mIModel.getAllCollect()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<MovieCollect>>() {
                    @Override
                    public void accept(List<MovieCollect> listCollect) throws Exception {
                        if (mIView != null) {
                            mIView.getCollectSuccess(listCollect);
                        }
                    }
                });
    }

    //更新侧滑栏菜单项的收藏数量
    public void updateMenuCollectCount() {
        mIModel.updateMenuCollectCount();
    }

    //从“电影收藏”表中删除某个电影
    public void deleteFromMyCollect(MovieCollect movieCollect) {
        mIModel.deleteFromMyCollect(movieCollect);
    }

    //从“电影收藏表”中获取收藏电影的数量
    public int getCollectCount() {
        return mIModel.getCollectCount();
    }

}
