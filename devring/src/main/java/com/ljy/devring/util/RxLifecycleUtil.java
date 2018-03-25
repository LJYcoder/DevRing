package com.ljy.devring.util;

import com.ljy.devring.DevRing;
import com.ljy.devring.base.activity.IBaseActivity;
import com.ljy.devring.base.fragment.FragmentLife;
import com.ljy.devring.base.fragment.IBaseFragment;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.subjects.PublishSubject;

/**
 * author:  ljy
 * date:    2018/3/20
 * description: 获取用于管理生命周期的Transformer
 */

public class RxLifecycleUtil {

    public static <T> LifecycleTransformer<T> bindUntilEvent(@NonNull Object lifecycleEmitter, ActivityEvent event) {
        Preconditions.checkNotNull(lifecycleEmitter, "lifecycleEmitter不能为空");
        if (lifecycleEmitter instanceof IBaseActivity) {
            return RxBindUntilEvent(getActivityLifeSubject(lifecycleEmitter.toString()), event);
        } else {
            throw new IllegalArgumentException("lifecycleEmitter需为实现IBaseActivity的类");
        }
    }

    public static <T> LifecycleTransformer<T> bindUntilEvent(@NonNull Object lifecycleEmitter, FragmentEvent event) {
        Preconditions.checkNotNull(lifecycleEmitter, "lifecycleEmitter不能为空");
        if (lifecycleEmitter instanceof IBaseFragment) {
            return RxBindUntilEvent(getFragmentLifeSubject(lifecycleEmitter.toString()), event);
        } else {
            throw new IllegalArgumentException("lifecycleEmitter需为实现IBaseFragment的类");
        }
    }

    public static <T> LifecycleTransformer<T> bindUntilDestroy(@NonNull Object lifecycleEmitter) {
        Preconditions.checkNotNull(lifecycleEmitter, "lifecycleEmitter不能为空");
        if (lifecycleEmitter instanceof IBaseActivity) {
            return RxBindUntilEvent(getActivityLifeSubject(lifecycleEmitter.toString()), ActivityEvent.DESTROY);
        } else if (lifecycleEmitter instanceof IBaseFragment) {
            return RxBindUntilEvent(getFragmentLifeSubject(lifecycleEmitter.toString()), FragmentEvent.DESTROY);
        } else {
            throw new IllegalArgumentException("如果请求在Activity中发起，则lifecycleEmitter需为实现IBaseActivity的类，如果在Fragment中发起，则lifecycleEmitter需为实现IBaseFragment的类");
        }
    }

    public static <T, R> LifecycleTransformer<T> RxBindUntilEvent(@NonNull Observable<R> lifecycleable, R event) {
        Preconditions.checkNotNull(lifecycleable, "lifecycleable == null");
        return RxLifecycle.bindUntilEvent(lifecycleable, event);
    }

    public static PublishSubject<ActivityEvent> getActivityLifeSubject(String key) {
        return DevRing.ringComponent().activityLifeCallback().getActivityLife(key).getLifecycleSubject();
    }

    public static PublishSubject<FragmentEvent> getFragmentLifeSubject(String key) {
        FragmentLife fragmentLife = DevRing.ringComponent().fragmentLifeCallback().getFragmentLife(key);
        if (fragmentLife == null) {
            throw new IllegalArgumentException("请确保实现Fragment所在的Activity的isUseFragment()方法返回为true");
        }
        return fragmentLife.getLifecycleSubject();
    }
}
