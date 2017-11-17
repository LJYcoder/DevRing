package com.dev.base.view.fragment.base;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
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
import com.dev.base.view.activity.base.BaseActivity;
import com.dev.base.view.widget.loadlayout.LoadLayout;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.PublishSubject;


/**
 * date：     2017/3/3
 * description Fragment基类
 * 继承后该类后，不需要再绑定ButterKnife。当fragment可见时才会进行初始化工作(懒加载)
 * 实现setContentLayout来设置并返回布局ID，
 * 实现initView来做视图相关的初始化，
 * 实现obtainData来做数据的初始化
 * 实现initEvent来做事件监听的初始化
 */
public abstract class BaseFragment extends Fragment implements IBaseFragment {

    private static final String SAVED_STATE = "saved_state";

    protected BaseActivity mActivity;
    //加载布局，可用于设置各种加载状态，也是根布局视图
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
    public PublishSubject<LifeCycleEvent> lifecycleSubject = PublishSubject.create();

    //该方法用于提供lifecycleSubject（相当于实现了IBaseView中的getLifeSubject抽象方法）。
    //方便Presenter中直接通过IBaseView获取lifecycleSubject，而不用每次都作为参数传递过去
    public PublishSubject<LifeCycleEvent> getLifeSubject() {
        return lifecycleSubject;
    }

    //一般的rxjava使用场景下，控制Observable的生命周期
    public <T> ObservableTransformer<T, T> controlLife(final LifeCycleEvent event) {
        return  new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                Observable<LifeCycleEvent> lifecycleObservable = lifecycleSubject.filter(new Predicate<LifeCycleEvent>() {
                    @Override
                    public boolean test(LifeCycleEvent lifeCycleEvent) throws Exception {
                        //当生命周期为event状态时，发射事件
                        return lifeCycleEvent.equals(event);
                    }
                }).take(1);

                return upstream.takeUntil(lifecycleObservable);
            }
        };
    }

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

        //视图准备完毕
        isViewReady = true;
        //如果视图准备完毕且Fragment处于可见状态，则开始初始化操作
        if (isViewReady && isFragmentVisible) onFragmentVisiable();

        //如果之前有保存数据，则恢复数据
        restoreStateFromArguments();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        isFragmentVisible = isVisibleToUser;
        //如果视图准备完毕且Fragment处于可见状态，则开始初始化操作
        if (isViewReady && isFragmentVisible) onFragmentVisiable();

    }

    //设置并返回布局ID
    protected abstract int setContentLayout();
    //做视图相关的初始化
    protected abstract void initView();
    //来做数据的初始化
    protected abstract void obtainData();
    //做事件监听的初始化
    protected abstract void initEvent();

    public void onFragmentVisiable() {
        if (!isLoaded) {
            isLoaded = true;
            initView();
            obtainData();
            initEvent();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (BaseActivity) context;
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
        super.onPause();
    }

    @Override
    public void onStop() {
        lifecycleSubject.onNext(LifeCycleEvent.STOP);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lifecycleSubject.onNext(LifeCycleEvent.DESTROY);

//        RefWatcher refWatcher = MyApplication.getRefWatcher(getActivity());
//        if (refWatcher != null) refWatcher.watch(this);//内存泄露检测
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

        //ButterKnife解绑
        if (unbinder != null) unbinder.unbind();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //保存数据
        saveStateToArguments();
    }

    //获取加载布局，从而设置各种加载状态
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
     * 可重写这个方法来恢复获取之前保存了的数据
     */
    protected void onRestoreState(@Nullable Bundle savedInstanceState) {

    }

    /**
     * 可重写这个方法来保存数据，将要保存的数据放入bundle中
     */
    protected void onSaveState(@Nullable Bundle outState) {

    }

    /**
     * 如果之前有保存数据，则恢复数据
     * @return false表示第一次加载，true表示有保存的数据
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

    //保存数据
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



}
