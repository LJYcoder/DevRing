package com.dev.base.view.fragment.base;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dev.base.R;
import com.dev.base.model.net.LifeCycleEvent;
import com.dev.base.util.CommonUtil;
import com.dev.base.view.widget.loadlayout.LoadLayout;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.subjects.PublishSubject;


/**
 * date：     2017/3/3
 * description
 * 1.自定义所有Fragment的基类, 其子类除特殊需求之外不需要再重写相关的生命周期函数
 * 2.简化以及规范了Fragment相关的操作
 * 3.实现了IBaseFragment接口, 可以调用其相关的方法
 * 4.其生命周期函数都在这个类中按照先后顺序列出,不清楚的可以看看
 * 5.实现了IObserver接口,为Fragment提供了两种状态时的操作,onChanged()中加载数据, 在onInvalidated()中取消加载
 * 6.实现了保存状态的功能,若需要则需要重写onFirstTimeLaunched(), onRestoreState(Bundle savedInstanceState),
 * onSaveState(Bundle outState), 这三个方法
 */
public abstract class BaseFragment extends Fragment implements IBaseFragment {

    private static final String SAVED_STATE = "saved_state";

    //各种加载状态的视图，也是根布局视图
    private LoadLayout mLoadLayout;
    //根布局视图
    private View mContentView;
    //保存数据
    private Bundle mSavedState;
    //视图是否已经初始化完毕
    private boolean isViewReady;
    //fragment是否处于可见状态
    private boolean isFragmentVisible;
    //是否已经加载过
    protected boolean isLoaded;
    //用于butterknife解绑
    private Unbinder unbinder;
    //用于控制retrofit的生命周期，以便在destroy或其他状态时终止网络请求
    public final PublishSubject<LifeCycleEvent> lifecycleSubject = PublishSubject.create();

    //用于提供lifecycleSubject到RetrofitUtil中
    public PublishSubject<LifeCycleEvent> getLifeSubject() {
        return lifecycleSubject;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    /**
     * 这个函数用于保存Fragment根视图的引用,避免不必要的重复加载
     * 建议子类重写此方法仅用于创建视图,不要做过多的操作
     *
     * @param inflater           inflater
     * @param container          根视图
     * @param savedInstanceState 保存的数据
     * @return 根视图
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mContentView == null) {
            try {
                mContentView = inflater.inflate(R.layout.fragment_base, container, false);
            } catch (Resources.NotFoundException e) {

            }

            if (mContentView == null) {
                throw new NullPointerException("根布局的id非法导致根布局为空,请检查后重试!");
            }

            View view = inflater.inflate(setContentLayout(), null);
            mLoadLayout = (LoadLayout) mContentView;
            mLoadLayout.addSuccessView(view);

            unbinder = ButterKnife.bind(this, mContentView);

        }
        return mContentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        isViewReady = true;
        if (isViewReady && isFragmentVisible) onFragmentVisiable();

        if (!restoreStateFromArguments()) {
            onFirstTimeLaunched();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        isFragmentVisible = isVisibleToUser;
        if (isViewReady && isFragmentVisible) onFragmentVisiable();

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        lifecycleSubject.onNext(LifeCycleEvent.PAUSE);
        lifecycleSubject.subscribe();
        super.onPause();
    }

    @Override
    public void onStop() {
        lifecycleSubject.onNext(LifeCycleEvent.STOP);
        super.onStop();
    }

    /**
     * 这个函数用于移除根视图
     * 因为已经有过父布局的View是不能再次添加到另一个新的父布局上面的
     */
    @Override
    public void onDestroyView() {
        if (mContentView != null) {
            ViewGroup parent = (ViewGroup) mContentView.getParent();
            if (parent != null) {
                parent.removeView(mContentView);
            }
        }
        isViewReady = false;
        //保存数据
        saveStateToArguments();
        super.onDestroyView();

        if (unbinder != null) unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        lifecycleSubject.onNext(LifeCycleEvent.DESTROY);
        super.onDestroy();

//        RefWatcher refWatcher = KmApplication.getRefWatcher(getActivity());
//        if (refWatcher != null) refWatcher.watch(this);//内存泄露检测
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //保存数据
        saveStateToArguments();
    }

    public LoadLayout getLoadLayout() {
        return mLoadLayout;
    }

    /**
     * 该函数可以Find一个被定义在XML中的根视图上的控件
     *
     * @param id 资源id
     * @return 这个id对应的控件
     */
    @CheckResult
    public View findViewById(@IdRes int id) {
        if (mContentView == null) {
            throw new NullPointerException("请检查你的根布局id合法性或view不为空后再调用此方法!");
        }
        return mContentView.findViewById(id);
    }

    /**
     * 这个方法第一次加载的抽象,需要保存数据就重写这个方法
     */
    protected void onFirstTimeLaunched() {

    }

    /**
     * 恢复状态的方法,若子类保存了状态,子类则需要重写这个方法来恢复状态
     *
     * @param savedInstanceState savedInstanceState
     */
    protected void onRestoreState(@Nullable Bundle savedInstanceState) {

    }

    /**
     * 保存状态的方法抽象,若需要,子类就重写这个方法
     *
     * @param outState outState
     */
    protected void onSaveState(@Nullable Bundle outState) {

    }

    /**
     * @return false第一次加载 true非第一次加载
     */
    private boolean restoreStateFromArguments() {
        Bundle b = getArguments();
        if (b != null) {
            mSavedState = b.getBundle(SAVED_STATE);
            if (mSavedState != null) {
                restoreState();
                return true;
            }
        }
        return false;
    }

    private void restoreState() {
        if (mSavedState != null) {
            onRestoreState(mSavedState);
        }
    }

    private void saveStateToArguments() {
        if (getView() != null) {
            mSavedState = saveState();
        }
        if (mSavedState != null) {
            Bundle b = getArguments();
            if (b != null) {
                b.putBundle(SAVED_STATE, mSavedState);
            }
        }
    }

    private Bundle saveState() {
        Bundle state = new Bundle();
        onSaveState(state);
        return state;
    }

    @Override
    public int getResourceColor(@ColorRes int colorId, @Nullable Resources.Theme theme) {
        return isAdded() ? ResourcesCompat.getColor(getResources(), colorId, null) : 0;
    }

    @Override
    public String getResourceString(@StringRes int stringId) {
        return isAdded() ? getResources().getString(stringId) : null;
    }

    @Override
    public Drawable getResourceDrawable(@DrawableRes int id) {
        return isAdded() ? ResourcesCompat.getDrawable(getResources(), id, null) : null;
    }

    /**
     * 隐藏输入法
     */
    public void hideInput() {
        View view = getActivity().getWindow().peekDecorView();
        CommonUtil.hideSoftInput(getContext(), view);
    }

    /**
     * 这个方法用于返回根布局的id,用于inflate根布局
     */
    @LayoutRes
    protected abstract int setContentLayout();

    protected abstract void initView();

    protected abstract void obtainData();

    protected abstract void initEvent();

    public void onFragmentVisiable() {
        if (!isLoaded) {
            isLoaded = true;
            initView();
            obtainData();
            initEvent();
        }
    }

}
