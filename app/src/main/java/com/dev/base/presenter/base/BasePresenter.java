package com.dev.base.presenter.base;

import com.dev.base.presenter.iview.IBaseView;
import com.dev.base.view.activity.base.BaseActivity;

import java.lang.ref.WeakReference;

/**
 * date：     2017/9/13
 * version    1.0
 * description
 * modify by
 */
public abstract class BasePresenter<T extends IBaseView> {

    protected T mIView;
    protected WeakReference<BaseActivity> mBaseViewActivity;

    public BasePresenter(T t, BaseActivity activity) {
        mIView = t;
        mBaseViewActivity = new WeakReference<>(activity);
    }

    public BasePresenter(BaseActivity activity) {
        mBaseViewActivity = new WeakReference<>(activity);
    }

    public BasePresenter(T t) {
        mIView = t;
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
