package com.dev.base.mvp.view.fragment.base;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dev.base.mvp.presenter.base.BasePresenter;
import com.ljy.devring.base.fragment.FragmentLifeCallback;
import com.ljy.devring.base.fragment.IBaseFragment;
import com.ljy.devring.util.Preconditions;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * author:    ljy
 * date：     2018/3/19
 * description： Fragment的基类
 *
 * 此基类进行了懒加载处理、ButterKnife绑定/解绑、Presenter销毁操作。
 *
 * 由于Java的单继承的限制，DevRing库就不提供基类了，所以把一些基类操作通过FragmentManager.FragmentLifecycleCallbacks来完成
 * 但是前提是1)你的Fragment需实现IBaseFragment接口，2)如果你的Activity实现了IBaseActivity，那请确保isUseFragment()返回true。
 *
 * FragmentLifecycleCallbacks进行的基类操作有：（具体请查看 {@link FragmentLifeCallback})
 * 1.Retrofit的生命周期控制
 * 2.EventBus的注册/注销
 * 3.数据的保存与恢复 <a>https://blog.csdn.net/donglynn/article/details/47065999</a>
 *
 * 这种基类实现方式，是从JessYan大神那学来的
 */
public abstract class BaseFragment<P extends BasePresenter> extends Fragment implements IBaseFragment {

    protected Activity mActivity;
    //根布局视图
    private View mContentView;
    //视图是否已经初始化完毕
    private boolean isViewReady;
    //fragment是否处于可见状态
    private boolean isFragmentVisible;
    //是否已经初始化加载过
    protected boolean isLoaded;
    //用于butterknife解绑
    private Unbinder unbinder;
    @Inject
    @Nullable
    protected P mPresenter;

    protected abstract int getContentLayout();
    protected abstract boolean isLazyLoad();
    protected abstract void initView();
    protected abstract void initData();
    protected abstract void initEvent();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mContentView == null) {
            try {
                mContentView = inflater.inflate(getContentLayout(), container, false);
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }

            Preconditions.checkNotNull(mContentView, "根布局的id非法导致根布局为空,请检查后重试!");

//            View view = inflater.inflate(getContentLayout(), null);
//            mLoadLayout = (LoadLayout) mContentView;
//            mLoadLayout.addSuccessView(view);

            unbinder = ButterKnife.bind(this, mContentView);
        }
        return mContentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //视图准备完毕
        isViewReady = true;
        if (!isLazyLoad() || isFragmentVisible) {
            init();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        isFragmentVisible = isVisibleToUser;
        //如果视图准备完毕且Fragment处于可见状态，则开始初始化操作
        if (isLazyLoad() && isViewReady && isFragmentVisible) {
            init();
        }
    }

    public void init() {
        if (!isLoaded) {
            isLoaded = true;
            initView();
            initData();
            initEvent();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //ButterKnife解绑
        if (unbinder != null) unbinder.unbind();
        isViewReady = false;
        isLoaded = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.destroy();
            mPresenter = null;
        }
    }

    @Override
    public void onSaveState(Bundle bundleToSave) {

    }

    @Override
    public void onRestoreState(Bundle bundleToRestore) {

    }

    @Override
    public boolean isUseEventBus() {
        return false;
    }
}
