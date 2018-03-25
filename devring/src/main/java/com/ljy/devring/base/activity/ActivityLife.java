package com.ljy.devring.base.activity;

import android.app.Activity;
import android.os.Bundle;

import com.ljy.devring.DevRing;
import com.trello.rxlifecycle2.android.ActivityEvent;

import io.reactivex.subjects.PublishSubject;

/**
 * author:  ljy
 * date:    2018/3/19
 * description: 负责Activity各生命周期处理，相当于基类的功能
 */

public class ActivityLife implements IActivityLife {

    private Activity mActivity;
    private final PublishSubject<ActivityEvent> mLifecycleSubject = PublishSubject.create();

    @Override
    public void onCreate(Activity activity, Bundle savedInstanceState) {
        mActivity = activity;

        mLifecycleSubject.onNext(ActivityEvent.CREATE);

        DevRing.activityStackManager().pushOneActivity(mActivity);

        if (((IBaseActivity) mActivity).isUseEventBus()) {
            DevRing.busManager().register(mActivity);
        }
    }

    @Override
    public void onStart() {
        mLifecycleSubject.onNext(ActivityEvent.START);
    }

    @Override
    public void onResume() {
        mLifecycleSubject.onNext(ActivityEvent.RESUME);
    }

    @Override
    public void onPause() {
        mLifecycleSubject.onNext(ActivityEvent.PAUSE);
    }

    @Override
    public void onStop() {
        mLifecycleSubject.onNext(ActivityEvent.STOP);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onDestroy() {
        mLifecycleSubject.onNext(ActivityEvent.DESTROY);

        DevRing.activityStackManager().popOneActivity(mActivity);

        if (((IBaseActivity) mActivity).isUseEventBus()) {
            DevRing.busManager().unregister(mActivity);
        }
        mActivity = null;
    }

    public PublishSubject<ActivityEvent> getLifecycleSubject() {
        return mLifecycleSubject;
    }
}
