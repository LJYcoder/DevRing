package com.dev.base.mvp.presenter.base;

import com.dev.base.mvp.model.imodel.IBaseModel;
import com.dev.base.mvp.view.iview.IBaseView;

/**
 * author:  ljy
 * date：   2018/3/18
 * description: Presenter基类
 */
public abstract class BasePresenter<V extends IBaseView,M extends IBaseModel>{

    protected V mIView;
    protected M mIModel;

    public BasePresenter(V iView,M iModel) {
        mIView = iView;
        mIModel = iModel;
    }

    public BasePresenter(V iView) {
        mIView = iView;
    }

    public BasePresenter() {
    }

    /**
     * 释放引用，防止内存泄露
     */
    public void destroy() {
        mIView = null;
    }
}
