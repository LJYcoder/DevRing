package com.ljy.devring.base.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;

import com.ljy.devring.DevRing;
import com.trello.rxlifecycle2.android.FragmentEvent;

import io.reactivex.subjects.PublishSubject;

/**
 * author:  ljy
 * date:    2018/3/19
 * description: 负责Fragment各生命周期处理，相当于基类的功能
 */

public class FragmentLife implements IFragmentLife {

    private static final String SAVED_STATE = "saved_state";
    private final PublishSubject<FragmentEvent> mLifecycleSubject = PublishSubject.create();

    private Fragment mFragment;
    private View mContentView;
    private Bundle mSavedState;//用于保存/恢复数据

    @Override
    public void onAttach(Fragment fragment, Context context) {
        mFragment = fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mLifecycleSubject.onNext(FragmentEvent.CREATE);
        if (((IBaseFragment) mFragment).isUseEventBus()) {
            DevRing.busManager().register(mFragment);
        }
    }

    @Override
    public void onCreateView(View view, Bundle savedInstanceState) {
        mLifecycleSubject.onNext(FragmentEvent.CREATE_VIEW);
        mContentView = view;
    }

    @Override
    public void onActivityCreate(Bundle savedInstanceState) {
        restoreStateFromArguments();
    }

    @Override
    public void onStart() {
        mLifecycleSubject.onNext(FragmentEvent.START);
    }

    @Override
    public void onResume() {
        mLifecycleSubject.onNext(FragmentEvent.RESUME);
    }

    @Override
    public void onPause() {
        mLifecycleSubject.onNext(FragmentEvent.PAUSE);
    }

    @Override
    public void onStop() {
        mLifecycleSubject.onNext(FragmentEvent.STOP);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        saveStateToArguments();
    }

    @Override
    public void onDestroyView() {
        mLifecycleSubject.onNext(FragmentEvent.DESTROY_VIEW);

        if (mContentView != null) {
            ViewGroup parent = (ViewGroup) mContentView.getParent();
            if (parent != null) {
                parent.removeView(mContentView);
            }
        }

        saveStateToArguments();
    }

    @Override
    public void onDestroy() {
        mLifecycleSubject.onNext(FragmentEvent.DESTROY);

        if (((IBaseFragment) mFragment).isUseEventBus()) {
            DevRing.busManager().unregister(mFragment);
        }
        mContentView = null;
        mFragment = null;
    }

    @Override
    public void onDetach() {
        mLifecycleSubject.onNext(FragmentEvent.DETACH);
    }

    @Override
    public boolean isAdded() {
        return mFragment != null && mFragment.isAdded();
    }

    private void saveStateToArguments() {
        if (mFragment.getView() != null) {
            Bundle state = new Bundle();
            ((IBaseFragment) mFragment).onSaveState(state);
            mSavedState = state;
        }
        if (mSavedState != null) {
            Bundle b = mFragment.getArguments();
            if (b != null) {
                b.putBundle(SAVED_STATE, mSavedState);
            }
        }
    }

    private void restoreStateFromArguments() {
        Bundle b = mFragment.getArguments();
        if (b != null) {
            mSavedState = b.getBundle(SAVED_STATE);
            if (mSavedState != null) {
                ((IBaseFragment) mFragment).onRestoreState(mSavedState);
            }
        }
    }

    public PublishSubject<FragmentEvent> getLifecycleSubject() {
        return mLifecycleSubject;
    }
}
